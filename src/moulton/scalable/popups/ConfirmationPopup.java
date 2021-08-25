package moulton.scalable.popups;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.image.BufferedImage;

import moulton.scalable.clickables.Button;
import moulton.scalable.containers.MenuManager;
import moulton.scalable.geometrics.Line;
import moulton.scalable.texts.Alignment;
import moulton.scalable.texts.Caption;
import moulton.scalable.texts.LineBreak;

/**
 * This is an example of what can be done with popups. Sometimes the user needs to choose between two options
 * before they proceed to the next action, and this popup fulfills this purpose. Admittedly its design does
 * not allow for much flexibility: the constructor calls an init method that creates the contents of the 
 * popup on its base panel.
 * @author Matthew Moulton
 */
public class ConfirmationPopup extends Popup {

	/**
	 * @param text the text that is the notification
	 * @param okId the ID that should be used for the accept button. <i>*This ID should be used to
	 * remove the popup from the MenuManager ({@link MenuManager#setPopup(Popup)}) as an event in 
	 * {@link MenuManager#clickableAction(Clickable)} for this popup to function properly.*</i>
	 * @param cancelId the ID that should be used for the decline and exit buttons. <i>*This ID should
	 * be used to remove the popup from the MenuManager ({@link MenuManager#setPopup(Popup)}) as an event
	 * in {@link MenuManager#clickableAction(Clickable)} for this popup to function properly.*</i>
	 * @param title the title of the popup. Set to null for no title.
	 * @param font the font of the text on the popup.
	 * @param exitButton whether an x button in the top right corner should be included in the popup.
	 * @param option whether the option should be presented as Yes/No or Ok/Cancel.
	 */
	public ConfirmationPopup(String text, String okId, String cancelId, String title, Font font, boolean exitButton, boolean option) {
		super(null, null, Color.WHITE);
		init(text, okId, cancelId, title, font, exitButton, option);
	}
	/**
	 * @param text the text that is the notification
	 * @param okId the ID that should be used for the accept button. <i>*This ID should be used to
	 * remove the popup from the MenuManager ({@link MenuManager#setPopup(Popup)}) as an event in 
	 * {@link MenuManager#clickableAction(Clickable)} for this popup to function properly.*</i>
	 * @param cancelId the ID that should be used for the decline and exit buttons. <i>*This ID should
	 * be used to remove the popup from the MenuManager ({@link MenuManager#setPopup(Popup)}) as an event
	 * in {@link MenuManager#clickableAction(Clickable)} for this popup to function properly.*</i>
	 * @param title the title of the popup. Set to null for no title.
	 * @param font the font of the text on the popup.
	 * @param x the x location of the popup
	 * @param y the y location of the popup
	 * @param exitButton whether an x button in the top right corner should be included in the popup.
	 * @param option whether the option should be presented as Yes/No or Ok/Cancel.
	 */
	public ConfirmationPopup(String text, String okId, String cancelId, String title, Font font, String x, String y, boolean exitButton, boolean option) {
		super(x, y, null, null, Color.WHITE);
		init(text, okId, cancelId, title, font, exitButton, option);
	}

	/**
	 * Called to set up the pop up
	 * @param text the text that the pop up will contain
	 * @param okId the id of the ok button
	 * @param cancelId the id of the cancel button
	 * @param title the title of the pop up
	 * @param font the pop up's font
	 * @param exitButton whether this pop up should contain an exit button
	 * @param option whether this pop up should be yes/no (for true), or ok/cancel (for false)
	 */
	private void init(String text, String okId, String cancelId, String title, Font font, boolean exitButton, boolean option) {
		base.setOutline(true);
		blanketBackground = new Color(0x33DDDDDD, true);
		BufferedImage img = new BufferedImage(1, 1, BufferedImage.TYPE_INT_RGB);
		FontMetrics fm = img.getGraphics().getFontMetrics(font);
		int fontHeight = fm.getHeight();
		int doubleHeight = fontHeight * 2;
		
		if(title != null) {
			title = "  " + title;
			new Caption(title,base,"0",""+fm.getHeight(), font, Alignment.LEFT_ALIGNMENT);
			new Line(base, "1",""+(doubleHeight-1), "width-2", "?", Color.GRAY);
		}
		
		if(exitButton)
			new Button(cancelId, "X", base, "width-"+(fm.stringWidth("X")*2), "0", "?width", ""+doubleHeight, font, Color.RED);
		
		String accept = option? "Yes":"Ok";
		String cancel = option? "No":"Cancel";
		int okWidth = fm.stringWidth(accept)*2;
		int noWidth = fm.stringWidth(cancel)*2;
		int spaceWidth = fm.stringWidth(" ");
		addTouchComponent(new Button(okId, accept, base, "centerx-"+(okWidth+spaceWidth), "height-1-"+doubleHeight, ""+okWidth, ""+doubleHeight, font, Color.LIGHT_GRAY));
		addTouchComponent(new Button(cancelId, cancel, base, "centerx+"+spaceWidth, "height-1-"+doubleHeight, ""+noWidth, ""+doubleHeight, font, Color.LIGHT_GRAY));
		
		final int DEFAULT_WIDTH = doubleHeight * 3; //default width of popup we arbitrarily set
		final int MAX_WIDTH = doubleHeight * 7; //the max width (as according to the height given).
		int textWidth = fm.stringWidth(text);
		int linesOfText = 1;
		if(textWidth < DEFAULT_WIDTH) {
			textWidth = DEFAULT_WIDTH;
		}else {
			if(fm.stringWidth(text) > MAX_WIDTH) { //only continue if our text is too long
				textWidth = MAX_WIDTH;
				String rem = text;
				text = ""; //text will be our final result with \n to separate lines
				eachLine: while(rem.length() > 0) {
					String line = "";
					while(rem.length() > 0 && fm.stringWidth(line) <= MAX_WIDTH) {
						//pass the first char from rem to line
						char next = rem.charAt(0); 
						rem = rem.substring(1);
						line += next;
						if(next == '\n') { //"short circuit"
							text += line;
							linesOfText++;
							continue eachLine;
						}
					}
					//now line is too long, or we ran out of rem
					if(rem.length() == 0)
						text += line; //append what we have
					else { //fix the issue that the line is too long
						LineBreak result = LineBreak.check(false, line, rem);
						text += result.LINE + '\n';
						rem = result.REMAINDER;
						linesOfText++;
					}
				}
			}else
				textWidth = fm.stringWidth(text);
		}
		new Caption(text, base, "0", "centery", font, "width");
		
		this.width = ""+(textWidth+doubleHeight); //doubleHeight serves as padding
		this.height = ""+(doubleHeight*3 + linesOfText*fontHeight);
	}

}
