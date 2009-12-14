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
	
	public void initialize( Configuration config ) {
		ConfigurationManager.checkProperties( config, REQUIRED_PROPERTIES );
		
		m_size = config.getInt( "concepts.builder.fixed_size.size" );
		
		logger.info( "Initializing: size=" + m_size );
	}

	public void setSize( int size ) {
		m_size = size;
		logger.info( "New settings: size=" + m_size );
	}
	
	public IConceptVector create( String docName, int[] conceptIds, double[] conceptScores, int maxConceptId ) {
		IConceptVector cv = new MTJConceptVector( docName, maxConceptId );
		
		for( int i=0; i<m_size && i<conceptIds.length; i++ ) {
			if( conceptScores[i] > 0 ) {
				cv.set( conceptIds[i], conceptScores[i] );
			}
		}
		
		return cv;
	}

	@Override
	public IConceptVector restrict( IConceptVector cv ) {
		if( cv.count() <= m_size ) {
			return cv;
		}

		IConceptVector newCv = new MTJConceptVector( cv.getData().getDocName(), cv.size() );
		
		IConceptIterator it = cv.orderedIterator();
		for( int i=0; it.next() && i<m_size; i++ ) {
			newCv.add( it.getId(), it.getValue() );
		}
		return newCv;
	}

}
