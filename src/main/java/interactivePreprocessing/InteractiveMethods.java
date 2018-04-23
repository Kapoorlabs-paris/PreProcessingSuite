package interactivePreprocessing;

import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.CardLayout;
import java.awt.Checkbox;
import java.awt.CheckboxGroup;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Label;
import java.awt.Scrollbar;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.io.File;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import snakeSegmentation.*;
import threeDViewer.ThreeDRoiobjectDisplayer;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;

import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;

import costMatrix.CostFunction;
import distanceTransform.CreateWatershed;
import distanceTransform.DistWatershed;
import dogGUI.CovistoDogPanel;
import dogSeg.DOGSeg;
import ij.IJ;
import ij.ImageJ;
import ij.ImagePlus;
import ij.ImageStack;
import ij.gui.Overlay;
import ij.gui.Roi;
import ij.plugin.frame.RoiManager;
import ij.process.ColorProcessor;
import ij3d.Image3DUniverse;
import kalmanGUI.CovistoKalmanPanel;
import linkers.Model3D;
import linkers.PRENNsearch;
import mpicbg.imglib.util.Util;
import mserGUI.CovistoMserPanel;
import mserMethods.MSERSeg;
import nearestNeighbourGUI.CovistoNearestNPanel;
import net.imglib2.Cursor;
import net.imglib2.FinalInterval;
import net.imglib2.Point;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.algorithm.componenttree.mser.MserTree;
import net.imglib2.algorithm.dog.DogDetection;
import net.imglib2.algorithm.localextrema.RefinedPeak;
import net.imglib2.algorithm.stats.Normalize;
import net.imglib2.img.array.ArrayImgFactory;
import net.imglib2.img.display.imagej.ImageJFunctions;
import net.imglib2.type.logic.BitType;
import net.imglib2.type.numeric.integer.IntType;
import net.imglib2.type.numeric.integer.UnsignedByteType;
import net.imglib2.type.numeric.real.FloatType;
import net.imglib2.util.Pair;
import net.imglib2.view.Views;
import preProcessing.GetLocalmaxminMT;
import preProcessing.GlobalThresholding;
import preProcessing.Kernels;
import snakeGUI.CovistoSnakePanel;
import utility.PreRoiobject;
import utility.ThreeDRoiobject;
import visualization.CovistoModelView;
import visualization.Draw3DLines;
import visualization.DummyTrackColorGenerator;
import visualization.SelectionModel;
import watershedGUI.CovistoWatershedPanel;

public class InteractiveMethods {

	public NumberFormat nf;
	public RandomAccessibleInterval<FloatType> originalimg;
	public int ndims;
	public int fourthDimension;
	public int thirdDimension;
	public int thirdDimensionSize;
	public int fourthDimensionSize;
	public int thirdDimensionslider = 1;
	public int thirdDimensionsliderInit = 1;
	public int fourthDimensionslider = 1;
	public int fourthDimensionsliderInit = 1;
	public int Maxlabel;
	public int thresholdsliderInit = 125;

	public ImagePlus imp;
	public boolean showMSER = false;
	public boolean showDOG = false;
	public boolean showWatershed = false;
	public HashMap<String, Integer> Accountedframes;
	public HashMap<String, Integer> AccountedZ;
	public boolean displayWatershedimg = true;
	public boolean displayBinaryimg = false;
	public boolean displayDistTransimg = false;
	public RandomAccessibleInterval<UnsignedByteType> newimg;
	public RandomAccessibleInterval<FloatType> CurrentView;
	public RoiManager roimanager;
	public final int scrollbarSize = 1000;
	public JProgressBar jpb;
	public File inputfile;

	public boolean autothreshwater = false;

	public int Unstability_ScoreInit = 1;
	public float Unstability_Score = Unstability_ScoreInit;

	public int minSizeInit = 50;
	public int maxSizeInit = 500;

	public int deltaInit = 10;
	public float delta = deltaInit;

	public float deltaMax = 255f;

	public boolean snakeongoing = false;
	public int Progressmin = 0;
	public int Progressmax = 100;
	public int max = Progressmax;

	public FloatType minval = new FloatType(0);
	public FloatType maxval = new FloatType(1);

	public MserTree<UnsignedByteType> newtree;

	public float thresholdMin = 0;
	public float thresholdMax = 255f;
	public int thresholdInit = 0;

	public Roi nearestRoiCurr;
	public int rowchoice;
	public static int standardSensitivity = 4;
	public int sensitivity = standardSensitivity;
	public Color colorChange = Color.RED;
	public float minDiversityMin = 0;
	public float minDiversityMax = 1;
	public int minDiversityInit = 1;
	public float minDiversity = minDiversityInit;
	public MouseMotionListener ml;
	public MouseListener mvl;
	public int timeMin = 1;
	public long minSize = 1;
	public long maxSize = maxSizeInit;
	public long minSizemin = 0;
	public long minSizemax = 1000;
	public long maxSizemin = 1;
	public long maxSizemax = 10000;
	public float deltaMin = 0;

	public boolean onlySeg = true;

	public boolean TrackandSeg = false;
	public float Unstability_ScoreMin = 0f;
	public float Unstability_ScoreMax = 1f;
	public int tablesize;
	public float sigma2 = 1.1f;
	public float threshold = 1f;

