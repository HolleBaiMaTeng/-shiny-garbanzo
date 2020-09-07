package MapEditor;

import java.awt.Graphics;

public class EltGrass extends Element {

	public EltGrass(int x, int y) {
		super(x, y);
		this.model=PanelMain.GRAESS;
	}

	public void draw(Graphics g) {
		ImageUtil.getInstance().drawGrass(g, x, y);
		super.draw(g);
	}

}
