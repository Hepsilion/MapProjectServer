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
 * 读消息线程和处理方法
 * 
 * @author wutingming
 * 
 */
public class InputThread extends Thread {
	private Socket socket;// socket对象
	private OutputThread out;// 传递进来的写消息线程，因为我们要给用户回复消息啊
	private OutputThreadMap map;// 写消息线程缓存器
	private ObjectInputStream ois;// 对象输入流
	private boolean isStart = true;// 是否循环读消息

	public InputThread(Socket socket, OutputThread out, OutputThreadMap map) {
		this.socket = socket;
		this.out = out;
		this.map = map;
		try {
			ois = new ObjectInputStream(socket.getInputStream());// 实例化对象输入流
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	// 提供接口给外部关闭读消息线程
	public void setStart(boolean isStart) {
		this.isStart = isStart;
	}

	@Override
	public void run() {
		try {
			while (isStart) {
				// 读取消息
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
	 * 读消息以及处理消息，抛出异常
	 * 
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	public void readMessage() throws IOException, ClassNotFoundException {
		System.out.println("开始读消息！");
		// 这一块有问题
		Object readObject = ois.readObject();// 从流中读取对象

		// 通过dao模式管理后台,获得dao实例
		UserDao dao = UserDaoFactory.getInstance();
		if (readObject != null && readObject instanceof TranObject) {
			TranObject read_tranObject = (TranObject) readObject;// 转换成传输对象
			switch (read_tranObject.getType()) {
			// 如果用户是注册
			case REGISTER:
				User registerUser = (User) read_tranObject.getObject();
				boolean registerResult = dao.register(registerUser);
				if (registerResult)
					System.out.println(MyDate.getDateCN() + " 新用户注册:"
							+ registerUser.getName());
				// 给用户回复消息
				TranObject<User> register2TranObject = new TranObject<User>(
						TranObjectType.REGISTER);
				register2TranObject.setObject(registerUser);
				out.setMessage(register2TranObject);
				break;
			// 如果是用户登录
			case LOGIN:
				User loginUser = (User) read_tranObject.getObject();
				map.add(loginUser.getName(), out);
				ArrayList<User> list = dao.login(loginUser);
				TranObject<ArrayList<User>> login2Object = new TranObject<ArrayList<User>>(
						TranObjectType.LOGIN);
				login2Object.setObject(list);// 把好友列表加入回复的对象中
				out.setMessage(login2Object);// 同时把登录信息回复给用户
				if (list != null)
					System.out.println(MyDate.getDateCN() + " 用户："
							+ loginUser.getName() + " 登录了");
				break;
			// 如果是用户退出
			case LOGOUT:
				User logoutUser = (User) read_tranObject.getObject();
				String name = logoutUser.getName();
				System.out.println(MyDate.getDateCN() + " 用户：" + name + " 下线了");
				dao.logout(name);
				// 结束结束与该用户连接的线程读循环
				isStart = false;
				map.remove(name);//从hashMap中移除该客户的写消息线程
				out.setMessage(null);// 先要设置一个空消息去唤醒写线程
				out.setStart(false);// 再结束写线程循环			
				break;
			// 添加好友请求
			case FRIEND_REQUEST:
				// 获取消息中要转发的对象用户名，然后获取缓存的该对象的写线程
				String toUserOfrf = read_tranObject.getToUser();
				OutputThread toOutOfrf = map.getByName(toUserOfrf);
				// 如果用户在线
				if (toOutOfrf != null) {
					toOutOfrf.setMessage(read_tranObject);
				}
				// 如果为空，说明用户已经下线,回复用户
				else {
					TextMessage text = new TextMessage();
					text.setMessage("抱歉，对方未登录，请先确定好友已登录！");
					TranObject<TextMessage> offText = new TranObject<TextMessage>(
							TranObjectType.MESSAGE);
					offText.setObject(text);
					offText.setFromUser(null);
					out.setMessage(offText);
				}
				break;
			// 同意好友请求
			case ANSWER_YES_FRIEND_REQUEST:
				// 获取消息中要转发的对象用户名，然后获取缓存的该对象的写线程
				String toUserOfaf = read_tranObject.getToUser();
				String fromUserOfaf = read_tranObject.getFromUser();
				OutputThread toOutOfaf = map.getByName(toUserOfaf);
				// 如果用户在线
				if (toOutOfaf != null) {
					toOutOfaf.setMessage(read_tranObject);

					// 将两个人分别插入到两个人的好友数据库
					dao.addFriend(fromUserOfaf, toUserOfaf);
					dao.addFriend(toUserOfaf, fromUserOfaf);
				}
				break;
			// 不同意好友请求
			case ANSWER_NO_FRIEND_REQUEST:
				// 获取消息中要转发的对象用户名，然后获取缓存的该对象的写线程
				String toUserOfnf = read_tranObject.getToUser();
				OutputThread toOutOfnf = map.getByName(toUserOfnf);
				// 如果用户在线
				if (toOutOfnf != null) {
					toOutOfnf.setMessage(read_tranObject);
				}
				break;
			// 位置共享请求
			case LOCATION_SHARE:
				// 获取消息中要转发的对象用户名，然后获取缓存的该对象的写线程
				String toUser1 = read_tranObject.getToUser();
				OutputThread toOut1 = map.getByName(toUser1);
				// 如果用户在线
				if (toOut1 != null) {
					toOut1.setMessage(read_tranObject);
				}
				// 如果为空，说明用户已经下线,回复用户
				else {
					TextMessage text = new TextMessage();
					text.setMessage("抱歉，对方未登录，请先确定好友已登录！");
					TranObject<TextMessage> offText = new TranObject<TextMessage>(
							TranObjectType.MESSAGE);
					offText.setObject(text);
					offText.setFromUser(null);
					out.setMessage(offText);
				}
				break;
			// 同意位置共享请求
			case ANSWER_YES_LOCATION_SHARE:
				// 获取消息中要转发的对象用户名，然后获取缓存的该对象的写线程
				String toUserOfal = read_tranObject.getToUser();
				System.out.println("客户端回应请求");
				OutputThread toOutOfal = map.getByName(toUserOfal);
				// 如果用户在线
				if (toOutOfal != null) {
					TextMessage text = new TextMessage();
					text.setMessage("好友已同意位置共享！");
					TranObject<TextMessage> offText = new TranObject<TextMessage>(
							TranObjectType.MESSAGE);
					offText.setObject(text);
					toOutOfal.setMessage(offText);
				}
				break;
			// 不同意位置共享请求
			case ANSWER_NO_LOCATION_SHARE:
				// 获取消息中要转发的对象用户名，然后获取缓存的该对象的写线程
				String toUserOfnl = read_tranObject.getToUser();
				OutputThread toOutOfnl = map.getByName(toUserOfnl);
				// 如果用户在线
				if (toOutOfnl != null) {
					TextMessage text = new TextMessage();
					text.setMessage("好友不同意位置共享！");
					TranObject<TextMessage> offText = new TranObject<TextMessage>(
							TranObjectType.MESSAGE);
					offText.setObject(text);
					toOutOfnl.setMessage(offText);
				}
				break;
			case LOCATION:// 如果是转发地址信息
				// 获取消息中要转发的对象用户名，然后获取缓存的该对象的写线程
				String toUser = read_tranObject.getToUser();
				OutputThread toOut = map.getByName(toUser);
				// 如果用户在线
				if (toOut != null) {
					toOut.setMessage(read_tranObject);
				}
				// 如果为空，说明用户已经下线,回复用户
				else {
					TextMessage text = new TextMessage();
					text.setMessage("抱歉，对方未登录，请先确定好友已登录！");
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
