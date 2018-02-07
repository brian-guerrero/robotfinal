import java.io.FileNotFoundException;

import com.stonedahl.robotmaze.SimRobot;

public class SimMazeSolver {

	public static void main(String[] args) throws FileNotFoundException, InterruptedException {

		SimRobot simRobot = new SimRobot("maze1.txt", 300); // 500 ms animation
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
			} else if (distLeft > distRight && distLeft > oldDistLeft) {
				System.out.println("Turn left.");
				simRobot.left90();
				simRobot.forwardOneCell();
			} else if (distStraight > 1) {
				System.out.println("We can go straight");
				// Note: the move should always succeed, because we checked for
				// walls ahead first and
				// the simulator distance sensor is always accurate (unlike the
				// physical sensor!)
				simRobot.forwardOneCell();
				// if moveSuceeded were false, that would mean it hit a wall
				// (and the robot's bump sensor was activated)

				if (simRobot.colorSensorSeesGoal()) {
					System.out.println("FOUND GOAL!");
					System.out.println("Now, if only I could find my way home...");

					break; // break out of the FOR loop early
				}
			} else if (distRight > distStraight) {
				simRobot.right90();
			} else if (distLeft > distStraight) {
				simRobot.left90();
			} else if (distLeft == distRight) {
				simRobot.right90();
				simRobot.right90();
			}
			moves++;

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
	}

}
