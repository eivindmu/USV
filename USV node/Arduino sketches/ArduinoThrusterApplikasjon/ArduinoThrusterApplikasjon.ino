#include <Servo.h>

int servoPins[] = {3, 5, 6, 9};
int outputPWM[4];
int stopSignal = 1500;
String thrusterCommands;

String line0;
String line1;
String line2;
String line3;
char c;

Servo servo[4];

void setup() {
  Serial.begin(115200);
  delay(30000);
  readBuffer();
  for (int i = 0; i < 4; i++) {
    outputPWM[i] = stopSignal; //stop/idle signal
    servo[i].attach(servoPins[i]);
    servo[i].writeMicroseconds(1500);
  }
  //give ESC time to start
  delay(2000);
  
}
void readBuffer(){
  while(Serial.available() >0){
    c = Serial.read();
  }
}
void loop() {
  if (Serial.available() > 0) {
    //read values from serial port
    //eksempel string "1600:1433:1700:1900:" (kolon til slutt viktig)
    line0 = Serial.readStringUntil(':');
    line1 = Serial.readStringUntil(':');
    line2 = Serial.readStringUntil(':');
    line3 = Serial.readStringUntil(':');

    Serial.println(line0 + " " + line1 + " " + line2 + " " + line3);

    outputPWM[0] = line0.toInt();
    outputPWM[1] = line1.toInt();
    outputPWM[2] = line2.toInt();
    outputPWM[3] = line3.toInt();
    //write values to thrusters ESC
    for (int i = 0; i < 4; i++) {
      servo[i].writeMicroseconds(outputPWM[i]);
    }
  }
  
}

