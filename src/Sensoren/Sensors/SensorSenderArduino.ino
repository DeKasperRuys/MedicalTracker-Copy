//can change
bool readyToSend = false;

//do not change
const byte bytes = 3;
byte res[bytes];
///BLEUTOOTH
String recString;
int recStringSize;
bool ready;
//BLUETOOTH
///GPS
#include <TinyGPS.h>
//long   lat,lon; // create variable for latitude and longitude object
float lat,lon;
TinyGPS gps; // create gps object

///GPS
///SENSOR
#include <Wire.h>
#include <SPI.h>
#include <SparkFunLSM9DS1.h>
#include <HIH61xx.h>
#include <AsyncDelay.h>
#include "Adafruit_Si7021.h"

HIH61xx<TwoWire> hih(Wire);

AsyncDelay samplingInterval;
LSM9DS1 imu;
Adafruit_Si7021 sensor = Adafruit_Si7021();
int sendSpeed = 500; //2x per seconde
bool tempCritical, shockCritical, orientCritical = false;
float humiditymax, humiditymin, tempmin, tempmax;
static unsigned long lastPrint = 0; // Keep track of print time
bool printed = true;
void printAccel(); 
void printAttitude(float ax, float ay, float az, float mx, float my, float mz);
float temperatuur;
float humidity; 
const byte pinTilt = 2;
byte counter = 0;
///SENSOR

void setup() {
  DDRD |= B11111111;
  DDRC |= B00000001;
  clearRegisters();

  readyToSend = true; //enable when needed

  ///SENSOR
pinMode(pinTilt, INPUT);
attachInterrupt(digitalPinToInterrupt(pinTilt), ISR_Tilt, RISING);

#if F_CPU >= 12000000UL
    Serial.begin(115200);
#else
  Serial.begin(9600);
#endif
  //Ingesteld via Bluetooth waarden
  humiditymax=80;
  humiditymin=20;
  tempmin=15;
  tempmax=30;
  Serial.begin(115200);
  Wire.begin();
  imu.settings.device.commInterface = IMU_MODE_I2C;
  if (!imu.begin())
  {
    Serial.println("Failed to communicate with 9dof");
    while (1)
      ;
  }
  hih.initialise();
  samplingInterval.start(1000, AsyncDelay::MILLIS);
  ///SENSOR
}

void loop() {
  
  //_delay_ms(10000);
///GPS
if(gps.encode(Serial.read()))// encode gps data
    { 
    gps.f_get_position(&lat,&lon); // get latitude and longitude

    Serial.print("Position: ");
    
    //Latitude
    Serial.print("Latitude: ");
    Serial.print(lat,6);
    
    Serial.print(",");
    
    //Longitude
    Serial.print("Longitude: ");
    Serial.println(lon,6); 
    
   }
///GPS
  
///BLUETOOTH
while (Serial.available()) {
    ready = true;
    recString += Serial.readString();
  }

  if (recString.length() > 0) {
    recStringSize = recString.length();
    int amount = 4;
    String del = ",";
    
    String str[amount];
    int startIndex = 0;
    int lastIndex = recString.indexOf(del, startIndex);
    for (int i = 0; i < amount; i++) {
      i != amount - 1 ? str[i] = recString.substring(startIndex, lastIndex) : str[i] = recString.substring(startIndex);
      startIndex = lastIndex + 1;
      lastIndex = recString.indexOf(del, startIndex);
    }
    
    String minTemp = str[0];
    String maxTemp = str[1];
    String minHum = str[2];
    String maxHum = str[3];

    //use data here
    Serial.println(minTemp);
    Serial.println(maxTemp);
    Serial.println(minHum);
    Serial.println(maxHum);
    
    recString = "";
  }
  if (ready) ready != ready;
///BLUETOOTH
  //SENSOR
if (samplingInterval.isExpired() && !hih.isSampling()) {
    hih.start();
    printed = false;
    samplingInterval.repeat();
    
  }
  hih.process();

  if (hih.isFinished() && !printed) {
    imu.readAccel(); 
    printAttitude(imu.ax, imu.ay, imu.az);
    Serial.println();
    lastPrint = millis(); // Update lastPrint time
    printed = true;
  }
  //SENSOR
if (readyToSend) {
    sendData(temperatuur);
    sendData(humidity);
    sendData(orientCritical);//boolean
    
    readyToSend = false;
  }

}

