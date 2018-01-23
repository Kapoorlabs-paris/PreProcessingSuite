package dogSeg;

import java.util.concurrent.ExecutionException;

import javax.swing.JProgressBar;
import javax.swing.SwingWorker;

import ij.gui.Roi;
import interactivePreprocessing.InteractiveMethods;
import net.imglib2.algorithm.dog.DogDetection;
import net.imglib2.type.numeric.real.FloatType;
import net.imglib2.view.Views;

public class DOGSeg extends SwingWorker<Void, Void> {

	final InteractiveMethods parent;
	final JProgressBar jpb;

	public DOGSeg(final InteractiveMethods parent, final JProgressBar jpb) {

		this.parent = parent;
		this.jpb = jpb;

	}

	@Override
	protected Void doInBackground() throws Exception {

		utility.ProgressBar.SetProgressBar(jpb, "Doing Difference of Gaussian Detection, Please Wait...");
		

		final DogDetection.ExtremaType type;
		if (parent.lookForMaxima)
			type = DogDetection.ExtremaType.MINIMA;
		else
			type = DogDetection.ExtremaType.MAXIMA;
		parent.sigma2 = utility.ScrollbarUtils.computeSigma2(parent.sigma, parent.sensitivity);
		final DogDetection<FloatType> newdog = new DogDetection<FloatType>(Views.extendBorder(parent.CurrentView),
				parent.interval, new double[] { 1, 1 }, parent.sigma, parent.sigma2, type, parent.threshold, true);

		parent.peaks = newdog.getSubpixelPeaks();

		return null;
	}
	
	
	@Override
	protected void done() {
		
		
		utility.ProgressBar.SetProgressBar(jpb, "Done");

		parent.Rois = utility.FinderUtils.getcurrentRois(parent.peaks, parent.sigma, parent.sigma2);
		
		
		for (int index = 0; index < parent.peaks.size(); ++index) {

			double[] center = new double[] { parent.peaks.get(index).getDoublePosition(0),
					parent.peaks.get(index).getDoublePosition(1) };

			Roi or = parent.Rois.get(index);

			or.setStrokeColor(parent.colorDrawDog);
			parent.overlay.add(or);
		}
		parent.imp.setOverlay(parent.overlay);
		parent.imp.updateAndDraw();
		try {
			get();
		} catch (InterruptedException e) {

		} catch (ExecutionException e) {

		
		}
		
	}

}
