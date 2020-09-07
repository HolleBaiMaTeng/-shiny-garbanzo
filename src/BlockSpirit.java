import java.awt.Graphics;

public class BlockSpirit extends Block {

	public BlockSpirit(int x, int y) {
		super(x, y);
		mapx=x;
		mapy=y;
		this.x=x*ImageUtil.BLOCK_MAP+ImageUtil.BLOCK_MAP;
		this.y=y*ImageUtil.BLOCK_MAP+ImageUtil.BLOCK_MAP;
		width=ImageUtil.BLOCK_MAP;
	}

	public void draw(Graphics g) {
		ImageUtil.getInstance().drawSpirit(g, x, y);
	}
	
	public void draw_0(Graphics g) {
		ImageUtil_0.getInstance().drawSpirit(g, x, y);
	}
	
}
