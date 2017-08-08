package com.karanumcoding.adamantineshield.lookup;

import java.util.UUID;

import org.spongepowered.api.CatalogType;

import com.flowpowered.math.vector.Vector3i;
import com.karanumcoding.adamantineshield.enums.ActionType;
import com.karanumcoding.adamantineshield.util.PlayerUtils;

public class LookupLine {

	private Vector3i pos;
	private UUID world;
	private ActionType type;
	private String cause;
	private CatalogType target;
	private int count;
	//private String data;
	private long timestamp;
	
	public LookupLine(Vector3i pos, UUID world, ActionType type, String cause, CatalogType target, int count, long timestamp) {
		this.pos = pos;
		this.world = world;
		this.type = type;
		this.cause = cause;
		this.target = target;
		this.count = count;
		//this.data = data;
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
