package snakeSegmentation;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import javax.swing.JOptionPane;
import javax.swing.SwingWorker;

import interactivePreprocessing.InteractiveMethods;
import interactivePreprocessing.InteractiveMethods.ValueChange;
import net.imglib2.Interval;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.type.logic.BitType;
import net.imglib2.type.numeric.real.FloatType;
import net.imglib2.util.Intervals;
import net.imglib2.view.Views;
import utility.PreRoiobject;

public class TSnake extends SwingWorker<Void, Void> {

	final InteractiveMethods parent;

	public TSnake(final InteractiveMethods parent) {

		this.parent = parent;

	}

	@Override
	protected Void doInBackground() throws Exception {

		
	
		
		
		for (int t = parent.fourthDimensionsliderInit; t <= parent.fourthDimensionSize; ++t) {

			parent.fourthDimension = t;
			System.out.println(t);
			String uniqueID = Integer.toString(parent.thirdDimension) + Integer.toString(t);
			parent.CurrentView = utility.CovistoSlicer.getCurrentView(parent.originalimg, parent.thirdDimension,
					parent.thirdDimensionSize, t, parent.fourthDimensionSize);
			parent.updatePreview(ValueChange.FOURTHDIMmouse);

			ArrayList<PreRoiobject> currentRoi = parent.CurrentPreRoiobject;

			// Expand the image by 10 pixels

			Interval spaceinterval = Intervals.createMinMax(new long[] { parent.CurrentView.min(0),
					parent.CurrentView.min(1), parent.CurrentView.max(0), parent.CurrentView.max(1) });
			Interval interval = Intervals.expand(spaceinterval, 10);
			parent.CurrentView = Views.interval(Views.extendBorder(parent.CurrentView), interval);
			SnakeonView applysnake = new SnakeonView(parent, parent.CurrentView, currentRoi);
			applysnake.process();
			ArrayList<PreRoiobject> resultrois = applysnake.getResult();
			currentRoi.clear();
			parent.ZTRois.put(uniqueID, resultrois);

			parent.updatePreview(ValueChange.SNAKE);

		}

		return null;

	}

	@Override
	protected void done() {
		parent.snakeinprogress = false;
		
		try {
			utility.CovsitoProgressBar.CovistoSetProgressBar(parent.jpb, "Done");
			get();
		} catch (ExecutionException | InterruptedException e) {
			e.printStackTrace();
		}

	}

}
