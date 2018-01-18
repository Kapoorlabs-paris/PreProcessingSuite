package userTESTING;

import distanceTransform.CreateBinary;
import distanceTransform.CreateDistanceTransform;
import distanceTransform.CreateWatershed;
import ij.IJ;
import ij.ImageJ;
import ij.ImagePlus;
import ij.io.Opener;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.algorithm.stats.Normalize;
import net.imglib2.img.display.imagej.ImageJFunctions;
import net.imglib2.type.logic.BitType;
import net.imglib2.type.numeric.integer.IntType;
import net.imglib2.type.numeric.real.FloatType;
import net.imglib2.view.IntervalView;
import net.imglib2.view.Views;
import preProcessing.FlatFieldOnly;
import preProcessing.Kernels;

public class BorisData {

	
	public static void main(String[] args) {
		
		
		new ImageJ();
		
		
		ImagePlus imp = new Opener().openImage("/Users/aimachine/Documents/Boris_Data/wt-PHGFP_scutellum_18-19hAPF/TESTgfp-pre.tif");
		RandomAccessibleInterval<FloatType> inputimage = ImageJFunctions.convertFloat(imp);
		new Normalize();
		FloatType minval = new FloatType(0);
		FloatType maxval = new FloatType(1);
		Normalize.normalize(Views.iterable(inputimage), minval, maxval);
		
		System.out.println(inputimage.dimension(0) + " " + inputimage.dimension(1) + " " + inputimage.dimension(2) +" " + inputimage.dimension(3) ) ;
	//	IntervalView<FloatType> view = Views.hyperSlice(Views.hyperSlice(inputimage, 3, 0), 2, 0);
	
		
		ImageJFunctions.show(inputimage);
		
		
		FlatFieldOnly flatimage = new FlatFieldOnly(inputimage, 50);
		flatimage.process();
		RandomAccessibleInterval<FloatType> flatout = flatimage.getResult();
		
		ImageJFunctions.show(flatout).setTitle("FlatField Correctred Image");
		
		
		
		RandomAccessibleInterval<FloatType> cannyflatout = Kernels.CannyEdge(flatout);
		ImageJFunctions.show(cannyflatout).setTitle("Canny Image");
		
		RandomAccessibleInterval<BitType> bitinput = CreateBinary.CreateAutoBinaryImage(cannyflatout);
     
		CreateDistanceTransform<FloatType> distout = new CreateDistanceTransform<FloatType>(cannyflatout, bitinput);
		distout.process();
		RandomAccessibleInterval<FloatType> distimg = distout.getResult();
		
		ImageJFunctions.show(distimg).setTitle("Distance Transformed Image");
		
		
		CreateWatershed<FloatType> waterout = new CreateWatershed<>(flatout, bitinput);
		waterout.process();
		RandomAccessibleInterval<IntType> waterimg = waterout.getResult();
		
		ImageJFunctions.show(waterimg).setTitle("Watershed Image");
		
		IJ.runMacroFile("/Users/aimachine/Documents/Boris_Data/wt-PHGFP_scutellum_18-19hAPF/Testwatershed.ijm");
	}
	
	
	
}
