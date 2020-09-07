import java.awt.Graphics;
import java.awt.Image;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;
import java.util.StringTokenizer;

public class Map {

	//map 的init函数
	public ArrayList<Bullet> spiritbullets=new ArrayList<Bullet>();							//精灵炮弹集合
	public ArrayList<SpiritTank> spirittank000s=new ArrayList<SpiritTank>();				//精灵坦克集合
	public ArrayList<SpiritTank001> spirittank001s=new ArrayList<SpiritTank001>();			//精灵坦克001集合
	public ArrayList<SpiritTank002> spirittank002s=new ArrayList<SpiritTank002>();			//精灵坦克002集合
	public ArrayList<Bullet> playerbullets=new ArrayList<Bullet>();							//玩家炮弹集合
	public ArrayList<int[][]> playerbullet_xy=new ArrayList<int[][]>();						//玩家炮弹坐标集合

	public int spirittankDestroyed=0;					//本关中击毁坦克数量
	
	
	//精灵坦克数量
	public int[] spirittankCount= {10,4,3};				//不同种类的精灵坦克不同次数的出现数量
	public int all_count=0;								//不同种类的精灵坦克总的出现数量
	
	//生成坦克速度
	private int tank_maketime000=30;				//000型坦克
	private int tank_maketime001=30;				//001型坦克
	private int tank_maketime002=60;				//002型坦克
	
	//玩家坦克生命
	public int[] playertankLife= {30,30};   		//玩家坦克初始生命[p1,p2]
	
	public int[][] mapmap;				  		//数字化地图映射//由mapmap快速的对各个地图块进行访问
	public int[][] mapmap_move_point001;	    //001型坦克数字化点制地图，用于规划实际移动路线
	
	public int[][] mapmap_move_point002;	    //002型数字化点制地图，用于规划实际移动路线//注：002型坦克使用
	public int[][] mapmap_move_point003;	    //003型数字化点制地图，用于规划实际移动路线//2-上3-右4-下5-左
	public int[][] mapmap_move_point004;	    //004型数字化点制地图，用于规划实际移动路线
	public int[][] mapmap_move_point005;	    //005型数字化点制地图，用于规划实际移动路线
	public int[][] mapmap_move_point006;	    //006型数字化点制地图，用于规划实际移动路线
	public int[][] mapmap_move_point007;	    //007型数字化点制地图，用于规划实际移动路线
	public int[][] mapmap_move_point008;	    //008型数字化点制地图，用于规划实际移动路线
	public int[][] mapmap_move_point009;	    //009型数字化点制地图，用于规划实际移动路线	
	
	//定制坦克数据设置
	public  int FIRE_TIME002=3;					//设置002坦克开火间隔时间（攻击前摇）
	public  int FIRE_SLEEP_TIME002=0;			//设置002坦克开火停歇时间（攻击后摇）
	public  int move_distance=4;				//设置002坦克行走半径
	
	public static final int KONG=-100;			//数字地图图块对应值
	public static final int SPIRIT1=0;  //实体	//数字地图图块对应值
	public static final int SPIRIT0=100;//虚体	//数字地图图块对应值
	public static final int WATER=10;			//数字地图图块对应值
	public static final int BRICK=20;			//数字地图图块对应值
	public static final int STONE=30;			//数字地图图块对应值
	public static final int GRAESS=50;			//数字地图图块对应值
	
	//玩家坦克生命g-s
	public int getPlayertankLife(int px) {		//得到p1-0或p2-1的生命值
		return playertankLife[px];
	}
	
	//精灵坦克计数
	public int getSpirittankNumber(int px) {	//得到x型的出现次数
		return spirittankCount[px];
	}

	private int[] mapPos= {0,0};				//在世界地图上的位置
	
	//窗体共有46*35坐标点
	public ArrayList<BlockBrick>Bricks=new ArrayList<BlockBrick>();
	public ArrayList<BlockStone>Stones=new ArrayList<BlockStone>();
	public ArrayList<BlockGrass>Grasses=new ArrayList<BlockGrass>();
	public ArrayList<BlockWater>Waters=new ArrayList<BlockWater>();
	public ArrayList<BlockSpirit>Spirits=new ArrayList<BlockSpirit>();
	
	
	
	
	
