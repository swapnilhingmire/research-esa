package edu.uka.aifb.api.concept;

import edu.uka.aifb.api.document.IDocument;
import edu.uka.aifb.api.nlp.ITokenAnalyzer;


public interface IConceptExtractor {

	public void setTokenAnalyzer( ITokenAnalyzer tokenAnalyzer );
	
	public IConceptVector extract( IDocument doc );
	
	public IConceptVector extract( IDocument doc, String... fields );
	
}
