package edu.uka.aifb.ir.terrier.model;

import uk.ac.gla.terrier.matching.models.Idf;
import uk.ac.gla.terrier.matching.models.WeightingModel;


/**
 * This class implements the Okapi BM25 weighting model. The
 * default parameters used are:<br>
 * k_1 = 2d<br>
 * b = 0.75d<br> The b parameter can be altered by using the setParameter method.
 * @author Gianni Amati, Ben He, Vassilis Plachouras, Philipp Sorg
 * @version $Revision: 1.19 $
 */
public class BM25Model extends WeightingModel {

	private static final long serialVersionUID = -7996680075588805628L;

	/** The constant k_1.*/
	private double k_1 = 2d;
	
	/** The parameter b.*/
	private double b;
	
	/** A default constructor.*/
	public BM25Model() {
		super();
		b=0.75d;
	}
	/**
	 * Returns the name of the model.
	 * @return the name of the model
	 */
	public final String getInfo() {
		return "BM25b"+b;
	}
	/**
	 * Uses BM25 to compute a weight for a term in a document.
	 * @param tf The term frequency in the document
	 * @param docLength the document's length
	 * @return the score assigned to a document with the given 
	 *         tf and docLength, and other preset parameters
	 */
	public final double score(double tf, double docLength) {
		double idf = Math.log(
				( numberOfDocuments - documentFrequency + 0.5d ) / ( documentFrequency + 0.5d ) );
			
		double K = k_1 * ((1 - b) + b * docLength / averageDocumentLength) + tf;
	   
		return idf * ((k_1 + 1d) * tf / K ) * keyFrequency;
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
		double keyFrequency) {
	    
		double idf = Math.log(
				( numberOfDocuments - n_t + 0.5d ) / ( n_t + 0.5d ) );
			
		double K = k_1 * ((1 - b) + b * docLength / averageDocumentLength) + tf;
	   
		return idf * ((k_1 + 1d) * tf / K ) * keyFrequency;
	}

	/**
	 * Sets the b parameter to BM25 ranking formula
	 * @param b the b parameter value to use.
	 */
	public void setParameter(double b) {
	    this.b = b;
	}


	/**
	 * Returns the b parameter to the BM25 ranking formula as set by setParameter()
	 */
	public double getParameter() {
	    return this.b;
	}
	
}
