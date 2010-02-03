package edu.uka.aifb.concept;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.log4j.Logger;

import edu.uka.aifb.api.concept.IConceptIterator;
import edu.uka.aifb.api.concept.IConceptVector;
import edu.uka.aifb.api.concept.IConceptVectorData;
import edu.uka.aifb.api.concept.index.ICVIndexEntryIterator;
import edu.uka.aifb.api.concept.index.ICVIndexReader;
import edu.uka.aifb.api.concept.search.IConceptMatcher;
import edu.uka.aifb.api.concept.search.IScorer;
import edu.uka.aifb.api.ir.IMatch;
import edu.uka.aifb.concept.index.PersistantCVData;
import edu.uka.aifb.ir.Match;


public class ConceptMatcher implements IConceptMatcher {

	static Logger logger = Logger.getLogger( ConceptMatcher.class );
	
	ICVIndexReader m_indexReader;
	IScorer[] m_documentScorers;
	
	public ConceptMatcher( ICVIndexReader indexReader ) {
		m_indexReader = indexReader;
		
		m_documentScorers = new IScorer[indexReader.getNumberOfDocuments()];
	}
	
	@SuppressWarnings("unchecked")
	public void setScorerClass( String className ) {
		try {
			Class scorerClass = Class.forName( className );
			setScorerClass( scorerClass );
		}
		catch( Exception e ) {
			logger.error( e );
		} 
	}
	
	@Override
	public void setScorerClass(Class<? extends IScorer> scorerClass) {
		logger.info( "Initializing concept matcher with scorer class " + scorerClass.getName() );
		
		for( int i=0; i<m_documentScorers.length; i++ )
		{
			try {
				m_documentScorers[i] = scorerClass.newInstance();
			}
			catch( Exception e ) {
				logger.error( e );
			} 
		}
	}

	public List<IMatch> getMatches( IConceptVector queryCV ) {
		logger.debug( "Matching " + queryCV.getData().getDocName() );
		
		logger.debug( "Resetting scorers" );
		IConceptVectorData queryCVData = new PersistantCVData( queryCV.getData() );
		for( int i=0; i<m_documentScorers.length; i++ )
		{
			m_documentScorers[i].reset(
					queryCVData,
					m_indexReader.getConceptVectorData( i ),
					m_indexReader.getNumberOfDocuments() );
		}
		
		IConceptIterator conceptIt = queryCV.iterator();
		while( conceptIt.next() )
		{
			int conceptId = conceptIt.getId();
			int documentFrequency = m_indexReader.getDocumentFrequency( conceptId );
			
			if( logger.isTraceEnabled() ) {
				logger.trace( "Getting index entries for concept " + conceptId + " ...");
			}
			
			ICVIndexEntryIterator documentIt = m_indexReader.getIndexEntryIterator( conceptId );
			while( documentIt.next() )
			{
				int documentId = documentIt.getDocId();
				
				m_documentScorers[documentId].addConcept(
						conceptId, conceptIt.getValue(),
						conceptId, documentIt.getValue(),
						documentFrequency );
			}
			if( logger.isTraceEnabled() ) {
				logger.trace( "Done." );
			}
			
		}
		
		logger.debug( "Finalizing scores and computing ranking" );

		List<IMatch> matchList = new ArrayList<IMatch>();
		
		for( int i=0; i<m_documentScorers.length; i++ ) {
			if( m_documentScorers[i].hasScore() )
			{
				IConceptVectorData docData = m_indexReader.getConceptVectorData( i );
				
				m_documentScorers[i].finalizeScore(
						queryCVData,
						docData );
				
				matchList.add( new Match(
						docData.getDocName(),
						m_documentScorers[i].getScore() ) );
			}
		}
		
		logger.debug( "Sorting result list" );
		Collections.sort( matchList );

		logger.info( "Found " + matchList.size() + " matches." );
		return matchList;
	}

}
