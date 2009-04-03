package edu.uka.aifb.api.nlp;

import edu.uka.aifb.api.document.IDocument;
import edu.uka.aifb.nlp.Language;

public interface ILanguageDetection {

	/**
	 * Classifies the language of a specified text.
	 * 
	 * @param text The text that will be classified.
	 * @return The detected language of the text.
	 */
	public Language classify( String text );
	
	/**
	 * For all fields with undefined language the text
	 * is classified and the language of this fields is
	 * set to the detected languages. 
	 * 
	 * @param document The document of which the fields with undefined
	 * language will be classified.
	 */
	public void classifyDocument( IDocument document );
	
}
