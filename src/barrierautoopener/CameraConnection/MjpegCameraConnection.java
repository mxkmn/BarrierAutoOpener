package barrierautoopener.CameraConnection;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.Authenticator;
import java.net.PasswordAuthentication;
import java.net.URL;
import java.time.LocalDateTime;

import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.imgcodecs.Imgcodecs;

public class MjpegCameraConnection extends CameraConnection {
	private final byte FIRST_JPEG_MAGIC_NUMBER = (byte) 0xFF;
	private final byte SECOND_JPEG_MAGIC_NUMBER_START_FILE = (byte) 0xD8;
	private final byte SECOND_JPEG_MAGIC_NUMBER_END_FILE = (byte) 0xD9;

	private enum State {
		FILE_END_FOUND, FILE_START_FOUND
	}

	private URL _url;
	private BufferedInputStream _in;
	private ByteArrayOutputStream _out;
	private byte[] _byteBuffer = new byte[1]; // reading byte by byte
	private boolean _previousByteIsFirstJpegMagicNumber = false;
	private State _stateInfo = State.FILE_END_FOUND;

	public MjpegCameraConnection(String cameraUri) throws IOException {
		this(cameraUri, null, null);
	}

	public MjpegCameraConnection(String cameraUri, String login, String password) throws IOException {
		if (login != null && password != null) {
			Authenticator.setDefault(new HTTPAuthenticator(login, password));
		}

		_url = new URL(cameraUri);
		_in = new BufferedInputStream(_url.openStream());
		_out = new ByteArrayOutputStream();

		openWindow();
	}

	public void run() {
		while (true) {
			try {
				if (_in.read(_byteBuffer) > -1) { // if data provided
					if (_stateInfo == State.FILE_START_FOUND) // if start found
						_out.write(_byteBuffer);

					if (_previousByteIsFirstJpegMagicNumber) {
						switch (_byteBuffer[0]) {
						case SECOND_JPEG_MAGIC_NUMBER_START_FILE:
							_stateInfo = State.FILE_START_FOUND;
							_out = new ByteArrayOutputStream(10000);
							_out.write(FIRST_JPEG_MAGIC_NUMBER);
							_out.write(SECOND_JPEG_MAGIC_NUMBER_START_FILE);
							break;
						case SECOND_JPEG_MAGIC_NUMBER_END_FILE:
							if (_stateInfo == State.FILE_START_FOUND) {
								if (DEBUG) System.out.println("Image found at " + LocalDateTime.now() + "!");
								_stateInfo = State.FILE_END_FOUND;
								_out.close();

								// try (FileOutputStream fos = new FileOutputStream("0.jpg")) {
								// 	fos.write(_out.toByteArray());
								// 	System.out.println("Saved to 0.jpg");
								// 	System.out.println("");
								// }

								Mat lastImage = Imgcodecs.imdecode(new MatOfByte(_out.toByteArray()), Imgcodecs.IMREAD_ANYCOLOR);
								saveImage(lastImage);
							}
							break;
						}
					}
					_previousByteIsFirstJpegMagicNumber = _byteBuffer[0] == FIRST_JPEG_MAGIC_NUMBER;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	static class HTTPAuthenticator extends Authenticator {
		private String login, password;

		public HTTPAuthenticator(String login, String password) {
			this.login = login;
			this.password = password;
		}

		protected PasswordAuthentication getPasswordAuthentication() {
			System.out.println("Requesting Host  : " + getRequestingHost());
			System.out.println("Requesting Port  : " + getRequestingPort());
			System.out.println("Requesting Prompt : " + getRequestingPrompt());
			System.out.println("Requesting Protocol: " + getRequestingProtocol());
			System.out.println("Requesting Scheme : " + getRequestingScheme());
			System.out.println("Requesting Site  : " + getRequestingSite());
			return new PasswordAuthentication(login, password.toCharArray());
		}
	}
}