package edu.kit.aifb.wikipedia.mlc;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Formatter;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;

import edu.kit.aifb.IJdbcStatementBuffer;
import edu.kit.aifb.nlp.Language;
import gnu.trove.TIntArrayList;

public class MLCDatabase {

	private static Logger logger = Logger.getLogger( MLCDatabase.class );
	
	String mlcTable;
	String mlcCategorylinksTable;
	IJdbcStatementBuffer jsb;
	TIntArrayList conceptIds;
	
	@Required
	public void setMlcTable( String table ) {
		mlcTable = table;
	}
	
	@Autowired
	public void setJdbcStatementBuffer( IJdbcStatementBuffer jsb ) {
		this.jsb = jsb;
	}
	
	public void setMlcCategorylinksTable( String table ) {
		mlcCategorylinksTable = table;
	}
	
	public void readConcepts() throws SQLException {
		logger.info( "Reading concept ids from " + mlcTable );
		conceptIds = new TIntArrayList();
		PreparedStatement pst = jsb.getPreparedStatement(
				"select distinct mlc_id from " + mlcTable + " order by mlc_id;" );
		ResultSet rs = pst.executeQuery();
		while( rs.next() ) {
			conceptIds.add( rs.getInt( 1 ) );
		}
		logger.info( "Found " + conceptIds.size() + " concepts." );
	}
	
	public TIntArrayList getPageIds( int conceptId, Language language ) throws SQLException {
		PreparedStatement pst = jsb.getPreparedStatement(
				"select mlc_page from " + mlcTable + " where mlc_id=? and mlc_lang=?;" );
		pst.setInt( 1, conceptId );
		pst.setString( 2, language.toString() );
		
		TIntArrayList pageIds = new TIntArrayList();
		ResultSet pageResultSet = pst.executeQuery();
		while( pageResultSet.next() ) {
			pageIds.add( pageResultSet.getInt( 1 ) );
		}
		return pageIds;
	}
	
	public TIntArrayList getMlcArticleIdsInCategory( int categoryId ) throws SQLException {
		TIntArrayList mlcArticleIds = new TIntArrayList();
		if( mlcCategorylinksTable == null ) {
			logger.error( "Table for MLC category links was not set!" );
		}
		else {
			PreparedStatement pstLinks = jsb.getPreparedStatement(
					"select mlcl_from from " + mlcCategorylinksTable
					+ " where mlcl_namespace=0 and mlcl_to=?;" );

			if( logger.isDebugEnabled() )
				logger.debug( "Retrieving mlc concepts linking to category " + categoryId );
			pstLinks.setInt( 1, categoryId );
			ResultSet linkResultSet = pstLinks.executeQuery();
			while( linkResultSet.next() ) {
				mlcArticleIds.add( linkResultSet.getInt( 1 ) );
			}
		}
		return mlcArticleIds;
	}
	
	public String getConceptName( int conceptId ) { 
		StringBuilder docNameBuilder = new StringBuilder();
		Formatter docIdFormatter = new Formatter( docNameBuilder );

		docIdFormatter.format( "%1$010d", conceptId );
		return docNameBuilder.toString();
	}
	
	public int getConceptId( String docName ) {
		return Integer.parseInt( docName );
	}
	
	public int size() {
		return conceptIds.size();
	}
	
	public TIntArrayList getConceptIds() {
		return conceptIds;
	}
}
