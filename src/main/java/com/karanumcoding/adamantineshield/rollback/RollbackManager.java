package com.karanumcoding.adamantineshield.rollback;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.block.BlockType;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.block.tileentity.TileEntity;
import org.spongepowered.api.block.tileentity.carrier.TileEntityCarrier;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.inventory.Inventory;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.property.SlotIndex;
import org.spongepowered.api.item.inventory.query.QueryOperationTypes;
import org.spongepowered.api.plugin.PluginContainer;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.world.World;

import com.google.common.collect.Lists;
import com.karanumcoding.adamantineshield.AdamantineShield;
import com.karanumcoding.adamantineshield.lookup.LookupLine;

public class RollbackManager {
	
	private AdamantineShield plugin;
	private PluginContainer container;
	private List<RollbackJob> queue;
	private Task task;
	
	public RollbackManager(AdamantineShield plugin) {
		this.plugin = plugin;
		container = Sponge.getPluginManager().fromInstance(plugin).get();
		
		queue = Lists.newArrayList();
		task = null;
	}
	
	public void queue(RollbackJob job) {
		if (job == null) return;
		queue.add(job);
		
		if (task == null) {
			task = Task.builder().delay(1, TimeUnit.SECONDS).interval(500, TimeUnit.MILLISECONDS)
					.execute(() -> doRollbackCycle())
					.submit(plugin);
		}
	}
	
	private void doRollbackCycle() {
		int i = 0;
		while (i < 100 && !queue.isEmpty()) {
			RollbackJob job = queue.get(0);
			LookupLine line = job.getNext();
			if (line == null) {
				queue.remove(0);
				job.commitToDatabase();
				continue;
			}
			
			if (line.getAction().isAddition() ^ job.isUndo())
				performRemoval(line);
			else
				performAddition(line);
			++i;
		}
		
		if (queue.isEmpty()) {
			task.cancel();
			task = null;
		}
	}

	//TODO: Set proper causes for block changes caused by rollback/undo

	private void performAddition(LookupLine line) {
		
		World w = Sponge.getServer().getWorld(line.getWorld()).orElse(null);
		if (w == null) return;
		
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
				Inventory slot = i.query(QueryOperationTypes.INVENTORY_PROPERTY.of(SlotIndex.of(line.getSlot())));
				slot.set(stack);
			}
			
		} else if (line.getTarget() instanceof BlockType) {
			BlockState block;
			if (line.getDataAsView() == null)
				block = BlockState.builder().blockType((BlockType) line.getTarget()).build();
			else
				block = BlockState.builder().build(line.getDataAsView()).orElse(null);

			if (block != null)
				w.setBlock(line.getPos(), block);
			
		}
	}
	
	private void performRemoval(LookupLine line) {
		World w = Sponge.getServer().getWorld(line.getWorld()).orElse(null);
		if (w == null) return;
		
		if (line.getTarget() instanceof ItemType) {
			
			Optional<TileEntity> te = w.getTileEntity(line.getPos());
			if (te.isPresent() && te.get() instanceof TileEntityCarrier) {
				TileEntityCarrier c = (TileEntityCarrier) te.get();
				Inventory i = c.getInventory();
				
				Inventory slot = i.query(QueryOperationTypes.INVENTORY_PROPERTY.of(SlotIndex.of(line.getSlot())));
				slot.set(ItemStack.of(ItemTypes.NONE, 0));
			}
			
		} else if (line.getTarget() instanceof BlockType) {
			
			BlockState block = BlockState.builder().blockType(BlockTypes.AIR).build();
			w.setBlock(line.getPos(), block);
			
		}
	}
	
}
