package com.karanumcoding.adamantineshield.util;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.spongepowered.api.CatalogType;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.block.BlockType;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.profile.GameProfile;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import com.google.common.collect.Lists;
import com.karanumcoding.adamantineshield.ConfigHandler;
import com.karanumcoding.adamantineshield.enums.LookupType;
import com.karanumcoding.adamantineshield.enums.Permissions;
import com.karanumcoding.adamantineshield.lookup.FilterSet;
import com.karanumcoding.adamantineshield.lookup.filters.ActionFilter;
import com.karanumcoding.adamantineshield.lookup.filters.AfterTimeFilter;
import com.karanumcoding.adamantineshield.lookup.filters.BeforeTimeFilter;
import com.karanumcoding.adamantineshield.lookup.filters.CauseFilter;
import com.karanumcoding.adamantineshield.lookup.filters.ExcludeTypeFilter;
import com.karanumcoding.adamantineshield.lookup.filters.IncludeTypeFilter;
import com.karanumcoding.adamantineshield.lookup.filters.PositionFilter;

public final class FilterParser {

	private FilterParser() {}
	
	private static ConfigHandler config = null;
	public static void setConfig(ConfigHandler config) {
		FilterParser.config = config;
	}
	
	public static void parse(Collection<String> filters, FilterSet container, Player p) throws CommandException {
		if (container == null || filters.isEmpty())
			return;
		
		List<String> includedTypes = Lists.newArrayList();
		List<String> excludedTypes = Lists.newArrayList();
		
		boolean isGlobal = false;
		
		for (String filter : filters) {
			if (filter.isEmpty()) continue;
			String[] bits = filter.split(":", 2);
			
			if (bits.length == 1) {
				if (filter.equalsIgnoreCase("#global")) {
					if (!p.hasPermission(Permissions.TARGET_GLOBAL.get()))
						throw new CommandException(Text.of(TextColors.RED, "You do not have permission to perform global operations!"));
					isGlobal = true;
				} else {
					container.getOrCreate(new CauseFilter()).addCause(getCause(filter));
				}
			} else {
				switch (bits[0].toLowerCase()) {
					case "u":
					case "c":
						for (String causeName : bits[1].split(","))
							container.getOrCreate(new CauseFilter()).addCause(getCause(causeName));
						break;
						
					case "i":
						for (String type : bits[1].split(","))
							includedTypes.add(type);
						break;
						
					case "e":
						for (String type : bits[1].split(","))
							excludedTypes.add(type);
						break;
						
					case "t":
						try {
							container.addFilter(new AfterTimeFilter(TimeUtils.timeStringToLong(bits[1])));
						} catch (NumberFormatException e) {
							throw new CommandException(Text.of(TextColors.RED, "Invalid time format: " + bits[1]));
						}
						break;
						
					case "b":
						try {
							container.addFilter(new BeforeTimeFilter(TimeUtils.timeStringToLong(bits[1])));
						} catch (NumberFormatException e) {
							throw new CommandException(Text.of(TextColors.RED, "Invalid time format: " + bits[1]));
						}
						break;
						
					case "a":
						try {
							container.addFilter(new ActionFilter(bits[1]));
						} catch (IllegalArgumentException e) {
							throw new CommandException(Text.of(TextColors.RED, "Unknown action type: " + bits[1]));
						}
						break;
						
					case "r":
						int radius;
						try {
							radius = Integer.parseInt(bits[1]);
						} catch (NumberFormatException e) {
							throw new CommandException(Text.of(TextColors.RED, "Invalid radius: " + bits[1]));
						}
						
						if (radius < 0) 
							throw new CommandException(Text.of(TextColors.RED, "Radius can not be negative!"));
						if (radius > config.getInt("lookup", "max-radius"))
							throw new CommandException(Text.of(TextColors.RED, "Radius is too large! Maximum allowed radius is ",
									config.getInt("lookup", "max-radius"), " blocks!"));
						container.getOrCreate(new PositionFilter(p.getLocation().getBlockPosition(), 0)).setRadius(radius);
						break;
				}
			}
		}
		
		for (String type : includedTypes)
			addIncluded(type, container);
		for (String type : excludedTypes)
			addExcluded(type, container);
		
		if (isGlobal)
			container.removeFilter(PositionFilter.class);
	}
	
	private static String getCause(String name) throws CommandException {
		if (name.startsWith("#")) {
			return name.toLowerCase().substring(1);
		}
		Optional<GameProfile> player = Sponge.getServer().getGameProfileManager().getCache().getByName(name);
		if (!player.isPresent()) {
			throw new CommandException(Text.of(TextColors.RED, "No player found with name ", name));
		}
		return player.get().getUniqueId().toString();
	}
	
	private static void addIncluded(String include, FilterSet container) throws CommandException {
		String id = include;
		if (!id.contains(":"))
			id = "minecraft:" + id;
		container.getOrCreate(new IncludeTypeFilter()).addType(getCatalogType(id, container.getLookupType()));
	}
	
	private static void addExcluded(String exclude, FilterSet container) throws CommandException {
		String id = exclude;
		if (!id.contains(":"))
			id = "minecraft:" + id;
		container.getOrCreate(new ExcludeTypeFilter()).addType(getCatalogType(id, container.getLookupType()));
	}
	
	private static CatalogType getCatalogType(String id, LookupType lookupType) throws CommandException {
		switch (lookupType) {
			case BLOCK_LOOKUP:
				Optional<BlockType> block = Sponge.getRegistry().getType(BlockType.class, id);
				if (block.isPresent())
					return block.get();
				throw new CommandException(Text.of(TextColors.RED, "Unknown block id: " + id));
			case ITEM_LOOKUP:
				Optional<ItemType> item = Sponge.getRegistry().getType(ItemType.class, id);
				if (item.isPresent())
					return item.get();
				throw new CommandException(Text.of(TextColors.RED, "Unknown item id: " + id));
			default:
				throw new CommandException(Text.of(TextColors.RED, "Could not determine lookup type!"));
		}
	}
	
}
