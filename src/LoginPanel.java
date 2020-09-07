import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.LayoutManager;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

public class LoginPanel extends JPanel implements KeyListener {
	
	private MainFrame mainFrame;
	private Image img; 				//login图片资源
	
	private PlayerTank playertank1;
	private PlayerTank playertank2;
	private int choice=0;
	private int[][] tank1Pos= {{300,352},{300,380},{300,408}};
	private int[][] tank2Pos= {{525,352},{540,380},{600,408}};
	public int player_1p_tankmodel=0;
	public int player_2p_tankmodel=0;
	
	
	
	public LoginPanel(MainFrame mainFrame) {
		super();
		this.mainFrame=mainFrame;
		File f=new File("login.png");
		try {
			img=ImageIO.read(f);
		}catch(IOException e) {
			e.printStackTrace();
		}
		playertank1=new PlayerTank(tank1Pos[choice][0],tank1Pos[choice][1],player_1p_tankmodel);
		playertank2=new PlayerTank(tank2Pos[choice][0],tank1Pos[choice][1],player_2p_tankmodel);
		playertank1.setDiraction(Spirit.RIGHT);
		playertank2.setDiraction(Spirit.LEFT);
	}
	
	public void initLogin() {
		choice=0;
	}
	
	public void paint (Graphics g) {
		//显示数据
		g.setFont(new Font("TimesRoman",Font.PLAIN,20));
		g.drawLine(0, 600, 800, 600);
		g.drawString("player_1p_tankparameter:", 10, 620);
		g.drawString("player_2p_tankparameter:", 10, 645);
		g.drawString("Ability : "+playertank1.JNname, 300, 620);
		g.drawString("Ability : "+playertank2.JNname, 300, 645);
		g.drawString("FirePower : "+playertank1.firepower, 510, 620);
		g.drawString("FirePower : "+playertank2.firepower, 510, 645);
		g.drawString("Speed: "+playertank1.getVelocity(), 700, 620);
		g.drawString("Speed: "+playertank2.getVelocity(), 700, 645);
		//由choice值变换坦克坐标
		playertank1.setX(tank1Pos[choice][0]);
		playertank1.setY(tank1Pos[choice][1]);
		playertank2.setX(tank2Pos[choice][0]);
		playertank2.setY(tank2Pos[choice][1]);
		
		g.drawImage(img, 0, 0, MainFrame.WIDTH, MainFrame.HEIGHT,
				0, 0,  MainFrame.WIDTH, MainFrame.HEIGHT, null);
		ImageUtil.getInstance().drawPlayerTank(g, playertank1);
		ImageUtil.getInstance().drawPlayerTank(g, playertank2);
		
		playertank1.caculateDate();
		playertank2.caculateDate();
	}
	
	
	
	
	@Override
	public void keyPressed(KeyEvent arg0) {
		int key=arg0.getKeyCode();
		switch(key) {
		case KeyEvent.VK_ENTER:
			if(choice==0) {//P1或者P2游戏模式则进入
				mainFrame.removeKeyListener(this);
				mainFrame.startgame_1p();
				mainFrame.revalidate();
				break;
			}
			if(choice==1) {//P1或者P2游戏模式则进入
				mainFrame.removeKeyListener(this);
				mainFrame.startgame_2p();
				mainFrame.revalidate();
				break;
			}
			if(choice==2) {
				//回归选择一（加入其它功能）
				choice=0;
				break;
			}
			break;
			
		case KeyEvent.VK_UP:
			choice=(choice+2)%3;
			break;
		case KeyEvent.VK_DOWN:
			choice=(choice+1)%3;
			break;
		case KeyEvent.VK_LEFT:
			player_2p_tankmodel=(player_2p_tankmodel+2)%3;
			playertank2.changePlayerTankModel(player_2p_tankmodel);
			break;
		case KeyEvent.VK_RIGHT:
			player_2p_tankmodel=(player_2p_tankmodel+1)%3;
			playertank2.changePlayerTankModel(player_2p_tankmodel);
			break;
		case KeyEvent.VK_A:
			player_1p_tankmodel=(player_1p_tankmodel+2)%3;
			playertank1.changePlayerTankModel(player_1p_tankmodel);
			break;
		case KeyEvent.VK_D:
			player_1p_tankmodel=(player_1p_tankmodel+1)%3;
			playertank1.changePlayerTankModel(player_1p_tankmodel);
			break;
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {

		
	}

	@Override
	public void keyTyped(KeyEvent e) {

		
	}
	


}
