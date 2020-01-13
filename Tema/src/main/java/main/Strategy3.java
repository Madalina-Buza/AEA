package main;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import org.chocosolver.solver.Solver;

public class Strategy3 {

	Solver solver;
	int numberOfMeetings;
	int numberOfAgents;
	int numberOfTimeslots;
	int[][] agentAttendance;
	int[][] distanceBetweenMeetings;

	Map<Integer, List<Integer>> possibleSlotsForMeeting;
	Map<Integer, Integer> meetingsTimeslots;
	List<Integer> possibleSchedule = new ArrayList<>();
	float sec =0;
	
	public Strategy3() {
		super();
	}

	Strategy3(String fileName) throws IOException {
		 long startTime = System.currentTimeMillis();
		possibleSlotsForMeeting = new HashMap<Integer, List<Integer>>();
		meetingsTimeslots = new HashMap<>();
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

			findSolution();

		}
		long endTime = System.currentTimeMillis();
		sec = (((endTime - startTime) / 1000F) % 60);//(endTime - startTime) / 1000F; 
		System.out.println("time: "+sec + " seconds");
	}

	void constrain(int m1, int m2) {
		// BitSet b = new BitSet();
		int i = 0;
		// for (int i = 0; i < numberOfTimeslots; i++) {
		for (int j = i + 1; j < numberOfTimeslots; j++) {
			if (Math.abs(possibleSlotsForMeeting.get(m1).get(i)
					- possibleSlotsForMeeting.get(m2).get(j)) > distanceBetweenMeetings[m1][m2]) {
				// b.set(i);
				if (meetingsTimeslots.get(m1) == null) {
					meetingsTimeslots.put(m1, possibleSlotsForMeeting.get(m1).get(i));
				}

			}
			if (Math.abs(possibleSlotsForMeeting.get(m2).get(i)
					- possibleSlotsForMeeting.get(m1).get(j)) > distanceBetweenMeetings[m1][m2]) {
				// b.set(j);
				if (meetingsTimeslots.get(m2) == null) {
					meetingsTimeslots.put(m2, possibleSlotsForMeeting.get(m2).get(j));
				}
			}
		}
	}

	void findSolution() {
		Map<Integer, List<Integer>> map = new HashMap<>();
		map = getAgentsMeetings();
		possibleSchedule = initialiazeList();

		for (Map.Entry<Integer, List<Integer>> entry : map.entrySet()) {
			List<Integer> values = entry.getValue();
			Integer distance = 0;
			Integer positionTimeslot = 0;
			for (int i = 0; i < values.size() - 1; i++) {
				distance = distanceBetweenMeetings[values.get(i)][values.get(i + 1)];
				positionTimeslot = values.get(i + 1);
				if (distance > possibleSchedule.get(positionTimeslot)) {
					possibleSchedule.set(positionTimeslot, distance);
				}
			}

			//modify in order the meeting is not overlapped
			for (Map.Entry<Integer, List<Integer>> entry2 : map.entrySet()) {
				List<Integer> valuess = entry2.getValue();
				Integer dist = 0;
				Integer posTimeslot = 0;
				for (int i = 0; i < valuess.size(); i++) {
					if (i < valuess.size() - 1) {
						dist = distanceBetweenMeetings[valuess.get(i)][valuess.get(i + 1)];
						posTimeslot = valuess.get(i + 1);
						if (dist > possibleSchedule.get(posTimeslot)) {
							possibleSchedule.set(posTimeslot, possibleSchedule.get(posTimeslot) + dist);
						}
					} else {
						if (i > 0
								&& (possibleSchedule.get(valuess.get(i - 1)) > possibleSchedule.get(valuess.get(i)))) {
							possibleSchedule.set(valuess.get(i),
									possibleSchedule.get(valuess.get(i - 1)) + possibleSchedule.get(valuess.get(i)));
						}
					}
				}
			}
		}
	}

	List<Integer> initialiazeList() {
		List<Integer> list = new ArrayList<>();
		for (int i = 0; i < numberOfMeetings; i++) {
			list.add(0);
		}
		return list;
	}

	Map<Integer, List<Integer>> getAgentsMeetings() {
		Map<Integer, List<Integer>> map = new HashMap<>();
		for (int i = 0; i < agentAttendance.length; i++) {
			List<Integer> list = new ArrayList<>();
			for (int j = 0; j < agentAttendance[i].length; j++) {
				if (agentAttendance[i][j] == 1) {
					list.add(j);
				}
			}
			map.put(i, list);
		}
		return map;
	}

	boolean canbeparallel(int m1, int m2) {
		boolean meetingsNotOverlap = true;
		for (int i = 0; i < numberOfAgents; i++) {
			if (agentAttendance[i][m1] == 1 && agentAttendance[i][m2] == 1) {
				return false;
			}
		}
		return meetingsNotOverlap;
	}

	void getResult() {
		for (int i = 0; i < numberOfMeetings; i++) {
			System.out.println("Meeting: " + i + " Timeslot: " + possibleSchedule.get(i) );
		}
		for (int i = 0; i < numberOfMeetings; i++) {
			try {
				Common.writeInFile(numberOfMeetings + "," + numberOfAgents + "," + numberOfTimeslots, "S3",sec);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

}