	public Map(int mapPos_x,int mapPos_y) {//构造世界位置
		mapPos[0]=mapPos_x;
		mapPos[1]=mapPos_y;
		
		//初始化mapamp
		mapmap=new int[47][36];
		for(int i=0;i<=46;i++) {
			for(int j=0;j<=35;j++) {
				mapmap[i][j]=KONG;
			}
		}
		
		//初始化all_count
		for(int i=0;i<spirittankCount.length;i++) {
			all_count=all_count+spirittankCount[i];
		}
		
		//初始化mapmap_move_point001
		mapmap_move_point001=new int[48][37];
		for(int i=0;i<=47;i++) {
			for(int j=0;j<=36;j++) {
				mapmap_move_point001[i][j]=0;
			}
		}
		//初始化mapmap_move_point002
		mapmap_move_point002=new int[48][37];
		for(int i=0;i<=47;i++) {
			for(int j=0;j<=36;j++) {
				mapmap_move_point002[i][j]=0;
			}
		}		
		//初始化mapmap_move_point003
		mapmap_move_point003=new int[48][37];
		for(int i=0;i<=47;i++) {
			for(int j=0;j<=36;j++) {
				mapmap_move_point003[i][j]=0;
			}
		}
		//初始化mapmap_move_point004
		mapmap_move_point004=new int[48][37];
		for(int i=0;i<=47;i++) {
			for(int j=0;j<=36;j++) {
				mapmap_move_point004[i][j]=0;
			}
		}
		//初始化mapmap_move_point005
		mapmap_move_point005=new int[48][37];
		for(int i=0;i<=47;i++) {
			for(int j=0;j<=36;j++) {
				mapmap_move_point005[i][j]=0;
			}
		}
		//初始化mapmap_move_point006
		mapmap_move_point006=new int[48][37];
		for(int i=0;i<=47;i++) {
			for(int j=0;j<=36;j++) {
				mapmap_move_point006[i][j]=0;
			}
		}
		//初始化mapmap_move_point007
		mapmap_move_point007=new int[48][37];
		for(int i=0;i<=47;i++) {
			for(int j=0;j<=36;j++) {
				mapmap_move_point007[i][j]=0;
			}
		}		
		//初始化mapmap_move_point008
		mapmap_move_point008=new int[48][37];
		for(int i=0;i<=47;i++) {
			for(int j=0;j<=36;j++) {
				mapmap_move_point008[i][j]=0;
			}
		}		
		//初始化mapmap_move_point009
		mapmap_move_point009=new int[48][37];
		for(int i=0;i<=47;i++) {
			for(int j=0;j<=36;j++) {
				mapmap_move_point009[i][j]=0;
			}
		}
	}
	
	
	
