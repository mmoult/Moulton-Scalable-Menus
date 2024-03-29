package moulton.scalable.utils;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.Collection;
import java.util.HashMap;

import moulton.scalable.utils.MenuSolver.Expression;

/**
 * GridFormatter will hold MenuComponents in a grid that can have variable margins, frames, and weights, then
 * will give the coordinate specifications for each component through {@link #findCompCoordinates(MenuComponent, Rectangle)}.
 * @author Matthew Moulton
 * @see #setFrame(String, String)
 * @see #setMargin(String, String)
 * @see #specifyRowWeight(int, double)
 * @see #specifyColumnWeight(int, double)
 */
public class GridFormatter {
	/**The maximum x and y values of the child components of the grid. This is kept track of as components
	 * are added to the grid. At run-time, the grid is split evenly into that many pieces for the x and y axes.*/
	protected Dimension gridDim = new Dimension(0,0);
	/**The components that are held at the location they are in the grid. Technically, the maximum x and y are
	 * already stored in the map, but for speed, {@link #gridDim} will keep track of that when components are
	 * added or removed.
	 * @see #addComponent(MenuComponent, int, int)
	 * @see #removeComponent(int, int, boolean)
	 * @see #getHeldComponents()*/
	protected HashMap<Point, MenuComponent> gridComponents = new HashMap<Point, MenuComponent>();
	/**Holds the values of unique row weights. At default, this map will be empty, and all shown rows will
	 * have an implied weight of 1. However, row weights can be specified otherwise with {@link #specifyRowWeight(int, double)}
	 * and they will be saved here. A row with a weight double to another row's weight will have double the
	 * width of the latter's row.
	 * @see #findYWeights(int)
	 * @see #getRowWeight(int)*/
	protected HashMap<Integer, Double> rowWeights = new HashMap<Integer, Double>();
	/**Holds the values of unique column weights. At default, this map will be empty, and all shown columns will
	 * have an implied weight of 1. However, column weights can be specified otherwise with 
	 * {@link #specifyColumnWeight(int, double)} and they will be saved here. A column with a weight double to
	 * another column's weight will have double the height of the latter's column.
	 * @see #findXWeights(int)
	 * @see #getColWeight(int)*/
	protected HashMap<Integer, Double> colWeights = new HashMap<Integer, Double>();
	
	/**Holds the used expression solver used to calculate the coordinates and weights*/
	protected MenuSolver solve = new MenuSolver();
	/**The width of the x margin. This margin will separate all elements in the x-plane. Defaults to null.
	 * @see #setMargin(String, String)
	 * @see #yMargin*/
	protected Expression xMargin = null;
	/**The height of the y margin. This margin will separate all elements in the y-plane. Defaults to null.
	 * @see #setMargin(String, String)
	 * @see #xMargin*/
	protected Expression yMargin = null;
	/**The width of the outside border. Defaults to null.
	 * @see #setFrame(String, String)
	 * @see #yFrame*/
	protected Expression xFrame = null;
	/**The height of the outside border. Defaults to null.
	 * @see #setFrame(String, String)
	 * @see #xFrame*/
	protected Expression yFrame = null;
	
	
	/**Adds a component onto the grid at the specified location. If the location is already taken by another
	 * component, comp will replace it. If the location is outside the grid size in {@link #gridDim}, the grid
	 * will expand to include it.
	 * @param comp the component to add to the grid
	 * @param x the x location of the component
	 * @param y the y location of the component*/
	public void addComponent(MenuComponent comp, int x, int y) {
		if (x >= gridDim.getWidth())
			gridDim.width = x+1;
		if (y >= gridDim.getHeight())
			gridDim.height = y+1;
		gridComponents.put(new Point(x, y), comp);
	}
	
	/**Returns the components that are held.
	 * @return more formally, returns the components in {@link #gridComponents}*/
	public Collection<MenuComponent> getHeldComponents(){
		return gridComponents.values();
	}
	
