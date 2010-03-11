package edu.uka.aifb.concept.expert;

import java.util.HashMap;
import java.util.Map;

import uk.ac.gla.terrier.matching.CollectionResultSet;
import uk.ac.gla.terrier.matching.ResultSet;
import edu.uka.aifb.api.concept.IConceptExtractor;
import edu.uka.aifb.api.concept.IConceptIterator;
import edu.uka.aifb.api.concept.IConceptVector;
import edu.uka.aifb.api.concept.index.ICVIndexReader;
import edu.uka.aifb.api.expert.IDocumentExpertIterator;
import edu.uka.aifb.api.expert.IExpertDocumentSet;
import edu.uka.aifb.api.expert.IExpertIndex;
import edu.uka.aifb.document.SingleTermTokenStream;
import edu.uka.aifb.nlp.Language;

public class ConceptExpertIndex implements IExpertIndex {

	ICVIndexReader indexReader;
	ResultSet rs;
	ConceptExpertDocumentSet eds;
	
	Map<Language,IConceptExtractor> conceptExtractors;
	
	public ConceptExpertIndex( ICVIndexReader indexReader ) {
		this.indexReader = indexReader;
		conceptExtractors = new HashMap<Language, IConceptExtractor>();
		
		rs = new CollectionResultSet( indexReader.getNumberOfDocuments() );
		eds = new ConceptExpertDocumentSet( indexReader );
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
		return eds;
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
		
		int[] ids = rs.getDocids();
		double[] scores = rs.getScores();
		
		int count = 0;
		IConceptIterator it = cv.orderedIterator();
		while( it.next() ) {
			ids[count] = it.getId();
			scores[count] = it.getValue();
			count++;
		}
		rs.setExactResultSize( count );
		rs.setResultSize( count );
		return rs;
	}

	@Override
	public IDocumentExpertIterator getDocumentExpertIterator( int docId ) {
		return new ConceptDocumentExpertIterator( indexReader.getIndexEntryIterator( docId ) );
	}

	public void setConceptExtractor( IConceptExtractor extractor, Language language ) {
		conceptExtractors.put( language, extractor );
	}

}
