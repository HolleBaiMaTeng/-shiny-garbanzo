import java.awt.Graphics;

public class BlockStone extends Block{
	public BlockStone (int x,int y) {
		super(x,y);
	}

	public void draw(Graphics g) {
		ImageUtil.getInstance().drawStone(g, x, y);
	}
	
	public void draw_0(Graphics g) {
		ImageUtil_0.getInstance().drawStone(g, x, y);
	}
	
	//BlockGrass end//

}
