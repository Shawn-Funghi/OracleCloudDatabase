package databaseEditor;

import java.awt.BorderLayout;
import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import java.awt.Font;
import javax.swing.BoxLayout;
import java.awt.Component;

public class aboutDialog extends JDialog {

	private final JPanel contentPanel = new JPanel();
	/**
	 * Create the dialog.
	 */
	public aboutDialog(JFrame frame) {
		super(frame);
		this.initialize();
		this.setLocationRelativeTo(frame);
		this.setModal(true);
		this.setVisible(true);
	}
	private void initialize() {
		setBounds(100, 100, 350, 220);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(new BorderLayout(0, 0));
		
		JLabel lblNewLabel = new JLabel("資料庫編輯器ver1.0");
		lblNewLabel.setFont(new Font("新細明體", Font.PLAIN, 20));
		lblNewLabel.setHorizontalAlignment(SwingConstants.CENTER);
		contentPanel.add(lblNewLabel, BorderLayout.NORTH);
		
		JPanel panel = new JPanel();
		contentPanel.add(panel, BorderLayout.CENTER);
		panel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
		
		JLabel lblNewLabel_2 = new JLabel("這是一個可以編輯資料庫的程式");
		lblNewLabel_2.setAlignmentX(Component.CENTER_ALIGNMENT);
		panel.add(lblNewLabel_2);
		
		JLabel lblNewLabel_1 = new JLabel("     作者：蘇雋勛 於 2021/7/1  完成第一版");
		panel.add(lblNewLabel_1);
		
		JLabel lblNewLabel_4 = new JLabel("指導老師 陳琨 於109下學期所開設的Java程式設計");
		panel.add(lblNewLabel_4);
		
		JLabel lblNewLabel_3 = new JLabel("在這次的專案中 學到了很多東西 若沒有老師的推進");
		panel.add(lblNewLabel_3);
		
		JLabel lblNewLabel_5 = new JLabel("我根本做不出這個程式");
		panel.add(lblNewLabel_5);
		
		JLabel lblNewLabel_6 = new JLabel("很感謝老師教了我這麼一個學期");
		panel.add(lblNewLabel_6);
		
		JLabel lblNewLabel_7 = new JLabel("E-mail：shawn900905@gmail.com");
		panel.add(lblNewLabel_7);
	}
	

}
