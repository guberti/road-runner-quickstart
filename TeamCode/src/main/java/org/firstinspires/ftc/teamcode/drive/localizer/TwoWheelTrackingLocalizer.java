package org.firstinspires.ftc.teamcode.drive.localizer;

import android.support.annotation.NonNull;

import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.roadrunner.geometry.Vector2d;
import com.acmerobotics.roadrunner.localization.ThreeTrackingWheelLocalizer;
import com.acmerobotics.roadrunner.localization.TwoTrackingWheelLocalizer;
import com.qualcomm.hardware.bosch.BNO055IMU;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.teamcode.drive.tank.SampleTankDriveREV;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;

/*
 * Sample tracking wheel localizer implementation assuming the standard configuration:
 *
 *    /--------------\
 *    |              |
 *    |              |
 *    | ||        || |
 *    | ||        || |
 *    |              |
 *    |              |
 *    \--------------/
 *
 * Note: this could be optimized significantly with REV bulk reads
 */
@Config
public class TwoWheelTrackingLocalizer extends TwoTrackingWheelLocalizer {
    public static double TICKS_PER_REV = 4 * 600;
    public static double WHEEL_RADIUS = 1.14426;
    public static double GEAR_RATIO = 1; // output (wheel) speed / input (encoder) speed

    public static double LEFT_Y_POS = 2.85;
    public static double RIGHT_Y_POS = -3.25;

    public DcMotor leftParallelEncoder, rightParallelEncoder;
    public DcMotor[] encoders;
    SampleTankDriveREV drive;


    public TwoWheelTrackingLocalizer(HardwareMap hardwareMap, SampleTankDriveREV drive) {
        super(Arrays.asList(
                new Vector2d(0, LEFT_Y_POS), // left
                new Vector2d(0, RIGHT_Y_POS)
        ), Arrays.asList(0.0, Math.toRadians(180.0)));
        // Second encoders must be flipped around 180 degrees, to account for wheel flipping

        leftParallelEncoder = hardwareMap.dcMotor.get("PTOLeft");
        rightParallelEncoder = hardwareMap.dcMotor.get("PTORight");
        encoders = new DcMotor[]{leftParallelEncoder, rightParallelEncoder};

        this.drive = drive;
    }

    public static double encoderTicksToInches(int ticks) {
        return WHEEL_RADIUS * 2 * Math.PI * GEAR_RATIO * ticks / TICKS_PER_REV;
    }

    @NonNull
    @Override
    public List<Double> getWheelPositions() {
        return Arrays.asList(
                encoderTicksToInches(leftParallelEncoder.getCurrentPosition()),
                encoderTicksToInches(rightParallelEncoder.getCurrentPosition())
        );
    }

    @Override
    public double getHeading() {
        return drive.getRawExternalHeading();
    }
}
