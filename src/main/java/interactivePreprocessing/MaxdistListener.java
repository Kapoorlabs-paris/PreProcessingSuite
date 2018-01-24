package interactivePreprocessing;

import java.awt.TextComponent;
import java.awt.event.TextEvent;
import java.awt.event.TextListener;

public class MaxdistListener implements TextListener  {
	
	
	final InteractiveMethods parent;
	
	public MaxdistListener(final InteractiveMethods parent) {
		
		
		this.parent = parent;
		
	}
	
	@Override
	public void textValueChanged(TextEvent e) {
		final TextComponent tc = (TextComponent)e.getSource();
	    String s = tc.getText();
	   
	    if (s.length() > 0)
		parent.DistMax = Integer.parseInt(s);
		
	}

}
