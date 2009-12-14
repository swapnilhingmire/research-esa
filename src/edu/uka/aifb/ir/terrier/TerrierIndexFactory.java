package edu.uka.aifb.ir.terrier;

import java.io.File;
import java.util.List;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

import uk.ac.gla.terrier.indexing.BasicIndexer;
import uk.ac.gla.terrier.indexing.BasicSinglePassIndexer;
import uk.ac.gla.terrier.indexing.Collection;
import uk.ac.gla.terrier.indexing.Indexer;
import uk.ac.gla.terrier.structures.Index;
import edu.uka.aifb.api.document.ICollection;
import edu.uka.aifb.api.ir.terrier.IIndexFactory;
import edu.uka.aifb.api.nlp.ITokenAnalyzer;
import edu.uka.aifb.nlp.Language;

public class TerrierIndexFactory implements IIndexFactory {

	static Logger logger = Logger.getLogger( TerrierIndexFactory.class );
	
	@Override
	public void buildIndex( String indexId, ITokenAnalyzer tokenAnalyzer, 
			ICollection collection, boolean overwrite, String... fields ) {
		buildIndex( indexId, (Language)null, tokenAnalyzer, collection, overwrite, fields );
	}
	
	@Override
	public void buildIndex( String indexId, Language lang, ITokenAnalyzer tokenAnalyzer, 
			ICollection collection, boolean overwrite, String... fields ) {
		String prefix = indexId;
		if( lang != null && lang != Language.UNKNOWN ) {
			prefix += "_" + lang;
		}
		
		if( Index.existsIndex( "index", prefix ) ) {
			if( ! overwrite ) {
				logger.info( "Index seems to exist already, skipping build of index." );
				return;
			}
			else {
				deleteIndexFiles( prefix );
			}
		}
		
		Collection wrappedCollection;
		if( lang != null && lang != Language.UNKNOWN ) {
			wrappedCollection = new CollectionWrapper(
					collection,
					tokenAnalyzer,
					lang );
		}
		else {
			wrappedCollection = new CollectionWrapper(
				collection,
				tokenAnalyzer,
				fields );
		}

		logger.info( "Initializing indexer, index: " + prefix );
		Indexer indexer = new BasicIndexer(	"index", prefix );

		logger.info( "Creating direct index ..." );
		Collection[] collections = { wrappedCollection };
		indexer.createDirectIndex( collections );
		
		logger.info( "Creating inverted index ..." );
		indexer.createInvertedIndex();
	}

	@Override
	public void buildIndex( String indexId, List<Language> lang, ITokenAnalyzer tokenAnalyzer, 
			ICollection collection, boolean overwrite ) {
		logger.error( "Not implemented!" );
	}
	
	public void buildIndexSinglePass( String indexId, Language lang, ITokenAnalyzer tokenAnalyzer, 
			ICollection collection, boolean overwrite, String... fields ) {
		String prefix = indexId;
		if( lang != null && lang != Language.UNKNOWN ) {
			prefix += "_" + lang;
		}
		
		if( Index.existsIndex( "index", prefix ) ) {
			if( ! overwrite ) {
				logger.info( "Index seems to exist already, skipping build of index." );
				return;
			}
			else {
				deleteIndexFiles( prefix );
			}
		}
		
		Collection wrappedCollection;
		if( lang != null && lang != Language.UNKNOWN ) {
			wrappedCollection = new CollectionWrapper(
					collection,
					tokenAnalyzer,
					lang );
		}
		else {
			wrappedCollection = new CollectionWrapper(
				collection,
				tokenAnalyzer,
				fields );
		}

		logger.info( "Initializing indexer, index: " + prefix );
		BasicSinglePassIndexer indexer = new BasicSinglePassIndexer( "index", prefix );

		logger.info( "Creating inverted index ..." );
		Collection[] collections = { wrappedCollection };
		indexer.createInvertedIndex( collections );
	}

	@Override
	public Index readIndex( String indexId ) {
		return readIndex( indexId, null );
	}
	
	@Override
	public Index readIndex( String indexId, Language lang ) {
		String prefix = indexId;
		if( lang != null && lang != Language.UNKNOWN ) {
			prefix += "_" + lang;
		}

		logger.info( "Initializing index: " + prefix );
		Index index = Index.createIndex( "index", prefix );
		if( index == null ) {
			logger.error( "Index could not be loaded!" );
		}
		return index;
	}

	private void deleteIndexFiles( String prefix ) {
		logger.info( "Index seems to exist already, deleting files." );
		String terrierHome = System.getProperty( "terrier.home" );
		logger.info( "Deleting all index file with prefix " + terrierHome + "/var/index/" + prefix );
		
		Pattern filePattern = Pattern.compile( "^" + prefix + "\\.\\w+$" );
		Pattern dirPattern = Pattern.compile( "^" + prefix + "_\\d+$" );
		
		try {
			File terrierIndexDir = new File( terrierHome + "/var/index" );
			for( File f : terrierIndexDir.listFiles() ) {
				if( f.isFile() && filePattern.matcher( f.getName() ).matches() ) {
					recursiveDelete( f );
				}
				else if( f.isDirectory() && dirPattern.matcher( f.getName() ).matches() ) {
					recursiveDelete( f );
				}
			}
		}
		catch( Exception e ) {
			logger.warn( "Exception while deleting old index files: " + e );
		}
	}
	
	private void recursiveDelete( File file ) { 
		if( file.isDirectory() ) {
			for( File f : file.listFiles() ) {
				recursiveDelete( f );
			}
		}
		logger.debug( "Deleting " + file.getAbsolutePath() );
		file.delete();
	}
}
