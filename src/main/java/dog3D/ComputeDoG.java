package dog3D;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.ExecutionException;

import javax.swing.JProgressBar;
import javax.swing.SwingWorker;

import ij.IJ;
import ij.gui.Roi;
import interactivePreprocessing.InteractiveMethods;
import net.imglib2.Cursor;
import net.imglib2.KDTree;
import net.imglib2.RandomAccess;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.RealPoint;
import net.imglib2.RealPointSampleList;
import net.imglib2.algorithm.componenttree.mser.MserTree;
import net.imglib2.algorithm.dog.DogDetection;
import net.imglib2.algorithm.labeling.AllConnectedComponents;
import net.imglib2.algorithm.labeling.Watershed;
import net.imglib2.img.ImgFactory;
import net.imglib2.img.array.ArrayImgFactory;
import net.imglib2.img.display.imagej.ImageJFunctions;
import net.imglib2.labeling.DefaultROIStrategyFactory;
import net.imglib2.labeling.Labeling;
import net.imglib2.labeling.LabelingROIStrategy;
import net.imglib2.labeling.NativeImgLabeling;
import net.imglib2.neighborsearch.NearestNeighborSearchOnKDTree;
import net.imglib2.roi.labeling.ImgLabeling;
import net.imglib2.type.NativeType;
import net.imglib2.type.logic.BitType;
import net.imglib2.type.numeric.RealType;
import net.imglib2.type.numeric.integer.IntType;
import net.imglib2.type.numeric.integer.UnsignedByteType;
import net.imglib2.type.numeric.real.FloatType;
import net.imglib2.util.Pair;
import net.imglib2.util.Util;
import net.imglib2.view.Views;
import utility.PreRoiobject;

public class ComputeDoG<T extends RealType<T> & NativeType<T>> {

	final InteractiveMethods parent;
	final JProgressBar jpb;
	public final RandomAccessibleInterval<T> source;

	public RandomAccessibleInterval<BitType> bitimg;
	public boolean apply3D;
	public int z;
	public int t;

	public ComputeDoG(final InteractiveMethods parent, final RandomAccessibleInterval<T> source,
			final JProgressBar jpb, boolean apply3D, int z, int t) {

		this.parent = parent;
		this.source = source;
		this.jpb = jpb;
		this.apply3D = apply3D;
		this.z = z;
		this.t = t;
		
		bitimg = new ArrayImgFactory<BitType>().create(source, new BitType());
	}

	public void execute() {

		final DogDetection.ExtremaType type;
		if (parent.lookForMaxima)
			type = DogDetection.ExtremaType.MINIMA;
		else
			type = DogDetection.ExtremaType.MAXIMA;
		parent.sigma2 = utility.ScrollbarUtils.computeSigma2(parent.sigma, parent.sensitivity);
		final DogDetection<T> newdog = new DogDetection<T>(Views.extendBorder(source),
				parent.interval, new double[] { 1, 1 }, parent.sigma, parent.sigma2, type, parent.threshold, true);

		parent.peaks = newdog.getSubpixelPeaks();
		parent.CurrentPreRoiobject = new ArrayList<PreRoiobject>();

		for (Roi currentroi : parent.Rois) {

			final double[] geocenter = currentroi.getContourCentroid();
			final Pair<Double, Integer> Intensityandpixels = PreRoiobject.getIntensity(currentroi, source);
			final double intensity = Intensityandpixels.getA();
			final double numberofpixels = Intensityandpixels.getB();
			final double averageintensity = intensity / numberofpixels;
			PreRoiobject currentobject = new PreRoiobject(currentroi,
					new double[] { geocenter[0], geocenter[1], parent.thirdDimension }, numberofpixels, intensity,
					averageintensity, parent.thirdDimension, parent.fourthDimension);
			parent.CurrentPreRoiobject.add(currentobject);
		}

		String uniqueID = Integer.toString(z) + Integer.toString(t);
		parent.ZTRois.put(uniqueID, parent.CurrentPreRoiobject);
		CreateBinary(source, bitimg);

	}



	public RandomAccessibleInterval<BitType> getBinaryimg() {

		return bitimg;
	}

	/***
	 * 
	 * Do the distance transform of the input image using the bit image provided.
	 * 
	 * @param inputimg
	 *            The pre-processed input image as RandomAccessibleInterval <T>
	 * @param outimg
	 *            The distance transormed image having the same dimensions as the
	 *            input image.
	 * @param invtype
	 *            Straight: The intensity value is set to the distance, gives white
	 *            on black background. Inverse: The intensity is set to the negative
	 *            of the distance, gives black on white background.
	 */

	private void CreateBinary(RandomAccessibleInterval<T> inputimg, RandomAccessibleInterval<BitType> outimg) {

		Cursor<T> incursor = Views.iterable(inputimg).localizingCursor();
		RandomAccess<BitType> outran = outimg.randomAccess();

		while (incursor.hasNext()) {

			incursor.fwd();
			outran.setPosition(incursor);

			for (Roi currentroi : parent.Rois) {

				if (currentroi.contains(incursor.getIntPosition(0), incursor.getIntPosition(1))) {

					outran.get().setOne();

				}

			}

		}
	}



}
