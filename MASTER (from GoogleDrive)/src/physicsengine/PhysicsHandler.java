package physicsengine;
import  graphics.*;
import javax.swing.*;

import entities.Ball;
import entities.Hole;
import gameModes.Game;
import graphics.GamePanel;
import utils.Calculator;
import utils.Line3D;
import utils.Plane;
import utils.Point3D;

import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import utils.Vector;
import main.MainFrame;

public class PhysicsHandler {

    private static final double LOWEST_VELOCITY = 0.05;
    private static final double COEFFICIENT_OF_RESTITUTION = 0.83;
    private static final int TIME_INTERVAL = 1;				//In milliseconds;

    private Ball ball;
    private ArrayList<Obstacle> obstacles = new ArrayList<Obstacle>();
    private ArrayList<Acceleration> accelerations;
    private Point3D location1;
    private boolean firstWall = true;

    private boolean ballStopped = true;

    private Timer t;

    private Game game;

    public PhysicsHandler(Ball ball, Game game) {
        this.ball = ball;
        addObstacles();
        addAccelerations();
        this.game = game;
        t = new Timer(TIME_INTERVAL,new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				if(!isStopped())
					step();
			}
		});
    }

    public void addObstacles(ArrayList<Obstacle> obstacles){
    	this.obstacles.addAll(obstacles);
    }

    public void start(){
    	t.start();
    }

    public void pause(){
    	t.stop();
    }

    public void step() {
        ball.move();
        bounceObstacle();
        applyAccelerations();
        if (ball.getVelocity().getLength() < LOWEST_VELOCITY) {
            System.out.println("Timer stopped int point " + ball.getCenter().toString() + " because factor 0");
            ballStopped = true;
            ball.setVelocity(new Vector());
        }
    }

    public void bounceObstacle() {
        for (Obstacle o : obstacles) {
            if (ball.collides(o)) {
                System.out.println("Ball center point: x = " + ball.getX() + ", y = " + ball.getY() + ", z = " + ball.getZ());
                if (o instanceof PolygonObstacle) {
                	Point3D closestPoint = ball.pushBallOut(o);
                    System.out.println("\nClosest point: x = " + closestPoint.getX() + ", y = " + closestPoint.getY() + ", z = " + closestPoint.getZ());
                    System.out.println("Ball center point: x = " + ball.getX() + ", y = " + ball.getY() + ", z = " + ball.getZ());
                    PolygonObstacle temp = (PolygonObstacle) o;
                    Line3D l = temp.closestEdge(closestPoint);
                    System.out.println("Ball hit polygon obstacle\nLine: x1 = " + l.getX1() + " y1 = " + l.getY1() + " z1 = " + l.getZ1() + " x2 = " + l.getX2() + " y2 = " + l.getY2() + " z2 = " + l.getZ2());
                    Plane p = new Plane(new Vector(l), new Vector(0, 0, 1), l.getP1());
                    System.out.println("Ball velocity before: x = " + ball.getVelocity().getX() + ", y = " + ball.getVelocity().getY() + ", z = " + ball.getVelocity().getZ());
                    Vector refl = Calculator.reflectedVector(p, ball);
                    System.out.println("New vector outside: x = " + refl.getX() + ", y = " + refl.getY() + ", z = " + refl.getZ());
                    ball.setVelocity(Calculator.reflectedVector(p, ball));
                    System.out.println("Ball velocity after: x = " + ball.getVelocity().getX() + ", y = " + ball.getVelocity().getY() + ", z = " + ball.getVelocity().getZ());
                    ball.multiplyVWith(COEFFICIENT_OF_RESTITUTION);
                    applyAccelerations();
                    ball.move();
                    ball.move();
                } else if (o instanceof RoundObstacle) {
                	if(o instanceof Hole){
                		System.out.println("Ball hit a hole");
                		Hole tempH = (Hole) o;
                		if(tempH.isBallIn(ball)){
                			System.out.println("game over");
                			game.over();
                		}

                	/*
                	if(o instanceof Hole && firstWall){
                    	System.out.println("hit hole");
                    	location1= ball.getCenter();
                    	firstWall=false;
                    } else if(o instanceof Hole && !firstWall ){
                    	Hole temp = (Hole) o;
                    	//Point3D location2= ball.getCenter();
                    	double vx= ball.getVelocity().getX()*0.000001;
                    	double vy=ball.getVelocity().getY()*0.0000001;
                    	double t;
                    	t=((temp.getRadius()/100.0)*2)/Math.sqrt(vx*vx+vy*vy);
                    	//t=Point3D.distanceBetween(location2,location1)/Math.sqrt(vx*vx+vy*vy);
        					if((ball.getVelocity().getZ()*t+9.81*t*t)/2.0 >= ball.getRadius()/100.0){
        						System.out.println("game over");
        						ballStopped = true;
        					}
        					else{
        						System.out.println("it didnt work");
        						applyAccelerations();
        	                    ball.move();
        	                    firstWall = true;
        						}*/
                	} else {
                	ball.pushBallOut(o);
                    System.out.println("\nBall hit round obstacle");
                    RoundObstacle temp = (RoundObstacle) o;
                    Vector ccVector = new Vector(temp.getCenter().getX() - ball.getX(), temp.getCenter().getY() - ball.getY(), temp.getCenter().getZ() - ball.getZ());
                    // will probably have to be changed once the Z-coordinates matter
                    Line3D l = new Line3D(-ccVector.getY(), ccVector.getX(), 0, ccVector.getY(), -ccVector.getX(), 0);
                    Plane p = new Plane(new Vector(l.getX1() - l.getX2(), l.getY1() - l.getY2(), l.getZ1() - l.getZ2()), new Vector(0, 0, 1), l.getP1());
                    System.out.println("\nBall velocity before: x = " + ball.getVelocity().getX() + ", y = " + ball.getVelocity().getY() + ", z = " + ball.getVelocity().getZ());
                    ball.setVelocity(Calculator.reflectedVector(p, ball));
                    System.out.println("Ball velocity after: x = " + ball.getVelocity().getX() + ", y = " + ball.getVelocity().getY() + ", z = " + ball.getVelocity().getZ());
                    ball.multiplyVWith(COEFFICIENT_OF_RESTITUTION);
                    applyAccelerations();
                    ball.move();
                    ball.move();
                    }
                }

                break;
            }
        }
    }

    public void applyAccelerations() {
    	if(!ballStopped)
    		for (Acceleration a : accelerations) {
    			if(a instanceof Friction)
    				((Friction) a).update(ball.getVelocity());

    			ball.applyAcceleration(a);
    			//System.out.println("Applying Acceleration -> " + " with value " + a.toString() + " has factor " + ((Friction)a).getFactor());
    		}
    }

    public boolean isStopped(){
    	return ballStopped;
    }

    public void addObstacles() {
        obstacles = new ArrayList<Obstacle>();

        int[] x1 = {500, 650, 650, 500};
        int[] y1 = {500, 500, 650, 650};
        Polygon p1 = new Polygon(x1, y1, 4);
        PolygonObstacle o1 = new PolygonObstacle(p1);
        obstacles.add(o1);
        obstacles.addAll(o1.getCorners());

        //RoundObstacle o2 = new RoundObstacle(500, 500, 1);
        //obstacles.add(o2);

        RoundObstacle o2 = new RoundObstacle(200, 200, 50);
        //obstacles.add(o2);

    }

    public void addAccelerations() {
        accelerations = new ArrayList<Acceleration>();

        Acceleration friction = new Friction(0, 0, 0, Friction.GRASS);
        accelerations.add(friction);
    }

    public void setBallSpeed(Vector v){
    	ball.setVelocity(v);
    	ballStopped = false;
    }



}
