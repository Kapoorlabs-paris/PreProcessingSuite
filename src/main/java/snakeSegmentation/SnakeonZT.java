package snakeSegmentation;

import java.awt.Rectangle;
import java.util.ArrayList;

import dogSeg.DOGSeg;
import snakeSegmentation.SnakeUtils;
import ij.IJ;
import ij.gui.GenericDialog;
import ij.gui.Roi;
import interactivePreprocessing.InteractiveMethods;
import interactivePreprocessing.InteractiveMethods.ValueChange;
import mserMethods.MSERSeg;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.type.numeric.real.FloatType;
import net.imglib2.util.Pair;
import utility.PreRoiobject;


public class SnakeonZT {

	
	final InteractiveMethods parent;
	final RandomAccessibleInterval<FloatType> CurrentView;
	int nbRois, percent = 0;
	ArrayList<PreRoiobject> resultrois;
	Roi processRoi = null;
	public SnakeonZT(final InteractiveMethods parent, final RandomAccessibleInterval<FloatType> CurrentView) {
		
		this.parent = parent;
		this.CurrentView = CurrentView;
	}
	
	
	public boolean process() {
		parent.snakeinprogress = true;
		parent.zslider.setEnabled(false);
		parent.timeslider.setEnabled(false);
		parent.inputFieldT.setEnabled(false);
		parent.inputFieldZ.setEnabled(false);
		resultrois = new ArrayList<PreRoiobject>();
		boolean dialog;
		boolean dialogAdvanced;
		
		SnakeUtils snakes = new SnakeUtils(parent, CurrentView);
		snakes.AdvancedParameters();
	
		
		ArrayList<PreRoiobject> rois = parent.CurrentPreRoiobject;

		if (parent.AutoSnake)
			dialog = false;
		else
			dialog = snakes.Dialogue();
		// many rois
	
		
		nbRois = rois.size();
		ABSnakeFast snake;
		for(PreRoiobject currentroi: rois) {
			
			percent++;
			
			utility.ProgressBar.SetProgressBar(parent.jpb, 100 * percent / nbRois,
					"Computing snake segmentation for " + " T = " +  parent.fourthDimension + "/" + parent.fourthDimensionSize 
							+ " Z = " + parent.thirdDimension+ "/" + parent.thirdDimensionSize);
			Roi current = currentroi.rois;
			
			snake = snakes.processSnake(current, percent);
			
			Roi Roiresult = snake.createRoi();
			double[] geocenter = Roiresult.getContourCentroid();
			final Pair<Double, Integer> Intensityandpixels = PreRoiobject.getIntensity(Roiresult, CurrentView);
			final double intensity = Intensityandpixels.getA();
			final double numberofpixels = Intensityandpixels.getB();
			final double averageintensity = intensity / numberofpixels;
			
			PreRoiobject currentobject = new PreRoiobject(Roiresult,  new double [] {geocenter[0], geocenter[1], parent.thirdDimension}, numberofpixels, intensity, averageintensity, parent.thirdDimension, parent.fourthDimension);
			resultrois.add(currentobject);

		}
		
		parent.zslider.setEnabled(true);
		parent.timeslider.setEnabled(true);
		parent.inputFieldT.setEnabled(true);
		parent.inputFieldZ.setEnabled(true);
		
		
		return true;
	}
	
	
	public ArrayList<PreRoiobject> getResult(){
		
		
		return resultrois;
	}
	
	
	

}
