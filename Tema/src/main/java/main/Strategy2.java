package main;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

import org.chocosolver.solver.Solver;
import org.chocosolver.solver.constraints.Constraint;
import org.chocosolver.solver.constraints.IntConstraintFactory;
import org.chocosolver.solver.constraints.LogicalConstraintFactory;
import org.chocosolver.solver.variables.IntVar;
import org.chocosolver.solver.variables.VariableFactory;

public class Strategy2 {

	Solver solver;
	int numberOfMeetings;
	int numberOfAgents;
	int numberOfTimeslots;
	int[][] agentAttendance;
	int[][] distanceBetweenMeetings;
	ArrayList<ArrayList<Integer>> meetingsAttendaceAgents;
	ArrayList<ArrayList<Integer>> agentsAttendanceMeetings;

	IntVar[][] agentsTimeslots;
	IntVar ONE;
	IntVar N;

	Strategy2(String filename) throws IOException {
		try (Scanner sc = new Scanner(new File(filename))) {
			numberOfMeetings = sc.nextInt();
			numberOfAgents = sc.nextInt();
			numberOfTimeslots = sc.nextInt();

			agentAttendance = new int[numberOfAgents][numberOfMeetings];
			distanceBetweenMeetings = new int[numberOfMeetings][numberOfMeetings];
			agentsAttendanceMeetings = new ArrayList<ArrayList<Integer>>();
			meetingsAttendaceAgents = new ArrayList<ArrayList<Integer>>();

			for (int i = 0; i < numberOfAgents; i++) {
				ArrayList<Integer> meetings = new ArrayList<Integer>();
				int n = 0;
				sc.next();
				for (int j = 0; j < numberOfMeetings; j++) {
					agentAttendance[i][j] = sc.nextInt();
					if (agentAttendance[i][j] == 1) {
						meetings.add(n, j);
						n++;
					}
				}
				agentsAttendanceMeetings.add(i, meetings);
			}

			for (int i = 0; i < numberOfMeetings; i++) {
				ArrayList<Integer> agents = new ArrayList<Integer>();
				int n = 0;
				for (int j = 0; j < numberOfAgents; j++) {
					if (agentAttendance[j][i] == 1) {
						agents.add(n, j);
						n++;
					}
				}
				meetingsAttendaceAgents.add(i, agents);
			}

			for (int i = 0; i < numberOfMeetings; i++) {
				sc.next();
				for (int j = 0; j < numberOfMeetings; j++) {
					distanceBetweenMeetings[i][j] = sc.nextInt();
				}
			}
			System.out.println("\n");

			solver = new Solver("MSP strategy 2");

			agentsTimeslots = VariableFactory.boundedMatrix("agents with the the meetings scheduled in timeslots ",
					numberOfAgents, numberOfTimeslots, -1, numberOfMeetings - 1, solver);

			IntVar ONE = VariableFactory.fixed("one", 1, solver);
			for (int i = 0; i < numberOfAgents; i++) {
				ArrayList<Integer> meetings = new ArrayList<Integer>();
				meetings = agentsAttendanceMeetings.get(i);
				for (int m = 0; m < meetings.size(); m++) {
					solver.post(IntConstraintFactory.among(ONE, agentsTimeslots[i], new int[] { meetings.get(m) }));
				}
				N = VariableFactory.fixed(numberOfTimeslots - meetings.size(), solver);
				solver.post(IntConstraintFactory.among(N, agentsTimeslots[i], new int[] { -1 }));
			}

			for (int m = 0; m < numberOfMeetings; m++) {
				ArrayList<Integer> agents = meetingsAttendaceAgents.get(m);
				if (agents.size() > 1) {
					addConstrainForTimeslot(agents, m);
				}
			}

		}
	}

	void addConstrainForTimeslot(ArrayList<Integer> agents, int m) {
		for (int a = 0; a < agents.size() - 1; a++) {
			for (int nrTimeslot = 0; nrTimeslot < numberOfTimeslots; nrTimeslot++) {
				Constraint a1 = IntConstraintFactory.arithm(agentsTimeslots[agents.get(a)][nrTimeslot], "=", m);
				Constraint a2 = IntConstraintFactory.arithm(agentsTimeslots[agents.get(a + 1)][nrTimeslot], "=", m);
				LogicalConstraintFactory.ifThen(a1, a2);
			}
		}
	}

	boolean findSolution() {
		return solver.findSolution();
	}

	void result() {
		for (int i = 0; i < numberOfAgents; i++) {
	        System.out.print("agent: " + i + ": |");
	          for (int j = 0; j < numberOfTimeslots; j++) {
	              System.out.print(agentsTimeslots[i][j].getValue() + ",");
	          }
	          System.out.println("\n");
	      }
		try {
			Common.writeInFile(numberOfMeetings + "," + numberOfAgents + "," + numberOfTimeslots, "S2",
					solver.getMeasures().getTimeCount());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	void getTimeExecution() {
		System.out.println("time: " + solver.getMeasures().getTimeCount());
	}

}
