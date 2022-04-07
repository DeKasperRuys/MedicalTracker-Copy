const int BAUDRATE = 9600;
const int data_sent = 5;
const String del = ",";

String recString;
int recStringSize;
bool ready;

void setup() {
  Serial.begin(BAUDRATE);
}

void loop() {
  while (Serial.available()) {
    ready = true;
    recString += Serial.readString();
  }

  if (recString.length() > 0) {
    recStringSize = recString.length();
    
    String str[data_sent];
    int startIndex = 0;
    int lastIndex = recString.indexOf(del, startIndex);
    for (int i = 0; i < data_sent; i++) {
      i != data_sent - 1 ? str[i] = recString.substring(startIndex, lastIndex) : str[i] = recString.substring(startIndex);
      startIndex = lastIndex + 1;
      lastIndex = recString.indexOf(del, startIndex);
    }

    String id = str[0];
    String minTemp = str[1];
    String maxTemp = str[2];
    String minHum = str[3];
    String maxHum = str[4];
    
    //use data here
    Serial.println(id);
    Serial.println(minTemp);
    Serial.println(maxTemp);
    Serial.println(minHum);
    Serial.println(maxHum);
    
    recString = "";
  }
  if (ready) ready != ready;

  _delay_ms(1);
}
