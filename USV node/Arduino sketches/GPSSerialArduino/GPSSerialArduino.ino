#include <Adafruit_GPS.h>
#include <SoftwareSerial.h>
SoftwareSerial mySerial(3, 2); //initialize software serial port
Adafruit_GPS GPS(&mySerial); //create GPS object

String NMEA1;//Variable for first NMEA sentence
String NMEA2;
String response;

char c; //to read character coming from the GPS
String antennaString;
char antennaStatus;

void setup() {
  Serial.begin(115200); //Turn on serial monitor, googlea earth real time uses 38400
  GPS.begin(9600); //Turn on GPS
  //GPS.sendCommand(PGCMD_NOANTENNA); //turn off antenna update nuisance data
  GPS.sendCommand(PGCMD_NOANTENNA);
  GPS.sendCommand(PMTK_SET_NMEA_UPDATE_5HZ); //set update rate to 5Hz
  GPS.sendCommand("$PMTK386,0*23"); //Nav speed threshold is zero
  GPS.sendCommand(PMTK_SET_NMEA_OUTPUT_RMCGGA); //Request RMC and GGA Sentences only
  GPS.sendCommand(PMTK_API_SET_FIX_CTL_5HZ);
  

  delay(1000);

}

void loop() {
  readGPS();
}

void readGPS() {
  //clearGPS();
  while (!GPS.newNMEAreceived()) { //loop until you have a good NMEA sentence
    c = GPS.read();
  }
  GPS.parse(GPS.lastNMEA()); //parse last good NMEA sentece
  NMEA1 = GPS.lastNMEA();


  while (!GPS.newNMEAreceived()) { //loop until you have a good NMEA sentence
    c = GPS.read();
  }
  GPS.parse(GPS.lastNMEA()); //parse last good NMEA sentece
  NMEA2 = GPS.lastNMEA();
  
  Serial.println(NMEA1.substring(1));
  Serial.println(NMEA2.substring(1));
  Serial.println("");

}

void clearGPS() { //clear old and corrupt data from serial port
//  while (!GPS.newNMEAreceived()) { //loop until you have a good NMEA sentence
//    c = GPS.read();
//  }
//  GPS.parse(GPS.lastNMEA()); //parse last good NMEA sentece
//
//  while (!GPS.newNMEAreceived()) { //loop until you have a good NMEA sentence
//    c = GPS.read();
//  }
//  GPS.parse(GPS.lastNMEA()); //parse last good NMEA sentece
//
//  while (!GPS.newNMEAreceived()) { //loop until you have a good NMEA sentence
//    c = GPS.read();
//  }
//  GPS.parse(GPS.lastNMEA()); //parse last good NMEA sentece
//
//  while (!GPS.newNMEAreceived()) { //loop until you have a good NMEA sentence
//    c = GPS.read();
//  }
//  GPS.parse(GPS.lastNMEA()); //parse last good NMEA sentece
}


