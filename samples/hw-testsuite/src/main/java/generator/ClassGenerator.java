package generator;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ClassGenerator {

	
	
	public List<GenClass> generateClassHierarchy(int classCount, int packageCount, double density){
		List<GenClass> result = createTypes(classCount, packageCount);
		wireConnections(result, density);
		return result;
	}

	private void wireConnections(List<GenClass> result, double density) {

		int[][] rndDag = generateRandomDag(result.size(), density);
		for(int i = 0; i < result.size(); ++i){
			for(int j = 0; j < result.size(); ++j)
				if (rndDag[i][j] > 0){
					int fieldId = result.get(i).getFields().size();
					GenField field = new GenField("field"+fieldId, result.get(j));
					result.get(i).getFields().add(field);
				}
		}
	}

	private List<GenClass> createTypes(int classCount, int packageCount) {
		Random rnd = new Random();
		
		List<GenClass> result = new ArrayList<GenClass>();
		for(int i = 0; i < classCount; ++i){
			GenClass c = new GenClass(i);
			c.setName("Type"+i);
			c.setPackageName("root.pack" + (rnd.nextInt(packageCount) ));
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
