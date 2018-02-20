package linkers;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.JProgressBar;

import net.imglib2.RealPoint;
import utility.PreRoiobject;
import utility.ThreeDRoiobject;

import java.util.HashSet;
import java.util.Iterator;

import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;

import costMatrix.CostFunction;
import costMatrix.JaqamanLinkingCostMatrixCreator;

public class KFsearch implements ThreeDBlobTracker {

	private static final double ALTERNATIVE_COST_FACTOR = 1.05d;

	private static final double PERCENTILE = 1d;

	private static final String BASE_ERROR_MSG = "[KalmanTracker] ";

	private final HashMap<Integer, ArrayList<ThreeDRoiobject>> Allblobs;
    public final JProgressBar jpb;
	private final double maxsearchRadius;
	private final double initialsearchRadius;
	private final CostFunction<ThreeDRoiobject, ThreeDRoiobject> UserchosenCostFunction;
	private final int maxframeGap;
	private HashMap<String, Integer> AccountedT;
	private SimpleWeightedGraph<ThreeDRoiobject, DefaultWeightedEdge> graph;

	protected Logger logger = Logger.DEFAULT_LOGGER;
	protected String errorMessage;
	ArrayList<ArrayList<ThreeDRoiobject>> Allblobscopy;

	public KFsearch(final HashMap<Integer, ArrayList<ThreeDRoiobject>> Allblobs,
			final CostFunction<ThreeDRoiobject, ThreeDRoiobject> UserchosenCostFunction, final double maxsearchRadius,
			final double initialsearchRadius, final int maxframeGap,
			 final HashMap<String, Integer> AccountedT, final JProgressBar jpb) {

		this.Allblobs = Allblobs;
		this.jpb = jpb;
		this.UserchosenCostFunction = UserchosenCostFunction;
		this.initialsearchRadius = initialsearchRadius;
		this.maxsearchRadius = maxsearchRadius;
		this.maxframeGap = maxframeGap;
		this.AccountedT = AccountedT;

	}

	@Override
	public SimpleWeightedGraph<ThreeDRoiobject, DefaultWeightedEdge> getResult() {
		return graph;
	}

	@Override
	public boolean checkInput() {
		final StringBuilder errrorHolder = new StringBuilder();
		;
		final boolean ok = checkInput();
		if (!ok) {
			errorMessage = errrorHolder.toString();
		}
		return ok;
	}

