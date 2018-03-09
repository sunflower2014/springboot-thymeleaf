package com.sunflower.jdbc;

import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

public class DynamicDataSource extends AbstractRoutingDataSource {
	private static final ThreadLocal<DatabaseType> contextHolder = new ThreadLocal<DatabaseType>();
	
	public static enum DatabaseType {
		Master, Slave
	}
	
	public static void master() {
		contextHolder.set(DatabaseType.Master);
	}
	
	public static void slave() {
		contextHolder.set(DatabaseType.Slave);
	}
	
	public static void setDatabaseType(DatabaseType type) {
		contextHolder.set(type);
	}

	@Override
	protected Object determineCurrentLookupKey() {
		return contextHolder.get();
	}

}
