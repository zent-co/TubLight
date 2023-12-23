# Tub Light System
This Tub Light system allows the user to change the color of the light in a bathtub through an Android App.

## Project Motivation and Description
I developed the Tub Light system after taking apart the light that came with our jetted tub to replace the lightbulb. 

The original light was simply an incandesecnt bulb with an on/off switch in a semi-water tight box. If you wanted a different color other than warm white, then you use a plastic color filter over the light port to acheive that color. The tub only came with blue and red filters, which was pretty limiting, and it was somewhat pricy to buy the remaining five color filters.

I started to imagine how I could improve this the tub light using a [Adafruit NeoPixel Jewel RGB LED assembly](https://www.adafruit.com/product/2859?gad_source=1&gclid=CjwKCAiAp5qsBhAPEiwAP0qeJkzRK5o67oNMlqqDC8S1sDyIZCz12_C3Y6A9JUmND2r-IASDWLSKcRoCs48QAvD_BwE) to acheive any color and brightness I wanted. The NeoPixel Jewel is controlled by a [Sparkfun ESP32 Thing Plus](https://www.sparkfun.com/products/15663?gclid=CjwKCAiAp5qsBhAPEiwAP0qeJnRRSU4OE6ZlrzAAaxC8sL1j2-f2hIqKbaGOr5SEi_CV_tvKprVjEhoCCmUQAvD_BwE)

#### Original Color Selection Demo Video Link and Tub Light System Demo Video Link:
[![Original Tub Light Color Selection](https://i9.ytimg.com/vi/9z2hBdIpH5c/mqdefault.jpg?sqp=CNzenKwG-oaymwEmCMACELQB8quKqQMa8AEB-AH-CYAC0AWKAgwIABABGGUgUSg8MA8%3D&rs=AOn4CLBb1GusxnwH_KhOwGTQBZzyc4McBg&retry=4)](https://www.youtube.com/watch?v=9z2hBdIpH5c "Original Tub Light Color Filter") [![Tub Light System Color Selection](https://i9.ytimg.com/vi/xVnbhTjhsxw/mqdefault.jpg?sqp=CODlnKwG-oaymwEmCMACELQB8quKqQMa8AEB-AH-CYAC0AWKAgwIABABGEEgGih_MA8=&rs=AOn4CLAyAq2NqwPwrnZ8UrE_IMCg9RHzuA)](https://www.youtube.com/watch?v=xVnbhTjhsxw "Tub Light System Demo")

## Technical Overview
The following sections describe the technical details of the Tub Light system.

### Hardware


### Firmware

#### Initialization:

```cpp
Adafruit_NeoPixel strip = Adafruit_NeoPixel(NUM_LEDS, PIN, NEO_GRBW + NEO_KHZ800);

AsyncWebServer server(80);

WiFiUDP Udp;
```

The light state information is initialized. The initial state is for red, green, and blue LEDs all on full power, which turns the light on to white for a first power on. Additionally, various states are initialized as false and the light timeout timer is set to zero for the updated time and time since last interaction. 

```cpp
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
```

Set the IP address to 192.168.1.25 to keep the app in sync (unfortunately, local IP address is hard coded into app)
```cpp
IPAddress local_IP(192,168,1,25);
IPAddress gateway(192, 168, 1, 1);
IPAddress subnet(255, 255, 255, 0);
IPAddress primaryDNS(8, 8, 8, 8);   //optional
IPAddress secondaryDNS(8, 8, 4, 4); //optional
```
#### Setup:

Setup the interrupt pin so that the air pressure switch drives an interrupt when the button is pressed:
```cpp
    pinMode(INTERRUPT_PIN, INPUT_PULLUP);
    attachInterrupt(INTERRUPT_PIN, isr, CHANGE);
```

Connect to WiFi given credentials.

Start the WebServer to provide initial status message when app connects to the Webserver

Start the UDP listener

Initialize lights by starting the strip NeoPixel object and setting the color to the sepcified red, green, and blue values. 

Start ArduinoOTA service so that FW updates can be done wirelessly since access to the hardware is difficult. 

```cpp
    startWifi();
    startWebServer();

    Udp.begin(localPortNum);

    initializeLights();

    setupArduinoOTA();
```
#### Loop:

### Android App


## Future Feature Development List
- Slow Color Change
- Candle/Fire Flicker
