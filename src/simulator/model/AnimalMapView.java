package simulator.model;

import java.util.List;
import java.util.function.Predicate;

public interface AnimalMapView extends MapInfo, FoodSupplier{
	//representa lo que un animal puede ver del gestor de regiones. Puede ver el mapa
	//pedir comida y pedir lista de animales en su campo visual
	public List<Animal> get_animals_in_range(Animal e, Predicate<Animal> filter);
}
