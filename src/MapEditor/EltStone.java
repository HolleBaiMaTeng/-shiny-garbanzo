package MapEditor;

import java.awt.Graphics;

public class EltStone extends Element{

	public EltStone(int x, int y) {
		super(x, y);
		this.model=PanelMain.STONE;
	}

	public void draw(Graphics g) {
		ImageUtil.getInstance().drawStone(g, x, y);
		super.draw(g);
	}
}
