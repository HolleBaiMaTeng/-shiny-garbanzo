import java.awt.Graphics;
import java.util.Random;

public class SpiritTank extends Tank {

	public static final int SIPIRITTANK_V=2;
	
	public SpiritTank(int x, int y) {
		super(x, y);
		this.setVelocity(SIPIRITTANK_V);
		this.setCategory(0);
	}

	
	//计算函数
	public void caculateDate() {
		Random random=new Random();
		int isChange;
		isChange=random.nextInt(10);//0,1,2,3,4,5,6,7,8,9
		if(isChange==0) {
			setDiraction(random.nextInt(4));
		}
		super.caculateDate();
	}
	
	//随机开火函数
	public Bullet fire() {
		/****
		//测试模式（不开火）
		if(true) {
			return null;
		}
		/****/
		Random random=new Random();
		if(random.nextInt(100)==0) {
			Bullet spiritbullet=super.fire();
			spiritbullet.setBulletType(Bullet.SPIRITBULLET);//变换种类为精灵坦克种类
			return spiritbullet;
		}else {
			return null;
		}
	}
	
	//绘图函数
		public void drawSpiritTank(Graphics g) {
			ImageUtil.getInstance().drawSpirittank(g, this);
		}
		
		//绘图函数
		public void drawSpiritTank_0(Graphics g) {
			ImageUtil_0.getInstance().drawSpirittank(g, this);
		}
}
