package com.karanumcoding.adamantineshield.listeners;

import java.util.Date;
import java.util.List;

import org.spongepowered.api.block.BlockSnapshot;
import org.spongepowered.api.block.BlockType;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.data.Transaction;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.living.Agent;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.Order;
import org.spongepowered.api.event.block.ChangeBlockEvent;
import org.spongepowered.api.event.data.ChangeDataHolderEvent;
import org.spongepowered.api.event.filter.cause.Root;
import org.spongepowered.api.event.item.inventory.AffectItemStackEvent;
import org.spongepowered.api.event.item.inventory.ChangeInventoryEvent;
import org.spongepowered.api.world.explosion.Explosion;

import com.google.common.collect.Lists;
import com.karanumcoding.adamantineshield.db.Database;
import com.karanumcoding.adamantineshield.db.queue.BlockQueueEntry;
import com.karanumcoding.adamantineshield.enums.ActionType;

public class EntityBlockChangeListener {

	private Database db;
	private List<BlockType> technicalBlocks;
	
	public EntityBlockChangeListener(Database db) {
		this.db = db;
		technicalBlocks = Lists.newArrayList();
		technicalBlocks.add(BlockTypes.PISTON_EXTENSION);
		technicalBlocks.add(BlockTypes.PISTON_HEAD);
	}
	
//	@Listener(order = Order.POST)
//	public void onTileEntityBlockPlace(ChangeBlockEvent.Place e, @Root TileEntity te) {
//		System.out.println("PLACE EVENT:");
//		long time = new Date().getTime();
//		for (Transaction<BlockSnapshot> transaction : e.getTransactions()) {
//			BlockType orig = transaction.getOriginal().getState().getType();
//			BlockType result = transaction.getFinal().getState().getType();
//			if (orig != BlockTypes.AIR && !technicalBlocks.contains(orig)) {
//				System.out.println(transaction);
//				//db.addToQueue(new BlockQueueEntry(transaction.getOriginal(), ActionType.MOB_DESTROY, te.getType().getName(), time));
//			}
//			if (!technicalBlocks.contains(result))
//				System.out.println(transaction);
//				//db.addToQueue(new BlockQueueEntry(transaction.getFinal(), ActionType.MOB_PLACE, te.getType().getName(), time));
//		}
//	}
//	
//	@Listener(order = Order.POST)
//	public void onTileEntityBlockBreak(ChangeBlockEvent.Break e, @Root TileEntity te) {
//		System.out.println("BREAK EVENT:");
//		long time = new Date().getTime();
//		for (Transaction<BlockSnapshot> transaction : e.getTransactions()) {
//			if (technicalBlocks.contains(transaction.getOriginal().getState().getType()))
//				return;
//			System.out.println(transaction);
//			//db.addToQueue(new BlockQueueEntry(transaction.getOriginal(), ActionType.MOB_PLACE, te.getType().getName(), time));
//		}
//	}
	
	@Listener(order = Order.POST)
	public void onBlockPlace(ChangeBlockEvent.Place e, @Root Entity ent) {
		if (ent instanceof Player || ent instanceof Agent) return;
		long time = new Date().getTime();
		for (Transaction<BlockSnapshot> transaction : e.getTransactions()) {
			String name = ent.getType().getName();
			if (transaction.getOriginal().getState().getType() != BlockTypes.AIR) {
				db.addToQueue(new BlockQueueEntry(transaction.getOriginal(), ActionType.MOB_DESTROY, name, time));
			}
			db.addToQueue(new BlockQueueEntry(transaction.getFinal(), ActionType.MOB_PLACE, name, time));
		}
	}
	
	@Listener(order = Order.POST)
	public void onBlockBreak(ChangeBlockEvent.Break e, @Root Entity ent) {
		if (ent instanceof Player || ent instanceof Agent) return;
		long time = new Date().getTime();
		for (Transaction<BlockSnapshot> transaction : e.getTransactions()) {
			db.addToQueue(new BlockQueueEntry(transaction.getOriginal(), ActionType.MOB_DESTROY, ent.getType().getName(), time));
		}
	}
	
	@Listener
	public void onBlockBreak(ChangeBlockEvent.Break e, @Root Explosion expl) {
		if (!expl.getSourceExplosive().isPresent()) return;
		String source = expl.getSourceExplosive().get().getType().getName();
		long time = new Date().getTime();
		for (Transaction<BlockSnapshot> transaction : e.getTransactions()) {
			db.addToQueue(new BlockQueueEntry(transaction.getOriginal(), ActionType.MOB_DESTROY, source, time));
		}
	}
	
}
