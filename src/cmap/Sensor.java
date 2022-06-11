package cmap;

import java.util.ArrayList;
import java.util.List;

public class Sensor {
	public List<Double> measurements = new ArrayList<Double>();
	private int iterator=0;
	public String name;
	
	public Sensor(String name, List<Double> measurements) {
		this.name = name;
		this.measurements = measurements;
	}
	
	public Sensor(String name) {
		this.name = name;
	}
	
	public Sensor(String name, double value) {
		this.name = name;
		this.measurements = new ArrayList<Double>();
		this.measurements.add(value);
	}
	
	public double Measurement() {
		if (this.measurements.size() == 0) {
			return 0;
		} else if (iterator < measurements.size()){
			double x = this.measurements.get(iterator);
			iterator +=1;
			return x;
		} else {
			iterator = 0;
			return this.measurements.get(iterator);
		}
	}
	
	@Override
	public boolean equals(Object a) {
		if ((((Sensor)a).name.equals(this.name))) {
			return true;
		}
		return false;
	}
}
