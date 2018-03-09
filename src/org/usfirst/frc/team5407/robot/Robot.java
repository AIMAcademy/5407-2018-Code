/*----------------------------------------------------------------------------*/
/* Copyright (c) 2017-2018 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package org.usfirst.frc.team5407.robot;

import edu.wpi.cscore.UsbCamera;
import edu.wpi.cscore.VideoMode;
import edu.wpi.cscore.VideoMode.PixelFormat;
import edu.wpi.first.wpilibj.CameraServer;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.SerialPort;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.Timer;



/**
 * This program contains FRC team 5407's code for the 2018 competition season 
 */
public class Robot extends IterativeRobot {
	// Create new classes and call them here 
	Sensors sensors;
	Air air;
	Inputs inputs;
	Variables variables;
	Lift lift;
	DriveTrain drivetrain;
	Intake intake;
	Winch winch;
	Vision vision;
	Timer timer;
	DriverStation ds;

	// Autos, creating string for new auto and has a sendable chooser at the end of it
	final String doNothingAuton = "Do Nothing!!";
	final String driveBaseLineStraight = "Drive Straight To BaseLine "; //Needs Testing
	final String centerDriveBaseLineToLeftOfPile = "Center Drive To Left Of Pile";
	final String centerDriveBaseLineToRightOfPile = "Center Drive To Right Of Pile";
	final String leftDrivetoLeftSideScale = "Left Drive to Left Side Scale";
	final String jordansDriveBaseline = "Jordan's Drive to Baseline";
	final String testAuton = "Test Auton";
	private String autonChooser;
	private SendableChooser<String> AutonChooser;

	final String leftSideStart = "Left Side Start";
	final String centerStartThenRight = "Center Start Then Right";
	final String centerStartThenLeft = "Center Start Then Left";
	final String rightSideStart = "Right Side Start";
	private String startSelected;
	private SendableChooser<String> StartChooser;

	String ownership;
	String ownership0;
	String ownership1;
	String ownership2;

	final double distanceAdjustment = 1.344;
	int autonCounter;

	@Override
	public void robotInit() {
		// Makes classes recognized in program and execute
		drivetrain = new DriveTrain();
		sensors = new Sensors();
		inputs = new Inputs(0, 1, 2); 
		variables = new Variables();
		lift = new Lift(0);
		intake = new Intake(1,2);
		winch = new Winch(3);
		vision = new Vision();
		timer = new Timer();
		ds = DriverStation.getInstance();

		vision.setJeVoisVideoMode();

		// Calls 4 solenoids in the air class
		air = new Air(0, 1, 2, 3, 4, 5);

		AutonChooser = new SendableChooser<String>();
		AutonChooser.addDefault("Do Nothing!!", doNothingAuton);
		AutonChooser.addObject("Drive Straight To BaseLine ", driveBaseLineStraight);
		AutonChooser.addObject("Center Drive To Left Of Pile", centerDriveBaseLineToLeftOfPile);
		AutonChooser.addObject("Center Drive To Right Of Pile", centerDriveBaseLineToRightOfPile);
		AutonChooser.addObject("Left Drive to Left Side Scale" , leftDrivetoLeftSideScale);
		AutonChooser.addObject("Jordan's Drive to Baseline", jordansDriveBaseline);
		AutonChooser.addObject("Test Auton", testAuton);
		SmartDashboard.putData("Auton Choices", AutonChooser);

		StartChooser = new SendableChooser<String>();
		StartChooser.addDefault("Center Start Then Right", centerStartThenRight);
		StartChooser.addObject("Center Start Then Left", centerStartThenLeft);
		StartChooser.addObject("Left Side Start", leftSideStart);
		StartChooser.addObject("Right Side Start", rightSideStart);
		SmartDashboard.putData("Start Choices", StartChooser);

		//		SmartDashboard.updateValues();
	}

	public void robotPeriodic() {}

	public void disabledInit() {}

	public void disabledPeriodic() {

		autonChooser = AutonChooser.getSelected();
		SmartDashboard.putString("My Selected Auton is ", autonChooser);

		startSelected = StartChooser.getSelected();
		SmartDashboard.putString("Robot Start Position is ", startSelected);
	}

