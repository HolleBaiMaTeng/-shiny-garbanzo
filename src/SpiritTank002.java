import java.util.Random;

public class SpiritTank002 extends SpiritTank {

	private Map map;
	public static final int SIPIRITTANK_V=2;
	
	private int fangxiang=0;		//横竖行进的方向变换控制
	public int mubiaoState00=0;		//控制目标状态地图选择： 2-9左上角起始，顺时针旋转
	//public int mubiaoState01=0;	//控制移动地图的切换 0-可切换 1锁死
	private int[][] map00;			//行动地图，暂存
	public int move_state=0;		//0-到达1-行进2-炮击模式
	public int move_state01=0;		//0-到达1-行进2-炮击模式(副本)
	private int move_fx;			//暂存原行驶方向
	private int[] mb= {0,0};		//目标点储存
	
	
	
	//坦克数值设置：
	public  int FIRE_TIME=3;				//设置坦克开火间隔时间
	private int firetime=0;					//记录坦克开火时间
	public  int FIRE_SLEEP_TIME=0;			//僵直时间设定
	private int fire_sleep_time=0;			//记录僵直时间
	public int distance=4;					//坦克行驶半径
	private int jingdu=12;					//坦克开火精度
	
	public SpiritTank002(int x, int y,Map map,int fire_time,int fire_sleep_time) {
		super(x, y);
		this.map=map;
		mb[0]=x/17;				//初始化目标点
		mb[1]=y/17;				//初始化目标点
		this.setVelocity(SIPIRITTANK_V);
		this.setCategory(15);
		map00=new int[48][37];	//行动地图初始化
		
		Random random=new Random();
		mubiaoState00=random.nextInt(8)+2;
		
		//初始化目标点：
		for(int p=0;p<8;p++) {
			mubiaoState00=(mubiaoState00-1)%8+2;		//顺时针选取下一点
			//变换并检验是否可行
			if(mubiaoState00==2) {
				map00=map.mapmap_move_point002;
			}else if(mubiaoState00==3) {
				map00=map.mapmap_move_point003;
			}else if(mubiaoState00==4) {
				map00=map.mapmap_move_point004;
			}else if(mubiaoState00==5) {
				map00=map.mapmap_move_point005;
			}else if(mubiaoState00==6) {
				map00=map.mapmap_move_point006;
			}else if(mubiaoState00==7) {
				map00=map.mapmap_move_point007;
			}else if(mubiaoState00==8) {
				map00=map.mapmap_move_point008;
			}else if(mubiaoState00==9){
				map00=map.mapmap_move_point009;
			}
			if(map00[mb[0]][mb[1]]==0) {
				continue;
			}else {
				break;
			}
		}
		
		
		//火炮激发时间赋值
		this.FIRE_TIME=fire_time;
		//火炮僵直时间赋值
		this.FIRE_SLEEP_TIME=fire_sleep_time;
	}

	
		//计算函数
		public void caculateDate001(int[][] map002,int[][] map003, int[][] map004,int[][] map005,
				int[][] map006,int[][] map007, int[][] map008,int[][] map009,
				int playertank_x,int playertank_y) {
			
			if(fire_sleep_time!=0) {
				fire_sleep_time--;						//若处于僵直，则设定跳过所有判定
			}else {
				if(move_state==2) {
					move_state=move_state01;			//炮火模式复原
					this.setDiraction(move_fx);
				}
				
				if(firetime!=0) {
					firetime--;
				}
				
				if(mubiaoState00==2) {
					map00=map.mapmap_move_point002;
				}else if(mubiaoState00==3) {
					map00=map.mapmap_move_point003;
				}else if(mubiaoState00==4) {
					map00=map.mapmap_move_point004;
				}else if(mubiaoState00==5) {
					map00=map.mapmap_move_point005;
				}else if(mubiaoState00==6) {
					map00=map.mapmap_move_point006;
				}else if(mubiaoState00==7) {
					map00=map.mapmap_move_point007;
				}else if(mubiaoState00==8) {
					map00=map.mapmap_move_point008;
				}else if(mubiaoState00==9){
					map00=map.mapmap_move_point009;
				}
				
				//帧数运算
				frameState++;
				if(frameState==aliveFrameCount) {
					frameState=0;
				}
				
				Random random=new Random();
				int x=this.getX();
				int y=this.getY();
				int i1=x/17;
				int j1=y/17;
				if(x%17>=3) {
					i1++;
				}
				if(y%17>=3) {
					j1++;
				}
				
				//判断其是否为炮火模式
				
				if((this.mol(this.getX(),playertank_x)<=this.jingdu||this.mol(this.getY(),playertank_y)<=this.jingdu)
						&&firetime==0
						&&map00[i1][j1]<-GamePanel.spirittank_miss) {			//到达精度值，火炮处于间歇状态，没有避让风险时，进入炮火模式
					move_state01=this.move_state;								//暂存已有模式
					move_state=2;												//调整为炮火模式
					firetime=this.FIRE_TIME;									//计入开火间隔
					this.setVelocity(0);										//静止发炮
					move_fx=this.getDiraction();								//储存方向
					fire_sleep_time=FIRE_SLEEP_TIME;							//进入僵直
				}
				
				
				if(mol(mb[0]*17,x)>34||mol(mb[1]*17,y)>34) {
					System.out.println("rrrrrr");
					System.out.println(mb[0]*17+"  "+mb[1]*17);
					System.out.println(x+"  "+y);
					System.out.println("Diraction"+this.getDiraction());
				}
				
				if(move_state==2) {
					//判定方向
					if(this.mol(this.getX(),playertank_x)<=15) {
						if(this.getY()>=playertank_y) {
							this.setDiraction(UP);
						}else {
							this.setDiraction(DOWN);
						}
					}else {
						if(this.getX()>=playertank_x) {
							this.setDiraction(LEFT);
						}else {
							this.setDiraction(RIGHT);
						}
					}
					//调整方向
				}else {
					this.setVelocity(SIPIRITTANK_V);  							//恢复启动
					if(move_state==1) { 										//前进状态(判断是否到达目标点)(同时调整向目标点的朝向)
						if(x<=mb[0]*17+2&&x>=mb[0]*17-2&&y<=mb[1]*17+2&&y>=mb[1]*17-2) {
							move_state=0;										//到达状态
							
							if(map00[mb[0]][mb[1]]==-GamePanel.spirittank_see) {//负责到达极值点时的转换
								for(int p=0;p<8;p++) {
									mubiaoState00=(mubiaoState00-1)%8+2;		//顺时针选取下一点
									//变换并检验是否可行
									if(mubiaoState00==2) {
										map00=map.mapmap_move_point002;
									}else if(mubiaoState00==3) {
										map00=map.mapmap_move_point003;
									}else if(mubiaoState00==4) {
										map00=map.mapmap_move_point004;
									}else if(mubiaoState00==5) {
										map00=map.mapmap_move_point005;
									}else if(mubiaoState00==6) {
										map00=map.mapmap_move_point006;
									}else if(mubiaoState00==7) {
										map00=map.mapmap_move_point007;
									}else if(mubiaoState00==8) {
										map00=map.mapmap_move_point008;
									}else if(mubiaoState00==9){
										map00=map.mapmap_move_point009;
									}
									if(map00[mb[0]][mb[1]]==0) {
										continue;
									}else {
										break;
									}
								}
								this.setVelocity(0);  							//暂时静止
							}else if(map00[mb[0]][mb[1]]==0) {					//负责正常到达状态时的转换
								for(int p=0;p<8;p++) {
									mubiaoState00=(mubiaoState00-1)%8+2;		//顺时针选取下一点
									//变换并检验是否可行
									if(mubiaoState00==2) {
										map00=map.mapmap_move_point002;
									}else if(mubiaoState00==3) {
										map00=map.mapmap_move_point003;
									}else if(mubiaoState00==4) {
										map00=map.mapmap_move_point004;
									}else if(mubiaoState00==5) {
										map00=map.mapmap_move_point005;
									}else if(mubiaoState00==6) {
										map00=map.mapmap_move_point006;
									}else if(mubiaoState00==7) {
										map00=map.mapmap_move_point007;
									}else if(mubiaoState00==8) {
										map00=map.mapmap_move_point008;
									}else if(mubiaoState00==9){
										map00=map.mapmap_move_point009;
									}
									if(map00[mb[0]][mb[1]]==0) {
										continue;
									}else {
										break;
									}
								}
								this.setVelocity(0);  							//暂时静止
							}
						}
					}
					if(move_state==0) {											//到达状态（重新选取下一个目标点,直接对目标点进行操作）
							
						//更换选取目标点
						int lingpai01=0;							
						int lingpai02=0;
						//暂存原目标点
						int[] o1=new int[2];
						o1[0]=mb[0];							//目标点储存不变
						o1[1]=mb[1];							//目标点储存不变
						int mb_mol=1;
						if(mb[1]-1>=0) {
							if(map00[o1[0]][o1[1]]>map00[o1[0]][o1[1]-1]&&map00[o1[0]][o1[1]-1]!=1) {
								mb[0]=o1[0];					//重置目标点
								mb[1]=o1[1];					//重置目标点
								mb[1]--;						//修改目标点
								mb_mol=map00[mb[0]][mb[1]];		//暂存目标点值
								lingpai02=1;					//lingpai02保证不会重复移动目标点
								move_state=1;					//更换前进模式
								this.setDiraction(UP); 			//更换方向
							}
						}
						if(mb[1]+1<=36) {
							if(map00[o1[0]][o1[1]]>map00[o1[0]][o1[1]+1]&&map00[o1[0]][o1[1]+1]!=1) {
								if(map00[o1[0]][o1[1]+1]<mb_mol) {
									mb[0]=o1[0];					//重置目标点
									mb[1]=o1[1];					//重置目标点
									mb[1]++;						//修改目标点
									mb_mol=map00[mb[0]][mb[1]];		//暂存目标点值
									lingpai02=1;					//lingpai02保证不会重复移动目标点
									move_state=1;					//更换前进模式
									this.setDiraction(DOWN); 		//更换方向
								}
							}
						}
						if(mb[0]-1>=0) {
							if(map00[o1[0]][o1[1]]>map00[o1[0]-1][o1[1]]&&map00[o1[0]-1][o1[1]]!=1) {
								if(map00[o1[0]-1][o1[1]]<mb_mol) {
									mb[0]=o1[0];					//重置目标点
									mb[1]=o1[1];					//重置目标点
									mb[0]--;						//修改目标点
									mb_mol=map00[mb[0]][mb[1]];		//暂存目标点值
									lingpai02=1;					//lingpai02保证不会重复移动目标点
									move_state=1;					//更换前进模式
									this.setDiraction(LEFT); 		//更换方向
								}
							}
						}
						if(mb[0]+1<=47) {
							if(map00[o1[0]][o1[1]]>map00[o1[0]+1][o1[1]]&&map00[o1[0]+1][o1[1]]!=1) {
								if(map00[o1[0]+1][o1[1]]<mb_mol) {
									mb[0]=o1[0];					//重置目标点
									mb[1]=o1[1];					//重置目标点
									mb[0]++;						//修改目标点
									mb_mol=map00[mb[0]][mb[1]];		//暂存目标点值
									lingpai02=1;					//lingpai02保证不会重复移动目标点
									move_state=1;					//更换前进模式
									this.setDiraction(RIGHT); 		//更换方向
								}
							}
						}
						
						if(lingpai02==0) { 							//未进行操作判定
							this.setVelocity(0); 					//若未进行操作，则设置暂停
							move_state=0; 							//行动状态为到达
							mb[0]=o1[0];							//还原目标点
							mb[1]=o1[1];
						}
					}
				}
				/*测试用显示数据*
				System.out.println("move_state"+move_state);
				System.out.println(mb[0]+" "+mb[1]);
				System.out.println(map00[mb[0]][mb[1]]);
				/**/
			}
		}

		//重写开火函数
		public Bullet fire() {
			Bullet bullet=null;
			int bulletX;
			int bulletY;
			bulletX=getX();
			bulletY=getY();
			switch(getDiraction()) {
			case Spirit.UP:
				bulletY=bulletY-getWidth()/2;
				break;
			case Spirit.RIGHT:
				bulletX=bulletX+getWidth()/2;
				break;
			case Spirit.DOWN:
				bulletY=bulletY+getWidth()/2;
				break;
			case Spirit.LEFT:
				bulletX=bulletX-getWidth()/2;
				break;
			}
			bullet=new Bullet(bulletX,bulletY,getDiraction());
			if(this.move_state==2&&this.fire_sleep_time==0) {
				return bullet;
			}else {
				return null;
			}
		}
		
	public int mol(int a,int b) {
		int result=0;
		if(a>=b) {
			result= a-b;
		}
		if(a<b) {
			result= b-a;
		}
		return result;
	}
	
	public int min(int a,int b) {
		if(a>b) {
			return b;
		}else {
			return a;
		}
	}
	
	public int max(int a,int b) {
		if(a>b) {
			return a;
		}else {
			return b;
		}
	}
	
	
}
