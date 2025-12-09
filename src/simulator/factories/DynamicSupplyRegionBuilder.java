package simulator.factories;

import org.json.JSONObject;

import simulator.model.DynamicSupplyRegion;
import simulator.model.Region;

public class DynamicSupplyRegionBuilder extends Builder<Region>{

	private static String _type_tag = "dynamic";
	private static String _desc = "Dynamic food supply";
	
	public DynamicSupplyRegionBuilder() {
		super(_type_tag, _desc);
	}

	@Override
	protected Region create_instance(JSONObject data) { 
		double factor;
		
		if(!data.has("factor")) {
			factor = 2.0;
		}
		else {
			factor = data.getDouble("factor");
		}
		double food;
		if(!data.has("food")) {
			food = 1000.0;
		}
		else {
			food = data.getDouble("food"); 
		}
		
		DynamicSupplyRegion dynamicregion = new DynamicSupplyRegion(factor, food);  
		return dynamicregion;    
	}
	
	@Override
	protected void fill_in_data(JSONObject o) {
		String des_factor = "food increase factor (optional, default 2.0)";
		String des_food = "initial amount of food (optional, default 100.0)";
		
		o.put("factor", des_factor);
		o.put("food", des_food); 
		
	}

}
