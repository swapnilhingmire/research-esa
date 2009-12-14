package edu.uka.aifb.concept;


import uk.ac.gla.terrier.sorting.HeapSort;
import no.uib.cipr.matrix.sparse.SparseVector;
import edu.uka.aifb.api.concept.IConceptIterator;


public class MTJConceptVectorOrderedIterator implements IConceptIterator {

	private int[] index;
	private double[] values;
	private int used;
	
	private int currentPos;
	
	public MTJConceptVectorOrderedIterator( SparseVector v ) {
		used = v.getUsed();
		index = v.getIndex().clone();
		values = v.getData().clone();
		HeapSort.heapSort( values, index );
		reset();
	}
	
	public int getId() {
		return index[currentPos];
	}

	public double getValue() {
		return values[currentPos];
	}

	public boolean next() {
		currentPos--;
		if( currentPos >= index.length - used ) {
			return true;
		}
		else {
			return false;
		}
	}

	public void reset() {
		currentPos = index.length;
	}

}
