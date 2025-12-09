package simulator.view;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.BoxLayout;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;

import org.json.JSONArray;
import org.json.JSONObject;

import simulator.control.Controller;
import simulator.launcher.Main;
import simulator.model.AnimalInfo;
import simulator.model.EcoSysObserver;
import simulator.model.MapInfo;
import simulator.model.RegionInfo;

public class ChangeRegionsDialog extends JDialog implements EcoSysObserver{
	
	private DefaultComboBoxModel<String> _regionsModel;
	private DefaultComboBoxModel<String> _fromRowModel;
	private DefaultComboBoxModel<String> _toRowModel;
	private DefaultComboBoxModel<String> _fromColModel;
	private DefaultComboBoxModel<String> _toColModel;
	
	private JComboBox<String> _regions; 
	private JComboBox<String> _fromRow; 
	private JComboBox<String> _toRow;
	private JComboBox<String> _fromCol;
	private JComboBox<String> _toCol;
	
	
	
	private DefaultTableModel _dataTableModel;
	private Controller _ctrl;
	private List<JSONObject> _regionsInfo;
	
	private String[] _headers = { "Key", "Value", "Description" };
	
	// en caso de ser necesario, añadir los atributos aquí…
	private int rows; 
	private int cols; 
	int _regionsIndex = 0; 
	
	private int selected_from_row;
	private int selected_to_row;
	private int selected_from_col;
	private int selected_to_col;

	private JSONObject region_data; 
	private String region_type; 

	
	ChangeRegionsDialog(Controller ctrl) {
		super((Frame)null, true);
		
		ctrl.addObserver(this); 
		_ctrl = ctrl;
		initGUI();
		
		
	}
	
	
	private void initGUI() {
		setTitle("Change Regions");
		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
		setContentPane(mainPanel); //establecemos mainPanel como el panel principal del JFrame
		
		// crea varios paneles para organizar los componentes visuales en el
		// dialogo, y añadelos al mainpanel. P.ej., uno para el texto de ayuda,
		// uno para la tabla, uno para los combobox, y uno para los botones.
		
		//TEXTO DER AYUDA
		String _help = "Select a region type, the rows/cols interval, and provide values for the parameters "
				+ "in the Value column (default values are used for parameters with no value)"; 
		JLabel ayuda = new JLabel("<html><p>"+_help+"<html><p>"); //html y p para que se cuadre el texto bien
		ayuda.setAlignmentX(CENTER_ALIGNMENT);  
		mainPanel.add(ayuda, BorderLayout.PAGE_START);  
		
		
		//TABLA
		JPanel tabla = new JPanel();
		tabla.setLayout(new BoxLayout(tabla, BoxLayout.Y_AXIS)); //esto lo centra mejor
		tabla.setPreferredSize(new Dimension(300,200)); //Ajustamos el tamaño para que no sea muy grande
		//tabla.setAlignmentX(CENTER_ALIGNMENT); 
		mainPanel.add(tabla, BorderLayout.CENTER); 

		
		
		//COMBOBOX PANEL
		JPanel comboBoxPanel = new JPanel();
		mainPanel.add(comboBoxPanel); 
		
		//BUTTONS PANEL
		JPanel buttonsPanel = new JPanel();
		buttonsPanel.setAlignmentX(CENTER_ALIGNMENT); 
		mainPanel.add(buttonsPanel, BorderLayout.PAGE_END); 	
		
		
		
		// _regionsInfo se usará para establecer la información en la tabla
		//_regionsInfo = Main._regions_factory.get_info(); 
		//diria de hacer esto
		_regionsInfo = Main.region_builders_factory.get_info(); 
		
		// _dataTableModel es un modelo de tabla que incluye todos los parámetros de
		// la region
		_dataTableModel = new DefaultTableModel() { 
			@Override
			public boolean isCellEditable(int row, int column) {
			// hacer editable solo la columna 1
				if(column == 1) {
					return true;
				}
				else {
					return false;
				}
			}
		}; 
		
		_dataTableModel.setColumnIdentifiers(_headers);
		
		// TODO crear un JTable que use _dataTableModel, y añadirlo al diálogo
		JTable dataTable = new JTable(_dataTableModel) {
			private static final long serialVersionUID = 1L;

			// we override prepareRenderer to resize columns to fit to content
			@Override
			public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
				Component component = super.prepareRenderer(renderer, row, column);
				int rendererWidth = component.getPreferredSize().width;
				TableColumn tableColumn = getColumnModel().getColumn(column);
				tableColumn.setPreferredWidth(
						Math.max(rendererWidth + getIntercellSpacing().width, tableColumn.getPreferredWidth()));
				return component;
			
			}
		};
		dataTable.setPreferredSize(new Dimension(300, 200)); //mismo tamaño que el JPanel
		JScrollPane scroll = new JScrollPane(dataTable);
		tabla.add(scroll);  
		
