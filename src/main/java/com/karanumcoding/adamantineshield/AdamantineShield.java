package com.karanumcoding.adamantineshield;

import java.io.IOException;
import java.sql.SQLException;

import org.slf4j.Logger;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandManager;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.GamePostInitializationEvent;
import org.spongepowered.api.event.game.state.GamePreInitializationEvent;
import org.spongepowered.api.event.game.state.GameStoppingServerEvent;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.text.Text;

import com.google.inject.Inject;
import com.karanumcoding.adamantineshield.commands.*;
import com.karanumcoding.adamantineshield.commands.lookup.*;
import com.karanumcoding.adamantineshield.commands.pages.*;
import com.karanumcoding.adamantineshield.commands.rollback.*;
import com.karanumcoding.adamantineshield.db.Database;
import com.karanumcoding.adamantineshield.enums.Permissions;
import com.karanumcoding.adamantineshield.listeners.MobChangeListener;
import com.karanumcoding.adamantineshield.listeners.NaturalChangeListener;
import com.karanumcoding.adamantineshield.listeners.PlayerChangeListener;
import com.karanumcoding.adamantineshield.lookup.InspectManager;

@Plugin(id = "adamantineshield", name = "AdamantineShield", version = "0.1.1", authors = { "Karanum", "Snootiful" },
	description = "Action logging and rollback plugin for Sponge"
)
public class AdamantineShield {
	
	@Inject
	private Logger logger;
	public Logger getLogger() {
		return logger;
	}
	
	private ConfigHandler config;
	private Database db;
	
	private InspectManager inspectManager;
	
	@Listener
	public void onPreInit(GamePreInitializationEvent e) {
		try {
			config = new ConfigHandler(this);
		} catch (IOException ioEx) {
			ioEx.printStackTrace();
			logger.error("Could not load configuration file! Plugin was not loaded!");
			return;
		}
		
		try {
			db = new Database(this, config.getJdbcString());
		} catch (SQLException sqlEx) {
			sqlEx.printStackTrace();
			logger.error("Could not connect to the database! Plugin was not loaded!");
			return;
		}
		
		inspectManager = new InspectManager(db);
		
		logger.info("Registering event listeners");
		Sponge.getEventManager().registerListeners(this, new PlayerChangeListener(this, db));
		Sponge.getEventManager().registerListeners(this, new MobChangeListener(db));
		Sponge.getEventManager().registerListeners(this, new NaturalChangeListener(db));
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
	}
	
	public Database getDatabase() {
		return db;
	}
	
	public ConfigHandler getConfig() {
		return config;
	}
	
	public InspectManager getInspectManager() {
		return inspectManager;
	}
	
	private void registerCommands(CommandManager man) {
		CommandSpec filterCommand = CommandSpec.builder()
				.permission(Permissions.FILTER.get())
				.executor(new CommandFilter())
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
				.executor(new CommandLookup())
				.build();
		
		CommandSpec nextPageCommand = CommandSpec.builder()
				.permission(Permissions.LOOKUP.get())
				.executor(new CommandNextPage())
				.build();
		
		CommandSpec pageCommand = CommandSpec.builder()
				.permission(Permissions.LOOKUP.get())
				.executor(new CommandPage())
				.build();
		
		CommandSpec prevPageCommand = CommandSpec.builder()
				.permission(Permissions.LOOKUP.get())
				.executor(new CommandPrevPage())
				.build();
		
		CommandSpec purgeCommand = CommandSpec.builder()
				.permission(Permissions.PURGE.get())
				.executor(new CommandPurge())
				.build();
		
		CommandSpec redoCommand = CommandSpec.builder()
				.permission(Permissions.REDO.get())
				.executor(new CommandRedo())
				.build();
		
		CommandSpec reloadCommand = CommandSpec.builder()
				.permission(Permissions.RELOAD.get())
				.executor(new CommandReload())
				.build();
		
		CommandSpec rollbackCommand = CommandSpec.builder()
				.permission(Permissions.ROLLBACK.get())
				.executor(new CommandRollback())
				.build();
		
		CommandSpec undoCommand = CommandSpec.builder()
				.permission(Permissions.UNDO.get())
				.executor(new CommandUndo())
				.build();
		
		CommandSpec parentCommand = CommandSpec.builder()
				.description(Text.of("Main command for AdamantineShield"))
				.child(inspectCommand, "inspect", "i")
				.child(lookupCommand, "lookup", "l")
				.child(filterCommand, "filter", "f")
				.child(pageCommand, "page", "p")
				.child(nextPageCommand, "nextpage", "next")
				.child(prevPageCommand, "prevpage", "prev")
				.child(rollbackCommand, "rollback", "rb", "r")
				.child(undoCommand, "undo", "u")
				.child(redoCommand, "redo", "re")
				.child(purgeCommand, "purge")
				.child(reloadCommand, "reload")
				.child(helpCommand, "help", "?")
				.executor(new CommandMain())
				.build();
		
		man.register(this, parentCommand, "ashield", "as");
	}
	
}