	public boolean darktobright = false;
	public boolean brighttodark = true;
	public ArrayList<Roi> Rois;
	public ArrayList<PreRoiobject> CurrentPreRoiobject;
	public ArrayList<Roi> NearestNeighbourRois;
	public ArrayList<Roi> BiggerRois;
	public JTable table;
	public int row;
	public int sigmaInit = 30;
	public float sigma = sigmaInit;
	public CostFunction<ThreeDRoiobject, ThreeDRoiobject> UserchosenCostFunction;
	public Color colorDrawMser = Color.green;
	public Color colorDrawDog = Color.red;
	public Color colorConfirm = Color.blue;
	public Color colorSnake = Color.YELLOW;
	public Color colorTrack = Color.GREEN;
	public Overlay overlay;
	public FinalInterval interval;
	public boolean lookForMaxima = true;
	public boolean lookForMinima = false;
	public float sigmaMin = 1f;
	public float sigmaMax = 100f;
	public RandomAccessibleInterval<BitType> bitimg;
	public RandomAccessibleInterval<FloatType> bitimgFloat;
	public RandomAccessibleInterval<IntType> intimg;
	public HashMap<Integer, ArrayList<ThreeDRoiobject>> Timetracks;
	public ArrayList<PreRoiobject> ZTPreRoiobject;
	public HashMap<String, ArrayList<PreRoiobject>> ZTRois;
	public HashMap<Integer, ArrayList<ThreeDRoiobject>> threeDTRois;

	public ArrayList<RefinedPeak<Point>> peaks;

	public String uniqueID, ZID, TID;

	public Image3DUniverse universe;
	public boolean apply3D = false;
	public Model3D model = new Model3D();
	public SelectionModel selmode = new SelectionModel(model);

	public ImageStack prestack;
	public boolean SegMode;

	public ColorProcessor cp = null;
	public double Mul_factor = 0.99;
	// maximum displacement
	public double force = 10;
	// regulari1ation factors, min and max
	public double reg = 5;
	public double regmin, regmax;

	public boolean AutoSnake = true;
	public boolean advancedSnake = false;
	public SnakeConfigDriver configDriver;

	public static enum ValueChange {

		ALL, MSER, DOG, SNAKE, WATER, DIST, DISTWATER, GAUSS, THRESHOLD, SIGMA, FOURTHDIMmouse, THIRDDIMmouse, THIRDDIM, MINDIVERSITY, DELTA, MINSIZE, MAXSIZE, MAXVAR, DARKTOBRIGHT, PREROI, NearestN, Kalman, ALPHA, BETA, ThreeDTrackDisplay, ThreeDTrackDisplayALL;

	}

	public void setTime(final int value) {

		fourthDimensionslider = value;
		fourthDimensionsliderInit = value;
		fourthDimension = value;
	}

	public int getTimeMax() {

		return thirdDimensionSize;
	}

	public void setZ(final int value) {
		thirdDimensionslider = value;
		thirdDimensionsliderInit = value;
		thirdDimension = value;
	}

	public void setInitialminDiversity(final float value) {
		minDiversity = value;
		minDiversityInit = computeScrollbarPositionFromValue(minDiversity, minDiversityMin, minDiversityMax,
				scrollbarSize);
	}

	public double getInitialminDiversity(final float value) {

		return minDiversity;

	}

	public void setInitialminSize(final int value) {
		minSize = value;
		minSizeInit = computeScrollbarPositionFromValue(minSize, minSizemin, minSizemax, scrollbarSize);
	}

	public double getInitialminSize(final int value) {

		return minSize;

	}

	public void setInitialmaxSize(final int value) {
		maxSize = value;
		maxSizeInit = computeScrollbarPositionFromValue(maxSize, maxSizemin, maxSizemax, scrollbarSize);
	}

	public double getInitialmaxSize(final int value) {

		return maxSize;

	}

	public double getInitialSigma() {
		return sigma;
	}

	public void setInitialSigma(final float value) {
		sigma = value;
		sigmaInit = computeScrollbarPositionFromValue(sigma, sigmaMin, sigmaMax, scrollbarSize);
	}

	public double getInitialThreshold() {
		return threshold;
	}

	public void setInitialThreshold(final float value) {
		threshold = value;
		thresholdInit = computeScrollbarPositionFromValue(threshold, thresholdMin, thresholdMax, scrollbarSize);
	}

	public void setInitialDelta(final float value) {
		delta = value;
		deltaInit = computeScrollbarPositionFromValue(delta, deltaMin, deltaMax, scrollbarSize);
	}

	public double getInitialDelta(final float value) {

		return delta;

	}

	public InteractiveMethods() {

		nf = NumberFormat.getInstance(Locale.ENGLISH);
		nf.setMaximumFractionDigits(3);

	}

	public InteractiveMethods(final RandomAccessibleInterval<FloatType> originalimg, final boolean SegMode,
			final boolean TrackandSeg) {

		this.originalimg = originalimg;
		this.SegMode = SegMode;
		this.TrackandSeg = TrackandSeg;
		nf = NumberFormat.getInstance(Locale.ENGLISH);
		nf.setMaximumFractionDigits(3);
		this.ndims = originalimg.numDimensions();
	}

	public InteractiveMethods(final RandomAccessibleInterval<FloatType> originalimg, File file, final boolean SegMode,
			final boolean TrackandSeg) {

		this.originalimg = originalimg;
		this.inputfile = file;
		this.SegMode = SegMode;
		this.TrackandSeg = TrackandSeg;
		nf = NumberFormat.getInstance(Locale.ENGLISH);
		nf.setMaximumFractionDigits(3);
		this.ndims = originalimg.numDimensions();
	}

	public void setInitialUnstability_Score(final float value) {
		Unstability_Score = value;
		Unstability_ScoreInit = computeScrollbarPositionFromValue(Unstability_Score, Unstability_ScoreMin,
				Unstability_ScoreMax, scrollbarSize);
	}

