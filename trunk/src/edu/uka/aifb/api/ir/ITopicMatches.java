package edu.uka.aifb.api.ir;

import java.util.Iterator;
import java.util.List;

public interface ITopicMatches {
	
	public Iterator<String> topicIterator();
	
	public List<IMatch> getMatches( String topic );

	public void addMatches( String topic, List<IMatch> matches );
		
}
