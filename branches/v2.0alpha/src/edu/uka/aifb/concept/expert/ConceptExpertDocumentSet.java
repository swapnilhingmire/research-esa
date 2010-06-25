package edu.uka.aifb.concept.expert;

import edu.uka.aifb.api.concept.index.ICVIndexReader;
import edu.uka.aifb.api.expert.IExpertDocumentSet;

public class ConceptExpertDocumentSet implements IExpertDocumentSet {

	ICVIndexReader indexReader;
	
	public ConceptExpertDocumentSet( ICVIndexReader indexReader ) {
		this.indexReader = indexReader;
	}
	
	@Override
	public int getExpertId( String expertName ) {
		return indexReader.getConceptVectorId( expertName );
	}

	@Override
	public String getExpertName( int expertId ) {
		return indexReader.getConceptVectorData( expertId ).getDocName();
	}

	@Override
	public int getNumberOfExperts() {
		return indexReader.getNumberOfDocuments();
	}

}
