package edu.uka.aifb.concept;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import no.uib.cipr.matrix.Vector;
import no.uib.cipr.matrix.sparse.SparseVector;

import org.apache.log4j.Logger;

import edu.uka.aifb.api.concept.IConceptIterator;
import edu.uka.aifb.api.concept.IConceptVector;
import edu.uka.aifb.api.concept.IConceptVectorData;

public class MTJConceptVector implements IConceptVector, Serializable {

	private static final long serialVersionUID = 7191931326501199986L;

	static Logger logger = Logger.getLogger( MTJConceptVector.class );
	
	transient private SparseVector m_v;

	private String m_docName;

	private IConceptVectorData m_data;

	public class MTJConceptVectorData implements IConceptVectorData, Serializable {
	
		private static final long serialVersionUID = 8995754838359534740L;

		public String getDocName() {
			return m_docName;
		}
		
		public int getConceptCount() {
			return m_v.getUsed();
		}
		
		public double getNorm1() {
			return m_v.norm( Vector.Norm.One );
		}
		
		public double getNorm2() {
			return m_v.norm( Vector.Norm.Two );
		}
	}
	
	public MTJConceptVector( String docName, int dim ) {
		m_docName = docName;
		
		m_v = new SparseVector( dim );
		
		m_data = new MTJConceptVectorData();
	}
	
	public double get(int key) {
		return m_v.get(key);
	}
	
	public void add(int key, double d) {
		m_v.add( key, d );
	}

	public void add( IConceptVector cv) {
		IConceptIterator it = cv.iterator();
		while( it.next() )
		{
			m_v.add( it.getId(), it.getValue() );
		}
	}

	public IConceptIterator iterator() {
		return new MTJConceptVectorIterator( m_v );
	}
	
	public String getDocName() {
		return m_docName;
	}

	public void set( int key, double d ) {
		m_v.set( key, d );
	}
	
	public int size() {
		return m_v.size();
	}
	
	private void writeObject(ObjectOutputStream out) throws IOException {
		out.defaultWriteObject();

		out.writeInt( m_v.size() );
		out.writeObject( m_v.getIndex() );
		out.writeObject( m_v.getData() );
	}

	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
		in.defaultReadObject();

		int size = in.readInt();
		int[] index = (int[])in.readObject();
		double[] data = (double[])in.readObject();
		m_v = new SparseVector( size, index, data, false );
	}

	public int hashCode() {
		return m_docName.hashCode();
	}

	public IConceptVectorData getData() {
		return m_data;
	}

	public int count() {
		return m_v.getUsed();
	}

}
