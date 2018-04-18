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
import linkers.PRENNsearch;
import net.imglib2.Interval;
import net.imglib2.util.Intervals;
import net.imglib2.util.Pair;
import net.imglib2.view.Views;
import utility.PreRoiobject;
import utility.ThreeDRoiobject;

public class SingleAllTrack extends SwingWorker<Void, Void> {

	final InteractiveMethods parent;

	public SingleAllTrack(final InteractiveMethods parent) {

		this.parent = parent;

	}

	@Override
	protected Void doInBackground() throws Exception {

		for (Map.Entry<String, Integer> entrytime : parent.Accountedframes.entrySet()) {
			
			
			int time = entrytime.getValue();
		
		PRENNsearch Zsearch = new PRENNsearch(parent.ZTRois, parent.maxSearchradius, time, parent.AccountedZ, parent.jpb);
		Zsearch.process();
		SimpleWeightedGraph< PreRoiobject, DefaultWeightedEdge > Zgraph = Zsearch.getResult();
		HashMap<Integer, ArrayList<PreRoiobject> > currentRoiobject = utility.AnalzeZTrack.get3Dobjects(Zgraph);
		ArrayList<ThreeDRoiobject> AllRois = new ArrayList<ThreeDRoiobject>();
		for(Map.Entry<Integer, ArrayList<PreRoiobject>> entry: currentRoiobject.entrySet()) {
			
			ArrayList<PreRoiobject> currentobject = entry.getValue();
			
			double[] geometriccenter = ThreeDRoiobject.getCentroid3D( currentobject);
			final Pair<Double, Integer> Intensityandpixels = ThreeDRoiobject.getIntensity3D(currentobject);
			ThreeDRoiobject current3D = new ThreeDRoiobject(currentobject, geometriccenter, Intensityandpixels.getB(), Intensityandpixels.getA(), Intensityandpixels.getA() / Intensityandpixels.getB(), parent.fourthDimension);
			AllRois.add(current3D);
			
		}
		parent.threeDTRois.put(time, AllRois);
		
		}
		return null;
	}

	@Override
	protected void done() {
		try {
			ThreeDTimetrack dosnake = new ThreeDTimetrack(parent);
			dosnake.execute();
			utility.CovsitoProgressBar.CovistoSetProgressBar(parent.jpb, "Done");
			get();
		} catch (ExecutionException | InterruptedException e) {
			e.printStackTrace();
		}

	}

}
