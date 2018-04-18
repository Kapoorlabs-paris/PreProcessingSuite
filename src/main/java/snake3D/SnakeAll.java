package snake3D;

import java.util.concurrent.ExecutionException;

import javax.swing.SwingWorker;

import interactivePreprocessing.InteractiveMethods;
import interactivePreprocessing.InteractiveMethods.ValueChange;
import mser3D.ComputeCompTree;
import net.imglib2.Cursor;
import net.imglib2.RandomAccess;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.img.array.ArrayImgFactory;
import net.imglib2.img.display.imagej.ImageJFunctions;
import net.imglib2.type.logic.BitType;
import net.imglib2.type.numeric.integer.UnsignedByteType;
import net.imglib2.view.Views;

public class SnakeAll extends SwingWorker<Void, Void> {

	final InteractiveMethods parent;

	public SnakeAll(final InteractiveMethods parent) {

		this.parent = parent;

	}

	@Override
	protected Void doInBackground() throws Exception {


		
		parent.apply3D = true;
		
		
		RandomAccessibleInterval<BitType> bitimg = new ArrayImgFactory<BitType>().create(parent.originalimg, new BitType());
		
		
		for (int t = parent.fourthDimensionsliderInit; t <= parent.fourthDimensionSize; ++t) {


			for (int z = parent.thirdDimensionsliderInit; z <= parent.thirdDimensionSize; ++z) {
				
				parent.thirdDimension = z;
				parent.fourthDimension = t;
				
				parent.CurrentView = utility.CovistoSlicer.getCurrentView(parent.originalimg, z, parent.thirdDimensionSize, t,
						parent.fourthDimensionSize);
				
				// UnsignedByteType image created here
				parent.updatePreview(ValueChange.THIRDDIMmouse);
				
				RandomAccessibleInterval<BitType> currentbitimg = utility.CovistoSlicer.getCurrentView(bitimg, z, parent.thirdDimensionSize, t,
						parent.fourthDimensionSize);
				
			
				
			processSlice(parent.newimg, currentbitimg, z, t);
			
			}
			
		
		}
		
		
			ImageJFunctions.show(bitimg).setTitle("Binary Image");
		
		
		
		
		
		
		
		
		
		
		return null;
	}
	
	
	protected void processSlice(RandomAccessibleInterval< UnsignedByteType > slice, RandomAccessibleInterval< BitType > bitoutputslice, int z, int t) {
		
		
		
		
	
		ComputeSnakeSeg<UnsignedByteType> ComputeSnake = new ComputeSnakeSeg<UnsignedByteType>(parent, slice, parent.jpb, parent.apply3D, z, t);
		ComputeSnake.execute();
		
		RandomAccessibleInterval<BitType> bitimg =  ComputeSnake.getBinaryimg();
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
