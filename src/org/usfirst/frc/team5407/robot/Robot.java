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
	final String testAuton = "Test Auton";
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
	final double autonLiftStart = 200;
	
	//test and change this number
	final double maxLiftHeight = 313;

	final double distanceAdjustment = 1.376;  //REMOVE THE 1.042 when we switch to the real robot
	int autonCounter;
	
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
		AutonChooser.addDefault("Base Line And Switch", baseLineAndSwitch);
		SmartDashboard.putData("Auton Choices", AutonChooser);

		StartChooser = new SendableChooser<String>();
		StartChooser.addDefault("Center Start Then Right", centerStartThenRight);
		StartChooser.addObject("Center Start Then Left", centerStartThenLeft);
		StartChooser.addObject("Left Side Start", leftSideStart);
		StartChooser.addObject("Right Side Start", rightSideStart);
		StartChooser.addObject("Center Start", centerStart);
		SmartDashboard.putData("Start Choices", StartChooser);

		// SmartDashboard.updateValues();
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
		drivetrain.frontLeftDriveMotor.setNeutralMode(NeutralMode.Brake);
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
		getGameData();

		// Getting the encoder values for the drivetrain and cooking and
		// returning them
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
		} else if (autonChooser == driveBaseline) {
			driveBaseline();
		} else if (autonChooser == baseLineAndSwitch){
			baseLineAndSwitch();
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
			air.s_sol2.set(inputs.getIsSolenoidTwoButtonPressed()); // release
																	// arm
		} else {
			air.s_sol2.set(false);
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
		
		// Updating the values put on SmartDashboard
		SmartDashboard.updateValues();
	}

	// Called during periodic, if it sees jevois it tells you how long it took
	// to connect and if it does not connect it tries to reconnect
	
	// When no Auton is called this one will be run, we just sit there
	public void DoNothingAuton() {
		if (autonChooser == doNothingAuton) {
		}
	}

	
	//subtract 5 from any angle you want to go to

	public void baseLineAndSwitch() {
		if (startSelected == leftSideStart && ownership0 == "L"){
			driveBaseline();
		}
		else if (startSelected == rightSideStart && ownership0 == "R"){
			driveBaseline();
		}
		else if ((startSelected == leftSideStart && ownership0 == "R")||
				(startSelected == rightSideStart && ownership0 == "L")){
			aroundTheBack();
		}
		else if (startSelected == centerStart){
			if (ownership0 == "L"){
				startSelected = centerStartThenLeft;
			}
			else {
				startSelected = centerStartThenRight;
			}
			driveBaseline();
		}
	}


	public void driveBaseline() {

		if (startSelected == leftSideStart || startSelected == rightSideStart) {
			driveBaselineSides();
		} else if (startSelected == centerStartThenRight) {
			driveBaselineCenterThenRight();
		} else if (startSelected == centerStartThenLeft) {
			driveBaselineCenterThenLeft();
		}

	}

	public void driveBaselineSides() {

		
		if (autonCounter == 1) {
			liftTo(autonLiftStart,0.50);
		}
		else if (autonCounter == 2) {
			driveTo(140, 0.65);
			if ((startSelected == leftSideStart && ownership0 == "R")
				|| (startSelected == rightSideStart && ownership0 == "L")){
				autonCounter = 0;
			}
		}
		else if (autonCounter == 3){
			if (startSelected == leftSideStart){
				turnTo(75, 0.50);
			}
			else {
				turnTo(75, -0.50);
			}
		}
		else if (autonCounter == 4){
			driveTo(30, 0.65);
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
			liftTo(autonLiftStart,0.50);
		}
		else if (autonCounter == 2) {
			driveTo(12, 0.50);
		} else if (autonCounter == 3) {
			turnTo(40, 0.50);
		} else if (autonCounter == 4) {
			driveTo(95, 0.65);
		} else if (autonCounter == 5) {
			turnTo(80, -0.50);
		} else if (autonCounter == 6){
			driveTo(30,0.65);
		} else if (autonCounter == 7){
			if (ownership0 == "R"){
				eject();
			}
		}
	}

	public void driveBaselineCenterThenLeft() {

		if (autonCounter == 1) {
			liftTo(autonLiftStart,0.50);
		}
		else if (autonCounter == 2) {
			driveTo(12, 0.50);
		} else if (autonCounter == 3) {
			turnTo(40, -0.50);
		} else if (autonCounter == 4) {
			driveTo(85, 0.65);
		} else if (autonCounter == 5) {
			turnTo(40, 0.50);
		} else if (autonCounter == 6){
			driveTo(14,0.65);
		} else if (autonCounter == 7){
			if (ownership0 == "L"){
				eject();
			}
		}
	}

	public void aroundTheBack(){
		if (autonCounter == 1) {
			liftTo(autonLiftStart,0.5);
		}else if (autonCounter == 2) {
			driveTo(200, 0.90);
		}
		else if (autonCounter == 3){
			if (startSelected == leftSideStart){
				turnTo(60,0.60);
			}
			else {
				turnTo(60,-0.60);
			}
		}
		else if (autonCounter == 4){
			driveTo(135, 0.90);
		}
		else if (autonCounter == 5){
			if (startSelected == leftSideStart){
				turnTo(60,0.50);
			}
			else {
				turnTo(60,-0.50);
			}
		}
		else if (autonCounter == 6){
			driveTo(12,0.90);
		}
		else if (autonCounter == 7){
			eject();
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
	
	public void closeScale(){
		if (autonCounter == 1){
			liftTo(autonLiftStart,0.50);
		}
		else if (autonCounter == 2){
			driveTo(232, 0.75);
		}
		else if (autonCounter == 3){
			liftTo(300,0.5);
		}
		else if (autonCounter == 4){
			eject();
		}
	}

	public void farScale(){
		if (autonCounter == 1){
			driveTo(240, 1);
		}else if (autonCounter == 2){
			turnTo(90, .80);
		}else if (autonCounter == 3){
			driveTo(208, .90);
		}else if (autonCounter == 4){
			turnTo(15, -.75);
		}else if (autonCounter == 5){
			liftTo(maxLiftHeight, .75);
		}else if (autonCounter == 6){
			driveTo(40, .70);
		}else if (autonCounter == 7){
			eject();
		}
	}
	
	public void testAuton() {

		if (autonCounter == 1) {
			turnTo(85, .80);
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
	}

	// drives to distance in inches at given speed. Then calls nextStep()
	// IMPORTANT: distance always positive. Speed determines forward/backward
	public void driveTo(double distance, double speed) {
		distance = distanceAdjustment * Math.abs(distance);

		if (timer.get() > 5){
			drivetrain.autonDrive(0,0);
			nextStep();
		}else if (drivetrain.getAveragePosition() < distance - 24){
			drivetrain.autonDrive(speed, -(sensors.getPresentAngleNAVX() * variables.autoDriveStraightKp));
		}else if (drivetrain.getAveragePosition() < distance - 12){
			drivetrain.autonDrive((speed / 2), -(sensors.getPresentAngleNAVX() * variables.autoDriveStraightKp));
		}else if (drivetrain.getAveragePosition() > distance + 24){
			drivetrain.autonDrive(-speed, -(sensors.getPresentAngleNAVX() * variables.autoDriveStraightKp));
		}else if (drivetrain.getAveragePosition() > distance + 12){
			drivetrain.autonDrive(-(speed / 2), -(sensors.getPresentAngleNAVX() * variables.autoDriveStraightKp));
		}else if (drivetrain.getAveragePosition() > (distance - 2) && drivetrain.getAveragePosition() < (distance + 2)){
			drivetrain.autonDrive(0.0, 0.0);
			nextStep();
		}
	}

	// turns to the given angle. Positive is to the right. Then calls nextStep()
	// IMPORTANT: angle always positive. Speed determines forward/backward

	public void turnTo(double angle, double speed) {
		angle = Math.abs(angle);
		
		if (timer.get() >3){
			drivetrain.autonDrive(0,0);
			nextStep();
		}else if (sensors.ahrs.getAngle() < (angle - 5) && sensors.ahrs.getAngle() < (angle + 5)){
			if (speed > 0) {
				if (sensors.ahrs.getAngle() < angle) {
					drivetrain.autonDrive(0, speed);
				} else {
					straightenOut(angle);
				}
			} else {
				if (sensors.ahrs.getAngle() > -angle) {
					drivetrain.autonDrive(0, speed);
				} else {
					straightenOut(angle);
				}
	}	} else if  (sensors.ahrs.getAngle() > (angle - 5) && sensors.ahrs.getAngle() > (angle + 5)){
		drivetrain.autonDrive(0, 0);
		nextStep();
		}
	}	

	public void straightenOut(double angle){
		if (timer.get() < 3){
			drivetrain.autonDrive(0, (angle -(sensors.getPresentAngleNAVX()))* 0.038) ;
		}
		else {
			nextStep();
		}
	}
	
	public void liftTo(double height, double speed) {

		if (timer.get() > 2){
			lift.mot_liftDart.set(0);
			nextStep();
		}
		else if (sensors.analogLiftPot.get() < height - 15) {
			lift.mot_liftDart.set(speed);
		} else if (sensors.analogLiftPot.get() < height - 5) {
			lift.mot_liftDart.set(speed / 2);
		} else if (sensors.analogLiftPot.get() > height + 15) {
			lift.mot_liftDart.set(-speed);
		} else if (sensors.analogLiftPot.get() > height + 5) {
			lift.mot_liftDart.set(-speed / 2);
		} else if (sensors.analogLiftPot.get() > (height - 5) && sensors.analogLiftPot.get() < (height + 5)) {
			lift.mot_liftDart.set(0);
			nextStep();
		}
	}

	public void eject() {
		if (timer.get() < 1) {
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


}
