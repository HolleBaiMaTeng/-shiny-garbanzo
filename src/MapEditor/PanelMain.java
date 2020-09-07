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

	//Ԥ������ƶ���(ʧ������)
	private int mousex=0;
	private int mousey=0;
	//����϶���
	private int tdx0;
	private int tdy0;
	private int tdx1;
	private int tdy1;
	//��ѡģʽ�������
	private int x1=0;//���ֵ�ͼ��Ӧ��
	private int y1=0;//���ֵ�ͼ��Ӧ��
	
	
	private FrameMain frameMain;
	//̹ͬ�˴�ս
	public int[][] mapmap;				  		//���ֻ���ͼӳ��//��mapmap���ٵĶԸ�����ͼ����з���
	public static final int KONG=-100;			//���ֵ�ͼͼ���Ӧֵ
	public static final int SPIRIT1=0; //ʵ��	//���ֵ�ͼͼ���Ӧֵ
	public static final int SPIRIT0=50;//����	//���ֵ�ͼͼ���Ӧֵ
	public static final int WATER=10;			//���ֵ�ͼͼ���Ӧֵ
	public static final int BRICK=20;			//���ֵ�ͼͼ���Ӧֵ
	public static final int STONE=30;			//���ֵ�ͼͼ���Ӧֵ
	public static final int GRAESS=40;			//���ֵ�ͼͼ���Ӧֵ
	
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
		
				
		//�����ֵ�ͼ����
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
		
		//����Ԥ����
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
		//���ļ�
		FileReader fr;
		fr = new FileReader(file);
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
			if(str1.compareTo("spirittankPos")==0) {//������д�ķ�ʽ��ӵз�̹�������뼺��Ѫ��
				line=str2.split(",");											//���ó�ʼ������
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
		System.out.println("��ȡ�ɹ�");
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
		//Ĭ�ϴ�������
		pw.println("spirittankCount=0,0,1,3,0");
		pw.println("playertankLife=3,3");
		//�����ֵ�ͼ����
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
		//�Ķ����β��������ֵ�ͼ
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
		//�Ķ����β��������ֵ�ͼ
		for(int i=0;i<=46;i++) {
			for(int j=0;j<=35;j++) {
				if(((i-tdx0)*(i-tdx1)<=0)&&((j-tdy0)*(j-tdy1)<=0)) {//�ж���ѡ����
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















