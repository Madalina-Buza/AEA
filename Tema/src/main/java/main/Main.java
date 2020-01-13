package main;

import java.io.IOException;

public class Main {

	public static void main(String[] args) throws IOException {

		Strategy1 msp = new Strategy1("D:\\Master\\An2\\AEA\\LAST-Tema2\\Tema2\\src\\main\\resources\\1.txt");
		if (msp.findSolution()) {
			msp.getResult();
		} else {
			System.out.println(false);
		}
		msp.getTimeExecution();

		Strategy2 msp2 = new Strategy2("D:\\Master\\An2\\AEA\\LAST-Tema2\\Tema2\\src\\main\\resources\\1.txt");
		if (msp2.findSolution()) {
			msp2.result();
		} else {
			System.out.println(false);
		}
		msp2.getTimeExecution();

		Strategy3 msp3 = new Strategy3("D:\\Master\\An2\\AEA\\LAST-Tema2\\Tema2\\src\\main\\resources\\1.txt");
		msp3.getResult();

	}

}
