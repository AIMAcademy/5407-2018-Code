package org.usfirst.frc.team5407.robot;


import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;

public class DriveTrain {

	public WPI_TalonSRX frontLeftDriveMotor, frontRightDriveMotor, backLeftDriveSlave, backRightDriveSlave;
	public DifferentialDrive drive;
	double RightsideQuadraturePosition = 0.0;
	double LeftsideQuadraturePosition = 0.0;
	double leftmath;
	
	public DriveTrain(){	

		/* talons for arcade drive */
		frontLeftDriveMotor = new WPI_TalonSRX(12); 		/* device IDs here (1 of 2) */
		frontRightDriveMotor = new WPI_TalonSRX(15); 		// changed master left to 12 because it has the encoder hooked up

		backLeftDriveSlave = new WPI_TalonSRX(11);
		backRightDriveSlave = new WPI_TalonSRX(16);

		// the commands are sent to the first TalonSRX's then the same sent to the back ones
		backLeftDriveSlave.follow(frontLeftDriveMotor);
		backRightDriveSlave.follow(frontRightDriveMotor);

		drive = new DifferentialDrive(frontLeftDriveMotor, frontRightDriveMotor);
	}

	public double getLeftQuadPosition(){		   	
		//return  (-frontRightDriveMotor.getSensorCollection().getQuadraturePosition()*1.0 / 3313 * 4 * Math.PI);
		RightsideQuadraturePosition = this.frontRightDriveMotor.getSensorCollection().getQuadraturePosition();
		// System.out.println("Right side quad position: " + RightsideQuadraturePosition); // Prints position to console
		return (RightsideQuadraturePosition / 3313 * 4 * Math.PI);
	}

	public double getRightQuadPosition(){
		//return  (-frontLeftDriveMotor.getSensorCollection().getQuadraturePosition()*1.0 / 3313 * 4 * Math.PI);
		LeftsideQuadraturePosition = this.frontLeftDriveMotor.getSensorCollection().getQuadraturePosition();
		// System.out.println("Left side quad position: " + LeftsideQuadraturePosition);  // Prints position to console
		return (LeftsideQuadraturePosition / 3313 * 4 * Math.PI);
	}

}
