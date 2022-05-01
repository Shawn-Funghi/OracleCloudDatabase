package databaseEditor;

import java.util.ArrayList;

import org.json.JSONObject;

public class jsonObjectList extends ArrayList<JSONObject> {
	private Object[][] data;
	private String[] nameKey;
	private String[] objPath;
	private int linkPosition;
	
	public jsonObjectList(String[] keys) {
		this.nameKey = keys;
	}
	
	public String[] getNameData() {
		return this.nameKey;
	}

	public void setObjectData() {
		this.data = new Object[this.size()][this.nameKey.length];
		this.objPath = new String[this.size()];
		for (int i = 0; i < this.size(); i++) {
			// 如果links改名字的話...
			this.objPath[i] = this.get(i).getJSONArray("links").getJSONObject(0).getString("href");
			for (int j = 0; j < this.nameKey.length; j++) {
				this.data[i][j] = this.get(i).get(this.nameKey[j]);
			}
		}
	}
	public Object[][] getObjectData() {
		if (this.data == null) {
			this.setObjectData();
		}
		return this.data;
	}

	public String getObjectURL(int index) {
		// 這裡要修的更General
		if(this.objPath==null) {
			this.setObjectData();
		}
		return this.objPath[index];
	}
}
