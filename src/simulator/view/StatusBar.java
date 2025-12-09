package simulator.view;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;

import simulator.control.Controller;
import simulator.model.AnimalInfo;
import simulator.model.EcoSysObserver;
import simulator.model.MapInfo;
import simulator.model.RegionInfo;

public class StatusBar extends JPanel implements EcoSysObserver{
	
	// TODO Añadir los atributos necesarios.
	private JLabel time; 
	private JLabel num_animales;
	private JLabel dimensiones;
	
	int widht;
	int height;
	int rows;
	int cols; 
	
	StatusBar(Controller ctrl) {
		initGUI();
		// TODO registrar this como observador
		ctrl.addObserver(this); 
	}
	
	private void initGUI() {
		this.setLayout(new FlowLayout(FlowLayout.LEFT));
		this.setBorder(BorderFactory.createBevelBorder(1));
		
		// TODO Crear varios JLabel para el tiempo, el número de animales, y la
		// dimensión y añadirlos al panel. Puedes utilizar el siguiente código
		// para añadir un separador vertical:
		time = new JLabel(); 
		time.setPreferredSize(new Dimension(100,20)); //ns si ponerlo
		
		num_animales = new JLabel();
		num_animales.setPreferredSize(new Dimension(100,20)); 
		
		dimensiones = new JLabel();
		dimensiones.setPreferredSize(new Dimension(100,20)); 
		
		
		JSeparator s = new JSeparator(JSeparator.VERTICAL);
		s.setPreferredSize(new Dimension(10, 20));
		
		
		this.add(new JLabel("Time: "));
		this.add(time); 
		this.add(s);
		
		this.add(new JLabel("Total Animals: "));
		this.add(num_animales);
		this.add(s);
		
		this.add(new JLabel("Dimension: "));
		this.add(dimensiones);
		
	}
	
	//RESTO DE METODOS
	private void updateTime(double c_time) {
		time.setText("" + c_time); 
	}
	
	private void updateNumAnimals(int n_animals) {
		num_animales.setText(""+n_animals); 
	}
	
	private void updateDimensiones(int widht, int height, int rows, int cols) {
		dimensiones.setText(""+widht + "x" +height + "  "+cols+"x"+rows);
	}
	
	private void setDimension(MapInfo map) {
		widht = map.get_width();
		height = map.get_height();
		rows = map.get_rows();
		cols = map.get_cols();
		updateDimensiones(widht, height, rows, cols);
	}

	@Override
	public void onRegister(double time, MapInfo map, List<AnimalInfo> animals) {
		updateTime(time); //actualizamos el tiempo actual
		int n = animals.size();
		updateNumAnimals(n); //actualizamos los animales
		setDimension(map); //actualizamos las dimensiones
		
	}

	@Override
	public void onReset(double time, MapInfo map, List<AnimalInfo> animals) {
		updateTime(time); 
		int n = animals.size();
		updateNumAnimals(n); //actualizamos los animales
		setDimension(map); //actualizamos las dimensiones
		
	}

	@Override
	public void onAnimalAdded(double time, MapInfo map, List<AnimalInfo> animals, AnimalInfo a) {
		// TODO Auto-generated method stub
		int n = animals.size();
		updateNumAnimals(n); //actualizamos los animales
	}

	@Override
	public void onRegionSet(int row, int col, MapInfo map, RegionInfo r) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onAvanced(double time, MapInfo map, List<AnimalInfo> animals, double dt) {
		// TODO Auto-generated method stub
		updateTime(time); 
		int n = animals.size();
		updateNumAnimals(n); //actualizamos los animales
	}
	
	
	
	
	

}
