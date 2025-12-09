package simulator.control;

import java.io.OutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import simulator.misc.Vector2D;
import simulator.model.AnimalInfo;
import simulator.model.EcoSysObserver;
import simulator.model.MapInfo;
import simulator.model.Simulator;
import simulator.view.SimpleObjectViewer;
import simulator.view.SimpleObjectViewer.ObjInfo;


public class Controller {
	public Simulator _sim; 
	public Controller(Simulator sim) { 
		this._sim = sim;  
	}
	
	public void load_data(JSONObject data) {
		//data.opt devuelve un valor asociado a lo q pasas por parametro
		//en este caso devolvera lo q tenga asociado a regions,
	   // es decir, el JSONArray de regions
		set_regions(data); 								
		JSONArray AnimalsArray = (JSONArray) data.opt("animals");
		Iterator<Object> animal_it = AnimalsArray.iterator();
		while(animal_it.hasNext()) {
			JSONObject animal = (JSONObject) animal_it.next(); 
			int N = animal.getInt("amount");
			JSONObject O = animal.getJSONObject("spec");
			for(int i = 0; i < N; i++) {
				_sim.add_animal(O); 
			}
		}
		
	}
	
	public void run(double t, double dt, boolean sv, OutputStream out) {
		//Creo la instancia del SimpleObjectViewer
		SimpleObjectViewer view = null;
		if (sv) {
			MapInfo m = _sim.get_map_info();
			view = new SimpleObjectViewer("[ECOSYSTEM]",
										   m.get_width(), m.get_height(),
										   m.get_cols(), m.get_rows());
		    view.update(to_animals_info(_sim.get_animals()), _sim.get_time(), dt);
		}
		//------------------
		PrintStream p = new PrintStream(out);
		JSONObject json = new JSONObject();
		//p.println(json); //se puede pasar un JSON?? Segun el enunciado si
		json.put("in", _sim.as_JSON()); 
		while(_sim.get_time()<t) {
			_sim.advance(dt); 
			if (sv) view.update(to_animals_info(_sim.get_animals()), _sim.get_time(), dt); 
		}
		json.put("out", _sim.as_JSON());   
		
	
		p.println(json); //se puede pasar un JSON?? 
		if (sv) view.close();
	}
	
	//metodo necesario para dibujar a los animales al convertir una lista de tipo List<? extends Animalnfo> a
	//List<ObjInfo> usando el siguiente m�todo:
	private List<ObjInfo> to_animals_info(List<? extends AnimalInfo> animals) {
		List<ObjInfo> ol = new ArrayList<>(animals.size());
		for (AnimalInfo a : animals) 
			ol.add(new ObjInfo(a.get_genetic_code(),
					(int) a.get_position().getX(),
					(int) a.get_position().getY(),(int)Math.round(a.get_age())+2));
		return ol;
		}
	
	//METODOS V2----------------------------------------------------------
	
	public void reset(int cols, int rows, int width, int height) {
		_sim.reset(cols, rows, width, height); 
	}
	
	public void set_regions(JSONObject rs) { 
		JSONArray regionArray = (JSONArray) rs.opt("regions"); 
		if(regionArray != null){
			//Al comienzo, el iterador esta apuntando a la posicion inmediatemente anterior a la primera
			//pos del array
			Iterator<Object> reg_iterator = regionArray.iterator(); 
			while(reg_iterator.hasNext()) { //mientras siga habiendo elementos que recorrer
				//1) Al hacer el next ya avanzamos a la primera posicion del array
				//2) Hacemos un casting JSONObject ya que el iterador nos devuelve un Objecto
				//al cual con el casting lo convertimos directamente en un JSONObject
				JSONObject region = (JSONObject) reg_iterator.next();
				//ahora extraemos del JSONObject la informacion que necesitamos
				JSONArray row = region.getJSONArray("row");
				int rf = row.getInt(0); 
				int rt = row.getInt(1); 
				
				JSONArray col = region.getJSONArray("col");
				int cf = col.getInt(0); 
				int ct = col.getInt(1);  
				
				JSONObject O = region.getJSONObject("spec"); 
				
				//System.out.println(rf);
				//System.out.println(rt); 
				
				//System.out.println(cf);
				//System.out.println(ct); 
				
				for(int r = rf; r <= rt; r++) {
					for(int c = cf; c <= ct; c++) {
						_sim.set_region(r, c, O); 
					}
				}
			}
		}
	}
	
	public void advance(double dt) {
		_sim.advance(dt); 
	}
	
	public void addObserver(EcoSysObserver o) {
		_sim.addObserver(o); 
	}
	
	public void removeObserver(EcoSysObserver o) {
		_sim.removeObserver(o); 
	}
	
	
	

}
