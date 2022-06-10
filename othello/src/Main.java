import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

import javax.swing.*;


public class Main {

	public static void main(String args[]) throws InterruptedException {
		Scanner sc = new Scanner(System.in);
		Player p1;
		// p1= new Player();
		// GUI test = new GUI(1);
		String[] info = new String[3];
		String address, port;
		gameStart test = new gameStart(info);
		while (true) {
			try {
				if (info[2].equals("done")) {
					if (isValid(info[0])) {
						System.out.println("game start");
						address = info[0];
						port = info[1];
						System.out.println(info[0] + "/" + info[1]);
						p1 = new Player(info);
						break;
					}
					else {
						int ok = JOptionPane.showOptionDialog(null, "올바르지 않은 주소입니다.", "Othello", JOptionPane.DEFAULT_OPTION,
						        JOptionPane.ERROR_MESSAGE, null, null, null);
						if(ok==0){
							test = new gameStart(info);
							info[2]="";
							continue;
						}
					}
					break;
				}
				if(info[2].equals("close")) {
					break;
				}
			} catch (NullPointerException e) {

			}
			Thread.sleep(1000);
		}
	}

	public static boolean isValid(String ip) {
		String[] num = ip.split("\\.");
		if (ip == null || ip.isEmpty()) {
			return false;
		}

		if (num.length != 4) {
			return false;
		}

		for (String s : num) {
			int i = Integer.parseInt(s);
			if ((i < 0) || (i > 255)) {
				return false;
			}
		}
		if (ip.endsWith(".")) {
			return false;
		}

		return true;
	}

}
