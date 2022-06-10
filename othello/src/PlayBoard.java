

class PlayBoard {// 1 for while 2 for black
	static int[][] board; // To share same board, declared as static
	int[][] tmp_board;
	int[][] dir_board;
	int[] dir_x = { -1, -1, 0, 1, 1, 1, 0, -1 };
	int[] dir_y = { 0, 1, 1, 1, 0, -1, -1, -1 }; // 북쪽에서부터 시계방향

	PlayBoard() {
		if(board==null) board = new int[10][10];
		tmp_board = new int[10][10];
		dir_board = new int[10][10];
		for (int i = 0; i < 10; i++) {
			for (int j = 0; j < 10; j++) {
				board[i][j] = -1; // out of boundary or obstacle
			}
		}
		for (int i = 1; i <= 8; i++) {
			for (int j = 1; j <= 8; j++) {
				board[i][j] = 0;
			}
		}
		board[4][4] = 1;
		board[4][5] = 2;
		board[5][4] = 2;
		board[5][5] = 1; // init
	}

	public void putStone(int color, int x, int y) {
		calPos(color);
		flipStones(color, x, y);
		System.out.printf("put %d %d \n", x, y);
		board[x][y] = color;
	}

	public void calPos(int color) { // color가 둘 수 있는 곳
		// Array copy
		for (int i = 0; i < 10; i++) {
			for (int j = 0; j < 10; j++) {
				tmp_board[i][j] = board[i][j];
				dir_board[i][j] = 0;
			}
		}
		for (int i = 1; i <= 8; i++) {
			for (int j = 1; j <= 8; j++) {
				if (board[i][j] != 0)
					continue;
				for (int k = 0; k < 8; k++) {
					int dx = i + dir_x[k];
					int dy = j + dir_y[k];

					if (board[dx][dy] == 3 - color) {
						for (int l = 2;; l++) {
							int tmp_x = i + dir_x[k] * l;
							int tmp_y = j + dir_y[k] * l;
							if(tmp_board[tmp_x][tmp_y]==3-color) continue;
							

							if (tmp_board[tmp_x][tmp_y] == color) {
								tmp_board[i][j] = color*10;
								dir_board[i][j] |= 1 << k;
								break;
							}
							else break;

						}
					}
				}
			}
		}

		// tmp_board calculation finished.
		
	}
	// showAll(tmp_board);//show all possible ways in console
	public void showAll(int[][] arg) {
		for (int i = 1; i <= 8; i++) {
			for (int j = 1; j <= 8; j++) {
				if (arg[i][j] == 0) {
					System.out.print("□");
				} else if (arg[i][j] == 1) {
					System.out.print("○");
				} else if (arg[i][j] == 2) {
					System.out.print("●");
				} else if (arg[i][j] >= 10) {
					System.out.print("x");
				}
			}
			System.out.print("\n");
		}
	}

	public void flipStones(int color, int x, int y) {
		int dir_flip = dir_board[x][y];
		int dir = 0;
		while (dir_flip != 0) {
			if ((dir_flip & 1) == 1) {
				int i = 1;
				while (true) {
					int dx = x + dir_x[dir] * i;
					int dy = y + dir_y[dir] * i;
					i++;
					if (board[dx][dy] == -1)
						break;
					if (board[dx][dy] == 3 - color)
						board[dx][dy] = color;
					else
						break;
				}
			}
			dir_flip = dir_flip >> 1;
			dir++;
		}
	}
}

