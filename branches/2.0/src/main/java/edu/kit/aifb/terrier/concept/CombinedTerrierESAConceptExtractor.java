package edu.kit.aifb.terrier.concept;

import java.util.Arrays;

import org.apache.log4j.Logger;
import org.terrier.matching.ResultSet;

import edu.kit.aifb.concept.IConceptExtractor;
import edu.kit.aifb.concept.IConceptVector;
import edu.kit.aifb.concept.IConceptVectorBuilder;
import edu.kit.aifb.document.IDocument;
import edu.kit.aifb.nlp.ITokenStream;
import edu.kit.aifb.nlp.Language;
import edu.kit.aifb.terrier.TerrierSearch;
import gnu.trove.TIntIntHashMap;

public class CombinedTerrierESAConceptExtractor implements IConceptExtractor {

	private static Logger logger = Logger.getLogger( CombinedTerrierESAConceptExtractor.class );
	
	TerrierSearch[] searches;
	double[] indexWeights;
	TIntIntHashMap[] idMap;
	IConceptVectorBuilder conceptVectorBuilder;
	int maxConceptId;
	Language language;
	
	public CombinedTerrierESAConceptExtractor(
			TerrierSearch[] searches, double[] indexWeights, TIntIntHashMap[] idMap,
			IConceptVectorBuilder builder, Language language )
	{  
		this.language = language;
		this.idMap = idMap;
		this.searches = searches;
		maxConceptId = searches[0].getIndex().getDocumentIndex().getNumberOfDocuments();
		this.indexWeights = indexWeights;
		conceptVectorBuilder = builder;
	}

	@Override
	public IConceptVector extract( IDocument doc ) {
		logger.info( "Extracting concepts for document " + doc.getName()+ ", language=" + language );
		reset( doc.getName() );
		for( int i=0; i<searches.length; i++ ) {
			searches[i].match( doc, language );
			addScores( i, searches[i].getResultSet() );
		}
		return conceptVectorBuilder.getConceptVector();
	}
	
	@Override
	public IConceptVector extract( IDocument doc, String... fields ) {
		logger.info( "Extracting concepts for document " + doc.getName() + ", fields=" + Arrays.toString( fields ) );
		reset( doc.getName() );
		for( int i=0; i<searches.length; i++ ) {
			searches[i].match( doc, fields );
			addScores( i, searches[i].getResultSet() );
		}
		return conceptVectorBuilder.getConceptVector();
	}

	@Override
	public IConceptVector extract( String docName, ITokenStream queryTokenStream ) {
		logger.info( "Extracting concepts for document " + docName );
		reset( docName );
		for( int i=0; i<searches.length; i++ ) {
			searches[i].match( queryTokenStream );
			addScores( i, searches[i].getResultSet() );
		}
		return conceptVectorBuilder.getConceptVector();
	}
	
	private void reset( String docName ) {
		conceptVectorBuilder.reset( docName, maxConceptId );
	}
	
	private void addScores( int indexId, ResultSet rs ) {
		logger.info( "Found " + rs.getResultSize() + " matches in index " + indexId + "." );
		int[] docIds = rs.getDocids();
		double[] scores = rs.getScores();
		
		TIntIntHashMap currentIdMap = idMap[indexId];
		double currentIndexWeight = indexWeights[indexId]; 
		
		for( int i=0; i<rs.getResultSize(); i++ ) {
			if( indexId > 0 ) {
				docIds[i] = currentIdMap.get( docIds[i] );
			}
			scores[i] *= currentIndexWeight;
		}
		
		conceptVectorBuilder.addScores( docIds, rs.getScores(), rs.getResultSize() );
	}
	
}
