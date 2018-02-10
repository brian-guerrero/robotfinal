import java.awt.Point;
import java.io.FileNotFoundException;
import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;
import com.stonedahl.robotmaze.SimRobot;

public class SimMazeSolver {
	
static char direction = '^';
static Point coordinates = new Point();
static int state = 0; //0 is searching, 1 is backtracking
static ArrayList<Point> map = new ArrayList<Point>();
	public static void main(String[] args) throws FileNotFoundException, InterruptedException {
		Stack<Character> movesMade =new Stack<Character>();
		coordinates.x = 0;
		coordinates.y = 0;//home
		map.add(coordinates);
		ArrayBlockingQueue<Character> reversedActions;
		SimRobot simRobot = new SimRobot("maze1.txt", 100); // 500 ms animation
																// delay...
		simRobot.neckRight90();
		float distRight = simRobot.getDistanceMeasurement();
		simRobot.neckLeft90();
		float distStraight = simRobot.getDistanceMeasurement();
		simRobot.neckLeft90();
		float distLeft = simRobot.getDistanceMeasurement();
		simRobot.neckRight90();
		Thread.sleep(2000);
		int moves = 1;
		// current pathfinding, just goes towards the most white space it sees
		while (simRobot.colorSensorSeesGoal() != true) {
			float oldDistRight = distRight;
			float oldDistLeft = distLeft;
			if (moves > 1) {
				simRobot.neckRight90();
				distRight = simRobot.getDistanceMeasurement();
				simRobot.neckLeft90();
				distStraight = simRobot.getDistanceMeasurement();
				simRobot.neckLeft90();
				distLeft = simRobot.getDistanceMeasurement();
				System.out.print("Distances Sensed:  R: " + distRight + " S: " + distStraight + " L:" + distLeft);
				simRobot.neckRight90();
				System.out.println(isVisited());
			}
				if (isVisited() && state == 0){
					if (distRight > distStraight) {
						simRobot.right90();
						changeDirection('>');
						movesMade.push('>');
					} else if (distLeft > distStraight) {
						simRobot.left90();
						changeDirection('<');
						movesMade.push('<');
					} 
					else if (distLeft == distRight) {
//						simRobot.right90();
//						changeDirection('>');
//						simRobot.right90();
//						changeDirection('>');
//						movesMade.push('>');
//						movesMade.push('>');
						state = 1;
					}
				} else if (state == 1){
					if (distStraight > 1) {
						System.out.println("We can go straight");
						// Note: the move should always succeed, because we checked for
						// walls ahead first and
						// the simulator distance sensor is always accurate (unlike the
						// physical sensor!)
						simRobot.forwardOneCell();
						changeCoord();
						state=0;
						Point temp = new Point();
						temp.x = coordinates.x;
						temp.y = coordinates.y;
						//map.set(map.size() -1, temp);
						map.add(map.size()-1, temp);
						movesMade.push('^');
						// if moveSuceeded were false, that would mean it hit a wall
						// (and the robot's bump sensor was activated)

					} else if (distRight > distStraight) {
						simRobot.right90();
						changeDirection('>');
						movesMade.push('>');
					} else if (distLeft > distStraight) {
						simRobot.left90();
						changeDirection('<');
						movesMade.push('<');
					} else if (distLeft == distRight) {
						simRobot.right90();
						changeDirection('>');
						simRobot.right90();
						changeDirection('>');
						movesMade.push('>');
						movesMade.push('>');
					}
				}else{
				Thread.sleep(2000);
				if (distLeft < distRight && distRight > oldDistRight) {
					System.out.println("Turn right");
					simRobot.right90();
					changeDirection('>');
					simRobot.forwardOneCell();
					changeCoord();
					state = 0;
					Point temp = new Point();
					temp.x = coordinates.x;
					temp.y = coordinates.y;
					//map.set(map.size() -1, temp);
					map.add(map.size()-1, temp);
					movesMade.push('>');
					movesMade.push('^');
					state = 0;
				} else if (distLeft > distRight && distLeft > oldDistLeft) {
					System.out.println("Turn left.");
					simRobot.left90();
					changeDirection('<');
					simRobot.forwardOneCell();
					changeCoord();
					state=0;
					Point temp = new Point();
					temp.x = coordinates.x;
					temp.y = coordinates.y;
					//map.set(map.size() -1, temp);
					map.add(map.size()-1, temp);
					movesMade.push('<');
					movesMade.push('^');
					state = 0;
				} else if (distStraight > 1) {
					System.out.println("We can go straight");
					// Note: the move should always succeed, because we checked for
					// walls ahead first and
					// the simulator distance sensor is always accurate (unlike the
					// physical sensor!)
					simRobot.forwardOneCell();
					changeCoord();
					state=0;
					Point temp = new Point();
					temp.x = coordinates.x;
					temp.y = coordinates.y;
					//map.set(map.size() -1, temp);
					map.add(map.size()-1, temp);
					movesMade.push('^');
					// if moveSuceeded were false, that would mean it hit a wall
					// (and the robot's bump sensor was activated)
	
					if (simRobot.colorSensorSeesGoal()) {
						System.out.println("FOUND GOAL!");
						System.out.println("Now, if only I could find my way home...");
						movesMade.push('G');
						
						break; // break out of the FOR loop early
					}
				} else if (distRight > distStraight) {
					simRobot.right90();
					changeDirection('>');
					movesMade.push('>');
				} else if (distLeft > distStraight) {
					simRobot.left90();
					changeDirection('<');
					movesMade.push('<');
				} else if (distLeft == distRight) {
					simRobot.right90();
					changeDirection('>');
					simRobot.right90();
					changeDirection('>');
					movesMade.push('>');
					movesMade.push('>');
					state = 1;
				}
			}
			moves++;
		}
		simRobot.right90();
		changeDirection('>');
		simRobot.right90();
		changeDirection('>');
		//System.out.println("Got to the goal " + reversedActions.isEmpty());
		System.out.println("Moves made: " + movesMade);
		reversedActions = new ArrayBlockingQueue<Character>(movesMade.size());
		reversedActions = reverseActions(movesMade);
		System.out.println(coordinates.x + " , " + coordinates.y);
		while(coordinates.x!=0 || coordinates.y!=0){
			char temp = (char) reversedActions.remove();
			simRobot.neckRight90();
			distRight = simRobot.getDistanceMeasurement();
			simRobot.neckLeft90();
			distStraight = simRobot.getDistanceMeasurement();
			simRobot.neckLeft90();
			distLeft = simRobot.getDistanceMeasurement();
			simRobot.neckRight90();
			System.out.println("Next move: " + temp);
			if (temp=='<'){
				simRobot.left90();
				changeDirection('<');
				if (distLeft > 1){
					simRobot.forwardOneCell();
					changeCoord();
					Point temp1 = new Point();
					temp1.x = coordinates.x;
					temp1.y = coordinates.y;
					//map.set(map.size() -1, temp1);
					map.add(map.size()-1, temp1);
				}
			}
			else if (temp=='>'){
				simRobot.right90();
				changeDirection('>');
				if (distRight > 1){
					simRobot.forwardOneCell();
					changeCoord();
					state = 0;
					Point temp1 = new Point();
					temp1.x = coordinates.x;
					temp1.y = coordinates.y;
					//map.set(map.size() -1, temp1);
					map.add(map.size()-1, temp1);
				}
			
			}
			else if (temp == '^'){
				if (distStraight > 1){
					simRobot.forwardOneCell();
					changeCoord();
					state = 0;
					Point temp1 = new Point();
					temp1.x = coordinates.x;
					temp1.y = coordinates.y;
					//map.set(map.size() -1, temp1);
					map.add(map.size()-1, temp1);
				}
			}
		}
	}
	public static boolean isVisited(){
		boolean visited = false;
		Point temp = new Point(coordinates.x, coordinates.y);
		if (direction == '^'){
			temp.y++;
		} else if (direction == '>'){
			temp.x++;
		} else if (direction == 'v'){
			temp.y--;
		}else{
			temp.x--;
		}
		if (map.contains(temp)){
			visited = true;
		}	
//		for (Point element : map){
//			System.out.println(element.x +  ", " + element.y);
//		}
		return visited;
	}
	public static void changeDirection(char turn){
		if(turn == '<'){
			if(direction == '^'){
				direction = '<';
			}
			else if(direction == '>'){
				direction = '^';
			}
			else if(direction == 'v'){
				direction = '>';
			}else{//direction is '<'
				direction = 'v';
			}
		}
		else{
			if(direction == '^'){
				direction = '>';
			}
			else if(direction == '>'){
				direction = 'v';
			}
			else if(direction == 'v'){
				direction = '<';
			}else{//direction is '<'
				direction = '^';
			}
		}
	}
	public static void changeCoord(){
		if(direction == '^'){
			coordinates.y++;
		}else if (direction == '>'){
			coordinates.x++;
		}else if (direction == 'v'){
			coordinates.y--;
		}else{//direction is '<'
			coordinates.x--;
		}
	}

	/**
	 * reverses the order of the previous actions so the robot can find its way home
	 */
	public static ArrayBlockingQueue<Character> reverseActions(Stack<Character> movesMade){
		System.out.println("called reverseActions function");
		ArrayBlockingQueue<Character> reversedActions = new ArrayBlockingQueue<Character>(movesMade.size());
		//TODO: do something to get rid of unnecessary moves
		while(!movesMade.isEmpty()){
			char temp = (char) movesMade.pop();
			if (temp=='<'){
				reversedActions.add('>');
			}
			else if (temp=='>'){
				reversedActions.add('<');
			}
			else{// (temp=='^'){
				reversedActions.add('^');
			}
		}
		System.out.println("reversedActions: " + reversedActions);
		return reversedActions;
	}
}