	/**********函数层************/
	public int getMapX() {
		return mapPos[0];
	}
	public int getMapY() {
		return mapPos[1];
	}
	
	
	public void readData(String file)throws IOException{//将文件之接转换放入Map中
		//读取新的地图前将原有数据删除
		Bricks.clear();
		Waters.clear();
		Stones.clear();
		Grasses.clear();
		Spirits.clear();											
		//读文件
		FileReader fr=new FileReader(file);
		//System.out.println(file);									//汇报读入情况
		BufferedReader br=new BufferedReader(fr);
		String str1,line[];
		String str2;
		while((str1=br.readLine())!=null) {//文件末尾不允许打空格！！！
			//读入数据用数字化地图统一管理
			//在更改数据时，同时调整数字化地图
			//sp:精灵坦克工厂直接生成
			StringTokenizer str=new StringTokenizer(str1,"=");
			str1=str.nextToken();					//标识名称数据
			str2=str.nextToken();					//数字数据
			if(str1.compareTo("spirittankCount")==0) {
				line=str2.split(",");
				if(line.length>=1) {//保证安全
					spirittankCount[0]=Integer.parseInt(line[0]);
				}
				if(line.length>=2) {//保证安全
					spirittankCount[1]=Integer.parseInt(line[1]);
				}
				if(line.length>=3) {//保证安全
					spirittankCount[2]=Integer.parseInt(line[2]);
				}
				if(line.length>=4) {//保证安全
					this.FIRE_TIME002=Integer.parseInt(line[3]);
				}
				if(line.length>=5) {//保证安全
					this.FIRE_SLEEP_TIME002=Integer.parseInt(line[4]);
				}
				if(line.length>=6) {//保证安全
					this.move_distance=Integer.parseInt(line[5]);
				}
			}else if(str1.compareTo("playertankLife")==0) {
				line=str2.split(",");
				playertankLife[0]=Integer.parseInt(line[0]);
				playertankLife[1]=Integer.parseInt(line[1]);
				playertankLife[0]= 10000;										   //定义玩家生命值（测试版）
				playertankLife[1]= 10000;										   //定义玩家生命值（测试版）
			}else if(str1.compareTo("spirittankPos")==0) {
				line=str2.split(",");
				mapmap[Integer.parseInt(line[0])][Integer.parseInt(line[1])]=SPIRIT1;					//mapmap数字地图映射*4
				mapmap[Integer.parseInt(line[0])+1][Integer.parseInt(line[1])]=SPIRIT0;
				mapmap[Integer.parseInt(line[0])][Integer.parseInt(line[1])+1]=SPIRIT0;
				mapmap[Integer.parseInt(line[0])+1][Integer.parseInt(line[1])+1]=SPIRIT0;
			}else if(str1.compareTo("BlockBricks")==0) {
				line=str2.split(",");
				mapmap[Integer.parseInt(line[0])][Integer.parseInt(line[1])]=BRICK;//mapmap数字地图映射
			}else if(str1.compareTo("BlockWater")==0) {
				line=str2.split(",");
				//新建水块
				mapmap[Integer.parseInt(line[0])][Integer.parseInt(line[1])]=WATER;//mapmap数字地图映射
			}else if(str1.compareTo("BlockStone")==0) {
				line=str2.split(",");
				//新建石头块
				mapmap[Integer.parseInt(line[0])][Integer.parseInt(line[1])]=STONE;//mapmap数字地图映射
			}else if(str1.compareTo("BlockGrass")==0) {
				line=str2.split(",");
				//新建草块
				mapmap[Integer.parseInt(line[0])][Integer.parseInt(line[1])]=GRAESS;//mapmap数字地图映射
			}
			//while end//
		}
		br.close();
		fr.close();
		this.copy_map_gradient(mapmap);
		//重新计算all_count
		all_count=0;										//重新给all_count赋值
		for(int i=0;i<spirittankCount.length;i++) {
			all_count=all_count+spirittankCount[i];
		}
		//readData end//
	}
	
