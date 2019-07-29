package org.firstinspires.ftc.teamcode.drive.opmode;

import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.roadrunner.geometry.Pose2d;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.teamcode.drive.tank.SampleTankDriveREV;

/*
 * Op mode for tuning follower PID coefficients. The robot drives in a DISTANCE-by-DISTANCE square
 * indefinitely.
 */
@Config
@Autonomous(group = "drive")
public class FollowerHeadingPIDTuner extends LinearOpMode {
    @Override
    public void runOpMode() throws InterruptedException {
        SampleTankDriveREV drive = new SampleTankDriveREV(hardwareMap);

        drive.setPoseEstimate(new Pose2d(0, 0, 0));
        waitForStart();

        if (isStopRequested()) return;

        drive.turnSync(Math.toRadians(90));
    }
}
