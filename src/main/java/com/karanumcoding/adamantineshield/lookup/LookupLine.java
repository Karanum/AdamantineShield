package com.karanumcoding.adamantineshield.lookup;

import java.util.UUID;

import org.spongepowered.api.block.BlockType;

import com.flowpowered.math.vector.Vector3i;
import com.karanumcoding.adamantineshield.enums.ActionType;
import com.karanumcoding.adamantineshield.util.PlayerUtils;

public class LookupLine {

	private Vector3i pos;
	private UUID world;
	private ActionType type;
	private String cause;
	private BlockType block;
	private long timestamp;
	
	public LookupLine(Vector3i pos, UUID world, ActionType type, String cause, BlockType block, long timestamp) {
		this.pos = pos;
		this.world = world;
		this.type = type;
		this.cause = cause;
		this.block = block;
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
	
	public BlockType getBlock() {
		return block;
	}
	
	public long getTime() {
		return timestamp;
	}
	
	@Override
	public String toString() {
		switch (type) {
			case PLACE:
				return PlayerUtils.getName(getPlayerCause()) + " placed " + block.getName();
			case DESTROY:
				return PlayerUtils.getName(getPlayerCause()) + " broke " + block.getName();
			case FLOW:
				return cause + " flow occurred";
			case MOB_DESTROY:
				return "Entity " + cause + " broke " + block.getName();
			case MOB_PLACE:
				return "Entity " + cause + " placed " + block.getName();
		}
		return "(unsupported action type)";
	}
	
}
