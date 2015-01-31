package parcours.task;

import lejos.nxt.Motor;
import lejos.nxt.MotorPort;
import lejos.nxt.NXTMotor;
import lejos.nxt.SensorPort;
import lejos.nxt.Sound;
import lejos.util.Delay;
import sensor.evaluation.LightSensorEvaluation;
import sensor.evaluation.SensorEvaluation;
import debug.DebugOutput;

public class FollowLineTaskConstTime extends Task {
	
	private static final long MS_COMPLETE_CYCLE_TIME = 50;
	private static final long MS_MEASURE_CYCLE_TIME  = 4;
	private static final long MS_REMAINING_TIME_INSUFICCIENT_WARNING_TIME = 11;
	
	private static final int BASE_POWER = 25;

	// TODO: are those the optimum values?
//	private static final double K_CRITICAL = 1.5;
//	private static final double T_PERIOD = 0.05;
//	private static final double DELTA_2 = MS_COMPLETE_CYCLE_TIME; // previously: 2.5 / 1000; // 2-3 ms
//
//	private static final double Kp_CALC = 0.6 * K_CRITICAL;
//	private static final double Ki_CALC = 2 * Kp_CALC * DELTA_2 / T_PERIOD;
//	private static final double Kd_CALC = Kp_CALC * T_PERIOD / (8 * DELTA_2);
//
//	private static final float Kp = (float) (Kp_CALC /* * PRECISION_FACTOR*/);
//	private static final float Ki = (float) (Ki_CALC /* * PRECISION_FACTOR*/) + 2;
//	private static final float Kd = (float) (Kd_CALC /* * PRECISION_FACTOR*/);
	
	private static final float Kp = 0.003f;
	private static final float Ki = 0.004f;
	private static final float Kd = 1.000f; // todo revert to 2
	
	// allows reducing the integral by an exp. value if necessary
	private static final float ALPHA_INTEGRAL = 0.0f;
	
	private SensorEvaluation lightSensor;
	private NXTMotor motorA;
	private NXTMotor motorB;
	private int lastPowerMotorA = 0;
	private int lastPowerMotorB = 0;
	
	private long nextCycleCompletion;
	
	// (y2;t2) represents the previous measure and point in time
	private float y2 = 0.0f;
	private long  t2 = 0;
	
	// (y1;t1) represents the first measure and point in time
	private float y1 = 0.0f;
	private long  t1 = 0;
	
	// for debugging purposes only:
	private DebugOutput out;

	@Override
	protected void init() {
		lightSensor = new LightSensorEvaluation(SensorPort.S1, 0.25f, 39);
		motorA = new NXTMotor(MotorPort.A);
		motorB = new NXTMotor(MotorPort.B);
		motorA.setPower(BASE_POWER);
		motorB.setPower(BASE_POWER);
		motorA.forward();
		motorB.forward();
		
		nextCycleCompletion = System.currentTimeMillis();
		
		out = new DebugOutput();
		out.setDescription( 0, "cycle_t" );
		out.setDescription( 1, "deviat." );
		out.setDescription( 2, "compens" );
		out.setDescription( 3, "measures" );

		out.setDescription( 4, "Kp * p" );
		out.setDescription( 5, "Ki * i" );
		out.setDescription( 6, "Kd * d" );
	}

	@Override
	protected void control() {
		final long tCycleStart = System.currentTimeMillis();
		
		nextCycleCompletion += MS_COMPLETE_CYCLE_TIME;
		// get at least 1 time new data from the sensor.
		final float deviation = lightSensor.measureError();
		
		// Warn if insufficient cycle time is detected.
		// This can be an early warning system for all kinds of problems.
		if ( nextCycleCompletion - tCycleStart
			 < MS_REMAINING_TIME_INSUFICCIENT_WARNING_TIME ) {
			Sound.beep();
			
			// beeping is synchronous, so without this a beep would cause an 
			// other one and so on...
			nextCycleCompletion = System.currentTimeMillis() +
					              MS_COMPLETE_CYCLE_TIME;
		}
		
		// regulate...
		float compensation = pid( deviation );
		calculateMotorSpeeds( compensation );
		
		out.write(1, deviation);
		out.write(2, compensation);
		out.write(0, (int)(System.currentTimeMillis() - tCycleStart));
		
		// do as many measures as is possible before the current cycle ends
		int count = 1; // 1 measure has already been performed at the beginning of the loop...
		while ( System.currentTimeMillis() + MS_MEASURE_CYCLE_TIME
				< nextCycleCompletion ) {
			++count;
			lightSensor.measureError();
			// Wait for a given number of ms before continuing.
			Delay.msDelay( MS_MEASURE_CYCLE_TIME );
		}
		
		out.write(3, count);
		
		// at this point, the most recent measures are weightened
		// with a factor of 1-(1-ewma_alpha)^cycles in the moving average.
		// This means one might have to adjust ewma_alpha is a way that
		// (1-ewma_alpha)^cycles < 0.2 for this to run smoothly.
		// use count to determine avg number of cycles.
	}

