package edu.uka.aifb.concept;

import org.apache.commons.configuration.Configuration;

import edu.uka.aifb.api.concept.IConceptVector;
import edu.uka.aifb.api.concept.IConceptVectorBuilder;
import edu.uka.aifb.tools.ConfigurationManager;


public class RankConceptVectorBuilder implements IConceptVectorBuilder {

	static final String[] REQUIRED_PROPERTIES = {
		"concepts.builder.rank.size"
	};
	
	int m_size;
	
	public void initialize( Configuration config ) {
		ConfigurationManager.checkProperties( config, REQUIRED_PROPERTIES );
		
		m_size = config.getInt( "concepts.builder.rank.size" );
	}

	public IConceptVector create( String docName, int[] conceptIds, double[] conceptScores, int maxConceptId ) {
		IConceptVector cv = new MTJConceptVector( docName, maxConceptId );
		
		for( int i=0; i<m_size && i<conceptIds.length && conceptScores[i] > 0; i++ ) {
			cv.set( conceptIds[i], rankToScore( i ) );
		}
		
		return cv;
	}

	private double rankToScore( int rank ) {
		return 1 - (double)rank / m_size; 
	}

	@Override
	public IConceptVector restrict(IConceptVector cv) {
		// TODO Auto-generated method stub
		return null;
	}
	
}
