package com.example.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.example.util.Constants;
import com.example.util.MyDate;

/**
 * 服务器，接受用户注册、登录、离线、转发地址信息
 * @author wutingming
 *
 */
public class Server {
	private ExecutorService executorService;// 线程池
	private ServerSocket serverSocket = null;
	private Socket socket = null;
	//服务器是否启动
	private boolean isStarted = true;

	public Server() {
		try {
			
			System.out.println("MapProjectServer:Server 实例化.....");
			
			// 创建线程池，池中具有(cpu个数*50)条线程
			executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() * 50);
			//构造一个监听某个端口的套接字
			serverSocket = new ServerSocket(Constants.SERVER_PORT);
		} catch (IOException e) 
		{
			e.printStackTrace();
			quit();
		}
	}

	public void start() {
		System.out.println(MyDate.getDateCN() + " 服务器已启动...");
		try {
			while (isStarted) {
				//获得连接的客户端套接字
				socket = serverSocket.accept();
				//与服务器连接的客户端IP地址
				String ip = socket.getInetAddress().toString();
				System.out.println(MyDate.getDateCN() + " 用户：" + ip + " 已建立连接");
				// 为支持多用户并发访问，采用线程池管理每一个用户的连接请求
				if (socket.isConnected())
					executorService.execute(new SocketTask(socket));// 添加到线程池
			}
			if (socket != null)
				socket.close();
			if (serverSocket != null)
				serverSocket.close();
		} catch (IOException e) {
			e.printStackTrace();
			quit();
		}
	}

	private final class SocketTask implements Runnable {
		private Socket socket = null;
		private InputThread in;
		private OutputThread out;
		private OutputThreadMap map;

		public SocketTask(Socket socket) {
			
			System.out.println("MapProjectServer:Server SocketTask 实例化.....");
			
			this.socket = socket;
			map = OutputThreadMap.getInstance();
			
			System.out.println(map);
			
		}

		@Override
		public void run() {
			
			System.out.println("MapProjectServer:Server SocketTask run.....");
			
			out = new OutputThread(socket, map);//以客户端套接字构造一个写消息线程
			// 先实例化写消息线程,（把对应用户的写线程存入map缓存器中）
			in = new InputThread(socket, out, map);// 以客户端套接字构造一个读消息线程
			out.setStart(true);
			in.setStart(true);
			in.start();
			out.start();
		}
	}

	/**
	 * 退出
	 */
	public void quit() {
		try {
			this.isStarted = false;
			serverSocket.close();
			System.out.println(MyDate.getDateCN() + " 服务器已关闭...");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		new Server().start();
	}
}
