package edu.uka.aifb.concept.model;

import java.util.Arrays;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;
import org.terrier.matching.models.WeightingModel;
import org.terrier.structures.Index;
import org.terrier.utility.HeapSort;

import edu.kit.aifb.concept.IConceptVector;
import edu.kit.aifb.concept.TroveConceptVector;
import edu.kit.aifb.terrier.concept.IConceptModel;
import edu.kit.aifb.terrier.model.RtfModel;
import edu.kit.aifb.terrier.tem.ITermEstimateModel;
import edu.kit.aifb.terrier.tem.IdfTermEstimateModel;

public class RtfIdfFixedSizeConceptModel implements IConceptModel {

	static Logger logger = Logger.getLogger( RtfIdfFixedSizeConceptModel.class );
	
	ITermEstimateModel termEstimateModel;
	WeightingModel model;

	int fixedSize;
	
	double[] scores;
	int[] ids;

	@Required
	public void setSize( int size ) {
		logger.info( "Initializing: size=" + size );
		this.fixedSize = size;
	}
	
	public RtfIdfFixedSizeConceptModel() {
		termEstimateModel = new IdfTermEstimateModel();
		model = new RtfModel();
	}
	
	@Override
	public ITermEstimateModel getTermEstimatModel() {
		return termEstimateModel;
	}

	@Override
	public WeightingModel getWeightingModel() {
		return model;
	}

	private void initialize( int numberOfConcepts ) {
		if( scores == null || scores.length != numberOfConcepts ) {
			scores = new double[numberOfConcepts];
			ids = new int[numberOfConcepts];
		}
		else {
			Arrays.fill( scores, 0d );
		}
		for( int i=0; i<numberOfConcepts; i++ ) {
			ids[i] = i;
		}
	}
	
	@Override
	public IConceptVector getConceptVector(
			String docName,
			String[] queryTerms,
			int[] queryTermFrequencies, double[] queryTermEstimates,
			double[] smoothingWeights, double[][] docScores, short[] support )
	{
		initialize( support.length );
		
		for( int i=0; i<support.length; i++ ) {
			if( support[i] > 0 ) {
				for( int termId=0; termId<queryTerms.length; termId++ ) {
					scores[i] += queryTermFrequencies[termId] * docScores[termId][i] * queryTermEstimates[termId];
				}
			}
		}

		HeapSort.descendingHeapSort( scores, ids, support );
		
		IConceptVector cv = new TroveConceptVector( docName, ids.length );
		for( int i=0; i<fixedSize && i<ids.length; i++ ) {
			 cv.set( ids[i], scores[i] );
		}
		return cv;
	}

	@Override
	public void setIndex(Index index) {
		termEstimateModel.setIndex( index );
	}

}
