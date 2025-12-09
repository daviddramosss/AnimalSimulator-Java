package simulator.model;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

public abstract class Region implements Entity, FoodSupplier, RegionInfo {
	
	protected static final double sesenta = 60.0;
	protected static final double cinco = 5.0;
	protected static final double dos = 2.0; 
	
	protected List<Animal> l = new LinkedList<>(); 
	//constructura por defecto
	Region(){
		
	}
	
	//Metodos
	//a�ade el animal a la lista de animales
	final void add_animal(Animal a) {
		l.add(a); 
	}
	
	// quita el animal de la lista de animales
	final void remove_animal(Animal a) {
		l.remove(a); 
	}
	
	// devuelve una versi�n inmodificable de la lista de animales.
	final List<Animal> getAnimals(){
		return l;  
	}
	
	
	/*Explicacion:
	 * el código return new ArrayList<>(l); 
	 * crea una nueva lista de AnimalInfo a partir de la lista l de Animal
	 * 
	 * Si devolviera return l devoleria una lista de Animal, NO de AnimalInfo.
	 * Al hacer ArrayList, solo devuelve los AnimalInfo de los animales ya presentes
	 * en la lista
	 */
	public List<AnimalInfo> getAnimalsInfo(){ 
		return new ArrayList<>(l);   
	}
	
	
	
	public JSONObject as_JSON() {
		JSONObject json = new JSONObject();
		JSONArray arrayjson = new JSONArray();  
		
		for(Animal a: l) {
			arrayjson.put(a.as_JSON());
		}
		
		json.put("animals", arrayjson); 
		
		return json; 
	} 
	
	
	
}
