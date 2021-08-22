package com.meepmeep.trajectoryviewer;

import com.acmerobotics.roadrunner.geometry.Pose2d;
import com.acmerobotics.roadrunner.geometry.Vector2d;
import com.acmerobotics.roadrunner.trajectory.Trajectory;
import com.acmerobotics.roadrunner.trajectory.TrajectoryBuilder;
import com.noahbres.meepmeep.MeepMeep;
import com.noahbres.meepmeep.core.colorscheme.scheme.ColorSchemeRedDark;
import com.noahbres.meepmeep.roadrunner.Constraints;
import com.noahbres.meepmeep.roadrunner.DriveShim;
import com.noahbres.meepmeep.roadrunner.DriveTrainType;
import com.noahbres.meepmeep.roadrunner.trajectorysequence.TrajectorySequenceBuilder;

public class MyClass {
	
	public static void main(String[] args) {
		// TODO: If you experience poor performance, enable this flag
		// System.setProperty("sun.java2d.opengl", "true");
		
		// Declare a MeepMeep instance
		// With a field size of 800 pixels
		MeepMeep mm = new MeepMeep(800, 30)
							  // Set field image
							  .setBackground(MeepMeep.Background.FIELD_ULTIMATE_GOAL_DARK)
							  // Set theme
							  .setTheme(new ColorSchemeRedDark())
							  // Background opacity from 0-1
							  .setBackgroundAlpha(1f)
							  // Set constraints: maxVel, maxAccel, maxAngVel, maxAngAccel, track width
							  .setConstraints(60, 60, Math.toRadians(180), Math.toRadians(180), 15)
							  .followTrajectorySequence(drive ->
																drive.trajectorySequenceBuilder(new Pose2d(-62, 40, 0))
																		.splineToConstantHeading(new Vector2d(-24,60), 0)
																		.lineToConstantHeading(new Vector2d(-10,60))
																		.lineToConstantHeading(new Vector2d(-10, 35))
																		.lineToConstantHeading(new Vector2d(12, 35))
																		.build()
							  )
							  .start();
	}
}