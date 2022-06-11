package cmap;

import java.util.*;

public class CorrelMap {
	public Scenario scenario;
	public Map<StringPair,Double> M;
	private Functions func = new Functions();
	private List<AggregateOperator> aggOperators;
	
	public CorrelMap(Scenario sc, List<AggregateOperator> aggOperators){
		if (aggOperators == null) {
			this.aggOperators = new ArrayList<AggregateOperator>();
			//TO DO: add operators
		} else {	
			this.aggOperators = aggOperators;
		}
		this.scenario = sc;
		this.M = new HashMap<StringPair,Double>(); //1
		List<Sensor> allSensors = new ArrayList<Sensor>();
		
		for(Node node:this.scenario.nodes) {
			for(Sensor sensor:node.sensors) {
				allSensors.add(sensor);
			}
		}
		
		//Initialize
		for(Node node:this.scenario.nodes) { // ad 2
			for(int m=0; m<node.sensors.size();m++) { // ad 3
				for(int s=m+1;s<node.sensors.size();s++) {
					this.M.put(new StringPair(node.sensors.get(m).name, node.sensors.get(s).name), 0.0); // ad 4
				}
			}
			for(int m=0; m<allSensors.size();m++) { // ad 6
				for(int s=m+1;s<allSensors.size();s++) {
					for (int a=0;a<this.aggOperators.size();a++) { // ad 7
						this.M.put(new StringPair(allSensors.get(m).name,this.aggOperators.get(a).name+"~X"+allSensors.get(s).name), 0.0); // ad 10
						
						for(int b=0;b<this.aggOperators.size();b++) { // ad 11
							if (b == a) continue;
							this.M.put(new StringPair(this.aggOperators.get(a).name+"~X"
							+allSensors.get(m).name,this.aggOperators.get(b).name+"~X"+allSensors.get(s).name), 0.0); // ad 12, 13
						}
						
					}
				}
			}
		}
		//End
		
		for(Node node:this.scenario.nodes) { //2
			int L = 0;
			for(int m=0; m<node.sensors.size();m++) { //3
				for(int s=m+1;s<node.sensors.size();s++) {
					double oldValue = this.M.get(new StringPair(node.sensors.get(m).name,node.sensors.get(s).name)); //4
					this.M.put(new StringPair(node.sensors.get(m).name,node.sensors.get(s).name),
							oldValue + func.FisherZ(Math.abs(func.corr(node.sensors.get(m).measurements, node.sensors.get(m).measurements))));
				}
			}
			L = node.neighbors.size(); //5
			for(int m=0; m<allSensors.size();m++) { //6
				for(int s=m+1;s<allSensors.size();s++) {
					for (int a=0;a<this.aggOperators.size();a++) { //7
						List<List<Double>> Xim = new ArrayList<List<Double>>(); //8
						Node sensorNode = FindParentNode(allSensors.get(m));
						
						for(Sensor sensor:sensorNode.sensors) {
							Xim.add(new ArrayList<Double>(sensor.measurements));
						}
						List<List<Double>> Xis = new ArrayList<List<Double>>(); //9
						sensorNode = FindParentNode(allSensors.get(s));
						
						for(Sensor sensor:sensorNode.sensors) {
							Xis.add(new ArrayList<Double>(sensor.measurements));
						}
						double old = this.M.get(new StringPair(allSensors.get(m).name,this.aggOperators.get(a).name+"~X"+allSensors.get(s).name));
						this.M.put(new StringPair(allSensors.get(m).name,this.aggOperators.get(a).name+"~X"+allSensors.get(s).name),
								old + func.FisherZ(Math.abs(func.corr(allSensors.get(m).measurements, this.aggOperators.get(a).Invoke(Xim))))); //10
						
						for(int b=0;b<this.aggOperators.size();b++) {
							if (b == a) continue; //11
							double oldValue = this.M.get(new StringPair(this.aggOperators.get(a).name+"~X"
							+allSensors.get(m).name,this.aggOperators.get(b).name+"~X"+allSensors.get(s).name));
							
							this.M.put(new StringPair(this.aggOperators.get(a).name+"~X"+allSensors.get(m).name,this.aggOperators.get(b).name+"~X"+allSensors.get(s).name),
									oldValue + func.FisherZ(Math.abs(func.corr(this.aggOperators.get(a).Invoke(Xim),this.aggOperators.get(b).Invoke(Xis))))); // 12, 13
						}				
					}
					
				}
			}
		}
	}
	
	public Node FindParentNode(Sensor s) {
		for (Node node:this.scenario.nodes) {
			for (Sensor sensor:node.sensors) {
				if (sensor.name == s.name) {
					return node;
				}
			}
		}
		return null;
	}
}