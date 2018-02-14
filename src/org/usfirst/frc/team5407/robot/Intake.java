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
	
	public void intakeStop() {
		this.mot_rightSideIntake.set(0.0);
		this.mot_leftSideIntake.set(0.0);
	}

}
