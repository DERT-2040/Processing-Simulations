int PIXELS_TO_METERS = 300;
float GRAVITY = 9.81 * PIXELS_TO_METERS;
float ANGLE_LOWER_LIMIT = 0.523599; //In radians: 30 degrees
float ANGLE_UPPER_LIMIT = 1.48;//In radians: 70 degrees

boolean adjustTargetHeight = false;
float velocity = 15 * PIXELS_TO_METERS;
int targetHeight;
int robotPos;

int targetTolerance = 50;
int targetDistance = 1300;
int robotHeight = 800;
float angles[][];

boolean debugPrint = false;
boolean moveRobTar = false;
void setup() {
   size(1500, 900);
   background(192, 64, 0);
   stroke(255);
   targetHeight = 100;
   robotPos = 600;
  strokeWeight(4);
}
void draw() {
  angles = calculateAngleTolerances();
  background(192, 64, 0);
  if(moveRobTar){//If the key is pressed 
    if(mouseY < robotHeight-50 && adjustTargetHeight){//if the mouse is above the robot and the target is allowed to move
      targetHeight = mouseY;
    }
    if(mouseX < targetDistance-50){// if the robot is behind the target
      robotPos = mouseX;
    }
  }
  
  //Display target and Robot
  line(targetDistance, targetHeight+targetTolerance, targetDistance, targetHeight-targetTolerance);
  rect(robotPos-50,robotHeight,100, 15);
  
  //Display projectile's motion. 
  if(angles[0][0] > ANGLE_LOWER_LIMIT){
    line(robotPos, robotHeight, robotPos+100*cos(angles[0][0]),robotHeight-1*(100*sin(angles[0][0])));
    graphProjectile(angles[0][0],velocity);
    //graphProjectile(angles[0][0]+angles[0][1],velocity);// Show the arcs used for the tolerances
    //graphProjectile(angles[0][0]-angles[0][1],velocity);
  }
  if(angles[1][0] < ANGLE_UPPER_LIMIT){
    line(robotPos, robotHeight, robotPos+100*cos(angles[1][0]),robotHeight-1*(100*sin(angles[1][0])));
    graphProjectile(angles[1][0],velocity);
  }
  
  float risingVeloTolerance = calculateVelocityTolerance(angles[0][0]);
  float fallingVeloTolerance = calculateVelocityTolerance(angles[1][0]);
  
  textSize(30); //Prints out all data
  text("C to Move Robot and Target, L to Lock Target, Up/Down to Change Velocity  " ,10,40);
  textSize(20); //Prints out all data
  text("VELOCITY = " + velocity/PIXELS_TO_METERS,10,65);
  text("DISTANCE TO TARGET = " + float(targetDistance - robotPos)/PIXELS_TO_METERS,10,90);
  text("HEIGHT TO TARGET = " +float(robotHeight - targetHeight)/PIXELS_TO_METERS,10,115);
  text("RISING ANGLE = " + (angles[0][0] * 180 / PI),10,140);
  text("FALLING ANGLE = " + (angles[1][0] * 180 / PI) ,10,165);
  text("RISING ANGLE TOLERANCE = " + (angles[0][1] * 180 / PI) ,10,190);
  text("FALLING ANGLE TOLERANCE = " + (angles[1][1] * 180 / PI) ,10,215);
  text("RISING ANGLE VELOCITY TOLERANCE = " + risingVeloTolerance ,10,240);
  text("FALLING ANGLE VELOCITY TOLERANCE = " + fallingVeloTolerance  ,10,265);
  if(debugPrint){
    debugPrint = false;
  }
}
 
