package databaseEditor;

import java.awt.EventQueue;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.ProtocolException;

import javax.swing.JFrame;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import javax.swing.JScrollPane;
import java.awt.BorderLayout;
import javax.swing.JPanel;
import java.awt.FlowLayout;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JTable;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;
import javax.swing.JMenuBar;
import javax.swing.JMenu;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;

public class mainPage {

	// 提供使用的註解/////////////////////////

	private JFileChooser fc;
	private JFrame frame;
	private JTable table;
	private boolean pathAvailable;
	private boolean keysAvailable;
//	資料庫的路徑
	private String path;
//	資料庫的資料
	private jsonObjectList list;
// 資料庫的key
	private String[] keys;
	private JScrollPane scrollPane;

	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					mainPage window = new mainPage();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	public mainPage() throws MalformedURLException, IOException {
//		檢查路徑，有路徑才開
		checkDataBasePath();
		if (pathAvailable) {
			this.keysAvailable = false;
			this.checkKey();
			if (keysAvailable) {
				// 配置JSONObjectList的空間
				this.list = new jsonObjectList(this.keys);
				getJSListData(this.path);
				initialize();
			} else {
				System.exit(0);
			}
		} else {
			System.exit(0);
		}

	}

	/**
	 * @wbp.parser.entryPoint
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 485, 300);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setTitle("資料庫連線成功");

		this.scrollPane = new JScrollPane();
		frame.getContentPane().add(scrollPane, BorderLayout.CENTER);

		table = new JTable();
		// Set Table Model
		table.setModel(this.getDFModal());
		// 載入資料(可放置於後面)
		scrollPane.setViewportView(table);

		JPanel buttonPanel = new JPanel();
		FlowLayout flowLayout = (FlowLayout) buttonPanel.getLayout();
		flowLayout.setAlignment(FlowLayout.RIGHT);
		frame.getContentPane().add(buttonPanel, BorderLayout.SOUTH);

		JButton btnRefresh = new JButton("重新整理");
		btnRefresh.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				btnRefresh_Click();
			}
		});
		buttonPanel.add(btnRefresh);

		JButton btnAddData = new JButton("新增資料");
		btnAddData.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				btnAddData_Click();
			}
		});
		buttonPanel.add(btnAddData);

		JButton btnDeleteData = new JButton("刪除選取的資料");
		btnDeleteData.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				btnDeleteData_Click();
			}
		});
		buttonPanel.add(btnDeleteData);

		JButton btnAlterData = new JButton("修改選取的資料");
		btnAlterData.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				btnAlterData_Click();
			}
		});
		buttonPanel.add(btnAlterData);

		JMenuBar menuBar = new JMenuBar();
		frame.setJMenuBar(menuBar);

		JMenu mnNewMenu = new JMenu("編輯");
		menuBar.add(mnNewMenu);

		JMenuItem mntmChangePath = new JMenuItem("修改資料庫路徑");
		mntmChangePath.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				mntmChangePath_Click();
			}
		});
		mnNewMenu.add(mntmChangePath);

		JMenuItem mntmChangeKeysOrder = new JMenuItem("修改欄位順序");
		mntmChangeKeysOrder.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				mntmChangeKeysOrder_Click();
			}
		});
		mnNewMenu.add(mntmChangeKeysOrder);

		JMenu mnNewMenu_1 = new JMenu("說明");
		menuBar.add(mnNewMenu_1);

		JMenuItem mntmAbout = new JMenuItem("關於");
		mntmAbout.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				mntmAbout_Click();
			}
		});
		mnNewMenu_1.add(mntmAbout);

		JMenu mnNewMenu_2 = new JMenu("功能");
		menuBar.add(mnNewMenu_2);

		JMenuItem mntmCSVOutPutSelected = new JMenuItem("將選取範圍輸出成CSV檔");
		mntmCSVOutPutSelected.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				mntmCSVOutPutSelected_Click();
			}
		});
		mnNewMenu_2.add(mntmCSVOutPutSelected);

		JMenuItem mntmCSVOutPut = new JMenuItem("將整個資料庫輸出成CSV檔");
		mntmCSVOutPut.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				mntmCSVOutPut_Click();
			}
		});
		mnNewMenu_2.add(mntmCSVOutPut);

		frame.setVisible(true);
	}

	protected void mntmChangeKeysOrder_Click() {
		getKeysDialog change = new getKeysDialog(this.frame, this.keys);
		if (change.getChanged()) {
			JSONArray jsKeys = new JSONArray();
			jsKeys = change.getKeys();
			this.keysAvailable = true;
			this.keys = new String[jsKeys.length()];
			for (int i = 0; i < keys.length; i++) {
				this.keys[i] = jsKeys.getString(i);
			}
			try {
				this.getJSListData();
				this.table.setModel(getDFModal());
			} catch (MalformedURLException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}

		}
	}

	protected void mntmCSVOutPut_Click() {
		fc = new JFileChooser();
		FileNameExtensionFilter filter = new FileNameExtensionFilter("逗號分隔值檔案 (.csv)", "csv");
		fc.setFileFilter(filter);

		int returnVal = fc.showOpenDialog(this.frame);

		if (returnVal == JFileChooser.APPROVE_OPTION) {

			editor ed = new editor(fc.getSelectedFile());
			this.refreshTable();
			try {
				ed.outPutCSV(this.list);
				JOptionPane.showMessageDialog(this.frame, "已輸出檔案", "輸出成功", JOptionPane.INFORMATION_MESSAGE);
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}

	protected void mntmCSVOutPutSelected_Click() {
		int row[] = table.getSelectedRows();
		if (row.length != 0) {
			fc = new JFileChooser();
			FileNameExtensionFilter filter = new FileNameExtensionFilter("逗號分隔值檔案 (.csv)", "csv");
			fc.setFileFilter(filter);
			int returnVal = fc.showOpenDialog(this.frame);
			if (returnVal == JFileChooser.APPROVE_OPTION) {
				editor ed = new editor(fc.getSelectedFile());
				jsonObjectList selectedList = new jsonObjectList(this.keys);
				for (int i = 0; i < row.length; i++) {
					selectedList.add(this.list.get(row[i]));
				}
				try {
					ed.outPutCSV(selectedList);
					JOptionPane.showMessageDialog(this.frame, "已輸出檔案", "輸出成功", JOptionPane.INFORMATION_MESSAGE);
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}else {
			JOptionPane.showMessageDialog(this.frame, "未選取", "Oops", JOptionPane.ERROR_MESSAGE);
		}
	}

	protected void mntmAbout_Click() {
		aboutDialog about = new aboutDialog(this.frame);
	}

	protected void mntmChangePath_Click() {
		checkPath setNewPath = new checkPath(this.frame, "修改資料庫路徑");
		// 若資料庫有資料
		// 將資料庫的Keys丟到Dialog作為檢查
		// 若資料庫沒有資料
		// 要求輸入新的欄位
		if (setNewPath.getTurn()) {
			// 成功連線
			try {
				String path = setNewPath.getURL();
				String oldPath = this.path;
				this.path = path;
				// 因為檢查資料庫的韓式是用原本的path
				// 所以必須要先把新的path取代後才能檢查
				// 但是要保留舊的
				if (this.checkDatabaseIsEmpty()) {
					// 資料庫是空的
					getKeysDialog keyDialog = new getKeysDialog(this.frame);
					if (keyDialog.getChanged()) {
						JSONArray jsKeys = new JSONArray();
						jsKeys = keyDialog.getKeys();
						this.keysAvailable = true;
						this.keys = new String[jsKeys.length()];
						for (int i = 0; i < keys.length; i++) {
							this.keys[i] = jsKeys.getString(i);
						}
						// 重新設置modal後置入
						this.list = new jsonObjectList(this.keys);
						getJSListData(this.path);
						this.refreshTable();
					} else {
						// 直接關掉視窗 返還原本的資料
						this.path = oldPath;
					}
				} else {
					// 資料庫是有資料的 傳入資料庫的資料
					getKeysDialog keyDialog = new getKeysDialog(this.frame, this.getDataBaseKeys());
					if (keyDialog.getChanged()) {
						JSONArray jsKeys = new JSONArray();
						jsKeys = keyDialog.getKeys();
						this.keysAvailable = true;
						this.keys = new String[jsKeys.length()];
						for (int i = 0; i < keys.length; i++) {
							this.keys[i] = jsKeys.getString(i);
						}
						this.refreshTable();
					} else {
						// 直接關掉視窗 返還原本的資料
						this.path = oldPath;
					}
				}
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}

		}
		try {
			this.getJSListData();
			this.table.setModel(getDFModal());
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

//////////////////////////說明欄位//////////////////////////////////////////
	// 修改欄
	protected void btnAlterData_Click() {
		DefaultTableModel model = (DefaultTableModel) table.getModel();
		int row[] = table.getSelectedRows();
		if (row.length != 0) {
			jsonObjectList altList = new jsonObjectList(this.keys);
			for (int i = 0; i < row.length; i++) {
				JSONObject data = this.list.get(row[i]);
				altList.add(data);
			}
			alterDataDialog altDialog = new alterDataDialog(this.frame, altList);
			if (altDialog.getOK()) {
				this.refreshTable();
			}
		} else {
			System.out.println("未選取");
		}
	}

	// 新增列
	protected void btnAddData_Click() {
		addDataDialog addDialog = new addDataDialog(this.frame, this.keys, this.path);
		this.refreshTable();
	}

	// 刪除列
	protected void btnDeleteData_Click() {
		DefaultTableModel model = (DefaultTableModel) table.getModel();
		int row[] = table.getSelectedRows();
		if (row.length != 0) {
			try {
				for (int i = 0; i < row.length; i++) {
					String objURL = this.list.getObjectURL(row[i]);
					new connection(objURL).delete();
				}
				refreshTable();
				JOptionPane.showMessageDialog(this.frame, "已刪除檔案", "刪除成功", JOptionPane.INFORMATION_MESSAGE);
			} catch (ProtocolException e) {
				e.printStackTrace();
			} catch (MalformedURLException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			System.out.println("未選取");
		}

	}

	// 按下重整按鈕
	protected void btnRefresh_Click() {
		refreshTable();
		JOptionPane.showMessageDialog(this.frame, "已重新整理", "重新整理成功", JOptionPane.INFORMATION_MESSAGE);
	}

	// 重新整理
	public void refreshTable() {
		DefaultTableModel model = (DefaultTableModel) table.getModel();
		// 清除後
		this.clearTable(model);
		// 要重新再拿一次資料
		try {
			this.getJSListData();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} // 如果要換排列方式的話可以從這裡下手，改以直排輸入
		for (int i = 0; i < this.list.size(); i++) {
			model.addRow(this.list.getObjectData()[i]);
		}
		this.frame.setTitle("資料已更新");
	}

	public void clearTable(DefaultTableModel model) {
		model.setRowCount(0);
	}

/////////////////////////////上方為控制區//////////////////////////////////
	// 最一開始要先獲取json檔案裡頭的DataBase網址
	public String getPath() throws UnsupportedEncodingException, FileNotFoundException, IOException {
		editor ed = new editor("./DataBasePath.json");
		this.path = ed.getJSON().getString("defaultDataBasePath");
		return this.path;
	}
	// 這邊要檢查
	// 1.檔案有無Keys
	// 1-1.若檔案有key 那檢查資料庫有無資料提供查證的keys
	// 1-1-1.如果資料庫沒有keys提供查證 就傳入不檢查的Dialog
	// 1-1-2.如果資料庫有資料，將資料抓下來檢查看名字有無正確
	// 1-2.若沒有key 自行輸入後
	// 1-2-1.如果資料庫沒有keys提供查證 就傳入不檢查的Dialog
	// 1-2-2.如果資料庫有資料，將資料抓下來檢查看名字有無正確

	// 先檢查檔案有無Keys
	public void checkKey() {
		editor ed = new editor("./DataBasePath.json");
		// 若檔案為空
		try {
			if (ed.getJSON().getJSONArray("keys").isEmpty()) {
				// 若資料庫為空
				if (this.checkDatabaseIsEmpty()) {
					// 傳入不檢查的Dialog
					getKeysDialog keyDialog = new getKeysDialog(this.frame);
					if (keyDialog.getChanged()) {
						JSONArray jsKeys = new JSONArray();
						jsKeys = keyDialog.getKeys();
						this.keysAvailable = true;
						this.keys = new String[jsKeys.length()];
						for (int i = 0; i < keys.length; i++) {
							this.keys[i] = jsKeys.getString(i);
						}
					}
				} else {
					// 若資料庫有資料 傳入DataBaseKeys
					getKeysDialog keyDialog = new getKeysDialog(this.frame, this.getDataBaseKeys());
					if (keyDialog.getChanged()) {
						JSONArray jsKeys = new JSONArray();
						jsKeys = keyDialog.getKeys();
						this.keysAvailable = true;
						this.keys = new String[jsKeys.length()];
						for (int i = 0; i < keys.length; i++) {
							this.keys[i] = jsKeys.getString(i);
						}
					}
				}
			} else {
				// 若檔案有資料
				// 檢查資料庫是否為空
				if (!this.checkDatabaseIsEmpty()) {
					// 若有 就檢查 看檔案與資料庫的資料有無一樣
					// 有的話直接用
					// 沒有的話呼叫Dialog不檢查
					// 從ed獲取原本陣列內容
					if (this.checkFileAndDataBase(ed.getJSON().getJSONArray("keys"))) {
						// true代表檔案相符
						// 直接使用本地的檔案
						String[] keysInFile = new String[ed.getJSON().getJSONArray("keys").length()];
						for (int i = 0; i < keysInFile.length; i++) {
							keysInFile[i] = ed.getJSON().getJSONArray("keys").getString(i);
						}
						this.keys = keysInFile;
						this.keysAvailable = true;
					} else {
						// 重新輸入欄位名稱
						getKeysDialog keyDialog = new getKeysDialog(this.frame, this.getDataBaseKeys());
						if (keyDialog.getChanged()) {
							JSONArray jsKeys = new JSONArray();
							jsKeys = keyDialog.getKeys();
							this.keysAvailable = true;
							this.keys = new String[jsKeys.length()];
							for (int i = 0; i < keys.length; i++) {
								this.keys[i] = jsKeys.getString(i);
							}
						}
					}

				} else {
					// 若沒有 直接用
					String[] keysInFile = new String[ed.getJSON().getJSONArray("keys").length()];
					for (int i = 0; i < keysInFile.length; i++) {
						keysInFile[i] = ed.getJSON().getJSONArray("keys").getString(i);
					}
					this.keys = keysInFile;
					this.keysAvailable = true;
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	// 再檢查資料庫有無檔案提供查證
	// true代表為空
	public boolean checkDatabaseIsEmpty() throws ProtocolException, IOException {
		connection conn = new connection(this.path);
		conn.get();
		System.out.println(conn.getJSONData().getJSONArray("items").isEmpty());
		if (conn.getJSONData().getJSONArray("items").isEmpty()) {
			return true;
		} else {
			return false;
		}
	}

	// 若非空，則將資料庫的資料取出地0分提供檢查
	public String[] getDataBaseKeys() throws MalformedURLException, IOException {
		connection conn = new connection(this.path);
		conn.get();
		int Flag = 0;
		// 因為式直接拿資料庫的資料所以需要去除掉links
		String[] keys = new String[conn.getJSONData().getJSONArray("items").getJSONObject(0).names().length() - 1];
		for (int i = 0; i < keys.length + 1; i++) {
			if (!conn.getJSONData().getJSONArray("items").getJSONObject(0).names().getString(i).equals("links")) {
				keys[Flag] = conn.getJSONData().getJSONArray("items").getJSONObject(0).names().getString(i);
				Flag++;
			}
		}
		return keys;
	}

	// 檢查檔案和資料庫檔案
	public boolean checkFileAndDataBase(JSONArray file) throws MalformedURLException, IOException {
		String[] dataBase = this.getDataBaseKeys();
		int flag = 0;
		if (dataBase.length != file.length()) {
			return false;
		}
		for (int i = 0; i < dataBase.length; i++) {
			flag = 0;
			for (int j = 0; j < file.length(); j++) {
				if (dataBase[i].equals(file.getString(j))) {
					flag = 1;
					break;
				}
			}
			// 若flag等於0或大於1代表本地資料和資料庫資料有誤
			if (flag == 0 || flag > 1) {
				return false;
			}
		}
		return true;
	}

	// 之後呼叫此函式檢查路徑
	public void checkDataBasePath() {
		try {
			connection conn = new connection(this.getPath());
			conn.get();
			// 因為連不上網路會跳例外，所以直接給true
			this.pathAvailable = true;
			// 藉由dialog獲取能不能打開
		} catch (ProtocolException e) {
			checkPath dialog = new checkPath(this.frame, "資料庫無法連接~");
			this.pathAvailable = dialog.getTurn();
			this.path = dialog.getURL();
		} catch (UnsupportedEncodingException e) {
			checkPath dialog = new checkPath(this.frame, "編碼例外");
			this.pathAvailable = dialog.getTurn();
			this.path = dialog.getURL();
		} catch (FileNotFoundException e) {
			checkPath dialog = new checkPath(this.frame, "找不到檔案 請確認路徑是否正確");
			this.pathAvailable = dialog.getTurn();
			this.path = dialog.getURL();
		} catch (JSONException e) {
			checkPath dialog = new checkPath(this.frame, "json物件錯誤");
			this.pathAvailable = dialog.getTurn();
			this.path = dialog.getURL();
		} catch (IOException e) {
			checkPath dialog = new checkPath(this.frame, "路徑為空白~");
			this.pathAvailable = dialog.getTurn();
			this.path = dialog.getURL();
		}
	}

	// 再來才能獲取資料 但這也可以單獨作為讀取資料的函式
	public void getJSListData() throws MalformedURLException, IOException {
		// 預設載入已存在的位置
		this.getJSListData(this.path);
	}

	public void getJSListData(String databasePath) throws MalformedURLException, IOException {
		connection conn = new connection(databasePath);
		conn.get();
		// 注意 獲取的陣列其Key應該之後在不同資料庫會不同 需要在json檔中獲取
		jsonObjectList jslist = new jsonObjectList(this.keys);
		JSONArray data = conn.getJSONData().getJSONArray("items");
		for (int i = 0; i < data.length(); i++) {
			jslist.add(data.getJSONObject(i));
		}
		this.list = jslist;
	}

	// 查詢資料庫資料並導入Table的函式(能夠傳回一個DFModal)
	public DefaultTableModel getDFModal() {
		DefaultTableModel model = new DefaultTableModel(this.list.getObjectData(), this.keys) {
			// 這個一定要留不然會可以編輯
			public boolean isCellEditable(int row, int column) {
				return false;
			}
		};
		return model;
	}
}
