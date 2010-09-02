package edu.kit.aifb.concept.builder.category;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;

import gnu.trove.TIntArrayList;

public class MeanScoreTopDownCatTreeVectorBuilder extends
		TopDownCatTreeVectorBuilder {
	private static Logger logger = Logger.getLogger( MeanScoreTopDownCatTreeVectorBuilder.class );

	@Required @Override
	public void setSize( int size ) {
		super.setSize( size );
	}
	
	@Override
	void propagateScores( double[] scores ) {
		int[] treeSize = new int[scores.length];
		
		logger.debug( "Using tree to propagate scores" );
		TIntArrayList orderedCatIds = tree.getLeafOrderedDocIds();
		for( int j=0; j<orderedCatIds.size(); j++ ) {
			int catId = orderedCatIds.get( j );
			TIntArrayList subCatIds = tree.getSubCategoryDocIds( catId );

			treeSize[catId] = 1;
			for( int i=0; i<subCatIds.size(); i++ ) {
				int subCatId = subCatIds.get( i );
				treeSize[catId] += treeSize[subCatId];

				scores[catId] += treeSize[subCatId] * scores[subCatId];
			}

			scores[catId] /= treeSize[catId];
		}
	}

}
