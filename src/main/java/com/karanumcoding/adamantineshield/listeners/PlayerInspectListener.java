package com.karanumcoding.adamantineshield.listeners;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.block.BlockSnapshot;
import org.spongepowered.api.block.tileentity.carrier.TileEntityCarrier;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.block.InteractBlockEvent;
import org.spongepowered.api.event.filter.cause.First;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import com.karanumcoding.adamantineshield.AdamantineShield;

public class PlayerInspectListener {

	private AdamantineShield plugin;
	
	public PlayerInspectListener(AdamantineShield plugin) {
		this.plugin = plugin;
	}
	
	@Listener
	public void onBlockPrimaryInteract(InteractBlockEvent.Primary.MainHand e, @First Player p) {
		if (!plugin.getInspectManager().isInspector(p))
			return;
		
		e.setCancelled(true);
		BlockSnapshot block = e.getTargetBlock();
		if (!block.getLocation().isPresent())
			return;
		
		Location<World> loc = block.getLocation().get();
		
		p.sendMessage(Text.of(TextColors.BLUE, "Querying database, please wait..."));
		plugin.getThreadPool().execute(() ->
				plugin.getInspectManager().inspect(p, block.getWorldUniqueId(), loc.getBlockPosition())
		);
	}
	
	@Listener
	public void onBlockSecondaryInteract(InteractBlockEvent.Secondary.MainHand e, @First Player p) {		
		if (!plugin.getInspectManager().isInspector(p))
			return;
		
		//TODO: Figure out why shearing sheep causes weird shit to happen
		
		e.setCancelled(true);
		BlockSnapshot block = e.getTargetBlock();
		if (!block.getLocation().isPresent())
			return;
		
		Location<World> loc = block.getLocation().get();
		
		p.sendMessage(Text.of(TextColors.BLUE, "Querying database, please wait..."));
		Runnable task;
		if (loc.getTileEntity().isPresent() && loc.getTileEntity().get() instanceof TileEntityCarrier) {
			task = () ->
				plugin.getInspectManager().inspectContainer(p, block.getWorldUniqueId(), loc.getBlockPosition());
		} else {
			task = () ->
				plugin.getInspectManager().inspect(p, block.getWorldUniqueId(), loc.getBlockPosition().add(e.getTargetSide().asBlockOffset()));
		}
		plugin.getThreadPool().execute(task);
	}
	
}