		//---------------------------------------------------------------------------------------
		
		// _regionsModel es un modelo de combobox que incluye los tipos de regiones
		_regionsModel = new DefaultComboBoxModel<>();
		
		
		// TODO añadir la descripción de todas las regiones a _regionsModel, para eso
		// usa la clave “desc” o “type” de los JSONObject en _regionsInfo,
		// ya que estos nos dan información sobre lo que puede crear la factoría.
		
		for(int i = 0; i < _regionsInfo.size(); i++) {
			JSONObject region = _regionsInfo.get(i);
			_regionsModel.addElement(region.getString("type")); 
			
			
		}
		
		// TODO crear un combobox que use _regionsModel y añadirlo al diálogo.
		this._regions = new JComboBox<>(_regionsModel);
		JLabel label_regions = new JLabel("Region type: "); 
		comboBoxPanel.add(label_regions); 
		comboBoxPanel.add(_regions); 
		
		//DE ESTA FORMA PUEDO MODIFICAR LA TABLA EN FUNCION DE LO QUE HAYA EN EL COMBOBOX
		_regions.addActionListener((e) -> {
			_regionsIndex = _regions.getSelectedIndex(); 
			update_tableModel(); 
		});
		
		
	
		//LO DEJO AQUI PARA SEPARAR LOS BOTONES
		JSeparator s = new JSeparator(JSeparator.VERTICAL);
		s.setPreferredSize(new Dimension(10, 20));
		comboBoxPanel.add(s); 
		
		
		// TODO crear 4 modelos de combobox para _fromRowModel, _toRowModel,
		// _fromColModel y _toColModel.
		
		_fromRowModel = new DefaultComboBoxModel<>(); 
		_toRowModel = new DefaultComboBoxModel<>(); 
		for(int i = 0; i < rows;i++) {   
			_fromRowModel.addElement(Integer.toString(i)); 
			_toRowModel.addElement(Integer.toString(i)); 
		}
	
		JLabel label_fromRow = new JLabel("Row from/to: ");
		this._fromRow = new JComboBox<>(_fromRowModel);
		comboBoxPanel.add(label_fromRow);
		comboBoxPanel.add(_fromRow); 
		
		
		this._toRow = new JComboBox<>(_toRowModel); 
		comboBoxPanel.add(_toRow); 
		
		comboBoxPanel.add(s); 
		
		
		_fromColModel = new DefaultComboBoxModel<>();
		_toColModel = new DefaultComboBoxModel<>(); 
		for(int i = 0; i < cols; i++) {
			_fromColModel.addElement(Integer.toString(i)); 
			_toColModel.addElement(Integer.toString(i));
		}
		this._fromCol = new JComboBox<>(_fromColModel); 
		this._toCol = new JComboBox<>(_toColModel); 
		JLabel label_fromCol = new JLabel("Column from/to: ");
		comboBoxPanel.add(label_fromCol);
		comboBoxPanel.add(_fromCol);
		comboBoxPanel.add(_toCol); 
		
		coordenadas_comboBox(); //Listeners de los ComboBoxes
		
		// TODO crear 4 combobox que usen estos modelos y añadirlos al diálogo.
		//HECHO ARRIBA
		
