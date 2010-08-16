package edu.kit.aifb.concept.builder;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;

import edu.kit.aifb.concept.IConceptIterator;
import edu.kit.aifb.concept.IConceptVector;
import edu.kit.aifb.concept.IConceptVectorBuilder;
import edu.kit.aifb.concept.TroveConceptVector;


public class RankConceptVectorBuilder implements IConceptVectorBuilder {

	private static Logger logger = Logger.getLogger( RankConceptVectorBuilder.class );
	static final String[] REQUIRED_PROPERTIES = {
		"concepts.builder.rank.size"
	};
	
	int size;
	IConceptVector cv;
	
	@Required
	public void setSize( int size ) {
		logger.info( "New settings: size=" + size );
		this.size = size;
	}
	
	private double rankToScore( int rank ) {
		return 1 - (double)rank / size; 
	}

	@Override
	public void addScores(int[] conceptIds, double[] conceptScores, int count ) {
		for( int i=0; i<count && i<conceptIds.length && conceptScores[i] > 0; i++ ) {
			cv.add( conceptIds[i], rankToScore( i ) );
		}
	}

	@Override
	public void addScores(IConceptVector oldCv) {
		IConceptIterator it = oldCv.orderedIterator();
		
		for( int i=0; it.next(); i++ ) {
			cv.add( it.getId(), rankToScore( i ) );
		}
	}

	@Override
	public IConceptVector getConceptVector() {
		IConceptVector newCv = new TroveConceptVector( cv.getData().getDocName(), cv.size() );
		IConceptIterator it = cv.orderedIterator();
		for( int count =0; count<size && it.next(); count++ ) {
			newCv.set( it.getId(), it.getValue() );
		}
		return newCv;
	}

	@Override
	public void reset(String docName, int maxConceptId) {
		cv = new TroveConceptVector( docName, maxConceptId );
	}
	
}
