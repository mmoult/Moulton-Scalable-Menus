package example5;

import java.awt.Color;
import java.awt.Font;

import moulton.scalable.clickables.Button;
import moulton.scalable.clickables.Clickable;
import moulton.scalable.containers.Container;
import moulton.scalable.containers.MenuManager;
import moulton.scalable.containers.Panel;
import moulton.scalable.draggables.ScrollBar;
import moulton.scalable.texts.TextBox;

public class Manager5 extends MenuManager{
	TextBox main, bottom;

	public Manager5(Container cont) {
		super(cont);
	}

	@Override
	public void createMenu() {
		this.menu = Panel.createRoot(Color.WHITE);
		Font font = new Font("Arial", Font.PLAIN, 30);
		main = new TextBox("",menu,"0","0","?7width/8","?3height/4+1",
				font,Color.LIGHT_GRAY);
		main.setOutline(true);
		main.setAcceptEnter(true);
		bottom = new TextBox("",menu,"0","3height/4","?7width/8","?height-height/8",
				font,Color.LIGHT_GRAY);
		bottom.setOutline(true);
		main.setTextScroller(new ScrollBar(true,menu,"7width/8","0","?width","?3height/4",
				Color.GRAY));
		bottom.setTextScroller(new ScrollBar(false,menu,"0","7height/8","?7width/8","?height",
				Color.GRAY));
		new Button("!",menu,"7width/8","3height/4","?width","?height",
				font,Color.RED).setId("virtuality");
	}

	@Override
	public void clickableAction(Clickable c) {
		if(c.getId() != null && c.getId().equals("virtuality")) {
			//do stuff here
			Button b = (Button)c;
			if(b.getText().equals("!")) {
				b.setText("Ok");
				//disable virtuality
				main.setHasVirtualSpace(false);
				bottom.setHasVirtualSpace(false);
			}else {
				b.setText("!");
				main.setHasVirtualSpace(true);
				bottom.setHasVirtualSpace(true);
			}
		}
	}

}
