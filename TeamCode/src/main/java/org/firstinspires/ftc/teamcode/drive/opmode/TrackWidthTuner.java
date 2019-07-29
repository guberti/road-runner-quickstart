package org.firstinspires.ftc.teamcode.drive.opmode;

import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.roadrunner.geometry.Pose2d;
import com.acmerobotics.roadrunner.util.Angle;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.util.MovingStatistics;

import org.firstinspires.ftc.robotcore.internal.system.Misc;
import org.firstinspires.ftc.teamcode.drive.DriveConstants;
import org.firstinspires.ftc.teamcode.drive.mecanum.SampleMecanumDriveBase;
import org.firstinspires.ftc.teamcode.drive.mecanum.SampleMecanumDriveREV;
import org.firstinspires.ftc.teamcode.drive.tank.SampleTankDriveREV;

import java.util.List;

/*
 * This routine determines the effective track width. The procedure works by executing a point turn
 * with a given angle and measuring the difference between that angle and the actual angle (as
 * indicated by an external IMU/gyro, track wheels, or some other localizer). The quotient
 * given angle / actual angle gives a multiplicative adjustment to the estimated track width
 * (effective track width = estimated track width * given angle / actual angle). The routine repeats
 * this procedure a few times and averages the values for additional accuracy. Note: a relatively
 * accurate track width estimate is important or else the angular constraints will be thrown off.
 */
@Config
@Autonomous(group = "drive")
public class TrackWidthTuner extends LinearOpMode {
    public static double ANGLE = Math.toRadians(180);
    public static int NUM_TRIALS = 5;
    static final String[] ENC_WHEEL_DESCS = {
            "Left parallel x-dist = ",
            "Right parallel x-dist = ",
            "Lateral y-dist = ",
    };

    @Override
    public void runOpMode() throws InterruptedException {
        SampleTankDriveREV drive = new SampleTankDriveREV(hardwareMap);
        // TODO: if you haven't already, set the localizer to something that doesn't depend on
        // drive encoders for computing the heading

        telemetry.log().add("Press play to begin the track width tuner routine");
        telemetry.log().add("Make sure your robot has enough clearance to turn smoothly");
        telemetry.update();

        waitForStart();

        if (isStopRequested()) return;

        telemetry.log().clear();
        telemetry.log().add("Running...");
        telemetry.update();

        MovingStatistics trackWidthStats = new MovingStatistics(NUM_TRIALS);
        MovingStatistics[] encoderWheelStats = new MovingStatistics[3];
        for (int i = 0; i < 3; i++) {
            encoderWheelStats[i] = new MovingStatistics(NUM_TRIALS);
        }


        for (int i = 0; i < NUM_TRIALS; i++) {
            drive.setPoseEstimate(new Pose2d());
            List<Double> prevWheelDists = drive.encoderWheels.getWheelPositions();

            // it is important to handle heading wraparounds
            double headingAccumulator = 0;
            double lastHeading = 0;

            drive.turn(ANGLE);

            while (!isStopRequested() && drive.isBusy()) {
                double heading = drive.getPoseEstimate().getHeading();
                headingAccumulator += Angle.norm(heading - lastHeading);
                lastHeading = heading;

                drive.update();
            }

            double trackWidth = DriveConstants.TRACK_WIDTH * ANGLE / headingAccumulator;
            trackWidthStats.add(trackWidth);

            List<Double> currentWheelDists = drive.encoderWheels.getWheelPositions();
            telemetry.log().add(String.valueOf(currentWheelDists.size()));
            for (int k = 0; i < 3; i++) {
                double distance = currentWheelDists.get(i) - prevWheelDists.get(i);
                encoderWheelStats[i].add(distance / headingAccumulator);
            }
            sleep(1000);
        }

        telemetry.log().clear();
        telemetry.log().add("Tuning complete");
        telemetry.log().add(Misc.formatInvariant("Effective track width = %.2f (SE = %.3f)",
                trackWidthStats.getMean(),
                trackWidthStats.getStandardDeviation() / Math.sqrt(NUM_TRIALS)));
        for (int i = 0; i < encoderWheelStats.length; i++) {
            telemetry.log().add(Misc.formatInvariant(ENC_WHEEL_DESCS[i] + "%.2f (SE = %.3f)",
                    encoderWheelStats[i].getMean(),
                    encoderWheelStats[i].getStandardDeviation() / Math.sqrt(NUM_TRIALS)));
        }
        telemetry.update();

        while (!isStopRequested()) {
            idle();
        }
    }
}
