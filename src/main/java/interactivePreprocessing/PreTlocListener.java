package interactivePreprocessing;

import java.awt.TextComponent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.TextEvent;
import java.awt.event.TextListener;

import ij.IJ;
import interactivePreprocessing.InteractiveMethods.ValueChange;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.img.array.ArrayImgFactory;
import net.imglib2.type.logic.BitType;


public class PreTlocListener implements TextListener {
	
	
	final InteractiveMethods parent;
	
	boolean pressed;
	public PreTlocListener(final InteractiveMethods parent, final boolean pressed) {
		
		this.parent = parent;
		this.pressed = pressed;
		
	}
	
	@Override
	public void textValueChanged(TextEvent e) {
		final TextComponent tc = (TextComponent)e.getSource();
	   
		 tc.addKeyListener(new KeyListener(){
			 @Override
			    public void keyTyped(KeyEvent arg0) {
				   
			    }

			    @Override
			    public void keyReleased(KeyEvent arg0) {
			    	
			    	if (arg0.getKeyChar() == KeyEvent.VK_ENTER ) {
						
						
						pressed = false;
						
					}

			    }

			    @Override
			    public void keyPressed(KeyEvent arg0) {
			    	String s = tc.getText();
			    	if (arg0.getKeyChar() == KeyEvent.VK_ENTER&& !pressed) {
						pressed = true;
			    		if (parent.fourthDimension > parent.fourthDimensionSize) {
							IJ.log("Max frame number exceeded, moving to last frame instead");
							parent.fourthDimension = parent.fourthDimensionSize;
						} else
							parent.fourthDimension = Integer.parseInt(s);
			    		ShowView show = new ShowView(parent);
					show.shownewT();
					parent.timeText.setText("Current T = " + parent.fourthDimension);
					
					if(!parent.snakeinprogress)
					parent.updatePreview(ValueChange.FOURTHDIMmouse);
					
					
					 }
			    	parent.timeslider.setValue(utility.CovistoSlicer.computeScrollbarPositionFromValue(parent.fourthDimension, parent.fourthDimensionsliderInit, parent.fourthDimensionSize, parent.scrollbarSize));
					parent.timeslider.repaint();
					parent.timeslider.validate();

			    }
			});
	

	

}

}
