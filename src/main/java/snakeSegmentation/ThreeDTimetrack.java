package snakeSegmentation;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingWorker;

import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;

import costMatrix.PixelratiowDistCostFunction;
import ij.ImagePlus;
import ij3d.Image3DUniverse;
import interactivePreprocessing.InteractiveMethods;
import interactivePreprocessing.InteractiveMethods.ValueChange;
import linkers.FeatureModel3D;
import linkers.KFsearch;
import linkers.Model3D;
import linkers.TrackModel3D;
import net.imglib2.img.display.imagej.ImageJFunctions;
import threeDViewer.ThreeDRoiobjectDisplayer;
import utility.PreRoiobject;
import utility.ThreeDRoiobject;
import utility.ThreeDRoiobjectCollection;
import visualization.AbstractCovistoModelView;
import visualization.CovistoModelView;
import visualization.Draw3DLines;
import visualization.DummyTrackColorGenerator;
import visualization.SelectionModel;
import visualization.TrackColorGenerator;
import visualization.TrackDisplayNode;
import visualization.TrackOverlay;
import visualization.Visualize3D;
import static visualization.CovistoModelView.KEY_ThreeDRoiobject_COLORING;
public class ThreeDTimetrack extends SwingWorker<Void, Void> {
	
	final InteractiveMethods parent;
	
	public ThreeDTimetrack(final InteractiveMethods parent) {
		
		this.parent= parent;
	}
	
	@Override
	protected Void doInBackground() throws Exception {
		
		parent.UserchosenCostFunction = new PixelratiowDistCostFunction(parent.alpha, parent.beta);
		ThreeDRoiobjectCollection coll = new ThreeDRoiobjectCollection();
		for(Map.Entry<Integer, ArrayList<ThreeDRoiobject>> entry : parent.threeDTRois.entrySet()) {
			
			int time = entry.getKey();
			ArrayList<ThreeDRoiobject> bloblist = entry.getValue();
			
			for (ThreeDRoiobject blobs: bloblist) {
				
				coll.add(blobs, time);
				
			}
			
			
		}
		
		
		KFsearch Tsearch = new KFsearch(coll, parent.UserchosenCostFunction, parent.maxSearchradius, parent.initialSearchradius, parent.maxframegap, parent.Accountedframes, parent.jpb);
		Tsearch.process();
		SimpleWeightedGraph< ThreeDRoiobject, DefaultWeightedEdge > Tgraph = Tsearch.getResult();
		
		parent.Timetracks = utility.AnalyzeTTrack.get3DTracks(Tgraph);
		Visualize3D vis = new Visualize3D(parent);

		Object[] colnames = new Object[] { "Track Id", "SLocation X", "SLocation Y", "SLocation Z", "Volume", "Intensity Total", "Intenstiy Average" };

		Object[][] rowvalues =  new Object[parent.Timetracks.size()][colnames.length];

		parent.table = new JTable(rowvalues, colnames);

		parent.table.setFillsViewportHeight(true);

	
		parent.table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

		parent.table.setMinimumSize(new Dimension(800, 500));

		parent.scrollPane = new JScrollPane(parent.table);
		parent.scrollPane.setMinimumSize(new Dimension(800, 500));

		parent.scrollPane.getViewport().add(parent.table);
		parent.scrollPane.setAutoscrolls(true);
		
		vis.CreateTable();
		vis.mark();
		vis.set();
		
		
	// Trackmate style track display
		
		
		parent.model.setThreeDRoiobjects(coll, false);
		parent.model.setTracks(Tgraph, true);
		
		parent.selmode = new SelectionModel(parent.model); 
		
	 

	
		
		
	
		
	
		
		
		
	  
		
	
		
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