	public void autonomousInit() {
		// Zero and initialize values for auton 
		air.initializeAir();

		//resets both drive encoders to zero
		drivetrain.frontLeftDriveMotor.setSelectedSensorPosition(variables.encoderpos, 0, 10);
		drivetrain.frontRightDriveMotor.setSelectedSensorPosition(variables.encoderpos, 0, 10);

		//resets gyro to zero
		sensors.ahrs.reset();

		//Reset encoders
		drivetrain.resetEncoders();

		autonCounter = 1;

		timer.reset();
		timer.start();

		getGameData();

	}

	public void autonomousPeriodic() {		
		getGameData();
		
		// Getting the encoder values for the drivetrain and cooking and returning them
		drivetrain.getLeftQuadPosition();
		drivetrain.getRightQuadPosition();
		// Gets all needed angles from NavX
		sensors.getPresentAngleNAVX();
		sensors.getFollowAngleNAVX();
		sensors.ahrs.getAngle();
		sensors.analogLiftPot.get();
		// Gets auto choosen and displays it on SmartDashboard

		autonChooser = AutonChooser.getSelected();
		SmartDashboard.putString("My Selected Auton is ", autonChooser);

		startSelected = StartChooser.getSelected();
		SmartDashboard.putString("Robot Start Position is ", startSelected);

		// If else statement for auton selection
		if (autonChooser == doNothingAuton) {
		}else if (autonChooser == driveBaseLineStraight) { driveBaseLineStraight();
		}else if (autonChooser == centerDriveBaseLineToLeftOfPile) { centerDriveBaseLineToLeftOfPile();
		}else if (autonChooser == centerDriveBaseLineToRightOfPile) { centerDriveBaseLineToRightOfPile();
		}else if (autonChooser == leftDrivetoLeftSideScale) { leftFarSideScale();
		}else if (autonChooser == jordansDriveBaseline){jordansDriveBaseline();
		}else if (autonChooser == testAuton){testAuton();
		}

		/*		if (startSelected == centerStartThenRight) {
		}else if (startSelected == centerStartThenLeft) {
		}else if (startSelected == rightSideStart) {
		}else if (startSelected == leftSideStart) {
		}*/

		//Puts values on SmartDashboard in Auto
		SmartDashboard.putNumber("Gyro-NAVX", sensors.ahrs.getAngle());
		SmartDashboard.putNumber("Air PSI", sensors.getAirPressurePsi());
		SmartDashboard.putNumber("left side inches", drivetrain.getLeftQuadPosition());
		SmartDashboard.putNumber("right side inches", drivetrain.getRightQuadPosition());
		SmartDashboard.updateValues();

	}

	public void teleopInit() {
		// Zero and initialize all inputs and sensors for teleop
		air.initializeAir();

		//resets both drive encoders to zero
		drivetrain.frontLeftDriveMotor.setSelectedSensorPosition(variables.encoderpos, 0, 10);
		drivetrain.frontRightDriveMotor.setSelectedSensorPosition(variables.encoderpos, 0, 10);

		//resets gyro to zero
		sensors.ahrs.reset();

		//Reset encoders
		drivetrain.resetEncoders();

	}

