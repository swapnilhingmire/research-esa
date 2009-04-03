package edu.uka.aifb.api.ir.terrier;

import java.util.List;

import uk.ac.gla.terrier.matching.dsms.DocumentScoreModifier;
import uk.ac.gla.terrier.matching.models.WeightingModel;
import uk.ac.gla.terrier.querying.parser.Query;
import uk.ac.gla.terrier.structures.Index;
import edu.uka.aifb.api.ir.IMatch;
import edu.uka.aifb.api.nlp.ITokenAnalyzer;

public interface ITerrierSearch {

	public void setIndex( Index index, WeightingModel model );
	
	public void setIndex(Index index, WeightingModel model, DocumentScoreModifier modifier);

	public void setTokenAnalyzer( ITokenAnalyzer analyzer );
	
	public List<IMatch> getMatches( Query query );

}
