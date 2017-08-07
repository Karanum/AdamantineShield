package com.karanumcoding.adamantineshield.listeners;

import java.util.Date;

import org.slf4j.Logger;
import org.spongepowered.api.block.tileentity.carrier.Chest;
import org.spongepowered.api.block.tileentity.carrier.TileEntityCarrier;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.item.inventory.AffectSlotEvent;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.inventory.Carrier;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.ItemStackSnapshot;
import org.spongepowered.api.item.inventory.entity.PlayerInventory;
import org.spongepowered.api.item.inventory.property.SlotIndex;
import org.spongepowered.api.item.inventory.transaction.SlotTransaction;
import org.spongepowered.api.item.inventory.type.CarriedInventory;

import com.karanumcoding.adamantineshield.db.queue.InventoryQueueEntry;
import com.karanumcoding.adamantineshield.enums.ActionType;

public class DebugListeners {

	private Logger logger;
	
	public DebugListeners(Logger logger) {
		this.logger = logger;
	}
	
	@Listener
	public void onAffectSlot(AffectSlotEvent e) {
		//logger.info(e.toString());
		
		if (e.getTransactions().isEmpty()) return;
		if (!(e.getTransactions().get(0).getSlot().parent() instanceof CarriedInventory))
			return;
		
		CarriedInventory<?> c = (CarriedInventory<?>) e.getTransactions().get(0).getSlot().parent();
		
		if (!c.getCarrier().isPresent())
			return;	// <----- Double chests fail here, so there is no Carrier for some reason?
	}
	
}
