package edu.kit.aifb.terrier.concept;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;
import org.terrier.matching.dsms.DocumentScoreModifier;
import org.terrier.matching.models.WeightingModel;
import org.terrier.structures.DocumentIndex;
import org.terrier.structures.Index;
import org.terrier.structures.MetaIndex;

import edu.kit.aifb.concept.IConceptExtractor;
import edu.kit.aifb.concept.IConceptIndex;
import edu.kit.aifb.concept.IConceptVectorBuilder;
import edu.kit.aifb.nlp.ITokenAnalyzer;
import edu.kit.aifb.nlp.Language;
import edu.kit.aifb.terrier.ITerrierSearch;
import edu.kit.aifb.terrier.TerrierSearch;
import gnu.trove.TDoubleArrayList;
import gnu.trove.TIntIntHashMap;

public class CombinedTerrierESAIndex implements IConceptIndex {

	private static Logger logger = Logger.getLogger( CombinedTerrierESAIndex.class );
	
	Index[] indexes;
	DocumentIndex documentIndex;
	MetaIndex metaIndex;
	
	Language language;

	IConceptVectorBuilder builder;
	
	List<TerrierSearch> searches;
	TDoubleArrayList weights;
	
	List<TIntIntHashMap> idMaps;
	
	public CombinedTerrierESAIndex() {
		searches = new ArrayList<TerrierSearch>();
		weights = new TDoubleArrayList(); 
		idMaps = new ArrayList<TIntIntHashMap>();
	}
	
	@Override
	public void setTokenAnalyzer( ITokenAnalyzer analyzer ) {
	}
	
	@Required @Override
	public void setLanguage( Language language ) {
		logger.info( "Setting language: " + language );
		this.language = language;
	}

	public void addSearch( TerrierSearch search, double weight ) throws IOException {
		searches.add( search );
		weights.add( weight );
		
		Index currentIndex = search.getIndex();
		TIntIntHashMap currentIdMap = new TIntIntHashMap();
		idMaps.add( currentIdMap );
		
		if( searches.size() == 1 ) {
			documentIndex = currentIndex.getDocumentIndex();
			metaIndex = currentIndex.getMetaIndex();
		}
		else {
			DocumentIndex currentDocumentIndex = currentIndex.getDocumentIndex();
			MetaIndex currentMetaIndex = currentIndex.getMetaIndex();

			for( int docId=0; docId<currentDocumentIndex.getNumberOfDocuments(); docId++ ) {
				int targetDocId = metaIndex.getDocument(
						"docno", currentMetaIndex.getItem( "docno", docId ) );
				currentIdMap.put( docId, targetDocId );
			}
		}
	}
	
	@Required
	public void setConceptVectorBuilder( IConceptVectorBuilder builder ) {
		logger.info( "Setting concept vector builder: " + builder.getClass().getName() );
		this.builder = builder;
	}

	@Override
	public IConceptExtractor getConceptExtractor() {
		return new CombinedTerrierESAConceptExtractor(
				searches, weights, idMaps,
				builder, language );
	}

	@Override
	public int getConceptId( String conceptName ) {
		try {
			return metaIndex.getDocument( "docno", conceptName );
		}
		catch( IOException e ) {
			logger.error( e );
			return -1;
		}
	}

	@Override
	public String getConceptName( int conceptId ) {
		try {
			return metaIndex.getItem( "docno", conceptId );
		}
		catch( IOException e ) {
			logger.error( e );
			return null;
		}
	}

	@Override
	public int size() {
		return documentIndex.getNumberOfDocuments();
	}

	@Override
	public Language getLanguage() {
		return language;
	}
}
