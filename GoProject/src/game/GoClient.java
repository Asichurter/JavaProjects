package game;

/**
 * 2018���ѧ�����´�ҵ��Ŀ������AI֧�ֵ�Χ��ģʽ��������
 * ���ߣ���ۤ��
 * ʱ�䣺2018.8
 * ����޸�ʱ�䣺2018.11.11
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
 * �������ɳ���������������Ŀ��
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
		JRadioButton mtcs_button = new JRadioButton("ʹ�����ؿ���������", true);
		mtcs_button.addActionListener(e->{
			GoClient.this.AiType = true;
			GoClient.this.requestFocus();
		});
		Button_Group.add(mtcs_button);
		JRadioButton gametree_button = new JRadioButton("ʹ�ù�ֵ��������������", false);
		gametree_button.addActionListener(e->{
			GoClient.this.AiType = false;
			GoClient.this.requestFocus();
		});
		Button_Group.add(gametree_button);
		
		JButton button = new JButton("AI����");
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					GoClient.this.gamepanel.togglePlayer(true, AiType);
					GoClient.this.requestFocus();
					//��JFrame���»�����뽹��
					GoClient.this.repaint();
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}
		});
		JButton simulate_button = new JButton("ģ������");
		simulate_button.addActionListener((e->{
			try {
				int times = Integer.parseInt(JOptionPane.showInputDialog(gamepanel, "������ģ��Ĵ�����", 10));
				GoClient.this.gamepanel.simulate(times);
				GoClient.this.requestFocus();
			} catch (FileNotFoundException | CloneNotSupportedException | GoException e1) {
				e1.printStackTrace();
			}
		}));
		JButton evaluate_button = new JButton("����");
		evaluate_button.addActionListener((m)->{
			int black_score = MTCS.externEvaluate(this.gamepanel.getBoard(), true);
			int white_score = MTCS.externEvaluate(this.gamepanel.getBoard(), false);
			JOptionPane.showMessageDialog(this.gamepanel, "�ڷ���" + black_score + "\n�׷�:" + white_score, "����",
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
	 * ���Χ��ͻ��˵İ���������
	 * @return ����������
	 * @throws GoException ���ɰ���������ʱ�׳����쳣
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
