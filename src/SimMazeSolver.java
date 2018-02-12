import java.awt.Point;
import java.io.FileNotFoundException;
import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;
import com.stonedahl.robotmaze.SimRobot;

public class SimMazeSolver {

	static char direction = '^';
	static Point coordinates = new Point();
	static boolean isBacktracking = false; // false is forward searching, true
											// is backtracking
	public static final double GODIST = .4;
	static ArrayList<Point> pointsVisited = new ArrayList<Point>();
	private static Stack<Character> movesMade;
	static ArrayBlockingQueue<Character> reversedActions;
	public static void main(String[] args) throws FileNotFoundException, InterruptedException {
		movesMade = new Stack<Character>();
		coordinates.x = 0;
		coordinates.y = 0;// home
		pointsVisited.add(new Point(coordinates.x, coordinates.y));
		SimRobot simRobot = new SimRobot("maze2.txt", 100); // 500 ms animation
															// delay...
		float distRight = 0, distStraight=0, distLeft=0;
		Thread.sleep(2000);
		// current pathfinding, just goes towards the most white space it sees
		while (simRobot.colorSensorSeesGoal() != true) {
			simRobot.neckRight90();
			distRight = simRobot.getDistanceMeasurement();
			simRobot.neckLeft90();
			distStraight = simRobot.getDistanceMeasurement();
			simRobot.neckLeft90();
			distLeft = simRobot.getDistanceMeasurement();
			System.out.print("Distances Sensed:  R: " + distRight + " S: " + distStraight + " L:" + distLeft);
			simRobot.neckRight90();
			
			if (isVisited() && !isBacktracking) {
				System.out.println("We've been here before and we're not backtracking");
				if (distRight > GODIST) {
					simRobot.right90();
					changeDirection('>');
					movesMade.push('>');
				} else if (distLeft > GODIST) {
					simRobot.left90();
					changeDirection('<');
					movesMade.push('<');
				} else if (distLeft == distRight) {
					isBacktracking = true;
				}
			} else if (isBacktracking) {
				System.out.println("We is backtracking");
				while (isBacktracking) {
					System.out.println("We is still backtracking");
					simRobot.neckRight90();
					distRight = simRobot.getDistanceMeasurement();
					simRobot.neckLeft90();
					distStraight = simRobot.getDistanceMeasurement();
					simRobot.neckLeft90();
					distLeft = simRobot.getDistanceMeasurement();
					simRobot.neckRight90();
					isBacktracking = backtrack(simRobot, distStraight, distRight, distLeft);
					// System.out.println(isBacktracking);
					// System.out.println(pointsVisited.toString());
				}
			} else {
				System.out.println("Regular operations " + hasVisitedStraight());
				if (!hasVisitedStraight() && distStraight > GODIST) {
					boolean moveSucceeded = simRobot.forwardOneCell();
					movesMade.push('^');
					changeCoord(moveSucceeded);
					pointsVisited.add(new Point(coordinates.x, coordinates.y));
				} else if (!hasVisitedRight() && distRight > GODIST) {
					simRobot.right90();
					boolean moveSucceeded = simRobot.forwardOneCell();
					changeDirection('>');
					movesMade.push('>');
					movesMade.push('^');
					changeCoord(moveSucceeded);
					pointsVisited.add(new Point(coordinates.x, coordinates.y));
				} else if (!hasVisitedLeft() && distLeft > GODIST) {
					simRobot.left90();
					boolean moveSucceeded = simRobot.forwardOneCell();
					changeDirection('<');
					movesMade.push('<');
					movesMade.push('^');
					changeCoord(moveSucceeded);
					pointsVisited.add(new Point(coordinates.x, coordinates.y));
				} else {
					simRobot.right90();
					simRobot.right90();
					movesMade.push('>');
					movesMade.push('>');
					changeDirection('>');
					changeDirection('>');
					isBacktracking = true;
				}
			}
		}
		simRobot.right90();
		changeDirection('>');
		simRobot.right90();
		changeDirection('>');
		System.out.println("Moves made: " + movesMade);
		retrace(simRobot, distRight, distStraight, distLeft);
		System.out.println(coordinates.x + " , " + coordinates.y);
		
	}

	public static void retrace(SimRobot simRobot, float distRight, float distStraight, float distLeft){
		reversedActions = new ArrayBlockingQueue<Character>(movesMade.size());
		reversedActions = reverseActions(movesMade);
		while (coordinates.x != 0 || coordinates.y != 0) {
			char temp = (char) reversedActions.remove();
			distStraight = simRobot.getDistanceMeasurement();
			System.out.println("Next move: " + temp);
			if (temp == '<') {
				simRobot.left90();
				changeDirection('<');
			} else if (temp == '>') {
				simRobot.right90();
				changeDirection('>');
			} else if (temp == '^') {
				if (distStraight > GODIST) {
					boolean moveSucceeded = simRobot.forwardOneCell();
					changeCoord(moveSucceeded);
					isBacktracking = false;
					pointsVisited.add(new Point(coordinates.x, coordinates.y));
				}
			}
		}
	}
	public static boolean isVisited() {
		boolean visited = false;
		Point temp = new Point(coordinates.x, coordinates.y);
		if (direction == '^') {
			temp.y++;
		} else if (direction == '>') {
			temp.x++;
		} else if (direction == 'v') {
			temp.y--;
		} else {
			temp.x--;
		}
		if (pointsVisited.contains(temp)) {
			visited = true;
		}
		return visited;
	}

