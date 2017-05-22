package com.karanumcoding.adamantineshield.db;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.concurrent.ConcurrentLinkedQueue;

import com.karanumcoding.adamantineshield.db.queue.QueueEntry;

public class DatabaseWriterTask implements Runnable {

	private Database database;
	
	public DatabaseWriterTask(Database database) {
		this.database = database;
	}
	
	@Override
	public void run() {
		Connection c = database.getConnection();
		if (c == null) return;
		
		try {
			ConcurrentLinkedQueue<QueueEntry> queue = database.getQueue();
			for (int i = 0; i < 20; ++i) {
				QueueEntry entry = queue.poll();
				if (entry == null) break;
				
				entry.writeToConnection(c);
			}
			c.commit();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		try {
			c.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

}
