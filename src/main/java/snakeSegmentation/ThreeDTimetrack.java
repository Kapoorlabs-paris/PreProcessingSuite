package snakeSegmentation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;

import javax.swing.SwingWorker;

import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;

import costMatrix.PixelratiowDistCostFunction;
import interactivePreprocessing.InteractiveMethods;
import linkers.KFsearch;
import utility.PreRoiobject;
import utility.ThreeDRoiobject;

public class ThreeDTimetrack extends SwingWorker<Void, Void> {
	
	final InteractiveMethods parent;
	
	public ThreeDTimetrack(final InteractiveMethods parent) {
		
		this.parent= parent;
	}
	
	@Override
	protected Void doInBackground() throws Exception {
		
		parent.UserchosenCostFunction = new PixelratiowDistCostFunction(parent.alpha, parent.beta);
		KFsearch Tsearch = new KFsearch(parent.threeDTRois, parent.UserchosenCostFunction, parent.maxSearchradius, parent.initialSearchradius, parent.maxframegap, parent.Accountedframes, parent.jpb);
		Tsearch.process();
		SimpleWeightedGraph< ThreeDRoiobject, DefaultWeightedEdge > Tgraph = Tsearch.getResult();
		parent.Timetracks = utility.AnalyzeTTrack.get3DTracks(Tgraph);
		
		
		
		return null;
	}
	
	@Override
	protected void done() {
		try {
			utility.ProgressBar.SetProgressBar(parent.jpb, "Done");
			get();
		} catch (ExecutionException | InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	
}
