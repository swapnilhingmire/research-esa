package edu.uka.aifb.document;


import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import edu.uka.aifb.api.document.IDocument;
import edu.uka.aifb.api.nlp.ITokenStream;
import edu.uka.aifb.nlp.Language;

public class TextDocument implements IDocument {
	
	public static final String SPLIT_REGEX = "[\\s,;:!\\.\\?\\(\\)\\[\\]\\{\\}]";
	
	private String m_docName;
	
	class TextField {
		Language language;
		String text;
		
		private TextField( Language language ) {
			this.language = language;
		}
	}
	
	private Map<String,TextField> m_fields;
	
	public TextDocument( String docName ) {
		m_docName = docName;
		
		m_fields = new HashMap<String,TextField>();
	}

	@Override
	public Set<String> getFields() {
		return m_fields.keySet();
	}

	@Override
	public Set<String> getFields( Language language ) {
		Set<String> fields = new HashSet<String>();
		
		for( String field : m_fields.keySet() ) {
			if( m_fields.get( field ).language == language ) {
				fields.add( field );
			}
		}
		
		return fields;
	}

	@Override
	public Language getLanguage( String field ) {
		if( m_fields.containsKey( field ) ) {
			return m_fields.get( field ).language;
		}
		else {
			return null;
		}
	}

	@Override
	public Set<Language> getLanguages() {
		Set<Language> languages = new HashSet<Language>();
		
		for( TextField f : m_fields.values() ) {
			languages.add( f.language );
		}
		
		return languages;
	}

	@Override
	public String getName() {
		return m_docName;
	}

	private TermListField getTermListField( String fieldName ) {
		List<String> tokens = new ArrayList<String>();
		for( String token : m_fields.get( fieldName ).text.split( SPLIT_REGEX ) )
		{
			if( token.length() > 0 ) {
				tokens.add( token );
			}
		}
		
		return new TermListField( tokens, m_fields.get( fieldName ).language );
	}
	
	@Override
	public ITokenStream getTokens() {
		List<TermListField> termListFields = new ArrayList<TermListField>();
		for( String fieldName : m_fields.keySet() ) {
			termListFields.add( getTermListField( fieldName ) );
		}
		return new TermListTokenStream( termListFields );
	}

	@Override
	public ITokenStream getTokens( String... fields ) {
		List<TermListField> termListFields = new ArrayList<TermListField>();
		for( String field : fields ) {
			if( m_fields.containsKey( field ) ) {
				termListFields.add( getTermListField( field ) );
			}
		}
		return new TermListTokenStream( termListFields );
	}

	@Override
	public ITokenStream getTokens( Language language ) {
		List<TermListField> fields = new ArrayList<TermListField>();
		for( String field : m_fields.keySet() ) {
			if( m_fields.get( field ).language == language ) {
				fields.add( getTermListField( field ) );
			}
		}
		return new TermListTokenStream( fields );
	}

	@Override
	public int compareTo( IDocument o ) {
		return m_docName.compareTo( o.getName() );
	}

	@Override
	public void setLanguage( String fieldName, Language language ) {
		if( m_fields.containsKey( fieldName ) ) {
			m_fields.get( fieldName ).language = language;
		}
	}

	@Override
	public Collection<String> getText() {
		List<String> textList = new ArrayList<String>();
		
		for( TextField field : m_fields.values() ) {
			textList.add( field.text );
		}

		return textList;
	}

	@Override
	public String getText( String field ) {
		return m_fields.get( field ).text;
	}

	@Override
	public Collection<String> getText( Language language ) {
		List<String> textList = new ArrayList<String>();
		
		for( TextField field : m_fields.values() ) {
			if( field.language == language )
			{
				textList.add( field.text );
			}
		}

		return textList;
	}

	private TextField getField( String fieldName ) {
		if( m_fields.containsKey( fieldName ) ) {
			return m_fields.get( fieldName );
		}
		else {
			TextField field = new TextField( Language.UNKNOWN );
			m_fields.put( fieldName, field );
			return field;
		}
	}
	
	public void setText( String fieldName, String text ) {
		getField( fieldName ).text = text;
	}

	public void setText( String fieldName, Language language, String text ) {
		TextField field = getField( fieldName );
		field.language = language;
		field.text = text;
	}
	
}