	public void copy_map_gradient(int[][] mapmap) {
		//重新初始化this.mapmap_move_point001
		this.mapmap_move_point001=new int[48][37];
		for(int i=0;i<=47;i++) {
			for(int j=0;j<=36;j++) {
				this.mapmap_move_point001[i][j]=0;
			}
		}
		for(int i=0;i<=46;i++) {
			for(int j=0;j<=35;j++) { 
				if(mapmap[i][j]==Map.STONE||
					mapmap[i][j]==Map.WATER||
					mapmap[i][j]==Map.BRICK) {
					this.mapmap_move_point001[i][j]=1;
					this.mapmap_move_point001[i][j+1]=1;
					this.mapmap_move_point001[i+1][j]=1;
					this.mapmap_move_point001[i+1][j+1]=1;
				}
				//边界规范化
				if(i==0) {
					this.mapmap_move_point001[i][j]=1;
				}
				if(i==46) {
					this.mapmap_move_point001[i+1][j]=1;
				}
				if(j==35) {//地图最下方不可走
					this.mapmap_move_point001[i][j]=1;
					this.mapmap_move_point001[i][j+1]=1;
				}
				if(j==0) {
					this.mapmap_move_point001[i][j]=1;
				}
			}
		}
		//重新初始化this.mapmap_move_point002
		this.mapmap_move_point002=new int[48][37];
		for(int i=0;i<=47;i++) {
			for(int j=0;j<=36;j++) {
				this.mapmap_move_point002[i][j]=0;
			}
		}
		for(int i=0;i<=46;i++) {
			for(int j=0;j<=35;j++) { 
				if(mapmap[i][j]==Map.STONE||
					mapmap[i][j]==Map.WATER||
					mapmap[i][j]==Map.BRICK) {
					this.mapmap_move_point002[i][j]=1;
					this.mapmap_move_point002[i][j+1]=1;
					this.mapmap_move_point002[i+1][j]=1;
					this.mapmap_move_point002[i+1][j+1]=1;
				}
				//边界规范化
				if(i==0) {
					this.mapmap_move_point002[i][j]=1;
				}
				if(i==46) {
					this.mapmap_move_point002[i+1][j]=1;
				}
				if(j==35) {//地图最下方不可走
					this.mapmap_move_point002[i][j]=1;
					this.mapmap_move_point002[i][j+1]=1;
				}
				if(j==0) {
					this.mapmap_move_point002[i][j]=1;
				}
			}
		}	
		
		//重新初始化this.mapmap_move_point003
		this.mapmap_move_point003=new int[48][37];
		for(int i=0;i<=47;i++) {
			for(int j=0;j<=36;j++) {
				this.mapmap_move_point003[i][j]=0;
			}
		}
		for(int i=0;i<=46;i++) {
			for(int j=0;j<=35;j++) { 
				if(mapmap[i][j]==Map.STONE||
					mapmap[i][j]==Map.WATER||
					mapmap[i][j]==Map.BRICK) {
					this.mapmap_move_point003[i][j]=1;
					this.mapmap_move_point003[i][j+1]=1;
					this.mapmap_move_point003[i+1][j]=1;
					this.mapmap_move_point003[i+1][j+1]=1;
				}
				//边界规范化
				if(i==0) {
					this.mapmap_move_point003[i][j]=1;
				}
				if(i==46) {
					this.mapmap_move_point003[i+1][j]=1;
				}
				if(j==35) {//地图最下方不可走
					this.mapmap_move_point003[i][j]=1;
					this.mapmap_move_point003[i][j+1]=1;
				}
				if(j==0) {
					this.mapmap_move_point003[i][j]=1;
				}
			}
		}	
		
		//重新初始化this.mapmap_move_point004
		this.mapmap_move_point004=new int[48][37];
		for(int i=0;i<=47;i++) {
			for(int j=0;j<=36;j++) {
				this.mapmap_move_point004[i][j]=0;
			}
		}
		for(int i=0;i<=46;i++) {
			for(int j=0;j<=35;j++) { 
				if(mapmap[i][j]==Map.STONE||
					mapmap[i][j]==Map.WATER||
					mapmap[i][j]==Map.BRICK) {
					this.mapmap_move_point004[i][j]=1;
					this.mapmap_move_point004[i][j+1]=1;
					this.mapmap_move_point004[i+1][j]=1;
					this.mapmap_move_point004[i+1][j+1]=1;
				}
				//边界规范化
				if(i==0) {
					this.mapmap_move_point004[i][j]=1;
				}
				if(i==46) {
					this.mapmap_move_point004[i+1][j]=1;
				}
				if(j==35) {//地图最下方不可走
					this.mapmap_move_point004[i][j]=1;
					this.mapmap_move_point004[i][j+1]=1;
				}
				if(j==0) {
					this.mapmap_move_point004[i][j]=1;
				}
			}
		}	
		
		//重新初始化this.mapmap_move_point005
		this.mapmap_move_point005=new int[48][37];
		for(int i=0;i<=47;i++) {
			for(int j=0;j<=36;j++) {
				this.mapmap_move_point005[i][j]=0;
			}
		}
		for(int i=0;i<=46;i++) {
			for(int j=0;j<=35;j++) { 
				if(mapmap[i][j]==Map.STONE||
					mapmap[i][j]==Map.WATER||
					mapmap[i][j]==Map.BRICK) {
					this.mapmap_move_point005[i][j]=1;
					this.mapmap_move_point005[i][j+1]=1;
					this.mapmap_move_point005[i+1][j]=1;
					this.mapmap_move_point005[i+1][j+1]=1;
				}
				//边界规范化
				if(i==0) {
					this.mapmap_move_point005[i][j]=1;
				}
				if(i==46) {
					this.mapmap_move_point005[i+1][j]=1;
				}
				if(j==35) {//地图最下方不可走
					this.mapmap_move_point005[i][j]=1;
					this.mapmap_move_point005[i][j+1]=1;
				}
				if(j==0) {
					this.mapmap_move_point005[i][j]=1;
				}
			}
		}
		
		//重新初始化this.mapmap_move_point006
		this.mapmap_move_point006=new int[48][37];
		for(int i=0;i<=47;i++) {
			for(int j=0;j<=36;j++) {
				this.mapmap_move_point006[i][j]=0;
			}
		}
		for(int i=0;i<=46;i++) {
			for(int j=0;j<=35;j++) { 
				if(mapmap[i][j]==Map.STONE||
					mapmap[i][j]==Map.WATER||
					mapmap[i][j]==Map.BRICK) {
					this.mapmap_move_point006[i][j]=1;
					this.mapmap_move_point006[i][j+1]=1;
					this.mapmap_move_point006[i+1][j]=1;
					this.mapmap_move_point006[i+1][j+1]=1;
				}
				//边界规范化
				if(i==0) {
					this.mapmap_move_point006[i][j]=1;
				}
				if(i==46) {
					this.mapmap_move_point006[i+1][j]=1;
				}
				if(j==35) {//地图最下方不可走
					this.mapmap_move_point006[i][j]=1;
					this.mapmap_move_point006[i][j+1]=1;
				}
				if(j==0) {
					this.mapmap_move_point006[i][j]=1;
				}
			}
		}		
		
		//重新初始化this.mapmap_move_point007
		this.mapmap_move_point007=new int[48][37];
		for(int i=0;i<=47;i++) {
			for(int j=0;j<=36;j++) {
				this.mapmap_move_point007[i][j]=0;
			}
		}
		for(int i=0;i<=46;i++) {
			for(int j=0;j<=35;j++) { 
				if(mapmap[i][j]==Map.STONE||
					mapmap[i][j]==Map.WATER||
					mapmap[i][j]==Map.BRICK) {
					this.mapmap_move_point007[i][j]=1;
					this.mapmap_move_point007[i][j+1]=1;
					this.mapmap_move_point007[i+1][j]=1;
					this.mapmap_move_point007[i+1][j+1]=1;
				}
				//边界规范化
				if(i==0) {
					this.mapmap_move_point007[i][j]=1;
				}
				if(i==46) {
					this.mapmap_move_point007[i+1][j]=1;
				}
				if(j==35) {//地图最下方不可走
					this.mapmap_move_point007[i][j]=1;
					this.mapmap_move_point007[i][j+1]=1;
				}
				if(j==0) {
					this.mapmap_move_point007[i][j]=1;
				}
			}
		}		
		
		//重新初始化this.mapmap_move_point008
		this.mapmap_move_point008=new int[48][37];
		for(int i=0;i<=47;i++) {
			for(int j=0;j<=36;j++) {
				this.mapmap_move_point008[i][j]=0;
			}
		}
		for(int i=0;i<=46;i++) {
			for(int j=0;j<=35;j++) { 
				if(mapmap[i][j]==Map.STONE||
					mapmap[i][j]==Map.WATER||
					mapmap[i][j]==Map.BRICK) {
					this.mapmap_move_point008[i][j]=1;
					this.mapmap_move_point008[i][j+1]=1;
					this.mapmap_move_point008[i+1][j]=1;
					this.mapmap_move_point008[i+1][j+1]=1;
				}
				//边界规范化
				if(i==0) {
					this.mapmap_move_point008[i][j]=1;
				}
				if(i==46) {
					this.mapmap_move_point008[i+1][j]=1;
				}
				if(j==35) {//地图最下方不可走
					this.mapmap_move_point008[i][j]=1;
					this.mapmap_move_point008[i][j+1]=1;
				}
				if(j==0) {
					this.mapmap_move_point008[i][j]=1;
				}
			}
		}	
		
		//重新初始化this.mapmap_move_point009
		this.mapmap_move_point009=new int[48][37];
		for(int i=0;i<=47;i++) {
			for(int j=0;j<=36;j++) {
				this.mapmap_move_point009[i][j]=0;
			}
		}
		for(int i=0;i<=46;i++) {
			for(int j=0;j<=35;j++) { 
				if(mapmap[i][j]==Map.STONE||
					mapmap[i][j]==Map.WATER||
					mapmap[i][j]==Map.BRICK) {
					this.mapmap_move_point009[i][j]=1;
					this.mapmap_move_point009[i][j+1]=1;
					this.mapmap_move_point009[i+1][j]=1;
					this.mapmap_move_point009[i+1][j+1]=1;
				}
				//边界规范化
				if(i==0) {
					this.mapmap_move_point009[i][j]=1;
				}
				if(i==46) {
					this.mapmap_move_point009[i+1][j]=1;
				}
				if(j==35) {//地图最下方不可走
					this.mapmap_move_point009[i][j]=1;
					this.mapmap_move_point009[i][j+1]=1;
				}
				if(j==0) {
					this.mapmap_move_point009[i][j]=1;
				}
			}
		}		
	}
	
