package com.karanumcoding.adamantineshield;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandManager;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.event.EventManager;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.GamePostInitializationEvent;
import org.spongepowered.api.event.game.state.GamePreInitializationEvent;
import org.spongepowered.api.event.game.state.GameStoppingServerEvent;
import org.spongepowered.api.event.network.ClientConnectionEvent;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.text.Text;

import com.google.inject.Inject;
import com.karanumcoding.adamantineshield.commands.*;
import com.karanumcoding.adamantineshield.commands.arguments.TimeStringArgument;
import com.karanumcoding.adamantineshield.commands.lookup.*;
import com.karanumcoding.adamantineshield.commands.pages.*;
import com.karanumcoding.adamantineshield.commands.rollback.*;
import com.karanumcoding.adamantineshield.db.Database;
import com.karanumcoding.adamantineshield.enums.Permissions;
import com.karanumcoding.adamantineshield.listeners.*;
import com.karanumcoding.adamantineshield.lookup.InspectManager;
import com.karanumcoding.adamantineshield.lookup.LookupResultManager;
import com.karanumcoding.adamantineshield.rollback.RollbackManager;
import com.karanumcoding.adamantineshield.util.DataUtils;
import com.karanumcoding.adamantineshield.util.FilterParser;

@Plugin(id = "adamantineshield", name = "AdamantineShield", version = "0.4.0", authors = { "Karanum", "Snootiful" },
	description = "Action logging and rollback plugin for Sponge"
)
public class AdamantineShield {

	private static AdamantineShield instance;

	@Inject
	private Logger logger;
	public Logger getLogger() {
		return logger;
	}
	
	private ConfigHandler config;
	private Database db;
	private ExecutorService threadPool;
	
	private InspectManager inspectManager;
	private RollbackManager rollbackManager;
	
	@Listener
	public void onPreInit(GamePreInitializationEvent e) {
		DataUtils.populateCompressionMap();

		instance = this;
		
		try {
			config = new ConfigHandler(this);
		} catch (IOException ioEx) {
			ioEx.printStackTrace();
			logger.error("Could not load configuration file! Plugin was not loaded!");
			return;
		}
		FilterParser.setConfig(config);
		LookupResultManager.instance().setLinesPerPage(config.getInt("lookup", "lines-per-page"));
		
		try {
			db = new Database(this, config.getJdbcString());
		} catch (SQLException sqlEx) {
			sqlEx.printStackTrace();
			logger.error("Could not connect to the database! Plugin was not loaded!");
			return;
		}

		int threadMax = config.getInt("threading", "max-threads");
		if (threadMax < 1) threadMax = 1;
		threadPool = Executors.newFixedThreadPool(threadMax, new ThreadFactory() {
			private int counter = 0;

			@Override
			public Thread newThread(Runnable r) {
				return new Thread(r, String.format("%sPool-Thread-%d",
						Sponge.getPluginManager().fromInstance(AdamantineShield.instance).get().getId(),
						counter++));
			}
		});
		
		if (config.getBool("purge", "auto-purge")) {
			int days = config.getInt("purge", "auto-purge-days");
			logger.info("Purging entries older than " + days + " days");
			if (!db.purgeEntries(new Date().getTime() - TimeUnit.DAYS.toMillis(days))) {
				logger.warn("Automatic purge failed!");
			}
		}
		
		inspectManager = new InspectManager(db);
		rollbackManager = new RollbackManager(this);
		
		logger.info("Registering event listeners");
		registerListeners(Sponge.getEventManager());
	}
	
	@Listener
	public void onPostInit(GamePostInitializationEvent e) {
		if (db == null) return;
		logger.info("Registering plugin commands");
		registerCommands(Sponge.getCommandManager());
	}
	
	@Listener
	public void onServerStopping(GameStoppingServerEvent e) {
		if (db == null) return;
		db.stop();

		threadPool.shutdown();
		logger.info("Waiting for tasks to finish executing");
		try {
			if (!threadPool.awaitTermination(10, TimeUnit.SECONDS))
				threadPool.shutdownNow();
		} catch (InterruptedException e1) {
			threadPool.shutdownNow();
		}
	}
	
	@Listener
	public void onPlayerLeave(ClientConnectionEvent.Disconnect e) {
		LookupResultManager.instance().clearLookupResult(e.getTargetEntity());
	}
	
	public Database getDatabase() {
		return db;
	}
	
	public ConfigHandler getConfig() {
		return config;
	}
	
	public ExecutorService getThreadPool() {
		return threadPool;
	}

	public InspectManager getInspectManager() {
		return inspectManager;
	}
	