void clearRegisters() {
  PORTD &= 0x0;
  PORTC &= ~0x1;
}

void sendData(float num) {
  splitFloat(num);
  for (byte i = 0; i < bytes; i++) {
    clearRegisters();
    PORTC |= 1;
    PORTD |= res[i];
    _delay_ms(25);
    PORTC &= ~1;
  }
}

void splitFloat(float num) {
  res[0] = num > 1 ? (int)num : 0x0;
  word decimalen = (word)((num - res[0]) * 10000);
  for (byte i = 1; i < sizeof(res); i++) res[i] = decimalen >> (i - 1) * 8;
}


///SENSORS
void printAttitude(float ax, float ay, float az)
{
  //SEND DATA MASTER SLAVE
  //9DOF WERKT SOLO
  //SI7021 WIL NIET VERSTUREN
 // Wire.beginTransmission(8);
   //Serial.write(temperatuur);
 // Wire.write(byte(sensor.readTemperature()));
 // Wire.endTransmission();

  //ORIENTATIE
  //Bereken pitch en roll van het device
  

  //SET CRITICAL VALUES
  
  float roll = atan2(ay, az);
  float pitch = atan2(-ax, sqrt(ay * ay + az * az));
  pitch *= 180.0 / PI;
  roll  *= 180.0 / PI;

    tempChecker();
    humidityChecker();
    orientationChecker(pitch, roll);
    
    shockChecker(ax,ay,az);
  //PITCH AND ROLL VOOR ORIENTATIE
  //
}

void tempChecker()
{
  temperatuur = sensor.readTemperature();
  //Serial.println(temperatuur);
  if(temperatuur >=tempmax || temperatuur <=tempmin)
  {
   // Serial.println("temperatuur is to high");
    tempCritical = true;
  }
}

void humidityChecker()
{
  humidity = sensor.readHumidity();
  //Serial.print(humidity);
  if(humidity >=humiditymax || humidity <=humiditymin)
  {
    //Serial.println("humidity is to high");
    tempCritical = true;
  }
}

void orientationChecker(float pitch, float roll){

  if(pitch <=30 && pitch >= -30 && roll <=30 && roll >= -30){
    //ALLES IN ORDE
    //DEZE code veranderen naar enkele if voor wanneer het buiten deze waardes gaat
    Serial.print("Pitch, Roll: ");
    Serial.print(pitch, 2);
    Serial.print(", ");
    Serial.println(roll, 2);
  }
  else{
    Serial.println("CRITICAL!!!");
    orientCritical = true;
    /*Serial.print("Pitch, Roll: ");
    Serial.print(pitch, 2);
    Serial.print(", ");
    Serial.println(roll, 2);*/
  } 
}
void ISR_Tilt(){
  counter++;
  Serial.println("Shock!");
}

void CheckForShock(){
  Serial.println(counter);
  if (counter >= 1){
    Serial.println("There was a shock!");
    counter = 0;
  } else {
    Serial.println("There was no shock to speak off.");
  }
}

void shockChecker(float xgraph, float ygraph, float zgraph){
  if(ygraph>4000 && zgraph>20000 && counter >= 1)
  {
    Serial.println("Big Shock!");
    shockCritical = true;
  }
  if(ygraph<250 && zgraph<14000  && counter >= 1)
  {
    Serial.println("Big Shock!");
    shockCritical = true;
  }
}
///SENSORS
