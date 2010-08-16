package edu.kit.aifb.wikipedia.wpm;

import junit.framework.Assert;

import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

import edu.kit.aifb.document.ICollection;
import edu.kit.aifb.document.ICollectionIterator;
import edu.kit.aifb.document.IDocument;

public class WPMCollectionTest {

	static ICollection wpCol;
	
	@BeforeClass
	public static void loadDatabase() {
		ApplicationContext context = new FileSystemXmlApplicationContext( "config/*_beans.xml" );
		wpCol = (ICollection)context.getBean( "wpm_wp200909_mlc_articles_collection_en" );
	}
	
	@Test
	public void collectionSize() {
		Assert.assertEquals( 358519, wpCol.size() );
	}
	
	@Test
	public void iterator() {
		ICollectionIterator iterator = wpCol.iterator();
		iterator.next();
		IDocument doc = iterator.getDocument();
		
		Assert.assertEquals( 1, Integer.parseInt( doc.getName() ) );
		Assert.assertTrue( doc.getText( "title" ).startsWith( "Alan Smithee" ) );
		Assert.assertEquals( 9, doc.getText( "title" ).split( "\n" ).length );
		
		Assert.assertTrue( doc.getText( "content" ).contains( "Alan Smithee" ) );
		
		Assert.assertEquals( 89, doc.getText( "anchor" ).split( "\n" ).length );
	}
}
