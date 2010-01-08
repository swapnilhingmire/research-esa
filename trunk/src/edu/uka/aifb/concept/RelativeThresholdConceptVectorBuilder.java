package edu.uka.aifb.concept;

import org.apache.commons.configuration.Configuration;

import edu.uka.aifb.api.concept.IConceptIterator;
import edu.uka.aifb.api.concept.IConceptVector;
import edu.uka.aifb.api.concept.IConceptVectorBuilder;
import edu.uka.aifb.tools.ConfigurationManager;


public class RelativeThresholdConceptVectorBuilder implements IConceptVectorBuilder {

	static final String[] REQUIRED_PROPERTIES = {
		"concepts.builder.threshold.relative_threshold"
	};
	
	double m_threshold;
	IConceptVector cv;
	
	public RelativeThresholdConceptVectorBuilder() {
		Configuration config = ConfigurationManager.getCurrentConfiguration();
		ConfigurationManager.checkProperties( config, REQUIRED_PROPERTIES );
		m_threshold = config.getDouble( "concepts.builder.threshold.relative_threshold" );
	}

	@Override
	public void addScores(int[] conceptIds, double[] conceptScores) {
		double maxScore = conceptScores[0];
		
		for( int i=0; conceptScores[i] > maxScore * m_threshold && i<conceptIds.length; i++ ) {
			cv.set( conceptIds[i], conceptScores[i] );
		}
	}

	@Override
	public void addScores(IConceptVector cv) {
		IConceptIterator it = cv.orderedIterator();
		if( it.next() ) {
			double maxScore = it.getValue();
			cv.set( it.getId(), it.getValue() );
			
			while( it.next() && it.getValue() > maxScore * m_threshold ) {
				cv.set( it.getId(), it.getValue() );
			}
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
