package databaseEditor;

import java.util.ArrayList;

import org.json.JSONObject;

public class stringLst extends ArrayList<String> {
	private Object[][] data;
	private String[] JSONKey;
	private String[] oldJSONKey;
	private int[] OrderArray;
	private boolean nomatch = false;

	// 最一開始獲得來自database的序列
	public void setJSONKeyOrder(String[] dbStringKeyOrder) {
		this.JSONKey = dbStringKeyOrder;
		this.oldJSONKey = new String[this.JSONKey.length];
	}

	// 獲取真實資料長度
	public int getStrListSize() {
		return this.size() - 1;
	}

	public void setOldJSONKey() {
		this.setOldJSONKey(false);
	}

	// 獲得資料後設置舊key的陣列
	public void setOldJSONKey(boolean nomatch) {
		if (nomatch) {
			for (int i = 0; i < JSONKey.length; i++) {
				System.out.println(this.oldJSONKey[i]);
				this.oldJSONKey[i] = this.oldJSONKey[i].replace("\"", "");
				System.out.println(this.oldJSONKey[i]);
			}
			this.setOderArray();
		} else {
			this.oldJSONKey = this.get(0).split(",");

		}
	}

	// 然後產產生可以對應databaseKey序列的陣列
	public void setOderArray() {
		if (!this.nomatch) {
			this.setOldJSONKey();
		}
		int flag = 0;
		this.OrderArray = new int[this.JSONKey.length];
		for (int i = 0; i < this.OrderArray.length; i++) {
			flag = 0;
			for (int j = 0; j < this.OrderArray.length; j++) {
				if (this.JSONKey[i].equals(this.oldJSONKey[j])) {
					this.OrderArray[i] = j;
					flag = 1;
					break;
				}
			}
			if (flag == 0) {
				this.nomatch = true;
				this.setOldJSONKey(true);
				break;
			}
		}
	}

	// 丟超出範圍的例外
	public jsonObjectList getJSONList() {
		this.setOderArray();
		String[] key = this.JSONKey;
		jsonObjectList list = new jsonObjectList(this.JSONKey);
		for (int i = 1; i < this.size(); i++) {
			JSONObject data = new JSONObject();
			// 將舊的key順序對到資料庫的key順序
			if (this.nomatch) {
				String str = this.get(i).replace("\"", "");
				String[] dataSTRSpecial = str.split(",");
				for (int j = 0; j < key.length; j++) {
					data.put(key[j], dataSTRSpecial[this.OrderArray[j]]);
					System.out.println(data.toString());				
				}
				list.add(data);
			} else {
				String[] dataSTR = this.get(i).split(",");
				for (int j = 0; j < key.length; j++) {
					data.put(key[j], dataSTR[this.OrderArray[j]]);
					System.out.println(data.toString());
				}
				list.add(data);
			}
		}
		return list;
	}

	// 產生完jsList後再轉換成物件陣列以輸出
	public Object[][] getObjectData() {
		jsonObjectList jsList = this.getJSONList();
		Object[][] data = new Object[this.getStrListSize()][this.JSONKey.length];
		for (int i = 0; i < this.getStrListSize(); i++) {
			for (int j = 0; j < this.JSONKey.length; j++) {
				data[i][j] = jsList.get(i).get(this.JSONKey[j]);
			}
		}
		this.data = data;
		return data;
	}
}
