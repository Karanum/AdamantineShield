package com.karanumcoding.adamantineshield.rollback;

import java.util.List;
import java.util.Optional;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.block.BlockType;
import org.spongepowered.api.block.tileentity.TileEntity;
import org.spongepowered.api.block.tileentity.carrier.TileEntityCarrier;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.cause.NamedCause;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.inventory.Inventory;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.Slot;
import org.spongepowered.api.item.inventory.property.SlotIndex;
import org.spongepowered.api.item.inventory.property.SlotPos;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import com.google.common.collect.Lists;
import com.karanumcoding.adamantineshield.lookup.LookupLine;
import com.karanumcoding.adamantineshield.lookup.LookupResult;
import com.karanumcoding.adamantineshield.util.DataUtils;

public class RollbackManager {
	
	private List<LookupLine> queue;
	
	public RollbackManager() {
		queue = Lists.newArrayList();
	}
	
	public void rollbackResult(LookupResult r) {
		queue(r, false);
	}
	
	public void undoResult(LookupResult r) {
		queue(r, true);
	}
	
	private void queue(LookupResult r, boolean rolledBack) {
		if (r == null) return;
		for (LookupLine line : r.getLines()) {
			if (line.getRolledBack() == rolledBack)
				queue.add(line);
		}
	}
	
	private void performAddition(LookupLine line) {
		World w = Sponge.getServer().getWorld(line.getWorld()).orElse(null);
		if (w == null) return;
		Location<World> loc = new Location<World>(w, line.getPos());
		
		if (line.getTarget() instanceof ItemType) {
			
			Optional<TileEntity> te = w.getTileEntity(line.getPos());
			if (te.isPresent() && te.get() instanceof TileEntityCarrier) {
				TileEntityCarrier c = (TileEntityCarrier) te.get();
				Inventory i = c.getInventory();
				
				ItemType type = (ItemType) line.getTarget();
				ItemStack stack = ItemStack.builder()
						.fromContainer(line.getDataAsView())
						.itemType(type)
						.quantity(line.getCount())
						.build();
				Inventory slot = i.query(new SlotIndex(line.getSlot()));
				slot.set(stack);
			}
			
		} else if (line.getTarget() instanceof BlockType) {
			
			BlockType type = (BlockType) line.getTarget();
			BlockState block = BlockState.builder().build(line.getDataAsView()).orElse(null);
			//if (block != null)
				//w.setBlock(line.getPos(), block, Cause.of(NamedCause.source("AdamantineShieldRollback")));
			
		}
	}
	
	private void performRemoval(LookupLine line) {
		World w = Sponge.getServer().getWorld(line.getWorld()).orElse(null);
		if (w == null) return;
		Location<World> loc = new Location<World>(w, line.getPos());
		
		if (line.getTarget() instanceof ItemType) {
			
			//Item removal here
			
		} else if (line.getTarget() instanceof BlockType) {
			
			//Block removal here
			
		}
	}
	
}
