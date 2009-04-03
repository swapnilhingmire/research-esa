package edu.uka.aifb.api.ir;

import java.io.File;
import java.io.IOException;

public interface ITrecFileAccess {

	public void writeTrecResults( ITopicMatches topicMatches, File outputFile, String runId, int maxResults ) throws IOException;

	public IRelevanceAssessment readRelevanceAssessment( File inputFile ) throws IOException;
	
}
