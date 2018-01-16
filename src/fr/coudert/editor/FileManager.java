package fr.coudert.editor;

import java.io.*;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;

public class FileManager {

	public static String readFile(String path) {
		try {
			BufferedReader reader = new BufferedReader(new FileReader(path));
			StringBuilder result = new StringBuilder();
			int c;
			try {
				while((c = reader.read()) != -1)
					result.append((char) c);
				reader.close();
				return result.toString();
			} catch (IOException e) {
				e.printStackTrace();
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static void writeFile(String path, String text) {
		try {
			FileWriter writer = new FileWriter(new File(path));
			writer.write(text);
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static String chooseFile(boolean save) {
		String result = null;
		JFileChooser chooser = new JFileChooser("./saves/");
		chooser.setFileFilter(new FileFilter() {
			public String getDescription() {
				return "Fichiers Voxels (*.vox)";
			}
			public boolean accept(File f) {
				return f.getAbsolutePath().toLowerCase().endsWith(".vox") || f.isDirectory();
			}
		});
		chooser.setAcceptAllFileFilterUsed(false);
		if((save && chooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) || chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
			result = chooser.getSelectedFile().getAbsolutePath();
			if(save && !result.toLowerCase().endsWith(".vox"))
				result += ".vox";
		}
		return result;
	}

}