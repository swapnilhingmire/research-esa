package edu.uka.aifb.nlp;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.configuration.Configuration;
import org.apache.log4j.Logger;
import org.tartarus.snowball.SnowballStemmer;
import org.tartarus.snowball.ext.englishStemmer;
import org.tartarus.snowball.ext.frenchStemmer;
import org.tartarus.snowball.ext.germanStemmer;

import edu.uka.aifb.api.nlp.ITokenAnalyzer;
import edu.uka.aifb.api.nlp.ITokenStream;

public class MultiLingualAnalyzer implements ITokenAnalyzer {

	static Logger logger = Logger.getLogger( MultiLingualAnalyzer.class );

	static Map<Language, Class<? extends SnowballStemmer>> STEMMER_CLASSES
		= new HashMap<Language, Class<? extends SnowballStemmer>>();
	static {
		STEMMER_CLASSES.put( Language.EN, englishStemmer.class );
		STEMMER_CLASSES.put( Language.DE, germanStemmer.class );
		STEMMER_CLASSES.put( Language.FR, frenchStemmer.class );
	}
	
	private Map<Language, Set<String>> m_stopwordSets;
	
	public MultiLingualAnalyzer( Configuration config ) {
		m_stopwordSets = new HashMap<Language, Set<String>>();
		for( Language lang : Language.getSupportedLanguages() ) {
			if( config.containsKey( "nlp." + lang + ".stopword_file" ) ) {
				try {
					m_stopwordSets.put(
							lang,
							importStopwords( config.getString( "nlp." + lang + ".stopword_file" ) ) );
				}
				catch( Exception e ) {
					logger.error( e );
				}
			}
		}
	}
	
	private Set<String> importStopwords( String fileName ) throws Exception {
		logger.info( "Reading stop word file: " + fileName );
		StringBuffer temp = new StringBuffer();

		BufferedReader in = new BufferedReader( new FileReader( fileName ) );

		String line = in.readLine();
		while (line != null)
		{    
			if (line.indexOf('|') >= 0 )
				line = line.substring( 0, line.indexOf('|') ); 

			if (line.length() > 0)
			{
				temp.append( line );
				temp.append( ' ' );
			}

			line = in.readLine();
		}
		in.close ();

		Set<String> stopwordSet = new HashSet<String>();
		
		StringTokenizer st = new StringTokenizer( temp.toString() );
		while( st.hasMoreTokens() )
		{
			String token = st.nextToken().toLowerCase();
			if (token != null && token.length() > 0)
				stopwordSet.add( token );
		}

		return stopwordSet;
	}
	
	@Override
	public ITokenStream getAnalyzedTokenStream( ITokenStream ts ) {
		return new MultiLingualTokenStream( ts );
	}

	private class MultiLingualTokenStream implements ITokenStream {
		
		private ITokenStream m_tokenStream;
		private String m_currentToken;
		private Language m_currentLanguage;
		
		private Map<Language,SnowballStemmer> m_stemmers;
		
		protected MultiLingualTokenStream( ITokenStream ts ) {
			m_tokenStream = ts;
			m_stemmers = new HashMap<Language, SnowballStemmer>();
		}
		
		private SnowballStemmer getStemmer( Language lang ) {
			if( m_stemmers.containsKey( lang ) ) {
				return m_stemmers.get( lang );
			}
			else if( STEMMER_CLASSES.containsKey( lang ) ) {
				try {
					logger.debug( "Initializing stemmer for language " + lang );
					SnowballStemmer stemmer;
					stemmer = STEMMER_CLASSES.get( lang ).newInstance();
					m_stemmers.put( lang, stemmer );
					return stemmer;
				} catch (Exception e) {
					logger.error( e );
				}
			}
			return null;
		}
		
		@Override
		public Language getLanguage() {
			return m_currentLanguage;
		}

		@Override
		public String getToken() {
			return m_currentToken;
		}

		@Override
		public boolean next() {
			while( m_tokenStream.next() ) {

				String token = m_tokenStream.getToken();
				Language lang = m_tokenStream.getLanguage();

				// delete all punctuation, spaces and numbers
				token = token.replaceAll( "[=~\\p{Z}\\p{P}\\p{N}]", "" );
				
				token = token.toLowerCase();

				if( token.length() > 2 && token.length() <= 35 ) {

					if( ! Pattern.matches( "^\\p{L}+$", token ) ) {
						continue;
					}

					if( m_stopwordSets.containsKey( lang )
							&& m_stopwordSets.get( lang ).contains( token ) ) {
						continue;
					}
					
					SnowballStemmer stemmer = getStemmer( lang );
					if( stemmer != null ) {
						stemmer.setCurrent( token );
						stemmer.stem ();
						token = stemmer.getCurrent();
					}
					
					if( token.length() > 0 ) {
						m_currentToken = token;
						m_currentLanguage = lang;
						return true;
					}
				}
			}
			return false;
		}
		
	}


}
