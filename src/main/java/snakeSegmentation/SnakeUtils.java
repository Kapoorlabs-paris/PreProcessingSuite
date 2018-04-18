package snakeSegmentation;

import ij.IJ;
import ij.gui.GenericDialog;
import ij.gui.Roi;
import interactivePreprocessing.InteractiveMethods;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.type.NativeType;
import net.imglib2.type.numeric.RealType;
import net.imglib2.type.numeric.real.FloatType;

public class SnakeUtils <T extends RealType<T> & NativeType<T>> {

	/*
	 * Dialog advanced
	 *
	 * @return dialog ok ?
	 */
	final InteractiveMethods parent;
	Roi processRoi = null;
	final RandomAccessibleInterval<T> CurrentView;
	public  SnakeUtils(final InteractiveMethods parent, final RandomAccessibleInterval<T> CurrentView) {
		
		this.parent = parent;
		this.CurrentView = CurrentView;
		
	}
	
	public ABSnakeFast<T> processSnake(Roi roi, int numRoi) {

		int i;

		SnakeConfig config;

		processRoi = roi;

		// initialisation of the snake
		ABSnakeFast<T> snake = new ABSnakeFast<T>();
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

			if (Math.abs(dist0 - dist) < 0.05){
				
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
	public boolean Dialogue() {

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

	


	public void AdvancedParameters() {
		// see advanced dialog class
		parent.configDriver.setMaxDisplacement(parent.Displacement_min, parent.Displacement_max);
		parent.configDriver.setInvAlphaD(parent.Inv_alpha_min, parent.Inv_alpha_max);
		parent.configDriver.setReg(parent.regmin, parent.regmax);
		parent.configDriver.setStep(parent.Mul_factor);
	}
	
}
