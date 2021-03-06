package rcms.utilities.daqaggregator.data.helper;

import java.util.Date;

import org.apache.log4j.Logger;

import rcms.utilities.daqaggregator.datasource.DateParser;

public class BackpressureConverter {

	private double lastValue;
	private long lastTime;
	private double lastResult;

	@SuppressWarnings("unused")
	private static final Logger logger = Logger.getLogger(BackpressureConverter.class);

	public double calculate(double value, long time) {

		int deltaTime = (int) (time - lastTime);

		double deltaVal = value - lastValue;

		double backpressure = (1000 * deltaVal) / deltaTime;

		/* restart of accumulated backpressure will be handled here */
		if (backpressure < 0)
			backpressure = 0;

		// Regular conditions
		if (time > lastTime) {
			lastValue = value;
			lastTime = time;
			lastResult = backpressure;
			return backpressure;
		}

		// repeat in case delta time cannot be >0
		else {
			return lastResult;
		}
	}

	public double calculate(double value, String time) {
		long timeLong;
		Date date = DateParser.parseDateTransparently(time);
		if (date == null) {
			return lastResult;
		}

		timeLong = date.getTime();
		return calculate(value, timeLong);
	}

	public float calculatePercent(double value, String time) {
		return (float) (calculate(value, time) * 100);
	}
}
