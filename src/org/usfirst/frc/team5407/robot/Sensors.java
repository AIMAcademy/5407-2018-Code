package org.usfirst.frc.team5407.robot;

import com.kauailabs.navx.frc.AHRS;
import edu.wpi.first.wpilibj.AnalogInput;
import edu.wpi.first.wpilibj.AnalogPotentiometer;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.SPI;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.interfaces.Potentiometer;

public class Sensors{
	// Names sensors and its type
	// ADXRS450_Gyro analogGyro;
	AHRS ahrs;
	Potentiometer TestPot;
	private AnalogInput mAnalogInputRevAirSensor; 

	// Create and put doubles here
	double followAngle;
    double rotateToAngleRate;

    // Creates a timer
	Timer counter;

	public Sensors(){
		//create the sensor named above and call its port number and any other needed settings
		// analogGyro = new ADXRS450_Gyro();
		mAnalogInputRevAirSensor = new AnalogInput(1);
		
		//no smartDashBoard output
		TestPot = new AnalogPotentiometer(0, 360, 30);
		
		// tries to call NavX and if it does not respond an printout appears in the driver station
	    try {
	        ahrs = new AHRS(SPI.Port.kMXP);
	    } catch (RuntimeException ex ) {
	        DriverStation.reportError("Error instantiating navX MXP:  " + ex.getMessage(), true);
	    }
	    
	}
	
	 // Rev Robotics Air Pressure Sensor doing calculation to get the pressure reading
	 public double getAirPressurePsi(){
		 //taken from the datasheet
		 return 250.0 * mAnalogInputRevAirSensor.getVoltage() / 5.0 - 25.0; 
	 }
	
	// NAVX Code
	// Sets the follow angle by getting angle and then getting the offset
	public void setFollowAngleNAVX(double offset){
		this.followAngle = this.ahrs.getAngle() + offset;
	}
	
	// Makes public and gets the follow angle
	public double getFollowAngleNAVX() {
		return this.followAngle;
	}
	
	// Makes public and gets the present angles
	public double getPresentAngleNAVX(){
		return this.ahrs.getAngle();
	}
	// END NAVX Code
	
}
