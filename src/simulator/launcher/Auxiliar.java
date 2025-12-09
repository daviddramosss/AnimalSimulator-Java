package simulator.launcher;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.json.JSONObject;

import simulator.misc.Utils;
import simulator.misc.Vector2D;
import simulator.model.Animal;
import simulator.model.AnimalInfo;
import simulator.model.SelectionStrategy;
import simulator.model.Sheep;
import simulator.model.Wolf;
import simulator.view.SimpleObjectViewer;
import simulator.view.SimpleObjectViewer.ObjInfo;

public class Auxiliar {
	
	
	public static void main(String[] args) {
		String jsonString = "{\"name\":\"John\", \"age\":30, \"city\":\"New York\"}";
        JSONObject jsonObject = new JSONObject(jsonString);
        System.out.println(jsonObject.toString());
	}
}
