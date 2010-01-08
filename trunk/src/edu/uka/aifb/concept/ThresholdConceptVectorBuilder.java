package edu.uka.aifb.concept;

import org.apache.commons.configuration.Configuration;

import edu.uka.aifb.api.concept.IConceptIterator;
import edu.uka.aifb.api.concept.IConceptVector;
import edu.uka.aifb.api.concept.IConceptVectorBuilder;
import edu.uka.aifb.tools.ConfigurationManager;


public class ThresholdConceptVectorBuilder implements IConceptVectorBuilder {

	static final String[] REQUIRED_PROPERTIES = {
		"concepts.builder.threshold.absolute_threshold"
	};
	
	double m_threshold;
	IConceptVector cv;
	
	public ThresholdConceptVectorBuilder() {
		Configuration config = ConfigurationManager.getCurrentConfiguration();
		ConfigurationManager.checkProperties( config, REQUIRED_PROPERTIES );
		m_threshold = config.getDouble( "concepts.builder.threshold.absolute_threshold" );
	}

	public IConceptVector create( String docName, int[] conceptIds, double[] conceptScores, int maxConceptId ) {
		
		
		return cv;
	}

	@Override
	public void addScores(int[] conceptIds, double[] conceptScores) {
		for( int i=0; conceptScores[i] > m_threshold && i<conceptIds.length; i++ ) {
			cv.set( conceptIds[i], conceptScores[i] );
		}
	}

	@Override
	public void addScores(IConceptVector cv) {
		IConceptIterator it = cv.orderedIterator();
		
		while( it.next() && it.getValue() > m_threshold ) {
			cv.set( it.getId(), it.getValue() );
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
