package generator;

import java.util.LinkedList;
import java.util.List;

public class GenClass {
	String name;
	List<GenClass> connections = new LinkedList<GenClass>();
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public List<GenClass> getConnections() {
		return connections;
	}
	public void setConnections(List<GenClass> connections) {
		this.connections = connections;
	}
	
	
	
}
