package cmap;

import java.util.List;

public class AggregateOperator {
	public String name;
	private AOFunction f;
	public AggregateOperator(String name, AOFunction f) {
		this.name = name;
		this.f = f;
	}

	@Override
	public boolean equals(Object a) {
		if ((((AggregateOperator)a).name.equals(this.name))) {
			return true;
		}
		return false;
	}
	
	public List<Double> Invoke(List<List<Double>> data) {
		return this.f.function(data);
	}
}
