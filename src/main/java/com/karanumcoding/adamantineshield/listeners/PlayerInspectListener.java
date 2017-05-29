package com.karanumcoding.adamantineshield.listeners;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.block.BlockSnapshot;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.block.InteractBlockEvent;
import org.spongepowered.api.event.filter.cause.First;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import com.flowpowered.math.vector.Vector3i;
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
		Vector3i blockPos = block.getPosition();
		
		p.sendMessage(Text.of(TextColors.BLUE, "Querying database, please wait..."));
		Sponge.getScheduler().createAsyncExecutor(plugin).execute(() -> {
				plugin.getInspectManager().inspect(p, block.getWorldUniqueId(), blockPos); });
	}
	
	@Listener
	public void onBlockSecondaryInteract(InteractBlockEvent.Secondary.MainHand e, @First Player p) {
		if (!plugin.getInspectManager().isInspector(p))
			return;
		
		//TODO: Figure out why shearing sheep causes weird shit to happen
		
		e.setCancelled(true);
		BlockSnapshot block = e.getTargetBlock();
		Vector3i blockPos = block.getPosition().add(e.getTargetSide().asBlockOffset());
		
		p.sendMessage(Text.of(TextColors.BLUE, "Querying database, please wait..."));
		Sponge.getScheduler().createAsyncExecutor(plugin).execute(() -> {
				plugin.getInspectManager().inspect(p, block.getWorldUniqueId(), blockPos); });
	}
	
}
