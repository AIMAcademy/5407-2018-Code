/*----------------------------------------------------------------------------*/
/* Copyright (c) 2017-2018 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package org.usfirst.frc.team5407.robot;


import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.Preferences;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 * This is a demo program showing the use of the RobotDrive class, specifically
 * it contains the code necessary to operate a robot with tank drive.
 */
public class Robot extends IterativeRobot {
	
	Sensors sensors;
	Air air;
	DriveTrain drivetrain;
	
	//private DifferentialDrive drive;
	private Joystick j_leftStick;
	private Joystick j_rightStick;
	
	//boolean bp_MinDisplay;
	
	//gyro kp
	double Kp = 0.015;
	
	//Auton
	final String defaultAuton = "Default Auton";
	final String DriveBaseLine = "Drive BaseLine";
	String autonSelected;
	SendableChooser<String> chooser;

	@Override
	public void robotInit() {
		
		drivetrain = new DriveTrain();
		sensors = new Sensors();

		j_leftStick = new Joystick(0);
		j_rightStick = new Joystick(1);
		
		
		air = new Air(0,1,2,3);
 	
	}
	
	public void autonInit(){
		
		//zero and initalize values 
		//need to zero encoder values
		air.initializeAir();
		
		//Auton Chooser and its SmartDashBoard component
		autonSelected = chooser.getSelected();
		SmartDashboard.putString("My Selected Auton is ", autonSelected);
		
		
	}		
	
	public void autonPeriodic() {
		
		if(autonSelected == defaultAuton){
			defaultAuton();
		}
		
		else if(autonSelected == DriveBaseLine){
			DriveBaseLine();
		}
		
		//test go 10 feet or 120 inches straight 
	//	if(_frontRightMotor.InchesRS >120 && InchesLS){
			
	//	}
    		
		
	}

	public void teleopInit() {
		//zero and initialize all inputs and sensors
		air.initializeAir();

	}

	@Override
	public void teleopPeriodic() {
		
		drivetrain.getLeftQuadPosition();
		drivetrain.getRightQuadPosition();

		//checkMinDisplay();
		
		// Arcade Drive
		//
    	double forward = -(j_leftStick.getY()); // logitech gampad left X, positive is forward
    	double turn = j_leftStick.getX(); // logitech gampad right X, positive means turn right
//    	boolean b_EnableGyro = false;
//    	if (turn <= .05 && turn >=-0.05 ){
//    		if(b_EnableGyro == false){sensors.setFollowAngle(0);}
//    		b_EnableGyro = true;
//    		_drive.arcadeDrive(forward, (sensors.getFollowAngle()-sensors.getPresentAngle())*Kp);
//    	}
//    	else{
//    		_drive.arcadeDrive(forward, turn);
//    		b_EnableGyro = false;
//    	}
    	
    	// TESTING NAVX
    	boolean b_EnableGyroNAVX = false;
    	if (turn <= .05 && turn >=-0.05 ){
    		if(b_EnableGyroNAVX == false){sensors.setFollowAngleNAVX(0);}
    		b_EnableGyroNAVX = true;
    		drivetrain.drive.arcadeDrive(forward, (sensors.getFollowAngleNAVX()-sensors.getPresentAngleNAVX())*Kp);
    	}
    	else{
    		drivetrain.drive.arcadeDrive(forward, turn);
    		b_EnableGyroNAVX = false;
    	}
    	// END TESTING NAVX
    	
    	// Tested PWM variable. Data does not seem reliable or helpful. //
    	//double pwm = _frontRightMotor.getSensorCollection().getPulseWidthPosition();
    	//SmartDashboard.putNumber("Pwm", pwm);
    	
    	//xbox button RB
    	if (j_leftStick.getRawButton(6)){
    		air.s_DSShifter.set(true);
    	}
    	else{
    		air.s_DSShifter.set(false);
    	}
    	
    	//xbox button A
    	if (j_rightStick.getRawButton(1)){
    		air.s_sol1.set(true);
    	}
    	else{
    		air.s_sol1.set(false);
    	}
    	
    	//xbox button B
    	if (j_rightStick.getRawButton(2)){
    		air.s_sol2.set(true);
    	}
    	else{
    		air.s_sol2.set(false);
    	}
    	
    	if (j_rightStick.getRawButton(6)){
    		air.s_sol3.set(true);
    	}
    	else{
    		air.s_sol3.set(false);
    	}
    	
    	//Temporary to run grip
    	//tc.setClosedLoopControl(true);
    	
    	
    	SmartDashboard.putNumber("Gyro", sensors.analogGyro.getAngle());
    	SmartDashboard.putNumber("Gyro-NAVX", sensors.ahrs.getAngle());    	
    	SmartDashboard.putNumber("Gyro-NAVX", sensors.ahrs.getAngle());
    	SmartDashboard.putNumber("Air PSI", sensors.getAirPressurePsi());
//		SmartDashboard.putNumber("left side inches", drivetrain.LeftSideMagEncoder());
//		SmartDashboard.putNumber("right side inches", drivetrain.RightSideMagEncoder());
    	
    	SmartDashboard.updateValues();
    
	}
		
//	public void checkMinDisplay(){
//		this.bp_MinDisplay = Preferences.getInstance().getBoolean("R_MinDisplay(bool)", (true));
//	}
	
	public void defaultAuton(){
		if (autonSelected == defaultAuton){}
	}
	
	public void DriveBaseLine(){
		//if(){}
		
	}




}
