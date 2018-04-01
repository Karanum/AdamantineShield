package com.karanumcoding.adamantineshield;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.asset.Asset;

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
		Asset defaultConfig = Sponge.getAssetManager().getAsset(plugin, "config.conf").get();
		if (Files.notExists(pluginPath)) {
			defaultConfig.copyToFile(pluginPath);
			configLoader = HoconConfigurationLoader.builder().setPath(pluginPath).build();
			mainNode = configLoader.load();
		} else {
			CommentedConfigurationNode defaultMainNode = HoconConfigurationLoader.builder()
					.setURL(defaultConfig.getUrl())
					.build().load();
			configLoader = HoconConfigurationLoader.builder().setPath(pluginPath).build();
			mainNode = configLoader.load();
			if (checkIntegrity(defaultMainNode, true)) {
				plugin.getLogger().info("Updated configuration file, be sure to check the new options!");
			}
		}
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
	
	private boolean checkIntegrity(CommentedConfigurationNode defaultNode, boolean isRoot) throws IOException {
		boolean configUpdated = false;
		for (CommentedConfigurationNode child : defaultNode.getChildrenMap().values()) {
			if (child.hasMapChildren()) {
				boolean childUpdated = checkIntegrity(child, false);
				configUpdated = configUpdated || childUpdated;
			} else {
				CommentedConfigurationNode targetNode = (CommentedConfigurationNode) mainNode.getNode(child.getPath());
				if (targetNode.isVirtual()) {
					targetNode.setValue(child.getValue());
					Optional<String> comment = child.getComment();
					if (comment.isPresent())
						targetNode.setComment(comment.get());
					configUpdated = true;
				}
			}
		}
		if (isRoot && configUpdated) {
			configLoader.save(mainNode);
		}
		return configUpdated;
	}
	
}