	public RollbackManager getRollbackManager() {
		return rollbackManager;
	}
	
	private void registerListeners(EventManager man) {
		man.registerListeners(this, new PlayerInspectListener(this));
		if (config.getBool("logging", "blocks")) {
			man.registerListeners(this, new PlayerBlockChangeListener(db));
			man.registerListeners(this, new EntityBlockChangeListener(db));
		}
		if (config.getBool("logging", "mobs")) {
			man.registerListeners(this, new MobBlockChangeListener(db));
		}
		if (config.getBool("logging", "flow")) {
			man.registerListeners(this, new LiquidFlowListener(db));
		}
		if (config.getBool("logging", "containers") || config.getBool("logging", "chests")) {
			man.registerListeners(this, new InventoryChangeListener(db, config.getBool("logging", "containers")));
		}
		if (config.getBool("logging", "plant-growth") || config.getBool("logging", "tree-growth")) {
			man.registerListeners(this, new PlantGrowthListener(
					db,
					config.getBool("logging", "plant-growth"),
					config.getBool("logging", "tree-growth")));
		}
		if (config.getBool("logging", "chat") || config.getBool("logging", "commands")) {
			man.registerListeners(this, new ChatListener(db, 
					config.getBool("logging", "chat"), config.getBool("logging", "commands")));
		}
	}
	
	private void registerCommands(CommandManager man) {
		CommandSpec filterCommand = CommandSpec.builder()
				.permission(Permissions.FILTER.get())
				.arguments(GenericArguments.allOf(GenericArguments.string(Text.of("filter"))))
				.executor(new CommandFilter(this))
				.build();
		
		CommandSpec helpCommand = CommandSpec.builder()
				.executor(new CommandMain())
				.build();
		
		CommandSpec inspectCommand = CommandSpec.builder()
				.permission(Permissions.LOOKUP.get())
				.executor(new CommandInspect(this))
				.build();
		
		CommandSpec lookupCommand = CommandSpec.builder()
				.permission(Permissions.LOOKUP.get())
				.arguments(GenericArguments.allOf(GenericArguments.string(Text.of("filter"))))
				.executor(new CommandLookup(this))
				.build();
		
		CommandSpec nextPageCommand = CommandSpec.builder()
				.permission(Permissions.LOOKUP.get())
				.executor(new CommandNextPage())
				.build();
		
		CommandSpec pageCommand = CommandSpec.builder()
				.permission(Permissions.LOOKUP.get())
				.arguments(GenericArguments.onlyOne(GenericArguments.integer(Text.of("page"))))
				.executor(new CommandPage())
				.build();
		
		CommandSpec prevPageCommand = CommandSpec.builder()
				.permission(Permissions.LOOKUP.get())
				.executor(new CommandPrevPage())
				.build();
		
		CommandSpec purgeCommand = CommandSpec.builder()
				.permission(Permissions.PURGE.get())
				.arguments(GenericArguments.onlyOne(new TimeStringArgument(Text.of("time"))))
				.executor(new CommandPurge(this))
				.build();
		
//		CommandSpec reloadCommand = CommandSpec.builder()
//				.permission(Permissions.RELOAD.get())
//				.executor(new CommandReload())
//				.build();
		
		CommandSpec rollbackCommand = CommandSpec.builder()
				.permission(Permissions.ROLLBACK.get())
				.arguments(GenericArguments.allOf(GenericArguments.string(Text.of("filter"))))
				.executor(new CommandRollback(this))
				.build();
//		
//		CommandSpec undoCommand = CommandSpec.builder()
//				.permission(Permissions.UNDO.get())
//				.arguments(GenericArguments.allOf(GenericArguments.string(Text.of("filter"))))
//				.executor(new CommandUndo(this))
//				.build();
		
		CommandSpec parentCommand = CommandSpec.builder()
				.description(Text.of("Main command for AdamantineShield"))
				.child(inspectCommand, "inspect", "i")
				.child(lookupCommand, "lookup", "l")
				.child(filterCommand, "filter", "f")
				.child(pageCommand, "page", "p")
				.child(nextPageCommand, "nextpage", "next")
				.child(prevPageCommand, "prevpage", "prev")
				.child(rollbackCommand, "rollback", "rb", "r")
//				.child(undoCommand, "undo", "u")
				.child(purgeCommand, "purge")
				//.child(reloadCommand, "reload")
				.child(helpCommand, "help", "?")
				.executor(new CommandMain())
				.build();
		
		man.register(this, parentCommand, "ashield", "as");
	}
	
}
