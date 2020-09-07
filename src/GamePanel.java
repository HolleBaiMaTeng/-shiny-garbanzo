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
	 * 要处理的问题：
	 * 1.精灵坦克添加种类，权值判定
	 * 2.002炮弹抵消
	 * 3.
	 * 
	 * 
	 * 胜利目标，到达中心地图并消灭对应敌人
	 * 
	 * 
	 */
	
	private MainFrame mainFrame;
	private LoginPanel loginPanel;
	
	private Image offscreenimage=null;				//第二缓存
	private Graphics goffscreen=null;
	
	private int timeSpeed=1;       					//管理游戏时间流逝速度：
	
    //预制地图由文件的形式读取，其他地图用随机算法生成
	private ArrayList<String> mapName=new ArrayList<String>();						//地图对应的文件名
	private int[][]  mapName_int;													//储存地图名中的数字
	
	private int mapchange=0;					 									//0-地图未变动1-地图变动（变动期间使用演示状态）
	private int maptime_jianbian=0;													//切换地图时的渐变设置  0-常态下值 2n-1全态下值
	private int mapblock[][];														//地图渐变设置
	private static final int JIANBIAN=25;											//渐变次数
	private int bj=0;																//边界碰撞方向记录
	
	private ArrayList<Map> World=new ArrayList<Map>();								//世界
	private static final int WORLDWIDTH=30;											//世界宽度
	private static final int WORLDHEIGHT=30;										//世界高度
	 
	//智能化设置
	public static final int spirittank_miss=8;
	public static final int spirittank_see=1000;
	public static final int spirittank_distance=0;
	public int spirittank_distance002=4;
	
	//随地图变换的关卡变量
	private int mapX=0;							    //map的世界横坐标
	private int mapY=0;								//map的世界纵坐标
	private int worldNumber=0;						//世界编号
	
	//playertank创建后一直存在
	private PlayerTank playertank;									//玩家坦克起始坐标（350，400）
	private int playerTankState=1;									//玩家坦克状态，1-正常，0-死亡(可复活),-1-死亡(不可复活)
	private ArrayList<Cartoon> cartoons=new ArrayList<Cartoon>();	//动画播放列表

	//主界面切换控制
	private static final int GAMEWIN=1;
	private static final int GAMELOSE=2;
	private static final int cv0=30;                       //游戏结束后缓冲时间
	private int gameState=0;							   //游戏状态判定0-正常1-胜利2-失败
	
	
	
		//构造函数
		public GamePanel(MainFrame mainFrame,LoginPanel loginPanel)  {//现场调用World.get(worldNumber)
			super();
			this.mapblock=new int[47][36];					//初始化地图渐变状态数组
			this.mainFrame=mainFrame;
			this.loginPanel=loginPanel;
			//玩家坦克初始化
			playertank=new PlayerTank(400,350,loginPanel.player_1p_tankmodel);
			
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
				StringTokenizer st = new StringTokenizer(str1,".");//用.分割
				mapName_int[i][0]=Integer.parseInt(st.nextToken());	//分别赋值
				mapName_int[i][1]=Integer.parseInt(st.nextToken());	//分别赋值
			}
			
			World.clear(); 							//世界清空
			initWorld();   							//世界初始化(建造World并读入与生成所有地图数据)
													//同时计算初始点坐标，mapx,mapy,worldNumber
			
			
		}
		
		
		//绘图paint函数
		public void paint(Graphics g) {
			
			/**共用检测部分**/
			//显示数据(下方仪表盘)
			g.drawLine(0, 600, 800, 600);
			g.setFont(new Font("TimesRoman",Font.PLAIN,20));
			String system_string=new String("tank_x : "+(playertank.getX()+800*this.mapX)+"  "+
											"tank_y : "+(600-playertank.getY()+600*this.mapY)+"        "+
											"world_x : "+this.mapX+"  "+
											"world_y : "+this.mapY);
			g.drawString(system_string, 10, 620);
			g.drawString("playerlife : "+World.get(worldNumber).playertankLife[0], 10, 645);
			g.drawString("|||destination-(X:15 , Y:15)|||", 540, 645);
			
			//显示数据(右侧仪表盘)
			g.drawLine(800, 0, 800, 690);
			
			//屏幕处理技术
			//使用第二缓存技术
			if(offscreenimage==null) {
				offscreenimage=this.createImage(MainFrame.WIDTH, MainFrame.HEIGHT);
				goffscreen=offscreenimage.getGraphics();
				}
			//调用父类方法，防止从底层重绘
			super.paint(goffscreen);
			
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
				playerTankState=-1;	
			}
			/**共用检测部分**/
			
			
			if(mapchange==0) {										//若此时为非地图变更状态
				//地图运算处理
				this.DO_map001();
				this.DO_map002();
				this.DO_map003();
				this.DO_map004();
				this.DO_map005();
				this.DO_map006();
				this.DO_map007();
				this.DO_map008();
				this.DO_map009();
				
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
				//随机生成精灵坦克002(由map中数据决定是否生成)
				SpiritTank002 stank002=World.get(worldNumber).CreateSpirittank002();
				if(stank002!=null) {
					World.get(worldNumber).spirittank002s.add(stank002);
				}
				
				//处理玩家坦克
				if(playerTankState==1) {							//玩家坦克存活时判断
					int goplayertank_1=1;
					int goplayertank_2=1;
					int goplayertank_3=1;
					//是否碰撞边框
					bj=playertank.isCollide_bianjie();				//边界赋值
				 	if(bj!=0) {//碰撞了边界（world地图四周不可走前提）//直接改变地图
				 		this.mapchange=1;							//地图变更（在变更后进入演示状态）
				 		this.maptime_jianbian=JIANBIAN*2-1;			//设置渐变次数
				 	}
				
				 		//是否碰撞精灵坦克000(相碰撞后爆炸)
				 		for(int i=World.get(worldNumber).spirittank000s.size()-1;i>=0;i--) {
				 			SpiritTank spirittank=World.get(worldNumber).spirittank000s.get(i);
				 			if(spirittank.tankCollide(playertank)) {
				 				playertankBOOM();//玩家与最后一辆精灵坦克同归于尽-玩家赢
				 				spirittank000BOOM(i);
				 				break;
				 			}
				 		}
				 		//是否碰撞精灵坦克001(相碰撞后爆炸)
				 		for(int i=World.get(worldNumber).spirittank001s.size()-1;i>=0;i--) {
				 			SpiritTank001 spirittank001=World.get(worldNumber).spirittank001s.get(i);
				 			if(spirittank001.tankCollide(playertank)) {
				 				playertankBOOM();//玩家与最后一辆精灵坦克同归于尽-玩家赢
				 				spirittank001BOOM(i);
				 				break;
				 			}
				 		}
				 		//是否碰撞精灵坦克002(相碰撞后爆炸)
				 		for(int i=World.get(worldNumber).spirittank002s.size()-1;i>=0;i--) {
				 			SpiritTank002 spirittank002=World.get(worldNumber).spirittank002s.get(i);
				 			if(spirittank002.tankCollide(playertank)) {
				 				playertankBOOM();//玩家与最后一辆精灵坦克同归于尽-玩家赢
				 				spirittank002BOOM(i);
				 				break;
				 			}
				 		}
				 		//是否碰撞地图块
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
				 		playertank.caculateDate();						//计算函数负责调整帧数、
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
				//处理精灵坦克002
				for(int i=World.get(worldNumber).spirittank002s.size()-1;i>=0;i--) {
					SpiritTank002 stank01=World.get(worldNumber).spirittank002s.get(i);
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
	
					
					if(this.timeSpeed==1) {
						stank01.drawSpiritTank(goffscreen);
				 	}else {
				 		stank01.drawSpiritTank_0(goffscreen);
				 	}
					//步骤一，运算函数
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
					//步骤二，移动函数
					if(gospirittank_1*gospirittank_2*gospirittank_3==1) {
						stank01.move();
						
					}
					//步骤三，开火函数
					Bullet spiritbullet=stank01.fire();
					if(spiritbullet!=null) {
						World.get(worldNumber).spiritbullets.add(spiritbullet);
					}
				}
				
				
				//画入地图组件
				if(this.timeSpeed==1) {
					World.get(worldNumber).drawBricks(goffscreen);
					World.get(worldNumber).drawStones(goffscreen);
					World.get(worldNumber).drawWaters(goffscreen);
			 	}else {
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
					//判断是否击中精灵坦克002
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
					
					//根据需要不同，实时变换目标点
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
					if(playerTankState==1) {					//玩家坦克存活，进行该判断
						if(playertank.isCollide(spiritbullet)) {//与玩家坦克的碰撞检测
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
				
				//梯度函数还原部分(梯度还原的本质是将非1部分化为0)
				//移动梯度函数还原001
				for(int i=0;i<=46;i++) {
					for(int j=0;j<=35;j++) {
						if(World.get(worldNumber).mapmap_move_point001[i][j]!=1) {
							World.get(worldNumber).mapmap_move_point001[i][j]=0;
						}
					}
				}
				//移动梯度函数还原002
				for(int i=0;i<=46;i++) {
					for(int j=0;j<=35;j++) {
						if(World.get(worldNumber).mapmap_move_point002[i][j]!=1) {
							World.get(worldNumber).mapmap_move_point002[i][j]=0;
						}
					}
				}
				//移动梯度函数还原003
				for(int i=0;i<=46;i++) {
					for(int j=0;j<=35;j++) {
						if(World.get(worldNumber).mapmap_move_point003[i][j]!=1) {
							World.get(worldNumber).mapmap_move_point003[i][j]=0;
						}
					}
				}
				//移动梯度函数还原004
				for(int i=0;i<=46;i++) {
					for(int j=0;j<=35;j++) {
						if(World.get(worldNumber).mapmap_move_point004[i][j]!=1) {
							World.get(worldNumber).mapmap_move_point004[i][j]=0;
						}
					}
				}
				//移动梯度函数还原005
				for(int i=0;i<=46;i++) {
					for(int j=0;j<=35;j++) {
						if(World.get(worldNumber).mapmap_move_point005[i][j]!=1) {
							World.get(worldNumber).mapmap_move_point005[i][j]=0;
						}
					}
				}
				//移动梯度函数还原006
				for(int i=0;i<=46;i++) {
					for(int j=0;j<=35;j++) {
						if(World.get(worldNumber).mapmap_move_point006[i][j]!=1) {
							World.get(worldNumber).mapmap_move_point006[i][j]=0;
						}
					}
				}
				//移动梯度函数还原007
				for(int i=0;i<=46;i++) {
					for(int j=0;j<=35;j++) {
						if(World.get(worldNumber).mapmap_move_point007[i][j]!=1) {
							World.get(worldNumber).mapmap_move_point007[i][j]=0;
						}
					}
				}
				//移动梯度函数还原008
				for(int i=0;i<=46;i++) {
					for(int j=0;j<=35;j++) {
						if(World.get(worldNumber).mapmap_move_point008[i][j]!=1) {
							World.get(worldNumber).mapmap_move_point008[i][j]=0;
						}
					}
				}
				//移动梯度函数还原009
				for(int i=0;i<=46;i++) {
					for(int j=0;j<=35;j++) {
						if(World.get(worldNumber).mapmap_move_point009[i][j]!=1) {
							World.get(worldNumber).mapmap_move_point009[i][j]=0;
						}
					}
				}
				//地图非变更状态结束
			}
			
			
			
			if(mapchange==1) {//若地图变更
				this.drawALL();										//绘制地图画面
				Random random=new Random();							//创建随机变量
				if(maptime_jianbian==this.JIANBIAN*2-1) {			//若是最初始状态
																	//进行过程一
					for(int i=0;i<=46;i++) {						//进行赋值
						for(int j=0;j<=35;j++) {
							mapblock[i][j]=random.nextInt(this.JIANBIAN);
						}
					}
				}
				if(maptime_jianbian==this.JIANBIAN-1) {				//如果渐变完成过程一
																	//进行过程二
					this.changeBJ();								//更换地图
			 		for(int i=0;i<=46;i++) {						//进行赋值
						for(int j=0;j<=35;j++) {
							mapblock[i][j]=random.nextInt(this.JIANBIAN);
						}
					}
				}
				if(maptime_jianbian<=this.JIANBIAN*2-1&&maptime_jianbian>=this.JIANBIAN) {
					for(int q=0;q<2*this.JIANBIAN-maptime_jianbian;q++) {				//逐渐增加黑色方块
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
				if(maptime_jianbian<this.JIANBIAN) {
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
		
		
		
		
		
		
		
		/***************************************************************************/
		
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
	    

	    
	    
		/****************************************************************************/
	    //玩家坦克爆炸函数
	  		private void playertankBOOM() {
	  			World.get(worldNumber).playertankLife[0]--;//爆炸后的胜负判定
	  			if(World.get(worldNumber).playertankLife[0]<=0) {			//若永久死亡 -1
	  				playerTankState=-1;										//设置状态死亡（期间不参与所有判断）
	  			}else if(World.get(worldNumber).playertankLife[0]!=0) {		//可复活死亡  0
	  				playerTankState=0;										//设置状态死亡（期间不参与所有判断）
	  			}
	  			if(playerTankState==-1) {
	  				//玩家输，延迟结束游戏，立刻移除监听器
	  				mainFrame.setTime(-cv0);
	  				mainFrame.removeKeyListener(this);
	  				gameState=GAMELOSE;
	  			}
	  			
	  			Cartoon cartoon=new Cartoon(Cartoon.TEXPLODE,playertank.getX(),playertank.getY());
	  			cartoon.addFinishListener(new Listener1());
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
	  		//精灵坦克002爆炸函数
	  		private void spirittank002BOOM(int i) {
	  			//玩家赢，延迟结束游戏，不立刻移除监听器
	  			World.get(worldNumber).spirittankDestroyed++;//被击毁数+1
	  			//爆炸后的胜负判定
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
	  		
	  		//玩家坦克爆炸后的监听器
	  		private class Listener1 extends FinishListener{
	  			public void doFinish() {
	  				Cartoon ct =new Cartoon(Cartoon.TCREATE,0,0);
	  				World.get(worldNumber).initPCartoonData(ct,playertank.getX(),playertank.getY());
	  				//若没有生命，则不能重生
	  				if(World.get(worldNumber).playertankLife[0]!=0) {
	  					ct.addFinishListener(new Listener2());
	  					cartoons.add(ct);	
	  				}	
	  			}
	  		}
	  		
	  		
	  		//玩家坦克重生后的监听器
	  		private class Listener2 extends FinishListener{
	  			public void doFinish() {
	  				playerTankState=1;									//设置坦克存活状态
	  			}
	  		}

	  		
	  		
	  		
	  		public void keyPressed(KeyEvent e) {
	  			int key =e.getKeyCode();
	  			
	  			if(key==KeyEvent.VK_J) {
	  				//正常模式
	  				if(playerTankState==1) {//只有存活状态下时才能开火
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
	  			
	  			//时间变量的修改
	  			this.timeSpeed=playertank.getTimeSpeed();
	  			this.mainFrame.timeSpeed=playertank.getTimeSpeed();
	  		}

	  		@Override
			public void keyReleased(KeyEvent e) {
				playertank.keyReleased(e);
				//时间变量的修改
	  			this.timeSpeed=playertank.getTimeSpeed();
	  			this.mainFrame.timeSpeed=playertank.getTimeSpeed();
			}
	  		
	  		public void setWorldNumber(int worldNumber) {
	  			this.worldNumber=worldNumber;
	  		}
	  		
	  		public int getWorldNumber() {
	  			return this.worldNumber;
	  		}


	  		private void changeBJ() {										//该方法用于边界的判定与计算
	  			if(bj==1) {										
		 			this.setWorldNumber(this.getWorldNumber()+1);				//更换地图
		 			playertank.setY(MainFrame.HEIGHT-playertank.getY());		//更换玩家坦克位置
		 		}
		 		else if(bj==2) {
		 			this.setWorldNumber(this.getWorldNumber()+WORLDHEIGHT);		//更换地图
		 			playertank.setX(MainFrame.WIDTH-playertank.getX());			//更换玩家坦克位置
		 		}
		 		else if(bj==3) {
		 			this.setWorldNumber(this.getWorldNumber()-1);				//更换地图
		 			playertank.setY(MainFrame.HEIGHT-playertank.getY());		//更换玩家坦克位置
		 		}
		 		else {
		 			this.setWorldNumber(this.getWorldNumber()-WORLDHEIGHT);		//更换地图
		 			playertank.setX(MainFrame.WIDTH-playertank.getX());			//更换玩家坦克位置
		 		}
		 		this.mapX=World.get(worldNumber).getMapX();
		 		this.mapY=World.get(worldNumber).getMapY();
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
				
				//绘制玩家坦克
				if(this.timeSpeed==1) {
			 		playertank.draw(goffscreen);
			 	}else {
			 		playertank.draw_0(goffscreen);
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
				
				//绘制精灵坦克002
				for(int i=World.get(worldNumber).spirittank002s.size()-1;i>=0;i--) {
					SpiritTank002 stank01=World.get(worldNumber).spirittank002s.get(i);
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
	  		
			@Override
			public void keyTyped(KeyEvent arg0) {
			}
			
			
		
		private void DO_map001() {
			//实时运算玩家坦克梯度运算
			//凝结核部分
			int playertank_block_x=playertank.getX()/17;
			int playertank_block_y=playertank.getY()/17;
			int key=0;											//令牌，当未进行任何操作时，跳出循环
			//设置权值点位置
			//方法一：自爆型权值点
			/*
			if(World.get(worldNumber).mapmap_move_point001[playertank_block_x][playertank_block_y]!=1) {
				World.get(worldNumber).mapmap_move_point001[playertank_block_x][playertank_block_y]=-spirittank_see;//设置最低梯度
			}
			if(World.get(worldNumber).mapmap_move_point001[playertank_block_x+1][playertank_block_y]!=1) {
				World.get(worldNumber).mapmap_move_point001[playertank_block_x+1][playertank_block_y]=-spirittank_see;//设置最低梯度
			}
			if(World.get(worldNumber).mapmap_move_point001[playertank_block_x][playertank_block_y+1]!=1) {
				World.get(worldNumber).mapmap_move_point001[playertank_block_x][playertank_block_y+1]=-spirittank_see;//设置最低梯度
			}
			if(World.get(worldNumber).mapmap_move_point001[playertank_block_x+1][playertank_block_y+1]!=1) {
				World.get(worldNumber).mapmap_move_point001[playertank_block_x+1][playertank_block_y+1]=-spirittank_see;//设置最低梯度
			}
			*/
			//方法二：距离把控权值点
			//衍生部分1：行动权值（中心）
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
					if(playerbullet.getX()%17<=1) {							//左判定
						for(int j=0;j<spirittank_miss;j++) {				//循环执行避让距离设置
							if(bullet_y-j>=0) {								//确保判定合理性
								if(World.get(worldNumber).mapmap_move_point001[bullet_x][bullet_y-j]!=1) {		//保证不会覆盖墙体
									World.get(worldNumber).mapmap_move_point001[bullet_x][bullet_y-j]=-j-1;		//赋值
								}
							}
						}
					}else if(playerbullet.getX()%17>=16) {
						for(int j=0;j<spirittank_miss;j++) {				//循环执行避让距离设置
							if(bullet_y-j>=0&&bullet_x+1<48) {				//确保判定合理性
								if(World.get(worldNumber).mapmap_move_point001[bullet_x+1][bullet_y-j]!=1) {	//保证不会覆盖墙体
									World.get(worldNumber).mapmap_move_point001[bullet_x+1][bullet_y-j]=-j-1;		//赋值
								}
							}
						}
					}else {													//右判定				
						for(int j=0;j<spirittank_miss;j++) {				//循环执行避让距离设置
							if(bullet_y-j>=0&&bullet_x+1<48) {				//确保判定合理性
								if(World.get(worldNumber).mapmap_move_point001[bullet_x+1][bullet_y-j]!=1) {	//保证不会覆盖墙体
									World.get(worldNumber).mapmap_move_point001[bullet_x+1][bullet_y-j]=-j-1;     //赋值
								}
							}
						}
						for(int j=0;j<spirittank_miss;j++) {				//循环执行避让距离设置
							if(bullet_y-j>=0) {								//确保判定合理性
								if(World.get(worldNumber).mapmap_move_point001[bullet_x][bullet_y-j]!=1) {		//保证不会覆盖墙体
									World.get(worldNumber).mapmap_move_point001[bullet_x][bullet_y-j]=-j-1;		//赋值
								}
							}
						}
					}
				}else if(playerbullet.getDiraction()==Spirit.RIGHT) {		/***方向为右***/
					if(playerbullet.getY()%17<=1) {							//上判定
						for(int j=0;j<spirittank_miss;j++) {				//循环执行避让距离设置
							if(bullet_x+j<48) {								//确保判定合理性
								if(World.get(worldNumber).mapmap_move_point001[bullet_x+j][bullet_y]!=1) {		//保证不会覆盖墙体
									World.get(worldNumber).mapmap_move_point001[bullet_x+j][bullet_y]=-j-1;     	//赋值
								}
							}
						}
					}else if(playerbullet.getY()%17>=16) {
						for(int j=0;j<spirittank_miss;j++) {				//循环执行避让距离设置
							if(bullet_x+j<48&&bullet_y+1<37) {				//确保判定合理性
								if(World.get(worldNumber).mapmap_move_point001[bullet_x+j][bullet_y+1]!=1) {		//保证不会覆盖墙体
									World.get(worldNumber).mapmap_move_point001[bullet_x+j][bullet_y+1]=-j-1;     	//赋值
								}
							}
						}
					}else {													//下判定
						for(int j=0;j<spirittank_miss;j++) {				//循环执行避让距离设置
							if(bullet_x+j<48&&bullet_y+1<37) {				//确保判定合理性
								if(World.get(worldNumber).mapmap_move_point001[bullet_x+j][bullet_y+1]!=1) {		//保证不会覆盖墙体
									World.get(worldNumber).mapmap_move_point001[bullet_x+j][bullet_y+1]=-j-1;     	//赋值
								}
							}
						}
						for(int j=0;j<spirittank_miss;j++) {				//循环执行避让距离设置
							if(bullet_x+j<48) {								//确保判定合理性
								if(World.get(worldNumber).mapmap_move_point001[bullet_x+j][bullet_y]!=1) {			//保证不会覆盖墙体
									World.get(worldNumber).mapmap_move_point001[bullet_x+j][bullet_y]=-j-1;     		//赋值
								}
							}
						}
					}
				}else if(playerbullet.getDiraction()==Spirit.DOWN) {		/***方向为下***/
					if(playerbullet.getX()%17<=1) {							//左判定
						for(int j=0;j<spirittank_miss;j++) {				//循环执行避让距离设置
							if(bullet_y+j<37) {								//确保判定合理性
								if(World.get(worldNumber).mapmap_move_point001[bullet_x][bullet_y+j]!=1) {		//保证不会覆盖墙体
									World.get(worldNumber).mapmap_move_point001[bullet_x][bullet_y+j]=-j-1;		//赋值
								}
							}
						}
					}else if(playerbullet.getX()%17>=16) {
						for(int j=0;j<spirittank_miss;j++) {				//循环执行避让距离设置
							if(bullet_y+j<37&&bullet_x+1<48) {				//确保判定合理性
								if(World.get(worldNumber).mapmap_move_point001[bullet_x+1][bullet_y+j]!=1) {	//保证不会覆盖墙体
									World.get(worldNumber).mapmap_move_point001[bullet_x+1][bullet_y+j]=-j-1;		//赋值
								}
							}
						}
					}else {													//右判定				
						for(int j=0;j<spirittank_miss;j++) {				//循环执行避让距离设置
							if(bullet_y+j<37&&bullet_x+1<48) {				//确保判定合理性
								if(World.get(worldNumber).mapmap_move_point001[bullet_x+1][bullet_y+j]!=1) {	//保证不会覆盖墙体
									World.get(worldNumber).mapmap_move_point001[bullet_x+1][bullet_y+j]=-j-1;     //赋值
								}
							}
						}
						for(int j=0;j<spirittank_miss;j++) {				//循环执行避让距离设置
							if(bullet_y+j<37) {								//确保判定合理性
								if(World.get(worldNumber).mapmap_move_point001[bullet_x][bullet_y+j]!=1) {		//保证不会覆盖墙体
									World.get(worldNumber).mapmap_move_point001[bullet_x][bullet_y+j]=-j-1;		//赋值
								}
							}
						}
					}
				}else if(playerbullet.getDiraction()==Spirit.LEFT) {		/***方向为左***/
					if(playerbullet.getY()%17<=1) {							//上判定
						for(int j=0;j<spirittank_miss;j++) {				//循环执行避让距离设置
							if(bullet_x-j>=0) {								//确保判定合理性
								if(World.get(worldNumber).mapmap_move_point001[bullet_x-j][bullet_y]!=1) {		//保证不会覆盖墙体
									World.get(worldNumber).mapmap_move_point001[bullet_x-j][bullet_y]=-j-1;     	//赋值
								}
							}
						}
					}else if(playerbullet.getY()%17>=16) {
						for(int j=0;j<spirittank_miss;j++) {				//循环执行避让距离设置
							if(bullet_x-j>=0&&bullet_y+1<37) {				//确保判定合理性
								if(World.get(worldNumber).mapmap_move_point001[bullet_x-j][bullet_y+1]!=1) {		//保证不会覆盖墙体
									World.get(worldNumber).mapmap_move_point001[bullet_x-j][bullet_y+1]=-j-1;     	//赋值
								}
							}
						}
					}else {													//下判定
						for(int j=0;j<spirittank_miss;j++) {				//循环执行避让距离设置
							if(bullet_x-j>=0&&bullet_y+1<37) {				//确保判定合理性
								if(World.get(worldNumber).mapmap_move_point001[bullet_x-j][bullet_y+1]!=1) {		//保证不会覆盖墙体
									World.get(worldNumber).mapmap_move_point001[bullet_x-j][bullet_y+1]=-j-1;     	//赋值
								}
							}
						}
						for(int j=0;j<spirittank_miss;j++) {				//循环执行避让距离设置
							if(bullet_x-j>=0) {								//确保判定合理性
								if(World.get(worldNumber).mapmap_move_point001[bullet_x-j][bullet_y]!=1) {			//保证不会覆盖墙体
									World.get(worldNumber).mapmap_move_point001[bullet_x-j][bullet_y]=-j-1;     		//赋值
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

		}

		private void DO_map002() {
			this.spirittank_distance002=World.get(worldNumber).move_distance;		//定制行走半径
			//实时运算玩家坦克梯度运算
			//凝结核部分
			int playertank_block_x=playertank.getX()/17;
			int playertank_block_y=playertank.getY()/17;
			int key=0;											//令牌，当未进行任何操作时，跳出循环
			//设置权值点位置
			//方法一：自爆型权值点
			/*
			if(World.get(worldNumber).mapmap_move_point001[playertank_block_x][playertank_block_y]!=1) {
				World.get(worldNumber).mapmap_move_point001[playertank_block_x][playertank_block_y]=-spirittank_see;//设置最低梯度
			}
			if(World.get(worldNumber).mapmap_move_point001[playertank_block_x+1][playertank_block_y]!=1) {
				World.get(worldNumber).mapmap_move_point001[playertank_block_x+1][playertank_block_y]=-spirittank_see;//设置最低梯度
			}
			if(World.get(worldNumber).mapmap_move_point001[playertank_block_x][playertank_block_y+1]!=1) {
				World.get(worldNumber).mapmap_move_point001[playertank_block_x][playertank_block_y+1]=-spirittank_see;//设置最低梯度
			}
			if(World.get(worldNumber).mapmap_move_point001[playertank_block_x+1][playertank_block_y+1]!=1) {
				World.get(worldNumber).mapmap_move_point001[playertank_block_x+1][playertank_block_y+1]=-spirittank_see;//设置最低梯度
			}
			*/
			//方法二：距离把控权值点
			//衍生部分1：行动权值
			//权值点集中在上方（左上）
			if(playertank_block_x-spirittank_distance002>=0&&playertank_block_y-spirittank_distance002>=0) {
				if(World.get(worldNumber).mapmap_move_point002[playertank_block_x-spirittank_distance002][playertank_block_y-spirittank_distance002]!=1) {
					World.get(worldNumber).mapmap_move_point002[playertank_block_x-spirittank_distance002][playertank_block_y-spirittank_distance002]=-spirittank_see;
				}
			}
			
			
			//权值部分2：避让权值(不计入衍生 )
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
			
			//权值部分3
			//非衍生部分
			for(int i=0;i<World.get(worldNumber).playerbullets.size();i++) {//避让设置
				Bullet playerbullet=World.get(worldNumber).playerbullets.get(i);
				int bullet_x=playerbullet.getX()/17;
				int bullet_y=playerbullet.getY()/17;
				if(playerbullet.getDiraction()==Spirit.UP) {				/***方向为上***/
					if(playerbullet.getX()%17<=1) {							//左判定
						for(int j=0;j<spirittank_miss;j++) {				//循环执行避让距离设置
							if(bullet_y-j>=0) {								//确保判定合理性
								if(World.get(worldNumber).mapmap_move_point002[bullet_x][bullet_y-j]!=1) {		//保证不会覆盖墙体
									World.get(worldNumber).mapmap_move_point002[bullet_x][bullet_y-j]=spirittank_miss-j+1;		//赋值
								}
							}
						}
					}else if(playerbullet.getX()%17>=16) {
						for(int j=0;j<spirittank_miss;j++) {				//循环执行避让距离设置
							if(bullet_y-j>=0&&bullet_x+1<48) {				//确保判定合理性
								if(World.get(worldNumber).mapmap_move_point002[bullet_x+1][bullet_y-j]!=1) {	//保证不会覆盖墙体
									World.get(worldNumber).mapmap_move_point002[bullet_x+1][bullet_y-j]=spirittank_miss-j+1;		//赋值
								}
							}
						}
					}else {													//右判定				
						for(int j=0;j<spirittank_miss;j++) {				//循环执行避让距离设置
							if(bullet_y-j>=0&&bullet_x+1<48) {				//确保判定合理性
								if(World.get(worldNumber).mapmap_move_point002[bullet_x+1][bullet_y-j]!=1) {	//保证不会覆盖墙体
									World.get(worldNumber).mapmap_move_point002[bullet_x+1][bullet_y-j]=spirittank_miss-j+1;     //赋值
								}
							}
						}
						for(int j=0;j<spirittank_miss;j++) {				//循环执行避让距离设置
							if(bullet_y-j>=0) {								//确保判定合理性
								if(World.get(worldNumber).mapmap_move_point002[bullet_x][bullet_y-j]!=1) {		//保证不会覆盖墙体
									World.get(worldNumber).mapmap_move_point002[bullet_x][bullet_y-j]=spirittank_miss-j+1;		//赋值
								}
							}
						}
					}
				}else if(playerbullet.getDiraction()==Spirit.RIGHT) {		/***方向为右***/
					if(playerbullet.getY()%17<=1) {							//上判定
						for(int j=0;j<spirittank_miss;j++) {				//循环执行避让距离设置
							if(bullet_x+j<48) {								//确保判定合理性
								if(World.get(worldNumber).mapmap_move_point002[bullet_x+j][bullet_y]!=1) {		//保证不会覆盖墙体
									World.get(worldNumber).mapmap_move_point002[bullet_x+j][bullet_y]=spirittank_miss-j+1;     	//赋值
								}
							}
						}
					}else if(playerbullet.getY()%17>=16) {
						for(int j=0;j<spirittank_miss;j++) {				//循环执行避让距离设置
							if(bullet_x+j<48&&bullet_y+1<37) {				//确保判定合理性
								if(World.get(worldNumber).mapmap_move_point002[bullet_x+j][bullet_y+1]!=1) {		//保证不会覆盖墙体
									World.get(worldNumber).mapmap_move_point002[bullet_x+j][bullet_y+1]=spirittank_miss-j+1;     	//赋值
								}
							}
						}
					}else {													//下判定
						for(int j=0;j<spirittank_miss;j++) {				//循环执行避让距离设置
							if(bullet_x+j<48&&bullet_y+1<37) {				//确保判定合理性
								if(World.get(worldNumber).mapmap_move_point002[bullet_x+j][bullet_y+1]!=1) {		//保证不会覆盖墙体
									World.get(worldNumber).mapmap_move_point002[bullet_x+j][bullet_y+1]=spirittank_miss-j+1;     	//赋值
								}
							}
						}
						for(int j=0;j<spirittank_miss;j++) {				//循环执行避让距离设置
							if(bullet_x+j<48) {								//确保判定合理性
								if(World.get(worldNumber).mapmap_move_point002[bullet_x+j][bullet_y]!=1) {			//保证不会覆盖墙体
									World.get(worldNumber).mapmap_move_point002[bullet_x+j][bullet_y]=spirittank_miss-j+1;     		//赋值
								}
							}
						}
					}
				}else if(playerbullet.getDiraction()==Spirit.DOWN) {		/***方向为下***/
					if(playerbullet.getX()%17<=1) {							//左判定
						for(int j=0;j<spirittank_miss;j++) {				//循环执行避让距离设置
							if(bullet_y+j<37) {								//确保判定合理性
								if(World.get(worldNumber).mapmap_move_point002[bullet_x][bullet_y+j]!=1) {		//保证不会覆盖墙体
									World.get(worldNumber).mapmap_move_point002[bullet_x][bullet_y+j]=spirittank_miss-j+1;		//赋值
								}
							}
						}
					}else if(playerbullet.getX()%17>=16) {
						for(int j=0;j<spirittank_miss;j++) {				//循环执行避让距离设置
							if(bullet_y+j<37&&bullet_x+1<48) {				//确保判定合理性
								if(World.get(worldNumber).mapmap_move_point002[bullet_x+1][bullet_y+j]!=1) {	//保证不会覆盖墙体
									World.get(worldNumber).mapmap_move_point002[bullet_x+1][bullet_y+j]=spirittank_miss-j+1;		//赋值
								}
							}
						}
					}else {													//右判定				
						for(int j=0;j<spirittank_miss;j++) {				//循环执行避让距离设置
							if(bullet_y+j<37&&bullet_x+1<48) {				//确保判定合理性
								if(World.get(worldNumber).mapmap_move_point002[bullet_x+1][bullet_y+j]!=1) {	//保证不会覆盖墙体
									World.get(worldNumber).mapmap_move_point002[bullet_x+1][bullet_y+j]=spirittank_miss-j+1;     //赋值
								}
							}
						}
						for(int j=0;j<spirittank_miss;j++) {				//循环执行避让距离设置
							if(bullet_y+j<37) {								//确保判定合理性
								if(World.get(worldNumber).mapmap_move_point002[bullet_x][bullet_y+j]!=1) {		//保证不会覆盖墙体
									World.get(worldNumber).mapmap_move_point002[bullet_x][bullet_y+j]=spirittank_miss-j+1;		//赋值
								}
							}
						}
					}
				}else if(playerbullet.getDiraction()==Spirit.LEFT) {		/***方向为左***/
					if(playerbullet.getY()%17<=1) {							//上判定
						for(int j=0;j<spirittank_miss;j++) {				//循环执行避让距离设置
							if(bullet_x-j>=0) {								//确保判定合理性
								if(World.get(worldNumber).mapmap_move_point002[bullet_x-j][bullet_y]!=1) {		//保证不会覆盖墙体
									World.get(worldNumber).mapmap_move_point002[bullet_x-j][bullet_y]=spirittank_miss-j+1;     	//赋值
								}
							}
						}
					}else if(playerbullet.getY()%17>=16) {
						for(int j=0;j<spirittank_miss;j++) {				//循环执行避让距离设置
							if(bullet_x-j>=0&&bullet_y+1<37) {				//确保判定合理性
								if(World.get(worldNumber).mapmap_move_point002[bullet_x-j][bullet_y+1]!=1) {		//保证不会覆盖墙体
									World.get(worldNumber).mapmap_move_point002[bullet_x-j][bullet_y+1]=spirittank_miss-j+1;     	//赋值
								}
							}
						}
					}else {													//下判定
						for(int j=0;j<spirittank_miss;j++) {				//循环执行避让距离设置
							if(bullet_x-j>=0&&bullet_y+1<37) {				//确保判定合理性
								if(World.get(worldNumber).mapmap_move_point002[bullet_x-j][bullet_y+1]!=1) {		//保证不会覆盖墙体
									World.get(worldNumber).mapmap_move_point002[bullet_x-j][bullet_y+1]=spirittank_miss-j+1;     	//赋值
								}
							}
						}
						for(int j=0;j<spirittank_miss;j++) {				//循环执行避让距离设置
							if(bullet_x-j>=0) {								//确保判定合理性
								if(World.get(worldNumber).mapmap_move_point002[bullet_x-j][bullet_y]!=1) {			//保证不会覆盖墙体
									World.get(worldNumber).mapmap_move_point002[bullet_x-j][bullet_y]=spirittank_miss-j+1;     		//赋值
								}
							}
						}
					}
				}
			}
			
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
						if(World.get(worldNumber).mapmap_move_point002[i][j]<-20&&
								lingpai[i][j]==0) {//判定为非墙，非0，非本轮运算
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
				//复原运算本轮运算判定
				for(int i=0;i<=47;i++) {
					for(int j=0;j<=36;j++) {
						lingpai[i][j]=0;
					}
				}
			}
			
			//非衍生，硬性添加部分

			/*********
			//显示数字地图运算过程
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
			this.spirittank_distance002=World.get(worldNumber).move_distance;		//定制行走半径
			//实时运算玩家坦克梯度运算
			//凝结核部分
			int playertank_block_x=playertank.getX()/17;
			int playertank_block_y=playertank.getY()/17;
			int key=0;											//令牌，当未进行任何操作时，跳出循环
			//设置权值点位置
			//方法一：自爆型权值点
			/*
			if(World.get(worldNumber).mapmap_move_point001[playertank_block_x][playertank_block_y]!=1) {
				World.get(worldNumber).mapmap_move_point001[playertank_block_x][playertank_block_y]=-spirittank_see;//设置最低梯度
			}
			if(World.get(worldNumber).mapmap_move_point001[playertank_block_x+1][playertank_block_y]!=1) {
				World.get(worldNumber).mapmap_move_point001[playertank_block_x+1][playertank_block_y]=-spirittank_see;//设置最低梯度
			}
			if(World.get(worldNumber).mapmap_move_point001[playertank_block_x][playertank_block_y+1]!=1) {
				World.get(worldNumber).mapmap_move_point001[playertank_block_x][playertank_block_y+1]=-spirittank_see;//设置最低梯度
			}
			if(World.get(worldNumber).mapmap_move_point001[playertank_block_x+1][playertank_block_y+1]!=1) {
				World.get(worldNumber).mapmap_move_point001[playertank_block_x+1][playertank_block_y+1]=-spirittank_see;//设置最低梯度
			}
			*/
			//方法二：距离把控权值点
			//衍生部分1：行动权值（上）
			if(playertank_block_y-spirittank_distance002>=0) {
				if(World.get(worldNumber).mapmap_move_point003[playertank_block_x][playertank_block_y-spirittank_distance002]!=1) {
					World.get(worldNumber).mapmap_move_point003[playertank_block_x][playertank_block_y-spirittank_distance002]=-spirittank_see;
				}
			}
			
			//权值部分2：避让权值(避让玩家)
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
			
			//权值部分3：避让权值(避让子弹)
			//非衍生部分
			for(int i=0;i<World.get(worldNumber).playerbullets.size();i++) {//避让设置
				Bullet playerbullet=World.get(worldNumber).playerbullets.get(i);
				int bullet_x=playerbullet.getX()/17;
				int bullet_y=playerbullet.getY()/17;
				if(playerbullet.getDiraction()==Spirit.UP) {				/***方向为上***/
					if(playerbullet.getX()%17<=1) {							//左判定
						for(int j=0;j<spirittank_miss;j++) {				//循环执行避让距离设置
							if(bullet_y-j>=0) {								//确保判定合理性
								if(World.get(worldNumber).mapmap_move_point003[bullet_x][bullet_y-j]!=1) {		//保证不会覆盖墙体
									World.get(worldNumber).mapmap_move_point003[bullet_x][bullet_y-j]=spirittank_miss-j+1;		//赋值
								}
							}
						}
					}else if(playerbullet.getX()%17>=16) {
						for(int j=0;j<spirittank_miss;j++) {				//循环执行避让距离设置
							if(bullet_y-j>=0&&bullet_x+1<48) {				//确保判定合理性
								if(World.get(worldNumber).mapmap_move_point003[bullet_x+1][bullet_y-j]!=1) {	//保证不会覆盖墙体
									World.get(worldNumber).mapmap_move_point003[bullet_x+1][bullet_y-j]=spirittank_miss-j+1;		//赋值
								}
							}
						}
					}else {													//右判定				
						for(int j=0;j<spirittank_miss;j++) {				//循环执行避让距离设置
							if(bullet_y-j>=0&&bullet_x+1<48) {				//确保判定合理性
								if(World.get(worldNumber).mapmap_move_point003[bullet_x+1][bullet_y-j]!=1) {	//保证不会覆盖墙体
									World.get(worldNumber).mapmap_move_point003[bullet_x+1][bullet_y-j]=spirittank_miss-j+1;     //赋值
								}
							}
						}
						for(int j=0;j<spirittank_miss;j++) {				//循环执行避让距离设置
							if(bullet_y-j>=0) {								//确保判定合理性
								if(World.get(worldNumber).mapmap_move_point003[bullet_x][bullet_y-j]!=1) {		//保证不会覆盖墙体
									World.get(worldNumber).mapmap_move_point003[bullet_x][bullet_y-j]=spirittank_miss-j+1;		//赋值
								}
							}
						}
					}
				}else if(playerbullet.getDiraction()==Spirit.RIGHT) {		/***方向为右***/
					if(playerbullet.getY()%17<=1) {							//上判定
						for(int j=0;j<spirittank_miss;j++) {				//循环执行避让距离设置
							if(bullet_x+j<48) {								//确保判定合理性
								if(World.get(worldNumber).mapmap_move_point003[bullet_x+j][bullet_y]!=1) {		//保证不会覆盖墙体
									World.get(worldNumber).mapmap_move_point003[bullet_x+j][bullet_y]=spirittank_miss-j+1;     	//赋值
								}
							}
						}
					}else if(playerbullet.getY()%17>=16) {
						for(int j=0;j<spirittank_miss;j++) {				//循环执行避让距离设置
							if(bullet_x+j<48&&bullet_y+1<37) {				//确保判定合理性
								if(World.get(worldNumber).mapmap_move_point003[bullet_x+j][bullet_y+1]!=1) {		//保证不会覆盖墙体
									World.get(worldNumber).mapmap_move_point003[bullet_x+j][bullet_y+1]=spirittank_miss-j+1;     	//赋值
								}
							}
						}
					}else {													//下判定
						for(int j=0;j<spirittank_miss;j++) {				//循环执行避让距离设置
							if(bullet_x+j<48&&bullet_y+1<37) {				//确保判定合理性
								if(World.get(worldNumber).mapmap_move_point003[bullet_x+j][bullet_y+1]!=1) {		//保证不会覆盖墙体
									World.get(worldNumber).mapmap_move_point003[bullet_x+j][bullet_y+1]=spirittank_miss-j+1;     	//赋值
								}
							}
						}
						for(int j=0;j<spirittank_miss;j++) {				//循环执行避让距离设置
							if(bullet_x+j<48) {								//确保判定合理性
								if(World.get(worldNumber).mapmap_move_point003[bullet_x+j][bullet_y]!=1) {			//保证不会覆盖墙体
									World.get(worldNumber).mapmap_move_point003[bullet_x+j][bullet_y]=spirittank_miss-j+1;     		//赋值
								}
							}
						}
					}
				}else if(playerbullet.getDiraction()==Spirit.DOWN) {		/***方向为下***/
					if(playerbullet.getX()%17<=1) {							//左判定
						for(int j=0;j<spirittank_miss;j++) {				//循环执行避让距离设置
							if(bullet_y+j<37) {								//确保判定合理性
								if(World.get(worldNumber).mapmap_move_point003[bullet_x][bullet_y+j]!=1) {		//保证不会覆盖墙体
									World.get(worldNumber).mapmap_move_point003[bullet_x][bullet_y+j]=spirittank_miss-j+1;		//赋值
								}
							}
						}
					}else if(playerbullet.getX()%17>=16) {
						for(int j=0;j<spirittank_miss;j++) {				//循环执行避让距离设置
							if(bullet_y+j<37&&bullet_x+1<48) {				//确保判定合理性
								if(World.get(worldNumber).mapmap_move_point003[bullet_x+1][bullet_y+j]!=1) {	//保证不会覆盖墙体
									World.get(worldNumber).mapmap_move_point003[bullet_x+1][bullet_y+j]=spirittank_miss-j+1;		//赋值
								}
							}
						}
					}else {													//右判定				
						for(int j=0;j<spirittank_miss;j++) {				//循环执行避让距离设置
							if(bullet_y+j<37&&bullet_x+1<48) {				//确保判定合理性
								if(World.get(worldNumber).mapmap_move_point003[bullet_x+1][bullet_y+j]!=1) {	//保证不会覆盖墙体
									World.get(worldNumber).mapmap_move_point003[bullet_x+1][bullet_y+j]=spirittank_miss-j+1;     //赋值
								}
							}
						}
						for(int j=0;j<spirittank_miss;j++) {				//循环执行避让距离设置
							if(bullet_y+j<37) {								//确保判定合理性
								if(World.get(worldNumber).mapmap_move_point003[bullet_x][bullet_y+j]!=1) {		//保证不会覆盖墙体
									World.get(worldNumber).mapmap_move_point003[bullet_x][bullet_y+j]=spirittank_miss-j+1;		//赋值
								}
							}
						}
					}
				}else if(playerbullet.getDiraction()==Spirit.LEFT) {		/***方向为左***/
					if(playerbullet.getY()%17<=1) {							//上判定
						for(int j=0;j<spirittank_miss;j++) {				//循环执行避让距离设置
							if(bullet_x-j>=0) {								//确保判定合理性
								if(World.get(worldNumber).mapmap_move_point003[bullet_x-j][bullet_y]!=1) {		//保证不会覆盖墙体
									World.get(worldNumber).mapmap_move_point003[bullet_x-j][bullet_y]=spirittank_miss-j+1;     	//赋值
								}
							}
						}
					}else if(playerbullet.getY()%17>=16) {
						for(int j=0;j<spirittank_miss;j++) {				//循环执行避让距离设置
							if(bullet_x-j>=0&&bullet_y+1<37) {				//确保判定合理性
								if(World.get(worldNumber).mapmap_move_point003[bullet_x-j][bullet_y+1]!=1) {		//保证不会覆盖墙体
									World.get(worldNumber).mapmap_move_point003[bullet_x-j][bullet_y+1]=spirittank_miss-j+1;     	//赋值
								}
							}
						}
					}else {													//下判定
						for(int j=0;j<spirittank_miss;j++) {				//循环执行避让距离设置
							if(bullet_x-j>=0&&bullet_y+1<37) {				//确保判定合理性
								if(World.get(worldNumber).mapmap_move_point003[bullet_x-j][bullet_y+1]!=1) {		//保证不会覆盖墙体
									World.get(worldNumber).mapmap_move_point003[bullet_x-j][bullet_y+1]=spirittank_miss-j+1;     	//赋值
								}
							}
						}
						for(int j=0;j<spirittank_miss;j++) {				//循环执行避让距离设置
							if(bullet_x-j>=0) {								//确保判定合理性
								if(World.get(worldNumber).mapmap_move_point003[bullet_x-j][bullet_y]!=1) {			//保证不会覆盖墙体
									World.get(worldNumber).mapmap_move_point003[bullet_x-j][bullet_y]=spirittank_miss-j+1;     		//赋值
								}
							}
						}
					}
				}
			}

			
			
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
						if(World.get(worldNumber).mapmap_move_point003[i][j]<-20&&
								lingpai[i][j]==0) {//判定为非墙，非0，非本轮运算
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
				//复原运算本轮运算判定
				for(int i=0;i<=47;i++) {
					for(int j=0;j<=36;j++) {
						lingpai[i][j]=0;
					}
				}
			}
			
			/*********
			//显示数字地图运算过程
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
			this.spirittank_distance002=World.get(worldNumber).move_distance;		//定制行走半径
			//实时运算玩家坦克梯度运算
			//凝结核部分
			int playertank_block_x=playertank.getX()/17;
			int playertank_block_y=playertank.getY()/17;
			int key=0;											//令牌，当未进行任何操作时，跳出循环
			//设置权值点位置
			//方法一：自爆型权值点
			/*
			if(World.get(worldNumber).mapmap_move_point001[playertank_block_x][playertank_block_y]!=1) {
				World.get(worldNumber).mapmap_move_point001[playertank_block_x][playertank_block_y]=-spirittank_see;//设置最低梯度
			}
			if(World.get(worldNumber).mapmap_move_point001[playertank_block_x+1][playertank_block_y]!=1) {
				World.get(worldNumber).mapmap_move_point001[playertank_block_x+1][playertank_block_y]=-spirittank_see;//设置最低梯度
			}
			if(World.get(worldNumber).mapmap_move_point001[playertank_block_x][playertank_block_y+1]!=1) {
				World.get(worldNumber).mapmap_move_point001[playertank_block_x][playertank_block_y+1]=-spirittank_see;//设置最低梯度
			}
			if(World.get(worldNumber).mapmap_move_point001[playertank_block_x+1][playertank_block_y+1]!=1) {
				World.get(worldNumber).mapmap_move_point001[playertank_block_x+1][playertank_block_y+1]=-spirittank_see;//设置最低梯度
			}
			*/
			//方法二：距离把控权值点
			//衍生部分1：行动权值（右上）
			if(playertank_block_x+spirittank_distance002<=47&&playertank_block_y-spirittank_distance002>=0) {
				if(World.get(worldNumber).mapmap_move_point004[playertank_block_x+spirittank_distance002][playertank_block_y-spirittank_distance002]!=1) {
					World.get(worldNumber).mapmap_move_point004[playertank_block_x+spirittank_distance002][playertank_block_y-spirittank_distance002]=-spirittank_see;
				}
			}
			//权值部分2：避让玩家
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
			//权值部分3：避让子弹
			//非衍生部分
			for(int i=0;i<World.get(worldNumber).playerbullets.size();i++) {//避让设置
				Bullet playerbullet=World.get(worldNumber).playerbullets.get(i);
				int bullet_x=playerbullet.getX()/17;
				int bullet_y=playerbullet.getY()/17;
				if(playerbullet.getDiraction()==Spirit.UP) {				/***方向为上***/
					if(playerbullet.getX()%17<=1) {							//左判定
						for(int j=0;j<spirittank_miss;j++) {				//循环执行避让距离设置
							if(bullet_y-j>=0) {								//确保判定合理性
								if(World.get(worldNumber).mapmap_move_point004[bullet_x][bullet_y-j]!=1) {		//保证不会覆盖墙体
									World.get(worldNumber).mapmap_move_point004[bullet_x][bullet_y-j]=spirittank_miss-j+1;		//赋值
								}
							}
						}
					}else if(playerbullet.getX()%17>=16) {
						for(int j=0;j<spirittank_miss;j++) {				//循环执行避让距离设置
							if(bullet_y-j>=0&&bullet_x+1<48) {				//确保判定合理性
								if(World.get(worldNumber).mapmap_move_point004[bullet_x+1][bullet_y-j]!=1) {	//保证不会覆盖墙体
									World.get(worldNumber).mapmap_move_point004[bullet_x+1][bullet_y-j]=spirittank_miss-j+1;		//赋值
								}
							}
						}
					}else {													//右判定				
						for(int j=0;j<spirittank_miss;j++) {				//循环执行避让距离设置
							if(bullet_y-j>=0&&bullet_x+1<48) {				//确保判定合理性
								if(World.get(worldNumber).mapmap_move_point004[bullet_x+1][bullet_y-j]!=1) {	//保证不会覆盖墙体
									World.get(worldNumber).mapmap_move_point004[bullet_x+1][bullet_y-j]=spirittank_miss-j+1;     //赋值
								}
							}
						}
						for(int j=0;j<spirittank_miss;j++) {				//循环执行避让距离设置
							if(bullet_y-j>=0) {								//确保判定合理性
								if(World.get(worldNumber).mapmap_move_point004[bullet_x][bullet_y-j]!=1) {		//保证不会覆盖墙体
									World.get(worldNumber).mapmap_move_point004[bullet_x][bullet_y-j]=spirittank_miss-j+1;		//赋值
								}
							}
						}
					}
				}else if(playerbullet.getDiraction()==Spirit.RIGHT) {		/***方向为右***/
					if(playerbullet.getY()%17<=1) {							//上判定
						for(int j=0;j<spirittank_miss;j++) {				//循环执行避让距离设置
							if(bullet_x+j<48) {								//确保判定合理性
								if(World.get(worldNumber).mapmap_move_point004[bullet_x+j][bullet_y]!=1) {		//保证不会覆盖墙体
									World.get(worldNumber).mapmap_move_point004[bullet_x+j][bullet_y]=spirittank_miss-j+1;     	//赋值
								}
							}
						}
					}else if(playerbullet.getY()%17>=16) {
						for(int j=0;j<spirittank_miss;j++) {				//循环执行避让距离设置
							if(bullet_x+j<48&&bullet_y+1<37) {				//确保判定合理性
								if(World.get(worldNumber).mapmap_move_point004[bullet_x+j][bullet_y+1]!=1) {		//保证不会覆盖墙体
									World.get(worldNumber).mapmap_move_point004[bullet_x+j][bullet_y+1]=spirittank_miss-j+1;     	//赋值
								}
							}
						}
					}else {													//下判定
						for(int j=0;j<spirittank_miss;j++) {				//循环执行避让距离设置
							if(bullet_x+j<48&&bullet_y+1<37) {				//确保判定合理性
								if(World.get(worldNumber).mapmap_move_point004[bullet_x+j][bullet_y+1]!=1) {		//保证不会覆盖墙体
									World.get(worldNumber).mapmap_move_point004[bullet_x+j][bullet_y+1]=spirittank_miss-j+1;     	//赋值
								}
							}
						}
						for(int j=0;j<spirittank_miss;j++) {				//循环执行避让距离设置
							if(bullet_x+j<48) {								//确保判定合理性
								if(World.get(worldNumber).mapmap_move_point004[bullet_x+j][bullet_y]!=1) {			//保证不会覆盖墙体
									World.get(worldNumber).mapmap_move_point004[bullet_x+j][bullet_y]=spirittank_miss-j+1;     		//赋值
								}
							}
						}
					}
				}else if(playerbullet.getDiraction()==Spirit.DOWN) {		/***方向为下***/
					if(playerbullet.getX()%17<=1) {							//左判定
						for(int j=0;j<spirittank_miss;j++) {				//循环执行避让距离设置
							if(bullet_y+j<37) {								//确保判定合理性
								if(World.get(worldNumber).mapmap_move_point004[bullet_x][bullet_y+j]!=1) {		//保证不会覆盖墙体
									World.get(worldNumber).mapmap_move_point004[bullet_x][bullet_y+j]=spirittank_miss-j+1;		//赋值
								}
							}
						}
					}else if(playerbullet.getX()%17>=16) {
						for(int j=0;j<spirittank_miss;j++) {				//循环执行避让距离设置
							if(bullet_y+j<37&&bullet_x+1<48) {				//确保判定合理性
								if(World.get(worldNumber).mapmap_move_point004[bullet_x+1][bullet_y+j]!=1) {	//保证不会覆盖墙体
									World.get(worldNumber).mapmap_move_point004[bullet_x+1][bullet_y+j]=spirittank_miss-j+1;		//赋值
								}
							}
						}
					}else {													//右判定				
						for(int j=0;j<spirittank_miss;j++) {				//循环执行避让距离设置
							if(bullet_y+j<37&&bullet_x+1<48) {				//确保判定合理性
								if(World.get(worldNumber).mapmap_move_point004[bullet_x+1][bullet_y+j]!=1) {	//保证不会覆盖墙体
									World.get(worldNumber).mapmap_move_point004[bullet_x+1][bullet_y+j]=spirittank_miss-j+1;     //赋值
								}
							}
						}
						for(int j=0;j<spirittank_miss;j++) {				//循环执行避让距离设置
							if(bullet_y+j<37) {								//确保判定合理性
								if(World.get(worldNumber).mapmap_move_point004[bullet_x][bullet_y+j]!=1) {		//保证不会覆盖墙体
									World.get(worldNumber).mapmap_move_point004[bullet_x][bullet_y+j]=spirittank_miss-j+1;		//赋值
								}
							}
						}
					}
				}else if(playerbullet.getDiraction()==Spirit.LEFT) {		/***方向为左***/
					if(playerbullet.getY()%17<=1) {							//上判定
						for(int j=0;j<spirittank_miss;j++) {				//循环执行避让距离设置
							if(bullet_x-j>=0) {								//确保判定合理性
								if(World.get(worldNumber).mapmap_move_point004[bullet_x-j][bullet_y]!=1) {		//保证不会覆盖墙体
									World.get(worldNumber).mapmap_move_point004[bullet_x-j][bullet_y]=spirittank_miss-j+1;     	//赋值
								}
							}
						}
					}else if(playerbullet.getY()%17>=16) {
						for(int j=0;j<spirittank_miss;j++) {				//循环执行避让距离设置
							if(bullet_x-j>=0&&bullet_y+1<37) {				//确保判定合理性
								if(World.get(worldNumber).mapmap_move_point004[bullet_x-j][bullet_y+1]!=1) {		//保证不会覆盖墙体
									World.get(worldNumber).mapmap_move_point004[bullet_x-j][bullet_y+1]=spirittank_miss-j+1;     	//赋值
								}
							}
						}
					}else {													//下判定
						for(int j=0;j<spirittank_miss;j++) {				//循环执行避让距离设置
							if(bullet_x-j>=0&&bullet_y+1<37) {				//确保判定合理性
								if(World.get(worldNumber).mapmap_move_point004[bullet_x-j][bullet_y+1]!=1) {		//保证不会覆盖墙体
									World.get(worldNumber).mapmap_move_point004[bullet_x-j][bullet_y+1]=spirittank_miss-j+1;     	//赋值
								}
							}
						}
						for(int j=0;j<spirittank_miss;j++) {				//循环执行避让距离设置
							if(bullet_x-j>=0) {								//确保判定合理性
								if(World.get(worldNumber).mapmap_move_point004[bullet_x-j][bullet_y]!=1) {			//保证不会覆盖墙体
									World.get(worldNumber).mapmap_move_point004[bullet_x-j][bullet_y]=spirittank_miss-j+1;     		//赋值
								}
							}
						}
					}
				}
			}		
			
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
						if(World.get(worldNumber).mapmap_move_point004[i][j]<-20&&
								lingpai[i][j]==0) {//判定为非墙，非0，非本轮运算
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
				//复原运算本轮运算判定
				for(int i=0;i<=47;i++) {
					for(int j=0;j<=36;j++) {
						lingpai[i][j]=0;
					}
				}
			}



			/*********
			//显示数字地图运算过程
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
			//实时运算玩家坦克梯度运算
			//凝结核部分
			int playertank_block_x=playertank.getX()/17;
			int playertank_block_y=playertank.getY()/17;
			int key=0;											//令牌，当未进行任何操作时，跳出循环
			//设置权值点位置
			//方法一：自爆型权值点
			/*
			if(World.get(worldNumber).mapmap_move_point001[playertank_block_x][playertank_block_y]!=1) {
				World.get(worldNumber).mapmap_move_point001[playertank_block_x][playertank_block_y]=-spirittank_see;//设置最低梯度
			}
			if(World.get(worldNumber).mapmap_move_point001[playertank_block_x+1][playertank_block_y]!=1) {
				World.get(worldNumber).mapmap_move_point001[playertank_block_x+1][playertank_block_y]=-spirittank_see;//设置最低梯度
			}
			if(World.get(worldNumber).mapmap_move_point001[playertank_block_x][playertank_block_y+1]!=1) {
				World.get(worldNumber).mapmap_move_point001[playertank_block_x][playertank_block_y+1]=-spirittank_see;//设置最低梯度
			}
			if(World.get(worldNumber).mapmap_move_point001[playertank_block_x+1][playertank_block_y+1]!=1) {
				World.get(worldNumber).mapmap_move_point001[playertank_block_x+1][playertank_block_y+1]=-spirittank_see;//设置最低梯度
			}
			*/
			//方法二：距离把控权值点
			//衍生部分1：行动权值（右）
			if(playertank_block_x+spirittank_distance002<=47) {
				if(World.get(worldNumber).mapmap_move_point005[playertank_block_x+spirittank_distance002][playertank_block_y]!=1) {
					World.get(worldNumber).mapmap_move_point005[playertank_block_x+spirittank_distance002][playertank_block_y]=-spirittank_see;
				}
			}
			//权值部分2：避让玩家
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
			//权值部分2：避让玩家
			//非衍生部分
			for(int i=0;i<World.get(worldNumber).playerbullets.size();i++) {//避让设置
				Bullet playerbullet=World.get(worldNumber).playerbullets.get(i);
				int bullet_x=playerbullet.getX()/17;
				int bullet_y=playerbullet.getY()/17;
				if(playerbullet.getDiraction()==Spirit.UP) {				/***方向为上***/
					if(playerbullet.getX()%17<=1) {							//左判定
						for(int j=0;j<spirittank_miss;j++) {				//循环执行避让距离设置
							if(bullet_y-j>=0) {								//确保判定合理性
								if(World.get(worldNumber).mapmap_move_point005[bullet_x][bullet_y-j]!=1) {		//保证不会覆盖墙体
									World.get(worldNumber).mapmap_move_point005[bullet_x][bullet_y-j]=spirittank_miss-j+1;		//赋值
								}
							}
						}
					}else if(playerbullet.getX()%17>=16) {
						for(int j=0;j<spirittank_miss;j++) {				//循环执行避让距离设置
							if(bullet_y-j>=0&&bullet_x+1<48) {				//确保判定合理性
								if(World.get(worldNumber).mapmap_move_point005[bullet_x+1][bullet_y-j]!=1) {	//保证不会覆盖墙体
									World.get(worldNumber).mapmap_move_point005[bullet_x+1][bullet_y-j]=spirittank_miss-j+1;		//赋值
								}
							}
						}
					}else {													//右判定				
						for(int j=0;j<spirittank_miss;j++) {				//循环执行避让距离设置
							if(bullet_y-j>=0&&bullet_x+1<48) {				//确保判定合理性
								if(World.get(worldNumber).mapmap_move_point005[bullet_x+1][bullet_y-j]!=1) {	//保证不会覆盖墙体
									World.get(worldNumber).mapmap_move_point005[bullet_x+1][bullet_y-j]=spirittank_miss-j+1;     //赋值
								}
							}
						}
						for(int j=0;j<spirittank_miss;j++) {				//循环执行避让距离设置
							if(bullet_y-j>=0) {								//确保判定合理性
								if(World.get(worldNumber).mapmap_move_point005[bullet_x][bullet_y-j]!=1) {		//保证不会覆盖墙体
									World.get(worldNumber).mapmap_move_point005[bullet_x][bullet_y-j]=spirittank_miss-j+1;		//赋值
								}
							}
						}
					}
				}else if(playerbullet.getDiraction()==Spirit.RIGHT) {		/***方向为右***/
					if(playerbullet.getY()%17<=1) {							//上判定
						for(int j=0;j<spirittank_miss;j++) {				//循环执行避让距离设置
							if(bullet_x+j<48) {								//确保判定合理性
								if(World.get(worldNumber).mapmap_move_point005[bullet_x+j][bullet_y]!=1) {		//保证不会覆盖墙体
									World.get(worldNumber).mapmap_move_point005[bullet_x+j][bullet_y]=spirittank_miss-j+1;     	//赋值
								}
							}
						}
					}else if(playerbullet.getY()%17>=16) {
						for(int j=0;j<spirittank_miss;j++) {				//循环执行避让距离设置
							if(bullet_x+j<48&&bullet_y+1<37) {				//确保判定合理性
								if(World.get(worldNumber).mapmap_move_point005[bullet_x+j][bullet_y+1]!=1) {		//保证不会覆盖墙体
									World.get(worldNumber).mapmap_move_point005[bullet_x+j][bullet_y+1]=spirittank_miss-j+1;     	//赋值
								}
							}
						}
					}else {													//下判定
						for(int j=0;j<spirittank_miss;j++) {				//循环执行避让距离设置
							if(bullet_x+j<48&&bullet_y+1<37) {				//确保判定合理性
								if(World.get(worldNumber).mapmap_move_point005[bullet_x+j][bullet_y+1]!=1) {		//保证不会覆盖墙体
									World.get(worldNumber).mapmap_move_point005[bullet_x+j][bullet_y+1]=spirittank_miss-j+1;     	//赋值
								}
							}
						}
						for(int j=0;j<spirittank_miss;j++) {				//循环执行避让距离设置
							if(bullet_x+j<48) {								//确保判定合理性
								if(World.get(worldNumber).mapmap_move_point005[bullet_x+j][bullet_y]!=1) {			//保证不会覆盖墙体
									World.get(worldNumber).mapmap_move_point005[bullet_x+j][bullet_y]=spirittank_miss-j+1;     		//赋值
								}
							}
						}
					}
				}else if(playerbullet.getDiraction()==Spirit.DOWN) {		/***方向为下***/
					if(playerbullet.getX()%17<=1) {							//左判定
						for(int j=0;j<spirittank_miss;j++) {				//循环执行避让距离设置
							if(bullet_y+j<37) {								//确保判定合理性
								if(World.get(worldNumber).mapmap_move_point005[bullet_x][bullet_y+j]!=1) {		//保证不会覆盖墙体
									World.get(worldNumber).mapmap_move_point005[bullet_x][bullet_y+j]=spirittank_miss-j+1;		//赋值
								}
							}
						}
					}else if(playerbullet.getX()%17>=16) {
						for(int j=0;j<spirittank_miss;j++) {				//循环执行避让距离设置
							if(bullet_y+j<37&&bullet_x+1<48) {				//确保判定合理性
								if(World.get(worldNumber).mapmap_move_point005[bullet_x+1][bullet_y+j]!=1) {	//保证不会覆盖墙体
									World.get(worldNumber).mapmap_move_point005[bullet_x+1][bullet_y+j]=spirittank_miss-j+1;		//赋值
								}
							}
						}
					}else {													//右判定				
						for(int j=0;j<spirittank_miss;j++) {				//循环执行避让距离设置
							if(bullet_y+j<37&&bullet_x+1<48) {				//确保判定合理性
								if(World.get(worldNumber).mapmap_move_point005[bullet_x+1][bullet_y+j]!=1) {	//保证不会覆盖墙体
									World.get(worldNumber).mapmap_move_point005[bullet_x+1][bullet_y+j]=spirittank_miss-j+1;     //赋值
								}
							}
						}
						for(int j=0;j<spirittank_miss;j++) {				//循环执行避让距离设置
							if(bullet_y+j<37) {								//确保判定合理性
								if(World.get(worldNumber).mapmap_move_point005[bullet_x][bullet_y+j]!=1) {		//保证不会覆盖墙体
									World.get(worldNumber).mapmap_move_point005[bullet_x][bullet_y+j]=spirittank_miss-j+1;		//赋值
								}
							}
						}
					}
				}else if(playerbullet.getDiraction()==Spirit.LEFT) {		/***方向为左***/
					if(playerbullet.getY()%17<=1) {							//上判定
						for(int j=0;j<spirittank_miss;j++) {				//循环执行避让距离设置
							if(bullet_x-j>=0) {								//确保判定合理性
								if(World.get(worldNumber).mapmap_move_point005[bullet_x-j][bullet_y]!=1) {		//保证不会覆盖墙体
									World.get(worldNumber).mapmap_move_point005[bullet_x-j][bullet_y]=spirittank_miss-j+1;     	//赋值
								}
							}
						}
					}else if(playerbullet.getY()%17>=16) {
						for(int j=0;j<spirittank_miss;j++) {				//循环执行避让距离设置
							if(bullet_x-j>=0&&bullet_y+1<37) {				//确保判定合理性
								if(World.get(worldNumber).mapmap_move_point005[bullet_x-j][bullet_y+1]!=1) {		//保证不会覆盖墙体
									World.get(worldNumber).mapmap_move_point005[bullet_x-j][bullet_y+1]=spirittank_miss-j+1;     	//赋值
								}
							}
						}
					}else {													//下判定
						for(int j=0;j<spirittank_miss;j++) {				//循环执行避让距离设置
							if(bullet_x-j>=0&&bullet_y+1<37) {				//确保判定合理性
								if(World.get(worldNumber).mapmap_move_point005[bullet_x-j][bullet_y+1]!=1) {		//保证不会覆盖墙体
									World.get(worldNumber).mapmap_move_point005[bullet_x-j][bullet_y+1]=spirittank_miss-j+1;     	//赋值
								}
							}
						}
						for(int j=0;j<spirittank_miss;j++) {				//循环执行避让距离设置
							if(bullet_x-j>=0) {								//确保判定合理性
								if(World.get(worldNumber).mapmap_move_point005[bullet_x-j][bullet_y]!=1) {			//保证不会覆盖墙体
									World.get(worldNumber).mapmap_move_point005[bullet_x-j][bullet_y]=spirittank_miss-j+1;     		//赋值
								}
							}
						}
					}
				}
			}

			
			
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
						if(World.get(worldNumber).mapmap_move_point005[i][j]<-20&&
								lingpai[i][j]==0) {//判定为非墙，非0，非本轮运算
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
				//复原运算本轮运算判定
				for(int i=0;i<=47;i++) {
					for(int j=0;j<=36;j++) {
						lingpai[i][j]=0;
					}
				}
			}
			
			/*********
			System.out.println("005");
			//显示数字地图运算过程
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
			this.spirittank_distance002=World.get(worldNumber).move_distance;		//定制行走半径
			//实时运算玩家坦克梯度运算
			//凝结核部分
			int playertank_block_x=playertank.getX()/17;
			int playertank_block_y=playertank.getY()/17;
			int key=0;											//令牌，当未进行任何操作时，跳出循环
			//设置权值点位置
			//衍生部分1：行动权值
			//权值点集中在右下方
			if(playertank_block_x+spirittank_distance002<=47&&playertank_block_y+spirittank_distance002<=36) {
				if(World.get(worldNumber).mapmap_move_point006[playertank_block_x+spirittank_distance002][playertank_block_y+spirittank_distance002]!=1) {
					World.get(worldNumber).mapmap_move_point006[playertank_block_x+spirittank_distance002][playertank_block_y+spirittank_distance002]=-spirittank_see;
				}
			}
			//权值部分2：避让玩家
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
			//权值部分3：避让子弹
			for(int i=0;i<World.get(worldNumber).playerbullets.size();i++) {//避让设置
				Bullet playerbullet=World.get(worldNumber).playerbullets.get(i);
				int bullet_x=playerbullet.getX()/17;
				int bullet_y=playerbullet.getY()/17;
				if(playerbullet.getDiraction()==Spirit.UP) {				/***方向为上***/
					if(playerbullet.getX()%17<=1) {							//左判定
						for(int j=0;j<spirittank_miss;j++) {				//循环执行避让距离设置
							if(bullet_y-j>=0) {								//确保判定合理性
								if(World.get(worldNumber).mapmap_move_point006[bullet_x][bullet_y-j]!=1) {		//保证不会覆盖墙体
									World.get(worldNumber).mapmap_move_point006[bullet_x][bullet_y-j]=spirittank_miss-j+1;		//赋值
								}
							}
						}
					}else if(playerbullet.getX()%17>=16) {
						for(int j=0;j<spirittank_miss;j++) {				//循环执行避让距离设置
							if(bullet_y-j>=0&&bullet_x+1<48) {				//确保判定合理性
								if(World.get(worldNumber).mapmap_move_point006[bullet_x+1][bullet_y-j]!=1) {	//保证不会覆盖墙体
									World.get(worldNumber).mapmap_move_point006[bullet_x+1][bullet_y-j]=spirittank_miss-j+1;		//赋值
								}
							}
						}
					}else {													//右判定				
						for(int j=0;j<spirittank_miss;j++) {				//循环执行避让距离设置
							if(bullet_y-j>=0&&bullet_x+1<48) {				//确保判定合理性
								if(World.get(worldNumber).mapmap_move_point006[bullet_x+1][bullet_y-j]!=1) {	//保证不会覆盖墙体
									World.get(worldNumber).mapmap_move_point006[bullet_x+1][bullet_y-j]=spirittank_miss-j+1;     //赋值
								}
							}
						}
						for(int j=0;j<spirittank_miss;j++) {				//循环执行避让距离设置
							if(bullet_y-j>=0) {								//确保判定合理性
								if(World.get(worldNumber).mapmap_move_point006[bullet_x][bullet_y-j]!=1) {		//保证不会覆盖墙体
									World.get(worldNumber).mapmap_move_point006[bullet_x][bullet_y-j]=spirittank_miss-j+1;		//赋值
								}
							}
						}
					}
				}else if(playerbullet.getDiraction()==Spirit.RIGHT) {		/***方向为右***/
					if(playerbullet.getY()%17<=1) {							//上判定
						for(int j=0;j<spirittank_miss;j++) {				//循环执行避让距离设置
							if(bullet_x+j<48) {								//确保判定合理性
								if(World.get(worldNumber).mapmap_move_point006[bullet_x+j][bullet_y]!=1) {		//保证不会覆盖墙体
									World.get(worldNumber).mapmap_move_point006[bullet_x+j][bullet_y]=spirittank_miss-j+1;     	//赋值
								}
							}
						}
					}else if(playerbullet.getY()%17>=16) {
						for(int j=0;j<spirittank_miss;j++) {				//循环执行避让距离设置
							if(bullet_x+j<48&&bullet_y+1<37) {				//确保判定合理性
								if(World.get(worldNumber).mapmap_move_point006[bullet_x+j][bullet_y+1]!=1) {		//保证不会覆盖墙体
									World.get(worldNumber).mapmap_move_point006[bullet_x+j][bullet_y+1]=spirittank_miss-j+1;     	//赋值
								}
							}
						}
					}else {													//下判定
						for(int j=0;j<spirittank_miss;j++) {				//循环执行避让距离设置
							if(bullet_x+j<48&&bullet_y+1<37) {				//确保判定合理性
								if(World.get(worldNumber).mapmap_move_point006[bullet_x+j][bullet_y+1]!=1) {		//保证不会覆盖墙体
									World.get(worldNumber).mapmap_move_point006[bullet_x+j][bullet_y+1]=spirittank_miss-j+1;     	//赋值
								}
							}
						}
						for(int j=0;j<spirittank_miss;j++) {				//循环执行避让距离设置
							if(bullet_x+j<48) {								//确保判定合理性
								if(World.get(worldNumber).mapmap_move_point006[bullet_x+j][bullet_y]!=1) {			//保证不会覆盖墙体
									World.get(worldNumber).mapmap_move_point006[bullet_x+j][bullet_y]=spirittank_miss-j+1;     		//赋值
								}
							}
						}
					}
				}else if(playerbullet.getDiraction()==Spirit.DOWN) {		/***方向为下***/
					if(playerbullet.getX()%17<=1) {							//左判定
						for(int j=0;j<spirittank_miss;j++) {				//循环执行避让距离设置
							if(bullet_y+j<37) {								//确保判定合理性
								if(World.get(worldNumber).mapmap_move_point006[bullet_x][bullet_y+j]!=1) {		//保证不会覆盖墙体
									World.get(worldNumber).mapmap_move_point006[bullet_x][bullet_y+j]=spirittank_miss-j+1;		//赋值
								}
							}
						}
					}else if(playerbullet.getX()%17>=16) {
						for(int j=0;j<spirittank_miss;j++) {				//循环执行避让距离设置
							if(bullet_y+j<37&&bullet_x+1<48) {				//确保判定合理性
								if(World.get(worldNumber).mapmap_move_point006[bullet_x+1][bullet_y+j]!=1) {	//保证不会覆盖墙体
									World.get(worldNumber).mapmap_move_point006[bullet_x+1][bullet_y+j]=spirittank_miss-j+1;		//赋值
								}
							}
						}
					}else {													//右判定				
						for(int j=0;j<spirittank_miss;j++) {				//循环执行避让距离设置
							if(bullet_y+j<37&&bullet_x+1<48) {				//确保判定合理性
								if(World.get(worldNumber).mapmap_move_point006[bullet_x+1][bullet_y+j]!=1) {	//保证不会覆盖墙体
									World.get(worldNumber).mapmap_move_point006[bullet_x+1][bullet_y+j]=spirittank_miss-j+1;     //赋值
								}
							}
						}
						for(int j=0;j<spirittank_miss;j++) {				//循环执行避让距离设置
							if(bullet_y+j<37) {								//确保判定合理性
								if(World.get(worldNumber).mapmap_move_point006[bullet_x][bullet_y+j]!=1) {		//保证不会覆盖墙体
									World.get(worldNumber).mapmap_move_point006[bullet_x][bullet_y+j]=spirittank_miss-j+1;		//赋值
								}
							}
						}
					}
				}else if(playerbullet.getDiraction()==Spirit.LEFT) {		/***方向为左***/
					if(playerbullet.getY()%17<=1) {							//上判定
						for(int j=0;j<spirittank_miss;j++) {				//循环执行避让距离设置
							if(bullet_x-j>=0) {								//确保判定合理性
								if(World.get(worldNumber).mapmap_move_point006[bullet_x-j][bullet_y]!=1) {		//保证不会覆盖墙体
									World.get(worldNumber).mapmap_move_point006[bullet_x-j][bullet_y]=spirittank_miss-j+1;     	//赋值
								}
							}
						}
					}else if(playerbullet.getY()%17>=16) {
						for(int j=0;j<spirittank_miss;j++) {				//循环执行避让距离设置
							if(bullet_x-j>=0&&bullet_y+1<37) {				//确保判定合理性
								if(World.get(worldNumber).mapmap_move_point006[bullet_x-j][bullet_y+1]!=1) {		//保证不会覆盖墙体
									World.get(worldNumber).mapmap_move_point006[bullet_x-j][bullet_y+1]=spirittank_miss-j+1;     	//赋值
								}
							}
						}
					}else {													//下判定
						for(int j=0;j<spirittank_miss;j++) {				//循环执行避让距离设置
							if(bullet_x-j>=0&&bullet_y+1<37) {				//确保判定合理性
								if(World.get(worldNumber).mapmap_move_point006[bullet_x-j][bullet_y+1]!=1) {		//保证不会覆盖墙体
									World.get(worldNumber).mapmap_move_point006[bullet_x-j][bullet_y+1]=spirittank_miss-j+1;     	//赋值
								}
							}
						}
						for(int j=0;j<spirittank_miss;j++) {				//循环执行避让距离设置
							if(bullet_x-j>=0) {								//确保判定合理性
								if(World.get(worldNumber).mapmap_move_point006[bullet_x-j][bullet_y]!=1) {			//保证不会覆盖墙体
									World.get(worldNumber).mapmap_move_point006[bullet_x-j][bullet_y]=spirittank_miss-j+1;     		//赋值
								}
							}
						}
					}
				}
			}
			
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
						if(World.get(worldNumber).mapmap_move_point006[i][j]<-20&&
								lingpai[i][j]==0) {//判定为非墙，非0，非本轮运算
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
				//复原运算本轮运算判定
				for(int i=0;i<=47;i++) {
					for(int j=0;j<=36;j++) {
						lingpai[i][j]=0;
					}
				}
			}
			
		
			/*********
			//显示数字地图运算过程
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
			this.spirittank_distance002=World.get(worldNumber).move_distance;		//定制行走半径
			//实时运算玩家坦克梯度运算
			//凝结核部分
			int playertank_block_x=playertank.getX()/17;
			int playertank_block_y=playertank.getY()/17;
			int key=0;											//令牌，当未进行任何操作时，跳出循环
			//设置权值点位置
			//方法一：自爆型权值点
			/*
			if(World.get(worldNumber).mapmap_move_point001[playertank_block_x][playertank_block_y]!=1) {
				World.get(worldNumber).mapmap_move_point001[playertank_block_x][playertank_block_y]=-spirittank_see;//设置最低梯度
			}
			if(World.get(worldNumber).mapmap_move_point001[playertank_block_x+1][playertank_block_y]!=1) {
				World.get(worldNumber).mapmap_move_point001[playertank_block_x+1][playertank_block_y]=-spirittank_see;//设置最低梯度
			}
			if(World.get(worldNumber).mapmap_move_point001[playertank_block_x][playertank_block_y+1]!=1) {
				World.get(worldNumber).mapmap_move_point001[playertank_block_x][playertank_block_y+1]=-spirittank_see;//设置最低梯度
			}
			if(World.get(worldNumber).mapmap_move_point001[playertank_block_x+1][playertank_block_y+1]!=1) {
				World.get(worldNumber).mapmap_move_point001[playertank_block_x+1][playertank_block_y+1]=-spirittank_see;//设置最低梯度
			}
			*/
			//方法二：距离把控权值点
			//衍生部分1：行动权值
			//权值点集中在下方
			if(playertank_block_y+spirittank_distance002<=36) {
				if(World.get(worldNumber).mapmap_move_point007[playertank_block_x][playertank_block_y+spirittank_distance002]!=1) {
					World.get(worldNumber).mapmap_move_point007[playertank_block_x][playertank_block_y+spirittank_distance002]=-spirittank_see;
				}
			}
			//权值部分2：避让玩家
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
			//权值部分3：避让子弹
			//非衍生部分
			for(int i=0;i<World.get(worldNumber).playerbullets.size();i++) {//避让设置
				Bullet playerbullet=World.get(worldNumber).playerbullets.get(i);
				int bullet_x=playerbullet.getX()/17;
				int bullet_y=playerbullet.getY()/17;
				if(playerbullet.getDiraction()==Spirit.UP) {				/***方向为上***/
					if(playerbullet.getX()%17<=1) {							//左判定
						for(int j=0;j<spirittank_miss;j++) {				//循环执行避让距离设置
							if(bullet_y-j>=0) {								//确保判定合理性
								if(World.get(worldNumber).mapmap_move_point007[bullet_x][bullet_y-j]!=1) {		//保证不会覆盖墙体
									World.get(worldNumber).mapmap_move_point007[bullet_x][bullet_y-j]=spirittank_miss-j+1;		//赋值
								}
							}
						}
					}else if(playerbullet.getX()%17>=16) {
						for(int j=0;j<spirittank_miss;j++) {				//循环执行避让距离设置
							if(bullet_y-j>=0&&bullet_x+1<48) {				//确保判定合理性
								if(World.get(worldNumber).mapmap_move_point007[bullet_x+1][bullet_y-j]!=1) {	//保证不会覆盖墙体
									World.get(worldNumber).mapmap_move_point007[bullet_x+1][bullet_y-j]=spirittank_miss-j+1;		//赋值
								}
							}
						}
					}else {													//右判定				
						for(int j=0;j<spirittank_miss;j++) {				//循环执行避让距离设置
							if(bullet_y-j>=0&&bullet_x+1<48) {				//确保判定合理性
								if(World.get(worldNumber).mapmap_move_point007[bullet_x+1][bullet_y-j]!=1) {	//保证不会覆盖墙体
									World.get(worldNumber).mapmap_move_point007[bullet_x+1][bullet_y-j]=spirittank_miss-j+1;     //赋值
								}
							}
						}
						for(int j=0;j<spirittank_miss;j++) {				//循环执行避让距离设置
							if(bullet_y-j>=0) {								//确保判定合理性
								if(World.get(worldNumber).mapmap_move_point007[bullet_x][bullet_y-j]!=1) {		//保证不会覆盖墙体
									World.get(worldNumber).mapmap_move_point007[bullet_x][bullet_y-j]=spirittank_miss-j+1;		//赋值
								}
							}
						}
					}
				}else if(playerbullet.getDiraction()==Spirit.RIGHT) {		/***方向为右***/
					if(playerbullet.getY()%17<=1) {							//上判定
						for(int j=0;j<spirittank_miss;j++) {				//循环执行避让距离设置
							if(bullet_x+j<48) {								//确保判定合理性
								if(World.get(worldNumber).mapmap_move_point007[bullet_x+j][bullet_y]!=1) {		//保证不会覆盖墙体
									World.get(worldNumber).mapmap_move_point007[bullet_x+j][bullet_y]=spirittank_miss-j+1;     	//赋值
								}
							}
						}
					}else if(playerbullet.getY()%17>=16) {
						for(int j=0;j<spirittank_miss;j++) {				//循环执行避让距离设置
							if(bullet_x+j<48&&bullet_y+1<37) {				//确保判定合理性
								if(World.get(worldNumber).mapmap_move_point007[bullet_x+j][bullet_y+1]!=1) {		//保证不会覆盖墙体
									World.get(worldNumber).mapmap_move_point007[bullet_x+j][bullet_y+1]=spirittank_miss-j+1;     	//赋值
								}
							}
						}
					}else {													//下判定
						for(int j=0;j<spirittank_miss;j++) {				//循环执行避让距离设置
							if(bullet_x+j<48&&bullet_y+1<37) {				//确保判定合理性
								if(World.get(worldNumber).mapmap_move_point007[bullet_x+j][bullet_y+1]!=1) {		//保证不会覆盖墙体
									World.get(worldNumber).mapmap_move_point007[bullet_x+j][bullet_y+1]=spirittank_miss-j+1;     	//赋值
								}
							}
						}
						for(int j=0;j<spirittank_miss;j++) {				//循环执行避让距离设置
							if(bullet_x+j<48) {								//确保判定合理性
								if(World.get(worldNumber).mapmap_move_point007[bullet_x+j][bullet_y]!=1) {			//保证不会覆盖墙体
									World.get(worldNumber).mapmap_move_point007[bullet_x+j][bullet_y]=spirittank_miss-j+1;     		//赋值
								}
							}
						}
					}
				}else if(playerbullet.getDiraction()==Spirit.DOWN) {		/***方向为下***/
					if(playerbullet.getX()%17<=1) {							//左判定
						for(int j=0;j<spirittank_miss;j++) {				//循环执行避让距离设置
							if(bullet_y+j<37) {								//确保判定合理性
								if(World.get(worldNumber).mapmap_move_point007[bullet_x][bullet_y+j]!=1) {		//保证不会覆盖墙体
									World.get(worldNumber).mapmap_move_point007[bullet_x][bullet_y+j]=spirittank_miss-j+1;		//赋值
								}
							}
						}
					}else if(playerbullet.getX()%17>=16) {
						for(int j=0;j<spirittank_miss;j++) {				//循环执行避让距离设置
							if(bullet_y+j<37&&bullet_x+1<48) {				//确保判定合理性
								if(World.get(worldNumber).mapmap_move_point007[bullet_x+1][bullet_y+j]!=1) {	//保证不会覆盖墙体
									World.get(worldNumber).mapmap_move_point007[bullet_x+1][bullet_y+j]=spirittank_miss-j+1;		//赋值
								}
							}
						}
					}else {													//右判定				
						for(int j=0;j<spirittank_miss;j++) {				//循环执行避让距离设置
							if(bullet_y+j<37&&bullet_x+1<48) {				//确保判定合理性
								if(World.get(worldNumber).mapmap_move_point007[bullet_x+1][bullet_y+j]!=1) {	//保证不会覆盖墙体
									World.get(worldNumber).mapmap_move_point007[bullet_x+1][bullet_y+j]=spirittank_miss-j+1;     //赋值
								}
							}
						}
						for(int j=0;j<spirittank_miss;j++) {				//循环执行避让距离设置
							if(bullet_y+j<37) {								//确保判定合理性
								if(World.get(worldNumber).mapmap_move_point007[bullet_x][bullet_y+j]!=1) {		//保证不会覆盖墙体
									World.get(worldNumber).mapmap_move_point007[bullet_x][bullet_y+j]=spirittank_miss-j+1;		//赋值
								}
							}
						}
					}
				}else if(playerbullet.getDiraction()==Spirit.LEFT) {		/***方向为左***/
					if(playerbullet.getY()%17<=1) {							//上判定
						for(int j=0;j<spirittank_miss;j++) {				//循环执行避让距离设置
							if(bullet_x-j>=0) {								//确保判定合理性
								if(World.get(worldNumber).mapmap_move_point007[bullet_x-j][bullet_y]!=1) {		//保证不会覆盖墙体
									World.get(worldNumber).mapmap_move_point007[bullet_x-j][bullet_y]=spirittank_miss-j+1;     	//赋值
								}
							}
						}
					}else if(playerbullet.getY()%17>=16) {
						for(int j=0;j<spirittank_miss;j++) {				//循环执行避让距离设置
							if(bullet_x-j>=0&&bullet_y+1<37) {				//确保判定合理性
								if(World.get(worldNumber).mapmap_move_point007[bullet_x-j][bullet_y+1]!=1) {		//保证不会覆盖墙体
									World.get(worldNumber).mapmap_move_point007[bullet_x-j][bullet_y+1]=spirittank_miss-j+1;     	//赋值
								}
							}
						}
					}else {													//下判定
						for(int j=0;j<spirittank_miss;j++) {				//循环执行避让距离设置
							if(bullet_x-j>=0&&bullet_y+1<37) {				//确保判定合理性
								if(World.get(worldNumber).mapmap_move_point007[bullet_x-j][bullet_y+1]!=1) {		//保证不会覆盖墙体
									World.get(worldNumber).mapmap_move_point007[bullet_x-j][bullet_y+1]=spirittank_miss-j+1;     	//赋值
								}
							}
						}
						for(int j=0;j<spirittank_miss;j++) {				//循环执行避让距离设置
							if(bullet_x-j>=0) {								//确保判定合理性
								if(World.get(worldNumber).mapmap_move_point007[bullet_x-j][bullet_y]!=1) {			//保证不会覆盖墙体
									World.get(worldNumber).mapmap_move_point007[bullet_x-j][bullet_y]=spirittank_miss-j+1;     		//赋值
								}
							}
						}
					}
				}
			}

			
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
						if(World.get(worldNumber).mapmap_move_point007[i][j]<-20&&
								lingpai[i][j]==0) {//判定为非墙，非0，非本轮运算
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
				//复原运算本轮运算判定
				for(int i=0;i<=47;i++) {
					for(int j=0;j<=36;j++) {
						lingpai[i][j]=0;
					}
				}
			}
			
			/*********
			//显示数字地图运算过程
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
			this.spirittank_distance002=World.get(worldNumber).move_distance;		//定制行走半径
			//实时运算玩家坦克梯度运算
			//凝结核部分
			int playertank_block_x=playertank.getX()/17;
			int playertank_block_y=playertank.getY()/17;
			int key=0;											//令牌，当未进行任何操作时，跳出循环
			//设置权值点位置
			//方法一：自爆型权值点
			/*
			if(World.get(worldNumber).mapmap_move_point001[playertank_block_x][playertank_block_y]!=1) {
				World.get(worldNumber).mapmap_move_point001[playertank_block_x][playertank_block_y]=-spirittank_see;//设置最低梯度
			}
			if(World.get(worldNumber).mapmap_move_point001[playertank_block_x+1][playertank_block_y]!=1) {
				World.get(worldNumber).mapmap_move_point001[playertank_block_x+1][playertank_block_y]=-spirittank_see;//设置最低梯度
			}
			if(World.get(worldNumber).mapmap_move_point001[playertank_block_x][playertank_block_y+1]!=1) {
				World.get(worldNumber).mapmap_move_point001[playertank_block_x][playertank_block_y+1]=-spirittank_see;//设置最低梯度
			}
			if(World.get(worldNumber).mapmap_move_point001[playertank_block_x+1][playertank_block_y+1]!=1) {
				World.get(worldNumber).mapmap_move_point001[playertank_block_x+1][playertank_block_y+1]=-spirittank_see;//设置最低梯度
			}
			*/
			//方法二：距离把控权值点
			//衍生部分1：行动权值
			//权值点集中在左下方
			if(playertank_block_x-spirittank_distance002>=0&&playertank_block_y+spirittank_distance002<=36) {
				if(World.get(worldNumber).mapmap_move_point008[playertank_block_x-spirittank_distance002][playertank_block_y+spirittank_distance002]!=1) {
					World.get(worldNumber).mapmap_move_point008[playertank_block_x-spirittank_distance002][playertank_block_y+spirittank_distance002]=-spirittank_see;
				}
			}
			//权值部分2：避让玩家
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
			//权值部分3：避让子弹
			//非衍生部分
			for(int i=0;i<World.get(worldNumber).playerbullets.size();i++) {//避让设置
				Bullet playerbullet=World.get(worldNumber).playerbullets.get(i);
				int bullet_x=playerbullet.getX()/17;
				int bullet_y=playerbullet.getY()/17;
				if(playerbullet.getDiraction()==Spirit.UP) {				/***方向为上***/
					if(playerbullet.getX()%17<=1) {							//左判定
						for(int j=0;j<spirittank_miss;j++) {				//循环执行避让距离设置
							if(bullet_y-j>=0) {								//确保判定合理性
								if(World.get(worldNumber).mapmap_move_point008[bullet_x][bullet_y-j]!=1) {		//保证不会覆盖墙体
									World.get(worldNumber).mapmap_move_point008[bullet_x][bullet_y-j]=spirittank_miss-j+1;		//赋值
								}
							}
						}
					}else if(playerbullet.getX()%17>=16) {
						for(int j=0;j<spirittank_miss;j++) {				//循环执行避让距离设置
							if(bullet_y-j>=0&&bullet_x+1<48) {				//确保判定合理性
								if(World.get(worldNumber).mapmap_move_point008[bullet_x+1][bullet_y-j]!=1) {	//保证不会覆盖墙体
									World.get(worldNumber).mapmap_move_point008[bullet_x+1][bullet_y-j]=spirittank_miss-j+1;		//赋值
								}
							}
						}
					}else {													//右判定				
						for(int j=0;j<spirittank_miss;j++) {				//循环执行避让距离设置
							if(bullet_y-j>=0&&bullet_x+1<48) {				//确保判定合理性
								if(World.get(worldNumber).mapmap_move_point008[bullet_x+1][bullet_y-j]!=1) {	//保证不会覆盖墙体
									World.get(worldNumber).mapmap_move_point008[bullet_x+1][bullet_y-j]=spirittank_miss-j+1;     //赋值
								}
							}
						}
						for(int j=0;j<spirittank_miss;j++) {				//循环执行避让距离设置
							if(bullet_y-j>=0) {								//确保判定合理性
								if(World.get(worldNumber).mapmap_move_point008[bullet_x][bullet_y-j]!=1) {		//保证不会覆盖墙体
									World.get(worldNumber).mapmap_move_point008[bullet_x][bullet_y-j]=spirittank_miss-j+1;		//赋值
								}
							}
						}
					}
				}else if(playerbullet.getDiraction()==Spirit.RIGHT) {		/***方向为右***/
					if(playerbullet.getY()%17<=1) {							//上判定
						for(int j=0;j<spirittank_miss;j++) {				//循环执行避让距离设置
							if(bullet_x+j<48) {								//确保判定合理性
								if(World.get(worldNumber).mapmap_move_point008[bullet_x+j][bullet_y]!=1) {		//保证不会覆盖墙体
									World.get(worldNumber).mapmap_move_point008[bullet_x+j][bullet_y]=spirittank_miss-j+1;     	//赋值
								}
							}
						}
					}else if(playerbullet.getY()%17>=16) {
						for(int j=0;j<spirittank_miss;j++) {				//循环执行避让距离设置
							if(bullet_x+j<48&&bullet_y+1<37) {				//确保判定合理性
								if(World.get(worldNumber).mapmap_move_point008[bullet_x+j][bullet_y+1]!=1) {		//保证不会覆盖墙体
									World.get(worldNumber).mapmap_move_point008[bullet_x+j][bullet_y+1]=spirittank_miss-j+1;     	//赋值
								}
							}
						}
					}else {													//下判定
						for(int j=0;j<spirittank_miss;j++) {				//循环执行避让距离设置
							if(bullet_x+j<48&&bullet_y+1<37) {				//确保判定合理性
								if(World.get(worldNumber).mapmap_move_point008[bullet_x+j][bullet_y+1]!=1) {		//保证不会覆盖墙体
									World.get(worldNumber).mapmap_move_point008[bullet_x+j][bullet_y+1]=spirittank_miss-j+1;     	//赋值
								}
							}
						}
						for(int j=0;j<spirittank_miss;j++) {				//循环执行避让距离设置
							if(bullet_x+j<48) {								//确保判定合理性
								if(World.get(worldNumber).mapmap_move_point008[bullet_x+j][bullet_y]!=1) {			//保证不会覆盖墙体
									World.get(worldNumber).mapmap_move_point008[bullet_x+j][bullet_y]=spirittank_miss-j+1;     		//赋值
								}
							}
						}
					}
				}else if(playerbullet.getDiraction()==Spirit.DOWN) {		/***方向为下***/
					if(playerbullet.getX()%17<=1) {							//左判定
						for(int j=0;j<spirittank_miss;j++) {				//循环执行避让距离设置
							if(bullet_y+j<37) {								//确保判定合理性
								if(World.get(worldNumber).mapmap_move_point008[bullet_x][bullet_y+j]!=1) {		//保证不会覆盖墙体
									World.get(worldNumber).mapmap_move_point008[bullet_x][bullet_y+j]=spirittank_miss-j+1;		//赋值
								}
							}
						}
					}else if(playerbullet.getX()%17>=16) {
						for(int j=0;j<spirittank_miss;j++) {				//循环执行避让距离设置
							if(bullet_y+j<37&&bullet_x+1<48) {				//确保判定合理性
								if(World.get(worldNumber).mapmap_move_point008[bullet_x+1][bullet_y+j]!=1) {	//保证不会覆盖墙体
									World.get(worldNumber).mapmap_move_point008[bullet_x+1][bullet_y+j]=spirittank_miss-j+1;		//赋值
								}
							}
						}
					}else {													//右判定				
						for(int j=0;j<spirittank_miss;j++) {				//循环执行避让距离设置
							if(bullet_y+j<37&&bullet_x+1<48) {				//确保判定合理性
								if(World.get(worldNumber).mapmap_move_point008[bullet_x+1][bullet_y+j]!=1) {	//保证不会覆盖墙体
									World.get(worldNumber).mapmap_move_point008[bullet_x+1][bullet_y+j]=spirittank_miss-j+1;     //赋值
								}
							}
						}
						for(int j=0;j<spirittank_miss;j++) {				//循环执行避让距离设置
							if(bullet_y+j<37) {								//确保判定合理性
								if(World.get(worldNumber).mapmap_move_point008[bullet_x][bullet_y+j]!=1) {		//保证不会覆盖墙体
									World.get(worldNumber).mapmap_move_point008[bullet_x][bullet_y+j]=spirittank_miss-j+1;		//赋值
								}
							}
						}
					}
				}else if(playerbullet.getDiraction()==Spirit.LEFT) {		/***方向为左***/
					if(playerbullet.getY()%17<=1) {							//上判定
						for(int j=0;j<spirittank_miss;j++) {				//循环执行避让距离设置
							if(bullet_x-j>=0) {								//确保判定合理性
								if(World.get(worldNumber).mapmap_move_point008[bullet_x-j][bullet_y]!=1) {		//保证不会覆盖墙体
									World.get(worldNumber).mapmap_move_point008[bullet_x-j][bullet_y]=spirittank_miss-j+1;     	//赋值
								}
							}
						}
					}else if(playerbullet.getY()%17>=16) {
						for(int j=0;j<spirittank_miss;j++) {				//循环执行避让距离设置
							if(bullet_x-j>=0&&bullet_y+1<37) {				//确保判定合理性
								if(World.get(worldNumber).mapmap_move_point008[bullet_x-j][bullet_y+1]!=1) {		//保证不会覆盖墙体
									World.get(worldNumber).mapmap_move_point008[bullet_x-j][bullet_y+1]=spirittank_miss-j+1;     	//赋值
								}
							}
						}
					}else {													//下判定
						for(int j=0;j<spirittank_miss;j++) {				//循环执行避让距离设置
							if(bullet_x-j>=0&&bullet_y+1<37) {				//确保判定合理性
								if(World.get(worldNumber).mapmap_move_point008[bullet_x-j][bullet_y+1]!=1) {		//保证不会覆盖墙体
									World.get(worldNumber).mapmap_move_point008[bullet_x-j][bullet_y+1]=spirittank_miss-j+1;     	//赋值
								}
							}
						}
						for(int j=0;j<spirittank_miss;j++) {				//循环执行避让距离设置
							if(bullet_x-j>=0) {								//确保判定合理性
								if(World.get(worldNumber).mapmap_move_point008[bullet_x-j][bullet_y]!=1) {			//保证不会覆盖墙体
									World.get(worldNumber).mapmap_move_point008[bullet_x-j][bullet_y]=spirittank_miss-j+1;     		//赋值
								}
							}
						}
					}
				}
			}

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
						if(World.get(worldNumber).mapmap_move_point008[i][j]<-20&&
								lingpai[i][j]==0) {//判定为非墙，非0，非本轮运算
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
				//复原运算本轮运算判定
				for(int i=0;i<=47;i++) {
					for(int j=0;j<=36;j++) {
						lingpai[i][j]=0;
					}
				}
			}
			
		
			/*********
			//显示数字地图运算过程
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
			this.spirittank_distance002=World.get(worldNumber).move_distance;		//定制行走半径
			//实时运算玩家坦克梯度运算
			//凝结核部分
			int playertank_block_x=playertank.getX()/17;
			int playertank_block_y=playertank.getY()/17;
			int key=0;											//令牌，当未进行任何操作时，跳出循环
			//设置权值点位置
			//方法一：自爆型权值点
			/*
			if(World.get(worldNumber).mapmap_move_point001[playertank_block_x][playertank_block_y]!=1) {
				World.get(worldNumber).mapmap_move_point001[playertank_block_x][playertank_block_y]=-spirittank_see;//设置最低梯度
			}
			if(World.get(worldNumber).mapmap_move_point001[playertank_block_x+1][playertank_block_y]!=1) {
				World.get(worldNumber).mapmap_move_point001[playertank_block_x+1][playertank_block_y]=-spirittank_see;//设置最低梯度
			}
			if(World.get(worldNumber).mapmap_move_point001[playertank_block_x][playertank_block_y+1]!=1) {
				World.get(worldNumber).mapmap_move_point001[playertank_block_x][playertank_block_y+1]=-spirittank_see;//设置最低梯度
			}
			if(World.get(worldNumber).mapmap_move_point001[playertank_block_x+1][playertank_block_y+1]!=1) {
				World.get(worldNumber).mapmap_move_point001[playertank_block_x+1][playertank_block_y+1]=-spirittank_see;//设置最低梯度
			}
			*/
			//方法二：距离把控权值点
			//衍生部分1：行动权值
			//权值点集中在左方
			if(playertank_block_x-spirittank_distance002>=0) {
				if(World.get(worldNumber).mapmap_move_point009[playertank_block_x-spirittank_distance002][playertank_block_y]!=1) {
					World.get(worldNumber).mapmap_move_point009[playertank_block_x-spirittank_distance002][playertank_block_y]=-spirittank_see;
				}
			}
			
			//权值部分2：避让玩家(波段-1至-20不参与衍生)
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
			//权值部分3：避让子弹(波段-1至-20不参与衍生)
			//非衍生部分
			for(int i=0;i<World.get(worldNumber).playerbullets.size();i++) {//避让设置
				Bullet playerbullet=World.get(worldNumber).playerbullets.get(i);
				int bullet_x=playerbullet.getX()/17;
				int bullet_y=playerbullet.getY()/17;
				if(playerbullet.getDiraction()==Spirit.UP) {				/***方向为上***/
					if(playerbullet.getX()%17<=1) {							//左判定
						for(int j=0;j<spirittank_miss;j++) {				//循环执行避让距离设置
							if(bullet_y-j>=0) {								//确保判定合理性
								if(World.get(worldNumber).mapmap_move_point009[bullet_x][bullet_y-j]!=1) {		//保证不会覆盖墙体
									World.get(worldNumber).mapmap_move_point009[bullet_x][bullet_y-j]=spirittank_miss-j+1;		//赋值
								}
							}
						}
					}else if(playerbullet.getX()%17>=16) {
						for(int j=0;j<spirittank_miss;j++) {				//循环执行避让距离设置
							if(bullet_y-j>=0&&bullet_x+1<48) {				//确保判定合理性
								if(World.get(worldNumber).mapmap_move_point009[bullet_x+1][bullet_y-j]!=1) {	//保证不会覆盖墙体
									World.get(worldNumber).mapmap_move_point009[bullet_x+1][bullet_y-j]=spirittank_miss-j+1;		//赋值
								}
							}
						}
					}else {													//右判定				
						for(int j=0;j<spirittank_miss;j++) {				//循环执行避让距离设置
							if(bullet_y-j>=0&&bullet_x+1<48) {				//确保判定合理性
								if(World.get(worldNumber).mapmap_move_point009[bullet_x+1][bullet_y-j]!=1) {	//保证不会覆盖墙体
									World.get(worldNumber).mapmap_move_point009[bullet_x+1][bullet_y-j]=spirittank_miss-j+1;     //赋值
								}
							}
						}
						for(int j=0;j<spirittank_miss;j++) {				//循环执行避让距离设置
							if(bullet_y-j>=0) {								//确保判定合理性
								if(World.get(worldNumber).mapmap_move_point009[bullet_x][bullet_y-j]!=1) {		//保证不会覆盖墙体
									World.get(worldNumber).mapmap_move_point009[bullet_x][bullet_y-j]=spirittank_miss-j+1;		//赋值
								}
							}
						}
					}
				}else if(playerbullet.getDiraction()==Spirit.RIGHT) {		/***方向为右***/
					if(playerbullet.getY()%17<=1) {							//上判定
						for(int j=0;j<spirittank_miss;j++) {				//循环执行避让距离设置
							if(bullet_x+j<48) {								//确保判定合理性
								if(World.get(worldNumber).mapmap_move_point009[bullet_x+j][bullet_y]!=1) {		//保证不会覆盖墙体
									World.get(worldNumber).mapmap_move_point009[bullet_x+j][bullet_y]=spirittank_miss-j+1;     	//赋值
								}
							}
						}
					}else if(playerbullet.getY()%17>=16) {
						for(int j=0;j<spirittank_miss;j++) {				//循环执行避让距离设置
							if(bullet_x+j<48&&bullet_y+1<37) {				//确保判定合理性
								if(World.get(worldNumber).mapmap_move_point009[bullet_x+j][bullet_y+1]!=1) {		//保证不会覆盖墙体
									World.get(worldNumber).mapmap_move_point009[bullet_x+j][bullet_y+1]=spirittank_miss-j+1;     	//赋值
								}
							}
						}
					}else {													//下判定
						for(int j=0;j<spirittank_miss;j++) {				//循环执行避让距离设置
							if(bullet_x+j<48&&bullet_y+1<37) {				//确保判定合理性
								if(World.get(worldNumber).mapmap_move_point009[bullet_x+j][bullet_y+1]!=1) {		//保证不会覆盖墙体
									World.get(worldNumber).mapmap_move_point009[bullet_x+j][bullet_y+1]=spirittank_miss-j+1;     	//赋值
								}
							}
						}
						for(int j=0;j<spirittank_miss;j++) {				//循环执行避让距离设置
							if(bullet_x+j<48) {								//确保判定合理性
								if(World.get(worldNumber).mapmap_move_point009[bullet_x+j][bullet_y]!=1) {			//保证不会覆盖墙体
									World.get(worldNumber).mapmap_move_point009[bullet_x+j][bullet_y]=spirittank_miss-j+1;     		//赋值
								}
							}
						}
					}
				}else if(playerbullet.getDiraction()==Spirit.DOWN) {		/***方向为下***/
					if(playerbullet.getX()%17<=1) {							//左判定
						for(int j=0;j<spirittank_miss;j++) {				//循环执行避让距离设置
							if(bullet_y+j<37) {								//确保判定合理性
								if(World.get(worldNumber).mapmap_move_point009[bullet_x][bullet_y+j]!=1) {		//保证不会覆盖墙体
									World.get(worldNumber).mapmap_move_point009[bullet_x][bullet_y+j]=spirittank_miss-j+1;		//赋值
								}
							}
						}
					}else if(playerbullet.getX()%17>=16) {
						for(int j=0;j<spirittank_miss;j++) {				//循环执行避让距离设置
							if(bullet_y+j<37&&bullet_x+1<48) {				//确保判定合理性
								if(World.get(worldNumber).mapmap_move_point009[bullet_x+1][bullet_y+j]!=1) {	//保证不会覆盖墙体
									World.get(worldNumber).mapmap_move_point009[bullet_x+1][bullet_y+j]=spirittank_miss-j+1;		//赋值
								}
							}
						}
					}else {													//右判定				
						for(int j=0;j<spirittank_miss;j++) {				//循环执行避让距离设置
							if(bullet_y+j<37&&bullet_x+1<48) {				//确保判定合理性
								if(World.get(worldNumber).mapmap_move_point009[bullet_x+1][bullet_y+j]!=1) {	//保证不会覆盖墙体
									World.get(worldNumber).mapmap_move_point009[bullet_x+1][bullet_y+j]=spirittank_miss-j+1;     //赋值
								}
							}
						}
						for(int j=0;j<spirittank_miss;j++) {				//循环执行避让距离设置
							if(bullet_y+j<37) {								//确保判定合理性
								if(World.get(worldNumber).mapmap_move_point009[bullet_x][bullet_y+j]!=1) {		//保证不会覆盖墙体
									World.get(worldNumber).mapmap_move_point009[bullet_x][bullet_y+j]=spirittank_miss-j+1;		//赋值
								}
							}
						}
					}
				}else if(playerbullet.getDiraction()==Spirit.LEFT) {		/***方向为左***/
					if(playerbullet.getY()%17<=1) {							//上判定
						for(int j=0;j<spirittank_miss;j++) {				//循环执行避让距离设置
							if(bullet_x-j>=0) {								//确保判定合理性
								if(World.get(worldNumber).mapmap_move_point009[bullet_x-j][bullet_y]!=1) {			//保证不会覆盖墙体
									World.get(worldNumber).mapmap_move_point009[bullet_x-j][bullet_y]=spirittank_miss-j+1;     	//赋值
								}
							}
						}
					}else if(playerbullet.getY()%17>=16) {
						for(int j=0;j<spirittank_miss;j++) {				//循环执行避让距离设置
							if(bullet_x-j>=0&&bullet_y+1<37) {				//确保判定合理性
								if(World.get(worldNumber).mapmap_move_point009[bullet_x-j][bullet_y+1]!=1) {		//保证不会覆盖墙体
									World.get(worldNumber).mapmap_move_point009[bullet_x-j][bullet_y+1]=spirittank_miss-j+1;     	//赋值
								}
							}
						}
					}else {													//下判定
						for(int j=0;j<spirittank_miss;j++) {				//循环执行避让距离设置
							if(bullet_x-j>=0&&bullet_y+1<37) {				//确保判定合理性
								if(World.get(worldNumber).mapmap_move_point009[bullet_x-j][bullet_y+1]!=1) {		//保证不会覆盖墙体
									World.get(worldNumber).mapmap_move_point009[bullet_x-j][bullet_y+1]=spirittank_miss-j+1;     	//赋值
								}
							}
						}
						for(int j=0;j<spirittank_miss;j++) {				//循环执行避让距离设置
							if(bullet_x-j>=0) {								//确保判定合理性
								if(World.get(worldNumber).mapmap_move_point009[bullet_x-j][bullet_y]!=1) {			//保证不会覆盖墙体
									World.get(worldNumber).mapmap_move_point009[bullet_x-j][bullet_y]=spirittank_miss-j+1;     		//赋值
								}
							}
						}
					}
				}
			}
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
						if(World.get(worldNumber).mapmap_move_point009[i][j]<-20&&
								lingpai[i][j]==0) {//判定为非墙，非0，非本轮运算
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
				//复原运算本轮运算判定
				for(int i=0;i<=47;i++) {
					for(int j=0;j<=36;j++) {
						lingpai[i][j]=0;
					}
				}
			}


			
			
			/*********
			//显示数字地图运算过程
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