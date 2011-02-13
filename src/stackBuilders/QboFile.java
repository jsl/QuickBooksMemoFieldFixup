package stackBuilders;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JOptionPane;

public class QboFile {
	private File qboFile;
	
	public QboFile(File f) {
		qboFile = f;
	}
	
	public void fix() {
		if (targetFile().exists()) {
			JOptionPane.showMessageDialog(null, "File with name " + targetFile() + " already exists, please remove file and try again.", "Output File Already Exists", JOptionPane.WARNING_MESSAGE);
			return;
		}
		
		Scanner freader = fileScanner();
		BufferedWriter out = bufferedWriter(targetFile());
		
		Pattern p = Pattern.compile("^(.*?<MEMO>)(.{256,})$");
		String line = null;
		while (freader.hasNextLine()) {
			line = freader.nextLine();
			Matcher m = p.matcher(line);

			try {
				if (m.find()) {
					out.write(m.group(1) + m.group(2).substring(0, 251) + " ...\r\n");
				} else {
					out.write(line + "\r\n");
				}        		
			} catch (IOException e) {
				System.err.println("Got IOException writing file: " + e.getMessage());
			}
		}

		freader.close();
		closeWriter(out);
		JOptionPane.showMessageDialog(null, "Wrote fixed QBO file to " + targetFile());
	}
	
	public File targetFile() {
		return new File(qboFile.getParent(), "FIXED_" + qboFile.getName());
	}
	
	private void closeWriter(BufferedWriter writer) {
		if (writer != null) {
			try { 
				writer.close();
			} catch (IOException e) {
				System.err.println("Caught error closing output file: " + e.getMessage());

			}
		}
	}
	
	private BufferedWriter bufferedWriter(File newFile) {
		BufferedWriter out = null;
		try {
			FileWriter fstream = new FileWriter(newFile);
			out = new BufferedWriter(fstream);
		} catch (Exception e) {
			System.err.println("Error: " + e.getMessage());
		}
		return out;
	}
	
	private Scanner fileScanner() {
		Scanner freader = null;
		try {
			freader = new Scanner(qboFile);			
		} catch (FileNotFoundException e) {
			System.err.println("File not found: " + e.getMessage());
		}
		return freader;
	}
}
