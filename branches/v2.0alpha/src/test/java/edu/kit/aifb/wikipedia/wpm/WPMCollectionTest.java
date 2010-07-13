package edu.kit.aifb.wikipedia.wpm;

import junit.framework.Assert;

import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

import edu.kit.aifb.document.ICollection;
import edu.kit.aifb.document.ICollectionIterator;

public class WPMCollectionTest {

	static ICollection wpCol;
	
	@BeforeClass
	public static void loadDatabase() {
		ApplicationContext context = new FileSystemXmlApplicationContext( "config/*_beans.xml" );
		wpCol = (ICollection)context.getBean( "wp200909_mlc_articles_wpm_collection_en" );
	}
	
	@Test
	public void collectionSize() {
		Assert.assertEquals( 358519, wpCol.size() );
	}
	
	@Test
	public void iterator() {
		ICollectionIterator iterator = wpCol.iterator();
		iterator.next();
		Assert.assertEquals( 1, Integer.parseInt( iterator.getDocument().getName() ) );
		Assert.assertTrue( iterator.getDocument().getText( "en" ).contains( "Alan Smithee" ) );
	}
}
