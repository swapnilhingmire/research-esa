package edu.uka.aifb.concept;

import org.apache.commons.configuration.Configuration;

import edu.uka.aifb.api.concept.IConceptVector;
import edu.uka.aifb.api.concept.IConceptVectorBuilder;
import edu.uka.aifb.tools.ConfigurationManager;


public class FixedSizeConceptVectorBuilder implements IConceptVectorBuilder {

	static final String[] REQUIRED_PROPERTIES = {
		"concepts.builder.fixed_size.size"
	};
	
	int m_size;
	
	public void initialize( Configuration config ) {
		ConfigurationManager.checkProperties( config, REQUIRED_PROPERTIES );
		
		m_size = config.getInt( "concepts.builder.fixed_size.size" );
	}

	public IConceptVector create( String docName, int[] conceptIds, double[] conceptScores, int maxConceptId ) {
		IConceptVector cv = new MTJConceptVector( docName, maxConceptId );
		
		for( int i=0; i<m_size && i<conceptIds.length; i++ ) {
			if( conceptScores[i] > 0 ) {
				cv.set( conceptIds[i], conceptScores[i] );
			}
		}
		
		return cv;
	}


}
