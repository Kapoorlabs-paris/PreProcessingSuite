package snakeSegmentation;

import java.awt.Rectangle;
import java.util.ArrayList;

import ij.IJ;
import ij.gui.GenericDialog;
import ij.gui.Roi;
import interactivePreprocessing.InteractiveMethods;
import interactivePreprocessing.InteractiveMethods.ValueChange;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.type.numeric.real.FloatType;
import net.imglib2.util.Pair;
import utility.Roiobject;


public class SnakeonView {

	
	final InteractiveMethods parent;
	final RandomAccessibleInterval<FloatType> CurrentView;
	final ArrayList<Roiobject> rois;
	int nbRois, percent = 0;
	ArrayList<Roiobject> resultrois;
	Roi processRoi = null;
	public SnakeonView(final InteractiveMethods parent, final RandomAccessibleInterval<FloatType> CurrentView, ArrayList<Roiobject> rois) {
		
		this.parent = parent;
		this.CurrentView = CurrentView;
		this.rois = rois;
	}
	
	
	public boolean process() {
		
		resultrois = new ArrayList<Roiobject>();
		boolean dialog;
		boolean dialogAdvanced;
		if (parent.AutoSnake)
			dialog = false;
		else
			dialog = Dialogue();
		// many rois
		if (parent.advancedSnake)
			dialogAdvanced = AdvancedDialog();
		
		nbRois = rois.size();
		ABSnakeFast snake;
		for(Roiobject currentroi: rois) {
			
			percent++;
			
			utility.ProgressBar.SetProgressBar(parent.jpb, 100 * percent / nbRois,
					"Computing snake segmentation for " + parent.fourthDimension 
							+ " Z = " + parent.thirdDimension);
			
			
			snake = processSnake(currentroi.rois, percent);
			
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
	
	public ABSnakeFast processSnake(Roi roi, int numRoi) {

		int i;

		SnakeConfig config;

		processRoi = roi;

		// initialisation of the snake
		ABSnakeFast snake = new ABSnakeFast();
		snake.Init(processRoi);

		snake.setOriginalImage(CurrentView);

		// start of computation
		IJ.showStatus("Calculating snake...");

		double InvAlphaD = parent.configDriver.getInvAlphaD(false);
		double regMax = parent.configDriver.getReg(false);
		double regMin = parent.configDriver.getReg(true);
		double DisplMax = parent.configDriver.getMaxDisplacement(false);
		double mul = parent.configDriver.getStep();

		config = new SnakeConfig(parent.Gradthresh, DisplMax, parent.DistMax, regMin, regMax, 1.0 / InvAlphaD);
		snake.setConfig(config);
		// compute image gradient
		snake.computeGrad(CurrentView);

		double dist0 = 0.0;
		double dist;
		double olddist;
		double term = 0;

		for (i = 0; i < parent.snakeiterations; i++) {
		
			// each iteration
			dist = snake.process();

			if (Math.abs(dist0 - dist) < 0.1){
				
				term++;
			}
			if (term > 10)
				break;
			
			if ((dist >= dist0) && (dist < parent.force)) {
				// System.out.println("update " + config.getAlpha());
				snake.computeGrad(CurrentView);
				config.update(mul);
			}
			dist0 = dist;

			// display of the snake
			if ((parent.displaysnake > 0) && ((i % parent.displaysnake) == 0)) {

			}
		}

		snake.setOriginalImage(null);

		return snake;
	}
	private boolean Dialogue() {

		// create dialog
		GenericDialog gd = new GenericDialog("Snake");
		gd.addNumericField("Gradient_threshold:", parent.Gradthresh, 0);
		gd.addNumericField("Number_of_iterations:", parent.snakeiterations, 0);
		gd.addNumericField("Display Snake result after:", parent.displaysnake, 0);
		gd.addCheckbox("Advanced_options", parent.advancedSnake);
	

		// show dialog
		gd.showDialog();

		// threshold of edge
		parent.Gradthresh = (int) gd.getNextNumber();
		// number of iterations
		parent.snakeiterations = (int) gd.getNextNumber();

		parent.displaysnake = (int) gd.getNextNumber();

		
		parent.advancedSnake = gd.getNextBoolean();

		

		return !gd.wasCanceled();
	}

	/**
	 * Dialog advanced
	 *
	 * @return dialog ok ?
	 */

	private boolean AdvancedDialog() {

		// dialog
		GenericDialog gd = new GenericDialog("Snake Advanced");
		gd.addNumericField("Distance_Search", parent.DistMax, 0);
		gd.addNumericField("Displacement_min", parent.Displacement_min, 2);
		gd.addNumericField("Displacement_max", parent.Displacement_max, 2);
		gd.addNumericField("Threshold_dist_positive", parent.Threshold_dist_positive, 0);
		gd.addNumericField("Threshold_dist_negative", parent.Threshold_dist_negative, 0);
		gd.addNumericField("Inv_alpha_min", parent.Inv_alpha_min, 2);
		gd.addNumericField("Inv_alpha_max", parent.Inv_alpha_max, 2);
		gd.addNumericField("Reg_min", parent.Reg_min, 2);
		gd.addNumericField("Reg_max", parent.Reg_max, 2);
		gd.addNumericField("Mul_factor", parent.Mul_factor, 4);
		// show dialog
		gd.showDialog();

		parent.DistMax = (int) gd.getNextNumber();
		parent.Displacement_min = gd.getNextNumber();
		parent.Displacement_max = gd.getNextNumber();
		parent.Threshold_dist_positive = gd.getNextNumber();
		parent.Threshold_dist_negative = gd.getNextNumber();
		parent.Inv_alpha_min = gd.getNextNumber();
		parent.Inv_alpha_max = gd.getNextNumber();
		parent.Reg_min = gd.getNextNumber();
		parent.Reg_max = gd.getNextNumber();
		parent.Mul_factor = gd.getNextNumber();

		return !gd.wasCanceled();

	}

	private void AdvancedParameters() {
		// see advanced dialog class
		parent.configDriver.setMaxDisplacement(parent.Displacement_min, parent.Displacement_max);
		parent.configDriver.setInvAlphaD(parent.Inv_alpha_min, parent.Inv_alpha_max);
		parent.configDriver.setReg(parent.Reg_min, parent.Reg_max);
		parent.configDriver.setStep(parent.Mul_factor);
	}

}
