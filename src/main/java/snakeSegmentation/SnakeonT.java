package snakeSegmentation;

import java.awt.Rectangle;
import java.util.ArrayList;

import dogSeg.DOGSeg;
import snakeSegmentation.SnakeUtils;
import timeGUI.CovistoTimeselectPanel;
import ij.IJ;
import ij.gui.GenericDialog;
import ij.gui.Roi;
import interactivePreprocessing.InteractiveMethods;
import interactivePreprocessing.InteractiveMethods.ValueChange;
import mserMethods.MSERSeg;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.type.NativeType;
import net.imglib2.type.numeric.RealType;
import net.imglib2.type.numeric.real.FloatType;
import net.imglib2.util.Pair;
import utility.PreRoiobject;
import zGUI.CovistoZselectPanel;


public class SnakeonT  <T extends RealType<T> & NativeType<T>>  {

	
	final InteractiveMethods parent;
	final RandomAccessibleInterval<FloatType> CurrentView;
	int nbRois, percent = 0;
	ArrayList<PreRoiobject> resultrois;
	Roi processRoi = null;
	public SnakeonT(final InteractiveMethods parent, final RandomAccessibleInterval<FloatType> CurrentView) {
		
		this.parent = parent;
		this.CurrentView = CurrentView;
	}
	
	
	public boolean process() {
		CovistoZselectPanel.zslider.setEnabled(false);
		CovistoTimeselectPanel.timeslider.setEnabled(false);
		CovistoTimeselectPanel.inputFieldT.setEnabled(false);
		CovistoZselectPanel.inputFieldZ.setEnabled(false);
		resultrois = new ArrayList<PreRoiobject>();
		boolean dialog;
		boolean dialogAdvanced;
		
		SnakeUtils<T> snakes = new SnakeUtils(parent, CurrentView);
		snakes.AdvancedParameters();
		
		ArrayList<PreRoiobject> rois = parent.CurrentPreRoiobject;
		
		if (parent.AutoSnake)
			dialog = false;
		else
			dialog = snakes.Dialogue();
		// many rois
	
		
		nbRois = rois.size();
		ABSnakeFast<T> snake;
		for(PreRoiobject currentroi: rois) {
			
			percent++;
			
			utility.CovsitoProgressBar.CovistoSetProgressBar(parent.jpb, 100 * percent / nbRois,
					"Computing snake segmentation for " + " T = " +  CovistoTimeselectPanel.fourthDimension + "/" + CovistoTimeselectPanel.fourthDimensionSize 
							+ " Z = " + CovistoZselectPanel.thirdDimension+ "/" + CovistoZselectPanel.thirdDimensionSize);
			
			
            Roi current = currentroi.rois;
			
			snake = snakes.processSnake(current, percent);
			
			Roi Roiresult = snake.createRoi();
			double[] geocenter = Roiresult.getContourCentroid();
			final Pair<Double, Integer> Intensityandpixels = PreRoiobject.getIntensity(Roiresult, CurrentView);
			final double intensity = Intensityandpixels.getA();
			final double numberofpixels = Intensityandpixels.getB();
			final double averageintensity = intensity / numberofpixels;
			
			PreRoiobject currentobject = new PreRoiobject(Roiresult, new double [] {geocenter[0], geocenter[1], CovistoZselectPanel.thirdDimension}, numberofpixels, 
					intensity, averageintensity, CovistoZselectPanel.thirdDimension, CovistoTimeselectPanel.fourthDimension);
			resultrois.add(currentobject);

		}
		
		CovistoZselectPanel.zslider.setEnabled(true);
		CovistoTimeselectPanel.timeslider.setEnabled(true);
		CovistoTimeselectPanel.inputFieldT.setEnabled(true);
		CovistoZselectPanel.inputFieldZ.setEnabled(true);
		
		
		return true;
	}
	
	
	public ArrayList<PreRoiobject> getResult(){
		
		
		return resultrois;
	}
	
	
	

}
