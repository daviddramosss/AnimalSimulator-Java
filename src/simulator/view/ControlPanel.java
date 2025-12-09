package simulator.view;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;

import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingUtilities;

import org.json.JSONException;
import org.json.JSONObject;

import simulator.control.Controller;
import simulator.misc.Utils;


public class ControlPanel extends JPanel{
	private Controller _ctrl;
	private ChangeRegionsDialog _changeRegionsDialog;
	
	private JToolBar _toolaBar;
	private JFileChooser _fc;
	
	private boolean _stopped = true; // utilizado en los botones de run/stop
	private JButton _quitButton; 
	private JButton _openButton; 
	private JButton _viewerButton; 
	private JButton _regions; 
	private JButton _runButton; 
	private JButton _stopButton; 
	
	private JTextField _deltaTimeTextField;
	private JSpinner _stepsSpinner; 
	
	
	
	ControlPanel(Controller ctrl) {
		_ctrl = ctrl;
		initGUI();
	}
	
	private void initGUI() {
		setLayout(new BorderLayout());
		_toolaBar = new JToolBar();
		add(_toolaBar, BorderLayout.PAGE_START);
		
		// TODO crear los diferentes botones/atributos y añadirlos a _toolaBar.
		// Todos ellos han de tener su correspondiente tooltip. Puedes utilizar
		// _toolaBar.addSeparator() para añadir la línea de separación vertical
		// entre las componentes que lo necesiten.
		
		//OPEN BOTTON
		_openButton = new JButton(); 
		_openButton.setToolTipText("Open File");
		_openButton.setIcon(new ImageIcon("resources/icons/open.png")); 
		_openButton.addActionListener( (e) -> {
			//1) Abrir el selector de ficheros
			if(_fc.showOpenDialog(ViewUtils.getWindow(this)) == JFileChooser.APPROVE_OPTION) {
				//2) cargar fichero como JSONObject
				JSONObject data;
				int row;
				int col;
				int widht;
				int height;
				try {
					//ns si se lee asi
					data = new JSONObject(new String(Files.readAllBytes(_fc.getSelectedFile().toPath())));
					
					row = data.getInt("rows");
					col = data.getInt("cols");
					widht = data.getInt("width");
					height = data.getInt("height"); 
					
					_ctrl.reset(col, row, widht, height); 
					_ctrl.load_data(data); 
	
				}catch(FileNotFoundException e1) {
					//throw new IllegalArgumentException ("Error al cargar los archivos.");
					ViewUtils.showErrorMsg("Error al cargar los archivos"); 
					//EXCEPCIONES DE LEER EL JSONOBJECT
				} catch (JSONException e1) {
					e1.printStackTrace();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
			
		} );
		
		_toolaBar.add(_openButton);
		_toolaBar.addSeparator(); 
		
		//Viewer Button
		_viewerButton = new JButton();
		_viewerButton.setToolTipText("Viewer");
		_viewerButton.setIcon(new ImageIcon("resources/icons/viewer.png")); 
		_viewerButton.addActionListener( (e) -> {
			
			new MapWindow(ViewUtils.getWindow(this), _ctrl);    
		
			
		});
		_toolaBar.add(_viewerButton);
		_toolaBar.addSeparator(); 
		
		//regions Button
		_regions = new JButton();
		_regions.setToolTipText("Change Regions Dialog");
		_regions.setIcon(new ImageIcon("resources/icons/regions.png"));
		_regions.addActionListener( (e) ->  _changeRegionsDialog.open(ViewUtils.getWindow(this))); 
		
		_toolaBar.add(_regions);
		_toolaBar.addSeparator();
		
		//run button
		_runButton = new JButton();
		_runButton.setToolTipText("Run"); 
		_runButton.setIcon(new ImageIcon("resources/icons/run.png"));
		_runButton.addActionListener( (e) -> {
			//1) Deshabilitar botones menos el de stop
			enableButton(false); 
			this._stopped = false; 
			//2) Sacar el valor dt del JTextField
			String text = _deltaTimeTextField.getText();
			double dt = Double.parseDouble(text); 
			//3) Llamar a run_sim
			//String text2 = _stepsSpinner.getValue(); 
			int n = Integer.parseInt(_stepsSpinner.getValue().toString());
			//int n = Integer.parseInt(text2); 
			run_sim(n, dt); 
			
		});
		
		_toolaBar.add(_runButton); 
		_toolaBar.addSeparator(); 
		
		//Stop Button
		_stopButton = new JButton();
		_stopButton.setToolTipText("Stop");
		_stopButton.setIcon(new ImageIcon("resources/icons/stop.png")); 
		_stopButton.addActionListener( (e) -> this._stopped = true); //detiene run_sim
		
		_toolaBar.add(_stopButton);
		_toolaBar.addSeparator();
		
		
		//JSpinner
		_toolaBar.add(new JLabel("Steps: "));
		//esto permite que el rango del spinner sea de 1000 a 10000 y se incremente en 100 uds
		_stepsSpinner = new JSpinner(new SpinnerNumberModel(1000,1,10000,100));
		_stepsSpinner.setMaximumSize(new Dimension(80,40));
		_stepsSpinner.setMinimumSize(new Dimension(80,40));
		_stepsSpinner.setPreferredSize(new Dimension(80,40));
		_stepsSpinner.setToolTipText("Steps"); 
		_toolaBar.add(_stepsSpinner); 
		
		//JTEXTFIELD
		_toolaBar.add(new JLabel ("Delta-time: "));
		//ancho suficiente para mostrat 5 caracteres
		_deltaTimeTextField = new JTextField(5);
		_deltaTimeTextField.setMaximumSize(new Dimension(80,40));
		_deltaTimeTextField.setMinimumSize(new Dimension(80,40));
		_deltaTimeTextField.setPreferredSize(new Dimension(80,40)); 
		_deltaTimeTextField.setToolTipText("delta-time");
		_deltaTimeTextField.setText("0.03"); 
		_toolaBar.add(_deltaTimeTextField); 
		
		
		
		// Quit Button
		_toolaBar.add(Box.createGlue()); // this aligns the button to the right
		_toolaBar.addSeparator();
		
		_quitButton = new JButton();
		_quitButton.setToolTipText("Quit");
		_quitButton.setIcon(new ImageIcon("resources/icons/exit.png"));
		_quitButton.addActionListener((e) -> ViewUtils.quit(this)); 
		_toolaBar.add(_quitButton);
		
		// TODO Inicializar _fc con una instancia de JFileChooser. Para que siempre
		// abre en la carpeta de ejemplos puedes usar:
		//
		// _fc.setCurrentDirectory(new File(System.getProperty("user.dir") + 
		//"/resources/examples"));
		
		_fc = new JFileChooser(); 
		_fc.setCurrentDirectory(new File(System.getProperty("C:\\Users\\ramos\\OneDrive - "
				+ "Universidad Complutense de Madrid (UCM)\\UCM\\3º\\2º Cuatri\\TP II"
				+ "\\Simulador Ecosistema V2") + "/resources/examples"));
		
	
		// TODO Inicializar _changeRegionsDialog con instancias del diálogo de cambio
		// de regiones
		_changeRegionsDialog = new ChangeRegionsDialog(_ctrl); 

	}
	
	//MAS METODOS
	private void run_sim(int n, double dt) { 
		if (n > 0 && !_stopped) {
			try {
				_ctrl.advance(dt);
				SwingUtilities.invokeLater(() -> run_sim(n - 1, dt));
			} catch (Exception e) {
				// TODO llamar a ViewUtils.showErrorMsg con el mensaje de error
				// que corresponda
				ViewUtils.showErrorMsg("Error en la simulacion"); 
				// TODO activar todos los botones
				enableButton(true);
				_stopped = true;
			}
		} else {
			// TODO activar todos los botones
			enableButton(true); 
			_stopped = true;
		}

	}
	
	private void enableButton(boolean b) {
		_openButton.setEnabled(b); 
		_viewerButton.setEnabled(b);
		_regions.setEnabled(b); 
		_quitButton.setEnabled(b); 
		//_runButton.setEnabled(b); 
	}



}
