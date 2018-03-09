package com.sunflower.jdbc;

import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.tomcat.jdbc.pool.PoolProperties;
import org.springframework.boot.bind.RelaxedPropertyResolver;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableTransactionManagement
public class DatabaseConfiguration implements EnvironmentAware {
	private RelaxedPropertyResolver propertyResolver1;
	private RelaxedPropertyResolver PropertyResolver2;
	
	public DatabaseConfiguration() {
		
	}
	
	public DataSource master() {
		org.apache.tomcat.jdbc.pool.DataSource datasource = new org.apache.tomcat.jdbc.pool.DataSource();
		datasource.setPoolProperties(getPoolProps(propertyResolver1));
		return datasource;
	}
	
	public DataSource slave() {
		org.apache.tomcat.jdbc.pool.DataSource datasource = new org.apache.tomcat.jdbc.pool.DataSource();
		datasource.setPoolProperties(getPoolProps(PropertyResolver2));
		return datasource;
	}
	
	private PoolProperties getPoolProps(RelaxedPropertyResolver propertyResolver) {
		PoolProperties p = new PoolProperties();
		p.setUrl(propertyResolver.getProperty("url"));
		p.setDriverClassName(propertyResolver.getProperty("driver-class-name"));
		p.setUsername(propertyResolver.getProperty("username"));
		p.setPassword(propertyResolver.getProperty("password"));
		p.setInitialSize(Integer.valueOf(propertyResolver.getProperty("initial-size")));
		p.setMinIdle(Integer.valueOf(propertyResolver.getProperty("min-idle")));
		p.setMaxWait(Integer.valueOf(propertyResolver.getProperty("max-wait")));
		p.setMaxActive(Integer.valueOf(propertyResolver.getProperty("max-active")));
		p.setMinEvictableIdleTimeMillis(Integer.valueOf(propertyResolver.getProperty("min-evictable-idle-time-millis")));
		return p;
	}

	@Override
	public void setEnvironment(Environment env) {
		this.propertyResolver1 = new RelaxedPropertyResolver(env, "spring.master.");
		this.PropertyResolver2 = new RelaxedPropertyResolver(env, "spring.slave.");
	}
	
	@Bean
	public DynamicDataSource dynamicDataSource() {
		DataSource master = master();
		DataSource slave = slave();
		Map<Object, Object> targetDataSources = new HashMap<Object, Object>();
		targetDataSources.put(DynamicDataSource.DatabaseType.Master, master);
		targetDataSources.put(DynamicDataSource.DatabaseType.Slave, slave);
		
		DynamicDataSource dynamicDataSource = new DynamicDataSource();
		dynamicDataSource.setTargetDataSources(targetDataSources);
		dynamicDataSource.setDefaultTargetDataSource(master);
		return dynamicDataSource;
	}

}
