package main;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class Common {
	static void  writeInFile(String datas, String strategy,float time ) throws IOException {
		BufferedWriter output=null;
		try {
			output = new BufferedWriter(new FileWriter("D:\\Master\\An2\\AEA\\LAST-Tema2\\Tema2\\src\\main\\resources\\tests.csv", true));
			output.append(datas+","+strategy+","+time);
			output.newLine();
		} catch (IOException e) {
			e.printStackTrace();
		}finally{
			output.close();
		}
	}

}
