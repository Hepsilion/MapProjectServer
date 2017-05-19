package com.example.server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import com.example.dao.UserDao;
import com.example.dao.UserDaoFactory;
import com.example.tran.TranObject;
import com.example.tran.TranObjectType;
import com.example.util.MyDate;
import com.example.util.TextMessage;
import com.example.util.User;

/**
 * ����Ϣ�̺߳ʹ�����
 * 
 * @author wutingming
 * 
 */
public class InputThread extends Thread {
	private Socket socket;// socket����
	private OutputThread out;// ���ݽ�����д��Ϣ�̣߳���Ϊ����Ҫ���û��ظ���Ϣ��
	private OutputThreadMap map;// д��Ϣ�̻߳�����
	private ObjectInputStream ois;// ����������
	private boolean isStart = true;// �Ƿ�ѭ������Ϣ

	public InputThread(Socket socket, OutputThread out, OutputThreadMap map) {
		this.socket = socket;
		this.out = out;
		this.map = map;
		try {
			ois = new ObjectInputStream(socket.getInputStream());// ʵ��������������
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	// �ṩ�ӿڸ��ⲿ�رն���Ϣ�߳�
	public void setStart(boolean isStart) {
		this.isStart = isStart;
	}

	@Override
	public void run() {
		try {
			while (isStart) {
				// ��ȡ��Ϣ
				readMessage();
			}
			if (ois != null)
				ois.close();
			if (socket != null)
				socket.close();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	/**
	 * ����Ϣ�Լ�������Ϣ���׳��쳣
	 * 
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	public void readMessage() throws IOException, ClassNotFoundException {
		System.out.println("��ʼ����Ϣ��");
		// ��һ��������
		Object readObject = ois.readObject();// �����ж�ȡ����

		// ͨ��daoģʽ�����̨,���daoʵ��
		UserDao dao = UserDaoFactory.getInstance();
		if (readObject != null && readObject instanceof TranObject) {
			TranObject read_tranObject = (TranObject) readObject;// ת���ɴ������
			switch (read_tranObject.getType()) {
			// ����û���ע��
			case REGISTER:
				User registerUser = (User) read_tranObject.getObject();
				boolean registerResult = dao.register(registerUser);
				if (registerResult)
					System.out.println(MyDate.getDateCN() + " ���û�ע��:"
							+ registerUser.getName());
				// ���û��ظ���Ϣ
				TranObject<User> register2TranObject = new TranObject<User>(
						TranObjectType.REGISTER);
				register2TranObject.setObject(registerUser);
				out.setMessage(register2TranObject);
				break;
			// ������û���¼
			case LOGIN:
				User loginUser = (User) read_tranObject.getObject();
				map.add(loginUser.getName(), out);
				ArrayList<User> list = dao.login(loginUser);
				TranObject<ArrayList<User>> login2Object = new TranObject<ArrayList<User>>(
						TranObjectType.LOGIN);
				login2Object.setObject(list);// �Ѻ����б����ظ��Ķ�����
				out.setMessage(login2Object);// ͬʱ�ѵ�¼��Ϣ�ظ����û�
				if (list != null)
					System.out.println(MyDate.getDateCN() + " �û���"
							+ loginUser.getName() + " ��¼��");
				break;
			// ������û��˳�
			case LOGOUT:
				User logoutUser = (User) read_tranObject.getObject();
				String name = logoutUser.getName();
				System.out.println(MyDate.getDateCN() + " �û���" + name + " ������");
				dao.logout(name);
				// ������������û����ӵ��̶߳�ѭ��
				isStart = false;
				map.remove(name);//��hashMap���Ƴ��ÿͻ���д��Ϣ�߳�
				out.setMessage(null);// ��Ҫ����һ������Ϣȥ����д�߳�
				out.setStart(false);// �ٽ���д�߳�ѭ��			
				break;
			// ��Ӻ�������
			case FRIEND_REQUEST:
				// ��ȡ��Ϣ��Ҫת���Ķ����û�����Ȼ���ȡ����ĸö����д�߳�
				String toUserOfrf = read_tranObject.getToUser();
				OutputThread toOutOfrf = map.getByName(toUserOfrf);
				// ����û�����
				if (toOutOfrf != null) {
					toOutOfrf.setMessage(read_tranObject);
				}
				// ���Ϊ�գ�˵���û��Ѿ�����,�ظ��û�
				else {
					TextMessage text = new TextMessage();
					text.setMessage("��Ǹ���Է�δ��¼������ȷ�������ѵ�¼��");
					TranObject<TextMessage> offText = new TranObject<TextMessage>(
							TranObjectType.MESSAGE);
					offText.setObject(text);
					offText.setFromUser(null);
					out.setMessage(offText);
				}
				break;
			// ͬ���������
			case ANSWER_YES_FRIEND_REQUEST:
				// ��ȡ��Ϣ��Ҫת���Ķ����û�����Ȼ���ȡ����ĸö����д�߳�
				String toUserOfaf = read_tranObject.getToUser();
				String fromUserOfaf = read_tranObject.getFromUser();
				OutputThread toOutOfaf = map.getByName(toUserOfaf);
				// ����û�����
				if (toOutOfaf != null) {
					toOutOfaf.setMessage(read_tranObject);

					// �������˷ֱ���뵽�����˵ĺ������ݿ�
					dao.addFriend(fromUserOfaf, toUserOfaf);
					dao.addFriend(toUserOfaf, fromUserOfaf);
				}
				break;
			// ��ͬ���������
			case ANSWER_NO_FRIEND_REQUEST:
				// ��ȡ��Ϣ��Ҫת���Ķ����û�����Ȼ���ȡ����ĸö����д�߳�
				String toUserOfnf = read_tranObject.getToUser();
				OutputThread toOutOfnf = map.getByName(toUserOfnf);
				// ����û�����
				if (toOutOfnf != null) {
					toOutOfnf.setMessage(read_tranObject);
				}
				break;
			// λ�ù�������
			case LOCATION_SHARE:
				// ��ȡ��Ϣ��Ҫת���Ķ����û�����Ȼ���ȡ����ĸö����д�߳�
				String toUser1 = read_tranObject.getToUser();
				OutputThread toOut1 = map.getByName(toUser1);
				// ����û�����
				if (toOut1 != null) {
					toOut1.setMessage(read_tranObject);
				}
				// ���Ϊ�գ�˵���û��Ѿ�����,�ظ��û�
				else {
					TextMessage text = new TextMessage();
					text.setMessage("��Ǹ���Է�δ��¼������ȷ�������ѵ�¼��");
					TranObject<TextMessage> offText = new TranObject<TextMessage>(
							TranObjectType.MESSAGE);
					offText.setObject(text);
					offText.setFromUser(null);
					out.setMessage(offText);
				}
				break;
			// ͬ��λ�ù�������
			case ANSWER_YES_LOCATION_SHARE:
				// ��ȡ��Ϣ��Ҫת���Ķ����û�����Ȼ���ȡ����ĸö����д�߳�
				String toUserOfal = read_tranObject.getToUser();
				System.out.println("�ͻ��˻�Ӧ����");
				OutputThread toOutOfal = map.getByName(toUserOfal);
				// ����û�����
				if (toOutOfal != null) {
					TextMessage text = new TextMessage();
					text.setMessage("������ͬ��λ�ù���");
					TranObject<TextMessage> offText = new TranObject<TextMessage>(
							TranObjectType.MESSAGE);
					offText.setObject(text);
					toOutOfal.setMessage(offText);
				}
				break;
			// ��ͬ��λ�ù�������
			case ANSWER_NO_LOCATION_SHARE:
				// ��ȡ��Ϣ��Ҫת���Ķ����û�����Ȼ���ȡ����ĸö����д�߳�
				String toUserOfnl = read_tranObject.getToUser();
				OutputThread toOutOfnl = map.getByName(toUserOfnl);
				// ����û�����
				if (toOutOfnl != null) {
					TextMessage text = new TextMessage();
					text.setMessage("���Ѳ�ͬ��λ�ù���");
					TranObject<TextMessage> offText = new TranObject<TextMessage>(
							TranObjectType.MESSAGE);
					offText.setObject(text);
					toOutOfnl.setMessage(offText);
				}
				break;
			case LOCATION:// �����ת����ַ��Ϣ
				// ��ȡ��Ϣ��Ҫת���Ķ����û�����Ȼ���ȡ����ĸö����д�߳�
				String toUser = read_tranObject.getToUser();
				OutputThread toOut = map.getByName(toUser);
				// ����û�����
				if (toOut != null) {
					toOut.setMessage(read_tranObject);
				}
				// ���Ϊ�գ�˵���û��Ѿ�����,�ظ��û�
				else {
					TextMessage text = new TextMessage();
					text.setMessage("��Ǹ���Է�δ��¼������ȷ�������ѵ�¼��");
					TranObject<TextMessage> offText = new TranObject<TextMessage>(
							TranObjectType.MESSAGE);
					offText.setObject(text);
					offText.setFromUser(null);
					out.setMessage(offText);
				}
				break;
			default:
				break;
			}
		}
	}
}
