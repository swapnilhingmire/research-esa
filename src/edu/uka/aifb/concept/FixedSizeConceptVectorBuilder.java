package edu.uka.aifb.concept;

import org.apache.commons.configuration.Configuration;
import org.apache.log4j.Logger;

import edu.uka.aifb.api.concept.IConceptIterator;
import edu.uka.aifb.api.concept.IConceptVector;
import edu.uka.aifb.api.concept.IConceptVectorBuilder;
import edu.uka.aifb.tools.ConfigurationManager;


public class FixedSizeConceptVectorBuilder implements IConceptVectorBuilder {

	static final String[] REQUIRED_PROPERTIES = {
		"concepts.builder.fixed_size.size"
	};
	
	static Logger logger = Logger.getLogger( FixedSizeConceptVectorBuilder.class );
	
	int m_size;
	IConceptVector cv;
	
	public FixedSizeConceptVectorBuilder() {
		Configuration config = ConfigurationManager.getCurrentConfiguration();
		ConfigurationManager.checkProperties( config, REQUIRED_PROPERTIES );
		
		m_size = config.getInt( "concepts.builder.fixed_size.size" );
		logger.info( "Initializing: size=" + m_size );
	}

	public void setSize( int size ) {
		m_size = size;
		logger.info( "New settings: size=" + m_size );
	}
	
	@Override
	public void reset( String docName, int maxConceptId ) {
		cv = new MTJConceptVector( docName, maxConceptId );
	}
	
	@Override
	public void addScores( int[] conceptIds, double[] conceptScores ) {
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
		MTJConceptVector newCv = new MTJConceptVector( cv.getData().getDocName(), cv.size() );
		IConceptIterator it = cv.orderedIterator();
		for( int count =0; count<m_size && it.next(); count++ ) {
			newCv.set( it.getId(), it.getValue() );
		}
		return newCv;
	}
	
}
