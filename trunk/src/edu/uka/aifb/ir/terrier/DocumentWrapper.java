package edu.uka.aifb.ir.terrier;

import java.io.Reader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import uk.ac.gla.terrier.indexing.Document;
import edu.uka.aifb.api.document.IDocument;
import edu.uka.aifb.api.nlp.ITokenAnalyzer;
import edu.uka.aifb.api.nlp.ITokenStream;


public class DocumentWrapper implements Document {

	ITokenStream m_tokenStream;
	
	private Map<String,String> m_properties;
	
	private Set<String> m_fields;	
	
	private boolean m_hasNext;
	
	
	public DocumentWrapper( IDocument doc, ITokenAnalyzer analyzer, String... fields ) {
		if( fields != null && fields.length > 0 ) {
			m_tokenStream = doc.getTokens( fields );
		}
		else {
			m_tokenStream = doc.getTokens();
		}
		
		if( analyzer != null ) {
			m_tokenStream = analyzer.getAnalyzedTokenStream( m_tokenStream );
		}
		
		m_properties = new HashMap<String,String>();
		
		m_fields = new HashSet<String>();
		m_fields.add( "text" );
		
		m_hasNext = true;
	}
	
	public boolean endOfDocument() {
		return ! m_hasNext;
	}

	public Map<String, String> getAllProperties() {
		return m_properties;
	}

	public Set<String> getFields() {
		return m_fields;
	}

	public String getNextTerm() {
		m_hasNext = m_tokenStream.next(); 
		if( m_hasNext ) {
			//System.out.print( "[" + m_tokenStream.getToken() + "]\n" );
			return m_tokenStream.getToken();
		}
		else {
			//System.out.print( "NULL\n" );
			return null;
		}
	}

	public String getProperty( String arg0 ) {
		return m_properties.get( arg0 );
	}

	public Reader getReader() {
		return null;
	}

}
