/*
* Copyright (c) the original authors
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
* http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
package com.stardog.ext.spring;

import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.complexible.stardog.StardogException;
import com.complexible.stardog.api.Connection;
import com.complexible.stardog.api.ConnectionConfiguration;
import com.complexible.stardog.api.ConnectionPool;
import com.complexible.stardog.api.ConnectionPoolConfig;

/**
 * DataSource
 * 
 * Similar to javax.sql.DataSource
 * 
 * Also will serve as a wrapper to weave in Spring transaction support 
 * and other Spring capabilities outside of the core Stardog connection API
 * 
 * @author Clark and Parsia, LLC
 * @author Al Baker
 *
 */
public class DataSource {

	final Logger log = LoggerFactory.getLogger(DataSource.class);
	
	private ConnectionPool pool;
	
	private ConnectionConfiguration connectionConfig;
	
	private ConnectionPoolConfig poolConfig;
	
	public DataSource() { }
	
	public DataSource(ConnectionConfiguration configuration) { 
		connectionConfig = configuration;
		poolConfig = ConnectionPoolConfig.using(configuration);
	}
	
	public DataSource(ConnectionConfiguration configuration, ConnectionPoolConfig poolConfiguration) { 
		connectionConfig = configuration;
		poolConfig = poolConfiguration;
	}
	
	public void afterPropertiesSet() { 
		log.debug("Creating Stardog connection pool");
		if (poolConfig == null) {
			poolConfig = ConnectionPoolConfig.using(connectionConfig);
		}
		pool = poolConfig.create();
	}

	public void setConnectionReasoning(boolean reasoningType) {
		this.releaseConnection(getConnection());
		this.destroyPool();
		connectionConfig.reasoning(reasoningType);
		poolConfig = ConnectionPoolConfig.using(connectionConfig);
		pool = poolConfig.create();
	}
	
	/**
	 * <code>getConnection</code>
	 * Similar to javax.sql.DataSource
	 * 
	 * Also serves as a place to weave in Spring transaction support
	 * 
	 * @return Stardog Connection
	 */
	public Connection getConnection() { 
		try {
			if (pool == null) {
				log.error("Stardog pool is null");
				throw new RuntimeException("Stardog pool is null");
			}
//			afterPropertiesSet();
			return pool.obtain();
		} catch (StardogException e) {
			log.error("Error obtaining connection from Stardog pool", e);
			throw new RuntimeException(e);
		}
	}

	/**
	 * <code>releaseConnection</code>
	 * @param connection Stardog Connection
	 */
	public void releaseConnection(Connection connection) {
		try {
			if (pool != null)
				pool.release(connection);
		} catch (StardogException e) {
			log.error("Error releasing connection from Stardog pool", e);
			throw new RuntimeException(e);
		}
	}

	public void destroyPool() {
		try {
			if (pool != null)
				pool.shutdown();
		} catch (StardogException e) {
			log.error("Error shutting down Stardog pool", e);
		}
		pool = null;
	}

	/**
	 * <code>destroy</code>
	 * Called by Spring 
	 */
	public void destroy() {
		this.destroyPool();
		poolConfig = null;
		connectionConfig = null;
	}
	
}
