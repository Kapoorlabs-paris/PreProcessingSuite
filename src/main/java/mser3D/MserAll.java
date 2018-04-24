package mser3D;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import javax.swing.SwingWorker;

import distanceTransform.DistWatershed;
import interactivePreprocessing.InteractiveMethods;
import interactivePreprocessing.InteractiveMethods.ValueChange;
import net.imglib2.Cursor;
import net.imglib2.RandomAccess;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.img.array.ArrayImgFactory;
import net.imglib2.img.display.imagej.ImageJFunctions;
import net.imglib2.type.logic.BitType;
import net.imglib2.type.numeric.integer.IntType;
import net.imglib2.type.numeric.integer.UnsignedByteType;
import net.imglib2.type.numeric.real.FloatType;
import net.imglib2.view.Views;
import preProcessing.GetLocalmaxminMT;
import preProcessing.GlobalThresholding;
import timeGUI.CovistoTimeselectPanel;
import utility.PreRoiobject;
import zGUI.CovistoZselectPanel;

public class MserAll extends SwingWorker<Void, Void> {

	final InteractiveMethods parent;

	public MserAll(final InteractiveMethods parent) {

		this.parent = parent;

	}

	@Override
	protected Void doInBackground() throws Exception {


		
		parent.apply3D = true;
		
		
		RandomAccessibleInterval<BitType> bitimg = new ArrayImgFactory<BitType>().create(parent.originalimg, new BitType());
		
		
		for (int t = CovistoTimeselectPanel.fourthDimensionsliderInit; t <= CovistoTimeselectPanel.fourthDimensionSize; ++t) {


			for (int z = CovistoZselectPanel.thirdDimensionsliderInit; z <= CovistoZselectPanel.thirdDimensionSize; ++z) {
				
				CovistoZselectPanel.thirdDimension = z;
				CovistoTimeselectPanel.fourthDimension = t;
				
				parent.CurrentView = utility.CovistoSlicer.getCurrentView(parent.originalimg, z, CovistoZselectPanel.thirdDimensionSize, t,
						CovistoTimeselectPanel.fourthDimensionSize);
				
				// UnsignedByteType image created here
				parent.updatePreview(ValueChange.THIRDDIMmouse);
				parent.CurrentPreRoiobject = new ArrayList<PreRoiobject>();
				RandomAccessibleInterval<BitType> currentbitimg = utility.CovistoSlicer.getCurrentView(bitimg, z, CovistoZselectPanel.thirdDimensionSize, t,
						CovistoTimeselectPanel.fourthDimensionSize);
				
			
				
			processSlice(parent.newimg, currentbitimg, z, t);
			
			}
			
		
		}
		
		
			ImageJFunctions.show(bitimg).setTitle("Binary Image");
		
		
		
		
		
		
		
		
		
		
		return null;
	}
	
	
	protected void processSlice(RandomAccessibleInterval< UnsignedByteType > slice, RandomAccessibleInterval< BitType > bitoutputslice, int z, int t) {
		
		
		
		
	
		ComputeCompTree<UnsignedByteType> ComputeMSER = new ComputeCompTree<UnsignedByteType>(parent, slice, parent.jpb, parent.apply3D, z, t);
		ComputeMSER.execute();
		
		RandomAccessibleInterval<BitType> bitimg =  ComputeMSER.getBinaryimg();
		Cursor< BitType > bitcursor = Views.iterable(bitoutputslice).localizingCursor();
		
		RandomAccess<BitType> ranac = bitimg.randomAccess();
		
		while(bitcursor.hasNext()) {
			
			bitcursor.fwd();
			
			ranac.setPosition(bitcursor);
			
			bitcursor.get().set(ranac.get());
			
			
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
