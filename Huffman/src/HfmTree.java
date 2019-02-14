import java.util.Comparator;
import java.util.LinkedList;

/**
 * ���͹�������
 * @param <T> �������������ֵ�����ͣ�һ����char
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
			System.out.println("�ַ���" + ((HfmLeafNode<T>)root).letter() + 
					",Ƶ�ʣ�" + ((HfmLeafNode<T>)root).weight() + 
					"�����룺" + code);
		else {
			traverse(((HfmItnNode<T>)root).left(), code+"0");
			traverse(((HfmItnNode<T>)root).right(), code+"1");
		}
	}
	
	public int size() {
		return this.Size;
	}
	
	/**
	 * ����Ƶ�ʺͶ�Ӧ��ֵ�����飬����һ�����������ֻ��һ�����ڵ�Ĺ���������ɭ�֣�����Ϊ���������Ĺ�����׼��
	 * @param fre Ȩ������
	 * @param ch ֵ������
	 * @param count ����
	 * @return ����������ɭ��
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
			//�ȳ�����Ԫ��СһЩ����Ϊ��С����
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
				//ֻ���ڽڵ�Ż����ı���ֵ
				i++;
			}
			else {
				string.append(((HfmLeafNode<T>)guild).letter());
				//������һ���ַ������̻ص����ڵ�
				guild = this.Root;
				if (i == mes.length())
					return string.toString();
			}
		}
		
	}

}
