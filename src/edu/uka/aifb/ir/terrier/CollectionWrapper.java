package edu.uka.aifb.ir.terrier;

import uk.ac.gla.terrier.indexing.Collection;
import uk.ac.gla.terrier.indexing.Document;
import edu.uka.aifb.api.document.ICollection;
import edu.uka.aifb.api.document.ICollectionIterator;
import edu.uka.aifb.api.nlp.ITokenAnalyzer;


public class CollectionWrapper implements Collection {

	ICollection m_collection;
	ICollectionIterator m_collectionIt;

	ITokenAnalyzer m_analyzer;
	String[] m_fields;
	
	boolean m_hasNext;
	
	public CollectionWrapper( ICollection collection, ITokenAnalyzer analyzer, String... fields ) {
		this( collection, analyzer );
		
		m_fields = fields;
	}

	public CollectionWrapper( ICollection collection, ITokenAnalyzer analyzer ) {
		this( collection );
		
		m_analyzer = analyzer;
	}

	public CollectionWrapper( ICollection collection ) {
		m_collection = collection;
		m_collectionIt = collection.iterator();
		
		m_hasNext = true;
	}
	
	public void close() {
	}

	public boolean endOfCollection() {
		return ! m_hasNext;
	}

	public String getDocid() {
		return m_collectionIt.getDocument().getName();
	}

	public Document getDocument() {
		//System.out.println( "Creating wrapper for document " + m_collectionIt.getDocument().getName() );
		return new DocumentWrapper( m_collectionIt.getDocument(), m_analyzer, m_fields );
	}

	public boolean nextDocument() {
		m_hasNext = m_collectionIt.next();
		return m_hasNext;
	}

	public void reset() {
		m_collectionIt = m_collection.iterator();
		
		m_hasNext = true;
	}

}
