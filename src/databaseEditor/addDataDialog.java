package databaseEditor;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.ProtocolException;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;

import org.json.JSONArray;
import org.json.JSONObject;

import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;

public class addDataDialog extends JDialog {

	private final JPanel contentPanel = new JPanel();
	private Object[][] objAdd;
	private String[] name;
	private JTable tableForAddData;
	private jsonObjectList addList;
	private String dataBaseURL;
	private JFrame frame;

	////////////////////////////////

	private JFileChooser fc;
	private JFrame frameFile;

	public addDataDialog(JFrame frame, String[] name, String dataBaseURL) {
		super(frame);
		this.frame = frame;
		this.name = name;
		this.addList = new jsonObjectList(name);
		this.dataBaseURL = dataBaseURL;
		this.initialize();
		this.setLocationRelativeTo(frame);
		this.setModal(true);
		this.setVisible(true);

	}

	private void initialize() {
		setResizable(true);
		this.setTitle("新增資料");
		setBounds(100, 100, 600, 300);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(new BorderLayout(0, 0));

		JScrollPane scrollPane = new JScrollPane();
		contentPanel.add(scrollPane, BorderLayout.CENTER);

		tableForAddData = new JTable();
		this.tableForAddData.setModel(this.getDFModal());
		scrollPane.setViewportView(tableForAddData);
		{
			JPanel btnPanel = new JPanel();
			btnPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
			getContentPane().add(btnPanel, BorderLayout.SOUTH);

			JButton btnAdd = new JButton("添一筆資料到表格");
			btnAdd.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					btnAdd_Click();
				}
			});
			btnPanel.add(btnAdd);

			JButton btnDelete = new JButton("刪除所選列");
			btnDelete.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					btnDelete_Click();
				}
			});
			btnPanel.add(btnDelete);

			JButton btnInputCSV = new JButton("開啟外部資料檔案位置");
			btnInputCSV.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					btnInputCSV_Click();
				}
			});
			btnPanel.add(btnInputCSV);

			JButton btnPost = new JButton("將資料導入資料庫");
			btnPost.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					btnPost_Click();
				}
			});
			btnPanel.add(btnPost);

		}
	}

	protected void btnInputCSV_Click() {

		// Add content to the window.
		fc = new JFileChooser();
		FileNameExtensionFilter filter = new FileNameExtensionFilter("逗號分隔值檔案 (.csv)", "csv");
		fc.setFileFilter(filter);
		int returnVal = fc.showOpenDialog(this);

		if (returnVal == JFileChooser.APPROVE_OPTION) {
			editor ed = new editor(fc.getSelectedFile());
			JSONArray dataList = new JSONArray();
			stringLst strList;
			try {
				strList = ed.csvStringList();
				strList.setJSONKeyOrder(this.name);
				jsonObjectList jsList = new jsonObjectList(this.name);
				jsList = strList.getJSONList();
				for (int j = 0; j < jsList.size(); j++) {
					dataList.put(jsList.get(j));
				}
				DefaultTableModel model = (DefaultTableModel) this.tableForAddData.getModel();
				for (int j = 0; j < dataList.length(); j++) {
					Object[] data = new Object[this.name.length];
					for (int k = 0; k < this.name.length; k++) {
						data[k] = dataList.getJSONObject(j).getString(this.name[k]);
					}
					model.addRow(data);
				}
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}

		}

		/*
		 * getCSVPathDialog addCSVDialog = new getCSVPathDialog(this.frame, this.name);
		 * if (addCSVDialog.getNotEmptyState()) { DefaultTableModel model =
		 * (DefaultTableModel) this.tableForAddData.getModel(); for (int i = 0; i <
		 * addCSVDialog.getDataLength(); i++) {
		 * model.addRow(addCSVDialog.getObject()[i]); } }
		 */
	}

	protected void btnPost_Click() {
		this.addList = receiveTableData();
		if (!this.addList.isEmpty()) {
			DefaultTableModel model = (DefaultTableModel) this.tableForAddData.getModel();
			try {

				for (int i = 0; i < this.addList.size(); i++) {
					connection conn = new connection(this.dataBaseURL);
					conn.post(this.addList.get(i), false);
				}
				JOptionPane.showMessageDialog(this, "資料上傳成功", "上傳成功", JOptionPane.INFORMATION_MESSAGE);
				this.dispose();
			} catch (ProtocolException e) {
				JOptionPane.showMessageDialog(this, e.getMessage(), "Oops", JOptionPane.ERROR_MESSAGE);
			} catch (MalformedURLException e) {
				JOptionPane.showMessageDialog(this, e.getMessage(), "Oops", JOptionPane.ERROR_MESSAGE);
			} catch (IOException e) {
				JOptionPane.showMessageDialog(this, e.getMessage(), "Oops", JOptionPane.ERROR_MESSAGE);
			}
		} else {
			JOptionPane.showMessageDialog(this, "未輸入資料", "Oops", JOptionPane.ERROR_MESSAGE);
		}
	}

	public jsonObjectList receiveTableData() {
		DefaultTableModel model = (DefaultTableModel) this.tableForAddData.getModel();
		jsonObjectList list = new jsonObjectList(this.name);
		/*
		 * 以後使用者自己設定要不要填null for (int i = 0; i < model.getRowCount(); i++) { for (int j
		 * = 0; j < model.getColumnCount(); j++) { if(model.getValueAt(i, j)==null){
		 * 
		 * break; } } }
		 * 
		 */
		for (int i = 0; i < model.getRowCount(); i++) {
			JSONObject data = new JSONObject();
			for (int j = 0; j < model.getColumnCount(); j++) {
				data.put(this.name[j], model.getValueAt(i, j));
			}
			list.add(data);
		}
		return list;
	}

	protected void btnDelete_Click() {
		DefaultTableModel model = (DefaultTableModel) this.tableForAddData.getModel();
		// 這邊可能要問老師一下會出bug
		int rows[] = this.tableForAddData.getSelectedRows();
		for (int i = rows.length - 1; i >= 0; i--) {
			model.removeRow(rows[i]);
		}
	}

	protected void btnAdd_Click() {
		DefaultTableModel model = (DefaultTableModel) this.tableForAddData.getModel();
		// 新增空白欄位
		model.addRow(new Object[] { "", "", "", "", "" });
	}

	public DefaultTableModel getDFModal() {
		DefaultTableModel model = new DefaultTableModel(this.objAdd, this.name) {
			// 這個一定要留不然會可以編輯
			public boolean isCellEditable(int row, int column) {
				return true;
			}
		};
		return model;
	}
}
