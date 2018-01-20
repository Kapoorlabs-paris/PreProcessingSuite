package userTESTING;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Label;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;

import ij.ImagePlus;
import interactivePreprocessing.InteractiveMethods;
import interactivePreprocessing.PreCurrentMovieListener;
import interactivePreprocessing.PreUploadMovieListener;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.img.display.imagej.ImageJFunctions;
import net.imglib2.type.numeric.real.FloatType;


public class PreprocessingFileChooser extends JPanel {

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public boolean wasDone = false;
	public boolean isFinished = false;
	public JButton TrackMeasure;
	public JButton Done;
	public JFileChooser chooserA;
	public String choosertitleA;
	public ImagePlus impA;
	public JFileChooser chooserB;
	public String choosertitleB;
	  public JFrame Cardframe = new JFrame("Welcome to Computer Vision Segmentation Tools (Covisto)");
	  public JPanel panelCont = new JPanel();
	  public JPanel panelFirst = new JPanel();
	  
	  public Label ImageType = new Label("Image format supported : XYZT/XYZ/XYT");
	  public JPanel Panelfile = new JPanel();
	  public static final Insets insets = new Insets(10, 0, 0, 0);
	  public final GridBagLayout layout = new GridBagLayout();
	  public final GridBagConstraints c = new GridBagConstraints();
	  public Border selectfile = new CompoundBorder(new TitledBorder("Select file"), new EmptyBorder(c.insets));
	
	public PreprocessingFileChooser () {
		
		
		

		   panelFirst.setLayout(layout);
		   Panelfile.setLayout(layout);
			CardLayout cl = new CardLayout();
			
			panelCont.setLayout(cl);
			panelCont.add(panelFirst, "1");
		    JButton Measureserial = new JButton("Load movie");
			
			JButton Current = new JButton("Use Current movie");
			
			
			 Panelfile.add( ImageType, new GridBagConstraints(0, 0, 1, 1, 0.0D, 0.0D, 17, 
				      2, insets, 0, 1));
			 
			
		    Panelfile.add(Measureserial, new GridBagConstraints(0, 1, 1, 1, 0.0D, 0.0D, 17, 
		      2, insets, 0, 1));
		    
		    Panelfile.add(Current, new GridBagConstraints(1, 1, 1, 1, 0.0D, 0.0D, 17, 
				      2, insets, 0, 0));
		    
		    Panelfile.setBorder(selectfile);
		 
		    panelFirst.add(Panelfile, new GridBagConstraints(0, 0, 3, 1, 0.0D, 0.0D, 17, 
		      -1, new Insets(10, 10, 0, 10), 0, 0));
		    
		    
		    

		
		
	

		Measureserial.addActionListener(new PreUploadMovieListener(this));
		Current.addActionListener(new PreCurrentMovieListener(this));

		
		panelFirst.setVisible(true);
		Cardframe.addWindowListener(new FrameListener(Cardframe));
		Cardframe.add(panelCont, BorderLayout.CENTER);
		Cardframe.pack();
		Cardframe.setVisible(true);

	}

	protected class FrameListener extends WindowAdapter {
		final Frame parent;

		public FrameListener(Frame parent) {
			super();
			this.parent = parent;
		}

		@Override
		public void windowClosing(WindowEvent e) {
			close(parent);
		}
	}


	
	public void Done(Frame parent){
		
		// Tracking and Measurement is done with imageA 
        
	    
		RandomAccessibleInterval<FloatType> image = ImageJFunctions.convertFloat(impA);
		
		new InteractiveMethods(image, chooserA.getSelectedFile()).run(null);
		close(parent);

		
	}
	
	
	public void DoneCurr(Frame parent){
		
		// Tracking and Measurement is done with imageA 
        
	    
		RandomAccessibleInterval<FloatType> image = ImageJFunctions.convertFloat(impA);
		
		
		new InteractiveMethods(image).run(null);
		close(parent);
		if(impA!=null)
        impA.close();
		
	}


	



	protected final void close(final Frame parent) {
		if (parent != null)
			parent.dispose();

		isFinished = true;
	}

	public Dimension getPreferredSize() {
		return new Dimension(500, 300);
	}
	
	
	
	
}