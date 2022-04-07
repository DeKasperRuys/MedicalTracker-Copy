const byte pin = 2;
volatile byte counter = 0;

void setup() {
  // put your setup code here, to run once:
  Serial.begin(9600);
  
  DDRD &= ~(1 << pin);
  PORTD |= (1 << pin);
  
  EICRA |= (1 << ISC01);
  EICRA &= ~(1 << ISC00);
  
  EIMSK |= (1 << INT0);
  
  sei();
}

ISR(INT0_vect){
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
  delay(5000);         // time to wait for tx
}

void loop() {
  CheckForShock();
}