		// TODO crear los botones OK y Cancel y añadirlos al diálogo
		JButton cancelButton = new JButton("Cancel");
		//añadir accion
		cancelButton.addActionListener( new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				//_status = 0; 
				setVisible(false);
			}
			
		});
		
		JButton okButton = new JButton("Ok");
		
		okButton.addActionListener( new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				
				String jsonString = "{\"regions\" : [{\"row\" : [" + selected_from_row + "," + selected_to_row 
						+ "], \"col\" : [" + selected_from_col +"," +  selected_to_col + "], "
								+ " \"spec\" : { \"type\" : " + region_type + ", \"data\" : " + get_JSON() + "} }]}"; 
			
				//System.out.println(jsonString); 
				JSONObject j = new JSONObject (jsonString);
				
				 
				try {
					_ctrl.set_regions(j); 
					setVisible(false); 
				} catch(Exception e1) { 
					ViewUtils.showErrorMsg("error al cargar");
				}
				
				
			}
			
		});
		
		buttonsPanel.add(cancelButton); 
		buttonsPanel.add(okButton);
		
		
		setPreferredSize(new Dimension(700, 400)); // puedes usar otro tamaño
		pack();
		setResizable(false);
		setVisible(false);

	}
	
	

	private void update_tableModel() {
		JSONObject info = _regionsInfo.get(_regionsIndex); 
		JSONObject data = info.getJSONObject("data");
		
		region_type = info.getString("type"); 
		//en data.keySet() estan todas las claves de data
		_dataTableModel.setNumRows(data.keySet().size()); 
		
		int row = 0;
		for(String k : data.keySet()) {
			_dataTableModel.setValueAt(k, row, 0);
			_dataTableModel.setValueAt(" ", row, 1);
			_dataTableModel.setValueAt(data.getString(k), row, 2); 
			row++; 
		}
	}
	
	//hacemos el JSON de region_data pero de la forma que indica el jdialog.ex2
	private String get_JSON() {
		
		StringBuilder s = new StringBuilder();
		s.append('{');
		String key;
		String value; 
		String def_factor;
		String def_food; 
		for(int i = 0; i < _dataTableModel.getRowCount(); i++) {
			key = _dataTableModel.getValueAt(i, 0).toString();
			
			value = _dataTableModel.getValueAt(i, 1).toString();
			if(!value.trim().isEmpty()) { 
				s.append('"');
				s.append(key);
				s.append('"');
				s.append(':');
				s.append(value);
				s.append(',');
			}
			else {
			
				
				 if (key.equals("factor")) { 
		                s.append('"');
		                s.append(key);
		                s.append('"');
		                s.append(':');
		                s.append("2.0");
		                s.append(',');
		            } else if (key.equals("food")) {
		                s.append('"');
		                s.append(key);
		                s.append('"');
		                s.append(':');
		                s.append("100.0");
		                s.append(',');
		            }
		            
		        }
			}
		
		if (s.length() > 1)
			s.deleteCharAt(s.length() - 1);
		s.append('}');
		
		return s.toString(); 
	}
	
	
	
	private void get_info_reg_data() { 
		region_data = new JSONObject();
		int row = _dataTableModel.getRowCount();
		String key; 
		String value; //es un numero realmente pero vamos a recogerlo como string
		//la col se que es 3 no necesito sacarla
		//relleno el JSONObject region_data 
		for(int i = 0; i < _dataTableModel.getRowCount(); i++) { 
			key = _dataTableModel.getValueAt(i, 0).toString(); 
			value = _dataTableModel.getValueAt(i, 1).toString();
			double v = Double.parseDouble(value);
			region_data.put(key, v);  
		}
		//Acaba teniendo factor -> 1.2(ej) y food -> 2.2(ej)
	}
	
	private void coordenadas_comboBox() {
		_fromRow.addActionListener( (e) -> {
			selected_from_row = _fromRow.getSelectedIndex(); 
		});
		
		_toRow.addActionListener( (e) -> {
			selected_to_row = _toRow.getSelectedIndex();
		});
		
		_fromCol.addActionListener((e)->{
			selected_from_col = _fromCol.getSelectedIndex();
		});
		
		_toCol.addActionListener( (e) -> {
			selected_to_col = _toCol.getSelectedIndex(); 
		});
		
	}
	
	

	
	public void open(Frame parent) {
		setLocation(//
				parent.getLocation().x + parent.getWidth() / 2 - getWidth() / 2, //
				parent.getLocation().y + parent.getHeight() / 2 - getHeight() / 2);
		pack();
		setVisible(true);
	}

	//RESTO DE METODOS
	
	private void setDimension(MapInfo map) {
		rows = map.get_rows();
		cols = map.get_cols(); 
		
	}

	@Override
	public void onRegister(double time, MapInfo map, List<AnimalInfo> animals) {
		// TODO Auto-generated method stub
		//this.map = map; 
		//rows = map.get_rows();
		
		
		setDimension(map); 
		
	}

	@Override
	public void onReset(double time, MapInfo map, List<AnimalInfo> animals) {
		//removeAllElements(); 
		setDimension(map); 
	}

	@Override
	public void onAnimalAdded(double time, MapInfo map, List<AnimalInfo> animals, AnimalInfo a) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onRegionSet(int row, int col, MapInfo map, RegionInfo r) {
		// TODO Auto-generated method stub
		setDimension(map); 
	}

	@Override
	public void onAvanced(double time, MapInfo map, List<AnimalInfo> animals, double dt) {
		// TODO Auto-generated method stub
		
	}

}
