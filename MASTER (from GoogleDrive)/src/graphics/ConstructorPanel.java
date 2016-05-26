package graphics;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Polygon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import physicsengine.Obstacle;
import physicsengine.RoundObstacle;
import physicsengine.PolygonObstacle;
import graphics.DrawPanel;
import main.MainFrame;
import entities.Course;

public class ConstructorPanel extends GenericPanel{

	private Course course = new Course();
	private DrawPanel contentPanel;
	private String currentObstacleString = "Green";
	private MainFrame frame;

	public ConstructorPanel(MainFrame frame) {
		this.setPreferredSize(new Dimension(1000, 1000));
		this.setLayout(new BorderLayout());
		JPanel titlePanel = getTitlePanel();
		JPanel midPanel = getMidPanel();
		JPanel botPanel = getBottomPanel();
		this.add(titlePanel, BorderLayout.NORTH);
		this.add(midPanel, BorderLayout.CENTER);
		this.add(botPanel, BorderLayout.SOUTH);
		this.frame = frame;
	}

	public JPanel getTitlePanel() {
		JPanel titlePanel = new JPanel();
		JLabel title = new JLabel("Course Constructor");
		titlePanel.add(title);
		return titlePanel;
	}

	public JPanel getMidPanel() {
		JPanel midPanel = new JPanel();
		midPanel.setLayout(new BorderLayout());
		JPanel drawPanel = getContentPanel();
		JPanel leftPanel = getLeftPanel();
		midPanel.add(leftPanel, BorderLayout.WEST);
		midPanel.add(drawPanel, BorderLayout.CENTER);
		return midPanel;
	}

	public JPanel getLeftPanel() {
		String[] Obstacles = {"Green", "Start", "Hole", "Square", "Circle", "Triangle"};
		JComboBox<String> obstacleList = new JComboBox<>(Obstacles);
		class obstacle implements ActionListener {
	    	public void actionPerformed(ActionEvent event) {
	    		currentObstacleString = Obstacles[obstacleList.getSelectedIndex()];
	    	}
	    }
		obstacleList.addActionListener(new obstacle());
		JPanel leftPanel = new JPanel();
		leftPanel.add(obstacleList);
		return leftPanel;
	}

	public JPanel getContentPanel() {
		DrawPanel drawPanel = new DrawPanel(course);
		contentPanel = new DrawPanel(course);
		drawPanel.add(contentPanel);
		drawPanel.addMouseListener(new MouseEvents());
		drawPanel.addMouseMotionListener(new mouseMovement());
		return drawPanel;
	}

