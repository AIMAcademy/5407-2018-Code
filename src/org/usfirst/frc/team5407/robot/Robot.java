/*----------------------------------------------------------------------------*/
/* Copyright (c) 2017-2018 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package org.usfirst.frc.team5407.robot;


import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.can.*;
import com.ctre.phoenix.motorcontrol.StatusFrameEnhanced;

import edu.wpi.first.wpilibj.Compressor;
import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.livewindow.*;

/**
 * This is a demo program showing the use of the RobotDrive class, specifically
 * it contains the code necessary to operate a robot with tank drive.
 */
public class Robot extends IterativeRobot {
	
	Sensors sensors = new Sensors();
	
	
	
	private DifferentialDrive _drive;
	private Joystick m_leftStick;
	
	
	/* talons for arcade drive */
	WPI_TalonSRX _frontLeftMotor = new WPI_TalonSRX(11); 		/* device IDs here (1 of 2) */
	WPI_TalonSRX _frontRightMotor = new WPI_TalonSRX(15);
	
	WPI_TalonSRX _backLeftSlave = new WPI_TalonSRX(12);
	WPI_TalonSRX _backRightSlave = new WPI_TalonSRX(16);

	//Temporary to run grip
	Compressor tc = new Compressor(0);
	
	//gyro kp
	double Kp = 0.02;

	@Override
	public void robotInit() {
		_backLeftSlave.follow(_frontLeftMotor);
		_backRightSlave.follow(_frontRightMotor);
		
		_drive = new DifferentialDrive(_frontLeftMotor, _frontRightMotor);
		m_leftStick = new Joystick(0);
	}

	public void autonInit(){
		_backLeftSlave.getSensorCollection().setQuadraturePosition(0, 0);
		_frontRightMotor.getSensorCollection().setQuadraturePosition(0, 0);
		
	}		
	
	public void autonPeriodic() {
    		
		
	}

	public void teleopInit() {
		_backLeftSlave.getSensorCollection().setQuadraturePosition(0, 0);
		_frontRightMotor.getSensorCollection().setQuadraturePosition(0, 0);

	}

	@Override
	public void teleopPeriodic() {
		// Arcade Drive
    	double forward = -m_leftStick.getY(); // logitech gampad left X, positive is forward
    	double turn = m_leftStick.getX(); // logitech gampad right X, positive means turn right
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
    		_drive.arcadeDrive(forward, (sensors.getFollowAngleNAVX()-sensors.getPresentAngleNAVX())*Kp);
    	}
    	else{
    		_drive.arcadeDrive(forward, turn);
    		b_EnableGyroNAVX = false;
    	}
    	// END TESTING NAVX
    	
    	double LeftsideQuadraturePosition = _backLeftSlave.getSensorCollection().getQuadraturePosition();
    	double InchesLS = LeftsideQuadraturePosition / 3313 * 4 * Math.PI;
    	SmartDashboard.putNumber("left side inches", InchesLS);

   	
     	double RightsideQuadraturePosition = _frontRightMotor.getSensorCollection().getQuadraturePosition();
    	double InchesRS = -RightsideQuadraturePosition / 3313 * 4 * Math.PI;
    	SmartDashboard.putNumber("right side inches", InchesRS);
    	
    	// Tested PWM variable. Data does not seem reliable or helpful. //
    	//double pwm = _frontRightMotor.getSensorCollection().getPulseWidthPosition();
    	//SmartDashboard.putNumber("Pwm", pwm);
    	
    	if (m_leftStick.getRawButton(1)){
    		_backLeftSlave.getSensorCollection().setQuadraturePosition(0, 0);
    		_frontRightMotor.getSensorCollection().setQuadraturePosition(0, 0);
    	}
    	
    	//Temporary to run grip
    	//tc.setClosedLoopControl(true);
    	
    	
    	SmartDashboard.putNumber("Gyro", sensors.analogGyro.getAngle());
    	SmartDashboard.putNumber("Gyro-NAVX", sensors.ahrs.getAngle());
    	
    	
    	SmartDashboard.updateValues();
    
	}
}
