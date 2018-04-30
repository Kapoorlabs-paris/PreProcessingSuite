package preProcessing;

import net.imglib2.Cursor;
import net.imglib2.RandomAccess;
import net.imglib2.RandomAccessible;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.img.array.ArrayImgFactory;
import net.imglib2.type.NativeType;
import net.imglib2.type.logic.BitType;
import net.imglib2.type.numeric.RealType;
import net.imglib2.type.numeric.real.FloatType;
import net.imglib2.view.Views;

public class GenericFilters {

	
	public static <T extends RealType<T> & NativeType<T>> RandomAccessibleInterval<BitType> GradientmagnitudeImage(
			RandomAccessibleInterval<T> inputimg) {
		
		
		final T type = inputimg.randomAccess().get().createVariable();
		RandomAccessibleInterval<BitType> gradientimg = new ArrayImgFactory<BitType>().create(inputimg,
				new BitType());
		Cursor<BitType> cursor = Views.iterable(gradientimg).localizingCursor();
		RandomAccessible<T> view = Views.extendBorder(inputimg);
		RandomAccess<T> randomAccess = view.randomAccess();

		// iterate over all pixels
		while (cursor.hasNext()) {
			// move the cursor to the next pixel
			cursor.fwd();

			// compute gradient and its direction in each dimension
			double gradient = 0;

			for (int d = 0; d < inputimg.numDimensions(); ++d) {
				// set the randomaccess to the location of the cursor
				randomAccess.setPosition(cursor);

				// move one pixel back in dimension d
				randomAccess.bck(d);

				// get the value
				double Back = randomAccess.get().getRealDouble();

				// move twice forward in dimension d, i.e.
				// one pixel above the location of the cursor
				randomAccess.fwd(d);
				randomAccess.fwd(d);

				// get the value
				double Fwd = randomAccess.get().getRealDouble();

				gradient += ((Fwd - Back) * (Fwd - Back)) / 4;

			}

			cursor.get().setReal(Math.sqrt(gradient));

		}

		return gradientimg;
	}
	
	public static <T extends RealType<T> & NativeType<T>> RandomAccessibleInterval<T> AutoThreshold(RandomAccessibleInterval<T> inputimg){
		
		final T type = inputimg.randomAccess().get().createVariable();
		RandomAccessibleInterval<T> Threshimg = new ArrayImgFactory<T>().create(inputimg,
				type);
		//Supress values below the low threshold
		int n = inputimg.numDimensions();
		double[] position = new double[n];
				final double Lowthreshold = GlobalThresholding.AutomaticThresholdingSec(inputimg);
				 double threshold = Lowthreshold;
				Cursor<T> inputcursor = Views.iterable(inputimg).localizingCursor();
				RandomAccess<T> outputran = Threshimg.randomAccess();
				while(inputcursor.hasNext()){
					inputcursor.fwd();
					inputcursor.localize(position);
					outputran.setPosition(inputcursor);
					if (inputcursor.get().getRealDouble()<= threshold)
						outputran.get().setZero();
					else
						outputran.get().set(inputcursor.get());
				}
			return Threshimg;	
				
				
	}
	
	
}
