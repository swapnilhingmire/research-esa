package edu.uka.aifb.nlp;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;


public class Language {
	
	public static Language CAT = new Language( "cat", "catalan", "katalanisch" );
	public static Language DE = new Language( "de", "german", "deutsch" );
	public static Language DK = new Language( "dk", "danish", "daenisch" );
	public static Language EE = new Language( "ee", "estonian", "estnisch" );
	public static Language EN = new Language( "en", "english", "englisch" );
	public static Language FI = new Language( "fi", "finnish", "finnisch" );
	public static Language FR = new Language( "fr", "french", "franzoesisch" );
	public static Language IT = new Language( "it", "italian", "italienisch" );
	public static Language JP = new Language( "jp", "japanese", "japanisch" );
	public static Language KR = new Language( "kr", "korean", "koreanisch" );
	public static Language NL = new Language( "nl", "dutch", "niederländisch" );
	public static Language NO = new Language( "no", "norwegian", "norwegisch" );
	public static Language SE = new Language( "se", "swedish", "schwedisch" );
	public static Language SORB = new Language( "sorb", "sorbian", "sorbisch" );
	public static Language TR = new Language( "tr", "turkish", "tuerkisch" );
	public static Language SP = new Language( "sp", "spanish", "spanisch" );
	public static Language UNKNOWN = new Language( "unknown" );
	
	private static Map<String,Language> s_labelToLanguage;
	private static Map<Language,String> s_languageToLabel;
	
	private Language( String... labels ) {
		if( s_labelToLanguage == null ) {
			s_labelToLanguage = new HashMap<String,Language>();
		}
		for( String label : labels )
		{
			s_labelToLanguage.put( label, this );
		}
		
		if( s_languageToLabel == null ) {
			s_languageToLabel = new HashMap<Language,String>();
		}
		if( labels.length > 0 ) {
			s_languageToLabel.put( this, labels[0] );
		}
	}

	public static Language getLanguage( String label ) {
		return s_labelToLanguage.get( label );
	} 
	
	public static Collection<Language> getSupportedLanguages() {
		return s_languageToLabel.keySet();
	}
	
	public String toString() {
		return s_languageToLabel.get( this );
	}
}

