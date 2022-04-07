/*
   pins: D4 - D7 && A0 - A3
   D8 receives interrupt
*/

const byte bytes = 4;    // aantal bytes per number
byte res[bytes];         // bytes that contain info about a number
int count = 0;           // counts interrupts
bool receiving;          // start processing data when an interrupt was received
byte numbersReceived;    // counts received numbers

const byte numbersToReceive = 6;         // numbers to receive
float values[numbersToReceive+1];        // saves the values of the received numbers

void setup() {
  Serial.begin(9600);

  PCICR &= 0;
  PCICR |= (1 << PCIF0);    // enable pin change interrupt on any pcint7:0
  PCMSK0 |= (1 << PCINT0);  // enable pin change on pcint0 (D8)
}

void loop() {
  if (receiving) {
    byte readbyte = ((PINC & B00001111) << 4) | ((PIND & B11110000) >> 4);
    bool setupbit = (readbyte >= 113 && readbyte <= 127) || (readbyte >= 241 && readbyte <= 255);

    if (setupbit && count == 0) res[count] = readbyte;
    else if (count > 0) res[count] = readbyte;
    else Serial.println("error");

    if (count >= 3) {
      // decide values factor
      float factor = (res[0] & 0x80) ? -1 : 1;

      // calculate value
      values[(int)(res[0] & 0x0F)] = ((float)res[1] + (float)((res[2] * 100 + res[3])) / 10000) * factor;

      // done sending bytes, clear everything just incase
      for (int i = 0; i < 4; i++) res[i] = 0;

      count = 0;
      numbersReceived++;
    } else count++;

    if (numbersReceived >= numbersToReceive) {
      Serial.println("done\n");

      // clear everything just incase
      for (int i = 1; i <= numbersToReceive; i++) Serial.println(values[i]), values[i] = 0;

      // done sending all numbers
      numbersReceived = 0;
    }

    receiving = false;
  }
}

ISR (PCINT0_vect) {
  if (PINB & 0x1) {
    receiving = true;
  }
}
