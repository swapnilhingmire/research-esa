package edu.kit.aifb.concept.builder.category;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;


import gnu.trove.TIntArrayList;

public class MaxScoreCatTreeVectorBuilder extends
		CatTreeVectorBuilder {
	private static Logger logger = Logger.getLogger( MaxScoreCatTreeVectorBuilder.class );

	double factor = 1.0;
	
	@Required @Override
	public void setSize( int size ) {
		super.setSize( size );
	}

	public void setThreshold( double factor ) {
		logger.info( "Setting new factor: " + Double.toString( factor ) );
		this.factor = factor;
	}
	
	@Override
	void propagateScores( double[] scores ) {
		logger.debug( "Using tree to propagate scores" );
		TIntArrayList orderedCatIds = tree.getLeafOrderedDocIds();
		for( int j=0; j<orderedCatIds.size(); j++ ) {
			int catId = orderedCatIds.get( j );
			TIntArrayList subCatIds = tree.getSubCategoryDocIds( catId );

			for( int i=0; i<subCatIds.size(); i++ ) {
				int subCatId = subCatIds.get( i );

				scores[catId] = Math.max( scores[catId], scores[subCatId] * factor );
			}
		}
	}

}
