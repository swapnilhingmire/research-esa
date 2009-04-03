package edu.uka.aifb.ir.terrier.model;

import uk.ac.gla.terrier.matching.models.WeightingModel;


public class TfIdfQAModel extends WeightingModel {

	private static final long serialVersionUID = 7217210428994800649L;

	@Override
	public String getInfo() {
		return "TF_IDF";
	}

	@Override
	public final double score(double tf, double docLength) {
		double idf = Math.log( numberOfDocuments / documentFrequency );

		return keyFrequency * tf * idf;
	}

	@Override
	public final double score(
			double tf,
			double docLength,
			double documentFrequency,
			double termFrequency,
			double keyFrequency) 
	{
		double idf = Math.log( numberOfDocuments / documentFrequency );

		return keyFrequency * tf * idf;
	}

}
