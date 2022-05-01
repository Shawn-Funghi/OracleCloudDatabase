package databaseEditor;

import java.awt.BorderLayout;
import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

import org.json.JSONArray;
import org.json.JSONObject;

import javax.swing.JScrollPane;
import javax.swing.JTable;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.awt.event.ActionEvent;

public class getCSVPathDialog extends JDialog {

	private final JPanel contentPanel = new JPanel();
	private JTable tablePath;
	private String[] path = new String[] { "編號", "路徑位置" };
	private Object[][] obj;
	private String[] JSONKey;
	private String[] pathString;
	private boolean notEmpty;
	private boolean hasBlank;
	private JSONArray dataJSONList;

	public getCSVPathDialog(JFrame frame, String[] JSONKey) {
		super(frame);
		this.JSONKey = JSONKey;
		this.notEmpty = false;
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

	protected void btnOK_Click() {
		DefaultTableModel model = (DefaultTableModel) this.tablePath.getModel();
		this.receiveTableData();
		if (model.getRowCount() > 0) {
			int flag = 0;
			JSONArray dataList = new JSONArray();

			try {
				for (int i = 0; i < this.path.length; i++) {
					editor ed = new editor(this.path[i]);
					flag = i + 1;
					stringLst strList = ed.csvStringList();
					strList.setJSONKeyOrder(this.JSONKey);
					for (int j = 0; j < strList.getStrListSize(); j++) {
						dataList.put(strList.getJSONList().get(j));
					}
				}
				this.dataJSONList = dataList;
				this.notEmpty = true;
				this.dispose();
			} catch (ArrayIndexOutOfBoundsException e) {
				JOptionPane.showMessageDialog(this, "檔案" + flag + "的表格的Table序列有誤", "Oops", JOptionPane.ERROR_MESSAGE);
			} catch (UnsupportedEncodingException e) {
				JOptionPane.showMessageDialog(this, "不支援的編碼", "Oops", JOptionPane.ERROR_MESSAGE);
			} catch (FileNotFoundException e) {
				if (this.hasBlank) {
					JOptionPane.showMessageDialog(this, "有未輸入的空格", "有空格", JOptionPane.ERROR_MESSAGE);
				} else {
					JOptionPane.showMessageDialog(this, "沒有找到檔案~", "Oops", JOptionPane.ERROR_MESSAGE);
				}
			} catch (IOException e) {
				JOptionPane.showMessageDialog(this, "輸出輸入有誤", "Oops", JOptionPane.ERROR_MESSAGE);
			}

		} else {
			JOptionPane.showMessageDialog(this, "請輸入檔案位置", "空的~", JOptionPane.ERROR_MESSAGE);
		}
	}

	public int getDataLength() {
		return this.dataJSONList.length();
	}

	public Object[][] getObject() {
		Object[][] data = new Object[this.dataJSONList.length()][this.JSONKey.length];
		for (int i = 0; i < this.dataJSONList.length(); i++) {
			for (int j = 0; j < this.JSONKey.length; j++) {
				data[i][j] = this.dataJSONList.getJSONObject(i).get(this.JSONKey[j]);
			}
		}
		return data;
	}

	public String[] getPathSting() {
		return this.path;
	}

	public boolean getNotEmptyState() {
		return this.notEmpty;
	}

	public void receiveTableData() {
		this.hasBlank = false;
		DefaultTableModel model = (DefaultTableModel) this.tablePath.getModel();
		String[] list = new String[model.getRowCount()];
		for (int i = 0; i < model.getRowCount(); i++) {
			list[i] = model.getValueAt(i, 1).toString();
			if (model.getValueAt(i, 1).toString().equals("")) {
				this.hasBlank = true;
			}
		}
		this.path = list;

	}

	protected void btnDeletePath_Click() {
		DefaultTableModel model = (DefaultTableModel) this.tablePath.getModel();
		int rows[] = this.tablePath.getSelectedRows();
		for (int i = 0; i < rows.length; i++) {
			model.removeRow(rows[i]);
		}
	}

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
