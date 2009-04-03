package edu.uka.aifb.ir.terrier;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import uk.ac.gla.terrier.matching.Matching;
import uk.ac.gla.terrier.matching.MatchingQueryTerms;
import uk.ac.gla.terrier.matching.Model;
import uk.ac.gla.terrier.matching.ResultSet;
import uk.ac.gla.terrier.matching.dsms.DocumentScoreModifier;
import uk.ac.gla.terrier.matching.models.TF_IDF;
import uk.ac.gla.terrier.matching.models.WeightingModel;
import uk.ac.gla.terrier.querying.parser.Query;
import uk.ac.gla.terrier.structures.DocumentIndex;
import uk.ac.gla.terrier.structures.Index;
import edu.uka.aifb.api.ir.IMatch;
import edu.uka.aifb.api.ir.terrier.ITerrierSearch;
import edu.uka.aifb.api.nlp.ITokenAnalyzer;
import edu.uka.aifb.ir.Match;

public class TerrierSearch implements ITerrierSearch {

	static Logger logger = Logger.getLogger( TerrierSearch.class );
	
	Index m_index;
	Model m_model;
	ITokenAnalyzer m_analyzer;
	
	DocumentIndex m_docIndex;
	Matching m_matching;
	
	
	public List<IMatch> getMatches( Query query ) {
		List<IMatch> matches = new ArrayList<IMatch>();
		
		MatchingQueryTerms mqt = new MatchingQueryTerms();
		query.obtainQueryTerms( mqt );

		if( mqt.getTerms() != null ) {
			String s = "";
			for( String term : mqt.getTerms() ) {
				s += " " + term;
			}
			logger.info( "Query:" + s );

			m_matching.match( "terrier", mqt );
	
			ResultSet resultSet = m_matching.getResultSet();
			int[] resultIds = resultSet.getDocids();
			double[] resultScores = resultSet.getScores();
			
			logger.info( "Found " + resultSet.getExactResultSize() + " matches." );
			
			for( int i=0; i<resultSet.getResultSize(); i++ ) {
				matches.add( new Match(
						m_docIndex.getDocumentNumber( resultIds[i] ),
						resultScores[i] ) );
			}
		}
		
		return matches;
	}
	
	@Override
	public void setTokenAnalyzer(ITokenAnalyzer analyzer) {
		m_analyzer = analyzer;
	}

	@Override
	public void setIndex(Index index, WeightingModel model) {
		m_docIndex = index.getDocumentIndex();
		
		m_matching = new Matching( index );
		m_matching.setModel( model );
	}

	@Override
	public void setIndex(Index index, WeightingModel model, DocumentScoreModifier modifier ) {
		setIndex( index, model );
		
		m_matching.addDocumentScoreModifier( modifier );
	}

}
