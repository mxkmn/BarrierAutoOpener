import barrierautoopener.Recognizer;
import barrierautoopener.WebServer;
import barrierautoopener.CameraConnection.*;

import java.io.BufferedReader;
import java.io.FileReader;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import org.json.*;
import org.opencv.core.Mat;

public class App {
	static private CameraConnection _cam;
	static private Recognizer _anpr;
	static private WebServer _server;
	static private Mat _previousMat = null;
	static private List<String> plates = Arrays.asList();

	public static void main(String[] args) throws Exception {
		try(BufferedReader br = new BufferedReader(new FileReader("config.txt"))) {
			for (int i = 0; i < 2; i++) {
				String[] configArgs = br.readLine().split(" ");
				if (i == 0) { // first line with camera info
					if (configArgs[0].contentEquals("LOCAL")) {
						_cam = new UsbCameraConnection();
					} else if (configArgs[0].contentEquals("RTSP")) {
						_cam = new RstpCameraConnection(configArgs[1]);
					} else if (configArgs[0].contentEquals("MJPEG") && configArgs.length == 2) {
						_cam = new MjpegCameraConnection(configArgs[1]);
					} else if (configArgs[0].contentEquals("MJPEG") && configArgs.length == 4) {
						_cam = new MjpegCameraConnection(configArgs[1], configArgs[2], configArgs[3]);
					} else {
						if (configArgs[0].contentEquals("MJPEG")) {
							throw new Exception("Problems with MJPEG args");
						} else {
							throw new Exception("Unknown camera type: " + configArgs[0]);
						}
					}
				} else { // second line with plates
					plates = Arrays.asList(configArgs);
				}
			}
		}
		new Thread(_cam).start();

		_server = new WebServer();
		new Thread(_server).start();

		_anpr = new Recognizer();

		while (true) {
			String jsonString = getJsonInfoFromImage();
			// System.out.println(jsonString); // uncomment to see the json string
			try {
				JSONArray foundsArray = new JSONObject(jsonString).getJSONArray("plates");
				for (int i = 0; i < foundsArray.length(); i++) {
					JSONObject found = foundsArray.getJSONObject(i);
					try {
						if (plates.contains(found.getString("text"))) {
							LocalDateTime _lastFoundDateTime = LocalDateTime.now();
							System.out.println("Found " + found.getString("text") + " at " + _lastFoundDateTime);
							_server.setLastFoundDateTime(_lastFoundDateTime);
							break;
						}
					} catch (Exception e) { }
				}
			} catch (Exception e) { }

			try {
				Thread.sleep(200); // wait for 200ms before next frame
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	private static String getJsonInfoFromImage() {
		Mat mat = _cam.showAndGetLastImage();
		if (mat == null || mat == _previousMat)
			return "";

		try {
			_previousMat = mat;
			return(_anpr.recognizeDataOnImage(mat));
		} catch (Exception e) {
			e.printStackTrace();
		}

		return "";
	}
}
