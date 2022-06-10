import java.io.IOException;
import java.util.Random;

import javax.swing.JOptionPane;

public class Player {
	int color;
	int address;
	boolean send;
	PlayBoard selfBoard;
	communication myNet;
	GUI playUI;
	int numOfPosStone;
	String[] info;
	Random rand;
	countPos posCounter;
	Player(String[] info) {
		this.info = info;
		myNet = new communication();
		playUI = new GUI();
		rand = new Random();
		rand.setSeed(System.currentTimeMillis());
		init();
		int lead = myNet.OpenNetwork(info);
		if(lead == 1) {
			int tmp_color = rand.nextInt(2)+1;
			color=tmp_color;
			myNet.send(Integer.toString(3-tmp_color));
		}
		else {
			try {
				String rec = myNet.recieve();
				color = Integer.parseInt(rec);
			} catch (IOException e) {

			}
		}
		colorInit();
		posCounter = new countPos();
		posCounter.start();
		playUI.counter.start();

		playing();
	}

	public void init() {
		selfBoard = new PlayBoard();
		playUI.nowPlaying = this;
		playUI.taskButtons.setEnabled(false);
		playUI.arrangeStone();
		playUI.notifyText("Waiting...");
	}

	public void colorInit() {
		playUI.notifyText("Connected!");
		try {
			Thread.sleep(1500);
		} catch (InterruptedException e) {

		}
		playUI.color = color;
		System.out.println("색깔 : " + Integer.toString(color));
		numOfPosStone = 0;
		// 내가놓을수 있는 위치를 계산하고 표시
		selfBoard.calPos(color);
		if (color == 1) {
			playUI.notifyText("You are WHITE!");
			try {
				Thread.sleep(1500);
			} catch (InterruptedException e) {

			}
			playUI.taskButtons.setEnabled(true);
			playUI.setStone();
			playUI.Clickable = true;
		}
		if (color == 2) {
			playUI.notifyText("You are BLACK!");
			playUI.arrangeStone();
			send = true;
		}
	}

