package edu.uka.aifb.api.ir;

import java.util.List;

public interface IMatchCombiner {

	public void addMatches( String id, List<IMatch> matches );
	
	public void reset();
	
	public void setCombinationWeight( String id, double weight );
	
	public List<IMatch> getCombinedMatches();
	
}
