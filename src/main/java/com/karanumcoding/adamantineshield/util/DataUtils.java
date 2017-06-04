package com.karanumcoding.adamantineshield.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;

import org.spongepowered.api.data.DataView;
import org.spongepowered.api.data.persistence.DataTranslators;

import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;

public final class DataUtils {

	private DataUtils() {}
	
	public static String dataToString(DataView data) throws IOException {
		StringWriter writer = new StringWriter();
		HoconConfigurationLoader loader = HoconConfigurationLoader.builder().setSink(() ->
			new BufferedWriter(writer)).build();
		loader.save(compress(DataTranslators.CONFIGURATION_NODE.translate(data)));
		return writer.toString();
	}
	
	public static DataView dataFromString(String str) throws IOException {
		return DataTranslators.CONFIGURATION_NODE.translate(decompress(
				HoconConfigurationLoader.builder().setSource(() -> new BufferedReader(
						new StringReader(str))).build().load()));
	}
	
	private static ConfigurationNode compress(ConfigurationNode data) {
		return data;
	}
	
	private static ConfigurationNode decompress(ConfigurationNode data) {
		return data;
	}
	
}
