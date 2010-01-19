package edu.uka.aifb.concept.model;

import org.apache.commons.configuration.Configuration;
import org.apache.log4j.Logger;

import uk.ac.gla.terrier.matching.models.WeightingModel;
import edu.uka.aifb.api.concept.IConceptModel;
import edu.uka.aifb.api.concept.IConceptVector;
import edu.uka.aifb.api.ir.ITermEstimateModel;
import edu.uka.aifb.concept.MTJConceptVector;
import edu.uka.aifb.ir.DummyTermEstimateModel;
import edu.uka.aifb.ir.terrier.model.RtfIdfModel;
import edu.uka.aifb.tools.ConfigurationManager;

public class RtfIdfFixedSizeConceptModel implements IConceptModel {

	static final String[] REQUIRED_PROPERTIES = {
		"concepts.builder.fixed_size.size"
	};

	static Logger logger = Logger.getLogger( RtfIdfFixedSizeConceptModel.class );
	
	ITermEstimateModel termEstimateModel;
	WeightingModel model;

	int fixedSize;
	
	public RtfIdfFixedSizeConceptModel() {
		Configuration config = ConfigurationManager.getCurrentConfiguration();
		ConfigurationManager.checkProperties( config, REQUIRED_PROPERTIES );
		
		fixedSize = config.getInt( "concepts.builder.fixed_size.size" );
		logger.info( "Initializing: size=" + fixedSize );

		termEstimateModel = new DummyTermEstimateModel();
		model = new RtfIdfModel();
	}
	
	@Override
	public ITermEstimateModel getTermEstimatModel() {
		return termEstimateModel;
	}

	@Override
	public WeightingModel getWeightingModel() {
		return model;
	}

	@Override
	public void computeConceptScores(double[] scores, String[] queryTerms,
			int[] queryTermFrequencies, double[] queryTermEstimates,
			double[] smoothingWeights, double[][] docScores, short[] support ) {
		
		for( int i=0; i<scores.length; i++ ) {
			if( support[i] > 0 ) {
				for( int j=0; j<queryTerms.length; j++ ) {
					scores[i] += queryTermFrequencies[j] * docScores[j][i];
				}
			}
		}
	}

	@Override
	public IConceptVector getConceptVector(
			String docName,
			int[] ids, double[] values) {
		MTJConceptVector cv = new MTJConceptVector( docName, ids.length );
		for( int i=0; i<fixedSize && i<ids.length; i++ ) {
			 cv.set( ids[i], values[i] );
		}
		
		return cv;
	}

}
