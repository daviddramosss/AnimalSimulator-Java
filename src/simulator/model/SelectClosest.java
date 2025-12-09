package simulator.model;

import java.util.List;

public class SelectClosest implements SelectionStrategy{

	@Override
	public Animal select(Animal a, List<Animal> as) {
		if(as.isEmpty()) { 
			return null;
		}
		else {
			Animal closest = as.get(0); 
			double minDistance = a.get_position().distanceTo(closest.get_position());   
			for(Animal an : as) {
				if(a.get_position().distanceTo(an.get_position()) < minDistance){ 
					minDistance = a.get_position().distanceTo(an.get_position()); 
					closest = an; 
				}
			}
			return closest; 
		}
	}

}
