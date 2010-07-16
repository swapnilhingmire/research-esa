package demo;

import org.apache.commons.configuration.Configuration;
import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

import edu.kit.aifb.ConfigurationException;
import edu.kit.aifb.ConfigurationManager;
import edu.kit.aifb.concept.IConceptExtractor;
import edu.kit.aifb.concept.IConceptIndex;
import edu.kit.aifb.concept.IConceptIterator;
import edu.kit.aifb.concept.IConceptVector;
import edu.kit.aifb.document.TextDocument;
import edu.kit.aifb.wikipedia.mlc.MLCDatabase;
import edu.kit.aifb.wikipedia.sql.IPage;
import edu.kit.aifb.wikipedia.sql.Page;
import edu.kit.aifb.wikipedia.sql.WikipediaDatabase;
import gnu.trove.TIntArrayList;


public class PrintESAVector {

	static final String[] REQUIRED_PROPERTIES = {
		"text",
		"concept_index_bean",
		"wp_database_bean",
	};

	static Logger logger = Logger.getLogger( PrintESAVector.class );

	/**
	 * @param args
	 */
	public static void main( String[] args ) throws Exception {
		try {
			ApplicationContext context = new FileSystemXmlApplicationContext( "config/*_beans.xml" );
			ConfigurationManager confMan = (ConfigurationManager) context.getBean( ConfigurationManager.class );
			confMan.parseArgs( args );
			confMan.checkProperties( REQUIRED_PROPERTIES );
			Configuration config = confMan.getConfig();
		
			IConceptIndex index = (IConceptIndex) context.getBean(
					config.getString( "concept_index_bean" ) );
			logger.info( "size of source index: " + index.size() );

			WikipediaDatabase wp = (WikipediaDatabase) context.getBean(
					config.getString( "wp_database_bean" ) );

			MLCDatabase mlcDb = null;
			if( config.containsKey( "mlc_database_bean" ) ) {
				mlcDb = (MLCDatabase) context.getBean( config.getString( "mlc_database_bean" ) );
			}
			
			IConceptExtractor esaExtractor = index.getConceptExtractor();

			TextDocument doc = new TextDocument( "text" );
			doc.setText( "content", index.getLanguage(), config.getString( "text" ) );

			logger.info( "Computing ESA vector of: " + doc.getText( "content" ) );
			IConceptVector cv = esaExtractor.extract( doc );

			logger.info( "Printing concept vector" );
			IConceptIterator it = cv.orderedIterator();
			while( it.next() ) {
				System.out.print( it.getValue() + " " );
				
				if( mlcDb == null ) {
					int articleId = Integer.parseInt( index.getConceptName( it.getId() ) );
					IPage article = new Page( articleId );
					wp.initializePage( article );
					if( article.isInitialized() )
						System.out.println( article.getTitle() + " (" + articleId + ")" );
				}
				else {
					TIntArrayList articleIds = mlcDb.getPageIds(
							Integer.parseInt( index.getConceptName( it.getId() ) ),
							wp.getLanguage() );
					System.out.print( "[ " );
					for( int i=0; i<articleIds.size(); i++ ) {
						IPage article = new Page( articleIds.get(i) );
						wp.initializePage( article );
						if( article.isInitialized() ) {
							System.out.print( article.getTitle() + " (" + articleIds.get(i) + ")" );
							if( i<articleIds.size()-1 ) {
								System.out.print( ", " );
							}
						}
					}
					System.out.println( " ]" );
				}
			}
		}
		catch( ConfigurationException e ) {
			e.printUsage();
		}
	}
}
