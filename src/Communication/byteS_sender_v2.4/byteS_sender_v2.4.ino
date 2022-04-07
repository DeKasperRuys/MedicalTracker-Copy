/*
   pins: D2-D7 and D8-D9
   A0 sends interrupt
*/

// do not change
bool readyToSend = false;
const int valuesToSend = 6;
const byte bytes = 4;
byte res[bytes];

void setup() {
  //Serial.begin(9600);

  DDRD |= B11111100;
  DDRB |= B00000011;
  DDRC |= B00000001;
  clearRegisters();

  readyToSend = true; // enable when needed
}

void loop() {
  /*if (readyToSend) {
    sendData(1, -0.7, 10, 88.1, -3.25, 1);
    readyToSend = false;
  }*/
  sendData(1, -0.7, 10, 88.1, -3.25, 1);
  delay(5000);
}

void sendData(float data1, float data2, float data3, float data4, float data5, float data6) {
  float valueArray[valuesToSend] = { data1, data2, data3, data4, data5, data6 };

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

  /*Serial.print("PORTD = ");
    Serial.print(getBinary(PORTD));
    Serial.print("\tPORTB = ");
    Serial.print(getBinary(PORTB));
    Serial.print("\tPORTC = ");
    Serial.println(getBinary(PORTC));*/

  // PORTC &= ~1;
  // can be removed cus registers will be cleared above anyway
  // unless the interrupt routine isn't working properly

  _delay_ms(25);
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

  //DebugFloat(num, id, positive, res[0], res[1], res[2], res[3]);
}

void DebugFloat(float input, int id, bool pos, int num1, int num2, int num3, int num4) {
  /*Serial.print("\nNumber to simulate = ");
    Serial.println(input, 4);
    Serial.print("Numbers id = ");
    Serial.println(id);

    if (pos) Serial.println("The number is positive");
    else Serial.println("The number is negative");

    Serial.print("res[0] = ");
    Serial.print(num1);
    Serial.print("\tBIN = ");
    Serial.println(getBinary(num1));

    Serial.print("res[1] = ");
    Serial.print(num2);
    Serial.print("\tBIN = ");
    Serial.println(getBinary(num2));

    Serial.print("res[2] = ");
    Serial.print(num3);
    Serial.print("\tBIN = ");
    Serial.println(getBinary(num3));

    Serial.print("res[3] = ");
    Serial.print(num4);
    Serial.print("\tBIN = ");
    Serial.println(getBinary(num4));*/
}

void clearRegisters() {
  PORTD &= 0x3;
  PORTB &= 0xFC;
  PORTC &= ~0x1;
}

/*String getBinary(int num) {
  String temp = String(num, BIN);
  while (temp.length() < 8) {
    temp = "0" + temp;
  }
  return temp;
  }*/
