package edu.uka.aifb.api.ir;

public interface IScoreNormalizer {

	public void setMaxScore( double score );
	
	public void setMinScore( double score );
	
	public double normalize( double score );
		
}
