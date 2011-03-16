package edu.kit.aifb.wikipedia.wpm;

import java.sql.Connection;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;
import org.wikipedia.miner.model.Wikipedia;

import edu.kit.aifb.JdbcFactory;

public class WikipediaMinerWrapper {
	private Logger logger = Logger.getLogger( WikipediaMinerWrapper.class );
	
	JdbcFactory jdbcFactory;
	String database;
	
	Wikipedia wikipedia;
	Connection connection;
	
	@Required
	public void setJdbcFactory( JdbcFactory jdbcFactory ) {
		this.jdbcFactory = jdbcFactory;
	}

	@Required
	public void setDatabase( String database ) {
		this.database = database;
	}
	
	public Wikipedia getWikipedia() throws Exception {
		if( wikipedia == null || connection == null || connection.isClosed() ) {
			logger.info( "Connecting to WikipediaMiner: " + database );
			connection = jdbcFactory.getConnection();
			wikipedia = new Wikipedia( database, connection );
		}
		return wikipedia;
	}
	
}
