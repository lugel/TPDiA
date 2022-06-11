package cmap;

import java.util.ArrayList;
import java.util.List;

public class Node {
	public List<Sensor> sensors = new ArrayList<Sensor>();
	public List<Node> neighbors = new ArrayList<Node>();
	
	public Node(List<Sensor> s) {
		this.sensors = s;
	}	
	
	public void AddNeighbor(Node n) {
		if (this.neighbors.contains(n)) return;
		this.neighbors.add(n);
	}
	
	public void RemoveNeighbor(Node n) {
		if (this.neighbors.contains(n)) {
			this.neighbors.remove(n);
		}
	}
	
	public void ClearNeighbors() {
		this.neighbors = new ArrayList<Node>();
	}
}
