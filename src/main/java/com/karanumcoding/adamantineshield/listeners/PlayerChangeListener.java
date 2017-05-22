package com.karanumcoding.adamantineshield.listeners;

import java.util.Date;
import java.util.UUID;

import org.spongepowered.api.block.BlockSnapshot;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.data.Transaction;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.Order;
import org.spongepowered.api.event.block.ChangeBlockEvent;
import org.spongepowered.api.event.block.InteractBlockEvent;
import org.spongepowered.api.event.filter.cause.First;

import com.flowpowered.math.vector.Vector3i;
import com.karanumcoding.adamantineshield.ActionType;
import com.karanumcoding.adamantineshield.AdamantineShield;
import com.karanumcoding.adamantineshield.db.Database;
import com.karanumcoding.adamantineshield.db.queue.BlockQueueEntry;

public class PlayerChangeListener {
	
	private Database db;
	private AdamantineShield plugin;
	
	public PlayerChangeListener(AdamantineShield plugin, Database db) {
		this.db = db;
		this.plugin = plugin;
	}
	
	@Listener(order = Order.POST)
	public void onBlockPlace(ChangeBlockEvent.Place e, @First Player p) {
		for (Transaction<BlockSnapshot> transaction : e.getTransactions()) {
			UUID id = p.getUniqueId();
			if (transaction.getOriginal().getState().getType() != BlockTypes.AIR) {
				db.addToQueue(new BlockQueueEntry(transaction.getOriginal(), ActionType.DESTROY, id.toString(), new Date().getTime()));
			}
			db.addToQueue(new BlockQueueEntry(transaction.getFinal(), ActionType.PLACE, id.toString(), new Date().getTime()));
		}
	}
	
	@Listener(order = Order.POST)
	public void onBlockBreak(ChangeBlockEvent.Break e, @First Player p) {
		for (Transaction<BlockSnapshot> transaction : e.getTransactions()) {
			db.addToQueue(new BlockQueueEntry(transaction.getOriginal(), ActionType.DESTROY, p.getUniqueId().toString(), new Date().getTime()));
		}
	}
	
	@Listener
	public void onBlockPrimaryInteract(InteractBlockEvent.Primary.MainHand e, @First Player p) {
		if (!plugin.getInspectManager().isInspector(p))
			return;
		
		e.setCancelled(true);
		BlockSnapshot block = e.getTargetBlock();
		Vector3i blockPos = block.getPosition();
		
		plugin.getInspectManager().inspect(p, block.getWorldUniqueId(), blockPos);
	}
	
	@Listener
	public void onBlockSecondaryInteract(InteractBlockEvent.Secondary.MainHand e, @First Player p) {
		if (!plugin.getInspectManager().isInspector(p))
			return;
		
		//TODO: Figure out why shearing sheep causes weird shit to happen
		
		e.setCancelled(true);
		BlockSnapshot block = e.getTargetBlock();
		Vector3i blockPos = block.getPosition().add(e.getTargetSide().asBlockOffset());
		
		plugin.getInspectManager().inspect(p, block.getWorldUniqueId(), blockPos);
	}
	
}
