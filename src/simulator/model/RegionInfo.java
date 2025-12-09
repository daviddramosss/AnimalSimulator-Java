package simulator.model;

import java.util.List;

public interface RegionInfo extends JSONable{
	// for now it is empty, later we will make it implements the interface
	//Iterable<AnimalInfo> getAnimalsInfo();
	public List<AnimalInfo> getAnimalsInfo();
	public String to_String(); 

}
