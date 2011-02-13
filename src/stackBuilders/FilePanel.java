package stackBuilders;

import stackBuilders.QboFileFilter;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import java.io.*;
import java.util.*;
import java.util.regex.*;
import javax.swing.JOptionPane;

public class FilePanel {

	public static void main(String args[]) {
		JFrame frame = new JFrame("QuickBooks HSBC FileFixer");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		Container contentPane = frame.getContentPane();

		final JLabel headerLabel = new JLabel("Choose QBO File to Fix");
		headerLabel.setFont(new Font("Serif", Font.BOLD, 36));
		contentPane.add(headerLabel, BorderLayout.NORTH);

		JFileChooser fileChooser = new JFileChooser(".");
		fileChooser.setControlButtonsAreShown(true);
	    fileChooser.setFileFilter(new QboFileFilter());

		contentPane.add(fileChooser, BorderLayout.CENTER);

		ActionListener actionListener = new ActionListener() {
			public void actionPerformed(ActionEvent actionEvent) {
				JFileChooser theFileChooser = (JFileChooser) actionEvent.getSource();

				String command = actionEvent.getActionCommand();

				if (command.equals(JFileChooser.APPROVE_SELECTION)) {
					parseFile(theFileChooser.getSelectedFile());
				} else if (command.equals(JFileChooser.CANCEL_SELECTION)) {
					System.exit(0);
				}
			}
		};
		fileChooser.addActionListener(actionListener);

		frame.pack();
		frame.setVisible(true);
	}
	
	public static void parseFile(File f) {
		Scanner freader = null;
		try {
	        freader = new Scanner(f);			
		} catch (FileNotFoundException e) {
			System.err.println("File not found: " + e.getMessage());
		}

		File newFile = new File(f.getParent(), "FIXED_" + f.getName());
		System.out.println("Writing new file to " + newFile);
		
		BufferedWriter out = null;
		try {
			FileWriter fstream = new FileWriter(newFile);
			out = new BufferedWriter(fstream);
		} catch (Exception e) {
			System.err.println("Error: " + e.getMessage());
		}
		
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
        if (out != null) {
        	try { 
        		out.close();
        	} catch (IOException e) {
            	System.err.println("Caught error closing output file: " + e.getMessage());
        		
        	}
        	
        }
        JOptionPane.showMessageDialog(null, "Wrote fixed QBO file to " + newFile);
	}
}