import java.util.Comparator;
import java.util.List;

public class Heap<T>{
	private final int DefaultSize = 20;
	private T[] Elements;
	private int Size;
	private int MaxSize;
	private boolean MaxOrMin;
	private Comparator<T> Comp;

	public Heap(T[] eles, Comparator<T> comp) {
		MaxSize = DefaultSize;
		Size = 0;
		MaxOrMin = true;
		Elements = eles;
		Comp = comp;
	}
	
	public Heap(T[] eles, Comparator<T> comp, int num, boolean M, int max) {
		assert(num <= max);
		this.MaxSize = max;
		this.Size += num;
		this.MaxOrMin = M;
		Elements = eles;
		Comp = comp;
		this.buildHeap(num);
	}
	
	public Heap(List<T> eles, Comparator<T> comp, int num, boolean M, int max) {
		assert(num <= max);
		this.MaxSize = max;
		this.Size += num;
		this.MaxOrMin = M;
		Elements = (T[])eles.toArray();
		Comp = comp;
		this.buildHeap(num);
	}
	
	public int parent(int pos) {
		if (pos == 0) {
			return -1;
		}
		return (pos - 1) / 2;
	}
	
	public int left(int pos) {
		if (2 * pos + 1 >= Size) {
			return -1;
		}
		return 2 * pos + 1;
	}
	
	int right(int pos) {
		if (2 * pos + 2 >= Size) {
			return -1;
		}
		return 2 * pos + 2;
	}

	public boolean isLeaf(int pos) {
		return pos >= Size / 2 && pos <= Size;
	}

	public int heapSize() {
		return Size;
	}
	
	public void insert(T ele) {
		if (Size == MaxSize) {
			System.out.println("���Դﵽ����������ƣ�");
		}
		else {
			Elements[Size] = ele;			
			Size++;
			insertHelper(Size-1);
		}
	}
	
	public void remove(T ele) {
		if (Size == 0) {
			System.out.println("����û��Ԫ�أ�");
			return;
		}
		int index = findHelper(ele);
		if (index == Size - 1) {
			Size--;
			return;
		}
		swap(Size - 1, index);
		Size--;
		removeHelper(index);
	}
	
	public T removeFirst() {
		remove(Elements[0]);
		return Elements[Size];
	}
	
	public void printHeap() {
		if (Size == 0) {
			System.out.println("����û��Ԫ�أ�");
			return;
		}
		for (int i = 0; i < Size; i++) {
			System.out.println(Elements[i]);
		}
		System.out.println("Ԫ�ظ�����" + Size);
	}
	
	public void foreach(Comsumer<T> func) {
		for (int i = 0; i < this.Size; i++) {
			func.act(this.Elements[i]);
		}
	}
	
	private boolean siftDown(int pos) {
		//�����Ҷ�ڵ㣬�Ǿ�û��Ҫ���������ƶ��ˣ�����Ҫ���ϵݹ�
		if (isLeaf(pos))
			return true;
		else {
			//���û���ҽڵ㣬��ֻ�Ƚ���ڵ�
			if (right(pos) < 0) {
				if (compareByMaxOrMin(Elements[left(pos)], Elements[pos])) {
					swap(pos, left(pos));
					siftDown(left(pos));
					return true;
				}
				else return false;
				//�������ѣ�������ﷵ��...
			}
			else {
				//���ݴ󶥶ѻ���С���ѣ�ѡ����ֵ���siftDown�Ľڵ���н���
				//�����ֵ���siftDown�Ľڵ�ֵ���㽻���������Ǿͽ���
				//���һ���ڵ㲻��Ҷ�ڵ㣬��ô���ٿ϶�����ڵ�
				if (compareByMaxOrMin(Elements[left(pos)], Elements[right(pos)])) {
					if (compareByMaxOrMin(Elements[left(pos)], Elements[pos])) {
						swap(pos, left(pos));
						siftDown(left(pos));
						return true;
					}
					//���ڵ㲻���㽻�����������ڵ㲻��Ҫ�����ݹ���
					else {
						return false;
					}
				}
				//������ҽڵ㣬�ٶ��ҽڵ���еݹ�
				else {
					if (compareByMaxOrMin(Elements[right(pos)], Elements[pos])) {
						swap(pos, right(pos));
						siftDown(right(pos));
						return true;
					}
					//���ڵ㲻���㽻�����������ڵ㲻��Ҫ�����ݹ���
					else return false;
				}
			}
		}
	}
	
	private boolean compareByMaxOrMin(T e1, T e2) {
		if (MaxOrMin) {
			return Comp.compare(e1, e2) >= 0;
		}
		else return Comp.compare(e2, e1) > 0;
	}
	
	private void swap(int p1, int p2) {
		if (p1 >= Size || p2 >= Size)
			return;
		T temp = Elements[p1];
		Elements[p1] = Elements[p2];
		Elements[p2] = temp;
	}
	
	private void buildHeap(int num) {
		for (int i = num / 2; i >= 0; i--) {
			siftDown(i);
		}
	}
	
	private void removeHelper(int pos) {
		while (pos != 0) {
			siftDown(pos);
			pos = parent(pos);
		}
		//������ѭ��ʱ���ض�����0��λ�ã���ʱ����ҪsiftDown
		siftDown(0);
	}
	
	private int findHelper(T ele) {
		if (Size == 0)
			return -1;
		else {
			for (int i = 0; i < Size; i++) {
				if (Elements[i] == ele)
					return i;
			}
			return -1;
		}
	}
	
	private void insertHelper(int pos) {
		//�ݹ鵽�˸��ڵ�
		if (parent(pos) < 0) {
			siftDown(pos);
		}
		else {
			if (siftDown(pos)) {
				insertHelper(parent(pos));
			}
			//����������ϵ�ĳ��siftdownû�иı䣬����Ҫ�����ϵݹ���
		}
	}
}