	public void drawSpirits(Graphics g) {
		for(int i=Spirits.size()-1;i>=0;i--) {
			BlockSpirit blockspirits=Spirits.get(i);
			blockspirits.draw(g);
		}
	}
	
	public void drawSpirits_0(Graphics g) {
		for(int i=Spirits.size()-1;i>=0;i--) {
			BlockSpirit blockspirits=Spirits.get(i);
			blockspirits.draw_0(g);
		}
	}
	
	public void drawWaters(Graphics g) {
		for(int i=Waters.size()-1;i>=0;i--) {
			BlockWater blockwater=Waters.get(i);
			blockwater.draw(g);
		}
	}
	
	public void drawWaters_0(Graphics g) {
		for(int i=Waters.size()-1;i>=0;i--) {
			BlockWater blockwater=Waters.get(i);
			blockwater.draw_0(g);
		}
	}
	
	public void drawStones(Graphics g) {
		for(int i=Stones.size()-1;i>=0;i--) {
			BlockStone blockstone=Stones.get(i);
			blockstone.draw(g);
		}
	}
	
	public void drawStones_0(Graphics g) {
		for(int i=Stones.size()-1;i>=0;i--) {
			BlockStone blockstone=Stones.get(i);
			blockstone.draw_0(g);
		}
	}
	
