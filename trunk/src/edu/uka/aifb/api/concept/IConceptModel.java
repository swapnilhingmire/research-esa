package edu.uka.aifb.api.concept;

import uk.ac.gla.terrier.matching.models.WeightingModel;
import edu.uka.aifb.api.ir.ITermEstimateModel;

public interface IConceptModel {

	public WeightingModel getWeightingModel();
	
	public ITermEstimateModel getTermEstimatModel();
	
	public void computeConceptScores(
			double[] scores,
			String[] queryTerms, int[] queryTermFrequencies,
			double[] queryTermEstimates, double[] smoothingWeights,
			double[][] docScores, short[] support );
	
	public IConceptVector getConceptVector(
			String docName, 
			int[] ids, double[] values );
	
}
