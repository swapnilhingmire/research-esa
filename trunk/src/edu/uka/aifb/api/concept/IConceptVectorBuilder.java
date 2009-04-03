package edu.uka.aifb.api.concept;

import org.apache.commons.configuration.Configuration;


public interface IConceptVectorBuilder {

	public void initialize( Configuration config );
	
	public IConceptVector create( String docName, int[] conceptIds, double[] conceptScores, int maxConceptId );
	
}
