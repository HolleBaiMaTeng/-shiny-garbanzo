import java.awt.Graphics;
import java.awt.Rectangle;

public class Tank extends Spirit {


	
	
	public Tank(int x, int y) {
		super(x, y);
		
		//��ս��������
		this.setVelocity(2);
		this.setWidth(25);
		
		aliveFrameCount=2;
		explodeFrameCount=3;
	}
	
	
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
		return bullet;
	}
	
	
	//���̹�˺���ͨͼ�����ײ����
	//̹����ͼ��֮��ѡ�������ײ
	public boolean isCollide(Block block) {
		boolean result=false;
		int tempX,tempY;
		tempX=this.getX();//ͼ����������
		tempY=this.getY();
		
		switch(this.getDiraction()) {
		case UP:
			tempY=tempY-this.getVelocity();
			break;
		case RIGHT:
			tempX=tempX+this.getVelocity();
			break;
		case DOWN:
			tempY=tempY+this.getVelocity();
			break;
		case LEFT:
			tempX=tempX-this.getVelocity();
			break;
		}
		
		Rectangle rect1=new Rectangle(tempX-this.getWidth()/2,tempY-this.getWidth()/2,
				this.getWidth(),this.getWidth());
		Rectangle rect2=new Rectangle(block.getX()-block.getWidth()/2,block.getY()-block.getWidth()/2,
				block.getWidth(),block.getWidth());
		
		
		if(rect1.intersects(rect2)) {
			result=true;
		}
		
		return result;
	}
	
	//���̹�����̬��Ԫ�ľ�����ײ
	public boolean isCollide_BlockBrick(BlockBrick blockbrick) {
		boolean result=false;
		int tempX,tempY;
		tempX=this.getX();//ͼ����������
		tempY=this.getY();
		
		switch(this.getDiraction()) {
		case UP:
			tempY=tempY-this.getVelocity();
			break;
		case RIGHT:
			tempX=tempX+this.getVelocity();
			break;
		case DOWN:
			tempY=tempY+this.getVelocity();
			break;
		case LEFT:
			tempX=tempX-this.getVelocity();
			break;
		}
		//����8��Ԫ�����ŵ�Ԫ����
		Rectangle rect_0=null;
		Rectangle rect_1=null;
		Rectangle rect_2=null;
		Rectangle rect_3=null;
		int rect0=1;
		int rect1=1;
		int rect2=1;
		int rect3=1;
		Rectangle rect_tank=new Rectangle(tempX-this.getWidth()/2,tempY-this.getWidth()/2,
				this.getWidth(),this.getWidth());
		if(blockbrick.getState_0()==1) {//����8��9ΪBLOCK_MAP�Ľ���һ��
			rect_0=new Rectangle(blockbrick.getX()-8,blockbrick.getY()-8,8,8);
			if(rect_0.intersects(rect_tank)) {
				rect0=0;
			}
		}
		if(blockbrick.getState_1()==1) {//����8��9ΪBLOCK_MAP�Ľ���һ��
			rect_1=new Rectangle(blockbrick.getX(),blockbrick.getY()-8,8,8);
			if(rect_1.intersects(rect_tank)) {
				rect1=0;
			}
		}
		if(blockbrick.getState_2()==1) {//����8��9ΪBLOCK_MAP�Ľ���һ��
			rect_2=new Rectangle(blockbrick.getX(),blockbrick.getY(),8,8);
			if(rect_2.intersects(rect_tank)) {
				rect2=0;
			}
		}
		if(blockbrick.getState_3()==1) {//����8��9ΪBLOCK_MAP�Ľ���һ��
			rect_3=new Rectangle(blockbrick.getX()-8,blockbrick.getY(),8,8);
			if(rect_3.intersects(rect_tank)) {
				rect3=0;
			}
		}
		
		if(rect0*rect1*rect2*rect3==0) {
			result=true;
		}
		
		return result;
	}

	//tank�����ײ���
	public boolean tankCollide(Tank tank) {
		boolean result=false;
		int tempX,tempY;
		tempX=this.getX();//ͼ����������
		tempY=this.getY();
		
		switch(this.getDiraction()) {
		case UP:
			tempY=tempY-this.getVelocity();
			break;
		case RIGHT:
			tempX=tempX+this.getVelocity();
			break;
		case DOWN:
			tempY=tempY+this.getVelocity();
			break;
		case LEFT:
			tempX=tempX-this.getVelocity();
			break;
		}
		
		//��С�ж�����
		this.setWidth(this.getWidth()-6);
		tank.setWidth(tank.getWidth()-6);
		Rectangle rect1=new Rectangle(tempX-this.getWidth()/2,tempY-this.getWidth()/2,
				this.getWidth(),this.getWidth());
		Rectangle rect2=new Rectangle(tank.getX()-tank.getWidth()/2,tank.getY()-tank.getWidth()/2,
				tank.getWidth(),tank.getWidth());
		if(rect1.intersects(rect2)) {
			result=true;
		}
		
		//��ԭ
		this.setWidth(this.getWidth()+6);
		tank.setWidth(tank.getWidth()+6);
		
		return result;
	}
	
	
	
	
	//class end//
}













