package com.project.capstone.config;
import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
@Configuration
public class DbConfig {
	
	@Bean(name="oracleDataSourceProperties")
	@Primary
    @ConfigurationProperties("spring.oracle.datasource")
    public DataSourceProperties oracleDataSourceProperties() {
        return new DataSourceProperties();
    }

	@Bean(name = "oracleDataSource", destroyMethod = "close")
	@Primary
	public DataSource oracleDataSource(){
		Map<String, Long> configMap = new HashMap<>();
		configMap.put(MAX_POOL_SIZE,15L);
		configMap.put(MINIMUM_IDLE,10L);
		return createDataSource(oracleDataSourceProperties(),configMap);
	}
		
	@Bean(name="oracleTransactionManager")
	@Autowired
	@Primary
	DataSourceTransactionManager oracleTransactionManager(@Qualifier ("oracleDataSource") DataSource datasource) {
	    return new DataSourceTransactionManager(datasource);
	}
	
	private DataSource createDataSource(DataSourceProperties dataSourceProperties, Map<String,Long> configMap) {
		
		HikariConfig config = new HikariConfig();
		config.setJdbcUrl(dataSourceProperties.getUrl());
        config.setUsername(dataSourceProperties.getUsername());
        config.setPassword(dataSourceProperties.getPassword());
        config.setDriverClassName(dataSourceProperties.getDriverClassName());
        config.setMaximumPoolSize(configMap.getOrDefault(MAX_POOL_SIZE, 10L).intValue());
        config.setMinimumIdle(configMap.getOrDefault(MINIMUM_IDLE, 5L).intValue());
        config.setIdleTimeout(configMap.getOrDefault(IDLE_TIMEOUT, 600000L));
        config.setMaxLifetime(configMap.getOrDefault(MAX_LIFE_TIME, 3600000L));
        config.setConnectionTimeout(configMap.getOrDefault(CONNECTION_TIMEOUT, 30000L));
        config.addDataSourceProperty("cachePrepStmts", "true");
        config.addDataSourceProperty("prepStmtCacheSize", "250");
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
        return new HikariDataSource(config);
	} 
	private static final String MAX_POOL_SIZE = "MAX_POOL_SIZE";
	private static final String MINIMUM_IDLE = "MINIMUM_IDLE";
	private static final String IDLE_TIMEOUT = "IDLE_TIMEOUT";
	private static final String MAX_LIFE_TIME = "MAX_LIFE_TIME";
	private static final String CONNECTION_TIMEOUT = "CONNECTION_TIMEOUT";
}
