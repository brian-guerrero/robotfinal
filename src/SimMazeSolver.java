import java.io.FileNotFoundException;

import com.stonedahl.robotmaze.SimRobot;


public class SimMazeSolver {

	public static void main(String[] args) throws FileNotFoundException, InterruptedException {
		
		SimRobot simRobot = new SimRobot("maze1.txt", 500); // 500 ms animation delay...

		// NOTE: The following is NOT a good algorithm for maze solving...
		//    the robot just moves straight if it can (without hitting a wall)  
		//    and otherwise it turns right 90 degrees.  (& gives up after 20 moves)
		//
		// The purpose is just to demonstrate how to use the SimRobot class...
		
		for (int robotMoveNum = 0; robotMoveNum < 20; robotMoveNum++)
		{
			simRobot.neckRight90();
			float distRight = simRobot.getDistanceMeasurement();
			simRobot.neckLeft90();
			float distStraight = simRobot.getDistanceMeasurement();
			simRobot.neckLeft90();
			float distLeft = simRobot.getDistanceMeasurement();
			System.out.print("Distances Sensed:  R: " + distRight + " S: " + distStraight + " L:" + distLeft);
			simRobot.neckRight90();
			Thread.sleep(2000);

			if (distStraight > 1) {
				System.out.println("We can go strianght");
				//Note: the move should always succeed, because we checked for walls ahead first and
				//      the simulator distance sensor is always accurate (unlike the physical sensor!)
				boolean moveSuceeded = simRobot.forwardOneCell();
				// if moveSuceeded were false, that would mean it hit a wall (and the robot's bump sensor was activated)
				
				if (simRobot.colorSensorSeesGoal()) {
					System.out.println("FOUND GOAL!");
					System.out.println("Now, if only I could find my way home...");

					break; // break out of the FOR loop early
				}
			} else if (distLeft > distRight){
				System.out.println("Need to turn right");
				simRobot.right90();
			} else  {
				System.out.println("Need to tturn left");
				simRobot.left90();
			}
		}
	}

}
