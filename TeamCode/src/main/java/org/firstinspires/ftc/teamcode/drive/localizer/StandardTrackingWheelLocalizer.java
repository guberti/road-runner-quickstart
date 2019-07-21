package org.firstinspires.ftc.teamcode.drive.localizer;

import android.support.annotation.NonNull;

import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.roadrunner.geometry.Vector2d;
import com.acmerobotics.roadrunner.localization.ThreeTrackingWheelLocalizer;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;

import java.util.Arrays;
import java.util.List;

/*
 * Sample tracking wheel localizer implementation assuming the standard configuration:
 *
 *    /--------------\
 *    |     ____     |
 *    |     ----     |
 *    | ||        || |
 *    | ||        || |
 *    |              |
 *    |              |
 *    \--------------/
 *
 * Note: this could be optimized significantly with REV bulk reads
 */
@Config
public class StandardTrackingWheelLocalizer extends ThreeTrackingWheelLocalizer {
    public static double TICKS_PER_REV = 4 * 600;
    public static double WHEEL_RADIUS = 1.14426;
    public static double GEAR_RATIO = 1; // output (wheel) speed / input (encoder) speed

    public static double LEFT_Y_POS = 2.81;
    public static double RIGHT_Y_POS = -3.30;
    public static double LAT_X_POS = -7.30;

    public DcMotor leftParallelEncoder, rightParallelEncoder, lateralEncoder;
    public DcMotor[] encoders;

    public StandardTrackingWheelLocalizer(HardwareMap hardwareMap) {
        super(Arrays.asList(
                new Vector2d(0, LEFT_Y_POS), // left
                new Vector2d(0, RIGHT_Y_POS), // right
                new Vector2d(LAT_X_POS, 0) // front
        ), Arrays.asList(0.0, Math.toRadians(180.0), Math.toRadians(90.0)));
        // Second encoders must be flipped around 180 degrees, but why?

        leftParallelEncoder = hardwareMap.dcMotor.get("PTOLeft");
        rightParallelEncoder = hardwareMap.dcMotor.get("PTORight");
        lateralEncoder = hardwareMap.dcMotor.get("driveLeft");
        encoders = new DcMotor[]{leftParallelEncoder, rightParallelEncoder, lateralEncoder};
    }

    public static double encoderTicksToInches(int ticks) {
        return WHEEL_RADIUS * 2 * Math.PI * GEAR_RATIO * ticks / TICKS_PER_REV;
    }

    @NonNull
    @Override
    public List<Double> getWheelPositions() {
        return Arrays.asList(
                encoderTicksToInches(leftParallelEncoder.getCurrentPosition()),
                encoderTicksToInches(rightParallelEncoder.getCurrentPosition()),
                encoderTicksToInches(lateralEncoder.getCurrentPosition())
        );
    }
}
