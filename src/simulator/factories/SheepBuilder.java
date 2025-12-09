package simulator.factories;

import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import simulator.misc.Utils;
import simulator.misc.Vector2D;
import simulator.model.Animal;
import simulator.model.SelectFirst;
import simulator.model.SelectionStrategy;
import simulator.model.Sheep;

public class SheepBuilder extends Builder<Animal>{

	private static String _type_tag = "sheep";
	private static String _desc = "Desc";
	private static List<Builder<SelectionStrategy>> selection_strategy_builders; 
	
	public SheepBuilder() {
		super(_type_tag, _desc);
		
	}
	
	public SheepBuilder(List<Builder<SelectionStrategy>> selection_strategy_builders) {
		this();
		this.selection_strategy_builders = selection_strategy_builders; 
	}

	@Override
	protected Animal create_instance(JSONObject data) {
		Vector2D _pos; 
		SelectionStrategy Mate_strategy = null; 
		SelectionStrategy Danger_strategy = null;
		
		if(!data.has("mate_strategy")) {
			//si no tiene un mate_strategy establecido en data, por defecto hacemos mate_strategy
			Mate_strategy = new SelectFirst(); 
		}
		else {
			//Creo que devuelve un String con el nombre de la mate strategy, no un JSONObject. Preguntar
			//JSONObject mate = data.getJSONObject("mate_strategy"); 
			//falta
			JSONObject mate = data.getJSONObject("mate_strategy");
			String mate_strategy = mate.getString("type");  
			//recorremos la factoria de selection strategy
			for(Builder<SelectionStrategy> b : selection_strategy_builders) {
				if(b.get_type_tag().equals(mate_strategy)) { //si coinciden los tipos
					Mate_strategy = b.create_instance(data); 
				}
			}
			
		}
		
		if(!data.has("danger_strategy")) {
			//Si no existe usamos Select First
			Danger_strategy = new SelectFirst();
		}
		else {
			//JSONObject danger = data.getJSONObject("danger_strategy");
			JSONObject danger_strat = data.getJSONObject("danger_strategy"); 
			String danger = danger_strat.getString("type"); 
			for(Builder<SelectionStrategy> b : selection_strategy_builders) {
				if(b.get_type_tag().equals(danger)) { //si coinciden los tipos
					Danger_strategy = b.create_instance(data);  
				}
			}
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
		
		
		
		Sheep sheep = new Sheep(Mate_strategy, Danger_strategy, _pos);  
				
		return sheep; 
	}

}
