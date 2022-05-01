package databaseEditor;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.ProtocolException;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import org.json.JSONException;

public class csvOutPutDialod extends JDialog {

	private final JPanel contentPanel = new JPanel();
	private JButton btnSendPath;
	private JTextField txtPathInput;
	private boolean btnclick;
	private String urlReturn;
	private boolean pathAvailable ;
	/**
	 * Create the dialog.
	 */
	//error字串是為了傳哪種錯誤
	public csvOutPutDialod(JFrame frame) {
		super(frame);
		this.btnclick = false;
		this.initialize();
		this.setLocationRelativeTo(frame);
		this.setModal(true);
		this.setVisible(true);
	}

	private void initialize() {

		setTitle("輸入輸出路徑");
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

		btnSendPath = new JButton("輸入CSV檔路徑");
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
				editor ed = new editor(this.urlReturn);		
				if(ed.getData().isBlank()) {
				this.pathAvailable = true;
				this.dispose();
				}else {
					JOptionPane.showMessageDialog(this,"該位置的檔案有內容", "空資料庫", JOptionPane.ERROR_MESSAGE);
				}
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
