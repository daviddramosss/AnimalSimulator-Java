package simulator.model;

import simulator.model.Animal.Diet;

public class DefaultRegion extends Region{

	//Da comida SOLO a HERVIBOROS
	@Override
	public void update(double dt) {
		//no hace nada
	}

	@Override
	public double get_food(Animal a, double dt) {
		if(a.get_diet() == Diet.CARNIVORE) {
			return 0.0; 
		}
		else {
			int n = 0; 
			for(Animal an : l) { //de esta forma sabemos el n� de herviboros que hay
				if(an.get_diet()==Diet.HERVIBORE) {
					n++;
				}
			}
			return sesenta*Math.exp(-Math.max(0,n-cinco)*dos)*dt; //n es el numero de herviboros
		}
		
	}
	
	@Override 
	public String to_String() {
		return "Default region"; 
	}

}
