package edu.kit.aifb.wikipedia.sql;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;

import edu.kit.aifb.concept.IConceptDescription;
import edu.kit.aifb.nlp.Language;

public class WikipediaPageDescription implements IConceptDescription {
	private Logger logger = Logger.getLogger( WikipediaPageDescription.class );
	
	WikipediaDatabase wpDb;
	
	@Required
	public void setWikipediaDatabase( WikipediaDatabase wpDb ) {
		this.wpDb = wpDb;
	}
	
	@Override
	public String getDescription( String conceptName, Language language ) throws Exception {
		Page p = new Page( WikipediaCollection.getArticleId( conceptName ) );
		wpDb.initializePage( p );
		if( p.isInitialized() ) {
			return p.getTitle();
		}
		else {
			logger.warn( "Description for concept " + conceptName + " could not be initialized." );
			return conceptName;
		}
	}

}
