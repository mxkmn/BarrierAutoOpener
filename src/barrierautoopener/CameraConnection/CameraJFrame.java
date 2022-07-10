package barrierautoopener.CameraConnection;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import javax.swing.JPanel;

import org.opencv.core.Mat;

public class CameraJFrame extends JPanel {
	private Mat _mat;

	public void paintComponent(Graphics g) {
		super.paintComponent(g);

		BufferedImage image = mat2BufferedImage(_mat);
		if (image != null)
			g.drawImage(image, 10, 10, image.getWidth(), image.getHeight(), null);
	}

	private BufferedImage mat2BufferedImage(Mat m) {
		int type = BufferedImage.TYPE_BYTE_GRAY;
		if (m == null)
			return null;

		if (m.channels() > 1) {
			type = BufferedImage.TYPE_3BYTE_BGR;
		}
		int bufferSize = m.channels() * m.cols() * m.rows();
		byte[] b = new byte[bufferSize];
		m.get(0, 0, b); // get all the pixels
		BufferedImage img = new BufferedImage(m.cols(), m.rows(), type);
		final byte[] targetPixels = ((DataBufferByte) img.getRaster().getDataBuffer()).getData();
		System.arraycopy(b, 0, targetPixels, 0, b.length);
		return img;
	}

	public void showMat(Mat m) {
		_mat = m;
		repaint();
	}
}