package edu.uka.aifb.concept.expert;

import edu.kit.aifb.concept.index.ICVIndexEntryIterator;
import edu.kit.aifb.document.expert.IDocumentExpertIterator;

public class ConceptDocumentExpertIterator implements IDocumentExpertIterator {

	ICVIndexEntryIterator indexEntryIt;
	
	public ConceptDocumentExpertIterator( ICVIndexEntryIterator indexEntryIt ) {
		this.indexEntryIt = indexEntryIt;
	}
	
	@Override
	public double getDocumentGivenExpertProb() {
		return indexEntryIt.getValue() / indexEntryIt.getValueSum();
	}

	@Override
	public int getExpertId() {
		return indexEntryIt.getDocId();
	}

	@Override
	public boolean next() {
		return indexEntryIt.next();
	}

}
