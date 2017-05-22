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
import com.karanumcoding.adamantineshield.commands.CommandInspect;
import com.karanumcoding.adamantineshield.commands.CommandMain;
import com.karanumcoding.adamantineshield.db.Database;
import com.karanumcoding.adamantineshield.listeners.MobChangeListener;
import com.karanumcoding.adamantineshield.listeners.NaturalChangeListener;
import com.karanumcoding.adamantineshield.listeners.PlayerChangeListener;
import com.karanumcoding.adamantineshield.lookup.InspectManager;

@Plugin(id = "adamantineshield", name = "AdamantineShield", version = "0.1", authors = { "Karanum" },
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
		logger.info("Registering plugin commands");
		registerCommands(Sponge.getCommandManager());
	}
	
	@Listener
	public void onServerStopping(GameStoppingServerEvent e) {
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
		CommandSpec inspectCommand = CommandSpec.builder()
				.permission("adamantineshield.use.inspect")
				.executor(new CommandInspect(this))
				.build();

//		CommandSpec undoCommand = CommandSpec.builder()
//				.permission("adamantineshield.use")
//				.build();	//TODO: Add executor
//		
//		CommandSpec lookupCommand = CommandSpec.builder()
//				.permission("adamantineshield.use.lookup")
//				.build();	//TODO: Add executor
//		
//		CommandSpec rollbackCommand = CommandSpec.builder()
//				.permission("adamantineshield.use.rollback")
//				.build();	//TODO: Add executor
		
		//TODO: Add commands for on-the-fly result filtering
		
		CommandSpec parentCommand = CommandSpec.builder()
				.description(Text.of("Main command for AdamantineShield"))
				.child(inspectCommand, "inspect", "i")
				//.child(undoCommand, "undo", "u")
				//.child(lookupCommand, "lookup", "l")
				//.child(rollbackCommand, "rollback", "rb", "r")
				.executor(new CommandMain())
				.build();
		
		man.register(this, parentCommand, "ashield", "as");
	}
	
}
