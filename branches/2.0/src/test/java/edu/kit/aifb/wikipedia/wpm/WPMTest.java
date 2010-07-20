package edu.kit.aifb.wikipedia.wpm;

import java.sql.SQLException;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.wikipedia.miner.model.Page;
import org.wikipedia.miner.model.Wikipedia;

import edu.kit.aifb.TestContextManager;

public class WPMTest {

	Wikipedia germanWp;
	
	@Before
	public void loadDatabase() {
		germanWp = (Wikipedia) TestContextManager.getContext().getBean(
				"wpm_de" );
	}
	
	@Test
	public void specialCharacters() throws SQLException {
		Page p = germanWp.getArticleByTitle( "Fähre" );
		Assert.assertNotNull( p );
		Assert.assertEquals( "Fähre", p.getTitle() );
		
		p = germanWp.getPageById( 16373 );
		Assert.assertNotNull( p );
		Assert.assertEquals( "Fähre", p.getTitle() );
	}
	
}
