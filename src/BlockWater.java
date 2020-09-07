import java.awt.Graphics;

public class BlockWater extends Block {

	private int frameState;
	public BlockWater(int x, int y) {
		super(x, y);
		frameState=0;
	}
	public void draw(Graphics g) {
		if((x/17+y/17)%2==0) {
			ImageUtil.getInstance().drawWater(g, x, y, frameState/20);
			frameState=(frameState+1)%40;
		}else {
			ImageUtil.getInstance().drawWater(g, x, y, (frameState/20+1)%2);
			frameState=(frameState+1)%40;
		}
	}
	
	public void draw_0(Graphics g) {
		if((x/17+y/17)%2==0) {
			ImageUtil_0.getInstance().drawWater(g, x, y, frameState/20);
			frameState=(frameState+1)%40;
		}else {
			ImageUtil_0.getInstance().drawWater(g, x, y, (frameState/20+1)%2);
			frameState=(frameState+1)%40;
		}
	}
}
