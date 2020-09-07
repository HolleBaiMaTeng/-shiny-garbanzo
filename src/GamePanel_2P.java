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
	 * 胜利目标，到达99号地图
	 * 
	 *双人游戏的系统后门：
	 *在一辆坦克被毁且生命为零时（不可复活状态），另一辆坦克可以在
	 *其复活时间内直接传送他在相同位置进入下一关，并重新获得下一关的生命
	 *一旦死亡坦克返回原地图则彻底消失
	 *
	 *playertank_1p采用主战坦克P1型
	 *playertank_2p采用辅助坦克P2型
	 */
	//下列变量检查init函数
	private MainFrame mainFrame;
	private LoginPanel loginPanel;
	private Image offscreenimage=null;				//第二缓存
	private Graphics goffscreen=null;
	
	private int timeSpeed=1;       						//管理游戏时间流逝速度：
	private int timeSpeed_p1=1;       					//管理游戏时间流逝速度：
	private int timeSpeed_p2=1;       					//管理游戏时间流逝速度：
	
	//预制地图由文件的形式读取，其他地图用随机算法生成
	private ArrayList<String> mapName=new ArrayList<String>();						//地图对应的文件名
	private int[][]  mapName_int;													//储存地图名中的数字
	private int mapchange=0;					 									//0-地图未变动1-地图变动（变动期间使用演示状态）
	private int maptime_jianbian=0;													//切换地图时的渐变设置  0-常态下值 2n-1全态下值
	private int mapblock[][];														//地图渐变设置
	private static final int JIANBIAN=25;											//渐变次数
	private int bj_1p=0;															//1p地图检测
	private int bj_2p=0;															//2p地图检测
	
	private ArrayList<Map> World=new ArrayList<Map>();								//世界
	private static final int WORLDWIDTH=30;											//世界宽度
	private static final int WORLDHEIGHT=30;										//世界高度
		
	//智能化设置
	private static final int spirittank_miss=8;
	private static final int spirittank_see=1000;
	private static final int spirittank_distance=0;
	
	//随地图变换的关卡变量
	private int mapX=0;							    //map的世界横坐标
	private int mapY=0;								//map的世界纵坐标
	private int worldNumber=0;						//世界编号
		
	//playertank创建后一直存在
	private PlayerTank_P1 playertank_1p=null;		//玩家坦克
	private PlayerTank_P2 playertank_2p=null;		//玩家坦克
	private int playerTankState_1p=1;				//玩家坦克1p状态1-正常，0-死亡(可复活),-1-死亡(不可复活)
	private int playerTankState_2p=1;				//玩家坦克2p状态1-正常，0-死亡(可复活),-1-死亡(不可复活)
	private ArrayList<Cartoon> cartoons=new ArrayList<Cartoon>();			//动画播放列表

		//主界面切换控制
		private static final int GAMEWIN=1;
		private static final int GAMELOSE=2;
		private static final int cv0=30;                       //游戏结束后缓冲时间
		private int gameState=0;							   //游戏状态判定0-正常1-胜利2-失败
	
		//构造函数
		public GamePanel_2P(MainFrame mainFrame,LoginPanel loginPanel)  {//现场调用World.get(worldNumber)
			super();
			this.mapblock=new int[47][36];					//初始化地图渐变状态数组
			this.mainFrame=mainFrame;
			this.loginPanel=loginPanel;
			
			//玩家坦克的初始化	
			playertank_1p=new PlayerTank_P1(350,350,loginPanel.player_1p_tankmodel);
			playertank_2p=new PlayerTank_P2(450,350,loginPanel.player_2p_tankmodel);	
			
			//地图名拓展读取
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
					
			//将mapName中的数字提取至mapName_int
			mapName_int=new int[mapName.size()][2];
			String str1;
			for(int i=0;i<mapName.size();i++) {
				str1=mapName.get(i);								//暂存文件名
				StringTokenizer st = new StringTokenizer(str1,".");	//用.分割
				mapName_int[i][0]=Integer.parseInt(st.nextToken());	//分别赋值
				mapName_int[i][1]=Integer.parseInt(st.nextToken());	//分别赋值
			}
					
			World.clear(); 							//世界清空
			initWorld();   							//世界初始化(建造World并读入与生成所有地图数据)
													//同时计算初始点坐标，mapx,mapy,worldNumber
		}
	
	
		//绘图paint函数
		public void paint(Graphics g) {
			
			//显示数据
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
			//对游戏状态的判定
			if(mainFrame.getTime()==-1) {
				if(gameState==GAMEWIN) {//监视器没有移除
					mainFrame.removeKeyListener(this);
					mainFrame.startGameWin();
				}
				if(gameState==GAMELOSE) {//监视器已经移除
					mainFrame.startGameOver();
				}
			}
			
			//现存坦克状态检测
			if(World.get(worldNumber).playertankLife[1]<=0) {
				playerTankState_2p=-1;	
			}
			if(World.get(worldNumber).playertankLife[0]<=0) {
				playerTankState_1p=-1;	
			}
			
			/**共用检测部分**/
			if(mapchange==0) {										//若此时为非地图变更状态
				
				//实时运算玩家坦克梯度运算
				//凝结核部分
				int playertank_block_x_1p=playertank_1p.getX()/17;
				int playertank_block_y_1p=playertank_1p.getY()/17;
				int playertank_block_x_2p=playertank_2p.getX()/17;
				int playertank_block_y_2p=playertank_2p.getY()/17;
				int key=0;											//令牌，当未进行任何操作时，跳出循环
				//设置权值点位置
				//方法一：自爆型权值点
				if(playerTankState_1p==1) {
					if(World.get(worldNumber).mapmap_move_point001[playertank_block_x_1p][playertank_block_y_1p]!=1) {
						World.get(worldNumber).mapmap_move_point001[playertank_block_x_1p][playertank_block_y_1p]=-spirittank_see;//设置最低梯度
					}
					if(playertank_block_x_1p+1<48) {
						if(World.get(worldNumber).mapmap_move_point001[playertank_block_x_1p+1][playertank_block_y_1p]!=1) {
							World.get(worldNumber).mapmap_move_point001[playertank_block_x_1p+1][playertank_block_y_1p]=-spirittank_see;//设置最低梯度
						}
					}
					if(playertank_block_y_1p+1<37) {
						if(World.get(worldNumber).mapmap_move_point001[playertank_block_x_1p][playertank_block_y_1p+1]!=1) {
							World.get(worldNumber).mapmap_move_point001[playertank_block_x_1p][playertank_block_y_1p+1]=-spirittank_see;//设置最低梯度
						}
					}
					if(playertank_block_y_1p+1<37&&playertank_block_x_1p+1<48) {
						if(World.get(worldNumber).mapmap_move_point001[playertank_block_x_1p+1][playertank_block_y_1p+1]!=1) {
							World.get(worldNumber).mapmap_move_point001[playertank_block_x_1p+1][playertank_block_y_1p+1]=-spirittank_see;//设置最低梯度
						}
					}
				}
				if(playerTankState_2p==1) {
					if(World.get(worldNumber).mapmap_move_point001[playertank_block_x_2p][playertank_block_y_2p]!=1) {
						World.get(worldNumber).mapmap_move_point001[playertank_block_x_2p][playertank_block_y_2p]=-spirittank_see;//设置最低梯度
					}
					if(playertank_block_x_2p+1<48) {
						if(World.get(worldNumber).mapmap_move_point001[playertank_block_x_2p+1][playertank_block_y_2p]!=1) {
							World.get(worldNumber).mapmap_move_point001[playertank_block_x_2p+1][playertank_block_y_2p]=-spirittank_see;//设置最低梯度
						}
					}
					if(playertank_block_y_1p+1<37) {
						if(World.get(worldNumber).mapmap_move_point001[playertank_block_x_2p][playertank_block_y_2p+1]!=1) {
							World.get(worldNumber).mapmap_move_point001[playertank_block_x_2p][playertank_block_y_2p+1]=-spirittank_see;//设置最低梯度
						}
					}
					if(playertank_block_y_1p+1<37&&playertank_block_x_1p+1<48) {
						if(World.get(worldNumber).mapmap_move_point001[playertank_block_x_2p+1][playertank_block_y_2p+1]!=1) {
							World.get(worldNumber).mapmap_move_point001[playertank_block_x_2p+1][playertank_block_y_2p+1]=-spirittank_see;//设置最低梯度
						}
					}	
				}
				//衍生部分2：避让权值
				
				//衍生部分
				int[][] lingpai=new int[48][37];				//循环中的身份令牌
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
									lingpai[i][j]==0) {//判定为非墙，非0，非本轮运算
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
					//复原运算本轮运算判定
					for(int i=0;i<=47;i++) {
						for(int j=0;j<=36;j++) {
							lingpai[i][j]=0;
						}
					}
				}
				//非衍生部分
				for(int i=0;i<World.get(worldNumber).playerbullets.size();i++) {//避让设置
					Bullet playerbullet=World.get(worldNumber).playerbullets.get(i);
					int bullet_x=playerbullet.getX()/17;
					int bullet_y=playerbullet.getY()/17;
					if(playerbullet.getDiraction()==Spirit.UP) {				/***方向为上***/
						if(playerbullet.getX()%17<=8) {							//左判定
							for(int j=0;j<spirittank_miss;j++) {				//循环执行避让距离设置
								if(bullet_y-j>=0) {								//确保判定合理性
									if(World.get(worldNumber).mapmap_move_point001[bullet_x][bullet_y-j]!=1) {		//保证不会覆盖墙体
										World.get(worldNumber).mapmap_move_point001[bullet_x][bullet_y-j]=-1;		//赋值
									}
								}
							}
							for(int j=0;j<spirittank_miss;j++) {				//循环执行避让距离设置
								if(bullet_y-j>=0&&bullet_x+1<48) {				//确保判定合理性
									if(World.get(worldNumber).mapmap_move_point001[bullet_x+1][bullet_y-j]!=1) {	//保证不会覆盖墙体
										World.get(worldNumber).mapmap_move_point001[bullet_x+1][bullet_y-j]=-2;     //赋值
									}
								}
							}
						}else {													//右判定				
							for(int j=0;j<spirittank_miss;j++) {				//循环执行避让距离设置
								if(bullet_y-j>=0&&bullet_x+1<48) {				//确保判定合理性
									if(World.get(worldNumber).mapmap_move_point001[bullet_x+1][bullet_y-j]!=1) {	//保证不会覆盖墙体
										World.get(worldNumber).mapmap_move_point001[bullet_x+1][bullet_y-j]=-1;     //赋值
									}
								}
							}
							for(int j=0;j<spirittank_miss;j++) {				//循环执行避让距离设置
								if(bullet_y-j>=0) {								//确保判定合理性
									if(World.get(worldNumber).mapmap_move_point001[bullet_x][bullet_y-j]!=1) {		//保证不会覆盖墙体
										World.get(worldNumber).mapmap_move_point001[bullet_x][bullet_y-j]=-2;		//赋值
									}
								}
							}
						}
					}else if(playerbullet.getDiraction()==Spirit.RIGHT) {		/***方向为右***/
						if(playerbullet.getY()%17<=8) {							//上判定
							for(int j=0;j<spirittank_miss;j++) {				//循环执行避让距离设置
								if(bullet_x+j<48) {								//确保判定合理性
									if(World.get(worldNumber).mapmap_move_point001[bullet_x+j][bullet_y]!=1) {		//保证不会覆盖墙体
										World.get(worldNumber).mapmap_move_point001[bullet_x+j][bullet_y]=-1;     	//赋值
									}
								}
							}
							for(int j=0;j<spirittank_miss;j++) {				//循环执行避让距离设置
								if(bullet_x+j<48&&bullet_y+1<37) {				//确保判定合理性
									if(World.get(worldNumber).mapmap_move_point001[bullet_x+j][bullet_y+1]!=1) {		//保证不会覆盖墙体
										World.get(worldNumber).mapmap_move_point001[bullet_x+j][bullet_y+1]=-2;     	//赋值
									}
								}
							}
						}else {													//下判定
							for(int j=0;j<spirittank_miss;j++) {				//循环执行避让距离设置
								if(bullet_x+j<48&&bullet_y+1<37) {				//确保判定合理性
									if(World.get(worldNumber).mapmap_move_point001[bullet_x+j][bullet_y+1]!=1) {		//保证不会覆盖墙体
										World.get(worldNumber).mapmap_move_point001[bullet_x+j][bullet_y+1]=-1;     	//赋值
									}
								}
							}
							for(int j=0;j<spirittank_miss;j++) {				//循环执行避让距离设置
								if(bullet_x+j<48) {								//确保判定合理性
									if(World.get(worldNumber).mapmap_move_point001[bullet_x+j][bullet_y]!=1) {		//保证不会覆盖墙体
										World.get(worldNumber).mapmap_move_point001[bullet_x+j][bullet_y]=-2;     	//赋值
									}
								}
							}
						}
					}else if(playerbullet.getDiraction()==Spirit.DOWN) {		/***方向为下***/
						if(playerbullet.getX()%17<=8) {							//左判定
							for(int j=0;j<spirittank_miss;j++) {				//循环执行避让距离设置
								if(bullet_y+j<37) {								//确保判定合理性
									if(World.get(worldNumber).mapmap_move_point001[bullet_x][bullet_y+j]!=1) {			//保证不会覆盖墙体
										World.get(worldNumber).mapmap_move_point001[bullet_x][bullet_y+j]=-1;			//赋值
									}
								}
							}
							for(int j=0;j<spirittank_miss;j++) {				//循环执行避让距离设置
								if(bullet_y+j<37&&bullet_x+1<48) {				//确保判定合理性
									if(World.get(worldNumber).mapmap_move_point001[bullet_x+1][bullet_y+j]!=1) {		//保证不会覆盖墙体
										World.get(worldNumber).mapmap_move_point001[bullet_x+1][bullet_y+j]=-2;			//赋值
									}
								}
							}
						}else {													//右判定
							for(int j=0;j<spirittank_miss;j++) {				//循环执行避让距离设置
								if(bullet_y+j<37&&bullet_x+1<48) {				//确保判定合理性
									if(World.get(worldNumber).mapmap_move_point001[bullet_x+1][bullet_y+j]!=1) {		//保证不会覆盖墙体
										World.get(worldNumber).mapmap_move_point001[bullet_x+1][bullet_y+j]=-1;			//赋值
									}
								}
							}
							for(int j=0;j<spirittank_miss;j++) {				//循环执行避让距离设置
								if(bullet_y+j<37) {								//确保判定合理性
									if(World.get(worldNumber).mapmap_move_point001[bullet_x][bullet_y+j]!=1) {			//保证不会覆盖墙体
										World.get(worldNumber).mapmap_move_point001[bullet_x][bullet_y+j]=-2;			//赋值
									}
								}
							}
						}
					}else if(playerbullet.getDiraction()==Spirit.LEFT) {		/***方向为左***/
						if(playerbullet.getY()%17<=8) {							//上判定
							for(int j=0;j<spirittank_miss;j++) {				//循环执行避让距离设置
								if(bullet_x-j>=0) {								//确保判定合理性
									if(World.get(worldNumber).mapmap_move_point001[bullet_x-j][bullet_y]!=1) {			//保证不会覆盖墙体
										World.get(worldNumber).mapmap_move_point001[bullet_x-j][bullet_y]=-1;			//赋值
									}
								}
							}
							for(int j=0;j<spirittank_miss;j++) {				//循环执行避让距离设置
								if(bullet_x-j>=0&&bullet_y+1<37) {								//确保判定合理性
									if(World.get(worldNumber).mapmap_move_point001[bullet_x-j][bullet_y+1]!=1) {			//保证不会覆盖墙体
										World.get(worldNumber).mapmap_move_point001[bullet_x-j][bullet_y+1]=-2;			//赋值
									}
								}
							}
						}else {													//下判定
							for(int j=0;j<spirittank_miss;j++) {				//循环执行避让距离设置
								if(bullet_x-j>=0&&bullet_y+1<37) {								//确保判定合理性
									if(World.get(worldNumber).mapmap_move_point001[bullet_x-j][bullet_y+1]!=1) {			//保证不会覆盖墙体
										World.get(worldNumber).mapmap_move_point001[bullet_x-j][bullet_y+1]=-1;			//赋值
									}
								}
							}
							for(int j=0;j<spirittank_miss;j++) {				//循环执行避让距离设置
								if(bullet_x-j>=0) {								//确保判定合理性
									if(World.get(worldNumber).mapmap_move_point001[bullet_x-j][bullet_y]!=1) {			//保证不会覆盖墙体
										World.get(worldNumber).mapmap_move_point001[bullet_x-j][bullet_y]=-2;			//赋值
									}
								}
							}
						}
					}
				}
				/*********
				//显示数字地图运算过程
				for(int i=0;i<=36;i++) {
					for(int j=0;j<=47;j++) {
						String str1=String.format("% 5d",World.get(worldNumber).mapmap_move_point001[j][i] );
						System.out.printf(str1);
					}
					System.out.println("");
				}
				System.out.println("");
				/*********/
				
				//屏幕处理技术
				//使用第二缓存技术
				if(offscreenimage==null) {
					offscreenimage=this.createImage(MainFrame.WIDTH, MainFrame.HEIGHT);
					goffscreen=offscreenimage.getGraphics();
					}
				//调用父类方法，防止从底层重绘
				super.paint(goffscreen);
				
				//清除现有屏幕
				Color c=goffscreen.getColor();
				goffscreen.setColor(Color.BLACK);
				goffscreen.fillRect(0, 0, MainFrame.WIDTH, MainFrame.HEIGHT);
				goffscreen.setColor(c);
	
						
				//处理动画
				for(int i=cartoons.size()-1;i>=0;i--) {
					Cartoon cartoon=cartoons.get(i);
					if(!cartoon.draw(goffscreen)) {
						cartoons.remove(i);
					}
				}
						
				
				//随机生成精灵坦克000(由map中数据决定是否生成)
				SpiritTank stank000=World.get(worldNumber).CreateSpirittank000();
				if(stank000!=null) {
					World.get(worldNumber).spirittank000s.add(stank000);
				}
				//随机生成精灵坦克001(由map中数据决定是否生成)
				SpiritTank001 stank001=World.get(worldNumber).CreateSpirittank001();
				if(stank001!=null) {
					World.get(worldNumber).spirittank001s.add(stank001);
				}
					    
						
				//双人坦克行驶逻辑：仅当两者同时接触同一面墙时，更换地图
				//否则无法移动过去
				//处理玩家坦克1p，2p与地图的交互
				bj_1p=playertank_1p.isCollide_bianjie();
				bj_2p=playertank_2p.isCollide_bianjie();
				if(playerTankState_1p==1//两者均存活且碰撞同一边界能切换地图
						&&playerTankState_2p==1
						&&bj_1p*bj_2p!=0
						&&bj_1p==bj_2p){
					//是否碰撞边框
					this.mapchange=1;									//地图变更
					this.maptime_jianbian=JIANBIAN*2-1;					//设置渐变次数
					 playertank_1p.draw(goffscreen);
					 playertank_1p.caculateDate();						//计算函数负责调整1p坦克履带
					 playertank_2p.draw(goffscreen);
					 playertank_2p.caculateDate();						//计算函数负责调整2p坦克履带
				}else if(playerTankState_1p==1
						&&playerTankState_2p==-1
						&&bj_1p!=0) {//仅有1p存活且仅有1p碰撞任意边界能切换地图
					this.mapchange=1;									//地图变更
					this.maptime_jianbian=JIANBIAN*2-1;					//设置渐变次数
					 playertank_1p.draw(goffscreen);
					 playertank_1p.caculateDate();						//计算函数负责调整1p坦克履带
				}else if(playerTankState_1p==-1
						&&playerTankState_2p==1
						&&bj_2p!=0) {//仅有1p存活且仅有1p碰撞任意边界能切换地图
					this.mapchange=1;									//地图变更
					this.maptime_jianbian=JIANBIAN*2-1;					//设置渐变次数
					 playertank_2p.draw(goffscreen);
					 playertank_2p.caculateDate();						//计算函数负责调整1p坦克履带
				}else{
					//处理玩家坦克1p
					if(playerTankState_1p==1) {//玩家1p坦克存活时判断
						int goplayertank_1=1;
						int goplayertank_2=1;
						int goplayertank_3=1;
						//是否碰撞边框
					 	if(bj_1p!=0) {//不满足更换地图条件，无法前进			
					 		goplayertank_1=0;
					 	}
					 	//是否碰撞精灵坦克000(相碰撞后爆炸)
				 		for(int i=World.get(worldNumber).spirittank000s.size()-1;i>=0;i--) {
				 			SpiritTank spirittank=World.get(worldNumber).spirittank000s.get(i);
				 			if(spirittank.tankCollide(playertank_1p)) {
				 				playertank_1pBOOM();//玩家与最后一辆精灵坦克同归于尽-玩家赢
				 				spirittank000BOOM(i);
				 				break;
				 			}
				 		}
				 		//是否碰撞精灵坦克001(相碰撞后爆炸)
				 		for(int i=World.get(worldNumber).spirittank001s.size()-1;i>=0;i--) {
				 			SpiritTank001 spirittank001=World.get(worldNumber).spirittank001s.get(i);
				 			if(spirittank001.tankCollide(playertank_1p)) {
				 				playertank_1pBOOM();//玩家与最后一辆精灵坦克同归于尽-玩家赢
				 				spirittank001BOOM(i);
				 				break;
				 			}
				 		}
					 	//是否碰撞地图块
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
					 		playertank_1p.caculateDate();						//计算函数负责调整2p坦克履带
						}
						//处理玩家坦克2p
						if(playerTankState_2p==1) {//玩家坦克存活时判断
							int goplayertank_1=1;
							int goplayertank_2=1;
							int goplayertank_3=1;
							//是否碰撞边框
						 	if(bj_2p!=0) {//不满足更换地图条件，无法前进
						 		goplayertank_1=0;
						 	}
						 	//是否碰撞精灵坦克000(相碰撞后爆炸)
					 		for(int i=World.get(worldNumber).spirittank000s.size()-1;i>=0;i--) {
					 			SpiritTank spirittank=World.get(worldNumber).spirittank000s.get(i);
					 			if(spirittank.tankCollide(playertank_2p)) {
					 				playertank_2pBOOM();//玩家与最后一辆精灵坦克同归于尽-玩家赢
					 				spirittank000BOOM(i);
					 				break;
					 			}
					 		}
					 		//是否碰撞精灵坦克001(相碰撞后爆炸)
					 		for(int i=World.get(worldNumber).spirittank001s.size()-1;i>=0;i--) {
					 			SpiritTank001 spirittank001=World.get(worldNumber).spirittank001s.get(i);
					 			if(spirittank001.tankCollide(playertank_2p)) {
					 				playertank_2pBOOM();//玩家与最后一辆精灵坦克同归于尽-玩家赢
					 				spirittank001BOOM(i);
					 				break;
					 			}
					 		}
						 	//是否碰撞地图
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
						 	playertank_2p.caculateDate();						//计算函数负责调整2p坦克履带
						 }
					}
				 	
				//处理精灵坦克000
				for(int i=World.get(worldNumber).spirittank000s.size()-1;i>=0;i--) {
					SpiritTank stank00=World.get(worldNumber).spirittank000s.get(i);
					//每一辆精灵坦克进行处理
					 int gospirittank_1=1;
					 int gospirittank_2=1;
					 int gospirittank_3=1;
					//是否碰撞边框
						if(stank00.isCollide_bianjie()!=0) {
							gospirittank_1=0;
						}else {
							gospirittank_1=1;
						}
					//是否碰撞玩家坦克（玩家坦克中已处理）
						
					//是否碰撞了地图快
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
				
				//处理精灵坦克001
				for(int i=World.get(worldNumber).spirittank001s.size()-1;i>=0;i--) {
					SpiritTank001 stank01=World.get(worldNumber).spirittank001s.get(i);
					//每一辆精灵坦克进行处理
					 int gospirittank_1=1;
					 int gospirittank_2=1;
					 int gospirittank_3=1;
					//是否碰撞边框
						if(stank01.isCollide_bianjie()!=0) {
							gospirittank_1=0;
						}else {
							gospirittank_1=1;
						}
					//是否碰撞玩家坦克（玩家坦克中已处理）
						
					//是否碰撞了地图快
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
						
				
				
				//画入地图组件
				if(this.timeSpeed_p1==1&&this.timeSpeed_p2==1) {
					World.get(worldNumber).drawBricks(goffscreen);
					World.get(worldNumber).drawStones(goffscreen);
					World.get(worldNumber).drawWaters(goffscreen);
			 	}else  {
			 		World.get(worldNumber).drawBricks_0(goffscreen);
					World.get(worldNumber).drawStones_0(goffscreen);
					World.get(worldNumber).drawWaters_0(goffscreen);
			 	}
				
						
				
				
				//处理玩家坦克发射的炮弹
				for(int i=World.get(worldNumber).playerbullets.size()-1;i>=0;i--) {
					Bullet playerbullet=World.get(worldNumber).playerbullets.get(i);	
					//判断是否击中地图块
					if(World.get(worldNumber).isCollide(playerbullet)) {
						playerbulletBOOM(i);
						continue;
					}	
					//判断边界碰撞
					if(playerbullet.isCollide_bianjie()!=0) {
						playerbulletBOOM(i);
						continue;
					}	
					//判断是否击中精灵坦克000
					int had_1=0;//表明未执行
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
					//判断是否击中精灵坦克001
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
					//判断是否击中精灵炮弹(炮弹判定时增大判定体积)
					int had_2=0;//表明未执行
					for(int k=World.get(worldNumber).spiritbullets.size()-1;k>=0;k--) {
						Bullet spiritbullet=World.get(worldNumber).spiritbullets.get(k);
						spiritbullet.setWidth(Bullet.BULLET_W_SPECIAL);//判断前将精灵炮弹调至30-w
						if(spiritbullet.isCollide(playerbullet)) {
							spiritbullet.setWidth(Bullet.BULLET_W_NORMAL);//判断后将精灵炮弹调至2-w
							playerbulletBOOM(i);
							spiritbulletBOOM(k);
							had_2=1;
							break;
						}
						spiritbullet.setWidth(Bullet.BULLET_W_NORMAL);//判断后将精灵炮弹调至2-w
					}
					if(had_2==1) {
						continue;
					}
					playerbullet.move();
					playerbullet.draw(goffscreen);
					playerbullet.caculateDate();
				}
							
				
						
				//处理精灵坦克发射的炮弹
				for(int i=World.get(worldNumber).spiritbullets.size()-1;i>=0;i--) {
					Bullet spiritbullet=World.get(worldNumber).spiritbullets.get(i);
					//判断边界碰撞
					if(spiritbullet.isCollide_bianjie()!=0) {
						spiritbulletBOOM(i);
						continue;
					}
					//判断地图碰撞
					if(World.get(worldNumber).isCollide(spiritbullet)) {
						spiritbulletBOOM(i);
						continue;
					}
					if(playerTankState_1p==1) {							//玩家1p坦克存活，进行该判断
						if(playertank_1p.isCollide(spiritbullet)) {		//与玩家坦克的碰撞检测
							spiritbulletBOOM(i);	
							playertank_1pBOOM();	
							continue;
						}
					}
					if(playerTankState_2p==1) {							//玩家2p坦克存活，进行该判断
						if(playertank_2p.isCollide(spiritbullet)) {		//与玩家坦克的碰撞检测
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
					//画入草块
					World.get(worldNumber).drawGrasses(goffscreen);
					//画入精灵块
					World.get(worldNumber).drawSpirits(goffscreen);
				}else{
					//画入草块
					World.get(worldNumber).drawGrasses_0(goffscreen);
					//画入精灵块
					World.get(worldNumber).drawSpirits_0(goffscreen);
				}
				
				//移动梯度函数还原
				for(int i=0;i<=46;i++) {
					for(int j=0;j<=35;j++) {
						if(World.get(worldNumber).mapmap_move_point001[i][j]!=1) {
							World.get(worldNumber).mapmap_move_point001[i][j]=0;
						}
					}
				}
				//地图非变更状态结束
			}
			
			if(mapchange==1) {//若地图变更
				this.drawALL();										//绘制地图画面
				Random random=new Random();							//创建随机变量
				if(maptime_jianbian==GamePanel_2P.JIANBIAN*2-1) {	//若是最初始状态
																	//进行过程一
					for(int i=0;i<=46;i++) {						//进行赋值
						for(int j=0;j<=35;j++) {
							mapblock[i][j]=random.nextInt(GamePanel_2P.JIANBIAN);
						}
					}
				}
				if(maptime_jianbian==GamePanel_2P.JIANBIAN-1) {				//如果渐变完成过程一
																	//进行过程二
					this.changeBJ();								//更换地图
			 		for(int i=0;i<=46;i++) {						//进行赋值
						for(int j=0;j<=35;j++) {
							mapblock[i][j]=random.nextInt(GamePanel_2P.JIANBIAN);
						}
					}
				}
				if(maptime_jianbian<=GamePanel_2P.JIANBIAN*2-1&&maptime_jianbian>=GamePanel_2P.JIANBIAN) {
					for(int q=0;q<2*GamePanel_2P.JIANBIAN-maptime_jianbian;q++) {				//逐渐增加黑色方块
						for(int i=0;i<=46;i++) {										//进行赋值
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
					for(int q=0;q<maptime_jianbian;q++) {				//逐渐减少黑色方块
						for(int i=0;i<=46;i++) {										//进行赋值
							for(int j=0;j<=35;j++) {
								if(mapblock[i][j]==q) {
									goffscreen.fill3DRect(i*17, j*17, 17, 17, true);
									//ImageUtil.getInstance().drawStone(goffscreen, i*17, j*17);
								}
							}
						}
					}
				}
				maptime_jianbian--;									//切换下一帧
				//地图变更状态结束
				if(maptime_jianbian==0) {							//结尾判定
					this.mapchange=0;								//调整为地图运算状态
				}
			}
			
			
			//一次性绘制
			g.drawImage(offscreenimage, 0, 0,null);
			//paint end//
		}	
	
	
	
		
		/**********************************************************************************/
		
		private void initWorld() {//重新加载世界
			//世界初始化
			for(int i=0;i<WORLDWIDTH;i++) {//i横坐标
				for(int j=0;j<WORLDWIDTH;j++) {//j纵坐标
					//判定是否需要读入数据(mapNmae_int与现有是否一致)
					int lingpai=0;										//制作令牌
					for(int k=0;k<mapName.size();k++) {
						if(mapName_int[k][0]==i&&mapName_int[k][1]==j) {//若一致则读取对应文件
							Map map_x1=new Map(i,j);					//x1型地图为读取文件型地图
							try {
								map_x1.readData(mapName.get(k)); 			//读取对应名字的地图
							}catch(IOException e) {
								e.printStackTrace();
							}
							
							/****定制各个地图执行特殊操作****/
							if(true) {
								this.maprules(map_x1);
							}
							if(false) {
								this.mapRandom_x1(map_x1,2);			//添加噪音
							}
							
							maprules(map_x1);							//标准化规范
							map_x1.copy_map_gradient(map_x1.mapmap);	//对应移动梯度变化
							this.mapfill(map_x1);						//数字化地图转换实体化地图
							World.add(map_x1);							//写入世界中
							lingpai=1;									//如果读了文件，不用随机生成
							System.out.println("World:("+i+
							","+j+") ready"+
							" Finished "+(i*WORLDWIDTH+j+1)*100/
							(WORLDWIDTH*WORLDWIDTH)+"%");				//生成世界汇报
							break;										//跳出循判定下一个点
						}
					}
					if(lingpai==0) {									//令牌为零时判定
						Map map_x2=new Map(i,j);						//随机型
						
						Random random=new Random();
						mapRandom_x2(map_x2,2-random.nextInt(2));							//随机生成世界
						
						//划分世界区域：
						this.ChangeBlockMake(map_x2);					//渐进地图变形
						
						maprules(map_x2);								//标准化规范
						
						map_x2.copy_map_gradient(map_x2.mapmap);		//对应移动梯度变化
						mapfill(map_x2);								//数字化地图转换实体化地图
						
						System.out.println("World:("+i+	
								","+j+") ready"+
								" Finished "+(i*WORLDWIDTH+j+1)*100/
								(WORLDWIDTH*WORLDWIDTH)+"%");			//生成世界汇报
						World.add(map_x2);								//写入世界中
					}
				}
			}
			
			
			//有四种可能的出生点情况：0-水，1-森林，2-石，3-土
			Random random=new Random();
			switch(random.nextInt(4)) {
			case 0:
				this.worldNumber=WORLDHEIGHT-1;								//左上角-水
				break;
			case 1:
				this.worldNumber=WORLDHEIGHT*WORLDWIDTH-1;					//右上角-森林
				break;
			case 2:
				this.worldNumber=WORLDHEIGHT*WORLDWIDTH-WORLDHEIGHT;		//右下角-石
				break;
			case 3:
				this.worldNumber=0;											//左下角-土
			}
			/*
			//原始出生点
			this.worldNumber=0;
			*/
			this.mapX=World.get(worldNumber).getMapX();
			this.mapY=World.get(worldNumber).getMapY();
			this.worldNumber=mapX*WORLDHEIGHT+mapY;
		}
		
		private void mapRandom_x2(Map map,int qmax) {		//随机方法填充map地图//完全空白
			Random random=new Random();
			int qqq=random.nextInt(2);
			if(qqq==0){
				this.eightMapMake(map,qmax);
				//this.fourMapMake(map);
			}else {
				this.fourMapMake(map,qmax+2);				//保证数量效果基本一致
				//this.eightMapMake(map);
			}
		}
		
		private void maprules(Map map) {
			//规范处理World边界(边界处理)
			if(map.getMapX()==0) {					//若World左界，则左边界为水
				for(int i=0;i<=35;i++) {
					map.mapmap[0][i]=Map.STONE;
				}
			}
			if(map.getMapX()==WORLDWIDTH-1) {		//若World右界，则右边界为水
				for(int i=0;i<=35;i++) {
					map.mapmap[46][i]=Map.STONE;
				}
			}
			if(map.getMapY()==0) {					//若World下界，则下边界为石
				for(int i=0;i<=46;i++) {
					map.mapmap[i][35]=Map.STONE;
				}
			}
			if(map.getMapY()==WORLDHEIGHT-1) {		//若World上界，则上边界为石
				for(int i=0;i<=46;i++) {
					map.mapmap[i][0]=Map.STONE;
				}
			}
		}
		
		private void mapRandom_x1(Map map,int qmax) {       		//随机方法填充map地图//在原有基础上进行添加
			Random random=new Random();
			//将mapamp调为原始模式（不改变原有边界）
			for(int i=0;i<=46;i++) {
				for(int j=0;j<=35;j++) {
					if(map.mapmap[i][j]!=Map.BRICK&&
							map.mapmap[i][j]!=Map.GRAESS&&
							map.mapmap[i][j]!=Map.STONE&&
							map.mapmap[i][j]!=Map.WATER&&
							map.mapmap[i][j]!=Map.SPIRIT1) {
						map.mapmap[i][j]=random.nextInt(50)+1;//添加随机地图块
					}
				}
			}
			for(int q=0;q<qmax;q++) {//执行该操作的次数
				for(int i=1;i<=45;i++) {//同化区域地图
					for(int j=1;j<=34;j++) {
						if(map.mapmap[i][j]==Map.BRICK||
							map.mapmap[i][j]==Map.WATER||
							map.mapmap[i][j]==Map.STONE||
							map.mapmap[i][j]==Map.GRAESS) {
							if(qmax%2==0) {
								if(map.mapmap[i-1][j]!=Map.SPIRIT1&&map.mapmap[i-1][j]!=Map.SPIRIT0) {
									if(mol(map.mapmap[i][j],map.mapmap[i-1][j])>5) {//同化区域地图
										map.mapmap[i-1][j]=map.mapmap[i][j];
									}
								}
								if(map.mapmap[i][j-1]!=Map.SPIRIT1&&map.mapmap[i][j-1]!=Map.SPIRIT0) {
									if(mol(map.mapmap[i][j],map.mapmap[i][j-1])>5) {//同化区域地图
										map.mapmap[i][j-1]=map.mapmap[i][j];
									}
								}
								if(map.mapmap[i+1][j]!=Map.SPIRIT1&&map.mapmap[i+1][j]!=Map.SPIRIT0) {
									if(mol(map.mapmap[i+1][j],map.mapmap[i+1][j])>5) {//同化区域地图
										map.mapmap[i+1][j]=map.mapmap[i][j];
									}
								}
								if(map.mapmap[i][j+1]!=Map.SPIRIT1&&map.mapmap[i][j+1]!=Map.SPIRIT0) {
									if(mol(map.mapmap[i][j+1],map.mapmap[i][j+1])>5) {//同化区域地图
										map.mapmap[i][j+1]=map.mapmap[i][j];
									}
								}
							}else {
								if(map.mapmap[i-1][j]!=Map.SPIRIT1&&map.mapmap[i-1][j]!=Map.SPIRIT0) {
									if(mol(map.mapmap[i][j],map.mapmap[i-1][j])>5) {//同化区域地图
										map.mapmap[i-1][j]=map.mapmap[i][j];
									}
								}
								if(map.mapmap[i][j-1]!=Map.SPIRIT1&&map.mapmap[i][j-1]!=Map.SPIRIT0) {
									if(mol(map.mapmap[i][j],map.mapmap[i][j-1])>24) {//同化区域地图
										map.mapmap[i][j-1]=map.mapmap[i][j];
									}
								}
								if(map.mapmap[i-1][j]!=Map.SPIRIT1&&map.mapmap[i-1][j]!=Map.SPIRIT0) {
									if(mol(map.mapmap[i+1][j],map.mapmap[i+1][j])>25) {//同化区域地图
										map.mapmap[i+1][j]=map.mapmap[i][j];
									}
								}
								if(map.mapmap[i][j+1]!=Map.SPIRIT1&&map.mapmap[i][j+1]!=Map.SPIRIT0) {
									if(mol(map.mapmap[i][j+1],map.mapmap[i][j+1])>15) {//同化区域地图
										map.mapmap[i][j+1]=map.mapmap[i][j];
									}
								}
							}
						}
					}
				}
			}
		}
		
		private void fourMapMake(Map map,int qmax) {   				//四格随机衍生地图
			Random random=new Random();
			//将mapamp调为原始模式
			for(int i=0;i<=46;i++) {
				for(int j=0;j<=35;j++) {
					map.mapmap[i][j]=random.nextInt(50)+1;//添加随机地图块
				}
			}
			for(int q=0;q<qmax;q++) {//执行该操作的次数
				for(int i=1;i<=45;i++) {//同化区域地图
					for(int j=1;j<=34;j++) {
						if(map.mapmap[i][j]==Map.BRICK||
							map.mapmap[i][j]==Map.WATER||
							map.mapmap[i][j]==Map.STONE||
							map.mapmap[i][j]==Map.GRAESS) {
							if(qmax%2==0) {
								if(map.mapmap[i-1][j]!=Map.SPIRIT1&&map.mapmap[i-1][j]!=Map.SPIRIT0) {
									if(mol(map.mapmap[i][j],map.mapmap[i-1][j])>5) {//同化区域地图
										map.mapmap[i-1][j]=map.mapmap[i][j];
									}
								}
								if(map.mapmap[i][j-1]!=Map.SPIRIT1&&map.mapmap[i][j-1]!=Map.SPIRIT0) {
									if(mol(map.mapmap[i][j],map.mapmap[i][j-1])>5) {//同化区域地图
										map.mapmap[i][j-1]=map.mapmap[i][j];
									}
								}
								if(map.mapmap[i+1][j]!=Map.SPIRIT1&&map.mapmap[i+1][j]!=Map.SPIRIT0) {
									if(mol(map.mapmap[i+1][j],map.mapmap[i+1][j])>5) {//同化区域地图
										map.mapmap[i+1][j]=map.mapmap[i][j];
									}
								}
								if(map.mapmap[i][j+1]!=Map.SPIRIT1&&map.mapmap[i][j+1]!=Map.SPIRIT0) {
									if(mol(map.mapmap[i][j+1],map.mapmap[i][j+1])>5) {//同化区域地图
										map.mapmap[i][j+1]=map.mapmap[i][j];
									}
								}
							}else {
								if(map.mapmap[i-1][j]!=Map.SPIRIT1&&map.mapmap[i-1][j]!=Map.SPIRIT0) {
									if(mol(map.mapmap[i][j],map.mapmap[i-1][j])>5) {//同化区域地图
										map.mapmap[i-1][j]=map.mapmap[i][j];
									}
								}
								if(map.mapmap[i][j-1]!=Map.SPIRIT1&&map.mapmap[i][j-1]!=Map.SPIRIT0) {
									if(mol(map.mapmap[i][j],map.mapmap[i][j-1])>5) {//同化区域地图
										map.mapmap[i][j-1]=map.mapmap[i][j];
									}
								}
								if(map.mapmap[i-1][j]!=Map.SPIRIT1&&map.mapmap[i-1][j]!=Map.SPIRIT0) {
									if(mol(map.mapmap[i+1][j],map.mapmap[i+1][j])>5) {//同化区域地图
										map.mapmap[i+1][j]=map.mapmap[i][j];
									}
								}
								if(map.mapmap[i][j+1]!=Map.SPIRIT1&&map.mapmap[i][j+1]!=Map.SPIRIT0) {
									if(mol(map.mapmap[i][j+1],map.mapmap[i][j+1])>5) {//同化区域地图
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
			for(int i=0;i<6;i++) {//随机添加精灵坦克工厂
				Pointx=random.nextInt(44)+1;
				Pointy=random.nextInt(33)+1;
				map.mapmap[Pointx][Pointy]=Map.SPIRIT1;
				map.mapmap[Pointx+1][Pointy]=Map.SPIRIT0;
				map.mapmap[Pointx][Pointy+1]=Map.SPIRIT0;
				map.mapmap[Pointx+1][Pointy+1]=Map.SPIRIT0;
			}
		}
		
		private void eightMapMake(Map map,int qmax) {				//八格随即衍生地图
			Random random=new Random();
			//将mapamp调为原始模式
			for(int i=0;i<=46;i++) {
				for(int j=0;j<=35;j++) {
					map.mapmap[i][j]=random.nextInt(50)+1;//添加随机地图块
				}
			}
			for(int q=0;q<qmax;q++) {//执行该操作的次数
				for(int i=1;i<=45;i++) {//同化区域地图
					for(int j=1;j<=34;j++) {
						if(map.mapmap[i][j]==Map.BRICK||
							map.mapmap[i][j]==Map.WATER||
							map.mapmap[i][j]==Map.STONE||
							map.mapmap[i][j]==Map.GRAESS) {
							//X型衍生
							if(map.mapmap[i-1][j-1]!=Map.SPIRIT1&&map.mapmap[i-1][j-1]!=Map.SPIRIT0) {
								if(mol(map.mapmap[i][j],map.mapmap[i-1][j-1])>25) {//同化区域地图
									map.mapmap[i-1][j-1]=map.mapmap[i][j];
								}
							}
							if(map.mapmap[i-1][j+1]!=Map.SPIRIT1&&map.mapmap[i-1][j+1]!=Map.SPIRIT0) {
								if(mol(map.mapmap[i][j],map.mapmap[i-1][j+1])>25) {//同化区域地图
									map.mapmap[i-1][j+1]=map.mapmap[i][j];
								}
							}
							if(map.mapmap[i+1][j-1]!=Map.SPIRIT1&&map.mapmap[i+1][j-1]!=Map.SPIRIT0) {
								if(mol(map.mapmap[i][j],map.mapmap[i+1][j-1])>25) {//同化区域地图
									map.mapmap[i+1][j-1]=map.mapmap[i][j];
								}
							}
							if(map.mapmap[i+1][j+1]!=Map.SPIRIT1&&map.mapmap[i+1][j+1]!=Map.SPIRIT0) {
								if(mol(map.mapmap[i][j],map.mapmap[i+1][j+1])>25) {//同化区域地图
									map.mapmap[i+1][j+1]=map.mapmap[i][j];
								}
							}
							//正十字型衍生
							if(map.mapmap[i-1][j]!=Map.SPIRIT1&&map.mapmap[i-1][j]!=Map.SPIRIT0) {
								if(mol(map.mapmap[i][j],map.mapmap[i-1][j])>25) {//同化区域地图
									map.mapmap[i-1][j]=map.mapmap[i][j];
								}
							}
							if(map.mapmap[i-1][j]!=Map.SPIRIT1&&map.mapmap[i-1][j]!=Map.SPIRIT0) {
								if(mol(map.mapmap[i][j],map.mapmap[i][j-1])>25) {//同化区域地图
									map.mapmap[i][j-1]=map.mapmap[i][j];
								}
							}
							if(map.mapmap[i+1][j]!=Map.SPIRIT1&&map.mapmap[i+1][j]!=Map.SPIRIT0) {
								if(mol(map.mapmap[i+1][j],map.mapmap[i+1][j])>25) {//同化区域地图
									map.mapmap[i+1][j]=map.mapmap[i][j];
								}
							}
							if(map.mapmap[i][j+1]!=Map.SPIRIT1&&map.mapmap[i][j+1]!=Map.SPIRIT0) {
								if(mol(map.mapmap[i][j+1],map.mapmap[i][j+1])>25) {//同化区域地图
									map.mapmap[i][j+1]=map.mapmap[i][j];
								}
							}
						}
					}
				}
			}
			int Pointx;
			int Pointy;
			for(int i=0;i<6;i++) {//随机添加精灵坦克工厂
				Pointx=random.nextInt(44)+1;
				Pointy=random.nextInt(33)+1;
				map.mapmap[Pointx][Pointy]=Map.SPIRIT1;
				map.mapmap[Pointx+1][Pointy]=Map.SPIRIT0;
				map.mapmap[Pointx][Pointy+1]=Map.SPIRIT0;
				map.mapmap[Pointx+1][Pointy+1]=Map.SPIRIT0;
			}
		}
		
		private void zoreMapMake(Map map) {							//原始地图衍生
			Random random=new Random();
			int Pointx;
			int Pointy;
			for(int i=0;i<6;i++) {//随机添加精灵坦克工厂
				Pointx=random.nextInt(44)+1;
				Pointy=random.nextInt(33)+1;
				map.mapmap[Pointx][Pointy]=Map.SPIRIT1;
				map.mapmap[Pointx+1][Pointy]=Map.SPIRIT0;
				map.mapmap[Pointx][Pointy+1]=Map.SPIRIT0;
				map.mapmap[Pointx+1][Pointy+1]=Map.SPIRIT0;
			}
		}
		
		private void ChangeBlockMake(Map map) {						//地图渐进变换函数
			int c1=1;
			int c2=2;
			int c3=3;
			//处理土区域：
			if(map.getMapX()<=8&&map.getMapY()<=8) {
				mapRandom_x2(map,c1);												//控制渐变
				map.spirittankCount[0]=10;											//精灵坦克数量
				map.spirittankCount[1]=4;											//精灵坦克数量
				map.spirittankCount[2]=1;											//精灵坦克数量
				for(int i=0;i<=46;i++) {											//土区域-第一层
					for(int j=0;j<=35;j++) {
						if(map.mapmap[i][j]==Map.WATER) {
							map.mapmap[i][j]=Map.KONG;
						}
					}
				}
				if(map.getMapX()<=5&&map.getMapY()<=5) {
					mapRandom_x2(map,c2);											//控制渐变
					map.spirittankCount[2]=0;										//精灵坦克数量
					for(int i=0;i<=46;i++) {										//土区域-第二层
						for(int j=0;j<=35;j++) {
							if(map.mapmap[i][j]==Map.STONE) {
								map.mapmap[i][j]=Map.KONG;
							}
						}
					}
					if(map.getMapX()<=2&&map.getMapY()<=2) {
						mapRandom_x2(map,c3);										//控制渐变
						map.spirittankCount[1]=0;									//精灵坦克数量
						for(int i=0;i<=46;i++) {									//土区域-第三层
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
			//处理水区域
			if(map.getMapX()<=8&&map.getMapY()<=29&&map.getMapY()>=21) {
				mapRandom_x2(map,c1);												//控制渐变
				map.spirittankCount[0]=10;											//精灵坦克数量
				map.spirittankCount[1]=4;											//精灵坦克数量
				map.spirittankCount[2]=1;											//精灵坦克数量
				for(int i=0;i<=46;i++) {											//水区域-第一层
					for(int j=0;j<=35;j++) {
							if(map.mapmap[i][j]==Map.BRICK) {
								map.mapmap[i][j]=Map.KONG;
						}
					}
				}
				if(map.getMapX()<=5&&map.getMapY()<=29&&map.getMapY()>=24) {
					mapRandom_x2(map,c2);											//控制渐变
					map.spirittankCount[2]=0;										//精灵坦克数量
					for(int i=0;i<=46;i++) {										//水区域-第二层
						for(int j=0;j<=35;j++) {
								if(map.mapmap[i][j]==Map.GRAESS) {
									map.mapmap[i][j]=Map.KONG;
							}
						}
					}
					if(map.getMapX()<=2&&map.getMapY()<=29&&map.getMapY()>=27) {
						mapRandom_x2(map,c3);										//控制渐变
						map.spirittankCount[1]=0;									//精灵坦克数量
						for(int i=0;i<=46;i++) {									//水区域-第三层
							for(int j=0;j<=35;j++) {
									if(map.mapmap[i][j]==Map.STONE) {
										map.mapmap[i][j]=Map.WATER;
								}
							}
						}
					}
				}
			}
			//处理石区域
			if(map.getMapX()>=21&&map.getMapX()<=29&&map.getMapY()<=29&&map.getMapY()>=21) {
				mapRandom_x2(map,c1);												//控制渐变
				map.spirittankCount[0]=10;											//精灵坦克数量
				map.spirittankCount[1]=4;											//精灵坦克数量
				map.spirittankCount[2]=1;											//精灵坦克数量
				for(int i=0;i<=46;i++) {											//石区域-第一层
					for(int j=0;j<=35;j++) {
							if(map.mapmap[i][j]==Map.BRICK) {
								map.mapmap[i][j]=Map.KONG;
						}
					}
				}
				if(map.getMapX()>=24&&map.getMapX()<=29&&map.getMapY()<=29&&map.getMapY()>=24) {
					mapRandom_x2(map,c2);											//控制渐变
					map.spirittankCount[2]=0;										//精灵坦克数量
					for(int i=0;i<=46;i++) {										//石区域-第二层
						for(int j=0;j<=35;j++) {
								if(map.mapmap[i][j]==Map.GRAESS) {
									map.mapmap[i][j]=Map.KONG;
							}
						}
					}
					if(map.getMapX()>=27&&map.getMapX()<=29&&map.getMapY()<=29&&map.getMapY()>=27) {
						mapRandom_x2(map,c3);										//控制渐变
						map.spirittankCount[1]=0;									//精灵坦克数量
						for(int i=0;i<=46;i++) {									//石区域-第三层
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
			//处理森林区域
			if(map.getMapX()>=21&&map.getMapX()<=29&&map.getMapY()<=8) {
				mapRandom_x2(map,c1);												//控制渐变
				map.spirittankCount[0]=10;											//精灵坦克数量
				map.spirittankCount[1]=4;											//精灵坦克数量
				map.spirittankCount[2]=1;											//精灵坦克数量
				for(int i=0;i<=46;i++) {											//森林区域-第一层
					for(int j=0;j<=35;j++) {
							if(map.mapmap[i][j]==Map.BRICK) {
								map.mapmap[i][j]=Map.KONG;
						}
					}
				}
				if(map.getMapX()>=24&&map.getMapX()<=29&&map.getMapY()<=5) {
					mapRandom_x2(map,c2);											//控制渐变
					map.spirittankCount[2]=0;										//精灵坦克数量
					for(int i=0;i<=46;i++) {										//森林区域-第二层
						for(int j=0;j<=35;j++) {
								if(map.mapmap[i][j]==Map.STONE) {
									map.mapmap[i][j]=Map.GRAESS;
							}
						}
					}
					if(map.getMapX()>=27&&map.getMapX()<=29&&map.getMapY()<=2) {
						mapRandom_x2(map,c3);										//控制渐变
						map.spirittankCount[1]=0;									//精灵坦克数量
						for(int i=0;i<=46;i++) {									//森林区域-第三层
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
		
		private void mapfill(Map map) {	//数字地图实例化函数
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
		
	  //玩家坦克爆炸函数1p
	  		private void playertank_1pBOOM() {
	  			World.get(worldNumber).playertankLife[0]--;//爆炸后的胜负判定
	  			
	  			if(World.get(worldNumber).playertankLife[0]<=0) {			//若永久死亡 -1
	  				playerTankState_1p=-1;									//设置状态死亡（期间不参与所有判断）
	  			}else if(World.get(worldNumber).playertankLife[0]!=0) {		//可复活死亡  0
	  				playerTankState_1p=0;									//设置状态死亡（期间不参与所有判断）
	  			}
	  			if(playerTankState_1p==-1&&
	  					playerTankState_2p==-1) {
	  				//玩家输，延迟结束游戏，立刻移除监听器
	  				mainFrame.setTime(-cv0);
	  				mainFrame.removeKeyListener(this);
	  				gameState=GAMELOSE;
	  			}
	  			
	  			Cartoon cartoon=new Cartoon(Cartoon.TEXPLODE,playertank_1p.getX(),playertank_1p.getY());
	  			cartoon.addFinishListener(new Listener1_1p());
	  			cartoons.add(cartoon);
	  			
	  		}
	  				
	  				
	  		//玩家坦克爆炸函数2p
	  		private void playertank_2pBOOM() {
	  			World.get(worldNumber).playertankLife[1]--;//爆炸后的胜负判定
	  			
	  			if(World.get(worldNumber).playertankLife[1]<=0) {			//若永久死亡 -1
	  				playerTankState_2p=-1;									//设置状态死亡（期间不参与所有判断）
	  			}else if(World.get(worldNumber).playertankLife[1]!=0) {		//可复活死亡  0
	  				playerTankState_2p=0;									//设置状态死亡（期间不参与所有判断）
	  			}
	  			
	  			if(playerTankState_1p==-1&&
	  					playerTankState_2p==-1) {
	  				//玩家输，延迟结束游戏，立刻移除监听器
	  				mainFrame.setTime(-cv0);
	  				mainFrame.removeKeyListener(this);
	  				gameState=GAMELOSE;
	  			}
	  			
	  			Cartoon cartoon=new Cartoon(Cartoon.TEXPLODE,playertank_2p.getX(),playertank_2p.getY());
	  			cartoon.addFinishListener(new Listener1_2p());
	  			cartoons.add(cartoon);
	  			
	  		}
	  			
	  				
	  	//精灵坦克000爆炸函数
	  		private void spirittank000BOOM(int i) {
	  			//玩家赢，延迟结束游戏，不立刻移除监听器
	  			World.get(worldNumber).spirittankDestroyed++;//被击毁数+1
	  			//爆炸后的胜负判定
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
	  		//精灵坦克001爆炸函数
	  		private void spirittank001BOOM(int i) {
	  			//玩家赢，延迟结束游戏，不立刻移除监听器
	  			World.get(worldNumber).spirittankDestroyed++;//被击毁数+1
	  			//爆炸后的胜负判定
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
	  				
	  		//玩家炮弹爆炸函数
	  		private void playerbulletBOOM(int i) {
	  			Bullet playerbullet = World.get(worldNumber).playerbullets.get(i);
	  			cartoons.add(new Cartoon(Cartoon.BEXPLODE,playerbullet.getX(),playerbullet.getY()));
	  			World.get(worldNumber).playerbullets.remove(i);
	  		}
	  				
	  		//精灵坦克炮弹爆炸函数
	  		private void spiritbulletBOOM(int i) {
	  			Bullet spiritbullet = World.get(worldNumber).spiritbullets.get(i);
	  			cartoons.add(new Cartoon(Cartoon.BEXPLODE,spiritbullet.getX(),spiritbullet.getY()));
	  			World.get(worldNumber).spiritbullets.remove(i);	
	  		}
	  				
	  		//玩家1p坦克爆炸后的监听器
	  		private class Listener1_1p extends FinishListener{
	  			public void doFinish() {
	  				Cartoon ct =new Cartoon(Cartoon.TCREATE,0,0);
	  				World.get(worldNumber).initPCartoonData(ct,playertank_1p.getX(),playertank_1p.getY());
	  				//若1p没有生命，则不能重生
	  				if(World.get(worldNumber).playertankLife[0]!=0) {
	  					ct.addFinishListener(new Listener2_1p());
	  					cartoons.add(ct);	
	  				}	
	  			}
	  		}
	  				
	  		//玩家1p坦克爆炸后的监听器
	  		private class Listener1_2p extends FinishListener{
	  			public void doFinish() {
	  				Cartoon ct =new Cartoon(Cartoon.TCREATE,0,0);
	  				World.get(worldNumber).initPCartoonData(ct,playertank_2p.getX(),playertank_2p.getY());
	  				//若2p没有生命，则不能重生
	  				if(World.get(worldNumber).playertankLife[1]!=0) {
	  					ct.addFinishListener(new Listener2_2p());
	  					cartoons.add(ct);	
	  				}	
	  			}
	  		}			
	  		
	  		//玩家1p坦克重生后的监听器
	  		private class Listener2_1p extends FinishListener{
	  			public void doFinish() {
	  				playerTankState_1p=1;									//设置坦克存活状态
	  			}
	  		}
	  				
	  		//玩家2p坦克重生后的监听器
	  		private class Listener2_2p extends FinishListener{
	  			public void doFinish() {
	  				playerTankState_2p=1;									//设置坦克存活状态
	  			}
	  		}
	  				
	  		public void keyPressed(KeyEvent e) {
	  			int key =e.getKeyCode();
	  			
	  			if(key==KeyEvent.VK_J) {
	  				//正常模式
	  				if(playerTankState_1p==1) {//只有存活状态下时才能开火
	  					for(int j=0;j<playertank_1p.firepower;j++) {
	  						Bullet playerbullet=playertank_1p.fire();
	  						World.get(worldNumber).playerbullets.add(playerbullet);
	  					}
	  				}
	  			}
	  			
	  			if(key==KeyEvent.VK_NUMPAD1){//小键盘数字1
	  				//正常模式
	  				if(playerTankState_2p==1) {//只有存活状态下时才能开火
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
	  			
	  			//传入移动操作指令
	  			playertank_1p.keyPressed(e);
	  			playertank_2p.keyPressed(e);
	  			//时间流逝速度赋值
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
	
	
			private void changeBJ() {										//该方法用于边界的判定与计算
				if(bj_1p==1) {//p1更改地图，并转换坦克坐标
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
				
				if(bj_2p==1) {//p2不更改地图，仅转换坦克坐标
				 	playertank_2p.setY(MainFrame.HEIGHT-playertank_2p.getY());
				 }else if(bj_2p==2) {
				 	playertank_2p.setX(MainFrame.WIDTH-playertank_2p.getX());
				 }else if(bj_2p==3) {
				 	playertank_2p.setY(MainFrame.HEIGHT-playertank_2p.getY());
				 }else {
				 	playertank_2p.setX(MainFrame.WIDTH-playertank_2p.getX());
				 }
				 this.mapX=World.get(worldNumber).getMapX();		//计算mapX
				 this.mapY=World.get(worldNumber).getMapY();		//计算mapY
	  		}
			
			private void drawALL() {
	  			//清除现有屏幕
				Color c=goffscreen.getColor();
				goffscreen.setColor(Color.BLACK);
				goffscreen.fillRect(0, 0, MainFrame.WIDTH, MainFrame.HEIGHT);
				goffscreen.setColor(c);
				
				//处理动画
				for(int i=cartoons.size()-1;i>=0;i--) {
					Cartoon cartoon=cartoons.get(i);
					if(!cartoon.draw(goffscreen)) {
						cartoons.remove(i);
					}
				}
				
				//绘制玩家坦克1p
				if(this.timeSpeed==1) {
			 		playertank_1p.draw(goffscreen);
			 	}else {
			 		playertank_1p.draw_0(goffscreen);
			 	}
				
				//绘制玩家坦克2p
				if(this.timeSpeed==1) {
			 		playertank_2p.draw(goffscreen);
			 	}else {
			 		playertank_2p.draw_0(goffscreen);
			 	}
				
				//绘制精灵坦克000
				for(int i=World.get(worldNumber).spirittank000s.size()-1;i>=0;i--) {
					SpiritTank stank00=World.get(worldNumber).spirittank000s.get(i);
					if(this.timeSpeed==1) {
						stank00.drawSpiritTank(goffscreen);
				 	}else {
				 		stank00.drawSpiritTank_0(goffscreen);
				 	}
				}
				
				//绘制精灵坦克001
				for(int i=World.get(worldNumber).spirittank001s.size()-1;i>=0;i--) {
					SpiritTank001 stank01=World.get(worldNumber).spirittank001s.get(i);
					if(this.timeSpeed==1) {
						stank01.drawSpiritTank(goffscreen);
				 	}else {
				 		stank01.drawSpiritTank_0(goffscreen);
				 	}
				}
				
				//绘制地图
				if(this.timeSpeed==1) {
					World.get(worldNumber).drawBricks(goffscreen);
					World.get(worldNumber).drawStones(goffscreen);
					World.get(worldNumber).drawWaters(goffscreen);
			 	}else {
			 		World.get(worldNumber).drawBricks_0(goffscreen);
					World.get(worldNumber).drawStones_0(goffscreen);
					World.get(worldNumber).drawWaters_0(goffscreen);
			 	}
				
				//绘制玩家坦克炮弹
				for(int i=World.get(worldNumber).playerbullets.size()-1;i>=0;i--) {
					Bullet playerbullet=World.get(worldNumber).playerbullets.get(i);
					playerbullet.draw(goffscreen);
				}
				
				//绘制精灵坦克炮弹
				for(int i=World.get(worldNumber).spiritbullets.size()-1;i>=0;i--) {
					Bullet spiritbullet=World.get(worldNumber).spiritbullets.get(i);
					spiritbullet.draw(goffscreen);
				}
				
				//绘制特殊地图
				if(this.timeSpeed==1) {
					//画入草块
					World.get(worldNumber).drawGrasses(goffscreen);
					//画入精灵块
					World.get(worldNumber).drawSpirits(goffscreen);
				}else {
					//画入草块
					World.get(worldNumber).drawGrasses_0(goffscreen);
					//画入精灵块
					World.get(worldNumber).drawSpirits_0(goffscreen);
				}
	  		}
	
	
	
	
	
	
	//class end//
}