	public void drawBricks(Graphics g) {
		for(int i=Bricks.size()-1;i>=0;i--) {
			BlockBrick blockbrick=Bricks.get(i);
			blockbrick.draw(g,blockbrick);
		}
	}
	
	public void drawBricks_0(Graphics g) {
		for(int i=Bricks.size()-1;i>=0;i--) {
			BlockBrick blockbrick=Bricks.get(i);
			blockbrick.draw_0(g,blockbrick);
		}
	}
	
	public void drawGrasses(Graphics g) {
		for(int i=Grasses.size()-1;i>=0;i--) {
			BlockGrass blockgrass=Grasses.get(i);
			blockgrass.draw(g);
		}
	}
	
	public void drawGrasses_0(Graphics g) {
		for(int i=Grasses.size()-1;i>=0;i--) {
			BlockGrass blockgrass=Grasses.get(i);
			blockgrass.draw_0(g);
		}
	}
	
	//遍历检测 块与坦克的碰撞
	public boolean isCollide(Tank tank) {
		boolean result=false;
		for(int i=Bricks.size()-1;i>=0;i--) {
			BlockBrick blockbrick = Bricks.get(i);
			if(tank.isCollide_BlockBrick(blockbrick)) {
				result=true;
				break;
			}
		}
		
		//防止重复判断
		if(result==false) {
			for(int i=Stones.size()-1;i>=0;i--) {
				BlockStone blockstone =Stones.get(i);
				if(tank.isCollide(blockstone)){
					result=true;
					break;
				}
			}
		}
		
		//防止重复判断
		if(result==false) {
			for(int i=Waters.size()-1;i>=0;i--) {
				BlockWater blockwater =Waters.get(i);
				if(tank.isCollide(blockwater)){
					result=true;
					break;
				}
			}
		}
		return result;
	}
	
	
	//遍历检测 块与炮弹的碰撞
	public boolean isCollide(Bullet bullet) {
		boolean result=false;
		for(int i=Bricks.size()-1;i>=0;i--) {
			//多态性质的判断
			BlockBrick blockbrick = Bricks.get(i);
			if(blockbrick.getState_0()==0&&
			   blockbrick.getState_1()==0&&
			   blockbrick.getState_2()==0&&
			   blockbrick.getState_3()==0) {
				mapmap[blockbrick.mapx][blockbrick.mapy]=KONG;			//数字坐标更改
				Bricks.remove(i);
				 continue;
			}
			if(bullet.isCollide(blockbrick)) {
				//若碰撞
				result=true;
				//相对位置分析
				if(bullet.getX()<=blockbrick.getX()&&bullet.getY()<=blockbrick.getY()) {
					if(blockbrick.getState_0()==0) {
						mapmap[blockbrick.mapx][blockbrick.mapy]=KONG;			//数字坐标更改
						 Bricks.remove(i);
						 continue;
					}else {
						blockbrick.setState_0(0);
					}
				}
				if(bullet.getX()>=blockbrick.getX()&&bullet.getY()<=blockbrick.getY()) {
					if(blockbrick.getState_1()==0) {
						mapmap[blockbrick.mapx][blockbrick.mapy]=KONG;			//数字坐标更改
						 Bricks.remove(i);
						 continue;
					}else {
						blockbrick.setState_1(0);
					}
				}
				if(bullet.getX()>=blockbrick.getX()&&bullet.getY()>=blockbrick.getY()) {
					if(blockbrick.getState_2()==0) {
						mapmap[blockbrick.mapx][blockbrick.mapy]=KONG;			//数字坐标更改
						 Bricks.remove(i);
						 continue;
					}else {
						blockbrick.setState_2(0);
					}
				}
				if(bullet.getX()<=blockbrick.getX()&&bullet.getY()>=blockbrick.getY()) {
					if(blockbrick.getState_3()==0) {
						mapmap[blockbrick.mapx][blockbrick.mapy]=KONG;			//数字坐标更改
						 Bricks.remove(i);
						 continue;
					}else {
						blockbrick.setState_3(0);
					}
				}
			}
		}
		//防止重复判断
		if(result==false) {
			for(int i=Stones.size()-1;i>=0;i--) {
				BlockStone blockstone =Stones.get(i);
				if(bullet.isCollide(blockstone)){
					result=true;
					break;
				}
			}
		}
		//更改对应的移动数字地图
		this.copy_map_gradient(mapmap);
		
		return result;
	}
	
