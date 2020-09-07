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





public class GamePanel_2P extends JPanel implements KeyListener{
	/*
	 * ʤ��Ŀ�꣬����99�ŵ�ͼ
	 * 
	 *˫����Ϸ��ϵͳ���ţ�
	 *��һ��̹�˱���������Ϊ��ʱ�����ɸ���״̬������һ��̹�˿�����
	 *�临��ʱ����ֱ�Ӵ���������ͬλ�ý�����һ�أ������»����һ�ص�����
	 *һ������̹�˷���ԭ��ͼ�򳹵���ʧ
	 *
	 *playertank_1p������ս̹��P1��
	 *playertank_2p���ø���̹��P2��
	 */
	//���б������init����
	private MainFrame mainFrame;
	private LoginPanel loginPanel;
	private Image offscreenimage=null;				//�ڶ�����
	private Graphics goffscreen=null;
	
	private int timeSpeed=1;       						//������Ϸʱ�������ٶȣ�
	private int timeSpeed_p1=1;       					//������Ϸʱ�������ٶȣ�
	private int timeSpeed_p2=1;       					//������Ϸʱ�������ٶȣ�
	
	//Ԥ�Ƶ�ͼ���ļ�����ʽ��ȡ��������ͼ������㷨����
	private ArrayList<String> mapName=new ArrayList<String>();						//��ͼ��Ӧ���ļ���
	private int[][]  mapName_int;													//�����ͼ���е�����
	private int mapchange=0;					 									//0-��ͼδ�䶯1-��ͼ�䶯���䶯�ڼ�ʹ����ʾ״̬��
	private int maptime_jianbian=0;													//�л���ͼʱ�Ľ�������  0-��̬��ֵ 2n-1ȫ̬��ֵ
	private int mapblock[][];														//��ͼ��������
	private static final int JIANBIAN=25;											//�������
	private int bj_1p=0;															//1p��ͼ���
	private int bj_2p=0;															//2p��ͼ���
	
	private ArrayList<Map> World=new ArrayList<Map>();								//����
	private static final int WORLDWIDTH=30;											//������
	private static final int WORLDHEIGHT=30;										//����߶�
		
	//���ܻ�����
	private static final int spirittank_miss=8;
	private static final int spirittank_see=1000;
	private static final int spirittank_distance=0;
	
	//���ͼ�任�Ĺؿ�����
	private int mapX=0;							    //map�����������
	private int mapY=0;								//map������������
	private int worldNumber=0;						//������
		
	//playertank������һֱ����
	private PlayerTank_P1 playertank_1p=null;		//���̹��
	private PlayerTank_P2 playertank_2p=null;		//���̹��
	private int playerTankState_1p=1;				//���̹��1p״̬1-������0-����(�ɸ���),-1-����(���ɸ���)
	private int playerTankState_2p=1;				//���̹��2p״̬1-������0-����(�ɸ���),-1-����(���ɸ���)
	private ArrayList<Cartoon> cartoons=new ArrayList<Cartoon>();			//���������б�

		//�������л�����
		private static final int GAMEWIN=1;
		private static final int GAMELOSE=2;
		private static final int cv0=30;                       //��Ϸ�����󻺳�ʱ��
		private int gameState=0;							   //��Ϸ״̬�ж�0-����1-ʤ��2-ʧ��
	
		//���캯��
		public GamePanel_2P(MainFrame mainFrame,LoginPanel loginPanel)  {//�ֳ�����World.get(worldNumber)
			super();
			this.mapblock=new int[47][36];					//��ʼ����ͼ����״̬����
			this.mainFrame=mainFrame;
			this.loginPanel=loginPanel;
			
			//���̹�˵ĳ�ʼ��	
			playertank_1p=new PlayerTank_P1(350,350,loginPanel.player_1p_tankmodel);
			playertank_2p=new PlayerTank_P2(450,350,loginPanel.player_2p_tankmodel);	
			
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
				StringTokenizer st = new StringTokenizer(str1,".");	//��.�ָ�
				mapName_int[i][0]=Integer.parseInt(st.nextToken());	//�ֱ�ֵ
				mapName_int[i][1]=Integer.parseInt(st.nextToken());	//�ֱ�ֵ
			}
					
