package simulator.view;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import simulator.control.Controller;
import simulator.model.Animal;
import simulator.model.Animal.Diet;
import simulator.model.AnimalInfo;
import simulator.model.EcoSysObserver;
import simulator.model.MapInfo;
import simulator.model.RegionInfo;

public class RegionsTableModel extends AbstractTableModel implements EcoSysObserver{
	
	//DEFINIR ATRIBUTOS
	Animal.Diet diet[] = Diet.values(); //referencia a la dieta de los animales 
	
	//String _header[] = { "Row", "Col", "Desc" , "CARNIVORE", "HERBIVORE" }; 
	
	MapInfo map; 
	
	int col;
	int row; 
	
	
	RegionsTableModel(Controller ctrl) {
		// TODO inicializar estructuras de datos correspondientes
		
		//registrar this como observador
		ctrl.addObserver(this); 
	}
	
	private int num_animales(List<AnimalInfo> l, Diet diet) {
		int contador = 0;
		for(AnimalInfo a : l) {
			if(a.get_diet() == diet) {
				contador++;
			}
		}
		
		return contador; 
	}
	
	private RegionInfo reg_info(int rowIndex, int columnIndex) {
		Iterator<MapInfo.RegionData> iterator = map.iterator(); 
		
		
		for(int i = 0; i < rowIndex; i++) {
			for(int j = 0; j < col; j++) {
				iterator.next(); 
			}
		}
		
		for(int i = 0; i < columnIndex; i++) {
			iterator.next(); 
		}
		
		
		/*
		for (int i = 0; i < rowIndex; i++) {
	        iterator.next(); 
	    }
	    for (int j = 0; j < columnIndex; j++) {
	        iterator.next(); 
	    }
	    */
		MapInfo.RegionData data = iterator.next();
		RegionInfo region = data.r(); 
	
		return region; 
	}

	@Override
	public int getRowCount() {
		return map.get_rows() * map.get_cols();   
	}

	@Override
	public int getColumnCount() {
		return diet.length + 3; //le sumo row, col y desc
	}
	
	@Override
	public String getColumnName(int column) {
		//return _header[column];
		if(column == 0) {
			return "Row";
		}
		else if(column == 1) {
			return "Col";
		}
		else if(column == 2) {
			return "Desc";
		}
		else {
			return diet[column - 3].toString(); 
		}
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		switch(columnIndex) {
			case 0: //Row
				//Esto funciona porque el índice de fila se calcula como el producto de la fila y columna
				return rowIndex / map.get_cols();  
			case 1: //Col
				//y el operador módulo devuelve el resto de la división, que es el número de columna.
				return rowIndex % map.get_cols(); 
			case 2: //Tipo de Region
			
				RegionInfo region = reg_info(rowIndex / map.get_cols(), rowIndex % map.get_cols());
				
				return region.to_String();  
			default: //CARNIVORE, HERVIBORE U OTRA DIETA NUEVA
				RegionInfo region2 = reg_info(rowIndex / map.get_cols(), rowIndex % map.get_cols()); 
				List<AnimalInfo> l = region2.getAnimalsInfo();
				Diet d = diet[columnIndex -3]; 
				return num_animales(l,d); 
			
						
		}
		
	}

	@Override
	public void onRegister(double time, MapInfo map, List<AnimalInfo> animals) {
		this.map = map; 
		col = map.get_cols();
		row = map.get_rows(); 
		fireTableStructureChanged();
		
	}

	@Override
	public void onReset(double time, MapInfo map, List<AnimalInfo> animals) {
		this.map = map; 
		fireTableDataChanged(); 
		
	}

	@Override
	public void onAnimalAdded(double time, MapInfo map, List<AnimalInfo> animals, AnimalInfo a) {
		this.map = map; 
		fireTableDataChanged(); 
		
	}

	@Override
	public void onRegionSet(int row, int col, MapInfo map, RegionInfo r) {
		this.map = map; 
		fireTableDataChanged(); 
		
	}

	@Override
	public void onAvanced(double time, MapInfo map, List<AnimalInfo> animals, double dt) {
		this.map = map; 
		fireTableDataChanged(); 
		
	}
	
	

}
