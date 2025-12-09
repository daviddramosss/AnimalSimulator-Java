package simulator.model;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.json.JSONObject;

import simulator.factories.Factory;
import simulator.model.Animal.Diet;
import simulator.model.Animal.State;

public class Simulator implements JSONable, Observable<EcoSysObserver>{ 
	
	private RegionManager _reg_mngr;
	List<Animal> l;  
	Factory<Animal> animals_factory; 
	Factory<Region> regions_factory; 
	private double time; 
	private int col;
	private int row;
	private int width;
	private int height; 
	List<EcoSysObserver> lista_ob;   
	
	 
	
	public Simulator(int cols, int rows, int width, int height,
			Factory<Animal> animals_factory, Factory<Region> regions_factory) {
		this.col = cols;
		this.row = rows;
		this.width = width;  
		this.height = height; 
		this.animals_factory = animals_factory;
		this.regions_factory = regions_factory; 
		this.l = new LinkedList<>();
		this._reg_mngr = new RegionManager(col, row, width, height); 
		this.time = 0.0; 
		this.lista_ob = new LinkedList<>();
	}
	
	//metodos
	private void set_region(int row, int col, Region r) { //IMPLEMENTAR ESTE
		_reg_mngr.set_region(row, col, r);
		for(EcoSysObserver o : lista_ob) { 
			o.onRegionSet(row, col, _reg_mngr, r); 
		}
	}
	
	public void set_region(int row, int col, JSONObject r_json) {
		Region r = regions_factory.create_instance(r_json);
		set_region(row, col, r);
		 
	}
	
	private void add_animal(Animal a) { //IMPLEMENTAR ESTE
		l.add(a); 
		_reg_mngr.register_animal(a); 
		
		List<AnimalInfo> animals = new ArrayList<>(l); 
		
		for(EcoSysObserver o : lista_ob) { 
			o.onAnimalAdded(time, _reg_mngr, animals, a); 
		}
		
		
	}
	
	public void add_animal(JSONObject a_json) {
		//Hay q pasar el genetic_code, diet, sight_range, init_speed, mate_strategy, pos
		//diferenciar de si es obeja o lobo
		
		Animal a = animals_factory.create_instance(a_json); 
		add_animal(a);
		
		
	}
	
	public MapInfo get_map_info() {
		return _reg_mngr;  
	}
	
	public List<? extends AnimalInfo> get_animals(){ 
		return l;  
	}
	
	public double get_time() {
		return time;
	}
	
	public void advance(double dt) {
		time += dt; 
		//quitamos a los muertos
		Iterator<Animal> it = l.iterator(); 
		while(it.hasNext()) {
			Animal a = it.next(); 
			if(a.get_state()==State.DEAD) {
				it.remove(); 
				_reg_mngr.unregister_animal(a); 
			}
			else {
				a.update(dt); 
				_reg_mngr.update_animal_region(a); 
			}
		}
		
		_reg_mngr.update_all_regions(dt); 
		
		List<Animal> newborns = new ArrayList<>();
	    for (Animal a : l) {
	        if (a.is_pregnant()) {
	            Animal baby = a.deliver_baby();
	            baby.init(_reg_mngr); //ns si esto es asi. Parece que funciona lol
	            newborns.add(baby);
	        } 
	    }

	    // Add newborn animals to the simulator
	    for (Animal baby : newborns) {
	        add_animal(baby); 
	    }
		
	    List<AnimalInfo> animals = new ArrayList<>(l); //lo hago cada vez, o con hacerlo una basta?
		for(EcoSysObserver o : lista_ob) { 
			o.onAvanced(time, _reg_mngr, animals, dt);    
		}
		
	}
	
	//NUEVO METODO
	public void reset(int cols, int rows, int width, int height) {
		l.clear(); //Vaciamos la lista
		this._reg_mngr = new RegionManager(col, row, width, height); 
		this.time = 0.0; 
		
		//El enunciado dice que se hace aqui, pero no tiene sentido pq animals va a estar vacio
		List<AnimalInfo> animals = new ArrayList<>(l); 
		for(EcoSysObserver o : lista_ob) { 
			o.onReset(time, _reg_mngr, animals);  
		}
		
	}
	
	public JSONObject as_JSON() {
		JSONObject json = new JSONObject();
		
		json.put("time", time);
		json.put("state",_reg_mngr.as_JSON());  
		return json;  
	}

	@Override
	public void addObserver(EcoSysObserver o) {
		this.lista_ob.add(o);
		
		//creo lista de animalInfo
		List<AnimalInfo> animals = new ArrayList<>(l); 
		//Duda si pasar re_mngr o pasar mapInfo 
		o.onRegister(time, _reg_mngr, animals);   
	}

	@Override
	public void removeObserver(EcoSysObserver o) {
		this.lista_ob.remove(o); 
		
	}
 
}
