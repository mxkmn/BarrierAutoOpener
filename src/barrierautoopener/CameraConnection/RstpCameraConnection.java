package barrierautoopener.CameraConnection;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.TimeUnit;
import org.opencv.core.Mat;
import org.opencv.videoio.VideoCapture;
import org.opencv.videoio.VideoWriter;
import org.opencv.videoio.Videoio;

public class RstpCameraConnection extends CameraConnection {
	private String _cameraUri;
	private VideoCapture _camera;
	private Boolean _cameraWasOpened = null;

	public RstpCameraConnection(String cameraUri) {
		_cameraUri = cameraUri;

		connectToCamera();
		_camera.set(Videoio.CAP_PROP_BUFFERSIZE, 3); // store only the last 3 frames

		if (!_camera.isOpened()) {
			System.out.println("Video is not captured, check connection!");
		}

		openWindow();
	}

	private void connectToCamera() {
		if (_cameraUri.isEmpty()) { // webcam mode
			if (_camera != null)
				_camera.release();

			if (System.getProperty("os.name").contains("Windows")) {
				_camera = new VideoCapture(0);
			} else { // linux
				_camera = new VideoCapture(Videoio.CAP_V4L); // Videoio.CAP_V4L for Linux
				_camera.set(Videoio.CAP_PROP_FOURCC, VideoWriter.fourcc('M','J','P','G'));
			}
		} else {
			_camera = new VideoCapture(_cameraUri);
		}
	}

	public void run() {
		while (true) {
			Mat mat = new Mat();
			Boolean cameraIsOpened = _camera.read(mat);

			if (cameraIsOpened != _cameraWasOpened) {
				System.out.println(cameraIsOpened ? "Camera is opened" : "Video is not captured, waiting for reconnection...");
				_cameraWasOpened = cameraIsOpened;
			}

			if (cameraIsOpened == true) {
				saveImage(mat); // Imgcodecs.imdecode(mat, Imgcodecs.IMREAD_ANYCOLOR)
			} else {
				if (_lastImageFoundAt == null || ChronoUnit.SECONDS.between(_lastImageFoundAt, LocalDateTime.now()) > 2) {
					try {
						TimeUnit.MILLISECONDS.sleep(1000);
					} catch (InterruptedException e) { }
					connectToCamera(); // reconnect
				}
			}
		}
	}
}