package com.karanumcoding.adamantineshield.lookup.chat;

import java.util.UUID;

import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import com.flowpowered.math.vector.Vector3i;
import com.karanumcoding.adamantineshield.enums.ChatType;
import com.karanumcoding.adamantineshield.lookup.LookupLine;
import com.karanumcoding.adamantineshield.util.PlayerUtils;

public class ChatLookupLine extends LookupLine {
	
	private ChatType type;
	private String text;
	
	public ChatLookupLine(Vector3i pos, UUID world, ChatType type, String cause, String text, long timestamp) {
		super(pos, world, null, cause, null, null, 0, 0, false, timestamp);
		this.type = type;
		this.text = text;
	}
	
	public ChatType getChatAction() {
		return type;
	}
	
	@Override
	public Text getHoverText() {
		return Text.of(TextColors.DARK_AQUA, "Location: ", TextColors.AQUA, getPos().toString());
	}
	
	@Override
	public String toString() {
		String result = PlayerUtils.getName(getPlayerCause());
		result += (type == ChatType.COMMAND ? " used /" : ": ");
		result += text;
		return result;
	}

}
