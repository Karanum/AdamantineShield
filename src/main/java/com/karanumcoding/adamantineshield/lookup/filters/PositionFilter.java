package com.karanumcoding.adamantineshield.lookup.filters;

import com.flowpowered.math.vector.Vector3i;
import com.karanumcoding.adamantineshield.enums.LookupType;
import com.karanumcoding.adamantineshield.lookup.LookupLine;

public class PositionFilter implements FilterBase {

	private Vector3i pos;
	private int radius;
	private boolean isInitialValue;
	
	public PositionFilter(Vector3i pos, int radius) {
		this.pos = pos;
		this.radius = radius;
		isInitialValue = true;
	}
	
	public void setRadius(int radius) {
		this.radius = radius;
		isInitialValue = false;
	}
	
	@Override
	public boolean matches(LookupLine line) {
		if (line.getX() < pos.getX() - radius || line.getX() > pos.getX() + radius)
			return false;
		if (line.getZ() < pos.getZ() - radius || line.getZ() > pos.getZ() + radius)
			return false;
		return true;
	}

	@Override
	public String getQueryCondition(LookupType lookupType) {
		if (lookupType == LookupType.CHAT_LOOKUP && isInitialValue)
			return "";
		
		if (radius == 0)
			return "x = " + pos.getX() + " AND z = " + pos.getZ();
		return String.format("x >= %d AND x <= %d AND z >= %d AND z <= %d",
				pos.getX() - radius, pos.getX() + radius, pos.getZ() - radius, pos.getZ() + radius);
	}

}
