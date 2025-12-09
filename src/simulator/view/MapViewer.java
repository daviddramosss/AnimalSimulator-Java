package simulator.view;

import simulator.misc.Vector2D;
import simulator.model.Animal;
import simulator.model.AnimalInfo;
import simulator.model.MapInfo;

import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Rectangle2D;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

@SuppressWarnings("serial")
public class MapViewer extends AbstractMapViewer {

	// Anchura/altura/ de la simulación -- se supone que siempre van a ser iguales
	// al tamaño del componente
	private int _width;
	private int _height;

	// Número de filas/columnas de la simulación
	private int _rows;
	private int _cols;

	// Anchura/altura de una región
	int _rwidth;
	int _rheight;

	// Mostramos sólo animales con este estado. Los posibles valores de _currState
	// son null, y los valores deAnimal.State.values(). Si es null mostramos todo.
	Animal.State _currState;

	// En estos atributos guardamos la lista de animales y el tiempo que hemos
	// recibido la última vez para dibujarlos.
	volatile private Collection<AnimalInfo> _objs;
	volatile private Double _time;

	// Una clase auxilar para almacenar información sobre una especie
	private static class SpeciesInfo {
		private Integer _count;
		private Color _color;

		SpeciesInfo(Color color) {
			_count = 0;
			_color = color;
		}
	}

	// Un mapa para la información sobre las especies
	Map<String, SpeciesInfo> _kindsInfo = new HashMap<>(); 

	// El font que usamos para dibujar texto
	private Font _font = new Font("Arial", Font.BOLD, 12);

	// Indica si mostramos el texto la ayuda o no
	private boolean _showHelp;

	public MapViewer() {
		initGUI();
	}