	public void run(String arg0) {
		FloatType minval = new FloatType(0);
		FloatType maxval = new FloatType(255);
		Normalize.normalize(Views.iterable(originalimg), minval, maxval);
		prestack = new ImageStack((int) originalimg.dimension(0), (int) originalimg.dimension(1),
				java.awt.image.ColorModel.getRGBdefault());
		Accountedframes = new HashMap<String, Integer>();
		AccountedZ = new HashMap<String, Integer>();
		universe = new Image3DUniverse((int) originalimg.dimension(0), (int) originalimg.dimension(1));
		jpb = new JProgressBar();
		overlay = new Overlay();
		interval = new FinalInterval(originalimg.dimension(0), originalimg.dimension(1));
		peaks = new ArrayList<RefinedPeak<Point>>();
		ZTRois = new HashMap<String, ArrayList<PreRoiobject>>();
		threeDTRois = new HashMap<Integer, ArrayList<ThreeDRoiobject>>();
		CurrentPreRoiobject = new ArrayList<PreRoiobject>();
		configDriver = new SnakeConfigDriver();
		ZTPreRoiobject = new ArrayList<PreRoiobject>();
		Timetracks = new HashMap<Integer, ArrayList<ThreeDRoiobject>>();
		setInitialUnstability_Score(Unstability_ScoreInit);
		setInitialDelta(deltaInit);

		setInitialminDiversity(minDiversityInit);
		setInitialmaxSize(maxSizeInit);
		setInitialminSize(minSizeInit);

		regmin = reg / 2.0;
		regmax = reg;
		if (ndims < 3) {

			thirdDimensionSize = 0;
			fourthDimensionSize = 0;
		}

		if (ndims == 3) {

			fourthDimension = 0;
			fourthDimensionsliderInit = 0;
			thirdDimension = 1;
			fourthDimensionSize = 0;

			thirdDimensionSize = (int) originalimg.dimension(2);

		}

		if (ndims == 4) {

			fourthDimension = 1;
			thirdDimension = 1;

			thirdDimensionSize = (int) originalimg.dimension(2);
			fourthDimensionSize = (int) originalimg.dimension(3);

			prestack = new ImageStack((int) originalimg.dimension(0), (int) originalimg.dimension(1),
					java.awt.image.ColorModel.getRGBdefault());
		}

		setTime(fourthDimension);
		setZ(thirdDimension);
		CurrentView = utility.CovistoSlicer.getCurrentView(originalimg, fourthDimension, thirdDimensionSize,
				thirdDimension, fourthDimensionSize);

		imp = ImageJFunctions.show(CurrentView);
		System.out.println(originalimg.dimension(0) + " " + originalimg.dimension(1) + " " + originalimg.dimension(2)
				+ " " + originalimg.dimension(3) + " " + ndims);
		imp.setTitle("Active image" + " " + "time point : " + fourthDimension + " " + " Z: " + thirdDimension);
		roimanager = RoiManager.getInstance();

		if (roimanager == null) {
			roimanager = new RoiManager();
		}

		updatePreview(ValueChange.ALL);

		Cardframe.repaint();
		Cardframe.validate();
		panelFirst.repaint();
		panelFirst.validate();

		Card();

	}

