# Barrier auto opener | Автоматический открывальщик шлагбаума

## Description (English)

This project makes possible to use computer vision to recognize license plates of passing cars and automatically open the barrier after finding a known license plate number.

A camera connected to a computer (e.g. a webcam) or an IP camera with MJPEG or RTSP protocol can be used. The esp8266 is chosen as the control device for interaction with the barrier, because it is able to connect to the recognizing computer remotely via Wi-Fi. To use the project with other microcontrollers via USB modify the WebServer class.

## Description (Русский)

Этот проект позволяет использовать возможности компьютерного зрения для распознавания номеров проезжающих автомобилей и автоматического открытия шлагбаума при нахождении известного номера.

В качестве камеры может быть использована камера, подключённая к компьютеру напрямую (например вебкамера) или IP-камера с протоколом MJPEG или RTSP. В качестве управляющего устройства для взаимодействия с шлагбаумом выбрана esp8266, потому что она умеет подключаться к распознавающему компьютеру удалённо по Wi-Fi. Для использования проекта с другими микроконтроллерами по USB измените класс WebServer.

## How to run

Tested with:

* ultimateALPR-SDK [v3.8.4](https://github.com/DoubangoTelecom/ultimateALPR-SDK/releases/tag/v3.8.4);

* JSON-java [20220320](https://github.com/stleary/JSON-java/releases/tag/20220320);

* Windows 11 amd64: OpenJDK Runtime Environment [Temurin-17.0.2+8](https://github.com/adoptium/temurin17-binaries/releases/tag/jdk-17.0.4%2B8), OpenCV [v4.6.0](https://sourceforge.net/projects/opencvlibrary/files/4.6.0/opencv-4.6.0-vc14_vc15.exe/download?use_mirror=altushost-swe&use_mirror=altushost-swe&r=https%3A%2F%2Fopencv.org%2Freleases%2F);

* Debian 11 amd64: OpenJDK Runtime Environment build 17.0.3+7-Debian-1deb11u1, OpenCV v4.5. Also tested with Ubuntu 22.04 amd64, almost no difference;

* Raspberry OS arm64: OpenJDK Runtime Environment build 17.0.3+8-Debian-1deb11u1, OpenCV v4.5.

To run it:

0. Install Java:
	* for Windows (amd64), [get package](https://adoptium.net/temurin/releases/) and install it;
	* for Debian 11 (amd64) / Ubuntu 22.04 (amd64) / Raspberry Pi OS (arm64), write in terminal:
	```
	sudo apt install openjdk-17-jdk
	```

1. Download the repository files and unzip the project anywhere you like.

2. Download [JSON-java](https://github.com/stleary/JSON-java) files. Next, move the files from `JSON-java/src/main/java/org/json/` to `./src/org/json/` folder.

3. Download [ultimateALPR-SDK](https://github.com/DoubangoTelecom/ultimateALPR-SDK) files:
	* move the files from `ultimateALPR-SDK/java/org/doubango/ultimateAlpr/Sdk/` to `./src/org/doubango/ultimateAlpr/Sdk/` folder;
	* move the files from `ultimateALPR-SDK/assets/` to `./lib/ultimatealpr_assets/` folder
	* for Windows (amd64), move the files from `ultimateALPR-SDK/binaries/windows/x86_64/` to `./lib/windows_amd64/ultimatealpr/` folder. Add this path (e.g. `C:\Coding\BarrierAutoOpener\lib\windows_amd64\ultimatealpr`) to the user PATH variable (unfortunately, I could not run the program without this). You should restart PC to use it;
	* for Linux (amd64), move the files from `ultimateALPR-SDK/binaries/linux/x86_64/` to `./lib/linux_amd64/ultimatealpr/` folder;
	* for Linux (arm64), move the files from `ultimateALPR-SDK/binaries/linux/aarch64/` to `./lib/linux_arm64/ultimatealpr/` folder.

4. Download OpenCV 4:
	* for Windows (amd64), [get package](https://opencv.org/releases/) and unzip it, move `opencv_videoio_ffmpegyourversion_64.dll` from `opencv/build/bin/`, `opencv-yourversion.jar` from `opencv/build/java/` and `opencv_javayourversion.dll` from `opencv/build/java/x64/` to `./lib/windows_amd64/opencv4/` folder;
	* for Debian 11 (amd64) / Ubuntu 22.04 (amd64) / Raspberry Pi OS (arm64), write in terminal:
	```
	sudo apt install libopencv-dev libopencv4.5-java
	```

5. Download [Tensorflow library](https://github.com/DoubangoTelecom/ultimateALPR-SDK/blob/41231cb9fe49e5d26d32f5fe89e701971bb60637/samples/c%2B%2B/README.md#tensorflow-libraries) and copy it to `ultimatealpr` folder (it's not needed for Raspberry Pi and other arm64 systems).

6. Change config file (or do it later). You can also enable or disable the DEBUG option to display the image window - find it in the CameraConnection.java file. To improve the performance of the system, a pause of 200 ms occurs after each processed frame - it can be removed (App.java file).

7. Run it from VS Code or terminal:
	* in VS Code, just select your OS and architecture in Build and run (CTRL+SHIFT+D) and Run code (Ctrl+F5). You may have to change the `launch.json' file;
	* from terminal:
		* on Windows:
		```bat
		REM  to compile:
		javac -cp "./src;./lib/windows_amd64/opencv4/opencv-yourversion.jar" -d bin ./src/App.java

		REM  to run:
		java "-Djava.library.path=lib/windows_amd64/opencv4;%PATH%" --add-opens=java.base/java.nio=ALL-UNNAMED --add-opens=java.base/sun.nio.ch=ALL-UNNAMED -XX:+ShowCodeDetailsInExceptionMessages -cp "%cd%/bin;%cd%/lib/windows_amd64/opencv4/opencv-yourversion.jar" App
		```
		* on Debian 11 (amd64) / Ubuntu 22.04 (amd64):
		```bash
		#  to compile:
		javac -cp "./src:/usr/share/java/opencv4/opencv-yourversion.jar" -d bin ./src/App.java

		#  to run:
		java -Djava.library.path=lib/linux_amd64/ultimatealpr:/usr/lib/jni:/usr/lib/x86_64-linux-gnu --add-opens=java.base/java.nio=ALL-UNNAMED --add-opens=java.base/sun.nio.ch=ALL-UNNAMED -XX:+ShowCodeDetailsInExceptionMessages -cp "$(pwd)/bin:/usr/share/java/opencv4/opencv-yourversion.jar" App
		```
		* on Raspberry Pi OS (arm64):
		```bash
		#  to compile:
		javac -cp "./src:/usr/share/java/opencv4/opencv-yourversion.jar" -d bin ./src/App.java

		#  to run:
		java -Djava.library.path=lib/linux_arm64/ultimatealpr:/usr/lib/jni --add-opens=java.base/java.nio=ALL-UNNAMED --add-opens=java.base/sun.nio.ch=ALL-UNNAMED -XX:+ShowCodeDetailsInExceptionMessages -cp "$(pwd)/bin:/usr/share/java/opencv4/opencv-yourversion.jar" App
		```

8. Flash your esp8266 with esp8266.ino file from Arduino IDE. PlatformIO is also available: just create a new project, move the contents of the `.ino` file to `main.cpp` and add the last lines of `platformio.ini` to your configuration file.


Troubleshooting:
* `no opencv_javayourversion in java.library.path`: find this in `CameraConnection.java` and change to name you can find in `/usr/lib/jni` (Linux) / `lib/windows_amd64/opencv4` (Windows). For example, set `"opencv_videoio"` if you found `libopencv_videoio.so` file;

* `no opencv_videoio_ffmpegyourversion_64 in java.library.path`: find this in `CameraConnection.java` and change `Core.NATIVE_LIBRARY_NAME` to name you can find in `/usr/lib/x86_64-linux-gnu` (for example, set `"opencv_java454d"` if you found `libopencv_java454d.so` file);

* `(1004) tryIoctl VIDEOIO(V4L2:/dev/video0): select() timeout` or picture is very glitchy:
	```bash
	sudo rmmod uvcvideo
	sudo modprobe uvcvideo nodrop=1 timeout=5000 quirks=0x80
	```

* Problems with `libopencv_java` or `libtensorflow.so.1`:
	```bash
	sudo nano /etc/ld.so.conf # add here /path/to/directory/with/lib (/usr/lib/jni for libopencv_java, /../lib/linux_amd64/ultimatealpr for libtensorflow)
	sudo /usr/sbin/ldconfig # apply changes
	sudo ldconfig -p | grep libopencv_java* # check libopencv_java
	```
	if problem with `libopencv_java` still exists, try to set `opencv_java<version>` instead of `Core.NATIVE_LIBRARY_NAME`

* If you have a problem with the OpenCL library and it does not come with the GPU drivers, you can add OpenCL.dll to `./lib/windows_amd64/ultimatealpr`.

For additional optimizations (such as OpenVINO), explore the [ultimateALPR](https://github.com/DoubangoTelecom/ultimateALPR-SDK) repository.