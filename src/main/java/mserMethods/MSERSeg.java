package mserMethods;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import javax.swing.JProgressBar;
import javax.swing.SwingWorker;

import ij.gui.Roi;
import ij.process.ColorProcessor;
import interactivePreprocessing.InteractiveMethods;
import interactivePreprocessing.InteractiveMethods.ValueChange;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.algorithm.componenttree.mser.MserTree;
import net.imglib2.type.NativeType;
import net.imglib2.util.Pair;
import utility.PreRoiobject;

public class MSERSeg extends SwingWorker<Void, Void> {

	final InteractiveMethods parent;
	final JProgressBar jpb;

	public MSERSeg(final InteractiveMethods parent, final JProgressBar jpb) {

		this.parent = parent;
		this.jpb = jpb;

	}

	@Override
	protected Void doInBackground() throws Exception {
		if(!parent.snakeinprogress)
			utility.CovsitoProgressBar.CovistoSetProgressBar(jpb, "Computing Component Tree for MSER, Please Wait...");

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

				double[] center = new double[] { centerRoi.get(index)[0], centerRoi.get(index)[1], parent.thirdDimension };

				Roi or = parent.Rois.get(index);

				or.setStrokeColor(parent.colorDrawMser);
				parent.overlay.add(or);
				
			
			}
			for (Roi currentroi: parent.Rois) {
				
				final double[] geocenter = currentroi.getContourCentroid();
				final Pair<Double, Integer> Intensityandpixels = PreRoiobject.getIntensity(currentroi, parent.CurrentView);
				final double intensity = Intensityandpixels.getA();
				final double numberofpixels = Intensityandpixels.getB();
				final double averageintensity = intensity / numberofpixels;
				PreRoiobject currentobject = new PreRoiobject(currentroi, new double[] {geocenter[0], geocenter[1], parent.thirdDimension}, numberofpixels, intensity, averageintensity, parent.thirdDimension, parent.fourthDimension);
				parent.CurrentPreRoiobject.add(currentobject);
			}
			parent.imp.setOverlay(parent.overlay);
			parent.imp.updateAndDraw();
			
			
			
			if(!parent.snakeinprogress)
				utility.CovsitoProgressBar.CovistoSetProgressBar(jpb, "Done");
			parent.updatePreview(ValueChange.SNAKE);
		return null;
	}

	@Override
	protected void done() {
		
       
		
		try {
			get();
		} catch (InterruptedException e) {

		} catch (ExecutionException e) {

		}

	}

}
