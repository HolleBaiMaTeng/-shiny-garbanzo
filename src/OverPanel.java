import java.awt.Graphics;
import java.awt.Image;
import java.awt.LayoutManager;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JPanel;



public class OverPanel extends JPanel implements KeyListener {

	private MainFrame mainFrame;
	private Image img;									//����ͼƬ��Դ
	
	private PlayerTank playertank;
	private int choice;									//ѡ�����
	private int[][] tankPos= {{280,352},{280,380}};		//ѡ�������λ��
	
	
	
	public OverPanel(MainFrame mainFrame){ 
		super();
		this.mainFrame=mainFrame;
		File f=new File("game_over.png");				//�����ļ�����
		try {
			img=ImageIO.read(f);						//���ļ��ж�ȡͼƬ
		}catch(IOException e) {
			e.printStackTrace();
		}
		
		choice=1;
		playertank=new PlayerTank(tankPos[choice][0],tankPos[choice][1],0);
		playertank.setDiraction(Spirit.RIGHT);
		
		
	}
	
	public void paint(Graphics g) {
		//���뱳��ͼƬ
		g.drawImage(img, 0, 0, MainFrame.WIDTH, MainFrame.HEIGHT, 0, 0,  MainFrame.WIDTH, MainFrame.HEIGHT, null);
		//����̹��
		ImageUtil.getInstance().drawPlayerTank(g, playertank);
	}
	
	
	
	public void initOverPanel() {
		choice=0;
	}
	
	
	
	@Override
	public void keyPressed(KeyEvent arg0) {
		int key=arg0.getKeyCode();
		switch(key){
			case KeyEvent.VK_ENTER:
				mainFrame.removeKeyListener(this);
				mainFrame.removeKeyListener(this);
				if(mainFrame.model==1) {					//������Ϸģʽ���¿�ʼ��Ϸ
					mainFrame.startgame_1p();
				}else if(mainFrame.model==2) {
					mainFrame.startgame_2p();
				}
				break;
			case KeyEvent.VK_ESCAPE:
				mainFrame.removeKeyListener(this);
				mainFrame.login();
				break;
		}
	}

	
	
	
	
	
	
	
	
	
	
	
	/**************************************************************************************************/
	@Override
	public void keyReleased(KeyEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}

	
	
	//class end//
}
