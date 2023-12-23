#include <Adafruit_NeoPixel.h>
#ifdef __AVR__
#include <avr/power.h>
#endif
 
#include <Arduino.h>
#include "AsyncUDP.h"
#include <ArduinoJson.h>
#ifdef ESP32
#include <WiFi.h>
#include <AsyncTCP.h>
#elif defined(ESP8266)
#include <ESP8266WiFi.h>
#include <ESPAsyncTCP.h>
#endif
#include <ESPmDNS.h>
#include <WiFiUdp.h>
#include <ArduinoOTA.h>
#include <ESPAsyncWebServer.h>

#define PIN 17
#define INTERRUPT_PIN 33
#define NUM_LEDS 2
#define DATA_OFFSET     10
#define POWER_OFF_TIMEOUT 14400
#define MS_MULTIPLIER 1000

const char* ssid = "SSID";
const char* password = "PW";

Adafruit_NeoPixel strip = Adafruit_NeoPixel(NUM_LEDS, PIN, NEO_GRBW + NEO_KHZ800);

AsyncWebServer server(80);

WiFiUDP Udp;

//UDP Port
unsigned int localPortNum = 1234;

//UDP Tx and Rx Buffers
char packetBuffer[256]; //buffer to hold incoming packet
char ReplyBuffer[] = "acknowledged";       // a string to send back

//Used to decode JSON message from App via UDP
DynamicJsonDocument doc(1024);

//light state information
int r = 255;
int g = 255;
int b = 255;
int w = 0;
boolean powerIsrCalled = false;
boolean togglePowerHandleCalled = false;
boolean light_state = false;
unsigned long old_time = 0;
unsigned long new_time = 0;

enum toggleLightReason {
    TIMER_TIMEOUT,
    INTERRUPT_CALLED,
    HANDLE_CALLED
};

enum toggleLightReason toggleLightReason;

IPAddress local_IP(192,168,1,25);
IPAddress gateway(192, 168, 1, 1);
IPAddress subnet(255, 255, 255, 0);
IPAddress primaryDNS(8, 8, 8, 8);   //optional
IPAddress secondaryDNS(8, 8, 4, 4); //optional

void notFound(AsyncWebServerRequest *request) {
    request->send(404, "text/plain", "Not found");
}

void setup() {

    Serial.begin(115200);

    pinMode(INTERRUPT_PIN, INPUT_PULLUP);
    attachInterrupt(INTERRUPT_PIN, isr, CHANGE);

     //Configures static IP address
    if (!WiFi.config(local_IP, gateway, subnet, primaryDNS, secondaryDNS)) {
      Serial.println("STA Failed to configure");
    }

    startWifi();
    startWebServer();

    Udp.begin(localPortNum);

    initializeLights();

    setupArduinoOTA();
}

void loop() {
    ArduinoOTA.handle();

    int packetSize = Udp.parsePacket();

    if(packetSize){
        setColorViaUdp(packetSize);
    }

    if (powerIsrCalled) {
        toggleLightReason = INTERRUPT_CALLED;
        togglePower();
        powerIsrCalled = false;
    }

    //Serial.println(String(new_time-old_time));

    if ((new_time - old_time) > POWER_OFF_TIMEOUT * MS_MULTIPLIER) {
        toggleLightReason = TIMER_TIMEOUT;
        togglePower();
    }
    else {
        //update new time if timer hasn't expired
        new_time = millis();
    }
}


void isr() {
    powerIsrCalled = true;
}

void startWifi() {
    WiFi.mode(WIFI_STA);
    WiFi.begin(ssid, password);
    while (WiFi.waitForConnectResult() != WL_CONNECTED) {
        Serial.println("Connection Failed! Rebooting...");
        delay(5000);
        ESP.restart();
    }
}

void startWebServer() {
    server.on("/", HTTP_GET, [](AsyncWebServerRequest *request){
        request->send(200, "text/plain", "hello, you have reached the root. Roots reggae!");
    });

    // Send a GET request to <IP>/get?message=<message>
    server.on("/getState", HTTP_GET, [] (AsyncWebServerRequest *request) {
        String message = "File Found:\n\n";
        message += "state:" + String(light_state) + "\n";
        message += "r:" + String(r) + "\n";
        message += "g:" + String(g) + "\n";
        message += "b:" + String(b) + "\n";
        message += "w:" + String(w) + "\n";
    
        Serial.println(message);
        
        request->send(200, "text/plain", "Hello, GET: " + message);
    });

    server.onNotFound(notFound);

    server.begin();

    Serial.println("HTTP server started");
}

void handleMode() {
    //String mode = server.arg(3); //third arg is the mode type. 

    //server.send(200, "text/html", "handleMode");
}

