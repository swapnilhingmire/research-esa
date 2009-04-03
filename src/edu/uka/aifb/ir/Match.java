package edu.uka.aifb.ir;

import edu.uka.aifb.api.ir.IMatch;



public class Match implements IMatch {

	private String m_docName;
	private double m_score;
		
	public Match( String docName, double score ) {
		m_docName = docName;
		m_score = score;
	}
	
	public String getDocName() {
		return m_docName;
	}
	
	public double getScore() {
		return m_score;
	}
	
	public int compareTo( IMatch match ) {
		if( m_score > ((Match)match).m_score ) {
			return -1;
		}
		else if( m_score < ((Match)match).m_score ) {
			return 1;
		}
		else {
			return 0;
		}
	}
}
