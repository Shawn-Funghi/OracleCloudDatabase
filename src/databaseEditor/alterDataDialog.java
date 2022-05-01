package databaseEditor;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.ProtocolException;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

import org.json.JSONObject;

import javax.swing.JScrollPane;
import javax.swing.JTable;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class alterDataDialog extends JDialog {

	private final JPanel contentPanel = new JPanel();
	private Object[][] objAdd;
	private String[] dataPath;
	private JTable tableForAlterData;
	private jsonObjectList alterList ;
	private boolean ok;
	/**
	 * Launch the application.
	 */

	/**
	 * Create the dialog.
	 */
	public alterDataDialog(JFrame frame,jsonObjectList alterList) {
		super(frame);	
		this.alterList = alterList;
		this.dataPath = new String[this.alterList.size()];
		for (int i = 0; i < this.alterList.size(); i++) {
			this.dataPath[i] = this.alterList.getObjectURL(i);
		}
		this.ok = false;
		this.initialize();
		this.setLocationRelativeTo(frame);
		this.setModal(true);
		this.setVisible(true);
	}

	private void initialize() {
		this.setTitle("修改資料");
		setBounds(100, 100, 450, 300);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(new BorderLayout(0, 0));
		{
			JScrollPane scrollPane = new JScrollPane();
			contentPanel.add(scrollPane);
			{
				tableForAlterData = new JTable();
				tableForAlterData.setModel(this.getDFModal());
				scrollPane.setViewportView(tableForAlterData);
			}
		}
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.CENTER));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				JButton btnOk = new JButton("確認修改");
				btnOk.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						btnOk_Click();
					}
				});
				btnOk.setActionCommand("OK");
				buttonPane.add(btnOk);
				getRootPane().setDefaultButton(btnOk);
			}
			{
				JButton btnDelete = new JButton("取消修改所選資料");
				btnDelete.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						btnDelete_Click();
					}
				});
				buttonPane.add(btnDelete);
			}
		}
	}

	protected void btnOk_Click() {
		this.alterList = receiveTableData();
		if (!this.alterList.isEmpty()) {
			DefaultTableModel model = (DefaultTableModel) this.tableForAlterData.getModel();
			try {
				for (int i = 0; i < this.alterList.size(); i++) {
					connection conn = new connection(this.dataPath[i]);
					conn.put(this.alterList.get(i), false);
				}
				JOptionPane.showMessageDialog(this, "資料修改成功", "修改成功", JOptionPane.INFORMATION_MESSAGE);
				this.ok = true;
				this.dispose();
			} catch (ProtocolException e) {
				JOptionPane.showMessageDialog(this, "有空格未填", "Oops", JOptionPane.ERROR_MESSAGE);
			} catch (MalformedURLException e) {
				JOptionPane.showMessageDialog(this, e.getMessage(), "Oops", JOptionPane.ERROR_MESSAGE);
			} catch (IOException e) {
				JOptionPane.showMessageDialog(this, e.getMessage(), "Oops", JOptionPane.ERROR_MESSAGE);
			}
		} else {
			JOptionPane.showMessageDialog(this, "請重新選取資料", "Oops", JOptionPane.ERROR_MESSAGE);
			this.dispose();
		}
	}

	public jsonObjectList receiveTableData() {
		DefaultTableModel model = (DefaultTableModel) this.tableForAlterData.getModel();
		jsonObjectList list = new jsonObjectList(this.alterList.getNameData());
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
				data.put(this.alterList.getNameData()[j], model.getValueAt(i, j));
			}
			list.add(data);
		}
		return list;
	}

	protected void btnDelete_Click() {
		DefaultTableModel model = (DefaultTableModel) this.tableForAlterData.getModel();
		// 這邊可能要問老師一下會出bug
		int rows[] = this.tableForAlterData.getSelectedRows();
		for (int i = rows.length-1; i >=0 ; i--) {
			model.removeRow(rows[i]);
		}
	}
	
	public boolean getOK() {
		return this.ok;
	}
	
	public DefaultTableModel getDFModal() {
		DefaultTableModel model = new DefaultTableModel(this.alterList.getObjectData(), this.alterList.getNameData()) {
			// 這個一定要留不然會可以編輯
			public boolean isCellEditable(int row, int column) {
				return true;
			}
		};
		return model;
	}
}
