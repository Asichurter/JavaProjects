
public class HfmItnNode<T> extends HfmNode<T>{
	private int Weight;
	private HfmNode<T> Left;
	private HfmNode<T> Right;

	public HfmItnNode(HfmNode<T> l, HfmNode<T> r) {
		this.Left = l;
		this.Right = r;
		Weight = l.weight() + r.weight();
	}
	
	int weight() {
		return Weight;
	}
	
	boolean isLeaf() {
		return false;
	}
	
	HfmNode<T> left(){
		return this.Left;
	}
	
	HfmNode<T> right(){
		return this.Right;
	}
}
