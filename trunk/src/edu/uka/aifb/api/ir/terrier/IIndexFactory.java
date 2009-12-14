package edu.uka.aifb.api.ir.terrier;

import java.util.List;

import uk.ac.gla.terrier.structures.Index;
import edu.uka.aifb.api.document.ICollection;
import edu.uka.aifb.api.nlp.ITokenAnalyzer;
import edu.uka.aifb.nlp.Language;

public interface IIndexFactory {

	public void buildIndex( String indexId, Language lang, ITokenAnalyzer tokenAnalyzer, ICollection collection, boolean overwrite, String... fields );

	public void buildIndex( String indexId, List<Language> lang, ITokenAnalyzer tokenAnalyzer, ICollection collection, boolean overwrite );

	public void buildIndex( String indexId, ITokenAnalyzer tokenAnalyzer, ICollection collection, boolean overwrite, String... fields );
	
	public void buildIndexSinglePass( String indexId, Language lang, ITokenAnalyzer tokenAnalyzer, ICollection collection, boolean overwrite, String... fields );
	
	public Index readIndex( String indexId );
	public Index readIndex( String indexId, Language lang );
	
}
