package moulton.scalable.visuals;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;

import moulton.scalable.containers.Panel;
import moulton.scalable.utils.MenuComponent;
import moulton.scalable.utils.MenuSolver.Expression;

/**
 * A menu component designed to display a {@link BufferedImage}. The component can either keep the
 * ratio of width to height for the image or stretch to fill the space. This is determined by
 * {@link #maintainAspectRatio}.
 * @author Matthew Moulton
 */
public class View extends MenuComponent {
	/**The image that will be drawn onto the coordinates provided.
	 * @see #setImage(BufferedImage)
	 * @see #getImage()*/
	protected BufferedImage image = null;
	/**String expressions to represent the dimensions of this view*/
	protected Expression width, height;
	/**Whether the view should draw the image in the same ratio as given or should stretch it to
	 * fill the area of the view.
	 * @see #isMaintainAspectRatio()
	 * @see #setMaintainAspectRatio(boolean)*/
	protected boolean maintainAspectRatio = true;
	/**Whether or not this component should render a black outline on the border of the component.
	 * @see #setOutline(boolean)
	 * @see #getOutline()*/
	protected boolean outline = false;
	
	/**
	 * @param img The image that will be drawn onto the coordinates provided
	 * @param parent the panel that this view will reside upon
	 * @param x the x coordinate on the parent, given in menu component value format
	 * @param y the y coordinate on the parent, given in menu component value format
	 * @param w the width of the component, given in menu component value format
	 * @param h the height of the component, given in menu component value format
	 */
	public View(BufferedImage img, Panel parent, String x, String y, String w, String h) {
		super(parent, x, y);
		this.image = img;
		this.width = solve.parse(w, true, false);
		this.height = solve.parse(h, true, false);
	}
	/**
	 * @param img The image that will be drawn onto the coordinates provided
	 * @param parent the panel that this view will reside upon
	 * @param x the integer x coordinate this button should appear on its panel
	 * @param y the integer y coordinate this button should appear on its panel
	 */
	public View(BufferedImage img, Panel parent, int x, int y) {
		super(parent, x, y);
		this.image = img;
	}

	@Override
	public void render(Graphics g, int xx, int yy, int ww, int hh) {
		if(image != null) {
			Rectangle rect = this.getRenderRect(xx, yy, ww, hh, width, height);
			int x = rect.x;
			int y = rect.y;
			int w = rect.width;
			int h = rect.height;
			
			if(maintainAspectRatio) {
				BufferedImage img = getImage();
				double widthRatio = w/(double)img.getWidth();
				double heightRatio = h/(double)img.getHeight();
				//find which aspect is the limiting dimension
				if(widthRatio <= heightRatio) { //the width is proportionately smaller
					int newHeight = (int)(widthRatio*img.getHeight());
					g.drawImage(img, x, y+h/2-newHeight/2, w, newHeight, null);
				}else { //the height is proportionately smaller
					int newWidth = (int)(heightRatio*img.getWidth());
					g.drawImage(img, x+w/2-newWidth/2, y, newWidth, h, null);
				}
			}else
				g.drawImage(getImage(), x, y, w, h, null);
			if(outline) {
				g.setColor(Color.BLACK);
				g.drawRect(x, y, w-1, h-1);
			}
		}
	}
	
	/**
	 * Sets the image that this view will draw when rendered.
	 * @param img {@link #image}.
	 * @return this
	 */
	public View setImage(BufferedImage img){
		this.image = img;
		return this;
	}
	/**
	 * Gets the image that this view will draw when rendered. By default this method returns
	 * {@link #image} but it can be overridden to display something else.
	 * @return {@link #image}
	 */
	public BufferedImage getImage(){
		return image;
	}
	
	/**
	 * Sets whether this view should maintain the aspect ratio of width:height when rendering.
	 * @param mar to replace {@link #maintainAspectRatio}
	 * @return this
	 */
	public View setMaintainAspectRatio(boolean mar){
		maintainAspectRatio = mar;
		return this;
	}
	/**
	 * Returns whether this view maintains the aspect ratio of width:height in drawing the picture
	 * @return {@link #maintainAspectRatio}
	 */
	public boolean isMaintainAspectRatio(){
		return maintainAspectRatio;
	}
	
	/**
	 * Sets whether or not the clickable should display a black outline on its border.
	 * @param outline {@link #outline}
	 * @return this
	 */
	public View setOutline(boolean outline){
		this.outline = outline;
		return this;
	}
	/**
	 * Returns whether or not the clickable is displaying a black outline on its border
	 * @return {@link #outline}
	 */
	public boolean getOutline(){
		return outline;
	}
}
