package cmap;

import java.util.*;

public class CorrelMap {
	public Scenario scenario;
	public Map<StringPair,Double> M;
	private Functions func = new Functions();
	private List<AggregateOperator> aggOperators;
	
	public CorrelMap(Scenario sc, List<AggregateOperator> aggOperators) {
		if (aggOperators == null) {
			this.aggOperators = new ArrayList<AggregateOperator>();
			
			AggregateOperator Avg = new AggregateOperator("average", 
					(p)->{
						ArrayList<Double> out = new ArrayList<Double>();
						for(int i=0;i<p.size();i++) {
							double sum = 0;
							for(int j=0;j<p.get(i).size();j++) {
								sum += p.get(i).get(j);
							}
							out.add(sum/p.get(i).size());
						}
						return out;
						});
			this.aggOperators.add(Avg);
			
			AggregateOperator Max = new AggregateOperator("max", 
					(p)->{
						ArrayList<Double> out = new ArrayList<Double>();
						for(int i=0;i<p.size();i++) {
							double max = p.get(i).get(0);
							for(int j=0;j<p.get(i).size();j++) {
								if(max < p.get(i).get(j)) {
									max = p.get(i).get(j);
								}			
							}
							out.add(max);
						}
						return out;
						});
			this.aggOperators.add(Max);
			
			AggregateOperator Min = new AggregateOperator("min", 
					(p)->{
						ArrayList<Double> out = new ArrayList<Double>();
						for(int i=0;i<p.size();i++) {
							double min = p.get(i).get(0);
							for(int j=0;j<p.get(i).size();j++) {
								if(min > p.get(i).get(j)) {
									min = p.get(i).get(j);
								}			
							}
							out.add(min);
						}
						return out;
						});
			this.aggOperators.add(Min);
			
			AggregateOperator Stdev = new AggregateOperator("standard dev", 
					(p)->{
						ArrayList<Double> out = new ArrayList<Double>();
						for(int i=0;i<p.size();i++) {
							out.add(func.stdev(out));
						}
						return out;
						});
			this.aggOperators.add(Stdev);
			
			AggregateOperator Med = new AggregateOperator("median", 
					(p)->{
						ArrayList<Double> out = new ArrayList<Double>();
						for(int i=0;i<p.size();i++) {
							out.add(func.median(out));
						}
						return out;
						});
			this.aggOperators.add(Med);
			
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
						
						for(int j=0;j<node.neighbors.size();j++) { // ad 14
							this.M.put(new StringPair(this.aggOperators.get(a).name+"~X"+allSensors.get(m).name, node.sensors.get(s).name), 0.0); // ad 15, 16
						}
					}
					
					for(int j=0;j<node.neighbors.size();j++) { // ad 17
						this.M.put(new StringPair(node.sensors.get(m).name,"~X"+allSensors.get(s).name), 0.0); // ad 18
					}
				}
			}
			for(int j=0; j<node.neighbors.size();j++) { // ad 20
				for(int k=j+1;k<node.neighbors.size();k++) {
					Node sensorNodeJ = FindParentNode(allSensors.get(j));
					for(int m=0;m<sensorNodeJ.sensors.size();m++) { // ad 21
						Node sensorNodeK = FindParentNode(allSensors.get(k));
						for(int s=0;s<sensorNodeK.sensors.size();s++) { // ad 22
							this.M.put(new StringPair("~X"+allSensors.get(m).name,"~X"+allSensors.get(s).name), 0.0); // ad 23
						}
					}
				}
			}
		}		
		//End
		
		for(Node node:this.scenario.nodes) { //2
			int L = 0;
			int P = 0;
			for(int m=0; m<node.sensors.size();m++) { //3
				for(int s=m+1;s<node.sensors.size();s++) {
					double oldValue = this.M.get(new StringPair(node.sensors.get(m).name,node.sensors.get(s).name)); 
					this.M.put(new StringPair(node.sensors.get(m).name,node.sensors.get(s).name),
							oldValue + func.FisherZ(Math.abs(func.corr(node.sensors.get(m).measurements, node.sensors.get(m).measurements)))); //4
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
									oldValue + func.FisherZ(Math.abs(func.corr(this.aggOperators.get(a).Invoke(Xim),this.aggOperators.get(b).Invoke(Xis))))); //12, 13
						}
						
						for(int j=0;j<node.neighbors.size();j++) { //14
							double oldValue = this.M.get(new StringPair(this.aggOperators.get(a).name+"~X"+allSensors.get(m).name, node.sensors.get(s).name));
							sensorNode = FindParentNode(allSensors.get(s));
							this.M.put(new StringPair(this.aggOperators.get(a).name+"~X"+allSensors.get(m).name, node.sensors.get(s).name),
									oldValue + func.FisherZ(Math.abs(func.corr(this.aggOperators.get(a).Invoke(Xim),sensorNode.sensors.get(s).measurements)))/L); //15, 16
						}
					}
					
					for(int j=0;j<node.neighbors.size();j++) { //17
						double oldValue = this.M.get(new StringPair(node.sensors.get(m).name,"~X"+allSensors.get(s).name));
						Node sensorNode = FindParentNode(allSensors.get(s));
						this.M.put(new StringPair(node.sensors.get(m).name,"~X"+allSensors.get(s).name),
								oldValue + func.FisherZ(Math.abs(func.corr(node.sensors.get(m).measurements, sensorNode.sensors.get(s).measurements)))/L); //18
					}
				}
			}
			
			P = func.combination(L, 2); // 19
			
			for(int j=0; j<node.neighbors.size();j++) { //20
				for(int k=j+1;k<node.neighbors.size();k++) {
					Node sensorNodeJ = FindParentNode(allSensors.get(j));
					for(int m=0;m<sensorNodeJ.sensors.size();m++) { //21
						Node sensorNodeK = FindParentNode(allSensors.get(k));
						for(int s=0;s<sensorNodeK.sensors.size();s++) { //22
							double oldValue = this.M.get(new StringPair("~X"+allSensors.get(m).name,"~X"+allSensors.get(s).name));
							this.M.put(new StringPair("~X"+allSensors.get(m).name,"~X"+allSensors.get(s).name),
									oldValue + func.FisherZ(Math.abs(func.corr(sensorNodeJ.sensors.get(m).measurements, sensorNodeK.sensors.get(s).measurements)))/P); //23
						}
					}
				}
			}
		}
		
		for(Map.Entry<StringPair, Double> entry:M.entrySet()) { //24
			entry.setValue(func.FisherZInv(entry.getValue()/this.scenario.nodes.size()));
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