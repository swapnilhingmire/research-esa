package edu.kit.aifb.concept.builder;

import org.springframework.beans.factory.annotation.Required;

import edu.kit.aifb.concept.IConceptIterator;
import edu.kit.aifb.concept.IConceptVector;
import edu.kit.aifb.concept.IConceptVectorBuilder;
import edu.kit.aifb.concept.TroveConceptVector;


public class RelativeThresholdConceptVectorBuilder implements IConceptVectorBuilder {

	static final String[] REQUIRED_PROPERTIES = {
		"concepts.builder.threshold.relative_threshold"
	};
	
	double threshold;
	IConceptVector cv;
	
	@Required
	public void setThreshold( double threshold ) {
		this.threshold = threshold;
	}
	
	@Override
	public void addScores(int[] conceptIds, double[] conceptScores) {
		for( int i=0; i<conceptIds.length && conceptScores[i] > 0; i++ ) {
			cv.add( conceptIds[i], conceptScores[i] );
		}
	}

	@Override
	public void addScores( IConceptVector oldCv ) {
		IConceptIterator it = oldCv.iterator();
		while( it.next() ) {
			cv.add( it.getId(), it.getValue() );
		}
	}

	@Override
	public IConceptVector getConceptVector() {
		IConceptVector newCv = new TroveConceptVector( cv.getData().getDocName(), cv.size() );
		IConceptIterator it = cv.orderedIterator();
		if( it.next() ) {
			double maxScore = it.getValue();
			newCv.set( it.getId(), it.getValue() );
			
			while( it.next() && it.getValue() > maxScore * threshold ) {
				newCv.set( it.getId(), it.getValue() );
			}
		}
		return newCv;
	}

	@Override
	public void reset(String docName, int maxConceptId) {
		cv = new TroveConceptVector( docName, maxConceptId );
	}
}
