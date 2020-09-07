import java.awt.Graphics;

public class Cartoon {

	
	
	public static final int BEXPLODE=0;	//�������ೣ��
	public static final int TEXPLODE=1;
	public static final int TCREATE=2;
	
	private int x;
	private int y;
	private int cartoonStyle;	//��������
	private int frameCount;		//������֡��
	private int frameNumber;	//������ǰ֡��
	private int repeatTime;		//�����ظ�����
	
	public FinishListener finishListener;
	
	public int getX() {return x;}
	public int getY() {return y;}
	public int getStyle() {return cartoonStyle;}
	public int getFrameNumber() {return frameNumber;}
	
	public void setX(int x) {
		this.x=x;
	}
	public void setY(int y) {
		this.y=y;
	}

	
	public Cartoon(int style,int x,int y) {
		// TODO Auto-generated constructor stub
		this.x=x;        		//���������ĳ�ʼ��
		this.y=y;
		cartoonStyle=style;
		frameNumber=0;
		repeatTime=1;
		finishListener=null;
		
		switch(style) {
		case BEXPLODE:
			frameCount=4;
			break;
		case TEXPLODE:
			frameCount=3;
			break;
		case TCREATE:
			frameCount=3;
			repeatTime=10;
			break;
		}
	}
	
	public void addFinishListener(FinishListener finishListener) {
		this.finishListener=finishListener;
	}

	
	
	public boolean draw(Graphics g) {
		boolean result=false;
		
		if(repeatTime!=0) {
			ImageUtil.getInstance().drawCartoon(g,this);
			frameNumber++;
			if(frameNumber==frameCount) {
				repeatTime--;		//�����ظ�������һ
				frameNumber=0;		//���ö���֡��Ϊ��
				if(repeatTime==0&&finishListener!=null) {
					finishListener.doFinish();
				}
			}
			result=true;
		}
		return result;
	}
}
















