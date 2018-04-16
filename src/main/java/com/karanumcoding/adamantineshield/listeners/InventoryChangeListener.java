package com.karanumcoding.adamantineshield.listeners;

import java.util.Date;

import org.spongepowered.api.block.tileentity.carrier.Chest;
import org.spongepowered.api.block.tileentity.carrier.Hopper;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.filter.cause.First;
import org.spongepowered.api.event.item.inventory.AffectSlotEvent;
import org.spongepowered.api.event.item.inventory.ChangeInventoryEvent;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.inventory.BlockCarrier;
import org.spongepowered.api.item.inventory.Inventory;
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
	public void onTransferPost(ChangeInventoryEvent.Transfer.Post e) {
		if (e.getTransactions().isEmpty()) return;
		if (!(e.getSourceInventory() instanceof CarriedInventory) || !(e.getTargetInventory() instanceof CarriedInventory)) {
			System.out.println("Either one of the inventories wasn't a Carrier, you nasty Bidoof");
			return;
		}
		
		String cause = null;
		if (e.getCause().containsType(Hopper.class)) {
			cause = e.getCause().first(Hopper.class).get().getType().getName();
		}
		
		if (cause == null)
			return;
		
		long timestamp = new Date().getTime();
		for (SlotTransaction transaction : e.getTransactions()) {
			Inventory root = transaction.getSlot().root();
			if (!(root instanceof CarriedInventory))
				continue;
			
			CarriedInventory<?> carriedRoot = (CarriedInventory<?>) root;
			if (!carriedRoot.getCarrier().isPresent() || !(carriedRoot.getCarrier().get() instanceof BlockCarrier))
				continue;
			BlockCarrier carrier = (BlockCarrier) carriedRoot.getCarrier().get();
			
			int slotId = transaction.getSlot().getProperty(SlotIndex.class, "slotindex").map(SlotIndex::getValue).orElse(-1);
			if (slotId >= root.capacity())
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
					db.addToQueue(new InventoryQueueEntry(carrier, slotId, stack, ActionType.ENTITY_CONTAINER_REMOVE, cause, timestamp));
				} else if (origItem.getQuantity() < finalItem.getQuantity()) {
					ItemStackSnapshot stack = ItemStack.builder().itemType(origItem.getType())
							.quantity(finalItem.getQuantity() - origItem.getQuantity())
							.build().createSnapshot();
					db.addToQueue(new InventoryQueueEntry(carrier, slotId, stack, ActionType.ENTITY_CONTAINER_ADD, cause, timestamp));
				}
			} else {
				if (origItem.getType() != ItemTypes.NONE) {
					db.addToQueue(new InventoryQueueEntry(carrier, slotId, origItem, ActionType.ENTITY_CONTAINER_REMOVE, cause, timestamp));
				}
				if (finalItem.getType() != ItemTypes.NONE) {
					db.addToQueue(new InventoryQueueEntry(carrier, slotId, finalItem, ActionType.ENTITY_CONTAINER_ADD, cause, timestamp));
				}
			}
		}
	}
	
	@Listener
	public void onAffectSlot(AffectSlotEvent e, @First Player p) {		
		if (e.getTransactions().isEmpty()) return;
		if (!(e.getTransactions().get(0).getSlot().parent() instanceof CarriedInventory))
			return;
		
		BlockCarrier carrier = null;
		CarriedInventory<?> c = (CarriedInventory<?>) e.getTransactions().get(0).getSlot().parent();
		if (!c.getCarrier().isPresent())
			return;
		
		if (c.getCarrier().get() instanceof BlockCarrier) {
			carrier = (BlockCarrier) c.getCarrier().get();
		}
		
		// Still needed for catching non-tile-entity carriers (MINECART CHESTS I'M LOOKING AT YOU)
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
