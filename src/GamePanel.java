import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;
import java.util.StringTokenizer;

import javax.swing.JPanel;



public class GamePanel extends JPanel implements KeyListener{
	
	/*
	 * Ҫ��������⣺
	 * 1.����̹��������࣬Ȩֵ�ж�
	 * 2.002�ڵ�����
	 * 3.
	 * 
	 * 
	 * ʤ��Ŀ�꣬�������ĵ�ͼ�������Ӧ����
	 * 
	 * 
	 */
	
	private MainFrame mainFrame;
	private LoginPanel loginPanel;
	
	private Image offscreenimage=null;				//�ڶ�����
	private Graphics goffscreen=null;
	
	private int timeSpeed=1;       					//������Ϸʱ�������ٶȣ�
	
    //Ԥ�Ƶ�ͼ���ļ�����ʽ��ȡ��������ͼ������㷨����
	private ArrayList<String> mapName=new ArrayList<String>();						//��ͼ��Ӧ���ļ���
	private int[][]  mapName_int;													//�����ͼ���е�����
	
	private int mapchange=0;					 									//0-��ͼδ�䶯1-��ͼ�䶯���䶯�ڼ�ʹ����ʾ״̬��
	private int maptime_jianbian=0;													//�л���ͼʱ�Ľ�������  0-��̬��ֵ 2n-1ȫ̬��ֵ
	private int mapblock[][];														//��ͼ��������
	private static final int JIANBIAN=25;											//�������
	private int bj=0;																//�߽���ײ�����¼
	
	private ArrayList<Map> World=new ArrayList<Map>();								//����
	private static final int WORLDWIDTH=30;											//������
	private static final int WORLDHEIGHT=30;										//����߶�
	 
	//���ܻ�����
	public static final int spirittank_miss=8;
	public static final int spirittank_see=1000;
	public static final int spirittank_distance=0;
	public int spirittank_distance002=4;
	
	//���ͼ�任�Ĺؿ�����
	private int mapX=0;							    //map�����������
	private int mapY=0;								//map������������
	private int worldNumber=0;						//������
	
	//playertank������һֱ����
	private PlayerTank playertank;									//���̹����ʼ���꣨350��400��
	private int playerTankState=1;									//���̹��״̬��1-������0-����(�ɸ���),-1-����(���ɸ���)
	private ArrayList<Cartoon> cartoons=new ArrayList<Cartoon>();	//���������б�

	//�������л�����
	private static final int GAMEWIN=1;
	private static final int GAMELOSE=2;
	private static final int cv0=30;                       //��Ϸ�����󻺳�ʱ��
	private int gameState=0;							   //��Ϸ״̬�ж�0-����1-ʤ��2-ʧ��
	
	
	
