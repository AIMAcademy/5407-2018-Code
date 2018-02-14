package org.usfirst.frc.team5407.robot;

import edu.wpi.first.wpilibj.Spark;

public class Lift {
	
	Spark mot_liftDart;
	
	public Lift(int i_PWM_LiftSpark) {
		mot_liftDart = new Spark(i_PWM_LiftSpark);
		mot_liftDart.set(0.0);

	}
	
	public void liftFast(double liftSpeed) {
		this.mot_liftDart.set(liftSpeed * .50);
	}
	
	public void liftSlow(double liftSpeed) {
		this.mot_liftDart.set(liftSpeed * .25);
	}
	

}
