#include <lmic.h>
#include <hal/hal.h>
#include <SPI.h>

/*
   Communication pins:
   D4 - D7 && A0 - A3
   D8 receives interrupt
*/
const byte bytes = 4;    // aantal bytes per number
byte res[bytes];         // bytes that contain info about a number
int count = 0;           // counts interrupts
bool receiving;          // start processing data when an interrupt was received
byte numbersReceived;    // counts received numbers

const byte numbersToReceive = 6;         // numbers to receive
float values[numbersToReceive+1];        // saves the values of the received numbers
float valuesToSend[numbersToReceive+1];        // saves the values of the received numbers

static const PROGMEM u1_t NWKSKEY[16] = { 0x78, 0x53, 0x5E, 0x73, 0x15, 0xCA, 0xED, 0x99, 0xC3, 0x29, 0xBA, 0x9D, 0xD6, 0xAE, 0x36, 0x04 };
static const u1_t PROGMEM APPSKEY[16] = { 0x54, 0x8E, 0xB9, 0xD4, 0xE8, 0xB0, 0x59, 0x26, 0xBB, 0x02, 0x67, 0x91, 0x08, 0xB7, 0xEB, 0xE4 };
static const u4_t DEVADDR = 0x2601148C; // <-- Change this address for every node!

void os_getArtEui (u1_t* buf) { }
void os_getDevEui (u1_t* buf) { }
void os_getDevKey (u1_t* buf) { }

static osjob_t sendjob;

// Schedule TX every this many seconds (might become longer due to duty cycle limitations).
const unsigned TX_INTERVAL = 20;

// Pin mapping
const lmic_pinmap lmic_pins = {
  .nss = 10,                       // chip select on feather (rf95module) CS
  .rxtx = LMIC_UNUSED_PIN,
  .rst = 9,                       // reset pin
  .dio = {2, 3, LMIC_UNUSED_PIN}, // assumes external jumpers [feather_lora_jumper]
  // DIO1 is on JP1-1: is io1 - we connect to GPO6
  // DIO1 is on JP5-3: is D2 - we connect to GPO5
};

void onEvent (ev_t ev) {
  Serial.print(os_getTime());
  Serial.print(": ");
  switch (ev) {
    case EV_SCAN_TIMEOUT:   Serial.println(F("EV_SCAN_TIMEOUT"));   break;
    case EV_BEACON_FOUND:   Serial.println(F("EV_BEACON_FOUND"));   break;
    case EV_BEACON_MISSED:  Serial.println(F("EV_BEACON_MISSED"));  break;
    case EV_BEACON_TRACKED: Serial.println(F("EV_BEACON_TRACKED")); break;
    case EV_JOINING:        Serial.println(F("EV_JOINING"));        break;
    case EV_JOINED:         Serial.println(F("EV_JOINED"));         break;
    case EV_JOIN_FAILED:    Serial.println(F("EV_JOIN_FAILED"));    break;
    case EV_REJOIN_FAILED:  Serial.println(F("EV_REJOIN_FAILED"));  break;
    case EV_TXCOMPLETE:     Serial.println(F("EV_TXCOMPLETE (includes waiting for RX windows)"));
      if (LMIC.txrxFlags & TXRX_ACK) Serial.println(F("Received ack"));
      if (LMIC.dataLen) {
        Serial.println(F("Received "));
        Serial.println(LMIC.dataLen);
        Serial.println(F(" bytes of payload"));
      }
      // Schedule next transmission
      os_setTimedCallback(&sendjob, os_getTime() + sec2osticks(TX_INTERVAL), do_send); break;
    case EV_LOST_TSYNC:   Serial.println(F("EV_LOST_TSYNC"));       break;
    case EV_RESET:        Serial.println(F("EV_RESET"));            break;
    case EV_RXCOMPLETE:   Serial.println(F("EV_RXCOMPLETE"));       break;
    case EV_LINK_DEAD:    Serial.println(F("EV_LINK_DEAD"));        break;
    case EV_LINK_ALIVE:   Serial.println(F("EV_LINK_ALIVE"));       break;
    case EV_TXSTART:      Serial.println(F("EV_TXSTART"));          break;
    default:              Serial.print(F("Unknown event: ")); Serial.println((unsigned) ev);  break;
  }
}

