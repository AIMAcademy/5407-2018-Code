package org.usfirst.frc.team5407.robot;

import edu.wpi.first.wpilibj.Spark;

public class Winch {
	Spark mot_Winch;
	
	public Winch(int i_mot_Winch){
		mot_Winch = new Spark(i_mot_Winch);
		mot_Winch.set(0.0);
	}
	public void winchStop() {
		mot_Winch.set(0.0);
		System.out.println("Winch Stopped!");
	}
}
