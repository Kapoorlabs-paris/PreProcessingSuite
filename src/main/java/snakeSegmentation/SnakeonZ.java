package snakeSegmentation;

import java.awt.Rectangle;
import java.util.ArrayList;
import snakeSegmentation.SnakeUtils;
import ij.IJ;
import ij.gui.GenericDialog;
import ij.gui.Roi;
import interactivePreprocessing.InteractiveMethods;
import interactivePreprocessing.InteractiveMethods.ValueChange;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.type.numeric.real.FloatType;
import net.imglib2.util.Pair;
import utility.PreRoiobject;


public class SnakeonZ {

	
	final InteractiveMethods parent;
	final RandomAccessibleInterval<FloatType> CurrentView;
	final ArrayList<PreRoiobject> rois;
	int nbRois, percent = 0;
	ArrayList<PreRoiobject> resultrois;
	Roi processRoi = null;
	public SnakeonZ(final InteractiveMethods parent, final RandomAccessibleInterval<FloatType> CurrentView, ArrayList<PreRoiobject> rois) {
		
		this.parent = parent;
		this.CurrentView = CurrentView;
		this.rois = rois;
	}
	
	
	public boolean process() {
		
		parent.snakeinprogress = true;
		resultrois = new ArrayList<PreRoiobject>();
		boolean dialog;
		boolean dialogAdvanced;
		
		
		SnakeUtils snakes = new SnakeUtils(parent, CurrentView);
		snakes.AdvancedParameters();
	
		
		if (parent.AutoSnake)
			dialog = false;
		else
			dialog = snakes.Dialogue();
	
		
		nbRois = rois.size();
		ABSnakeFast snake;
		
		for(PreRoiobject currentroi: rois) {
			
			percent++;
			
			utility.ProgressBar.SetProgressBar(parent.jpb, 100 * percent / nbRois,
					"Computing snake segmentation for " +   " T = " + parent.fourthDimension 
							+ " Z = " + parent.thirdDimension + "/" + parent.thirdDimensionSize);
			
			
			snake = snakes.processSnake(currentroi.rois, percent);
			
			Roi Roiresult = snake.createRoi();
			double[] geometriccenter = Roiresult.getContourCentroid();
			final Pair<Double, Integer> Intensityandpixels = PreRoiobject.getIntensity(currentroi.rois, CurrentView);
			final double intensity = Intensityandpixels.getA();
			final double numberofpixels = Intensityandpixels.getB();
			final double averageintensity = intensity / numberofpixels;
			
			PreRoiobject currentobject = new PreRoiobject(Roiresult, geometriccenter, numberofpixels, intensity, averageintensity, parent.thirdDimension, parent.fourthDimension);
			resultrois.add(currentobject);

		}
		
		
		
		
		return true;
	}
	
	
	public ArrayList<PreRoiobject> getResult(){
		
		
		return resultrois;
	}
	
	
	

}
