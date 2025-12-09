package simulator.model;

import java.util.List;

public class SelectYoungest implements SelectionStrategy{

	@Override
	public Animal select(Animal a, List<Animal> as) {
		if(as.isEmpty()) {
			return null;
		}
		else {
			Animal youngest = as.get(0); //asumimos que el primer animal es el mas joven
			for(Animal an: as) {
				if(an.get_age() < youngest.get_age()) {
					youngest = an; 
				}
			}
			a = youngest;
			return a; 
		}
	}

}