	public void teleopPeriodic() {
		inputs.ReadValues();

		// Camera toggle between PassThrough and ObjectTracker
		boolean setCameraToTrackObjects = inputs.getIsCameraButtonPressed();
		if (setCameraToTrackObjects && vision._currentCameraSettings.getIsUsingDefaultSettings()) {
			vision._currentCameraSettings.setObjectTrackerSettings();
			vision.setJeVoisVideoMode();
			vision.setJeVoisConfigParameters();
		} else if (!setCameraToTrackObjects && !vision._currentCameraSettings.getIsUsingDefaultSettings()) {
			vision._currentCameraSettings.setDefaultSettings();
			vision.setJeVoisVideoMode();
			vision.setJeVoisConfigParameters();
		}

		//put all buttons here
		air.s_DSShifter.set(inputs.getIsDualSpeedShifterButtonPressed());
		air.s_sol4.set(inputs.getIsSolenoidFourButtonPressed());  // open intake
		air.s_sol3.set(inputs.getIsSuperButtonPressed());		  // super squeeze
		air.s_sol1.set(inputs.getIsSolenoidThreeButtonPressed()); // 
		air.s_sol5.set(inputs.getIsSolenoidFiveButtonPresses());  // 


		if(inputs.getIsSolenoidTwoButtonPressed() && inputs.getIsemJoyButtonPressed()){
			air.s_sol2.set(inputs.getIsSolenoidTwoButtonPressed());	  // release arm
		}else{
			air.s_sol2.set(false);
		}

		// NOTE: May have to make direction negative
		if(inputs.getIsemJoyButtonPressed() && inputs.getWinchSpeed()<0){
			winch.mot_Winch.set(inputs.getWinchSpeed());
		}else{
			winch.mot_Winch.set(0.0);
		}

		if(inputs.getIsIntakeButtonPressed()) {
			intake.intakeIn();
		}else if (inputs.getIsIntakeOutButtonPressed()) {
			intake.intakeOut();
		}else {
			intake.intakeStop();
		}

		// Lift postion needs testing!!
		if(inputs.getisScaleLiftButtonPressed() == true) {
			scaleLiftPosition();
		} else if(inputs.getisPortalLiftButtonPressed() == true) {
			portalLiftPosition();
		} else if(inputs.getisDefaultLiftButtonPressed() == true) {
			defaultLiftPosition();
		}else	{
			lift.mot_liftDart.set(-inputs.j_rightStick.getY());
		}

		// Getting the encoder values for the drivetrain and cooking and returning them
		drivetrain.getLeftQuadPosition();
		drivetrain.getRightQuadPosition();

		// BEGIN NAVX Gyro Code //
		// Creates a boolean for enabling or disabling NavX
		//Move to wolfDrive once created!!!
		boolean b_EnableGyroNAVX = false;

		// If robot is going forward or back ward with thin certain values, enable NavX drive straight 
		if (inputs.getTurn() <= .05 && inputs.getTurn() >= -0.05) {
			if (b_EnableGyroNAVX == false) {
				sensors.setFollowAngleNAVX(0);
			}
			b_EnableGyroNAVX = true;
			drivetrain.drive.arcadeDrive(inputs.getThrottle(), (sensors.getFollowAngleNAVX() - sensors.getPresentAngleNAVX()) * variables.GyroKp);
		}
		// If robot is doing anything other than forward or backward turn NavX Drive straight off
		else {
			drivetrain.drive.arcadeDrive(inputs.getThrottle(), inputs.getTurn());
			b_EnableGyroNAVX = false;
		}

		//Puts values on SmartDashBoard
		SmartDashboard.putNumber("Gyro-NAVX", sensors.ahrs.getAngle());
		SmartDashboard.putNumber("Air PSI", sensors.getAirPressurePsi());
		SmartDashboard.putNumber("left side inches", drivetrain.getLeftQuadPosition());
		SmartDashboard.putNumber("right side inches", drivetrain.getRightQuadPosition());
		SmartDashboard.putNumber("Lift Pot", sensors.analogLiftPot.get());

		// Updating the values put on SmartDashboard
		SmartDashboard.updateValues();
	}

	// Called during periodic, if it sees jevois it tells you how long it took to connect and if it does not connect it tries to reconnect


	// Lift Position methods
	//may need to add an else statement
	//To go up make it negative

	public void scaleLiftPosition() {
		if(sensors.analogLiftPot.get() > variables.scaleLiftPot) {
			lift.mot_liftDart.set(-0.75);
		}else if (sensors.analogLiftPot.get() == variables.scaleLiftPot) {
			lift.mot_liftDart.set(0.0);
		}
	}	

	public void portalLiftPosition() {
		if(sensors.analogLiftPot.get() > variables.portalLiftPot) {
			lift.mot_liftDart.set(-0.50);
		}else if (sensors.analogLiftPot.get() < variables.portalLiftPot) {
			lift.mot_liftDart.set(0.50);
		}else if (sensors.analogLiftPot.get() < 10 && variables.portalLiftPot> 10 ){
			lift.mot_liftDart.set(0.0);
		}
	}

	public void defaultLiftPosition() {
		if(sensors.analogLiftPot.get() < variables.defaultLiftPot) {
			lift.mot_liftDart.set(0.75);
		}else if (sensors.analogLiftPot.get() == variables.defaultLiftPot) {
			lift.mot_liftDart.set(0.0);

		}
	}

