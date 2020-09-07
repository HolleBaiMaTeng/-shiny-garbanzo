import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;

/*****************************************************
 * 目的：制作公用的精灵动态类
 * 开发时间：2020.4.3
 * 维护次数：0
 * 最后维护时间：
 * 电话联系方式：18237561532
 * @author BMT
 *
 ****************************************************/
public class Spirit {
	
	public static final int UP=0;
	public static final int RIGHT=1;
	public static final int DOWN=2;
	public static final int LEFT=3;
	
	//注意：xy为图形中心坐标
	private int x;
	private int y;
	
	private int width;       //大小
	private int velocity;	 //速度
	private int direction;   //方向
	private int category;    //类别
	private int state;       //状态
	protected int frameState;  //窗体状态
	
	
	protected int aliveFrameCount=0;	//存活状态下动画帧数
	protected int explodeFrameCount=0;	//爆炸状态下动画帧数


	public Spirit(int x, int y) {
		//TODO 建立起统一动态父类
		this.x=x;
		this.y=y;
		width=34;
		
		//初始化设置
		category=0;		//种类初始化
		velocity=2;	   	//速度初始化
		direction=UP;	//方向初始化
		
		frameState=0;	//窗体状态初始化
		
		aliveFrameCount=0;		//存活动画帧初始化
		explodeFrameCount=0;	//爆炸动画帧初始化
	}
	
	
	
	//建立get函数
	public int getX() {return x;}
	public int getY() {return y;}
	public int getCategory() {return category;}
	public int getDiraction() {return direction;}
	public int getFrameState() {return frameState;}
	public int getWidth() {return width;}
	public int getVelocity() {return velocity;}
	
	
	//建立set函数
	public void setX(int x){
		this.x=x;
	}
	public void setY(int y) {
		this.y=y;
	}
	public void setCategory(int category) {
		this.category=category;
	}
	public void setDiraction(int diraction) {
		this.direction=diraction;
	}
	public void setFrameState(int frameState) {
		this.frameState=frameState;
	}
	public void setVelocity(int velocity) {
		this.velocity=velocity;
	}
	public void setWidth(int width) {
		this.width=width;
	}
	
	
	//移动函数的构造
	public void move() {
		switch(direction) {
		case UP:
			y=y-velocity;
			break;
		case RIGHT:
			x=x+velocity;
			break;
		case DOWN:
			y=y+velocity;
			break;
		case LEFT:
			x=x-velocity;
			break;
		}
	}
	
	
	
	//绘图函数
	public void draw(Graphics g) {
		if(this instanceof Bullet) {
			ImageUtil.getInstance().drawBullet(g,(Bullet)this);
		}
	}
	
	//绘图函数
	public void draw_0(Graphics g) {
		if(this instanceof Bullet) {
			ImageUtil_0.getInstance().drawBullet(g,(Bullet)this);
		}
	}
	
	//检测碰撞函数1
	//与其他精灵类
	public boolean isCollide(Spirit spirit) {
		boolean result=false;
		/*
		double length=Math.sqrt(Math.pow(x-spirit.getX(),2)
				+Math.pow(y-spirit.getY(),2));
		if(length<(width+spirit.getWidth())/2) {
			result=true;
		}
		*/
		Rectangle rect1=new Rectangle(this.getX()-this.getWidth()/2,
				                      this.getY()-this.getWidth()/2,
				                      this.getWidth(),
				                      this.getWidth());
		
		Rectangle rect2=new Rectangle(spirit.getX()-spirit.getWidth()/2,
				spirit.getY()-spirit.getWidth()/2,
				spirit.getWidth(),
				spirit.getWidth());
		
		if(rect1.intersects(rect2)) {
			result=true;
		}
			return result;
		
	}
	
	//碰撞检测函数2(与地图的检测碰撞函数以后将用于视角的转换)
	//与地图边界
	public int isCollide_bianjie() {//0-未碰撞//1-上//2-右//3-下//4-左
		int result=0;
		switch(direction) {
		case(UP):
			if(y-velocity<width/2) {
				result=1;
			}
		break;
		case(RIGHT):
			if(x+velocity>MainFrame.WIDTH-width/2) {
				result=2;
			}
		break;
		case(DOWN):
			if(y+velocity>MainFrame.HEIGHT-width/2) {
				result=3;
			}
		break;
		case(LEFT):
			if(x-velocity<width/2) {
				result=4;
			}
		break;
		}
		return result;
	}
	
	
	//计算函数
	public void caculateDate() {
		frameState++;
		if(frameState==aliveFrameCount) {
			frameState=0;
		}
	}
	
	
	
	//Spirit Class end//
}
	
	
	
	
	
	
	

	
	

