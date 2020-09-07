import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;

/*****************************************************
 * Ŀ�ģ��������õľ��鶯̬��
 * ����ʱ�䣺2020.4.3
 * ά��������0
 * ���ά��ʱ�䣺
 * �绰��ϵ��ʽ��18237561532
 * @author BMT
 *
 ****************************************************/
public class Spirit {
	
	public static final int UP=0;
	public static final int RIGHT=1;
	public static final int DOWN=2;
	public static final int LEFT=3;
	
	//ע�⣺xyΪͼ����������
	private int x;
	private int y;
	
	private int width;       //��С
	private int velocity;	 //�ٶ�
	private int direction;   //����
	private int category;    //���
	private int state;       //״̬
	protected int frameState;  //����״̬
	
	
	protected int aliveFrameCount=0;	//���״̬�¶���֡��
	protected int explodeFrameCount=0;	//��ը״̬�¶���֡��


	public Spirit(int x, int y) {
		//TODO ������ͳһ��̬����
		this.x=x;
		this.y=y;
		width=34;
		
		//��ʼ������
		category=0;		//�����ʼ��
		velocity=2;	   	//�ٶȳ�ʼ��
		direction=UP;	//�����ʼ��
		
		frameState=0;	//����״̬��ʼ��
		
		aliveFrameCount=0;		//����֡��ʼ��
		explodeFrameCount=0;	//��ը����֡��ʼ��
	}
	
	
	
	//����get����
	public int getX() {return x;}
	public int getY() {return y;}
	public int getCategory() {return category;}
	public int getDiraction() {return direction;}
	public int getFrameState() {return frameState;}
	public int getWidth() {return width;}
	public int getVelocity() {return velocity;}
	
	
	//����set����
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
	
	
	//�ƶ������Ĺ���
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
	
	
	
	//��ͼ����
	public void draw(Graphics g) {
		if(this instanceof Bullet) {
			ImageUtil.getInstance().drawBullet(g,(Bullet)this);
		}
	}
	
	//��ͼ����
	public void draw_0(Graphics g) {
		if(this instanceof Bullet) {
			ImageUtil_0.getInstance().drawBullet(g,(Bullet)this);
		}
	}
	
	//�����ײ����1
	//������������
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
	
	//��ײ��⺯��2(���ͼ�ļ����ײ�����Ժ������ӽǵ�ת��)
	//���ͼ�߽�
	public int isCollide_bianjie() {//0-δ��ײ//1-��//2-��//3-��//4-��
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
	
	
	//���㺯��
	public void caculateDate() {
		frameState++;
		if(frameState==aliveFrameCount) {
			frameState=0;
		}
	}
	
	
	
	//Spirit Class end//
}
	
	
	
	
	
	
	

	
	

