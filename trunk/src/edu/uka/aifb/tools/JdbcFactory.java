package edu.uka.aifb.tools;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.configuration.Configuration;
import org.apache.log4j.Logger;


public class JdbcFactory {

	static Logger logger = Logger.getLogger( JdbcFactory.class );
	
	static final String[] DATABASE_PROPERTIES = {
		"jdbc.%1.driver",
		"jdbc.%1.url"
	};
	
	private static Map<String,Connection> m_connections = new HashMap<String,Connection>();
	
	public static Connection getConnection( Configuration config, String jdbcId ) throws Exception {
		if( m_connections.containsKey( jdbcId )
				&& ! m_connections.get( jdbcId ).isClosed() )
		{
			return m_connections.get( jdbcId );
		}
		else
		{
			ConfigurationManager.checkProperties( config, DATABASE_PROPERTIES, jdbcId );
			
			Class.forName( config.getString( "jdbc." + jdbcId + ".driver" ) );
			
			Connection con;
			if( config.containsKey( "jdbc." + jdbcId + ".user" ) ) {
				con = DriverManager.getConnection(
						config.getString( "jdbc." + jdbcId + ".url" ),
						config.getString( "jdbc." + jdbcId + ".user" ),
						config.getString( "jdbc." + jdbcId + ".password" ) );
			}
			else {
				con = DriverManager.getConnection( config.getString( "jdbc." + jdbcId + ".url" ) );
			}
			m_connections.put( jdbcId, con );
			return con;
		}
	}
	
}
