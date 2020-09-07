import java.awt.Graphics;

public class Cartoon {

	
	
	public static final int BEXPLODE=0;	//动画种类常量
	public static final int TEXPLODE=1;
	public static final int TCREATE=2;
	
	private int x;
	private int y;
	private int cartoonStyle;	//动画种类
	private int frameCount;		//动画总帧数
	private int frameNumber;	//动画当前帧号
	private int repeatTime;		//动画重复次数
	
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
		this.x=x;        		//各个变量的初始化
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
				repeatTime--;		//动画重复次数减一
				frameNumber=0;		//重置动画帧数为零
				if(repeatTime==0&&finishListener!=null) {
					finishListener.doFinish();
				}
			}
			result=true;
		}
		return result;
	}
}
