	public void updatePreview(final ValueChange change) {

		overlay = imp.getOverlay();
		int localthirddim = thirdDimension, localfourthdim = fourthDimension;
		uniqueID = Integer.toString(thirdDimension) + Integer.toString(fourthDimension);
		ZID = Integer.toString(thirdDimension);
		TID = Integer.toString(fourthDimension);
		if (overlay == null) {

			overlay = new Overlay();
			imp.setOverlay(overlay);
		}

		roimanager = RoiManager.getInstance();

		if (roimanager == null) {
			roimanager = new RoiManager();
		}

		if (change == ValueChange.ThreeDTrackDisplay) {

			Integer ID = (Integer) table.getValueAt(rowchoice, 0);

			ThreeDRoiobjectDisplayer displaymodel = new ThreeDRoiobjectDisplayer(model, selmode, universe);

			displaymodel.setDisplaySettings(CovistoModelView.KEY_TRACK_COLORING, new DummyTrackColorGenerator(), ID);

			for (int trackID : model.getTrackModel().trackIDs(true)) {

				if (ID == trackID)
					model.setTrackVisibility(trackID, true);
				else
					model.setTrackVisibility(trackID, false);

			}
			displaymodel.render(ID);
			displaymodel.refresh();

		}

		if (change == ValueChange.SNAKE) {

			overlay.clear();
			Accountedframes.put(TID, fourthDimension);

			AccountedZ.put(ZID, thirdDimension);

			for (Map.Entry<String, ArrayList<PreRoiobject>> entry : ZTRois.entrySet()) {

				ArrayList<PreRoiobject> current = entry.getValue();
				for (PreRoiobject currentroi : current) {

					if (currentroi.fourthDimension == fourthDimension && currentroi.thirdDimension == thirdDimension) {

						currentroi.rois.setStrokeColor(colorSnake);
						overlay.add(currentroi.rois);
						localthirddim = currentroi.thirdDimension;
						localfourthdim = currentroi.fourthDimension;
					}

				}
			}
			imp.setOverlay(overlay);
			imp.updateAndDraw();
			zText.setText("Current Z = " + localthirddim);
			zgenText.setText("Current Z / T = " + localthirddim);
			zslider.setValue(utility.CovistoSlicer.computeScrollbarPositionFromValue(localthirddim,
					thirdDimensionsliderInit, thirdDimensionSize, scrollbarSize));
			zslider.repaint();
			zslider.validate();

			timeText.setText("Current T = " + localfourthdim);
			timeslider.setValue(utility.CovistoSlicer.computeScrollbarPositionFromValue(localfourthdim,
					fourthDimensionsliderInit, fourthDimensionSize, scrollbarSize));
			timeslider.repaint();
			timeslider.validate();

		}

		if (change == ValueChange.PREROI) {

			ZTPreRoiobject.clear();
			for (Roi currentroi : Rois) {

				final double[] geocenter = currentroi.getContourCentroid();
				final Pair<Double, Integer> Intensityandpixels = PreRoiobject.getIntensity(currentroi, CurrentView);
				final double intensity = Intensityandpixels.getA();
				final double numberofpixels = Intensityandpixels.getB();
				final double averageintensity = intensity / numberofpixels;
				PreRoiobject currentobject = new PreRoiobject(currentroi,
						new double[] { geocenter[0], geocenter[1], thirdDimension }, numberofpixels, intensity,
						averageintensity, thirdDimension, fourthDimension);
				ZTPreRoiobject.add(currentobject);
			}
			Accountedframes.put(TID, fourthDimension);

			AccountedZ.put(ZID, thirdDimension);
			ZTRois.put(uniqueID, ZTPreRoiobject);

			if (overlay != null)
				overlay.clear();

			for (Map.Entry<String, ArrayList<PreRoiobject>> entry : ZTRois.entrySet()) {

				ArrayList<PreRoiobject> current = entry.getValue();
				for (PreRoiobject currentroi : current) {

					if (currentroi.fourthDimension == fourthDimension && currentroi.thirdDimension == thirdDimension) {

						currentroi.rois.setStrokeColor(colorConfirm);
						overlay.add(currentroi.rois);
					}

				}
			}
			imp.setOverlay(overlay);
			imp.updateAndDraw();

		}

		if (change == ValueChange.THIRDDIM) {

			if (imp == null) {
				imp = ImageJFunctions.show(CurrentView);

			}

			else {

				final float[] pixels = (float[]) imp.getProcessor().getPixels();
				final Cursor<FloatType> c = Views.iterable(CurrentView).cursor();

				for (int i = 0; i < pixels.length; ++i)
					pixels[i] = c.next().get();

				imp.updateAndDraw();

			}

			imp.setTitle("Active image" + " " + "time point : " + fourthDimension + " " + " Z: " + thirdDimension);

			newimg = utility.CovistoSlicer.PREcopytoByteImage(CurrentView);

		}

		if (change == ValueChange.FOURTHDIMmouse || change == ValueChange.THIRDDIMmouse) {

			if (imp == null) {
				imp = ImageJFunctions.show(CurrentView);

			}

			else {

				final float[] pixels = (float[]) imp.getProcessor().getPixels();
				final Cursor<FloatType> c = Views.iterable(CurrentView).cursor();

				for (int i = 0; i < pixels.length; ++i)
					pixels[i] = c.next().get();

				imp.updateAndDraw();

			}

			imp.setTitle("Active image" + " " + "time point : " + fourthDimension + " " + " Z: " + thirdDimension);

			newimg = utility.CovistoSlicer.PREcopytoByteImage(CurrentView);

			if (showMSER) {

				MSERSeg computeMSER = new MSERSeg(this, jpb);
				computeMSER.execute();

			}

			if (showDOG) {

				DOGSeg computeDOG = new DOGSeg(this, jpb);
				computeDOG.execute();
			}

			zText.setText("Current Z = " + localthirddim);
			zgenText.setText("Current Z / T = " + localthirddim);
			zslider.setValue(utility.CovistoSlicer.computeScrollbarPositionFromValue(localthirddim,
					thirdDimensionsliderInit, thirdDimensionSize, scrollbarSize));
			zslider.repaint();
			zslider.validate();

			timeText.setText("Current T = " + localfourthdim);
			timeslider.setValue(utility.CovistoSlicer.computeScrollbarPositionFromValue(localfourthdim,
					fourthDimensionsliderInit, fourthDimensionSize, scrollbarSize));
			timeslider.repaint();
			timeslider.validate();
		}

		if (change == ValueChange.MSER) {
			if (imp == null) {
				imp = ImageJFunctions.show(CurrentView);

			}

			else {

				final float[] pixels = (float[]) imp.getProcessor().getPixels();
				final Cursor<FloatType> c = Views.iterable(CurrentView).cursor();

				for (int i = 0; i < pixels.length; ++i)
					pixels[i] = c.next().get();

				imp.updateAndDraw();

			}

			imp.setTitle("Active image" + " " + "time point : " + fourthDimension + " " + " Z: " + thirdDimension);

			newimg = utility.CovistoSlicer.PREcopytoByteImage(CurrentView);

			MSERSeg computeMSER = new MSERSeg(this, jpb);
			computeMSER.execute();

		}

		if (change == ValueChange.WATER) {

			if (overlay != null)
				overlay.clear();

			newimg = utility.CovistoSlicer.PREcopytoByteImage(CurrentView);
			bitimg = new ArrayImgFactory<BitType>().create(newimg, new BitType());
			bitimgFloat = new ArrayImgFactory<FloatType>().create(newimg, new FloatType());
			GetLocalmaxminMT.ThresholdingMTBit(CurrentView, bitimg, CovistoWatershedPanel.thresholdWater);
			if (displayBinaryimg && !apply3D)
				ImageJFunctions.show(bitimg);
			DistWatershed<FloatType> WaterafterDisttransform = new DistWatershed<FloatType>(this, CurrentView, bitimg,
					jpb, false);
			WaterafterDisttransform.execute();
			if (displayWatershedimg && !apply3D)
				ImageJFunctions.show(WaterafterDisttransform.getResult());

			if (displayDistTransimg && !apply3D)
				ImageJFunctions.show(WaterafterDisttransform.getDistanceTransformedimg());

		}

		if (change == ValueChange.DOG) {

			if (imp == null) {
				imp = ImageJFunctions.show(CurrentView);

			}

			else {

				final float[] pixels = (float[]) imp.getProcessor().getPixels();
				final Cursor<FloatType> c = Views.iterable(CurrentView).cursor();

				for (int i = 0; i < pixels.length; ++i)
					pixels[i] = c.next().get();

				imp.updateAndDraw();

			}

			imp.setTitle("Active image" + " " + "time point : " + fourthDimension + " " + " Z: " + thirdDimension);

			newimg = utility.CovistoSlicer.PREcopytoByteImage(CurrentView);

			DOGSeg computeDOG = new DOGSeg(this, jpb);
			computeDOG.execute();
		}

	}

