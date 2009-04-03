package edu.uka.aifb.ir.terrier.model;

import org.apache.log4j.Logger;

import uk.ac.gla.terrier.matching.MatchingQueryTerms;
import uk.ac.gla.terrier.matching.ResultSet;
import uk.ac.gla.terrier.matching.dsms.DocumentScoreModifier;
import uk.ac.gla.terrier.structures.DirectIndex;
import uk.ac.gla.terrier.structures.DocumentIndex;
import uk.ac.gla.terrier.structures.Index;
import uk.ac.gla.terrier.structures.Lexicon;


public class CosineRtfIdfDocumentScoreModifier implements DocumentScoreModifier {

	static Logger logger = Logger.getLogger( CosineRtfIdfDocumentScoreModifier.class );
	
	private double[] m_documentVectorSizeCache;
	
	public String getName() {
		return "CosineModifier";
	}

	public boolean modifyScores( Index index, MatchingQueryTerms mqt, ResultSet rs ) {
		if ( m_documentVectorSizeCache == null ) {
			m_documentVectorSizeCache = new double[index.getCollectionStatistics().getNumberOfDocuments()];
		}
		
		//logger.debug( "Modifying scores for query " + mqt.toString() );
		
		/*
		 * Calculate vector size for the query
		 */
		double queryVectorSize = 0;
		String[] queryTermStrings = mqt.getTerms();
		for (int i = 0; i < queryTermStrings.length; i++) {
			double d = mqt.getTermWeight( queryTermStrings[i] );
			queryVectorSize += d * d;  
		}
		queryVectorSize = Math.sqrt( queryVectorSize );
		//logger.debug( "query vector size: " + queryVectorSize );
		
		DirectIndex directIndex = index.getDirectIndex();
		DocumentIndex docIndex = index.getDocumentIndex();

		Lexicon lexicon = index.getLexicon();
		int numberOfDocuments = index.getCollectionStatistics().getNumberOfDocuments();
		
		int[] docIds = rs.getDocids();
		double[] scores = rs.getScores();
		for( int i=0; i<rs.getExactResultSize(); i++ ) {
			//logger.debug( "Document length of doc " + docIds[i]+ ": " + index.getDocumentIndex().getDocumentLength( docIds[i] ) );
			
			double documentVectorSize = m_documentVectorSizeCache[docIds[i]];
			
			if( documentVectorSize == 0 ) {
				
				/*
				 * Calculate vector size for the document
				 */
				int docLength = docIndex.getDocumentLength( docIds[i] );

				int[][] terms = directIndex.getTerms( docIds[i] );
				//logger.debug( "Length of term id array: " + terms[0].length );
				for( int j=0; j<terms[0].length; j++ ) {
					double rtf = (double)terms[1][j] / (double)docLength;
					
					double idf = Math.log( numberOfDocuments / lexicon.getLexiconEntry( terms[0][j] ).n_t );
					
					documentVectorSize += rtf * idf * rtf * idf;
					// logger.debug( "Doc: " + docIds[i] + ", Term: " + terms[0][j] + ", Weight: " + terms[1][j] );
				}
				documentVectorSize = Math.sqrt( documentVectorSize );
				//logger.debug( "document vector size for doc " + docIds[i] + ": " + documentVectorSize );
				m_documentVectorSizeCache[docIds[i]] = documentVectorSize;
				
			}
			
			scores[i] = scores[i] / ( queryVectorSize * documentVectorSize );
		}

		return true;
	}
	
	public CosineRtfIdfDocumentScoreModifier clone() {
		return new CosineRtfIdfDocumentScoreModifier();
	}

}
