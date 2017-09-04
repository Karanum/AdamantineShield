package com.karanumcoding.adamantineshield.listeners;

import java.util.Date;
import java.util.Optional;

import org.spongepowered.api.block.tileentity.carrier.Chest;
import org.spongepowered.api.block.tileentity.carrier.TileEntityCarrier;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.cause.entity.spawn.EntitySpawnCause;
import org.spongepowered.api.event.filter.cause.First;
import org.spongepowered.api.event.item.inventory.AffectSlotEvent;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.ItemStackComparators;
import org.spongepowered.api.item.inventory.ItemStackSnapshot;
import org.spongepowered.api.item.inventory.property.SlotIndex;
import org.spongepowered.api.item.inventory.transaction.SlotTransaction;
import org.spongepowered.api.item.inventory.type.CarriedInventory;

import com.karanumcoding.adamantineshield.db.Database;
import com.karanumcoding.adamantineshield.db.queue.InventoryQueueEntry;
import com.karanumcoding.adamantineshield.enums.ActionType;
import com.karanumcoding.adamantineshield.util.ContainerAccessManager;

public class InventoryChangeListener {

	private Database db;
	private ContainerAccessManager accessMan;
	private boolean logContainers;
	
	public InventoryChangeListener(Database db, ContainerAccessManager accessMan, boolean logContainers) {
		this.db = db;
		this.accessMan = accessMan;
		this.logContainers = logContainers;
	}
	
	@Listener
	public void onInventoryTransfer(AffectSlotEvent e, @First EntitySpawnCause c) {
		Entity entity = c.getEntity();
		if (!(entity instanceof Player)) return;
		onInventoryTransfer(e, (Player) entity);
	}
	
	@Listener
	public void onInventoryTransfer(AffectSlotEvent e, @First Player p) {
		if (e.getTransactions().isEmpty()) return;
		if (!(e.getTransactions().get(0).getSlot().parent() instanceof CarriedInventory))
			return;
		
		TileEntityCarrier carrier = null;
		CarriedInventory<?> c = (CarriedInventory<?>) e.getTransactions().get(0).getSlot().parent();
		if (!c.getCarrier().isPresent()) {
			Optional<TileEntityCarrier> optCarrier = accessMan.getEntity(p.getUniqueId());
			if (optCarrier.isPresent() && accessMan.isActive(p.getUniqueId()))
				carrier = optCarrier.get();
		} else if (c.getCarrier().get() instanceof TileEntityCarrier) {
			carrier = (TileEntityCarrier) c.getCarrier().get();
		}
		
		if (carrier == null)
			return;
		
		if (!logContainers && !(carrier instanceof Chest))
			return;
		
		long timestamp = new Date().getTime();
		int containerSize = c.iterator().next().capacity();
		for (SlotTransaction transaction : e.getTransactions()) {
			int slotId = transaction.getSlot().getProperty(SlotIndex.class, "slotindex").map(SlotIndex::getValue).orElse(-1);
			if (slotId >= containerSize)
				continue;
			
			ItemStackSnapshot origItem = transaction.getOriginal();
			ItemStackSnapshot finalItem = transaction.getFinal();
			if (origItem == finalItem)
				continue;
			
			if (origItem.createGameDictionaryEntry().matches(finalItem.createStack()) &&
					ItemStackComparators.ITEM_DATA.compare(origItem.createStack(), finalItem.createStack()) == 0) {
				if (origItem.getCount() > finalItem.getCount()) {
					ItemStackSnapshot stack = ItemStack.builder().itemType(origItem.getType())
							.quantity(origItem.getCount() - finalItem.getCount())
							.build().createSnapshot();
					db.addToQueue(new InventoryQueueEntry(carrier, slotId, stack, ActionType.CONTAINER_REMOVE, p, timestamp));
				} else if (origItem.getCount() < finalItem.getCount()) {
					ItemStackSnapshot stack = ItemStack.builder().itemType(origItem.getType())
							.quantity(finalItem.getCount() - origItem.getCount())
							.build().createSnapshot();
					db.addToQueue(new InventoryQueueEntry(carrier, slotId, stack, ActionType.CONTAINER_ADD, p, timestamp));
				}
			} else {
				if (origItem.getType() != ItemTypes.NONE) {
					db.addToQueue(new InventoryQueueEntry(carrier, slotId, origItem, ActionType.CONTAINER_REMOVE, p, timestamp));
				}
				if (finalItem.getType() != ItemTypes.NONE) {
					db.addToQueue(new InventoryQueueEntry(carrier, slotId, finalItem, ActionType.CONTAINER_ADD, p, timestamp));
				}
			}
		}
	}
	
}
