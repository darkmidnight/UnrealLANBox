#include <Adafruit_NeoPixel.h>

#define PIN 6
#define PIXCOUNT 15
Adafruit_NeoPixel strip = Adafruit_NeoPixel(PIXCOUNT, PIN, NEO_GRB + NEO_KHZ800);

uint32_t teamColors[4] = { strip.Color(255, 255, 255), strip.Color(0, 0, 255), strip.Color(0, 255, 0), strip.Color(255, 255, 0) };

void setup() {

  strip.begin();
  strip.show();
    Serial.begin(9600);
}

byte data[6] = { 0, 0, 0, 0, 0, 0 };
int idx = 0;

int gameMode = 0; // 0=CTF, 1=DOM, 2=RESET
int scores[4] = { 0,0,0,0 }; // indexes are 0=Red, 1=Blue, 2=Green, 3=Gold
int totalScore = 0; // Sum of scores array values
int displayMode = 0; // See loop conditionals
int winningTeam = 0; // as per scores indices
int hiScore = 0; // The current score of the winning team
int eventTeam = 0;
int eventType = 0;

void mySerial() {
  while (Serial.available()) {
    byte inByte = Serial.read();

    data[idx] = inByte;
    idx++;
    if (idx >=sizeof(data)) {
   process();
      idx = 0;
    }
  }
}

void process() {
  Serial.println("Processing");
  
  displayMode = data[0] >> 4;
  displayMode = displayMode << 4;
  displayMode = displayMode >> 4;
  gameMode = data[0] - (displayMode << 4);
  Serial.println("Display Mode");
  Serial.println(displayMode);
  Serial.println("Game Mode");
  Serial.println(gameMode);
 
  eventTeam = data[1] >> 4;
  eventTeam = eventTeam << 4;
  eventTeam = eventTeam >> 4;
  eventType = data[1] - (eventTeam << 4);
  Serial.println("Event Team");
  Serial.println(eventTeam);
  Serial.println("Event Type");
  Serial.println(eventType);     

totalScore = 0;
hiScore = 0;
winningTeam = 0;
  for (int i=2;i<sizeof(data);i++) {
    totalScore += data[i];
    scores[i-2] = data[i];
    if (scores[i-2] > hiScore) {
      hiScore = scores[i-2];
      winningTeam = i-2;
    }
  }
  Serial.println("Winning Score");
  Serial.println(hiScore);
  Serial.println(winningTeam);
}

void myReset() {
  for (int i=0;i<sizeof(data);i++) {
    data[i] = 0;
  }
  gameMode = 0;
  for (int i=0;i<sizeof(scores);i++) {
    scores[i] = 0;
  }
  totalScore = 0;
  displayMode = 0;
  winningTeam = 0;
  hiScore = 0;
  eventTeam = 0;
  eventType = 0;
}



void loop() {
  mySerial();
  switch (displayMode) {
    default: // Same as 0 = Winning Team
        colorWipe(teamColors[winningTeam], 50);
    break;
    case 1: // Winning Proportion (eg if score is 3-1 red v blue, it'll show 75% red and 25% blue)
      int tS = 0;
      for (int team =0; team<4; team++) {
        uint32_t c = teamColors[team];
        int aMap = map(scores[team],0,totalScore,0,15);
        for(int i=0; i<aMap; i++) {
          strip.setPixelColor(tS+i, c);
        }
        tS = tS + aMap;
     }
     strip.show();
    break;
  }
  
}

// Fill the dots one after the other with a color
void colorWipe(uint32_t c, uint8_t wait) {
  for(uint16_t i=0; i<strip.numPixels(); i++) {
      strip.setPixelColor(i, c);
      strip.show();
 //     delay(wait);
  }
}

