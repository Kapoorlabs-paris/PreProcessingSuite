package threeDViewer;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.jgrapht.graph.DefaultWeightedEdge;
import org.scijava.java3d.BadTransformException;
import org.scijava.vecmath.Color3f;
import org.scijava.vecmath.Color4f;
import org.scijava.vecmath.Point4d;

import ij3d.Content;
import ij3d.ContentInstant;
import ij3d.Image3DUniverse;
import linkers.Model3D;
import linkers.ThreeDRoiobjectGroupNode;
import utility.ModelChangeEvent;
import utility.ThreeDRoiobject;
import utility.ThreeDRoiobjectCollection;
import visualization.AbstractCovistoModelView;
import visualization.CovistoModelView;
import visualization.FeatureColorGenerator;
import visualization.SelectionChangeEvent;
import visualization.SelectionModel;
import visualization.TrackColorGenerator;
import visualization.TrackDisplayNode;

public class ThreeDRoiobjectDisplayer extends AbstractCovistoModelView {

	static final String KEY = "3DVIEWER";

	public static final int DEFAULT_RESAMPLING_FACTOR = 4;

	// public static final int DEFAULT_THRESHOLD = 50;

	private static final boolean DEBUG = false;

	private static final String TRACK_CONTENT_NAME = "Tracks";

	private static final String ThreeDRoiobject_CONTENT_NAME = "ThreeDRoiobjectS";

	private TreeMap<Integer, ThreeDRoiobjectGroupNode<ThreeDRoiobject>> blobs;

	private TrackDisplayNode trackNode;

	private Content ThreeDRoiobjectContent;

	private Content trackContent;

	private final Image3DUniverse universe;

	// For highlighting
	private ArrayList<ThreeDRoiobject> previousThreeDRoiobjectHighlight;

	private HashMap<ThreeDRoiobject, Color3f> previousColorHighlight;

	private HashMap<ThreeDRoiobject, Integer> previousFrameHighlight;

	private TreeMap<Integer, ContentInstant> contentAllFrames;

	public ThreeDRoiobjectDisplayer(final Model3D model, final SelectionModel selectionModel,
			final Image3DUniverse universe) {
		super(model, selectionModel);
		this.universe = universe;
		setModel(model);
	}

	/*
	 * OVERRIDDEN METHODS
	 */

	@Override
	public void modelChanged(final ModelChangeEvent event) {
		if (DEBUG) {
			System.out
					.println("[ThreeDRoiobjectDisplayer3D: modelChanged() called with event ID: " + event.getEventID());
			System.out.println(event);
		}

		switch (event.getEventID()) {

		case ModelChangeEvent.ThreeDRoiobjectS_COMPUTED:
			makeThreeDRoiobjectContent();
			break;

		case ModelChangeEvent.ThreeDRoiobjectS_FILTERED:
			for (final int frame : blobs.keySet()) {
				final ThreeDRoiobjectGroupNode<ThreeDRoiobject> frameBlobs = blobs.get(frame);
				for (final Iterator<ThreeDRoiobject> it = model.getThreeDRoiobjects().iterator(frame, false); it
						.hasNext();) {
					final ThreeDRoiobject ThreeDRoiobject = it.next();
					final boolean visible = ThreeDRoiobject.getFeature(ThreeDRoiobjectCollection.VISIBLITY)
							.compareTo(ThreeDRoiobjectCollection.ZERO) > 0;
					frameBlobs.setVisible(ThreeDRoiobject, visible);
				}
			}
			break;

		case ModelChangeEvent.TRACKS_COMPUTED:
			trackContent = makeTrackContent();
			universe.removeContent(TRACK_CONTENT_NAME);
			universe.addContent(trackContent);
			break;

		case ModelChangeEvent.TRACKS_VISIBILITY_CHANGED:
			updateTrackColors();
			trackNode.setTrackVisible(model.getTrackModel().trackIDs(true));
			break;

		case ModelChangeEvent.MODEL_MODIFIED: {
			/*
			 * We do not do anything. I could not find a good way to dynamically change the
			 * content of a 3D viewer content. So the 3D viewer just shows a snapshot of the
			 * TrackMate model when it was launched, and is not kept in sync with
			 * modifications afterwards.
			 */
			break;
		}

		default: {
			System.err.println("[ThreeDRoiobjectDisplayer3D] Unknown event ID: " + event.getEventID());
		}
		}
	}

