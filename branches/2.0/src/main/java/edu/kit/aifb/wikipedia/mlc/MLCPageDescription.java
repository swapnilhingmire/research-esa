package edu.kit.aifb.wikipedia.mlc;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;

import edu.kit.aifb.concept.IConceptDescription;
import edu.kit.aifb.nlp.Language;
import edu.kit.aifb.wikipedia.sql.Page;
import edu.kit.aifb.wikipedia.sql.WikipediaDatabase;
import gnu.trove.TIntArrayList;

public class MLCPageDescription implements IConceptDescription {
	private Logger logger = Logger.getLogger( MLCPageDescription.class );
	
	MLCDatabase mlcDb;
	Map<Language,WikipediaDatabase> wpDbMap;
	
	public MLCPageDescription() {
		wpDbMap = new HashMap<Language, WikipediaDatabase>();
	}
	
	@Required
	public void setMlcDatabase( MLCDatabase mlcDb ) {
		this.mlcDb = mlcDb;
	}
	
	@Required
	public void setWikipediaDatabase( Collection<WikipediaDatabase> wpDatabases ) {
		for( WikipediaDatabase wpDb : wpDatabases ) {
			wpDbMap.put( wpDb.getLanguage(), wpDb );
		}
	}
	
	@Override
	public String getDescription( String conceptName, Language language ) throws Exception {
		if( !wpDbMap.containsKey( language ) ) {
			throw new Exception( "Concept description in language " + language + " is not supported." );
		}
		WikipediaDatabase wpDb = wpDbMap.get( language );

		int conceptId = mlcDb.getConceptId( conceptName );
		TIntArrayList articleIds = mlcDb.getPageIds( conceptId, language ); 
		
		StringBuilder sb = new StringBuilder();
		sb.append( '[' );
		for( int i=0; i<articleIds.size(); i++ ) {
			Page p = new Page( articleIds.get( i ) );
			wpDb.initializePage( p );
			if( p.isInitialized() ) {
				sb.append( '"' ).append( p.getTitle() ).append( '"' );
				if( i<articleIds.size()-1 ) {
					sb.append( ", " );
				}
			}
		}
		sb.append( ']' );
		
		logger.info( sb.toString() );
		return sb.toString();
	}

}
