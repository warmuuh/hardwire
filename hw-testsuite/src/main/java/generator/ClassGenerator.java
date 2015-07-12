package generator;

import java.util.ArrayList;
import java.util.List;

public class ClassGenerator {

	
	
	public List<GenClass> generateClassHierarchy(){
		int classCount = 5;
		List<GenClass> result = createTypes(classCount);
		wireConnections(result);
		return result;
	}

	private void wireConnections(List<GenClass> result) {

		int[][] rndDag = generateRandomDag(result.size(), 0.5);
		for(int i = 0; i < result.size(); ++i){
			for(int j = 0; j < result.size(); ++j)
				if (rndDag[i][j] > 1)
					result.get(i).getConnections().add(result.get(j));
		}
	}

	private List<GenClass> createTypes(int classCount) {
		List<GenClass> result = new ArrayList<GenClass>();
		for(int i = 0; i < classCount; ++i){
			GenClass c = new GenClass();
			c.setName("Type"+i);
			result.add(c);
		}
		return result;
	}

	private int[][] generateRandomDag(int n, double density) {
		int[][] result = new int[n][];
		for(int i = 0; i < n; ++i){
			result[i] = new int[n];
			for(int j = 0; j < i; ++j){
				if (Math.random() < density){
					result[i][j] = 1;
				}
			}
		}
		return result;
	}
}