	@Override
	public void selectionChanged(final SelectionChangeEvent event, final int trackID) {
		// Highlight edges.
		trackNode.setSelection(selectionModel.getEdgeSelection());
		trackNode.refresh();
		// Highlight ThreeDRoiobjectS.
		displayThreeDRoiobjectSelection((Integer) displaySettings
				.get(KEY_TRACK_DISPLAY_MODE) == CovistoModelView.TRACK_DISPLAY_MODE_SELECTION_ONLY, trackID);
		// Center on last ThreeDRoiobject
		super.selectionChanged(event);
	}

	@Override
	public void centerViewOn(final ThreeDRoiobject ThreeDRoiobject) {
		final int frame = ThreeDRoiobject.getFeature(ThreeDRoiobject.Time).intValue();
		universe.showTimepoint(frame);
	}

	@Override
	public void refresh() {
		if (null != trackNode)
			trackNode.refresh();
	}

	@Override
	public void render(final int trackID) {
		if (DEBUG)
			System.out.println("[ThreeDRoiobjectDisplayer3D] Call to render().");

		updateRadiuses();
		updateThreeDRoiobjectColors();

		ThreeDRoiobjectContent.setVisible((Boolean) displaySettings.get(KEY_ThreeDRoiobjectS_VISIBLE));
		if (null != trackContent) {
			trackContent.setVisible((Boolean) displaySettings.get(KEY_TRACKS_VISIBLE));
			trackNode.setTrackDisplayMode((Integer) displaySettings.get(KEY_TRACK_DISPLAY_MODE));
			trackNode.setTrackDisplayDepth((Integer) displaySettings.get(KEY_TRACK_DISPLAY_DEPTH));
			updateTrackColors();
			trackNode.refresh();
			universe.updateStartAndEndTime(blobs.firstKey(), blobs.lastKey());
			universe.updateTimelineGUI();
		}
	}

	@Override
	public void setDisplaySettings(final String key, final Object value, final int trackID) {
		super.setDisplaySettings(key, value, trackID);
		// Treat change of radius
		if (key == KEY_ThreeDRoiobject_RADIUS_RATIO) {
			updateRadiuses();
		} else if (key == KEY_ThreeDRoiobject_COLORING) {
			updateThreeDRoiobjectColors();
		} else if (key == KEY_TRACK_COLORING) {
			updateTrackColors();
		} else if (key == KEY_DISPLAY_ThreeDRoiobject_NAMES) {
			for (final int frame : blobs.keySet()) {
				blobs.get(frame).setShowLabels((Boolean) value);
			}
		} else if (key == KEY_ThreeDRoiobjectS_VISIBLE) {
			ThreeDRoiobjectContent.setVisible((Boolean) value);
		} else if (key == KEY_TRACKS_VISIBLE && null != trackContent) {
			trackContent.setVisible((Boolean) value);
		} else if (key == KEY_TRACK_DISPLAY_MODE && null != trackNode) {
			trackNode.setTrackDisplayMode((Integer) value);
			displayThreeDRoiobjectSelection((Integer) value == CovistoModelView.TRACK_DISPLAY_MODE_SELECTION_ONLY,
					trackID);
		} else if (key == KEY_TRACK_DISPLAY_DEPTH && null != trackNode) {
			trackNode.setTrackDisplayDepth((Integer) value);
		}
	}

	@Override
	public void clear() {
		universe.removeContent(ThreeDRoiobject_CONTENT_NAME);
		universe.removeContent(TRACK_CONTENT_NAME);
	}

	/*
	 * PRIVATE METHODS
	 */

