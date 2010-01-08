package edu.uka.aifb.terrier;

import org.apache.commons.configuration.Configuration;
import org.apache.log4j.Logger;

import uk.ac.gla.terrier.matching.Matching;
import uk.ac.gla.terrier.matching.MatchingQueryTerms;
import uk.ac.gla.terrier.matching.ResultSet;
import uk.ac.gla.terrier.querying.parser.SingleTermQuery;
import uk.ac.gla.terrier.structures.Index;
import edu.uka.aifb.api.concept.IConceptExtractor;
import edu.uka.aifb.api.concept.IConceptModel;
import edu.uka.aifb.api.concept.IConceptVector;
import edu.uka.aifb.api.concept.IConceptVectorBuilder;
import edu.uka.aifb.api.document.IDocument;
import edu.uka.aifb.api.nlp.ITokenAnalyzer;
import edu.uka.aifb.api.nlp.ITokenStream;
import edu.uka.aifb.nlp.Language;
import edu.uka.aifb.tools.ConfigurationManager;

public class TerrierConceptModelExtractor implements IConceptExtractor {

	static final String[] REQUIRED_PROPERTIES = {
		"concepts.model_class",
	};
	
	static Logger logger = Logger.getLogger( TerrierConceptModelExtractor.class );
	
	private Matching m_matching;
	
	private Language m_language;
	
	private int m_maxConceptId;
	
	private IConceptModel conceptModel;
	
	private ITokenAnalyzer m_tokenAnalyzer;
	
	protected TerrierConceptModelExtractor( Configuration config, Index index, Language language ) throws Exception {
		ConfigurationManager.checkProperties( config, REQUIRED_PROPERTIES );
		
		m_language = language;
		m_maxConceptId = index.getDocumentIndex().getNumberOfDocuments();
		
		logger.info( "Setting concept model: " + config.getString( "concepts.model_class" ) );
		conceptModel = (IConceptModel)Class.forName( config.getString( "concepts.model_class" ) ).newInstance();

		m_matching = new Matching( index );
		m_matching.setModel( conceptModel.getWeightingModel() );
	}
	
	public IConceptVector extract( IDocument doc ) {
		return extract( doc, doc.getTokens( m_language ) );
	}
	
	public IConceptVector extract( IDocument doc, String... fields ) {
		return extract( doc, doc.getTokens( fields ) );
	}

	private IConceptVector extract( IDocument doc, ITokenStream queryTokenStream ) {
		logger.info( "Extracting concepts for document " + doc.getName() );
		
		IConceptVectorBuilder builder = conceptModel.getConceptVectorBuilder();
		builder.reset( doc.getName(), m_maxConceptId );
		
		/*
		 * Build query
		 */
		ITokenStream ts = queryTokenStream;
		if( m_tokenAnalyzer != null) {
			ts = m_tokenAnalyzer.getAnalyzedTokenStream( ts );
		}
		
		while( ts.next() ) {
			if( ts.getToken() == null || ts.getToken().length() == 0 ) {
				logger.debug( "Skipping empty token!" );
				continue;
			}
			
			logger.debug( "Setting query term " + ts.getToken() );
			SingleTermQuery query = new SingleTermQuery( ts.getToken() );

			/*
			 * Search
			 */
			MatchingQueryTerms mqt = new MatchingQueryTerms();
			query.obtainQueryTerms( mqt );
			mqt.addDocumentScoreModifier( conceptModel );
			
			m_matching.match( "ESA", mqt );
			
			/*
			 * Create concept vector
			 */
			ResultSet rs = m_matching.getResultSet();
			logger.info( "Found " + rs.getResultSize() + " matches in index." );
			
			builder.addScores( rs.getDocids(), rs.getScores() );
		}
		
		return builder.getConceptVector();
	}

	public void setTokenAnalyzer( ITokenAnalyzer tokenAnalyzer ) {
		m_tokenAnalyzer = tokenAnalyzer;
	}

	@Override
	public IConceptVectorBuilder getConceptVectorBuilder() {
		return conceptModel.getConceptVectorBuilder();
	}
}
