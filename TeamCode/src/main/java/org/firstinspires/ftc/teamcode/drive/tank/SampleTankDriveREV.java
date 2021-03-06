package org.firstinspires.ftc.teamcode.drive.tank;

import android.support.annotation.NonNull;

import com.acmerobotics.roadrunner.control.PIDCoefficients;
import com.acmerobotics.roadrunner.localization.Localizer;
import com.acmerobotics.roadrunner.localization.ThreeTrackingWheelLocalizer;
import com.qualcomm.hardware.bosch.BNO055IMU;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.PIDFCoefficients;

import org.firstinspires.ftc.robotcore.external.navigation.AxesOrder;
import org.firstinspires.ftc.teamcode.drive.localizer.StandardTrackingWheelLocalizer;
import org.firstinspires.ftc.teamcode.drive.localizer.TwoWheelTrackingLocalizer;
import org.firstinspires.ftc.teamcode.util.AxesSigns;
import org.firstinspires.ftc.teamcode.util.BNO055IMUUtil;
import org.firstinspires.ftc.teamcode.util.LynxModuleUtil;

import java.util.Arrays;
import java.util.List;

import static org.firstinspires.ftc.teamcode.drive.DriveConstants.encoderTicksToInches;

public class SampleTankDriveREV extends SampleTankDriveBase {
    private List<DcMotorEx> motors, leftMotors, rightMotors;
    private BNO055IMU imu;
    public StandardTrackingWheelLocalizer encoderWheels;

    public SampleTankDriveREV(HardwareMap hardwareMap) {
        super();

        //LynxModuleUtil.ensureMinimumFirmwareVersion(hardwareMap);

        imu = hardwareMap.get(BNO055IMU.class, "imu");
        BNO055IMU.Parameters parameters = new BNO055IMU.Parameters();
        parameters.angleUnit = BNO055IMU.AngleUnit.RADIANS;
        imu.initialize(parameters);

        BNO055IMUUtil.remapAxes(imu, AxesOrder.XYZ, AxesSigns.NPN);

        // add/remove motors depending on your robot (e.g., 6WD)
        DcMotorEx PTOLeft = hardwareMap.get(DcMotorEx.class, "PTOLeft");
        DcMotorEx PTORight = hardwareMap.get(DcMotorEx.class, "PTORight");
        DcMotorEx driveLeft = hardwareMap.get(DcMotorEx.class, "driveLeft");
        DcMotorEx driveRight = hardwareMap.get(DcMotorEx.class, "driveRight");

        motors = Arrays.asList(PTOLeft, PTORight, driveLeft, driveRight);
        leftMotors = Arrays.asList(PTOLeft, driveLeft);
        rightMotors = Arrays.asList(PTORight, driveRight);

        for (DcMotorEx motor : motors) {
            // We're not using built-in motor PID
            motor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
            motor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        }

        // Left side motors are reversed so that powering
        // both sides of the drive train to +1.0 (+12V) will drive
        // the robot forward
        driveRight.setDirection(DcMotor.Direction.REVERSE);
        PTORight.setDirection(DcMotor.Direction.REVERSE);

        encoderWheels = new StandardTrackingWheelLocalizer(hardwareMap);
        setLocalizer(encoderWheels);
    }

    @Override
    public PIDCoefficients getPIDCoefficients(DcMotor.RunMode runMode) {
        PIDFCoefficients coefficients = leftMotors.get(0).getPIDFCoefficients(runMode);
        return new PIDCoefficients(coefficients.p, coefficients.i, coefficients.d);
    }

    @Override
    public void setPIDCoefficients(DcMotor.RunMode runMode, PIDCoefficients coefficients) {
        for (DcMotorEx motor : motors) {
            motor.setPIDFCoefficients(runMode, new PIDFCoefficients(
                    coefficients.kP, coefficients.kI, coefficients.kD, 1
            ));
        }
    }

    @NonNull
    @Override
    public List<Double> getWheelPositions() {
        double leftSum = 0, rightSum = 0;
        for (DcMotorEx leftMotor : leftMotors) {
            leftSum += encoderTicksToInches(leftMotor.getCurrentPosition());
        }
        for (DcMotorEx rightMotor : rightMotors) {
            rightSum += encoderTicksToInches(rightMotor.getCurrentPosition());
        }
        return Arrays.asList(leftSum / leftMotors.size(), rightSum / rightMotors.size());
    }

    @Override
    public void setMotorPowers(double v, double v1) {
        for (DcMotorEx leftMotor : leftMotors) {
            leftMotor.setPower(v);
        }
        for (DcMotorEx rightMotor : rightMotors) {
            rightMotor.setPower(v1);
        }
    }

    @Override
    public double getRawExternalHeading() {
        return imu.getAngularOrientation().thirdAngle;
    }
}
