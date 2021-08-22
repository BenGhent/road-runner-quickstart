package org.firstinspires.ftc.teamcode;

import com.acmerobotics.roadrunner.geometry.Pose2d;
import com.acmerobotics.roadrunner.trajectory.Trajectory;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.teamcode.RR.RRInit;

@Autonomous(name = "TESTING", group = "Test")
//@Disabled
public class TESTING extends LinearOpMode {
	
	RConf r = new RConf(this);
	
	RRInit drive = new RRInit(r.driveMotors, r.imu, hardwareMap);
	
	@Override
	public void runOpMode() throws InterruptedException {
		
		Trajectory trajectoryForward = drive.trajectoryBuilder(new Pose2d())
											   .forward(10)
											   .build();
		
		Trajectory trajectoryBackward = drive.trajectoryBuilder(trajectoryForward.end())
												.back(10)
												.build();
		
		waitForStart();
	}
}