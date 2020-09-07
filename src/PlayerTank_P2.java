import java.awt.Graphics;
import java.awt.event.KeyEvent;

public class PlayerTank_P2 extends PlayerTank {
	public int PLAYERTANK_V=3;					//设置默认速度
	private int playertankModel=1;				//0-原始坦克 1-主战坦克 2-时停坦克
	private int jn=0;    						//0-技能关闭 1-技能开启
	public int firepower=1;						//火力大小 （加入子弹个数）
	
	
	public PlayerTank_P2(int x, int y,int playertankModel) {
		super(x, y,playertankModel);
		this.setVelocity(Spirit.UP);
		this.setCategory(4);
		this.setVelocity(PLAYERTANK_V);
		this.playertankModel=playertankModel;
		
		//与playertankmodel有关变量的初始化
		switch(this.playertankModel) {
		case 0:
			this.firepower=fire_0_0;
			this.PLAYERTANK_V=1;
			this.setCategory(5);
			break;
		case 1:
			this.firepower=fire_1_0;
			this.setCategory(6);
			break;
		case 2:
			this.firepower=1;
			this.setCategory(4);
			break;
		}
	}
	
	//访问函数
	public int getPlayerTankModel() {
		return this.playertankModel;
	}
	public int getJN() {
		return this.jn;
	}
	public void chageJN(int jn) {
		this.jn=jn;
	}
	
	
	//定制时停函数
	public int getTimeSpeed() {
		if(this.playertankModel==2&&jn==1) {
			return 2;//时停坦克时间放慢2倍(双人模式)
		}else {
			return 1;
		}
	}
	
	//绘图函数
	public void draw(Graphics g) {
		ImageUtil.getInstance().drawPlayerTank(g, this);
	}
	public void draw_0(Graphics g) {
		ImageUtil_0.getInstance().drawPlayerTank(g, this);
	}
	
	//定义按键函数
	public void keyPressed(KeyEvent e) {
		int key=e.getKeyCode();
		//按键启动
		switch(key) {
		case KeyEvent.VK_UP:
			setDiraction(Spirit.UP);
			break;
		case KeyEvent.VK_RIGHT:
			setDiraction(Spirit.RIGHT);
			break;
		case KeyEvent.VK_DOWN:
			setDiraction(Spirit.DOWN);
			break;
		case KeyEvent.VK_LEFT:
			setDiraction(Spirit.LEFT);
			break;
		case KeyEvent.VK_NUMPAD2:  						//技能键判定
			if(this.playertankModel==1) {				//若坦克类型为1
				setVelocity(PLAYERTANK_V/2);			//启动瞄准状态
				this.firepower=fire_1_1;				//增加火力
				this.jn=1;								//开启技能
			}else if(this.playertankModel==2) {			//若坦克类型为2
				this.jn=1;								//开启技能
			}
			break;
		}
	}
	
	//定义松键函数
	public void keyReleased(KeyEvent e) {
		int key=e.getKeyCode();
		if(key==KeyEvent.VK_NUMPAD2) {
			if(this.playertankModel==1) {
				this.jn=0;									//关闭技能
				setVelocity(PLAYERTANK_V);					//恢复速度
				this.firepower=fire_1_0;					//恢复火力
			}else if(this.playertankModel==2) {
				this.jn=0;									//关闭技能
			}
		}
	}
}
	
	
	
	
	
	
	
	
	
	
	
	
	