package server;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;

public class Server {
	ServerSocket serverSocket;
	int port = 1998;

	public void deal_byte(Socket socket) {
		byte[] bytes;
		InputStream inputStream = null;
		OutputStream outputStream = null;
		String user = null;
		String password = null;
		String mes = null;
		int flag = 0;// 0是user 1是pswd 用于分两次接收数据
		try {
			// 获取客户端的IP地址等信息
			InetAddress address = socket.getInetAddress();
			System.out.println("IP    :" + address.getHostAddress());

			// 建立好连接后，从socket中获取输入流，并建立缓冲区进行读取
			inputStream = socket.getInputStream();

			// 用户名和密码分两次接受,要进入while两次
			while (true) {
				// 首先读取两个字节表示的长度
				int first = inputStream.read();
				// 如果读取的值为-1 说明到了流的末尾，
				// Socket已经被关闭了，此时将不能再去读取
				if (first == -1) {
					System.out.println("break");
					break;
				}
				int second = inputStream.read();
				int length = (first << 8) + second;

				// 然后构造一个指定长的byte数组
				bytes = new byte[length];
				// 然后读取指定长度的消息即可
				inputStream.read(bytes);
				if (flag == 0) {
					user = new String(bytes, "UTF-8");
					System.out.println("user  :" + user);
					flag++;
				} else {
					password = new String(bytes, "UTF-8");
					System.out.println("pswd  :" + password);

					// 用户名，密码验证
					if (user.equals("rs") && password.equals("123456789"))
						mes = "true";// 验证成功
					else
						mes = "false";// 验证失败
					/****** 发送数据 ******/
					outputStream = socket.getOutputStream();
					// 先获取消息的长度
					byte[] sendBytes = mes.getBytes();
					// 发送消息的长度
					outputStream.write(sendBytes.length >> 8);
					outputStream.write(sendBytes.length);
					// 发送消息数据
					outputStream.write(sendBytes);
					outputStream.flush();
					System.out.println("return_state :" + mes);
					System.out.println("***Finish the respond***\n");
					flag = 0;
					break;
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {// 关闭资源
			try {
				if (inputStream != null)
					inputStream.close();
				if (socket != null)
					socket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public void work() {
		try {
			// 创建一个服务器Socket，即ServerSocket，指定绑定的端口，并监听
			serverSocket = new ServerSocket(port);

			System.out.println("***Server starts，waiting for a client***");

			while (true) {
				// 调用accept()方法开始监听，等待客户端的连接
				Socket socket = serverSocket.accept();// 阻塞特性
				Date date = new Date();
				System.out.println("New request***" + date.toString());
				deal_byte(socket);
			}

		} catch (IOException e) {
			e.printStackTrace();
		} finally {// 程序停止运行的时候关闭serverSocket对象
			try {
				if (serverSocket != null)
					serverSocket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public static void main(String[] args) {
		new Server().work();// 服务器开始运行
	}
}
