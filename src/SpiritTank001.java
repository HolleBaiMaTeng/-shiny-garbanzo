import java.util.Random;

public class SpiritTank001 extends SpiritTank {

	private Map map;
	public int move_state=0;		//0-到达1-行进
	private int[] mb= {0,0};
	public static final int SIPIRITTANK_V=2;
	private int fangxiang=0;		//横竖行进的方向变换控制
	
	
	public SpiritTank001(int x, int y,Map map) {
		super(x, y);
		this.map=map;
		mb[0]=x/17;//初始化目标点
		mb[1]=y/17;//初始化目标点
		this.setVelocity(SIPIRITTANK_V);
		this.setCategory(44);
	}

	
		//计算函数
		public void caculateDate001(int[][] map) {
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
			if(x%17>3) {
				i1++;
			}
			if(y%17>3) {
				j1++;
			}
			
			if(mol(mb[0]*17,x)>34||mol(mb[1]*17,y)>34) {
				System.out.println("rrrrrr");
				System.out.println(mb[0]*17+"  "+mb[1]*17);
				System.out.println(x+"  "+y);
				System.out.println("Diraction"+this.getDiraction());
			}
			
			
			if(map[mb[0]][mb[1]]==0) {
				this.setVelocity(0);  							//原地静止模式，方向不变待命状态
			}else{
				this.setVelocity(SIPIRITTANK_V);  				//恢复启动
				if(move_state==1) { 	//前进状态(判断是否到达目标点)(同时调整向目标点的朝向)
					if(x<=mb[0]*17+2&&x>=mb[0]*17-2&&y<=mb[1]*17+2&&y>=mb[1]*17-2) {
						move_state=0;	//到达状态
					}
				}
				if(move_state==0) {		//到达状态（重新选取下一个目标点,直接对目标点进行操作）
					//更换选取目标点
					int lingpai01=0;							
					int lingpai02=0;
					//暂存原目标点
					int[] o1=new int[2];
					o1[0]=mb[0];							//目标点储存不变
					o1[1]=mb[1];							//目标点储存不变
					int mb_mol=1;
					if(mb[1]-1>=0) {
						if(map[o1[0]][o1[1]]>map[o1[0]][o1[1]-1]) {
							mb[0]=o1[0];					//重置目标点
							mb[1]=o1[1];					//重置目标点
							mb[1]--;						//修改目标点
							mb_mol=map[mb[0]][mb[1]];		//暂存目标点值
							lingpai02=1;					//lingpai02保证不会重复移动目标点
							move_state=1;					//更换前进模式
							this.setDiraction(UP); 			//更换方向
						}
					}
					if(mb[1]+1<=36) {
						if(map[o1[0]][o1[1]]>map[o1[0]][o1[1]+1]) {
							if(map[o1[0]][o1[1]+1]<mb_mol) {
								mb[0]=o1[0];					//重置目标点
								mb[1]=o1[1];					//重置目标点
								mb[1]++;						//修改目标点
								mb_mol=map[mb[0]][mb[1]];		//暂存目标点值
								lingpai02=1;					//lingpai02保证不会重复移动目标点
								move_state=1;					//更换前进模式
								this.setDiraction(DOWN); 		//更换方向
							}
						}
					}
					if(mb[0]-1>=0) {
						if(map[o1[0]][o1[1]]>map[o1[0]-1][o1[1]]) {
							if(map[o1[0]-1][o1[1]]<mb_mol) {
								mb[0]=o1[0];					//重置目标点
								mb[1]=o1[1];					//重置目标点
								mb[0]--;						//修改目标点
								mb_mol=map[mb[0]][mb[1]];		//暂存目标点值
								lingpai02=1;					//lingpai02保证不会重复移动目标点
								move_state=1;					//更换前进模式
								this.setDiraction(LEFT); 		//更换方向
							}
						}
					}
					if(mb[0]+1<=47) {
						if(map[o1[0]][o1[1]]>map[o1[0]+1][o1[1]]) {
							if(map[o1[0]+1][o1[1]]<mb_mol) {
								mb[0]=o1[0];					//重置目标点
								mb[1]=o1[1];					//重置目标点
								mb[0]++;						//修改目标点
								mb_mol=map[mb[0]][mb[1]];		//暂存目标点值
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
			System.out.println(map[mb[0]][mb[1]]);
			/**/
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
