import java.io.FileNotFoundException;
import java.util.Stack;

import com.stonedahl.robotmaze.SimRobot;

public class SimMazeSolver {

	public static void main(String[] args) throws FileNotFoundException, InterruptedException {
		Stack movesMade =new Stack();
		SimRobot simRobot = new SimRobot("maze6.txt", 300); // 500 ms animation
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
				Thread.sleep(2000);
			}
			if (distLeft < distRight && distRight > oldDistRight) {
				System.out.println("Turn right");
				simRobot.right90();
				simRobot.forwardOneCell();
				movesMade.push('>');
			} else if (distLeft > distRight && distLeft > oldDistLeft) {
				System.out.println("Turn left.");
				simRobot.left90();
				simRobot.forwardOneCell();
				movesMade.push('<');
			} else if (distStraight > 1) {
				System.out.println("We can go straight");
				// Note: the move should always succeed, because we checked for
				// walls ahead first and
				// the simulator distance sensor is always accurate (unlike the
				// physical sensor!)
				simRobot.forwardOneCell();
				movesMade.push('^');
				// if moveSuceeded were false, that would mean it hit a wall
				// (and the robot's bump sensor was activated)

				if (simRobot.colorSensorSeesGoal()) {
					System.out.println("FOUND GOAL!");
					System.out.println("Now, if only I could find my way home...");
					movesMade.push('G');
					Stack reversedActions = reverseActions(movesMade);
					retrace(reversedActions, simRobot);
					break; // break out of the FOR loop early
				}
			} else if (distRight > distStraight) {
				simRobot.right90();
				movesMade.push('>');
			} else if (distLeft > distStraight) {
				simRobot.left90();
				movesMade.push('<');
			} else if (distLeft == distRight) {
				simRobot.right90();
				simRobot.right90();
				movesMade.push('>');
				movesMade.push('>');
			}
			moves++;
			}

			/*
			 * Pseudocode from the internet that may be useful while(myPos !=
			 * goal) //Scan left, forward, right to determine if theirs a
			 * path(edge) to an adjacent node(vertex) //create new node object
			 * and add to FIFO stack //if no new paths backtrack to deepest node
			 * on stack
			 * 
			 * //scan right and compare to a pre defined distance to a wall if
			 * (scan right > wall distance && direction = north) { CREATE new
			 * node object (X co-ordinate +1 + Y co-ordinate + east) add to
			 * stack }
			 * 
			 * elseif (scan right > wall distance && direction = east) { CREATE
			 * new node object (X co-ordinate + Y co-ordinate -1 + south) add to
			 * stack }
			 * 
			 * etc etc
			 * 
			 * scan front…
			 * 
			 * scan left…
			 * 
			 * Next move pop node object off stack
			 * 
			 * direction of travel = new direction in node object call
			 * appropriate movement methods
			 * 
			 */
		}
	/**
	 * reverses the order of the previous actions so the robot can find its way home
	 */
	public static Stack reverseActions(Stack movesMade){
		Stack reversedActions = new Stack();
		int length = movesMade.size();
		//TODO: do something to get rid of unnecessary moves
		while(!movesMade.isEmpty()){
			char temp = (char) movesMade.pop();
			if(temp=='G'){//do nothing
			}
			else if (temp=='<'){
				reversedActions.push('>');
				break;
			}
			else if (temp=='>'){
				reversedActions.push('>');
				break;
			}
			else{// (temp=='^'){
				reversedActions.push('^');
				break;
			}
		}
		return reversedActions;
	}
	public static void retrace(Stack reversedActions, SimRobot simRobot){
		while(!reversedActions.isEmpty()){
			char temp = (char) reversedActions.pop();
			if (temp=='<'){
				simRobot.left90();
				simRobot.forwardOneCell();
				break;
			}
			else if (temp=='>'){
				simRobot.right90();
				simRobot.forwardOneCell();
				break;
			}
			else{// (temp=='^'){
				simRobot.forwardOneCell();
				break;
			}
		}
	}
		//Backtracking method pseudo found online
		// Get the start location (x,y) and try to solve the maze
		/*public static int counter = 0;
		public void solve(int x, int y) {
			if (step(x,y)) {
				maze[x][y] = 'S';
				
			}
		}
		
		// Backtracking method
		public boolean step (int x, int y) {
			
			counter++;			
			//** Accept case - we found the exit **
			if (maze[x][y] == 'G') {
				return true;
			}
			
			//** Reject case - we hit a wall or our path **
			if (maze[x][y] == 'X' || maze[x][y] == '.') {
				return false;
			}
			
			/** Backtracking Step *
			
			// Mark this location as part of our path
			maze[x][y] = '.';
			boolean result;	
			
			// Try to go Right
			result = step(x, y+1);
			if (result) { return true;}
			
			// Try to go Up
			result = step(x-1, y);
			if (result) { return true;}
			
			// Try to go Left
			result = step(x, y-1);
			if (result) { return true;}		
			
			// Try to go Down
			result = step(x+1, y);
			if (result) { return true;}		
			
			
			/** Deadend - this location can't be part of the solution *
			
			// Unmark this location
			maze[x][y] = ' ';
			
			// Go back
			return false;
		}
		
		public String toString() {
			String output = "";
			for (int x=0; x<10; x++) {
				for (int y=0; y<10; y++) {
					output += maze[x][y] + " ";
				}
				output += "\n";
			}
			return output;
		}*/
	
	}


