package simulator.model;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

import org.json.JSONArray;
import org.json.JSONObject;

import simulator.misc.Utils;
import simulator.misc.Vector2D;

public class RegionManager implements AnimalMapView{

	private int map_widht; //anchura mapa
	private int map_height; //altura mapa
	private int col;
	private int row;
	private int region_widht;
	private int region_height; 
	
	private Region[][] _regions;  
	private Map<Animal, Region> _animal_region;  //asigna a cada animal su region actual
	
	//Constructora
	public RegionManager(int cols, int rows, int widht, int height) {
		this.col = cols;
		this.row = rows;
		this.map_widht = widht;
		this.map_height = height;  
		this.region_widht = map_widht / cols; //preguntar. ESTA BIEN
		this.region_height = map_height / rows; //preguntar. ESTA BIEN
		this._regions = new Region[row][col]; //
		this._animal_region = new HashMap<Animal, Region>();  //LA clase Map es una interfaz,
															  //hay q llamar a la clase HashMap
		initialice_regions(); //para inicializar la matriz de regiones
	}
	
	private void initialice_regions() {
		for(int i = 0; i < row; i++) { 
			for(int j = 0; j < col; j++) {
				_regions[i][j] = new DefaultRegion(); 
			}
		}
	}
	@Override
	public int get_cols() {
		// TODO Auto-generated method stub
		return col;  
	}

	@Override
	public int get_rows() {
		// TODO Auto-generated method stub
		return row;
	}

	@Override
	public int get_width() {
		// TODO Auto-generated method stub
		return map_widht;
	}

	@Override
	public int get_height() {
		// TODO Auto-generated method stub
		return map_height; 
	}

	@Override
	public int get_region_width() {
		// TODO Auto-generated method stub
		return region_widht; 
	}

	@Override
	public int get_region_height() {
		// TODO Auto-generated method stub
		return region_height; 
	}

	
	void set_region(int row, int col, Region r) { 
		List<Animal> l = _regions[row][col].getAnimals();
		for(Animal an : l) {
			r.add_animal(an);  
			
			Region current_region = _animal_region.get(an);
			unregister_animal(an); //vaciamos la region en la que se encuentra originalmente
								   //y sus respectivos maps
			current_region = r;
			_animal_region.put(an, current_region); //modificamos el map con la nueva region
		}
	
		
		_regions[row][col] = r; //introducimos en la matriz la nueva region ya modificada
								//con los animales nuevos
		System.out.println(row);
		System.out.println(col);
		System.out.println(_regions[row][col].to_String());  
		System.out.println("-------");
		
	}
	
	void register_animal(Animal a) {
		Vector2D _pos = a.get_position();
		if(a.get_position() == null) {
			double x1 = Utils._rand.nextDouble(this.get_width()-1); //800
			double y1 = Utils._rand.nextDouble(this.get_height()-1);  
			Vector2D v = new Vector2D(x1, y1);  
			_pos = v;
		}
		double x = _pos.getX() / region_widht; //col
		double y = _pos.getY()/ region_height; //row 
		_regions[(int) y][(int) x].add_animal(a);
		_animal_region.put(a, _regions[(int) y][(int) x]); //se a�ade al MAP
		
		//llamar al metodo init
		a.init(this); 
		
	}
	
	void unregister_animal(Animal a) {
		
		Region current_region = _animal_region.get(a);
		current_region.remove_animal(a);
		_animal_region.remove(a); 
	}
	
	void update_animal_region(Animal a) {
		Vector2D _pos = a.get_position(); 
		double x = _pos.getX() / region_widht; 
		double y = _pos.getY() / region_height;  
		
		Region current_region = _animal_region.get(a); 
		Region new_region = _regions[(int) y][(int) x]; //region a la q tiene q pertenecer
		if(new_region != current_region) { //ns si es asi
			//primer valor es la region donde deberia ir por posicion y el segundo es
			//la posicion donde realmente esta, sabido del map donde se guarda el animal y su region
			//_regions[(int) x][(int) y].add_animal(a); 
			unregister_animal(a); //lo borra de donde esta
			register_animal(a); //lo mete donde debe
			//como lo elimino de la region en la que esta dentro de la matriz
			//_animal_region.remove(a);
			//_animal_region.put(a, new_region); 
		}
	}
	
	//llama a get_food de la region a la que pertenece el animal
	public double get_food(Animal a, double dt) {
		Vector2D _pos = a.get_position();
		double x = _pos.getX() /region_widht; 
		double y = _pos.getY() / region_height;  
		return _regions[(int) y][(int) x].get_food(a, dt); 
	}
	
	void update_all_regions(double dt) { 
		for(int i = 0; i < row; i++) { 
			for(int j = 0; j < col; j++) {
				_regions[i][j].update(dt); 
			}
		}
	}
	
	@Override
	public List<Animal> get_animals_in_range(Animal e, Predicate<Animal> filter) {
		Vector2D pos = e.get_position();
		double sight_range = e.get_sight_range(); 
		List<Animal> animalsInRange = new LinkedList<>();
		
		//se coge el maximo para q en caso de salirte del borde cogas el 0 como limite
		int minX = (int) Math.max(0, pos.getX()-sight_range);
		int maxX = (int) Math.min(map_widht, pos.getX()+sight_range); 
		
		int minY = (int) Math.max(0, pos.getY()-sight_range); 
		int maxY = (int) Math.min(map_height, pos.getY()+sight_range); 
		
		for(int i = minY; i < maxY; i++) {
			for(int j = minX; j < maxX; j++) {
				Region reg = _regions[i / region_height][j / region_widht];
				List<Animal> animalsInRegion = reg.getAnimals(); 
				for(Animal an : animalsInRegion) {
					if(filter.test(an)) {
						animalsInRange.add(an); 
					} 
				}
			}
		}
		
		
		return animalsInRange;
	}
	
	public JSONObject as_JSON() { 
		JSONObject json = new JSONObject();
		//JSONArray arrayjson = new JSONArray();
		
		
		JSONArray arrayO = new JSONArray(); 
		
		for(int i = 0; i < row; i++) {
			for(int j = 0; j < col; j++) {
				JSONObject o = new JSONObject();
				o.put("row", i);
				o.put("col", j); 
				o.put("data", _regions[i][j].as_JSON());    
				
				arrayO.put(o); 
			}
		}
		json.put("regiones", arrayO);  
		
		return json; 
	}

	@Override
	public Iterator<RegionData> iterator() {
		return new RegionManagerIterator(row, col);
	}
	
	
	//CLASE INTERNA ----------------------------------------------
	class RegionManagerIterator implements Iterator<RegionData>{
		private int row;
		private int col;
		private int max_rows;
		private int max_cols; 
		
		public RegionManagerIterator(int max_rows, int max_cols) {
			this.max_rows = max_rows;
			this.max_cols = max_cols;
			this.row = 0;
			this.col = 0; 
		}

		@Override
		public boolean hasNext() { //Devuelve TRUE si aun se puede recorre la matriz
			boolean next = false;
			if(row < max_rows && col < max_cols) {
				next = true;
			}
			return next;
		}

		@Override
		public RegionData next() { 
			//devuelve una instancia de RegionData 
			//que contenga la fila, columna y referencia a la región actual.
			Region region = _regions[row][col];
			//Le paso la region o RegionInfo???
	        RegionData regionData = new RegionData(row, col, region);
	        col++;
	        if (col == max_cols) {
	            col = 0;
	            row++;
	        }
	        return regionData;
		}
		
	}

}