	private void setModel(final Model3D model) {
		if (model.getThreeDRoiobjects() != null) {
			makeThreeDRoiobjectContent();
		}
		if (model.getTrackModel().nTracks(true) > 0) {
			trackContent = makeTrackContent();
			universe.removeContent(TRACK_CONTENT_NAME);
			universe.addContentLater(trackContent);
		}
	}

	private Content makeTrackContent() {
		// Prepare tracks instant
		trackNode = new TrackDisplayNode(model);

		universe.addTimelapseListener(trackNode);

		// Pass tracks instant to all instants
		final TreeMap<Integer, ContentInstant> instants = new TreeMap<>();
		final ContentInstant trackCI = new ContentInstant("Tracks_all_frames");
		trackCI.display(trackNode);
		instants.put(0, trackCI);
		final Content tc = new Content(TRACK_CONTENT_NAME, instants);
		tc.setShowAllTimepoints(true);
		tc.showCoordinateSystem(false);
		return tc;
	}

	private void makeThreeDRoiobjectContent() {

		blobs = new TreeMap<>();
		contentAllFrames = new TreeMap<>();
		final double radiusRatio = (Double) displaySettings.get(KEY_ThreeDRoiobject_RADIUS_RATIO);

		final ThreeDRoiobjectCollection ThreeDRoiobjectS = model.getThreeDRoiobjects();
		
		
		@SuppressWarnings("unchecked")
		final FeatureColorGenerator<ThreeDRoiobject> ThreeDRoiobjectColorGenerator = (FeatureColorGenerator<ThreeDRoiobject>) displaySettings
				.get(KEY_ThreeDRoiobject_COLORING);
		for (final int frame : ThreeDRoiobjectS.keySet()) {
			if (ThreeDRoiobjectS.getNThreeDRoiobjects(frame, false) == 0) {
				continue; // Do not create content for empty frames
			}

			buildFrameContent(ThreeDRoiobjectS, frame, radiusRatio, ThreeDRoiobjectColorGenerator);

		}

		ThreeDRoiobjectContent = new Content(ThreeDRoiobject_CONTENT_NAME, contentAllFrames);
		ThreeDRoiobjectContent.showCoordinateSystem(false);
		universe.removeContent(ThreeDRoiobject_CONTENT_NAME);
		universe.addContentLater(ThreeDRoiobjectContent);
	}

	private void buildFrameContent(final ThreeDRoiobjectCollection ThreeDRoiobjectS, final Integer frame,
			final double radiusRatio, final FeatureColorGenerator<ThreeDRoiobject> ThreeDRoiobjectColorGenerator) {
		final Map<ThreeDRoiobject, Point4d> centers = new HashMap<>(
				ThreeDRoiobjectS.getNThreeDRoiobjects(frame, false));
		final Map<ThreeDRoiobject, Color4f> colors = new HashMap<>(ThreeDRoiobjectS.getNThreeDRoiobjects(frame, false));
		final double[] coords = new double[3];

		for (final Iterator<ThreeDRoiobject> it = ThreeDRoiobjectS.iterator(frame, false); it.hasNext();) {
			
			final ThreeDRoiobject ThreeDRoiobject = it.next();
			utility.ViewUtils.localize(ThreeDRoiobject, coords);
			final Double radius = ThreeDRoiobject.getFeature(ThreeDRoiobject.Size);
			final double[] pos = new double[] { coords[0], coords[1], coords[2], radius * radiusRatio };
			centers.put(ThreeDRoiobject, new Point4d(pos));
			final Color4f col = new Color4f(ThreeDRoiobjectColorGenerator.color(ThreeDRoiobject));
			col.w = 0f;
			colors.put(ThreeDRoiobject, col);
		}
		final ThreeDRoiobjectGroupNode<ThreeDRoiobject> blobGroup = new ThreeDRoiobjectGroupNode<>(centers, colors);
		final ContentInstant contentThisFrame = new ContentInstant("ThreeDRoiobjectS_frame_" + frame);

		try {
			contentThisFrame.display(blobGroup);
		} catch (final BadTransformException bte) {
			System.err.println("Bad content for frame " + frame + ". Generated an exception:\n"
					+ bte.getLocalizedMessage() + "\nContent was:\n" + blobGroup.toString());
		}

		// Set visibility:
		if (ThreeDRoiobjectS.getNThreeDRoiobjects(frame, true) > 0) {
			blobGroup.setVisible(ThreeDRoiobjectS.iterable(frame, true));
		}

		contentAllFrames.put(frame, contentThisFrame);
		blobs.put(frame, blobGroup);
	}