	private void initGUI() {

		addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				switch (e.getKeyChar()) {
				case 'h':
					_showHelp = !_showHelp;
					repaint();
					break;
				case 's':
					// Cambiar _currState al siguiente (de manera circular). Después de null
					// viene el primero de Animal.State.values() y después del último viene null.
					_currState = _currState == null ? Animal.State.values()[0] : 
					   (_currState == Animal.State.values()[Animal.State.values().length - 1] ? null : 
						   Animal.State.values()[_currState.ordinal() + 1]);
					
					repaint();
				default:
				}
			}

		});

		addMouseListener(new MouseAdapter() {

			@Override
			public void mouseEntered(MouseEvent e) {
				requestFocus(); // Esto es necesario para capturar las teclas cuando el ratón está sobre este
								// componente.
			}
		});

		// Por defecto mostramos todos los animales
		_currState = null;

		// Por defecto mostramos el texto de ayuda
		_showHelp = true;
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);

		Graphics2D gr = (Graphics2D) g;
		gr.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		gr.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

		// Cambiar el font para dibujar texto
		g.setFont(_font);

		// Dibujar fondo blanco
		gr.setBackground(Color.WHITE);
		gr.clearRect(0, 0, _width, _height);

		// Dibujar los animales, el tiempo, etc.
		if (_objs != null)
			drawObjects(gr, _objs, _time);

		// Mostrar el texto de ayuda si _showHelp es true. El texto a mostrar es el
		// siguiente (en 2 líneas):
		//
		// h: toggle help
		// s: show animals of a specific state
		if(_showHelp) {
			drawStringWithRect(gr, 10, 20, "h: toggle help"); 
			drawStringWithRect(gr, 10, 40, "s: show animals of a specific state"); 
		
		}

	}

	private boolean visible(AnimalInfo a) {
		//Devolver true si el animal es visible, es decir si _currState es null o
		// su estado es igual a _currState.
		boolean mostrar = false;
		if(_currState == null || _currState == a.get_state()) {
			mostrar = true; 
		}
		return mostrar;
	}

	private void drawObjects(Graphics2D g, Collection<AnimalInfo> animals, Double time) {

		// Dibujar el grid de regiones
		for (int i = 0; i < _rows; i++) {
		    for (int j = 0; j < _cols; j++) {
		        int x = j * _rwidth;
		        int y = i * _rheight;
		        g.drawRect(x, y, _rwidth, _rheight);
		    }
		}

		// Dibujar los animales
		for (AnimalInfo a : animals) {

			// Si no es visible saltamos la iteración
			if (!visible(a))
				continue;

			// La información sobre la especie de 'a'
			SpeciesInfo esp_info = _kindsInfo.get(a.get_genetic_code());

			//Si esp_info es null, añade una entrada correspondiente al mapa. Para el
			// color usa ViewUtils.get_color(a.get_genetic_code())
			if(esp_info == null) {
				String genetic_code = a.get_genetic_code();
				Color color = ViewUtils.get_color(a.get_genetic_code()); 
				SpeciesInfo info = new SpeciesInfo(color);  
				_kindsInfo.put(genetic_code, info); 
			}
			esp_info = _kindsInfo.get(a.get_genetic_code());

			// TODO Incrementar el contador de la especie (es decir el contador dentro de
			// tag_info)
			if(esp_info != null) {
				esp_info._count++;
			}
			
			
			// TODO Dibijar el animal en la posicion correspondiente, usando el color
			// tag_info._color. Su tamaño tiene que ser relativo a su edad, por ejemplo
			// edad/2+2. Se puede dibujar usando fillRoundRect, fillRect o fillOval.
			if(esp_info != null) {
				Vector2D _pos = a.get_position();
				int x = (int) _pos.getX();
				int y = (int) _pos.getY();
				int size = (int) (a.get_age()/2+2); //ns donde meter el size
				g.setColor(esp_info._color);
				g.fillOval(x, y, size, size);  
			}
			

		}

		// TODO Dibujar la etiqueta del estado visible, sin no es null.
		if(_currState != null) {
			String estado_visible = "State " + _currState.toString(); 
			drawStringWithRect(g, 10, 60, estado_visible); 
		}

		// TODO Dibujar la etiqueta del tiempo. Para escribir solo 3 decimales puede
		// usar String.format("%.3f", time)
		String etiqueta_tiempo = "Time " + String.format("%.3f", time); 
		drawStringWithRect(g, 10, 80, etiqueta_tiempo);  

		// TODO Dibujar la información de todas la especies. Al final de cada iteración
		// poner el contador de la especie correspondiente a 0 (para resetear el cuento)
		int y = 100;
		for (Entry<String, SpeciesInfo> e : _kindsInfo.entrySet()) {
			String etiqueta_especies = e.getKey() + ": " + e.getValue()._count; 
			drawStringWithRect(g, 10, y, etiqueta_especies);
			y+=20; 
			e.getValue()._count = 0; 
		}
		
	}

	// Un método que dibujar un texto con un rectángulo
	void drawStringWithRect(Graphics2D g, int x, int y, String s) {
		Rectangle2D rect = g.getFontMetrics().getStringBounds(s, g);
		g.drawString(s, x, y);
		g.drawRect(x - 1, y - (int) rect.getHeight(), (int) rect.getWidth() + 1, (int) rect.getHeight() + 5);
	}

	@Override
	public void update(List<AnimalInfo> objs, Double time) {
		// Almacenar objs y time en los atributos correspondientes y llamar a
		// repaint() para redibujar el componente.
		this._objs = objs;
		this._time = time; 
		repaint();
	}

	@Override
	public void reset(double time, MapInfo map, List<AnimalInfo> animals) {
		// Actualizar los atributos _width, _height, _cols, _rows, etc.
		this._width = map.get_width();
		this._height = map.get_height();
		this._cols = map.get_cols();
		this._rows = map.get_rows(); 
		//creo que tmb hay q hacer esto
		this._objs = animals;
		this._time = time; 

		// Esto cambia el tamaño del componente, y así cambia el tamaño de la ventana
		// porque en MapWindow llamamos a pack() después de llamar a reset
		setPreferredSize(new Dimension(map.get_width(), map.get_height()));

		// Dibuja el estado
		update(animals, time);
	}

}
