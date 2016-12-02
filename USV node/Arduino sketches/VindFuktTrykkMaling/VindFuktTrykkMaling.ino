#include <stdint.h>
#include "SparkFunBME280.h"
//Library allows either I2C or SPI, so include both.
#include "Wire.h"
#include "SPI.h"

//Global sensor object
BME280 mySensor;

// diameter of anemometer
float diameter = 0.6985; //meters from center pin to middle of cup
float metersPerSecond; //meters per second
float speedPerClick = 0.3333; // meter per second per half_revolution

int half_revolutions1 = 0;
int half_revolutions2 = 0;
int half_revolutions3 = 0;
unsigned long lastmillis1 = 0;
unsigned long lastmillis2 = 0;
unsigned long lastmillis3 = 0;
unsigned long lastmicros = 0;

int vaneInput = 0;

int inc = 1;



void setup() {
  Serial.begin(57600);

  //***Driver settings********************************//
  //commInterface can be I2C_MODE or SPI_MODE
  //specify chipSelectPin using arduino pin names
  //specify I2C address.  Can be 0x77(default) or 0x76

  //For I2C, enable the following and disable the SPI section
  mySensor.settings.commInterface = I2C_MODE;
  mySensor.settings.I2CAddress = 0x77;

  //***Operation settings*****************************//

  //renMode can be:
  //  0, Sleep mode
  //  1 or 2, Forced mode
  //  3, Normal mode
  mySensor.settings.runMode = 3; //Normal mode

  //tStandby can be:
  //  0, 0.5ms
  //  1, 62.5ms
  //  2, 125ms
  //  3, 250ms
  //  4, 500ms
  //  5, 1000ms
  //  6, 10ms
  //  7, 20ms
  mySensor.settings.tStandby = 0;

  //filter can be off or number of FIR coefficients to use:
  //  0, filter off
  //  1, coefficients = 2
  //  2, coefficients = 4
  //  3, coefficients = 8
  //  4, coefficients = 16
  mySensor.settings.filter = 0;

  //tempOverSample can be:
  //  0, skipped
  //  1 through 5, oversampling *1, *2, *4, *8, *16 respectively
  mySensor.settings.tempOverSample = 1;

  //pressOverSample can be:
  //  0, skipped
  //  1 through 5, oversampling *1, *2, *4, *8, *16 respectively
  mySensor.settings.pressOverSample = 1;

  //humidOverSample can be:
  //  0, skipped
  //  1 through 5, oversampling *1, *2, *4, *8, *16 respectively
  mySensor.settings.humidOverSample = 1;

  Serial.println(mySensor.begin(), HEX);

  pinMode(A0, INPUT);
  attachInterrupt(digitalPinToInterrupt(3), rpm_fan, FALLING);
}

void loop() {
  vaneInput = analogRead(A0);

  if ((millis() - lastmillis1 >= 1000) && inc == 1) {
    metersPerSecond = speedPerClick * half_revolutions1 / 3;
    half_revolutions1 = 0;
    lastmillis2 = millis();
    inc++;
    printWindTempHumidity(metersPerSecond);
  }

  if ((millis() - lastmillis2 >= 1000) && inc == 2) {
    metersPerSecond = speedPerClick * half_revolutions2 / 3;
    half_revolutions2 = 0;
    lastmillis3 = millis();
    inc++;
    printWindTempHumidity(metersPerSecond);
  }

  if ((millis() - lastmillis3 >= 1000) && inc == 3) {
    metersPerSecond = speedPerClick * half_revolutions3 / 3;
    half_revolutions3 = 0;
    lastmillis1 = millis();
    inc = 1;
    printWindTempHumidity(metersPerSecond);
  }
}

// this code will be executed every time the interrupt 0 (pin2) gets low.
void rpm_fan() {
  if (micros() - lastmicros >= 100) {
    lastmicros = micros();
    half_revolutions1++;
    half_revolutions2++;
    half_revolutions3++;
  }
}

float lookupDegreesFromReading(int input) {
  if (input >= 125 && input <= 135) return 0;
  if (input >= 306 && input <= 318) return 22.5;
  if (input >= 225 && input <= 235) return 45;
  if (input >= 605 && input <= 615) return 67.5;
  if (input >= 550 && input <= 560) return 90;
  if (input >= 935 && input <= 940) return 112.5;
  if (input >= 922 && input <= 934) return 135;
  if (input >= 950 && input <= 960) return 157.5;
  if (input >= 828 && input <= 840) return 180;
  if (input >= 888 && input <= 900) return 202.5;
  if (input >= 724 && input <= 736) return 225;
  if (input >= 765 && input <= 780) return 247.5;
  if (input >= 380 && input <= 393) return 270;
  if (input >= 410 && input <= 425) return 292.5;
  if (input >= 67 && input <= 80) return 315;
  if (input >= 184 && input <= 196) return 337.5;
  return -1; //error
}

void printWindTempHumidity(float metersPerSecond) {
  Serial.print("&Wind speed: ");
  Serial.print(metersPerSecond);
  Serial.print(" Wind direction(deg): ");
  Serial.print(lookupDegreesFromReading(vaneInput));
  Serial.print(" Temperature(C): ");
  Serial.print(mySensor.readTempC(), 2);
  Serial.print(" Pressure(kPa): ");
  Serial.println((mySensor.readFloatPressure())/100, 1);
}

