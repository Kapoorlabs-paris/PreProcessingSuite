package utility;

import java.util.ArrayList;

import ij.gui.Roi;
import net.imglib2.Cursor;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.type.numeric.real.FloatType;
import net.imglib2.util.Pair;
import net.imglib2.util.ValuePair;
import net.imglib2.view.Views;

public class PreRoiobject {

	
	public Roi rois;
	public double[] geometriccenter;
	public double area;
	public double totalintensity;
	public double averageintensity;
	public int thirdDimension;
	public int fourthDimension;
	
	
	public PreRoiobject(final Roi rois, final double[] geometriccenter, final double area, final double totalintensity, final double averageintensity, final int thirdDimension, final int fourthDimension) {
		
		this.rois = rois;
		this.geometriccenter = geometriccenter;
		this.area = area;
		this.totalintensity = totalintensity;
		this.averageintensity = averageintensity;
		this.thirdDimension = thirdDimension;
		this.fourthDimension = fourthDimension;
		
	}
	
	public static Pair<Double, Integer> getIntensity(Roi roi, RandomAccessibleInterval<FloatType> source) {

		double Intensity = 0;
        int NumberofPixels = 0;
		Cursor<FloatType> currentcursor = Views.iterable(source).localizingCursor();

		final double[] position = new double[source.numDimensions()];

		while (currentcursor.hasNext()) {

			currentcursor.fwd();

			currentcursor.localize(position);

			int x = (int) position[0];
			int y = (int) position[1];

			if (roi.contains(x, y)) {

				Intensity += currentcursor.get().getRealDouble();

				NumberofPixels++;
			}

		}

		return new ValuePair<Double, Integer>(Intensity, NumberofPixels);

	}

}
