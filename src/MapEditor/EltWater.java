package MapEditor;

import java.awt.Graphics;

public class EltWater extends Element {

	public EltWater(int x, int y) {
		super(x, y);
		this.model=PanelMain.WATER;
	}

	public void draw(Graphics g) {
		ImageUtil.getInstance().drawWater(g, x, y);
		super.draw(g);
	}
}