			World.clear(); 							//�������
			initWorld();   							//�����ʼ��(����World���������������е�ͼ����)
													//ͬʱ�����ʼ�����꣬mapx,mapy,worldNumber
		}
	
	
		//��ͼpaint����
		public void paint(Graphics g) {
			
			//��ʾ����
			g.drawLine(0, 600, 800, 600);
			g.setFont(new Font("TimesRoman",Font.PLAIN,15));
			String system_string_0=new String("1p : tank_x : "+(playertank_1p.getX()+800*this.mapX)+"  "+
											  "tank_y : "+(600-playertank_1p.getY()+600*this.mapY)+"        "+
											  "world_x : "+this.mapX+"  "+
											  "world_y : "+this.mapY);
			String system_string_1=new String("2p : tank_x : "+(playertank_2p.getX()+800*this.mapX)+"  "+
											  "tank_y : "+(600-playertank_2p.getY()+600*this.mapY)+"  ");
		
			g.drawString(system_string_0, 10, 614);
			g.drawString(system_string_1, 10, 631);
			g.drawString("|||destination-(X:15 , Y:15)|||", 620, 645);
			g.drawString("playerlife 1P : "+World.get(worldNumber).playertankLife[0]+
					"        "+
					"playerlife 2P : "+World.get(worldNumber).playertankLife[1], 10, 647);
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
				playerTankState_2p=-1;	
			}
			if(World.get(worldNumber).playertankLife[0]<=0) {
				playerTankState_1p=-1;	
			}
			
			/**���ü�ⲿ��**/
			if(mapchange==0) {										//����ʱΪ�ǵ�ͼ���״̬
				
				//ʵʱ�������̹���ݶ�����
				//����˲���
				int playertank_block_x_1p=playertank_1p.getX()/17;
				int playertank_block_y_1p=playertank_1p.getY()/17;
				int playertank_block_x_2p=playertank_2p.getX()/17;
				int playertank_block_y_2p=playertank_2p.getY()/17;
				int key=0;											//���ƣ���δ�����κβ���ʱ������ѭ��
				//����Ȩֵ��λ��
				//����һ���Ա���Ȩֵ��
				if(playerTankState_1p==1) {
					if(World.get(worldNumber).mapmap_move_point001[playertank_block_x_1p][playertank_block_y_1p]!=1) {
						World.get(worldNumber).mapmap_move_point001[playertank_block_x_1p][playertank_block_y_1p]=-spirittank_see;//��������ݶ�
					}
					if(playertank_block_x_1p+1<48) {
						if(World.get(worldNumber).mapmap_move_point001[playertank_block_x_1p+1][playertank_block_y_1p]!=1) {
							World.get(worldNumber).mapmap_move_point001[playertank_block_x_1p+1][playertank_block_y_1p]=-spirittank_see;//��������ݶ�
						}
					}
					if(playertank_block_y_1p+1<37) {
						if(World.get(worldNumber).mapmap_move_point001[playertank_block_x_1p][playertank_block_y_1p+1]!=1) {
							World.get(worldNumber).mapmap_move_point001[playertank_block_x_1p][playertank_block_y_1p+1]=-spirittank_see;//��������ݶ�
						}
					}
					if(playertank_block_y_1p+1<37&&playertank_block_x_1p+1<48) {
						if(World.get(worldNumber).mapmap_move_point001[playertank_block_x_1p+1][playertank_block_y_1p+1]!=1) {
							World.get(worldNumber).mapmap_move_point001[playertank_block_x_1p+1][playertank_block_y_1p+1]=-spirittank_see;//��������ݶ�
						}
					}
				}
				if(playerTankState_2p==1) {
					if(World.get(worldNumber).mapmap_move_point001[playertank_block_x_2p][playertank_block_y_2p]!=1) {
						World.get(worldNumber).mapmap_move_point001[playertank_block_x_2p][playertank_block_y_2p]=-spirittank_see;//��������ݶ�
					}
					if(playertank_block_x_2p+1<48) {
						if(World.get(worldNumber).mapmap_move_point001[playertank_block_x_2p+1][playertank_block_y_2p]!=1) {
							World.get(worldNumber).mapmap_move_point001[playertank_block_x_2p+1][playertank_block_y_2p]=-spirittank_see;//��������ݶ�
						}
					}
					if(playertank_block_y_1p+1<37) {
						if(World.get(worldNumber).mapmap_move_point001[playertank_block_x_2p][playertank_block_y_2p+1]!=1) {
							World.get(worldNumber).mapmap_move_point001[playertank_block_x_2p][playertank_block_y_2p+1]=-spirittank_see;//��������ݶ�
						}
					}
					if(playertank_block_y_1p+1<37&&playertank_block_x_1p+1<48) {
						if(World.get(worldNumber).mapmap_move_point001[playertank_block_x_2p+1][playertank_block_y_2p+1]!=1) {
							World.get(worldNumber).mapmap_move_point001[playertank_block_x_2p+1][playertank_block_y_2p+1]=-spirittank_see;//��������ݶ�
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
						if(playerbullet.getX()%17<=8) {							//���ж�
							for(int j=0;j<spirittank_miss;j++) {				//ѭ��ִ�б��þ�������
								if(bullet_y-j>=0) {								//ȷ���ж�������
									if(World.get(worldNumber).mapmap_move_point001[bullet_x][bullet_y-j]!=1) {		//��֤���Ḳ��ǽ��
										World.get(worldNumber).mapmap_move_point001[bullet_x][bullet_y-j]=-1;		//��ֵ
									}
								}
							}
							for(int j=0;j<spirittank_miss;j++) {				//ѭ��ִ�б��þ�������
								if(bullet_y-j>=0&&bullet_x+1<48) {				//ȷ���ж�������
									if(World.get(worldNumber).mapmap_move_point001[bullet_x+1][bullet_y-j]!=1) {	//��֤���Ḳ��ǽ��
										World.get(worldNumber).mapmap_move_point001[bullet_x+1][bullet_y-j]=-2;     //��ֵ
									}
								}
							}
						}else {													//���ж�				
							for(int j=0;j<spirittank_miss;j++) {				//ѭ��ִ�б��þ�������
								if(bullet_y-j>=0&&bullet_x+1<48) {				//ȷ���ж�������
									if(World.get(worldNumber).mapmap_move_point001[bullet_x+1][bullet_y-j]!=1) {	//��֤���Ḳ��ǽ��
										World.get(worldNumber).mapmap_move_point001[bullet_x+1][bullet_y-j]=-1;     //��ֵ
									}
								}
							}
							for(int j=0;j<spirittank_miss;j++) {				//ѭ��ִ�б��þ�������
								if(bullet_y-j>=0) {								//ȷ���ж�������
									if(World.get(worldNumber).mapmap_move_point001[bullet_x][bullet_y-j]!=1) {		//��֤���Ḳ��ǽ��
										World.get(worldNumber).mapmap_move_point001[bullet_x][bullet_y-j]=-2;		//��ֵ
									}
								}
							}
						}
					}else if(playerbullet.getDiraction()==Spirit.RIGHT) {		/***����Ϊ��***/
						if(playerbullet.getY()%17<=8) {							//���ж�
							for(int j=0;j<spirittank_miss;j++) {				//ѭ��ִ�б��þ�������
								if(bullet_x+j<48) {								//ȷ���ж�������
									if(World.get(worldNumber).mapmap_move_point001[bullet_x+j][bullet_y]!=1) {		//��֤���Ḳ��ǽ��
										World.get(worldNumber).mapmap_move_point001[bullet_x+j][bullet_y]=-1;     	//��ֵ
									}
								}
							}
							for(int j=0;j<spirittank_miss;j++) {				//ѭ��ִ�б��þ�������
								if(bullet_x+j<48&&bullet_y+1<37) {				//ȷ���ж�������
									if(World.get(worldNumber).mapmap_move_point001[bullet_x+j][bullet_y+1]!=1) {		//��֤���Ḳ��ǽ��
										World.get(worldNumber).mapmap_move_point001[bullet_x+j][bullet_y+1]=-2;     	//��ֵ
									}
								}
							}
						}else {													//���ж�
							for(int j=0;j<spirittank_miss;j++) {				//ѭ��ִ�б��þ�������
								if(bullet_x+j<48&&bullet_y+1<37) {				//ȷ���ж�������
									if(World.get(worldNumber).mapmap_move_point001[bullet_x+j][bullet_y+1]!=1) {		//��֤���Ḳ��ǽ��
										World.get(worldNumber).mapmap_move_point001[bullet_x+j][bullet_y+1]=-1;     	//��ֵ
									}
								}
							}
							for(int j=0;j<spirittank_miss;j++) {				//ѭ��ִ�б��þ�������
								if(bullet_x+j<48) {								//ȷ���ж�������
									if(World.get(worldNumber).mapmap_move_point001[bullet_x+j][bullet_y]!=1) {		//��֤���Ḳ��ǽ��
										World.get(worldNumber).mapmap_move_point001[bullet_x+j][bullet_y]=-2;     	//��ֵ
									}
								}
							}
						}
					}else if(playerbullet.getDiraction()==Spirit.DOWN) {		/***����Ϊ��***/
						if(playerbullet.getX()%17<=8) {							//���ж�
							for(int j=0;j<spirittank_miss;j++) {				//ѭ��ִ�б��þ�������
								if(bullet_y+j<37) {								//ȷ���ж�������
									if(World.get(worldNumber).mapmap_move_point001[bullet_x][bullet_y+j]!=1) {			//��֤���Ḳ��ǽ��
										World.get(worldNumber).mapmap_move_point001[bullet_x][bullet_y+j]=-1;			//��ֵ
									}
								}
							}
							for(int j=0;j<spirittank_miss;j++) {				//ѭ��ִ�б��þ�������
								if(bullet_y+j<37&&bullet_x+1<48) {				//ȷ���ж�������
									if(World.get(worldNumber).mapmap_move_point001[bullet_x+1][bullet_y+j]!=1) {		//��֤���Ḳ��ǽ��
										World.get(worldNumber).mapmap_move_point001[bullet_x+1][bullet_y+j]=-2;			//��ֵ
									}
								}
							}
						}else {													//���ж�
							for(int j=0;j<spirittank_miss;j++) {				//ѭ��ִ�б��þ�������
								if(bullet_y+j<37&&bullet_x+1<48) {				//ȷ���ж�������
									if(World.get(worldNumber).mapmap_move_point001[bullet_x+1][bullet_y+j]!=1) {		//��֤���Ḳ��ǽ��
										World.get(worldNumber).mapmap_move_point001[bullet_x+1][bullet_y+j]=-1;			//��ֵ
									}
								}
							}
							for(int j=0;j<spirittank_miss;j++) {				//ѭ��ִ�б��þ�������
								if(bullet_y+j<37) {								//ȷ���ж�������
									if(World.get(worldNumber).mapmap_move_point001[bullet_x][bullet_y+j]!=1) {			//��֤���Ḳ��ǽ��
										World.get(worldNumber).mapmap_move_point001[bullet_x][bullet_y+j]=-2;			//��ֵ
									}
								}
							}
						}
					}else if(playerbullet.getDiraction()==Spirit.LEFT) {		/***����Ϊ��***/
						if(playerbullet.getY()%17<=8) {							//���ж�
							for(int j=0;j<spirittank_miss;j++) {				//ѭ��ִ�б��þ�������
								if(bullet_x-j>=0) {								//ȷ���ж�������
									if(World.get(worldNumber).mapmap_move_point001[bullet_x-j][bullet_y]!=1) {			//��֤���Ḳ��ǽ��
										World.get(worldNumber).mapmap_move_point001[bullet_x-j][bullet_y]=-1;			//��ֵ
									}
								}
							}
							for(int j=0;j<spirittank_miss;j++) {				//ѭ��ִ�б��þ�������
								if(bullet_x-j>=0&&bullet_y+1<37) {								//ȷ���ж�������
									if(World.get(worldNumber).mapmap_move_point001[bullet_x-j][bullet_y+1]!=1) {			//��֤���Ḳ��ǽ��
										World.get(worldNumber).mapmap_move_point001[bullet_x-j][bullet_y+1]=-2;			//��ֵ
									}
								}
							}
						}else {													//���ж�
							for(int j=0;j<spirittank_miss;j++) {				//ѭ��ִ�б��þ�������
								if(bullet_x-j>=0&&bullet_y+1<37) {								//ȷ���ж�������
									if(World.get(worldNumber).mapmap_move_point001[bullet_x-j][bullet_y+1]!=1) {			//��֤���Ḳ��ǽ��
										World.get(worldNumber).mapmap_move_point001[bullet_x-j][bullet_y+1]=-1;			//��ֵ
									}
								}
							}
							for(int j=0;j<spirittank_miss;j++) {				//ѭ��ִ�б��þ�������
								if(bullet_x-j>=0) {								//ȷ���ж�������
									if(World.get(worldNumber).mapmap_move_point001[bullet_x-j][bullet_y]!=1) {			//��֤���Ḳ��ǽ��
										World.get(worldNumber).mapmap_move_point001[bullet_x-j][bullet_y]=-2;			//��ֵ
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
				
				//��Ļ������
				//ʹ�õڶ����漼��
				if(offscreenimage==null) {
					offscreenimage=this.createImage(MainFrame.WIDTH, MainFrame.HEIGHT);
					goffscreen=offscreenimage.getGraphics();
					}
				//���ø��෽������ֹ�ӵײ��ػ�
				super.paint(goffscreen);
				
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
					    
						
				//˫��̹����ʻ�߼�����������ͬʱ�Ӵ�ͬһ��ǽʱ��������ͼ
				//�����޷��ƶ���ȥ
				//�������̹��1p��2p���ͼ�Ľ���
				bj_1p=playertank_1p.isCollide_bianjie();
				bj_2p=playertank_2p.isCollide_bianjie();
				if(playerTankState_1p==1//���߾��������ײͬһ�߽����л���ͼ
						&&playerTankState_2p==1
						&&bj_1p*bj_2p!=0
						&&bj_1p==bj_2p){
					//�Ƿ���ײ�߿�
					this.mapchange=1;									//��ͼ���
					this.maptime_jianbian=JIANBIAN*2-1;					//���ý������
					 playertank_1p.draw(goffscreen);
					 playertank_1p.caculateDate();						//���㺯���������1p̹���Ĵ�
					 playertank_2p.draw(goffscreen);
					 playertank_2p.caculateDate();						//���㺯���������2p̹���Ĵ�
				}else if(playerTankState_1p==1
						&&playerTankState_2p==-1
						&&bj_1p!=0) {//����1p����ҽ���1p��ײ����߽����л���ͼ
					this.mapchange=1;									//��ͼ���
					this.maptime_jianbian=JIANBIAN*2-1;					//���ý������
					 playertank_1p.draw(goffscreen);
					 playertank_1p.caculateDate();						//���㺯���������1p̹���Ĵ�
				}else if(playerTankState_1p==-1
						&&playerTankState_2p==1
						&&bj_2p!=0) {//����1p����ҽ���1p��ײ����߽����л���ͼ
					this.mapchange=1;									//��ͼ���
					this.maptime_jianbian=JIANBIAN*2-1;					//���ý������
					 playertank_2p.draw(goffscreen);
					 playertank_2p.caculateDate();						//���㺯���������1p̹���Ĵ�
				}else{
					//�������̹��1p
					if(playerTankState_1p==1) {//���1p̹�˴��ʱ�ж�
						int goplayertank_1=1;
						int goplayertank_2=1;
						int goplayertank_3=1;
						//�Ƿ���ײ�߿�
					 	if(bj_1p!=0) {//�����������ͼ�������޷�ǰ��			
					 		goplayertank_1=0;
					 	}
					 	//�Ƿ���ײ����̹��000(����ײ��ը)
				 		for(int i=World.get(worldNumber).spirittank000s.size()-1;i>=0;i--) {
				 			SpiritTank spirittank=World.get(worldNumber).spirittank000s.get(i);
				 			if(spirittank.tankCollide(playertank_1p)) {
				 				playertank_1pBOOM();//��������һ������̹��ͬ���ھ�-���Ӯ
				 				spirittank000BOOM(i);
				 				break;
				 			}
				 		}
				 		//�Ƿ���ײ����̹��001(����ײ��ը)
				 		for(int i=World.get(worldNumber).spirittank001s.size()-1;i>=0;i--) {
				 			SpiritTank001 spirittank001=World.get(worldNumber).spirittank001s.get(i);
				 			if(spirittank001.tankCollide(playertank_1p)) {
				 				playertank_1pBOOM();//��������һ������̹��ͬ���ھ�-���Ӯ
				 				spirittank001BOOM(i);
				 				break;
				 			}
				 		}
					 	//�Ƿ���ײ��ͼ��
					 	if(World.get(worldNumber).isCollide(playertank_1p)) {
					 		goplayertank_3=0;
					 	}else {
					 		goplayertank_3=1;
					 	}
					 	if(goplayertank_1*goplayertank_2*goplayertank_3==1) {
					 		playertank_1p.move();
					 	}		
					 	if(this.timeSpeed_p1==1&&this.timeSpeed_p2==1) {
					 		playertank_1p.draw(goffscreen);
					 	}else {
					 		playertank_1p.draw_0(goffscreen);
					 	}
					 		playertank_1p.caculateDate();						//���㺯���������2p̹���Ĵ�
						}
						//�������̹��2p
						if(playerTankState_2p==1) {//���̹�˴��ʱ�ж�
							int goplayertank_1=1;
							int goplayertank_2=1;
							int goplayertank_3=1;
							//�Ƿ���ײ�߿�
						 	if(bj_2p!=0) {//�����������ͼ�������޷�ǰ��
						 		goplayertank_1=0;
						 	}
						 	//�Ƿ���ײ����̹��000(����ײ��ը)
					 		for(int i=World.get(worldNumber).spirittank000s.size()-1;i>=0;i--) {
					 			SpiritTank spirittank=World.get(worldNumber).spirittank000s.get(i);
					 			if(spirittank.tankCollide(playertank_2p)) {
					 				playertank_2pBOOM();//��������һ������̹��ͬ���ھ�-���Ӯ
					 				spirittank000BOOM(i);
					 				break;
					 			}
					 		}
					 		//�Ƿ���ײ����̹��001(����ײ��ը)
					 		for(int i=World.get(worldNumber).spirittank001s.size()-1;i>=0;i--) {
					 			SpiritTank001 spirittank001=World.get(worldNumber).spirittank001s.get(i);
					 			if(spirittank001.tankCollide(playertank_2p)) {
					 				playertank_2pBOOM();//��������һ������̹��ͬ���ھ�-���Ӯ
					 				spirittank001BOOM(i);
					 				break;
					 			}
					 		}
						 	//�Ƿ���ײ��ͼ
						 	if(World.get(worldNumber).isCollide(playertank_2p)) {
						 		goplayertank_3=0;
						 	}else {
						 		goplayertank_3=1;
						 	}
						 	if(goplayertank_1*goplayertank_2*goplayertank_3==1) {
						 		playertank_2p.move();
						 	}		
						 	if(this.timeSpeed_p1==1&&this.timeSpeed_p2==1) {
						 		playertank_2p.draw(goffscreen);
						 	}else {
						 		playertank_2p.draw_0(goffscreen);
						 	}
						 	playertank_2p.caculateDate();						//���㺯���������2p̹���Ĵ�
						 }
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
						
				
				
				//�����ͼ���
				if(this.timeSpeed_p1==1&&this.timeSpeed_p2==1) {
					World.get(worldNumber).drawBricks(goffscreen);
					World.get(worldNumber).drawStones(goffscreen);
					World.get(worldNumber).drawWaters(goffscreen);
			 	}else  {
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
					if(playerTankState_1p==1) {							//���1p̹�˴����и��ж�
						if(playertank_1p.isCollide(spiritbullet)) {		//�����̹�˵���ײ���
							spiritbulletBOOM(i);	
							playertank_1pBOOM();	
							continue;
						}
					}
					if(playerTankState_2p==1) {							//���2p̹�˴����и��ж�
						if(playertank_2p.isCollide(spiritbullet)) {		//�����̹�˵���ײ���
							spiritbulletBOOM(i);	
							playertank_2pBOOM();	
							continue;
						}
					}
					spiritbullet.move();
					spiritbullet.draw(goffscreen);
					spiritbullet.caculateDate();
				}
	
				if(this.timeSpeed_p1==1&&this.timeSpeed_p2==1) {
					//����ݿ�
					World.get(worldNumber).drawGrasses(goffscreen);
					//���뾫���
					World.get(worldNumber).drawSpirits(goffscreen);
				}else{
					//����ݿ�
					World.get(worldNumber).drawGrasses_0(goffscreen);
					//���뾫���
					World.get(worldNumber).drawSpirits_0(goffscreen);
				}
				
				//�ƶ��ݶȺ�����ԭ
				for(int i=0;i<=46;i++) {
					for(int j=0;j<=35;j++) {
						if(World.get(worldNumber).mapmap_move_point001[i][j]!=1) {
							World.get(worldNumber).mapmap_move_point001[i][j]=0;
						}
					}
				}
				//��ͼ�Ǳ��״̬����
			}
			
			if(mapchange==1) {//����ͼ���
				this.drawALL();										//���Ƶ�ͼ����
				Random random=new Random();							//�����������
				if(maptime_jianbian==GamePanel_2P.JIANBIAN*2-1) {	//�������ʼ״̬
																	//���й���һ
					for(int i=0;i<=46;i++) {						//���и�ֵ
						for(int j=0;j<=35;j++) {
							mapblock[i][j]=random.nextInt(GamePanel_2P.JIANBIAN);
						}
					}
				}
				if(maptime_jianbian==GamePanel_2P.JIANBIAN-1) {				//���������ɹ���һ
																	//���й��̶�
					this.changeBJ();								//������ͼ
			 		for(int i=0;i<=46;i++) {						//���и�ֵ
						for(int j=0;j<=35;j++) {
							mapblock[i][j]=random.nextInt(GamePanel_2P.JIANBIAN);
						}
					}
				}
				if(maptime_jianbian<=GamePanel_2P.JIANBIAN*2-1&&maptime_jianbian>=GamePanel_2P.JIANBIAN) {
					for(int q=0;q<2*GamePanel_2P.JIANBIAN-maptime_jianbian;q++) {				//�����Ӻ�ɫ����
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
				if(maptime_jianbian<GamePanel_2P.JIANBIAN) {
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
	
	
	
		
		/**********************************************************************************/
		
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
	    
		/*******************************************************************************/
		
	  //���̹�˱�ը����1p
	  		private void playertank_1pBOOM() {
	  			World.get(worldNumber).playertankLife[0]--;//��ը���ʤ���ж�
	  			
	  			if(World.get(worldNumber).playertankLife[0]<=0) {			//���������� -1
	  				playerTankState_1p=-1;									//����״̬�������ڼ䲻���������жϣ�
	  			}else if(World.get(worldNumber).playertankLife[0]!=0) {		//�ɸ�������  0
	  				playerTankState_1p=0;									//����״̬�������ڼ䲻���������жϣ�
	  			}
	  			if(playerTankState_1p==-1&&
	  					playerTankState_2p==-1) {
	  				//����䣬�ӳٽ�����Ϸ�������Ƴ�������
	  				mainFrame.setTime(-cv0);
	  				mainFrame.removeKeyListener(this);
	  				gameState=GAMELOSE;
	  			}
	  			
	  			Cartoon cartoon=new Cartoon(Cartoon.TEXPLODE,playertank_1p.getX(),playertank_1p.getY());
	  			cartoon.addFinishListener(new Listener1_1p());
	  			cartoons.add(cartoon);
	  			
	  		}
	  				
	  				
	  		//���̹�˱�ը����2p
	  		private void playertank_2pBOOM() {
	  			World.get(worldNumber).playertankLife[1]--;//��ը���ʤ���ж�
	  			
	  			if(World.get(worldNumber).playertankLife[1]<=0) {			//���������� -1
	  				playerTankState_2p=-1;									//����״̬�������ڼ䲻���������жϣ�
	  			}else if(World.get(worldNumber).playertankLife[1]!=0) {		//�ɸ�������  0
	  				playerTankState_2p=0;									//����״̬�������ڼ䲻���������жϣ�
	  			}
	  			
	  			if(playerTankState_1p==-1&&
	  					playerTankState_2p==-1) {
	  				//����䣬�ӳٽ�����Ϸ�������Ƴ�������
	  				mainFrame.setTime(-cv0);
	  				mainFrame.removeKeyListener(this);
	  				gameState=GAMELOSE;
	  			}
	  			
	  			Cartoon cartoon=new Cartoon(Cartoon.TEXPLODE,playertank_2p.getX(),playertank_2p.getY());
	  			cartoon.addFinishListener(new Listener1_2p());
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
	  				
	  		//���1p̹�˱�ը��ļ�����
	  		private class Listener1_1p extends FinishListener{
	  			public void doFinish() {
	  				Cartoon ct =new Cartoon(Cartoon.TCREATE,0,0);
	  				World.get(worldNumber).initPCartoonData(ct,playertank_1p.getX(),playertank_1p.getY());
	  				//��1pû����������������
	  				if(World.get(worldNumber).playertankLife[0]!=0) {
	  					ct.addFinishListener(new Listener2_1p());
	  					cartoons.add(ct);	
	  				}	
	  			}
	  		}
	  				
	  		//���1p̹�˱�ը��ļ�����
	  		private class Listener1_2p extends FinishListener{
	  			public void doFinish() {
	  				Cartoon ct =new Cartoon(Cartoon.TCREATE,0,0);
	  				World.get(worldNumber).initPCartoonData(ct,playertank_2p.getX(),playertank_2p.getY());
	  				//��2pû����������������
	  				if(World.get(worldNumber).playertankLife[1]!=0) {
	  					ct.addFinishListener(new Listener2_2p());
	  					cartoons.add(ct);	
	  				}	
	  			}
	  		}			
	  		
	  		//���1p̹��������ļ�����
	  		private class Listener2_1p extends FinishListener{
	  			public void doFinish() {
	  				playerTankState_1p=1;									//����̹�˴��״̬
	  			}
	  		}
	  				
	  		//���2p̹��������ļ�����
	  		private class Listener2_2p extends FinishListener{
	  			public void doFinish() {
	  				playerTankState_2p=1;									//����̹�˴��״̬
	  			}
	  		}
	  				
	  		public void keyPressed(KeyEvent e) {
	  			int key =e.getKeyCode();
	  			
	  			if(key==KeyEvent.VK_J) {
	  				//����ģʽ
	  				if(playerTankState_1p==1) {//ֻ�д��״̬��ʱ���ܿ���
	  					for(int j=0;j<playertank_1p.firepower;j++) {
	  						Bullet playerbullet=playertank_1p.fire();
	  						World.get(worldNumber).playerbullets.add(playerbullet);
	  					}
	  				}
	  			}
	  			
	  			if(key==KeyEvent.VK_NUMPAD1){//С��������1
	  				//����ģʽ
	  				if(playerTankState_2p==1) {//ֻ�д��״̬��ʱ���ܿ���
	  					for(int j=0;j<playertank_2p.firepower;j++) {
	  						Bullet playerbullet=playertank_2p.fire();
	  						World.get(worldNumber).playerbullets.add(playerbullet);
	  					}
	  				}
	  			}

	  			if(key==KeyEvent.VK_ESCAPE) {
	  				mainFrame.removeKeyListener(this);
	  				mainFrame.login();
	  			}
	  			
	  			//�����ƶ�����ָ��
	  			playertank_1p.keyPressed(e);
	  			playertank_2p.keyPressed(e);
	  			//ʱ�������ٶȸ�ֵ
	  			timeSpeed_p1=playertank_1p.getTimeSpeed();
	  			timeSpeed_p2=playertank_2p.getTimeSpeed();
	  			this.timeSpeed=playertank_2p.getTimeSpeed()*playertank_1p.getTimeSpeed();
				this.mainFrame.timeSpeed=this.timeSpeed;
	  		}

	  		
	  		
	  		
	  		
	  		public void setWorldNumber(int worldNumber) {
	  			this.worldNumber=worldNumber;
	  		}
	  		
	  		public int getWorldNumber() {
	  			return this.worldNumber;
	  		}
	  		
			@Override
			public void keyReleased(KeyEvent e) {
				playertank_1p.keyReleased(e);
				playertank_2p.keyReleased(e);
				timeSpeed_p1=playertank_1p.getTimeSpeed();
	  			timeSpeed_p2=playertank_2p.getTimeSpeed();
				this.timeSpeed=playertank_2p.getTimeSpeed()*playertank_1p.getTimeSpeed();
				this.mainFrame.timeSpeed=this.timeSpeed;
			}
			
			@Override
			public void keyTyped(KeyEvent e) {
			}
	
	
			private void changeBJ() {										//�÷������ڱ߽���ж������
				if(bj_1p==1) {//p1���ĵ�ͼ����ת��̹������
				 	this.setWorldNumber(this.getWorldNumber()+1);
				 	playertank_1p.setY(MainFrame.HEIGHT-playertank_1p.getY());
				 }else if(bj_1p==2) {
				 	this.setWorldNumber(this.getWorldNumber()+WORLDHEIGHT);
				 	playertank_1p.setX(MainFrame.WIDTH-playertank_1p.getX());
				 }else if(bj_1p==3) {
				 	this.setWorldNumber(this.getWorldNumber()-1);
				 	playertank_1p.setY(MainFrame.HEIGHT-playertank_1p.getY());
				 }else {
				 	this.setWorldNumber(this.getWorldNumber()-WORLDHEIGHT);
				 	playertank_1p.setX(MainFrame.WIDTH-playertank_1p.getX());
				 }
				
				if(bj_2p==1) {//p2�����ĵ�ͼ����ת��̹������
				 	playertank_2p.setY(MainFrame.HEIGHT-playertank_2p.getY());
				 }else if(bj_2p==2) {
				 	playertank_2p.setX(MainFrame.WIDTH-playertank_2p.getX());
				 }else if(bj_2p==3) {
				 	playertank_2p.setY(MainFrame.HEIGHT-playertank_2p.getY());
				 }else {
				 	playertank_2p.setX(MainFrame.WIDTH-playertank_2p.getX());
				 }
				 this.mapX=World.get(worldNumber).getMapX();		//����mapX
				 this.mapY=World.get(worldNumber).getMapY();		//����mapY
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
				
				//�������̹��1p
				if(this.timeSpeed==1) {
			 		playertank_1p.draw(goffscreen);
			 	}else {
			 		playertank_1p.draw_0(goffscreen);
			 	}
				
				//�������̹��2p
				if(this.timeSpeed==1) {
			 		playertank_2p.draw(goffscreen);
			 	}else {
			 		playertank_2p.draw_0(goffscreen);
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
	
	
	
	
	
	
	//class end//
}
