package simulator.view;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;

import simulator.control.Controller;

public class MainWindow extends JFrame{
	
	private Controller _ctrl;
	
	public MainWindow(Controller ctrl) {
		super("[ECOSYSTEM SIMULATOR]");
		_ctrl = ctrl;
		initGUI();
	}
	
	private void initGUI() {
		JPanel mainPanel = new JPanel(new BorderLayout()); 
		setContentPane(mainPanel); //añade el panel al JFrame y lo establece como principal 
		
		// TODO crear ControlPanel y añadirlo en PAGE_START de mainPanel
		mainPanel.add(new ControlPanel(_ctrl), BorderLayout.PAGE_START);  
		// TODO crear StatusBar y añadirlo en PAGE_END de mainPanel
		mainPanel.add(new StatusBar(_ctrl), BorderLayout.PAGE_END); 
		
		// Definición del panel de tablas (usa un BoxLayout vertical)
		JPanel contentPanel = new JPanel();
		contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
		mainPanel.add(contentPanel, BorderLayout.CENTER);
		
		// TODO crear la tabla de especies y añadirla a contentPanel.
		// Usa setPreferredSize(new Dimension(500, 250)) para fijar su tamaño
		InfoTable species = new InfoTable("Species", new SpeciesTableModel(_ctrl));
		species.setPreferredSize(new Dimension(500, 250)); 
		contentPanel.add(species);
		
		// TODO crear la tabla de regiones.
		// Usa setPreferredSize(new Dimension(500, 250)) para fijar su tamaño
		InfoTable regions = new InfoTable("Regions", new RegionsTableModel(_ctrl));
		regions.setPreferredSize(new Dimension(500, 250));
		contentPanel.add(regions); 
		
		// TODO llama a ViewUtils.quit(MainWindow.this) en el método windowClosing
		addWindowListener(new WindowListener() {

			@Override
			public void windowOpened(WindowEvent e) {
				
			}

			@Override
			public void windowClosing(WindowEvent e) {
				ViewUtils.quit(MainWindow.this);
			}

			@Override
			public void windowClosed(WindowEvent e) {
				
			}

			@Override
			public void windowIconified(WindowEvent e) {
				
			}

			@Override
			public void windowDeiconified(WindowEvent e) {
				
			}

			@Override
			public void windowActivated(WindowEvent e) {
				
			}

			@Override
			public void windowDeactivated(WindowEvent e) {
				
			}
			
		});
			
		
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		pack();
		setVisible(true);

	
	}


}

