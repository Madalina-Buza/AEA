package main;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;

import org.chocosolver.solver.Solver;
import org.chocosolver.solver.constraints.Constraint;
import org.chocosolver.solver.constraints.IntConstraintFactory;
import org.chocosolver.solver.constraints.LogicalConstraintFactory;
import org.chocosolver.solver.variables.IntVar;
import org.chocosolver.solver.variables.VariableFactory;

public class Strategy1 {

	Solver solver;
	int numberOfMeetings;
	int numberOfAgents;
	int numberOfTimeslots;
	int[][] agentAttendance;
	int[][] distanceBetweenMeetings;

	IntVar[] meeting;

	public Strategy1() {
		super();
	}

	Strategy1(String fileName) throws IOException {
		try (Scanner sc = new Scanner(new File(fileName))) {

			numberOfMeetings = sc.nextInt();
			numberOfAgents = sc.nextInt();
			numberOfTimeslots = sc.nextInt();

			agentAttendance = new int[numberOfAgents][numberOfMeetings];
			distanceBetweenMeetings = new int[numberOfMeetings][numberOfMeetings];

			for (int i = 0; i < numberOfAgents; i++) {
				sc.next();
				for (int j = 0; j < numberOfMeetings; j++) {
					agentAttendance[i][j] = sc.nextInt();
				}
			}

			for (int i = 0; i < numberOfMeetings; i++) {
				sc.next();
				for (int j = 0; j < numberOfMeetings; j++) {
					distanceBetweenMeetings[i][j] = sc.nextInt();
				}
			}

			solver = new Solver("MSP Strategy 1");

			meeting = VariableFactory.enumeratedArray("all meetings", numberOfMeetings, 0, numberOfTimeslots - 1,
					solver);

			for (int m1 = 0; m1 < numberOfMeetings; m1++) {
				for (int m2 = m1 + 1; m2 < numberOfMeetings; m2++) {
					boolean meetingsNotOverlaps = theMeetingsNotOverlaps(m1, m2);
					if (!meetingsNotOverlaps) {
						Constraint eq1 = IntConstraintFactory.arithm(meeting[m1], "-", meeting[m2], ">",
								distanceBetweenMeetings[m1][m2]);
						Constraint eq2 = IntConstraintFactory.arithm(meeting[m2], "-", meeting[m1], ">",
								distanceBetweenMeetings[m1][m2]);

						solver.post(LogicalConstraintFactory.or(eq1, eq2));
					}
				}
			}
		}
	}

	boolean theMeetingsNotOverlaps(int m1, int m2) {
		boolean meetingsNotOverlap = true;
		for (int i = 0; i < numberOfAgents; i++) {
			if (agentAttendance[i][m1] == 1 && agentAttendance[i][m2] == 1) {
				return false;
			}
		}
		return meetingsNotOverlap;
	}

	boolean findSolution() {
		return solver.findSolution();
	}

	void getResult() {
		for (int i = 0; i < numberOfMeetings; i++) {
			System.out.println(i + " " + meeting[i].getValue());
		}
		try {
			Common.writeInFile(numberOfMeetings + "," + numberOfAgents + "," + numberOfTimeslots, "S1",
					solver.getMeasures().getTimeCount());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	void getTimeExecution() {
		System.out.println("time : " + solver.getMeasures().getTimeCount());
	}

}
