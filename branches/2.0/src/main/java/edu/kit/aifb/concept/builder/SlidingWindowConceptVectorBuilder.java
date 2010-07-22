package edu.kit.aifb.concept.builder;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;

import edu.kit.aifb.concept.IConceptIterator;
import edu.kit.aifb.concept.IConceptVector;
import edu.kit.aifb.concept.IConceptVectorBuilder;
import edu.kit.aifb.concept.TroveConceptVector;


public class SlidingWindowConceptVectorBuilder implements IConceptVectorBuilder {

	static final String[] REQUIRED_PROPERTIES = {
		"concepts.builder.sliding_window.rel_threshold",
		"concepts.builder.sliding_window.window_size"
	};

	static Logger logger = Logger.getLogger( SlidingWindowConceptVectorBuilder.class );
	IConceptVector cv;
	
	int windowSize;
	double threshold;
	
	@Required
	public void setThreshold( double threshold ) {
		this.threshold = threshold;
	}

	@Required
	public void setWindowSize( int windowsSize ) {
		this.windowSize = windowsSize;
	}
	
	@Override
	public void addScores(int[] conceptIds, double[] conceptScores, int count ) {
		for( int i=0; i<count && i<conceptIds.length && conceptScores[i] > 0; i++ ) {
			cv.add( conceptIds[i], conceptScores[i] );
		}
	}

	@Override
	public void addScores(IConceptVector cv) {
		logger.error( "addScore( IConceptVector) is not implemented!" );
		//TODO Implementation
	}

	@Override
	public IConceptVector getConceptVector() {
		IConceptVector newCv = new TroveConceptVector( cv.getData().getDocName(), cv.size() );

		IConceptIterator it = cv.orderedIterator();
		for( int i=0; i<windowSize && it.next(); i++ ) {
			newCv.set( it.getId(), it.getValue() );
		}

		IConceptIterator windowIt = cv.orderedIterator();
		if( windowIt.next() ) {
			double maxScore = windowIt.getValue();
			if( logger.isTraceEnabled() ) {
				logger.trace( "Max score=" + maxScore );
			}

			while( it.next() && windowIt.next() ) {
				double difference = windowIt.getValue() - it.getId(); 
				if( logger.isTraceEnabled() ) {
					logger.trace( "difference=" + difference );
				}

				if( difference <= maxScore * threshold ) {
					break;
				}
				
				newCv.add( it.getId(), it.getValue() );
			}
		}
		return newCv;
	}

	@Override
	public void reset(String docName, int maxConceptId) {
		cv = new TroveConceptVector( docName, maxConceptId );
	}


}
