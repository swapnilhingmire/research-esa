package edu.uka.aifb.concept;


import no.uib.cipr.matrix.sparse.SparseVector;
import edu.uka.aifb.api.concept.IConceptIterator;


public class MTJConceptVectorIterator implements IConceptIterator {

	private int[] index;
	private double[] values;
	private int used;
	
	private int currentPos;
	
	public MTJConceptVectorIterator( SparseVector v ) {
		used = v.getUsed();
		index = v.getIndex();
		values = v.getData();
		reset();
	}
	
	public int getId() {
		return index[currentPos];
	}

	public double getValue() {
		return values[currentPos];
	}

	public boolean next() {
		currentPos++;
		if( currentPos < used ) {
			return true;
		}
		else {
			return false;
		}
	}

	public void reset() {
		currentPos = -1;
	}

}
