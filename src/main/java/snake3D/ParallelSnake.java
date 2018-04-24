package snake3D;

import ij.gui.Roi;
import interactivePreprocessing.InteractiveMethods;
import interactivePreprocessing.InteractiveMethods.ValueChange;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.type.NativeType;
import net.imglib2.type.numeric.RealType;
import net.imglib2.util.Pair;
import snakeSegmentation.ABSnakeFast;
import snakeSegmentation.SnakeUtils;
import timeGUI.CovistoTimeselectPanel;
import utility.PreRoiobject;
import zGUI.CovistoZselectPanel;

public class ParallelSnake <T extends RealType<T> & NativeType<T>> implements Runnable {

	final InteractiveMethods parent;
	final PreRoiobject currentroi;
	int percent;
	final int nbRois;
	final RandomAccessibleInterval<T> source;
	final SnakeUtils<T> snakes;
	public ParallelSnake(final InteractiveMethods parent,final RandomAccessibleInterval<T> source,SnakeUtils<T> snakes, final PreRoiobject currentroi, final int percent, final int nbRois) {
		
		this.parent = parent;
		this.source = source;
		this.currentroi = currentroi;
		this.percent = percent;
		this.nbRois = nbRois;
		this.snakes = snakes;
	}
	
	
	
	@Override
	public void run() {
		percent++;
		utility.CovsitoProgressBar.CovistoSetProgressBar(parent.jpb, 100 * percent / nbRois,
				"Computing snake segmentation for " +   " T = " + CovistoTimeselectPanel.fourthDimension  + "/" + CovistoTimeselectPanel.fourthDimensionSize
						+ " Z = " + CovistoZselectPanel.thirdDimension + "/" + CovistoZselectPanel.thirdDimensionSize);
		
		
		
		ABSnakeFast<T> snake = snakes.processSnake(currentroi.rois, percent);
		
		Roi Roiresult = snake.createRoi();
		double[] geocenter = Roiresult.getContourCentroid();
		final Pair<Double, Integer> Intensityandpixels = PreRoiobject.getIntensity(currentroi.rois, source);
		final double intensity = Intensityandpixels.getA();
		final double numberofpixels = Intensityandpixels.getB();
		final double averageintensity = intensity / numberofpixels;
		
		PreRoiobject currentobject = new PreRoiobject(Roiresult, new double [] {geocenter[0], geocenter[1], CovistoZselectPanel.thirdDimension}, numberofpixels, intensity, averageintensity,
				CovistoZselectPanel.thirdDimension, CovistoTimeselectPanel.fourthDimension);
		parent.CurrentPreRoiobject.add(currentobject);
	}

}
