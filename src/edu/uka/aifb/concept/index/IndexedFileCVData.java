package edu.uka.aifb.concept.index;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import edu.uka.aifb.api.concept.IConceptVectorData;


public class IndexedFileCVData implements Serializable {

	private static final long serialVersionUID = -7535661407888974211L;

	private List<IConceptVectorData> m_idToCVData;
	
	public IndexedFileCVData() {
		m_idToCVData = new ArrayList<IConceptVectorData>();
	}
	
	public int add( IConceptVectorData data ) {
		m_idToCVData.add( data );
		return m_idToCVData.size() - 1;
	}
	
	public IConceptVectorData get( int id ) {
		return m_idToCVData.get( id );
	}
	
	public void saveToFile( File file ) throws IOException {
		ObjectOutputStream objectOut = new ObjectOutputStream(
				new BufferedOutputStream( new FileOutputStream (
						file ) ) );
		
		objectOut.writeObject( this );
		
		objectOut.close();
	}
	
	public int size() {
		return m_idToCVData.size();
	}
	
	static public IndexedFileCVData readFromFile( File file ) throws IOException, ClassNotFoundException {
		ObjectInputStream objectIn = new ObjectInputStream( 
				new BufferedInputStream( new FileInputStream( 
						file ) ) );
		
		IndexedFileCVData cvData = (IndexedFileCVData)objectIn.readObject();
		objectIn.close();
		
		return cvData;
	}
	
}
