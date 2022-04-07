#include "Adafruit_Si7021.h"
Adafruit_Si7021 sensor = Adafruit_Si7021();

static unsigned long lastPrint = 0;
void setup() {
  //Serial.begin(115200);
  
}
void loop() {
  //Elke 5seconden..
  if((lastPrint + 5000) < millis()){
  float currentHumi = sensor.readHumidity();
  float currentTemp = sensor.readTemperature();
  /*Serial.print("Humidity:    ");
  Serial.print(currentHumi);
  Serial.print("\tTemperature: ");
  Serial.println(sensor.readTemperature(), 2);*/
    lastPrint = millis();
  }
  //delay(1000);
}
