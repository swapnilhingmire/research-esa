package edu.kit.aifb.wikipedia.sql;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;

public class BufferedLanglinksApi extends FastLanglinksApi {

	private static Logger logger = Logger.getLogger(BufferedLanglinksApi.class);
	
	String llTable;
	String llSourceColumn;
	String llTargetColumn;

	@Required
	public void setLanglinkTable( String table ) {
		llTable = table;
	}
	
	@Required
	public void setSourceColumn( String column ) {
		llSourceColumn = column;
	}

	@Required
	public void setTargetColumn( String column ) {
		llTargetColumn = column;
	}

	@Override
	public int getTargetPageId( int sourcePageId ) {
		int targetPageId = -1;
		try {
			String sql = 
				"select " + llTargetColumn + " " +
				"from " + llTable + " " +
				"where " + llSourceColumn + "=?;";
			PreparedStatement st = jsb.getPreparedStatement( sql );
			st.setInt(1, sourcePageId);
			ResultSet rs = st.executeQuery();
			if (rs.next()) {
				targetPageId = rs.getInt(1);
			}
			rs.close();
		} catch (SQLException e) {
			logger.error(e);
		}
		return targetPageId;
	}
	
	@Override
	public int[] getTargetPageIds( int[] sourcePageIds ) {
		if( logger.isTraceEnabled() )
			logger.trace( "Building sql command string, #ids=" + sourcePageIds.length );
		StringBuilder idStringBuilder = new StringBuilder();
		if( sourcePageIds.length > 0)
		{
			idStringBuilder.append( sourcePageIds[0] );
			for (int i=1; i<sourcePageIds.length; i++) {
				idStringBuilder.append(',');
				idStringBuilder.append(sourcePageIds[i]);
			}
		}
		else {
			return new int[0];
		}
		
		try {
			String sql =
				"select " + llSourceColumn + "," + llTargetColumn + " " +
				"from " + llTable + " " +
				"where " + llSourceColumn + " in " +
				"(" + idStringBuilder.toString() + ") " +
				"order by " + llSourceColumn + ";";
			
			if (logger.isDebugEnabled())
				logger.debug("SQL: selectManyBySource");
			Statement st = jsb.getStatement();
			ResultSet rs = st.executeQuery(sql);
			
			int index = 0;
			int[] targetPageIds = new int[sourcePageIds.length];

			while (rs.next()) {
				int sourceId = rs.getInt(1);
				int targetId = rs.getInt(2);
				while (sourcePageIds[index] < sourceId && index < sourcePageIds.length) {
					targetPageIds[index] = -1;
					index++;
				}
				
				if (index >= sourcePageIds.length) {
					break;
				}
				
				if (sourcePageIds[index] == sourceId) {
					targetPageIds[index] = targetId;
					index++;
				}
			}
			rs.close();
			return targetPageIds;

		} catch (SQLException e) {
			logger.error(e);
			return null;
		}
	}

}
