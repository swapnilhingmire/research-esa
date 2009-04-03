package edu.uka.aifb.document;

import java.util.Collection;
import java.util.Iterator;

import edu.uka.aifb.api.nlp.ITokenStream;
import edu.uka.aifb.nlp.Language;

class TermListTokenStream implements ITokenStream {

	private Iterator<TermListField> m_termListFieldIt;
	private Iterator<String> m_termListIt;

	private TermListField m_currentField;
	private String m_currentTerm;

	protected TermListTokenStream( TermListField field ) {
		m_currentField = field;
		m_termListIt = field.getTerms().iterator();
	}
	
	protected TermListTokenStream( Collection<TermListField> fieldSet ) {
		m_termListFieldIt = fieldSet.iterator();
	}

	public Language getLanguage() {
		if( m_currentField != null ) {
			return m_currentField.getLanguage();
		}
		else {
			return null;
		}
	}

	public String getToken() {
		return m_currentTerm;
	}

	public boolean next() {
		while( m_termListIt == null || ! m_termListIt.hasNext() ) {
			if( m_termListFieldIt != null && m_termListFieldIt.hasNext() ) {
				m_currentField = m_termListFieldIt.next();
				m_termListIt = m_currentField.getTerms().iterator();
			}
			else {
				return false;
			}
		}

		m_currentTerm = m_termListIt.next();
		return true;
	}

}
