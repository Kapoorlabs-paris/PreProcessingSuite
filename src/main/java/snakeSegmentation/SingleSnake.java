package snakeSegmentation;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import javax.swing.JOptionPane;
import javax.swing.SwingWorker;

import interactivePreprocessing.InteractiveMethods;
import interactivePreprocessing.InteractiveMethods.ValueChange;
import net.imglib2.Interval;
import net.imglib2.util.Intervals;
import net.imglib2.view.Views;
import utility.PreRoiobject;

public class SingleSnake extends SwingWorker<Void, Void> {

	final InteractiveMethods parent;

	public SingleSnake(final InteractiveMethods parent) {

		this.parent = parent;

	}

	@Override
	protected Void doInBackground() throws Exception {
		parent.snakeinprogress = true;
		String uniqueID = Integer.toString(parent.thirdDimension) + Integer.toString(parent.fourthDimension);

		ArrayList<PreRoiobject> currentRoi = parent.ZTRois.get(uniqueID);
		// Expand the image by 10 pixels

		Interval spaceinterval = Intervals.createMinMax(new long[] { parent.CurrentView.min(0),
				parent.CurrentView.min(1), parent.CurrentView.max(0), parent.CurrentView.max(1) });
		Interval interval = Intervals.expand(spaceinterval, 10);
		parent.CurrentView = Views.interval(Views.extendBorder(parent.CurrentView), interval);
		
		
		SnakeonView applysnake = new SnakeonView(parent, parent.CurrentView, currentRoi);
		applysnake.process();
		ArrayList<PreRoiobject> resultrois = applysnake.getResult();
		
		
		
		parent.ZTRois.put(uniqueID, resultrois);
		parent.updatePreview(ValueChange.SNAKE);
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
