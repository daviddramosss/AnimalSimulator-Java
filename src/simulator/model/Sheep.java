package simulator.model;

import java.util.List;
import java.util.Random;
import java.util.function.Predicate;

import simulator.misc.Utils;
import simulator.misc.Vector2D;

public class Sheep extends Animal{
	
	public Animal _danger_source; //animal que se considera un peligro
	public SelectionStrategy _danger_strategy; //estrategia
	
	
	private static final double _sight_range = 40.0;   
	private static final double _speed = 35.0; 
	private static final String _genetic_code = "Sheep";   
	private static final Diet diet = Diet.HERVIBORE; 
	
	
	private double velocidad; 

	//Constructora
	public Sheep(SelectionStrategy mate_strategy, SelectionStrategy danger_strategy, Vector2D pos) {
		super(_genetic_code, diet, _sight_range, _speed, mate_strategy, pos);     
		this._danger_strategy = danger_strategy;
	}
	
	protected Sheep(Sheep p1, Animal p2) {
		super(p1, p2);
		this._danger_strategy = p1._danger_strategy; 
		this._danger_source = null; 
	}
	
	//METODOS
	private void avanzar(double dt) { //PASO 1 DEL METODO NORMAL
		if(_pos.distanceTo(_dest) < ocho) { 
			//Elegimos otra posicion de destino
			double x1 = Utils._rand.nextDouble(_region_mngr.get_width()-1);
			double y1 = Utils._rand.nextDouble(_region_mngr.get_height()-1);  
			Vector2D v = new Vector2D(x1, y1); 
			_dest = v; 
			}
			velocidad =  _speed*dt*Math.exp((_energy-cien)*tresCerosSiete);  
			move(velocidad); 
			_age += dt; 
			_energy = _energy - (veinte*dt);  
			if (_energy < cero){_energy = cero;}
			if(_energy > cien) {_energy = cien;}
			
			_desire = _desire + (cuarenta*dt);
			if(_desire < cero) { _desire = cero;}
			if(_desire > cien) {_desire = cien; } 
			
	}
	
	
	@Override
	public void update(double dt) { 
		// TODO Auto-generated method stub
		Predicate<Animal> obejas = (Animal) -> Animal.get_genetic_code() == "Sheep";   
		List<Animal> obejas_visibles = _region_mngr.get_animals_in_range(this, obejas);
		//_mate_target = _mate_strategy.select(this, obejas_visibles);   
		
	if(_state == State.DEAD) {  
		//nada
	}
	else {
		if(_state == State.NORMAL) { //NORMAL
		//1. AVANZAR AL ANIMAL 	
			avanzar(dt); //creo esto para no repetir codigo
		//2. CAMBIO DE ESTADO
			if(_danger_source == null) {  
				Predicate<Animal> carnivoros = (Animal) -> Animal.get_diet() == Animal._diet.CARNIVORE;
				//lista de carnivoros en mi campo de vision
				List<Animal> carnivoros_visibles = _region_mngr.get_animals_in_range(this, carnivoros); 
				//estrategia
				_danger_source = _danger_strategy.select(this, carnivoros_visibles);   
				//la estrategia que implemente ya no se como va tipo ns cual elegira si closest o cual xd
				
			}
			if(_danger_source != null) {_state = State.DANGER; _mate_target = null;} 
			else {
				if(_desire > sesentaycinco) {_state = State.MATE;} 
			}
			
		}
		
		else if(_state == State.DANGER) { //DANGER 
			if(_danger_source != null && _danger_source.get_state() == State.DEAD) { //1 
				_danger_source = null; 
			}
			if(_danger_source == null) { //2
				avanzar(dt); 
			}
			else {
				//2.1
				_dest = _pos.plus(_pos.minus(_danger_source.get_position()).direction()); 
				//2.2
				velocidad = dos*_speed*dt*Math.exp((_energy-cien)*tresCerosSiete); 
				move(velocidad); 
				//2.3
				_age += dt; 
				//2.4
				_energy -= veinte*unoDos*dt;  
				if (_energy < cero){_energy = cero;}
				if(_energy > cien) {_energy = cien;} 
				//2.5
				_desire += cuarenta*dt;  
				if(_desire < cero) {_desire = cero;}
				if(_desire > cien) {_desire = cien;}
			}
			//3 Cambio de estado
			Predicate<Animal> carnivoros = (Animal) -> Animal.get_diet() == _diet.CARNIVORE;  
			List<Animal> carnivoros_visibles = _region_mngr.get_animals_in_range(this, carnivoros); 
			if(_danger_source == null || !carnivoros_visibles.contains(_danger_source)) { 
				_danger_source = _danger_strategy.select(this, carnivoros_visibles); 
				if(_danger_source == null) {
					if(_desire < sesentaycinco) {_state = State.NORMAL; _mate_target = null;}
					else {_state = State.MATE;}
				}
				
			}
		}
		
		
		else if(_state == State.MATE) { //MATE
			if(_mate_target != null && 
					(_mate_target.get_state() != State.DEAD || !obejas_visibles.contains(_mate_target))){
				_mate_target = null; 
			}
			else if(_mate_target == null) {
				_mate_target = _mate_strategy.select(this, obejas_visibles); 
				if(_mate_target == null) {
					avanzar(dt); 
				}
				else {
					//2.1
					_dest = _mate_target.get_position(); 
					//2.2
					velocidad = dos*_speed*dt*Math.exp((_energy-cien)*tresCerosSiete); 
					move(velocidad);
					//2.3
					_age += dt; 
					//2.4
					_energy -=  veinte*unoDos*dt;
					if (_energy < cero){_energy = cero;}
					if(_energy > cien) {_energy = cien;}
					//2.5
					_desire += cuarenta*dt; 
					if(_desire < cero) {_desire = cero;}
					if(_desire > cien) {_desire = cien;}
					//2.6
					if(_pos.distanceTo(_mate_target.get_position()) < ocho) {
						//2.6.1
						_desire = 0.0; 
						_mate_target._desire = 0.0; //hago esto?
						//2.6.2
						//Random r = new Random();
						//double random_value = r.nextDouble();
						if(this._baby == null && run < ceroNueve) { 
							 this._baby = new Sheep(this, _mate_target);
						} 
						//2.6.3
						_mate_target = null; 
					}
				}
				//3
				if(_danger_source == null) {
					Predicate<Animal> carnivoros = (Animal) -> Animal.get_diet() == _diet.CARNIVORE;  
					List<Animal> carnivoros_visibles = _region_mngr.get_animals_in_range(this, carnivoros); 
					_danger_source = _danger_strategy.select(this, carnivoros_visibles); 
				}
				//4
				if(_danger_source != null) {
					_state = State.DANGER; 
					_mate_target = null;
				}
				else {
					if(_desire < sesentaycinco) {
						_state = State.NORMAL; 
						_mate_target = null; 
					}
				}
				
			}
		}//elsif de MATE
		double x = _pos.getX();
		double y = _pos.getY();  
	   //3) Ajustar la posicion. PREGUNTAR SI SE HACE ASI
		actualizar_pos(x, y); 
		//4)
		if(_energy == 0.0 || _age > ocho) { 
			_state = State.DEAD;
		}
		//5)
		if(_state != State.DEAD) {
			//pedir comida al gestor de regiones. get_food(this, dt)
			double food =_region_mngr.get_food(this, dt); 
			_energy += food;
			if (_energy < cero){ _energy = cero; }
			if(_energy > cien) { _energy = cien;} 
		}//if state != dead
	}//else grande
		
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
