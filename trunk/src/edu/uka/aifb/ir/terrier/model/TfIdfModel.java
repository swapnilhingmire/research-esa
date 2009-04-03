package edu.uka.aifb.ir.terrier.model;

import uk.ac.gla.terrier.matching.models.WeightingModel;


public class TfIdfModel extends WeightingModel {

	private static final long serialVersionUID = 7217210428994800649L;

	@Override
	public String getInfo() {
		return "TF_IDF";
	}

	@Override
	public final double score(double tf, double docLength) {
		double rtf = tf / docLength;
		double idf = Math.log( numberOfDocuments / documentFrequency );

		return rtf * idf;
	}

	@Override
	public final double score(
			double tf,
			double docLength,
			double documentFrequency,
			double termFrequency,
			double keyFrequency) 
	{
		double rtf = tf / docLength;
		double idf = Math.log( numberOfDocuments / documentFrequency );

		return rtf * idf;
	}

}
