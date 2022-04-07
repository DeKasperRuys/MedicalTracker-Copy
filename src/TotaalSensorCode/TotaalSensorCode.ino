// GPS
#include <TinyGPS.h>
//long   lat,lon; // create variable for latitude and longitude object
float lat = 51.2297026;
float lon = 4.4160227;
float lati, loni = 0.00;
bool canSend = false;
bool didShock = false;
TinyGPS gps; // create gps object
// TILT
const byte pin = 2;
volatile byte counter = 0;
//9 DOF
#include <SparkFunLSM9DS1.h>
LSM9DS1 imu;
int sendSpeed = 500; //2x per seconde
bool tempCritical, shockCritical, orientCritical = false;
static unsigned long lastPrint = 0; // Keep track of print time
// TEMP/HUM
#include "Adafruit_Si7021.h"
Adafruit_Si7021 sensor = Adafruit_Si7021();
// Communication
/*
   pins: D2-D7 and D8-D9
   A0 sends interrupt
*/

const int valuesToSend = 7;
const byte bytes = 4;
byte res[bytes];


void setup() {
  // put your setup code here, to run once:
  //COMM
  DDRD |= B11111100;
  DDRB |= B00000011;
  DDRC |= B00000001;
  clearRegisters();

  // GPS
  Serial.begin(9600); // connect serial
  //Serial.println("The GPS Received Signal:");
  // TILT
  
  PCICR &= 0;
  PCICR |= (1 << PCIF0);    // enable pin change interrupt on any pcint7:0
  PCMSK0 |= (1 << PCINT4);  // enable pin change on pcint0 (D12)
  
  // TEMP/HUM
  
  if (!sensor.begin()) {
    Serial.println("Did not find Si7021 sensor!");
    while (true)
      ;
  }

  Serial.print("Found model ");
  switch(sensor.getModel()) {
    case SI_Engineering_Samples:
      Serial.print("SI engineering samples"); break;
    case SI_7013:
      Serial.print("Si7013"); break;
    case SI_7020:
      Serial.print("Si7020"); break;
    case SI_7021:
      Serial.print("Si7021"); break;
    case SI_UNKNOWN:
    default:
      Serial.print("Unknown");
  }
  Serial.print(" Rev(");
  Serial.print(sensor.getRevision());
  Serial.print(")");
  Serial.print(" Serial #"); Serial.print(sensor.sernum_a, HEX); Serial.println(sensor.sernum_b, HEX);

  // 9DOF
  if (!imu.begin())
  {
    Serial.println("Failed to communicate with 9dof");
    while (1)
      ;
  }

  delay(5000);
}
//float lati, longi;
// PRINT GPS IF FOUND
void PrintSensorData(){
  //while(Serial.available() && canSend == true){ // check for gps data
    if(gps.encode(Serial.read()))// encode gps data
    { 
      gps.f_get_position(&lat,&lon); // get latitude and longitude

      
      Serial.print("Position: ");
      //lati = lat;
      //loni = lon;
      //Latitude
      Serial.print("Latitude: ");
      //Serial.print(lat,6);
      
      Serial.print(",");
      
      //Longitude
      Serial.print("Longitude: ");
      //Serial.println(lon,6); 

      //CheckTempHum();                   // moet hierbinnen!! anders kan gps niet uitgelezen worden!
      CheckForShock();
      // 9DOF
      
    }
  //}
}
// TILT
void CheckForShock(){
  //Serial.println(counter);
  if (counter >= 100){
    //Serial.println("There was a shock!");
    didShock = true;
    counter = 0;
  } else {
    //Serial.println("There was no shock to speak off.");
    didShock = false;
    counter = 0;
  }
  delay(20000);         // time to wait for tx
}

// TILT ISR
ISR(PCINT0_vect){
  counter++;
  Serial.println("Shock!");
}

// TEMP/HUM
void CheckTempHum(){
  Serial.print("Humidity:    ");
  Serial.print(sensor.readHumidity(), 2);
  Serial.print("\tTemperature: ");
  Serial.println(sensor.readTemperature(), 2);
}
// 9DOF
void printAttitude(float ax, float ay, float az)
{
  //Bereken pitch en roll van het device 
  float roll = atan2(ay, az);
  float pitch = atan2(-ax, sqrt(ay * ay + az * az));
  pitch *= 180.0 / PI;
  roll  *= 180.0 / PI;
  
  orientationChecker(pitch, roll);
}

void orientationChecker(float pitch, float roll){
  if(pitch <=30 && pitch >= -30 && roll <=30 && roll >= -30){
    orientCritical = false;
  }else{
    Serial.println("CRITICAL!!!");
    orientCritical = true;
  } 
}

// COM
void sendData(float data1, float data2, float data3, float data4, float data5, float data6, float id) {
  float valueArray[valuesToSend] = { data1, data2, data3, data4, data5, data6, id };

  for (int i = 0; i < valuesToSend; i++) {
    splitFloat(valueArray[i], i + 1);
    for (byte i = 0; i < bytes; i++) sendRegisters(i);
  }
}

void sendRegisters(byte i) {
  clearRegisters();
  PORTD |= res[i] << 2;
  PORTB |= res[i] >> 6;
  PORTC |= 1;

  _delay_ms(100);
  
}

void splitFloat(float num, int id) {
  if (num > 255) num = 255;
  if (num < -255) num = -255;

  bool positive = num >= 0 ? true : false;
  int factor = positive ? 1 : -1;
  int intnum = (int)num * factor;
  int decimalen = (int)((long)(num * factor * 10000) % 10000);
  id = id > 15 ? 4 : id;              // default to 4 numbers, incase of an overflow

  res[0] = 0x70;                      // setup bits
  res[0] |= !positive ? 0x80 : 0x0;   // indicate negative number
  res[0] |= id;                       // id of current number
  res[1] = intnum;
  res[2] = decimalen / 100;
  res[3] = decimalen % 100;

}

void clearRegisters() {
  PORTD &= 0x3;
  PORTB &= 0xFC;
  PORTC &= ~0x1;
}

void loop() {
  // put your main code here, to run repeatedly:
  sendData(didShock, sensor.readTemperature(), sensor.readHumidity(), (lat), lon, orientCritical, 88);
  canSend = true;
  clearRegisters();
  //delay(0000);

  PrintSensorData();
  CheckForShock();
  if ((lastPrint + sendSpeed) < millis())
      {
        imu.readAccel(); 
        printAttitude(imu.ax, imu.ay, imu.az);
        lastPrint = millis();
      }
  
}
