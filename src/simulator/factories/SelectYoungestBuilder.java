package simulator.factories;

import org.json.JSONObject;

import simulator.model.SelectYoungest;
import simulator.model.SelectionStrategy;

public class SelectYoungestBuilder extends Builder<SelectionStrategy>{

	private static String _type_tag = "youngest"; 
	private static String _desc = "Desc";
	
	public SelectYoungestBuilder() {
		super(_type_tag, _desc);
	}
	@Override
	protected SelectionStrategy create_instance(JSONObject data) {
		// TODO Auto-generated method stub
		SelectYoungest youngest = new SelectYoungest();
		return youngest;  
	}

}