		//���캯��
		public GamePanel(MainFrame mainFrame,LoginPanel loginPanel)  {//�ֳ�����World.get(worldNumber)
			super();
			this.mapblock=new int[47][36];					//��ʼ����ͼ����״̬����
			this.mainFrame=mainFrame;
			this.loginPanel=loginPanel;
			//���̹�˳�ʼ��
			playertank=new PlayerTank(400,350,loginPanel.player_1p_tankmodel);
			
			//��ͼ����չ��ȡ
			FileReader fr=null;
			BufferedReader br = null;
			try {
				fr = new FileReader("mapPro.txt");
				br=new BufferedReader(fr);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
			try {
				String str1;
				str1=br.readLine();
				while(str1!=null) {
					mapName.add(str1);
					str1=br.readLine();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			//��mapName�е�������ȡ��mapName_int
			mapName_int=new int[mapName.size()][2];
			String str1;
			for(int i=0;i<mapName.size();i++) {
				str1=mapName.get(i);								//�ݴ��ļ���
				StringTokenizer st = new StringTokenizer(str1,".");//��.�ָ�
				mapName_int[i][0]=Integer.parseInt(st.nextToken());	//�ֱ�ֵ
				mapName_int[i][1]=Integer.parseInt(st.nextToken());	//�ֱ�ֵ
			}
			
			World.clear(); 							//�������
			initWorld();   							//�����ʼ��(����World���������������е�ͼ����)
													//ͬʱ�����ʼ�����꣬mapx,mapy,worldNumber
			
			
		}
		
		
		//��ͼpaint����
		public void paint(Graphics g) {
			
			/**���ü�ⲿ��**/
			//��ʾ����(�·��Ǳ���)
			g.drawLine(0, 600, 800, 600);
			g.setFont(new Font("TimesRoman",Font.PLAIN,20));
			String system_string=new String("tank_x : "+(playertank.getX()+800*this.mapX)+"  "+
											"tank_y : "+(600-playertank.getY()+600*this.mapY)+"        "+
											"world_x : "+this.mapX+"  "+
											"world_y : "+this.mapY);
			g.drawString(system_string, 10, 620);
			g.drawString("playerlife : "+World.get(worldNumber).playertankLife[0], 10, 645);
			g.drawString("|||destination-(X:15 , Y:15)|||", 540, 645);
			
			//��ʾ����(�Ҳ��Ǳ���)
			g.drawLine(800, 0, 800, 690);
			
			//��Ļ������
			//ʹ�õڶ����漼��
			if(offscreenimage==null) {
				offscreenimage=this.createImage(MainFrame.WIDTH, MainFrame.HEIGHT);
				goffscreen=offscreenimage.getGraphics();
				}
			//���ø��෽������ֹ�ӵײ��ػ�
			super.paint(goffscreen);
			
			//����Ϸ״̬���ж�
			if(mainFrame.getTime()==-1) {
				if(gameState==GAMEWIN) {//������û���Ƴ�
					mainFrame.removeKeyListener(this);
					mainFrame.startGameWin();
				}
				if(gameState==GAMELOSE) {//�������Ѿ��Ƴ�
					mainFrame.startGameOver();
				}
			}
			
			//�ִ�̹��״̬���
			if(World.get(worldNumber).playertankLife[1]<=0) {
				playerTankState=-1;	
			}
			/**���ü�ⲿ��**/
			
			
			if(mapchange==0) {										//����ʱΪ�ǵ�ͼ���״̬
				//��ͼ���㴦��
				this.DO_map001();
				this.DO_map002();
				this.DO_map003();
				this.DO_map004();
				this.DO_map005();
				this.DO_map006();
				this.DO_map007();
				this.DO_map008();
				this.DO_map009();
				
				//���������Ļ
				Color c=goffscreen.getColor();
				goffscreen.setColor(Color.BLACK);
				goffscreen.fillRect(0, 0, MainFrame.WIDTH, MainFrame.HEIGHT);
				goffscreen.setColor(c);
	
				
				//������
				for(int i=cartoons.size()-1;i>=0;i--) {
					Cartoon cartoon=cartoons.get(i);
					if(!cartoon.draw(goffscreen)) {
						cartoons.remove(i);
					}
				}
		
				
				//������ɾ���̹��000(��map�����ݾ����Ƿ�����)
				SpiritTank stank000=World.get(worldNumber).CreateSpirittank000();
				if(stank000!=null) {
					World.get(worldNumber).spirittank000s.add(stank000);
				}
				//������ɾ���̹��001(��map�����ݾ����Ƿ�����)
				SpiritTank001 stank001=World.get(worldNumber).CreateSpirittank001();
				if(stank001!=null) {
					World.get(worldNumber).spirittank001s.add(stank001);
				}
				//������ɾ���̹��002(��map�����ݾ����Ƿ�����)
				SpiritTank002 stank002=World.get(worldNumber).CreateSpirittank002();
				if(stank002!=null) {
					World.get(worldNumber).spirittank002s.add(stank002);
				}
				
				//�������̹��
				if(playerTankState==1) {							//���̹�˴��ʱ�ж�
					int goplayertank_1=1;
					int goplayertank_2=1;
					int goplayertank_3=1;
					//�Ƿ���ײ�߿�
					bj=playertank.isCollide_bianjie();				//�߽縳ֵ
				 	if(bj!=0) {//��ײ�˱߽磨world��ͼ���ܲ�����ǰ�ᣩ//ֱ�Ӹı��ͼ
				 		this.mapchange=1;							//��ͼ������ڱ���������ʾ״̬��
				 		this.maptime_jianbian=JIANBIAN*2-1;			//���ý������
				 	}
				
				 		//�Ƿ���ײ����̹��000(����ײ��ը)
				 		for(int i=World.get(worldNumber).spirittank000s.size()-1;i>=0;i--) {
				 			SpiritTank spirittank=World.get(worldNumber).spirittank000s.get(i);
				 			if(spirittank.tankCollide(playertank)) {
				 				playertankBOOM();//��������һ������̹��ͬ���ھ�-���Ӯ
				 				spirittank000BOOM(i);
				 				break;
				 			}
				 		}
				 		//�Ƿ���ײ����̹��001(����ײ��ը)
				 		for(int i=World.get(worldNumber).spirittank001s.size()-1;i>=0;i--) {
				 			SpiritTank001 spirittank001=World.get(worldNumber).spirittank001s.get(i);
				 			if(spirittank001.tankCollide(playertank)) {
				 				playertankBOOM();//��������һ������̹��ͬ���ھ�-���Ӯ
				 				spirittank001BOOM(i);
				 				break;
				 			}
				 		}
				 		//�Ƿ���ײ����̹��002(����ײ��ը)
				 		for(int i=World.get(worldNumber).spirittank002s.size()-1;i>=0;i--) {
				 			SpiritTank002 spirittank002=World.get(worldNumber).spirittank002s.get(i);
				 			if(spirittank002.tankCollide(playertank)) {
				 				playertankBOOM();//��������һ������̹��ͬ���ھ�-���Ӯ
				 				spirittank002BOOM(i);
				 				break;
				 			}
				 		}
				 		//�Ƿ���ײ��ͼ��
				 		if(World.get(worldNumber).isCollide(playertank)) {
				 			goplayertank_3=0;
				 		}else {
				 			goplayertank_3=1;
				 		}
				 		if(goplayertank_1*goplayertank_2*goplayertank_3==1) {
				 			playertank.move();
				 		}		
				 		if(this.timeSpeed==1) {
					 		playertank.draw(goffscreen);
					 	}else {
					 		playertank.draw_0(goffscreen);
					 	}
				 		playertank.caculateDate();						//���㺯���������֡����
				}
				
				//������̹��000
				for(int i=World.get(worldNumber).spirittank000s.size()-1;i>=0;i--) {
					SpiritTank stank00=World.get(worldNumber).spirittank000s.get(i);
					//ÿһ������̹�˽��д���
					 int gospirittank_1=1;
					 int gospirittank_2=1;
					 int gospirittank_3=1;
					//�Ƿ���ײ�߿�
						if(stank00.isCollide_bianjie()!=0) {
							gospirittank_1=0;
						}else {
							gospirittank_1=1;
						}
					//�Ƿ���ײ���̹�ˣ����̹�����Ѵ���
						
					//�Ƿ���ײ�˵�ͼ��
						if(World.get(worldNumber).isCollide(stank00)) {
							gospirittank_3=0;
						}else {
							gospirittank_3=1;
						}
	
					if(gospirittank_1*gospirittank_2*gospirittank_3==1) {
						stank00.move();
						
					}
					Bullet spiritbullet=stank00.fire();
					if(spiritbullet!=null) {
						World.get(worldNumber).spiritbullets.add(spiritbullet);
					}
					if(this.timeSpeed==1) {
						stank00.drawSpiritTank(goffscreen);
				 	}else {
				 		stank00.drawSpiritTank_0(goffscreen);
				 	}
					stank00.caculateDate();
				}
				
				//������̹��001
				for(int i=World.get(worldNumber).spirittank001s.size()-1;i>=0;i--) {
					SpiritTank001 stank01=World.get(worldNumber).spirittank001s.get(i);
					//ÿһ������̹�˽��д���
					 int gospirittank_1=1;
					 int gospirittank_2=1;
					 int gospirittank_3=1;
					//�Ƿ���ײ�߿�
						if(stank01.isCollide_bianjie()!=0) {
							gospirittank_1=0;
						}else {
							gospirittank_1=1;
						}
					//�Ƿ���ײ���̹�ˣ����̹�����Ѵ���
						
					//�Ƿ���ײ�˵�ͼ��
						if(World.get(worldNumber).isCollide(stank01)) {
							gospirittank_3=0;
						}else {
							gospirittank_3=1;
						}
	
					if(gospirittank_1*gospirittank_2*gospirittank_3==1) {
						stank01.move();
						
					}
					Bullet spiritbullet=stank01.fire();
					if(spiritbullet!=null) {
						World.get(worldNumber).spiritbullets.add(spiritbullet);
					}
					if(this.timeSpeed==1) {
						stank01.drawSpiritTank(goffscreen);
				 	}else {
				 		stank01.drawSpiritTank_0(goffscreen);
				 	}
					stank01.caculateDate001(World.get(worldNumber).mapmap_move_point001);
				}
				//������̹��002
				for(int i=World.get(worldNumber).spirittank002s.size()-1;i>=0;i--) {
					SpiritTank002 stank01=World.get(worldNumber).spirittank002s.get(i);
					//ÿһ������̹�˽��д���
					 int gospirittank_1=1;
					 int gospirittank_2=1;
					 int gospirittank_3=1;
					//�Ƿ���ײ�߿�
						if(stank01.isCollide_bianjie()!=0) {
							gospirittank_1=0;
						}else {
							gospirittank_1=1;
						}
					//�Ƿ���ײ���̹�ˣ����̹�����Ѵ���
						
					//�Ƿ���ײ�˵�ͼ��
						if(World.get(worldNumber).isCollide(stank01)) {
							gospirittank_3=0;
						}else {
							gospirittank_3=1;
						}
	
					
					if(this.timeSpeed==1) {
						stank01.drawSpiritTank(goffscreen);
				 	}else {
				 		stank01.drawSpiritTank_0(goffscreen);
				 	}
					//����һ�����㺯��
					stank01.caculateDate001(
							World.get(worldNumber).mapmap_move_point002,
							World.get(worldNumber).mapmap_move_point003,
							World.get(worldNumber).mapmap_move_point004,
							World.get(worldNumber).mapmap_move_point005,
							World.get(worldNumber).mapmap_move_point006,
							World.get(worldNumber).mapmap_move_point007,
							World.get(worldNumber).mapmap_move_point008,
							World.get(worldNumber).mapmap_move_point009,
							playertank.getX(),
							playertank.getY());
					//��������ƶ�����
					if(gospirittank_1*gospirittank_2*gospirittank_3==1) {
						stank01.move();
						
					}
					//��������������
					Bullet spiritbullet=stank01.fire();
					if(spiritbullet!=null) {
						World.get(worldNumber).spiritbullets.add(spiritbullet);
					}
				}
				
				
				//�����ͼ���
				if(this.timeSpeed==1) {
					World.get(worldNumber).drawBricks(goffscreen);
					World.get(worldNumber).drawStones(goffscreen);
					World.get(worldNumber).drawWaters(goffscreen);
			 	}else {
			 		World.get(worldNumber).drawBricks_0(goffscreen);
					World.get(worldNumber).drawStones_0(goffscreen);
					World.get(worldNumber).drawWaters_0(goffscreen);
			 	}
				
				
				
				//�������̹�˷�����ڵ�
				for(int i=World.get(worldNumber).playerbullets.size()-1;i>=0;i--) {
					Bullet playerbullet=World.get(worldNumber).playerbullets.get(i);
					
					//�ж��Ƿ���е�ͼ��
					if(World.get(worldNumber).isCollide(playerbullet)) {
						playerbulletBOOM(i);
						continue;
					}
					//�жϱ߽���ײ
					if(playerbullet.isCollide_bianjie()!=0) {
						playerbulletBOOM(i);
						continue;
					}
					//�ж��Ƿ���о���̹��000
					int had_1=0;//����δִ��
					for(int j=World.get(worldNumber).spirittank000s.size()-1;j>=0;j--) {
						SpiritTank spirittank000=World.get(worldNumber).spirittank000s.get(j);
						if(spirittank000.isCollide(playerbullet)) {
							spirittank000BOOM(j);
							playerbulletBOOM(i);
							had_1=1;
							break;
						}
					}
					if(had_1==1) {
						continue;
					}
					//�ж��Ƿ���о���̹��001
					for(int j=World.get(worldNumber).spirittank001s.size()-1;j>=0;j--) {
						SpiritTank001 spirittank001=World.get(worldNumber).spirittank001s.get(j);
						if(spirittank001.isCollide(playerbullet)) {
							spirittank001BOOM(j);
							playerbulletBOOM(i);
							had_1=1;
							break;
						}
					}
					if(had_1==1) {
						continue;
					}
					//�ж��Ƿ���о���̹��002
					for(int j=World.get(worldNumber).spirittank002s.size()-1;j>=0;j--) {
						SpiritTank002 spirittank002=World.get(worldNumber).spirittank002s.get(j);
						if(spirittank002.isCollide(playerbullet)) {
							spirittank002BOOM(j);
							playerbulletBOOM(i);
							had_1=1;
							break;
						}
					}
					if(had_1==1) {
						continue;
					}
					//�ж��Ƿ���о����ڵ�(�ڵ��ж�ʱ�����ж����)
					int had_2=0;//����δִ��
					for(int k=World.get(worldNumber).spiritbullets.size()-1;k>=0;k--) {
						Bullet spiritbullet=World.get(worldNumber).spiritbullets.get(k);
						spiritbullet.setWidth(Bullet.BULLET_W_SPECIAL);//�ж�ǰ�������ڵ�����30-w
						if(spiritbullet.isCollide(playerbullet)) {
							spiritbullet.setWidth(Bullet.BULLET_W_NORMAL);//�жϺ󽫾����ڵ�����2-w
							playerbulletBOOM(i);
							spiritbulletBOOM(k);
							had_2=1;
							break;
						}
						spiritbullet.setWidth(Bullet.BULLET_W_NORMAL);//�жϺ󽫾����ڵ�����2-w
					}
					if(had_2==1) {
						continue;
					}
					playerbullet.move();
					playerbullet.draw(goffscreen);
					
					//������Ҫ��ͬ��ʵʱ�任Ŀ���
					playerbullet.caculateDate();
				}
					
				
					
				
				
				
				//������̹�˷�����ڵ�
				for(int i=World.get(worldNumber).spiritbullets.size()-1;i>=0;i--) {
					Bullet spiritbullet=World.get(worldNumber).spiritbullets.get(i);
					//�жϱ߽���ײ
					if(spiritbullet.isCollide_bianjie()!=0) {
						spiritbulletBOOM(i);
						continue;
					}
					//�жϵ�ͼ��ײ
					if(World.get(worldNumber).isCollide(spiritbullet)) {
						spiritbulletBOOM(i);
						continue;
					}
					if(playerTankState==1) {					//���̹�˴����и��ж�
						if(playertank.isCollide(spiritbullet)) {//�����̹�˵���ײ���
							spiritbulletBOOM(i);	
							playertankBOOM();	
							continue;
						}
					}
					spiritbullet.move();
					spiritbullet.draw(goffscreen);
					spiritbullet.caculateDate();
				}
	
				
				if(this.timeSpeed==1) {
					//����ݿ�
					World.get(worldNumber).drawGrasses(goffscreen);
					//���뾫���
					World.get(worldNumber).drawSpirits(goffscreen);
				}else {
					//����ݿ�
					World.get(worldNumber).drawGrasses_0(goffscreen);
					//���뾫���
					World.get(worldNumber).drawSpirits_0(goffscreen);
				}
				
				//�ݶȺ�����ԭ����(�ݶȻ�ԭ�ı����ǽ���1���ֻ�Ϊ0)
				//�ƶ��ݶȺ�����ԭ001
				for(int i=0;i<=46;i++) {
					for(int j=0;j<=35;j++) {
						if(World.get(worldNumber).mapmap_move_point001[i][j]!=1) {
							World.get(worldNumber).mapmap_move_point001[i][j]=0;
						}
					}
				}
				//�ƶ��ݶȺ�����ԭ002
				for(int i=0;i<=46;i++) {
					for(int j=0;j<=35;j++) {
						if(World.get(worldNumber).mapmap_move_point002[i][j]!=1) {
							World.get(worldNumber).mapmap_move_point002[i][j]=0;
						}
					}
				}
				//�ƶ��ݶȺ�����ԭ003
				for(int i=0;i<=46;i++) {
					for(int j=0;j<=35;j++) {
						if(World.get(worldNumber).mapmap_move_point003[i][j]!=1) {
							World.get(worldNumber).mapmap_move_point003[i][j]=0;
						}
					}
				}
				//�ƶ��ݶȺ�����ԭ004
				for(int i=0;i<=46;i++) {
					for(int j=0;j<=35;j++) {
						if(World.get(worldNumber).mapmap_move_point004[i][j]!=1) {
							World.get(worldNumber).mapmap_move_point004[i][j]=0;
						}
					}
				}
				//�ƶ��ݶȺ�����ԭ005
				for(int i=0;i<=46;i++) {
					for(int j=0;j<=35;j++) {
						if(World.get(worldNumber).mapmap_move_point005[i][j]!=1) {
							World.get(worldNumber).mapmap_move_point005[i][j]=0;
						}
					}
				}
				//�ƶ��ݶȺ�����ԭ006
				for(int i=0;i<=46;i++) {
					for(int j=0;j<=35;j++) {
						if(World.get(worldNumber).mapmap_move_point006[i][j]!=1) {
							World.get(worldNumber).mapmap_move_point006[i][j]=0;
						}
					}
				}
				//�ƶ��ݶȺ�����ԭ007
				for(int i=0;i<=46;i++) {
					for(int j=0;j<=35;j++) {
						if(World.get(worldNumber).mapmap_move_point007[i][j]!=1) {
							World.get(worldNumber).mapmap_move_point007[i][j]=0;
						}
					}
				}
				//�ƶ��ݶȺ�����ԭ008
				for(int i=0;i<=46;i++) {
					for(int j=0;j<=35;j++) {
						if(World.get(worldNumber).mapmap_move_point008[i][j]!=1) {
							World.get(worldNumber).mapmap_move_point008[i][j]=0;
						}
					}
				}
				//�ƶ��ݶȺ�����ԭ009
				for(int i=0;i<=46;i++) {
					for(int j=0;j<=35;j++) {
						if(World.get(worldNumber).mapmap_move_point009[i][j]!=1) {
							World.get(worldNumber).mapmap_move_point009[i][j]=0;
						}
					}
				}
				//��ͼ�Ǳ��״̬����
			}
			
			
			
			if(mapchange==1) {//����ͼ���
				this.drawALL();										//���Ƶ�ͼ����
				Random random=new Random();							//�����������
				if(maptime_jianbian==this.JIANBIAN*2-1) {			//�������ʼ״̬
																	//���й���һ
					for(int i=0;i<=46;i++) {						//���и�ֵ
						for(int j=0;j<=35;j++) {
							mapblock[i][j]=random.nextInt(this.JIANBIAN);
						}
					}
				}
				if(maptime_jianbian==this.JIANBIAN-1) {				//���������ɹ���һ
																	//���й��̶�
					this.changeBJ();								//������ͼ
			 		for(int i=0;i<=46;i++) {						//���и�ֵ
						for(int j=0;j<=35;j++) {
							mapblock[i][j]=random.nextInt(this.JIANBIAN);
						}
					}
				}
				if(maptime_jianbian<=this.JIANBIAN*2-1&&maptime_jianbian>=this.JIANBIAN) {
					for(int q=0;q<2*this.JIANBIAN-maptime_jianbian;q++) {				//�����Ӻ�ɫ����
						for(int i=0;i<=46;i++) {										//���и�ֵ
							for(int j=0;j<=35;j++) {
								if(mapblock[i][j]==q) {
									goffscreen.fill3DRect(i*17, j*17, 17, 17, true);
									//ImageUtil.getInstance().drawStone(goffscreen, i*17, j*17);
								}
							}
						}
					}
				}
				if(maptime_jianbian<this.JIANBIAN) {
					for(int q=0;q<maptime_jianbian;q++) {				//�𽥼��ٺ�ɫ����
						for(int i=0;i<=46;i++) {										//���и�ֵ
							for(int j=0;j<=35;j++) {
								if(mapblock[i][j]==q) {
									goffscreen.fill3DRect(i*17, j*17, 17, 17, true);
									//ImageUtil.getInstance().drawStone(goffscreen, i*17, j*17);
								}
							}
						}
					}
				}
				maptime_jianbian--;									//�л���һ֡
				//��ͼ���״̬����
				if(maptime_jianbian==0) {							//��β�ж�
					this.mapchange=0;								//����Ϊ��ͼ����״̬
				}
			}
			
			//һ���Ի���
			g.drawImage(offscreenimage, 0, 0,null);
			//paint end//
		}
		
		
		
		
		
		
		
		/***************************************************************************/
		
		private void initWorld() {//���¼�������
			//�����ʼ��
			for(int i=0;i<WORLDWIDTH;i++) {//i������
				for(int j=0;j<WORLDWIDTH;j++) {//j������
					//�ж��Ƿ���Ҫ��������(mapNmae_int�������Ƿ�һ��)
					int lingpai=0;										//��������
					for(int k=0;k<mapName.size();k++) {
						if(mapName_int[k][0]==i&&mapName_int[k][1]==j) {//��һ�����ȡ��Ӧ�ļ�
							Map map_x1=new Map(i,j);					//x1�͵�ͼΪ��ȡ�ļ��͵�ͼ
							try {
								map_x1.readData(mapName.get(k)); 			//��ȡ��Ӧ���ֵĵ�ͼ
							}catch(IOException e) {
								e.printStackTrace();
							}
							
							/****���Ƹ�����ͼִ���������****/
							if(true) {
								this.maprules(map_x1);
							}
							if(false) {
								this.mapRandom_x1(map_x1,2);			//�������
							}
							
							maprules(map_x1);							//��׼���淶
							map_x1.copy_map_gradient(map_x1.mapmap);	//��Ӧ�ƶ��ݶȱ仯
							this.mapfill(map_x1);						//���ֻ���ͼת��ʵ�廯��ͼ
							World.add(map_x1);							//д��������
							lingpai=1;									//��������ļ��������������
							System.out.println("World:("+i+
							","+j+") ready"+
							" Finished "+(i*WORLDWIDTH+j+1)*100/
							(WORLDWIDTH*WORLDWIDTH)+"%");				//��������㱨
							break;										//����ѭ�ж���һ����
						}
					}
					if(lingpai==0) {									//����Ϊ��ʱ�ж�
						Map map_x2=new Map(i,j);						//�����
						
						Random random=new Random();
						mapRandom_x2(map_x2,2-random.nextInt(2));							//�����������
						
						//������������
						this.ChangeBlockMake(map_x2);					//������ͼ����
						
						maprules(map_x2);								//��׼���淶
						
						map_x2.copy_map_gradient(map_x2.mapmap);		//��Ӧ�ƶ��ݶȱ仯
						mapfill(map_x2);								//���ֻ���ͼת��ʵ�廯��ͼ
						
						System.out.println("World:("+i+	
								","+j+") ready"+
								" Finished "+(i*WORLDWIDTH+j+1)*100/
								(WORLDWIDTH*WORLDWIDTH)+"%");			//��������㱨
						World.add(map_x2);								//д��������
					}
				}
			}
			
			
			//�����ֿ��ܵĳ����������0-ˮ��1-ɭ�֣�2-ʯ��3-��
			Random random=new Random();
			switch(random.nextInt(4)) {
			case 0:
				this.worldNumber=WORLDHEIGHT-1;								//���Ͻ�-ˮ
				break;
			case 1:
				this.worldNumber=WORLDHEIGHT*WORLDWIDTH-1;					//���Ͻ�-ɭ��
				break;
			case 2:
				this.worldNumber=WORLDHEIGHT*WORLDWIDTH-WORLDHEIGHT;		//���½�-ʯ
				break;
			case 3:
				this.worldNumber=0;											//���½�-��
			}
			/*
			//ԭʼ������
			this.worldNumber=0;
			*/
			this.mapX=World.get(worldNumber).getMapX();
			this.mapY=World.get(worldNumber).getMapY();
			this.worldNumber=mapX*WORLDHEIGHT+mapY;
		}
		
		private void mapRandom_x2(Map map,int qmax) {		//����������map��ͼ//��ȫ�հ�
			Random random=new Random();
			int qqq=random.nextInt(2);
			if(qqq==0){
				this.eightMapMake(map,qmax);
				//this.fourMapMake(map);
			}else {
				this.fourMapMake(map,qmax+2);				//��֤����Ч������һ��
				//this.eightMapMake(map);
			}
		}
		
		private void maprules(Map map) {
			//�淶����World�߽�(�߽紦��)
			if(map.getMapX()==0) {					//��World��磬����߽�Ϊˮ
				for(int i=0;i<=35;i++) {
					map.mapmap[0][i]=Map.STONE;
				}
			}
			if(map.getMapX()==WORLDWIDTH-1) {		//��World�ҽ磬���ұ߽�Ϊˮ
				for(int i=0;i<=35;i++) {
					map.mapmap[46][i]=Map.STONE;
				}
			}
			if(map.getMapY()==0) {					//��World�½磬���±߽�Ϊʯ
				for(int i=0;i<=46;i++) {
					map.mapmap[i][35]=Map.STONE;
				}
			}
			if(map.getMapY()==WORLDHEIGHT-1) {		//��World�Ͻ磬���ϱ߽�Ϊʯ
				for(int i=0;i<=46;i++) {
					map.mapmap[i][0]=Map.STONE;
				}
			}
		}
		
		private void mapRandom_x1(Map map,int qmax) {       		//����������map��ͼ//��ԭ�л����Ͻ������
			Random random=new Random();
			//��mapamp��Ϊԭʼģʽ�����ı�ԭ�б߽磩
			for(int i=0;i<=46;i++) {
				for(int j=0;j<=35;j++) {
					if(map.mapmap[i][j]!=Map.BRICK&&
							map.mapmap[i][j]!=Map.GRAESS&&
							map.mapmap[i][j]!=Map.STONE&&
							map.mapmap[i][j]!=Map.WATER&&
							map.mapmap[i][j]!=Map.SPIRIT1) {
						map.mapmap[i][j]=random.nextInt(50)+1;//��������ͼ��
					}
				}
			}
			for(int q=0;q<qmax;q++) {//ִ�иò����Ĵ���
				for(int i=1;i<=45;i++) {//ͬ�������ͼ
					for(int j=1;j<=34;j++) {
						if(map.mapmap[i][j]==Map.BRICK||
							map.mapmap[i][j]==Map.WATER||
							map.mapmap[i][j]==Map.STONE||
							map.mapmap[i][j]==Map.GRAESS) {
							if(qmax%2==0) {
								if(map.mapmap[i-1][j]!=Map.SPIRIT1&&map.mapmap[i-1][j]!=Map.SPIRIT0) {
									if(mol(map.mapmap[i][j],map.mapmap[i-1][j])>5) {//ͬ�������ͼ
										map.mapmap[i-1][j]=map.mapmap[i][j];
									}
								}
								if(map.mapmap[i][j-1]!=Map.SPIRIT1&&map.mapmap[i][j-1]!=Map.SPIRIT0) {
									if(mol(map.mapmap[i][j],map.mapmap[i][j-1])>5) {//ͬ�������ͼ
										map.mapmap[i][j-1]=map.mapmap[i][j];
									}
								}
								if(map.mapmap[i+1][j]!=Map.SPIRIT1&&map.mapmap[i+1][j]!=Map.SPIRIT0) {
									if(mol(map.mapmap[i+1][j],map.mapmap[i+1][j])>5) {//ͬ�������ͼ
										map.mapmap[i+1][j]=map.mapmap[i][j];
									}
								}
								if(map.mapmap[i][j+1]!=Map.SPIRIT1&&map.mapmap[i][j+1]!=Map.SPIRIT0) {
									if(mol(map.mapmap[i][j+1],map.mapmap[i][j+1])>5) {//ͬ�������ͼ
										map.mapmap[i][j+1]=map.mapmap[i][j];
									}
								}
							}else {
								if(map.mapmap[i-1][j]!=Map.SPIRIT1&&map.mapmap[i-1][j]!=Map.SPIRIT0) {
									if(mol(map.mapmap[i][j],map.mapmap[i-1][j])>5) {//ͬ�������ͼ
										map.mapmap[i-1][j]=map.mapmap[i][j];
									}
								}
								if(map.mapmap[i][j-1]!=Map.SPIRIT1&&map.mapmap[i][j-1]!=Map.SPIRIT0) {
									if(mol(map.mapmap[i][j],map.mapmap[i][j-1])>24) {//ͬ�������ͼ
										map.mapmap[i][j-1]=map.mapmap[i][j];
									}
								}
								if(map.mapmap[i-1][j]!=Map.SPIRIT1&&map.mapmap[i-1][j]!=Map.SPIRIT0) {
									if(mol(map.mapmap[i+1][j],map.mapmap[i+1][j])>25) {//ͬ�������ͼ
										map.mapmap[i+1][j]=map.mapmap[i][j];
									}
								}
								if(map.mapmap[i][j+1]!=Map.SPIRIT1&&map.mapmap[i][j+1]!=Map.SPIRIT0) {
									if(mol(map.mapmap[i][j+1],map.mapmap[i][j+1])>15) {//ͬ�������ͼ
										map.mapmap[i][j+1]=map.mapmap[i][j];
									}
								}
							}
						}
					}
				}
			}
		}
		
		private void fourMapMake(Map map,int qmax) {   				//�ĸ����������ͼ
			Random random=new Random();
			//��mapamp��Ϊԭʼģʽ
			for(int i=0;i<=46;i++) {
				for(int j=0;j<=35;j++) {
					map.mapmap[i][j]=random.nextInt(50)+1;//��������ͼ��
				}
			}
			for(int q=0;q<qmax;q++) {//ִ�иò����Ĵ���
				for(int i=1;i<=45;i++) {//ͬ�������ͼ
					for(int j=1;j<=34;j++) {
						if(map.mapmap[i][j]==Map.BRICK||
							map.mapmap[i][j]==Map.WATER||
							map.mapmap[i][j]==Map.STONE||
							map.mapmap[i][j]==Map.GRAESS) {
							if(qmax%2==0) {
								if(map.mapmap[i-1][j]!=Map.SPIRIT1&&map.mapmap[i-1][j]!=Map.SPIRIT0) {
									if(mol(map.mapmap[i][j],map.mapmap[i-1][j])>5) {//ͬ�������ͼ
										map.mapmap[i-1][j]=map.mapmap[i][j];
									}
								}
								if(map.mapmap[i][j-1]!=Map.SPIRIT1&&map.mapmap[i][j-1]!=Map.SPIRIT0) {
									if(mol(map.mapmap[i][j],map.mapmap[i][j-1])>5) {//ͬ�������ͼ
										map.mapmap[i][j-1]=map.mapmap[i][j];
									}
								}
								if(map.mapmap[i+1][j]!=Map.SPIRIT1&&map.mapmap[i+1][j]!=Map.SPIRIT0) {
									if(mol(map.mapmap[i+1][j],map.mapmap[i+1][j])>5) {//ͬ�������ͼ
										map.mapmap[i+1][j]=map.mapmap[i][j];
									}
								}
								if(map.mapmap[i][j+1]!=Map.SPIRIT1&&map.mapmap[i][j+1]!=Map.SPIRIT0) {
									if(mol(map.mapmap[i][j+1],map.mapmap[i][j+1])>5) {//ͬ�������ͼ
										map.mapmap[i][j+1]=map.mapmap[i][j];
									}
								}
							}else {
								if(map.mapmap[i-1][j]!=Map.SPIRIT1&&map.mapmap[i-1][j]!=Map.SPIRIT0) {
									if(mol(map.mapmap[i][j],map.mapmap[i-1][j])>5) {//ͬ�������ͼ
										map.mapmap[i-1][j]=map.mapmap[i][j];
									}
								}
								if(map.mapmap[i][j-1]!=Map.SPIRIT1&&map.mapmap[i][j-1]!=Map.SPIRIT0) {
									if(mol(map.mapmap[i][j],map.mapmap[i][j-1])>5) {//ͬ�������ͼ
										map.mapmap[i][j-1]=map.mapmap[i][j];
									}
								}
								if(map.mapmap[i-1][j]!=Map.SPIRIT1&&map.mapmap[i-1][j]!=Map.SPIRIT0) {
									if(mol(map.mapmap[i+1][j],map.mapmap[i+1][j])>5) {//ͬ�������ͼ
										map.mapmap[i+1][j]=map.mapmap[i][j];
									}
								}
								if(map.mapmap[i][j+1]!=Map.SPIRIT1&&map.mapmap[i][j+1]!=Map.SPIRIT0) {
									if(mol(map.mapmap[i][j+1],map.mapmap[i][j+1])>5) {//ͬ�������ͼ
										map.mapmap[i][j+1]=map.mapmap[i][j];
									}
								}
							}
						}
					}
				}
			}
			
			int Pointx;
			int Pointy;
			for(int i=0;i<6;i++) {//�����Ӿ���̹�˹���
				Pointx=random.nextInt(44)+1;
				Pointy=random.nextInt(33)+1;
				map.mapmap[Pointx][Pointy]=Map.SPIRIT1;
				map.mapmap[Pointx+1][Pointy]=Map.SPIRIT0;
				map.mapmap[Pointx][Pointy+1]=Map.SPIRIT0;
				map.mapmap[Pointx+1][Pointy+1]=Map.SPIRIT0;
			}
		}
		
		private void eightMapMake(Map map,int qmax) {				//�˸��漴������ͼ
			Random random=new Random();
			//��mapamp��Ϊԭʼģʽ
			for(int i=0;i<=46;i++) {
				for(int j=0;j<=35;j++) {
					map.mapmap[i][j]=random.nextInt(50)+1;//��������ͼ��
				}
			}
			for(int q=0;q<qmax;q++) {//ִ�иò����Ĵ���
				for(int i=1;i<=45;i++) {//ͬ�������ͼ
					for(int j=1;j<=34;j++) {
						if(map.mapmap[i][j]==Map.BRICK||
							map.mapmap[i][j]==Map.WATER||
							map.mapmap[i][j]==Map.STONE||
							map.mapmap[i][j]==Map.GRAESS) {
							//X������
							if(map.mapmap[i-1][j-1]!=Map.SPIRIT1&&map.mapmap[i-1][j-1]!=Map.SPIRIT0) {
								if(mol(map.mapmap[i][j],map.mapmap[i-1][j-1])>25) {//ͬ�������ͼ
									map.mapmap[i-1][j-1]=map.mapmap[i][j];
								}
							}
							if(map.mapmap[i-1][j+1]!=Map.SPIRIT1&&map.mapmap[i-1][j+1]!=Map.SPIRIT0) {
								if(mol(map.mapmap[i][j],map.mapmap[i-1][j+1])>25) {//ͬ�������ͼ
									map.mapmap[i-1][j+1]=map.mapmap[i][j];
								}
							}
							if(map.mapmap[i+1][j-1]!=Map.SPIRIT1&&map.mapmap[i+1][j-1]!=Map.SPIRIT0) {
								if(mol(map.mapmap[i][j],map.mapmap[i+1][j-1])>25) {//ͬ�������ͼ
									map.mapmap[i+1][j-1]=map.mapmap[i][j];
								}
							}
							if(map.mapmap[i+1][j+1]!=Map.SPIRIT1&&map.mapmap[i+1][j+1]!=Map.SPIRIT0) {
								if(mol(map.mapmap[i][j],map.mapmap[i+1][j+1])>25) {//ͬ�������ͼ
									map.mapmap[i+1][j+1]=map.mapmap[i][j];
								}
							}
							//��ʮ��������
							if(map.mapmap[i-1][j]!=Map.SPIRIT1&&map.mapmap[i-1][j]!=Map.SPIRIT0) {
								if(mol(map.mapmap[i][j],map.mapmap[i-1][j])>25) {//ͬ�������ͼ
									map.mapmap[i-1][j]=map.mapmap[i][j];
								}
							}
							if(map.mapmap[i-1][j]!=Map.SPIRIT1&&map.mapmap[i-1][j]!=Map.SPIRIT0) {
								if(mol(map.mapmap[i][j],map.mapmap[i][j-1])>25) {//ͬ�������ͼ
									map.mapmap[i][j-1]=map.mapmap[i][j];
								}
							}
							if(map.mapmap[i+1][j]!=Map.SPIRIT1&&map.mapmap[i+1][j]!=Map.SPIRIT0) {
								if(mol(map.mapmap[i+1][j],map.mapmap[i+1][j])>25) {//ͬ�������ͼ
									map.mapmap[i+1][j]=map.mapmap[i][j];
								}
							}
							if(map.mapmap[i][j+1]!=Map.SPIRIT1&&map.mapmap[i][j+1]!=Map.SPIRIT0) {
								if(mol(map.mapmap[i][j+1],map.mapmap[i][j+1])>25) {//ͬ�������ͼ
									map.mapmap[i][j+1]=map.mapmap[i][j];
								}
							}
						}
					}
				}
			}
			int Pointx;
			int Pointy;
			for(int i=0;i<6;i++) {//�����Ӿ���̹�˹���
				Pointx=random.nextInt(44)+1;
				Pointy=random.nextInt(33)+1;
				map.mapmap[Pointx][Pointy]=Map.SPIRIT1;
				map.mapmap[Pointx+1][Pointy]=Map.SPIRIT0;
				map.mapmap[Pointx][Pointy+1]=Map.SPIRIT0;
				map.mapmap[Pointx+1][Pointy+1]=Map.SPIRIT0;
			}
		}
		
		private void zoreMapMake(Map map) {							//ԭʼ��ͼ����
			Random random=new Random();
			int Pointx;
			int Pointy;
			for(int i=0;i<6;i++) {//�����Ӿ���̹�˹���
				Pointx=random.nextInt(44)+1;
				Pointy=random.nextInt(33)+1;
				map.mapmap[Pointx][Pointy]=Map.SPIRIT1;
				map.mapmap[Pointx+1][Pointy]=Map.SPIRIT0;
				map.mapmap[Pointx][Pointy+1]=Map.SPIRIT0;
				map.mapmap[Pointx+1][Pointy+1]=Map.SPIRIT0;
			}
		}
		
		private void ChangeBlockMake(Map map) {						//��ͼ�����任����
			int c1=1;
			int c2=2;
			int c3=3;
			//����������
			if(map.getMapX()<=8&&map.getMapY()<=8) {
				mapRandom_x2(map,c1);												//���ƽ���
				map.spirittankCount[0]=10;											//����̹������
				map.spirittankCount[1]=4;											//����̹������
				map.spirittankCount[2]=1;											//����̹������
				for(int i=0;i<=46;i++) {											//������-��һ��
					for(int j=0;j<=35;j++) {
						if(map.mapmap[i][j]==Map.WATER) {
							map.mapmap[i][j]=Map.KONG;
						}
					}
				}
				if(map.getMapX()<=5&&map.getMapY()<=5) {
					mapRandom_x2(map,c2);											//���ƽ���
					map.spirittankCount[2]=0;										//����̹������
					for(int i=0;i<=46;i++) {										//������-�ڶ���
						for(int j=0;j<=35;j++) {
							if(map.mapmap[i][j]==Map.STONE) {
								map.mapmap[i][j]=Map.KONG;
							}
						}
					}
					if(map.getMapX()<=2&&map.getMapY()<=2) {
						mapRandom_x2(map,c3);										//���ƽ���
						map.spirittankCount[1]=0;									//����̹������
						for(int i=0;i<=46;i++) {									//������-������
							for(int j=0;j<=35;j++) {
								if(map.mapmap[i][j]==Map.GRAESS) {
									map.mapmap[i][j]=Map.BRICK;
								}
								if(map.mapmap[i][j]==Map.WATER) {
									map.mapmap[i][j]=Map.KONG;
								}
							}
						}
					}
				}
			}
			//����ˮ����
			if(map.getMapX()<=8&&map.getMapY()<=29&&map.getMapY()>=21) {
				mapRandom_x2(map,c1);												//���ƽ���
				map.spirittankCount[0]=10;											//����̹������
				map.spirittankCount[1]=4;											//����̹������
				map.spirittankCount[2]=1;											//����̹������
				for(int i=0;i<=46;i++) {											//ˮ����-��һ��
					for(int j=0;j<=35;j++) {
							if(map.mapmap[i][j]==Map.BRICK) {
								map.mapmap[i][j]=Map.KONG;
						}
					}
				}
				if(map.getMapX()<=5&&map.getMapY()<=29&&map.getMapY()>=24) {
					mapRandom_x2(map,c2);											//���ƽ���
					map.spirittankCount[2]=0;										//����̹������
					for(int i=0;i<=46;i++) {										//ˮ����-�ڶ���
						for(int j=0;j<=35;j++) {
								if(map.mapmap[i][j]==Map.GRAESS) {
									map.mapmap[i][j]=Map.KONG;
							}
						}
					}
					if(map.getMapX()<=2&&map.getMapY()<=29&&map.getMapY()>=27) {
						mapRandom_x2(map,c3);										//���ƽ���
						map.spirittankCount[1]=0;									//����̹������
						for(int i=0;i<=46;i++) {									//ˮ����-������
							for(int j=0;j<=35;j++) {
									if(map.mapmap[i][j]==Map.STONE) {
										map.mapmap[i][j]=Map.WATER;
								}
							}
						}
					}
				}
			}
			//����ʯ����
			if(map.getMapX()>=21&&map.getMapX()<=29&&map.getMapY()<=29&&map.getMapY()>=21) {
				mapRandom_x2(map,c1);												//���ƽ���
				map.spirittankCount[0]=10;											//����̹������
				map.spirittankCount[1]=4;											//����̹������
				map.spirittankCount[2]=1;											//����̹������
				for(int i=0;i<=46;i++) {											//ʯ����-��һ��
					for(int j=0;j<=35;j++) {
							if(map.mapmap[i][j]==Map.BRICK) {
								map.mapmap[i][j]=Map.KONG;
						}
					}
				}
				if(map.getMapX()>=24&&map.getMapX()<=29&&map.getMapY()<=29&&map.getMapY()>=24) {
					mapRandom_x2(map,c2);											//���ƽ���
					map.spirittankCount[2]=0;										//����̹������
					for(int i=0;i<=46;i++) {										//ʯ����-�ڶ���
						for(int j=0;j<=35;j++) {
								if(map.mapmap[i][j]==Map.GRAESS) {
									map.mapmap[i][j]=Map.KONG;
							}
						}
					}
					if(map.getMapX()>=27&&map.getMapX()<=29&&map.getMapY()<=29&&map.getMapY()>=27) {
						mapRandom_x2(map,c3);										//���ƽ���
						map.spirittankCount[1]=0;									//����̹������
						for(int i=0;i<=46;i++) {									//ʯ����-������
							for(int j=0;j<=35;j++) {
								if(map.mapmap[i][j]==Map.GRAESS) {
									map.mapmap[i][j]=Map.KONG;
								}
								if(map.mapmap[i][j]==Map.WATER) {
									map.mapmap[i][j]=Map.STONE;
								}
							}
						}
					}
				}
			}
			//����ɭ������
			if(map.getMapX()>=21&&map.getMapX()<=29&&map.getMapY()<=8) {
				mapRandom_x2(map,c1);												//���ƽ���
				map.spirittankCount[0]=10;											//����̹������
				map.spirittankCount[1]=4;											//����̹������
				map.spirittankCount[2]=1;											//����̹������
				for(int i=0;i<=46;i++) {											//ɭ������-��һ��
					for(int j=0;j<=35;j++) {
							if(map.mapmap[i][j]==Map.BRICK) {
								map.mapmap[i][j]=Map.KONG;
						}
					}
				}
				if(map.getMapX()>=24&&map.getMapX()<=29&&map.getMapY()<=5) {
					mapRandom_x2(map,c2);											//���ƽ���
					map.spirittankCount[2]=0;										//����̹������
					for(int i=0;i<=46;i++) {										//ɭ������-�ڶ���
						for(int j=0;j<=35;j++) {
								if(map.mapmap[i][j]==Map.STONE) {
									map.mapmap[i][j]=Map.GRAESS;
							}
						}
					}
					if(map.getMapX()>=27&&map.getMapX()<=29&&map.getMapY()<=2) {
						mapRandom_x2(map,c3);										//���ƽ���
						map.spirittankCount[1]=0;									//����̹������
						for(int i=0;i<=46;i++) {									//ɭ������-������
							for(int j=0;j<=35;j++) {
									if(map.mapmap[i][j]==Map.WATER) {
										map.mapmap[i][j]=Map.GRAESS;
								}
							}
						}
					}
				}
			}//end
			
			
		}
		
		private void mapfill(Map map) {	//���ֵ�ͼʵ��������
			for(int i=0;i<=46;i++) {
				for(int j=0;j<=35;j++) {
					if(map.mapmap[i][j]==Map.KONG) {
						continue;
					}else if(map.mapmap[i][j]==Map.STONE) {
						map.Stones.add(new BlockStone(i,j));
					}else if(map.mapmap[i][j]==Map.WATER) {
						map.Waters.add(new BlockWater(i,j));
					}else if(map.mapmap[i][j]==Map.BRICK) {
						map.Bricks.add(new BlockBrick(i,j));
					}else if(map.mapmap[i][j]==Map.GRAESS) {
						map.Grasses.add(new BlockGrass(i,j));
					}else if(map.mapmap[i][j]==Map.SPIRIT1){
						map.Spirits.add(new BlockSpirit(i,j));
					}
				}
			}
		}
		
		
	    private int mol(int x,int y) {
	    	if(x==y) {
	    		return 0;
	    	}else if(x>y) {
	    		return x-y;
	    	}else {
	    		return y-x;
	    	}
	    }
	    

	    
	    
		/****************************************************************************/
	    //���̹�˱�ը����
	  		private void playertankBOOM() {
	  			World.get(worldNumber).playertankLife[0]--;//��ը���ʤ���ж�
	  			if(World.get(worldNumber).playertankLife[0]<=0) {			//���������� -1
	  				playerTankState=-1;										//����״̬�������ڼ䲻���������жϣ�
	  			}else if(World.get(worldNumber).playertankLife[0]!=0) {		//�ɸ�������  0
	  				playerTankState=0;										//����״̬�������ڼ䲻���������жϣ�
	  			}
	  			if(playerTankState==-1) {
	  				//����䣬�ӳٽ�����Ϸ�������Ƴ�������
	  				mainFrame.setTime(-cv0);
	  				mainFrame.removeKeyListener(this);
	  				gameState=GAMELOSE;
	  			}
	  			
	  			Cartoon cartoon=new Cartoon(Cartoon.TEXPLODE,playertank.getX(),playertank.getY());
	  			cartoon.addFinishListener(new Listener1());
	  			cartoons.add(cartoon);
	  			
	  		}
	  		
	  		//����̹��000��ը����
	  		private void spirittank000BOOM(int i) {
	  			//���Ӯ���ӳٽ�����Ϸ���������Ƴ�������
	  			World.get(worldNumber).spirittankDestroyed++;//��������+1
	  			//��ը���ʤ���ж�
	  			if((World.get(worldNumber).spirittankDestroyed==World.get(worldNumber).all_count)&&
	  					this.mapX==15&&
	  					this.mapY==15) {
	  				mainFrame.setTime(-cv0);
	  				gameState=GAMEWIN;
	  			}
	  			SpiritTank spirittank000=World.get(worldNumber).spirittank000s.get(i);
	  			cartoons.add(new Cartoon(Cartoon.TEXPLODE,spirittank000.getX(),spirittank000.getY()));
	  			World.get(worldNumber).spirittank000s.remove(i);
	  		}
	  		//����̹��001��ը����
	  		private void spirittank001BOOM(int i) {
	  			//���Ӯ���ӳٽ�����Ϸ���������Ƴ�������
	  			World.get(worldNumber).spirittankDestroyed++;//��������+1
	  			//��ը���ʤ���ж�
	  			if((World.get(worldNumber).spirittankDestroyed==World.get(worldNumber).all_count)&&
	  					this.mapX==15&&
	  					this.mapY==15) {
	  				mainFrame.setTime(-cv0);
	  				gameState=GAMEWIN;
	  			}
	  			SpiritTank001 spirittank001=World.get(worldNumber).spirittank001s.get(i);
	  			cartoons.add(new Cartoon(Cartoon.TEXPLODE,spirittank001.getX(),spirittank001.getY()));
	  			World.get(worldNumber).spirittank001s.remove(i);
	  		}
	  		//����̹��002��ը����
	  		private void spirittank002BOOM(int i) {
	  			//���Ӯ���ӳٽ�����Ϸ���������Ƴ�������
	  			World.get(worldNumber).spirittankDestroyed++;//��������+1
	  			//��ը���ʤ���ж�
	  			if((World.get(worldNumber).spirittankDestroyed==World.get(worldNumber).all_count)&&
	  					this.mapX==15&&
	  					this.mapY==15) {
	  				mainFrame.setTime(-cv0);
	  				gameState=GAMEWIN;
	  			}
	  			SpiritTank002 spirittank002=World.get(worldNumber).spirittank002s.get(i);
	  			cartoons.add(new Cartoon(Cartoon.TEXPLODE,spirittank002.getX(),spirittank002.getY()));
	  			World.get(worldNumber).spirittank002s.remove(i);
	  		}
	  		//����ڵ���ը����
	  		private void playerbulletBOOM(int i) {
	  			Bullet playerbullet = World.get(worldNumber).playerbullets.get(i);
	  			cartoons.add(new Cartoon(Cartoon.BEXPLODE,playerbullet.getX(),playerbullet.getY()));
	  			World.get(worldNumber).playerbullets.remove(i);
	  		}
	  		
	  		//����̹���ڵ���ը����
	  		private void spiritbulletBOOM(int i) {
	  			Bullet spiritbullet = World.get(worldNumber).spiritbullets.get(i);
	  			cartoons.add(new Cartoon(Cartoon.BEXPLODE,spiritbullet.getX(),spiritbullet.getY()));
	  			World.get(worldNumber).spiritbullets.remove(i);	
	  		}
	  		
	  		//���̹�˱�ը��ļ�����
	  		private class Listener1 extends FinishListener{
	  			public void doFinish() {
	  				Cartoon ct =new Cartoon(Cartoon.TCREATE,0,0);
	  				World.get(worldNumber).initPCartoonData(ct,playertank.getX(),playertank.getY());
	  				//��û����������������
	  				if(World.get(worldNumber).playertankLife[0]!=0) {
	  					ct.addFinishListener(new Listener2());
	  					cartoons.add(ct);	
	  				}	
	  			}
	  		}
	  		
	  		
	  		//���̹��������ļ�����
	  		private class Listener2 extends FinishListener{
	  			public void doFinish() {
	  				playerTankState=1;									//����̹�˴��״̬
	  			}
	  		}

	  		
	  		
	  		
	  		public void keyPressed(KeyEvent e) {
	  			int key =e.getKeyCode();
	  			
	  			if(key==KeyEvent.VK_J) {
	  				//����ģʽ
	  				if(playerTankState==1) {//ֻ�д��״̬��ʱ���ܿ���
	  					for(int j=0;j<playertank.firepower;j++) {
	  						Bullet playerbullet=playertank.fire();
	  						World.get(worldNumber).playerbullets.add(playerbullet);
	  					}
	  				}
	  			}
	  			
	  			if(key==KeyEvent.VK_ESCAPE) {
	  				mainFrame.removeKeyListener(this);
	  				mainFrame.login();
	  			}
	  			
	  			playertank.keyPressed(e);
	  			
	  			//ʱ��������޸�
	  			this.timeSpeed=playertank.getTimeSpeed();
	  			this.mainFrame.timeSpeed=playertank.getTimeSpeed();
	  		}

	  		@Override
			public void keyReleased(KeyEvent e) {
				playertank.keyReleased(e);
				//ʱ��������޸�
	  			this.timeSpeed=playertank.getTimeSpeed();
	  			this.mainFrame.timeSpeed=playertank.getTimeSpeed();
			}
	  		
	  		public void setWorldNumber(int worldNumber) {
	  			this.worldNumber=worldNumber;
	  		}
	  		
	  		public int getWorldNumber() {
	  			return this.worldNumber;
	  		}


	  		private void changeBJ() {										//�÷������ڱ߽���ж������
	  			if(bj==1) {										
		 			this.setWorldNumber(this.getWorldNumber()+1);				//������ͼ
		 			playertank.setY(MainFrame.HEIGHT-playertank.getY());		//�������̹��λ��
		 		}
		 		else if(bj==2) {
		 			this.setWorldNumber(this.getWorldNumber()+WORLDHEIGHT);		//������ͼ
		 			playertank.setX(MainFrame.WIDTH-playertank.getX());			//�������̹��λ��
		 		}
		 		else if(bj==3) {
		 			this.setWorldNumber(this.getWorldNumber()-1);				//������ͼ
		 			playertank.setY(MainFrame.HEIGHT-playertank.getY());		//�������̹��λ��
		 		}
		 		else {
		 			this.setWorldNumber(this.getWorldNumber()-WORLDHEIGHT);		//������ͼ
		 			playertank.setX(MainFrame.WIDTH-playertank.getX());			//�������̹��λ��
		 		}
		 		this.mapX=World.get(worldNumber).getMapX();
		 		this.mapY=World.get(worldNumber).getMapY();
	  		}
	  		
	  		private void drawALL() {
	  			//���������Ļ
				Color c=goffscreen.getColor();
				goffscreen.setColor(Color.BLACK);
				goffscreen.fillRect(0, 0, MainFrame.WIDTH, MainFrame.HEIGHT);
				goffscreen.setColor(c);
				
				//������
				for(int i=cartoons.size()-1;i>=0;i--) {
					Cartoon cartoon=cartoons.get(i);
					if(!cartoon.draw(goffscreen)) {
						cartoons.remove(i);
					}
				}
				
				//�������̹��
				if(this.timeSpeed==1) {
			 		playertank.draw(goffscreen);
			 	}else {
			 		playertank.draw_0(goffscreen);
			 	}
				
				//���ƾ���̹��000
				for(int i=World.get(worldNumber).spirittank000s.size()-1;i>=0;i--) {
					SpiritTank stank00=World.get(worldNumber).spirittank000s.get(i);
					if(this.timeSpeed==1) {
						stank00.drawSpiritTank(goffscreen);
				 	}else {
				 		stank00.drawSpiritTank_0(goffscreen);
				 	}
				}
				
				//���ƾ���̹��001
				for(int i=World.get(worldNumber).spirittank001s.size()-1;i>=0;i--) {
					SpiritTank001 stank01=World.get(worldNumber).spirittank001s.get(i);
					if(this.timeSpeed==1) {
						stank01.drawSpiritTank(goffscreen);
				 	}else {
				 		stank01.drawSpiritTank_0(goffscreen);
				 	}
				}
				
				//���ƾ���̹��002
				for(int i=World.get(worldNumber).spirittank002s.size()-1;i>=0;i--) {
					SpiritTank002 stank01=World.get(worldNumber).spirittank002s.get(i);
					if(this.timeSpeed==1) {
						stank01.drawSpiritTank(goffscreen);
				 	}else {
				 		stank01.drawSpiritTank_0(goffscreen);
				 	}
				}
				
				//���Ƶ�ͼ
				if(this.timeSpeed==1) {
					World.get(worldNumber).drawBricks(goffscreen);
					World.get(worldNumber).drawStones(goffscreen);
					World.get(worldNumber).drawWaters(goffscreen);
			 	}else {
			 		World.get(worldNumber).drawBricks_0(goffscreen);
					World.get(worldNumber).drawStones_0(goffscreen);
					World.get(worldNumber).drawWaters_0(goffscreen);
			 	}
				
				//�������̹���ڵ�
				for(int i=World.get(worldNumber).playerbullets.size()-1;i>=0;i--) {
					Bullet playerbullet=World.get(worldNumber).playerbullets.get(i);
					playerbullet.draw(goffscreen);
				}
				
				//���ƾ���̹���ڵ�
				for(int i=World.get(worldNumber).spiritbullets.size()-1;i>=0;i--) {
					Bullet spiritbullet=World.get(worldNumber).spiritbullets.get(i);
					spiritbullet.draw(goffscreen);
				}
				
				//���������ͼ
				if(this.timeSpeed==1) {
					//����ݿ�
					World.get(worldNumber).drawGrasses(goffscreen);
					//���뾫���
					World.get(worldNumber).drawSpirits(goffscreen);
				}else {
					//����ݿ�
					World.get(worldNumber).drawGrasses_0(goffscreen);
					//���뾫���
					World.get(worldNumber).drawSpirits_0(goffscreen);
				}
	  		}
	  		
			@Override
			public void keyTyped(KeyEvent arg0) {
			}
			
			
		
		private void DO_map001() {
			//ʵʱ�������̹���ݶ�����
			//����˲���
			int playertank_block_x=playertank.getX()/17;
			int playertank_block_y=playertank.getY()/17;
			int key=0;											//���ƣ���δ�����κβ���ʱ������ѭ��
			//����Ȩֵ��λ��
			//����һ���Ա���Ȩֵ��
			/*
			if(World.get(worldNumber).mapmap_move_point001[playertank_block_x][playertank_block_y]!=1) {
				World.get(worldNumber).mapmap_move_point001[playertank_block_x][playertank_block_y]=-spirittank_see;//��������ݶ�
			}
			if(World.get(worldNumber).mapmap_move_point001[playertank_block_x+1][playertank_block_y]!=1) {
				World.get(worldNumber).mapmap_move_point001[playertank_block_x+1][playertank_block_y]=-spirittank_see;//��������ݶ�
			}
			if(World.get(worldNumber).mapmap_move_point001[playertank_block_x][playertank_block_y+1]!=1) {
				World.get(worldNumber).mapmap_move_point001[playertank_block_x][playertank_block_y+1]=-spirittank_see;//��������ݶ�
			}
			if(World.get(worldNumber).mapmap_move_point001[playertank_block_x+1][playertank_block_y+1]!=1) {
				World.get(worldNumber).mapmap_move_point001[playertank_block_x+1][playertank_block_y+1]=-spirittank_see;//��������ݶ�
			}
			*/
			//������������ѿ�Ȩֵ��
			//��������1���ж�Ȩֵ�����ģ�
			for(int i=0;i<3;i++) {
				if(playertank_block_y-spirittank_distance>=0&&
						playertank_block_x-1+i<48&&playertank_block_x-1+i>=0) {
					if(World.get(worldNumber).mapmap_move_point001[playertank_block_x-1+i][playertank_block_y-spirittank_distance]!=1) {
						World.get(worldNumber).mapmap_move_point001[playertank_block_x-1+i][playertank_block_y-spirittank_distance]=-spirittank_see;
					}
				}
			}
			for(int i=0;i<3;i++) {
				if(playertank_block_y+spirittank_distance<37&&
						playertank_block_x-1+i<48&&playertank_block_x-1+i>=0) {
					if(World.get(worldNumber).mapmap_move_point001[playertank_block_x-1+i][playertank_block_y+spirittank_distance]!=1) {
						World.get(worldNumber).mapmap_move_point001[playertank_block_x-1+i][playertank_block_y+spirittank_distance]=-spirittank_see;
					}
				}
			}
			for(int i=0;i<3;i++) {
				if(playertank_block_x-spirittank_distance>=0&&
						playertank_block_y-1+i<37&&playertank_block_y-1+i>=0) {
					if(World.get(worldNumber).mapmap_move_point001[playertank_block_x-spirittank_distance][playertank_block_y-1+i]!=1) {
						World.get(worldNumber).mapmap_move_point001[playertank_block_x-spirittank_distance][playertank_block_y-1+i]=-spirittank_see;
					}
				}
			}
			for(int i=0;i<3;i++) {
				if(playertank_block_x+spirittank_distance<48&&
						playertank_block_y-1+i<37&&playertank_block_y-1+i>=0) {
					if(World.get(worldNumber).mapmap_move_point001[playertank_block_x+spirittank_distance][playertank_block_y-1+i]!=1) {
						World.get(worldNumber).mapmap_move_point001[playertank_block_x+spirittank_distance][playertank_block_y-1+i]=-spirittank_see;
					}
				}
			}
			//��������2������Ȩֵ
			
			//��������
			int[][] lingpai=new int[48][37];				//ѭ���е��������
			for(int i=0;i<=47;i++) {
				for(int j=0;j<=36;j++) {
					lingpai[i][j]=0;
				}
			}
			while(key==0) {
				key=1;
				for(int i=0;i<=47;i++) {
					for(int j=0;j<=36;j++) {
						if(World.get(worldNumber).mapmap_move_point001[i][j]<-1&&
								lingpai[i][j]==0) {//�ж�Ϊ��ǽ����0���Ǳ�������
							if(j-1>=0) {
								if(World.get(worldNumber).mapmap_move_point001[i][j-1]==0) {
									key=0;
									World.get(worldNumber).mapmap_move_point001[i][j-1]=World.get(worldNumber).mapmap_move_point001[i][j]+1;
									lingpai[i][j-1]=1;
								}
							}
							if(j+1<=36) {
								if(World.get(worldNumber).mapmap_move_point001[i][j+1]==0) {
									key=0;
									World.get(worldNumber).mapmap_move_point001[i][j+1]=World.get(worldNumber).mapmap_move_point001[i][j]+1;
									lingpai[i][j+1]=1;
								}
							}
							if(i-1>=0) {
								if(World.get(worldNumber).mapmap_move_point001[i-1][j]==0) {
									key=0;
									World.get(worldNumber).mapmap_move_point001[i-1][j]=World.get(worldNumber).mapmap_move_point001[i][j]+1;
									lingpai[i-1][j]=1;
								}
							}
							if(i+1<=47) {
								if(World.get(worldNumber).mapmap_move_point001[i+1][j]==0) {
									key=0;
									World.get(worldNumber).mapmap_move_point001[i+1][j]=World.get(worldNumber).mapmap_move_point001[i][j]+1;
									lingpai[i+1][j]=1;
								}
							}
						}
					}
				}
				//��ԭ���㱾�������ж�
				for(int i=0;i<=47;i++) {
					for(int j=0;j<=36;j++) {
						lingpai[i][j]=0;
					}
				}
			}
			//����������
			for(int i=0;i<World.get(worldNumber).playerbullets.size();i++) {//��������
				Bullet playerbullet=World.get(worldNumber).playerbullets.get(i);
				int bullet_x=playerbullet.getX()/17;
				int bullet_y=playerbullet.getY()/17;
				if(playerbullet.getDiraction()==Spirit.UP) {				/***����Ϊ��***/
					if(playerbullet.getX()%17<=1) {							//���ж�
						for(int j=0;j<spirittank_miss;j++) {				//ѭ��ִ�б��þ�������
							if(bullet_y-j>=0) {								//ȷ���ж�������
								if(World.get(worldNumber).mapmap_move_point001[bullet_x][bullet_y-j]!=1) {		//��֤���Ḳ��ǽ��
									World.get(worldNumber).mapmap_move_point001[bullet_x][bullet_y-j]=-j-1;		//��ֵ
								}
							}
						}
					}else if(playerbullet.getX()%17>=16) {
						for(int j=0;j<spirittank_miss;j++) {				//ѭ��ִ�б��þ�������
							if(bullet_y-j>=0&&bullet_x+1<48) {				//ȷ���ж�������
								if(World.get(worldNumber).mapmap_move_point001[bullet_x+1][bullet_y-j]!=1) {	//��֤���Ḳ��ǽ��
									World.get(worldNumber).mapmap_move_point001[bullet_x+1][bullet_y-j]=-j-1;		//��ֵ
								}
							}
						}
					}else {													//���ж�				
						for(int j=0;j<spirittank_miss;j++) {				//ѭ��ִ�б��þ�������
							if(bullet_y-j>=0&&bullet_x+1<48) {				//ȷ���ж�������
								if(World.get(worldNumber).mapmap_move_point001[bullet_x+1][bullet_y-j]!=1) {	//��֤���Ḳ��ǽ��
									World.get(worldNumber).mapmap_move_point001[bullet_x+1][bullet_y-j]=-j-1;     //��ֵ
								}
							}
						}
						for(int j=0;j<spirittank_miss;j++) {				//ѭ��ִ�б��þ�������
							if(bullet_y-j>=0) {								//ȷ���ж�������
								if(World.get(worldNumber).mapmap_move_point001[bullet_x][bullet_y-j]!=1) {		//��֤���Ḳ��ǽ��
									World.get(worldNumber).mapmap_move_point001[bullet_x][bullet_y-j]=-j-1;		//��ֵ
								}
							}
						}
					}
				}else if(playerbullet.getDiraction()==Spirit.RIGHT) {		/***����Ϊ��***/
					if(playerbullet.getY()%17<=1) {							//���ж�
						for(int j=0;j<spirittank_miss;j++) {				//ѭ��ִ�б��þ�������
							if(bullet_x+j<48) {								//ȷ���ж�������
								if(World.get(worldNumber).mapmap_move_point001[bullet_x+j][bullet_y]!=1) {		//��֤���Ḳ��ǽ��
									World.get(worldNumber).mapmap_move_point001[bullet_x+j][bullet_y]=-j-1;     	//��ֵ
								}
							}
						}
					}else if(playerbullet.getY()%17>=16) {
						for(int j=0;j<spirittank_miss;j++) {				//ѭ��ִ�б��þ�������
							if(bullet_x+j<48&&bullet_y+1<37) {				//ȷ���ж�������
								if(World.get(worldNumber).mapmap_move_point001[bullet_x+j][bullet_y+1]!=1) {		//��֤���Ḳ��ǽ��
									World.get(worldNumber).mapmap_move_point001[bullet_x+j][bullet_y+1]=-j-1;     	//��ֵ
								}
							}
						}
					}else {													//���ж�
						for(int j=0;j<spirittank_miss;j++) {				//ѭ��ִ�б��þ�������
							if(bullet_x+j<48&&bullet_y+1<37) {				//ȷ���ж�������
								if(World.get(worldNumber).mapmap_move_point001[bullet_x+j][bullet_y+1]!=1) {		//��֤���Ḳ��ǽ��
									World.get(worldNumber).mapmap_move_point001[bullet_x+j][bullet_y+1]=-j-1;     	//��ֵ
								}
							}
						}
						for(int j=0;j<spirittank_miss;j++) {				//ѭ��ִ�б��þ�������
							if(bullet_x+j<48) {								//ȷ���ж�������
								if(World.get(worldNumber).mapmap_move_point001[bullet_x+j][bullet_y]!=1) {			//��֤���Ḳ��ǽ��
									World.get(worldNumber).mapmap_move_point001[bullet_x+j][bullet_y]=-j-1;     		//��ֵ
								}
							}
						}
					}
				}else if(playerbullet.getDiraction()==Spirit.DOWN) {		/***����Ϊ��***/
					if(playerbullet.getX()%17<=1) {							//���ж�
						for(int j=0;j<spirittank_miss;j++) {				//ѭ��ִ�б��þ�������
							if(bullet_y+j<37) {								//ȷ���ж�������
								if(World.get(worldNumber).mapmap_move_point001[bullet_x][bullet_y+j]!=1) {		//��֤���Ḳ��ǽ��
									World.get(worldNumber).mapmap_move_point001[bullet_x][bullet_y+j]=-j-1;		//��ֵ
								}
							}
						}
					}else if(playerbullet.getX()%17>=16) {
						for(int j=0;j<spirittank_miss;j++) {				//ѭ��ִ�б��þ�������
							if(bullet_y+j<37&&bullet_x+1<48) {				//ȷ���ж�������
								if(World.get(worldNumber).mapmap_move_point001[bullet_x+1][bullet_y+j]!=1) {	//��֤���Ḳ��ǽ��
									World.get(worldNumber).mapmap_move_point001[bullet_x+1][bullet_y+j]=-j-1;		//��ֵ
								}
							}
						}
					}else {													//���ж�				
						for(int j=0;j<spirittank_miss;j++) {				//ѭ��ִ�б��þ�������
							if(bullet_y+j<37&&bullet_x+1<48) {				//ȷ���ж�������
								if(World.get(worldNumber).mapmap_move_point001[bullet_x+1][bullet_y+j]!=1) {	//��֤���Ḳ��ǽ��
									World.get(worldNumber).mapmap_move_point001[bullet_x+1][bullet_y+j]=-j-1;     //��ֵ
								}
							}
						}
						for(int j=0;j<spirittank_miss;j++) {				//ѭ��ִ�б��þ�������
							if(bullet_y+j<37) {								//ȷ���ж�������
								if(World.get(worldNumber).mapmap_move_point001[bullet_x][bullet_y+j]!=1) {		//��֤���Ḳ��ǽ��
									World.get(worldNumber).mapmap_move_point001[bullet_x][bullet_y+j]=-j-1;		//��ֵ
								}
							}
						}
					}
				}else if(playerbullet.getDiraction()==Spirit.LEFT) {		/***����Ϊ��***/
					if(playerbullet.getY()%17<=1) {							//���ж�
						for(int j=0;j<spirittank_miss;j++) {				//ѭ��ִ�б��þ�������
							if(bullet_x-j>=0) {								//ȷ���ж�������
								if(World.get(worldNumber).mapmap_move_point001[bullet_x-j][bullet_y]!=1) {		//��֤���Ḳ��ǽ��
									World.get(worldNumber).mapmap_move_point001[bullet_x-j][bullet_y]=-j-1;     	//��ֵ
								}
							}
						}
					}else if(playerbullet.getY()%17>=16) {
						for(int j=0;j<spirittank_miss;j++) {				//ѭ��ִ�б��þ�������
							if(bullet_x-j>=0&&bullet_y+1<37) {				//ȷ���ж�������
								if(World.get(worldNumber).mapmap_move_point001[bullet_x-j][bullet_y+1]!=1) {		//��֤���Ḳ��ǽ��
									World.get(worldNumber).mapmap_move_point001[bullet_x-j][bullet_y+1]=-j-1;     	//��ֵ
								}
							}
						}
					}else {													//���ж�
						for(int j=0;j<spirittank_miss;j++) {				//ѭ��ִ�б��þ�������
							if(bullet_x-j>=0&&bullet_y+1<37) {				//ȷ���ж�������
								if(World.get(worldNumber).mapmap_move_point001[bullet_x-j][bullet_y+1]!=1) {		//��֤���Ḳ��ǽ��
									World.get(worldNumber).mapmap_move_point001[bullet_x-j][bullet_y+1]=-j-1;     	//��ֵ
								}
							}
						}
						for(int j=0;j<spirittank_miss;j++) {				//ѭ��ִ�б��þ�������
							if(bullet_x-j>=0) {								//ȷ���ж�������
								if(World.get(worldNumber).mapmap_move_point001[bullet_x-j][bullet_y]!=1) {			//��֤���Ḳ��ǽ��
									World.get(worldNumber).mapmap_move_point001[bullet_x-j][bullet_y]=-j-1;     		//��ֵ
								}
							}
						}
					}
				}
			}
			
			/*********
			//��ʾ���ֵ�ͼ�������
			for(int i=0;i<=36;i++) {
				for(int j=0;j<=47;j++) {
					String str1=String.format("% 5d",World.get(worldNumber).mapmap_move_point001[j][i] );
					System.out.printf(str1);
				}
				System.out.println("");
			}
			System.out.println("");
			/*********/

		}

		private void DO_map002() {
			this.spirittank_distance002=World.get(worldNumber).move_distance;		//�������߰뾶
			//ʵʱ�������̹���ݶ�����
			//����˲���
			int playertank_block_x=playertank.getX()/17;
			int playertank_block_y=playertank.getY()/17;
			int key=0;											//���ƣ���δ�����κβ���ʱ������ѭ��
			//����Ȩֵ��λ��
			//����һ���Ա���Ȩֵ��
			/*
			if(World.get(worldNumber).mapmap_move_point001[playertank_block_x][playertank_block_y]!=1) {
				World.get(worldNumber).mapmap_move_point001[playertank_block_x][playertank_block_y]=-spirittank_see;//��������ݶ�
			}
			if(World.get(worldNumber).mapmap_move_point001[playertank_block_x+1][playertank_block_y]!=1) {
				World.get(worldNumber).mapmap_move_point001[playertank_block_x+1][playertank_block_y]=-spirittank_see;//��������ݶ�
			}
			if(World.get(worldNumber).mapmap_move_point001[playertank_block_x][playertank_block_y+1]!=1) {
				World.get(worldNumber).mapmap_move_point001[playertank_block_x][playertank_block_y+1]=-spirittank_see;//��������ݶ�
			}
			if(World.get(worldNumber).mapmap_move_point001[playertank_block_x+1][playertank_block_y+1]!=1) {
				World.get(worldNumber).mapmap_move_point001[playertank_block_x+1][playertank_block_y+1]=-spirittank_see;//��������ݶ�
			}
			*/
			//������������ѿ�Ȩֵ��
			//��������1���ж�Ȩֵ
			//Ȩֵ�㼯�����Ϸ������ϣ�
			if(playertank_block_x-spirittank_distance002>=0&&playertank_block_y-spirittank_distance002>=0) {
				if(World.get(worldNumber).mapmap_move_point002[playertank_block_x-spirittank_distance002][playertank_block_y-spirittank_distance002]!=1) {
					World.get(worldNumber).mapmap_move_point002[playertank_block_x-spirittank_distance002][playertank_block_y-spirittank_distance002]=-spirittank_see;
				}
			}
			
			
			//Ȩֵ����2������Ȩֵ(���������� )
			int d1=5;
			for(int i1=0;i1<d1;i1++) {
				for(int j1=0;j1<d1;j1++) {
					if(playertank_block_x-2+i1>=0&&
							playertank_block_x-2+i1<=47&&
							playertank_block_y-2+j1>=0&&
							playertank_block_y-2+j1<=36) {
						if(World.get(worldNumber).mapmap_move_point002[playertank_block_x-2+i1][playertank_block_y-2+j1]!=1) {
							World.get(worldNumber).mapmap_move_point002[playertank_block_x-2+i1][playertank_block_y-2+j1]=-13;
						}
					}
				}
			}
			int d2=3;
			for(int i1=0;i1<d2;i1++) {
				for(int j1=0;j1<d2;j1++) {
					if(playertank_block_x-1+i1>=0&&
							playertank_block_x-1+i1<=47&&
							playertank_block_y-1+j1>=0&&
							playertank_block_y-1+j1<=36) {
						if(World.get(worldNumber).mapmap_move_point002[playertank_block_x-1+i1][playertank_block_y-1+j1]!=1) {
							World.get(worldNumber).mapmap_move_point002[playertank_block_x-1+i1][playertank_block_y-1+j1]=-12;
						}
					}
				}
			}
			int d3=1;
			if(World.get(worldNumber).mapmap_move_point002[playertank_block_x][playertank_block_y]!=1) {
				World.get(worldNumber).mapmap_move_point002[playertank_block_x][playertank_block_y]=-11;
			}
			
			//Ȩֵ����3
			//����������
			for(int i=0;i<World.get(worldNumber).playerbullets.size();i++) {//��������
				Bullet playerbullet=World.get(worldNumber).playerbullets.get(i);
				int bullet_x=playerbullet.getX()/17;
				int bullet_y=playerbullet.getY()/17;
				if(playerbullet.getDiraction()==Spirit.UP) {				/***����Ϊ��***/
					if(playerbullet.getX()%17<=1) {							//���ж�
						for(int j=0;j<spirittank_miss;j++) {				//ѭ��ִ�б��þ�������
							if(bullet_y-j>=0) {								//ȷ���ж�������
								if(World.get(worldNumber).mapmap_move_point002[bullet_x][bullet_y-j]!=1) {		//��֤���Ḳ��ǽ��
									World.get(worldNumber).mapmap_move_point002[bullet_x][bullet_y-j]=spirittank_miss-j+1;		//��ֵ
								}
							}
						}
					}else if(playerbullet.getX()%17>=16) {
						for(int j=0;j<spirittank_miss;j++) {				//ѭ��ִ�б��þ�������
							if(bullet_y-j>=0&&bullet_x+1<48) {				//ȷ���ж�������
								if(World.get(worldNumber).mapmap_move_point002[bullet_x+1][bullet_y-j]!=1) {	//��֤���Ḳ��ǽ��
									World.get(worldNumber).mapmap_move_point002[bullet_x+1][bullet_y-j]=spirittank_miss-j+1;		//��ֵ
								}
							}
						}
					}else {													//���ж�				
						for(int j=0;j<spirittank_miss;j++) {				//ѭ��ִ�б��þ�������
							if(bullet_y-j>=0&&bullet_x+1<48) {				//ȷ���ж�������
								if(World.get(worldNumber).mapmap_move_point002[bullet_x+1][bullet_y-j]!=1) {	//��֤���Ḳ��ǽ��
									World.get(worldNumber).mapmap_move_point002[bullet_x+1][bullet_y-j]=spirittank_miss-j+1;     //��ֵ
								}
							}
						}
						for(int j=0;j<spirittank_miss;j++) {				//ѭ��ִ�б��þ�������
							if(bullet_y-j>=0) {								//ȷ���ж�������
								if(World.get(worldNumber).mapmap_move_point002[bullet_x][bullet_y-j]!=1) {		//��֤���Ḳ��ǽ��
									World.get(worldNumber).mapmap_move_point002[bullet_x][bullet_y-j]=spirittank_miss-j+1;		//��ֵ
								}
							}
						}
					}
				}else if(playerbullet.getDiraction()==Spirit.RIGHT) {		/***����Ϊ��***/
					if(playerbullet.getY()%17<=1) {							//���ж�
						for(int j=0;j<spirittank_miss;j++) {				//ѭ��ִ�б��þ�������
							if(bullet_x+j<48) {								//ȷ���ж�������
								if(World.get(worldNumber).mapmap_move_point002[bullet_x+j][bullet_y]!=1) {		//��֤���Ḳ��ǽ��
									World.get(worldNumber).mapmap_move_point002[bullet_x+j][bullet_y]=spirittank_miss-j+1;     	//��ֵ
								}
							}
						}
					}else if(playerbullet.getY()%17>=16) {
						for(int j=0;j<spirittank_miss;j++) {				//ѭ��ִ�б��þ�������
							if(bullet_x+j<48&&bullet_y+1<37) {				//ȷ���ж�������
								if(World.get(worldNumber).mapmap_move_point002[bullet_x+j][bullet_y+1]!=1) {		//��֤���Ḳ��ǽ��
									World.get(worldNumber).mapmap_move_point002[bullet_x+j][bullet_y+1]=spirittank_miss-j+1;     	//��ֵ
								}
							}
						}
					}else {													//���ж�
						for(int j=0;j<spirittank_miss;j++) {				//ѭ��ִ�б��þ�������
							if(bullet_x+j<48&&bullet_y+1<37) {				//ȷ���ж�������
								if(World.get(worldNumber).mapmap_move_point002[bullet_x+j][bullet_y+1]!=1) {		//��֤���Ḳ��ǽ��
									World.get(worldNumber).mapmap_move_point002[bullet_x+j][bullet_y+1]=spirittank_miss-j+1;     	//��ֵ
								}
							}
						}
						for(int j=0;j<spirittank_miss;j++) {				//ѭ��ִ�б��þ�������
							if(bullet_x+j<48) {								//ȷ���ж�������
								if(World.get(worldNumber).mapmap_move_point002[bullet_x+j][bullet_y]!=1) {			//��֤���Ḳ��ǽ��
									World.get(worldNumber).mapmap_move_point002[bullet_x+j][bullet_y]=spirittank_miss-j+1;     		//��ֵ
								}
							}
						}
					}
				}else if(playerbullet.getDiraction()==Spirit.DOWN) {		/***����Ϊ��***/
					if(playerbullet.getX()%17<=1) {							//���ж�
						for(int j=0;j<spirittank_miss;j++) {				//ѭ��ִ�б��þ�������
							if(bullet_y+j<37) {								//ȷ���ж�������
								if(World.get(worldNumber).mapmap_move_point002[bullet_x][bullet_y+j]!=1) {		//��֤���Ḳ��ǽ��
									World.get(worldNumber).mapmap_move_point002[bullet_x][bullet_y+j]=spirittank_miss-j+1;		//��ֵ
								}
							}
						}
					}else if(playerbullet.getX()%17>=16) {
						for(int j=0;j<spirittank_miss;j++) {				//ѭ��ִ�б��þ�������
							if(bullet_y+j<37&&bullet_x+1<48) {				//ȷ���ж�������
								if(World.get(worldNumber).mapmap_move_point002[bullet_x+1][bullet_y+j]!=1) {	//��֤���Ḳ��ǽ��
									World.get(worldNumber).mapmap_move_point002[bullet_x+1][bullet_y+j]=spirittank_miss-j+1;		//��ֵ
								}
							}
						}
					}else {													//���ж�				
						for(int j=0;j<spirittank_miss;j++) {				//ѭ��ִ�б��þ�������
							if(bullet_y+j<37&&bullet_x+1<48) {				//ȷ���ж�������
								if(World.get(worldNumber).mapmap_move_point002[bullet_x+1][bullet_y+j]!=1) {	//��֤���Ḳ��ǽ��
									World.get(worldNumber).mapmap_move_point002[bullet_x+1][bullet_y+j]=spirittank_miss-j+1;     //��ֵ
								}
							}
						}
						for(int j=0;j<spirittank_miss;j++) {				//ѭ��ִ�б��þ�������
							if(bullet_y+j<37) {								//ȷ���ж�������
								if(World.get(worldNumber).mapmap_move_point002[bullet_x][bullet_y+j]!=1) {		//��֤���Ḳ��ǽ��
									World.get(worldNumber).mapmap_move_point002[bullet_x][bullet_y+j]=spirittank_miss-j+1;		//��ֵ
								}
							}
						}
					}
				}else if(playerbullet.getDiraction()==Spirit.LEFT) {		/***����Ϊ��***/
					if(playerbullet.getY()%17<=1) {							//���ж�
						for(int j=0;j<spirittank_miss;j++) {				//ѭ��ִ�б��þ�������
							if(bullet_x-j>=0) {								//ȷ���ж�������
								if(World.get(worldNumber).mapmap_move_point002[bullet_x-j][bullet_y]!=1) {		//��֤���Ḳ��ǽ��
									World.get(worldNumber).mapmap_move_point002[bullet_x-j][bullet_y]=spirittank_miss-j+1;     	//��ֵ
								}
							}
						}
					}else if(playerbullet.getY()%17>=16) {
						for(int j=0;j<spirittank_miss;j++) {				//ѭ��ִ�б��þ�������
							if(bullet_x-j>=0&&bullet_y+1<37) {				//ȷ���ж�������
								if(World.get(worldNumber).mapmap_move_point002[bullet_x-j][bullet_y+1]!=1) {		//��֤���Ḳ��ǽ��
									World.get(worldNumber).mapmap_move_point002[bullet_x-j][bullet_y+1]=spirittank_miss-j+1;     	//��ֵ
								}
							}
						}
					}else {													//���ж�
						for(int j=0;j<spirittank_miss;j++) {				//ѭ��ִ�б��þ�������
							if(bullet_x-j>=0&&bullet_y+1<37) {				//ȷ���ж�������
								if(World.get(worldNumber).mapmap_move_point002[bullet_x-j][bullet_y+1]!=1) {		//��֤���Ḳ��ǽ��
									World.get(worldNumber).mapmap_move_point002[bullet_x-j][bullet_y+1]=spirittank_miss-j+1;     	//��ֵ
								}
							}
						}
						for(int j=0;j<spirittank_miss;j++) {				//ѭ��ִ�б��þ�������
							if(bullet_x-j>=0) {								//ȷ���ж�������
								if(World.get(worldNumber).mapmap_move_point002[bullet_x-j][bullet_y]!=1) {			//��֤���Ḳ��ǽ��
									World.get(worldNumber).mapmap_move_point002[bullet_x-j][bullet_y]=spirittank_miss-j+1;     		//��ֵ
								}
							}
						}
					}
				}
			}
			
			//��������
			int[][] lingpai=new int[48][37];				//ѭ���е��������
			for(int i=0;i<=47;i++) {
				for(int j=0;j<=36;j++) {
					lingpai[i][j]=0;
				}
			}
			while(key==0) {
				key=1;
				for(int i=0;i<=47;i++) {
					for(int j=0;j<=36;j++) {
						if(World.get(worldNumber).mapmap_move_point002[i][j]<-20&&
								lingpai[i][j]==0) {//�ж�Ϊ��ǽ����0���Ǳ�������
							if(j-1>=0) {
								if(World.get(worldNumber).mapmap_move_point002[i][j-1]==0) {
									key=0;
									World.get(worldNumber).mapmap_move_point002[i][j-1]=World.get(worldNumber).mapmap_move_point002[i][j]+1;
									lingpai[i][j-1]=1;
								}
							}
							if(j+1<=36) {
								if(World.get(worldNumber).mapmap_move_point002[i][j+1]==0) {
									key=0;
									World.get(worldNumber).mapmap_move_point002[i][j+1]=World.get(worldNumber).mapmap_move_point002[i][j]+1;
									lingpai[i][j+1]=1;
								}
							}
							if(i-1>=0) {
								if(World.get(worldNumber).mapmap_move_point002[i-1][j]==0) {
									key=0;
									World.get(worldNumber).mapmap_move_point002[i-1][j]=World.get(worldNumber).mapmap_move_point002[i][j]+1;
									lingpai[i-1][j]=1;
								}
							}
							if(i+1<=47) {
								if(World.get(worldNumber).mapmap_move_point002[i+1][j]==0) {
									key=0;
									World.get(worldNumber).mapmap_move_point002[i+1][j]=World.get(worldNumber).mapmap_move_point002[i][j]+1;
									lingpai[i+1][j]=1;
								}
							}
						}
					}
				}
				//��ԭ���㱾�������ж�
				for(int i=0;i<=47;i++) {
					for(int j=0;j<=36;j++) {
						lingpai[i][j]=0;
					}
				}
			}
			
			//��������Ӳ����Ӳ���

			/*********
			//��ʾ���ֵ�ͼ�������
			System.out.println("002");
			for(int i=0;i<=36;i++) {
				for(int j=0;j<=47;j++) {
					String str1=String.format("% 5d",World.get(worldNumber).mapmap_move_point002[j][i] );
					System.out.printf(str1);
				}
				System.out.println("");
			}
			System.out.println("");
			/*********/
		}
		
		private void DO_map003() {
			this.spirittank_distance002=World.get(worldNumber).move_distance;		//�������߰뾶
			//ʵʱ�������̹���ݶ�����
			//����˲���
			int playertank_block_x=playertank.getX()/17;
			int playertank_block_y=playertank.getY()/17;
			int key=0;											//���ƣ���δ�����κβ���ʱ������ѭ��
			//����Ȩֵ��λ��
			//����һ���Ա���Ȩֵ��
			/*
			if(World.get(worldNumber).mapmap_move_point001[playertank_block_x][playertank_block_y]!=1) {
				World.get(worldNumber).mapmap_move_point001[playertank_block_x][playertank_block_y]=-spirittank_see;//��������ݶ�
			}
			if(World.get(worldNumber).mapmap_move_point001[playertank_block_x+1][playertank_block_y]!=1) {
				World.get(worldNumber).mapmap_move_point001[playertank_block_x+1][playertank_block_y]=-spirittank_see;//��������ݶ�
			}
			if(World.get(worldNumber).mapmap_move_point001[playertank_block_x][playertank_block_y+1]!=1) {
				World.get(worldNumber).mapmap_move_point001[playertank_block_x][playertank_block_y+1]=-spirittank_see;//��������ݶ�
			}
			if(World.get(worldNumber).mapmap_move_point001[playertank_block_x+1][playertank_block_y+1]!=1) {
				World.get(worldNumber).mapmap_move_point001[playertank_block_x+1][playertank_block_y+1]=-spirittank_see;//��������ݶ�
			}
			*/
			//������������ѿ�Ȩֵ��
			//��������1���ж�Ȩֵ���ϣ�
			if(playertank_block_y-spirittank_distance002>=0) {
				if(World.get(worldNumber).mapmap_move_point003[playertank_block_x][playertank_block_y-spirittank_distance002]!=1) {
					World.get(worldNumber).mapmap_move_point003[playertank_block_x][playertank_block_y-spirittank_distance002]=-spirittank_see;
				}
			}
			
			//Ȩֵ����2������Ȩֵ(�������)
			int d1=5;
			for(int i1=0;i1<d1;i1++) {
				for(int j1=0;j1<d1;j1++) {
					if(playertank_block_x-2+i1>=0&&
							playertank_block_x-2+i1<=47&&
							playertank_block_y-2+j1>=0&&
							playertank_block_y-2+j1<=36) {
						if(World.get(worldNumber).mapmap_move_point003[playertank_block_x-2+i1][playertank_block_y-2+j1]!=1) {
							World.get(worldNumber).mapmap_move_point003[playertank_block_x-2+i1][playertank_block_y-2+j1]=-13;
						}
					}
				}
			}
			int d2=3;
			for(int i1=0;i1<d2;i1++) {
				for(int j1=0;j1<d2;j1++) {
					if(playertank_block_x-1+i1>=0&&
							playertank_block_x-1+i1<=47&&
							playertank_block_y-1+j1>=0&&
							playertank_block_y-1+j1<=36) {
						if(World.get(worldNumber).mapmap_move_point003[playertank_block_x-1+i1][playertank_block_y-1+j1]!=1) {
							World.get(worldNumber).mapmap_move_point003[playertank_block_x-1+i1][playertank_block_y-1+j1]=-12;
						}
					}
				}
			}
			int d3=1;
			if(World.get(worldNumber).mapmap_move_point003[playertank_block_x][playertank_block_y]!=1) {
				World.get(worldNumber).mapmap_move_point003[playertank_block_x][playertank_block_y]=-11;
			}
			
			//Ȩֵ����3������Ȩֵ(�����ӵ�)
			//����������
			for(int i=0;i<World.get(worldNumber).playerbullets.size();i++) {//��������
				Bullet playerbullet=World.get(worldNumber).playerbullets.get(i);
				int bullet_x=playerbullet.getX()/17;
				int bullet_y=playerbullet.getY()/17;
				if(playerbullet.getDiraction()==Spirit.UP) {				/***����Ϊ��***/
					if(playerbullet.getX()%17<=1) {							//���ж�
						for(int j=0;j<spirittank_miss;j++) {				//ѭ��ִ�б��þ�������
							if(bullet_y-j>=0) {								//ȷ���ж�������
								if(World.get(worldNumber).mapmap_move_point003[bullet_x][bullet_y-j]!=1) {		//��֤���Ḳ��ǽ��
									World.get(worldNumber).mapmap_move_point003[bullet_x][bullet_y-j]=spirittank_miss-j+1;		//��ֵ
								}
							}
						}
					}else if(playerbullet.getX()%17>=16) {
						for(int j=0;j<spirittank_miss;j++) {				//ѭ��ִ�б��þ�������
							if(bullet_y-j>=0&&bullet_x+1<48) {				//ȷ���ж�������
								if(World.get(worldNumber).mapmap_move_point003[bullet_x+1][bullet_y-j]!=1) {	//��֤���Ḳ��ǽ��
									World.get(worldNumber).mapmap_move_point003[bullet_x+1][bullet_y-j]=spirittank_miss-j+1;		//��ֵ
								}
							}
						}
					}else {													//���ж�				
						for(int j=0;j<spirittank_miss;j++) {				//ѭ��ִ�б��þ�������
							if(bullet_y-j>=0&&bullet_x+1<48) {				//ȷ���ж�������
								if(World.get(worldNumber).mapmap_move_point003[bullet_x+1][bullet_y-j]!=1) {	//��֤���Ḳ��ǽ��
									World.get(worldNumber).mapmap_move_point003[bullet_x+1][bullet_y-j]=spirittank_miss-j+1;     //��ֵ
								}
							}
						}
						for(int j=0;j<spirittank_miss;j++) {				//ѭ��ִ�б��þ�������
							if(bullet_y-j>=0) {								//ȷ���ж�������
								if(World.get(worldNumber).mapmap_move_point003[bullet_x][bullet_y-j]!=1) {		//��֤���Ḳ��ǽ��
									World.get(worldNumber).mapmap_move_point003[bullet_x][bullet_y-j]=spirittank_miss-j+1;		//��ֵ
								}
							}
						}
					}
				}else if(playerbullet.getDiraction()==Spirit.RIGHT) {		/***����Ϊ��***/
					if(playerbullet.getY()%17<=1) {							//���ж�
						for(int j=0;j<spirittank_miss;j++) {				//ѭ��ִ�б��þ�������
							if(bullet_x+j<48) {								//ȷ���ж�������
								if(World.get(worldNumber).mapmap_move_point003[bullet_x+j][bullet_y]!=1) {		//��֤���Ḳ��ǽ��
									World.get(worldNumber).mapmap_move_point003[bullet_x+j][bullet_y]=spirittank_miss-j+1;     	//��ֵ
								}
							}
						}
					}else if(playerbullet.getY()%17>=16) {
						for(int j=0;j<spirittank_miss;j++) {				//ѭ��ִ�б��þ�������
							if(bullet_x+j<48&&bullet_y+1<37) {				//ȷ���ж�������
								if(World.get(worldNumber).mapmap_move_point003[bullet_x+j][bullet_y+1]!=1) {		//��֤���Ḳ��ǽ��
									World.get(worldNumber).mapmap_move_point003[bullet_x+j][bullet_y+1]=spirittank_miss-j+1;     	//��ֵ
								}
							}
						}
					}else {													//���ж�
						for(int j=0;j<spirittank_miss;j++) {				//ѭ��ִ�б��þ�������
							if(bullet_x+j<48&&bullet_y+1<37) {				//ȷ���ж�������
								if(World.get(worldNumber).mapmap_move_point003[bullet_x+j][bullet_y+1]!=1) {		//��֤���Ḳ��ǽ��
									World.get(worldNumber).mapmap_move_point003[bullet_x+j][bullet_y+1]=spirittank_miss-j+1;     	//��ֵ
								}
							}
						}
						for(int j=0;j<spirittank_miss;j++) {				//ѭ��ִ�б��þ�������
							if(bullet_x+j<48) {								//ȷ���ж�������
								if(World.get(worldNumber).mapmap_move_point003[bullet_x+j][bullet_y]!=1) {			//��֤���Ḳ��ǽ��
									World.get(worldNumber).mapmap_move_point003[bullet_x+j][bullet_y]=spirittank_miss-j+1;     		//��ֵ
								}
							}
						}
					}
				}else if(playerbullet.getDiraction()==Spirit.DOWN) {		/***����Ϊ��***/
					if(playerbullet.getX()%17<=1) {							//���ж�
						for(int j=0;j<spirittank_miss;j++) {				//ѭ��ִ�б��þ�������
							if(bullet_y+j<37) {								//ȷ���ж�������
								if(World.get(worldNumber).mapmap_move_point003[bullet_x][bullet_y+j]!=1) {		//��֤���Ḳ��ǽ��
									World.get(worldNumber).mapmap_move_point003[bullet_x][bullet_y+j]=spirittank_miss-j+1;		//��ֵ
								}
							}
						}
					}else if(playerbullet.getX()%17>=16) {
						for(int j=0;j<spirittank_miss;j++) {				//ѭ��ִ�б��þ�������
							if(bullet_y+j<37&&bullet_x+1<48) {				//ȷ���ж�������
								if(World.get(worldNumber).mapmap_move_point003[bullet_x+1][bullet_y+j]!=1) {	//��֤���Ḳ��ǽ��
									World.get(worldNumber).mapmap_move_point003[bullet_x+1][bullet_y+j]=spirittank_miss-j+1;		//��ֵ
								}
							}
						}
					}else {													//���ж�				
						for(int j=0;j<spirittank_miss;j++) {				//ѭ��ִ�б��þ�������
							if(bullet_y+j<37&&bullet_x+1<48) {				//ȷ���ж�������
								if(World.get(worldNumber).mapmap_move_point003[bullet_x+1][bullet_y+j]!=1) {	//��֤���Ḳ��ǽ��
									World.get(worldNumber).mapmap_move_point003[bullet_x+1][bullet_y+j]=spirittank_miss-j+1;     //��ֵ
								}
							}
						}
						for(int j=0;j<spirittank_miss;j++) {				//ѭ��ִ�б��þ�������
							if(bullet_y+j<37) {								//ȷ���ж�������
								if(World.get(worldNumber).mapmap_move_point003[bullet_x][bullet_y+j]!=1) {		//��֤���Ḳ��ǽ��
									World.get(worldNumber).mapmap_move_point003[bullet_x][bullet_y+j]=spirittank_miss-j+1;		//��ֵ
								}
							}
						}
					}
				}else if(playerbullet.getDiraction()==Spirit.LEFT) {		/***����Ϊ��***/
					if(playerbullet.getY()%17<=1) {							//���ж�
						for(int j=0;j<spirittank_miss;j++) {				//ѭ��ִ�б��þ�������
							if(bullet_x-j>=0) {								//ȷ���ж�������
								if(World.get(worldNumber).mapmap_move_point003[bullet_x-j][bullet_y]!=1) {		//��֤���Ḳ��ǽ��
									World.get(worldNumber).mapmap_move_point003[bullet_x-j][bullet_y]=spirittank_miss-j+1;     	//��ֵ
								}
							}
						}
					}else if(playerbullet.getY()%17>=16) {
						for(int j=0;j<spirittank_miss;j++) {				//ѭ��ִ�б��þ�������
							if(bullet_x-j>=0&&bullet_y+1<37) {				//ȷ���ж�������
								if(World.get(worldNumber).mapmap_move_point003[bullet_x-j][bullet_y+1]!=1) {		//��֤���Ḳ��ǽ��
									World.get(worldNumber).mapmap_move_point003[bullet_x-j][bullet_y+1]=spirittank_miss-j+1;     	//��ֵ
								}
							}
						}
					}else {													//���ж�
						for(int j=0;j<spirittank_miss;j++) {				//ѭ��ִ�б��þ�������
							if(bullet_x-j>=0&&bullet_y+1<37) {				//ȷ���ж�������
								if(World.get(worldNumber).mapmap_move_point003[bullet_x-j][bullet_y+1]!=1) {		//��֤���Ḳ��ǽ��
									World.get(worldNumber).mapmap_move_point003[bullet_x-j][bullet_y+1]=spirittank_miss-j+1;     	//��ֵ
								}
							}
						}
						for(int j=0;j<spirittank_miss;j++) {				//ѭ��ִ�б��þ�������
							if(bullet_x-j>=0) {								//ȷ���ж�������
								if(World.get(worldNumber).mapmap_move_point003[bullet_x-j][bullet_y]!=1) {			//��֤���Ḳ��ǽ��
									World.get(worldNumber).mapmap_move_point003[bullet_x-j][bullet_y]=spirittank_miss-j+1;     		//��ֵ
								}
							}
						}
					}
				}
			}

			
			
			//��������
			int[][] lingpai=new int[48][37];				//ѭ���е��������
			for(int i=0;i<=47;i++) {
				for(int j=0;j<=36;j++) {
					lingpai[i][j]=0;
				}
			}
			while(key==0) {
				key=1;
				for(int i=0;i<=47;i++) {
					for(int j=0;j<=36;j++) {
						if(World.get(worldNumber).mapmap_move_point003[i][j]<-20&&
								lingpai[i][j]==0) {//�ж�Ϊ��ǽ����0���Ǳ�������
							if(j-1>=0) {
								if(World.get(worldNumber).mapmap_move_point003[i][j-1]==0) {
									key=0;
									World.get(worldNumber).mapmap_move_point003[i][j-1]=World.get(worldNumber).mapmap_move_point003[i][j]+1;
									lingpai[i][j-1]=1;
								}
							}
							if(j+1<=36) {
								if(World.get(worldNumber).mapmap_move_point003[i][j+1]==0) {
									key=0;
									World.get(worldNumber).mapmap_move_point003[i][j+1]=World.get(worldNumber).mapmap_move_point003[i][j]+1;
									lingpai[i][j+1]=1;
								}
							}
							if(i-1>=0) {
								if(World.get(worldNumber).mapmap_move_point003[i-1][j]==0) {
									key=0;
									World.get(worldNumber).mapmap_move_point003[i-1][j]=World.get(worldNumber).mapmap_move_point003[i][j]+1;
									lingpai[i-1][j]=1;
								}
							}
							if(i+1<=47) {
								if(World.get(worldNumber).mapmap_move_point003[i+1][j]==0) {
									key=0;
									World.get(worldNumber).mapmap_move_point003[i+1][j]=World.get(worldNumber).mapmap_move_point003[i][j]+1;
									lingpai[i+1][j]=1;
								}
							}
						}
					}
				}
				//��ԭ���㱾�������ж�
				for(int i=0;i<=47;i++) {
					for(int j=0;j<=36;j++) {
						lingpai[i][j]=0;
					}
				}
			}
			
			/*********
			//��ʾ���ֵ�ͼ�������
			System.out.println("003");
			for(int i=0;i<=36;i++) {
				for(int j=0;j<=47;j++) {
					String str1=String.format("% 5d",World.get(worldNumber).mapmap_move_point003[j][i] );
					System.out.printf(str1);
				}
				System.out.println("");
			}
			System.out.println("");
			/*********/
		}
		
		private void DO_map004() {
			this.spirittank_distance002=World.get(worldNumber).move_distance;		//�������߰뾶
			//ʵʱ�������̹���ݶ�����
			//����˲���
			int playertank_block_x=playertank.getX()/17;
			int playertank_block_y=playertank.getY()/17;
			int key=0;											//���ƣ���δ�����κβ���ʱ������ѭ��
			//����Ȩֵ��λ��
			//����һ���Ա���Ȩֵ��
			/*
			if(World.get(worldNumber).mapmap_move_point001[playertank_block_x][playertank_block_y]!=1) {
				World.get(worldNumber).mapmap_move_point001[playertank_block_x][playertank_block_y]=-spirittank_see;//��������ݶ�
			}
			if(World.get(worldNumber).mapmap_move_point001[playertank_block_x+1][playertank_block_y]!=1) {
				World.get(worldNumber).mapmap_move_point001[playertank_block_x+1][playertank_block_y]=-spirittank_see;//��������ݶ�
			}
			if(World.get(worldNumber).mapmap_move_point001[playertank_block_x][playertank_block_y+1]!=1) {
				World.get(worldNumber).mapmap_move_point001[playertank_block_x][playertank_block_y+1]=-spirittank_see;//��������ݶ�
			}
			if(World.get(worldNumber).mapmap_move_point001[playertank_block_x+1][playertank_block_y+1]!=1) {
				World.get(worldNumber).mapmap_move_point001[playertank_block_x+1][playertank_block_y+1]=-spirittank_see;//��������ݶ�
			}
			*/
			//������������ѿ�Ȩֵ��
			//��������1���ж�Ȩֵ�����ϣ�
			if(playertank_block_x+spirittank_distance002<=47&&playertank_block_y-spirittank_distance002>=0) {
				if(World.get(worldNumber).mapmap_move_point004[playertank_block_x+spirittank_distance002][playertank_block_y-spirittank_distance002]!=1) {
					World.get(worldNumber).mapmap_move_point004[playertank_block_x+spirittank_distance002][playertank_block_y-spirittank_distance002]=-spirittank_see;
				}
			}
			//Ȩֵ����2���������
			int d1=5;
			for(int i1=0;i1<d1;i1++) {
				for(int j1=0;j1<d1;j1++) {
					if(playertank_block_x-2+i1>=0&&
							playertank_block_x-2+i1<=47&&
							playertank_block_y-2+j1>=0&&
							playertank_block_y-2+j1<=36) {
						if(World.get(worldNumber).mapmap_move_point004[playertank_block_x-2+i1][playertank_block_y-2+j1]!=1) {
							World.get(worldNumber).mapmap_move_point004[playertank_block_x-2+i1][playertank_block_y-2+j1]=-13;
						}
					}
				}
			}
			int d2=3;
			for(int i1=0;i1<d2;i1++) {
				for(int j1=0;j1<d2;j1++) {
					if(playertank_block_x-1+i1>=0&&
							playertank_block_x-1+i1<=47&&
							playertank_block_y-1+j1>=0&&
							playertank_block_y-1+j1<=36) {
						if(World.get(worldNumber).mapmap_move_point004[playertank_block_x-1+i1][playertank_block_y-1+j1]!=1) {
							World.get(worldNumber).mapmap_move_point004[playertank_block_x-1+i1][playertank_block_y-1+j1]=-12;
						}
					}
				}
			}
			int d3=1;
			if(World.get(worldNumber).mapmap_move_point004[playertank_block_x][playertank_block_y]!=1) {
				World.get(worldNumber).mapmap_move_point004[playertank_block_x][playertank_block_y]=-11;
			}
			//Ȩֵ����3�������ӵ�
			//����������
			for(int i=0;i<World.get(worldNumber).playerbullets.size();i++) {//��������
				Bullet playerbullet=World.get(worldNumber).playerbullets.get(i);
				int bullet_x=playerbullet.getX()/17;
				int bullet_y=playerbullet.getY()/17;
				if(playerbullet.getDiraction()==Spirit.UP) {				/***����Ϊ��***/
					if(playerbullet.getX()%17<=1) {							//���ж�
						for(int j=0;j<spirittank_miss;j++) {				//ѭ��ִ�б��þ�������
							if(bullet_y-j>=0) {								//ȷ���ж�������
								if(World.get(worldNumber).mapmap_move_point004[bullet_x][bullet_y-j]!=1) {		//��֤���Ḳ��ǽ��
									World.get(worldNumber).mapmap_move_point004[bullet_x][bullet_y-j]=spirittank_miss-j+1;		//��ֵ
								}
							}
						}
					}else if(playerbullet.getX()%17>=16) {
						for(int j=0;j<spirittank_miss;j++) {				//ѭ��ִ�б��þ�������
							if(bullet_y-j>=0&&bullet_x+1<48) {				//ȷ���ж�������
								if(World.get(worldNumber).mapmap_move_point004[bullet_x+1][bullet_y-j]!=1) {	//��֤���Ḳ��ǽ��
									World.get(worldNumber).mapmap_move_point004[bullet_x+1][bullet_y-j]=spirittank_miss-j+1;		//��ֵ
								}
							}
						}
					}else {													//���ж�				
						for(int j=0;j<spirittank_miss;j++) {				//ѭ��ִ�б��þ�������
							if(bullet_y-j>=0&&bullet_x+1<48) {				//ȷ���ж�������
								if(World.get(worldNumber).mapmap_move_point004[bullet_x+1][bullet_y-j]!=1) {	//��֤���Ḳ��ǽ��
									World.get(worldNumber).mapmap_move_point004[bullet_x+1][bullet_y-j]=spirittank_miss-j+1;     //��ֵ
								}
							}
						}
						for(int j=0;j<spirittank_miss;j++) {				//ѭ��ִ�б��þ�������
							if(bullet_y-j>=0) {								//ȷ���ж�������
								if(World.get(worldNumber).mapmap_move_point004[bullet_x][bullet_y-j]!=1) {		//��֤���Ḳ��ǽ��
									World.get(worldNumber).mapmap_move_point004[bullet_x][bullet_y-j]=spirittank_miss-j+1;		//��ֵ
								}
							}
						}
					}
				}else if(playerbullet.getDiraction()==Spirit.RIGHT) {		/***����Ϊ��***/
					if(playerbullet.getY()%17<=1) {							//���ж�
						for(int j=0;j<spirittank_miss;j++) {				//ѭ��ִ�б��þ�������
							if(bullet_x+j<48) {								//ȷ���ж�������
								if(World.get(worldNumber).mapmap_move_point004[bullet_x+j][bullet_y]!=1) {		//��֤���Ḳ��ǽ��
									World.get(worldNumber).mapmap_move_point004[bullet_x+j][bullet_y]=spirittank_miss-j+1;     	//��ֵ
								}
							}
						}
					}else if(playerbullet.getY()%17>=16) {
						for(int j=0;j<spirittank_miss;j++) {				//ѭ��ִ�б��þ�������
							if(bullet_x+j<48&&bullet_y+1<37) {				//ȷ���ж�������
								if(World.get(worldNumber).mapmap_move_point004[bullet_x+j][bullet_y+1]!=1) {		//��֤���Ḳ��ǽ��
									World.get(worldNumber).mapmap_move_point004[bullet_x+j][bullet_y+1]=spirittank_miss-j+1;     	//��ֵ
								}
							}
						}
					}else {													//���ж�
						for(int j=0;j<spirittank_miss;j++) {				//ѭ��ִ�б��þ�������
							if(bullet_x+j<48&&bullet_y+1<37) {				//ȷ���ж�������
								if(World.get(worldNumber).mapmap_move_point004[bullet_x+j][bullet_y+1]!=1) {		//��֤���Ḳ��ǽ��
									World.get(worldNumber).mapmap_move_point004[bullet_x+j][bullet_y+1]=spirittank_miss-j+1;     	//��ֵ
								}
							}
						}
						for(int j=0;j<spirittank_miss;j++) {				//ѭ��ִ�б��þ�������
							if(bullet_x+j<48) {								//ȷ���ж�������
								if(World.get(worldNumber).mapmap_move_point004[bullet_x+j][bullet_y]!=1) {			//��֤���Ḳ��ǽ��
									World.get(worldNumber).mapmap_move_point004[bullet_x+j][bullet_y]=spirittank_miss-j+1;     		//��ֵ
								}
							}
						}
					}
				}else if(playerbullet.getDiraction()==Spirit.DOWN) {		/***����Ϊ��***/
					if(playerbullet.getX()%17<=1) {							//���ж�
						for(int j=0;j<spirittank_miss;j++) {				//ѭ��ִ�б��þ�������
							if(bullet_y+j<37) {								//ȷ���ж�������
								if(World.get(worldNumber).mapmap_move_point004[bullet_x][bullet_y+j]!=1) {		//��֤���Ḳ��ǽ��
									World.get(worldNumber).mapmap_move_point004[bullet_x][bullet_y+j]=spirittank_miss-j+1;		//��ֵ
								}
							}
						}
					}else if(playerbullet.getX()%17>=16) {
						for(int j=0;j<spirittank_miss;j++) {				//ѭ��ִ�б��þ�������
							if(bullet_y+j<37&&bullet_x+1<48) {				//ȷ���ж�������
								if(World.get(worldNumber).mapmap_move_point004[bullet_x+1][bullet_y+j]!=1) {	//��֤���Ḳ��ǽ��
									World.get(worldNumber).mapmap_move_point004[bullet_x+1][bullet_y+j]=spirittank_miss-j+1;		//��ֵ
								}
							}
						}
					}else {													//���ж�				
						for(int j=0;j<spirittank_miss;j++) {				//ѭ��ִ�б��þ�������
							if(bullet_y+j<37&&bullet_x+1<48) {				//ȷ���ж�������
								if(World.get(worldNumber).mapmap_move_point004[bullet_x+1][bullet_y+j]!=1) {	//��֤���Ḳ��ǽ��
									World.get(worldNumber).mapmap_move_point004[bullet_x+1][bullet_y+j]=spirittank_miss-j+1;     //��ֵ
								}
							}
						}
						for(int j=0;j<spirittank_miss;j++) {				//ѭ��ִ�б��þ�������
							if(bullet_y+j<37) {								//ȷ���ж�������
								if(World.get(worldNumber).mapmap_move_point004[bullet_x][bullet_y+j]!=1) {		//��֤���Ḳ��ǽ��
									World.get(worldNumber).mapmap_move_point004[bullet_x][bullet_y+j]=spirittank_miss-j+1;		//��ֵ
								}
							}
						}
					}
				}else if(playerbullet.getDiraction()==Spirit.LEFT) {		/***����Ϊ��***/
					if(playerbullet.getY()%17<=1) {							//���ж�
						for(int j=0;j<spirittank_miss;j++) {				//ѭ��ִ�б��þ�������
							if(bullet_x-j>=0) {								//ȷ���ж�������
								if(World.get(worldNumber).mapmap_move_point004[bullet_x-j][bullet_y]!=1) {		//��֤���Ḳ��ǽ��
									World.get(worldNumber).mapmap_move_point004[bullet_x-j][bullet_y]=spirittank_miss-j+1;     	//��ֵ
								}
							}
						}
					}else if(playerbullet.getY()%17>=16) {
						for(int j=0;j<spirittank_miss;j++) {				//ѭ��ִ�б��þ�������
							if(bullet_x-j>=0&&bullet_y+1<37) {				//ȷ���ж�������
								if(World.get(worldNumber).mapmap_move_point004[bullet_x-j][bullet_y+1]!=1) {		//��֤���Ḳ��ǽ��
									World.get(worldNumber).mapmap_move_point004[bullet_x-j][bullet_y+1]=spirittank_miss-j+1;     	//��ֵ
								}
							}
						}
					}else {													//���ж�
						for(int j=0;j<spirittank_miss;j++) {				//ѭ��ִ�б��þ�������
							if(bullet_x-j>=0&&bullet_y+1<37) {				//ȷ���ж�������
								if(World.get(worldNumber).mapmap_move_point004[bullet_x-j][bullet_y+1]!=1) {		//��֤���Ḳ��ǽ��
									World.get(worldNumber).mapmap_move_point004[bullet_x-j][bullet_y+1]=spirittank_miss-j+1;     	//��ֵ
								}
							}
						}
						for(int j=0;j<spirittank_miss;j++) {				//ѭ��ִ�б��þ�������
							if(bullet_x-j>=0) {								//ȷ���ж�������
								if(World.get(worldNumber).mapmap_move_point004[bullet_x-j][bullet_y]!=1) {			//��֤���Ḳ��ǽ��
									World.get(worldNumber).mapmap_move_point004[bullet_x-j][bullet_y]=spirittank_miss-j+1;     		//��ֵ
								}
							}
						}
					}
				}
			}		
			
			//��������
			int[][] lingpai=new int[48][37];				//ѭ���е��������
			for(int i=0;i<=47;i++) {
				for(int j=0;j<=36;j++) {
					lingpai[i][j]=0;
				}
			}
			while(key==0) {
				key=1;
				for(int i=0;i<=47;i++) {
					for(int j=0;j<=36;j++) {
						if(World.get(worldNumber).mapmap_move_point004[i][j]<-20&&
								lingpai[i][j]==0) {//�ж�Ϊ��ǽ����0���Ǳ�������
							if(j-1>=0) {
								if(World.get(worldNumber).mapmap_move_point004[i][j-1]==0) {
									key=0;
									World.get(worldNumber).mapmap_move_point004[i][j-1]=World.get(worldNumber).mapmap_move_point004[i][j]+1;
									lingpai[i][j-1]=1;
								}
							}
							if(j+1<=36) {
								if(World.get(worldNumber).mapmap_move_point004[i][j+1]==0) {
									key=0;
									World.get(worldNumber).mapmap_move_point004[i][j+1]=World.get(worldNumber).mapmap_move_point004[i][j]+1;
									lingpai[i][j+1]=1;
								}
							}
							if(i-1>=0) {
								if(World.get(worldNumber).mapmap_move_point004[i-1][j]==0) {
									key=0;
									World.get(worldNumber).mapmap_move_point004[i-1][j]=World.get(worldNumber).mapmap_move_point004[i][j]+1;
									lingpai[i-1][j]=1;
								}
							}
							if(i+1<=47) {
								if(World.get(worldNumber).mapmap_move_point004[i+1][j]==0) {
									key=0;
									World.get(worldNumber).mapmap_move_point004[i+1][j]=World.get(worldNumber).mapmap_move_point004[i][j]+1;
									lingpai[i+1][j]=1;
								}
							}
						}
					}
				}
				//��ԭ���㱾�������ж�
				for(int i=0;i<=47;i++) {
					for(int j=0;j<=36;j++) {
						lingpai[i][j]=0;
					}
				}
			}



			/*********
			//��ʾ���ֵ�ͼ�������
			System.out.println("004");
			for(int i=0;i<=36;i++) {
				for(int j=0;j<=47;j++) {
					String str1=String.format("% 5d",World.get(worldNumber).mapmap_move_point004[j][i] );
					System.out.printf(str1);
				}
				System.out.println("");
			}
			System.out.println("");
			/*********/
		}

		private void DO_map005() {
			this.spirittank_distance002=World.get(worldNumber).move_distance;
			//ʵʱ�������̹���ݶ�����
			//����˲���
			int playertank_block_x=playertank.getX()/17;
			int playertank_block_y=playertank.getY()/17;
			int key=0;											//���ƣ���δ�����κβ���ʱ������ѭ��
			//����Ȩֵ��λ��
			//����һ���Ա���Ȩֵ��
			/*
			if(World.get(worldNumber).mapmap_move_point001[playertank_block_x][playertank_block_y]!=1) {
				World.get(worldNumber).mapmap_move_point001[playertank_block_x][playertank_block_y]=-spirittank_see;//��������ݶ�
			}
			if(World.get(worldNumber).mapmap_move_point001[playertank_block_x+1][playertank_block_y]!=1) {
				World.get(worldNumber).mapmap_move_point001[playertank_block_x+1][playertank_block_y]=-spirittank_see;//��������ݶ�
			}
			if(World.get(worldNumber).mapmap_move_point001[playertank_block_x][playertank_block_y+1]!=1) {
				World.get(worldNumber).mapmap_move_point001[playertank_block_x][playertank_block_y+1]=-spirittank_see;//��������ݶ�
			}
			if(World.get(worldNumber).mapmap_move_point001[playertank_block_x+1][playertank_block_y+1]!=1) {
				World.get(worldNumber).mapmap_move_point001[playertank_block_x+1][playertank_block_y+1]=-spirittank_see;//��������ݶ�
			}
			*/
			//������������ѿ�Ȩֵ��
			//��������1���ж�Ȩֵ���ң�
			if(playertank_block_x+spirittank_distance002<=47) {
				if(World.get(worldNumber).mapmap_move_point005[playertank_block_x+spirittank_distance002][playertank_block_y]!=1) {
					World.get(worldNumber).mapmap_move_point005[playertank_block_x+spirittank_distance002][playertank_block_y]=-spirittank_see;
				}
			}
			//Ȩֵ����2���������
			int d1=5;
			for(int i1=0;i1<d1;i1++) {
				for(int j1=0;j1<d1;j1++) {
					if(playertank_block_x-2+i1>=0&&
							playertank_block_x-2+i1<=47&&
							playertank_block_y-2+j1>=0&&
							playertank_block_y-2+j1<=36) {
						if(World.get(worldNumber).mapmap_move_point005[playertank_block_x-2+i1][playertank_block_y-2+j1]!=1) {
							World.get(worldNumber).mapmap_move_point005[playertank_block_x-2+i1][playertank_block_y-2+j1]=-13;
						}
					}
				}
			}
			int d2=3;
			for(int i1=0;i1<d2;i1++) {
				for(int j1=0;j1<d2;j1++) {
					if(playertank_block_x-1+i1>=0&&
							playertank_block_x-1+i1<=47&&
							playertank_block_y-1+j1>=0&&
							playertank_block_y-1+j1<=36) {
						if(World.get(worldNumber).mapmap_move_point005[playertank_block_x-1+i1][playertank_block_y-1+j1]!=1) {
							World.get(worldNumber).mapmap_move_point005[playertank_block_x-1+i1][playertank_block_y-1+j1]=-12;
						}
					}
				}
			}
			int d3=1;
			if(World.get(worldNumber).mapmap_move_point005[playertank_block_x][playertank_block_y]!=1) {
				World.get(worldNumber).mapmap_move_point005[playertank_block_x][playertank_block_y]=-11;
			}	
			//Ȩֵ����2���������
			//����������
			for(int i=0;i<World.get(worldNumber).playerbullets.size();i++) {//��������
				Bullet playerbullet=World.get(worldNumber).playerbullets.get(i);
				int bullet_x=playerbullet.getX()/17;
				int bullet_y=playerbullet.getY()/17;
				if(playerbullet.getDiraction()==Spirit.UP) {				/***����Ϊ��***/
					if(playerbullet.getX()%17<=1) {							//���ж�
						for(int j=0;j<spirittank_miss;j++) {				//ѭ��ִ�б��þ�������
							if(bullet_y-j>=0) {								//ȷ���ж�������
								if(World.get(worldNumber).mapmap_move_point005[bullet_x][bullet_y-j]!=1) {		//��֤���Ḳ��ǽ��
									World.get(worldNumber).mapmap_move_point005[bullet_x][bullet_y-j]=spirittank_miss-j+1;		//��ֵ
								}
							}
						}
					}else if(playerbullet.getX()%17>=16) {
						for(int j=0;j<spirittank_miss;j++) {				//ѭ��ִ�б��þ�������
							if(bullet_y-j>=0&&bullet_x+1<48) {				//ȷ���ж�������
								if(World.get(worldNumber).mapmap_move_point005[bullet_x+1][bullet_y-j]!=1) {	//��֤���Ḳ��ǽ��
									World.get(worldNumber).mapmap_move_point005[bullet_x+1][bullet_y-j]=spirittank_miss-j+1;		//��ֵ
								}
							}
						}
					}else {													//���ж�				
						for(int j=0;j<spirittank_miss;j++) {				//ѭ��ִ�б��þ�������
							if(bullet_y-j>=0&&bullet_x+1<48) {				//ȷ���ж�������
								if(World.get(worldNumber).mapmap_move_point005[bullet_x+1][bullet_y-j]!=1) {	//��֤���Ḳ��ǽ��
									World.get(worldNumber).mapmap_move_point005[bullet_x+1][bullet_y-j]=spirittank_miss-j+1;     //��ֵ
								}
							}
						}
						for(int j=0;j<spirittank_miss;j++) {				//ѭ��ִ�б��þ�������
							if(bullet_y-j>=0) {								//ȷ���ж�������
								if(World.get(worldNumber).mapmap_move_point005[bullet_x][bullet_y-j]!=1) {		//��֤���Ḳ��ǽ��
									World.get(worldNumber).mapmap_move_point005[bullet_x][bullet_y-j]=spirittank_miss-j+1;		//��ֵ
								}
							}
						}
					}
				}else if(playerbullet.getDiraction()==Spirit.RIGHT) {		/***����Ϊ��***/
					if(playerbullet.getY()%17<=1) {							//���ж�
						for(int j=0;j<spirittank_miss;j++) {				//ѭ��ִ�б��þ�������
							if(bullet_x+j<48) {								//ȷ���ж�������
								if(World.get(worldNumber).mapmap_move_point005[bullet_x+j][bullet_y]!=1) {		//��֤���Ḳ��ǽ��
									World.get(worldNumber).mapmap_move_point005[bullet_x+j][bullet_y]=spirittank_miss-j+1;     	//��ֵ
								}
							}
						}
					}else if(playerbullet.getY()%17>=16) {
						for(int j=0;j<spirittank_miss;j++) {				//ѭ��ִ�б��þ�������
							if(bullet_x+j<48&&bullet_y+1<37) {				//ȷ���ж�������
								if(World.get(worldNumber).mapmap_move_point005[bullet_x+j][bullet_y+1]!=1) {		//��֤���Ḳ��ǽ��
									World.get(worldNumber).mapmap_move_point005[bullet_x+j][bullet_y+1]=spirittank_miss-j+1;     	//��ֵ
								}
							}
						}
					}else {													//���ж�
						for(int j=0;j<spirittank_miss;j++) {				//ѭ��ִ�б��þ�������
							if(bullet_x+j<48&&bullet_y+1<37) {				//ȷ���ж�������
								if(World.get(worldNumber).mapmap_move_point005[bullet_x+j][bullet_y+1]!=1) {		//��֤���Ḳ��ǽ��
									World.get(worldNumber).mapmap_move_point005[bullet_x+j][bullet_y+1]=spirittank_miss-j+1;     	//��ֵ
								}
							}
						}
						for(int j=0;j<spirittank_miss;j++) {				//ѭ��ִ�б��þ�������
							if(bullet_x+j<48) {								//ȷ���ж�������
								if(World.get(worldNumber).mapmap_move_point005[bullet_x+j][bullet_y]!=1) {			//��֤���Ḳ��ǽ��
									World.get(worldNumber).mapmap_move_point005[bullet_x+j][bullet_y]=spirittank_miss-j+1;     		//��ֵ
								}
							}
						}
					}
				}else if(playerbullet.getDiraction()==Spirit.DOWN) {		/***����Ϊ��***/
					if(playerbullet.getX()%17<=1) {							//���ж�
						for(int j=0;j<spirittank_miss;j++) {				//ѭ��ִ�б��þ�������
							if(bullet_y+j<37) {								//ȷ���ж�������
								if(World.get(worldNumber).mapmap_move_point005[bullet_x][bullet_y+j]!=1) {		//��֤���Ḳ��ǽ��
									World.get(worldNumber).mapmap_move_point005[bullet_x][bullet_y+j]=spirittank_miss-j+1;		//��ֵ
								}
							}
						}
					}else if(playerbullet.getX()%17>=16) {
						for(int j=0;j<spirittank_miss;j++) {				//ѭ��ִ�б��þ�������
							if(bullet_y+j<37&&bullet_x+1<48) {				//ȷ���ж�������
								if(World.get(worldNumber).mapmap_move_point005[bullet_x+1][bullet_y+j]!=1) {	//��֤���Ḳ��ǽ��
									World.get(worldNumber).mapmap_move_point005[bullet_x+1][bullet_y+j]=spirittank_miss-j+1;		//��ֵ
								}
							}
						}
					}else {													//���ж�				
						for(int j=0;j<spirittank_miss;j++) {				//ѭ��ִ�б��þ�������
							if(bullet_y+j<37&&bullet_x+1<48) {				//ȷ���ж�������
								if(World.get(worldNumber).mapmap_move_point005[bullet_x+1][bullet_y+j]!=1) {	//��֤���Ḳ��ǽ��
									World.get(worldNumber).mapmap_move_point005[bullet_x+1][bullet_y+j]=spirittank_miss-j+1;     //��ֵ
								}
							}
						}
						for(int j=0;j<spirittank_miss;j++) {				//ѭ��ִ�б��þ�������
							if(bullet_y+j<37) {								//ȷ���ж�������
								if(World.get(worldNumber).mapmap_move_point005[bullet_x][bullet_y+j]!=1) {		//��֤���Ḳ��ǽ��
									World.get(worldNumber).mapmap_move_point005[bullet_x][bullet_y+j]=spirittank_miss-j+1;		//��ֵ
								}
							}
						}
					}
				}else if(playerbullet.getDiraction()==Spirit.LEFT) {		/***����Ϊ��***/
					if(playerbullet.getY()%17<=1) {							//���ж�
						for(int j=0;j<spirittank_miss;j++) {				//ѭ��ִ�б��þ�������
							if(bullet_x-j>=0) {								//ȷ���ж�������
								if(World.get(worldNumber).mapmap_move_point005[bullet_x-j][bullet_y]!=1) {		//��֤���Ḳ��ǽ��
									World.get(worldNumber).mapmap_move_point005[bullet_x-j][bullet_y]=spirittank_miss-j+1;     	//��ֵ
								}
							}
						}
					}else if(playerbullet.getY()%17>=16) {
						for(int j=0;j<spirittank_miss;j++) {				//ѭ��ִ�б��þ�������
							if(bullet_x-j>=0&&bullet_y+1<37) {				//ȷ���ж�������
								if(World.get(worldNumber).mapmap_move_point005[bullet_x-j][bullet_y+1]!=1) {		//��֤���Ḳ��ǽ��
									World.get(worldNumber).mapmap_move_point005[bullet_x-j][bullet_y+1]=spirittank_miss-j+1;     	//��ֵ
								}
							}
						}
					}else {													//���ж�
						for(int j=0;j<spirittank_miss;j++) {				//ѭ��ִ�б��þ�������
							if(bullet_x-j>=0&&bullet_y+1<37) {				//ȷ���ж�������
								if(World.get(worldNumber).mapmap_move_point005[bullet_x-j][bullet_y+1]!=1) {		//��֤���Ḳ��ǽ��
									World.get(worldNumber).mapmap_move_point005[bullet_x-j][bullet_y+1]=spirittank_miss-j+1;     	//��ֵ
								}
							}
						}
						for(int j=0;j<spirittank_miss;j++) {				//ѭ��ִ�б��þ�������
							if(bullet_x-j>=0) {								//ȷ���ж�������
								if(World.get(worldNumber).mapmap_move_point005[bullet_x-j][bullet_y]!=1) {			//��֤���Ḳ��ǽ��
									World.get(worldNumber).mapmap_move_point005[bullet_x-j][bullet_y]=spirittank_miss-j+1;     		//��ֵ
								}
							}
						}
					}
				}
			}

			
			
			//��������
			int[][] lingpai=new int[48][37];				//ѭ���е��������
			for(int i=0;i<=47;i++) {
				for(int j=0;j<=36;j++) {
					lingpai[i][j]=0;
				}
			}
			while(key==0) {
				key=1;
				for(int i=0;i<=47;i++) {
					for(int j=0;j<=36;j++) {
						if(World.get(worldNumber).mapmap_move_point005[i][j]<-20&&
								lingpai[i][j]==0) {//�ж�Ϊ��ǽ����0���Ǳ�������
							if(j-1>=0) {
								if(World.get(worldNumber).mapmap_move_point005[i][j-1]==0) {
									key=0;
									World.get(worldNumber).mapmap_move_point005[i][j-1]=World.get(worldNumber).mapmap_move_point005[i][j]+1;
									lingpai[i][j-1]=1;
								}
							}
							if(j+1<=36) {
								if(World.get(worldNumber).mapmap_move_point005[i][j+1]==0) {
									key=0;
									World.get(worldNumber).mapmap_move_point005[i][j+1]=World.get(worldNumber).mapmap_move_point005[i][j]+1;
									lingpai[i][j+1]=1;
								}
							}
							if(i-1>=0) {
								if(World.get(worldNumber).mapmap_move_point005[i-1][j]==0) {
									key=0;
									World.get(worldNumber).mapmap_move_point005[i-1][j]=World.get(worldNumber).mapmap_move_point005[i][j]+1;
									lingpai[i-1][j]=1;
								}
							}
							if(i+1<=47) {
								if(World.get(worldNumber).mapmap_move_point005[i+1][j]==0) {
									key=0;
									World.get(worldNumber).mapmap_move_point005[i+1][j]=World.get(worldNumber).mapmap_move_point005[i][j]+1;
									lingpai[i+1][j]=1;
								}
							}
						}
					}
				}
				//��ԭ���㱾�������ж�
				for(int i=0;i<=47;i++) {
					for(int j=0;j<=36;j++) {
						lingpai[i][j]=0;
					}
				}
			}
			
			/*********
			System.out.println("005");
			//��ʾ���ֵ�ͼ�������
			for(int i=0;i<=36;i++) {
				for(int j=0;j<=47;j++) {
					String str1=String.format("% 5d",World.get(worldNumber).mapmap_move_point005[j][i] );
					System.out.printf(str1);
				}
				System.out.println("");
			}
			System.out.println("");
			/*********/
		}
		
		private void DO_map006() {
			this.spirittank_distance002=World.get(worldNumber).move_distance;		//�������߰뾶
			//ʵʱ�������̹���ݶ�����
			//����˲���
			int playertank_block_x=playertank.getX()/17;
			int playertank_block_y=playertank.getY()/17;
			int key=0;											//���ƣ���δ�����κβ���ʱ������ѭ��
			//����Ȩֵ��λ��
			//��������1���ж�Ȩֵ
			//Ȩֵ�㼯�������·�
			if(playertank_block_x+spirittank_distance002<=47&&playertank_block_y+spirittank_distance002<=36) {
				if(World.get(worldNumber).mapmap_move_point006[playertank_block_x+spirittank_distance002][playertank_block_y+spirittank_distance002]!=1) {
					World.get(worldNumber).mapmap_move_point006[playertank_block_x+spirittank_distance002][playertank_block_y+spirittank_distance002]=-spirittank_see;
				}
			}
			//Ȩֵ����2���������
			int d1=5;
			for(int i1=0;i1<d1;i1++) {
				for(int j1=0;j1<d1;j1++) {
					if(playertank_block_x-2+i1>=0&&
							playertank_block_x-2+i1<=47&&
							playertank_block_y-2+j1>=0&&
							playertank_block_y-2+j1<=36) {
						if(World.get(worldNumber).mapmap_move_point006[playertank_block_x-2+i1][playertank_block_y-2+j1]!=1) {
							World.get(worldNumber).mapmap_move_point006[playertank_block_x-2+i1][playertank_block_y-2+j1]=-13;
						}
					}
				}
			}
			int d2=3;
			for(int i1=0;i1<d2;i1++) {
				for(int j1=0;j1<d2;j1++) {
					if(playertank_block_x-1+i1>=0&&
							playertank_block_x-1+i1<=47&&
							playertank_block_y-1+j1>=0&&
							playertank_block_y-1+j1<=36) {
						if(World.get(worldNumber).mapmap_move_point006[playertank_block_x-1+i1][playertank_block_y-1+j1]!=1) {
							World.get(worldNumber).mapmap_move_point006[playertank_block_x-1+i1][playertank_block_y-1+j1]=-12;
						}
					}
				}
			}
			int d3=1;
			if(World.get(worldNumber).mapmap_move_point006[playertank_block_x][playertank_block_y]!=1) {
				World.get(worldNumber).mapmap_move_point006[playertank_block_x][playertank_block_y]=-11;
			}		
			//Ȩֵ����3�������ӵ�
			for(int i=0;i<World.get(worldNumber).playerbullets.size();i++) {//��������
				Bullet playerbullet=World.get(worldNumber).playerbullets.get(i);
				int bullet_x=playerbullet.getX()/17;
				int bullet_y=playerbullet.getY()/17;
				if(playerbullet.getDiraction()==Spirit.UP) {				/***����Ϊ��***/
					if(playerbullet.getX()%17<=1) {							//���ж�
						for(int j=0;j<spirittank_miss;j++) {				//ѭ��ִ�б��þ�������
							if(bullet_y-j>=0) {								//ȷ���ж�������
								if(World.get(worldNumber).mapmap_move_point006[bullet_x][bullet_y-j]!=1) {		//��֤���Ḳ��ǽ��
									World.get(worldNumber).mapmap_move_point006[bullet_x][bullet_y-j]=spirittank_miss-j+1;		//��ֵ
								}
							}
						}
					}else if(playerbullet.getX()%17>=16) {
						for(int j=0;j<spirittank_miss;j++) {				//ѭ��ִ�б��þ�������
							if(bullet_y-j>=0&&bullet_x+1<48) {				//ȷ���ж�������
								if(World.get(worldNumber).mapmap_move_point006[bullet_x+1][bullet_y-j]!=1) {	//��֤���Ḳ��ǽ��
									World.get(worldNumber).mapmap_move_point006[bullet_x+1][bullet_y-j]=spirittank_miss-j+1;		//��ֵ
								}
							}
						}
					}else {													//���ж�				
						for(int j=0;j<spirittank_miss;j++) {				//ѭ��ִ�б��þ�������
							if(bullet_y-j>=0&&bullet_x+1<48) {				//ȷ���ж�������
								if(World.get(worldNumber).mapmap_move_point006[bullet_x+1][bullet_y-j]!=1) {	//��֤���Ḳ��ǽ��
									World.get(worldNumber).mapmap_move_point006[bullet_x+1][bullet_y-j]=spirittank_miss-j+1;     //��ֵ
								}
							}
						}
						for(int j=0;j<spirittank_miss;j++) {				//ѭ��ִ�б��þ�������
							if(bullet_y-j>=0) {								//ȷ���ж�������
								if(World.get(worldNumber).mapmap_move_point006[bullet_x][bullet_y-j]!=1) {		//��֤���Ḳ��ǽ��
									World.get(worldNumber).mapmap_move_point006[bullet_x][bullet_y-j]=spirittank_miss-j+1;		//��ֵ
								}
							}
						}
					}
				}else if(playerbullet.getDiraction()==Spirit.RIGHT) {		/***����Ϊ��***/
					if(playerbullet.getY()%17<=1) {							//���ж�
						for(int j=0;j<spirittank_miss;j++) {				//ѭ��ִ�б��þ�������
							if(bullet_x+j<48) {								//ȷ���ж�������
								if(World.get(worldNumber).mapmap_move_point006[bullet_x+j][bullet_y]!=1) {		//��֤���Ḳ��ǽ��
									World.get(worldNumber).mapmap_move_point006[bullet_x+j][bullet_y]=spirittank_miss-j+1;     	//��ֵ
								}
							}
						}
					}else if(playerbullet.getY()%17>=16) {
						for(int j=0;j<spirittank_miss;j++) {				//ѭ��ִ�б��þ�������
							if(bullet_x+j<48&&bullet_y+1<37) {				//ȷ���ж�������
								if(World.get(worldNumber).mapmap_move_point006[bullet_x+j][bullet_y+1]!=1) {		//��֤���Ḳ��ǽ��
									World.get(worldNumber).mapmap_move_point006[bullet_x+j][bullet_y+1]=spirittank_miss-j+1;     	//��ֵ
								}
							}
						}
					}else {													//���ж�
						for(int j=0;j<spirittank_miss;j++) {				//ѭ��ִ�б��þ�������
							if(bullet_x+j<48&&bullet_y+1<37) {				//ȷ���ж�������
								if(World.get(worldNumber).mapmap_move_point006[bullet_x+j][bullet_y+1]!=1) {		//��֤���Ḳ��ǽ��
									World.get(worldNumber).mapmap_move_point006[bullet_x+j][bullet_y+1]=spirittank_miss-j+1;     	//��ֵ
								}
							}
						}
						for(int j=0;j<spirittank_miss;j++) {				//ѭ��ִ�б��þ�������
							if(bullet_x+j<48) {								//ȷ���ж�������
								if(World.get(worldNumber).mapmap_move_point006[bullet_x+j][bullet_y]!=1) {			//��֤���Ḳ��ǽ��
									World.get(worldNumber).mapmap_move_point006[bullet_x+j][bullet_y]=spirittank_miss-j+1;     		//��ֵ
								}
							}
						}
					}
				}else if(playerbullet.getDiraction()==Spirit.DOWN) {		/***����Ϊ��***/
					if(playerbullet.getX()%17<=1) {							//���ж�
						for(int j=0;j<spirittank_miss;j++) {				//ѭ��ִ�б��þ�������
							if(bullet_y+j<37) {								//ȷ���ж�������
								if(World.get(worldNumber).mapmap_move_point006[bullet_x][bullet_y+j]!=1) {		//��֤���Ḳ��ǽ��
									World.get(worldNumber).mapmap_move_point006[bullet_x][bullet_y+j]=spirittank_miss-j+1;		//��ֵ
								}
							}
						}
					}else if(playerbullet.getX()%17>=16) {
						for(int j=0;j<spirittank_miss;j++) {				//ѭ��ִ�б��þ�������
							if(bullet_y+j<37&&bullet_x+1<48) {				//ȷ���ж�������
								if(World.get(worldNumber).mapmap_move_point006[bullet_x+1][bullet_y+j]!=1) {	//��֤���Ḳ��ǽ��
									World.get(worldNumber).mapmap_move_point006[bullet_x+1][bullet_y+j]=spirittank_miss-j+1;		//��ֵ
								}
							}
						}
					}else {													//���ж�				
						for(int j=0;j<spirittank_miss;j++) {				//ѭ��ִ�б��þ�������
							if(bullet_y+j<37&&bullet_x+1<48) {				//ȷ���ж�������
								if(World.get(worldNumber).mapmap_move_point006[bullet_x+1][bullet_y+j]!=1) {	//��֤���Ḳ��ǽ��
									World.get(worldNumber).mapmap_move_point006[bullet_x+1][bullet_y+j]=spirittank_miss-j+1;     //��ֵ
								}
							}
						}
						for(int j=0;j<spirittank_miss;j++) {				//ѭ��ִ�б��þ�������
							if(bullet_y+j<37) {								//ȷ���ж�������
								if(World.get(worldNumber).mapmap_move_point006[bullet_x][bullet_y+j]!=1) {		//��֤���Ḳ��ǽ��
									World.get(worldNumber).mapmap_move_point006[bullet_x][bullet_y+j]=spirittank_miss-j+1;		//��ֵ
								}
							}
						}
					}
				}else if(playerbullet.getDiraction()==Spirit.LEFT) {		/***����Ϊ��***/
					if(playerbullet.getY()%17<=1) {							//���ж�
						for(int j=0;j<spirittank_miss;j++) {				//ѭ��ִ�б��þ�������
							if(bullet_x-j>=0) {								//ȷ���ж�������
								if(World.get(worldNumber).mapmap_move_point006[bullet_x-j][bullet_y]!=1) {		//��֤���Ḳ��ǽ��
									World.get(worldNumber).mapmap_move_point006[bullet_x-j][bullet_y]=spirittank_miss-j+1;     	//��ֵ
								}
							}
						}
					}else if(playerbullet.getY()%17>=16) {
						for(int j=0;j<spirittank_miss;j++) {				//ѭ��ִ�б��þ�������
							if(bullet_x-j>=0&&bullet_y+1<37) {				//ȷ���ж�������
								if(World.get(worldNumber).mapmap_move_point006[bullet_x-j][bullet_y+1]!=1) {		//��֤���Ḳ��ǽ��
									World.get(worldNumber).mapmap_move_point006[bullet_x-j][bullet_y+1]=spirittank_miss-j+1;     	//��ֵ
								}
							}
						}
					}else {													//���ж�
						for(int j=0;j<spirittank_miss;j++) {				//ѭ��ִ�б��þ�������
							if(bullet_x-j>=0&&bullet_y+1<37) {				//ȷ���ж�������
								if(World.get(worldNumber).mapmap_move_point006[bullet_x-j][bullet_y+1]!=1) {		//��֤���Ḳ��ǽ��
									World.get(worldNumber).mapmap_move_point006[bullet_x-j][bullet_y+1]=spirittank_miss-j+1;     	//��ֵ
								}
							}
						}
						for(int j=0;j<spirittank_miss;j++) {				//ѭ��ִ�б��þ�������
							if(bullet_x-j>=0) {								//ȷ���ж�������
								if(World.get(worldNumber).mapmap_move_point006[bullet_x-j][bullet_y]!=1) {			//��֤���Ḳ��ǽ��
									World.get(worldNumber).mapmap_move_point006[bullet_x-j][bullet_y]=spirittank_miss-j+1;     		//��ֵ
								}
							}
						}
					}
				}
			}
			
			//��������
			int[][] lingpai=new int[48][37];				//ѭ���е��������
			for(int i=0;i<=47;i++) {
				for(int j=0;j<=36;j++) {
					lingpai[i][j]=0;
				}
			}
			while(key==0) {
				key=1;
				for(int i=0;i<=47;i++) {
					for(int j=0;j<=36;j++) {
						if(World.get(worldNumber).mapmap_move_point006[i][j]<-20&&
								lingpai[i][j]==0) {//�ж�Ϊ��ǽ����0���Ǳ�������
							if(j-1>=0) {
								if(World.get(worldNumber).mapmap_move_point006[i][j-1]==0) {
									key=0;
									World.get(worldNumber).mapmap_move_point006[i][j-1]=World.get(worldNumber).mapmap_move_point006[i][j]+1;
									lingpai[i][j-1]=1;
								}
							}
							if(j+1<=36) {
								if(World.get(worldNumber).mapmap_move_point006[i][j+1]==0) {
									key=0;
									World.get(worldNumber).mapmap_move_point006[i][j+1]=World.get(worldNumber).mapmap_move_point006[i][j]+1;
									lingpai[i][j+1]=1;
								}
							}
							if(i-1>=0) {
								if(World.get(worldNumber).mapmap_move_point006[i-1][j]==0) {
									key=0;
									World.get(worldNumber).mapmap_move_point006[i-1][j]=World.get(worldNumber).mapmap_move_point006[i][j]+1;
									lingpai[i-1][j]=1;
								}
							}
							if(i+1<=47) {
								if(World.get(worldNumber).mapmap_move_point006[i+1][j]==0) {
									key=0;
									World.get(worldNumber).mapmap_move_point006[i+1][j]=World.get(worldNumber).mapmap_move_point006[i][j]+1;
									lingpai[i+1][j]=1;
								}
							}
						}
					}
				}
				//��ԭ���㱾�������ж�
				for(int i=0;i<=47;i++) {
					for(int j=0;j<=36;j++) {
						lingpai[i][j]=0;
					}
				}
			}
			
		
			/*********
			//��ʾ���ֵ�ͼ�������
			System.out.println("006");
			for(int i=0;i<=36;i++) {
				for(int j=0;j<=47;j++) {
					String str1=String.format("% 5d",World.get(worldNumber).mapmap_move_point006[j][i] );
					System.out.printf(str1);
				}
				System.out.println("");
			}
			System.out.println("");
			/*********/
		}
		
		private void DO_map007() {
			this.spirittank_distance002=World.get(worldNumber).move_distance;		//�������߰뾶
			//ʵʱ�������̹���ݶ�����
			//����˲���
			int playertank_block_x=playertank.getX()/17;
			int playertank_block_y=playertank.getY()/17;
			int key=0;											//���ƣ���δ�����κβ���ʱ������ѭ��
			//����Ȩֵ��λ��
			//����һ���Ա���Ȩֵ��
			/*
			if(World.get(worldNumber).mapmap_move_point001[playertank_block_x][playertank_block_y]!=1) {
				World.get(worldNumber).mapmap_move_point001[playertank_block_x][playertank_block_y]=-spirittank_see;//��������ݶ�
			}
			if(World.get(worldNumber).mapmap_move_point001[playertank_block_x+1][playertank_block_y]!=1) {
				World.get(worldNumber).mapmap_move_point001[playertank_block_x+1][playertank_block_y]=-spirittank_see;//��������ݶ�
			}
			if(World.get(worldNumber).mapmap_move_point001[playertank_block_x][playertank_block_y+1]!=1) {
				World.get(worldNumber).mapmap_move_point001[playertank_block_x][playertank_block_y+1]=-spirittank_see;//��������ݶ�
			}
			if(World.get(worldNumber).mapmap_move_point001[playertank_block_x+1][playertank_block_y+1]!=1) {
				World.get(worldNumber).mapmap_move_point001[playertank_block_x+1][playertank_block_y+1]=-spirittank_see;//��������ݶ�
			}
			*/
			//������������ѿ�Ȩֵ��
			//��������1���ж�Ȩֵ
			//Ȩֵ�㼯�����·�
			if(playertank_block_y+spirittank_distance002<=36) {
				if(World.get(worldNumber).mapmap_move_point007[playertank_block_x][playertank_block_y+spirittank_distance002]!=1) {
					World.get(worldNumber).mapmap_move_point007[playertank_block_x][playertank_block_y+spirittank_distance002]=-spirittank_see;
				}
			}
			//Ȩֵ����2���������
			int d1=5;
			for(int i1=0;i1<d1;i1++) {
				for(int j1=0;j1<d1;j1++) {
					if(playertank_block_x-2+i1>=0&&
							playertank_block_x-2+i1<=47&&
							playertank_block_y-2+j1>=0&&
							playertank_block_y-2+j1<=36) {
						if(World.get(worldNumber).mapmap_move_point007[playertank_block_x-2+i1][playertank_block_y-2+j1]!=1) {
							World.get(worldNumber).mapmap_move_point007[playertank_block_x-2+i1][playertank_block_y-2+j1]=-13;
						}
					}
				}
			}
			int d2=3;
			for(int i1=0;i1<d2;i1++) {
				for(int j1=0;j1<d2;j1++) {
					if(playertank_block_x-1+i1>=0&&
							playertank_block_x-1+i1<=47&&
							playertank_block_y-1+j1>=0&&
							playertank_block_y-1+j1<=36) {
						if(World.get(worldNumber).mapmap_move_point007[playertank_block_x-1+i1][playertank_block_y-1+j1]!=1) {
							World.get(worldNumber).mapmap_move_point007[playertank_block_x-1+i1][playertank_block_y-1+j1]=-12;
						}
					}
				}
			}
			int d3=1;
			if(World.get(worldNumber).mapmap_move_point007[playertank_block_x][playertank_block_y]!=1) {
				World.get(worldNumber).mapmap_move_point007[playertank_block_x][playertank_block_y]=-11;
			}
			//Ȩֵ����3�������ӵ�
			//����������
			for(int i=0;i<World.get(worldNumber).playerbullets.size();i++) {//��������
				Bullet playerbullet=World.get(worldNumber).playerbullets.get(i);
				int bullet_x=playerbullet.getX()/17;
				int bullet_y=playerbullet.getY()/17;
				if(playerbullet.getDiraction()==Spirit.UP) {				/***����Ϊ��***/
					if(playerbullet.getX()%17<=1) {							//���ж�
						for(int j=0;j<spirittank_miss;j++) {				//ѭ��ִ�б��þ�������
							if(bullet_y-j>=0) {								//ȷ���ж�������
								if(World.get(worldNumber).mapmap_move_point007[bullet_x][bullet_y-j]!=1) {		//��֤���Ḳ��ǽ��
									World.get(worldNumber).mapmap_move_point007[bullet_x][bullet_y-j]=spirittank_miss-j+1;		//��ֵ
								}
							}
						}
					}else if(playerbullet.getX()%17>=16) {
						for(int j=0;j<spirittank_miss;j++) {				//ѭ��ִ�б��þ�������
							if(bullet_y-j>=0&&bullet_x+1<48) {				//ȷ���ж�������
								if(World.get(worldNumber).mapmap_move_point007[bullet_x+1][bullet_y-j]!=1) {	//��֤���Ḳ��ǽ��
									World.get(worldNumber).mapmap_move_point007[bullet_x+1][bullet_y-j]=spirittank_miss-j+1;		//��ֵ
								}
							}
						}
					}else {													//���ж�				
						for(int j=0;j<spirittank_miss;j++) {				//ѭ��ִ�б��þ�������
							if(bullet_y-j>=0&&bullet_x+1<48) {				//ȷ���ж�������
								if(World.get(worldNumber).mapmap_move_point007[bullet_x+1][bullet_y-j]!=1) {	//��֤���Ḳ��ǽ��
									World.get(worldNumber).mapmap_move_point007[bullet_x+1][bullet_y-j]=spirittank_miss-j+1;     //��ֵ
								}
							}
						}
						for(int j=0;j<spirittank_miss;j++) {				//ѭ��ִ�б��þ�������
							if(bullet_y-j>=0) {								//ȷ���ж�������
								if(World.get(worldNumber).mapmap_move_point007[bullet_x][bullet_y-j]!=1) {		//��֤���Ḳ��ǽ��
									World.get(worldNumber).mapmap_move_point007[bullet_x][bullet_y-j]=spirittank_miss-j+1;		//��ֵ
								}
							}
						}
					}
				}else if(playerbullet.getDiraction()==Spirit.RIGHT) {		/***����Ϊ��***/
					if(playerbullet.getY()%17<=1) {							//���ж�
						for(int j=0;j<spirittank_miss;j++) {				//ѭ��ִ�б��þ�������
							if(bullet_x+j<48) {								//ȷ���ж�������
								if(World.get(worldNumber).mapmap_move_point007[bullet_x+j][bullet_y]!=1) {		//��֤���Ḳ��ǽ��
									World.get(worldNumber).mapmap_move_point007[bullet_x+j][bullet_y]=spirittank_miss-j+1;     	//��ֵ
								}
							}
						}
					}else if(playerbullet.getY()%17>=16) {
						for(int j=0;j<spirittank_miss;j++) {				//ѭ��ִ�б��þ�������
							if(bullet_x+j<48&&bullet_y+1<37) {				//ȷ���ж�������
								if(World.get(worldNumber).mapmap_move_point007[bullet_x+j][bullet_y+1]!=1) {		//��֤���Ḳ��ǽ��
									World.get(worldNumber).mapmap_move_point007[bullet_x+j][bullet_y+1]=spirittank_miss-j+1;     	//��ֵ
								}
							}
						}
					}else {													//���ж�
						for(int j=0;j<spirittank_miss;j++) {				//ѭ��ִ�б��þ�������
							if(bullet_x+j<48&&bullet_y+1<37) {				//ȷ���ж�������
								if(World.get(worldNumber).mapmap_move_point007[bullet_x+j][bullet_y+1]!=1) {		//��֤���Ḳ��ǽ��
									World.get(worldNumber).mapmap_move_point007[bullet_x+j][bullet_y+1]=spirittank_miss-j+1;     	//��ֵ
								}
							}
						}
						for(int j=0;j<spirittank_miss;j++) {				//ѭ��ִ�б��þ�������
							if(bullet_x+j<48) {								//ȷ���ж�������
								if(World.get(worldNumber).mapmap_move_point007[bullet_x+j][bullet_y]!=1) {			//��֤���Ḳ��ǽ��
									World.get(worldNumber).mapmap_move_point007[bullet_x+j][bullet_y]=spirittank_miss-j+1;     		//��ֵ
								}
							}
						}
					}
				}else if(playerbullet.getDiraction()==Spirit.DOWN) {		/***����Ϊ��***/
					if(playerbullet.getX()%17<=1) {							//���ж�
						for(int j=0;j<spirittank_miss;j++) {				//ѭ��ִ�б��þ�������
							if(bullet_y+j<37) {								//ȷ���ж�������
								if(World.get(worldNumber).mapmap_move_point007[bullet_x][bullet_y+j]!=1) {		//��֤���Ḳ��ǽ��
									World.get(worldNumber).mapmap_move_point007[bullet_x][bullet_y+j]=spirittank_miss-j+1;		//��ֵ
								}
							}
						}
					}else if(playerbullet.getX()%17>=16) {
						for(int j=0;j<spirittank_miss;j++) {				//ѭ��ִ�б��þ�������
							if(bullet_y+j<37&&bullet_x+1<48) {				//ȷ���ж�������
								if(World.get(worldNumber).mapmap_move_point007[bullet_x+1][bullet_y+j]!=1) {	//��֤���Ḳ��ǽ��
									World.get(worldNumber).mapmap_move_point007[bullet_x+1][bullet_y+j]=spirittank_miss-j+1;		//��ֵ
								}
							}
						}
					}else {													//���ж�				
						for(int j=0;j<spirittank_miss;j++) {				//ѭ��ִ�б��þ�������
							if(bullet_y+j<37&&bullet_x+1<48) {				//ȷ���ж�������
								if(World.get(worldNumber).mapmap_move_point007[bullet_x+1][bullet_y+j]!=1) {	//��֤���Ḳ��ǽ��
									World.get(worldNumber).mapmap_move_point007[bullet_x+1][bullet_y+j]=spirittank_miss-j+1;     //��ֵ
								}
							}
						}
						for(int j=0;j<spirittank_miss;j++) {				//ѭ��ִ�б��þ�������
							if(bullet_y+j<37) {								//ȷ���ж�������
								if(World.get(worldNumber).mapmap_move_point007[bullet_x][bullet_y+j]!=1) {		//��֤���Ḳ��ǽ��
									World.get(worldNumber).mapmap_move_point007[bullet_x][bullet_y+j]=spirittank_miss-j+1;		//��ֵ
								}
							}
						}
					}
				}else if(playerbullet.getDiraction()==Spirit.LEFT) {		/***����Ϊ��***/
					if(playerbullet.getY()%17<=1) {							//���ж�
						for(int j=0;j<spirittank_miss;j++) {				//ѭ��ִ�б��þ�������
							if(bullet_x-j>=0) {								//ȷ���ж�������
								if(World.get(worldNumber).mapmap_move_point007[bullet_x-j][bullet_y]!=1) {		//��֤���Ḳ��ǽ��
									World.get(worldNumber).mapmap_move_point007[bullet_x-j][bullet_y]=spirittank_miss-j+1;     	//��ֵ
								}
							}
						}
					}else if(playerbullet.getY()%17>=16) {
						for(int j=0;j<spirittank_miss;j++) {				//ѭ��ִ�б��þ�������
							if(bullet_x-j>=0&&bullet_y+1<37) {				//ȷ���ж�������
								if(World.get(worldNumber).mapmap_move_point007[bullet_x-j][bullet_y+1]!=1) {		//��֤���Ḳ��ǽ��
									World.get(worldNumber).mapmap_move_point007[bullet_x-j][bullet_y+1]=spirittank_miss-j+1;     	//��ֵ
								}
							}
						}
					}else {													//���ж�
						for(int j=0;j<spirittank_miss;j++) {				//ѭ��ִ�б��þ�������
							if(bullet_x-j>=0&&bullet_y+1<37) {				//ȷ���ж�������
								if(World.get(worldNumber).mapmap_move_point007[bullet_x-j][bullet_y+1]!=1) {		//��֤���Ḳ��ǽ��
									World.get(worldNumber).mapmap_move_point007[bullet_x-j][bullet_y+1]=spirittank_miss-j+1;     	//��ֵ
								}
							}
						}
						for(int j=0;j<spirittank_miss;j++) {				//ѭ��ִ�б��þ�������
							if(bullet_x-j>=0) {								//ȷ���ж�������
								if(World.get(worldNumber).mapmap_move_point007[bullet_x-j][bullet_y]!=1) {			//��֤���Ḳ��ǽ��
									World.get(worldNumber).mapmap_move_point007[bullet_x-j][bullet_y]=spirittank_miss-j+1;     		//��ֵ
								}
							}
						}
					}
				}
			}

			
			//��������
			int[][] lingpai=new int[48][37];				//ѭ���е��������
			for(int i=0;i<=47;i++) {
				for(int j=0;j<=36;j++) {
					lingpai[i][j]=0;
				}
			}
			while(key==0) {
				key=1;
				for(int i=0;i<=47;i++) {
					for(int j=0;j<=36;j++) {
						if(World.get(worldNumber).mapmap_move_point007[i][j]<-20&&
								lingpai[i][j]==0) {//�ж�Ϊ��ǽ����0���Ǳ�������
							if(j-1>=0) {
								if(World.get(worldNumber).mapmap_move_point007[i][j-1]==0) {
									key=0;
									World.get(worldNumber).mapmap_move_point007[i][j-1]=World.get(worldNumber).mapmap_move_point007[i][j]+1;
									lingpai[i][j-1]=1;
								}
							}
							if(j+1<=36) {
								if(World.get(worldNumber).mapmap_move_point007[i][j+1]==0) {
									key=0;
									World.get(worldNumber).mapmap_move_point007[i][j+1]=World.get(worldNumber).mapmap_move_point007[i][j]+1;
									lingpai[i][j+1]=1;
								}
							}
							if(i-1>=0) {
								if(World.get(worldNumber).mapmap_move_point007[i-1][j]==0) {
									key=0;
									World.get(worldNumber).mapmap_move_point007[i-1][j]=World.get(worldNumber).mapmap_move_point007[i][j]+1;
									lingpai[i-1][j]=1;
								}
							}
							if(i+1<=47) {
								if(World.get(worldNumber).mapmap_move_point007[i+1][j]==0) {
									key=0;
									World.get(worldNumber).mapmap_move_point007[i+1][j]=World.get(worldNumber).mapmap_move_point007[i][j]+1;
									lingpai[i+1][j]=1;
								}
							}
						}
					}
				}
				//��ԭ���㱾�������ж�
				for(int i=0;i<=47;i++) {
					for(int j=0;j<=36;j++) {
						lingpai[i][j]=0;
					}
				}
			}
			
			/*********
			//��ʾ���ֵ�ͼ�������
			System.out.println("007");
			for(int i=0;i<=36;i++) {
				for(int j=0;j<=47;j++) {
					String str1=String.format("% 5d",World.get(worldNumber).mapmap_move_point007[j][i] );
					System.out.printf(str1);
				}
				System.out.println("");
			}
			System.out.println("");
			/*********/
		}			

		private void DO_map008() {
			this.spirittank_distance002=World.get(worldNumber).move_distance;		//�������߰뾶
			//ʵʱ�������̹���ݶ�����
			//����˲���
			int playertank_block_x=playertank.getX()/17;
			int playertank_block_y=playertank.getY()/17;
			int key=0;											//���ƣ���δ�����κβ���ʱ������ѭ��
			//����Ȩֵ��λ��
			//����һ���Ա���Ȩֵ��
			/*
			if(World.get(worldNumber).mapmap_move_point001[playertank_block_x][playertank_block_y]!=1) {
				World.get(worldNumber).mapmap_move_point001[playertank_block_x][playertank_block_y]=-spirittank_see;//��������ݶ�
			}
			if(World.get(worldNumber).mapmap_move_point001[playertank_block_x+1][playertank_block_y]!=1) {
				World.get(worldNumber).mapmap_move_point001[playertank_block_x+1][playertank_block_y]=-spirittank_see;//��������ݶ�
			}
			if(World.get(worldNumber).mapmap_move_point001[playertank_block_x][playertank_block_y+1]!=1) {
				World.get(worldNumber).mapmap_move_point001[playertank_block_x][playertank_block_y+1]=-spirittank_see;//��������ݶ�
			}
			if(World.get(worldNumber).mapmap_move_point001[playertank_block_x+1][playertank_block_y+1]!=1) {
				World.get(worldNumber).mapmap_move_point001[playertank_block_x+1][playertank_block_y+1]=-spirittank_see;//��������ݶ�
			}
			*/
			//������������ѿ�Ȩֵ��
			//��������1���ж�Ȩֵ
			//Ȩֵ�㼯�������·�
			if(playertank_block_x-spirittank_distance002>=0&&playertank_block_y+spirittank_distance002<=36) {
				if(World.get(worldNumber).mapmap_move_point008[playertank_block_x-spirittank_distance002][playertank_block_y+spirittank_distance002]!=1) {
					World.get(worldNumber).mapmap_move_point008[playertank_block_x-spirittank_distance002][playertank_block_y+spirittank_distance002]=-spirittank_see;
				}
			}
			//Ȩֵ����2���������
			int d1=5;
			for(int i1=0;i1<d1;i1++) {
				for(int j1=0;j1<d1;j1++) {
					if(playertank_block_x-2+i1>=0&&
							playertank_block_x-2+i1<=47&&
							playertank_block_y-2+j1>=0&&
							playertank_block_y-2+j1<=36) {
						if(World.get(worldNumber).mapmap_move_point008[playertank_block_x-2+i1][playertank_block_y-2+j1]!=1) {
							World.get(worldNumber).mapmap_move_point008[playertank_block_x-2+i1][playertank_block_y-2+j1]=-13;
						}
					}
				}
			}
			int d2=3;
			for(int i1=0;i1<d2;i1++) {
				for(int j1=0;j1<d2;j1++) {
					if(playertank_block_x-1+i1>=0&&
							playertank_block_x-1+i1<=47&&
							playertank_block_y-1+j1>=0&&
							playertank_block_y-1+j1<=36) {
						if(World.get(worldNumber).mapmap_move_point008[playertank_block_x-1+i1][playertank_block_y-1+j1]!=1) {
							World.get(worldNumber).mapmap_move_point008[playertank_block_x-1+i1][playertank_block_y-1+j1]=-12;
						}
					}
				}
			}
			int d3=1;
			if(World.get(worldNumber).mapmap_move_point008[playertank_block_x][playertank_block_y]!=1) {
				World.get(worldNumber).mapmap_move_point008[playertank_block_x][playertank_block_y]=-11;
			}	
			//Ȩֵ����3�������ӵ�
			//����������
			for(int i=0;i<World.get(worldNumber).playerbullets.size();i++) {//��������
				Bullet playerbullet=World.get(worldNumber).playerbullets.get(i);
				int bullet_x=playerbullet.getX()/17;
				int bullet_y=playerbullet.getY()/17;
				if(playerbullet.getDiraction()==Spirit.UP) {				/***����Ϊ��***/
					if(playerbullet.getX()%17<=1) {							//���ж�
						for(int j=0;j<spirittank_miss;j++) {				//ѭ��ִ�б��þ�������
							if(bullet_y-j>=0) {								//ȷ���ж�������
								if(World.get(worldNumber).mapmap_move_point008[bullet_x][bullet_y-j]!=1) {		//��֤���Ḳ��ǽ��
									World.get(worldNumber).mapmap_move_point008[bullet_x][bullet_y-j]=spirittank_miss-j+1;		//��ֵ
								}
							}
						}
					}else if(playerbullet.getX()%17>=16) {
						for(int j=0;j<spirittank_miss;j++) {				//ѭ��ִ�б��þ�������
							if(bullet_y-j>=0&&bullet_x+1<48) {				//ȷ���ж�������
								if(World.get(worldNumber).mapmap_move_point008[bullet_x+1][bullet_y-j]!=1) {	//��֤���Ḳ��ǽ��
									World.get(worldNumber).mapmap_move_point008[bullet_x+1][bullet_y-j]=spirittank_miss-j+1;		//��ֵ
								}
							}
						}
					}else {													//���ж�				
						for(int j=0;j<spirittank_miss;j++) {				//ѭ��ִ�б��þ�������
							if(bullet_y-j>=0&&bullet_x+1<48) {				//ȷ���ж�������
								if(World.get(worldNumber).mapmap_move_point008[bullet_x+1][bullet_y-j]!=1) {	//��֤���Ḳ��ǽ��
									World.get(worldNumber).mapmap_move_point008[bullet_x+1][bullet_y-j]=spirittank_miss-j+1;     //��ֵ
								}
							}
						}
						for(int j=0;j<spirittank_miss;j++) {				//ѭ��ִ�б��þ�������
							if(bullet_y-j>=0) {								//ȷ���ж�������
								if(World.get(worldNumber).mapmap_move_point008[bullet_x][bullet_y-j]!=1) {		//��֤���Ḳ��ǽ��
									World.get(worldNumber).mapmap_move_point008[bullet_x][bullet_y-j]=spirittank_miss-j+1;		//��ֵ
								}
							}
						}
					}
				}else if(playerbullet.getDiraction()==Spirit.RIGHT) {		/***����Ϊ��***/
					if(playerbullet.getY()%17<=1) {							//���ж�
						for(int j=0;j<spirittank_miss;j++) {				//ѭ��ִ�б��þ�������
							if(bullet_x+j<48) {								//ȷ���ж�������
								if(World.get(worldNumber).mapmap_move_point008[bullet_x+j][bullet_y]!=1) {		//��֤���Ḳ��ǽ��
									World.get(worldNumber).mapmap_move_point008[bullet_x+j][bullet_y]=spirittank_miss-j+1;     	//��ֵ
								}
							}
						}
					}else if(playerbullet.getY()%17>=16) {
						for(int j=0;j<spirittank_miss;j++) {				//ѭ��ִ�б��þ�������
							if(bullet_x+j<48&&bullet_y+1<37) {				//ȷ���ж�������
								if(World.get(worldNumber).mapmap_move_point008[bullet_x+j][bullet_y+1]!=1) {		//��֤���Ḳ��ǽ��
									World.get(worldNumber).mapmap_move_point008[bullet_x+j][bullet_y+1]=spirittank_miss-j+1;     	//��ֵ
								}
							}
						}
					}else {													//���ж�
						for(int j=0;j<spirittank_miss;j++) {				//ѭ��ִ�б��þ�������
							if(bullet_x+j<48&&bullet_y+1<37) {				//ȷ���ж�������
								if(World.get(worldNumber).mapmap_move_point008[bullet_x+j][bullet_y+1]!=1) {		//��֤���Ḳ��ǽ��
									World.get(worldNumber).mapmap_move_point008[bullet_x+j][bullet_y+1]=spirittank_miss-j+1;     	//��ֵ
								}
							}
						}
						for(int j=0;j<spirittank_miss;j++) {				//ѭ��ִ�б��þ�������
							if(bullet_x+j<48) {								//ȷ���ж�������
								if(World.get(worldNumber).mapmap_move_point008[bullet_x+j][bullet_y]!=1) {			//��֤���Ḳ��ǽ��
									World.get(worldNumber).mapmap_move_point008[bullet_x+j][bullet_y]=spirittank_miss-j+1;     		//��ֵ
								}
							}
						}
					}
				}else if(playerbullet.getDiraction()==Spirit.DOWN) {		/***����Ϊ��***/
					if(playerbullet.getX()%17<=1) {							//���ж�
						for(int j=0;j<spirittank_miss;j++) {				//ѭ��ִ�б��þ�������
							if(bullet_y+j<37) {								//ȷ���ж�������
								if(World.get(worldNumber).mapmap_move_point008[bullet_x][bullet_y+j]!=1) {		//��֤���Ḳ��ǽ��
									World.get(worldNumber).mapmap_move_point008[bullet_x][bullet_y+j]=spirittank_miss-j+1;		//��ֵ
								}
							}
						}
					}else if(playerbullet.getX()%17>=16) {
						for(int j=0;j<spirittank_miss;j++) {				//ѭ��ִ�б��þ�������
							if(bullet_y+j<37&&bullet_x+1<48) {				//ȷ���ж�������
								if(World.get(worldNumber).mapmap_move_point008[bullet_x+1][bullet_y+j]!=1) {	//��֤���Ḳ��ǽ��
									World.get(worldNumber).mapmap_move_point008[bullet_x+1][bullet_y+j]=spirittank_miss-j+1;		//��ֵ
								}
							}
						}
					}else {													//���ж�				
						for(int j=0;j<spirittank_miss;j++) {				//ѭ��ִ�б��þ�������
							if(bullet_y+j<37&&bullet_x+1<48) {				//ȷ���ж�������
								if(World.get(worldNumber).mapmap_move_point008[bullet_x+1][bullet_y+j]!=1) {	//��֤���Ḳ��ǽ��
									World.get(worldNumber).mapmap_move_point008[bullet_x+1][bullet_y+j]=spirittank_miss-j+1;     //��ֵ
								}
							}
						}
						for(int j=0;j<spirittank_miss;j++) {				//ѭ��ִ�б��þ�������
							if(bullet_y+j<37) {								//ȷ���ж�������
								if(World.get(worldNumber).mapmap_move_point008[bullet_x][bullet_y+j]!=1) {		//��֤���Ḳ��ǽ��
									World.get(worldNumber).mapmap_move_point008[bullet_x][bullet_y+j]=spirittank_miss-j+1;		//��ֵ
								}
							}
						}
					}
				}else if(playerbullet.getDiraction()==Spirit.LEFT) {		/***����Ϊ��***/
					if(playerbullet.getY()%17<=1) {							//���ж�
						for(int j=0;j<spirittank_miss;j++) {				//ѭ��ִ�б��þ�������
							if(bullet_x-j>=0) {								//ȷ���ж�������
								if(World.get(worldNumber).mapmap_move_point008[bullet_x-j][bullet_y]!=1) {		//��֤���Ḳ��ǽ��
									World.get(worldNumber).mapmap_move_point008[bullet_x-j][bullet_y]=spirittank_miss-j+1;     	//��ֵ
								}
							}
						}
					}else if(playerbullet.getY()%17>=16) {
						for(int j=0;j<spirittank_miss;j++) {				//ѭ��ִ�б��þ�������
							if(bullet_x-j>=0&&bullet_y+1<37) {				//ȷ���ж�������
								if(World.get(worldNumber).mapmap_move_point008[bullet_x-j][bullet_y+1]!=1) {		//��֤���Ḳ��ǽ��
									World.get(worldNumber).mapmap_move_point008[bullet_x-j][bullet_y+1]=spirittank_miss-j+1;     	//��ֵ
								}
							}
						}
					}else {													//���ж�
						for(int j=0;j<spirittank_miss;j++) {				//ѭ��ִ�б��þ�������
							if(bullet_x-j>=0&&bullet_y+1<37) {				//ȷ���ж�������
								if(World.get(worldNumber).mapmap_move_point008[bullet_x-j][bullet_y+1]!=1) {		//��֤���Ḳ��ǽ��
									World.get(worldNumber).mapmap_move_point008[bullet_x-j][bullet_y+1]=spirittank_miss-j+1;     	//��ֵ
								}
							}
						}
						for(int j=0;j<spirittank_miss;j++) {				//ѭ��ִ�б��þ�������
							if(bullet_x-j>=0) {								//ȷ���ж�������
								if(World.get(worldNumber).mapmap_move_point008[bullet_x-j][bullet_y]!=1) {			//��֤���Ḳ��ǽ��
									World.get(worldNumber).mapmap_move_point008[bullet_x-j][bullet_y]=spirittank_miss-j+1;     		//��ֵ
								}
							}
						}
					}
				}
			}

			//��������
			int[][] lingpai=new int[48][37];				//ѭ���е��������
			for(int i=0;i<=47;i++) {
				for(int j=0;j<=36;j++) {
					lingpai[i][j]=0;
				}
			}
			while(key==0) {
				key=1;
				for(int i=0;i<=47;i++) {
					for(int j=0;j<=36;j++) {
						if(World.get(worldNumber).mapmap_move_point008[i][j]<-20&&
								lingpai[i][j]==0) {//�ж�Ϊ��ǽ����0���Ǳ�������
							if(j-1>=0) {
								if(World.get(worldNumber).mapmap_move_point008[i][j-1]==0) {
									key=0;
									World.get(worldNumber).mapmap_move_point008[i][j-1]=World.get(worldNumber).mapmap_move_point008[i][j]+1;
									lingpai[i][j-1]=1;
								}
							}
							if(j+1<=36) {
								if(World.get(worldNumber).mapmap_move_point008[i][j+1]==0) {
									key=0;
									World.get(worldNumber).mapmap_move_point008[i][j+1]=World.get(worldNumber).mapmap_move_point008[i][j]+1;
									lingpai[i][j+1]=1;
								}
							}
							if(i-1>=0) {
								if(World.get(worldNumber).mapmap_move_point008[i-1][j]==0) {
									key=0;
									World.get(worldNumber).mapmap_move_point008[i-1][j]=World.get(worldNumber).mapmap_move_point008[i][j]+1;
									lingpai[i-1][j]=1;
								}
							}
							if(i+1<=47) {
								if(World.get(worldNumber).mapmap_move_point008[i+1][j]==0) {
									key=0;
									World.get(worldNumber).mapmap_move_point008[i+1][j]=World.get(worldNumber).mapmap_move_point008[i][j]+1;
									lingpai[i+1][j]=1;
								}
							}
						}
					}
				}
				//��ԭ���㱾�������ж�
				for(int i=0;i<=47;i++) {
					for(int j=0;j<=36;j++) {
						lingpai[i][j]=0;
					}
				}
			}
			
		
			/*********
			//��ʾ���ֵ�ͼ�������
			System.out.println("008");
			for(int i=0;i<=36;i++) {
				for(int j=0;j<=47;j++) {
					String str1=String.format("% 5d",World.get(worldNumber).mapmap_move_point008[j][i] );
					System.out.printf(str1);
				}
				System.out.println("");
			}
			System.out.println("");
			/*********/
		}		
	
		private void DO_map009() {
			this.spirittank_distance002=World.get(worldNumber).move_distance;		//�������߰뾶
			//ʵʱ�������̹���ݶ�����
			//����˲���
			int playertank_block_x=playertank.getX()/17;
			int playertank_block_y=playertank.getY()/17;
			int key=0;											//���ƣ���δ�����κβ���ʱ������ѭ��
			//����Ȩֵ��λ��
			//����һ���Ա���Ȩֵ��
			/*
			if(World.get(worldNumber).mapmap_move_point001[playertank_block_x][playertank_block_y]!=1) {
				World.get(worldNumber).mapmap_move_point001[playertank_block_x][playertank_block_y]=-spirittank_see;//��������ݶ�
			}
			if(World.get(worldNumber).mapmap_move_point001[playertank_block_x+1][playertank_block_y]!=1) {
				World.get(worldNumber).mapmap_move_point001[playertank_block_x+1][playertank_block_y]=-spirittank_see;//��������ݶ�
			}
			if(World.get(worldNumber).mapmap_move_point001[playertank_block_x][playertank_block_y+1]!=1) {
				World.get(worldNumber).mapmap_move_point001[playertank_block_x][playertank_block_y+1]=-spirittank_see;//��������ݶ�
			}
			if(World.get(worldNumber).mapmap_move_point001[playertank_block_x+1][playertank_block_y+1]!=1) {
				World.get(worldNumber).mapmap_move_point001[playertank_block_x+1][playertank_block_y+1]=-spirittank_see;//��������ݶ�
			}
			*/
			//������������ѿ�Ȩֵ��
			//��������1���ж�Ȩֵ
			//Ȩֵ�㼯������
			if(playertank_block_x-spirittank_distance002>=0) {
				if(World.get(worldNumber).mapmap_move_point009[playertank_block_x-spirittank_distance002][playertank_block_y]!=1) {
					World.get(worldNumber).mapmap_move_point009[playertank_block_x-spirittank_distance002][playertank_block_y]=-spirittank_see;
				}
			}
			
			//Ȩֵ����2���������(����-1��-20����������)
			int d1=5;
			for(int i1=0;i1<d1;i1++) {
				for(int j1=0;j1<d1;j1++) {
					if(playertank_block_x-2+i1>=0&&
							playertank_block_x-2+i1<=47&&
							playertank_block_y-2+j1>=0&&
							playertank_block_y-2+j1<=36) {
						if(World.get(worldNumber).mapmap_move_point009[playertank_block_x-2+i1][playertank_block_y-2+j1]!=1) {
							World.get(worldNumber).mapmap_move_point009[playertank_block_x-2+i1][playertank_block_y-2+j1]=-13;
						}
					}
				}
			}
			int d2=3;
			for(int i1=0;i1<d2;i1++) {
				for(int j1=0;j1<d2;j1++) {
					if(playertank_block_x-1+i1>=0&&
							playertank_block_x-1+i1<=47&&
							playertank_block_y-1+j1>=0&&
							playertank_block_y-1+j1<=36) {
						if(World.get(worldNumber).mapmap_move_point009[playertank_block_x-1+i1][playertank_block_y-1+j1]!=1) {
							World.get(worldNumber).mapmap_move_point009[playertank_block_x-1+i1][playertank_block_y-1+j1]=-12;
						}
					}
				}
			}
			int d3=1;
			if(World.get(worldNumber).mapmap_move_point009[playertank_block_x][playertank_block_y]!=1) {
				World.get(worldNumber).mapmap_move_point009[playertank_block_x][playertank_block_y]=-11;
			}
			//Ȩֵ����3�������ӵ�(����-1��-20����������)
			//����������
			for(int i=0;i<World.get(worldNumber).playerbullets.size();i++) {//��������
				Bullet playerbullet=World.get(worldNumber).playerbullets.get(i);
				int bullet_x=playerbullet.getX()/17;
				int bullet_y=playerbullet.getY()/17;
				if(playerbullet.getDiraction()==Spirit.UP) {				/***����Ϊ��***/
					if(playerbullet.getX()%17<=1) {							//���ж�
						for(int j=0;j<spirittank_miss;j++) {				//ѭ��ִ�б��þ�������
							if(bullet_y-j>=0) {								//ȷ���ж�������
								if(World.get(worldNumber).mapmap_move_point009[bullet_x][bullet_y-j]!=1) {		//��֤���Ḳ��ǽ��
									World.get(worldNumber).mapmap_move_point009[bullet_x][bullet_y-j]=spirittank_miss-j+1;		//��ֵ
								}
							}
						}
					}else if(playerbullet.getX()%17>=16) {
						for(int j=0;j<spirittank_miss;j++) {				//ѭ��ִ�б��þ�������
							if(bullet_y-j>=0&&bullet_x+1<48) {				//ȷ���ж�������
								if(World.get(worldNumber).mapmap_move_point009[bullet_x+1][bullet_y-j]!=1) {	//��֤���Ḳ��ǽ��
									World.get(worldNumber).mapmap_move_point009[bullet_x+1][bullet_y-j]=spirittank_miss-j+1;		//��ֵ
								}
							}
						}
					}else {													//���ж�				
						for(int j=0;j<spirittank_miss;j++) {				//ѭ��ִ�б��þ�������
							if(bullet_y-j>=0&&bullet_x+1<48) {				//ȷ���ж�������
								if(World.get(worldNumber).mapmap_move_point009[bullet_x+1][bullet_y-j]!=1) {	//��֤���Ḳ��ǽ��
									World.get(worldNumber).mapmap_move_point009[bullet_x+1][bullet_y-j]=spirittank_miss-j+1;     //��ֵ
								}
							}
						}
						for(int j=0;j<spirittank_miss;j++) {				//ѭ��ִ�б��þ�������
							if(bullet_y-j>=0) {								//ȷ���ж�������
								if(World.get(worldNumber).mapmap_move_point009[bullet_x][bullet_y-j]!=1) {		//��֤���Ḳ��ǽ��
									World.get(worldNumber).mapmap_move_point009[bullet_x][bullet_y-j]=spirittank_miss-j+1;		//��ֵ
								}
							}
						}
					}
				}else if(playerbullet.getDiraction()==Spirit.RIGHT) {		/***����Ϊ��***/
					if(playerbullet.getY()%17<=1) {							//���ж�
						for(int j=0;j<spirittank_miss;j++) {				//ѭ��ִ�б��þ�������
							if(bullet_x+j<48) {								//ȷ���ж�������
								if(World.get(worldNumber).mapmap_move_point009[bullet_x+j][bullet_y]!=1) {		//��֤���Ḳ��ǽ��
									World.get(worldNumber).mapmap_move_point009[bullet_x+j][bullet_y]=spirittank_miss-j+1;     	//��ֵ
								}
							}
						}
					}else if(playerbullet.getY()%17>=16) {
						for(int j=0;j<spirittank_miss;j++) {				//ѭ��ִ�б��þ�������
							if(bullet_x+j<48&&bullet_y+1<37) {				//ȷ���ж�������
								if(World.get(worldNumber).mapmap_move_point009[bullet_x+j][bullet_y+1]!=1) {		//��֤���Ḳ��ǽ��
									World.get(worldNumber).mapmap_move_point009[bullet_x+j][bullet_y+1]=spirittank_miss-j+1;     	//��ֵ
								}
							}
						}
					}else {													//���ж�
						for(int j=0;j<spirittank_miss;j++) {				//ѭ��ִ�б��þ�������
							if(bullet_x+j<48&&bullet_y+1<37) {				//ȷ���ж�������
								if(World.get(worldNumber).mapmap_move_point009[bullet_x+j][bullet_y+1]!=1) {		//��֤���Ḳ��ǽ��
									World.get(worldNumber).mapmap_move_point009[bullet_x+j][bullet_y+1]=spirittank_miss-j+1;     	//��ֵ
								}
							}
						}
						for(int j=0;j<spirittank_miss;j++) {				//ѭ��ִ�б��þ�������
							if(bullet_x+j<48) {								//ȷ���ж�������
								if(World.get(worldNumber).mapmap_move_point009[bullet_x+j][bullet_y]!=1) {			//��֤���Ḳ��ǽ��
									World.get(worldNumber).mapmap_move_point009[bullet_x+j][bullet_y]=spirittank_miss-j+1;     		//��ֵ
								}
							}
						}
					}
				}else if(playerbullet.getDiraction()==Spirit.DOWN) {		/***����Ϊ��***/
					if(playerbullet.getX()%17<=1) {							//���ж�
						for(int j=0;j<spirittank_miss;j++) {				//ѭ��ִ�б��þ�������
							if(bullet_y+j<37) {								//ȷ���ж�������
								if(World.get(worldNumber).mapmap_move_point009[bullet_x][bullet_y+j]!=1) {		//��֤���Ḳ��ǽ��
									World.get(worldNumber).mapmap_move_point009[bullet_x][bullet_y+j]=spirittank_miss-j+1;		//��ֵ
								}
							}
						}
					}else if(playerbullet.getX()%17>=16) {
						for(int j=0;j<spirittank_miss;j++) {				//ѭ��ִ�б��þ�������
							if(bullet_y+j<37&&bullet_x+1<48) {				//ȷ���ж�������
								if(World.get(worldNumber).mapmap_move_point009[bullet_x+1][bullet_y+j]!=1) {	//��֤���Ḳ��ǽ��
									World.get(worldNumber).mapmap_move_point009[bullet_x+1][bullet_y+j]=spirittank_miss-j+1;		//��ֵ
								}
							}
						}
					}else {													//���ж�				
						for(int j=0;j<spirittank_miss;j++) {				//ѭ��ִ�б��þ�������
							if(bullet_y+j<37&&bullet_x+1<48) {				//ȷ���ж�������
								if(World.get(worldNumber).mapmap_move_point009[bullet_x+1][bullet_y+j]!=1) {	//��֤���Ḳ��ǽ��
									World.get(worldNumber).mapmap_move_point009[bullet_x+1][bullet_y+j]=spirittank_miss-j+1;     //��ֵ
								}
							}
						}
						for(int j=0;j<spirittank_miss;j++) {				//ѭ��ִ�б��þ�������
							if(bullet_y+j<37) {								//ȷ���ж�������
								if(World.get(worldNumber).mapmap_move_point009[bullet_x][bullet_y+j]!=1) {		//��֤���Ḳ��ǽ��
									World.get(worldNumber).mapmap_move_point009[bullet_x][bullet_y+j]=spirittank_miss-j+1;		//��ֵ
								}
							}
						}
					}
				}else if(playerbullet.getDiraction()==Spirit.LEFT) {		/***����Ϊ��***/
					if(playerbullet.getY()%17<=1) {							//���ж�
						for(int j=0;j<spirittank_miss;j++) {				//ѭ��ִ�б��þ�������
							if(bullet_x-j>=0) {								//ȷ���ж�������
								if(World.get(worldNumber).mapmap_move_point009[bullet_x-j][bullet_y]!=1) {			//��֤���Ḳ��ǽ��
									World.get(worldNumber).mapmap_move_point009[bullet_x-j][bullet_y]=spirittank_miss-j+1;     	//��ֵ
								}
							}
						}
					}else if(playerbullet.getY()%17>=16) {
						for(int j=0;j<spirittank_miss;j++) {				//ѭ��ִ�б��þ�������
							if(bullet_x-j>=0&&bullet_y+1<37) {				//ȷ���ж�������
								if(World.get(worldNumber).mapmap_move_point009[bullet_x-j][bullet_y+1]!=1) {		//��֤���Ḳ��ǽ��
									World.get(worldNumber).mapmap_move_point009[bullet_x-j][bullet_y+1]=spirittank_miss-j+1;     	//��ֵ
								}
							}
						}
					}else {													//���ж�
						for(int j=0;j<spirittank_miss;j++) {				//ѭ��ִ�б��þ�������
							if(bullet_x-j>=0&&bullet_y+1<37) {				//ȷ���ж�������
								if(World.get(worldNumber).mapmap_move_point009[bullet_x-j][bullet_y+1]!=1) {		//��֤���Ḳ��ǽ��
									World.get(worldNumber).mapmap_move_point009[bullet_x-j][bullet_y+1]=spirittank_miss-j+1;     	//��ֵ
								}
							}
						}
						for(int j=0;j<spirittank_miss;j++) {				//ѭ��ִ�б��þ�������
							if(bullet_x-j>=0) {								//ȷ���ж�������
								if(World.get(worldNumber).mapmap_move_point009[bullet_x-j][bullet_y]!=1) {			//��֤���Ḳ��ǽ��
									World.get(worldNumber).mapmap_move_point009[bullet_x-j][bullet_y]=spirittank_miss-j+1;     		//��ֵ
								}
							}
						}
					}
				}
			}
			//��������
			int[][] lingpai=new int[48][37];				//ѭ���е��������
			for(int i=0;i<=47;i++) {
				for(int j=0;j<=36;j++) {
					lingpai[i][j]=0;
				}
			}
			while(key==0) {
				key=1;
				for(int i=0;i<=47;i++) {
					for(int j=0;j<=36;j++) {
						if(World.get(worldNumber).mapmap_move_point009[i][j]<-20&&
								lingpai[i][j]==0) {//�ж�Ϊ��ǽ����0���Ǳ�������
							if(j-1>=0) {
								if(World.get(worldNumber).mapmap_move_point009[i][j-1]==0) {
									key=0;
									World.get(worldNumber).mapmap_move_point009[i][j-1]=World.get(worldNumber).mapmap_move_point009[i][j]+1;
									lingpai[i][j-1]=1;
								}
							}
							if(j+1<=36) {
								if(World.get(worldNumber).mapmap_move_point009[i][j+1]==0) {
									key=0;
									World.get(worldNumber).mapmap_move_point009[i][j+1]=World.get(worldNumber).mapmap_move_point009[i][j]+1;
									lingpai[i][j+1]=1;
								}
							}
							if(i-1>=0) {
								if(World.get(worldNumber).mapmap_move_point009[i-1][j]==0) {
									key=0;
									World.get(worldNumber).mapmap_move_point009[i-1][j]=World.get(worldNumber).mapmap_move_point009[i][j]+1;
									lingpai[i-1][j]=1;
								}
							}
							if(i+1<=47) {
								if(World.get(worldNumber).mapmap_move_point009[i+1][j]==0) {
									key=0;
									World.get(worldNumber).mapmap_move_point009[i+1][j]=World.get(worldNumber).mapmap_move_point009[i][j]+1;
									lingpai[i+1][j]=1;
								}
							}
						}
					}
				}
				//��ԭ���㱾�������ж�
				for(int i=0;i<=47;i++) {
					for(int j=0;j<=36;j++) {
						lingpai[i][j]=0;
					}
				}
			}


			
			
			/*********
			//��ʾ���ֵ�ͼ�������
			System.out.println("009");
			for(int i=0;i<=36;i++) {
				for(int j=0;j<=47;j++) {
					String str1=String.format("% 5d",World.get(worldNumber).mapmap_move_point009[j][i] );
					System.out.printf(str1);
				}
				System.out.println("");
			}
			System.out.println("");
			/*********/
		}		
		
		//end//
}