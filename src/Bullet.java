import java.awt.Rectangle;

public class Bullet extends Spirit {

	public static final int PLAYERBULLET=0;//���̹���ڵ�
	public static final int SPIRITBULLET=1;//����̹���ڵ�
	public static final int BULLET_W_NORMAL=2; //������С�ڵ����ж����
	public static final int BULLET_W_SPECIAL=16;//�ڵ�����ײʱ������
	private int bullettype=PLAYERBULLET;
	
	
	public Bullet(int x, int y,int diraction) {
		super(x, y);
		super.setDiraction(diraction);
		// TODO Auto-generated constructor stub
		
		//��ս��������
		setVelocity(8); //�趨�ڵ��ٶ�
		setWidth(BULLET_W_NORMAL);	//�趨��С	
		
		
		aliveFrameCount=1;
		explodeFrameCount=3;
		
	}
	
	public int getBulletType() {
		return bullettype;
	}
	public void setBulletType(int bullettype) {
		this.bullettype=bullettype;
	}
	
	//�ڵ���ײ��ͼ�ļ��
	public boolean isCollide(Block block) {
		boolean result=false;
		
		Rectangle rect1=new Rectangle(this.getX()-this.getWidth()/2,this.getY()-this.getWidth()/2,
				this.getWidth(),this.getWidth());
		Rectangle rect2=new Rectangle(block.getX()-block.getWidth()/2,block.getY()-block.getWidth()/2,
				block.getWidth(),block.getWidth());
		if(rect1.intersects(rect2)) {
			result=true;
		}
		return result;
	}
	
	
	//class end//
}
