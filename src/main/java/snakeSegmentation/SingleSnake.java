package snakeSegmentation;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import javax.swing.JOptionPane;
import javax.swing.SwingWorker;

import interactivePreprocessing.InteractiveMethods;
import interactivePreprocessing.InteractiveMethods.ValueChange;
import utility.Roiobject;

public class SingleSnake extends SwingWorker<Void, Void> {
	
	final InteractiveMethods parent;

	public SingleSnake(final InteractiveMethods parent) {
		
		this.parent = parent;
		
	}
	
	@Override
	protected Void doInBackground() throws Exception {
		
		String uniqueID = Integer.toString(parent.thirdDimension) + Integer.toString(parent.fourthDimension);
		
		ArrayList<Roiobject> currentRoi = parent.ZTRois.get(uniqueID);	
		
		SnakeonView applysnake = new SnakeonView(parent, parent.CurrentView, currentRoi);
		applysnake.process();
		ArrayList<Roiobject> resultrois = applysnake.getResult();
		parent.ZTRois.put(uniqueID, resultrois);
		parent.updatePreview(ValueChange.SNAKE);
		return null;
	}
	

	@Override
	protected void done() {
		try {
			utility.ProgressBar.SetProgressBar(parent.jpb, "Done");
			get();
		} catch (ExecutionException | InterruptedException e) {
			e.printStackTrace();
		}

	}
	
}
