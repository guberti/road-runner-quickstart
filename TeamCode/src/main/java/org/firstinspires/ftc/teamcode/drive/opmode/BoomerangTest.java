package org.firstinspires.ftc.teamcode.drive.opmode;

import com.acmerobotics.roadrunner.geometry.Pose2d;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.teamcode.drive.tank.SampleTankDriveREV;

import java.util.List;

/*
 * This is an example of a more complex path to really test the tuning.
 */
@Autonomous(group = "drive")
public class BoomerangTest extends LinearOpMode {
    @Override
    public void runOpMode() throws InterruptedException {
        SampleTankDriveREV drive = new SampleTankDriveREV(hardwareMap);

        waitForStart();

        if (isStopRequested()) return;

        while (!isStopRequested() && !gamepad1.left_stick_button && !gamepad1.right_stick_button) {
            drive.setDrivePower(new Pose2d(
                    -gamepad1.left_stick_y,
                    0,
                    -gamepad1.right_stick_x
            ));

            drive.update();

            Pose2d poseEstimate = drive.getPoseEstimate();
            telemetry.addData("x", poseEstimate.getX());
            telemetry.addData("y", poseEstimate.getY());
            telemetry.addData("heading", poseEstimate.getHeading());
            telemetry.update();
        }

        sleep(2000);

        drive.followTrajectorySync(
                drive.trajectoryBuilder()
                        .reverse()
                        .splineTo(new Pose2d(0, 0, 0))
                        .build()
        );
    }
}
