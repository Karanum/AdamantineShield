package com.karanumcoding.adamantineshield.listeners;

import java.util.Date;
import java.util.Optional;

import org.spongepowered.api.block.BlockSnapshot;
import org.spongepowered.api.block.BlockType;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.data.property.block.MatterProperty;
import org.spongepowered.api.data.property.block.MatterProperty.Matter;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.Order;
import org.spongepowered.api.event.block.ChangeBlockEvent;

import com.karanumcoding.adamantineshield.db.Database;
import com.karanumcoding.adamantineshield.db.queue.BlockQueueEntry;
import com.karanumcoding.adamantineshield.enums.ActionType;

public class NaturalChangeListener {

	private Database db;
	
	public NaturalChangeListener(Database db) {
		this.db = db;
	}
	
	@Listener(order = Order.POST)
	public void onLiquidFlow(ChangeBlockEvent.Pre e) {
		BlockSnapshot snapshot = e.getTargetWorld().createSnapshot(e.getLocations().get(0).getBlockPosition());
		Optional<MatterProperty> matter = snapshot.getState().getProperty(MatterProperty.class);
		if (matter.isPresent() && matter.get().getValue() == Matter.LIQUID) {
			String name = "Water";
			BlockType type = snapshot.getState().getType();
			if (type == BlockTypes.LAVA || type == BlockTypes.FLOWING_LAVA)
				name = "Lava";
			db.addToQueue(new BlockQueueEntry(snapshot, ActionType.FLOW, name, new Date().getTime()));
		}
	}
	
}