	public JFrame Cardframe = new JFrame("Computer Vision Segmentation Tools (CoViSto)");
	public JPanel panelCont = new JPanel();
	public JPanel panelFirst = new JPanel();
	public JPanel panelSecond = new JPanel();
	public JPanel panelThird = new JPanel();
	public JPanel Timeselect = new JPanel();
	public JPanel Zselect = new JPanel();
	public JPanel MserPanel = new JPanel();
	public JPanel DogPanel = new JPanel();
	public JPanel WaterPanel = new JPanel();
	public JPanel SnakePanel = new JPanel();
	public JPanel RoiPanel = new JPanel();
	public JPanel DetectionPanel = new JPanel();

	public JPanel NearestNPanel = new JPanel();
	public JPanel KalmanPanel = new JPanel();

	final String timestring = "Current T";
	final String zstring = "Current Z";
	final String zgenstring = "Current Z / T";

	public Label timeText = new Label("Current T = " + 1, Label.CENTER);
	public Label zText = new Label("Current Z = " + 1, Label.CENTER);
	public Label zgenText = new Label("Current Z / T = " + 1, Label.CENTER);

	final String maxSearchstring = "Maximum search radius";

	public JButton Roibutton = new JButton("Confirm current roi selection");

	public CheckboxGroup detection = new CheckboxGroup();
	final Checkbox Watershed = new Checkbox("Do watershedding", detection, showWatershed);
	final Checkbox DOG = new Checkbox("Do DoG detection", detection, showDOG);
	final Checkbox MSER = new Checkbox("Do MSER detection", detection, showMSER);

	public final Insets insets = new Insets(10, 0, 0, 0);
	public final GridBagLayout layout = new GridBagLayout();
	public final GridBagConstraints c = new GridBagConstraints();

	public JScrollBar timeslider = new JScrollBar(Scrollbar.HORIZONTAL, fourthDimensionsliderInit, 10, 0,
			scrollbarSize + 10);
	public JScrollBar zslider = new JScrollBar(Scrollbar.HORIZONTAL, thirdDimensionsliderInit, 10, 0,
			10 + scrollbarSize);

	public TextField inputField = new TextField();
	public TextField inputFieldT, inputtrackField;
	public TextField inputFieldZ;
	public int SizeXbig = 400;
	public int SizeXsmall = 200;
	public int SizeX = 400;
	public int SizeY = 200;
	public int SizeYsmall = 200;
	public int SizeYbig = 500;
	public JScrollPane scrollPane;
	public JPanel PanelSelectFile = new JPanel();
	public Border selectfile = new CompoundBorder(new TitledBorder("Select Track"), new EmptyBorder(c.insets));
	public JPanel controlnextthird = new JPanel();
	public JPanel controlprevthird = new JPanel();

	public void Card() {

		CardLayout cl = new CardLayout();

		c.insets = new Insets(5, 5, 5, 5);
		panelCont.setLayout(cl);

		panelCont.add(panelFirst, "1");

		panelCont.add(panelSecond, "2");

		panelCont.add(panelThird, "3");

		panelFirst.setLayout(layout);
		panelSecond.setLayout(layout);
		panelThird.setLayout(layout);
		Timeselect.setLayout(layout);

		Zselect.setLayout(layout);

		DetectionPanel.setLayout(layout);
		SnakePanel.setLayout(layout);
		WaterPanel.setLayout(layout);
		RoiPanel.setLayout(layout);

		//
		inputFieldZ = new TextField();
		inputFieldZ = new TextField(5);
		inputFieldZ.setText(Integer.toString(thirdDimension));

		inputField.setColumns(10);

		inputFieldT = new TextField();
		inputFieldT = new TextField(5);
		inputFieldT.setText(Integer.toString(fourthDimension));

		Border timeborder = new CompoundBorder(new TitledBorder("Select time"), new EmptyBorder(c.insets));
		Border zborder = new CompoundBorder(new TitledBorder("Select Z"), new EmptyBorder(c.insets));
		Border methodborder = new CompoundBorder(new TitledBorder("Choose a segmentation algorithm"),
				new EmptyBorder(c.insets));

		c.anchor = GridBagConstraints.BOTH;
		c.ipadx = 35;

		c.gridwidth = 10;
		c.gridheight = 10;
		c.gridy = 1;
		c.gridx = 0;

		// Put time slider

		Timeselect.add(timeText, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.WEST,
				GridBagConstraints.HORIZONTAL, insets, 0, 0));