	//随机生成机灵坦克000函数
	public SpiritTank CreateSpirittank000() {
		SpiritTank spirittank=null;
		Random random=new Random();
		//对零型精灵坦克分析：
		if(spirittankCount[0]>0) {
			if(random.nextInt(this.tank_maketime000)==0){				//精灵坦克工厂数量越多生成速度越快
				if(Spirits.size()!=0) {
					int pos =random.nextInt(Spirits.size());
					spirittank=new SpiritTank(Spirits.get(pos).getX(),
											Spirits.get(pos).getY());			//转化成mapmap数字地图格式
					spirittankCount[0]--;
				}
			}
		}
		return spirittank;
	}
	
	//随机生成机灵坦克001函数
	public SpiritTank001 CreateSpirittank001() {
		SpiritTank001 spirittank=null;
		Random random=new Random();
		if(spirittankCount[1]>0) {
			if(random.nextInt(this.tank_maketime001)==0){				//精灵坦克工厂数量越多生成速度越快
				if(Spirits.size()!=0) {
					int pos =random.nextInt(Spirits.size());
					spirittank=new SpiritTank001(Spirits.get(pos).getX(),
											Spirits.get(pos).getY(),this);		//转化成mapmap数字地图格式
					spirittankCount[1]--;
				}
			}
		}
		return spirittank;
	}
	
	//随机生成机灵坦克002函数
	public SpiritTank002 CreateSpirittank002() {
		SpiritTank002 spirittank=null;
		Random random=new Random();
		if(spirittankCount[2]>0) {
			if(random.nextInt(this.tank_maketime002)==0){				//精灵坦克工厂数量越多生成速度越快
				if(Spirits.size()!=0) {
					int pos =random.nextInt(Spirits.size());
					spirittank=new SpiritTank002(Spirits.get(pos).getX(),
											Spirits.get(pos).getY(),this,this.FIRE_TIME002,this.FIRE_SLEEP_TIME002);		//转化成mapmap数字地图格式
					spirittankCount[2]--;
				}
			}
		}
		return spirittank;
	}
	
	
	//初始化玩家坦克重生动画
	public void initPCartoonData(Cartoon cartoon,int x,int y) {
		cartoon.setX(x);		//在原有位置播放重生动画
		cartoon.setY(y);		//在原有位置播放重生动画
	}
	
	

	
	
	
	
	
	
	
	
	
	
	
	
	
	//class end//
}