	private void updateRadiuses() {
		final double radiusRatio = (Double) displaySettings.get(KEY_ThreeDRoiobject_RADIUS_RATIO);

		for (final int frame : blobs.keySet()) {
			final ThreeDRoiobjectGroupNode<ThreeDRoiobject> ThreeDRoiobjectGroup = blobs.get(frame);
			for (final Iterator<ThreeDRoiobject> iterator = model.getThreeDRoiobjects().iterator(frame, false); iterator
					.hasNext();) {
				final ThreeDRoiobject ThreeDRoiobject = iterator.next();

				System.out.println(ThreeDRoiobject.volume + " " + ThreeDRoiobject.Size + " " + "changing radius");
				ThreeDRoiobjectGroup.setRadius(ThreeDRoiobject, 10);
				// radiusRatio * ThreeDRoiobject.getFeature( ThreeDRoiobject.Size ) );
			}
		}
	}

	private void updateThreeDRoiobjectColors() {
		@SuppressWarnings("unchecked")
		final FeatureColorGenerator<ThreeDRoiobject> ThreeDRoiobjectColorGenerator = (FeatureColorGenerator<ThreeDRoiobject>) displaySettings
				.get(KEY_ThreeDRoiobject_COLORING);

		for (final int frame : blobs.keySet()) {
			final ThreeDRoiobjectGroupNode<ThreeDRoiobject> ThreeDRoiobjectGroup = blobs.get(frame);

			

				for (final Iterator<ThreeDRoiobject> iterator = model.getThreeDRoiobjects().iterator(frame,
						false); iterator.hasNext();) {
					final ThreeDRoiobject ThreeDRoiobject = iterator.next();
					
						ThreeDRoiobjectGroup.setColor(ThreeDRoiobject,
								new Color3f(ThreeDRoiobjectColorGenerator.color(ThreeDRoiobject)));
					
				

			}
		}

	}

	private void updateTrackColors() {
		final TrackColorGenerator colorGenerator = (TrackColorGenerator) displaySettings.get(KEY_TRACK_COLORING);

		for (final Integer trackID : model.getTrackModel().trackIDs(true)) {
			colorGenerator.setCurrentTrackID(trackID);
			for (final DefaultWeightedEdge edge : model.getTrackModel().trackEdges(trackID)) {
				final Color color = colorGenerator.color(edge);
				trackNode.setColor(edge, color);
			}
		}
	}

