package interactivePreprocessing;

import java.awt.CardLayout;
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

import ij.IJ;
import ij.ImageJ;
import ij.ImagePlus;
import ij.ImageStack;
import ij.gui.Overlay;
import ij.gui.Roi;
import ij.plugin.frame.RoiManager;
import mpicbg.imglib.util.Util;
import net.imglib2.Cursor;
import net.imglib2.FinalInterval;
import net.imglib2.Point;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.algorithm.componenttree.mser.MserTree;
import net.imglib2.algorithm.dog.DogDetection;
import net.imglib2.algorithm.localextrema.RefinedPeak;
import net.imglib2.img.display.imagej.ImageJFunctions;
import net.imglib2.type.numeric.integer.UnsignedByteType;
import net.imglib2.type.numeric.real.FloatType;
import net.imglib2.view.Views;
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
	
	public int sigmasliderInit = 0;
	public int thresholdsliderInit = 0;
	
	public ImagePlus imp;
	public boolean showMSER = false;
	public boolean showDOG = false;
	public RandomAccessibleInterval<UnsignedByteType> newimg;
	public RandomAccessibleInterval<FloatType> CurrentView;
	public RoiManager roimanager;
	public final int scrollbarSize = 1000;
	public JProgressBar jpb;
	public File inputfile;
	public int alphaInit = 1;
	public int betaInit = 0;
	
	public int minSizeInit = 1;
	public int maxSizeInit = 100;
	public float delta = 1f;
	public int deltaInit = 10;
	public int maxVarInit = 1;
	public float deltaMax = 400f;
	public float maxVarMin = 0;
	public float maxVarMax = 1;
	public float maxVar = 1;
	public int Progressmin = 0;
	public int Progressmax = 100;
	public int max = Progressmax;
	public float minDiversity = 1;
	public float thresholdHough = 1;
	public FloatType minval = new FloatType(0);
	public FloatType maxval = new FloatType(1);
	public MserTree<UnsignedByteType> newtree;
	public float thresholdMin = 0f;
	public float thresholdMax = 1f;
	public int thresholdInit = 1;

	public float minDiversityMin = 0;
	public float minDiversityMax = 1;
	public int minDiversityInit = 1;
	public int timeMin = 1;
	public long minSize = 1;
	public long maxSize = 1000;
	public long minSizemin = 0;
	public long minSizemax = 10000;
	public long maxSizemin = 1;
	public long maxSizemax = 10000;
	public float deltaMin = 0;
	public float sigma = 0.5f;
	public float sigma2 = 0.5f;
	public float threshold = 1f;
	public boolean darktobright = true;
	public ArrayList<Roi> Rois;
	public ArrayList<Roi> NearestNeighbourRois;
	public ArrayList<Roi> BiggerRois;
	public int sigmaInit = 30;
	public Color colorDrawMser = Color.green;
	public Color colorDrawDog = Color.red;
	public Overlay overlay;
	public FinalInterval interval;
	public boolean lookForMaxima = false;
	public float sigmaMin = 0.5f;
	public float sigmaMax = 100f;
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
		maxVar = value;
		maxVarInit = computeScrollbarPositionFromValue(maxVar, maxVarMin, maxVarMax, scrollbarSize);
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
		jpb = new JProgressBar();
		overlay = new Overlay();
		interval = new FinalInterval(originalimg.dimension(0), originalimg.dimension(1));
		peaks = new ArrayList<RefinedPeak<Point>>();
		jpb = new JProgressBar();
		
		setInitialUnstability_Score(maxVarInit);
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

			
			newimg = utility.Slicer.copytoByteImage(CurrentView);
			
			if (showMSER) {

				IJ.log(" Computing the Component tree");

				newtree = MserTree.buildMserTree(newimg, delta, minSize, maxSize, maxVar, minDiversity, darktobright);
				Rois = utility.FinderUtils.getcurrentRois(newtree);
				ArrayList<double[]> centerRoi = utility.FinderUtils.getRoiMean(newtree);
				for (int index = 0; index < centerRoi.size(); ++index) {

					double[] center = new double[] { centerRoi.get(index)[0], centerRoi.get(index)[1] };

					Roi or = Rois.get(index);

					or.setStrokeColor(colorDrawMser);
					overlay.add(or);
				}

		
			}

			if (showDOG) {

			

				final DogDetection.ExtremaType type;

				if (lookForMaxima)
					type = DogDetection.ExtremaType.MINIMA;
				else
					type = DogDetection.ExtremaType.MAXIMA;

				final DogDetection<FloatType> newdog = new DogDetection<FloatType>(Views.extendBorder(CurrentView),
						interval, new double[] { 1, 1 }, sigma, sigma2, type, threshold, true);

				peaks = newdog.getSubpixelPeaks();

				Rois = utility.FinderUtils.getcurrentRois(peaks, sigma, sigma2);
				
				
				for (int index = 0; index < peaks.size(); ++index) {

					double[] center = new double[] { peaks.get(index).getDoublePosition(0),
							peaks.get(index).getDoublePosition(1) };

					Roi or = Rois.get(index);

					or.setStrokeColor(colorDrawDog);
					overlay.add(or);
				}

			}

			
		}

	}
	

	public JFrame Cardframe = new JFrame("Computer Vision Segmentation Tools");
	public JPanel panelCont = new JPanel();
	public JPanel panelFirst = new JPanel();
	public JPanel Timeselect = new JPanel();
	public JPanel Zselect = new JPanel();
	public JPanel DogPanel = new JPanel();
	public JPanel MserPanel = new JPanel();
	
	final String timestring = "Current T";
	final String zstring = "Current Z";
	final String zgenstring = "Current Z / T";
	
	
	final String sigmastring = "Approximate object size";
	final String thresholdstring = "Approximate normalized peak intensity";

	public Label timeText = new Label("Current T = " + 1, Label.CENTER);
	public Label zText = new Label("Current Z = " + 1, Label.CENTER);
	public Label zgenText = new Label("Current Z / T = " + 1, Label.CENTER);
	

	public Label sigmaText = new Label("Approximate object size = " + sigmaInit, Label.CENTER);
	public Label thresholdText = new Label("Approximate normalized peak intensity " + thresholdInit, Label.CENTER);
	
	
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
	
	public TextField inputField = new TextField();
	public TextField inputFieldT, inputtrackField;
	public TextField inputFieldZ;
	
	public int SizeX = 400;
	public int SizeY = 300;
	
	
	public void Card() {
		CardLayout cl = new CardLayout();

		c.insets = new Insets(5, 5, 5, 5);
		panelCont.setLayout(cl);

		panelCont.add(panelFirst, "1");
		
		panelFirst.setLayout(layout);

		Timeselect.setLayout(layout);

		Zselect.setLayout(layout);
		
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
				
				
				
				
				
				
				sigmaslider.addAdjustmentListener(new PreSigmaListener(this, sigmaText, sigmastring, sigmasliderInit,
						sigmaMax, scrollbarSize, sigmaslider));
				
				thresholdslider.addAdjustmentListener(new PreThresholdListener(this, thresholdText, thresholdstring, thresholdsliderInit, thresholdMax,
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
