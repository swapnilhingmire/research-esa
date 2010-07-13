package edu.kit.aifb.concept.terrier;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

import edu.kit.aifb.concept.IConceptExtractor;
import edu.kit.aifb.concept.IConceptVector;
import edu.kit.aifb.document.TextDocument;
import edu.kit.aifb.nlp.Language;
import edu.kit.aifb.terrier.concept.TerrierESAIndex;

public class TerrierESAIndexTest {

	static TerrierESAIndex terrierESAIndex;
	
	@BeforeClass
	public static void loadDatabase() {
		ApplicationContext context = new FileSystemXmlApplicationContext( "config/*_beans.xml" );
		terrierESAIndex = (TerrierESAIndex) context.getBean( "wp200909_mlc_articles_concept_index_de" );
	}
	
	@Test
	public void indexStatistics() {
		Assert.assertEquals( 358519, terrierESAIndex.size() );
	}

	@Test
	public void conceptExtractor() {
		IConceptExtractor extractor = terrierESAIndex.getConceptExtractor();
		
		TextDocument query = new TextDocument( "query" );
		query.setText( "query", Language.DE, "Albert Einstein" );
		
		IConceptVector cv = extractor.extract( query );
		Assert.assertTrue( cv.size() > 0 );
	}
	
}
