package edu.uka.aifb.api.document;


public interface ICollection {

	/**
	 * @return An iterator for the documents in this collection.
	 */
	public ICollectionIterator iterator();

	/**
	 * @return The number of documents in this collection.
	 */
	public int size();
	
	/**
	 * @param docName The name of the document.
	 * @return The document with the specified name. If the document does not exist, null is returned.
	 */
	public IDocument getDocument( String docName );
	
}
