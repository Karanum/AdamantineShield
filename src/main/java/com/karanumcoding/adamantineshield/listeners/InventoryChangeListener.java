package com.karanumcoding.adamantineshield.listeners;

import java.util.Date;

import org.spongepowered.api.block.tileentity.carrier.Chest;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.filter.cause.First;
import org.spongepowered.api.event.item.inventory.AffectSlotEvent;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.inventory.BlockCarrier;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.ItemStackComparators;
import org.spongepowered.api.item.inventory.ItemStackSnapshot;
import org.spongepowered.api.item.inventory.property.SlotIndex;
import org.spongepowered.api.item.inventory.transaction.SlotTransaction;
import org.spongepowered.api.item.inventory.type.CarriedInventory;

import com.karanumcoding.adamantineshield.db.Database;
import com.karanumcoding.adamantineshield.db.queue.InventoryQueueEntry;
import com.karanumcoding.adamantineshield.enums.ActionType;

public class InventoryChangeListener {

	private Database db;
	private boolean logContainers;
	
	public InventoryChangeListener(Database db, boolean logContainers) {
		this.db = db;
		this.logContainers = logContainers;
	}
	
	@Listener
	public void onInventoryTransfer(AffectSlotEvent e, @First Player p) {		
		if (e.getTransactions().isEmpty()) return;
		if (!(e.getTransactions().get(0).getSlot().parent() instanceof CarriedInventory))
			return;
		
		BlockCarrier carrier = null;
		CarriedInventory<?> c = (CarriedInventory<?>) e.getTransactions().get(0).getSlot().parent();
		if (c.getCarrier().get() instanceof BlockCarrier) {
			carrier = (BlockCarrier) c.getCarrier().get();
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
				if (origItem.getQuantity() > finalItem.getQuantity()) {
					ItemStackSnapshot stack = ItemStack.builder().itemType(origItem.getType())
							.quantity(origItem.getQuantity() - finalItem.getQuantity())
							.build().createSnapshot();
					db.addToQueue(new InventoryQueueEntry(carrier, slotId, stack, ActionType.CONTAINER_REMOVE, p, timestamp));
				} else if (origItem.getQuantity() < finalItem.getQuantity()) {
					ItemStackSnapshot stack = ItemStack.builder().itemType(origItem.getType())
							.quantity(finalItem.getQuantity() - origItem.getQuantity())
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