		Timeselect.add(timeslider, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.WEST,
				GridBagConstraints.HORIZONTAL, insets, 0, 0));

		Timeselect.add(inputFieldT, new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0, GridBagConstraints.WEST,
				GridBagConstraints.HORIZONTAL, insets, 0, 0));

		Timeselect.setBorder(timeborder);
		panelFirst.add(Timeselect, new GridBagConstraints(0, 0, 3, 1, 0.0, 0.0, GridBagConstraints.EAST,
				GridBagConstraints.HORIZONTAL, insets, 0, 0));

		// Put z slider
		if (ndims > 3)
			Zselect.add(zText, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.WEST,
					GridBagConstraints.HORIZONTAL, insets, 0, 0));
		else
			Zselect.add(zgenText, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.WEST,
					GridBagConstraints.HORIZONTAL, insets, 0, 0));
		Zselect.add(zslider, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.WEST,
				GridBagConstraints.HORIZONTAL, insets, 0, 0));

		Zselect.add(inputFieldZ, new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0, GridBagConstraints.WEST,
				GridBagConstraints.HORIZONTAL, insets, 0, 0));

		Zselect.setBorder(zborder);
		panelFirst.add(Zselect, new GridBagConstraints(3, 0, 3, 1, 0.0, 0.0, GridBagConstraints.WEST,
				GridBagConstraints.HORIZONTAL, insets, 0, 0));

		if (ndims < 4) {

			timeslider.setEnabled(false);
			inputFieldT.setEnabled(false);
		}

		DetectionPanel.add(Watershed, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.WEST,
				GridBagConstraints.HORIZONTAL, insets, 0, 0));
		DetectionPanel.add(DOG, new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0, GridBagConstraints.WEST,
				GridBagConstraints.HORIZONTAL, insets, 0, 0));
		DetectionPanel.add(MSER, new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0, GridBagConstraints.WEST,
				GridBagConstraints.HORIZONTAL, insets, 0, 0));

		DetectionPanel.setBorder(methodborder);
		panelFirst.add(DetectionPanel, new GridBagConstraints(0, 1, 3, 1, 0.0, 0.0, GridBagConstraints.WEST,
				GridBagConstraints.HORIZONTAL, insets, 0, 0));

		WaterPanel = CovistoWatershedPanel.WaterPanel();
		panelFirst.add(WaterPanel, new GridBagConstraints(3, 1, 3, 1, 0.0, 0.0, GridBagConstraints.WEST,
				GridBagConstraints.RELATIVE, insets, 0, 0));

		DogPanel = CovistoDogPanel.DogPanel();
		panelFirst.add(DogPanel, new GridBagConstraints(0, 3, 3, 1, 0.0, 0.0, GridBagConstraints.WEST,
				GridBagConstraints.HORIZONTAL, insets, 0, 0));

		MserPanel = CovistoMserPanel.MserPanel();

		panelFirst.add(MserPanel, new GridBagConstraints(3, 3, 3, 1, 0.0, 0.0, GridBagConstraints.WEST,
				GridBagConstraints.RELATIVE, new Insets(10, 10, 0, 10), 0, 0));

		final JButton Zsnakes = new JButton("Apply snakes in Z");
		final JButton Tsnakes = new JButton("Apply snakes in T");
		final JButton Allsnakes = new JButton("Apply snakes in Z & T");

		final JButton SinglethreeD = new JButton("Track in Z for currrent T");

		JPanel controlprev = new JPanel();
		JPanel controlnext = new JPanel();

		controlprev.setLayout(layout);
		controlnext.setLayout(layout);
		controlnextthird.setLayout(layout);
		controlprevthird.setLayout(layout);
		controlprev.add(new JButton(new AbstractAction("\u22b2Prev") {

			@Override
			public void actionPerformed(ActionEvent e) {
				CardLayout cl = (CardLayout) panelCont.getLayout();
				cl.previous(panelCont);
			}
		}));

		controlnext.add(new JButton(new AbstractAction("Next\u22b3") {

			@Override
			public void actionPerformed(ActionEvent e) {
				CardLayout cl = (CardLayout) panelCont.getLayout();
				cl.next(panelCont);
			}
		}));

		controlnextthird.add(new JButton(new AbstractAction("Next\u22b3") {

			@Override
			public void actionPerformed(ActionEvent e) {
				CardLayout cl = (CardLayout) panelCont.getLayout();
				cl.next(panelCont);
			}
		}));
		controlprevthird.add(new JButton(new AbstractAction("\u22b2Prev") {

			@Override
			public void actionPerformed(ActionEvent e) {
				CardLayout cl = (CardLayout) panelCont.getLayout();
				cl.previous(panelCont);
			}
		}));

		SnakePanel = CovistoSnakePanel.SnakePanel(originalimg.numDimensions());
		panelSecond.add(SnakePanel, new GridBagConstraints(0, 0, 3, 1, 0.0, 0.0, GridBagConstraints.EAST,
				GridBagConstraints.HORIZONTAL, new Insets(10, 10, 0, 10), 0, 0));

		if (!SegMode) {

			NearestNPanel = CovistoNearestNPanel.NearestNPanel();

			KalmanPanel = CovistoKalmanPanel.KalmanPanel();

			panelThird.add(KalmanPanel, new GridBagConstraints(0, 0, 3, 1, 0.0, 0.0, GridBagConstraints.EAST,
					GridBagConstraints.HORIZONTAL, new Insets(10, 10, 0, 10), 0, 0));
			CovistoNearestNPanel.AllthreeD.addActionListener(new PREAllZTrackListener(this));
			CovistoKalmanPanel.Timetrack.addActionListener(new PRE3DTListener(this));
			CovistoKalmanPanel.lostframe.addTextListener(new PRELostFrameListener(this));
			CovistoKalmanPanel.alphaS.addAdjustmentListener(new PREAlphaListener(this, CovistoKalmanPanel.alphaText,
					CovistoKalmanPanel.alphastring, CovistoKalmanPanel.alphaMin, CovistoKalmanPanel.alphaMax,
					CovistoKalmanPanel.scrollbarSize, CovistoKalmanPanel.alphaS));
			CovistoKalmanPanel.betaS.addAdjustmentListener(new PREBetaListener(this, CovistoKalmanPanel.betaText,
					CovistoKalmanPanel.betastring, CovistoKalmanPanel.betaMin, CovistoKalmanPanel.betaMax,
					CovistoKalmanPanel.scrollbarSize, CovistoKalmanPanel.betaS));
			CovistoNearestNPanel.maxSearchNearest.addAdjustmentListener(new PREMaxSearchListener(this,
					CovistoNearestNPanel.maxSearchTextNearest, CovistoNearestNPanel.maxSearchstringNearest,
					CovistoNearestNPanel.maxSearchradiusMinNearest, CovistoNearestNPanel.maxSearchradiusMaxNearest,
					CovistoNearestNPanel.scrollbarSize, CovistoNearestNPanel.maxSearchNearest));
			CovistoKalmanPanel.maxSearchKalman.addAdjustmentListener(new PREMaxSearchTListener(this,
					CovistoKalmanPanel.maxSearchTextKalman, CovistoKalmanPanel.maxSearchstringKalman,
					CovistoKalmanPanel.maxSearchradiusMin, CovistoKalmanPanel.maxSearchradiusMax,
					CovistoKalmanPanel.scrollbarSize, CovistoKalmanPanel.maxSearchSS));
			CovistoKalmanPanel.initialSearchS.addAdjustmentListener(new PREIniSearchListener(this,
					CovistoKalmanPanel.iniSearchText, CovistoKalmanPanel.initialSearchstring,
					CovistoKalmanPanel.initialSearchradiusMin, CovistoKalmanPanel.initialSearchradiusMax,
					CovistoKalmanPanel.scrollbarSize, CovistoKalmanPanel.initialSearchS));
		}

		Watershed.addItemListener(new DoWatershedListener(this));
		DOG.addItemListener(new DoDOGListener(this));
		MSER.addItemListener(new DoMSERListener(this));
		CovistoWatershedPanel.autothreshold.addItemListener(new PREauto(this));

		CovistoWatershedPanel.Water3D.addActionListener(new PREApplyWater3DListener(this));

		CovistoDogPanel.findminima.addItemListener(new FindMinimaListener(this));
		CovistoDogPanel.findmaxima.addItemListener(new FindMaximaListener(this));
		CovistoMserPanel.findminimaMser.addItemListener(new FindMinimaMserListener(this));
		CovistoMserPanel.findmaximaMser.addItemListener(new FindMaximaMserListener(this));

		CovistoWatershedPanel.displayBinary.addItemListener(new PREShowBinary(this));
		CovistoWatershedPanel.displayWater.addItemListener(new PREShowWatershed(this));
		CovistoWatershedPanel.displayDist.addItemListener(new PREShowDist(this));
		SinglethreeD.addActionListener(new PRESingleZTrackListener(this));

		CovistoSnakePanel.Singlesnake.addActionListener(new PRESinglesnakeListener(this));
		Zsnakes.addActionListener(new PREZSnakeListener(this));
		Tsnakes.addActionListener(new PRETSnakeListener(this));
		Allsnakes.addActionListener(new PREZTSnakeListener(this));
		CovistoDogPanel.AllDog.addActionListener(new PREApplyDog3DListener(this));
		CovistoMserPanel.AllMser.addActionListener(new PREZMserListener(this));
		CovistoSnakePanel.AllSnake.addActionListener(new PREApplySnake3DListener(this));
		CovistoSnakePanel.advanced.addItemListener(new AdvancedSnakeListener(this));
		CovistoSnakePanel.Snakeiter.addTextListener(new IterationListener(this));
		CovistoSnakePanel.gradientthresh.addTextListener(new GradientListener(this));
		CovistoSnakePanel.maxdist.addTextListener(new MaxdistListener(this));

		CovistoWatershedPanel.thresholdWaterslider.addAdjustmentListener(
				new PreWaterListener(this, CovistoWatershedPanel.watertext, CovistoWatershedPanel.waterstring,
						CovistoWatershedPanel.thresholdMinWater, CovistoWatershedPanel.thresholdMaxWater, scrollbarSize,
						CovistoWatershedPanel.thresholdWaterslider));
		CovistoDogPanel.sigmaslider.addAdjustmentListener(new PreSigmaListener(this, CovistoDogPanel.sigmaText,
				CovistoDogPanel.sigmastring, CovistoDogPanel.sigmaMin, CovistoDogPanel.sigmaMax,
				CovistoDogPanel.scrollbarSize, CovistoDogPanel.sigmaslider));

		CovistoMserPanel.deltaS.addAdjustmentListener(new PREDeltaListener(this, CovistoMserPanel.deltaText,
				CovistoMserPanel.deltastring, CovistoMserPanel.deltaMin, CovistoMserPanel.deltaMax,
				CovistoMserPanel.scrollbarSize, CovistoMserPanel.deltaS));

		CovistoMserPanel.Unstability_ScoreS.addAdjustmentListener(new PREUnstability_ScoreListener(this,
				CovistoMserPanel.Unstability_ScoreText, CovistoMserPanel.Unstability_Scorestring,
				CovistoMserPanel.Unstability_ScoreMin, CovistoMserPanel.Unstability_ScoreMax,
				CovistoMserPanel.scrollbarSize, CovistoMserPanel.Unstability_ScoreS));

		CovistoMserPanel.minDiversityS.addAdjustmentListener(new PREMinDiversityListener(this,
				CovistoMserPanel.minDivText, CovistoMserPanel.minDivstring, CovistoMserPanel.minDiversityMin,
				CovistoMserPanel.minDiversityMax, CovistoMserPanel.scrollbarSize, CovistoMserPanel.minDiversityS));

		CovistoMserPanel.minSizeS.addAdjustmentListener(new PREMinSizeListener(this, CovistoMserPanel.minSizeText,
				CovistoMserPanel.minSizestring, CovistoMserPanel.minSizemin, CovistoMserPanel.minSizemax,
				CovistoMserPanel.scrollbarSize, CovistoMserPanel.minSizeS));

		CovistoMserPanel.maxSizeS.addAdjustmentListener(new PREMaxSizeListener(this, CovistoMserPanel.maxSizeText,
				CovistoMserPanel.maxSizestring, CovistoMserPanel.maxSizemin, CovistoMserPanel.maxSizemax,
				CovistoMserPanel.scrollbarSize, CovistoMserPanel.maxSizeS));

		CovistoDogPanel.thresholdslider.addAdjustmentListener(new PreThresholdListener(this,
				CovistoDogPanel.thresholdText, CovistoDogPanel.thresholdstring, CovistoDogPanel.thresholdMin,
				CovistoDogPanel.thresholdMax, CovistoDogPanel.scrollbarSize, CovistoDogPanel.thresholdslider));

		timeslider.addAdjustmentListener(new PreTimeListener(this, timeText, timestring, fourthDimensionsliderInit,
				fourthDimensionSize, scrollbarSize, timeslider));

		if (ndims > 3)
			zslider.addAdjustmentListener(new PreZListener(this, zText, zstring, thirdDimensionsliderInit,
					thirdDimensionSize, scrollbarSize, zslider));
		else
			zslider.addAdjustmentListener(new PreZListener(this, zgenText, zgenstring, thirdDimensionsliderInit,
					thirdDimensionSize, scrollbarSize, zslider));

		inputFieldZ.addTextListener(new PreZlocListener(this, false));
		inputFieldT.addTextListener(new PreTlocListener(this, false));

		panelSecond.add(controlprev, new GridBagConstraints(0, 4, 3, 1, 0.0, 0.0, GridBagConstraints.WEST,
				GridBagConstraints.RELATIVE, new Insets(10, 10, 0, 10), 0, 0));
		if (!SegMode) {
			panelSecond.add(controlnextthird, new GridBagConstraints(2, 4, 3, 1, 0.0, 0.0, GridBagConstraints.EAST,
					GridBagConstraints.RELATIVE, new Insets(10, 10, 0, 10), 0, 0));
		}
		panelSecond.setPreferredSize(SnakePanel.getPreferredSize());
		controlnextthird.setEnabled(false);
		panelFirst.add(controlnext, new GridBagConstraints(3, 4, 3, 1, 0.0, 0.0, GridBagConstraints.EAST,
				GridBagConstraints.RELATIVE, new Insets(10, 10, 0, 10), 0, 0));
		panelThird.add(PanelSelectFile, new GridBagConstraints(0, 4, 3, 1, 0.0, 0.0, GridBagConstraints.EAST,
				GridBagConstraints.HORIZONTAL, insets, 0, 0));
		panelThird.add(controlprevthird, new GridBagConstraints(0, 6, 3, 1, 0.0, 0.0, GridBagConstraints.ABOVE_BASELINE,
				GridBagConstraints.HORIZONTAL, new Insets(10, 10, 0, 10), 0, 0));

		Cardframe.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		cl.show(panelCont, "1");

		Cardframe.add(panelCont, "Center");
		Cardframe.add(jpb, "Last");
		panelFirst.setVisible(true);
		Cardframe.pack();
		Cardframe.setVisible(true);
	}

	public float computeValueFromScrollbarPosition(final int scrollbarPosition, final float min, final float max,
			final int scrollbarSize) {
		return min + (scrollbarPosition / (float) scrollbarSize) * (max - min);
	}

	protected static float computeIntValueFromScrollbarPosition(final int scrollbarPosition, final float min,
			final float max, final int scrollbarSize) {
		return min + (scrollbarPosition / (max)) * (max - min);
	}

	public int computeScrollbarPositionFromValue(final float sigma, final float min, final float max,
			final int scrollbarSize) {
		return Util.round(((sigma - min) / (max - min)) * scrollbarSize);
	}

	public int computeIntScrollbarPositionFromValue(final float thirdDimensionslider, final float min, final float max,
			final int scrollbarSize) {
		return Util.round(((thirdDimensionslider - min) / (max - min)) * max);
	}

	public static void main(String[] args) {

		new ImageJ();
		JFrame frame = new JFrame("");
		PreprocessingFileChooser panel = new PreprocessingFileChooser();

		frame.getContentPane().add(panel, "Center");
		frame.setSize(panel.getPreferredSize());
	}

}
