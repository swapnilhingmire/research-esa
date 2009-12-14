package edu.uka.aifb.concept;

import org.apache.commons.configuration.Configuration;

import edu.uka.aifb.api.concept.IConceptVector;
import edu.uka.aifb.api.concept.IConceptVectorBuilder;
import edu.uka.aifb.tools.ConfigurationManager;


public class ThresholdConceptVectorBuilder implements IConceptVectorBuilder {

	static final String[] REQUIRED_PROPERTIES = {
		"concepts.builder.threshold.absolute_threshold"
	};
	
	double m_threshold;
	
	public void initialize( Configuration config ) {
		ConfigurationManager.checkProperties( config, REQUIRED_PROPERTIES );
		
		m_threshold = config.getDouble( "concepts.builder.threshold.absolute_threshold" );
	}

	public IConceptVector create( String docName, int[] conceptIds, double[] conceptScores, int maxConceptId ) {
		IConceptVector cv = new MTJConceptVector( docName, maxConceptId );
		
		for( int i=0; conceptScores[i] > m_threshold && i<conceptIds.length; i++ ) {
			cv.set( conceptIds[i], conceptScores[i] );
		}
		
		return cv;
	}

	@Override
	public IConceptVector restrict(IConceptVector cv) {
		// TODO Auto-generated method stub
		return null;
	}


}
