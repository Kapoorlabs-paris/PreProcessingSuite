package watershed3D;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import javax.swing.JOptionPane;
import javax.swing.SwingWorker;

import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;

import distanceTransform.DistWatershed;
import interactivePreprocessing.InteractiveMethods;
import interactivePreprocessing.InteractiveMethods.ValueChange;
import linkers.PRENNsearch;
import net.imglib2.Cursor;
import net.imglib2.Interval;
import net.imglib2.RandomAccess;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.img.array.ArrayImgFactory;
import net.imglib2.img.display.imagej.ImageJFunctions;
import net.imglib2.type.NativeType;
import net.imglib2.type.logic.BitType;
import net.imglib2.type.numeric.RealType;
import net.imglib2.type.numeric.integer.IntType;
import net.imglib2.type.numeric.integer.UnsignedByteType;
import net.imglib2.type.numeric.real.FloatType;
import net.imglib2.util.Intervals;
import net.imglib2.util.Pair;
import net.imglib2.view.IntervalView;
import net.imglib2.view.Views;
import preProcessing.GetLocalmaxminMT;
import preProcessing.GlobalThresholding;
import utility.PreRoiobject;
import utility.ThreeDRoiobject;

public class WatershedAll extends SwingWorker<Void, Void> {

	final InteractiveMethods parent;

	public WatershedAll(final InteractiveMethods parent) {

		this.parent = parent;

	}

	@Override
	protected Void doInBackground() throws Exception {


		
		parent.apply3D = true;
		
		RandomAccessibleInterval<FloatType> newimg = new ArrayImgFactory<FloatType>().create(parent.originalimg, new FloatType());
		
		RandomAccessibleInterval<BitType> bitimg = new ArrayImgFactory<BitType>().create(newimg, new BitType());
		
		RandomAccessibleInterval<IntType> intimg = new ArrayImgFactory<IntType>().create(newimg, new IntType());
		
		for (int t = parent.fourthDimensionsliderInit; t <= parent.fourthDimensionSize; ++t) {


			for (int z = parent.thirdDimensionsliderInit; z <= parent.thirdDimensionSize; ++z) {
				
				parent.thirdDimension = z;
				parent.fourthDimension = t;
				
				parent.CurrentView = utility.CovistoSlicer.getCurrentView(parent.originalimg, z, parent.thirdDimensionSize, t,
						parent.fourthDimensionSize);
				parent.updatePreview(ValueChange.THIRDDIMmouse);
				
				RandomAccessibleInterval<BitType> currentbitimg = utility.CovistoSlicer.getCurrentView(bitimg, z, parent.thirdDimensionSize, t,
						parent.fourthDimensionSize);
				
				RandomAccessibleInterval<IntType> currentintimg = utility.CovistoSlicer.getCurrentView(intimg, z, parent.thirdDimensionSize, t,
						parent.fourthDimensionSize);
				
				RandomAccessibleInterval<FloatType> currentnewimg = utility.CovistoSlicer.getCurrentView(newimg, z, parent.thirdDimensionSize, t,
						parent.fourthDimensionSize);
				
			processSlice(parent.CurrentView, currentintimg, currentbitimg, currentnewimg);
			
			}
			
		
		}
		
		if(parent.displayBinaryimg)
			ImageJFunctions.show(bitimg).setTitle("Binary Image");
		
		if (parent.displayWatershedimg)
			ImageJFunctions.show(intimg).setTitle("Integer Image");
		parent.intimg = intimg;
		
		if (parent.displayDistTransimg)
			ImageJFunctions.show(newimg ).setTitle("Distance Transform Image");
		
		
		
		
		
		return null;
	}
	
	
	protected void processSlice(RandomAccessibleInterval< FloatType > slice, RandomAccessibleInterval< IntType > intoutputslice, RandomAccessibleInterval< BitType > bitoutputslice,
			RandomAccessibleInterval< FloatType > distslice) {
		
		
		RandomAccessibleInterval<BitType> bitimg = new ArrayImgFactory<BitType>().create(slice, new BitType());
		
		
		
		if(parent.autothreshwater) {
		parent.thresholdWater = (float) ( GlobalThresholding.AutomaticThresholding(slice));
		System.out.println(parent.thresholdWater);
		parent.thresholdWaterslider.setValue(utility.ScrollbarUtils.computeScrollbarPositionFromValue(parent.thresholdWater, parent.thresholdMinWater, parent.thresholdMaxWater, parent.scrollbarSize));
	    parent.watertext.setText(parent.waterstring +  " = "  + parent.thresholdWater );
		parent.thresholdslider.validate();
		parent.thresholdslider.repaint();
		}
		GetLocalmaxminMT.ThresholdingMTBit(slice, bitimg, parent.thresholdWater);
		
		
		DistWatershed<FloatType> WaterafterDisttransform = new DistWatershed<FloatType>(parent, slice, bitimg,
				parent.jpb, parent.apply3D);
		WaterafterDisttransform.execute();
		RandomAccessibleInterval<IntType> waterint =  WaterafterDisttransform.getResult();
		RandomAccessibleInterval<FloatType> distwater =  WaterafterDisttransform.getDistanceTransformedimg();
		Cursor< BitType > bitcursor = Views.iterable(bitoutputslice).localizingCursor();
		Cursor< IntType > intcursor = Views.iterable(intoutputslice).localizingCursor();
		Cursor< FloatType > distcursor = Views.iterable(distslice).localizingCursor();
		
		RandomAccess<BitType> ranac = bitimg.randomAccess();
		RandomAccess<IntType> intranac = waterint.randomAccess();
		RandomAccess<FloatType> distranac = distwater.randomAccess();
		
		while(bitcursor.hasNext()) {
			
			bitcursor.fwd();
			
			ranac.setPosition(bitcursor);
			
			bitcursor.get().set(ranac.get());
			
			
		}
		
		while(intcursor.hasNext()) {
			
			intcursor.fwd();
			
			intranac.setPosition(intcursor);
			
			intcursor.get().set(intranac.get());
			
			
		}
		
		while(distcursor.hasNext()) {
			
			distcursor.fwd();
			
			distranac.setPosition(distcursor);
			
			distcursor.get().set(distranac.get());
			
		}
		
		
	}

	@Override
	protected void done() {
		try {
		
			parent.apply3D = false;
			get();
		} catch (ExecutionException | InterruptedException e) {
			e.printStackTrace();
		}

	}

}
