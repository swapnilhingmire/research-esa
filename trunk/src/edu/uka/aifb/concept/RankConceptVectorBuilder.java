package edu.uka.aifb.concept;

import org.apache.commons.configuration.Configuration;

import edu.uka.aifb.api.concept.IConceptIterator;
import edu.uka.aifb.api.concept.IConceptVector;
import edu.uka.aifb.api.concept.IConceptVectorBuilder;
import edu.uka.aifb.tools.ConfigurationManager;


public class RankConceptVectorBuilder implements IConceptVectorBuilder {

	static final String[] REQUIRED_PROPERTIES = {
		"concepts.builder.rank.size"
	};
	
	int m_size;
	IConceptVector cv;
	
	public RankConceptVectorBuilder() {
		Configuration config = ConfigurationManager.getCurrentConfiguration();
		ConfigurationManager.checkProperties( config, REQUIRED_PROPERTIES );
		m_size = config.getInt( "concepts.builder.rank.size" );
	}

	private double rankToScore( int rank ) {
		return 1 - (double)rank / m_size; 
	}

	@Override
	public void addScores(int[] conceptIds, double[] conceptScores) {
		for( int i=0; i<m_size && i<conceptIds.length && conceptScores[i] > 0; i++ ) {
			cv.set( conceptIds[i], rankToScore( i ) );
		}
	}

	@Override
	public void addScores(IConceptVector cv) {
		IConceptIterator it = cv.orderedIterator();
		
		for( int i=0; it.next(); i++ ) {
			cv.set( it.getId(), rankToScore( i ) );
		}
	}

	@Override
	public IConceptVector getConceptVector() {
		return cv;
	}

	@Override
	public void reset(String docName, int maxConceptId) {
		cv = new MTJConceptVector( docName, maxConceptId );
	}
	
}
