package simulator.factories;

import org.json.JSONObject;

import simulator.model.SelectFirst;
import simulator.model.SelectionStrategy;

public class SelectFirstBuilder extends Builder<SelectionStrategy> {

	private static String _type_tag = "first";
	private static String _desc = "Desc"; //esta es una descripcion de la clase. No es importante ahora. Dejar vacio
	//Aqui data esta vacio
	
	public SelectFirstBuilder() { 
		super(_type_tag, _desc); 
	}
	
	@Override
	protected SelectionStrategy create_instance(JSONObject data) {
		// TODO Auto-generated method stub
		SelectFirst strategy_first = new SelectFirst();  
		return strategy_first;  
	}

}
