package com.karanumcoding.adamantineshield.listeners;

import java.util.Date;

import org.spongepowered.api.block.BlockSnapshot;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.data.Transaction;
import org.spongepowered.api.entity.living.Agent;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.Order;
import org.spongepowered.api.event.block.ChangeBlockEvent;
import org.spongepowered.api.event.filter.cause.First;

import com.karanumcoding.adamantineshield.db.Database;
import com.karanumcoding.adamantineshield.db.queue.BlockQueueEntry;
import com.karanumcoding.adamantineshield.enums.ActionType;

public class MobChangeListener {

	private Database db;
	
	public MobChangeListener(Database db) {
		this.db = db;
	}
	
	@Listener(order = Order.POST)
	public void onBlockPlace(ChangeBlockEvent.Place e, @First Agent a) {
		for (Transaction<BlockSnapshot> transaction : e.getTransactions()) {
			String type = a.getType().getName();
			if (transaction.getOriginal().getState().getType() != BlockTypes.AIR) {
				db.addToQueue(new BlockQueueEntry(transaction.getOriginal(), ActionType.MOB_DESTROY, type, new Date().getTime()));
			}
			db.addToQueue(new BlockQueueEntry(transaction.getFinal(), ActionType.MOB_PLACE, type, new Date().getTime()));
		}
	}
	
	@Listener(order = Order.POST)
	public void onBlockBreak(ChangeBlockEvent.Break e, @First Agent a) {
		for (Transaction<BlockSnapshot> transaction : e.getTransactions()) {
			db.addToQueue(new BlockQueueEntry(transaction.getOriginal(), ActionType.MOB_DESTROY, a.getType().getName(), new Date().getTime()));
		}
	}
	
}
