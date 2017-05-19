package com.example.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.example.util.Constants;
import com.example.util.MyDate;

/**
 * �������������û�ע�ᡢ��¼�����ߡ�ת����ַ��Ϣ
 * @author wutingming
 *
 */
public class Server {
	private ExecutorService executorService;// �̳߳�
	private ServerSocket serverSocket = null;
	private Socket socket = null;
	//�������Ƿ�����
	private boolean isStarted = true;

	public Server() {
		try {
			
			System.out.println("MapProjectServer:Server ʵ����.....");
			
			// �����̳߳أ����о���(cpu����*50)���߳�
			executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() * 50);
			//����һ������ĳ���˿ڵ��׽���
			serverSocket = new ServerSocket(Constants.SERVER_PORT);
		} catch (IOException e) 
		{
			e.printStackTrace();
			quit();
		}
	}

	public void start() {
		System.out.println(MyDate.getDateCN() + " ������������...");
		try {
			while (isStarted) {
				//������ӵĿͻ����׽���
				socket = serverSocket.accept();
				//����������ӵĿͻ���IP��ַ
				String ip = socket.getInetAddress().toString();
				System.out.println(MyDate.getDateCN() + " �û���" + ip + " �ѽ�������");
				// Ϊ֧�ֶ��û��������ʣ������̳߳ع���ÿһ���û�����������
				if (socket.isConnected())
					executorService.execute(new SocketTask(socket));// ��ӵ��̳߳�
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
			
			System.out.println("MapProjectServer:Server SocketTask ʵ����.....");
			
			this.socket = socket;
			map = OutputThreadMap.getInstance();
			
			System.out.println(map);
			
		}

		@Override
		public void run() {
			
			System.out.println("MapProjectServer:Server SocketTask run.....");
			
			out = new OutputThread(socket, map);//�Կͻ����׽��ֹ���һ��д��Ϣ�߳�
			// ��ʵ����д��Ϣ�߳�,���Ѷ�Ӧ�û���д�̴߳���map�������У�
			in = new InputThread(socket, out, map);// �Կͻ����׽��ֹ���һ������Ϣ�߳�
			out.setStart(true);
			in.setStart(true);
			in.start();
			out.start();
		}
	}

	/**
	 * �˳�
	 */
	public void quit() {
		try {
			this.isStarted = false;
			serverSocket.close();
			System.out.println(MyDate.getDateCN() + " �������ѹر�...");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		new Server().start();
	}
}
