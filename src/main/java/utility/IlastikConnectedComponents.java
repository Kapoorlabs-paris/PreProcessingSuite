package utility;

import java.util.Iterator;

import ij.ImageJ;
import io.scif.img.ImgIOException;
import io.scif.img.ImgOpener;
import net.imglib2.Cursor;
import net.imglib2.KDTree;
import net.imglib2.RandomAccess;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.RealPoint;
import net.imglib2.RealPointSampleList;
import net.imglib2.algorithm.labeling.AllConnectedComponents;
import net.imglib2.algorithm.labeling.Watershed;
import net.imglib2.algorithm.stats.Normalize;
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
import net.imglib2.type.numeric.integer.IntType;
import net.imglib2.type.numeric.integer.UnsignedByteType;
import net.imglib2.type.numeric.real.FloatType;
import net.imglib2.util.Util;
import net.imglib2.view.Views;

public class IlastikConnectedComponents {

	static RandomAccessibleInterval<IntType> connectedcomponentImage;
	static RandomAccessibleInterval<BitType> bitimg;

	public static <T extends NativeType<T>> RandomAccessibleInterval<IntType> GetLabelledImage(
			RandomAccessibleInterval<T> source) {
		final ImgFactory<UnsignedByteType> factory = Util.getArrayOrCellImgFactory(source, new UnsignedByteType());
		

		// Prepare seed image
		NativeImgLabeling<Integer, IntType> oldseedLabeling = new NativeImgLabeling<Integer, IntType>(
				new ArrayImgFactory<IntType>().create(source, new IntType()));
		oldseedLabeling = PrepareSeedImage(source);

		connectedcomponentImage = oldseedLabeling.getStorageImg();

		return connectedcomponentImage;
	}

	

	public static <T extends NativeType<T>> NativeImgLabeling<Integer, IntType> PrepareSeedImage(
			RandomAccessibleInterval<T> inputimg) {

		// New Labeling type
		final ImgLabeling<Integer, IntType> seedLabeling = new ImgLabeling<Integer, IntType>(
				new ArrayImgFactory<IntType>().create(inputimg, new IntType()));

		// Old Labeling type
		final NativeImgLabeling<Integer, IntType> oldseedLabeling = new NativeImgLabeling<Integer, IntType>(
				new ArrayImgFactory<IntType>().create(inputimg, new IntType()));

		// The label generator for both new and old type
		final Iterator<Integer> labelGenerator = AllConnectedComponents.getIntegerNames(0);

		// Getting unique labelled image (old version)
		AllConnectedComponents.labelAllConnectedComponents(oldseedLabeling, bitimg, labelGenerator,
				AllConnectedComponents.getStructuringElement(inputimg.numDimensions()));
		return oldseedLabeling;
	}

	public static NativeImgLabeling<Integer, IntType> GetlabeledImage(
			RandomAccessibleInterval<BitType> inputimg, NativeImgLabeling<Integer, IntType> seedLabeling) {

		int n = inputimg.numDimensions();
		long[] dimensions = new long[n];

		for (int d = 0; d < n; ++d)
			dimensions[d] = inputimg.dimension(d);
		final NativeImgLabeling<Integer, IntType> outputLabeling = new NativeImgLabeling<Integer, IntType>(
				new ArrayImgFactory<IntType>().create(inputimg, new IntType()));

		final Watershed<BitType, Integer> watershed = new Watershed<BitType, Integer>();

		watershed.setSeeds(seedLabeling);
		watershed.setIntensityImage(inputimg);
		watershed.setStructuringElement(AllConnectedComponents.getStructuringElement(2));
		watershed.setOutputLabeling(outputLabeling);
		watershed.process();
		DefaultROIStrategyFactory<Integer> deffactory = new DefaultROIStrategyFactory<Integer>();
		LabelingROIStrategy<Integer, Labeling<Integer>> factory = deffactory
				.createLabelingROIStrategy(watershed.getResult());
		outputLabeling.setLabelingCursorStrategy(factory);

		return outputLabeling;

	}
	public static RandomAccessibleInterval<BitType> CreateBinaryImage(RandomAccessibleInterval<FloatType> inputimage,
			final FloatType threshold) {

		final ImgFactory<BitType> factory = net.imglib2.util.Util.getArrayOrCellImgFactory(inputimage, new BitType());
		RandomAccessibleInterval<BitType> binaryimage = factory.create(inputimage, new BitType());

		Cursor<FloatType> cursor = Views.iterable(inputimage).localizingCursor();
		RandomAccess<BitType> ranac = binaryimage.randomAccess();

		while (cursor.hasNext()) {

			cursor.fwd();

			ranac.setPosition(cursor);

			if (cursor.get().compareTo(threshold) >= 1)
				ranac.get().setOne();
			else
				ranac.get().setZero();

		}

		return binaryimage;
	}

	public static void main(String[] args) throws ImgIOException {

		// Open an ImageJ instance
		new ImageJ();
		
		// Input directory where the probability map of the required class is
		String directory = "/Users/aimachine/Documents/IlastikJLM/datasets_for_ilastic_training/Stage3Training/BoundarySegmentation/";
		
		// Input filename of the map
		String fileName = "20171027_stage3_2-normalized_Probabilitiesexported_data.tif";
		
		// Scifio logger
		org.apache.log4j.BasicConfigurator.configure();
		
		// Open Image
		RandomAccessibleInterval<FloatType> image = new ImgOpener().openImgs(directory + fileName, new FloatType())
				.iterator().next();
		
		FloatType minval = new FloatType(0);
		FloatType maxval = new FloatType(1);
		Normalize.normalize(Views.iterable(image), minval, maxval);
		
	    // Seelct threshold for making the binary image
		FloatType threshold = new FloatType(0.5f);
		
		bitimg = CreateBinaryImage(image, threshold);
		
		// Get the labelled image
		RandomAccessibleInterval<IntType> intimg = GetLabelledImage(image);
		
		
	
		// Show all images

		ImageJFunctions.show(image).setTitle("Probability Maps");
		
		ImageJFunctions.show(bitimg).setTitle("Binary Image");
		
		ImageJFunctions.show(intimg).setTitle("Connected Components");

	}

}
