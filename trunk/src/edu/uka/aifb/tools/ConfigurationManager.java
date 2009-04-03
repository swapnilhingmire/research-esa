package edu.uka.aifb.tools;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import org.apache.commons.configuration.CompositeConfiguration;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;


public class ConfigurationManager {

	static public void printHelp() {
		System.err.println( "Use: <Class> -D<property>=<value>... <config file>..." );
		System.exit( 1 );
	}
	
	static private Configuration m_configuration;
	
	static public Configuration getCurrentConfiguration() {
		return m_configuration;
	}
	
	static public Configuration parseArgs( String[] args ) throws ConfigurationException {
		BasicConfigurator.configure();
		Logger.getRootLogger().setLevel( Level.INFO );
		
		CompositeConfiguration config = new CompositeConfiguration();
		
		/*
		 *  parse command line arguments:
		 *  -D<property>=<value>
		 *  <file name>
		 */
		for( String arg : args )
		{
			if( arg.equals( "-h" ) )
			{
				printHelp();
			}
			else if( arg.startsWith( "-D" ) )
			{
				String[] argSplit = arg.substring( 2 ).split( "=" );
				if ( argSplit.length == 2 ) {
					config.addProperty( argSplit[0], argSplit[1] );
				}
				else {
					printHelp();
				}
			}
			else
			{
				File configFile = new File( arg );
				if( configFile.isFile() && configFile.canRead() ) {
					config.addConfiguration( new PropertiesConfiguration( configFile ) );
				}
				else {
					printHelp();
				}
			}			
		}
		
		// initialize log4j
		Properties log4jProperties = new Properties();
		Iterator<String> keyIt = config.getKeys( "log4j" );
		while( keyIt.hasNext() ) {
			String key = keyIt.next();
			log4jProperties.setProperty( key, config.getString( key ) );			
		}
		PropertyConfigurator.configure( log4jProperties );
		
		m_configuration = config;
		return config;
	}
	
	static public void checkProperties( Configuration config, String[] properties ) {
		List<String> missingProperties = new ArrayList<String>();
		for( String property : properties )
		{
			if( ! config.containsKey( property ) ) {
				missingProperties.add( property );
			}
		}
		
		if( missingProperties.size() > 0 ) {
			System.err.println( "ERROR: The following properties are missing:" );
			for( String missingProperty : missingProperties ) {
				System.err.println( "* " + missingProperty );
			}
			System.err.println();
			printHelp();
		}
	}
	
	static public void checkProperties( Configuration config, String[] properties, Object... vars ) {
		String[] newProperties = new String[properties.length];
		for( int i=0; i<properties.length; i++ ) {
			String property = properties[i];
			for( int j=0; j<vars.length; j++ ) {
				property = property.replaceAll( "\\%" + (j+1), vars[j].toString() );
			}
			newProperties[i] = property;
		}
		checkProperties( config, newProperties );
	}
	
}