	public static void changeDirection(char turn) {
		if (turn == '<') {
			if (direction == '^') {
				direction = '<';
			} else if (direction == '>') {
				direction = '^';
			} else if (direction == 'v') {
				direction = '>';
			} else {// direction is '<'
				direction = 'v';
			}
		} else {
			if (direction == '^') {
				direction = '>';
			} else if (direction == '>') {
				direction = 'v';
			} else if (direction == 'v') {
				direction = '<';
			} else {// direction is '<'
				direction = '^';
			}
		}
	}

	public static void changeCoord(boolean moveSucceeded) {
		if (moveSucceeded) {
			if (direction == '^') {
				coordinates.y++;
			} else if (direction == '>') {
				coordinates.x++;
			} else if (direction == 'v') {
				coordinates.y--;
			} else {// direction is '<'
				coordinates.x--;
			}
		}
	}

	/**
	 * reverses the order of the previous actions so the robot can find its way
	 * home
	 */
	public static ArrayBlockingQueue<Character> reverseActions(Stack<Character> movesMade) {
		System.out.println("called reverseActions function");
		ArrayBlockingQueue<Character> reversedActions = new ArrayBlockingQueue<Character>(movesMade.size());
		// TODO: do something to get rid of unnecessary moves
		while (!movesMade.isEmpty()) {
			char temp = (char) movesMade.pop();
			if (temp == '<') {
				reversedActions.add('>');
			} else if (temp == '>') {
				reversedActions.add('<');
			} else {// (temp=='^'){
				reversedActions.add('^');
			}
		}
		System.out.println("reversedActions: " + reversedActions);
		return reversedActions;
	}

	public static boolean hasVisitedStraight() {
		Point point = new Point(coordinates.x, coordinates.y);
		if (direction == '^') {
			point.y++;
			System.out.println(point);
			System.out.println("Points visited: " + pointsVisited);
			System.out.println(pointsVisited.contains(point) + " and its index is " + pointsVisited.indexOf(point));
			if (pointsVisited.contains(point)) {
				return true;
			}
		} else if (direction == '<') {
			point.x--;
			if (pointsVisited.contains(point)) {
				return true;
			}
		} else if (direction == '>') {
			point.x++;
			if (pointsVisited.contains(point)) {
				return true;
			}
		} else {
			point.y--;
			if (pointsVisited.contains(point)) {
				return true;
			}
		}
		return false;
	}

	public static boolean hasVisitedLeft() {
		Point point = new Point(coordinates.x, coordinates.y);
		if (direction == '^') {
			point.x--;
			if (pointsVisited.contains(point)) {
				return true;
			}
		} else if (direction == '<') {
			point.y--;
			if (pointsVisited.contains(point)) {
				return true;
			}
		} else if (direction == '>') {
			point.y++;
			if (pointsVisited.contains(point)) {
				return true;
			}
		} else {
			point.x++;
			if (pointsVisited.contains(point)) {
				return true;
			}
		}
		return false;
	}

	public static boolean hasVisitedRight() {
		Point point = new Point(coordinates.x, coordinates.y);
		if (direction == '^') {
			point.x++;
			if (pointsVisited.contains(point)) {
				return true;
			}
		} else if (direction == '<') {
			point.y++;
			if (pointsVisited.contains(point)) {
				return true;
			}
		} else if (direction == '>') {
			point.y--;
			if (pointsVisited.contains(point)) {
				return true;
			}
		} else {
			point.x--;
			if (pointsVisited.contains(point)) {
				return true;
			}
		}
		return false;
	}

	public static boolean backtrack(SimRobot simRobot, float distStraight, float distRight, float distLeft) {
		System.out.println("hindsight is 20/20");
		
		if (!hasVisitedRight() && distRight > GODIST) {
			System.out.println("we should go right");
			simRobot.right90();
			changeDirection('>');
			movesMade.push('>');
			return false;
		} else if (!hasVisitedLeft() && distLeft > GODIST) {
			System.out.println("we should go left");
			simRobot.left90();
			changeDirection('<');
			movesMade.push('<');
			return false;
		} else { // all directions visited
			if (distRight > GODIST) {
				System.out.println("we can go right");
				simRobot.right90();
				changeDirection('>');
				movesMade.push('>');
				boolean moveSucceeded = simRobot.forwardOneCell();
				changeCoord(moveSucceeded);
				pointsVisited.add(new Point(coordinates.x, coordinates.y));
				movesMade.push('^');
				return true;
			} else if (distLeft > GODIST) {
				System.out.println("we can go left");
				simRobot.left90();
				changeDirection('<');
				movesMade.push('<');
				boolean moveSucceeded = simRobot.forwardOneCell();
				changeCoord(moveSucceeded);
				pointsVisited.add(new Point(coordinates.x, coordinates.y));
				movesMade.push('^');
				return true;
			} // else this runs again and goes straight one
		}
		if (distStraight > GODIST) {
			System.out.println("Backtracking and going straight");
			// Note: the move should always succeed, because we checked for
			// walls ahead first and
			// the simulator distance sensor is always accurate (unlike the
			// physical sensor!)
			boolean moveSucceeded = simRobot.forwardOneCell();
			changeCoord(moveSucceeded);
			pointsVisited.add(new Point(coordinates.x, coordinates.y));
			movesMade.push('^');
			// if moveSuceeded were false, that would mean it hit a wall
			// (and the robot's bump sensor was activated)
		}
		return true;
	}
}
