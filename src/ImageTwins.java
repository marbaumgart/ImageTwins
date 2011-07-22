import java.awt.BorderLayout;
import java.awt.Color;
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
    private JSlider alphaSlider;
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
		    firstImg = openImage(file1);firstImgOrig = openImage(file1);
		    secondImg = openImage(file2);secondImgOrig = openImage(file2);
		    firstImgLabel.setIcon(new ImageIcon(firstImgOrig));
		    secondImgLabel.setIcon(new ImageIcon(secondImgOrig));
		    frame.pack();
		}
	    }
	});
	controlPane.add(openImageButton);

	// slider to set the tolerance
	toleranceSlider = new JSlider(JSlider.HORIZONTAL, 0, 255, 0);
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
		int r1 = firstRGB & 0xFF0000 >> 16;
		int g1 = firstRGB & 0x00FF00 >> 8;
		int b1 = firstRGB & 0x0000FF;
		int r2 = secondRGB & 0xFF0000 >> 16;
		int g2 = secondRGB & 0x00FF00 >> 8;
		int b2 = secondRGB & 0x0000FF;

		// convert to lab
		int[] lab1 = rgbToLab(r1, g1, b1);
		int[] lab2 = rgbToLab(r2, g2, b2);

		// compare
		int result = compareLab(lab1, lab2);

		// tolerance
		if (result > tolerance) {
		    secondImg.setRGB(x, y, 0xFF00FF00);
		} else {
		    secondImg.setRGB(x, y, secondRGB);
		}
	    }
	}
	
	// update GUI
	secondImgLabel.setIcon(new ImageIcon(secondImg));
    }

    /**
     * Compares two LAB colors with the CIE76 Algorithm.
     * @param lab1
     * @param lab2
     * @return
     */
    private int compareLab(int[] lab1, int[] lab2) {

	int result = (int) Math.sqrt((lab2[0] - lab1[0]) * (lab2[0] - lab1[0]) + (lab2[1] - lab1[1])
		* (lab2[1] - lab1[1]) + (lab2[2] - lab1[2]) * (lab2[2] - lab1[2]));

	return result;
    }

    /**
     * http://www.f4.fhtw-berlin.de/~barthel/ImageJ/ColorInspector//HTMLHelp/
     * farbraumJava.htm
     * 
     * @param R
     * @param G
     * @param B
     * @return int array with L, a, b
     */
    private int[] rgbToLab(int R, int G, int B) {
	float r, g, b, X, Y, Z, fx, fy, fz, xr, yr, zr;
	float Ls, as, bs;
	float eps = 216.f / 24389.f;
	float k = 24389.f / 27.f;

	float Xr = 0.964221f; // reference white D50
	float Yr = 1.0f;
	float Zr = 0.825211f;

	// RGB to XYZ
	r = R / 255.f; // R 0..1
	g = G / 255.f; // G 0..1
	b = B / 255.f; // B 0..1

	// assuming sRGB (D65)
	if (r <= 0.04045)
	    r = r / 12;
	else
	    r = (float) Math.pow((r + 0.055) / 1.055, 2.4);

	if (g <= 0.04045)
	    g = g / 12;
	else
	    g = (float) Math.pow((g + 0.055) / 1.055, 2.4);

	if (b <= 0.04045)
	    b = b / 12;
	else
	    b = (float) Math.pow((b + 0.055) / 1.055, 2.4);

	X = 0.436052025f * r + 0.385081593f * g + 0.143087414f * b;
	Y = 0.222491598f * r + 0.71688606f * g + 0.060621486f * b;
	Z = 0.013929122f * r + 0.097097002f * g + 0.71418547f * b;

	// XYZ to Lab
	xr = X / Xr;
	yr = Y / Yr;
	zr = Z / Zr;

	if (xr > eps)
	    fx = (float) Math.pow(xr, 1 / 3.);
	else
	    fx = (float) ((k * xr + 16.) / 116.);

	if (yr > eps)
	    fy = (float) Math.pow(yr, 1 / 3.);
	else
	    fy = (float) ((k * yr + 16.) / 116.);

	if (zr > eps)
	    fz = (float) Math.pow(zr, 1 / 3.);
	else
	    fz = (float) ((k * zr + 16.) / 116);

	Ls = (116 * fy) - 16;
	as = 500 * (fx - fy);
	bs = 200 * (fy - fz);

	int[] lab = new int[3];

	lab[0] = (int) (2.55 * Ls + .5);
	lab[1] = (int) (as + .5);
	lab[2] = (int) (bs + .5);

	return lab;
    }
}
