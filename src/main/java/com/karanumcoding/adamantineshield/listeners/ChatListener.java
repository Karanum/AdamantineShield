package com.karanumcoding.adamantineshield.listeners;

import java.util.Date;

import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.command.SendCommandEvent;
import org.spongepowered.api.event.filter.cause.Root;
import org.spongepowered.api.event.message.MessageChannelEvent;

import com.karanumcoding.adamantineshield.db.Database;
import com.karanumcoding.adamantineshield.db.queue.ChatQueueEntry;
import com.karanumcoding.adamantineshield.enums.ChatType;

public class ChatListener {

	private Database db;
	private boolean logChat;
	private boolean logCommands;
	
	public ChatListener(Database db, boolean logChat, boolean logCommands) {
		this.db = db;
		this.logChat = logChat;
		this.logCommands = logCommands;
	}
	
	@Listener
	public void onChatEvent(MessageChannelEvent.Chat e, @Root Player p) {
		if (!logChat) return;
		
		db.addToQueue(new ChatQueueEntry(p, ChatType.CHAT, e.getRawMessage().toPlain(), new Date().getTime()));
	}
	
	@Listener
	public void onChatCommandEvent(SendCommandEvent e, @Root Player p) {
		if (!logCommands) return;
		
		String command = e.getCommand();
		if (!e.getArguments().isEmpty()) {
			command += " " + e.getArguments();
		}
		
		db.addToQueue(new ChatQueueEntry(p, ChatType.COMMAND, command, new Date().getTime()));
	}
	
}
