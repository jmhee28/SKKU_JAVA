import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.*;

public class GUI extends JFrame implements ActionListener {
	JPanel board;
	ImageIcon myImage;
	ImageIcon circle, circle_white, pos, blank;
	JLabel notification,white_score,vs,black_score;

	JButton[] buttons;
	JButton taskButtons;
	boolean Clickable;
	Player nowPlaying;
	Thread noti;
	countScore counter;
	int color;

	GUI() {
		board = new JPanel();
		this.setTitle("Othello");
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setSize(8 * 64 + 80 + 300, 8 * 64 + 104);
		this.setResizable(false);
		this.setLayout(null);
		
		noti = new Thread(new notify());
		counter = new countScore();

		board.setLayout(new GridBagLayout());
		// board.setMaximumSize(new Dimension(64,64));
		board.setBackground(new Color(0, 0, 0));
		board.setBounds(300, 0, 8 * 64 + 64, 8 * 64 + 64);

		GridBagConstraints constraint = new GridBagConstraints();
		Clickable = false;
		constraint.fill = GridBagConstraints.BOTH;
		constraint.weightx = 1.0;
		constraint.weighty = 1.0;
		//Texts
		JLabel title = new JLabel("Othello");
		JLabel blank_area = new JLabel("vs");
		
		blank_area.setBounds(25, 395, 250, 50);
		blank_area.setBackground(new Color(200,200,200));
		blank_area.setOpaque(true);
		blank_area.setFont(new Font("Arial", Font.BOLD | Font.ITALIC, 30));
		blank_area.setHorizontalAlignment(SwingConstants.CENTER);
		
		title.setFont(new Font("Arial", Font.BOLD | Font.ITALIC, 30));
		title.setBounds(100, 100, 150, 100);
		
		white_score = new JLabel("0");
		white_score.setBounds(60, 400, 100, 50);
		white_score.setFont(new Font("Algerian", Font.BOLD , 50));
		white_score.setForeground(new Color(255,255,255));

		
		black_score = new JLabel("0");
		black_score.setBounds(185, 400, 100, 50);
		black_score.setFont(new Font("Algerian", Font.BOLD , 50));
		this.add(white_score);
		this.add(black_score);
		this.add(blank_area);
		
		
		notification = new JLabel("GAME START");
		notification.setBorder(new TitledBorder(new LineBorder(new Color(123, 123, 123), 5)));
		notification.setFont(new Font("Arial", Font.BOLD, 30));
		notification.setForeground(new Color(255, 255, 255));
		notification.setBounds(25, 200, 250, 50);
		notification.setHorizontalAlignment(SwingConstants.CENTER);
		notification.setOpaque(true);
		notification.setBackground(new Color(0, 0, 0));

		circle = new ImageIcon("images/circle.png");
		circle_white = new ImageIcon("images/circle_white.png");
		blank = new ImageIcon("images/blank.png");
		pos = new ImageIcon("images/pos.png");

		this.add(board);
		this.add(title);
		this.add(notification);
		//Button Add
		buttons = new JButton[64];
		for (int i = 1; i <= 64; i++) {
			buttons[i - 1] = new JButton(circle);
			buttons[i - 1].addActionListener(this);
			// buttons[i - 1].setMargin(new Insets(0, 0, 0, 0));

			buttons[i - 1].setOpaque(false);
			buttons[i - 1].setContentAreaFilled(false);
			buttons[i - 1].setBorderPainted(false);
			buttons[i - 1].setFocusPainted(false);
			constraint.gridx = (i - 1) % 8;
			constraint.gridy = (i - 1) / 8;
			constraint.gridwidth = 1;
			constraint.gridheight = 1;

			board.add(buttons[i - 1], constraint);
		}
		taskButtons=new JButton("Surrender");
		taskButtons.setBounds(25, 500, 250, 50);
		taskButtons.addActionListener(this);
		this.add(taskButtons);
		// Adding Components to the frame.
		// this.getContentPane().add(BorderLayout.EAST, board);
		this.setVisible(true);
	}

