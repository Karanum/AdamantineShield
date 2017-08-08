package com.karanumcoding.adamantineshield.listeners;

import java.util.Date;

import org.spongepowered.api.block.BlockSnapshot;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.data.Transaction;
import org.spongepowered.api.entity.living.Agent;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.Order;
import org.spongepowered.api.event.block.ChangeBlockEvent;
import org.spongepowered.api.event.filter.cause.Root;

import com.karanumcoding.adamantineshield.db.Database;
import com.karanumcoding.adamantineshield.db.queue.BlockQueueEntry;
import com.karanumcoding.adamantineshield.enums.ActionType;

public class MobBlockChangeListener {

	private Database db;
	
	public MobBlockChangeListener(Database db) {
		this.db = db;
	}
	
	@Listener(order = Order.POST)
	public void onBlockPlace(ChangeBlockEvent.Place e, @Root Agent a) {
		if (a instanceof Player) return;
		long time = new Date().getTime();
		for (Transaction<BlockSnapshot> transaction : e.getTransactions()) {
			String type = a.getType().getName();
			if (transaction.getOriginal().getState().getType() != BlockTypes.AIR) {
				db.addToQueue(new BlockQueueEntry(transaction.getOriginal(), ActionType.MOB_DESTROY, type, time));
			}
			db.addToQueue(new BlockQueueEntry(transaction.getFinal(), ActionType.MOB_PLACE, type, time));
		}
	}
	
	@Listener(order = Order.POST)
	public void onBlockBreak(ChangeBlockEvent.Break e, @Root Agent a) {
		if (a instanceof Player) return;
		long time = new Date().getTime();
		for (Transaction<BlockSnapshot> transaction : e.getTransactions()) {
			db.addToQueue(new BlockQueueEntry(transaction.getOriginal(), ActionType.MOB_DESTROY, a.getType().getName(), time));
		}
	}
	
}
