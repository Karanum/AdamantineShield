package com.karanumcoding.adamantineshield.lookup.filters;

import java.util.Iterator;
import java.util.Set;

import com.google.common.collect.Sets;
import com.karanumcoding.adamantineshield.enums.ActionType;
import com.karanumcoding.adamantineshield.enums.ChatType;
import com.karanumcoding.adamantineshield.enums.LookupType;
import com.karanumcoding.adamantineshield.lookup.LookupLine;
import com.karanumcoding.adamantineshield.lookup.chat.ChatLookupLine;

public class ActionFilter implements FilterBase {

	private Set<ActionType> actions;
	private Set<ChatType> chatActions;
	
	public ActionFilter() {
		actions = Sets.newHashSet();
		actions.add(ActionType.PLACE);
		actions.add(ActionType.DESTROY);
		actions.add(ActionType.MOB_PLACE);
		actions.add(ActionType.MOB_DESTROY);
		actions.add(ActionType.FLOW);
		chatActions = Sets.newHashSet();
	}
	
	public ActionFilter(String filter) {
		actions = Sets.newHashSet();
		chatActions = Sets.newHashSet();
		switch (filter.toLowerCase()) {
			case "block":
				actions.add(ActionType.PLACE);
				actions.add(ActionType.DESTROY);
				actions.add(ActionType.MOB_PLACE);
				actions.add(ActionType.MOB_DESTROY);
				break;
			case "+block":
			case "place":
				actions.add(ActionType.PLACE);
				actions.add(ActionType.MOB_PLACE);
				break;
			case "-block":
			case "destroy":
				actions.add(ActionType.DESTROY);
				actions.add(ActionType.MOB_DESTROY);
				break;
			case "container":
				actions.add(ActionType.CONTAINER_ADD);
				actions.add(ActionType.CONTAINER_REMOVE);
				break;
			case "+container":
			case "add":
				actions.add(ActionType.CONTAINER_ADD);
				break;
			case "-container":
			case "remove":
				actions.add(ActionType.CONTAINER_REMOVE);
				break;
			case "flow":
				actions.add(ActionType.FLOW);
				break;
			case "chat":
				chatActions.add(ChatType.CHAT);
				break;
			case "command":
				chatActions.add(ChatType.COMMAND);
				break;
			default:
				throw new IllegalArgumentException();
		}
	}
	
	public boolean isItemLookup() {
		return (actions.contains(ActionType.CONTAINER_ADD) || actions.contains(ActionType.CONTAINER_REMOVE));
	}
	
	public boolean isChatLookup() {
		return (!chatActions.isEmpty());
	}
	
	@Override
	public boolean matches(LookupLine line) {
		if (line instanceof ChatLookupLine) {
			ChatLookupLine chatLine = (ChatLookupLine) line;
			return chatActions.contains(chatLine.getChatAction());
		}
		return actions.contains(line.getAction());
	}

	@Override
	public String getQueryCondition(LookupType lookupType) {
		if (lookupType == LookupType.CHAT_LOOKUP)
			return getChatQueryCondition();
		Iterator<ActionType> iter = actions.iterator();
		String result = "(type = " + iter.next().ordinal();
		while (iter.hasNext()) {
			result += " OR type = " + iter.next().ordinal();
		}
		result += ")";
		return result;
	}
	
	private String getChatQueryCondition() {
		Iterator<ChatType> iter = chatActions.iterator();
		String result = "(type = " + iter.next().ordinal();
		while (iter.hasNext()) {
			result += " OR type = " + iter.next().ordinal();
		}
		result += ")";
		return result;
	}

}