	@Override
	public void actionPerformed(ActionEvent e) {// 버튼 눌렀을 때 반응하는 곳
		for (int i = 0; i < 64; i++) {
			if (e.getSource() == buttons[i]) {
				if (Clickable) {
					int dx = i / 8 + 1;
					int dy = i % 8 + 1;
					if (nowPlaying.selfBoard.tmp_board[dx][dy] == color * 10) {
						nowPlaying.myNet.send(Integer.toString(dx) + "," + Integer.toString(dy));
						nowPlaying.selfBoard.putStone(color, dx, dy);
						nowPlaying.send = true;
						Clickable = false;
						notification.setText("");
						arrangeStone();
					} else {
						// 팝업
						JOptionPane.showMessageDialog(null, "놓을수 없는 자리입니다.");
					}
					taskButtons.setEnabled(false);
				}
			}
		}
		if(e.getSource() == taskButtons) {
			nowPlaying.myNet.send("E1");//항복
			taskButtons.setEnabled(false);
			nowPlaying.send = true;
			Clickable = false;
		}
	}

	/*
	 * public void setTurn(Player p) { nowPlaying = p;
	 * nowPlaying.selfBoard.calPos(color);//PlayBoard.java setStone(); }
	 */
	public void setStone() {// tmp_board의 정보를 활용해 Gui변경
		int[][] board = nowPlaying.selfBoard.tmp_board;// PlayBoard.java
		for (int i = 1; i <= 8; i++) {
			for (int j = 1; j <= 8; j++) {
				int idx = 8 * (i - 1) + j - 1;
				if (board[i][j] == 1) {
					buttons[idx].setIcon(circle_white);
				} else if (board[i][j] == 2) {
					buttons[idx].setIcon(circle);
				} else if (board[i][j] >= 10) {
					buttons[idx].setIcon(pos);
				} else {
					buttons[idx].setIcon(blank);
				}

			}
		}
	}

	public void arrangeStone() {// tmp_board의 정보를 활용해 Gui변경
		int[][] board = nowPlaying.selfBoard.board;// PlayBoard.java
		for (int i = 1; i <= 8; i++) {
			for (int j = 1; j <= 8; j++) {
				int idx = 8 * (i - 1) + j - 1;
				if (board[i][j] == 1) {
					buttons[idx].setIcon(circle_white);
				} else if (board[i][j] == 2) {
					buttons[idx].setIcon(circle);
				} else if (board[i][j] >= 10) {
					buttons[idx].setIcon(pos);
				} else {
					buttons[idx].setIcon(blank);
				}

			}
		}
	}

	public void setButtonClickable(boolean val) {
		for (int i = 0; i < 64; i++) {
			buttons[i].setEnabled(val);

		}
	}
	public void notifyText(String data) {
		noti.interrupt();
		noti = new Thread(new notify());
		notification.setText(data);
		noti.start();
	}
	class notify implements Runnable {

		@Override
		public void run() {
			for (int k = 0; k < 2; k++) {
				for (int i = 1; i <=5; i++) {
					notification.setForeground(new Color(255 / i, 255 / i, 255 / i));
					try {
						Thread.sleep(100);
					} catch (InterruptedException e) {

					}
				}
				for (int i = 5; i >= 1; i--) {
					notification.setForeground(new Color(255 / i, 255 / i, 255 / i));
					try {
						Thread.sleep(100);
					} catch (InterruptedException e) {

					}
				}
			}
		}
	}
	public class countScore extends Thread {
		boolean stop;
		countScore(){
			stop=false;
		}
		public void stopThread() {
			stop=true;
		}
		
		@Override
		public void run() {
			System.out.println("점수 카운팅 시작");
			while(!stop) {
				int white = 0 ;
				int black = 0 ;
				for(int i = 1 ; i <= 8 ; i ++) {
					for(int j = 1 ; j <= 8 ; j ++) {
						if(nowPlaying.selfBoard.board[i][j]==1) {
							white++;
						}
						else if(nowPlaying.selfBoard.board[i][j]==2) {
							black++;
						}
					}
				}
				white_score.setText(Integer.toString(white));
				black_score.setText(Integer.toString(black));
				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {

				}
			}
		}
	}


}