package simulator.model;

import java.util.List;
import java.util.Random;
import java.util.function.Predicate;

import simulator.misc.Utils;
import simulator.misc.Vector2D;
import simulator.model.Animal.Diet;

public class Wolf extends Animal{
	
	private static final double _sight_range = 50.0;   
	private static final double _speed = 60.0; 
	private static final String _genetic_code = "Wolf"; 
	private static final Diet diet = Diet.CARNIVORE;  
	
	private Animal _hunt_target; 
	private SelectionStrategy _hunting_strategy; 
	private double velocidad; 
	
	//CONSTRUCTORA
	public Wolf(SelectionStrategy mate_strategy, SelectionStrategy hunting_strategy, Vector2D pos) {
		super(_genetic_code, diet, _sight_range, _speed, mate_strategy, pos);    
		this._hunting_strategy = hunting_strategy; 
	}
	
	protected Wolf(Wolf p1, Animal p2) {
		super(p1, p2);
		this._hunting_strategy = p1._hunting_strategy;
		this._hunt_target = null; 
	}

	private void avanzar(double dt) {
		if(_pos.distanceTo(_dest) < ocho) {
			//Elegimos otra posicion de destino
			double x = Utils._rand.nextDouble(_region_mngr.get_width()-1); 
			double y = Utils._rand.nextDouble(_region_mngr.get_height()-1); 
			Vector2D v = new Vector2D(x, y); 
			_dest = v; 
		}
		velocidad = _speed*dt*Math.exp((_energy-cien)*tresCerosSiete); 
		move(velocidad); 
		_age += dt; 
		_energy = _energy - (dieciocho*dt);    
		if (_energy < cero){_energy = cero;}
		if(_energy > cien) {_energy = cien;}
		_desire = _desire + (treinta*dt);
		if(_desire < cero) {_desire = cero;}
		if(_desire > cien) {_desire = cien; }
	}
	
	private void actualizar_normal(double dt) {
		//1) Avanzar al animal
		avanzar(dt); 
		//2) Cambio de estado
		if(_energy < cincuenta) {
			_state = State.HUNGER;
			_mate_target = null; 
		}
		else if(_desire > sesentaycinco && _energy >= sesentaycinco) {
			_state = State.MATE;
			_hunt_target = null; 
		}
		/*
		else {
			if(_desire > 65.0) {
				_state = State.MATE; 
				_hunt_target = null; 
			}
		}
		*/
	}
	
	private void actualizar_hunger(double dt) {
		Predicate<Animal> animales_cazables = (animal) -> animal.get_diet() == diet.HERVIBORE;  
		List<Animal> visibles = _region_mngr.get_animals_in_range(this, animales_cazables);  
		//_hunt_target = _hunting_strategy.select(this, visibles);  //obtengo a un animal en su campo de vision	
		if(_hunt_target == null || _hunt_target != null && 
				(_hunt_target.get_state() == State.DEAD || !visibles.contains(_hunt_target))) {
			_hunt_target = _hunting_strategy.select(this, visibles);  //obtengo a un animal en su campo de vision
		}
		if(_hunt_target == null) {
			avanzar(dt); 
		}
		else {
			//2.1
			_dest = _hunt_target.get_position(); 
			//2.2
			velocidad = tres*_speed*dt*Math.exp((_energy-cien)*tresCerosSiete);
			move(velocidad);
			//2.3
			_age += dt; 
			//2.4
			_energy -=  dieciocho*unoDos*dt; 
			if (_energy < cero){_energy = cero;}
			if(_energy > cien) {_energy = cien;}
			//2.5
			_desire += treinta*dt;
			if(_desire < cero) {_desire = cero;}
			if(_desire > cien) {_desire = cien; }
			//2.6
			if(_pos.distanceTo(_hunt_target.get_position()) < ocho) {
				//vamos a cazar
				_hunt_target._state = State.DEAD; 
				_hunt_target = null; 
				_energy += cincuenta;
				if (_energy < cero){_energy = cero;}
				if(_energy > cien) {_energy = cien;}
			}
		}
		//3 Cambiar de estado
		if(_energy > cincuenta) {
			if(_desire < sesentaycinco) { 
				_state = State.NORMAL; 
				_hunt_target = null;
				_mate_target = null; 
			}
			else {
				_state = State.MATE; 
				_hunt_target = null; 
			}
		}
	}
	
