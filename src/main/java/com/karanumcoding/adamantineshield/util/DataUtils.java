package com.karanumcoding.adamantineshield.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Set;
import java.util.regex.Pattern;

import org.spongepowered.api.data.DataView;
import org.spongepowered.api.data.persistence.DataTranslators;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.Sets;

import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.gson.GsonConfigurationLoader;

public final class DataUtils {
	
	public static final Pattern SIGN_TEXT_REGEX = Pattern.compile("\\{\"text\"\\:\"(.*)\"\\}");
	
	private static BiMap<String, String> compressionMap = HashBiMap.create();
	private static Set<String> ignoredKeys = Sets.newHashSet();
	private static Set<String> ignoredTopLevelKeys = Sets.newHashSet();
	
	private DataUtils() {}
	
	public static void populateCompressionMap() {
		//Parent keys
		compressionMap.put("BlockState", "b");
		compressionMap.put("BlockExtendedState", "be");
		compressionMap.put("ContentVersion", "v");
		compressionMap.put("DataClass", "dc");
		compressionMap.put("ManipulatorData", "m");
		compressionMap.put("TileEntityData", "td");
		compressionMap.put("UnsafeDamage", "ud");
		compressionMap.put("UnsafeData", "u");
		
		//Unsafe data keys
		compressionMap.put("BannerBaseColor", "bbc");
		compressionMap.put("Base", "bb");
		compressionMap.put("BurnTime", "bt");
		compressionMap.put("CookTime", "ct");
		compressionMap.put("CookTimeTotal", "ctt");
		compressionMap.put("Count", "c");
		compressionMap.put("Damage", "d");
		compressionMap.put("display", "dp");
		compressionMap.put("ench", "e");
		compressionMap.put("ForgeData", "fd");
		compressionMap.put("Items", "i");
		compressionMap.put("Levels", "lv");
		compressionMap.put("Lock", "lo");
		compressionMap.put("Lore", "l");
		compressionMap.put("Name", "n");
		compressionMap.put("Potion", "po");
		compressionMap.put("Primary", "bcp");
		compressionMap.put("RepairCost", "rc");
		compressionMap.put("Secondary", "bcs");
		compressionMap.put("Slot", "s");
		
		//Block data keys
		compressionMap.put("ConnectedDirections", "cd");
		compressionMap.put("ConnectedEast", "ce");
		compressionMap.put("ConnectedNorth", "cn");
		compressionMap.put("ConnectedSouth", "cs");
		compressionMap.put("ConnectedWest", "cw");
		
		//Ignored keys
		ignoredKeys.add("Data");
		ignoredKeys.add("WorldUuid");
		ignoredKeys.add("Position");
		ignoredKeys.add("x");
		ignoredKeys.add("y");
		ignoredKeys.add("z");
		
		ignoredTopLevelKeys.add("ItemType");
		ignoredTopLevelKeys.add("Count");
	}
	
	public static void addCompressionEntry(String original, String compressed) {
		compressionMap.put(original, compressed);
	}
	
	public static void addIgnoredKey(String key) {
		ignoredKeys.add(key);
	}
	
	public static void addIgnoredTopLevelKey(String key) {
		ignoredTopLevelKeys.add(key);
	}
	
	public static String dataToString(DataView data) throws IOException {
		StringWriter writer = new StringWriter();
		GsonConfigurationLoader loader = GsonConfigurationLoader.builder().setSink(() ->
			new BufferedWriter(writer)).build();
		
		ConfigurationNode result = compress(DataTranslators.CONFIGURATION_NODE.translate(data));
		if (result.getChildrenMap().size() == 1 && result.getChildrenMap().containsKey(compressionMap.get("ContentVersion")))
			return null;
		
		loader.save(compress(DataTranslators.CONFIGURATION_NODE.translate(data)));
		return writer.toString().replaceAll("\\n\\s*", "");
	}
	
	public static DataView dataFromString(String str) throws IOException {
		return DataTranslators.CONFIGURATION_NODE.translate(configNodeFromString(str));
	}
	
	public static ConfigurationNode configNodeFromString(String str) throws IOException {
		return decompress(GsonConfigurationLoader.builder().setSource(() -> new BufferedReader(
				new StringReader(str))).build().load());
	}
	
	private static ConfigurationNode compress(ConfigurationNode data) {
		for (Object key : data.getChildrenMap().keySet()) {
			compress(data.getNode(key));
		}
		for (ConfigurationNode child : data.getChildrenList()) {
			compress(child);
		}
		
		if (data.getKey() != null && data.getKey() instanceof String) {
			ConfigurationNode parent = data.getParent();
			String key = data.getKey().toString();
			
			if (key.equals("BlockState") || key.equals("BlockExtendedState")) {
				if (data.hasMapChildren()) {
					if (data.getChildrenMap().size() == 1 && data.getChildrenMap().containsKey(compressionMap.get("ContentVersion"))) {
						parent.removeChild(key);
						return data;
					}
				} else if (!data.getString().contains("[")) {
					parent.removeChild(key);
					return data;
				}
			}
			
			if (key.equals("Lock") && data.getString().isEmpty()) {
				parent.removeChild(key);
				return data;
			}
			
			if (ignoredKeys.contains(key) || (parent.getParent() == null && ignoredTopLevelKeys.contains(key))) {
				parent.removeChild(key);
			} else if (key.equals("UnsafeDamage") && data.getInt() == 0) {
				parent.removeChild(key);
			} else if (compressionMap.containsKey(key)) {
				parent.getNode(compressionMap.get(key)).setValue(data.getValue());
				parent.removeChild(key);
			}
		}
		
		return data;
	}
	
	private static ConfigurationNode decompress(ConfigurationNode data) {		
		for (Object key : data.getChildrenMap().keySet()) {
			decompress(data.getNode(key));
		}
		for (ConfigurationNode child : data.getChildrenList()) {
			decompress(child);
		}
		
		if (data.getKey() != null && data.getKey() instanceof String) {
			ConfigurationNode parent = data.getParent();
			String key = data.getKey().toString();
			
			if (compressionMap.inverse().containsKey(key)) {
				parent.getNode(compressionMap.inverse().get(key)).setValue(data.getValue());
				parent.removeChild(key);
			} else {
				if (key.equals("lvl") || key.equals("id")) {
					try {
						data.setValue(Integer.parseInt(data.getString()));
					} catch (NumberFormatException e) {}
				}
			}
		}
		
		return data;
	}
	
}
