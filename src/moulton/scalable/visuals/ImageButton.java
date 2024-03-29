package moulton.scalable.visuals;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;

import moulton.scalable.clickables.RadioButton;
import moulton.scalable.containers.Panel;
import moulton.scalable.utils.MenuComponent;
import moulton.scalable.utils.MenuSolver.Expression;

/**
 * A button that displays an image instead of text on its button face.
 * @author Matthew Moulton
 */
public class ImageButton extends RadioButton {
	/**The image drawn on the face of the button when not clicked.
	 * @see #setImage(BufferedImage)*/
	protected BufferedImage image;
	/**The image to draw on the face of the button when touched. If none specified, the normal
	 * image will be used.
	 * @see #setTouchedImage(BufferedImage)*/
	protected BufferedImage touchedImage;
	/**The image to draw on the face of the button when clicked. If none specified, the touched
	 * image will be used,
	 * unless that is not specified, in which case the normal image will be used.
	 * @see #setClickedImage(BufferedImage)*/
	protected BufferedImage clickedImage;
	/**The algebraic equations to determine the bounds of this button. */
	protected Expression width, height;
	/**The algebraic expression to determine the padding between the button boundaries and the
	 * image.
	 * @see #setPadding(String, String)*/
	protected Expression vertPadding, horizPadding;

	/**
	 * @param img the image to draw as the face of the button
	 * @param parent the panel that this image button will reside upon.
	 * @param x the x coordinate on the screen, given in menu component value format
	 * @param y the y coordinate on the screen, given in menu component value format
	 * @param width the width of the component, given in menu component value format
	 * @param height the height of the component, given in menu component value format
	 * @param background if the image does not fill up the entire button face, this fill color is
	 * used for the rest
	 */
	public ImageButton(BufferedImage img, Panel parent, String x, String y,
			String width, String height, Color background) {
		super(parent, x, y, background);
		this.width = solve.parse(width, true, false);
		this.height = solve.parse(height, true, false);
		this.image = img;
	}
	/**
	 * @deprecated use {@link #ImageButton(BufferedImage, Panel, String, String, String, String, Color)}
	 * and {@link #setId(String)}
	 */
	public ImageButton(String id, BufferedImage img, Panel parent, String x, String y,
			String width, String height, Color background) {
		super(id, parent, x, y, background);
		this.width = solve.parse(width, true, false);
		this.height = solve.parse(height, true, false);
		this.image = img;
	}
	/**
	 * @param img the BufferedImage to draw
	 * @param parent the panel that this button will reside upon
	 * @param x the integer x coordinate this button should appear on its panel
	 * @param y the integer y coordinate this button should appear on its panel
	 * @param background the background color for the box when editable
	 */
	public ImageButton(BufferedImage img, Panel parent,  int x, int y,
			Color background) {
		super(parent, x, y, background);
		this.image = img;
	}
	/**
	 * @deprecated use {@link #ImageButton(BufferedImage, Panel, int, int, Color) and
	 * {@link #setId(String)}
	 */
	public ImageButton(String id, BufferedImage img, Panel parent,  int x, int y,
			Color background) {
		super(id, parent, x, y, background);
		this.image = img;
	}
	
	/**
	 * Sets the image that this button will display on its face.
	 * @param img {@link image}
	 * @return this
	 */
	public ImageButton setImage(BufferedImage img){
		image = img;
		return this;
	}
	/**
	 * Sets the image that this button will display on its face when touched.
	 * @param img {@link #touchedImage}
	 * @return this
	 */
	public ImageButton setTouchedImage(BufferedImage img) {
		//if it was relying on the outline toggle right now
		if(img != null && touched && touchedImage==null && colorTouched==null) {
			//Therefore, the outline should go back to the original state
			setOutline(!getOutline());
		}
		touchedImage = img;
		return this;
	}
	/**
	 * Sets the image that this button will display on its face when clicked
	 * @param img {@link #clickedImage}
	 * @return this
	 */
	public ImageButton setClickedImage(BufferedImage img) {
		clickedImage = img;
		return this;
	}

