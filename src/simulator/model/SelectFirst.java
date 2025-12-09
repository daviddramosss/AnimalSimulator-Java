package simulator.model;

import java.util.List;
 
public class SelectFirst implements SelectionStrategy{

	public SelectFirst() {
		
	}
	@Override
	public Animal select(Animal a, List<Animal> as) {
		if(as.isEmpty()) {
			return null;
		}
		else {
			a = as.get(0);
			return a; 
		}
	}

}
