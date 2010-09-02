package edu.kit.aifb.wikipedia.mlc;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;

public class MLCategory extends AbstractMLConcept {

	public MLCategory( MLCFactory factory, int id ) {
		super( factory, id, factory.getCategoryTable() );
	}
	
	public Collection<MLCategory> getSubCategories() throws SQLException {
		PreparedStatement pst = factory.getJdbcStatementBuffer().getPreparedStatement(
				"select mlcl_from from "
				+ factory.getCategorylinksTable()
				+ " where mlcl_namespace=14 and mlcl_to=?;" );
		pst.setInt( 1, id );
		
		Collection<MLCategory> categories = new ArrayList<MLCategory>();
		ResultSet rs = pst.executeQuery();
		while( rs.next() ) {
			int subId = rs.getInt( 1 );
			if( subId != id ) {
				categories.add( factory.createMLCategory( subId ) );
			}
		}
		return categories;
	}

	public Collection<MLCategory> getSupCategories() throws SQLException {
		PreparedStatement pst = factory.getJdbcStatementBuffer().getPreparedStatement(
				"select mlcl_to from "
				+ factory.getCategorylinksTable()
				+ " where mlcl_namespace=14 and mlcl_from=?;" );
		pst.setInt( 1, id );
		
		Collection<MLCategory> categories = new ArrayList<MLCategory>();
		ResultSet rs = pst.executeQuery();
		while( rs.next() ) {
			int supId = rs.getInt( 1 );
			if( supId != id ) {
				categories.add( factory.createMLCategory( supId ) );
			}
		}
		return categories;
	}

	public Collection<MLArticle> getArticles() throws SQLException {
		PreparedStatement pst = factory.getJdbcStatementBuffer().getPreparedStatement(
				"select mlcl_from from "
				+ factory.getCategorylinksTable()
				+ " where mlcl_namespace=0 and mlcl_to=?;" );
		pst.setInt( 1, id );
		
		Collection<MLArticle> concepts = new ArrayList<MLArticle>();
		ResultSet rs = pst.executeQuery();
		while( rs.next() ) {
			concepts.add( factory.createMLArticle( rs.getInt( 1 ) ) );
		}
		return concepts;
	}

}
