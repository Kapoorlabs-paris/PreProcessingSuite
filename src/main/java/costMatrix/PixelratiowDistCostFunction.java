package costMatrix;

import utility.PreRoiobject;

public class PixelratiowDistCostFunction implements CostFunction< PreRoiobject, PreRoiobject >
	{

	
	// Alpha is the weightage given to distance and Beta is the weightage given to the ratio of pixels
		public final double beta;
		public final double alpha;
		
		
	
		
		public double getAlpha(){
			
			return alpha;
		}
		
	  
		public double getBeta(){
			
			return beta;
		}

		public PixelratiowDistCostFunction (double alpha, double beta){
			
			this.alpha = alpha;
			this.beta = beta;
			
		}
		
		
	@Override
	public double linkingCost( final PreRoiobject source, final PreRoiobject target )
	{
		return source.NormalizedPixelratioandDistanceTo(target, alpha, beta);
	}
		


	
	
	
}
