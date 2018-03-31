/*----------------------------------------------------------------------------*/
/* Copyright (c) 2017-2018 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package org.usfirst.frc.team5407.robot;

import com.ctre.phoenix.motorcontrol.NeutralMode;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.IterativeRobot;
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

	// Autos, creating string for new auto and has a sendable chooser at the end
	// of it
	final String doNothingAuton = "Do Nothing!!";
	final String driveBaseLineStraight = "Drive Straight To BaseLine "; // Needs
	// Testing
	final String driveBaseline = "Drive to Baseline";
	final String baseLineAndSwitch = "Base Line And Switch";
	final String switchThenScale = "Switch Then Scale";
	final String scaleThenSwitch = "Scale Then Switch";
	final String eitherScale = "Either Scale";
	final String testAuton = "Test Auton";
	final String sameSideSwitchAndScale = "Same Side Switch And Scale";
	final String sameSideScaleAndSwitch = "Same Side Scale And Switch";
	private String autonChooser;
	private SendableChooser<String> AutonChooser;

	final String leftSideStart = "Left Side Start";
	final String centerStartThenRight = "Center Start Then Right";
	final String centerStartThenLeft = "Center Start Then Left";
	final String rightSideStart = "Right Side Start";
	final String centerStart = "Center Start";
	private String startSelected;
	private SendableChooser<String> StartChooser;

	String ownership;
	String ownership0;
	String ownership1;
	String ownership2;

	// Lift goes to this height at the beginning of each match to avoid cube hitting floor
	final double autonLiftStart = 287;

	//test and change this number
	final double maxLiftHeight = 104;

	final double distanceAdjustment = 1.376;  //REMOVE THE 1.042 when we switch to the real robot
	int autonCounter;
	double turnDirection;

	@Override
	public void robotInit() {
		// Makes classes recognized in program and execute
		drivetrain = new DriveTrain();
		sensors = new Sensors();
		inputs = new Inputs(0, 1, 2);
		variables = new Variables();
		lift = new Lift(0);
		intake = new Intake(1, 2);
		winch = new Winch(3);
		vision = new Vision();
		timer = new Timer();
		ds = DriverStation.getInstance();

		vision.setJeVoisVideoMode();

		// Calls 4 solenoids in the air class
		air = new Air(0, 1, 2, 3, 4, 5, 6);

		AutonChooser = new SendableChooser<String>();
		AutonChooser.addDefault("Do Nothing!!", doNothingAuton);
		/*AutonChooser.addObject("Drive Straight To BaseLine ", driveBaseLineStraight);
		AutonChooser.addObject("Center Drive To Left Of Pile", centerDriveBaseLineToLeftOfPile);
		AutonChooser.addObject("Center Drive To Right Of Pile", centerDriveBaseLineToRightOfPile);
		AutonChooser.addObject("Left Drive to Left Side Scale", leftDrivetoLeftSideScale);*/
		AutonChooser.addObject("Drive to Baseline", driveBaseline);
		AutonChooser.addObject("Test Auton", testAuton);
		AutonChooser.addObject("Switch Then Scale", switchThenScale);
		AutonChooser.addObject("Scale Then Switch", scaleThenSwitch);
		AutonChooser.addObject("Either Scale", eitherScale);
		AutonChooser.addDefault("Base Line And Switch", baseLineAndSwitch);
		AutonChooser.addObject("Same Side Switch And Scale", sameSideSwitchAndScale);
		AutonChooser.addObject("Same Side Scale And Switch", sameSideScaleAndSwitch);
		SmartDashboard.putData("Auton Choices", AutonChooser);

		StartChooser = new SendableChooser<String>();
		StartChooser.addDefault("Center Start Then Right", centerStartThenRight);
		StartChooser.addObject("Center Start Then Left", centerStartThenLeft);
		StartChooser.addObject("Left Side Start", leftSideStart);
		StartChooser.addObject("Right Side Start", rightSideStart);
		StartChooser.addObject("Center Start", centerStart);
		SmartDashboard.putData("Start Choices", StartChooser);

		// SmartDashboard.updateValues();
		
		air.s_sol2.set(true);
	}

	public void robotPeriodic() {}

	public void disabledInit() {
		air.s_sol2.set(true);
	}

	public void disabledPeriodic() {

		autonChooser = AutonChooser.getSelected();
		SmartDashboard.putString("My Selected Auton is ", autonChooser);

		startSelected = StartChooser.getSelected();
		SmartDashboard.putString("Robot Start Position is ", startSelected);
		
		air.s_sol2.set(true);
	}

	public void autonomousInit() {
		// Zero and initialize values for auton
		air.initializeAir();
		drivetrain.frontLeftDriveMotor.setNeutralMode(NeutralMode.Brake);
		drivetrain.backLeftDriveSlave.setNeutralMode(NeutralMode.Brake);
		drivetrain.frontRightDriveMotor.setNeutralMode(NeutralMode.Brake);
		drivetrain.backRightDriveSlave.setNeutralMode(NeutralMode.Brake);

		air.s_sol6.set(true);

		// resets both drive encoders to zero
		drivetrain.frontLeftDriveMotor.setSelectedSensorPosition(variables.encoderpos, 0, 10);
		drivetrain.frontRightDriveMotor.setSelectedSensorPosition(variables.encoderpos, 0, 10);

		// resets gyro to zero
		sensors.ahrs.reset();

		// Reset encoders
		drivetrain.resetEncoders();

		autonCounter = 1;

		timer.reset();
		timer.start();

		getGameData();
	}

	public void autonomousPeriodic() {
		


		//getGameData();

		// Gets auto choosen and displays it on SmartDashboard

		autonChooser = AutonChooser.getSelected();
		SmartDashboard.putString("My Selected Auton is ", autonChooser);

		startSelected = StartChooser.getSelected();
		SmartDashboard.putString("Robot Start Position is ", startSelected);

		// If else statement for auton selection
		if (autonChooser == doNothingAuton) {
			// do nothing
		} 
		else if (autonChooser == driveBaseline) {
			driveBaseline();
		} 
		else if (autonChooser == baseLineAndSwitch){
			baseLineAndSwitch();
		}
		else if (autonChooser == switchThenScale){
			switchThenScale();
		}
		else if (autonChooser == scaleThenSwitch){
			scaleThenSwitch();
		}
		else if (autonChooser == eitherScale){
			eitherScale();
		}
		else if (autonChooser == sameSideSwitchAndScale){
			sameSideSwitchAndScale();
		}
		else if (autonChooser == sameSideScaleAndSwitch){
			sameSideScaleAndSwitch();
		}
		else if (autonChooser == testAuton) {
			testAuton();
		}

		// Puts values on SmartDashboard in Auto
		SmartDashboard.putNumber("Gyro-NAVX", sensors.ahrs.getAngle());
		SmartDashboard.putNumber("Air PSI", sensors.getAirPressurePsi());
		SmartDashboard.putNumber("left side inches", drivetrain.getLeftQuadPosition());
		SmartDashboard.putNumber("right side inches", drivetrain.getRightQuadPosition());
		SmartDashboard.updateValues();

	}

	public void teleopInit() {
		// Zero and initialize all inputs and sensors for teleop
		air.initializeAir();

		drivetrain.frontLeftDriveMotor.setNeutralMode(NeutralMode.Coast);
		drivetrain.frontRightDriveMotor.setNeutralMode(NeutralMode.Coast);		
		drivetrain.backLeftDriveSlave.setNeutralMode(NeutralMode.Coast);
		drivetrain.backRightDriveSlave.setNeutralMode(NeutralMode.Coast);
		

		// resets both drive encoders to zero
		drivetrain.frontLeftDriveMotor.setSelectedSensorPosition(variables.encoderpos, 0, 10);
		drivetrain.frontRightDriveMotor.setSelectedSensorPosition(variables.encoderpos, 0, 10);

		// resets gyro to zero
		sensors.ahrs.reset();

		// Reset encoders
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

		// put all buttons here
		//	air.s_DSShifter.set(inputs.getIsDualSpeedShifterButtonPressed());
		air.s_sol4.set(inputs.getIsSolenoidFourButtonPressed()); // open intake
		air.s_sol3.set(inputs.getIsSuperButtonPressed()); // super squeeze
		air.s_sol1.set(inputs.getIsSolenoidThreeButtonPressed()); //
		air.s_sol5.set(inputs.getIsSolenoidFiveButtonPresses()); //

		if (inputs.getIsSolenoidTwoButtonPressed() && inputs.getIsemJoyButtonPressed()) {
			air.s_sol2.set(!inputs.getIsSolenoidTwoButtonPressed()); // release
			// arm
		} else {
			air.s_sol2.set(true);
		}

		if (inputs.getIsemJoyButtonPressed() && inputs.getWinchSpeed() < 0) {
			winch.mot_Winch.set(inputs.getWinchSpeed());
		} else if (inputs.getIsCameraButtonPressed() && inputs.getIsDualSpeedShifterButtonPressed()) {
			winch.mot_Winch.set(-inputs.getWinchSpeed());
		}else {
			winch.mot_Winch.set(0.0);
		}

		if (inputs.getIsIntakeButtonPressed()) {
			intake.intakeIn();
		} else if (inputs.getIsIntakeOutButtonPressed()) {
			intake.intakeOut();
		} else {
			intake.intakeStop();
		}

		lift.mot_liftDart.set(inputs.getLiftSpeed());

		if (drivetrain.getAverageVelocity() > 1200){
			timer.reset();
			timer.start();
			if (timer.get() > 2){
				air.s_DSShifter.set(false);
			}else if (timer.get() <= 2){
				air.s_DSShifter.set(true);
			}
		}else if(drivetrain.getAverageVelocity() < 1200) {
			if (inputs.getIsDualSpeedShifterButtonPressed()){
				air.s_DSShifter.set(inputs.getIsDualSpeedShifterButtonPressed());
			}else {
				air.s_DSShifter.set(false);
			}
		}


		// Getting the encoder values for the drivetrain and cooking and
		// returning them
		drivetrain.getLeftQuadPosition();
		drivetrain.getRightQuadPosition();

		// BEGIN NAVX Gyro Code //
		// Creates a boolean for enabling or disabling NavX
		// Move to wolfDrive once created!!!
		boolean b_EnableGyroNAVX = false;

		// If robot is going forward or back ward with thin certain values,
		// enable NavX drive straight
		if (inputs.getTurn() <= .05 && inputs.getTurn() >= -0.05) {
			if (b_EnableGyroNAVX == false) {
				sensors.setFollowAngleNAVX(0);
			}
			b_EnableGyroNAVX = true;
			drivetrain.drive.arcadeDrive(inputs.getThrottle(),
					(sensors.getFollowAngleNAVX() - sensors.getPresentAngleNAVX()) * variables.GyroKp);
		}
		// If robot is doing anything other than forward or backward turn NavX
		// Drive straight off
		else {
			drivetrain.drive.arcadeDrive(inputs.getThrottle(), inputs.getTurn());
			b_EnableGyroNAVX = false;
		}

		// Puts values on SmartDashBoard
		SmartDashboard.putNumber("Gyro-NAVX", sensors.ahrs.getAngle());
		SmartDashboard.putNumber("Air PSI", sensors.getAirPressurePsi());
		SmartDashboard.putNumber("left side inches", drivetrain.getLeftQuadPosition());
		SmartDashboard.putNumber("right side inches", drivetrain.getRightQuadPosition());
		SmartDashboard.putNumber("Lift Pot", sensors.analogLiftPot.get());
		SmartDashboard.putNumber("AverageVelocity", drivetrain.getAverageVelocity());
		SmartDashboard.putNumber("Average Positon", drivetrain.getAveragePosition());
		SmartDashboard.putNumber("Right Vel", drivetrain.getRightSideVelocity);
		SmartDashboard.putNumber("Left Vel", drivetrain.getLeftSideVelocity);
		

		// Updating the values put on SmartDashboard
		SmartDashboard.updateValues();
	}

	// Called during periodic, if it sees jevois it tells you how long it took
	// to connect and if it does not connect it tries to reconnect


	//subtract 5 from any angle you want to go to


	// choses which version of drive= -1;Baseline to use based on the starting position
	public void driveBaseline() {

		if (startSelected == leftSideStart || startSelected == rightSideStart) {
			driveBaselineSides();
		} 
		else if (startSelected == centerStartThenRight) {
			driveBaselineCenterThenRight();
		} 
		else if (startSelected == centerStartThenLeft) {
			driveBaselineCenterThenLeft();
		}

	}

	public void baseLineAndSwitch() {
		if ((startSelected == leftSideStart && ownership0 == "L")||(startSelected == rightSideStart && ownership0 == "R")){
			driveBaseline();
		}

		else if ((startSelected == leftSideStart && ownership0 == "R")||
				(startSelected == rightSideStart && ownership0 == "L")){
			aroundTheBack();
		}
		else if ((startSelected == centerStart)|| (startSelected == centerStartThenLeft) || (startSelected == centerStartThenRight)){

			// determines if it should go right or left around the pile
			// and changes the startSelcted from centerStart to centerStartThenLeft or centerStartThenRight
			if (ownership0 == "L"){
				startSelected = centerStartThenLeft;
			}
			else {
				startSelected = centerStartThenRight;
			}

			// then runs the appropriate version of driveBaseline
			driveBaseline();
		}
	}

	public void switchOrScale() {
		if (startSelected == "Left Side Start" && ownership0 == "L") {

		} else if (startSelected == "Left Side Start" && ownership0 == "R") {

		} else if (startSelected == "Right Side Start" && ownership0 == "L") {

		} else if (startSelected == "Right Side Start" && ownership0 == "R") {

		} else if (startSelected == "Center Start Then Left" && ownership0 == "L") {

		} else if (startSelected == "Center Start Then Left" && ownership0 == "R") {

		} else if (startSelected == "Center Start Then Right" && ownership0 == "L") {

		} else if (startSelected == "Center Start Then Right" && ownership0 == "R") {

		}
	}

	public void switchThenScale(){
		if ((startSelected == leftSideStart && ownership0 == "L")||(startSelected == rightSideStart && ownership0 == "R")){
			driveBaseline();
		}

		else if ((startSelected == leftSideStart && ownership1 == "L")||(startSelected == rightSideStart && ownership1 == "R")){
			closeScale();
		}


		else if ((startSelected == leftSideStart && ownership0 == "R")||
				(startSelected == rightSideStart && ownership0 == "L")){
			aroundTheBack();
		}
		else if (startSelected == centerStart){

			// determines if it should go right or left around the pile
			// and changes the startSelcted from centerStart to centerStartThenLeft or centerStartThenRight
			if (ownership0 == "L"){
				startSelected = centerStartThenLeft;
			}
			else {
				startSelected = centerStartThenRight;
			}

			// then runs the appropriate version of driveBaseline
			driveBaseline();
		}
	}

	public void scaleThenSwitch(){

		if ((startSelected == leftSideStart && ownership1 == "L")||(startSelected == rightSideStart && ownership1 == "R")){
			closeScale();
		}
		else if ((startSelected == leftSideStart && ownership0 == "L")||(startSelected == rightSideStart && ownership0 == "R")){
			driveBaseline();
		}

		else if ((startSelected == leftSideStart && ownership0 == "R")||    //uncomment after match 48
				(startSelected == rightSideStart && ownership0 == "L")){
			aroundTheBack();
		}
		else if (startSelected == centerStart){

			// determines if it should go right or left around the pile
			// and changes the startSelcted from centerStart to centerStartThenLeft or centerStartThenRight
			if (ownership0 == "L"){
				startSelected = centerStartThenLeft;
			}
			else {
				startSelected = centerStartThenRight;
			}

			// then runs the appropriate version of driveBaseline
			driveBaseline();
		}
	}

	public void eitherScale(){
		if ((startSelected == leftSideStart && ownership1 == "L")||(startSelected == rightSideStart && ownership1 == "R")){
			closeScale();
		}
		else if ((startSelected == leftSideStart && ownership1 == "R")||(startSelected == rightSideStart && ownership1 == "L")){
			farScale();
		}
		else if (startSelected == centerStart){

			// determines if it should go right or left around the pile
			// and changes the startSelcted from centerStart to centerStartThenLeft or centerStartThenRight
			if (ownership0 == "L"){
				startSelected = centerStartThenLeft;
			}
			else {
				startSelected = centerStartThenRight;
			}

			// then runs the appropriate version of driveBaseline
			driveBaseline();
		}
	}

	
	public void sameSideSwitchAndScale(){
		if ((startSelected == leftSideStart && ownership0 == "L")||(startSelected == rightSideStart && ownership0 == "R")){
			driveBaseline();
		}

		else if ((startSelected == leftSideStart && ownership1 == "L")||(startSelected == rightSideStart && ownership1 == "R")){
			closeScale();
		}


		else if ((startSelected == leftSideStart && ownership0 == "R")||
				(startSelected == rightSideStart && ownership0 == "L")){
			driveBaseline();
		}
		else if (startSelected == centerStart){

			// determines if it should go right or left around the pile
			// and changes the startSelcted from centerStart to centerStartThenLeft or centerStartThenRight
			if (ownership0 == "L"){
				startSelected = centerStartThenLeft;
			}
			else {
				startSelected = centerStartThenRight;
			}

			// then runs the appropriate version of driveBaseline
			driveBaseline();
		}
	}
	

	public void sameSideScaleAndSwitch(){

		if ((startSelected == leftSideStart && ownership1 == "L")||(startSelected == rightSideStart && ownership1 == "R")){
			closeScale();
		}
		else if ((startSelected == leftSideStart && ownership0 == "L")||(startSelected == rightSideStart && ownership0 == "R")){
			driveBaseline();
		}

		else if ((startSelected == leftSideStart && ownership0 == "R")||    //uncomment after match 48
				(startSelected == rightSideStart && ownership0 == "L")){
			driveBaseline();
		}
		else if (startSelected == centerStart){

			// determines if it should go right or left around the pile
			// and changes the startSelcted from centerStart to centerStartThenLeft or centerStartThenRight
			if (ownership0 == "L"){
				startSelected = centerStartThenLeft;
			}
			else {
				startSelected = centerStartThenRight;
			}

			// then runs the appropriate version of driveBaseline
			driveBaseline();
		}
	}
	
	// When no Auton is called this one will be run, we just sit there

	public void DoNothingAuton() {
		if (autonChooser == doNothingAuton) {
			// Do nothing
		}
	}

	public void driveBaselineSides() {

		
		if (autonCounter == 1) {
			liftTo(autonLiftStart, 1);
		}

		else if (autonCounter == 2) {
			driveTo(150, 1, 5);
			if ((startSelected == leftSideStart && ownership0 == "R")
					|| (startSelected == rightSideStart && ownership0 == "L")){
			}
		}
		 

		else if (autonCounter == 3){
			if ((startSelected == leftSideStart && ownership0 == "L")
					|| (startSelected == rightSideStart && ownership0 == "R")){
				if (startSelected == leftSideStart){
					turnTo(85, 0.80);
				}
				else {
					turnTo(85, -0.80);
				}
			}
			
			
		}
		else if (autonCounter == 4){
			
			if ((startSelected == leftSideStart && ownership0 == "L")
					|| (startSelected == rightSideStart && ownership0 == "R")){
				driveTo(30, 0.80, 2);
			}
			
		}
		else if (autonCounter == 5){
			if ((startSelected == leftSideStart && ownership0 == "L")
					|| (startSelected == rightSideStart && ownership0 == "R")){
				eject();
			}
		}
	}

	public void driveBaselineCenterThenRight() {


	
		if (autonCounter == 1) {
			liftTo(autonLiftStart,1);
		}
		else if (autonCounter == 2) {
			driveTo(12, 1, 1);
		} 
		else if (autonCounter == 3) {
			turnTo(50, 0.65);
		} 
		else if (autonCounter == 4) {
			driveTo(48, .90, 4);
		} 
		else if (autonCounter == 5) {
			turnTo(65, -0.65);
		} 
		else if (autonCounter == 6){
			driveTo(74,1, 2);
		} 
		else if (autonCounter == 7){
			if (ownership0 == "R"){
				eject();
			}
		}
	}

	public void driveBaselineCenterThenLeft() {

		
		if (autonCounter == 1) {
			liftTo(autonLiftStart,1);
		}
		else if (autonCounter == 2) {
			driveTo(12, 1, 1);
		} 
		else if (autonCounter == 3) {
			turnTo(50, -0.65);
		} 
		else if (autonCounter == 4) {
			driveTo(80, 0.90, 4);
		} 
		else if (autonCounter == 5) {
			turnTo(45, 0.65);
		} 
		else if (autonCounter == 6){
			driveTo(50,1, 2);
		} 
		else if (autonCounter == 7){
			if (ownership0 == "L"){
				eject();
			}
		}
	}

	public void aroundTheBack(){

		if (autonCounter == 1) {
			liftTo(autonLiftStart, 1);
		}
		else if (autonCounter == 2) {
			driveTo(212, 1, 5);
		}
		else if (autonCounter == 3){
			if (startSelected == leftSideStart){
				turnTo(70,0.80);
			}
			else {
				turnTo(85,-0.80);
			}
		}
		else if (autonCounter == 4){
			driveTo(168, 0.90, 6);
		}
		else if (autonCounter == 5){
			if (startSelected == leftSideStart){
				turnTo(80,0.80);
			}
			else {
				turnTo(75,-0.80);
			}
		}
		else if (autonCounter == 6){
			driveTo(24, .90, 2);
		}
		else if (autonCounter == 7){
			eject();
		}
	}	

	public void closeScale(){
		if (startSelected == leftSideStart){
			turnDirection = 1;
		}
		else {
			turnDirection = -1;
		}


		
		if (autonCounter == 1){
			liftTo(autonLiftStart,1);
		}
		else if (autonCounter == 2){
			driveTo(284, 1, 7);
		}
		else if (autonCounter == 3){
			liftTo(maxLiftHeight, 1);
		}
		else if (autonCounter == 4){
			turnTo(55, turnDirection * .75); //85
		}
		else if (autonCounter == 5){
			eject();
		}
	}

	public void farScale(){

		if (startSelected == leftSideStart){
			turnDirection = 1;
		}
		else {
			turnDirection = -1;
		}


		
		if (autonCounter == 1){
			liftTo(autonLiftStart, 1);
		}
		else if (autonCounter == 2){
			driveTo(212, 1, 5);
		}
		else if (autonCounter == 3){
			turnTo(70, turnDirection * .80);
		}
		else if (autonCounter == 4){
			driveTo(205, 1, 5);
		}
		else if (autonCounter == 5){
			liftAndTurn(maxLiftHeight, 1, 90, turnDirection * -.85);
		}
		else if (autonCounter == 6){
			driveTo(30, .80, 1);
		}
		else if (autonCounter == 7){
			eject();
		}
	}

	public void testAuton() {


		if (autonCounter == 1) {
			liftTo(autonLiftStart,1);
		}


	}







	//Auton Steps to create autos
	public void getGameData() {
		ownership = ds.getGameSpecificMessage();

		if (ownership.length() > 0) {
			if (ownership.charAt(0) == 'L') {
				ownership0 = "L";
			} else {
				ownership0 = "R";
			}
			if (ownership.charAt(1) == 'L') {
				ownership1 = "L";
			} else {
				ownership1 = "R";
			}
			if (ownership.charAt(2) == 'L') {
				ownership2 = "L";
			} else {
				ownership2 = "R";
			}
			System.out.println(ownership0 + ownership1 + ownership2);
		}
	}

	// steps through the auton counter, stops drive, and resets all sensors
	public void nextStep() {
		drivetrain.stop();
		sensors.ahrs.reset();
		drivetrain.resetEncoders();
		timer.reset();
		timer.start();
		autonCounter++;
		System.out.println("Next Step "+ autonCounter);
	}

	// drives to distance in inches at given speed. Then calls nextStep()
	// IMPORTANT: distance always positive. Speed determines forward/backward

	public void driveTo(double distance, double speed, double time) {
		distance = distanceAdjustment * Math.abs(distance);

		if (timer.get() > time){
			drivetrain.autonDrive(0,0);
			nextStep();
		}
		else{
			if ((drivetrain.getAveragePosition() > (distance - 10) && drivetrain.getAveragePosition() < (distance + 10))){
				drivetrain.autonDrive(0,0);
				nextStep();
			}else if (drivetrain.getAveragePosition() < distance - 25){
				drivetrain.autonDrive(speed, -(sensors.getPresentAngleNAVX() * variables.autoDriveStraightKp));
			}else if (drivetrain.getAveragePosition() < distance - 15){
				drivetrain.autonDrive((speed / 2), -(sensors.getPresentAngleNAVX() * variables.autoDriveStraightKp));
			}else if (drivetrain.getAveragePosition() > distance + 25){
				drivetrain.autonDrive(-speed, -(sensors.getPresentAngleNAVX() * variables.autoDriveStraightKp));
			}else if (drivetrain.getAveragePosition() > distance + 15){
				drivetrain.autonDrive(-(speed / 2), -(sensors.getPresentAngleNAVX() * variables.autoDriveStraightKp));
			}
		}
	}

	// turns to the given angle. Positive is to the right. Then calls nextStep()
	// IMPORTANT: angle always positive. Speed determines forward/backward

	public void turnTo(double angle, double speed) {
		angle = Math.abs(angle);

		if ((sensors.ahrs.getAngle() > (angle - 5) && sensors.ahrs.getAngle() > (angle + 5))){
			drivetrain.autonDrive(0,0);
			nextStep();
		}
		else if (sensors.ahrs.getAngle() < (angle - 5) && sensors.ahrs.getAngle() < (angle + 5)){
			if (speed > 0) {
				if (sensors.ahrs.getAngle() < angle) {
					drivetrain.autonDrive(0, speed);
				} 
				else {
					straightenOut(angle);
				}
			} 
			else {
				if (sensors.ahrs.getAngle() > -angle) {
					drivetrain.autonDrive(0, speed);
				} 
				else {
					straightenOut(angle);
				}
			}	
		} 

	}	

	public void straightenOut(double angle){
		if (timer.get() < 0.25){
			drivetrain.autonDrive(0, (angle -(sensors.getPresentAngleNAVX()))* 0.038) ;
		}
		else {
			nextStep();
		}
	}

	public void liftTo(double height, double speed) {

		System.out.println(sensors.analogLiftPot.get());
		System.out.println(speed);
		
		if (timer.get() > 2){
			lift.mot_liftDart.set(0);
			nextStep();
		}
		else if (sensors.analogLiftPot.get() > height - 15) {
			lift.mot_liftDart.set(speed);
		} else if (sensors.analogLiftPot.get() > height - 8) {
			lift.mot_liftDart.set(speed / 2);
		} else if (sensors.analogLiftPot.get() < height + 15) {
			lift.mot_liftDart.set(-speed);
		} else if (sensors.analogLiftPot.get() < height + 8) {
			lift.mot_liftDart.set(-speed / 2);
		} else if (sensors.analogLiftPot.get() <= (height - 8) && sensors.analogLiftPot.get() >= (height + 8)) {
			lift.mot_liftDart.set(0);
			nextStep();
		}
	}

	public void liftPlus(double height, double speed) {

		if (timer.get() > 2){
			lift.mot_liftDart.set(0);
		}
		else if (sensors.analogLiftPot.get() < height - 15) {
			lift.mot_liftDart.set(speed);
		} else if (sensors.analogLiftPot.get() < height - 8) {
			lift.mot_liftDart.set(speed / 2);
		} else if (sensors.analogLiftPot.get() > height + 15) {
			lift.mot_liftDart.set(-speed);
		} else if (sensors.analogLiftPot.get() > height + 8) {
			lift.mot_liftDart.set(-speed / 2);
		} else if (sensors.analogLiftPot.get() >= (height - 8) && sensors.analogLiftPot.get() <= (height + 8)) {
			lift.mot_liftDart.set(0);
		}
	}

	public void liftAndDrive(double height, double liftSpeed, double distance, double driveSpeed, double time){
		liftPlus(height, liftSpeed);
		driveTo(distance, driveSpeed, time);
	}

	public void liftAndTurn(double height, double liftSpeed, double angle, double turnSpeed){
		liftPlus(height, liftSpeed);
		turnTo(angle, turnSpeed);
	}

	public void eject() {
		if (timer.get() < 2) {
			intake.mot_leftSideIntake.set(0.6);
			intake.mot_rightSideIntake.set(-0.6);
		} else {
			intake.intakeStop();
			nextStep();
		}
	}

	public void intake() {
		if (timer.get() < 1) {
			intake.intakeIn();
		} else {
			intake.intakeStop();
			nextStep();
		}
	}

	
	public void drop() {
		
	}

}
