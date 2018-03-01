package snakeSegmentation;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;

import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingWorker;

import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;

import costMatrix.PixelratiowDistCostFunction;
import ij.ImagePlus;
import interactivePreprocessing.InteractiveMethods;
import interactivePreprocessing.InteractiveMethods.ValueChange;
import linkers.FeatureModel3D;
import linkers.KFsearch;
import linkers.Model3D;
import linkers.TrackModel3D;
import net.imglib2.img.display.imagej.ImageJFunctions;
import utility.PreRoiobject;
import utility.ThreeDRoiobject;
import visualization.AbstractCovistoModelView;
import visualization.CovistoModelView;
import visualization.Draw3DLines;
import visualization.DummyTrackColorGenerator;
import visualization.HyperStackDisplayer;
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
		KFsearch Tsearch = new KFsearch(parent.threeDTRois, parent.UserchosenCostFunction, parent.maxSearchradius, parent.initialSearchradius, parent.maxframegap, parent.Accountedframes, parent.jpb);
		Tsearch.process();
		SimpleWeightedGraph< ThreeDRoiobject, DefaultWeightedEdge > Tgraph = Tsearch.getResult();
		
		parent.Timetracks = utility.AnalyzeTTrack.get3DTracks(Tgraph);
		Visualize3D vis = new Visualize3D(parent);
		
		vis.CreateTable();
		vis.mark();
		vis.set();
		
		
		// Trackmate style track display
		/*
		Model3D model = new Model3D();
		model.setTracks(Tgraph, true);
		SelectionModel selmode = new SelectionModel(model); 
		
		ImagePlus imp =  ImageJFunctions.show(parent.originalimg);
		HyperStackDisplayer modelview = new HyperStackDisplayer( model, selmode, imp );
		
		System.out.println(model.getTrackModel().nTracks(true));
		modelview.setDisplaySettings(CovistoModelView.KEY_TRACK_COLORING, new DummyTrackColorGenerator());
	    TrackOverlay overlay = new TrackOverlay(model, imp, modelview.getDisplaySettings());
	    overlay.setTrackColorGenerator(new DummyTrackColorGenerator());
	    
	  
	    Graphics g = imp.getCanvas().getGraphics();
	   System.out.println(g);
	    overlay.drawOverlay(g);
		*/
	
		
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