	// I don't think we need this
	public void centerStart() {}
	// I don't think we need this
	public void rightSideStart() {}
	// I don't think we need this
	public void leftSideStart() {}

	// When no Auton is called this one will be run, we just sit there
	public void DoNothingAuton() {
		if (autonChooser == doNothingAuton) {}
	}

	// The most basic Auton: Drive forward 11 feet and stop, ready testing and tuning!!!!!
	// Replace with jordan's version
	public void driveBaseLineStraight() {
		if (drivetrain.getLeftQuadPosition() < 132 && drivetrain.getRightQuadPosition() < 132) {
			drivetrain.drive.arcadeDrive(-0.60,(sensors.getFollowAngleNAVX() - sensors.getPresentAngleNAVX()) * variables.GyroKp);
		}else {
			drivetrain.drive.arcadeDrive(0, 0);
		}
	} //ready for testing 
	// Replace with jordan's version
	public void centerDriveBaseLineToLeftOfPile() {
		if (drivetrain.getLeftQuadPosition() < 80 && drivetrain.getRightQuadPosition() < 80) {
			drivetrain.drive.arcadeDrive(-0.50,(sensors.getFollowAngleNAVX() - sensors.getPresentAngleNAVX()) * variables.GyroKp);
		}else if (drivetrain.getLeftQuadPosition() >= 80 && drivetrain.getRightQuadPosition() >= 80) {
			if(sensors.getPresentAngleNAVX() < 270) {
				drivetrain.drive.arcadeDrive(-0.50, sensors.getPresentAngleNAVX() * variables.autoTurnKp);
			}else if (sensors.getPresentAngleNAVX() >= 270) {
				drivetrain.drive.arcadeDrive(-0.50,(sensors.getFollowAngleNAVX() - sensors.getPresentAngleNAVX()) * variables.GyroKp);
				sensors.ahrs.reset();
			}
		}else if (drivetrain.getLeftQuadPosition() < 130 && drivetrain.getRightQuadPosition() < 130) {
			drivetrain.drive.arcadeDrive(-0.50,(sensors.getFollowAngleNAVX() - sensors.getPresentAngleNAVX()) * variables.GyroKp);
		}else if (drivetrain.getLeftQuadPosition() >= 130 && drivetrain.getRightQuadPosition() >= 130)	{
			if(sensors.getPresentAngleNAVX() < 90) {
				drivetrain.drive.arcadeDrive(-0.50, sensors.getPresentAngleNAVX() * variables.autoTurnKp);
			}else if(sensors.getPresentAngleNAVX() >= 90) {
				drivetrain.drive.arcadeDrive(-0.50,(sensors.getFollowAngleNAVX() - sensors.getPresentAngleNAVX()) * variables.GyroKp);
			}
		}else if(drivetrain.getLeftQuadPosition() < 185 && drivetrain.getRightQuadPosition() < 185) {
			drivetrain.drive.arcadeDrive(-0.50,(sensors.getFollowAngleNAVX() - sensors.getPresentAngleNAVX()) * variables.GyroKp);
		}else if(drivetrain.getLeftQuadPosition() >= 185 && drivetrain.getRightQuadPosition() >= 185) {
			drivetrain.drive.arcadeDrive(0.0, 0.0);
		}

	}//Ready for testing and tuning
	// Replace with Jordan's version
	public void centerDriveBaseLineToRightOfPile() {}//will be similar to centerDriveBaseLineToLeftOfPile() just needs testing and tuning first


