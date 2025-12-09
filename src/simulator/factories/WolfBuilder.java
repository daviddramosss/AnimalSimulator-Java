package simulator.factories;

import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import simulator.misc.Utils;
import simulator.misc.Vector2D;
import simulator.model.Animal;
import simulator.model.SelectFirst;
import simulator.model.SelectionStrategy;
import simulator.model.Wolf;

public class WolfBuilder extends Builder<Animal>{

	private static String type_tag = "wolf";
	private static String desc = "desc"; 
	private static List<Builder<SelectionStrategy>> selection_strategy_builders = null; 
	
	public WolfBuilder() {
		super(type_tag, desc);
		
	}
	
	public WolfBuilder(List<Builder<SelectionStrategy>> selection_strategy_builders) {
		this();
		this.selection_strategy_builders = selection_strategy_builders;  
	}

	@Override
	protected Animal create_instance(JSONObject data) {
		SelectionStrategy mate_strategy = null;
		SelectionStrategy hunt_strategy = null; 
		Vector2D _pos = null; 
		
		if(data.has("mate_strategy")) {
			JSONObject mate_strat = data.getJSONObject("mate_strategy");
			String mate = mate_strat.getString("type");  
			for(Builder<SelectionStrategy> b : selection_strategy_builders) {
				if(b.get_type_tag().equalsIgnoreCase(mate)) {
					mate_strategy = b.create_instance(data);
				}
			}
		}
		else {
			mate_strategy = new SelectFirst();  
		}
		
		if(data.has("hunt_strategy")) {
			JSONObject hunt_strat = data.getJSONObject("hunt_strategy");
			String hunt = hunt_strat.getString("type"); 
			for(Builder<SelectionStrategy> b : selection_strategy_builders) {
				if(b.get_type_tag().equalsIgnoreCase(hunt)) { 
					hunt_strategy = b.create_instance(data); 
				}
			}
		}
		else {
			hunt_strategy = new SelectFirst(); 
		}
		
		if(!data.has("pos")) {
			_pos = null;  
		}
		else {
			JSONObject pos = data.getJSONObject("pos");  //es un object
			
			JSONArray x_range = pos.getJSONArray("x_range"); 
			double uno = x_range.getDouble(0);
			double dos = x_range.getDouble(1); 
			double x = Utils._rand.nextDouble(uno, dos);
					
			JSONArray y_range = pos.getJSONArray("y_range"); 
			double uno_aux = y_range.getDouble(0);
			double dos_aux = y_range.getDouble(1); 
			double y = Utils._rand.nextDouble(uno_aux, dos_aux); 
			
			_pos = new Vector2D(x, y); //ya tengo la nueva pos
		}
		
		Wolf wolf = new Wolf(mate_strategy, hunt_strategy, _pos);
		
		return wolf; 
	}

}
