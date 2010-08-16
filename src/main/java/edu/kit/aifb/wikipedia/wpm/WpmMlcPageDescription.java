package edu.kit.aifb.wikipedia.wpm;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;
import org.wikipedia.miner.model.Page;
import org.wikipedia.miner.model.Wikipedia;

import edu.kit.aifb.concept.IConceptDescription;
import edu.kit.aifb.nlp.Language;
import edu.kit.aifb.wikipedia.mlc.MLCDatabase;
import gnu.trove.TIntArrayList;

public class WpmMlcPageDescription implements IConceptDescription {
	private Logger logger = Logger.getLogger( WpmMlcPageDescription.class );
	
	MLCDatabase mlcDb;
	Map<Language,Wikipedia> wpmMap;
	
	List<Wikipedia> wpms;
	List<Language> languages;
	
	public WpmMlcPageDescription() {
	}
	
	@Required
	public void setMlcDatabase( MLCDatabase mlcDb ) {
		this.mlcDb = mlcDb;
	}
	
	@Required
	public void setWikipediaminers( List<Wikipedia> wpms ) {
		this.wpms = wpms;
		initializeWpmMap();
	}
	
	@Required
	public void setLanguages( List<Language> languages ) {
		this.languages = languages;
		initializeWpmMap();
	}

	private void initializeWpmMap() {
		if( wpms != null && languages != null ) {
			wpmMap = new HashMap<Language, Wikipedia>();
			for( int i=0; i<wpms.size(); i++ ) {
				wpmMap.put( languages.get(i), wpms.get(i) );
			}
		}
	}
	
	@Override
	public String getDescription( String conceptName, Language language ) throws Exception {
		if( !wpmMap.containsKey( language ) ) {
			throw new Exception( "Concept description in language " + language + " is not supported." );
		}
		Wikipedia wpm = wpmMap.get( language );

		int conceptId = mlcDb.getConceptId( conceptName );
		TIntArrayList articleIds = mlcDb.getPageIds( conceptId, language ); 
		
		StringBuilder sb = new StringBuilder();
		sb.append( '[' );
		for( int i=0; i<articleIds.size(); i++ ) {
			Page p = wpm.getPageById( articleIds.get( i ) );
			sb.append( '"' ).append( p.getTitle() ).append( '"' );
			if( i<articleIds.size()-1 ) {
				sb.append( ", " );
			}
		}
		sb.append( ']' );
		
		logger.debug( sb.toString() );
		return sb.toString();
	}

}
