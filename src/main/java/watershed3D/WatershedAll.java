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
import timeGUI.CovistoTimeselectPanel;
import utility.PreRoiobject;
import utility.ThreeDRoiobject;
import watershedGUI.CovistoWatershedPanel;
import zGUI.CovistoZselectPanel;

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
		
		for (int t = CovistoTimeselectPanel.fourthDimensionsliderInit; t <= CovistoTimeselectPanel.fourthDimensionSize; ++t) {


			for (int z = CovistoZselectPanel.thirdDimensionsliderInit; z <= CovistoZselectPanel.thirdDimensionSize; ++z) {
				
				CovistoZselectPanel.thirdDimension = z;
				CovistoTimeselectPanel.fourthDimension = t;
				
				parent.CurrentView = utility.CovistoSlicer.getCurrentView(parent.originalimg, z, CovistoZselectPanel.thirdDimensionSize, t,
						CovistoTimeselectPanel.fourthDimensionSize);
				parent.updatePreview(ValueChange.THIRDDIMmouse);
				
				RandomAccessibleInterval<BitType> currentbitimg = utility.CovistoSlicer.getCurrentView(bitimg, z, CovistoZselectPanel.thirdDimensionSize, t,
						CovistoTimeselectPanel.fourthDimensionSize);
				
				RandomAccessibleInterval<IntType> currentintimg = utility.CovistoSlicer.getCurrentView(intimg, z, CovistoZselectPanel.thirdDimensionSize, t,
						CovistoTimeselectPanel.fourthDimensionSize);
				
				RandomAccessibleInterval<FloatType> currentnewimg = utility.CovistoSlicer.getCurrentView(newimg, z, CovistoZselectPanel.thirdDimensionSize, t,
						CovistoTimeselectPanel.fourthDimensionSize);
				
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
		CovistoWatershedPanel.thresholdWater = (float) ( GlobalThresholding.AutomaticThresholding(slice));
		CovistoWatershedPanel.thresholdWaterslider.setValue(utility.ScrollbarUtils.computeScrollbarPositionFromValue(CovistoWatershedPanel.thresholdWater, CovistoWatershedPanel.thresholdMinWater, 
				CovistoWatershedPanel.thresholdMaxWater, CovistoWatershedPanel.scrollbarSize));
		CovistoWatershedPanel.watertext.setText(CovistoWatershedPanel.waterstring +  " = "  + CovistoWatershedPanel.thresholdWater );
		CovistoWatershedPanel.thresholdWaterslider.validate();
		CovistoWatershedPanel.thresholdWaterslider.repaint();
		}
		GetLocalmaxminMT.ThresholdingMTBit(slice, bitimg, CovistoWatershedPanel.thresholdWater);
		
		
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
