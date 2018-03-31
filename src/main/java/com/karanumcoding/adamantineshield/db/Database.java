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
import com.karanumcoding.adamantineshield.compat.DatabaseUpdater;
import com.karanumcoding.adamantineshield.db.queue.QueueEntry;

public class Database {

	private AdamantineShield plugin;
	private SqlService service;
	private DataSource source;
	
	private ConcurrentLinkedQueue<QueueEntry> queue;
	private Task task;
	
	public static final int DB_VERSION = 4;
	
	public static final DataCache worldCache = new DataCache("AS_World", "world");
	public static final DataCache causeCache = new DataCache("AS_Cause", "cause");
	public static final DataCache idCache = new DataCache("AS_Id", "value");
	
	public Database(AdamantineShield plugin, String jdbc) throws SQLException {
		this.plugin = plugin;		
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
			c.createStatement().executeUpdate("DELETE FROM AS_Container WHERE time < " + before + ";");
			c.close();
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	private void prepareTables() throws SQLException {
		Connection c = source.getConnection();
		int version = DatabaseUpdater.checkVersion(c);
		
		c.createStatement().executeUpdate("CREATE TABLE IF NOT EXISTS AS_World ("
				+ "id INT AUTO_INCREMENT NOT NULL, world TEXT NOT NULL, "
				+ "PRIMARY KEY (id));");
		
		c.createStatement().executeUpdate("CREATE TABLE IF NOT EXISTS AS_Cause ("
				+ "id INT AUTO_INCREMENT NOT NULL, cause TEXT NOT NULL, "
				+ "PRIMARY KEY (id));");
		
		c.createStatement().executeUpdate("CREATE TABLE IF NOT EXISTS AS_Id ("
				+ "id INT AUTO_INCREMENT NOT NULL, value TEXT NOT NULL, "
				+ "PRIMARY KEY (id));");
		
		c.createStatement().executeUpdate("CREATE TABLE IF NOT EXISTS AS_Container ("
				+ "x INT, y INT, z INT, world INT, type TINYINT, slot INT, "
				+ "cause INT, id INT, count TINYINT, data TEXT, time BIGINT, "
				+ "multiblock INT, rolled_back TINYINT(1) DEFAULT 0, "
				+ "FOREIGN KEY (world) REFERENCES AS_World(id), "
				+ "FOREIGN KEY (cause) REFERENCES AS_Cause(id), "
				+ "FOREIGN KEY (id) REFERENCES AS_Id(id));");
		
		c.createStatement().executeUpdate("CREATE TABLE IF NOT EXISTS AS_Block ("
				+ "x INT, y INT, z INT, world INT, type TINYINT, "
				+ "cause INT, id INT, data TEXT, time BIGINT, "
				+ "rolled_back TINYINT(1) DEFAULT 0, "
				+ "FOREIGN KEY (world) REFERENCES AS_World(id), "
				+ "FOREIGN KEY (cause) REFERENCES AS_Cause(id), "
				+ "FOREIGN KEY (id) REFERENCES AS_Id(id));");
		
		if (version > 0 && version < DB_VERSION) {
			plugin.getLogger().info("Updating database from version " + version + " to version " + DB_VERSION);
			DatabaseUpdater.updateDatabase(plugin, c, version);
			plugin.getLogger().info("Database has been updated successfully!");
		}
		
		c.createStatement().executeUpdate("DROP TABLE IF EXISTS AS_Meta;");
		c.createStatement().executeUpdate("CREATE TABLE AS_Meta (version_id SMALLINT);");
		c.createStatement().executeUpdate("INSERT INTO AS_Meta VALUES (" + DB_VERSION + ");");
		c.close();
	}
	
}
