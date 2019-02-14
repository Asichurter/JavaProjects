package game;

/**
 * 2018年大学生创新创业项目：符号AI支持的围棋模式概括程序
 * 作者：唐郅杰
 * 时间：2018.8
 * 最后修改时间：2018.11.11
 */

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.FileNotFoundException;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

import ai.MTCS;

/**
 * 用于容纳程序面板和其他组件的框架
 *
 */

public class GoClient extends JFrame{
	
	private GoPanel gamepanel;
	private final int Bounds = 700;
	private boolean AiType;
	private ButtonGroup Button_Group;

	public GoClient() throws GoException, FileNotFoundException {
		this.setTitle("GoProject");
		this.setVisible(true);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setBounds(100, 100, Bounds, Bounds);
		this.AiType = true;
		
		this.gamepanel = new GoPanel();
		
		Button_Group = new ButtonGroup();
		JRadioButton mtcs_button = new JRadioButton("使用蒙特卡洛树搜索", true);
		mtcs_button.addActionListener(e->{
			GoClient.this.AiType = true;
			GoClient.this.requestFocus();
		});
		Button_Group.add(mtcs_button);
		JRadioButton gametree_button = new JRadioButton("使用估值函数博弈树搜索", false);
		gametree_button.addActionListener(e->{
			GoClient.this.AiType = false;
			GoClient.this.requestFocus();
		});
		Button_Group.add(gametree_button);
		
		JButton button = new JButton("AI行棋");
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					GoClient.this.gamepanel.togglePlayer(true, AiType);
					GoClient.this.requestFocus();
					//让JFrame重新获得输入焦点
					GoClient.this.repaint();
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}
		});
		JButton simulate_button = new JButton("模拟行棋");
		simulate_button.addActionListener((e->{
			try {
				int times = Integer.parseInt(JOptionPane.showInputDialog(gamepanel, "请输入模拟的次数：", 10));
				GoClient.this.gamepanel.simulate(times);
				GoClient.this.requestFocus();
			} catch (FileNotFoundException | CloneNotSupportedException | GoException e1) {
				e1.printStackTrace();
			}
		}));
		JButton evaluate_button = new JButton("局势");
		evaluate_button.addActionListener((m)->{
			int black_score = MTCS.externEvaluate(this.gamepanel.getBoard(), true);
			int white_score = MTCS.externEvaluate(this.gamepanel.getBoard(), false);
			JOptionPane.showMessageDialog(this.gamepanel, "黑方：" + black_score + "\n白方:" + white_score, "局势",
					JOptionPane.INFORMATION_MESSAGE);
			GoClient.this.requestFocus();
		});
		JPanel panel = new JPanel();
		JPanel assembly = new JPanel();
		assembly.setLayout(new BorderLayout());
		panel.add(mtcs_button);
		panel.add(gametree_button);
		panel.add(button);
		panel.add(simulate_button);
		panel.add(evaluate_button);
		assembly.add(panel, BorderLayout.NORTH);
		assembly.add(gamepanel, BorderLayout.CENTER);
		this.add(assembly);
		this.addKeyListener(this.getClientListener());
		this.requestFocus();
	}
	
	/**
	 * 获得围棋客户端的按键监听器
	 * @return 按键监听器
	 * @throws GoException 生成按键监听器时抛出的异常
	 */
	public KeyAdapter getClientListener() throws GoException{
		return new KeyAdapter() {
			public void keyPressed(KeyEvent e) {
				try{
					switch(e.getKeyCode()){
						case KeyEvent.VK_W:
						case KeyEvent.VK_UP:
							GoClient.this.gamepanel.highlightMove(1);
							GoClient.this.repaint();
							break;
						case KeyEvent.VK_D:
						case KeyEvent.VK_RIGHT:
							GoClient.this.gamepanel.highlightMove(2);
							GoClient.this.repaint();
							break;
						case KeyEvent.VK_S:
						case KeyEvent.VK_DOWN:
							GoClient.this.gamepanel.highlightMove(3);
							GoClient.this.repaint();
							break;
						case KeyEvent.VK_A:
						case KeyEvent.VK_LEFT:
							GoClient.this.gamepanel.highlightMove(4);
							GoClient.this.repaint();
							break;
						case KeyEvent.VK_ENTER:
							if (GoClient.this.gamepanel.playerAct()) {
								GoClient.this.gamepanel.togglePlayer(false, false);
							}
							GoClient.this.repaint();
							break;
						default:
							break;
					}
				}	
				catch(GoException | FileNotFoundException | CloneNotSupportedException E) {
					E.printStackTrace();
				}
			}
		};
	}
	
	public void act() throws FileNotFoundException, GoException, CloneNotSupportedException {
		this.gamepanel.togglePlayer(true, this.AiType);
		this.AiType = !this.AiType;
		this.repaint();
	}
}