	/**
	 * Draws on the graphics object to represent this image button. The button will be bounded by
	 * either the {@link #x}, {@link #y}, {@link #width}, and {@link #height} algebraic expressions
	 * or by the grid of {@link MenuComponent#parent}. It will draw the image dictated by {@link
	 * #getDrawImage()} in those bounds in the original aspect ratio. Any space not covered by the
	 * drawing of the image will be filled with {@link RadioButton#color}.
	 * @param g the graphics object to draw on
	 * @param ww the width of this component's container or {@link #parent} that will be drawn on.
	 * @param hh the height of this component's container or {@link #parent} that will be drawn on.
	 */
	@Override
	public void render(Graphics g, int xx, int yy, int ww, int hh) {
		Rectangle rect = this.getRenderRect(xx, yy, ww, hh, width, height);
		int x = rect.x;
		int y = rect.y;
		int w = rect.width;
		int h = rect.height;
		
		g.setColor(getFillColor());
		g.fillRect(x, y, w, h);
		if(parent != null)
			defineClickBoundary(parent.handleOffsets(new int[] {x, x+w, x+w, x},
					new int[] {y, y, y+h, y+h}, this));
		
		// draw the picture
		BufferedImage imageToDraw = getDrawImage();
		if (imageToDraw != null) {
			int imgWidth, imgHeight;
			int availableW = w, availableH = h;
			int horizPad = 0, vertPad = 0;
			if(horizPadding != null) {
				horizPad = (int)solve.eval(horizPadding);
				availableW -= horizPad;
			}
			if(vertPadding != null) {
				vertPad = (int)solve.eval(vertPadding);
				availableH -= vertPad;
			}
			
			if(availableW/(double)imageToDraw.getWidth() <
					availableH/(double)imageToDraw.getHeight()) {
				//the ratio of width to imagewidth is lowest, thus we will keep its ratio
				imgWidth = availableW;
				imgHeight= (availableW*imageToDraw.getHeight())/imageToDraw.getWidth();
			}else{
				//keep the ratio of height to imageheight
				imgHeight= availableH;
				imgWidth = (availableH*imageToDraw.getWidth())/imageToDraw.getHeight();
			}
			
			g.drawImage(imageToDraw, x+(w-imgWidth)/2, y+(h-imgHeight)/2,
					imgWidth, imgHeight, null);
		}
		
		//draw outline if necessary
		if (outline) {
			g.setColor(enabled? Color.BLACK:Color.GRAY);
			g.drawRect(x, y, w - 1, h - 1);
		}
	}
	
	/**
	 * Returns the image to draw. If the button is neither touched nor clicked, then the normal
	 * image will be used. If the button is clicked, the clicked image will be used if there is
	 * one. If there is not, the touched image will be used if there is one. If the button is
	 * touched, then the touch image will be used if there is one.
	 * @return the appropriate image to draw on the button's face
	 */
	protected BufferedImage getDrawImage() {
		//if it is neither touched nor clicked, return the normal image
		if(!isClicked() && !isTouched())
			return image;
		
		//otherwise, it gets more complicated. If it is clicked, return the click image (if any)
		if(isClicked() && clickedImage!=null)
			return clickedImage;
		//clicked buttons are touched, so if it is touched and there is a touch image, return that
		if(isTouched() && touchedImage!=null)
			return touchedImage;
		
		//if none of those worked, just return the normal image
		return image;
	}
	
	@Override
	public void setTouched(boolean touched) {
		//if the touch state has changed and the outline toggle is to be used.
		if(touched != this.touched && colorTouched==null && touchedImage==null) {
			setOutline(!getOutline());
		}
		this.touched = touched;
	}
	
	/**
	 * If touchedColor is null and the touchedImage is null, the outline toggle will be used to
	 * show touch.
	 * @param touchedColor the new touched color
	 * @return this
	 */
	public ImageButton setTouchedColor(Color touchedColor) {
		if(touchedColor != null) {
			if(touchedImage==null && colorTouched==null) {
				/* if the button is touched presently and the toggle is used, that means that the
				 * component will show touch through the new color instead of toggling outline.
				 * Therefore, the outline should go back to the original state.
				 */
				if(touched)
					setOutline(!getOutline());
				
				//set the new darker color
				colorDark = touchedColor.darker();
			}
		}else {
			//resets to the old darker color
			colorDark = color.darker();
		}	
		this.colorTouched = touchedColor;
		return this;
	}
	
	/**
	 * Sets the padding for this image button. The padding is the space between the button border
	 * and the image.
	 * @param vertPadding A string expression to represent the amount of vertical padding desired.
	 * Sets {@link #vertPadding}.
	 * @param horizPadding A string expression to represent the amount of horizontal padding
	 * desired. Sets {@link #horizPadding}.
	 * @return this
	 */
	public ImageButton setPadding(String vertPadding, String horizPadding) {
		this.vertPadding = (vertPadding == null)? null : solve.parse(vertPadding, false, false);
		this.horizPadding = (horizPadding == null)? null : solve.parse(horizPadding, false, false);
		return this;
	}
}
