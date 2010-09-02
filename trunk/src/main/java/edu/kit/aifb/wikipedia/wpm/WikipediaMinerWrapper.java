package edu.kit.aifb.wikipedia.wpm;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;
import org.wikipedia.miner.model.Wikipedia;
import org.wikipedia.miner.model.WikipediaDatabase;

public class WikipediaMinerWrapper {
	private Logger logger = Logger.getLogger( WikipediaMinerWrapper.class );
	
	String user;
	String password;
	String server;
	String database;
	
	Wikipedia wikipedia;
	WikipediaDatabase wikipediaDatabase;
	
	@Required
	public void setUser( String user ) {
		this.user = user;
	}

	@Required
	public void setPassword( String password ) {
		this.password = password;
	}

	@Required
	public void setServer( String server ) {
		this.server = server;
	}

	@Required
	public void setDatabase( String database ) {
		this.database = database;
	}
	
	public Wikipedia getWikipedia() throws Exception {
		if( wikipedia == null || !wikipediaDatabase.checkConnection() ) {
			logger.info( "Connecting to WikipediaMiner: " + database + "@" + server );
			wikipedia = new Wikipedia( server, database, user, password );
			wikipediaDatabase = wikipedia.getDatabase();
		}
		return wikipedia;
	}
	
}
