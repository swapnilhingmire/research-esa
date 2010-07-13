package edu.kit.aifb.concept.builder;

import org.springframework.beans.factory.annotation.Required;

import edu.kit.aifb.concept.IConceptIterator;
import edu.kit.aifb.concept.IConceptVector;
import edu.kit.aifb.concept.IConceptVectorBuilder;
import edu.kit.aifb.concept.TroveConceptVector;


public class ThresholdConceptVectorBuilder implements IConceptVectorBuilder {

	static final String[] REQUIRED_PROPERTIES = {
		"concepts.builder.threshold.absolute_threshold"
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
	public void addScores(IConceptVector cv) {
		IConceptIterator it = cv.iterator();
		while( it.next() ) {
			cv.add( it.getId(), it.getValue() );
		}
	}

	@Override
	public IConceptVector getConceptVector() {
		IConceptVector newCv = new TroveConceptVector( cv.getData().getDocName(), cv.size() );
		IConceptIterator it = cv.orderedIterator();
		while( it.next() && it.getValue() > threshold ) {
			newCv.set( it.getId(), it.getValue() );
		}
		return newCv;
	}

	@Override
	public void reset(String docName, int maxConceptId) {
		cv = new TroveConceptVector( docName, maxConceptId );
	}


}
