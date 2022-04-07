
// PITCH EN ROLL VOOR ORIENTATIE
//GYRO OF ACCEL VOOR SCHOKKEN TESTEN OP WEG

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

void printAccel(); 

void printAttitude(float ax, float ay, float az, float mx, float my, float mz);

void setup() 
{
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
}


bool printed = true;
void loop()
{
 
  
  if ((lastPrint + sendSpeed) < millis())
  {
    
  }
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
}

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
//&    shockChecker(ax,ay,az);
  //PITCH AND ROLL VOOR ORIENTATIE
  //
}

void tempChecker()
{
  float temperatuur = sensor.readTemperature();
  //Serial.println(temperatuur);
  if(temperatuur >=tempmax || temperatuur <=tempmin)
  {
   // Serial.println("temperatuur is to high");
    tempCritical = true;
  }
}

void humidityChecker()
{
  float humidity = sensor.readHumidity();
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

//Toevoegen aan tiltswitch. Als tiltswitch aan gaat EN de accelerometer waardes zijn hoog
//DAN is er een schok
/*
void shockChecker(float xgraph, float ygraph, float zgraph){
  if(ygraph>4000 && zgraph>20000)
  {
    Serial.println("Big Shock!");
    shockCritical = true;
  }
  if(ygraph<250 && zgraph<14000)
  {
    Serial.println("Big Shock!");
    shockCritical = true;
  }
}
*/


