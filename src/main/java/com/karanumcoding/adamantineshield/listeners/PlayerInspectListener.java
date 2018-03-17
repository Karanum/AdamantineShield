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

@SuppressWarnings("ConstantConditions")
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
		if (block == null || !block.getLocation().isPresent())
			return;
		
		Location<World> loc = block.getLocation().get();
		
		p.sendMessage(Text.of(TextColors.BLUE, "Querying database, please wait..."));
//		Sponge.getScheduler().createAsyncExecutor(plugin).execute(() -> {
//			plugin.getInspectManager().inspect(p, block.getWorldUniqueId(), loc.getBlockPosition()); });
		new Thread(() ->
			plugin.getInspectManager().inspect(p, block.getWorldUniqueId(), loc.getBlockPosition())
		).start();
	}
	
	@Listener
	public void onBlockSecondaryInteract(InteractBlockEvent.Secondary.MainHand e, @First Player p) {		
		if (!plugin.getInspectManager().isInspector(p))
			return;
		
		//TODO: Figure out why shearing sheep causes weird shit to happen
		
		e.setCancelled(true);
		BlockSnapshot block = e.getTargetBlock();
		if (block == null || !block.getLocation().isPresent())
			return;
		
		Location<World> loc = block.getLocation().get();
		
		p.sendMessage(Text.of(TextColors.BLUE, "Querying database, please wait..."));
		Runnable task;
		if (loc.getTileEntity().isPresent() && loc.getTileEntity().get() instanceof TileEntityCarrier) {
//			Sponge.getScheduler().createAsyncExecutor(plugin).execute(() -> {
//				plugin.getInspectManager().inspectContainer(p, block.getWorldUniqueId(), loc.getBlockPosition()); });
			task = () ->
				plugin.getInspectManager().inspectContainer(p, block.getWorldUniqueId(), loc.getBlockPosition());
		} else {
//			Sponge.getScheduler().createAsyncExecutor(plugin).execute(() -> {
//				plugin.getInspectManager().inspect(p, block.getWorldUniqueId(), loc.getBlockPosition().add(e.getTargetSide().asBlockOffset())); });
			task = () ->
				plugin.getInspectManager().inspect(p, block.getWorldUniqueId(), loc.getBlockPosition().add(e.getTargetSide().asBlockOffset()));
		}
		new Thread(task).start();
	}
	
}
