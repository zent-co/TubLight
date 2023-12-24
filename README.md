# Tub Light System
This Tub Light system allows the user to change the color of the light in a bathtub through an Android App.

## Project Motivation and Description
I developed the Tub Light system after taking apart the light that came with our jetted tub to replace the lightbulb. 

The original light was simply an incandesecnt bulb with an on/off switch in a semi-water tight box. If you wanted a different color other than warm white, then you use a plastic color filter over the light port to acheive that color. The tub only came with blue and red filters, which was pretty limiting, and it was somewhat pricy to buy the remaining five color filters.

I imagined how I could improve this the tub light using a [Adafruit NeoPixel Jewel RGB LED assembly](https://www.adafruit.com/product/2859?gad_source=1&gclid=CjwKCAiAp5qsBhAPEiwAP0qeJkzRK5o67oNMlqqDC8S1sDyIZCz12_C3Y6A9JUmND2r-IASDWLSKcRoCs48QAvD_BwE) to acheive any color and brightness I wanted. The NeoPixel Jewel is controlled by a [Sparkfun ESP32 Thing Plus](https://www.sparkfun.com/products/15663?gclid=CjwKCAiAp5qsBhAPEiwAP0qeJnRRSU4OE6ZlrzAAaxC8sL1j2-f2hIqKbaGOr5SEi_CV_tvKprVjEhoCCmUQAvD_BwE)

#### Original Color Selection Demo Video Link and Tub Light System Demo Video Link:
[![Original Tub Light Color Selection](https://i9.ytimg.com/vi/9z2hBdIpH5c/mqdefault.jpg?sqp=CNzenKwG-oaymwEmCMACELQB8quKqQMa8AEB-AH-CYAC0AWKAgwIABABGGUgUSg8MA8%3D&rs=AOn4CLBb1GusxnwH_KhOwGTQBZzyc4McBg&retry=4)](https://www.youtube.com/watch?v=9z2hBdIpH5c "Original Tub Light Color Filter") [![Tub Light System Color Selection](https://i9.ytimg.com/vi/xVnbhTjhsxw/mqdefault.jpg?sqp=CODlnKwG-oaymwEmCMACELQB8quKqQMa8AEB-AH-CYAC0AWKAgwIABABGEEgGih_MA8=&rs=AOn4CLAyAq2NqwPwrnZ8UrE_IMCg9RHzuA)](https://www.youtube.com/watch?v=xVnbhTjhsxw "Tub Light System Demo")

## Technical Overview
The following sections describe the technical details of the Tub Light system.

### Hardware


### Firmware
- Connects to household WiFi, for local network use only.
- 4 hour inactivity auto light shut off.
- Physical button turns light on and off via interrupt service routine.
- The light can also be turned on and off via WiFi commands.
- Uses a WebServer to establish initial connection with App and reports light state to App.
- Uses a UDP port to receive on/off and color change messages.
- UDP messages are sent via JSON formatted text. The firmware must deserialize the JSON message to set the proper light color.
- Firmware Updatable via FOTA from Arduino IDE.
  
### Android App
- Based on MVC architecture
- Turn light on or off with power button at top.
- Select from any color on the color wheel.
- If color is selected from the color wheel, then it can be dimmed with the slider bar.
- Our favorite colors are set as favorites with the button the color the light will be.
- Retrieves the status of the light at startup from the WebServer running on the ESP32, sets power on/off appropriately.
- Sends commands via UDP to the ESP32 to set the light color, brightness, or to turn the light on/off.
  
#### Screenshots:
| Initial Screen | Light Dimmed |
| -------------- | ------------ |
| ![Initial Screen](https://drive.google.com/file/d/1TLAa2hbJHlJc1kMml1bSH-LhY9FykrJv/view?usp=drive_link)  | $250    |

[![Initial Screen](https://drive.google.com/file/d/1TLAa2hbJHlJc1kMml1bSH-LhY9FykrJv/view?usp=drive_link)]
## Future Feature Development List
- Slow Color Change
- Candle/Fire Flicker
- User based favorite color list
