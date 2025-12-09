package simulator.view;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.table.AbstractTableModel;

import simulator.control.Controller;
import simulator.model.Animal;
import simulator.model.Animal.State;
import simulator.model.AnimalInfo;
import simulator.model.EcoSysObserver;
import simulator.model.MapInfo;
import simulator.model.RegionInfo;

public class SpeciesTableModel extends AbstractTableModel implements EcoSysObserver{
	
	//DEFINIR ATRIBUTOS
	//String[] _header = { "Species", "NORMAL", "MATE", "HUNGER", "DANGER", "DEAD" };
	List<AnimalInfo> animals;
	Animal.State[] estados = State.values(); //Array de todos los posibles states 
	//String[] especies = { "Wolf" , "Sheep" };
	
	List<String> species;
	
	
	SpeciesTableModel(Controller ctrl) { 
		//inicializar estructuras de datos correspondientes
		animals = new ArrayList<>();
		species = new ArrayList<>();
		//registrar this como observador
		ctrl.addObserver(this); 
	}
	
	//METODOS PRIVADOS PARA OBTENER NUMERO DE OVEJAS Y DE LOBOS EN UN ESTADO DETERMINADO
	int countAnimalsInState(String genetic_code, State estado) { 
		int contador = 0;
		for(AnimalInfo a : animals) {
			if(a.get_genetic_code() == genetic_code) {
				if(a.get_state() == estado) {  
					contador++; 
				}
			}
		}
		
		return contador; 
	}
	
	//METODO PARA SABER CUANTOS GENETIC CODES HAY
	int num_especies() {
		int contador = 0; 
		Set<String> especies = new HashSet<>();   
		for(AnimalInfo a: animals) {
			if(especies.contains(a.get_genetic_code())) {
				contador++;
				especies.add(a.get_genetic_code()); 
			}
		}
		return contador;  
	}
	

	@Override
	public int getRowCount() {
		return species.size(); 
		//return 0;
	}

	@Override
	public int getColumnCount() {
		return estados.length + 1; //+1 para añadir el "Species" de la col 0  
	}
	
	@Override
	public String getColumnName(int column) {  
		if(column == 0) {
			return "Species";
		}
		else {
			
		}
		return estados[column-1].toString(); 
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		
		if(columnIndex == 0) {
			return species.get(rowIndex); 
		}
		else {
			return countAnimalsInState(species.get(rowIndex), estados[columnIndex-1]);   
			
		}
		
		/*
		switch(columnIndex) {
			case 0: //Species
				return species.get(rowIndex);    
			case 1: //NORMAL
				return countAnimalsInState(species.get(rowIndex), states[columnIndex-1]);  
			case 2: //MATE
				return countAnimalsInState(species.get(rowIndex), states[columnIndex-1]);
			case 3: //HUNGER
				return countAnimalsInState(species.get(rowIndex), states[columnIndex-1]);  
			case 4: //DANGER
				return countAnimalsInState(species.get(rowIndex), states[columnIndex-1]);
			case 5: //DEAD
				return countAnimalsInState(species.get(rowIndex), states[columnIndex-1]);  
			default: 
					return null;
					
		}
		*/
		
	}
	
	private void get_species(List<AnimalInfo> animals) {
		for(AnimalInfo a : animals) {
			if(!this.species.contains(a.get_genetic_code())) {
				this.species.add(a.get_genetic_code()); 
			}
			
		}
		
	}
	
	
	

	@Override
	public void onRegister(double time, MapInfo map, List<AnimalInfo> animals) {
		//ns si se hace asi
		 this.animals = animals;
		 get_species(animals);  
		 fireTableStructureChanged();
	}

	@Override
	public void onReset(double time, MapInfo map, List<AnimalInfo> animals) {
		this.animals = animals;
		get_species(animals);
		fireTableStructureChanged();
		
	}

	@Override
	public void onAnimalAdded(double time, MapInfo map, List<AnimalInfo> animals, AnimalInfo a) {
		animals.add(a);
		get_species(animals);
		fireTableStructureChanged();
	}

	@Override
	public void onRegionSet(int row, int col, MapInfo map, RegionInfo r) {
		//AQUI NO HACEMOS NADA
	}

	@Override
	public void onAvanced(double time, MapInfo map, List<AnimalInfo> animals, double dt) {
		this.animals = animals; 
		fireTableStructureChanged();
	}

}
