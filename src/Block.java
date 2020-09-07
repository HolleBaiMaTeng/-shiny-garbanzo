
import java.awt.Graphics;

public class Block {

	
	protected int x;
	protected int y;
	protected int width;
	
	public int mapx;				//mapmap��������
	public int mapy;				//mapmap��������
		
	public int getX() {
		return this.x;
	}
	public int getY() {
		return this.y;
	}
	public int getWidth() {
		return this.width;
	}
	
	public Block(int x,int y) {
		mapx=x;
		mapy=y;
		this.x=x*ImageUtil.BLOCK_MAP+ImageUtil.BLOCK_MAP/2;
		this.y=y*ImageUtil.BLOCK_MAP+ImageUtil.BLOCK_MAP/2;
		width=ImageUtil.BLOCK_MAP;
	}
	
	public void draw(Graphics g) {
		
	}
}
