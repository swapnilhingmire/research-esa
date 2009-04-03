package edu.uka.aifb.api.document;


public interface ICollectionIterator {

	/**
	 * Method to move to the next document.
	 * 
	 * @return Returns true if there is a next document, false if the end of the collection is reached.
	 */
	public boolean next();
	
	/**
	 * @return The current document.
	 */
	public IDocument getDocument();
	
}
