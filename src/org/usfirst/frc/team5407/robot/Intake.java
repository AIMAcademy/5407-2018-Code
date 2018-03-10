package org.usfirst.frc.team5407.robot;

import edu.wpi.first.wpilibj.Spark;

public class Intake {
	
	Spark mot_rightSideIntake;
	Spark mot_leftSideIntake;
	
	public Intake(int i_mot_rightSideIntake, int i_mot_leftSideIntake) {
		mot_rightSideIntake = new Spark(i_mot_rightSideIntake);
		mot_rightSideIntake.set(0.0);
		mot_leftSideIntake = new Spark(i_mot_leftSideIntake);
		mot_leftSideIntake.set(0.0);
	}
	public void intakeIn() {
		mot_leftSideIntake.set(-0.8);
		mot_rightSideIntake.set(0.8);
	}
	
	public void intakeOut() {
		mot_leftSideIntake.set(0.8);
		mot_rightSideIntake.set(-0.8);
	}
	
	public void intakeStop() {
		mot_leftSideIntake.set(0.0);
		mot_rightSideIntake.set(0.0);
	}
}
