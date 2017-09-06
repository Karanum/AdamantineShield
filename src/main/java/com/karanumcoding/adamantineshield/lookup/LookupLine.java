package com.karanumcoding.adamantineshield.lookup;

import java.io.IOException;
import java.util.UUID;

import org.spongepowered.api.CatalogType;
import org.spongepowered.api.block.BlockType;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import com.flowpowered.math.vector.Vector3i;
import com.google.common.reflect.TypeToken;
import com.karanumcoding.adamantineshield.enums.ActionType;
import com.karanumcoding.adamantineshield.util.DataUtils;
import com.karanumcoding.adamantineshield.util.PlayerUtils;

import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;

public class LookupLine {

	private Vector3i pos;
	private UUID world;
	private ActionType type;
	private String cause;
	private CatalogType target;
	private int count;
	private String data;
	private long timestamp;
	
	public LookupLine(Vector3i pos, UUID world, ActionType type, String cause, String data, CatalogType target, int count, long timestamp) {
		this.pos = pos;
		this.world = world;
		this.type = type;
		this.cause = cause;
		this.target = target;
		this.count = count;
		this.data = data;
		this.timestamp = timestamp;
	}
	
	public Vector3i getPos() {
		return pos;
	}
	
	public int getX() {
		return pos.getX();
	}
	
	public int getY() {
		return pos.getY();
	}
	
	public int getZ() {
		return pos.getZ();
	}
	
	public UUID getWorld() {
		return world;
	}
	
	public ActionType getAction() {
		return type;
	}
	
	public String getNamedCause() {
		return cause;
	}
	
	public boolean causeIsPlayer() {
		return (type == ActionType.PLACE || type == ActionType.DESTROY);
	}
	
	public UUID getPlayerCause() {
		return UUID.fromString(cause);
	}
	
	public CatalogType getTarget() {
		return target;
	}
	
	public int getCount() {
		return count;
	}
	
	public long getTime() {
		return timestamp;
	}
	
	public Text getHoverText() {
		Text result = Text.of(TextColors.DARK_AQUA, "Location: ", TextColors.AQUA, pos.toString());
		if (data == null)
			return result;
		
		ConfigurationNode workingNode = null;
		ConfigurationNode node = null;
		try {
			node = DataUtils.configNodeFromString(data);
		} catch (IOException e) {
			e.printStackTrace();
			return result;
		}
		
		//Common tags
		workingNode = node.getNode("UnsafeData", "Damage");
		if (!workingNode.isVirtual()) {
			result = Text.of(result, Text.NEW_LINE, TextColors.DARK_AQUA, "Damage: ", TextColors.AQUA, workingNode.getInt());
		}
		
		workingNode = node.getNode("UnsafeData", "SkullOwner", "Name");
		if (!workingNode.isVirtual() && !workingNode.getString().isEmpty()) {
			result = Text.of(result, Text.NEW_LINE, TextColors.DARK_AQUA, "Player: ", TextColors.AQUA, workingNode.getString());
		}
		
		//Item exclusive tags
		if (target instanceof ItemType) {
			workingNode = node.getNode("UnsafeData", "display");
			if (!workingNode.isVirtual()) {
				ConfigurationNode innerNode = workingNode.getNode("Name");
				if (!innerNode.isVirtual()) {
					result = Text.of(result, Text.NEW_LINE, TextColors.DARK_AQUA, "Name: ", TextColors.AQUA, innerNode.getString());
				}
				
				innerNode = workingNode.getNode("color");
				if (!innerNode.isVirtual()) {
					int color = innerNode.getInt();
					result = Text.of(result, Text.NEW_LINE, TextColors.DARK_AQUA, "Color: ", TextColors.AQUA, "(",
							TextColors.RED, color << 16, TextColors.AQUA, ", ",
							TextColors.GREEN, color << 8 % 255, TextColors.AQUA, ", ",
							TextColors.BLUE, color % 255, TextColors.AQUA, ")");
				}
				
				innerNode = workingNode.getNode("Lore");
				if (!innerNode.isVirtual()) {
					try {
						Text sub = Text.of(TextColors.DARK_AQUA, "Lore: ");
						for (String line : innerNode.getList(TypeToken.of(String.class))) {
							sub = Text.of(sub, Text.NEW_LINE, TextColors.DARK_AQUA, " - ", TextColors.AQUA, line);
						}
						result = Text.of(result, Text.NEW_LINE, sub);
					} catch (ObjectMappingException e) {
						e.printStackTrace();
					}
				}
			}
			
			workingNode = node.getNode("UnsafeData", "author");
			if (!workingNode.isVirtual()) {
				result = Text.of(result, Text.NEW_LINE, TextColors.DARK_AQUA, "Author: ", TextColors.AQUA, workingNode.getString());
			}
			
			workingNode = node.getNode("UnsafeData", "Unbreakable");
			if (!workingNode.isVirtual()) {
				result = Text.of(result, Text.NEW_LINE, TextColors.DARK_AQUA, "Is unbreakable");
			}
		}
		
		//Block exclusive tags
		if (target instanceof BlockType) {
			workingNode = node.getNode("UnsafeData", "CustomName");
			if (!workingNode.isVirtual()) {
				result = Text.of(result, Text.NEW_LINE, TextColors.DARK_AQUA, "Name: ", TextColors.AQUA, workingNode.getString());
			}
			
			workingNode = node.getNode("UnsafeData", "Text1");
			if (!workingNode.isVirtual()) {
				ConfigurationNode dataNode = node.getNode("UnsafeData");
				if (!(dataNode.getNode("Text2").isVirtual() || dataNode.getNode("Text3").isVirtual() || dataNode.getNode("Text4").isVirtual())) {
					result = Text.of(result, Text.NEW_LINE, TextColors.DARK_AQUA, "Text: ", 
							Text.NEW_LINE, TextColors.DARK_AQUA, " - ", TextColors.AQUA, workingNode.getString(),
							Text.NEW_LINE, TextColors.DARK_AQUA, " - ", TextColors.AQUA, dataNode.getNode("Text2").getString(),
							Text.NEW_LINE, TextColors.DARK_AQUA, " - ", TextColors.AQUA, dataNode.getNode("Text3").getString(),
							Text.NEW_LINE, TextColors.DARK_AQUA, " - ", TextColors.AQUA, dataNode.getNode("Text4").getString());
				} else {
					result = Text.of(result, Text.NEW_LINE, TextColors.RED, "Contains incomplete sign data");
				}
			}
			
			workingNode = node.getNode("UnsafeData", "Lock");
			if (!workingNode.isVirtual()) {
				result = Text.of(result, Text.NEW_LINE, TextColors.DARK_AQUA, "Is locked");
			}
		}
		
		return result;
	}
	
	@Override
	public String toString() {
		switch (type) {
			case PLACE:
				return PlayerUtils.getName(getPlayerCause()) + " placed " + target.getId();
			case DESTROY:
				return PlayerUtils.getName(getPlayerCause()) + " broke " + target.getId();
			case FLOW:
				return cause.substring(0, 1).toUpperCase() + cause.substring(1) + " flow occurred";
			case MOB_DESTROY:
				return "Entity " + cause + " broke " + target.getId();
			case MOB_PLACE:
				return "Entity " + cause + " placed " + target.getId();
			case CONTAINER_ADD:
				return PlayerUtils.getName(getPlayerCause()) + " added " + count + "x " + target.getId();
			case CONTAINER_REMOVE:
				return PlayerUtils.getName(getPlayerCause()) + " removed " + count + "x " + target.getId();
			default:
				return "(unsupported action type)";
		}
	}
	
}
