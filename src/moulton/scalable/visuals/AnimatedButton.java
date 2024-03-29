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
 * A button that displays an {@link Animation} on its button face.
 * @author Matthew Moulton
 */
public class AnimatedButton extends RadioButton {
	/**The image drawn on the face of the button when not clicked.
	 * @see #getDrawAnimation()
	 * @see #setAnimation(Animation)*/
	protected Animation mainAnimation;
	/**The animation to draw on the face of the button when touched. If none specified, the normal
	 * animation will be used.
	 * @see #setTouchedAnimation(Animation)*/
	protected Animation touchedAnimation;
	/**The image to draw on the face of the button when clicked. If none specified, the touched
	 * image will be used, unless that is not specified, in which case the normal image will be
	 * used.
	 * @see #setClickedAnimation(Animation)*/
	protected Animation clickedAnimation;
	/**The equations to determine the dimensions of this button. */
	protected Expression width, height;

	/**
	 * @param animation the animation to draw on the face of the button
	 * @param parent the panel that this image button will reside upon.
	 * @param x the x coordinate on the screen, given in menu component value format
	 * @param y the y coordinate on the screen, given in menu component value format
	 * @param width the width of the component, given in menu component value format
	 * @param height the height of the component, given in menu component value format
	 * @param background if the image does not fill up the entire button face, this fill color is
	 * used for the rest.
	 */
	public AnimatedButton(Animation animation, Panel parent, String x, String y,
			String width, String height, Color background) {
		super(parent, x, y, background);
		this.width = solve.parse(width, true, false);
		this.height = solve.parse(height, true, false);
		this.mainAnimation = animation;
	}
	/**
	 * @deprecated use {@link #AnimatedButton(Animation, Panel, String, String, String, String, Color)}
	 * and {@link #setId(String)}
	 */
	public AnimatedButton(String id, Animation animation, Panel parent, String x, String y,
			String width, String height, Color background) {
		super(parent, x, y, background);
		this.id = id;
		this.width = solve.parse(width, true, false);
		this.height = solve.parse(height, true, false);
		this.mainAnimation = animation;
	}
	/**
	 * @param animation the animation to draw on the face of the button
	 * @param parent the panel that this button will reside upon
	 * @param x the integer x coordinate this button should appear on its panel
	 * @param y the integer y coordinate this button should appear on its panel
	 * @param background the background color for the box when editable
	 */
	public AnimatedButton(Animation animation, Panel parent,  int x, int y,
			Color background) {
		super(parent, x, y, background);
		this.mainAnimation = animation;
	}
	/**
	 * @param id a unique string designed to identify this component when an event occurs.
	 * @param animation the animation to draw on the face of the button
	 * @param parent the panel that this button will reside upon
	 * @param x the integer x coordinate this button should appear on its panel
	 * @param y the integer y coordinate this button should appear on its panel
	 * @param background the background color for the box when editable
	 */
	public AnimatedButton(String id, Animation animation, Panel parent,  int x, int y,
			Color background) {
		super(parent, x, y, background);
		this.id = id;
		this.mainAnimation = animation;
	}
	
	/**
	 * Sets the animation that this button will display on its face.
	 * @param animation {@link #mainAnimation}
	 * @return this
	 */
	public AnimatedButton setAnimation(Animation animation){
		mainAnimation = animation;
		return this;
	}
	/**
	 * Sets the animation that this button will display on its face when touched.
	 * @param animation {@link #touchedAnimation}
	 */
	public AnimatedButton setTouchedAnimation(Animation animation) {
		//if it was relying on the outline toggle right now
		if(animation != null && touched && touchedAnimation==null && colorTouched==null) {
			//Therefore, the outline should go back to the original state
			setOutline(!getOutline());
		}
		touchedAnimation = animation;
		return this;
	}
	/**
	 * Sets the animation that this button will display on its face when clicked
	 * @param animation {@link #clickedAnimation}
	 * @return this
	 */
	public AnimatedButton setClickedAnimation(Animation animation) {
		clickedAnimation = animation;
		return this;
	}

	/**
	 * Draws on the graphics object to represent this animated button. The button will be bounded
	 * by either the {@link #x}, {@link #y}, {@link #width}, and {@link #height} algebraic
	 * expressions or by the grid of {@link MenuComponent#parent}. It will draw the image dictated
	 * by {@link #getDrawAnimation()} and {@link Animation#getPicture()} in those bounds in the
	 * original aspect ratio. Any space not covered by the drawing of the image will be filled with
	 * {@link RadioButton#color}.
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
		BufferedImage imageToDraw = getDrawAnimation().getPicture();
		if (imageToDraw != null) {
			int imgWidth, imgHeight;
			if(w/(double)imageToDraw.getWidth() < h/(double)imageToDraw.getHeight()){
				//the ratio of width to imagewidth is lowest, thus we will keep its ratio
				imgWidth = w;
				imgHeight= (w*imageToDraw.getHeight())/imageToDraw.getWidth();
			}else{
				//keep the ratio of height to imageheight
				imgHeight= h;
				imgWidth = (h*imageToDraw.getWidth())/imageToDraw.getHeight();
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
	 * Returns the animation to draw. If the button is neither touched nor clicked, then the normal
	 * animation will be used. If the button is clicked, the clicked animation will be used if
	 * there is one. If there is not, the touched animation will be used if there is one. If the 
	 * button is touched, then the touch animation will be used if there is one.
	 * @return the appropriate animation to draw on the button's face
	 */
	protected Animation getDrawAnimation() {
		//if it is neither touched nor clicked, return the normal image
		if(!isClicked() && !isTouched())
			return mainAnimation;
		
		//otherwise, it gets more complicated. If it is clicked, return the click image (if any)
		if(isClicked() && clickedAnimation!=null)
			return clickedAnimation;
		//clicked buttons are touched, so if it is touched and there is a touch image, return that
		if(isTouched() && touchedAnimation!=null)
			return touchedAnimation;
		
		//if none of those worked, just return the normal image
		return mainAnimation;
	}
	
	@Override
	public void setTouched(boolean touched) {
		//if the touch state has changed and the outline toggle is to be used.
		if(touched != this.touched && colorTouched==null && touchedAnimation==null) {
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
	public AnimatedButton setTouchedColor(Color touchedColor) {
		if(touchedColor != null) {
			if(touchedAnimation==null && colorTouched==null) {
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
}
