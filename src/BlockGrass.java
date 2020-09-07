import java.awt.Graphics;

public class BlockGrass extends Block{

	public BlockGrass (int x,int y) {
		super(x,y);
	}

	public void draw(Graphics g) {
		ImageUtil.getInstance().drawGrass(g, x, y);
	}
	public void draw_0(Graphics g) {
		ImageUtil_0.getInstance().drawGrass(g, x, y);
	}
	//BlockGrass end//
}