	/**Sets the {@link #xMargin} and {@link #yMargin} for this panel. The margins will
	 * be used to separate components in the grid. Thus the number of marginal dimensions
	 * for the width of a panel is (number of x components)-1, where the number of x 
	 * components is at least one. A null value indicates no margin.
	 * @param xMargin the width of the margin on the x-axis
	 * @param yMargin the height of the margin on the y-axis*/
	public void setMargin(String xMargin, String yMargin) {
		this.xMargin = (xMargin == null)? null : solve.parse(xMargin, false, false);
		this.yMargin = (yMargin == null)? null : solve.parse(yMargin, false, false);
	}
	
	/**Sets the {@link #xFrame} and {@link #yFrame} for this panel. Unlike margins, the frame
	 * will only be on the outside of the panel, not between individual components. A null
	 * value indicates no frame.
	 * @param xFrame the algebraic expression for the width of the frame
	 * @param yFrame the algebraic expression for the height of the frame.*/
	public void setFrame(String xFrame, String yFrame){
		this.xFrame = (xFrame == null)? null : solve.parse(xFrame, false, false);
		this.yFrame = (yFrame == null)? null : solve.parse(yFrame, false, false);
	}
	
	/**Deletes the component found at the location (x,y) in {@link #gridComponents}.
	 * @param x the x-value of the component to remove
	 * @param y the y-value of the component to remove
	 * @param resize whether the grid should check for a resize after the deletion.
	 * @return whether a component was removed at (x,y)*/
	public boolean removeComponent(int x, int y, boolean resize) {
		Point toRemove = new Point(x,y);
		//should check to resize even if the value at the key was null
		boolean removed = gridComponents.containsKey(toRemove);
		gridComponents.remove(toRemove);
		if(gridComponents.size() == 0) {
			//if there are no more, we need the dimensions to be 0,0
			gridDim.width = 0;
			gridDim.height = 0;
			return removed;
		}
		
		//otherwise we will have to find the new maxes to resize to
		if(removed && resize && gridDim.width>x && gridDim.height>y) {
			int maxX=0, maxY=0;
			boolean resized = true;
			for(Point p:gridComponents.keySet()) {
				if(p.x > maxX) maxX = p.x;
				if(p.y > maxY) maxY = p.y;
				//if components are found to have higher xs and ys, then the deletion of this object was in the middle
				if(maxX>=x && maxY>=y) { 
					resized = false;
					break;
				}
			}
			if(resized) {
				gridDim.width = maxX + 1;
				gridDim.height = maxY + 1;
			}
		}
		return removed;
	}
	
	/**Finds the specified component in the grid and returns its pixel coordinates. If it cannot be found,
	 * null is returned.
	 * @param comp the component to look for in the grid
	 * @param self the location and dimension of the container component in the render. Ordered as x, y, width, and height.
	 * @return the pixel coordinates for the specified component to be rendered. Ordered as x, y, width, and height.*/
	public Rectangle findCompCoordinates(MenuComponent comp, Rectangle self) {
		Point gridPoint = comp.getGridLocation();
		//The component must be in a grid for the following calculations to work!
		if(gridPoint!=null){
			Rectangle details = new Rectangle();
			//search through grid for this component
			if (gridComponents.get(gridPoint) != comp) // not found!
				return details;
			//found @ gridPoint
			
			//find children components from self
			solve.updateValues(self.width, self.height);
			//frame
			if(xFrame!=null) {
				int frame = (int)solve.evalExtended(xFrame, self.width, self.height);
				self.width -= frame*2;
				if(self.width<0)
					self.width = 0;
				else
					self.x += frame;
			}
			if(yFrame!=null) {
				int frame = (int)solve.evalExtended(yFrame, self.width, self.height);
				self.height -= frame*2;
				if(self.height<0)
					self.height = 0;
				else
					self.y += frame;
			}
			//margins
			int marginSize = 0;
			if(xMargin!=null) {
				marginSize = (int)solve.evalExtended(xMargin, self.width, self.height);
				if(marginSize<0 || self.width<1)
					marginSize = 0;
			}
			int numMargins = gridDim.width-1;
			if(numMargins<0)
				numMargins=0;
			double totalWeight = findXWeights(gridDim.width);
			details.x = self.x + (int)((self.width-marginSize*numMargins)*
					findXWeights(gridPoint.x)/totalWeight) + marginSize*gridPoint.x;
			int endPoint = self.x + (int)((self.width-marginSize*numMargins)*
					findXWeights(gridPoint.x+1)/totalWeight) + marginSize*(gridPoint.x+1);
			details.width = endPoint - details.x - marginSize;
			
			marginSize = 0;
			if(yMargin!=null) {
				marginSize = (int)solve.evalExtended(yMargin, self.width, self.height);
				if(marginSize<0 || self.height<1)
					marginSize = 0;
			}
			numMargins = gridDim.height-1;
			if(numMargins<0)
				numMargins=0;
			totalWeight = findYWeights(gridDim.height);
			details.y = self.y + (int)((self.height-marginSize*numMargins)*
					findYWeights(gridPoint.y)/totalWeight) + marginSize*gridPoint.y;
			endPoint = self.y + (int)((self.height-marginSize*numMargins)*
					findYWeights(gridPoint.y+1)/totalWeight) + marginSize*(gridPoint.y+1);
			details.height = endPoint - details.y - marginSize;
			return details;
		}
		return null;
	}
	
