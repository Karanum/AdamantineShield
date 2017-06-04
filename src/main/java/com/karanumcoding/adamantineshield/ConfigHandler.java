package com.karanumcoding.adamantineshield;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.spongepowered.api.Sponge;

import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;
import ninja.leaping.configurate.loader.ConfigurationLoader;

public class ConfigHandler {

	private Path pluginPath;
	private ConfigurationLoader<CommentedConfigurationNode> configLoader;
	private ConfigurationNode mainNode;
	
	public ConfigHandler(AdamantineShield plugin) throws IOException {
		pluginPath = Sponge.getGame().getConfigManager().getPluginConfig(plugin).getConfigPath();
		if (Files.notExists(pluginPath)) {
			Sponge.getAssetManager().getAsset(plugin, "config.conf").get().copyToFile(pluginPath);
		}
		
		configLoader = HoconConfigurationLoader.builder().setPath(pluginPath).build();
		mainNode = configLoader.load();
	}
	
	public boolean getBool(Object... path) {
		return mainNode.getNode(path).getBoolean();
	}
	
	public int getInt(Object... path) {
		return mainNode.getNode(path).getInt();
	}
	
	public String getJdbcString() {
		if (mainNode.getNode("database", "local-mode").getBoolean())
			return "jdbc:h2:file:" + pluginPath.getParent().toAbsolutePath() + "/db/database";
		
		return String.format("jdbc:%s://%s:%d/%s?user=%s&password=%s",
				mainNode.getNode("database", "type").getString(),
				mainNode.getNode("database", "hostname").getString(),
				mainNode.getNode("database", "port").getInt(),
				mainNode.getNode("database", "database").getString(),
				mainNode.getNode("database", "user").getString(),
				mainNode.getNode("database", "pass").getString());
	}
	
}
