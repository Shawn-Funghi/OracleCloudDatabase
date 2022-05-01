package databaseEditor;

import java.awt.BorderLayout;
import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import org.json.JSONException;

import javax.swing.JTextField;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.awt.event.ActionEvent;

public class checkPath extends JDialog {

	private final JPanel contentPanel = new JPanel();
	private JButton btnSendPath;
	private JTextField txtPathInput;
	private boolean btnclick;
	private String urlReturn;
	private String error;
	private boolean pathAvailable ;
	/**
	 * Create the dialog.
	 */
	//error字串是為了傳哪種錯誤
	public checkPath(JFrame frame, String error) {
		super(frame);
		this.btnclick = false;
		this.error = error;
		this.initialize();
		this.setLocationRelativeTo(frame);
		this.setModal(true);
		this.setVisible(true);
	}

	private void initialize() {

		setTitle(error);
		setResizable(false);
		setBounds(100, 100, 650, 100);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(new BorderLayout(0, 0));

		txtPathInput = new JTextField();
		contentPanel.add(txtPathInput, BorderLayout.CENTER);
		txtPathInput.setColumns(10);

		JPanel buttonPane = new JPanel();
		getContentPane().add(buttonPane, BorderLayout.SOUTH);

		btnSendPath = new JButton("輸入資料庫路徑");
		btnSendPath.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				btnSendPath_Click();
			}
		});
		buttonPane.setLayout(new BorderLayout(0, 0));
		btnSendPath.setActionCommand("OK");
		buttonPane.add(btnSendPath);
		getRootPane().setDefaultButton(btnSendPath);

	}

	protected void btnSendPath_Click() {
		this.btnclick = true;
		if (this.txtPathInput.getText().equals("")) {
			JOptionPane.showMessageDialog(this, "輸入位置不可空白", "輸入錯誤", JOptionPane.ERROR_MESSAGE);
		} else {
			this.urlReturn = this.txtPathInput.getText();
			try {
				connection pathTest = new connection(this.urlReturn);
				//連上database 沒有連上會跳例外
				pathTest.get();
				JOptionPane.showMessageDialog(this, "資料庫連網正常! 修改成功!", "成功連網", JOptionPane.INFORMATION_MESSAGE);
				editor ed = new editor("./DataBasePath.json");
				//將原本value的值換成新輸入的值  
				ed.replace("\""+ed.getJSON().getString("defaultDataBasePath")+"\"", "\""+this.urlReturn+"\"");
				this.pathAvailable = true;
				this.dispose();
			} catch (ProtocolException e) {
				JOptionPane.showMessageDialog(this, e.getMessage(), "發生錯誤", JOptionPane.ERROR_MESSAGE);
			} catch (MalformedURLException e) {
				JOptionPane.showMessageDialog(this, e.getMessage(), "發生錯誤", JOptionPane.ERROR_MESSAGE);
			} catch (IOException e) {
				JOptionPane.showMessageDialog(this, e.getMessage(), "發生錯誤", JOptionPane.ERROR_MESSAGE);
			} catch (JSONException e) {
				JOptionPane.showMessageDialog(this, e.getMessage(), "發生錯誤", JOptionPane.ERROR_MESSAGE);
			}
		}
	}
   public boolean getTurn() {
	   return this.pathAvailable;
   }
	public String getURL() {
		return this.urlReturn;
	}
}
