package interactivePreprocessing;

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
import java.io.File;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollBar;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;

import distanceTransform.CreateWatershed;
import distanceTransform.DistWatershed;
import dogSeg.DOGSeg;
import ij.IJ;
import ij.ImageJ;
import ij.ImagePlus;
import ij.ImageStack;
import ij.gui.Overlay;
import ij.gui.Roi;
import ij.plugin.frame.RoiManager;

import mpicbg.imglib.util.Util;
import mserMethods.MSERSeg;
import net.imglib2.Cursor;
import net.imglib2.FinalInterval;
import net.imglib2.Point;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.algorithm.componenttree.mser.MserTree;
import net.imglib2.algorithm.dog.DogDetection;
import net.imglib2.algorithm.localextrema.RefinedPeak;
import net.imglib2.img.array.ArrayImgFactory;
import net.imglib2.img.display.imagej.ImageJFunctions;
import net.imglib2.type.logic.BitType;
import net.imglib2.type.numeric.integer.IntType;
import net.imglib2.type.numeric.integer.UnsignedByteType;
import net.imglib2.type.numeric.real.FloatType;
import net.imglib2.view.Views;
import preProcessing.GetLocalmaxminMT;
import preProcessing.GlobalThresholding;
import preProcessing.Kernels;
import userTESTING.PreprocessingFileChooser;

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
	public int sigmasliderInit = 0;
	public int thresholdsliderInit = 125;
	public int thresholdsliderWaterInit = 125;
	
	public ImagePlus imp;
	public boolean showMSER = false;
	public boolean showDOG = false;
	public boolean showWatershed = false;
	
	public boolean displayWatershedimg = true;
	public boolean displayBinaryimg = false;
	public boolean displayDistTransimg = false;
	public RandomAccessibleInterval<UnsignedByteType> newimg;
	public RandomAccessibleInterval<FloatType> CurrentView;
	public RoiManager roimanager;
	public final int scrollbarSize = 1000;
	public JProgressBar jpb;
	public File inputfile;
	public int alphaInit = 1;
	public int betaInit = 0;

	public boolean autothreshwater = false;
	
	public int Unstability_ScoreInit = 1;
	public float Unstability_Score = Unstability_ScoreInit ;
	
	
	
	
	public int minSizeInit = 1;
	public int maxSizeInit = 100;
	
	public int deltaInit = 10;
	public float delta = deltaInit;
	
	
	
	
	public float deltaMax = 255f;
	
	public int Progressmin = 0;
	public int Progressmax = 100;
	public int max = Progressmax;


	
	
	
	
	public FloatType minval = new FloatType(0);
	public FloatType maxval = new FloatType(1);
	
	
	public MserTree<UnsignedByteType> newtree;
	
	
	public float thresholdMin = 0f;
	public float thresholdMax = 255f;
	public int thresholdInit = 0;
	
	
	public float thresholdMinWater = 0f;
	public float thresholdMaxWater = 255f;
	public int thresholdInitWater = 0;
	
	public static int standardSensitivity = 4;
	public int sensitivity = standardSensitivity;
	
	public float minDiversityMin = 0;
	public float minDiversityMax = 1;
	public int minDiversityInit = 1;
	public float minDiversity = minDiversityInit;
	
	
	public int timeMin = 1;
	public long minSize = 1;
	public long maxSize = maxSizeInit;
	public long minSizemin = 0;
	public long minSizemax = 100;
	public long maxSizemin = 1;
	public long maxSizemax = 10000;
	public float deltaMin = 0;
	
	
	public float Unstability_ScoreMin = 0f;
	public float Unstability_ScoreMax = 1f;
	
	public float sigma2 = 0.5f;
	public float threshold = 1f;
	public float thresholdWater = 255f;
	public boolean darktobright = false;
	public boolean brighttodark = true;
	public ArrayList<Roi> Rois;
	public ArrayList<Roi> NearestNeighbourRois;
	public ArrayList<Roi> BiggerRois;
	
	
	public int sigmaInit = 30;
	public float sigma = sigmaInit;
	
	
	public Color colorDrawMser = Color.green;
	public Color colorDrawDog = Color.red;
	public Overlay overlay;
	public FinalInterval interval;
	public boolean lookForMaxima = false;
	public boolean lookForMinima = true;
	
	
	public float sigmaMin = 0.5f;
	public float sigmaMax = 100f;
	public RandomAccessibleInterval<BitType> bitimg;
	public RandomAccessibleInterval<FloatType> bitimgFloat;
	public RandomAccessibleInterval<IntType> intimg;
	
	
	public float initialSearchradius = 10;
	public float maxSearchradius = 15;
	public int missedframes = 20;
	public int initialSearchradiusInit = (int) initialSearchradius;
	public float initialSearchradiusMin = 0;
	public float initialSearchradiusMax = 100;
	public float alphaMin = 0;
	public float alphaMax = 1;
	public float betaMin = 0;
	public float betaMax = 1;
	public ArrayList<RefinedPeak<Point>> peaks;
	public int maxSearchradiusInit = (int) maxSearchradius;
	public float maxSearchradiusMin = 10;
	public float maxSearchradiusMax = 500;
	
	public float alpha = 0.5f;
	public float beta = 0.5f;

	public static enum ValueChange {
		
		ALL, MSER, DOG, SNAKE, WATER, DIST, DISTWATER, GAUSS, THRESHOLD, SIGMA, FOURTHDIMmouse, THIRDDIMmouse, MINDIVERSITY, DELTA, MINSIZE, MAXSIZE, MAXVAR, DARKTOBRIGHT;
	
	}
	
	public void setTime(final int value) {
		thirdDimensionslider = value;
		thirdDimensionsliderInit = 1;
		thirdDimension = 1;
	}

	public int getTimeMax() {

		return thirdDimensionSize;
	}

	public void setZ(final int value) {
		fourthDimensionslider = value;
		fourthDimensionsliderInit = 1;
		fourthDimension = 1;
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
	
	public double getInitialThresholdWater() {
		return thresholdInitWater;
	}

	public void setInitialThresholdWater(final float value) {
		thresholdWater = value;
		thresholdInitWater = computeScrollbarPositionFromValue(thresholdWater, thresholdMinWater, thresholdMaxWater, scrollbarSize);
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
	
	public InteractiveMethods(final RandomAccessibleInterval<FloatType> originalimg) {
		
		this.originalimg = originalimg;
		nf = NumberFormat.getInstance(Locale.ENGLISH);
		nf.setMaximumFractionDigits(3);
		this.ndims = originalimg.numDimensions();
	}
	
	
    public InteractiveMethods(final RandomAccessibleInterval<FloatType> originalimg, File file) {
		
		this.originalimg = originalimg;
		this.inputfile = file;
		nf = NumberFormat.getInstance(Locale.ENGLISH);
		nf.setMaximumFractionDigits(3);
		this.ndims = originalimg.numDimensions();
	}
	public void setInitialsearchradius(final float value) {
		initialSearchradius = value;
		initialSearchradiusInit = computeScrollbarPositionFromValue(initialSearchradius, initialSearchradiusMin,
				initialSearchradiusMax, scrollbarSize);
	}

	public void setInitialmaxsearchradius(final float value) {
		maxSearchradius = value;
		maxSearchradiusInit = computeScrollbarPositionFromValue(maxSearchradius, maxSearchradiusMin, maxSearchradiusMax,
				scrollbarSize);
	}

	public double getInitialsearchradius(final float value) {

		return initialSearchradius;

	}

	public void setInitialUnstability_Score(final float value) {
		Unstability_Score = value;
		Unstability_ScoreInit = computeScrollbarPositionFromValue(Unstability_Score, Unstability_ScoreMin, Unstability_ScoreMax, scrollbarSize);
	}

	public void setInitialAlpha(final float value) {
		alpha = value;
		alphaInit = computeScrollbarPositionFromValue(alpha, alphaMin, alphaMax, scrollbarSize);
	}

	public double getInitialAlpha(final float value) {

		return alpha;

	}
	
	public void setInitialBeta(final float value) {
		beta = value;
		betaInit = computeScrollbarPositionFromValue(beta, betaMin, betaMax, scrollbarSize);
	}

	public double getInitialBeta(final float value) {

		return beta;

	}
	public void run(String arg0) {
		
		System.out.println(minSizeInit + " " + maxSizeInit + " " + Unstability_ScoreInit + " " + minDiversityInit); 
		jpb = new JProgressBar();
		overlay = new Overlay();
		interval = new FinalInterval(originalimg.dimension(0), originalimg.dimension(1));
		peaks = new ArrayList<RefinedPeak<Point>>();
		jpb = new JProgressBar();
		
		setInitialUnstability_Score(Unstability_ScoreInit);
		setInitialDelta(deltaInit);
		setInitialAlpha(alphaInit);
		setInitialBeta(betaInit);
		setInitialminDiversity(minDiversityInit);
		setInitialmaxSize(maxSizeInit);
		setInitialminSize(minSizeInit);
		setInitialsearchradius(initialSearchradiusInit);
		setInitialmaxsearchradius(maxSearchradius);
		
		if (ndims < 3) {

			thirdDimensionSize = 0;
			fourthDimensionSize = 0;
		}

		if (ndims == 3) {

			fourthDimension = 1;
			thirdDimension = 1;
			fourthDimensionSize = 0;

			thirdDimensionSize = (int) originalimg.dimension(2);

		}

		if (ndims == 4) {

			fourthDimension = 1;
			thirdDimension = 1;

			thirdDimensionSize = (int) originalimg.dimension(2);
			fourthDimensionSize = (int) originalimg.dimension(3);

		
		}

		setTime(fourthDimension);
		setZ(thirdDimension);
		CurrentView = utility.Slicer.getCurrentView(originalimg, fourthDimension, thirdDimensionSize, thirdDimension,
				fourthDimensionSize);

		imp = ImageJFunctions.show(CurrentView);
		System.out.println(originalimg.dimension(0) + " " + originalimg.dimension(1) + " " + originalimg.dimension(2) +" " + originalimg.dimension(3) + " " + ndims) ;
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
		
		if (overlay == null) {

			overlay = new Overlay();
			imp.setOverlay(overlay);
		}
		
		
		roimanager = RoiManager.getInstance();
		
		if (roimanager == null) {
			roimanager = new RoiManager();
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

			if (overlay!=null)
				overlay.clear();
			
			newimg = utility.Slicer.copytoByteImage(CurrentView);
			
			if (showMSER) {

				MSERSeg computeMSER = new MSERSeg(this,jpb);
				computeMSER.execute();
					
			
			}

			if (showDOG) {

			
				
			

				DOGSeg computeDOG = new DOGSeg(this, jpb);
				computeDOG.execute();
			}

			
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

			if (overlay!=null)
				overlay.clear();
			newimg = utility.Slicer.copytoByteImage(CurrentView);
		     
			
			MSERSeg computeMSER = new MSERSeg(this,jpb);
			computeMSER.execute();
					
			

	
		}

		
		if (change == ValueChange.WATER) {
		
			if (overlay!=null)
				overlay.clear();
			
			
			newimg = utility.Slicer.copytoByteImage(CurrentView);
			bitimg = new ArrayImgFactory<BitType>().create(newimg, new BitType());
			bitimgFloat = new ArrayImgFactory<FloatType>().create(newimg, new FloatType());
			GetLocalmaxminMT.ThresholdingMTBit(CurrentView, bitimg, thresholdWater);
			if (displayBinaryimg)
				ImageJFunctions.show(bitimg);
			DistWatershed<FloatType> WaterafterDisttransform = new DistWatershed<FloatType>(this, 
					CurrentView, bitimg, jpb);
			WaterafterDisttransform.execute();
			
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

			if (overlay!=null)
				overlay.clear();
			newimg = utility.Slicer.copytoByteImage(CurrentView);

		

			DOGSeg computeDOG = new DOGSeg(this, jpb);
			computeDOG.execute();
		}

	}
	

	public JFrame Cardframe = new JFrame("Computer Vision Segmentation Tools");
	public JPanel panelCont = new JPanel();
	public JPanel panelFirst = new JPanel();
	public JPanel Timeselect = new JPanel();
	public JPanel Zselect = new JPanel();
	public JPanel DogPanel = new JPanel();
	public JPanel MserPanel = new JPanel();
	public JPanel WaterPanel = new JPanel();
	
	public JPanel DetectionPanel = new JPanel();
	
	
	final String timestring = "Current T";
	final String zstring = "Current Z";
	final String zgenstring = "Current Z / T";
	
	
	final String sigmastring = "Approximate object size";
	final String thresholdstring = "Approximate peak intensity";
	
	
	final String deltastring = "Stride in intensiy threshold space";
	final String Unstability_Scorestring = "Unstability score";
	final String minDivstring = "Minimum diversity b/w components of tree";
	final String minSizestring = "Minimum size of MSER ellipses";
	final String maxSizestring = "Maximum size of MSER ellipses";
	final String waterstring = "Threshold for Watershedding";
	

	public Label timeText = new Label("Current T = " + 1, Label.CENTER);
	public Label zText = new Label("Current Z = " + 1, Label.CENTER);
	public Label zgenText = new Label("Current Z / T = " + 1, Label.CENTER);
	


	
	final Label deltaText = new Label( deltastring + " = " + deltaInit, Label.CENTER);
	final Label Unstability_ScoreText = new Label(Unstability_Scorestring + " = " + Unstability_ScoreInit, Label.CENTER);
	final Label minDivText = new Label(minDivstring +" = " + minDiversityInit, Label.CENTER);
	final Label minSizeText = new Label(minSizestring +" = " + minSizeInit, Label.CENTER);
	final Label maxSizeText = new Label( maxSizestring +" = " + maxSizeInit, Label.CENTER);
	
	public CheckboxGroup minormax = new CheckboxGroup();
	final Checkbox findminima = new Checkbox("Locate Minima", minormax, lookForMinima );
	final Checkbox findmaxima = new Checkbox("Locate Maxima", minormax, lookForMaxima );
	
	public CheckboxGroup minormaxMser = new CheckboxGroup();
	final Checkbox findminimaMser = new Checkbox("Locate Maxima", minormaxMser, brighttodark );
	final Checkbox findmaximaMser = new Checkbox("Locate Minima", minormaxMser, darktobright );
	
	
	final Checkbox displayWater = new Checkbox("Display Watershed image", true);
	
	final Checkbox displayBinary = new Checkbox("Display Binary image");
	
	final Checkbox displayDist = new Checkbox("Display Distance transformed image");
	
	final Checkbox autothreshold = new Checkbox("Auto determine threshold intensity");
	
	
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
	
	
	public JScrollBar sigmaslider = new JScrollBar(Scrollbar.HORIZONTAL, sigmaInit, 10, 0,
			scrollbarSize + 10);
	public JScrollBar thresholdslider = new JScrollBar(Scrollbar.HORIZONTAL, thresholdInit, 10, 0,
			10 + scrollbarSize);
	public JScrollBar thresholdWaterslider = new JScrollBar(Scrollbar.HORIZONTAL, thresholdsliderWaterInit, 10, 0,
			10 + scrollbarSize);
	
	final JScrollBar deltaS = new JScrollBar(Scrollbar.HORIZONTAL, deltaInit, 10, 0, 10 + scrollbarSize);
	final JScrollBar Unstability_ScoreS = new JScrollBar(Scrollbar.HORIZONTAL, Unstability_ScoreInit, 10, 0, 10 + scrollbarSize);
	final JScrollBar minDiversityS = new JScrollBar(Scrollbar.HORIZONTAL, minDiversityInit, 10, 0,
			10 + scrollbarSize);
	
	final JScrollBar minSizeS = new JScrollBar(Scrollbar.HORIZONTAL, minSizeInit, 10, 0, 10 + scrollbarSize);
	final JScrollBar maxSizeS = new JScrollBar(Scrollbar.HORIZONTAL, maxSizeInit, 10, 0, 10 + scrollbarSize);
	
	
	public Label sigmaText = new Label("Approximate object size = " + sigmaInit, Label.CENTER);
	public Label thresholdText = new Label("Approximate peak intensity " + thresholdInit, Label.CENTER);
	public Label watertext = new Label(waterstring + " = "+ thresholdInitWater, Label.CENTER);
	
	
	public TextField inputField = new TextField();
	public TextField inputFieldT, inputtrackField;
	public TextField inputFieldZ;
	
	public int SizeX = 400;
	public int SizeY = 200;
	public int SizeYsmall = 200;
	public int SizeYbig = 400;
	
	
	public void Card() {
		
		Unstability_ScoreS.setValue(utility.ScrollbarUtils.computeScrollbarPositionFromValue(Unstability_ScoreInit, Unstability_ScoreMin, Unstability_ScoreMax, scrollbarSize));
		
		minDiversityS.setValue(utility.ScrollbarUtils.computeScrollbarPositionFromValue(minDiversityInit, minDiversityMin, minDiversityMax, scrollbarSize));
		
		maxSizeS.setValue(utility.ScrollbarUtils.computeScrollbarPositionFromValue(maxSizeInit, maxSizemin, maxSizemax, scrollbarSize));
		
		minSizeS.setValue(utility.ScrollbarUtils.computeScrollbarPositionFromValue(minSizeInit, minSizemin, minSizemax, scrollbarSize));
		
		deltaS.setValue(utility.ScrollbarUtils.computeScrollbarPositionFromValue(deltaInit, deltaMin, deltaMax, scrollbarSize));
		
		sigmaslider.setValue(utility.ScrollbarUtils.computeScrollbarPositionFromValue(sigmaInit, sigmaMin, sigmaMax, scrollbarSize));
		
		thresholdWaterslider.setValue(utility.ScrollbarUtils.computeScrollbarPositionFromValue(thresholdsliderWaterInit, thresholdMinWater, thresholdMaxWater, scrollbarSize));
		
		
		thresholdslider.setValue(utility.ScrollbarUtils.computeScrollbarPositionFromValue(thresholdsliderInit, thresholdMin, thresholdMax, scrollbarSize));
		
		sigma = utility.ScrollbarUtils.computeValueFromScrollbarPosition(sigmaslider.getValue(), sigmaMin, sigmaMax,
				scrollbarSize);
		
		threshold =  utility.ScrollbarUtils.computeValueFromScrollbarPosition(thresholdslider.getValue(), thresholdMin, thresholdMax,
				scrollbarSize);
		
		thresholdWater = utility.ScrollbarUtils.computeValueFromScrollbarPosition(thresholdWaterslider.getValue(), thresholdMinWater, thresholdMaxWater,
				scrollbarSize);

		delta = utility.ScrollbarUtils.computeValueFromScrollbarPosition(deltaS.getValue(), deltaMin, deltaMax,
				scrollbarSize);
		minDiversity = utility.ScrollbarUtils.computeValueFromScrollbarPosition(minDiversityS.getValue(), minDiversityMin, minDiversityMax,
				scrollbarSize);
		Unstability_Score = utility.ScrollbarUtils.computeValueFromScrollbarPosition(Unstability_ScoreS.getValue(), Unstability_ScoreMin, Unstability_ScoreMax,
				scrollbarSize);
		minSize = (long) utility.ScrollbarUtils.computeValueFromScrollbarPosition(minSizeS.getValue(), minSizemin, minSizemax,
				scrollbarSize);
		
		maxSize = (long) utility.ScrollbarUtils.computeValueFromScrollbarPosition(maxSizeS.getValue(), maxSizemin, maxSizemax,
				scrollbarSize);
		
		
		sigmaText = new Label("Approximate object size = " + sigma , Label.CENTER);
		thresholdText = new Label("Approximate peak intensity " + threshold, Label.CENTER);
	    watertext = new Label(waterstring + " = "+ thresholdWater, Label.CENTER);
	    
	    
		
		CardLayout cl = new CardLayout();

		c.insets = new Insets(5, 5, 5, 5);
		panelCont.setLayout(cl);

		panelCont.add(panelFirst, "1");
		
		panelFirst.setLayout(layout);

		Timeselect.setLayout(layout);

		Zselect.setLayout(layout);
		DogPanel.setLayout(layout);
		MserPanel.setLayout(layout);
		DetectionPanel.setLayout(layout);
		
		WaterPanel.setLayout(layout);
		
		inputFieldZ = new TextField();
		inputFieldZ = new TextField(5);
		inputFieldZ.setText(Integer.toString(thirdDimension));

		inputField.setColumns(10);

		inputFieldT = new TextField();
		inputFieldT = new TextField(5);
		inputFieldT.setText(Integer.toString(fourthDimension));
		
		Border timeborder = new CompoundBorder(new TitledBorder("Select time"), new EmptyBorder(c.insets));
		Border zborder = new CompoundBorder(new TitledBorder("Select Z"), new EmptyBorder(c.insets));
		Border dogborder = new CompoundBorder(new TitledBorder("Difference of Gaussian detection"), new EmptyBorder(c.insets));
		Border mserborder = new CompoundBorder(new TitledBorder("MSER detection"), new EmptyBorder(c.insets));
		Border waterborder = new CompoundBorder(new TitledBorder("Watershed detection"), new EmptyBorder(c.insets));
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
				Timeselect.setMinimumSize(new Dimension(SizeX, SizeY));
				Timeselect.setPreferredSize(new Dimension(SizeX, SizeY));
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
				Zselect.setMinimumSize(new Dimension(SizeX, SizeY));
				Zselect.setPreferredSize(new Dimension(SizeX, SizeY));
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
				DetectionPanel.add(MSER, new GridBagConstraints(4, 0, 1, 1, 0.0, 0.0, GridBagConstraints.WEST,
						GridBagConstraints.HORIZONTAL, insets, 0, 0));
				panelFirst.add(DetectionPanel, new GridBagConstraints(0, 1, 3, 1, 0.0, 0.0, GridBagConstraints.WEST,
						GridBagConstraints.HORIZONTAL, insets, 0, 0));
				
			
				
				

				WaterPanel.add(watertext, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.WEST,
						GridBagConstraints.HORIZONTAL, insets, 0, 0));

				WaterPanel.add(thresholdWaterslider, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.WEST,
						GridBagConstraints.HORIZONTAL, insets, 0, 0));
				
				
				
				WaterPanel.add(displayWater, new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0, GridBagConstraints.WEST,
						GridBagConstraints.HORIZONTAL, insets, 0, 0));
				WaterPanel.add(displayBinary, new GridBagConstraints(2, 1, 1, 1, 0.0, 0.0, GridBagConstraints.WEST,
						GridBagConstraints.HORIZONTAL, insets, 0, 0));
				WaterPanel.add(displayDist, new GridBagConstraints(0, 3, 1, 1, 0.0, 0.0, GridBagConstraints.WEST,
						GridBagConstraints.HORIZONTAL, insets, 0, 0));
				WaterPanel.add(autothreshold, new GridBagConstraints(2, 3, 1, 1, 0.0, 0.0, GridBagConstraints.WEST,
						GridBagConstraints.HORIZONTAL, insets, 0, 0));

				WaterPanel.setBorder(waterborder);
				
				
				panelFirst.add(WaterPanel, new GridBagConstraints(0, 2, 3, 1, 0.0, 0.0, GridBagConstraints.WEST,
						GridBagConstraints.HORIZONTAL, insets, 0, 0));
				
				
				
				DogPanel.add(sigmaText, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.WEST,
						GridBagConstraints.HORIZONTAL, insets, 0, 0));

				DogPanel.add(sigmaslider, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.WEST,
						GridBagConstraints.HORIZONTAL, insets, 0, 0));
				DogPanel.add(thresholdText, new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0, GridBagConstraints.WEST,
						GridBagConstraints.HORIZONTAL, insets, 0, 0));

				DogPanel.add(thresholdslider, new GridBagConstraints(0, 3, 1, 1, 0.0, 0.0, GridBagConstraints.WEST,
						GridBagConstraints.HORIZONTAL, insets, 0, 0));
				
				DogPanel.add(findminima, new GridBagConstraints(0, 4, 1, 1, 0.0, 0.0, GridBagConstraints.WEST,
						GridBagConstraints.HORIZONTAL, insets, 0, 0));

				DogPanel.add(findmaxima, new GridBagConstraints(3, 4, 1, 1, 0.0, 0.0, GridBagConstraints.WEST,
						GridBagConstraints.HORIZONTAL, insets, 0, 0));
				
				DogPanel.setBorder(dogborder);
				DogPanel.setMinimumSize(new Dimension(SizeX, SizeYsmall));
				DogPanel.setPreferredSize(new Dimension(SizeX, SizeYsmall));
				panelFirst.add(DogPanel, new GridBagConstraints(0, 3, 3, 1, 0.0, 0.0, GridBagConstraints.WEST,
						GridBagConstraints.HORIZONTAL, insets, 0, 0));
				
				
				
				
				 MserPanel.add(deltaText,  new GridBagConstraints(0, 0, 3, 1, 0.0, 0.0, GridBagConstraints.EAST,
							GridBagConstraints.HORIZONTAL, new Insets(10, 10, 0, 10), 0, 0));
				 
				 MserPanel.add(deltaS,  new GridBagConstraints(0, 1, 3, 1, 0.0, 0.0, GridBagConstraints.EAST,
							GridBagConstraints.HORIZONTAL, new Insets(10, 10, 0, 10), 0, 0));

				 MserPanel.add(Unstability_ScoreText,  new GridBagConstraints(0, 2, 3, 1, 0.0, 0.0, GridBagConstraints.EAST,
							GridBagConstraints.HORIZONTAL, new Insets(10, 10, 0, 10), 0, 0));

				 MserPanel.add(Unstability_ScoreS,  new GridBagConstraints(0, 3, 3, 1, 0.0, 0.0, GridBagConstraints.EAST,
							GridBagConstraints.HORIZONTAL, new Insets(10, 10, 0, 10), 0, 0));
				   
				 
				 MserPanel.add(minDivText,  new GridBagConstraints(0, 4, 3, 1, 0.0, 0.0, GridBagConstraints.EAST,
							GridBagConstraints.HORIZONTAL, new Insets(10, 10, 0, 10), 0, 0));

				 MserPanel.add(minDiversityS,  new GridBagConstraints(0, 5, 3, 1, 0.0, 0.0, GridBagConstraints.EAST,
							GridBagConstraints.HORIZONTAL, new Insets(10, 10, 0, 10), 0, 0));
				 
				 MserPanel.add(minSizeText,  new GridBagConstraints(0, 6, 3, 1, 0.0, 0.0, GridBagConstraints.EAST,
							GridBagConstraints.HORIZONTAL, new Insets(10, 10, 0, 10), 0, 0));

				 MserPanel.add(minSizeS,  new GridBagConstraints(0, 7, 3, 1, 0.0, 0.0, GridBagConstraints.EAST,
							GridBagConstraints.HORIZONTAL, new Insets(10, 10, 0, 10), 0, 0));
				 
				 MserPanel.add(maxSizeText,  new GridBagConstraints(0, 8, 3, 1, 0.0, 0.0, GridBagConstraints.EAST,
							GridBagConstraints.HORIZONTAL, new Insets(10, 10, 0, 10), 0, 0));
				 MserPanel.add(maxSizeS,  new GridBagConstraints(0, 9, 3, 1, 0.0, 0.0, GridBagConstraints.EAST,
							GridBagConstraints.HORIZONTAL, new Insets(10, 10, 0, 10), 0, 0));
				 MserPanel.add(findminimaMser, new GridBagConstraints(0, 10, 1, 1, 0.0, 0.0, GridBagConstraints.WEST,
							GridBagConstraints.HORIZONTAL, insets, 0, 0));

				 MserPanel.add(findmaximaMser, new GridBagConstraints(1, 10, 1, 1, 0.0, 0.0, GridBagConstraints.WEST,
							GridBagConstraints.HORIZONTAL, insets, 0, 0));
					
				
				 MserPanel.setMinimumSize(new Dimension(SizeX, SizeYbig));
				 MserPanel.setPreferredSize(new Dimension(SizeX, SizeYbig));
					
					
				
				 
				

				 MserPanel.setBorder(mserborder);
				
				 panelFirst.add(MserPanel, new GridBagConstraints(3, 2, 3, 1, 0.0, 0.0, GridBagConstraints.EAST,
							GridBagConstraints.HORIZONTAL, new Insets(10, 10, 0, 10), 0, 0));
				
				
				
				sigmaslider.addAdjustmentListener(new PreSigmaListener(this, sigmaText, sigmastring, sigmaMin,
						sigmaMax, scrollbarSize, sigmaslider));

				thresholdWaterslider.addAdjustmentListener(new PreWaterListener(this, watertext, waterstring, thresholdMinWater,
						thresholdMaxWater, scrollbarSize, thresholdWaterslider));
			
				Watershed.addItemListener(new DoWatershedListener(this));
				DOG.addItemListener(new DoDOGListener(this));
				MSER.addItemListener(new DoMSERListener(this));
				autothreshold.addItemListener(new PREauto(this));
				
				
				findminima.addItemListener(new FindMinimaListener(this));
				findmaxima.addItemListener(new FindMaximaListener(this));
				findminimaMser.addItemListener(new FindMinimaMserListener(this));
				findmaximaMser.addItemListener(new FindMaximaMserListener(this));
				
				displayBinary.addItemListener(new PREShowBinary(this));
				displayWater.addItemListener(new PREShowWatershed(this));
				displayDist.addItemListener(new PREShowDist(this));
				
				deltaS.addAdjustmentListener(new PREDeltaListener(this, deltaText, deltastring, deltaMin, deltaMax, 
						scrollbarSize, deltaS));

				Unstability_ScoreS.addAdjustmentListener(
						new PREUnstability_ScoreListener(this, Unstability_ScoreText, Unstability_Scorestring, Unstability_ScoreMin, Unstability_ScoreMax, 
								scrollbarSize, Unstability_ScoreS));

				minDiversityS.addAdjustmentListener(new PREMinDiversityListener(this, minDivText, minDivstring, minDiversityMin,
						minDiversityMax, scrollbarSize, minDiversityS));

				minSizeS.addAdjustmentListener(
						new PREMinSizeListener(this, minSizeText, minSizestring, minSizemin, minSizemax,
                      scrollbarSize, minSizeS));

				maxSizeS.addAdjustmentListener(
						new PREMaxSizeListener(this,maxSizeText, maxSizestring, maxSizemin, maxSizemax, 
								scrollbarSize, maxSizeS));

				
				thresholdslider.addAdjustmentListener(new PreThresholdListener(this, thresholdText, thresholdstring, thresholdMin, thresholdMax,
						scrollbarSize, thresholdslider));
				timeslider.addAdjustmentListener(new PreTimeListener(this, timeText, timestring, fourthDimensionsliderInit,
						fourthDimensionSize, scrollbarSize, timeslider));
				if (ndims > 3)
				zslider.addAdjustmentListener(new PreZListener(this, zText, zstring, thirdDimensionsliderInit, thirdDimensionSize,
						scrollbarSize, zslider));
				else
				zslider.addAdjustmentListener(new PreZListener(this, zgenText, zgenstring, thirdDimensionsliderInit, thirdDimensionSize,
						scrollbarSize, zslider));
				
				inputFieldZ.addTextListener(new PreZlocListener(this, false));
				inputFieldT.addTextListener(new PreTlocListener(this, false));
				panelFirst.setMinimumSize(new Dimension(SizeX, SizeY));

				panelFirst.setVisible(true);
				cl.show(panelCont, "1");
				Cardframe.add(panelCont, "Center");
				Cardframe.add(jpb, "Last");

				Cardframe.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
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
