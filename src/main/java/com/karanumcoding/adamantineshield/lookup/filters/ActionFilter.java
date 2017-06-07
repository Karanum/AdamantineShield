package com.karanumcoding.adamantineshield.lookup.filters;

import java.util.Iterator;
import java.util.Set;

import com.google.common.collect.Sets;
import com.karanumcoding.adamantineshield.enums.ActionType;
import com.karanumcoding.adamantineshield.enums.LookupType;
import com.karanumcoding.adamantineshield.lookup.LookupLine;

public class ActionFilter implements FilterBase {

	private Set<ActionType> actions;
	
	public ActionFilter() {
		actions = Sets.newHashSet();
		actions.add(ActionType.PLACE);
		actions.add(ActionType.DESTROY);
		actions.add(ActionType.MOB_PLACE);
		actions.add(ActionType.MOB_DESTROY);
		actions.add(ActionType.FLOW);
	}
	
	public ActionFilter(String filter) {
		actions = Sets.newHashSet();
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
			default:
				throw new IllegalArgumentException();
		}
	}
	
	public boolean isItemLookup() {
		return (actions.contains(ActionType.CONTAINER_ADD) || actions.contains(ActionType.CONTAINER_REMOVE));
	}
	
	@Override
	public boolean matches(LookupLine line) {
		return actions.contains(line.getAction());
	}

	@Override
	public String getQueryCondition(LookupType lookupType) {
		Iterator<ActionType> iter = actions.iterator();
		String result = "(type = " + iter.next().ordinal();
		while (iter.hasNext()) {
			result += " OR type = " + iter.next().ordinal();
		}
		result += ")";
		return result;
	}

}
