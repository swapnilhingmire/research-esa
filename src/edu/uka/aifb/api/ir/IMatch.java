package edu.uka.aifb.api.ir;

/**
 * This specifies an match for a defined topic.
 * 
 * @author pso
 *
 */
public interface IMatch extends Comparable<IMatch> {

	/**
	 * @return The name of the document that is retrieved.
	 */
	public String getDocName();
	
	/**
	 * @return The score of the document.
	 */
	public double getScore();
	
}
