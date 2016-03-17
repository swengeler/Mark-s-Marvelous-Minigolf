package course_designer;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;

public class StartPoint{
	 final int RADIUS = 7;
	 Point2D.Double coord;
	 Ellipse2D.Double shape;
	 Color color = Color.LIGHT_GRAY;
	
	public StartPoint(int x, int y){
		coord = new Point2D.Double(x, y);
		shape = new Ellipse2D.Double(x-(0.5*RADIUS),y-(0.5*RADIUS), RADIUS, RADIUS);		
	}
	public void draw(Graphics2D g2d){
		g2d.setColor(color);
	    g2d.fill(shape);
	    g2d.setColor(Color.BLACK);
	    g2d.draw(shape);	
	}
}
