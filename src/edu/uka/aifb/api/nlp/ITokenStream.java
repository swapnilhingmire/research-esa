package edu.uka.aifb.api.nlp;

import edu.uka.aifb.nlp.Language;

public interface ITokenStream {
	
	/**
	 * Move stream to the next token.
	 * 
	 * @return Returns true if there is a next token, false if the end of the stream is reached.
	 */
	public boolean next();

	/**
	 * @return The current token in this stream.
	 */
	public String getToken();
	
	/**
	 * @return The language of the current token.
	 */
	public Language getLanguage();
	
}
