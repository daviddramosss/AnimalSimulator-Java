package simulator.factories;

import java.util.List;

import org.json.JSONObject;

import simulator.model.Animal;
import simulator.model.SelectClosest;
import simulator.model.SelectionStrategy;

public class SelectClosestBuilder extends Builder<SelectionStrategy>{

	private static String _type_tag = "closest"; 
	private static String _desc = "Desc";
	
	public SelectClosestBuilder() {
		super(_type_tag, _desc);
	}
	@Override
	protected SelectionStrategy create_instance(JSONObject data) {
		SelectClosest closest = new SelectClosest();
		return closest; 
	}

	

}
