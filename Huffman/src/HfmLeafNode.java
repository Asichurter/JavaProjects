
public class HfmLeafNode<T> extends HfmNode<T>{
	private int Weight;
	private T Letter;

	public HfmLeafNode(int w, T ch) {
		Weight = w;
		Letter = ch;
	}
	
	public int weight() {
		return this.Weight;
	}
	
	public boolean isLeaf() {
		return true;
	}
	
	public T letter() {
		return this.Letter;
	}

}
