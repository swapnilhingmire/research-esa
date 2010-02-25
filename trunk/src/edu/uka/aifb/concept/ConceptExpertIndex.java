package edu.uka.aifb.concept;

import java.util.HashMap;
import java.util.Map;

import uk.ac.gla.terrier.matching.ResultSet;
import edu.uka.aifb.api.concept.IConceptExtractor;
import edu.uka.aifb.api.concept.IConceptVector;
import edu.uka.aifb.api.concept.index.ICVIndexReader;
import edu.uka.aifb.api.expert.IExpertDocumentSet;
import edu.uka.aifb.api.expert.IExpertIndex;
import edu.uka.aifb.api.expert.IExpertModel;
import edu.uka.aifb.document.SingleTermTokenStream;
import edu.uka.aifb.nlp.Language;

public class ConceptExpertIndex implements IExpertIndex {

	ICVIndexReader indexReader;
	ConceptMatcher matcher;
	IExpertDocumentSet expertDocumentSet;
	IExpertModel expertModel;
	
	Map<Language,IConceptExtractor> conceptExtractors;
	
	public ConceptExpertIndex(
			ICVIndexReader indexReader, String scorerClass,
			IExpertDocumentSet expertDocumentSet,
			IExpertModel expertModel )
	{
		this.indexReader = indexReader;
		this.expertDocumentSet = expertDocumentSet;
		this.expertModel = expertModel;
		
		matcher = new ConceptMatcher( indexReader );
		matcher.setScorerClass( scorerClass );
		
		conceptExtractors = new HashMap<Language, IConceptExtractor>();
	}
	
	@Override
	public int getDocId( String docName ) {
		return indexReader.getConceptVectorId( docName );
	}

	@Override
	public String getDocName( int docId ) {
		return indexReader.getConceptVectorData( docId ).getDocName();
	}

	@Override
	public IExpertDocumentSet getExpertDocumentSet() {
		return expertDocumentSet;
	}

	@Override
	public IExpertModel getExpertModel() {
		return expertModel;
	}

	@Override
	public String getId() {
		return "ConceptIndex";
	}

	@Override
	public boolean isSupportedLanguage( Language language ) {
		return conceptExtractors.containsKey( language );
	}

	@Override
	public ResultSet match( String token, Language language ) {
		IConceptVector cv = conceptExtractors.get( language ).extract(
				"query",
				new SingleTermTokenStream( token, language ) );
		
		matcher.match( cv );
		return matcher.getResultSet();
	}

	public void setConceptExtractor( IConceptExtractor extractor, Language language ) {
		conceptExtractors.put( language, extractor );
	}
}
