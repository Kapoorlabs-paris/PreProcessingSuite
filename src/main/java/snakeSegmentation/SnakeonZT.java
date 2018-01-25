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
import utility.Roiobject;


public class SnakeonZT {

	
	final InteractiveMethods parent;
	final RandomAccessibleInterval<FloatType> CurrentView;
	final ArrayList<Roiobject> rois;
	int nbRois, percent = 0;
	ArrayList<Roiobject> resultrois;
	Roi processRoi = null;
	public SnakeonZT(final InteractiveMethods parent, final RandomAccessibleInterval<FloatType> CurrentView, ArrayList<Roiobject> rois) {
		
		this.parent = parent;
		this.CurrentView = CurrentView;
		this.rois = rois;
	}
	
	
	public boolean process() {
		parent.snakeinprogress = true;
		resultrois = new ArrayList<Roiobject>();
		boolean dialog;
		boolean dialogAdvanced;
		
		SnakeUtils snakes = new SnakeUtils(parent, CurrentView);
		snakes.AdvancedParameters();
	
		
		if (parent.AutoSnake)
			dialog = false;
		else
			dialog = snakes.Dialogue();
		// many rois
	
		
		nbRois = rois.size();
		ABSnakeFast snake;
		for(Roiobject currentroi: rois) {
			
			percent++;
			
			utility.ProgressBar.SetProgressBar(parent.jpb, 100 * percent / nbRois,
					"Computing snake segmentation for " + " T = " +  parent.fourthDimension + "/" + parent.fourthDimensionSize 
							+ " Z = " + parent.thirdDimension);
			
			
			snake = snakes.processSnake(currentroi.rois, percent);
			
			Roi Roiresult = snake.createRoi();
			double[] geometriccenter = Roiresult.getContourCentroid();
			final Pair<Double, Integer> Intensityandpixels = Roiobject.getIntensity(currentroi.rois, CurrentView);
			final double intensity = Intensityandpixels.getA();
			final double numberofpixels = Intensityandpixels.getB();
			final double averageintensity = intensity / numberofpixels;
			
			Roiobject currentobject = new Roiobject(Roiresult, geometriccenter, numberofpixels, intensity, averageintensity, parent.thirdDimension, parent.fourthDimension);
			resultrois.add(currentobject);

		}
		
		
		
		
		return true;
	}
	
	
	public ArrayList<Roiobject> getResult(){
		
		
		return resultrois;
	}
	
	
	

}
