package snakeSegmentation;

public class EnergyFunctions {

	
	public double shapeEnergy(double[][] position, double[][] derivative, double[] steerableresponse, double[] orientation, int width, int height,  int controlpoints, int ndims ) {
		
		double magicnumber = 100000.0D;
		double energy = 0.0D;
		double snakelength = 0.0D;
		 
		
		int interpolants = position[0].length;
		 
		for (int i = 0; i < interpolants; ++i) {
			
			double interph = Interpolation.bilinearInterpolation(position[0][i], position[1][i], steerableresponse, width, height);
			
			double interpnx = Interpolation.bilinearInterpolation(position[0][i], position[1][i], derivative[0], width, height);
			
			double interpny = Interpolation.bilinearInterpolation(position[0][i], position[1][i], derivative[1], width, height);
			
			snakelength += Math.sqrt(derivative[0][i] * derivative[0][i] + derivative[1][i] * derivative[1][i] );
			
			energy += Math.abs(interpnx * derivative[0][i] + interpny * derivative[1][i]) * interph;
			
		}
		
		energy = -1.0D * energy / (500.0D * snakelength);
		
		return magicnumber * energy;
		 
		
		
	}
	
}
