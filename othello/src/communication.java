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
	Socket socket = null; // Client�� ����ϱ� ���� Socket
	ServerSocket server_socket = null; // ���� ������ ���� ServerSocket
	BufferedReader in = null; // Client�κ��� �����͸� �о���̱� ���� �Է½�Ʈ��
	PrintWriter out = null; // Client�� �����͸� �������� ���� ��� ��Ʈ��
	BufferedReader in2 = null; // Ű����κ��� �о���̱� ���� �Է½�Ʈ��
	InetAddress ia = null;
	//

	public int OpenNetwork(String[] info) {
		try {// ���� ���� üũ
			System.out.println("���� ���� üũ");
			ia = InetAddress.getByName(info[0]); // ������ ����
			System.out.println("���� ����");
			socket = new Socket();
			socket.connect(new InetSocketAddress(info[0],Integer.parseInt(info[1])),500);
			System.out.println("���� ����" + socket.toString());
		} catch (IOException e) {
			System.out.println("����� ������ �����ϴ�. �������� ��ȯ�մϴ�.");
			try {
				server_socket = new ServerSocket(Integer.parseInt(info[1]));

			} catch (IOException e1) {
				System.out.println("�ش� ��Ʈ�� �����ֽ��ϴ�.");
			}
			try {

				if (server_socket != null) {
					System.out.println("���� ����");
					server_socket.setSoTimeout(10 * 1000);
					socket = server_socket.accept(); // ���� ���� , Client ���� ���
					System.out.println("Ŭ���̾�Ʈ ���� ����");
					in = new BufferedReader(new InputStreamReader(socket.getInputStream())); // �Է½�Ʈ�� ����
					out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()))); // ��½�Ʈ��
					System.out.println("���� ���� ����");
					return 1;

				} else {
					server_socket = null;
					System.out.println("���� ���� :: Ŭ���̾�Ʈ ���� ��ȯ�մϴ�.");
					return ClientMode(info);
				}
			} catch (SocketTimeoutException e2) {
				System.out.println("���� ������� Ŭ���̾�Ʈ�� �����ϴ�. Ŭ���̾�Ʈ ���� ��ȯ�մϴ�.");
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
			System.out.println("����/���� ���� ����");
			return 2;

		} catch (IOException e) {
			System.out.println(e);
			return -1;
		}
		
	}

	public int ClientMode(String[] info) {
		while (true) {
			try {// ���� ���
				Thread.sleep(1000);
				ia = InetAddress.getByName(info[0]); // ������ ����
				socket = new Socket(ia, Integer.parseInt(info[1]));
				System.out.println("���� ����" + socket.toString());
				break;
			} catch (IOException e) {
				System.out.println("�����..." + e);
				continue;
			} catch (InterruptedException e) {
				continue;
			}
		}
		try {
			in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			in2 = new BufferedReader(new InputStreamReader(System.in));
			out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())));
			System.out.println("����/���� ���� ����");
			return 2;


		} catch (IOException e) {
			System.out.println(e);
			return -1;
		}

	}

	public void send(String data) {
		System.out.println("������ ���� : " + data);
		out.println(data); // ������ ������ ����
		out.flush();
	}

	public void sendWithInput() {
		String data;
		try {
			data = in2.readLine();
			System.out.println("������ ���� : " + data);
			out.println(data); // ������ ������ ����
			out.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println("���� �߻�" + e);
		}

	}

	public String recieve() throws IOException {
		String str2;
		try {
			str2 = in.readLine(); // �����κ��� �ǵ��ƿ��� ������ �о����
			System.out.println("���� ������ : " + str2);
		} catch (SocketException e) {// ����������
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
				System.out.println("���� ��Ʈ ���� / ���� ����");

				return false;
			}
		}
		if (socket.isConnected() && !socket.isClosed()) {
			System.out.println("���� ��Ʈ ���� / ���� ������ �����");

			return true;
		}
		return false;
	}

}