	// Rewrite this
	public void leftDrivetoLeftSideScale() {
		if (drivetrain.getLeftQuadPosition() < 122 && drivetrain.getRightQuadPosition() < 122 ) {
			drivetrain.drive.arcadeDrive(-0.50,(sensors.getFollowAngleNAVX() - sensors.getPresentAngleNAVX()) * variables.GyroKp);
		}else if (drivetrain.getLeftQuadPosition() >= 122 && drivetrain.getRightQuadPosition() >= 122){
			if(sensors.getPresentAngleNAVX() > 345){
				drivetrain.drive.arcadeDrive(-0.50, sensors.getPresentAngleNAVX() * variables.autoTurnKp);
			}else if (sensors.getPresentAngleNAVX() <= 345){
				drivetrain.drive.arcadeDrive(0.0, 0.0);
				sensors.ahrs.reset();
			}
		}else if(drivetrain.getLeftQuadPosition() < 156 && drivetrain.getRightQuadPosition() < 156){
			drivetrain.drive.arcadeDrive(-0.50,(sensors.getFollowAngleNAVX() - sensors.getPresentAngleNAVX()) * variables.GyroKp);
		}else if(drivetrain.getLeftQuadPosition() >= 156 && drivetrain.getRightQuadPosition() >= 156){
			if(sensors.getPresentAngleNAVX() < 15 ){
				drivetrain.drive.arcadeDrive(-0.50, sensors.getPresentAngleNAVX() * variables.autoTurnKp);
			}else if(sensors.getPresentAngleNAVX() <= 15){
				drivetrain.drive.arcadeDrive(-0.50,(sensors.getFollowAngleNAVX() - sensors.getPresentAngleNAVX()) * variables.GyroKp);
			}
		}else if(drivetrain.getLeftQuadPosition() < 289 && drivetrain.getRightQuadPosition() <289){
			drivetrain.drive.arcadeDrive(-0.50,(sensors.getFollowAngleNAVX() - sensors.getPresentAngleNAVX()) * variables.GyroKp);
		}else if(drivetrain.getLeftQuadPosition() >= 289 && drivetrain.getRightQuadPosition() >= 289){
			drivetrain.drive.arcadeDrive(0.0, 0.0);
		}
	}
	// Rewrite this
	public void leftFarSideScale(){

		if (drivetrain.getLeftQuadPosition() < 253 && drivetrain.getRightQuadPosition() < 253 ){
			drivetrain.drive.arcadeDrive(-0.50,(sensors.getFollowAngleNAVX() - sensors.getPresentAngleNAVX()) * variables.GyroKp);
		}else if (drivetrain.getLeftQuadPosition() >= 253 && drivetrain.getRightQuadPosition() >= 253 ){
			if (sensors.getPresentAngleNAVX() < 90){
				drivetrain.drive.arcadeDrive(-0.50, sensors.getPresentAngleNAVX() * variables.autoTurnKp);
			}else if (sensors.getPresentAngleNAVX() >= 90){
				drivetrain.drive.arcadeDrive(-0.50,(sensors.getFollowAngleNAVX() - sensors.getPresentAngleNAVX()) * variables.GyroKp);
			}
		}else if (drivetrain.getLeftQuadPosition() < 436 && drivetrain.getRightQuadPosition() < 436 ){
			drivetrain.drive.arcadeDrive(-0.50,(sensors.getFollowAngleNAVX() - sensors.getPresentAngleNAVX()) * variables.GyroKp);
		}else if (drivetrain.getLeftQuadPosition() >= 436 && drivetrain.getRightQuadPosition() >= 436 ){
			if(sensors.getPresentAngleNAVX() > 255){
				drivetrain.drive.arcadeDrive(-0.50, sensors.getPresentAngleNAVX() * variables.autoTurnKp);
			}else if(sensors.getPresentAngleNAVX() <= 255){
				drivetrain.drive.arcadeDrive(-0.50,(sensors.getFollowAngleNAVX() - sensors.getPresentAngleNAVX()) * variables.GyroKp);
			}
		}else if (drivetrain.getLeftQuadPosition() < 494 && drivetrain.getRightQuadPosition() < 494 ){
			drivetrain.drive.arcadeDrive(-0.50,(sensors.getFollowAngleNAVX() - sensors.getPresentAngleNAVX()) * variables.GyroKp);
		}else if (drivetrain.getLeftQuadPosition() >= 494 && drivetrain.getRightQuadPosition() >= 494 ){
			drivetrain.drive.arcadeDrive(0.0, 0.0);
		}
	}




	public void jordansDriveBaseline(){

		if (startSelected == leftSideStart || startSelected == rightSideStart) {
			jordansDriveBaselineSides();
		}
		else if (startSelected == centerStartThenRight){
			jordansDriveBaselineCenterThenRight();
		}
		else if (startSelected == centerStartThenLeft){
			jordansDriveBaselineCenterThenLeft();
		}

	}

	public void jordansDriveBaselineSides(){

		if (autonCounter == 1){
			driveTo(120,0.5);
		}
	}

