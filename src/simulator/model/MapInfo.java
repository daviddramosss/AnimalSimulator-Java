package simulator.model;

public interface MapInfo extends JSONable, Iterable<MapInfo.RegionData>{
	public record RegionData(int row, int col, RegionInfo r) {}
	
	/*EXPLICACION
	 * Iterable<MapInfo.RegionData> permite recorrer una instancia de MapInfo 
	 * mediante un bucle for-each y obtener objetos RegionData 
	 * que contienen información sobre las filas, columnas y regiones de un mapa.
	 * 
	 */
	
	public int get_cols();
	public int get_rows();
	public int get_width();
	public int get_height();
	public int get_region_width();
	public int get_region_height();
}
