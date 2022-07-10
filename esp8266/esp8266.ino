#include <Arduino.h> // for PlatformIO
#include <ESP8266WiFi.h>

const char* WIFI_SSID = "Name_of_your_WiFi";
const char* WIFI_PASSWORD = "Your_password";
const String HOST = "192.168.0.211"; // server IP address
const int PORT = 6880; // port you choosed in WebServer.java
// Serial speed: 74880 bauds



bool _isConnectedToWifi = false;
uint32_t _lastOpenStatus = 0;

bool isCarNearBarrier() { // add code for your sensors here
  return false;
}

void openBarrier() {
  Serial.print("Opening barrier... ");

  digitalWrite(LED_BUILTIN, LOW);

  uint32_t waitStartedAt = millis();
  while (millis() - waitStartedAt < 1000) { // wait until barrier will be fully opened (change it if you have a sensor for that)
    delay(10);
  }
  Serial.println("Barrier opened");
}

void closeBarrier() {
  if (isCarNearBarrier()) return;

  Serial.print("Closing barrier... ");

  digitalWrite(LED_BUILTIN, HIGH);

  uint32_t waitStartedAt = millis();
  while (millis() - waitStartedAt < 1000) { // wait until barrier will be fully closed (change it if you have a sensor for that)
    if (isCarNearBarrier()) { // will stop the loop if the car is near the barrier and open it again
      openBarrier();
      return;
    }
    delay(10);
  }
  Serial.println("Barrier closed");
}

void workWithString(String result) {
  bool isOpenStatusReceieved = result.indexOf("Open") != -1;
  // Serial.println(result);

  if (isOpenStatusReceieved) {
    _lastOpenStatus = millis();
  }

  if (isOpenStatusReceieved or _lastOpenStatus + 5000 > millis()) { // if car is near barier or last status was received less than 5 seconds ago (for connection issues)
    openBarrier();
  }
  else {
    closeBarrier();
  }
}

void checkWiFiChanges() {
  if (1) {
  wl_status_t  lastWiFiStatus = WiFi.status();
    switch (lastWiFiStatus) {
      case WL_CONNECTED: { Serial.println("WL_CONNECTED"); return; }
      case WL_IDLE_STATUS: { Serial.println("WL_IDLE_STATUS (in process of changing between statuses)"); return; }
      case WL_DISCONNECTED: { Serial.println("WL_DISCONNECTED"); return; }
      case WL_CONNECTION_LOST: { Serial.println("WL_CONNECTION_LOST"); return; }
      case WL_CONNECT_FAILED: { Serial.println("WL_CONNECT_FAILED"); return; }
      case WL_NO_SSID_AVAIL: { Serial.println("WL_NO_SSID_AVAIL"); return; }
      case WL_SCAN_COMPLETED: { Serial.println("WL_SCAN_COMPLETED"); return; }
      case WL_NO_SHIELD: { Serial.println("WL_NO_SHIELD"); return; }
      default: { Serial.println("Unknown"); return; }
    }
  }
}

void setup() {
  pinMode(LED_BUILTIN, OUTPUT);
  digitalWrite(LED_BUILTIN, HIGH);
  Serial.begin(74880);

  Serial.print("\nConnecting to network ");
  Serial.print(WIFI_SSID);
  WiFi.mode(WIFI_STA); // switching to client mode
  WiFi.begin(WIFI_SSID, WIFI_PASSWORD); // connecting to wifi network
}

void loop() {
  if ((WiFi.status() == WL_CONNECTED)) {
    if (!_isConnectedToWifi) {
      Serial.println(" Connected!");
      _isConnectedToWifi = true;
    }

    WiFiClient client;

    if (!client.connect(HOST, PORT)) { // connect to server
      Serial.println("Connection to server failed");
      client.stop();
      delay(500);
      return;
    }

    Serial.print("Getting data... ");
    if (client.connected()) {
      client.print("hello from ESP8266");
    }
    delay(500);

    // wait for data to be available
    unsigned long timeout = millis();
    while (client.available() == 0) {
      if (millis() - timeout > 2000) {
        Serial.println("Client Timeout!");
        client.stop();
        delay(500);
        return;
      }
    }

    String result = "";
    while (client.available()) { // getting the result
      result += client.readStringUntil('\n');
      yield();
    }
    workWithString(result);

    client.stop();
    delay(500);
  }
  else if (_isConnectedToWifi) {
    Serial.print("Disconnected, waiting for WiFi connection");
    _isConnectedToWifi = false;
  }
  else {
    Serial.print(".");
    // checkWiFiChanges();
    delay(1000);
  }
}
