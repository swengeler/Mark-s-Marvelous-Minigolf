package entities;

import java.awt.Color;

public class GameTile {

	private Color baseColor;
	private Color edgeColor;
	private final int SIZE = 140;
	private boolean Hole = false;
	private boolean Start = false;


	private boolean hasRightEdge = false;
	private boolean hasLeftEdge = false;
	private boolean hasTopEdge = false;
	private boolean hasBottomEdge = false;

	private boolean hasTopLeft = false;
	private boolean hasTopRight = false;
	private boolean hasBottomLeft = false;
	private boolean hasBottomRight = false;

	public GameTile() {
		baseColor = new Color(0, 55, 0);
		edgeColor = Color.LIGHT_GRAY;
	}

	public boolean getStart() {
		return Start;
	}

	public boolean getEnd() {
		return Hole;
	}

	public int getSize() {
		return SIZE;
	}

	public boolean isStart() {
		return Start;
	}

	public boolean isEnd() {
		return Hole;
	}

	public void setStart(boolean start) {
		this.Start = start;
	}

	public void setEnd(boolean end) {
		this.Hole = end;
	}

	public Color getBaseColor() {
		return baseColor;
	}

	public Color getEdgeColor() {
		return edgeColor;
	}

	public boolean hasRightEdge() {
		return hasRightEdge;
	}

	public boolean hasLeftEdge() {
		return hasLeftEdge;
	}

	public boolean hasTopEdge() {
		return hasTopEdge;
	}

	public boolean hasBottomEdge() {
		return hasBottomEdge;
	}

	public boolean hasTopLeft() {
		return hasTopLeft;
	}

	public boolean hasTopRight() {
		return hasTopRight;
	}

	public boolean hasBottomLeft() {
		return hasBottomLeft;
	}

	public boolean hasBottomRight() {
		return hasBottomRight;
	}

	public void setRightEdge(boolean t) {
		hasRightEdge = t;
	}

	public void setLeftEdge(boolean t) {
		hasLeftEdge = t;
	}

	public void setTopEdge(boolean t) {
		hasTopEdge = t;
	}

	public void setBottomEdge(boolean t) {
		hasBottomEdge = t;
	}

	public void setTopLeft(boolean t) {
		hasTopLeft = t;
	}

	public void setTopRight(boolean t) {
		hasTopRight = t;
	}

	public void setBottomLeft(boolean t) {
		hasBottomLeft = t;
	}

	public void setBottomRight(boolean t) {
		hasBottomRight = t;
	}

	public void initBall(Ball ball1) {


	}
}
