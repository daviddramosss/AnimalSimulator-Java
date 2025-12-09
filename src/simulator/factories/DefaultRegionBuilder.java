package simulator.factories;

import org.json.JSONObject;

import simulator.model.DefaultRegion;
import simulator.model.Region;

public class DefaultRegionBuilder extends Builder<Region>{

	private static String _type_tag = "default";
	private static String _desc = "Infinite food supply";
	
	public DefaultRegionBuilder() {
		super(_type_tag, _desc);
	}

	@Override
	protected Region create_instance(JSONObject data) {
		DefaultRegion defaultregion = new DefaultRegion();  
		return defaultregion; 
	}
	
	@Override
	protected void fill_in_data(JSONObject o) {
		//NO HACE FALTA MODIFICARLO YA QUE DATA ESTA VACIO IGUALMENTE
	}

}
