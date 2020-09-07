import java.awt.Graphics;
import java.util.Random;

public class BlockBrick extends Block{

	
	//实现多态
	private int[] State= {1,1,1,1};//0-左上//1-右上//2-右下//3-左下{{值为1：有 值为2：无}}
	
		
	
	public int getState_3() {
		return State[3];
	}
	public int getState_2() {
		return State[2];
	}
	public int getState_1() {
		return State[1];
	}
	public int getState_0() {
		return State[0];
	}
	
	public void setState_0(int State_0) {
		this.State[0]=State_0;
	}
	public void setState_1(int State_1) {
		this.State[1]=State_1;
	}
	public void setState_2(int State_2) {
		this.State[2]=State_2;
	}
	public void setState_3(int State_3) {
		this.State[3]=State_3;
	}
	
	
	public BlockBrick(int x,int y) {
		super(x,y);
		Random random=new Random();
	}

	public void draw(Graphics g,BlockBrick blockbrick) {
		ImageUtil.getInstance().drawBrick(g, x, y,blockbrick);
	}
	
	public void draw_0(Graphics g,BlockBrick blockbrick) {
		ImageUtil_0.getInstance().drawBrick(g, x, y,blockbrick);
	}
}