	private void highlightThreeDRoiobjectS(final Collection<ThreeDRoiobject> ThreeDRoiobjectS) {
		// Restore previous display settings for previously highlighted ThreeDRoiobject
		if (null != previousThreeDRoiobjectHighlight)
			for (final ThreeDRoiobject ThreeDRoiobject : previousThreeDRoiobjectHighlight) {
				final Integer frame = previousFrameHighlight.get(ThreeDRoiobject);
				if (null != frame) {
					final ThreeDRoiobjectGroupNode<ThreeDRoiobject> ThreeDRoiobjectGroupNode = blobs.get(frame);
					if (null != ThreeDRoiobjectGroupNode) {
						ThreeDRoiobjectGroupNode.setColor(ThreeDRoiobject, previousColorHighlight.get(ThreeDRoiobject));
					}
				}
			}

		/*
		 * Don't color ThreeDRoiobject selection in the highlight color if we are
		 * displaying selection only.
		 */
		final Integer trackDisplayMode = (Integer) displaySettings.get(KEY_TRACK_DISPLAY_MODE);
		if (trackDisplayMode == CovistoModelView.TRACK_DISPLAY_MODE_SELECTION_ONLY)
			return;

		/*
		 * Store previous color value and color the ThreeDRoiobject selection with the
		 * highlight color.
		 */

		previousThreeDRoiobjectHighlight = new ArrayList<>(ThreeDRoiobjectS.size());
		previousColorHighlight = new HashMap<>(ThreeDRoiobjectS.size());
		previousFrameHighlight = new HashMap<>(ThreeDRoiobjectS.size());

		final Color3f highlightColor = new Color3f((Color) displaySettings.get(KEY_HIGHLIGHT_COLOR));
		for (final ThreeDRoiobject ThreeDRoiobject : ThreeDRoiobjectS) {
			final int frame = ThreeDRoiobject.getFeature(ThreeDRoiobject.Time).intValue();
			// Store current settings
			previousThreeDRoiobjectHighlight.add(ThreeDRoiobject);
			final ThreeDRoiobjectGroupNode<ThreeDRoiobject> ThreeDRoiobjectGroupNode = blobs.get(frame);
			if (null != ThreeDRoiobjectGroupNode) {
				previousColorHighlight.put(ThreeDRoiobject, ThreeDRoiobjectGroupNode.getColor3f(ThreeDRoiobject));
				previousFrameHighlight.put(ThreeDRoiobject, frame);
				// Update target ThreeDRoiobject display
				blobs.get(frame).setColor(ThreeDRoiobject, highlightColor);
			}
		}
	}

	/**
	 * Changes the visibility of the displayed ThreeDRoiobject.
	 *
	 * @param onlyThreeDRoiobjectSelection
	 *            If <code>true</code>, we display on the ThreeDRoiobjectS in the
	 *            selection. Otherwise we display all ThreeDRoiobjectS marked as
	 *            visible.
	 */
	private void displayThreeDRoiobjectSelection(final boolean onlyThreeDRoiobjectSelection, final int trackID) {
		final Set<ThreeDRoiobject> ThreeDRoiobjectSelection = selectionModel.getThreeDRoiobjectSelection();

		if (onlyThreeDRoiobjectSelection) {
			if (ThreeDRoiobjectSelection.isEmpty()) {
				for (final Integer frame : blobs.keySet()) {
					blobs.get(frame).setVisible(false);
				}
				return;
			}

			// Sort ThreeDRoiobjectS in selection per frame.
			final HashMap<Integer, ArrayList<ThreeDRoiobject>> ThreeDRoiobjectSPerFrame = new HashMap<>(blobs.size());
			for (final Integer frame : blobs.keySet()) {
				final ArrayList<ThreeDRoiobject> ThreeDRoiobjectS = new ArrayList<ThreeDRoiobject>();
				ThreeDRoiobjectSPerFrame.put(frame, ThreeDRoiobjectS);
			}

			for (final ThreeDRoiobject ThreeDRoiobject : ThreeDRoiobjectSelection) {
				final int frame = ThreeDRoiobject.getFeature(ThreeDRoiobject.Time).intValue();
				final ArrayList<ThreeDRoiobject> ThreeDRoiobjectS = ThreeDRoiobjectSPerFrame
						.get(Integer.valueOf(frame));
				ThreeDRoiobjectS.add(ThreeDRoiobject);
			}

			// Mark then as visible, the others as invisible.
			for (final Integer frame : ThreeDRoiobjectSPerFrame.keySet()) {
				blobs.get(frame).setVisible(ThreeDRoiobjectSPerFrame.get(frame));
			}

			// Restore proper color.
			updateThreeDRoiobjectColors();
			updateTrackColors();
		} else {
			// Make all visible ThreeDRoiobjectS visible here.
			for (final int frame : blobs.keySet()) {
				final Iterable<ThreeDRoiobject> ThreeDRoiobjectS = model.getThreeDRoiobjects().iterable(frame, true);
				blobs.get(frame).setVisible(ThreeDRoiobjectS);
			}
			highlightThreeDRoiobjectS(ThreeDRoiobjectSelection);
		}
	}

	@Override
	public String getKey() {
		return KEY;
	}

}