void keyPressed() {
   if((key == 'c' || key == 'C')){//When the key is pressed, the robot and target can be moved
     moveRobTar = true;
   }
   if((key == 'd' || key == 'D')) {//Used for printing out debugging code
       debugPrint = true;
   }
   if((key == 'l' || key == 'L')) {//Changes if target is locked
       adjustTargetHeight = !adjustTargetHeight;
   }
   if(keyCode == DOWN && velocity > 0){ //Increments velocity of projectile.
     velocity -= 0.1* PIXELS_TO_METERS;
   }
   if(keyCode == UP && velocity > 20){
     velocity += 0.1* PIXELS_TO_METERS;
   }
}
void mousePressed() {
   moveRobTar = false;
}
//Calculates the rising and falling trajectory
float[] calculateTrajectory(int tHeight){
  float distanceToTarget = targetDistance - robotPos;
  float heightToTarget = robotHeight - tHeight;
  //implementation of Angle Required to hit Target
  //https://en.wikipedia.org/wiki/Projectile_motion#Angle_'%22%60UNIQ--postMath-000000A8-QINU%60%22'_required_to_hit_coordinate_(x,y)
  
  float term1 = pow(velocity,2.0);
  float term2P = (GRAVITY * pow(distanceToTarget,2)) + (2* heightToTarget * pow(velocity,2)); 
  float term2 = sqrt( pow(velocity,4)-GRAVITY*(term2P));
  float risingTerm3 = (term1 - term2)/(GRAVITY * distanceToTarget);
  float fallingTerm3 = (term1 + term2)/(GRAVITY * distanceToTarget);
  float angles[] = new float[2];
  angles[0] = atan(risingTerm3);
  angles[1] = atan(fallingTerm3);
  
  if(debugPrint){
    print(angles[0] + ", " + angles[1] + "\n");
  }
  return angles;
}
//Graphs trajectory with projectile velocity and angle of launch. Independent of other calculations
void graphProjectile(float angle, float initVelocity){
  float xVelocity = initVelocity*cos(angle);
  float yVelocity = initVelocity*sin(angle);
  int heightP = 0;
  int lengthP = 0;
  float timeIterator = 0.005;
  float time = 0+timeIterator;
  int xPoint = int(xVelocity*time);
  int yPoint = int(yVelocity*time)-int(0.5*GRAVITY*pow(time,2));
  do{
    xPoint = int(xVelocity*time);
    yPoint = int(yVelocity*time)-int(0.5*GRAVITY*pow(time,2));
    point(robotPos + xPoint,800-yPoint);
    time += timeIterator;
  }while (time <200 && robotPos + xPoint<1500 && yPoint > -100);
}
//Calculates angles with the tolerances of how much more up and down it can go. Returns a 2d array with angle of launch (Rising or Falling) and the tolerance 
float[][] calculateAngleTolerances(){
  float[] anglesHigh = calculateTrajectory(targetHeight + targetTolerance);
  float[] anglesLow = calculateTrajectory(targetHeight - targetTolerance);
  float[][] anglesWithTolerances = new float[2][2];
  anglesWithTolerances[0][0] = anglesLow[0]+(anglesHigh[0]-anglesLow[0])/2;
  anglesWithTolerances[0][1] = (anglesLow[0]-anglesHigh[0])/2;
  anglesWithTolerances[1][0] = anglesLow[1]+(anglesHigh[1]-anglesLow[1])/2;
  anglesWithTolerances[1][1] = (anglesHigh[1]-anglesLow[1])/2;
  return anglesWithTolerances;
}
float calculateVelocity(int tHeight, float angle){
  float distanceToTarget = targetDistance - robotPos;
  float heightToTarget = robotHeight - tHeight;
  /*float term1 = 1/cos(angle);
  float term2 = 0.5*9.8*pow(distanceToTarget,2);
  float term3 = (distanceToTarget*tan(angle)) - heightToTarget;
  float term2wit3 = pow(term2/term3,0.5);
  float velocity = term1*term2wit3;*/
  
  float term1 = GRAVITY*pow(distanceToTarget,2);
  float term2 = 2* pow(cos(angle),2);
  float term3 = distanceToTarget*tan(angle)-heightToTarget;
  float velocity = pow((term1/(term2*term3)),0.5);
  if (debugPrint){
    println("velocity " + velocity);
  }
  return velocity;
}

float calculateVelocityTolerance(float angle){
  float[] velocities = new float[2];
  velocities[0] = calculateVelocity(targetHeight+targetTolerance, angle);

  velocities[1] = calculateVelocity(targetHeight-targetTolerance, angle);
   
  return velocities[1]-velocities[0];
}
