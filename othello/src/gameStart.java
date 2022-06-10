import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;

public class gameStart extends JFrame implements ActionListener {
	JTextField addressField;
	JTextField portField;
	JLabel addr, port;
	JButton Connect, Close;
	String[] info;

	gameStart(String[] info) {
		this.info = info;
		setTitle("Othello");
		setResizable(false);
		setDefaultCloseOperation(EXIT_ON_CLOSE);

		this.setSize(300, 400);
		this.setResizable(false);
		this.setLayout(null);
		addr = new JLabel("Connection Address :");
		addr.setFont(new Font("Arial", Font.BOLD, 15));
		addr.setBounds(50, 50, 200, 50);

		port = new JLabel("Port :");
		port.setFont(new Font("Arial", Font.BOLD, 15));
		port.setBounds(50, 150, 100, 50);

		addressField = new JTextField();
		addressField.setBounds(50, 100, 175, 50);
		portField = new JTextField();
		portField.setBounds(50, 200, 175, 50);

		Connect = new JButton("Connect");
		Connect.setBounds(25, 300, 100, 40);
		Connect.addActionListener(this);
		Close = new JButton("Close");
		Close.setBounds(160, 300, 100, 40);
		Close.addActionListener(this);
		this.add(addr);
		this.add(port);
		this.add(addressField);
		this.add(portField);
		this.add(Connect);
		this.add(Close);

		this.setVisible(true);
	}

	public void actionPerformed(ActionEvent e) {// 버튼 눌렀을 때 반응하는 곳
		if (e.getSource() == Close) {
			info[2] = "close";
			this.setVisible(false);
			this.dispose();
		}
		if (e.getSource() == Connect) {
			info[0] = addressField.getText();
			info[1] = portField.getText();
			info[2] = "done";
			this.setVisible(false);
			this.dispose();
		}

	}
}

