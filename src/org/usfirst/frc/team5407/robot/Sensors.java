package org.usfirst.frc.team5407.robot;

import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.can.*;
import com.ctre.phoenix.motorcontrol.StatusFrameEnhanced;
import edu.wpi.first.wpilibj.ADXRS450_Gyro;
import edu.wpi.first.wpilibj.AnalogInput;
import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.Timer;

public class Sensors {
	
	ADXRS450_Gyro analogGyro;

	double followAngle;
	
	Timer counter;


	public Sensors(){
		
		analogGyro = new ADXRS450_Gyro();
		analogGyro.reset();

		
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
	
	
}
