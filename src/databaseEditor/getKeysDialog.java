package databaseEditor;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

import org.json.JSONArray;
import org.json.JSONException;

public class getKeysDialog extends JDialog {

	private final JPanel contentPanel = new JPanel();
	private JTable tablePath;
	private String[] path = new String[] { "編號", "欄位名稱" };
	// 檢查的keys
	private String[] beforeKeys;
	private String[] keysReceived;
	private JSONArray keys;
	private Object[][] obj;
	private boolean hasBlank;
	private boolean checkOn;
	private boolean changed;

//////////////////////////////////////////////////////////////////
	// 有檢查要使的話
	public getKeysDialog(JFrame frame, String[] beforeKeys) {
		super(frame);
		this.beforeKeys = beforeKeys;
		this.dfModalObjectSetup();
		// checkOn表示為檢查模式
		this.checkOn = true;
		// 提供判斷有無按下OK按鈕
		this.changed = false;

		this.setTitle("換順序");
		this.initialize();
		this.setLocationRelativeTo(frame);
		this.setModal(true);
		this.setVisible(true);
	}

	// 最一開始，無法從資料中獲取欄位
	public getKeysDialog(JFrame frame) {
		super(frame);
		// checkOn為False表示為非檢查模式
		this.checkOn = false;
		// 提供判斷有無按下OK按鈕
		this.changed = false;
		this.setTitle("未給予欄位名稱");
		this.initialize();
		this.setLocationRelativeTo(frame);
		this.setModal(true);
		this.setVisible(true);
	}

	private void initialize() {
		setResizable(false);
		setBounds(100, 100, 550, 300);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(new BorderLayout(0, 0));
		{
			JScrollPane scrollPane = new JScrollPane();
			contentPanel.add(scrollPane);
			{
				tablePath = new JTable();
				// 此處若先前有呼叫設置DF的韓式就會有東西先跑出來
				this.tablePath.setModel(getDFModal());
				scrollPane.setViewportView(tablePath);
			}
		}
		{
			JPanel buttonPane = new JPanel();
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			buttonPane.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));

