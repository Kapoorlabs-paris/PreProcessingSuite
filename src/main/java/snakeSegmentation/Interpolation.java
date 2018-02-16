package snakeSegmentation;


public  class Interpolation {

	
	
	public static double bilinearInterpolation(double x, double y, double [] array, int width, int height) {
		
		
	
		
		int x1 = (int) Math.floor(x);
		int y1 = (int) Math.floor(y);
		
		
		if (x1 < 1) {
			x1 = 1;
		} else if (x1 > width) {
			x1 = width;
		}

		if (y1 < 1) {
			y1 = 1;
		} else if (y1 > height) {
			y1 = height;
		}
		int x2 = x1 + 1;
		int y2 = y1 + 1;
		
		double DeltaX1 = x - x1;
		double DeltaY1 = y - y1;
		double DeltaY2 = y2 - y;
		double DeltaX2 = x2 - x;

		return array[(x1 + y1 * width)] * DeltaX2 * DeltaY2 + array[(x1 + y2 * width)] * DeltaX2 * DeltaY1
				+ array[(x2 + y1 * width)] * DeltaX1 * DeltaY2 + array[(x2 + y2 * width)] * DeltaX1 * DeltaY1;
		
		
	}
	
}
