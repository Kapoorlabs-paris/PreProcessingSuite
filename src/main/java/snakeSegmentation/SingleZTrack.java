package snakeSegmentation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import javax.swing.JOptionPane;
import javax.swing.SwingWorker;

import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;

import interactivePreprocessing.InteractiveMethods;
import interactivePreprocessing.InteractiveMethods.ValueChange;
import kalmanGUI.CovistoKalmanPanel;
import linkers.PRENNsearch;
import nearestNeighbourGUI.CovistoNearestNPanel;
import net.imglib2.Interval;
import net.imglib2.util.Intervals;
import net.imglib2.util.Pair;
import net.imglib2.view.Views;
import timeGUI.CovistoTimeselectPanel;
import utility.PreRoiobject;
import utility.ThreeDRoiobject;

public class SingleZTrack extends SwingWorker<Void, Void> {

	final InteractiveMethods parent;

	public SingleZTrack(final InteractiveMethods parent) {

		this.parent = parent;

	}

	@Override
	protected Void doInBackground() throws Exception {

		PRENNsearch Zsearch = new PRENNsearch(parent.ZTRois, CovistoNearestNPanel.maxSearchradiusNearest, CovistoTimeselectPanel.fourthDimension, parent.AccountedZ, parent.jpb);
		Zsearch.process();
		SimpleWeightedGraph< PreRoiobject, DefaultWeightedEdge > Zgraph = Zsearch.getResult();
		HashMap<Integer, ArrayList<PreRoiobject> > currentRoiobject = utility.AnalzeZTrack.get3Dobjects(Zgraph);
		ArrayList<ThreeDRoiobject> AllRois = new ArrayList<ThreeDRoiobject>();
		for(Map.Entry<Integer, ArrayList<PreRoiobject>> entry: currentRoiobject.entrySet()) {
			
			ArrayList<PreRoiobject> currentobject = entry.getValue();
			
			double[] geometriccenter = ThreeDRoiobject.getCentroid3D( currentobject);
			final Pair<Double, Integer> Intensityandpixels = ThreeDRoiobject.getIntensity3D(currentobject);
			ThreeDRoiobject current3D = new ThreeDRoiobject(currentobject, geometriccenter, Intensityandpixels.getB(), Intensityandpixels.getA(), Intensityandpixels.getA() / Intensityandpixels.getB(), CovistoTimeselectPanel.fourthDimension);
			AllRois.add(current3D);
			
		}
		parent.threeDTRois.put(CovistoTimeselectPanel.fourthDimension, AllRois);
		
		
		return null;
	}

	@Override
	protected void done() {
		try {
			utility.CovsitoProgressBar.CovistoSetProgressBar(parent.jpb, "Done");
			get();
		} catch (ExecutionException | InterruptedException e) {
			e.printStackTrace();
		}

	}

}
