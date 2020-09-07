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

	//map ��init����
	public ArrayList<Bullet> spiritbullets=new ArrayList<Bullet>();							//�����ڵ�����
	public ArrayList<SpiritTank> spirittank000s=new ArrayList<SpiritTank>();				//����̹�˼���
	public ArrayList<SpiritTank001> spirittank001s=new ArrayList<SpiritTank001>();			//����̹��001����
	public ArrayList<SpiritTank002> spirittank002s=new ArrayList<SpiritTank002>();			//����̹��002����
	public ArrayList<Bullet> playerbullets=new ArrayList<Bullet>();							//����ڵ�����
	public ArrayList<int[][]> playerbullet_xy=new ArrayList<int[][]>();						//����ڵ����꼯��

	public int spirittankDestroyed=0;					//�����л���̹������
	
	
	//����̹������
	public int[] spirittankCount= {10,4,3};				//��ͬ����ľ���̹�˲�ͬ�����ĳ�������
	public int all_count=0;								//��ͬ����ľ���̹���ܵĳ�������
	
	//����̹���ٶ�
	private int tank_maketime000=30;				//000��̹��
	private int tank_maketime001=30;				//001��̹��
	private int tank_maketime002=60;				//002��̹��
	
	//���̹������
	public int[] playertankLife= {30,30};   		//���̹�˳�ʼ����[p1,p2]
	
	public int[][] mapmap;				  		//���ֻ���ͼӳ��//��mapmap���ٵĶԸ�����ͼ����з���
	public int[][] mapmap_move_point001;	    //001��̹�����ֻ����Ƶ�ͼ�����ڹ滮ʵ���ƶ�·��
	
	public int[][] mapmap_move_point002;	    //002�����ֻ����Ƶ�ͼ�����ڹ滮ʵ���ƶ�·��//ע��002��̹��ʹ��
	public int[][] mapmap_move_point003;	    //003�����ֻ����Ƶ�ͼ�����ڹ滮ʵ���ƶ�·��//2-��3-��4-��5-��
	public int[][] mapmap_move_point004;	    //004�����ֻ����Ƶ�ͼ�����ڹ滮ʵ���ƶ�·��
	public int[][] mapmap_move_point005;	    //005�����ֻ����Ƶ�ͼ�����ڹ滮ʵ���ƶ�·��
	public int[][] mapmap_move_point006;	    //006�����ֻ����Ƶ�ͼ�����ڹ滮ʵ���ƶ�·��
	public int[][] mapmap_move_point007;	    //007�����ֻ����Ƶ�ͼ�����ڹ滮ʵ���ƶ�·��
	public int[][] mapmap_move_point008;	    //008�����ֻ����Ƶ�ͼ�����ڹ滮ʵ���ƶ�·��
	public int[][] mapmap_move_point009;	    //009�����ֻ����Ƶ�ͼ�����ڹ滮ʵ���ƶ�·��	
	
	//����̹����������
	public  int FIRE_TIME002=3;					//����002̹�˿�����ʱ�䣨����ǰҡ��
	public  int FIRE_SLEEP_TIME002=0;			//����002̹�˿���ͣЪʱ�䣨������ҡ��
	public  int move_distance=4;				//����002̹�����߰뾶
	
	public static final int KONG=-100;			//���ֵ�ͼͼ���Ӧֵ
	public static final int SPIRIT1=0;  //ʵ��	//���ֵ�ͼͼ���Ӧֵ
	public static final int SPIRIT0=100;//����	//���ֵ�ͼͼ���Ӧֵ
	public static final int WATER=10;			//���ֵ�ͼͼ���Ӧֵ
	public static final int BRICK=20;			//���ֵ�ͼͼ���Ӧֵ
	public static final int STONE=30;			//���ֵ�ͼͼ���Ӧֵ
	public static final int GRAESS=50;			//���ֵ�ͼͼ���Ӧֵ
	
	//���̹������g-s
	public int getPlayertankLife(int px) {		//�õ�p1-0��p2-1������ֵ
		return playertankLife[px];
	}
	
	//����̹�˼���
	public int getSpirittankNumber(int px) {	//�õ�x�͵ĳ��ִ���
		return spirittankCount[px];
	}

	private int[] mapPos= {0,0};				//�������ͼ�ϵ�λ��
	
	//���干��46*35�����
	public ArrayList<BlockBrick>Bricks=new ArrayList<BlockBrick>();
	public ArrayList<BlockStone>Stones=new ArrayList<BlockStone>();
	public ArrayList<BlockGrass>Grasses=new ArrayList<BlockGrass>();
	public ArrayList<BlockWater>Waters=new ArrayList<BlockWater>();
	public ArrayList<BlockSpirit>Spirits=new ArrayList<BlockSpirit>();
	
	
	
	
	
	public Map(int mapPos_x,int mapPos_y) {//��������λ��
		mapPos[0]=mapPos_x;
		mapPos[1]=mapPos_y;
		
		//��ʼ��mapamp
		mapmap=new int[47][36];
		for(int i=0;i<=46;i++) {
			for(int j=0;j<=35;j++) {
				mapmap[i][j]=KONG;
			}
		}
		
		//��ʼ��all_count
		for(int i=0;i<spirittankCount.length;i++) {
			all_count=all_count+spirittankCount[i];
		}
		
		//��ʼ��mapmap_move_point001
		mapmap_move_point001=new int[48][37];
		for(int i=0;i<=47;i++) {
			for(int j=0;j<=36;j++) {
				mapmap_move_point001[i][j]=0;
			}
		}
		//��ʼ��mapmap_move_point002
		mapmap_move_point002=new int[48][37];
		for(int i=0;i<=47;i++) {
			for(int j=0;j<=36;j++) {
				mapmap_move_point002[i][j]=0;
			}
		}		
		//��ʼ��mapmap_move_point003
		mapmap_move_point003=new int[48][37];
		for(int i=0;i<=47;i++) {
			for(int j=0;j<=36;j++) {
				mapmap_move_point003[i][j]=0;
			}
		}
		//��ʼ��mapmap_move_point004
		mapmap_move_point004=new int[48][37];
		for(int i=0;i<=47;i++) {
			for(int j=0;j<=36;j++) {
				mapmap_move_point004[i][j]=0;
			}
		}
		//��ʼ��mapmap_move_point005
		mapmap_move_point005=new int[48][37];
		for(int i=0;i<=47;i++) {
			for(int j=0;j<=36;j++) {
				mapmap_move_point005[i][j]=0;
			}
		}
		//��ʼ��mapmap_move_point006
		mapmap_move_point006=new int[48][37];
		for(int i=0;i<=47;i++) {
			for(int j=0;j<=36;j++) {
				mapmap_move_point006[i][j]=0;
			}
		}
		//��ʼ��mapmap_move_point007
		mapmap_move_point007=new int[48][37];
		for(int i=0;i<=47;i++) {
			for(int j=0;j<=36;j++) {
				mapmap_move_point007[i][j]=0;
			}
		}		
		//��ʼ��mapmap_move_point008
		mapmap_move_point008=new int[48][37];
		for(int i=0;i<=47;i++) {
			for(int j=0;j<=36;j++) {
				mapmap_move_point008[i][j]=0;
			}
		}		
		//��ʼ��mapmap_move_point009
		mapmap_move_point009=new int[48][37];
		for(int i=0;i<=47;i++) {
			for(int j=0;j<=36;j++) {
				mapmap_move_point009[i][j]=0;
			}
		}
	}
	
	
	
	/**********������************/
	public int getMapX() {
		return mapPos[0];
	}
	public int getMapY() {
		return mapPos[1];
	}
	
	
	public void readData(String file)throws IOException{//���ļ�֮��ת������Map��
		//��ȡ�µĵ�ͼǰ��ԭ������ɾ��
		Bricks.clear();
		Waters.clear();
		Stones.clear();
		Grasses.clear();
		Spirits.clear();											
		//���ļ�
		FileReader fr=new FileReader(file);
		//System.out.println(file);									//�㱨�������
		BufferedReader br=new BufferedReader(fr);
		String str1,line[];
		String str2;
		while((str1=br.readLine())!=null) {//�ļ�ĩβ�������ո񣡣���
			//�������������ֻ���ͼͳһ����
			//�ڸ�������ʱ��ͬʱ�������ֻ���ͼ
			//sp:����̹�˹���ֱ������
			StringTokenizer str=new StringTokenizer(str1,"=");
			str1=str.nextToken();					//��ʶ��������
			str2=str.nextToken();					//��������
			if(str1.compareTo("spirittankCount")==0) {
				line=str2.split(",");
				if(line.length>=1) {//��֤��ȫ
					spirittankCount[0]=Integer.parseInt(line[0]);
				}
				if(line.length>=2) {//��֤��ȫ
					spirittankCount[1]=Integer.parseInt(line[1]);
				}
				if(line.length>=3) {//��֤��ȫ
					spirittankCount[2]=Integer.parseInt(line[2]);
				}
				if(line.length>=4) {//��֤��ȫ
					this.FIRE_TIME002=Integer.parseInt(line[3]);
				}
				if(line.length>=5) {//��֤��ȫ
					this.FIRE_SLEEP_TIME002=Integer.parseInt(line[4]);
				}
				if(line.length>=6) {//��֤��ȫ
					this.move_distance=Integer.parseInt(line[5]);
				}
			}else if(str1.compareTo("playertankLife")==0) {
				line=str2.split(",");
				playertankLife[0]=Integer.parseInt(line[0]);
				playertankLife[1]=Integer.parseInt(line[1]);
				playertankLife[0]= 10000;										   //�����������ֵ�����԰棩
				playertankLife[1]= 10000;										   //�����������ֵ�����԰棩
			}else if(str1.compareTo("spirittankPos")==0) {
				line=str2.split(",");
				mapmap[Integer.parseInt(line[0])][Integer.parseInt(line[1])]=SPIRIT1;					//mapmap���ֵ�ͼӳ��*4
				mapmap[Integer.parseInt(line[0])+1][Integer.parseInt(line[1])]=SPIRIT0;
				mapmap[Integer.parseInt(line[0])][Integer.parseInt(line[1])+1]=SPIRIT0;
				mapmap[Integer.parseInt(line[0])+1][Integer.parseInt(line[1])+1]=SPIRIT0;
			}else if(str1.compareTo("BlockBricks")==0) {
				line=str2.split(",");
				mapmap[Integer.parseInt(line[0])][Integer.parseInt(line[1])]=BRICK;//mapmap���ֵ�ͼӳ��
			}else if(str1.compareTo("BlockWater")==0) {
				line=str2.split(",");
				//�½�ˮ��
				mapmap[Integer.parseInt(line[0])][Integer.parseInt(line[1])]=WATER;//mapmap���ֵ�ͼӳ��
			}else if(str1.compareTo("BlockStone")==0) {
				line=str2.split(",");
				//�½�ʯͷ��
				mapmap[Integer.parseInt(line[0])][Integer.parseInt(line[1])]=STONE;//mapmap���ֵ�ͼӳ��
			}else if(str1.compareTo("BlockGrass")==0) {
				line=str2.split(",");
				//�½��ݿ�
				mapmap[Integer.parseInt(line[0])][Integer.parseInt(line[1])]=GRAESS;//mapmap���ֵ�ͼӳ��
			}
			//while end//
		}
		br.close();
		fr.close();
		this.copy_map_gradient(mapmap);
		//���¼���all_count
		all_count=0;										//���¸�all_count��ֵ
		for(int i=0;i<spirittankCount.length;i++) {
			all_count=all_count+spirittankCount[i];
		}
		//readData end//
	}
	
	public void copy_map_gradient(int[][] mapmap) {
		//���³�ʼ��this.mapmap_move_point001
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
				//�߽�淶��
				if(i==0) {
					this.mapmap_move_point001[i][j]=1;
				}
				if(i==46) {
					this.mapmap_move_point001[i+1][j]=1;
				}
				if(j==35) {//��ͼ���·�������
					this.mapmap_move_point001[i][j]=1;
					this.mapmap_move_point001[i][j+1]=1;
				}
				if(j==0) {
					this.mapmap_move_point001[i][j]=1;
				}
			}
		}
		//���³�ʼ��this.mapmap_move_point002
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
				//�߽�淶��
				if(i==0) {
					this.mapmap_move_point002[i][j]=1;
				}
				if(i==46) {
					this.mapmap_move_point002[i+1][j]=1;
				}
				if(j==35) {//��ͼ���·�������
					this.mapmap_move_point002[i][j]=1;
					this.mapmap_move_point002[i][j+1]=1;
				}
				if(j==0) {
					this.mapmap_move_point002[i][j]=1;
				}
			}
		}	
		
		//���³�ʼ��this.mapmap_move_point003
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
				//�߽�淶��
				if(i==0) {
					this.mapmap_move_point003[i][j]=1;
				}
				if(i==46) {
					this.mapmap_move_point003[i+1][j]=1;
				}
				if(j==35) {//��ͼ���·�������
					this.mapmap_move_point003[i][j]=1;
					this.mapmap_move_point003[i][j+1]=1;
				}
				if(j==0) {
					this.mapmap_move_point003[i][j]=1;
				}
			}
		}	
		
		//���³�ʼ��this.mapmap_move_point004
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
				//�߽�淶��
				if(i==0) {
					this.mapmap_move_point004[i][j]=1;
				}
				if(i==46) {
					this.mapmap_move_point004[i+1][j]=1;
				}
				if(j==35) {//��ͼ���·�������
					this.mapmap_move_point004[i][j]=1;
					this.mapmap_move_point004[i][j+1]=1;
				}
				if(j==0) {
					this.mapmap_move_point004[i][j]=1;
				}
			}
		}	
		
		//���³�ʼ��this.mapmap_move_point005
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
				//�߽�淶��
				if(i==0) {
					this.mapmap_move_point005[i][j]=1;
				}
				if(i==46) {
					this.mapmap_move_point005[i+1][j]=1;
				}
				if(j==35) {//��ͼ���·�������
					this.mapmap_move_point005[i][j]=1;
					this.mapmap_move_point005[i][j+1]=1;
				}
				if(j==0) {
					this.mapmap_move_point005[i][j]=1;
				}
			}
		}
		
		//���³�ʼ��this.mapmap_move_point006
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
				//�߽�淶��
				if(i==0) {
					this.mapmap_move_point006[i][j]=1;
				}
				if(i==46) {
					this.mapmap_move_point006[i+1][j]=1;
				}
				if(j==35) {//��ͼ���·�������
					this.mapmap_move_point006[i][j]=1;
					this.mapmap_move_point006[i][j+1]=1;
				}
				if(j==0) {
					this.mapmap_move_point006[i][j]=1;
				}
			}
		}		
		
		//���³�ʼ��this.mapmap_move_point007
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
				//�߽�淶��
				if(i==0) {
					this.mapmap_move_point007[i][j]=1;
				}
				if(i==46) {
					this.mapmap_move_point007[i+1][j]=1;
				}
				if(j==35) {//��ͼ���·�������
					this.mapmap_move_point007[i][j]=1;
					this.mapmap_move_point007[i][j+1]=1;
				}
				if(j==0) {
					this.mapmap_move_point007[i][j]=1;
				}
			}
		}		
		
		//���³�ʼ��this.mapmap_move_point008
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
				//�߽�淶��
				if(i==0) {
					this.mapmap_move_point008[i][j]=1;
				}
				if(i==46) {
					this.mapmap_move_point008[i+1][j]=1;
				}
				if(j==35) {//��ͼ���·�������
					this.mapmap_move_point008[i][j]=1;
					this.mapmap_move_point008[i][j+1]=1;
				}
				if(j==0) {
					this.mapmap_move_point008[i][j]=1;
				}
			}
		}	
		
		//���³�ʼ��this.mapmap_move_point009
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
				//�߽�淶��
				if(i==0) {
					this.mapmap_move_point009[i][j]=1;
				}
				if(i==46) {
					this.mapmap_move_point009[i+1][j]=1;
				}
				if(j==35) {//��ͼ���·�������
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
	
	//������� ����̹�˵���ײ
	public boolean isCollide(Tank tank) {
		boolean result=false;
		for(int i=Bricks.size()-1;i>=0;i--) {
			BlockBrick blockbrick = Bricks.get(i);
			if(tank.isCollide_BlockBrick(blockbrick)) {
				result=true;
				break;
			}
		}
		
		//��ֹ�ظ��ж�
		if(result==false) {
			for(int i=Stones.size()-1;i>=0;i--) {
				BlockStone blockstone =Stones.get(i);
				if(tank.isCollide(blockstone)){
					result=true;
					break;
				}
			}
		}
		
		//��ֹ�ظ��ж�
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
	
	
	//������� �����ڵ�����ײ
	public boolean isCollide(Bullet bullet) {
		boolean result=false;
		for(int i=Bricks.size()-1;i>=0;i--) {
			//��̬���ʵ��ж�
			BlockBrick blockbrick = Bricks.get(i);
			if(blockbrick.getState_0()==0&&
			   blockbrick.getState_1()==0&&
			   blockbrick.getState_2()==0&&
			   blockbrick.getState_3()==0) {
				mapmap[blockbrick.mapx][blockbrick.mapy]=KONG;			//�����������
				Bricks.remove(i);
				 continue;
			}
			if(bullet.isCollide(blockbrick)) {
				//����ײ
				result=true;
				//���λ�÷���
				if(bullet.getX()<=blockbrick.getX()&&bullet.getY()<=blockbrick.getY()) {
					if(blockbrick.getState_0()==0) {
						mapmap[blockbrick.mapx][blockbrick.mapy]=KONG;			//�����������
						 Bricks.remove(i);
						 continue;
					}else {
						blockbrick.setState_0(0);
					}
				}
				if(bullet.getX()>=blockbrick.getX()&&bullet.getY()<=blockbrick.getY()) {
					if(blockbrick.getState_1()==0) {
						mapmap[blockbrick.mapx][blockbrick.mapy]=KONG;			//�����������
						 Bricks.remove(i);
						 continue;
					}else {
						blockbrick.setState_1(0);
					}
				}
				if(bullet.getX()>=blockbrick.getX()&&bullet.getY()>=blockbrick.getY()) {
					if(blockbrick.getState_2()==0) {
						mapmap[blockbrick.mapx][blockbrick.mapy]=KONG;			//�����������
						 Bricks.remove(i);
						 continue;
					}else {
						blockbrick.setState_2(0);
					}
				}
				if(bullet.getX()<=blockbrick.getX()&&bullet.getY()>=blockbrick.getY()) {
					if(blockbrick.getState_3()==0) {
						mapmap[blockbrick.mapx][blockbrick.mapy]=KONG;			//�����������
						 Bricks.remove(i);
						 continue;
					}else {
						blockbrick.setState_3(0);
					}
				}
			}
		}
		//��ֹ�ظ��ж�
		if(result==false) {
			for(int i=Stones.size()-1;i>=0;i--) {
				BlockStone blockstone =Stones.get(i);
				if(bullet.isCollide(blockstone)){
					result=true;
					break;
				}
			}
		}
		//���Ķ�Ӧ���ƶ����ֵ�ͼ
		this.copy_map_gradient(mapmap);
		
		return result;
	}
	
	//������ɻ���̹��000����
	public SpiritTank CreateSpirittank000() {
		SpiritTank spirittank=null;
		Random random=new Random();
		//�����;���̹�˷�����
		if(spirittankCount[0]>0) {
			if(random.nextInt(this.tank_maketime000)==0){				//����̹�˹�������Խ�������ٶ�Խ��
				if(Spirits.size()!=0) {
					int pos =random.nextInt(Spirits.size());
					spirittank=new SpiritTank(Spirits.get(pos).getX(),
											Spirits.get(pos).getY());			//ת����mapmap���ֵ�ͼ��ʽ
					spirittankCount[0]--;
				}
			}
		}
		return spirittank;
	}
	
	//������ɻ���̹��001����
	public SpiritTank001 CreateSpirittank001() {
		SpiritTank001 spirittank=null;
		Random random=new Random();
		if(spirittankCount[1]>0) {
			if(random.nextInt(this.tank_maketime001)==0){				//����̹�˹�������Խ�������ٶ�Խ��
				if(Spirits.size()!=0) {
					int pos =random.nextInt(Spirits.size());
					spirittank=new SpiritTank001(Spirits.get(pos).getX(),
											Spirits.get(pos).getY(),this);		//ת����mapmap���ֵ�ͼ��ʽ
					spirittankCount[1]--;
				}
			}
		}
		return spirittank;
	}
	
	//������ɻ���̹��002����
	public SpiritTank002 CreateSpirittank002() {
		SpiritTank002 spirittank=null;
		Random random=new Random();
		if(spirittankCount[2]>0) {
			if(random.nextInt(this.tank_maketime002)==0){				//����̹�˹�������Խ�������ٶ�Խ��
				if(Spirits.size()!=0) {
					int pos =random.nextInt(Spirits.size());
					spirittank=new SpiritTank002(Spirits.get(pos).getX(),
											Spirits.get(pos).getY(),this,this.FIRE_TIME002,this.FIRE_SLEEP_TIME002);		//ת����mapmap���ֵ�ͼ��ʽ
					spirittankCount[2]--;
				}
			}
		}
		return spirittank;
	}
	
	
	//��ʼ�����̹����������
	public void initPCartoonData(Cartoon cartoon,int x,int y) {
		cartoon.setX(x);		//��ԭ��λ�ò�����������
		cartoon.setY(y);		//��ԭ��λ�ò�����������
	}
	
	

	
	
	
	
	
	
	
	
	
	
	
	
	
	//class end//
}















