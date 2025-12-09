package simulator.model;

import java.util.List;

public interface SelectionStrategy {
	Animal select(Animal a, List<Animal> as);  
	//El metodo select selecciona para el animal "a" un animal de la lista "as"
	//Lo implementan las clases Select que implementan el metodo select
	
}
