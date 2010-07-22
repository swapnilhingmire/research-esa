package edu.kit.aifb.concept.index;



import java.util.List;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.terrier.matching.ResultSet;

import edu.kit.aifb.concept.ConceptMatcher;
import edu.kit.aifb.concept.TroveConceptVector;
import edu.kit.aifb.concept.scorer.CosineScorer;
import edu.kit.aifb.ir.IMatch;
import edu.kit.aifb.nlp.Language;

public class TestCVIndex {

	static IndexedFileCVIndexReader reader;
	
	@BeforeClass
	public static void buildIndex() throws Exception {
		IndexedFileCVIndexBuilder builder = new IndexedFileCVIndexBuilder();
		builder.setBaseDirectory( "data/test" );
		builder.setLanguage( Language.EN );
		builder.setCacheSize( 10000 );
		builder.setSize( 10 );
		builder.setId( "test_CVIndexTest" );
		
		TroveConceptVector cv0 = new TroveConceptVector( "doc0", 10 );
		cv0.add( 4, .1 );
		cv0.add( 7, .3 );
		builder.add( cv0 );
		
		TroveConceptVector cv1 = new TroveConceptVector( "doc1", 10 );
		cv1.add( 4, .4 );
		cv1.add( 9, .1 );
		builder.add( cv1 );
		builder.buildIndex();
		
		reader = new IndexedFileCVIndexReader();
		reader.setBaseDirectory( "data/test" );
		reader.setCacheSize( 10000 );
		reader.setLanguage( Language.EN );
		reader.setId( "test_CVIndexTest" );
		reader.readIndex();
	}
	
	@Test
	public void readEntries() throws Exception {
		ICVIndexEntryIterator it = reader.getIndexEntryIterator( 9 );
		Assert.assertTrue( it.next() );
		Assert.assertEquals( "doc1", reader.getConceptVectorData( it.getDocId() ).getDocName() );
		Assert.assertEquals( .1, it.getValue(), .000001 );
		Assert.assertFalse( it.next() );
		
		it = reader.getIndexEntryIterator( 4 );
		Assert.assertTrue( it.next() );
		Assert.assertEquals( "doc1", reader.getConceptVectorData( it.getDocId() ).getDocName() );
		Assert.assertEquals( .4, it.getValue(), .000001 );
		
		Assert.assertTrue( it.next() );
		Assert.assertEquals( "doc0", reader.getConceptVectorData( it.getDocId() ).getDocName() );
		Assert.assertEquals( .1, it.getValue(), .000001 );
		Assert.assertFalse( it.next() );
	}
	
	@Test
	public void matchVector() {
		ConceptMatcher matcher = new ConceptMatcher( reader );
		matcher.setScorerClass( CosineScorer.class );

		TroveConceptVector cv2 = new TroveConceptVector( "query", 10 );
		cv2.add( 4, .1 );

		double valueDoc1 = .1 * .4 / ( Math.sqrt( .1*.1 ) * Math.sqrt( .4*.4 + .1*.1 ) );
		double valueDoc0 = .1 * .1 / ( Math.sqrt( .1*.1 ) * Math.sqrt( .3*.3 + .1*.1 ) );
		
		matcher.match( cv2 );
		List<IMatch> matches = matcher.getMatches();
		Assert.assertEquals( 2, matches.size() );
		Assert.assertEquals( "doc1", matches.get(0).getDocName() );
		Assert.assertEquals( valueDoc1, matches.get(0).getScore(), .000001 );
		Assert.assertEquals( "doc0", matches.get(1).getDocName() );
		Assert.assertEquals( valueDoc0, matches.get(1).getScore(), .000001 );
		
		ResultSet rs = matcher.getResultSet();
		Assert.assertEquals( 2, rs.getResultSize() );
		
		int[] ids = rs.getDocids();
		double[] values = rs.getScores();
		Assert.assertEquals( "doc1", reader.getConceptVectorData( ids[0] ).getDocName() );
		Assert.assertEquals( valueDoc1, values[0], .000001 );
		Assert.assertEquals( "doc0", reader.getConceptVectorData( ids[1] ).getDocName() );
		Assert.assertEquals( valueDoc0, values[1], .000001 );
	}

}