	public void playing() {
		while (true) {
			if (send) {
				System.out.println("Send Signal Detected");
				try {
					System.out.println("수신 모드 전환");
					Thread.sleep(500);
				} catch (InterruptedException e) {

				}
				try {
					String rec = myNet.recieve();
					System.out.println(rec);
					String[] rec_pos = rec.split(",");
					playUI.notifyText("Your Turn!");
					playUI.taskButtons.setEnabled(true);

					if (rec.equals("E1")) {
						playUI.taskButtons.setEnabled(false);
						myNet.send("END0");
						playUI.notifyText("You Win!!");
						myNet.closeServerSocket();// 게임 종료
						myNet.closeSocket();
						System.out.println("e1: socket close");
						try {
							Thread.sleep(2000);
						} catch (InterruptedException e) {

						}
						
						int ok = JOptionPane.showOptionDialog(null, "상대방이 항복했습니다!\n다시 시작하시겠습니까?", "Othello",
								JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE, null, null, null);
						if (ok == 0) {
							System.out.print("/e1 init");
							myNet=new communication();
							init();
							int lead = myNet.OpenNetwork(info);
							if(lead == 1) {
								int tmp_color = rand.nextInt(2)+1;
								color=tmp_color;
								myNet.send(Integer.toString(3-tmp_color));
							}
							else {
								try {
									String rec2 = myNet.recieve();
									System.out.print("what we get : "+rec2);
									color = Integer.parseInt(rec2);
								} catch (IOException e) {

								}
							}
							colorInit();
						} else {
							playUI.setVisible(false);
							playUI.dispose();
							playUI.counter.stopThread();
							posCounter.stopThread();
							break;
						}
					}
					if (rec.equals("END0")) {
						// 나의 항복 패
						playUI.taskButtons.setEnabled(false);
						myNet.closeServerSocket();// 게임 종료
						myNet.closeSocket();
						System.out.println("end0: socket close");
						System.out.println("MY SURRENDER");
						playUI.notifyText("You Lose..");
						try {
							Thread.sleep(2000);
						} catch (InterruptedException e) {

						}
						
						int ok = JOptionPane.showOptionDialog(null, "항복했습니다..\n다시 시작하시겠습니까?", "Othello",
								JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE, null, null, null);
						if (ok == 0) {
							System.out.print("end0 init");

							myNet=new communication();
							init();
							int lead = myNet.OpenNetwork(info);
							if(lead == 1) {
								int tmp_color = rand.nextInt(2)+1;
								color=tmp_color;
								myNet.send(Integer.toString(3-tmp_color));
							}
							else {
								try {
									String rec2 = myNet.recieve();
									System.out.print("what we get :"+rec2);
									color = Integer.parseInt(rec2);
								} catch (IOException e) {

								}
							}
							colorInit();
						} else {
							playUI.setVisible(false);
							playUI.dispose();
							playUI.counter.stopThread();
							posCounter.stopThread();
							break;
						}
						
					}
					if (rec.equals("E2")) {
						playUI.taskButtons.setEnabled(false);
						playUI.Clickable=false;
						playUI.notifyText("You Win!!");
						JOptionPane.showMessageDialog(null, "상대방이 게임을 종료했습니다.");
						try {
							Thread.sleep(2000);
						} catch (InterruptedException e) {

						}
						playUI.setVisible(false);
						playUI.dispose();
						playUI.counter.stopThread();
						posCounter.stopThread();
						break;
					}
					if (rec.equals("END")) { // 정상종료
						playUI.notifyText("GAME FINISHED");
						myNet.closeServerSocket();// 게임 종료
						myNet.closeSocket();
						if(normalEND()==0) {
							playUI.counter.stopThread();
							posCounter.stopThread();
							break;
						}
					}
					if (rec.equals("E0")) {
						selfBoard.calPos(color);
						playUI.setStone();
						countOnce();
						if (numOfPosStone == 0) {
							playUI.notifyText("GAME FINISHED");
							System.out.println("No more place to put");
							myNet.send("END");// 게임 종료
							
							myNet.closeServerSocket();
							myNet.closeSocket();
							try {
								Thread.sleep(1000);
							} catch (InterruptedException e) {

							}
							if(normalEND()==0) {
								playUI.counter.stopThread();
								posCounter.stopThread();
								break;
							}
						} else {
							System.out.println("Unlock1");
							playUI.Clickable = true; // Unlock the Button
							send = false;
							setTurn();
						}
					}
					if (rec_pos.length == 2) {
						System.out.println("GET :"+rec_pos[0]+", "+rec_pos[1]);
						selfBoard.putStone(3 - color, Integer.parseInt(rec_pos[0]), Integer.parseInt(rec_pos[1]));
						selfBoard.calPos(color);
						playUI.setStone();
						countOnce();
						if (numOfPosStone == 0) {
							playUI.notifyText("No possible..");
							playUI.taskButtons.setEnabled(false);
							System.out.println("Send E0");
							myNet.send("E0");
						} else {
							System.out.println("Unlock2");
							playUI.Clickable = true; // Unlock the Button
							send = false;
							setTurn();
						}
					}

				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}

			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {

			}
		}
	}

	public void setTurn() {
		selfBoard.calPos(color);
		playUI.setStone();
	}

	public void countOnce() {
		int res = 0;
		for (int i = 1; i <= 8; i++) {
			for (int j = 1; j <= 8; j++) {
				if (selfBoard.tmp_board[i][j] == color * 10) {
					res++;
				}
			}
		}
		numOfPosStone = res;
	}

	public int normalEND() {
		if (color == 1) {
			if (Integer.parseInt(playUI.white_score.getText()) > Integer.parseInt(playUI.black_score.getText())) {
				// 나 승리
				int ok = JOptionPane.showOptionDialog(null, "축하합니다 승리하였습니다!\n다시 시작하시겠습니까?", "Othello",
						JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE, null, null, null);
				if (ok == 0) {
					myNet=new communication();
					init();
					int lead = myNet.OpenNetwork(info);
					if(lead == 1) {
						int tmp_color = rand.nextInt(2)+1;
						color=tmp_color;
						myNet.send(Integer.toString(3-tmp_color));
					}
					else {
						try {
							String rec2 = myNet.recieve();
							System.out.print("what we get :"+rec2);
							color = Integer.parseInt(rec2);
						} catch (IOException e) {

						}
					}
					colorInit();
					return 1;
				} else {
					playUI.setVisible(false);
					playUI.dispose();
					playUI.counter.stopThread();
					posCounter.stopThread();
					return 0;
				}

			} else if (Integer.parseInt(playUI.white_score.getText()) < Integer
					.parseInt(playUI.black_score.getText())) {
				// 나 패배
				int ok = JOptionPane.showOptionDialog(null, "패배했습니다!\n다시 시작하시겠습니까?", "Othello",
						JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE, null, null, null);
				if (ok == 0) {
					myNet=new communication();
					init();
					int lead = myNet.OpenNetwork(info);
					if(lead == 1) {
						int tmp_color = rand.nextInt(2)+1;
						color=tmp_color;
						myNet.send(Integer.toString(3-tmp_color));
					}
					else {
						try {
							String rec2 = myNet.recieve();
							System.out.print("what we get :"+rec2);
							color = Integer.parseInt(rec2);
						} catch (IOException e) {

						}
					}
					colorInit();
					return 1;
				} else {
					playUI.setVisible(false);
					playUI.dispose();
					playUI.counter.stopThread();
					posCounter.stopThread();
					return 0;
				}
			} else {
				// 비김
				int ok = JOptionPane.showOptionDialog(null, "비겼습니다!\n다시 시작하시겠습니까?", "Othello",
						JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE, null, null, null);
				if (ok == 0) {
					myNet=new communication();
					init();
					int lead = myNet.OpenNetwork(info);
					if(lead == 1) {
						int tmp_color = rand.nextInt(2)+1;
						color=tmp_color;
						myNet.send(Integer.toString(3-tmp_color));
					}
					else {
						try {
							String rec2 = myNet.recieve();
							System.out.print("what we get :"+rec2);
							color = Integer.parseInt(rec2);
						} catch (IOException e) {

						}
					}
					colorInit();
					return 1;
				} else {
					playUI.setVisible(false);
					playUI.dispose();
					playUI.counter.stopThread();
					posCounter.stopThread();
					return 0;
				}
			}
		} else {
			if (Integer.parseInt(playUI.white_score.getText()) > Integer.parseInt(playUI.black_score.getText())) {
				// 나 패배
				int ok = JOptionPane.showOptionDialog(null, "패배했습니다!\n다시 시작하시겠습니까?", "Othello",
						JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE, null, null, null);
				if (ok == 0) {
					myNet=new communication();
					init();
					int lead = myNet.OpenNetwork(info);
					if(lead == 1) {
						int tmp_color = rand.nextInt(2)+1;
						color=tmp_color;
						myNet.send(Integer.toString(3-tmp_color));
					}
					else {
						try {
							String rec2 = myNet.recieve();
							System.out.print("what we get :"+rec2);
							color = Integer.parseInt(rec2);
						} catch (IOException e) {

						}
					}
					colorInit();
					return 1;
				} else {
					playUI.setVisible(false);
					playUI.dispose();
					playUI.counter.stopThread();
					posCounter.stopThread();
					return 0;
				}
			} else if (Integer.parseInt(playUI.white_score.getText()) < Integer
					.parseInt(playUI.black_score.getText())) {
				// 나 승리
				int ok = JOptionPane.showOptionDialog(null, "축하합니다 승리하였습니다!\n다시 시작하시겠습니까?", "Othello",
						JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE, null, null, null);
				if (ok == 0) {
					myNet=new communication();
					init();
					int lead = myNet.OpenNetwork(info);
					if(lead == 1) {
						int tmp_color = rand.nextInt(2)+1;
						color=tmp_color;
						myNet.send(Integer.toString(3-tmp_color));
					}
					else {
						try {
							String rec2 = myNet.recieve();
							System.out.print("what we get :"+rec2);
							color = Integer.parseInt(rec2);
						} catch (IOException e) {

						}
					}
					colorInit();
					return 1;
				} else {
					playUI.setVisible(false);
					playUI.dispose();
					playUI.counter.stopThread();
					posCounter.stopThread();
					return 0;
				}
			} else {
				// 비김
				int ok = JOptionPane.showOptionDialog(null, "비겼습니다!\n다시 시작하시겠습니까?", "Othello",
						JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE, null, null, null);
				if (ok == 0) {
					myNet=new communication();
					init();
					color = myNet.OpenNetwork(info);
					colorInit();
					return 1;
				} else {
					playUI.setVisible(false);
					playUI.dispose();
					playUI.counter.stopThread();
					posCounter.stopThread();
					return 0;
				}
			}
		}
	}

	class countPos extends Thread {
		boolean stop;
		countPos(){
			stop=false;
		}
		public void stopThread() {
			stop=true;
		}
		
		@Override
		public void run() {
			while (!stop) {
				countOnce();
				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {

				}
			}

		}
	}

}
