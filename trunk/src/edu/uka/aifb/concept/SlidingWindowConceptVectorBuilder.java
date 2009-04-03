package edu.uka.aifb.concept;

import org.apache.commons.configuration.Configuration;
import org.apache.log4j.Logger;

import edu.uka.aifb.api.concept.IConceptVector;
import edu.uka.aifb.api.concept.IConceptVectorBuilder;
import edu.uka.aifb.tools.ConfigurationManager;


public class SlidingWindowConceptVectorBuilder implements IConceptVectorBuilder {

	static final String[] REQUIRED_PROPERTIES = {
		"concepts.builder.sliding_window.rel_threshold",
		"concepts.builder.sliding_window.window_size"
	};

	static Logger logger = Logger.getLogger( SlidingWindowConceptVectorBuilder.class );
	
	int m_windowSize;
	double m_relThreshold;
	
	public void initialize( Configuration config ) {
		ConfigurationManager.checkProperties( config, REQUIRED_PROPERTIES );
		
		m_windowSize = config.getInt( "concepts.builder.sliding_window.window_size" );
		m_relThreshold = config.getDouble( "concepts.builder.sliding_window.rel_threshold" );
	}

	public IConceptVector create( String docName, int[] conceptIds, double[] conceptScores, int maxConceptId ) {
		IConceptVector cv = new MTJConceptVector( docName, maxConceptId );
		
		for( int i=0; i<m_windowSize && i<conceptIds.length; i++ ) {
			cv.set( conceptIds[i], conceptScores[i] );
		}

		double maxScore = conceptScores[0];
		if( logger.isTraceEnabled() ) {
			logger.trace( "Max score=" + maxScore );
		}
		
		int i = m_windowSize;
		double difference = conceptScores[i-m_windowSize] - conceptScores[i]; 
		
		while( difference > maxScore * m_relThreshold && i<conceptIds.length ) {
			cv.set( conceptIds[i], conceptScores[i] );
			
			i++;
			difference = conceptScores[i-m_windowSize] - conceptScores[i];
			if( logger.isTraceEnabled() ) {
				logger.trace( i + ": difference=" + difference );
			}
		}
		
		return cv;
	}


}
