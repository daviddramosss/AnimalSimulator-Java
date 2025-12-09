package simulator.launcher;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import javax.swing.SwingUtilities;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.json.JSONObject;
import org.json.JSONTokener;

import simulator.control.Controller;
import simulator.factories.Builder;
import simulator.factories.BuilderBasedFactory;
import simulator.factories.DefaultRegionBuilder;
import simulator.factories.DynamicSupplyRegionBuilder;
import simulator.factories.Factory;
import simulator.factories.SelectClosestBuilder;
import simulator.factories.SelectFirstBuilder;
import simulator.factories.SelectYoungestBuilder;
import simulator.factories.SheepBuilder;
import simulator.factories.WolfBuilder;
import simulator.misc.Utils;
import simulator.model.Animal;
import simulator.model.Region;
import simulator.model.SelectYoungest;
import simulator.model.SelectionStrategy;
import simulator.model.Simulator;
import simulator.view.MainWindow;

public class Main {

	private enum ExecMode {
		BATCH("batch", "Batch mode"), GUI("gui", "Graphical User Interface mode");

		private String _tag;
		private String _desc;

		private ExecMode(String modeTag, String modeDesc) {
			_tag = modeTag;
			_desc = modeDesc;
		}

		public String get_tag() {
			return _tag;
		}

		public String get_desc() {
			return _desc;
		}
	}

	//factorias 
	public static Factory<SelectionStrategy> selection_strategy_factory = null; 
	public static Factory<Region> region_builders_factory = null; 
	public static Factory<Animal> animal_factory = null;
	//Simulador
	private static Simulator _sim = null;
	//Controller
	private static Controller controller = null; 
	//sv
	private static boolean sv = true;   
	
	// default values for some parameters
	//
	private final static Double _default_time = 10.0; // in seconds
	private final static Double _default_dtime = 0.03; 
	private final static ExecMode _default_mode = ExecMode.GUI; 

	// some attributes to stores values corresponding to command-line parameters
	//
	private static Double _time = null;
	private static String _in_file = null;
	private static ExecMode _mode = ExecMode.GUI; 
	private static String _outFile = null;
	public static Double _dtime = null; 
	
	

	private static void parse_args(String[] args) {

		// define the valid command line options
		//
		Options cmdLineOptions = build_options();

		// parse the command line as provided in args
		//
		CommandLineParser parser = new DefaultParser();
		try {
			CommandLine line = parser.parse(cmdLineOptions, args);
			parse_help_option(line, cmdLineOptions);
			parse_in_file_option(line);
			parse_m(line);
			parse_time_option(line);
			parseOutputStream(line);
			parseDeltaTimeOption(line); 
			parse_sv(line);
			 
			

			// if there are some remaining arguments, then something wrong is
			// provided in the command line!
			//
			String[] remaining = line.getArgs();
			if (remaining.length > 0) {
				String error = "Illegal arguments:";
				for (String o : remaining)
					error += (" " + o);
				throw new ParseException(error);
			}

		} catch (ParseException e) {
			System.err.println(e.getLocalizedMessage());
			System.exit(1);
		}

	}

	private static Options build_options() {
		Options cmdLineOptions = new Options();

		// help
		cmdLineOptions.addOption(Option.builder("h").longOpt("help").desc("Print this message.").build());

		// input file
		cmdLineOptions.addOption(Option.builder("i").longOpt("input").hasArg().desc("A configuration file.").build());

		// steps
		cmdLineOptions.addOption(Option.builder("t").longOpt("time").hasArg()
				.desc("An real number representing the total simulation time in seconds. Default value: "
						+ _default_time + ".")
				.build());

		//dt
		cmdLineOptions.addOption(Option.builder("dt").longOpt("delta-time").hasArg()
				.desc("A double representing actual time, in seconds, per simulation step. Default value: "
						+ _default_dtime + ".").build()); 
		
		//ouput
	cmdLineOptions.addOption(Option.builder("o").longOpt("output").hasArg().desc("Output file, where output is written").build());
		
		//sv
	cmdLineOptions.addOption(Option.builder("sv").longOpt("simple-viewer")
			.desc("Show the viewer window in console mode.").build());
	
		//m
	cmdLineOptions.addOption(Option.builder("m").longOpt("mode").hasArg()
			.desc("Possible values: 'batch' (Batch\n"
					+ "mode), 'gui' (Graphical User Interface mode).\n"
					+ "Default value: "
					+ _default_mode + ".").build());   
		return cmdLineOptions;
	}

	//-h
	private static void parse_help_option(CommandLine line, Options cmdLineOptions) {
		if (line.hasOption("h")) {
			HelpFormatter formatter = new HelpFormatter();
			formatter.printHelp(Main.class.getCanonicalName(), cmdLineOptions, true);
			System.exit(0);
		}
	}

	//-i
	private static void parse_in_file_option(CommandLine line) throws ParseException {
		_in_file = line.getOptionValue("i");
		if (_mode == ExecMode.BATCH && _in_file == null) {
			throw new ParseException("In batch mode an input configuration file is required");
		}
	}

	//-t SOLO PARA BATCH
	private static void parse_time_option(CommandLine line) throws ParseException {
		if(_mode == ExecMode.BATCH) {
			String t = line.getOptionValue("t", _default_time.toString());
			try {
				_time = Double.parseDouble(t);
				assert (_time >= 0);
			} catch (Exception e) {
				throw new ParseException("Invalid value for time: " + t);
			}
		}
	}
	