void setColorViaUdp(int packetSize){
      Serial.print("Received packet of size ");
      Serial.println(packetSize);
      Serial.print("From ");
      IPAddress remoteIp = Udp.remoteIP();
      Serial.print(remoteIp);
      Serial.print(", port ");
      Serial.println(Udp.remotePort());

      // read the UDP packet into packetBufffer
      int len = Udp.read(packetBuffer, 255);
      if (len > 0) {
        packetBuffer[len] = 0;
      }
  
      Serial.println("Contents:");
      Serial.println(packetBuffer);
  
      // send a reply, to the IP address and port that sent us the packet we received

      const char message[] = "ack";
  
      Udp.beginPacket(Udp.remoteIP(), Udp.remotePort());
      Udp.write((const uint8_t*) message, sizeof(message));
      Udp.endPacket();

      DeserializationError error = deserializeJson(doc, packetBuffer);

      if(error){
        Serial.print("deserializationJson() failed with code: ");
        Serial.println(error.c_str());
        return;
      }
        
      const char* command = doc["cmd"];
      Serial.print("Command received: ");
      Serial.println(command);

      String commandString = String(command);
      Serial.println(commandString);

      const char* response = "ack";

      if(commandString == "togglePower"){
        togglePowerHandle();
        
      } else if (commandString == "color"){
        Serial.println("Change Color");

        r = doc["r"];
        g = doc["g"];
        b = doc["b"];

        setColor();
      } else {
        Serial.println("command not recognized");
      }
}

void getState() {
    String message = "File Found:\n\n";
    message += "state:" + String(light_state) + "\n";
    message += "r:" + String(r) + "\n";
    message += "g:" + String(g) + "\n";
    message += "b:" + String(b) + "\n";
    message += "w:" + String(w) + "\n";

    Serial.println(message);

    //server.send(200, "text/html", message);
}

String togglePowerHandle() {
    toggleLightReason = HANDLE_CALLED;
    return togglePower();

    Serial.println("toggle power");
}

void setColor() {
    Serial.println("r: "+String(r)+"g: "+String(g)+"b: "+String(b)+"w: "+String(w));
    setStripColor(r, g, b, 0);
}

String togglePower() {
    static unsigned long last_isr_call = 0;
    unsigned long interrupt_time = millis();

    //interrupt_time - last_isr_call is for debouncing the switch to turn lights on and off. If interrupt time is more than 100ms since last isr call, then act on interrupt. 

    Serial.println(light_state);

    if (interrupt_time - last_isr_call > 100 ||
        toggleLightReason == HANDLE_CALLED ||
        toggleLightReason == INTERRUPT_CALLED) {
        if (light_state) {
            //then turn off    
            setStripColor(0, 0, 0, 0);
            return "light turned off";
        }
        else if (toggleLightReason == TIMER_TIMEOUT) {
            setStripColor(0, 0, 0, 0);
            return "light turned off";
        }
        else {
            setStripColor(r, g, b, 0);
            return "light turned on";
        }
    }
    else {
        return String(toggleLightReason);
    }

    //sets last isr call to this interrupt time.
    last_isr_call = interrupt_time;
}

void handleRoot() {
    //server.send(200, "text/html", "hello, you have reached the root. Roots reggae!");
}

void initializeLights() {
    strip.begin();
    //strip.show();     // Initialize all pixels to 'off'

    setStripColor(r, g, b, 0);
}

void setStripColor(int r, int g, int b, int w) {

    if (r == 0 && g == 0 && b == 0 && w == 0) {
        setLightTimerAndLightState(false);
    }
    else {
        setLightTimerAndLightState(true);
    }

    for (uint16_t i = 0; i < strip.numPixels(); i++) {
        strip.setPixelColor(i, strip.Color(r, g, b, 0));
    }
    strip.show();
}

void setLightTimerAndLightState(boolean lightStateSwitch) {
    light_state = lightStateSwitch;

    Serial.println("light state switch: "+String(light_state));
    
    if (lightStateSwitch) {
        old_time = millis();
        new_time = millis();
    }
    else {
        old_time = 0;
        new_time = 0;
    }
}

void setupArduinoOTA() {
    ArduinoOTA
        .onStart([]() {
        String type;
        if (ArduinoOTA.getCommand() == U_FLASH)
            type = "sketch";
        else // U_SPIFFS
            type = "filesystem";

        // NOTE: if updating SPIFFS this would be the place to unmount SPIFFS using SPIFFS.end()
        Serial.println("Start updating " + type);
            })
        .onEnd([]() {
                Serial.println("\nEnd");
            })
                .onProgress([](unsigned int progress, unsigned int total) {
                Serial.printf("Progress: %u%%\r", (progress / (total / 100)));
                    })
                .onError([](ota_error_t error) {
                        Serial.printf("Error[%u]: ", error);
                        if (error == OTA_AUTH_ERROR) Serial.println("Auth Failed");
                        else if (error == OTA_BEGIN_ERROR) Serial.println("Begin Failed");
                        else if (error == OTA_CONNECT_ERROR) Serial.println("Connect Failed");
                        else if (error == OTA_RECEIVE_ERROR) Serial.println("Receive Failed");
                        else if (error == OTA_END_ERROR) Serial.println("End Failed");
                    });

                    ArduinoOTA.begin();

                    Serial.println("Ready");
                    Serial.print("IP address: ");
                    Serial.println(WiFi.localIP());
}
