package org.usfirst.frc.team5407.robot;

import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;

import edu.wpi.first.wpilibj.drive.DifferentialDrive;

public class DriveTrain {

	public DifferentialDrive drive;
	
	/* talons for arcade drive */
	WPI_TalonSRX _frontLeftMotor = new WPI_TalonSRX(12); 		/* device IDs here (1 of 2) */
	WPI_TalonSRX _frontRightMotor = new WPI_TalonSRX(15); 		// changed master left to 12 because it has the encoder hooked up
	
	WPI_TalonSRX _backLeftSlave = new WPI_TalonSRX(11);
	WPI_TalonSRX _backRightSlave = new WPI_TalonSRX(16);
	

public DriveTrain(){	
	
	// the commands are sent to the first TalonSRX's then the same sent to the back ones
	_backLeftSlave.follow(_frontLeftMotor);
	_backRightSlave.follow(_frontRightMotor);
				
	drive = new DifferentialDrive(_frontLeftMotor, _frontRightMotor);
	
	}
}
