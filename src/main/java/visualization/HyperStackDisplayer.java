package visualization;

import java.util.Set;

import org.jgrapht.graph.DefaultWeightedEdge;


import ij.ImagePlus;
import ij.gui.Overlay;
import ij.gui.Roi;
import linkers.Model3D;
import utility.ModelChangeEvent;
import utility.ThreeDRoiobject;
import utility.ViewUtils;

public class HyperStackDisplayer extends AbstractCovistoModelView
{

	private static final boolean DEBUG = false;

	public final ImagePlus imp;

	public ThreeDRoiobjectOverlay ThreeDRoiobjectOverlay;

	protected TrackOverlay trackOverlay;


	private Roi initialROI;

	public static final String KEY = "HYPERSTACKDISPLAYER";

	/*
	 * CONSTRUCTORS
	 */

	public HyperStackDisplayer( final Model3D model, final SelectionModel selectionModel, final ImagePlus imp )
	{
		super( model, selectionModel );
		if ( null != imp )
		{
			this.imp = imp;
		}
		else
		{
			this.imp = ViewUtils.makeEmpytImagePlus( model );
		}
		this.ThreeDRoiobjectOverlay = createThreeDRoiobjectOverlay();
		this.trackOverlay = createTrackOverlay();
	}

	public HyperStackDisplayer( final Model3D model, final SelectionModel selectionModel )
	{
		this( model, selectionModel, null );
	}

	/*
	 * PROTECTED METHODS
	 */

	/**
	 * Hook for subclassers. Instantiate here the overlay you want to use for
	 * the ThreeDRoiobjects.
	 *
	 * @return the ThreeDRoiobject overlay
	 */
	protected ThreeDRoiobjectOverlay createThreeDRoiobjectOverlay()
	{
		return new ThreeDRoiobjectOverlay( model, imp, displaySettings );
	}

	/**
	 * Hook for subclassers. Instantiate here the overlay you want to use for
	 * the ThreeDRoiobjects.
	 *
	 * @return the track overlay
	 */
	protected TrackOverlay createTrackOverlay()
	{
		final TrackOverlay to = new TrackOverlay( model, imp, displaySettings );
		final TrackColorGenerator colorGenerator = ( TrackColorGenerator ) displaySettings.get( KEY_TRACK_COLORING );
		to.setTrackColorGenerator( colorGenerator );
		return to;
	}

	/*
	 * PUBLIC METHODS
	 */

	/**
	 * Exposes the {@link ImagePlus} on which the model is drawn by this view.
	 *
	 * @return the ImagePlus used in this view.
	 */
	public ImagePlus getImp()
	{
		return imp;
	}

	@Override
	public void modelChanged( final ModelChangeEvent event )
	{
		if ( DEBUG )
			System.out.println( "[HyperStackDisplayer] Received model changed event ID: " + event.getEventID() + " from " + event.getSource() );
		boolean redoOverlay = false;

		switch ( event.getEventID() )
		{

		case ModelChangeEvent.MODEL_MODIFIED:
			// Rebuild track overlay only if edges were added or removed, or if
			// at least one ThreeDRoiobject was removed.
			final Set< DefaultWeightedEdge > edges = event.getEdges();
			if ( edges != null && edges.size() > 0 )
			{
				redoOverlay = true;
			}
			break;

		case ModelChangeEvent.ThreeDRoiobjectS_FILTERED:
			redoOverlay = true;
			break;

		case ModelChangeEvent.ThreeDRoiobjectS_COMPUTED:
			redoOverlay = true;
			break;

		case ModelChangeEvent.TRACKS_VISIBILITY_CHANGED:
		case ModelChangeEvent.TRACKS_COMPUTED:
			redoOverlay = true;
			break;
		}

		if ( redoOverlay )
			refresh();
	}

	@Override
	public void selectionChanged( final SelectionChangeEvent event )
	{
		// Highlight selection
		trackOverlay.setHighlight( selectionModel.getEdgeSelection() );
		ThreeDRoiobjectOverlay.setThreeDRoiobjectSelection( selectionModel.getThreeDRoiobjectSelection() );
		// Center on last ThreeDRoiobject
		super.selectionChanged( event );
		// Redraw
		imp.updateAndDraw();
	}

	@Override
	public void centerViewOn( final ThreeDRoiobject ThreeDRoiobject )
	{
		final int frame = ThreeDRoiobject.getFeature( ThreeDRoiobject.Time ).intValue();
		final double dz = imp.getCalibration().pixelDepth;
		final long z = Math.round( ThreeDRoiobject.getFeature( ThreeDRoiobject.ZPOSITION ) / dz ) + 1;
		imp.setPosition( imp.getC(), ( int ) z, frame + 1 );
	}

	@Override
	public void render()
	{
		initialROI = imp.getRoi();
		if ( initialROI != null )
		{
			imp.killRoi();
		}

		clear();
		imp.setOpenAsHyperStack( true );
		if ( !imp.isVisible() )
		{
			imp.show();
		}

		addOverlay( ThreeDRoiobjectOverlay );
		addOverlay( trackOverlay );
		imp.updateAndDraw();
	}

	@Override
	public void refresh()
	{
		if ( null != imp )
		{
			imp.updateAndDraw();
		}
	}

	@Override
	public void clear()
	{
		Overlay overlay = imp.getOverlay();
		if ( overlay == null )
		{
			overlay = new Overlay();
			imp.setOverlay( overlay );
		}
		overlay.clear();
		if ( initialROI != null )
		{
			imp.getOverlay().add( initialROI );
		}
		refresh();
	}

	public void addOverlay( final Roi overlay )
	{
		imp.getOverlay().add( overlay );
	}

	public SelectionModel getSelectionModel()
	{
		return selectionModel;
	}

	

	@Override
	public void setDisplaySettings( final String key, final Object value )
	{
		boolean dorefresh = false;

		if ( key == CovistoModelView.KEY_ThreeDRoiobject_COLORING || key == CovistoModelView.KEY_LIMIT_DRAWING_DEPTH || key == KEY_DRAWING_DEPTH )
		{
			dorefresh = true;

		}
		else if ( key == CovistoModelView.KEY_TRACK_COLORING )
		{
			// pass the new one to the track overlay - we ignore its ThreeDRoiobject
			// coloring and keep the ThreeDRoiobject coloring
			final TrackColorGenerator colorGenerator = ( TrackColorGenerator ) value;
			trackOverlay.setTrackColorGenerator( colorGenerator );
			dorefresh = true;
		}

		super.setDisplaySettings( key, value );
		if ( dorefresh )
		{
			refresh();
		}
	}

	@Override
	public String getKey()
	{
		return KEY;
	}
}
