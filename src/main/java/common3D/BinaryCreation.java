package common3D;

import java.util.ArrayList;

import ij.gui.Roi;
import interactivePreprocessing.InteractiveMethods;
import net.imglib2.Cursor;
import net.imglib2.RandomAccess;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.type.NativeType;
import net.imglib2.type.logic.BitType;
import net.imglib2.type.numeric.RealType;
import net.imglib2.view.Views;
import utility.PreRoiobject;

public class BinaryCreation  {

	
	public static <T extends RealType<T> & NativeType<T>> void  CreateBinary(final InteractiveMethods parent, RandomAccessibleInterval<T> inputimg, RandomAccessibleInterval<BitType> outimg, final ArrayList<Roi> Rois, int z, int t) {

		Cursor<T> incursor = Views.iterable(inputimg).localizingCursor();
		RandomAccess<BitType> outran = outimg.randomAccess();

		while (incursor.hasNext()) {

			incursor.fwd();
			outran.setPosition(incursor);

			for (Roi currentroi : Rois) {

				if (currentroi.contains(incursor.getIntPosition(0), incursor.getIntPosition(1))) {

					outran.get().setOne();

				}

			}

		}
	}
}
