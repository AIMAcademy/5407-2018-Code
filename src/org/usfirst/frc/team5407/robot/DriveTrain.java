package org.usfirst.frc.team5407.robot;

import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;

public class DriveTrain {

	// Declaring all the drive train motors and making it public
	public WPI_TalonSRX frontLeftDriveMotor, frontRightDriveMotor, backLeftDriveSlave, backRightDriveSlave;
	// Creating a differentialDrive and naming it 
	public DifferentialDrive drive;
	// Creating doubles for getting encoder quad positioning and setting them to equal 0 at beginning
	double RightsideQuadraturePosition = 0.0;
	double LeftsideQuadraturePosition = 0.0;
	
	public DriveTrain(){	

		/* talons for arcade drive */
		// Both front motors have the encoders attached 
		frontLeftDriveMotor = new WPI_TalonSRX(16); 		/* device IDs here (1 of 2) */
		frontRightDriveMotor = new WPI_TalonSRX(15); 	

		backLeftDriveSlave = new WPI_TalonSRX(11);
		backRightDriveSlave = new WPI_TalonSRX(12);

		// The commands are sent to the first TalonSRX's then the same sent to the back ones
		backLeftDriveSlave.follow(frontLeftDriveMotor);
		backRightDriveSlave.follow(frontRightDriveMotor);

		// Calling differentalDrive and says what motors are in it, we only need the front ones because the back ones follow them
		drive = new DifferentialDrive(frontLeftDriveMotor, frontRightDriveMotor);
	}

	// Encoder for left side, gets value from encoder, and returns a value in inches divided by 3313 then multiples by the wheel diameter * Pi 
	public double getLeftQuadPosition(){		   	
		//return  (-frontRightDriveMotor.getSensorCollection().getQuadraturePosition()*1.0 / 3313 * 4 * Math.PI);
		LeftsideQuadraturePosition = this.frontLeftDriveMotor.getSensorCollection().getQuadraturePosition();
		// System.out.println("Right side quad position: " + RightsideQuadraturePosition); // Prints position to console
		return (LeftsideQuadraturePosition / 3613 * 4 * Math.PI);
	}

	// Encoder for right side, gets value from encoder, and returns a value in inches divided by 3313 then multiples by the wheel diameter * Pi 
	public double getRightQuadPosition(){
		//return  (-frontLeftDriveMotor.getSensorCollection().getQuadraturePosition()*1.0 / 3313 * 4 * Math.PI);
		RightsideQuadraturePosition = this.frontRightDriveMotor.getSensorCollection().getQuadraturePosition();
		// System.out.println("Left side quad position: " + LeftsideQuadraturePosition);  // Prints position to console
		return -(RightsideQuadraturePosition / 3313 * 4 * Math.PI);
		//negative 1 added for invert sensor, needs testing!!!
	}

}