	public void jordansDriveBaselineCenterThenRight(){

		if (autonCounter == 1){
			driveTo(60,0.5);
		}
		else if (autonCounter == 2) {
			turnTo(45,0.5);
		}
		else if (autonCounter == 3) {
			driveTo(85,0.5);
		}
		else if (autonCounter == 4) {
			turnTo(45,-0.5);
		}
	}

	public void jordansDriveBaselineCenterThenLeft(){

		if (autonCounter == 1){
			driveTo(60,0.5);
		}
		else if (autonCounter == 2) {
			turnTo(45,-0.5);
		}
		else if (autonCounter == 3) {
			driveTo(85,0.5);
		}
		else if (autonCounter == 4) {
			turnTo(45,0.5);
		}
	}


	public void testAuton(){

		if (autonCounter == 1){
			liftTo(150,1);
		}

		else if (autonCounter == 2){
			driveTo(72, 0.5);
		}

		else if (autonCounter == 3){
			turnTo(180, 0.5);
		}

		else if (autonCounter == 4){
			driveTo(72,0.5);
		}

		else if (autonCounter == 5){
			eject();
		}
	}








	public void getGameData(){
		ownership = ds.getGameSpecificMessage();

		if (ownership.length()>0){
			if (ownership.charAt(0)=='L'){
				ownership0 = "L";
			}
			else{
				ownership0 = "R";
			}
			if (ownership.charAt(1)=='L'){
				ownership1 = "L";
			}
			else{
				ownership1 = "R";
			}
			if (ownership.charAt(2)=='L'){
				ownership2 = "L";
			}
			else{
				ownership2 = "R";
			}
			System.out.println(ownership0 + ownership1 + ownership2);
		}
	}




	// steps through the auton counter, stops drive, and resets all sensors
	public void nextStep(){
		drivetrain.stop();
		sensors.ahrs.reset();
		drivetrain.resetEncoders();
		timer.reset();
		timer.start();
		autonCounter++;
	}


	// drives to distance in inches at given speed.  Then calls nextStep()
	// IMPORTANT: distance always positive.  Speed determines forward/backward
	public void driveTo(double distance, double speed){
		distance = distanceAdjustment * Math.abs(distance);

		if (speed > 0){
			if (drivetrain.getLeftQuadPosition() < distance){
				drivetrain.autonDrive(speed, 0);
			}
			else {
				nextStep();
			}
		}
		else {
			if (drivetrain.getLeftQuadPosition() > -distance){
				drivetrain.autonDrive(speed, 0);
			}
			else {
				nextStep();
			}
		}
	}


	// turns to the given angle.  Positive is to the right.  Then calls nextStep()
	// IMPORTANT: angle always positive.  Speed determines forward/backward

	public void turnTo(double angle, double speed){
		angle = Math.abs(angle);
		if (speed > 0){
			if(sensors.ahrs.getAngle() < angle){
				drivetrain.autonDrive(0, speed);
			}
			else{
				nextStep();
			}
		}
		else {
			if(sensors.ahrs.getAngle() > -angle){
				drivetrain.autonDrive(0, speed);
			}
			else{
				nextStep();
			}
		}
	}

	public void liftTo(double height, double speed){


		if (sensors.analogLiftPot.get()< height-20){
			lift.mot_liftDart.set(speed);
		}
		else if (sensors.analogLiftPot.get() <height-10){
			lift.mot_liftDart.set(speed/2);
		}
		else if (sensors.analogLiftPot.get()> height+20){
			lift.mot_liftDart.set(-speed);
		}
		else if (sensors.analogLiftPot.get()>height+10){
			lift.mot_liftDart.set(-speed/2);
		}
		else if ((sensors.analogLiftPot.get() > (height-10) && sensors.analogLiftPot.get() < (height+10)) || timer.get() > 2){
			lift.mot_liftDart.set(0);
			nextStep();
		}
	}

	public void eject (){
		if (timer.get() <1){
			intake.intakeOut();
		}
		else{
			intake.intakeStop();
			nextStep();
		}
	}

	public void intake (){
		if (timer.get() <1){
			intake.intakeIn();
		}
		else{
			intake.intakeStop();
			nextStep();
		}
	}


}
