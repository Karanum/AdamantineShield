package com.karanumcoding.adamantineshield.db.queue;

import java.sql.Connection;
import java.sql.SQLException;

public abstract class QueueEntry {

	public abstract void writeToConnection(Connection c) throws SQLException;
	
}
