package com.karanumcoding.adamantineshield.listeners;

import com.karanumcoding.adamantineshield.db.Database;
import com.karanumcoding.adamantineshield.db.queue.BlockQueueEntry;
import com.karanumcoding.adamantineshield.enums.ActionType;
import org.spongepowered.api.block.BlockSnapshot;
import org.spongepowered.api.block.BlockType;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.data.Transaction;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.Order;
import org.spongepowered.api.event.block.ChangeBlockEvent;
import org.spongepowered.api.event.filter.cause.Root;
import org.spongepowered.api.world.LocatableBlock;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class PlantGrowthListener {

    private Database db;
    private List<BlockType> acceptedBlocks;

    public PlantGrowthListener(Database db, boolean logPlantGrowth, boolean logTreeGrowth) {
        this.db = db;
        acceptedBlocks = new ArrayList<>();
        if (logPlantGrowth) {
            acceptedBlocks.add(BlockTypes.REEDS);
            acceptedBlocks.add(BlockTypes.BROWN_MUSHROOM);
            acceptedBlocks.add(BlockTypes.RED_MUSHROOM);
            acceptedBlocks.add(BlockTypes.MELON_STEM);
            acceptedBlocks.add(BlockTypes.PUMPKIN_STEM);
            acceptedBlocks.add(BlockTypes.CHORUS_FLOWER);
        }
        if (logTreeGrowth) {
            acceptedBlocks.add(BlockTypes.SAPLING);
        }
    }

    @Listener(order = Order.POST)
    public void onGrowth(ChangeBlockEvent.Place e, @Root LocatableBlock b) {
        BlockType type = b.getBlockState().getType();
        if (!acceptedBlocks.contains(type)) return;
        long time = new Date().getTime();
        for (Transaction<BlockSnapshot> transaction : e.getTransactions()) {
            db.addToQueue(new BlockQueueEntry(transaction.getFinal(), ActionType.MOB_PLACE, "block_growth", time));
        }
    }

}
