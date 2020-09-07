package MapEditor;

import java.awt.Graphics;

public class kong  extends Element{

	public kong(int x, int y) {
		super(x, y);
		this.model=PanelMain.KONG;
	}

	public void draw(Graphics g) {
		ImageUtil.getInstance().drawKong(g, x, y);
		super.draw(g);
	}
	
	
}
