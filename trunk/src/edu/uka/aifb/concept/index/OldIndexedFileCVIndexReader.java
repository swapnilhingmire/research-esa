package edu.uka.aifb.concept.index;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.RandomAccessFile;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.commons.configuration.Configuration;
import org.apache.log4j.Logger;

import edu.uka.aifb.api.concept.IConceptVectorData;
import edu.uka.aifb.api.concept.index.ICVIndexEntryIterator;
import edu.uka.aifb.api.concept.index.ICVIndexReader;
import edu.uka.aifb.nlp.Language;
import edu.uka.aifb.tools.ConfigurationManager;

public class OldIndexedFileCVIndexReader implements ICVIndexReader {

	static final short CV_ENTRY_ITEM_BYTE_SIZE = 12;
	
	static final String[] REQUIRED_PROPERTIES = {
		"concepts.base_dir",
		"concepts.%1.%2.file_prefix",
		"concepts.cache_size",
	};
	
	static Logger logger = Logger.getLogger( OldIndexedFileCVIndexReader.class );
	
	private IndexedFileCVData m_cvData;
	private IndexedFilePositions m_positions;
	
	private File m_conceptsFile;
	private File m_positionsFile;
	private File m_cvDataFile;
	
	private RandomAccessFile m_conceptsRAF;
	private byte[] m_buffer;
	private ByteArrayInputStream m_bufferInputStream;
	
	private Map<Integer,TroveCVIndexEntry> m_cachedIndexEntries;
	private long m_cacheByteSize = 0;
	private int m_maxCacheKByteSize;
	
	public OldIndexedFileCVIndexReader( Configuration config, String name, Language language ) throws Exception {
		ConfigurationManager.checkProperties( config, REQUIRED_PROPERTIES, name, language );
		
		String pathPrefix =	config.getString( "concepts.base_dir" ) + "/" +
			config.getString( "concepts." + name + "." + language + ".file_prefix" );
		m_conceptsFile = new File( pathPrefix + ".concepts" );
		m_positionsFile = new File( pathPrefix + ".positions" );
		m_cvDataFile = new File( pathPrefix + ".cv_data" );

		logger.info( "Reading positions from " + m_positionsFile.getName() );
		m_positions = IndexedFilePositions.readFromFile( m_positionsFile );
		logger.info( "Found " + m_positions.size() + " position entries." );
		
		logger.info( "Reading concept vector data from " + m_cvDataFile.getName() );
		m_cvData = IndexedFileCVData.readFromFile( m_cvDataFile );
		logger.info( "Found " + m_cvData.size() + " concept vector data entries." );
		
		m_conceptsRAF = new RandomAccessFile( m_conceptsFile, "r" );
		m_buffer = new byte[10000000];
		m_bufferInputStream = new ByteArrayInputStream( m_buffer );
		
		m_cachedIndexEntries = new LinkedHashMap<Integer, TroveCVIndexEntry>();
		
		m_cacheByteSize = 0;
		m_maxCacheKByteSize = config.getInt( "concepts.cache_size" );
	}

	public IConceptVectorData getConceptVectorData( int cvId ) {
		return m_cvData.get( cvId );
	}

	public ICVIndexEntryIterator getIndexEntryIterator( int conceptId ) {
		return loadEntry( conceptId ).iterator();
	}
	
	synchronized private TroveCVIndexEntry loadEntry( int conceptId ) {
		TroveCVIndexEntry entry = m_cachedIndexEntries.remove( conceptId );

		if( entry != null ) {
			logger.debug( "Found index entry in cache for concept " + conceptId );
			m_cacheByteSize -= entry.numberOfDocuments() * CV_ENTRY_ITEM_BYTE_SIZE;
		}
		else {
			try {
				logger.debug( "Reading index entry from file for concept " + conceptId );
				m_conceptsRAF.seek( m_positions.getFileOffset( conceptId ) );
				m_conceptsRAF.read( m_buffer, 0, m_positions.getByteSize( conceptId ) );

				m_bufferInputStream.reset();
				DataInputStream dataIn = new DataInputStream( m_bufferInputStream );

				entry = TroveCVIndexEntry.readFromDataInput( dataIn );
			}
			catch( Exception e ) {
				logger.error( e );
			}
		}

		if( entry != null ) {
			m_cachedIndexEntries.put( conceptId, entry );
			m_cacheByteSize += entry.numberOfDocuments() * CV_ENTRY_ITEM_BYTE_SIZE;
		}
		
		while( m_cacheByteSize / 1024 > m_maxCacheKByteSize ) {
			Integer key = m_cachedIndexEntries.keySet().iterator().next();
			logger.debug( "Removing eldest index entry for concept " + key );
			TroveCVIndexEntry eldestEntry = m_cachedIndexEntries.remove( key );
			m_cacheByteSize -= eldestEntry.numberOfDocuments() * CV_ENTRY_ITEM_BYTE_SIZE;
		}
		
		logger.debug( "Cache byte size: " + m_cacheByteSize );
		
		return entry;			
	}

	public int getNumberOfDocuments() {
		return m_cvData.size();
	}

	public int getDocumentFrequency( int conceptId ) {
		return loadEntry( conceptId ).numberOfDocuments();
	}
	
}
