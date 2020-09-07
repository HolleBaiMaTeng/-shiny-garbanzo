import java.awt.Graphics;
import java.awt.Image;
import java.awt.LayoutManager;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

public class WinPanel extends JPanel implements KeyListener {

	private MainFrame mainFrame;
	private Image img;
	
	private PlayerTank playertank;
	private int choice=0;
	private int[][] tankPos= {{280,352},{280,380}};
	
	
	
	public WinPanel(MainFrame mainFrame) {
		super();
		this.mainFrame=mainFrame;
		File f=new File("game_win.png");//�����ļ�����
		try {
			img=ImageIO.read(f);
		}catch(IOException e) {
			e.printStackTrace();
		}
		
		playertank=new PlayerTank(tankPos[choice][0],tankPos[choice][1],0);
		playertank.setDiraction(Spirit.RIGHT);
	}

	
	public void initWinPanel() {
		this.choice=0;//ѡ�����λ
	}
	
	public void paint(Graphics g) {
		//���뱳��ͼƬ
		g.drawImage(img, 0, 0, MainFrame.WIDTH, MainFrame.HEIGHT, 0, 0,  MainFrame.WIDTH, MainFrame.HEIGHT, null);
		//����̹��
		ImageUtil.getInstance().drawPlayerTank(g, playertank);
	}
	
	@Override
	public void keyPressed(KeyEvent arg0) {
		int key=arg0.getKeyCode();
		switch(key) {
		case KeyEvent.VK_ENTER:
			mainFrame.removeKeyListener(this);				//�Ƴ����м�����
			if(choice==0) {
				mainFrame.login();                          //������һ��
			}
			if(choice==1) {
				mainFrame.startgame_1p(); 					//���¿�ʼ��ǰ�ؿ�
			}
			break;
		case KeyEvent.VK_UP:
			choice=(choice+1)%2;
			playertank.setX(tankPos[choice][0]);
			playertank.setY(tankPos[choice][1]);
			break;
		case KeyEvent.VK_DOWN:
			choice=(choice+1)%2;
			playertank.setX(tankPos[choice][0]);
			playertank.setY(tankPos[choice][1]);
			break;
		case KeyEvent.VK_ESCAPE:
			mainFrame.removeKeyListener(this);
			mainFrame.login();
			break;
		}
	}
	
	
	
	
	
	
	
	
	public WinPanel(LayoutManager arg0) {
		super(arg0);
		// TODO Auto-generated constructor stub
	}

	public WinPanel(boolean arg0) {
		super(arg0);
		// TODO Auto-generated constructor stub
	}

	public WinPanel(LayoutManager arg0, boolean arg1) {
		super(arg0, arg1);
		// TODO Auto-generated constructor stub
	}

	
	@Override
	public void keyReleased(KeyEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void keyTyped(KeyEvent arg0) {
		// TODO Auto-generated method stub

	}

}