	@Override
	public boolean process() {

		/*
		 * Outputs
		 */

		System.out.println(AccountedT.size());
		graph = new SimpleWeightedGraph<ThreeDRoiobject, DefaultWeightedEdge>(DefaultWeightedEdge.class);

		Iterator<Map.Entry<String, Integer>> it = AccountedT.entrySet().iterator();
		int T = 0;
		int nextT = 1;
		if (it.hasNext()) {

			T = it.next().getValue();

			if (it.hasNext()) {

				nextT = it.next().getValue();

			}
			
		}

		int uniqueID = T;
		int uniqueIDnext = nextT;

		Collection<ThreeDRoiobject> Firstorphan = Allblobs.get(uniqueID);

		Collection<ThreeDRoiobject> Secondorphan = Allblobs.get(uniqueIDnext);

		// Max KF search cost.
		final double maxCost = maxsearchRadius * maxsearchRadius;

		// Max cost to nucleate KFs.
		final double maxInitialCost = initialsearchRadius * initialsearchRadius;

		/*
		 * Estimate Kalman filter variances.
		 *
		 * The search radius is used to derive an estimate of the noise that affects
		 * position and velocity. The two are linked: if we need a large search radius,
		 * then the fluoctuations over predicted states are large.
		 */
		final double positionProcessStd = maxsearchRadius / 2d;
		final double velocityProcessStd = maxsearchRadius / 2d;

		double meanSpotRadius = 0d;
		for (final ThreeDRoiobject Blob : Secondorphan) {

			meanSpotRadius += Blob.volume;
		}
		meanSpotRadius /= Secondorphan.size();
		final double positionMeasurementStd = meanSpotRadius / 1d;

		final Map<CVMKalmanFilter, ThreeDRoiobject> kalmanFiltersMap = new HashMap<CVMKalmanFilter, ThreeDRoiobject>(
				Secondorphan.size());

		// Loop from the second frame to the last frame and build
		// KalmanFilterMap
		Iterator<Map.Entry<String, Integer>> itSec = AccountedT.entrySet().iterator();
		int percent = 0;
		if (itSec.hasNext())
			itSec.next();
		while (itSec.hasNext()) {
			percent++;
			utility.ProgressBar.SetProgressBar(jpb, 100 * percent / AccountedT.size(),
					"Kalman Filter Search for " + " T = " + T);
			int currentT = itSec.next().getValue();
			uniqueID = currentT;

			SimpleWeightedGraph<ThreeDRoiobject, DefaultWeightedEdge> subgraph = new SimpleWeightedGraph<ThreeDRoiobject, DefaultWeightedEdge>(
					DefaultWeightedEdge.class);

			ArrayList<ThreeDRoiobject> measurements = Allblobs.get(uniqueID);

			// Make the preditiction map
			final Map<ComparableRealPoint, CVMKalmanFilter> predictionMap = new HashMap<ComparableRealPoint, CVMKalmanFilter>(
					kalmanFiltersMap.size());

			for (final CVMKalmanFilter kf : kalmanFiltersMap.keySet()) {
				final double[] X = kf.predict();
				final ComparableRealPoint point = new ComparableRealPoint(X);
				predictionMap.put(point, kf);

			}
			final List<ComparableRealPoint> predictions = new ArrayList<ComparableRealPoint>(predictionMap.keySet());

			// Orphans are dealt with later
			final Collection<CVMKalmanFilter> childlessKFs = new HashSet<CVMKalmanFilter>(kalmanFiltersMap.keySet());

			/*
			 * Here we simply link based on minimizing the squared distances to get an
			 * initial starting point, more advanced Kalman filter costs will be built in
			 * the next step
			 */

			if (!predictions.isEmpty() && !measurements.isEmpty()) {
				// Only link measurements to predictions if we have predictions.

				final JaqamanLinkingCostMatrixCreator<ComparableRealPoint, ThreeDRoiobject> crm = new JaqamanLinkingCostMatrixCreator<ComparableRealPoint, ThreeDRoiobject>(
						predictions, measurements, DistanceBasedcost, maxCost, ALTERNATIVE_COST_FACTOR, PERCENTILE);

				final JaqamanLinker<ComparableRealPoint, ThreeDRoiobject> linker = new JaqamanLinker<ComparableRealPoint, ThreeDRoiobject>(
						crm);
				if (!linker.checkInput() || !linker.process()) {
					errorMessage = BASE_ERROR_MSG + "Error linking candidates in frame " + currentT + ": "
							+ linker.getErrorMessage();
					return false;
				}
				final Map<ComparableRealPoint, ThreeDRoiobject> agnts = linker.getResult();
				final Map<ComparableRealPoint, Double> costs = linker.getAssignmentCosts();

				// Deal with found links.
				Secondorphan = new HashSet<ThreeDRoiobject>(measurements);
				for (final ComparableRealPoint cm : agnts.keySet()) {
					final CVMKalmanFilter kf = predictionMap.get(cm);

					// Create links for found match.
					final ThreeDRoiobject source = kalmanFiltersMap.get(kf);
					final ThreeDRoiobject target = agnts.get(cm);

					graph.addVertex(source);
					graph.addVertex(target);
					final DefaultWeightedEdge edge = graph.addEdge(source, target);
					final double cost = costs.get(cm);
					graph.setEdgeWeight(edge, cost);

					subgraph.addVertex(source);
					subgraph.addVertex(target);
					final DefaultWeightedEdge subedge = subgraph.addEdge(source, target);
					subgraph.setEdgeWeight(subedge, cost);

					// Update Kalman filter
					kf.update(MeasureBlob(target));

					// Update Kalman track PreRoiobject
					kalmanFiltersMap.put(kf, target);

					// Remove from orphan set
					Secondorphan.remove(target);

					// Remove from childless KF set
					childlessKFs.remove(kf);
				}
			}

			// Deal with orphans from the previous frame.
			// Here is the real linking with the actual cost function

			if (!Firstorphan.isEmpty() && !Secondorphan.isEmpty()) {

				// Trying to link orphans with unlinked candidates.

				final JaqamanLinkingCostMatrixCreator<ThreeDRoiobject, ThreeDRoiobject> ic = new JaqamanLinkingCostMatrixCreator<ThreeDRoiobject, ThreeDRoiobject>(
						Firstorphan, Secondorphan, UserchosenCostFunction, maxInitialCost, ALTERNATIVE_COST_FACTOR,
						PERCENTILE);
				final JaqamanLinker<ThreeDRoiobject, ThreeDRoiobject> newLinker = new JaqamanLinker<ThreeDRoiobject, ThreeDRoiobject>(
						ic);
				if (!newLinker.checkInput() || !newLinker.process()) {
					errorMessage = BASE_ERROR_MSG + "Error linking Blobs from frame " + (currentT - 1) + " to frame "
							+ currentT + ": " + newLinker.getErrorMessage();
					return false;
				}
				final Map<ThreeDRoiobject, ThreeDRoiobject> newAssignments = newLinker.getResult();
				final Map<ThreeDRoiobject, Double> assignmentCosts = newLinker.getAssignmentCosts();

				// Build links and new KFs from these links.
				for (final ThreeDRoiobject source : newAssignments.keySet()) {
					final ThreeDRoiobject target = newAssignments.get(source);

					// Remove from orphan collection.

					// Derive initial state and create Kalman filter.
					final double[] XP = estimateInitialState(source, target);
					final CVMKalmanFilter kt = new CVMKalmanFilter(XP, Double.MIN_NORMAL, positionProcessStd,
							velocityProcessStd, positionMeasurementStd);
					// We trust the initial state a lot.

					// Store filter and source
					kalmanFiltersMap.put(kt, target);
					synchronized (graph) {
						// Add edge to the graph.
						graph.addVertex(source);
						graph.addVertex(target);
						final DefaultWeightedEdge edge = graph.addEdge(source, target);
						final double cost = assignmentCosts.get(source);
						graph.setEdgeWeight(edge, cost);

						subgraph.addVertex(source);
						subgraph.addVertex(target);
						final DefaultWeightedEdge subedge = subgraph.addEdge(source, target);
						subgraph.setEdgeWeight(subedge, cost);
					}

				}
			}

			Firstorphan = Secondorphan;
			// Deal with childless KFs.
			for (final CVMKalmanFilter kf : childlessKFs) {
				// Echo we missed a measurement
				kf.update(null);

				// We can bridge a limited number of gaps. If too much, we die.
				// If not, we will use predicted state next time.
				if (kf.getNOcclusion() > maxframeGap) {
					kalmanFiltersMap.remove(kf);
				}
			}

		}
		return true;
	}

