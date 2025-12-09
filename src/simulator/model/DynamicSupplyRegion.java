package simulator.model;

import java.util.Random;

import simulator.model.Animal.Diet;

public class DynamicSupplyRegion extends Region{

	private double _food;
	private double _factor;
	
	public DynamicSupplyRegion(double comida_inicial, double crecimiento) {
		this._food = comida_inicial;
		this._factor = crecimiento;  
	}
	@Override
	public void update(double dt) {
		// TODO Auto-generated method stub
		Random r = new Random();
		double random_value = r.nextDouble();
		if(random_value < 0.5) {
			_food += dt*_factor;
		}
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
			double res = Math.min(_food,sesenta*Math.exp(-Math.max(0,n-cinco)*dos)*dt);  
			_food -= res;
			return res; 
			
		}

	}
	
	@Override 
	public String to_String() {
		return "Dynamic region"; 
	}

}
