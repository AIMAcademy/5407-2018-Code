/*----------------------------------------------------------------------------*/
/* Copyright (c) 2017-2018 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package org.usfirst.frc.team5407.robot;

import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;

import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.Spark;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;

/**
 * This is a demo program showing the use of the RobotDrive class, specifically
 * it contains the code necessary to operate a robot with tank drive.
 */
public class Robot extends IterativeRobot {
	private DifferentialDrive _drive;
	private Joystick m_leftStick;
	
	/* talons for arcade drive */
	WPI_TalonSRX _frontLeftMotor = new WPI_TalonSRX(11); 		/* device IDs here (1 of 2) */
	WPI_TalonSRX _frontRightMotor = new WPI_TalonSRX(15);
	
	WPI_TalonSRX _backLeftSlave = new WPI_TalonSRX(12);
	WPI_TalonSRX _backRightSlave = new WPI_TalonSRX(16);


	@Override
	public void robotInit() {
		_backLeftSlave.follow(_frontLeftMotor);
		_backRightSlave.follow(_frontRightMotor);
		
		_drive = new DifferentialDrive(_frontLeftMotor, _frontRightMotor);
		m_leftStick = new Joystick(0);
	}

	@Override
	public void teleopPeriodic() {
		// Arcade Drive
    	double forward = -m_leftStick.getY(); // logitech gampad left X, positive is forward
    	double turn = m_leftStick.getX(); // logitech gampad right X, positive means turn right
    	_drive.arcadeDrive(forward, turn);
	}
}
