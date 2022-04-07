# Sensors

#### Temperature/Humidity Honeywell HIH8120

Making use of a the HIH61XX.h library we use this to call out temperature and humidity. Since we make use of everything the chip is using there is no further setup required. However it is possible that the measurements you're getting are incorrect which can be caused by wrong use of pins or intference of the sensor. (for example dust on the white paper)

#### 9DoF stick sensor

The 9DoF should be using a different serial address but you can change this on the chip if it requires changing. We made use of the SparkFunLSM9DS1.h library. However we've no need for anything other than the accelerometer so disabling anything involving gyroscope and magnetometer is adviced. We then proceed to calculate pitch and roll to know the orientation of the device.
Creating a shock sensor of the accelerometer can prove more difficult but depending on mostly the Y-axis value you can determine if this shock is big enough or not.
We combined a tilt switch that sends an interrupt on a big shock and the accelerometer to verify if is was a damaging shock.

#### GPS

This device measures longitude and lattitude. TinyGPS.h library is very easy to use and simplistic that requires little to no memory. Make sure the sensor has signal as it may not be able to work inside of building with thick walls. The smartbox and car casing should not be a problem for this however.

# LoRaWAN

# Android

# Database

# PCB

