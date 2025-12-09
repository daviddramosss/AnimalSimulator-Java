package simulator.factories;

import java.util.List;

import org.json.JSONObject;

public interface Factory<T> {
	public T create_instance(JSONObject info); 
	/*
	 * 
	 *
	 * */
	
	//devuelve lista de objetos JSON que describen que puede ser creado por la factoria
	public List<JSONObject> get_info();
}
