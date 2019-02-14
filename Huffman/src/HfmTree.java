import java.util.Comparator;
import java.util.LinkedList;

/**
 * 泛型哈夫曼树
 * @param <T> 哈夫曼树储存的值的类型，一般是char
 */
public class HfmTree<T>{
	private HfmNode<T> Root;
	private int Size;
	
	public HfmTree(HfmNode<T> r) {
		Root = r;
		Size = 0;
	}
	
	public HfmNode<T> root(){
		return this.Root;
	}
	
	public void traverse(HfmNode<T> root, String code) {
		if (root.isLeaf())
			System.out.println("字符：" + ((HfmLeafNode<T>)root).letter() + 
					",频率：" + ((HfmLeafNode<T>)root).weight() + 
					"，编码：" + code);
		else {
			traverse(((HfmItnNode<T>)root).left(), code+"0");
			traverse(((HfmItnNode<T>)root).right(), code+"1");
		}
	}
	
	public int size() {
		return this.Size;
	}
	
	/**
	 * 利用频率和对应的值的数组，创建一个由链表储存的只有一个根节点的哈夫曼树的森林，用于为哈夫曼树的构建做准备
	 * @param fre 权重数组
	 * @param ch 值的数组
	 * @param count 数量
	 * @return 哈夫曼树的森林
	 */
	public static <T> LinkedList<HfmTree<T>> makeForest(int[] fre, T[] ch, int count){
        LinkedList<HfmTree<T>> forest = new LinkedList<>();
        for (int i = 0; i < count; i++){
            forest.add(new HfmTree<T>(new HfmLeafNode<T>(fre[i], ch[i])));
        }
        return forest;
	}
	
	public static <T> HfmTree<T> buildHuff(LinkedList<HfmTree<T>> forest, Comparator<HfmTree<T>> comp, int count){
		Heap<HfmTree<T>> heap = new Heap<HfmTree<T>>(forest, comp, count, false, count);
		while (heap.heapSize() > 1){
			HfmTree<T> temp1 = heap.removeFirst();		
			HfmTree<T> temp2 = heap.removeFirst();
			//先出来的元素小一些，因为是小顶堆
			HfmItnNode<T> newRoot = new HfmItnNode<T>(temp1.root(), temp2.root());
			HfmTree<T> newTree = new HfmTree<T>(newRoot);
			heap.insert(newTree);
		}
		return heap.removeFirst();
	}
	
	public String decode(String mes) {
		StringBuilder string = new StringBuilder();
		HfmNode<T> guild = this.Root;
		for (int i = 0;; ) {
			if (!guild.isLeaf()) {
				if (mes.charAt(i) == '0')
					guild = ((HfmItnNode<T>)guild).left();
				else guild = ((HfmItnNode<T>)guild).right();
				//只有内节点才会消耗编码值
				i++;
			}
			else {
				string.append(((HfmLeafNode<T>)guild).letter());
				//编码完一个字符后立刻回到根节点
				guild = this.Root;
				if (i == mes.length())
					return string.toString();
			}
		}
		
	}

}