			JButton btnAddPath = new JButton("新增");
			btnAddPath.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					btnAddPath_Click();
				}
			});
			buttonPane.add(btnAddPath);

			JButton btnDeletePath = new JButton("刪除");
			btnDeletePath.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					btnDeletePath_Click();
				}
			});
			buttonPane.add(btnDeletePath);

			JButton btnOK = new JButton("OK");
			btnOK.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					btnOK_Click();
				}
			});
			btnOK.setActionCommand("OK");
			buttonPane.add(btnOK);
			getRootPane().setDefaultButton(btnOK);

		}
	}

	// 一旦按下OK會觸發很多事情
	// 1.假如是用來換位置的話
	// 傳入原來的keynames提供查證
	// 2.如果資料庫已經有資料可以提供查證的話
	// 傳資料庫的keynames近來提供查證
	// 3.如果連資料庫都沒有資料的話，只要發生Array或是其他錯誤就是代表欄位有錯

	// 如果傳入陣列提供查證的話呼叫此函示將DFModal所需的物件陣列進行排序
	public void dfModalObjectSetup() {
		this.obj = new Object[beforeKeys.length][2];
		for (int i = 0; i < beforeKeys.length; i++) {
			// 欄位
			this.obj[i][0] = i + 1;
			// 欄位名稱
			this.obj[i][1] = this.beforeKeys[i];
		}
	}

	protected void btnOK_Click() {
		// 若為檢察模式
		if (this.checkOn) {
			// 先回收Table的資訊
			this.receiveTableData();
			// 在進行檢查
			if (this.checkOldAndReceive()) {
				// 將檔案寫入
				try {
					this.newKeysWriteIn();
					this.changed = true;
					JOptionPane.showMessageDialog(this, "成功更改", "成功", JOptionPane.INFORMATION_MESSAGE);
					this.dispose();
				} catch (UnsupportedEncodingException e) {
					JOptionPane.showMessageDialog(this, e.getMessage(), "Oops", JOptionPane.ERROR_MESSAGE);
				} catch (FileNotFoundException e) {
					JOptionPane.showMessageDialog(this, "找不到檔案", "Oops", JOptionPane.ERROR_MESSAGE);
				} catch (JSONException e) {
					JOptionPane.showMessageDialog(this, "欄位出現問題", "Oops", JOptionPane.ERROR_MESSAGE);
				} catch (IOException e) {
					JOptionPane.showMessageDialog(this, e.getMessage(), "Oops", JOptionPane.ERROR_MESSAGE);

				}
			}else {
				JOptionPane.showMessageDialog(this, "與原欄位名稱不相符", "Oops", JOptionPane.ERROR_MESSAGE);
			}
		} else {
			// 非檢查模式
			DefaultTableModel model = (DefaultTableModel) this.tablePath.getModel();
			this.receiveTableData();
			// 檢查有無空白
			if (!this.hasBlank) {
				// 以防沒有任何東西輸入
				if (model.getRowCount() > 0) {
					try {
						this.newKeysWriteIn();
						this.changed = true;
						JOptionPane.showMessageDialog(this, "成功更改", "成功", JOptionPane.INFORMATION_MESSAGE);
						this.dispose();
					} catch (UnsupportedEncodingException e) {
						JOptionPane.showMessageDialog(this, e.getMessage(), "Oops", JOptionPane.ERROR_MESSAGE);
					} catch (FileNotFoundException e) {
						JOptionPane.showMessageDialog(this, "找不到檔案", "Oops", JOptionPane.ERROR_MESSAGE);
					} catch (JSONException e) {
						JOptionPane.showMessageDialog(this, "欄位出現問題", "Oops", JOptionPane.ERROR_MESSAGE);
					} catch (IOException e) {
						JOptionPane.showMessageDialog(this, e.getMessage(), "Oops", JOptionPane.ERROR_MESSAGE);
					}
				} else {
					JOptionPane.showMessageDialog(this, "未輸入", "Oops", JOptionPane.ERROR_MESSAGE);
				}
			} else {
				JOptionPane.showMessageDialog(this, "有空格", "Oops", JOptionPane.ERROR_MESSAGE);
			}
		}

	}

	// 將要傳入的新key換成JSONArray後再寫入
	public void newKeysWriteIn()
			throws UnsupportedEncodingException, FileNotFoundException, JSONException, IOException {
		JSONArray keys = new JSONArray();
		for (int i = 0; i < this.keysReceived.length; i++) {
			keys.put(i, this.keysReceived[i]);
		}
		this.keys = keys;
		// 將原本value的值換成新輸入的值
		editor ed = new editor("./DataBasePath.json");
		// 先從ed獲取原本陣列內容並替換掉
		ed.replace(ed.getJSON().getJSONArray("keys").toString(), keys.toString());
	}

	// 獲取欄位名稱
	public JSONArray getKeys() {
		return this.keys;
	}

	// 確保有按下新增按鈕
	public boolean getChanged() {
		return this.changed;
	}

	// 換順序時 檢查收回的key有沒有跟傳入的key數量一樣 且 名字一樣
	// 如果正確才傳true 並將新的key寫入檔案中
	public boolean checkOldAndReceive() {
		int flag = 0;
		if (this.beforeKeys.length == this.keysReceived.length) {
			for (int i = 0; i < this.beforeKeys.length; i++) {
				flag = 0;
				for (int j = 0; j < this.keysReceived.length; j++) {
					if (this.beforeKeys[i].equals(this.keysReceived[j])) {
						flag++;
					}
				}
				if (flag == 0 || flag != 1) {
					return false;
				}
			}
			return true;
		} else {
			return false;
		}
	}

	// 收回表格的內容
	public void receiveTableData() {
		this.hasBlank = false;
		// 預設表格沒有空白
		DefaultTableModel model = (DefaultTableModel) this.tablePath.getModel();
		String[] list = new String[model.getRowCount()];
		for (int i = 0; i < model.getRowCount(); i++) {
			list[i] = model.getValueAt(i, 1).toString();
			if (model.getValueAt(i, 1).toString().equals("")) {
				// 發現有空白就設為true
				this.hasBlank = true;
			}
		}
		// 將得到的key儲存
		this.keysReceived = list;

	}

	// 刪除所選欄位
	protected void btnDeletePath_Click() {
		DefaultTableModel model = (DefaultTableModel) this.tablePath.getModel();
		int rows[] = this.tablePath.getSelectedRows();
		for (int i = 0; i < rows.length; i++) {
			model.removeRow(rows[i]);
		}
	}

	// 增加欄位用
	protected void btnAddPath_Click() {
		DefaultTableModel model = (DefaultTableModel) this.tablePath.getModel();
		model.addRow(new Object[] { model.getRowCount() + 1 + "", "" });
	}

	public DefaultTableModel getDFModal() {
		DefaultTableModel model = new DefaultTableModel(this.obj, this.path) {
			// 這個一定要留不然會可以編輯
			public boolean isCellEditable(int row, int column) {
				if (column == 1) {
					return true;
				} else {
					return false;
				}
			}
		};
		return model;
	}

}
