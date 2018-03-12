package interactivePreprocessing;

import java.awt.Color;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import javax.swing.JOptionPane;
import javax.swing.SwingWorker;

import ij.ImagePlus;
import ij.gui.EllipseRoi;
import ij.gui.Roi;
import ij.process.ColorProcessor;
import interactivePreprocessing.InteractiveMethods;
import interactivePreprocessing.InteractiveMethods.ValueChange;
import net.imglib2.Interval;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.algorithm.componenttree.mser.MserTree;
import net.imglib2.img.display.imagej.ImageJFunctions;
import net.imglib2.type.logic.BitType;
import net.imglib2.type.numeric.real.FloatType;
import net.imglib2.util.Intervals;
import net.imglib2.util.Pair;
import net.imglib2.view.Views;
import utility.PreRoiobject;

public class ZMser extends SwingWorker<Void, Void> {

	final InteractiveMethods parent;

	public ZMser(final InteractiveMethods parent) {

		this.parent = parent;

	}

	@Override
	protected Void doInBackground() throws Exception {

		ImagePlus Localimp = ImageJFunctions.show(parent.originalimg);
		for (int z = parent.thirdDimensionsliderInit; z <= parent.thirdDimensionSize; ++z) {

			parent.thirdDimension = z;

			parent.CurrentView = utility.Slicer.getCurrentView(parent.originalimg, z, parent.thirdDimensionSize,
					parent.fourthDimension, parent.fourthDimensionSize);
			parent.updatePreview(ValueChange.THIRDDIM);

			parent.prestack.addSlice(Localimp.getImageStack().getProcessor(z).convertToRGB());
			parent.cp = (ColorProcessor) (parent.prestack.getProcessor(z).duplicate());
			utility.ProgressBar.SetProgressBar(parent.jpb, "Computing Component Tree for MSER, Please Wait...");

			if (parent.darktobright)

				parent.newtree = MserTree.buildMserTree(parent.newimg, parent.delta, parent.minSize, parent.maxSize,
						parent.Unstability_Score, parent.minDiversity, true);

			else

				parent.newtree = MserTree.buildMserTree(parent.newimg, parent.delta, parent.minSize, parent.maxSize,
						parent.Unstability_Score, parent.minDiversity, false);
			parent.overlay.clear();
			parent.Rois = utility.FinderUtils.getcurrentRois(parent.newtree);

			parent.CurrentPreRoiobject = new ArrayList<PreRoiobject>();
			ArrayList<double[]> centerRoi = utility.FinderUtils.getRoiMean(parent.newtree);

			for (int index = 0; index < centerRoi.size(); ++index) {

				Roi or = parent.Rois.get(index);

				or.setStrokeColor(parent.colorDrawMser);
				parent.overlay.add(or);

			}
			for (Roi currentroi : parent.Rois) {

				currentroi.setStrokeColor(Color.red);

				parent.cp.setColor(Color.red);
				parent.cp.setLineWidth(1);
				parent.cp.draw(currentroi);

			}
			parent.imp.setOverlay(parent.overlay);
			parent.imp.updateAndDraw();

			parent.prestack.setPixels(parent.cp.getPixels(), z);
			Localimp.hide();
		}

		return null;

	}

	@Override
	protected void done() {

		new ImagePlus("Sim", parent.prestack).show();

		try {
			utility.ProgressBar.SetProgressBar(parent.jpb, "Done");
			get();
		} catch (ExecutionException | InterruptedException e) {
			e.printStackTrace();
		}

	}

}
