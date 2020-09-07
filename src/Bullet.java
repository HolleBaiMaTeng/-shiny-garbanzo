import java.awt.Rectangle;

public class Bullet extends Spirit {

	public static final int PLAYERBULLET=0;//玩家坦克炮弹
	public static final int SPIRITBULLET=1;//精灵坦克炮弹
	public static final int BULLET_W_NORMAL=2; //正常大小炮弹的判定宽度
	public static final int BULLET_W_SPECIAL=16;//炮弹间碰撞时计算宽度
	private int bullettype=PLAYERBULLET;
	
	
	public Bullet(int x, int y,int diraction) {
		super(x, y);
		super.setDiraction(diraction);
		// TODO Auto-generated constructor stub
		
		//对战数据设置
		setVelocity(8); //设定炮弹速度
		setWidth(BULLET_W_NORMAL);	//设定大小	
		
		
		aliveFrameCount=1;
		explodeFrameCount=3;
		
	}
	
	public int getBulletType() {
		return bullettype;
	}
	public void setBulletType(int bullettype) {
		this.bullettype=bullettype;
	}
	
	//炮弹碰撞地图的检测
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
