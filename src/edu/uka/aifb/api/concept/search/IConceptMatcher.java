package edu.uka.aifb.api.concept.search;

import java.util.List;

import edu.uka.aifb.api.concept.IConceptVector;
import edu.uka.aifb.api.ir.IMatch;

/**
 * This specifies a class for concept based search. Based on a ceratin scoring model
 * all matches for a specified query are returned.
 * 
 * @author pso
 *
 */
public interface IConceptMatcher {
	
	/**
	 * This sets the scoring model for the concept based retrieval.
	 * 
	 * @param className The name of the IScorer class that implements the
	 * concept based retrieval model.
	 */
	public void setScorerClass( String className );
	
	/**
	 * This function performs a complete retrieval step for a specified query which
	 * is represented by a concept vector. A sorted list of matches is returned that
	 * contain a ranking of documents relevant to the query based on the retrieval model.  
	 * 
	 * @param queryCV The concept vector of the query that should be searched for.
	 * @return A sorted list of matches containing relevant documents and their scores.
	 */
	public List<IMatch> getMatches( IConceptVector queryCV );
	
}
