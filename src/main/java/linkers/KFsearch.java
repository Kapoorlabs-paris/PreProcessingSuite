package linkers;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.imglib2.RealPoint;
import utility.PreRoiobject;

import java.util.HashSet;
import java.util.Iterator;

import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;

import costMatrix.CostFunction;
import costMatrix.JaqamanLinkingCostMatrixCreator;

public class KFsearch implements BlobTracker {

	private static final double ALTERNATIVE_COST_FACTOR = 1.05d;

	private static final double PERCENTILE = 1d;

	private static final String BASE_ERROR_MSG = "[KalmanTracker] ";

	private final HashMap<String, ArrayList<PreRoiobject>> Allblobs;

	private final double maxsearchRadius;
	private final double initialsearchRadius;
	private final CostFunction<PreRoiobject, PreRoiobject> UserchosenCostFunction;
	private final int maxframe;
	private final int centralZ;
	private int currentframe;
	private final int maxframeGap;
	private HashMap<String, Integer> AccountedT;
	private SimpleWeightedGraph<PreRoiobject, DefaultWeightedEdge> graph;

	protected Logger logger = Logger.DEFAULT_LOGGER;
	protected String errorMessage;
	ArrayList<ArrayList<PreRoiobject>> Allblobscopy;

	public KFsearch(final HashMap<String, ArrayList<PreRoiobject>> Allblobs,
			final CostFunction<PreRoiobject, PreRoiobject> UserchosenCostFunction, final double maxsearchRadius,
			final double initialsearchRadius, final int currentframe, final int maxframe, final int maxframeGap,
			int centralZ, final HashMap<String, Integer> AccountedT) {

		this.Allblobs = Allblobs;
		this.UserchosenCostFunction = UserchosenCostFunction;
		this.initialsearchRadius = initialsearchRadius;
		this.maxsearchRadius = maxsearchRadius;
		this.maxframe = maxframe;
		this.currentframe = currentframe;
		this.maxframeGap = maxframeGap;
		this.AccountedT = AccountedT;
		this.centralZ = centralZ;

	}

