package simulator.model;

import simulator.misc.Vector2D;

import org.json.JSONArray;
import org.json.JSONObject;

import simulator.misc.Utils; 

public abstract class Animal implements Entity, AnimalInfo { 
	
	public static final double energia = 100.0; 
	public static final double velocidad = 0.1; //cambiar a 0,1 
	
	public final static double initspeed = 0.1; 
	public final static double sesenta = 60.0; 
	public final static double cer0dos = 0.2;  
	protected final static double unoDos = 1.2; 
	
	protected static final double ocho = 8.0; 
	protected static final double cero = 0.0; 
	protected static final double cien = 100.0; 
	protected static final double tresCerosSiete = 0.007; 
	protected static final double veinte = 20.0;
	protected static final double cuarenta = 40.0; 
	protected static final double sesentaycinco = 65.0; 
	protected static final double dos = 2.0; 
	protected static final double ceroNueve = 0.9; 
	protected static final double catorce = 14.0; 
	protected static final double dieciocho = 18.0; 
	protected static final double treinta = 30.0;
	protected static final double cincuenta = 50.0; 
	protected static final double tres = 3.0;  
	protected static final double diez = 10.0;   
	

	protected double run = Utils._rand.nextDouble(); //la `prob va de 0 a 1
	
	
	
	
	//constructoras
	/*
	 Donde genetic_code tiene que ser una cadena de caracteres no vac�a, sight_range y init_speed
	n�meros positivos y mate_strategy no es null. Hay que lanzar la excepci�n correspondiente con un
	mensaje informativo si alg�n valor es incorrecto.

	  */
	
	protected Animal(String genetic_code, Diet diet, double sight_range,
			double init_speed, SelectionStrategy mate_strategy, Vector2D pos) {
		this._genetic_code = genetic_code; 
		this._diet = diet; 
		this._sight_range = sight_range;
		this._pos = pos; 
		this._mate_strategy = mate_strategy;  
		this._speed = Utils.get_randomized_parameter(init_speed, initspeed); //ns si funciona asi 
		
		this._state = State.NORMAL;  
		this._energy = energia; 
		this._desire = 0.0;
		
		//this._dest = null; POR AHORA NO PONEMOS NULL
		this._dest = null;  
		
		this._mate_target = null; 
		this._baby = null;
		this._region_mngr = null; 
		
		this._age = 0.0; 
		
	}
	
	protected Animal(Animal p1, Animal p2) {
		this._dest = null;
		this._baby = null;
		this._mate_target = null;
		this._region_mngr = null; 
		this._state = State.NORMAL; 
		this._desire = 0.0;
		this._genetic_code = p1.get_genetic_code();
		this._diet = p1.get_diet(); 
		this._energy = (p1.get_energy() + p2.get_energy()) / 2; 
		this._pos = p1.get_position().plus(Vector2D.get_random_vector(-1,1).scale(sesenta*(Utils._rand
				.nextGaussian()+1))); 
		this._sight_range = Utils.get_randomized_parameter((p1.get_sight_range()+p2.get_sight_range())/2, cer0dos);
		this._speed = Utils.get_randomized_parameter((p1.get_speed()+p2.get_speed())/2, cer0dos); 
		
		this._mate_strategy = p1._mate_strategy; 
		
		this._age = 0.0; 
	}
	
	//Atributos
	
	public enum Diet{
		CARNIVORE,
		HERVIBORE,
	};
	
	public enum State{
		NORMAL,
		MATE,
		HUNGER,
		DANGER,
		DEAD,
	};
	

	
	
	protected String _genetic_code; //cada subclase asigna un valor a esto. Se usa para 
									//saber si 2 animales pueden o no emparejarse
	protected Diet _diet; 
	protected State _state; 
	protected Vector2D _pos;  
	protected Vector2D _dest; //el destino del animal
	protected double _energy; 
	protected double _speed; 
	protected double _age; //cuando llega a un maximo, el animal muere
	protected double _desire; //se usa para saber si un animal entra o sale de un emparejamiento
	protected double _sight_range; //campo visual del animal
	protected Animal _mate_target; //referencia a animal con quien emparejarse
	protected Animal _baby; //referencia que indica is el animal lleva un beb� que NO ha nacido aun
	protected AnimalMapView _region_mngr; //se inicializa a null
	protected SelectionStrategy _mate_strategy; //es la estrategia de buscar pareja
	
	//protected static final int width = 800; 
	//protected static final int height = 600;  
	
	
	//METODOS
	
	//el gestor de regiones llama a este metodo al a�adir animales a la simulacion
	void init(AnimalMapView reg_mngr) {
		this._region_mngr = reg_mngr;  
		int width = _region_mngr.get_width(); 
		int height = _region_mngr.get_height(); 
		if(_pos == null) {
			//elegir posicion aleatoria dentro del mapa. INICIALMENTE PONDREMOS 800 Y 600
			double x1 = Utils._rand.nextDouble(_region_mngr.get_width()-1); //800
			double y1 = Utils._rand.nextDouble(_region_mngr.get_height()-1);  
			Vector2D v = new Vector2D(x1, y1); 
			this._pos = v;  
		}
		else {
			//ajustar posicion si es necesario
			double x1 = _pos.getX();
			double y1 = _pos.getY();
			actualizar_pos(x1, y1); 
			
			 
		}
		//elegir posicion aleatoria para _dest
		double x2 = Utils._rand.nextDouble(_region_mngr.get_width()-1);
		double y2 = Utils._rand.nextDouble(_region_mngr.get_height()-1);   
		Vector2D v = new Vector2D(x2, y2);  
		_dest = v; 
		
	}
	
	Animal deliver_baby() { 
		Animal babyaux = this._baby; 
		this._baby = null;
		return babyaux; 
	}
	
	protected void move(double speed) {  
		_pos = _pos.plus(_dest.minus(_pos).direction().scale(speed)); 
	}
	
	public JSONObject as_JSON() {
		JSONObject json = new JSONObject(); 
		
		Vector2D pos = this.get_position(); 
		json.put("pos", new JSONArray().put(pos.getX()).put(pos.getY())); 
		json.put("gcode", this.get_genetic_code());  
		json.put("diet", this.get_diet().name()); //metodo name para para obtener el string
		json.put("state", this.get_state().name());   
		
		
		return json;  
		 
	}
	
	
	
	@Override
	public boolean is_pregnant() {
		if(this._baby != null) {
			return true;
		}
		else {
			return false; 
		}
	}
	
	protected void actualizar_pos(double x, double y) {
		int width = _region_mngr.get_width(); 
		int height = _region_mngr.get_height();
		while (x >= width) x = (x - width);  
		while (x < 0) x = (x + width); 
		while (y >= height) y = (y - height);
		while (y < 0) y = (y + height);
		_pos = new Vector2D(x,y); //modificamos la posicion 
	}
	

}
