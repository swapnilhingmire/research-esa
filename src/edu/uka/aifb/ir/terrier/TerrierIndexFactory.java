package edu.uka.aifb.ir.terrier;

import org.apache.commons.configuration.Configuration;
import org.apache.log4j.Logger;

import uk.ac.gla.terrier.indexing.BasicIndexer;
import uk.ac.gla.terrier.indexing.Collection;
import uk.ac.gla.terrier.indexing.Indexer;
import uk.ac.gla.terrier.structures.Index;
import edu.uka.aifb.api.document.ICollection;
import edu.uka.aifb.api.ir.terrier.IIndexFactory;
import edu.uka.aifb.api.nlp.ITokenAnalyzer;
import edu.uka.aifb.nlp.Language;
import edu.uka.aifb.tools.ConfigurationManager;

public class TerrierIndexFactory implements IIndexFactory {

	static final String[] REQUIRED_PROPERTIES = {
		"terrier.%1.%2.index_prefix"
	};
	
	static Logger logger = Logger.getLogger( TerrierIndexFactory.class );
	
	private Configuration m_config;
	
	public TerrierIndexFactory( Configuration config ) {
		m_config = config;
	}
	
	@Override
	public void buildIndex( String indexId, Language lang, ITokenAnalyzer tokenAnalyzer, 
			ICollection collection, boolean overwrite, String... fields ) {
		ConfigurationManager.checkProperties( m_config, REQUIRED_PROPERTIES, indexId, lang );
		
		String indexDir =  m_config.getString(
				"terrier." + indexId + "." + lang + ".index_prefix" );
		
		if( !overwrite && Index.existsIndex( "index", indexDir ) ) {
			logger.info( "Index seems to exist already." );
			return;
		}
		
		logger.info( "Initializing indexer, index id: " + indexId + ", index dir: " + indexDir );
		Indexer indexer = new BasicIndexer(	"index", indexDir );
		
		Collection wrappedCollection = new CollectionWrapper(
				collection,
				tokenAnalyzer,
				fields );
		

		logger.info( "Creating direct index ..." );
		Collection[] collections = { wrappedCollection };
		indexer.createDirectIndex( collections );
		
		logger.info( "Creating inverted index ..." );
		indexer.createInvertedIndex();
	}

	@Override
	public Index readIndex( String indexId, Language lang ) {
		ConfigurationManager.checkProperties( m_config, REQUIRED_PROPERTIES, indexId, lang );

		Index index = Index.createIndex(
				"index",
				m_config.getString( "terrier." + indexId + "." + lang + ".index_prefix" ) );
		
		return index;
	}

}