	@Override
	public SimpleWeightedGraph<PreRoiobject, DefaultWeightedEdge> getResult() {
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

		graph = new SimpleWeightedGraph<PreRoiobject, DefaultWeightedEdge>(DefaultWeightedEdge.class);

		Iterator<Map.Entry<String, Integer>> it = AccountedT.entrySet().iterator();
		int T = 0;
		int nextT = 1;
		while (it.hasNext()) {

			T = it.next().getValue();

			while (it.hasNext()) {

				nextT = it.next().getValue();

			}
		}

		String uniqueID = Integer.toString(centralZ) + Integer.toString(T);
		String uniqueIDnext = Integer.toString(centralZ) + Integer.toString(nextT);

		Collection<PreRoiobject> Firstorphan = Allblobs.get(uniqueID);

		Collection<PreRoiobject> Secondorphan = Allblobs.get(uniqueIDnext);

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
		for (final PreRoiobject Blob : Secondorphan) {

			meanSpotRadius += Blob.area;
		}
		meanSpotRadius /= Secondorphan.size();
		final double positionMeasurementStd = meanSpotRadius / 1d;

		final Map<CVMKalmanFilter, PreRoiobject> kalmanFiltersMap = new HashMap<CVMKalmanFilter, PreRoiobject>(
				Secondorphan.size());

		// Loop from the second frame to the last frame and build
		// KalmanFilterMap
		Iterator<Map.Entry<String, Integer>> itSec = AccountedT.entrySet().iterator();
		if (itSec.hasNext())
			itSec.next();
		while (itSec.hasNext()) {

			int currentT = itSec.next().getValue();
			uniqueID = Integer.toString(centralZ) + Integer.toString(currentT);

			SimpleWeightedGraph<PreRoiobject, DefaultWeightedEdge> subgraph = new SimpleWeightedGraph<PreRoiobject, DefaultWeightedEdge>(
					DefaultWeightedEdge.class);

			ArrayList<PreRoiobject> measurements = Allblobs.get(uniqueID);

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

				final JaqamanLinkingCostMatrixCreator<ComparableRealPoint, PreRoiobject> crm = new JaqamanLinkingCostMatrixCreator<ComparableRealPoint, PreRoiobject>(
						predictions, measurements, DistanceBasedcost, maxCost, ALTERNATIVE_COST_FACTOR, PERCENTILE);

				final JaqamanLinker<ComparableRealPoint, PreRoiobject> linker = new JaqamanLinker<ComparableRealPoint, PreRoiobject>(
						crm);
				if (!linker.checkInput() || !linker.process()) {
					errorMessage = BASE_ERROR_MSG + "Error linking candidates in frame " + currentT + ": "
							+ linker.getErrorMessage();
					return false;
				}
				final Map<ComparableRealPoint, PreRoiobject> agnts = linker.getResult();
				final Map<ComparableRealPoint, Double> costs = linker.getAssignmentCosts();

				// Deal with found links.
				Secondorphan = new HashSet<PreRoiobject>(measurements);
				for (final ComparableRealPoint cm : agnts.keySet()) {
					final CVMKalmanFilter kf = predictionMap.get(cm);

					// Create links for found match.
					final PreRoiobject source = kalmanFiltersMap.get(kf);
					final PreRoiobject target = agnts.get(cm);

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

				final JaqamanLinkingCostMatrixCreator<PreRoiobject, PreRoiobject> ic = new JaqamanLinkingCostMatrixCreator<PreRoiobject, PreRoiobject>(
						Firstorphan, Secondorphan, UserchosenCostFunction, maxInitialCost, ALTERNATIVE_COST_FACTOR,
						PERCENTILE);
				final JaqamanLinker<PreRoiobject, PreRoiobject> newLinker = new JaqamanLinker<PreRoiobject, PreRoiobject>(
						ic);
				if (!newLinker.checkInput() || !newLinker.process()) {
					errorMessage = BASE_ERROR_MSG + "Error linking Blobs from frame " + (currentT - 1) + " to frame "
							+ currentT + ": " + newLinker.getErrorMessage();
					return false;
				}
				final Map<PreRoiobject, PreRoiobject> newAssignments = newLinker.getResult();
				final Map<PreRoiobject, Double> assignmentCosts = newLinker.getAssignmentCosts();

				// Build links and new KFs from these links.
				for (final PreRoiobject source : newAssignments.keySet()) {
					final PreRoiobject target = newAssignments.get(source);

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

		graph = new SimpleWeightedGraph<PreRoiobject, DefaultWeightedEdge>(DefaultWeightedEdge.class);
		final Iterator<PreRoiobject> it = Allblobs.get(0).iterator();
		while (it.hasNext()) {
			graph.addVertex(it.next());
		}
	}

	private static final double[] MeasureBlob(final PreRoiobject target) {
		final double[] location = new double[] { target.geometriccenter[0], target.geometriccenter[1],
				target.geometriccenter[2] };
		return location;
	}

	private static final double[] estimateInitialState(final PreRoiobject first, final PreRoiobject second) {
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
	private static final CostFunction<ComparableRealPoint, PreRoiobject> DistanceBasedcost = new CostFunction<ComparableRealPoint, PreRoiobject>() {

		@Override
		public double linkingCost(final ComparableRealPoint state, final PreRoiobject Blob) {
			final double dx = state.getDoublePosition(0) - Blob.geometriccenter[0];
			final double dy = state.getDoublePosition(1) - Blob.geometriccenter[1];
			final double dz = state.getDoublePosition(2) - Blob.geometriccenter[2];
			return dx * dx + dy * dy + dz * dz + Double.MIN_NORMAL;
			// So that it's never 0
		}
	};

}