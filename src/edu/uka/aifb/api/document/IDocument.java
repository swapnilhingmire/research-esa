package edu.uka.aifb.api.document;

import java.util.Collection;
import java.util.Set;

import edu.uka.aifb.api.nlp.ITokenStream;
import edu.uka.aifb.nlp.Language;

public interface IDocument extends Comparable<IDocument> {

	/**
	 * @return The name of this document.
	 */
	public String getName();
	
	/**
	 * @return The set of the languages of all fields.
	 */
	public Set<Language> getLanguages();
	
	/**
	 * @param field The field name.
	 * @return The language of the specified field.
	 */
	public Language getLanguage( String field );
	
	/**
	 * Set the language of a specified field to a knew value.
	 * 
	 * @param fieldName The field that should be changed.
	 * @param language The new language of the specified field.
	 */
	public void setLanguage( String field, Language language );
	
	/**
	 * @return The set of non empty fields.
	 */
	public Set<String> getFields();
	
	/**
	 * @param language
	 * @return The set of fields for the specified language.
	 */
	public Set<String> getFields( Language language );
	
	/**
	 * @return The set of texts of all fields of the document.
	 */
	public Collection<String> getText();
	
	/**
	 * @param field The field name.
	 * @return The text of the specified field.
	 */
	public String getText( String field );
	
	/**
	 * @param language
	 * @return The text of all fields in the specified language.
	 */
	public Collection<String> getText( Language language );
	
	/**
	 * @return A token stream for all fields of the document.
	 */
	public ITokenStream getTokens();
	
	/**
	 * @param field
	 * @return A token stream for the specified field.
	 */
	public ITokenStream getTokens( String... fields );
	
	/**
	 * @param language
	 * @return A token stream for all fields in the specified language.
	 */
	public ITokenStream getTokens( Language language );
	
}
