package physicsengine;

import javax.swing.*;

import entities.Ball;
import graphics.GenericPanel;
import utils.Calculator;
import utils.Line3D;
import utils.Point3D;

import java.awt.*;
import java.awt.geom.*;
import java.util.ArrayList;

public class PolygonObstacle implements Obstacle {

    private Polygon shape;
    private Color color;
    private ArrayList<Line3D> edges;
    private ArrayList<RoundObstacle> corners;
    private boolean overlay;

    public PolygonObstacle(Polygon p, boolean overlay) {
        shape = p;
        color = new Color((int)(Math.random() * 255), (int)(Math.random() * 255), (int)(Math.random() * 255));
        corners = new ArrayList<RoundObstacle>();
        for (int i = 0; i < p.xpoints.length; i++) {
            corners.add(new RoundObstacle(p.xpoints[i], p.ypoints[i], 0));
        }
        this.overlay = overlay;
    }
    public PolygonObstacle(Polygon p, boolean overlay, Color c) {
    	this(p,overlay);
    	color = c;

    }
    public PolygonObstacle(Polygon p) {
        shape = p;
        color = new Color((int)(Math.random() * 255), (int)(Math.random() * 255), (int)(Math.random() * 255));
        corners = new ArrayList<RoundObstacle>();
        for (int i = 0; i < p.xpoints.length; i++) {
            corners.add(new RoundObstacle(p.xpoints[i], p.ypoints[i], 1));
        }
    }

    public boolean isOverlay() {
		return overlay;
	}

	public void setOverlay(boolean overlay) {
		this.overlay = overlay;
	}

	public void draw(Graphics2D g2d, boolean inGame) {
        g2d.setColor(color);
        int npoints = shape.npoints;
        int[] xpoints = new int[npoints];
        int[] ypoints = new int[npoints];
        if(inGame){
        	for(int i = 0; i<npoints;i++){
        		xpoints[i] = (int)(shape.xpoints[i] * GenericPanel.PX_SCALE);
        		ypoints[i] = (int)(shape.ypoints[i] * GenericPanel.PX_SCALE);
        	}
        } else {
        	for(int i = 0; i<npoints;i++){
            	xpoints[i] = shape.xpoints[i];
            	ypoints[i] = shape.ypoints[i];
            }
        }
        Polygon Nshape = new Polygon(xpoints,ypoints,npoints);
        g2d.fill(Nshape);
        g2d.setColor(Color.BLACK);
        g2d.draw(Nshape);
    }

    public ArrayList<RoundObstacle> getCorners() {
        return corners;
    }

    public Line3D closestEdge(Ball b) {
        if (edges == null)
            computeEdges();
        Line3D closestEdge = null;
        double closest = Double.MAX_VALUE;
        double curDistance = 0;
        for (Line3D edge : edges) {
            curDistance = Calculator.distanceLinePoint(edge, b.getCenter());
            if (curDistance < closest) {
                closest = curDistance;
                closestEdge = edge;
            }
        }
        return closestEdge;
    }

    public Line3D closestEdge(Point3D p) {
        if (edges == null)
            computeEdges();
        Line3D closestEdge = null;
        double closest = Double.MAX_VALUE;
        double curDistance = 0;
        for (Line3D edge : edges) {
            curDistance = Calculator.distanceLinePoint(edge, p);
            if (curDistance < closest) {
                closest = curDistance;
                closestEdge = edge;
            }
        }
        return closestEdge;
    }

    public void computeEdges() {
        PathIterator iterator = shape.getPathIterator(null);
        double[][] curSegment = new double[shape.npoints][3];
        for (int i = 0; i < curSegment.length; i++) {
            iterator.currentSegment(curSegment[i]);
            iterator.next();
        }

        edges = new ArrayList<Line3D>();
        for (int i = 0; i < curSegment.length; i++) {
            // will have to be changed once there are slanted walls
            edges.add(new Line3D(curSegment[i % curSegment.length][0], curSegment[i % curSegment.length][1], 0, curSegment[(i + 1) % curSegment.length][0], curSegment[(i + 1) % curSegment.length][1], 0));
        }
    }

    public ArrayList<Line3D> getEdges() {
        if (edges == null)
            computeEdges();
        return edges;
    }

    public boolean contains(double x, double y) {
    	return shape.contains(x,y);
    }

	@Override
	public Point3D getReferencePoint() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void translate(int x, int y) {
		shape.translate(x, y);

	}
	@Override
	public void scale(double s) {
        for (int i = 0; i < shape.npoints; i++) {
            shape.xpoints[i] *= s;
            shape.ypoints[i] *= s;
        }
	}

}
