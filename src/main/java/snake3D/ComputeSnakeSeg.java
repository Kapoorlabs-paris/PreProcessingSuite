package snake3D;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.swing.JProgressBar;

import ij.gui.Roi;
import interactivePreprocessing.InteractiveMethods;
import net.imglib2.Cursor;
import net.imglib2.RandomAccess;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.algorithm.componenttree.mser.MserTree;
import net.imglib2.img.array.ArrayImgFactory;
import net.imglib2.type.NativeType;
import net.imglib2.type.logic.BitType;
import net.imglib2.type.numeric.RealType;
import net.imglib2.util.Pair;
import net.imglib2.view.Views;
import snakeSegmentation.ABSnakeFast;
import snakeSegmentation.SnakeUtils;
import utility.PreRoiobject;

public class ComputeSnakeSeg <T extends RealType<T> & NativeType<T>> {

	final InteractiveMethods parent;
	final JProgressBar jpb;
	public final RandomAccessibleInterval<T> source;

	public RandomAccessibleInterval<BitType> bitimg;
	public boolean apply3D;
	public int z;
	public int t;
	String uniqueID;
	public ComputeSnakeSeg(final InteractiveMethods parent, final RandomAccessibleInterval<T> source,
			final JProgressBar jpb, boolean apply3D, int z, int t) {

		this.parent = parent;
		this.source = source;
		this.jpb = jpb;
		this.apply3D = apply3D;
		this.z = z;
		this.t = t;
		uniqueID = Integer.toString(z) + Integer.toString(t);
		bitimg = new ArrayImgFactory<BitType>().create(source, new BitType());
	}

	public void execute() {

		
		ArrayList<PreRoiobject> rois = parent.ZTRois.get(uniqueID);

		int nbRois = rois.size();
		
		
		boolean dialog;
		boolean dialogAdvanced;
	
		SnakeUtils<T> snakes = new SnakeUtils<T>(parent, source);
		snakes.AdvancedParameters();
		int percent = 0;
		int nThreads = Runtime.getRuntime().availableProcessors();
		// set up executor service
		final ExecutorService taskExecutor = Executors.newFixedThreadPool(nThreads);
		List<Callable<Object>> tasks = new ArrayList<Callable<Object>>();
		
         for(PreRoiobject currentroi: rois) {
			percent++;
		tasks.add(Executors.callable(new ParallelSnake<T>(parent, source, snakes, currentroi, percent, nbRois)));

		}
         try {
			taskExecutor.invokeAll(tasks);
		} catch (InterruptedException e) {

			
		}
		parent.ZTRois.put(uniqueID, parent.CurrentPreRoiobject);
		
		// Get displaced Rois
		
		ArrayList<PreRoiobject> currrentpreroi = parent.CurrentPreRoiobject;
		
		ArrayList<Roi> currentroi = new ArrayList<Roi>();
		
		for(PreRoiobject pre : currrentpreroi) {
			
			currentroi.add(pre.rois);
			
		}
		
		common3D.BinaryCreation.CreateBinaryRoi(parent, source, bitimg, currentroi, z, t);

	}



	public RandomAccessibleInterval<BitType> getBinaryimg() {

		return bitimg;
	}




}
