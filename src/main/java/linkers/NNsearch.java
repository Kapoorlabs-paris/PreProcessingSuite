package linkers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;


import net.imglib2.KDTree;
import net.imglib2.RealPoint;
import utility.PreRoiobject;

public class NNsearch implements BlobTracker {

	private final HashMap<String, ArrayList<PreRoiobject>> Allblobs;
	private final double maxdistance;
	private int T;
	private SimpleWeightedGraph< PreRoiobject, DefaultWeightedEdge > graph;
	protected Logger logger = Logger.DEFAULT_LOGGER;
	protected String errorMessage;
	private HashMap<String, Integer> AccountedZ;
	public NNsearch(
			final HashMap<String, ArrayList<PreRoiobject>> Allblobs, final double maxdistance, final int T,
			final long maxframe, final HashMap<String, Integer> AccountedZ){
		this.Allblobs = Allblobs;
		this.maxdistance = maxdistance;
		this.T = T;
		this.AccountedZ = AccountedZ;
		
		
	}
	
	
	

	@Override
	public boolean process() {

		reset();
		
		Iterator<Map.Entry<String, Integer>> it = AccountedZ.entrySet().iterator();
		
		while(it.hasNext()) {
		
			
			int Z = it.next().getValue();
			
			
			while(it.hasNext()) {
				
				
				int nextZ = it.next().getValue();
			
				

				String uniqueID = Integer.toString(Z) + Integer.toString(T);
				String uniqueIDnext = Integer.toString(nextZ) + Integer.toString(T);
				
			ArrayList<PreRoiobject> Spotmaxbase = Allblobs.get(uniqueID);
			
			ArrayList<PreRoiobject> Spotmaxtarget = Allblobs.get(uniqueIDnext);
			
			
			if (Spotmaxtarget != null && Spotmaxtarget.size() > 0) {
			
			Iterator<PreRoiobject> baseobjectiterator = Spotmaxbase.iterator();
			
			
			
	        final int Targetblobs = Spotmaxtarget.size();
	        
			final List<RealPoint> targetCoords = new ArrayList<RealPoint>(Targetblobs);

			final List<FlagNode<PreRoiobject>> targetNodes = new ArrayList<FlagNode<PreRoiobject>>(Targetblobs);
			
			
	      
			for (int index = 0; index < Spotmaxtarget.size(); ++index){
				
				
				
				targetCoords.add(new RealPoint(Spotmaxtarget.get(index).geometriccenter));

				targetNodes.add(new FlagNode<PreRoiobject>(Spotmaxtarget.get(index)));
				
				
			}
			
			if (targetNodes.size() > 0 && targetCoords.size() > 0){
			
			final KDTree<FlagNode<PreRoiobject>> Tree = new KDTree<FlagNode<PreRoiobject>>(targetNodes, targetCoords);
			
			final NNFlagsearchKDtree<PreRoiobject> Search = new NNFlagsearchKDtree<PreRoiobject>(Tree);
			
			
			
			while(baseobjectiterator.hasNext()){
				
				final PreRoiobject source = baseobjectiterator.next();
				final RealPoint sourceCoords = new RealPoint(source.geometriccenter);
				Search.search(sourceCoords);
				final double squareDist = Search.getSquareDistance();
				final FlagNode<PreRoiobject> targetNode = Search.getSampler().get();
				if (squareDist > maxdistance)
					continue;

				targetNode.setVisited(true);
				
				synchronized (graph) {
					
					graph.addVertex(source);
					graph.addVertex(targetNode.getValue());
					final DefaultWeightedEdge edge = graph.addEdge(source, targetNode.getValue());
					graph.setEdgeWeight(edge, squareDist);
					
					
				}
			
		       
			}
			
			System.out.println("NN detected, moving to next time point!");
		}
		}
		}
	}
			return true;
			
		}
	

	@Override
	public void setLogger( final Logger logger) {
		this.logger = logger;
		
	}
	

	@Override
	public SimpleWeightedGraph< PreRoiobject, DefaultWeightedEdge > getResult()
	{
		return graph;
	}
	
	
	
	@Override
	public boolean checkInput() {
		final StringBuilder errrorHolder = new StringBuilder();;
		final boolean ok = checkInput();
		if (!ok) {
			errorMessage = errrorHolder.toString();
		}
		return ok;
	}
	

	public void reset() {
		
		graph = new SimpleWeightedGraph<PreRoiobject, DefaultWeightedEdge>(DefaultWeightedEdge.class);
		
		if (Allblobs!=null) {
			
		ArrayList<PreRoiobject> firstobject = 	Allblobs.entrySet().iterator().next().getValue();
			
		
		
		final Iterator<PreRoiobject> it = firstobject.iterator();
		while (it.hasNext()) {
			graph.addVertex(it.next());
		}
		
		}
	}

	@Override
	public String getErrorMessage() {
		
		return errorMessage;
	}
}
