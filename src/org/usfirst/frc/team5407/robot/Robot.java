/*----------------------------------------------------------------------------*/
/* Copyright (c) 2017-2018 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package org.usfirst.frc.team5407.robot;


import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 * This is a demo program showing the use of the RobotDrive class, specifically
 * it contains the code necessary to operate a robot with tank drive.
 */
public class Robot extends IterativeRobot {
	
	// Create new classes and call them here 
	Sensors sensors;
	Air air;
	DriveTrain drivetrain;
	
	// Private DifferentialDrive drive;
	private Joystick j_leftStick;
	private Joystick j_rightStick;
	
	// Gyro kp, the smaller the value the small the corrections get
	double Kp = 0.015;
	
	// Auton, creating string for new auton and has a sendable chooser at the end of it
	final String defaultAuton = "Default Auton";
	final String DriveBaseLine = "Drive BaseLine";
	String autonSelected;
	SendableChooser<String> chooser;

	@Override
	public void robotInit() {
		
		// Makes classes recongized in program and execute
		drivetrain = new DriveTrain();
		sensors = new Sensors();
		
		// Calling the 0 and 1 port for usb on driverstation computer
		j_leftStick = new Joystick(0);
		j_rightStick = new Joystick(1);
		
		// Called 4 solenoids in the air class
		air = new Air(0,1,2,3);
 	
	}
	
	public void autonInit(){
		
		// Zero and initalize values for auton 
		air.initializeAir();
		
		// Auton Chooser and its SmartDashBoard component
		autonSelected = chooser.getSelected();
		SmartDashboard.putString("My Selected Auton is ", autonSelected);
			
	}		
	
	public void autonPeriodic() {
		
		// If else statement for auton selection
		if(autonSelected == defaultAuton){
			defaultAuton();
		}
		
		else if(autonSelected == DriveBaseLine){
			DriveBaseLine();
		}
		
    		
		
	}

	public void teleopInit() {
		// Zero and initialize all inputs and sensors for teleop
		air.initializeAir();

	}

	@Override
	public void teleopPeriodic() {
		
		// Getting the encoder values for the drivetrain and cooking and returning them
		drivetrain.getLeftQuadPosition();
		drivetrain.getRightQuadPosition();
		
		// Arcade Drive using first joystick
    	double forward = -(j_leftStick.getY()); // xbox gampad left X, positive is forward
    	double turn = j_leftStick.getX(); // xbox gampad right X, positive means turn right
    	
    	// BEGIN NAVX Gyro Code //
    	// Creates a boolean for enabling or disabling NavX
    	boolean b_EnableGyroNAVX = false;
    	// If robot is going forward or back ward with thin certain values, enable NavX drive straight 
    	if (turn <= .05 && turn >=-0.05 ){
    		if(b_EnableGyroNAVX == false){sensors.setFollowAngleNAVX(0);}
    		b_EnableGyroNAVX = true;
    		drivetrain.drive.arcadeDrive(forward, (sensors.getFollowAngleNAVX()-sensors.getPresentAngleNAVX())*Kp);
    	}
    	// If robot is doing anything other than forward or backward turn NavX Drive straight off
    	else{
    		drivetrain.drive.arcadeDrive(forward, turn);
    		b_EnableGyroNAVX = false;
    	}
    	// END NAVX Gyro Code //
    	
    	// If else statement for first joystick button RB, switched between high and low gear
    	if (j_leftStick.getRawButton(6)){
    		air.s_DSShifter.set(true);
    	}
    	else{
    		air.s_DSShifter.set(false);
    	}
    	
    	// If else statement for button A on second joystick
    	if (j_rightStick.getRawButton(1)){
    		air.s_sol1.set(true);
    	}
    	else{
    		air.s_sol1.set(false);
    	}
    	
    	// If else statement for button B on second joystick
    	if (j_rightStick.getRawButton(2)){
    		air.s_sol2.set(true);
    	}
    	else{
    		air.s_sol2.set(false);
    	}
    	
    	// If else statement for #6 button on second joystick
    	if (j_rightStick.getRawButton(6)){
    		air.s_sol3.set(true);
    	}
    	else{
    		air.s_sol3.set(false);
    	}
    	
    	// Puts values on SmartDashBoard
    	SmartDashboard.putNumber("Gyro", sensors.analogGyro.getAngle());
    	SmartDashboard.putNumber("Gyro-NAVX", sensors.ahrs.getAngle());    	
    	SmartDashboard.putNumber("Gyro-NAVX", sensors.ahrs.getAngle());
    	SmartDashboard.putNumber("Air PSI", sensors.getAirPressurePsi());
	SmartDashboard.putNumber("left side inches", drivetrain.getLeftQuadPosition());
	SmartDashboard.putNumber("right side inches", drivetrain.getRightQuadPosition());
    	
	// Updating the values put on SmartDashboard
    	SmartDashboard.updateValues();
    
	}
		
	// When no Auton is called this one will be run
	public void defaultAuton(){
		if (autonSelected == defaultAuton){}
	}
	
	// The most basic Auton: Drive forward 10 feet and stop
	public void DriveBaseLine(){
	
		
	}




}
