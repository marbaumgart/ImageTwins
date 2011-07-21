import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.border.EtchedBorder;

public class ImageTwins {

    /**
     * Runnable Main method
     * 
     * @param args
     */
    public static void main(String[] args) {
	new ImageTwins();
    }

    private JFrame frame;
    private JSlider toleranceSlider;
    private JLabel firstImgLabel;
    private JLabel secondImgLabel;
    private BufferedImage firstImg;
    private BufferedImage secondImg;
    private BufferedImage firstImgOrig;
    private BufferedImage secondImgOrig;

    /**
     * Standard Constructor
     */
    public ImageTwins() {
	// init GUI
	frame = new JFrame(this.getClass().getName());

	// top panel for controls
	JPanel controlPane = new JPanel();
	controlPane.setLayout(new BoxLayout(controlPane, BoxLayout.X_AXIS));

	// button to open images
	JButton openImageButton = new JButton("Zwei Bilder …ffnen");
	openImageButton.addActionListener(new ActionListener() {
	    public void actionPerformed(ActionEvent e) {
		JFileChooser fc1 = new JFileChooser();
		int choice1 = fc1.showOpenDialog(frame);
		JFileChooser fc2 = new JFileChooser();
		int choice2 = fc2.showOpenDialog(frame);
		if (choice1 == JFileChooser.APPROVE_OPTION
			&& choice2 == JFileChooser.APPROVE_OPTION) {
		    File file1 = fc1.getSelectedFile();
		    File file2 = fc2.getSelectedFile();
		    firstImg = firstImgOrig = openImage(file1);
		    secondImg = secondImgOrig = openImage(file2);
		    firstImgLabel.setIcon(new ImageIcon(firstImg));
		    secondImgLabel.setIcon(new ImageIcon(secondImg));
		    frame.pack();
		}
	    }
	});
	controlPane.add(openImageButton);

	// slider to set the tolerance
	toleranceSlider = new JSlider(JSlider.HORIZONTAL, 0, 250, 0);
	toleranceSlider.setMajorTickSpacing(50);
	toleranceSlider.setMinorTickSpacing(10);
	toleranceSlider.setPaintTicks(true);
	toleranceSlider.setPaintLabels(true);
	controlPane.add(toleranceSlider);

	// button to compare images
	JButton compareImageButton = new JButton("Vergleichen");
	compareImageButton.addActionListener(new ActionListener() {
	    public void actionPerformed(ActionEvent e) {
		if (firstImgOrig.getHeight(null) == secondImgOrig.getHeight(null)
			&& firstImgOrig.getWidth(null) == secondImgOrig.getWidth(null)) {
		    compareImages(toleranceSlider.getValue());
		} else {
		    JOptionPane.showMessageDialog(frame, "Die Bildgrš§en stimmen nicht Ÿberein.",
			    "Achtung", JOptionPane.WARNING_MESSAGE);
		}
	    }
	});
	controlPane.add(compareImageButton);

	// center panel for both images
	JPanel imagePane = new JPanel();
	imagePane.setLayout(new BoxLayout(imagePane, BoxLayout.X_AXIS));

	firstImgLabel = new JLabel();
	firstImgLabel.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
	imagePane.add(firstImgLabel);

	secondImgLabel = new JLabel();
	secondImgLabel.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
	imagePane.add(secondImgLabel);

	frame.getContentPane().add(controlPane, BorderLayout.PAGE_START);
	frame.getContentPane().add(imagePane, BorderLayout.CENTER);

	// finish GUI
	frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	frame.pack();
	frame.setResizable(false);
	frame.setVisible(true);
    }

    /**
     * Returns a BufferedImage read from the given path or null. TODO: File
     * types
     * 
     * @param path
     * @return BufferedImage
     */
    private BufferedImage openImage(File file) {
	BufferedImage image = null;
	if (file.isFile() && file.canRead()) {
	    try {
		image = ImageIO.read(file);
	    } catch (Exception e) {
		e.printStackTrace();
	    }
	}
	return image;
    }

    /**
     * Compares both images depending on the tolerance and draws differences red
     * into both images.
     * 
     * @param tolerance
     */
    private void compareImages(int tolerance) {
	
	int width = firstImgOrig.getWidth(null);
	int height = firstImgOrig.getHeight(null);
	
	for (int x = 0; x < width; x++) {
	    for (int y = 0; y < height; y++) {
		// get pixel
		int firstRGB = firstImgOrig.getRGB(x, y);
		int secondRGB = secondImgOrig.getRGB(x, y);
		
		// split into values
		int fr = firstRGB & 0xFF0000 >> 16;
	    	int fg = firstRGB & 0x00FF00 >> 8;
	    	int fb = firstRGB & 0x0000FF;
	    	int sr = secondRGB & 0xFF0000 >> 16;
	    	int sg = secondRGB & 0x00FF00 >> 8;
	    	int sb = secondRGB & 0x0000FF;
		
		// testing, not final algorithm
		if (firstRGB != secondRGB) {
		    secondImg.setRGB(x, y, 0xFF00FF00);
		}
	    }
	}
	
	// update GUI
	secondImgLabel.setIcon(new ImageIcon(secondImg));
    }
}
