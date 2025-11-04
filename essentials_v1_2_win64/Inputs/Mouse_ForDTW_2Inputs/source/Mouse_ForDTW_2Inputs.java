import processing.core.*; 
import processing.data.*; 
import processing.event.*; 
import processing.opengl.*; 

import controlP5.*; 
import oscP5.*; 
import netP5.*; 

import java.util.HashMap; 
import java.util.ArrayList; 
import java.io.File; 
import java.io.BufferedReader; 
import java.io.PrintWriter; 
import java.io.InputStream; 
import java.io.OutputStream; 
import java.io.IOException; 

public class Mouse_ForDTW_2Inputs extends PApplet {

/**
* Very simple sketch that sends x,y values to Wekinator  
* Run Wekinator with 2 inputs (mouse x & y)
* Unlike the DTW Mouse Explorer, this does NOT also act as an output!
* You should use one of your own outputs.
**/





OscP5 oscP5;
NetAddress dest;
ControlP5 cp5;
PFont f, f2;
boolean isRecording = true; //mode
boolean isRecordingNow = true;

int areaTopX = 140;
int areaTopY = 70;
int areaWidth = 450;
int areaHeight = 390;

int currentClass = 1;

public void setup() {
 // colorMode(HSB);
  
  noStroke();

  /* start oscP5, listening for incoming messages at port 12000 */
  oscP5 = new OscP5(this,6449);
  dest = new NetAddress("127.0.0.1",6448);
  
  //Create the font
  f = createFont("Courier", 14);
  textFont(f);
  f2 = createFont("Courier", 40);
  textAlign(LEFT, TOP);
  
  createControls();
  sendNames();
}

public void sendNames() {
  OscMessage msg = new OscMessage("/wekinator/control/setInputNames");
  msg.add("mouseX"); 
  msg.add("mouseY");
  oscP5.send(msg, dest);
}

public void createControls() {
  cp5 = new ControlP5(this);
  cp5.addToggle("isRecording")
     .setPosition(10,20)
     .setSize(75,20)
     .setValue(true)
     .setCaptionLabel("record/run")
     .setMode(ControlP5.SWITCH)
     ;
}

public void drawText() {
  fill(255);
  textFont(f);
  if (isRecording) {
    text("Run Wekinator with 2 inputs (mouse x,y), 1 DTW output", 100, 20);
    text("Click and drag to record gesture #" + currentClass + " (press number to change)", 100, 35);
  } else {
    text("Click and drag to test", 100, 20);    
  }
  text ("This program does NOT act as an output; run with your own output!", 100, 50);  
}

public void draw() {
  background(0);
   smooth();
   drawText();
  
  if (mousePressed) {
    noStroke();
    fill(255, 0, 0);
    ellipse(mouseX, mouseY, 10, 10);
  }
  drawClassifierArea();
  if(mousePressed && frameCount % 2 == 0) {
    sendOsc();
  }
}

public void drawClassifierArea() {
  stroke(255);
  noFill();
  rect(areaTopX, areaTopY, areaWidth, areaHeight, 7);
}

public boolean inBounds(int x, int y) {
 if (x < areaTopX || y < areaTopY) {
    return false;
 }
 if (x > areaTopX + areaWidth || y > areaTopY + areaHeight) {
    return false;
 } 
 return true;
}

public void mousePressed() {
  if (! inBounds(mouseX, mouseY)) {
    return;
  }
  if (isRecording) {
     isRecordingNow = true;
     OscMessage msg = new OscMessage("/wekinator/control/startDtwRecording");
     msg.add(currentClass);
     oscP5.send(msg, dest);
  } else {
    OscMessage msg = new OscMessage("/wekinator/control/startRunning");
    oscP5.send(msg, dest);
  }
}

public void mouseReleased() {
  if (isRecordingNow) {
     isRecordingNow = false;
     OscMessage msg = new OscMessage("/wekinator/control/stopDtwRecording");
      oscP5.send(msg, dest);
  }
}



public void keyPressed() {
  int keyIndex = -1;
  if (key >= '1' && key <= '9') {
    currentClass = key - '1' + 1;
  }
}

public void sendOsc() {
  OscMessage msg = new OscMessage("/wek/inputs");
  msg.add((float)mouseX); 
  msg.add((float)mouseY);
  oscP5.send(msg, dest);
}

//This is called automatically when OSC message is received
public void oscEvent(OscMessage theOscMessage) {
 
}
  public void settings() {  size(640, 480, P2D); }
  static public void main(String[] passedArgs) {
    String[] appletArgs = new String[] { "Mouse_ForDTW_2Inputs" };
    if (passedArgs != null) {
      PApplet.main(concat(appletArgs, passedArgs));
    } else {
      PApplet.main(appletArgs);
    }
  }
}
