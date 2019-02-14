package map;

public interface MapBuilder <T extends MapType> {
	T get(int x, int y, String des);
}
