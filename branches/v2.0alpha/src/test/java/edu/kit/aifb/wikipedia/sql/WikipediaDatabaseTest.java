package edu.kit.aifb.wikipedia.sql;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

public class WikipediaDatabaseTest {

	static WikipediaDatabase englishWp;
	static WikipediaDatabase germanWp;
	
	@BeforeClass
	public static void loadDatabase() {
		ApplicationContext context = new FileSystemXmlApplicationContext( "config/*_beans.xml" );
		englishWp = (WikipediaDatabase)context.getBean( "wp200909_database_en" );
		germanWp = (WikipediaDatabase)context.getBean( "wp200909_database_de" );
	}
	
	@Test
	public void articleText() throws Exception {
		IPage p = englishWp.getArticle( "Mulled_wine" );
		Assert.assertEquals( "Mulled_wine", p.getTitle() );
		Assert.assertTrue( p.getId() >= 0 );
		
		String text = englishWp.getText( p );
		Assert.assertTrue( text.length() > 10 );
		Assert.assertTrue( text.substring( 100 ), text.contains( "red wine" ) );
		Assert.assertTrue( text.substring( 100 ), text.contains( "cinnamon" ) );
	}
	
}
