package com.karanumcoding.adamantineshield.db;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.TimeUnit;

import javax.sql.DataSource;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.service.sql.SqlService;

import com.karanumcoding.adamantineshield.AdamantineShield;
import com.karanumcoding.adamantineshield.db.cache.CauseCache;
import com.karanumcoding.adamantineshield.db.cache.WorldCache;
import com.karanumcoding.adamantineshield.db.queue.QueueEntry;

public class Database {

	private SqlService service;
	private DataSource source;
	
	private ConcurrentLinkedQueue<QueueEntry> queue;
	private Task task;
	
	public static final WorldCache worldCache = new WorldCache();
	public static final CauseCache causeCache = new CauseCache();
	
	public Database(AdamantineShield plugin, String jdbc) throws SQLException {
		queue = new ConcurrentLinkedQueue<>();
		
		service = Sponge.getServiceManager().provide(SqlService.class).get();
		source = service.getDataSource(jdbc);
		prepareTables();
		plugin.getLogger().info("AS database loaded successfully");
		
		task = Task.builder().async()
				.interval(1, TimeUnit.SECONDS)
				.execute(new DatabaseWriterTask(this))
				.submit(plugin);
	}
	
	public void stop() {
		if (task == null) return;
		task.cancel();
	}
	
	public Connection getConnection() {
		try {
			return source.getConnection();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public void addToQueue(QueueEntry entry) {
		queue.add(entry);
	}
	
	public ConcurrentLinkedQueue<QueueEntry> getQueue() {
		return queue;
	}
	
	public boolean purgeEntries(long before) {
		try {
			Connection c = source.getConnection();
			c.createStatement().executeUpdate("DELETE FROM AS_Block WHERE time < " + before + ";");
			c.close();
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	private void prepareTables() throws SQLException {
		Connection c = source.getConnection();
		
		c.createStatement().executeUpdate("CREATE TABLE IF NOT EXISTS AS_World ("
				+ "id INT AUTO_INCREMENT NOT NULL, world TEXT NOT NULL, "
				+ "PRIMARY KEY (id));");
		
		c.createStatement().executeUpdate("CREATE TABLE IF NOT EXISTS AS_Cause ("
				+ "id INT AUTO_INCREMENT NOT NULL, cause TEXT NOT NULL, "
				+ "PRIMARY KEY (id));");
		
		c.createStatement().executeUpdate("CREATE TABLE IF NOT EXISTS AS_Container ("
				+ "x INT, y INT, z INT, world INT, type TINYINT, slot INT, "
				+ "cause INT, item TEXT, count TINYINT, data TEXT, time BIGINT, "
				+ "FOREIGN KEY (world) REFERENCES AS_World(id), "
				+ "FOREIGN KEY (cause) REFERENCES AS_Cause(id));");
		
		c.createStatement().executeUpdate("CREATE TABLE IF NOT EXISTS AS_Block ("
				+ "x INT, y INT, z INT, world INT, type TINYINT, "
				+ "cause INT, block TEXT, data TEXT, time BIGINT, "
				+ "FOREIGN KEY (world) REFERENCES AS_World(id), "
				+ "FOREIGN KEY (cause) REFERENCES AS_Cause(id));");
		
		c.createStatement().executeUpdate("DROP TABLE IF EXISTS AS_Meta;");
		c.createStatement().executeUpdate("CREATE TABLE AS_Meta (version_id SMALLINT);");
		c.createStatement().executeUpdate("INSERT INTO AS_Meta VALUES (1);");
		c.close();
	}
	
}
