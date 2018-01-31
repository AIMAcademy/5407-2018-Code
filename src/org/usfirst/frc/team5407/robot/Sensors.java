package org.usfirst.frc.team5407.robot;

import com.kauailabs.navx.frc.AHRS;
import edu.wpi.first.wpilibj.ADXRS450_Gyro;
import edu.wpi.first.wpilibj.AnalogInput;
import edu.wpi.first.wpilibj.AnalogPotentiometer;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.SPI;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.interfaces.Potentiometer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Sensors {
	
	ADXRS450_Gyro analogGyro;
	AHRS ahrs;
	Potentiometer TestPot;
	private AnalogInput mAnalogInputRevAirSensor; 

	double followAngle;
    double rotateToAngleRate;

	Timer counter;

	public Sensors(){
		analogGyro = new ADXRS450_Gyro();
	//	TestPot = new AnalogPotentiometer(0, 360, 30);
		mAnalogInputRevAirSensor = new AnalogInput(1);
		 
		analogGyro.reset();
		
	    try {
	        ahrs = new AHRS(SPI.Port.kMXP);
	    } catch (RuntimeException ex ) {
	        DriverStation.reportError("Error instantiating navX MXP:  " + ex.getMessage(), true);
	    }
	    
	}

	//need to make drivetrain class
//	 public double LeftSideMagEncoder(){
//	    	double LeftsideQuadraturePosition = Robot._backLeftSlave.getSensorCollection().getQuadraturePosition();
//	    	double InchesLS = LeftsideQuadraturePosition / 3313 * 4 * Math.PI;
//	    	SmartDashboard.putNumber("left side inches", InchesLS);
//	 }
	 //need to make drivetrain class
//	 public double RightSideMagEncoder(){		   	
//	     	double RightsideQuadraturePosition = _frontRightMotor.getSensorCollection().getQuadraturePosition();
//	    	double InchesRS = -RightsideQuadraturePosition / 3313 * 4 * Math.PI;
//	    	SmartDashboard.putNumber("right side inches", InchesRS);
//	 }
	 //Rev Robotics Air Pressure Sensor doing calculation to get the pressure reading
	 public double getAirPressurePsi(){
		 //taken from the datasheet
		 return 250.0 * mAnalogInputRevAirSensor.getVoltage() / 5.0 - 25.0; 
	 }

//	public void setFollowAngle(double offset){
//		this.followAngle = this.analogGyro.getAngle() + offset;
//	}
//	
//	public double getFollowAngle() {
//		return this.followAngle;
//	}
	
//	public double getPresentAngle(){
//		return this.analogGyro.getAngle();
//	}
	
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
