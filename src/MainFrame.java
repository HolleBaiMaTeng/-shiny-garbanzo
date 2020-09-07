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
	 * 需要解决的问题：2.读秒计算优化3.窗口优化
	 * 
	 * 
	 * 
	 * 
	 */
	public static final int WIDTH=800;				//画面区域宽
	public static final int HEIGHT=600;				//画面区域高
	public static final int WIDTH_all=800;			//窗口总宽
	public static final int HEIGHT_all=690;			//窗口总高
	public static final int P1GAME=1;
	public static final int P2GAME=2;
	private LoginPanel loginPanel;
	private GamePanel gamePanel;
	private GamePanel_2P gamePanel_2p;
	private WinPanel winPanel;
	private OverPanel overPanel;
	
	public int timeSpeed=1;
	
	public int model=1;						//用于记录现在的游戏模式：1-单人游戏 2-双人游戏
	
	//AudioPlayer player;
	
	private int time=0;                     //创建游戏计时器（攻击间隔，加速，结束游戏延迟）
	private int stop=0;						//创建暂停器，stop=0正常运行stop=1停止运行
	public MainFrame(String string) {
		super("tankgame");//窗体名称
		setResizable(false);
		
		
		this.setBackground(Color.BLACK);
		
		/*(不方便进行设置)
		//主窗体设置：
		JMenuBar menuBar=new JMenuBar();			//新建一个菜单条
		this.setJMenuBar(menuBar);					//添加至本窗体
		
		JMenu startMenu_about=new JMenu("about");		//新建一个菜单条
		JMenu startMenu_help=new JMenu("help");			//新建一个菜单条
		JMenu startMenu_tool=new JMenu("tool");			//新建一个菜单条
		menuBar.add(startMenu_about);					//添加关于菜单
		menuBar.add(startMenu_help);					//添加帮助菜单
		menuBar.add(startMenu_tool);					//添加帮助菜单
		
		JMenuItem help_menuItem01=new JMenuItem("Operating instructions");		//操作说明
		JMenuItem help_menuItem02=new JMenuItem("Coordinate instructions");		//坐标说明
		JMenuItem about_menuItem01=new JMenuItem("want");						//我想做什么
		JMenuItem tool_menuItem01=new JMenuItem("World Map");					//世界坐标
		
		about_menuItem01.addActionListener(new i_want());						//添加监听器
		
		startMenu_about.add(about_menuItem01);
		startMenu_help.add(help_menuItem01);
		startMenu_help.addSeparator(); 											//添加分割条
		startMenu_help.add(help_menuItem02);
		
		*/
		
		Insets insets =getInsets();
		int x,y,tempW,tempH;													//游戏窗口大小
		tempW=WIDTH_all+insets.left+insets.right;									
		tempH=HEIGHT_all+insets.top+insets.bottom;				
		
		x=(Toolkit.getDefaultToolkit().getScreenSize().width-tempW)/2;
		y=(Toolkit.getDefaultToolkit().getScreenSize().height-tempH)/2;
		setBounds(x,y,tempW,tempH);
		
		//设置窗体结束行为与可见性
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setVisible(true);
				
		
		this.addKeyListener(this);//添加管理器
		
		loginPanel=new LoginPanel(this);
		winPanel=new WinPanel(this);
		overPanel=new OverPanel(this);
		//gamePanel=new GamePanel(this,loginPanel);
		//gamePanel_2p=new GamePanel_2P(this,loginPanel);
		login();
		
		//线程管理
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
					if(stop==0) {//暂停器，stop=0正常运行stop=1停止运行
						time++;
						repaint();
					}
				}catch(InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	public int getTime() {				//获得计时器
		return time;
	}
	public void initTime() {			//计时器归零
		time=0;
	}
	public void setTime(int time) {
		this.time=time;					//设置计时器时间
	}
	
	public void login() {
		loginPanel.initLogin();
		this.setContentPane(loginPanel);
		this.addKeyListener(loginPanel);
		initTime();
		this.revalidate();
	}
	
	
	public void startgame_1p() {
		this.model=1;									//更换为单人模式标识
		gamePanel=null;									//清空模式一
		gamePanel_2p=null;								//清空模式二
		gamePanel=new GamePanel(this,loginPanel);
		this.setContentPane(gamePanel);
		this.addKeyListener(gamePanel);
		initTime();
		this.revalidate();
	}
	
	public void startgame_2p() {
		this.model=2;									//更换为双人模式标识
		gamePanel=null;									//清空模式一
		gamePanel_2p=null;								//清空模式二
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
	
	public void setStop(int stop) {//改变暂停模式
		this.stop=stop;
	}
	
	
	@Override
	//创建暂停器，stop=0正常运行stop=1停止运行
	public void keyPressed(KeyEvent e) {
		int a=e.getKeyCode();
		if(a==KeyEvent.VK_Q) {//暂停
			setStop(1);
			System.out.println("捕获");
		}
		if(a==KeyEvent.VK_E) {//继续
			setStop(0);
			System.out.println("捕获");
		}
		
	}
	@Override
	public void keyReleased(KeyEvent e) {
		
	}
	@Override
	public void keyTyped(KeyEvent e) {//敲击Q键暂停
	}
	
	private class i_want implements ActionListener{
		@Override
		public void actionPerformed(ActionEvent e) {
			new I_want();
		}
	}
	
	
	//CLASS END//
}

















