package simulator.factories;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;

public class BuilderBasedFactory<T> implements Factory<T> {
	private Map<String, Builder<T>> _builders; 
	private List<JSONObject> _builders_info;
	
	//CONSTRUCTORAS
	public BuilderBasedFactory() {
		// Create a HashMap for _builders, and a LinkedList _builders_info
		// …
		this._builders = new HashMap<String, Builder<T>>();  
		this._builders_info = new LinkedList<>(); 
	}
	
	public BuilderBasedFactory(List<Builder<T>> builders) { 
		this();
		// call add_builder(b) for each builder b in builder
		// …
		for(Builder<T> b : builders) { 
			//hacer algo
			add_builder(b); 
		}
	}
	
	
	public void add_builder(Builder<T> b) {
		// add an entry “b.getTag() |−> b” to _builders.
		// ...
		// add b.get_info() to _buildersInfo
		// ...
		_builders.put(b.get_type_tag(), b); 
		_builders_info.add(b.get_info()); //lo metemos en la lista 
		
	}
	
	
	@Override
	public T create_instance(JSONObject info) {
		// TODO Auto-generated method stub
		if(info == null) {
			throw new IllegalArgumentException("’info’ cannot be null");
		}
		// Look for a builder with a tag equals to info.getString("type"), in the
		// map _builder, and call its create_instance method and return the result
		// if it is not null. The value you pass to create_instance is the following
		// because ‘data’ is optional:
		//
		// info.has("data") ? info.getJSONObject("data") : new getJSONObject()
		// …
	
		String type = info.getString("type"); 
		Builder<T> b = _builders.get(type); 
		//b.create_instance(info); No le pasamos info. Solo queremos paaarle el valor de data
		if(b != null) {
			JSONObject data = info.has("data") ? info.getJSONObject("data") : new JSONObject(); 
			return b.create_instance(data); 
		}
		
		// If no builder is found or the result is null ...
		throw new IllegalArgumentException("Unrecognized ‘info’:" + info.toString());
	}

	@Override
	public List<JSONObject> get_info() {
		// TODO Auto-generated method stub
		return Collections.unmodifiableList(_builders_info);  
	}
	
	

}
