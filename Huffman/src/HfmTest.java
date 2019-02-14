import java.time.Duration;
import java.time.Instant;
import java.util.LinkedList;

public class HfmTest {
	public static void main(String[] args) {
		int[] fres = { 120, 37, 42, 42, 32, 2, 7, 24 };
		Character[] chs = { 'E', 'U', 'L', 'D', 'C', 'Z', 'K', 'M' };
		Instant s = Instant.now();
		LinkedList<HfmTree<Character>> forest = HfmTree.makeForest(fres, chs, 8);
		HfmTree<Character> tree = HfmTree.buildHuff(forest, (n1 ,n2)->n1.root().weight() - n2.root().weight(), 8);
		tree.traverse(tree.root(), "");
		System.out.println("耗时：" + Duration.between(s, Instant.now()).toMillis() + "ms");
		String raw = "0100";
		System.out.println("数据码：" + raw + "\n编码结果: " + tree.decode(raw));
	}
}
