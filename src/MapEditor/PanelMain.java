package MapEditor;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.LayoutManager;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.StringTokenizer;

import javax.swing.JPanel;

public class PanelMain extends JPanel implements MouseMotionListener,MouseListener{

	//预制鼠标移动点(失真数据)
	private int mousex=0;
	private int mousey=0;
	//鼠标拖动点
	private int tdx0;
	private int tdy0;
	private int tdx1;
	private int tdy1;
	//单选模式下坐标点
	private int x1=0;//数字地图对应点
	private int y1=0;//数字地图对应点
	
	
	private FrameMain frameMain;
	//同坦克大战
	public int[][] mapmap;				  		//数字化地图映射//由mapmap快速的对各个地图块进行访问
	public static final int KONG=-100;			//数字地图图块对应值
	public static final int SPIRIT1=0; //实体	//数字地图图块对应值
	public static final int SPIRIT0=50;//虚体	//数字地图图块对应值
	public static final int WATER=10;			//数字地图图块对应值
	public static final int BRICK=20;			//数字地图图块对应值
	public static final int STONE=30;			//数字地图图块对应值
	public static final int GRAESS=40;			//数字地图图块对应值
	
	public PanelMain(FrameMain frameMain) {
		mapmap=new int[47][36];
		this.frameMain=frameMain;
		this.addMouseMotionListener(this);
		this.addMouseListener(this);
		for(int i=0;i<=46;i++) {
			for(int j=0;j<=35;j++) {
				mapmap[i][j]=KONG;
			}
		}
	}
	
	public void paint(Graphics g) {
		super.paint(g);
		
		
		
		g.fillRect(0, 0, 800, 600);
		g.setColor(Color.gray);
		
		for(int i=1;i<=800/17;i++) {
			g.drawLine(i*17, 0, i*17, 600);
		}
		
		for(int i=1;i<=600/17;i++) {
			g.drawLine(0, i*17, 800, i*17);
		}
		
				
		//将数字地图画出
		for(int i=0;i<=46;i++) {
			for(int j=0;j<=35;j++) {
				if(mapmap[i][j]==SPIRIT1) {
					ImageUtil.getInstance().drawSpirittank01(g, i*17, j*17);
				}else if(mapmap[i][j]==WATER) {
					ImageUtil.getInstance().drawWater01(g, i*17, j*17);
				}else if(mapmap[i][j]==BRICK) {
					ImageUtil.getInstance().drawBrick01(g, i*17, j*17);
				}else if(mapmap[i][j]==STONE) {
					ImageUtil.getInstance().drawStone01(g, i*17, j*17);
				}else if(mapmap[i][j]==GRAESS) {
					ImageUtil.getInstance().drawGrass01(g, i*17, j*17);
				}
			}
		}
		
		//绘制预加载
		switch(Operation.getInstance().getModel()) {
		case SPIRIT1:
			ImageUtil.getInstance().drawSpirittank01(g, mousex, mousey);
			break;
		case WATER:
			ImageUtil.getInstance().drawWater01(g, mousex, mousey);
			break;
		case BRICK:
			ImageUtil.getInstance().drawBrick01(g, mousex, mousey);
			break;
		case STONE:
			ImageUtil.getInstance().drawStone01(g, mousex, mousey);
			break;
		case GRAESS:
			ImageUtil.getInstance().drawGrass01(g, mousex, mousey);
			break;
		case KONG:
			ImageUtil.getInstance().drawKong01(g, mousex, mousey);
			break;
		}
	}

	public void openMap(File file) throws IOException{
		//读文件
		FileReader fr;
		fr = new FileReader(file);
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
			if(str1.compareTo("spirittankPos")==0) {//采用手写的方式添加敌方坦克数量与己方血量
				line=str2.split(",");											//采用初始化方向
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
		System.out.println("读取成功");
	}
	
	public void saveMap(File file) {
		OutputStream out = null;
		try {
			out = new FileOutputStream(file);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		OutputStreamWriter writer=new OutputStreamWriter(out);
		BufferedWriter bw=new BufferedWriter(writer);
		PrintWriter pw=new PrintWriter(bw,true);
		//默认储存设置
		pw.println("spirittankCount=0,0,1,3,0");
		pw.println("playertankLife=3,3");
		//将数字地图画出
		for(int i=0;i<=46;i++) {
			for(int j=0;j<=35;j++) {
				if(mapmap[i][j]==SPIRIT1) {
					pw.println("spirittankPos="+i+","+j);
				}else if(mapmap[i][j]==WATER) {
					pw.println("BlockWater="+i+","+j);
				}else if(mapmap[i][j]==BRICK) {
					pw.println("BlockBricks="+i+","+j+","+1+","+1+","+1+","+1);
				}else if(mapmap[i][j]==STONE) {
					pw.println("BlockStone="+i+","+j);
				}else if(mapmap[i][j]==GRAESS) {
					pw.println("BlockGrass="+i+","+j);
				}
			}
		}
		pw.close();
	}
	
	public void newMap() {
		for(int i=0;i<=46;i++) {
			for(int j=0;j<=35;j++) {
				mapmap[i][j]=KONG;
				frameMain.repaint();
			}
		}
	}
	
	@Override
	public void mouseDragged(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		mousex=e.getX()/17*17;
		mousey=e.getY()/17*17;
		frameMain.repaint();
	}

	@Override
	public void mouseClicked(MouseEvent m) {
		x1=m.getX()/17;
		y1=m.getY()/17;
		//改动本次操作的数字地图
		for(int i=0;i<=46;i++) {
			for(int j=0;j<=35;j++) {
				if(x1==i&&y1==j) {
					switch(Operation.getInstance().getModel()) {
					case SPIRIT1:
						mapmap[i][j]=SPIRIT1;
						mapmap[i+1][j]=SPIRIT0;
						mapmap[i][j+1]=SPIRIT0;
						mapmap[i+1][j+1]=SPIRIT0;
						break;
					case WATER:
						mapmap[i][j]=WATER;
						break;
					case BRICK:
						mapmap[i][j]=BRICK;
						break;
					case STONE:
						mapmap[i][j]=STONE;
						break;
					case GRAESS:
						mapmap[i][j]=GRAESS;
						break;
					case KONG:
						mapmap[i][j]=KONG;
						break;
					}
				}
			}
		}
		frameMain.repaint();
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mousePressed(MouseEvent e) {
		tdx0=e.getX()/17;
		tdy0=e.getY()/17;
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		tdx1=e.getX()/17;
		tdy1=e.getY()/17;
		//改动本次操作的数字地图
		for(int i=0;i<=46;i++) {
			for(int j=0;j<=35;j++) {
				if(((i-tdx0)*(i-tdx1)<=0)&&((j-tdy0)*(j-tdy1)<=0)) {//判定所选区域
					switch(Operation.getInstance().getModel()) {
					case WATER:
						mapmap[i][j]=WATER;
						break;
					case BRICK:
						mapmap[i][j]=BRICK;
						break;
					case STONE:
						mapmap[i][j]=STONE;
						break;
					case GRAESS:
						mapmap[i][j]=GRAESS;
						break;
					case KONG:
						mapmap[i][j]=KONG;
						break;
					}
				}
			}
		}
		frameMain.repaint();	
		
	}
	
	
	
	
	
	


	
	
	
	
	
	
	
}















