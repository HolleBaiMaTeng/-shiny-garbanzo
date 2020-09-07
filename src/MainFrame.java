import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GraphicsConfiguration;
import java.awt.GridLayout;
import java.awt.HeadlessException;
import java.awt.Insets;
import java.awt.Panel;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JFrame;




public class MainFrame extends JFrame implements KeyListener {
	/*
	 * ��Ҫ��������⣺2.��������Ż�3.�����Ż�
	 * 
	 * 
	 * 
	 * 
	 */
	public static final int WIDTH=800;				//���������
	public static final int HEIGHT=600;				//���������
	public static final int WIDTH_all=800;			//�����ܿ�
	public static final int HEIGHT_all=690;			//�����ܸ�
	public static final int P1GAME=1;
	public static final int P2GAME=2;
	private LoginPanel loginPanel;
	private GamePanel gamePanel;
	private GamePanel_2P gamePanel_2p;
	private WinPanel winPanel;
	private OverPanel overPanel;
	
	public int timeSpeed=1;
	
	public int model=1;						//���ڼ�¼���ڵ���Ϸģʽ��1-������Ϸ 2-˫����Ϸ
	
	//AudioPlayer player;
	
	private int time=0;                     //������Ϸ��ʱ����������������٣�������Ϸ�ӳ٣�
	private int stop=0;						//������ͣ����stop=0��������stop=1ֹͣ����
	public MainFrame(String string) {
		super("tankgame");//��������
		setResizable(false);
		
		
		this.setBackground(Color.BLACK);
		
		/*(�������������)
		//���������ã�
		JMenuBar menuBar=new JMenuBar();			//�½�һ���˵���
		this.setJMenuBar(menuBar);					//�����������
		
		JMenu startMenu_about=new JMenu("about");		//�½�һ���˵���
		JMenu startMenu_help=new JMenu("help");			//�½�һ���˵���
		JMenu startMenu_tool=new JMenu("tool");			//�½�һ���˵���
		menuBar.add(startMenu_about);					//��ӹ��ڲ˵�
		menuBar.add(startMenu_help);					//��Ӱ����˵�
		menuBar.add(startMenu_tool);					//��Ӱ����˵�
		
		JMenuItem help_menuItem01=new JMenuItem("Operating instructions");		//����˵��
		JMenuItem help_menuItem02=new JMenuItem("Coordinate instructions");		//����˵��
		JMenuItem about_menuItem01=new JMenuItem("want");						//������ʲô
		JMenuItem tool_menuItem01=new JMenuItem("World Map");					//��������
		
		about_menuItem01.addActionListener(new i_want());						//��Ӽ�����
		
		startMenu_about.add(about_menuItem01);
		startMenu_help.add(help_menuItem01);
		startMenu_help.addSeparator(); 											//��ӷָ���
		startMenu_help.add(help_menuItem02);
		
		*/
		
		Insets insets =getInsets();
		int x,y,tempW,tempH;													//��Ϸ���ڴ�С
		tempW=WIDTH_all+insets.left+insets.right;									
		tempH=HEIGHT_all+insets.top+insets.bottom;				
		
		x=(Toolkit.getDefaultToolkit().getScreenSize().width-tempW)/2;
		y=(Toolkit.getDefaultToolkit().getScreenSize().height-tempH)/2;
		setBounds(x,y,tempW,tempH);
		
		//���ô��������Ϊ��ɼ���
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setVisible(true);
				
		
		this.addKeyListener(this);//��ӹ�����
		
		loginPanel=new LoginPanel(this);
		winPanel=new WinPanel(this);
		overPanel=new OverPanel(this);
		//gamePanel=new GamePanel(this,loginPanel);
		//gamePanel_2p=new GamePanel_2P(this,loginPanel);
		login();
		
		//�̹߳���
		Thread tankThread;
		tankThread = new Thread(new myThread());
		tankThread.start();
		
		
		
		//mainframe end//
	}

	
	
	
	private class myThread implements Runnable{
		@Override 
		public void run() {
			for(int i=0;;i++) {
				try {
					Thread.sleep(30*timeSpeed);
					//System.out.println(time%10);
					if(stop==0) {//��ͣ����stop=0��������stop=1ֹͣ����
						time++;
						repaint();
					}
				}catch(InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	public int getTime() {				//��ü�ʱ��
		return time;
	}
	public void initTime() {			//��ʱ������
		time=0;
	}
	public void setTime(int time) {
		this.time=time;					//���ü�ʱ��ʱ��
	}
	
	public void login() {
		loginPanel.initLogin();
		this.setContentPane(loginPanel);
		this.addKeyListener(loginPanel);
		initTime();
		this.revalidate();
	}
	
	
	public void startgame_1p() {
		this.model=1;									//����Ϊ����ģʽ��ʶ
		gamePanel=null;									//���ģʽһ
		gamePanel_2p=null;								//���ģʽ��
		gamePanel=new GamePanel(this,loginPanel);
		this.setContentPane(gamePanel);
		this.addKeyListener(gamePanel);
		initTime();
		this.revalidate();
	}
	
	public void startgame_2p() {
		this.model=2;									//����Ϊ˫��ģʽ��ʶ
		gamePanel=null;									//���ģʽһ
		gamePanel_2p=null;								//���ģʽ��
		gamePanel_2p=new GamePanel_2P (this,loginPanel);
		this.setContentPane(gamePanel_2p);
		this.addKeyListener(gamePanel_2p);
		initTime();
		this.revalidate();
	}
	
	public void startGameWin() {
		this.setContentPane(winPanel);
		this.addKeyListener(winPanel);
		winPanel.initWinPanel();
		this.revalidate();
	}

	public void startGameOver() {
		this.setContentPane(overPanel);
		this.addKeyListener(overPanel);
		overPanel.initOverPanel();
		this.revalidate();
	}
	
	public void setStop(int stop) {//�ı���ͣģʽ
		this.stop=stop;
	}
	
	
	@Override
	//������ͣ����stop=0��������stop=1ֹͣ����
	public void keyPressed(KeyEvent e) {
		int a=e.getKeyCode();
		if(a==KeyEvent.VK_Q) {//��ͣ
			setStop(1);
			System.out.println("����");
		}
		if(a==KeyEvent.VK_E) {//����
			setStop(0);
			System.out.println("����");
		}
		
	}
	@Override
	public void keyReleased(KeyEvent e) {
		
	}
	@Override
	public void keyTyped(KeyEvent e) {//�û�Q����ͣ
	}
	
	private class i_want implements ActionListener{
		@Override
		public void actionPerformed(ActionEvent e) {
			new I_want();
		}
	}
	
	
	//CLASS END//
}

