	/**Defines the weight for the given row. A weight of 1 is default and will delete the entry in {@link #rowWeights}
	 * @param row the row to set the weight for
	 * @param weight the weight value to set*/
	public void specifyRowWeight(int row, double weight) {
		//if the row weight will be default, remove it
		if(weight==1)
			rowWeights.remove(row);
		else
			rowWeights.put(row, weight);
	}
	/**Defines the weight for the given column. A weight of 1 is default and will delete the entry in
	 * {@link #colWeights}
	 * @param col the column to set the weight for
	 * @param weight the weight value to set*/
	public void specifyColumnWeight(int col, double weight) {
		//if the col weight will be default, remove it
		if(weight==1)
			colWeights.remove(col);
		else
			colWeights.put(col, weight);
	}
	
	/**Calculates the total weight of the rows until maxX using saved values in {@link #colWeights}.
	 * @param maxX the limit where the weight totaling should stop. Using {@link #gridDim}.width will yield
	 * a complete total.
	 * @return the total of all column weights*/
	protected double findXWeights(int maxX) {
		double runningTotal = 0;
		for(int i=0; i<maxX; i++) {
			if(colWeights.containsKey(i))
				runningTotal += colWeights.get(i);
			else //add the default of 1 to the total
				runningTotal++;
		}
		return runningTotal;
	}
	/**Calculates the total weight of the columns until maxY using saved values in {@link #rowWeights}.
	 * @param maxY the limit where the weight totaling should stop. Using {@link #gridDim}.height will yield
	 * a complete total.
	 * @return the total of all row weights*/
	protected double findYWeights(int maxY) {
		double runningTotal = 0;
		for(int i=0; i<maxY; i++) {
			if(rowWeights.containsKey(i))
				runningTotal += rowWeights.get(i);
			else //add the default of 1 to the total
				runningTotal++;
		}
		return runningTotal;
	}
	
	/**Returns the width of the saved grid.
	 * @return the width of {@link #gridDim}*/
	public int getGridWidth() {
		return gridDim.width;
	}
	/**Returns the height of the saved grid.
	 * @return the height of {@link #gridDim}*/
	public int getGridHeight() {
		return gridDim.height;
	}
	
	/**Returns the weight of the specified column.
	 * @param column the column to find the weight of
	 * @return the weight of the column specified.*/
	public Double getColWeight(int column) {
		return colWeights.get(column);
	}
	/**Returns the weight of the specified row.
	 * @param row the row to find the weight of
	 * @return the weight of the row specified.*/
	public Double getRowWeight(int row) {
		return rowWeights.get(row);
	}
	
	/**Returns the menu component in the grid at the specified location
	 * @param x the x-value for the component's location
	 * @param y the y-value for the component's location
	 * @return the component found at that location. Null is returned if nothing was found there.*/
	public MenuComponent getAt(int x, int y) {
		return gridComponents.get(new Point(x,y));
	}

}