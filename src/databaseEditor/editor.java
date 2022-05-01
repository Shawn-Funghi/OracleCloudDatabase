package databaseEditor;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;

import org.json.JSONException;
import org.json.JSONObject;

public class editor {
	private String path;
	private BufferedReader br;
	private BufferedWriter bw;
	private String originalData;
	private File file;

	public editor(File file) {
		this.file = file;
		this.path = file.getAbsolutePath();
	}

	public editor(String path) {
		this.path = path;
	}

	public void createBr() throws UnsupportedEncodingException, FileNotFoundException {
		if (this.file != null) {
			this.br = new BufferedReader(new FileReader(this.file));
		} else {
			this.br = new BufferedReader(new FileReader(this.path));
		}
	}

	public void createBw(boolean append) throws UnsupportedEncodingException, FileNotFoundException, IOException {
		if (this.file != null) {
			this.bw = new BufferedWriter(new FileWriter(this.file, append));
		} else {
			this.bw = new BufferedWriter(new FileWriter(this.path, append));
		}
	}

	public void replace(String from, String to)
			throws UnsupportedEncodingException, FileNotFoundException, IOException {
		this.createBr();
		String line = "";
		String str = null;
		while ((str = this.br.readLine()) != null) {
			line = line + str.replace(from, to) + "\n";
		}
		this.createBw(false);
		this.bw.write(line);
		this.bw.flush();
		this.bw.close();
		this.br.close();
	}

	public JSONObject getJSON() throws UnsupportedEncodingException, JSONException, FileNotFoundException, IOException {
		this.createBr();
		String line = "";
		String str = null;
		while ((str = this.br.readLine()) != null) {
			line += str.trim();
		}
		this.br.close();
		return new JSONObject(line);
	}

	public String getData() throws UnsupportedEncodingException, FileNotFoundException, IOException {
		this.createBr();
		String line = "";
		String str = null;
		while ((str = this.br.readLine()) != null) {
			System.out.println(str.trim());
			line += str.trim();
		}
		this.br.close();
		return line;
	}

	public void outPutCSV(jsonObjectList List) throws UnsupportedEncodingException, FileNotFoundException, IOException {
		this.createBw(false);
		for (int i = 0; i < List.getNameData().length - 1; i++) {
			this.bw.write(List.getNameData()[i] + ",");
		}
		this.bw.write(List.getNameData()[List.getNameData().length - 1] + "\n");
		for (int i = 0; i < List.size(); i++) {
			for (int j = 0; j < List.getNameData().length - 1; j++) {
				this.bw.write(List.getObjectData()[i][j].toString() + ",");
			}
			this.bw.write(List.getObjectData()[i][List.getNameData().length - 1].toString() + "\n");
		}
		this.bw.flush();
		this.bw.close();
	}

	public stringLst csvStringList() throws UnsupportedEncodingException, FileNotFoundException, IOException {
		this.createBr();
		stringLst list = new stringLst();
		String str = null;
		while ((str = this.br.readLine()) != null) {
			list.add(str.trim());
		}
		this.br.close();
		return list;
	}
}
