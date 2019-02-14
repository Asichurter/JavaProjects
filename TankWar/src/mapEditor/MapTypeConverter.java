package mapEditor;

import java.awt.Image;
import java.awt.Toolkit;

/**
 * 用于将地图代码生成地图图像的类
 */
public class MapTypeConverter{
	/**
	 * 
	 * @param iden 地图的标识代码
	 * @param otherDes 地图类型的额外描述（如果是公路类型的话，将会是全称）
	 * @return	地图类型对应的Image对象
	 */
	public static Image getMapImage(int iden, String otherDes) {
		Toolkit tool = Toolkit.getDefaultToolkit();
		switch(iden) {
			case 2:
				return tool.getImage("Icon/地形/草地.jpg");
			case 3:
				return tool.getImage("Icon/地形/沙漠.jpg");
			case 4:
				return tool.getImage("Icon/地形/水域.png");
			case 5:
				return tool.getImage("Icon/地形/山地.png");
			case 6:
				return tool.getImage("Icon/地形/砖墙.png");
			case 7:
				return tool.getImage("Icon/地形/" + otherDes + ".png");
			case 8:
				return tool.getImage("Icon/地形/平原.png");
			case 9:
				return tool.getImage("Icon/地形/铁墙.png");
			/*case -1:
				return tool.getImage("Icon/删除.png");*/
			default:
				return null;
		}
	}
}