	@Override
	protected boolean abort() {
		// TODO Detect line end
		// TODO Use ultrasonic or touch sensors
		return false;
	}

	@Override
	protected void tearDown() {
	}

	/**
	 * PID regulates the loop :)
	 * @param deviationFromTarget
	 * @return
	 */
	private float pid(float deviationFromTarget) {
		// y3 represents the current measure, which means the last point in time
		final float y3 = deviationFromTarget;
		final long  t3 = System.currentTimeMillis();
		
		// accumulate integral over the last two points
		int last_delta_t = (int)(t3 - t2);
		final float integral = integrate(y3, y2, last_delta_t) ;
		
		// calculate the derivate over the last 2 or 3 points
		final float derivate = derive2pt(y3, y2, last_delta_t);
		
		// remember last two measures
		y1 = y2;
		y2 = y3;
		t1 = t2;
		t2 = t3;

		out.write(4, deviationFromTarget * Kp);
		out.write(5, integral * Ki);
		out.write(6, derivate * Kd);
		
		// TODO: use algorithm from NXTRegulatedMotor l 715
		// TODO: try out improved derivate.
		// TODO: add integral only if above a certain threshold.
		return deviationFromTarget * Kp + derivate * Kd + integral * Ki;
	}
	
	private float integral = 0.0f;
	/**
	 * Integrates the x (sensor error) measures over time.
	 * @param y3 : the current measure
	 * @param y2 : the previous measure
	 * @param delta_t : the time between the last two measures
	 * @return The current integration value.
	 */
	private float integrate(float y3, float y2, int delta_t) {
		return ( 1.0f - ALPHA_INTEGRAL ) * integral + 
	           (y3 + y2) * delta_t / 2 ;
	}

	/**
	 * Calculates the derivate over 2 points.
	 * @param y3 : the most recent measure
	 * @param y2 : the previous measure
	 * @param delta_t : the time between those two measures
	 * @return The slope of a line between the two given points of data.
	 */
	private float derive2pt(float y3, float y2, int delta_t) {
		return ( y3 - y2 ) / delta_t;
	}
	
	/**
	 * Calculates the derivate for a parabol of three equally spaced points (x1;t1), (x2;t2); (x3;t3).
	 * (x1;t1) is the current/last point in time and (x3;t3) the first one. (e.g. t1 > t3)
	 * Returns the derivate at the point (x1;t1).
	 * t is supposed to be given in ms
	 */
	/*public int derive3pt( long t1, long t3, float x1, float x2, float x3 ) {
		final int delta_t = (int)( (t3 - t1) / 2 );
		final float x1_x2 = x1 - x2;
		final float x2_x3 = x2 - x3;
		final float x3_x1 = x3 - x1;
		final float x1_sqr = x1 * x1;
		final float x2_sqr = x2 * x2;
		final float x3_sqr = x3 * x3;
		
		float divisor = x1_x2 * x2_x3 * x3_x1;
		
		divisor = ( Math.abs(divisor) < 0.0001f ) ? 0.0001f : divisor;
		
		//ax^2 + bx + c
		float a = ( 2 * x2 - (x1 + x3) ) * delta_t;
		float b = ( x1_sqr - 2 * x2_sqr + x3_sqr ) * delta_t;
		
		a /= divisor;
		b /= divisor;
		
		return (int)(a * x3 + b);
	}*/
	
	private void calculateMotorSpeeds(float compensation) {
		int powerMotorA = (int) (BASE_POWER * (1 + compensation));
		int powerMotorB = (int) (BASE_POWER * (1 - compensation));
		
		setMotorPower(motorA, powerMotorA, lastPowerMotorA);
		setMotorPower(motorB, powerMotorB, lastPowerMotorB);
		lastPowerMotorA = powerMotorA;
		lastPowerMotorB = powerMotorB;
	}

	private static void setMotorPower(NXTMotor motor, int power, int oldPower) {
		final int absPower = Math.abs(power);
		motor.setPower( absPower );
		
		float power_signum = Math.signum(power);
		if ( power_signum == Math.signum(oldPower)) {
			return;
		}
		
		if ( power_signum == -1.0f) {
			motor.backward();
		} else {
			motor.forward();
		}
	}
}
