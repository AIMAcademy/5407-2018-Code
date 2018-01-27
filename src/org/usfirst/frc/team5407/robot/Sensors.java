package org.usfirst.frc.team5407.robot;

import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;
import com.kauailabs.navx.frc.AHRS;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.can.*;
import com.ctre.phoenix.motorcontrol.StatusFrameEnhanced;
import edu.wpi.first.wpilibj.ADXRS450_Gyro;
import edu.wpi.first.wpilibj.AnalogInput;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.SPI;
import edu.wpi.first.wpilibj.Timer;

public class Sensors {
	
	ADXRS450_Gyro analogGyro;
	AHRS ahrs;

	double followAngle;
    double rotateToAngleRate;

	Timer counter;

	public Sensors(){
		analogGyro = new ADXRS450_Gyro();
		analogGyro.reset();
		
	    try {
	        ahrs = new AHRS(SPI.Port.kMXP);
	    } catch (RuntimeException ex ) {
	        DriverStation.reportError("Error instantiating navX MXP:  " + ex.getMessage(), true);
	    }
	}

	public void setFollowAngle(double offset){
		this.followAngle = this.analogGyro.getAngle() + offset;
	}
	
	public double getFollowAngle() {
		return this.followAngle;
	}
	
	public double getPresentAngle(){
		return this.analogGyro.getAngle();
	}
	
	// NAVX Code
	public void setFollowAngleNAVX(double offset){
		this.followAngle = this.ahrs.getAngle() + offset;
	}
	
	public double getFollowAngleNAVX() {
		return this.followAngle;
	}
	
	public double getPresentAngleNAVX(){
		return this.ahrs.getAngle();
	}
	// END NAVX Code
	
}