	public JPanel getBottomPanel() {
		JPanel botPanel = new JPanel();
		JButton undoButton = new JButton("Undo");
		class undo implements ActionListener {
	    	public void actionPerformed(ActionEvent event) {
	    		course.removeObstacle();
	    		repaint();
	    	}
	    }
		undoButton.addActionListener(new undo());
		JButton resetButton = new JButton("Reset");
		class reset implements ActionListener {
	    	public void actionPerformed(ActionEvent event) {
	    		course.resetCourse();
	    		repaint();
	    	}
	    }
		resetButton.addActionListener(new reset());
		JButton saveButton = new JButton("Save");
		JButton loadButton = new JButton("Load");
		JButton playButton = new JButton("Play");
		playButton.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				// first check all the edges
				course.combineEdges();
				frame.openGame(course);
			}
		});

		botPanel.add(undoButton);
		botPanel.add(resetButton);
		botPanel.add(saveButton);
		botPanel.add(loadButton);
		botPanel.add(playButton);
		return botPanel;
	}

	public class MouseEvents implements MouseListener {

		@Override
		public void mouseClicked(MouseEvent e) {
			//nothing
		}

		@Override
		public void mouseEntered(MouseEvent e) {
			// nothing
		}

		@Override
		public void mouseExited(MouseEvent e) {
			// nothing
		}

		@Override
		public void mousePressed(MouseEvent e) {
			if (currentObstacleString.equals("Green")) {
				if (SwingUtilities.isLeftMouseButton(e)) {
					if (e.getX() >= 100 && e.getX() < 600 && e.getY() >= 20 && e.getY() < 520) {
						course.addTile((int)((e.getX() -100) / 50), (int)((e.getY() - 20) / 50));
					}
				} else if (SwingUtilities.isRightMouseButton(e)) {
					if (e.getX() >= 100 && e.getX() < 600 && e.getY() >= 20 && e.getY() < 520) {
						course.removeTile((int)((e.getX() -100) / 50), (int)((e.getY() - 20) / 50));
					}
				}
			} else if (currentObstacleString.equals("Square")) {
				if (e.getX() >= 100 && e.getX() < 600 && e.getY() >= 20 && e.getY() < 520) {
					//e.getX(), e.getY(), 40, false
					int[] xpoints = {e.getX()-20, e.getX()+20, e.getX()+20, e.getX()-20};
					int[] ypoints = {e.getY()-20, e.getY()-20, e.getY()+20, e.getY()+20};
					Obstacle obstacle = new PolygonObstacle(new Polygon(xpoints,ypoints,4),false);
					course.addObstacle(obstacle);
				}
			} else if (currentObstacleString.equals("Triangle")) {
				if (e.getX() >= 100 && e.getX() < 600 && e.getY() >= 20 && e.getY() < 520) {
					int [] xCoordinates = {e.getX(), (int) (e.getX()-(0.5*40)), (int) (e.getX()+(0.5*40))};
					int [] yCoordinates = {e.getY(), e.getY() + 40, e.getY() + 40};
					Obstacle obstacle = new PolygonObstacle(new Polygon(xCoordinates, yCoordinates,3),false);
					course.addObstacle(obstacle);
				}
			} else if (currentObstacleString.equals("Circle")) {
				if (e.getX() >= 100 && e.getX() < 600 && e.getY() >= 20 && e.getY() < 520) {
					Obstacle obstacle = new RoundObstacle(e.getX(), e.getY(), 20, false);
					course.addObstacle(obstacle);
				}
			} else if (currentObstacleString.equals("Hole")) {
				if (e.getX() >= 100 && e.getX() < 600 && e.getY() >= 20 && e.getY() < 520) {
						if (SwingUtilities.isLeftMouseButton(e)) {
							if (course.hasEndHole() || (course.checkTile((int)((e.getX() -100) / 50), (int)((e.getY() - 20) / 50)) && course.checkTileForStart((int)((e.getX() -100) / 50), (int)((e.getY() - 20) / 50)))) {
								//do nothing
							} else {
								if (course.checkTile((int)((e.getX() -100) / 50), (int)((e.getY() - 20) / 50))) {
									course.setHole((int)((e.getX() -100) / 50), (int)((e.getY() - 20) / 50));
									course.setEndHole(true);
								} else {
									course.addTile((int)((e.getX() -100) / 50), (int)((e.getY() - 20) / 50));
									course.setHole((int)((e.getX() -100) / 50), (int)((e.getY() - 20) / 50));
									course.setEndHole(true);
								}
							}
						} else if (SwingUtilities.isRightMouseButton(e)) {
							if (!course.hasEndHole()){
								//do nothing
							} else {
								if (course.checkTile((int)((e.getX() -100) / 50), (int)((e.getY() - 20) / 50))) {
									course.removeHole((int)((e.getX() -100) / 50), (int)((e.getY() - 20) / 50));
									course.setEndHole(false);
								}
							}
						}
					}
				} else if (currentObstacleString.equals("Start")) {
					if (e.getX() >= 100 && e.getX() < 600 && e.getY() >= 20 && e.getY() < 520) {
						if (SwingUtilities.isLeftMouseButton(e)) {
							if (course.hasStartPoint() || (course.checkTile((int)((e.getX() -100) / 50), (int)((e.getY() - 20) / 50)) && course.checkTileForHole((int)((e.getX() -100) / 50), (int)((e.getY() - 20) / 50)))) {
								//do nothing
							} else {
								if (course.checkTile((int)((e.getX() -100) / 50), (int)((e.getY() - 20) / 50))) {
									course.setStart((int)((e.getX() -100) / 50), (int)((e.getY() - 20) / 50));
									course.setStartPoint(true);
								} else {
									course.addTile((int)((e.getX() -100) / 50), (int)((e.getY() - 20) / 50));
									course.setStart((int)((e.getX() -100) / 50), (int)((e.getY() - 20) / 50));
									course.setStartPoint(true);
								}
							}
						} else if (SwingUtilities.isRightMouseButton(e)) {
							if (!course.hasStartPoint()){
								//do nothing
							} else {
								if (course.checkTile((int)((e.getX() -100) / 50), (int)((e.getY() - 20) / 50))) {
									course.removeStart((int)((e.getX() -100) / 50), (int)((e.getY() - 20) / 50));
									course.setStartPoint(false);
								}
							}
						}
					}
				}
				repaint();
			}

		@Override
		public void mouseReleased(MouseEvent e) {
			// nothing
		}

	}

	public class mouseMovement implements MouseMotionListener {

		boolean inGrid = false;

		@Override
		public void mouseDragged(MouseEvent e) {
			// nothing
		}

		@Override
		public void mouseMoved(MouseEvent e) {
			if (e.getX() >= 100 && e.getX() <= 600 && e.getY() >= 20 && e.getY() <= 520) {
				if (currentObstacleString.equals("Square") && e.getX() >= 60 && e.getX() <= 560 && e.getY() >= 0 && e.getY() <= 480) {
					int[] xpoints = {e.getX()-20, e.getX()+20, e.getX()+20, e.getX()-20};
					int[] ypoints = {e.getY()-20, e.getY()-20, e.getY()+20, e.getY()+20};
					Obstacle overLay = new PolygonObstacle(new Polygon(xpoints,ypoints,4),false);
					course.setOverLay(overLay);
				} else if (currentObstacleString.equals("Triangle") && e.getX() >= 120 && e.getX() <= 580 && e.getY() >= 0 && e.getY() <= 480) {
					int [] xCoordinates = {e.getX(), (int) (e.getX()-(0.5*40)), (int) (e.getX()+(0.5*40))};
					int [] yCoordinates = {e.getY(), e.getY() + 40, e.getY() + 40};
					Obstacle obstacle = new PolygonObstacle(new Polygon(xCoordinates, yCoordinates,3),false);
					course.setOverLay(obstacle);
				} else if (currentObstacleString.equals("Circle") && e.getX() >= 120 && e.getX() <= 580 && e.getY() >= 40 && e.getY() <= 500) {
					Obstacle overLay = new RoundObstacle(e.getX(), e.getY(), 20, true);
					course.setOverLay(overLay);
				}
			} else {
				course.setOverLay(null);
			}
			repaint();
		}
	}
}
