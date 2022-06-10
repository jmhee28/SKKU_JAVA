import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;

public class communication {
	Socket socket = null; // Client와 통신하기 위한 Socket
	ServerSocket server_socket = null; // 서버 생성을 위한 ServerSocket
	BufferedReader in = null; // Client로부터 데이터를 읽어들이기 위한 입력스트림
	PrintWriter out = null; // Client로 데이터를 내보내기 위한 출력 스트림
	BufferedReader in2 = null; // 키보드로부터 읽어들이기 위한 입력스트림
	InetAddress ia = null;
	//

	public int OpenNetwork(String[] info) {
		try {// 서버 오픈 체크
			System.out.println("서버 오픈 체크");
			ia = InetAddress.getByName(info[0]); // 서버로 접속
			System.out.println("서버 접속");
			socket = new Socket();
			socket.connect(new InetSocketAddress(info[0],Integer.parseInt(info[1])),500);
			System.out.println("연결 성공" + socket.toString());
		} catch (IOException e) {
			System.out.println("연결된 서버가 없습니다. 서버모드로 전환합니다.");
			try {
				server_socket = new ServerSocket(Integer.parseInt(info[1]));

			} catch (IOException e1) {
				System.out.println("해당 포트가 열려있습니다.");
			}
			try {

				if (server_socket != null) {
					System.out.println("서버 오픈");
					server_socket.setSoTimeout(10 * 1000);
					socket = server_socket.accept(); // 서버 생성 , Client 접속 대기
					System.out.println("클라이언트 연결 성공");
					in = new BufferedReader(new InputStreamReader(socket.getInputStream())); // 입력스트림 생성
					out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()))); // 출력스트림
					System.out.println("버퍼 연결 성공");
					return 1;

				} else {
					server_socket = null;
					System.out.println("소켓 에러 :: 클라이언트 모드로 전환합니다.");
					return ClientMode(info);
				}
			} catch (SocketTimeoutException e2) {
				System.out.println("연결 대기중인 클라이언트가 없습니다. 클라이언트 모드로 전환합니다.");
				try {
					server_socket.close();
					server_socket = null;
					return ClientMode(info);
				} catch (IOException e3) {

					return -1;
				}

			} catch (IOException e3) {

				return -1;
			}
		}
		try {
			in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			in2 = new BufferedReader(new InputStreamReader(System.in));
			out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())));
			System.out.println("소켓/버퍼 연결 성공");
			return 2;

		} catch (IOException e) {
			System.out.println(e);
			return -1;
		}
		
	}

	public int ClientMode(String[] info) {
		while (true) {
			try {// 무한 대기
				Thread.sleep(1000);
				ia = InetAddress.getByName(info[0]); // 서버로 접속
				socket = new Socket(ia, Integer.parseInt(info[1]));
				System.out.println("연결 성공" + socket.toString());
				break;
			} catch (IOException e) {
				System.out.println("대기중..." + e);
				continue;
			} catch (InterruptedException e) {
				continue;
			}
		}
		try {
			in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			in2 = new BufferedReader(new InputStreamReader(System.in));
			out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())));
			System.out.println("소켓/버퍼 연결 성공");
			return 2;


		} catch (IOException e) {
			System.out.println(e);
			return -1;
		}

	}

	public void send(String data) {
		System.out.println("데이터 전송 : " + data);
		out.println(data); // 서버로 데이터 전송
		out.flush();
	}

	public void sendWithInput() {
		String data;
		try {
			data = in2.readLine();
			System.out.println("데이터 전송 : " + data);
			out.println(data); // 서버로 데이터 전송
			out.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println("오류 발생" + e);
		}

	}

	public String recieve() throws IOException {
		String str2;
		try {
			str2 = in.readLine(); // 서버로부터 되돌아오는 데이터 읽어들임
			System.out.println("받은 데이터 : " + str2);
		} catch (SocketException e) {// 빡종디텍터
			str2 = "E2";
		}
		return str2;
	}

	//
	public Socket getSocket() {
		return this.socket;
	}

	public ServerSocket getServerSocket() {
		return this.server_socket;
	}

	public BufferedReader getClientBR() {
		return this.in;
	}

	public PrintWriter getClientPW() {
		return this.out;
	}

	public BufferedReader getBR() {
		return this.in2;
	}

	public InetAddress getia() {
		return this.ia;
	}

	public void closeSocket() {
		try {
			socket.close();
		} catch (IOException e) {

		}
	}

	public void closeServerSocket() {
		if (server_socket != null) {
			try {
				server_socket.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public boolean isOpen() {
		if (server_socket != null) {
			if (server_socket.isClosed()) {
				System.out.println("서버 포트 열림 / 소켓 닫힘");

				return false;
			}
		}
		if (socket.isConnected() && !socket.isClosed()) {
			System.out.println("서버 포트 닫힘 / 소켓 열리고 연결됨");

			return true;
		}
		return false;
	}

}