	@Override
	public void setLogger(final Logger logger) {
		this.logger = logger;

	}

	@Override
	public String getErrorMessage() {

		return errorMessage;
	}

	private static final class ComparableRealPoint extends RealPoint implements Comparable<ComparableRealPoint> {
		public ComparableRealPoint(final double[] A) {
			// Wrap array.
			super(A, false);
		}

		/**
		 * Sort based on X, Y
		 */
		@Override
		public int compareTo(final ComparableRealPoint o) {
			int i = 0;
			while (i < n) {
				if (getDoublePosition(i) != o.getDoublePosition(i)) {
					return (int) Math.signum(getDoublePosition(i) - o.getDoublePosition(i));
				}
				i++;
			}
			return hashCode() - o.hashCode();
		}
	}

	public void reset() {

        graph = new SimpleWeightedGraph<ThreeDRoiobject, DefaultWeightedEdge>(DefaultWeightedEdge.class);
		
		if (Allblobs!=null && Allblobs.size() > 0) {
			
		ArrayList<ThreeDRoiobject> firstobject = 	Allblobs.entrySet().iterator().next().getValue();
			
		
		
		final Iterator<ThreeDRoiobject> it = firstobject.iterator();
		while (it.hasNext()) {
			graph.addVertex(it.next());
		}
		
		}
	}

	private static final double[] MeasureBlob(final ThreeDRoiobject target) {
		final double[] location = new double[] { target.geometriccenter[0], target.geometriccenter[1],
				target.geometriccenter[2] };
		return location;
	}

	private static final double[] estimateInitialState(final ThreeDRoiobject first, final ThreeDRoiobject second) {
		final double[] xp = new double[] { second.geometriccenter[0], second.geometriccenter[1],
				second.geometriccenter[2], second.diffTo(first, 0), second.diffTo(first, 1), second.diffTo(first, 2) };
		return xp;
	}

	/**
	 * 
	 * Implementations of various cost functions, starting with the simplest one,
	 * based on minimizing the distances between the links, followed by minimizing
	 * cost function based on intensity differences between the links.
	 *
	 * Cost function that returns the square distance between a KF state and a Blob.
	 */
	private static final CostFunction<ComparableRealPoint, ThreeDRoiobject> DistanceBasedcost = new CostFunction<ComparableRealPoint, ThreeDRoiobject>() {

		@Override
		public double linkingCost(final ComparableRealPoint state, final ThreeDRoiobject Blob) {
			final double dx = state.getDoublePosition(0) - Blob.geometriccenter[0];
			final double dy = state.getDoublePosition(1) - Blob.geometriccenter[1];
			final double dz = state.getDoublePosition(2) - Blob.geometriccenter[2];
			return dx * dx + dy * dy + dz * dz + Double.MIN_NORMAL;
			// So that it's never 0
		}
	};

}