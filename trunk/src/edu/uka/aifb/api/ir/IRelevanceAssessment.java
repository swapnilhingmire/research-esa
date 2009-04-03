package edu.uka.aifb.api.ir;

import java.util.Set;

public interface IRelevanceAssessment {

	public enum Relevance {
		YES, NO, UNKNOWN
	}
	
	public Set<String> getRelevantDocuments( String topic );
	
	public Relevance getRelevance( String topic, String document );
	
}
