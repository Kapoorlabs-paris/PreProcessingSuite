package snake3D;

import java.util.ArrayList;

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

		parent.snakeinprogress = true;

		ArrayList<PreRoiobject> rois = parent.ZTRois.get(uniqueID);

		int nbRois = rois.size();
		ABSnakeFast<T> snake;
		
		
		boolean dialog;
		boolean dialogAdvanced;
		parent.zslider.setEnabled(false);
		parent.timeslider.setEnabled(false);
		parent.inputFieldT.setEnabled(false);
		parent.inputFieldZ.setEnabled(false);
		SnakeUtils<T> snakes = new SnakeUtils(parent, source);
		snakes.AdvancedParameters();
		int percent = 0;
         for(PreRoiobject currentroi: rois) {
			
			percent++;
			
			utility.CovsitoProgressBar.CovistoSetProgressBar(parent.jpb, 100 * percent / nbRois,
					"Computing snake segmentation for " +   " T = " + parent.fourthDimension  + "/" + parent.fourthDimensionSize
							+ " Z = " + parent.thirdDimension + "/" + parent.thirdDimensionSize);
			
			
			
			snake = snakes.processSnake(currentroi.rois, percent);
			
			Roi Roiresult = snake.createRoi();
			double[] geocenter = Roiresult.getContourCentroid();
			final Pair<Double, Integer> Intensityandpixels = PreRoiobject.getIntensity(currentroi.rois, source);
			final double intensity = Intensityandpixels.getA();
			final double numberofpixels = Intensityandpixels.getB();
			final double averageintensity = intensity / numberofpixels;
			
			PreRoiobject currentobject = new PreRoiobject(Roiresult, new double [] {geocenter[0], geocenter[1], parent.thirdDimension}, numberofpixels, intensity, averageintensity, parent.thirdDimension, parent.fourthDimension);
			parent.CurrentPreRoiobject.add(currentobject);

		}
		
		parent.ZTRois.put(uniqueID, parent.CurrentPreRoiobject);
		
		// Get displaced Rois
		
		ArrayList<PreRoiobject> currrentpreroi = parent.CurrentPreRoiobject;
		
		ArrayList<Roi> currentroi = new ArrayList<Roi>();
		
		for(PreRoiobject pre : currrentpreroi) {
			
			currentroi.add(pre.rois);
			
		}
		
		common3D.BinaryCreation.CreateBinary(parent, source, bitimg, currentroi, z, t);

	}



	public RandomAccessibleInterval<BitType> getBinaryimg() {

		return bitimg;
	}




}
