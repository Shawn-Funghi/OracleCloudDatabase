package databaseEditor;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class connection {
	private URL url;
	private HttpURLConnection conn;
	private int responseCode;
	private JSONObject feedback;
	private String htmlData;
	public connection(String urlString) throws MalformedURLException, IOException {
		this.url = new URL(urlString);
		this.conn = (HttpURLConnection) this.url.openConnection();
		// 設定允許輸出
		conn.setDoOutput(true);
		conn.setDoInput(true);
		// 設定不用快取
		conn.setUseCaches(false);
		// 設定維持長連線
		conn.setRequestProperty("Connection", "Keep-Alive");
		// 設定檔案字符集:
		conn.setRequestProperty("Charset", "UTF-8");
	}

	// Get

	public void get() throws ProtocolException, IOException {
		this.get(true);
	}

	public void get(boolean needFeedBack) throws ProtocolException, IOException {
		// 設定Method
		this.conn.setRequestMethod("GET");
		this.conn.connect();
		this.responseCode = this.conn.getResponseCode();
		if ((this.responseCode / 100) == 2) {
			System.out.println("debug用 獲取成功");
			// 獲取成功 再來請求返還值
			if (needFeedBack) {
				this.getFeedBack(this.conn);
			}
		} else {
			throw new ProtocolException("網頁狀態:" + conn.getResponseCode());
		}
		this.conn.disconnect();
	}

	// Delete
	public void delete() throws ProtocolException, IOException {
		this.delete(true);
	}

	public void delete(boolean needFeedBack) throws ProtocolException, IOException {
		// 設定Method
		this.conn.setRequestMethod("DELETE");
		this.conn.connect();
		this.responseCode = this.conn.getResponseCode();
		if ((this.responseCode / 100) == 2) {
			System.out.println("debug用 刪除成功");
			// 獲取成功 再來請求返還值
			if (needFeedBack) {
				this.getFeedBack(this.conn);
			}
		} else {
			throw new ProtocolException("網頁狀態:" + conn.getResponseCode());
		}
		this.conn.disconnect();
	}

	// Put
	public void put(JSONObject data) throws ProtocolException, IOException {
		this.put(data, true);
	}

	public void put(JSONObject data, boolean needFeedBack) throws ProtocolException, IOException {
		if (!(data.toString().equals("{}"))) {
			byte[] byteArrOutput = (data.toString()).getBytes();
			// 設定檔案長度
			conn.setRequestProperty("Content-Length", String.valueOf(byteArrOutput.length)); // 設定檔案型別
			// 設定檔案型別
			conn.setRequestProperty("Content-Type", "application/json");
			this.conn.setRequestMethod("PUT");
			this.conn.connect();
			OutputStream out = this.conn.getOutputStream();
			out.write(byteArrOutput);
			out.flush();
			out.close();
			if ((conn.getResponseCode() / 100) == 2) {
				System.out.println("debug用 連線成功"); // 獲取成功 再來請求返還值
				if (needFeedBack) {
					this.getFeedBack(this.conn);
				}
			} else {
				throw new ProtocolException("網頁狀態:" + conn.getResponseCode());
			}
			this.conn.disconnect();
		} else {
			System.out.println("並未有資料傳入");
		}
	}

	// Post
	public void post(JSONObject data) throws ProtocolException, IOException {
		this.post(data, true);
	}

	public void post(JSONObject data, boolean needFeedBack) throws ProtocolException, IOException {

		if (!(data.toString().equals("{}"))) {
			// 設定檔案長度
			byte[] byteArrOutput = (data.toString()).getBytes();
			conn.setRequestProperty("Content-Length", String.valueOf(byteArrOutput.length));
			// 設定檔案型別
			conn.setRequestProperty("Content-Type", "application/json");
			this.conn.setRequestMethod("POST");
			this.conn.connect();
			OutputStream out = this.conn.getOutputStream();
			out.write(byteArrOutput);
			out.flush();
			out.close();
			if ((conn.getResponseCode() / 100) == 2) {
				System.out.println("debug用 上傳成功");
				// 獲取成功 再來請求返還值
				if (needFeedBack) {
					this.getFeedBack(this.conn);
				}
			} else {
				throw new ProtocolException("網頁狀態:" + conn.getResponseCode());
			}
			this.conn.disconnect();

		} else {
			System.out.println("並未有資料傳入");
		}
	}

	public void getFeedBack(HttpURLConnection conn) throws IOException {
		InputStream in = conn.getInputStream();
		String str = null;
		byte[] data = new byte[in.available()];
		in.read(data);
		in.close();
		str = new String(data);
		this.htmlData = str;
		this.feedback = new JSONObject(str);
	}

	public JSONObject getJSONData() {
		return this.feedback;
	}
	
	public String getHTMLData() {
		return this.htmlData;
	}
}
