package edu.uka.aifb.api.concept;

import uk.ac.gla.terrier.matching.Model;
import uk.ac.gla.terrier.matching.dsms.DocumentScoreModifier;

public interface IConceptModel extends DocumentScoreModifier {

	public IConceptVectorBuilder getConceptVectorBuilder();
	public Model getWeightingModel();
	
}
