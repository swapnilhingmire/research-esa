package edu.uka.aifb.ir.terrier.model;

import uk.ac.gla.terrier.matching.models.WeightingModel;


public class CosineRtfIdfModel extends WeightingModel {

	private static final long serialVersionUID = -7996680075588805628L;

	/**
	 * Returns the name of the model.
	 * @return the name of the model
	 */
	public final String getInfo() {
		return "Cosine";
	}

	/**
	 * Uses BM25 to compute a weight for a term in a document.
	 * @param tf The term frequency in the document
	 * @param docLength the document's length
	 * @return the score assigned to a document with the given 
	 *         tf and docLength, and other preset parameters
	 */
	public final double score(double tf, double docLength) {
		double idf = Math.log( numberOfDocuments / documentFrequency );
		
		return tf / docLength * keyFrequency * idf;
	}
	
	/**
	 * Uses BM25 to compute a weight for a term in a document.
	 * @param tf The term frequency in the document
	 * @param docLength the document's length
	 * @param n_t The document frequency of the term
	 * @param F_t the term frequency in the collection
	 * @param keyFrequency the term frequency in the query
	 * @return the score assigned by the weighting model BM25.
	 */
	public final double score(
		double tf,
		double docLength,
		double n_t,
		double F_t,
		double keyFrequency)
	{
		double idf = Math.log( numberOfDocuments / documentFrequency );

		return tf / docLength * keyFrequency * idf;
	}

	
}
