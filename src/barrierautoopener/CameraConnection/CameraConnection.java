package barrierautoopener.CameraConnection;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import javax.swing.JFrame;
import org.opencv.core.Core;
import org.opencv.core.Mat;

public abstract class CameraConnection extends Thread {
	protected final boolean DEBUG = true; // show image in window

	private CameraJFrame _cameraFrame = new CameraJFrame();
	private Mat _lastImage;
	protected LocalDateTime _lastImageFoundAt;

	public CameraConnection() {
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		if (System.getProperty("os.name").contains("Windows")) {
			System.loadLibrary("opencv_videoio_ffmpeg460_64");
		} else { // linux
			System.loadLibrary("opencv_videoio"); // comment this on arm64
		}
	}

	protected void openWindow() {
		if (DEBUG) {
			JFrame frame = new JFrame();
			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			frame.add(_cameraFrame); // generates one null pointer exception without mat, calm down
			frame.setSize(800, 800);
			frame.setVisible(true);
		}
	}

	protected void saveImage(Mat mat) {
		_lastImage = mat;
		_lastImageFoundAt = LocalDateTime.now();
	}

	public Mat showAndGetLastImage() {
		if (_lastImageFoundAt == null || ChronoUnit.SECONDS.between(_lastImageFoundAt, LocalDateTime.now()) > 2 || _lastImage == null)
			return null;

		if (DEBUG) { // show image
			_cameraFrame.showMat(_lastImage.clone());
		}

		return _lastImage;
	}
}
