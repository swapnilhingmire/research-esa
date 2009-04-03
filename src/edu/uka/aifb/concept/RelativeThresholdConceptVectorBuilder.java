package edu.uka.aifb.concept;

import org.apache.commons.configuration.Configuration;

import edu.uka.aifb.api.concept.IConceptVector;
import edu.uka.aifb.api.concept.IConceptVectorBuilder;
import edu.uka.aifb.tools.ConfigurationManager;


public class RelativeThresholdConceptVectorBuilder implements IConceptVectorBuilder {

	static final String[] REQUIRED_PROPERTIES = {
		"concepts.builder.threshold.relative_threshold"
	};
	
	double m_threshold;
	
	public void initialize( Configuration config ) {
		ConfigurationManager.checkProperties( config, REQUIRED_PROPERTIES );
		
		m_threshold = config.getDouble( "concepts.builder.threshold.relative_threshold" );
	}

	public IConceptVector create( String docName, int[] conceptIds, double[] conceptScores, int maxConceptId ) {
		IConceptVector cv = new MTJConceptVector( docName, maxConceptId );
		
		double maxScore = conceptScores[0];
		
		for( int i=0; conceptScores[i] > maxScore * m_threshold && i<conceptIds.length; i++ ) {
			cv.set( conceptIds[i], conceptScores[i] );
		}
		
		return cv;
	}


}