void do_send(osjob_t* j) {
  // Check if there is not a current TX/RX job running
  if (LMIC.opmode & OP_TXRXPEND) Serial.println(F("OP_TXRXPEND, not sending"));

  // ready to send
  else {

    typedef struct sensorData {
      float temperature;
      float humidity;
      float latitude;
      float longitude;
      bool movement;
      bool orientation;
    };

#define PACKET_SIZE sizeof(sensorData)

    typedef union LoRa_Packet {
      sensorData sensor;
      byte LoRaPacketBytes[PACKET_SIZE];
    };

    LoRa_Packet levelinfo;

    // test print received data
    for (int i = 1; i < 7; i++) {
      Serial.print(F("i = "));
      Serial.print(i);
      Serial.print(F("val = "));
      Serial.println(valuesToSend[i]);
    }

    // update mydata voor de transmissie.
    levelinfo.sensor.movement = valuesToSend[1] >= 1 ? 1 : 0;
    levelinfo.sensor.temperature = valuesToSend[2];
    levelinfo.sensor.humidity = valuesToSend[3];
    levelinfo.sensor.latitude = valuesToSend[4];
    levelinfo.sensor.longitude = valuesToSend[5];
    levelinfo.sensor.orientation = valuesToSend[6] >= 1 ? 1 : 0;

    // data check again (to see if levelinfo & sensor objects were initiated correctly)
    Serial.print(F("1. movement = "));
    Serial.println(levelinfo.sensor.movement);
    Serial.print(F("2. temperature = "));
    Serial.println(levelinfo.sensor.temperature);
    Serial.print(F("3. humidity = "));
    Serial.println(levelinfo.sensor.humidity);
    Serial.print(F("4. latitude = "));
    Serial.println(levelinfo.sensor.latitude);
    Serial.print(F("5. longitude = "));
    Serial.println(levelinfo.sensor.longitude);
    Serial.print(F("6. orientation = "));
    Serial.println(levelinfo.sensor.orientation);

    clearData();

    LMIC_setTxData2(1, levelinfo.LoRaPacketBytes, sizeof(levelinfo.LoRaPacketBytes) , 0);
    Serial.println(F("Packet queued"));
  }
}

void clearData() {
  for (int i = 0; i < 4; i++) res[i] = 0; // clear transfer bits
  numbersReceived = 0;                    // incase there was a sending mistake
  count = 0;                              // incase there was a sending mistake
}

void setup() {
  PCICR &= 0;
  PCICR |= (1 << PCIF0);    // enable pin change interrupt on any pcint7:0
  PCMSK0 |= (1 << PCINT0);  // enable pin change on pcint0 (D8)

  while (!Serial);
  Serial.begin(115200);
  _delay_ms(100);
  Serial.println(F("Starting"));

  // LMIC init
  os_init();

  // Reset the MAC state. Session and pending data transfers will be discarded.
  LMIC_reset();

#ifdef PROGMEM
  uint8_t appskey[sizeof(APPSKEY)];
  uint8_t nwkskey[sizeof(NWKSKEY)];
  memcpy_P(appskey, APPSKEY, sizeof(APPSKEY));
  memcpy_P(nwkskey, NWKSKEY, sizeof(NWKSKEY));
  LMIC_setSession (0x13, DEVADDR, nwkskey, appskey);
#else
  // If not running an AVR with PROGMEM, just use the arrays directly
  LMIC_setSession (0x13, DEVADDR, NWKSKEY, APPSKEY);
#endif

#if defined(CFG_eu868)
  LMIC_setupChannel(0, 868100000, DR_RANGE_MAP(DR_SF12, DR_SF7),  BAND_CENTI);      // g-band
  LMIC_setupChannel(1, 868300000, DR_RANGE_MAP(DR_SF12, DR_SF7B), BAND_CENTI);      // g-band
  LMIC_setupChannel(2, 868500000, DR_RANGE_MAP(DR_SF12, DR_SF7),  BAND_CENTI);      // g-band
  LMIC_setupChannel(3, 867100000, DR_RANGE_MAP(DR_SF12, DR_SF7),  BAND_CENTI);      // g-band
  LMIC_setupChannel(4, 867300000, DR_RANGE_MAP(DR_SF12, DR_SF7),  BAND_CENTI);      // g-band
  LMIC_setupChannel(5, 867500000, DR_RANGE_MAP(DR_SF12, DR_SF7),  BAND_CENTI);      // g-band
  LMIC_setupChannel(6, 867700000, DR_RANGE_MAP(DR_SF12, DR_SF7),  BAND_CENTI);      // g-band
  LMIC_setupChannel(7, 867900000, DR_RANGE_MAP(DR_SF12, DR_SF7),  BAND_CENTI);      // g-band
  LMIC_setupChannel(8, 868800000, DR_RANGE_MAP(DR_FSK,  DR_FSK),  BAND_MILLI);      // g2-band
#elif defined(CFG_us915)
  LMIC_selectSubBand(1);
#endif

  // Disable link check validation
  LMIC_setLinkCheckMode(0);

  // TTN uses SF9 for its RX2 window.
  LMIC.dn2Dr = DR_SF9;

  // Set data rate and transmit power for uplink
  LMIC_setDrTxpow(DR_SF7, 14);

  // Start job
  do_send(&sendjob);
}

ISR (PCINT0_vect) {
  if (PINB & 0x1) {
    receiving = true;
  }
}

void loop() {
  os_runloop_once();
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
      for (int i = 1; i <= numbersToReceive; i++) valuesToSend[i] = values[i], Serial.println(values[i]), values[i] = 0;

      // done sending all numbers
      numbersReceived = 0;
      
    }

    receiving = false;
  }
  
}
