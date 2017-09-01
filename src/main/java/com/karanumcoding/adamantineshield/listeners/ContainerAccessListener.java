package com.karanumcoding.adamantineshield.listeners;

import java.util.Optional;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.block.BlockSnapshot;
import org.spongepowered.api.block.tileentity.TileEntity;
import org.spongepowered.api.block.tileentity.carrier.TileEntityCarrier;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.block.InteractBlockEvent;
import org.spongepowered.api.event.filter.cause.First;
import org.spongepowered.api.event.item.inventory.InteractInventoryEvent;
import org.spongepowered.api.event.network.ClientConnectionEvent;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import com.karanumcoding.adamantineshield.util.ContainerAccessManager;

public class ContainerAccessListener {
	
	private ContainerAccessManager manager;
	
	public ContainerAccessListener(ContainerAccessManager manager) {
		this.manager = manager;
	}
	
	@Listener
	public void onBlockSecondaryInteract(InteractBlockEvent.Secondary.MainHand e, @First Player p) {
		BlockSnapshot target = e.getTargetBlock();
		Location<World> loc = target.getLocation().orElse(null);
		if (loc == null)
			return;
		
		Optional<TileEntity> entity = loc.getExtent().getTileEntity(loc.getBlockPosition());
		if (!entity.isPresent() || !(entity.get() instanceof TileEntityCarrier))
			return;

		manager.addPlayer(p.getUniqueId(), Sponge.getServer().getRunningTimeTicks(), (TileEntityCarrier) entity.get());
	}
	
	@Listener
	public void onOpenInventory(InteractInventoryEvent.Open e, @First Player p) {
		if (manager.matchesTick(p.getUniqueId(), Sponge.getServer().getRunningTimeTicks())) {
			manager.setActive(p.getUniqueId());
		}
	}
	
	@Listener
	public void onCloseInventory(InteractInventoryEvent.Close e, @First Player p) {
		manager.removePlayer(p.getUniqueId());
	}
	
	@Listener
	public void onPlayerLeave(ClientConnectionEvent.Disconnect e) {
		manager.removePlayer(e.getTargetEntity().getUniqueId());
	}

}
