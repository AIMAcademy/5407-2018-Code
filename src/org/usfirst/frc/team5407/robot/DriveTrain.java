package org.usfirst.frc.team5407.robot;


import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;

public class DriveTrain {

	public WPI_TalonSRX frontLeftDriveMotor, frontRightDriveMotor, backLeftDriveSlave, backRightDriveSlave;
	public DifferentialDrive drive;

	public DriveTrain(){	

		/* talons for arcade drive */
		WPI_TalonSRX frontLeftDriveMotor = new WPI_TalonSRX(12); 		/* device IDs here (1 of 2) */
		WPI_TalonSRX frontRightDriveMotor = new WPI_TalonSRX(15); 		// changed master left to 12 because it has the encoder hooked up

		WPI_TalonSRX backLeftDriveSlave = new WPI_TalonSRX(11);
		WPI_TalonSRX backRightDriveSlave = new WPI_TalonSRX(16);

		// the commands are sent to the first TalonSRX's then the same sent to the back ones
		backLeftDriveSlave.follow(frontLeftDriveMotor);
		backRightDriveSlave.follow(frontRightDriveMotor);

		drive = new DifferentialDrive(frontLeftDriveMotor, frontRightDriveMotor);

	}

	public double RightSideMagEncoder(){		   	
		double RightsideQuadraturePosition = frontRightDriveMotor.getSensorCollection().getQuadraturePosition()*1.0;
		double InchesRS = -RightsideQuadraturePosition / 3313 * 4 * Math.PI;
		return InchesRS;
	}

	public double LeftSideMagEncoder(){
		double LeftsideQuadraturePosition = frontLeftDriveMotor.getSensorCollection().getQuadraturePosition()*1.0;
		double InchesLS = LeftsideQuadraturePosition / 3313 * 4 * Math.PI;
		return InchesLS;
	}

}