	//sv
	private static void parse_sv(CommandLine line) throws ParseException {
		//sv = line.hasOption("sv"); 
		if(line.hasOption("sv")) {
			sv = true;
		} else {
			sv = false;
		}
	}
	
	//-m
	private static void parse_m(CommandLine line) throws ParseException{
		//este metodo coge lo que viene en "m" o si no hay nada, el valor por defecto
		String m = line.getOptionValue("m", _default_mode.toString());  
		try {
			_mode = ExecMode.valueOf(m); 
		}
		catch (Exception e){
			throw new ParseException("Invalid value for time: " + m); 
		}
		
	}
	
	//-o SOLO PARA BATCH
	private static void parseOutputStream(CommandLine line) throws ParseException {
		if(_mode == ExecMode.BATCH) {
			_outFile = line.getOptionValue("o");
		}
		
	}
	
	//-dt
	private static void parseDeltaTimeOption(CommandLine line) throws ParseException {
		String dt = line.getOptionValue("dt", _default_dtime.toString());  
		try { 
			_dtime = Double.parseDouble(dt);
			assert (_dtime > 0);
		} catch (Exception e) {
			throw new ParseException("Invalid delta-time value: " + dt);
		}
	}

	//implementar
	private static void init_factories() {
		//inicializamos las factorias.
		//empezamos con las estrategias
		List<Builder<SelectionStrategy>> selection_strategy_builders = new ArrayList<>();
		selection_strategy_builders.add(new SelectFirstBuilder());
		selection_strategy_builders.add(new SelectClosestBuilder());
		selection_strategy_builders.add(new SelectYoungestBuilder()); 
		selection_strategy_factory = new BuilderBasedFactory<SelectionStrategy>(selection_strategy_builders);
		//creamos las factorias de las regiones
		List<Builder<Region>> region_builders = new ArrayList<>();
		region_builders.add(new DefaultRegionBuilder());
		region_builders.add(new DynamicSupplyRegionBuilder());
		region_builders_factory = new BuilderBasedFactory<Region>(region_builders); 
		//creamos las factorias de los animales
		List<Builder<Animal>> animal_builders = new ArrayList<>();
		animal_builders.add(new SheepBuilder(selection_strategy_builders));
		animal_builders.add(new WolfBuilder(selection_strategy_builders)); 
		animal_factory = new BuilderBasedFactory<Animal>(animal_builders); 		 
	}

	private static JSONObject load_JSON_file(InputStream in) {
		return new JSONObject(new JSONTokener(in));
	}

	//terminar
	private static void start_batch_mode() throws Exception {
		InputStream is = new FileInputStream(new File(_in_file));
		//1) cargar el archivo de entrada en un JSONObject
		JSONObject json = load_JSON_file(is); 
		//2) crear archivo de salida
		OutputStream out; //ns si es asi
		if(_outFile != null) {
			out = new FileOutputStream(new File(_outFile));  
		}
		else {
			out = System.out; 
		}
		//3) crear instancia de Simulator
		int widht = json.getInt("width"); 
		int height = json.getInt("height");
		int rows = json.getInt("rows");
		int cols = json.getInt("cols"); 
		_sim = new Simulator(cols, rows, widht, height, animal_factory, region_builders_factory);  
		//4) crear controller pasandole simulador
		controller = new Controller(_sim); 
		//5) llamar a load_data
		controller.load_data(json); 
		//6) llamar al metodo run
		controller.run(_time, _dtime, sv, out); 
		//7) cerrar alchivo de salida
		out.close();
	}

	private static void start_GUI_mode() throws Exception {
		//throw new UnsupportedOperationException("GUI mode is not ready yet ...");
		InputStream is = new FileInputStream(new File(_in_file));
		//Si hay archivo de entrada lo usamos para crear la instancia del Simulator. Igual que antes
		if(is != null) {
			//1) cargar el archivo de entrada en un JSONObject
			JSONObject json = load_JSON_file(is); 
			//3) crear instancia de Simulator
			int widht = json.getInt("width"); 
			int height = json.getInt("height");
			int rows = json.getInt("rows");
			int cols = json.getInt("cols"); 
			_sim = new Simulator(cols, rows, widht, height, animal_factory, region_builders_factory);  
			//4) crear controller pasandole simulador
			controller = new Controller(_sim); 
			//5) llamar a load_data
			controller.load_data(json);  
			//6) En lugar de llamar a controller.run
			SwingUtilities.invokeAndWait(() -> new MainWindow(controller)); 
			
		}
		else {
			//simulator con valores predeterminados
			int widht = 800;
			int height = 600;
			int rows = 15;
			int cols = 20; 
			_sim = new Simulator(cols, rows, widht, height, animal_factory, region_builders_factory);  
			controller = new Controller(_sim); 
			SwingUtilities.invokeAndWait(() -> new MainWindow(controller)); 
		}
	}

	private static void start(String[] args) throws Exception {
		init_factories();
		parse_args(args);
		switch (_mode) {
		case BATCH:
			start_batch_mode();
			break;
		case GUI:
			start_GUI_mode();
			break;
		}
	}
	

	public static void main(String[] args) {
		Utils._rand.setSeed(2147483647l);
		try { 
			start(args);
		} catch (Exception e) {
			System.err.println("Something went wrong ...");
			System.err.println();
			e.printStackTrace();
		}
	}
}
