package MapEditor;

import java.awt.Graphics;

public class EltBrick extends Element {
	
	public EltBrick(int x, int y) {
		super(x, y);
		this.model=PanelMain.BRICK;
	}

	public void draw(Graphics g) {
		ImageUtil.getInstance().drawBrick(g, x, y);
		super.draw(g);
	}
}
