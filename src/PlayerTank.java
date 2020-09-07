import java.awt.Graphics;
import java.awt.event.KeyEvent;

public class PlayerTank extends Tank {

	public int PLAYERTANK_V=3;					//设置默认速度
	private int playertankModel=1;				//0-原始坦克 1-主战坦克 2-时停坦克
	private int jn=0;    						//0-技能关闭 1-技能开启
	public int firepower=1;						//火力大小 （加入子弹个数）
	public String JNname="NULL!!!";				//坦克技能名字
	
	public static final int fire_0_0=4;	
	public static final int fire_0_1=4;
	public static final int fire_1_0=3;	
	public static final int fire_1_1=15;
	
	public PlayerTank(int x, int y,int playertankModel) {
		super(x, y);
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
	
	//定制转换坦克可类型的函数
	public void changePlayerTankModel(int playerTankModel) {
		this.playertankModel=playerTankModel;
		switch(playertankModel) {
		case 0:
			this.firepower=fire_0_0;
			this.setCategory(5);
			this.JNname="NULL!!!";//注意字符长度控制
			break;
		case 1:
			this.firepower=fire_1_0;
			this.setCategory(6);
			this.JNname="MORE FIRE!!!";//注意字符长度控制
			break;
		case 2:
			this.firepower=1;
			this.setCategory(4);
			this.JNname="TIME STOP!!!";//注意字符长度控制
			break;
		}
	}
	
	//定制时停函数
	public int getTimeSpeed() {
		if(this.playertankModel==2&&jn==1) {
			return 3;//时停坦克时间放慢3倍
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
		case KeyEvent.VK_W:
			setDiraction(Spirit.UP);
			break;
		case KeyEvent.VK_D:
			setDiraction(Spirit.RIGHT);
			break;
		case KeyEvent.VK_S:
			setDiraction(Spirit.DOWN);
			break;
		case KeyEvent.VK_A:
			setDiraction(Spirit.LEFT);
			break;
		case KeyEvent.VK_K:  						//技能键判定
			if(this.playertankModel==1) {				//若坦克类型为1
				setVelocity(PLAYERTANK_V/2);			//启动瞄准状态
				this.firepower=fire_1_1;				//增加火力
				this.jn=1;								//开启技能
			}else if(this.playertankModel==2) {			//若坦克类型为2
				this.jn=1;								//开启技能
			}
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
		if(key==KeyEvent.VK_K||key==KeyEvent.VK_NUMPAD2) {//技能键
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
