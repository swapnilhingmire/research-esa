package edu.kit.aifb.terrier.concept;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
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
import edu.kit.aifb.terrier.TerrierIndexFactory;
import edu.kit.aifb.terrier.TerrierSearch;
import gnu.trove.TIntIntHashMap;

public class CombinedTerrierESAIndex implements IConceptIndex {

	private static Logger logger = Logger.getLogger( CombinedTerrierESAIndex.class );
	
	Index[] indexes;
	DocumentIndex documentIndex;
	MetaIndex metaIndex;
	
	Language language;
	WeightingModel model;
	DocumentScoreModifier dsm;
	IConceptVectorBuilder builder;
	List<String> indexIds;
	double[] indexWeights;
	
	ITokenAnalyzer analyzer;
	
	TerrierIndexFactory terrierIndexFactory;
	
	TIntIntHashMap[] idMap;
	
	@Autowired  @Override
	public void setTokenAnalyzer( ITokenAnalyzer analyzer ) {
		logger.info( "Setting token analyzer: " + analyzer.getClass().getName() );
		this.analyzer = analyzer;
	}
	
	@Autowired
	public void setTerrierIndexFactory( TerrierIndexFactory factory ) {
		terrierIndexFactory = factory;
	}
	
	@Required @Override
	public void setLanguage( Language language ) {
		logger.info( "Setting language: " + language );
		this.language = language;
	}

	@Required
	public void setIndexIds( List<String> indexIds ) {
		logger.info( "Setting indexes: " + indexIds.toString() );
		this.indexIds = indexIds;
	}
	
	@Required
	public void setIndexWeights( List<String> indexWeightList ) {
		logger.info( "Setting index weights: " + indexWeightList.toString() );
		indexWeights = new double[indexWeightList.size()];
		for( int i=0; i<indexWeightList.size(); i++ ) {
			indexWeights[i] = Double.parseDouble( indexWeightList.get( i ) );
		}
	}
	
	@Required
	public void setWeightingModel( WeightingModel model ) {
		logger.info( "Setting weighting model: " + model.getClass().getName() );
		this.model = model;
	}
	
	public void setDocumentScoreModifier( DocumentScoreModifier dsm ) {
		logger.info( "Setting document score modifier: " + dsm.getClass().getName() );
		this.dsm = dsm;
	}
	
	@Required
	public void setConceptVectorBuilder( IConceptVectorBuilder builder ) {
		logger.info( "Setting concept vector builder: " + builder.getClass().getName() );
		this.builder = builder;
	}

	public void readIndexes() throws IOException {
		indexes = new Index[indexIds.size()];
		idMap = new TIntIntHashMap[indexIds.size()];
		
		Index firstIndex = terrierIndexFactory.readIndex( indexIds.get(0), language );
		indexes[0] = firstIndex;
		documentIndex = firstIndex.getDocumentIndex();
		metaIndex = firstIndex.getMetaIndex();
		
		for( int i=1; i<indexIds.size(); i++ ) {
			Index currentIndex = terrierIndexFactory.readIndex( indexIds.get(1), language );
			DocumentIndex currentDocumentIndex = currentIndex.getDocumentIndex();
			MetaIndex currentMetaIndex = currentIndex.getMetaIndex();
			
			TIntIntHashMap currentIdMap = new TIntIntHashMap();
			for( int docId=0; docId<currentDocumentIndex.getNumberOfDocuments(); docId++ ) {
				int targetDocId = metaIndex.getDocument(
						"docno", currentMetaIndex.getItem( "docno", docId ) );
				currentIdMap.put( docId, targetDocId );
			}
			
			indexes[i] = currentIndex;
			idMap[i] = currentIdMap;
		}
	}
	
	public IConceptExtractor getConceptExtractor() {
		TerrierSearch[] searches = new TerrierSearch[indexes.length];
		for( int i=0; i<indexes.length; i++ ) {
			searches[i] = new TerrierSearch();
			if( dsm == null ) {
				searches[i].setIndex( indexes[i], (WeightingModel)model.clone() );
			}
			else {
				searches[i].setIndex(
						indexes[i],
						(WeightingModel)model.clone(),
						(DocumentScoreModifier)dsm.clone() );
			}
			searches[i].setTokenAnalyzer( analyzer );
		}
		
		return new CombinedTerrierESAConceptExtractor(
				searches, indexWeights, idMap,
				builder, language );
	}

	public int getConceptId( String conceptName ) {
		try {
			return metaIndex.getDocument( "docno", conceptName );
		}
		catch( IOException e ) {
			logger.error( e );
			return -1;
		}
	}

	public String getConceptName( int conceptId ) {
		try {
			return metaIndex.getItem( "docno", conceptId );
		}
		catch( IOException e ) {
			logger.error( e );
			return null;
		}
	}

	public int size() {
		return documentIndex.getNumberOfDocuments();
	}

	@Override
	public Language getLanguage() {
		return language;
	}
}