	private void actualizar_mate(double dt) {
		Predicate<Animal> wolfs_mates = (animal) -> animal.get_genetic_code() == "Wolf"; 
		List<Animal> mates_vision = _region_mngr.get_animals_in_range(this, wolfs_mates); 
		//1) la pareja actual esta muerta o fuera de la vision
		if(_mate_target != null && 
				(_mate_target.get_state() == State.DEAD || !mates_vision.contains(_mate_target))) {
			
			_mate_target = null;
		}
		//2) Si no tenemos pareja
		if(_mate_target == null) {
			//la buscamos en el area de vision
			_mate_target = _mate_strategy.select(this, mates_vision); 
			//si aun asi no la encontramos avanzamos
			if(_mate_target == null) {
				avanzar(dt);
			}
		}//si tenemos pareja
		else {
			//2.1
			_dest = _mate_target.get_position();
			//2.2
			velocidad = tres*_speed*dt*Math.exp((_energy-cien)*tresCerosSiete);
			move(velocidad); 
			//2.3
			_age += dt; 
			//2.4
			_energy -= dieciocho*unoDos*dt; 
			if (_energy < cero){_energy = cero;}
			if(_energy > cien) {_energy = cien;}
			//2.5
			_desire += treinta*dt; 
			if(_desire < cero) {_desire = cero;}
			if(_desire > cien) {_desire = cien; }
			//2.6
			if(_pos.distanceTo(_mate_target.get_position()) < ocho) {
				//2.6.1
				_desire = 0.0;
				_mate_target._desire = 0.0;
				//2.6.2
				//Random r = new Random();
				//double random_value = r.nextDouble();
				if(this._baby == null && this.run < ceroNueve) {    
					 _baby = new Wolf(this, _mate_target); //no estoy seguro de esto
				}
				
				//2.6.3
				_energy -= diez; 
				if (_energy < cero){_energy = cero;}
				if(_energy > cien) {_energy = cien;}
				//2.6.4
				_mate_target = null; 
			}
		}
		//3) Energia
		if(_energy < cincuenta) {
			_state = State.HUNGER;
			_mate_target = null;
		}
		else {
			if(_desire < sesentaycinco) { 
				_state = State.NORMAL; 
				_mate_target = null;
				_hunt_target = null; 
			}
		}
	}
	
	@Override
	public void update(double dt) {
		if(_state == State.DEAD) {
			
		}
		else {
			//Actualizar objeto
			if(this._state == State.NORMAL) {
				actualizar_normal(dt);
			}
			else if(this._state == State.HUNGER) {
				actualizar_hunger(dt);
			}
			else if(this._state == State.MATE) { 
				actualizar_mate(dt); 
			}
			//CODIGO PARA AJUSTAR POSICION 
			double x = _pos.getX();
		    double y = _pos.getY(); 
		    actualizar_pos(x,y); 
		    
			//4
			if(_energy == 0.0 || _age > catorce) {
				_state = State.DEAD;
			}
			if(_state != State.DEAD) {
				double food = _region_mngr.get_food(this, dt);
				_energy += food;
				if (_energy < cero){_energy = cero;}
				if(_energy > cien) {_energy = cien;}
			}
		}
		
	}

	
	@Override
	public State get_state() {
		// TODO Auto-generated method stub
		return _state; 
	}

	@Override
	public Vector2D get_position() {
		// TODO Auto-generated method stub
		return _pos; 
	}

	@Override
	public String get_genetic_code() {
		// TODO Auto-generated method stub
		return _genetic_code; 
	}

	@Override
	public Diet get_diet() {
		// TODO Auto-generated method stub
		return _diet;
	}

	@Override
	public double get_speed() {
		// TODO Auto-generated method stub
		return _speed;
	}

	@Override
	public double get_sight_range() {
		// TODO Auto-generated method stub
		return _sight_range;
	}

	@Override
	public double get_energy() {
		// TODO Auto-generated method stub
		return _energy; 
	}

	@Override
	public double get_age() {
		// TODO Auto-generated method stub
		return _age;
	}

	@Override
	public Vector2D get_destination() {
		// TODO Auto-generated method stub
		return _dest;
	}

	
}
