/*
   pins: D4 - D7 && A0 - A3
   D8 receives interrupt
*/

// could disable all other interrupts with PCMSK0 after data was sent
// (to disable LoRa) and only enable after data was received

const byte bytes = 4;             // aantal bytes per number
const byte numbersToReceive = 5;
volatile byte res[bytes];         // bytes that contain info about a number
volatile float factor = 1;          // assigns positive or negative value to a number
volatile int count = 0;           // counts interrupts
volatile bool rest = false;       // assigns values to res[1-3]
volatile byte numbersReceived;    // counts received numbers
float values[10];                 // saves the values of the received numbers

volatile int tel;

void setup() {
  Serial.begin(9600);

  PCICR &= 0;
  PCICR |= (1 << PCIF0);    // enable pin change interrupt on any pcint7:0
  PCMSK0 |= (1 << PCINT0);  // enable pin change on pcint0 (D8)
}

void loop() {
  delay(1000);
  Serial.print("nums received = ");
  Serial.println(numbersReceived);

  // do stuff here
  if (numbersReceived >= 6) {

    Serial.println("disabling interrupts");
    cli(); // disable global interrupts when processing data

    // save the numbers here
    for (int i = 0; i < 10; i++) {
      Serial.print("Num ");
      Serial.print(i);
      Serial.print(" is ");
      Serial.println(values[i], 4);
    }

    for (int i = 0; i < 4; i++) res[i] = 0;
    sei(); // enable global interrupts when done processing data
    numbersReceived = 0;
    count = 0;
  }
}

ISR (PCINT0_vect) {
  //tel++;

  byte port = PINB;
  if (PINB & 0x1) {
    count++;
    byte readbyte = ((PINC & B00001111) << 4) | ((PIND & B11110000) >> 4);
    // ((readbyte > 99) && (readbyte & 0x70)) ? res[0] = readbyte, count = 1 : res[++count / 2] = readbyte);
    if ((readbyte > 99) && (readbyte & 0x70) && count == 1) {
      res[0] = readbyte;
    } else {
      res[count / 2] = readbyte;
      if (count > 7) count = 0;
    }

    if (count == 7) {
      numbersReceived++;
      factor = (res[0] & 0x80) ? -1 : 1;          // decide new values factor
      values[(int)(res[0] & 0x0F)] = ((float)res[1] + (float)((res[2] * 100 + res[3])) / 10000) * factor; // calculate value
    }
  }
}

String getBinary(int num) {
  String temp = String(num, BIN);
  while (temp.length() < 8) {
    temp = "0" + temp;
  }
  return temp;
}
