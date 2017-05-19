package com.example.tran;

import java.io.Serializable;

/**
 * ����Ķ���
 * @author wutingming
 *
 * @param <T>
 */
public class TranObject<T> implements Serializable{
	private static final long serialVersionUID = 1L;
	private TranObjectType type;// ���͵���Ϣ����
	private String fromUser;// �����ĸ��û�
	private String toUser;// �����ĸ��û�
	private T object;// ����Ķ���

	public TranObject(TranObjectType type) {
		this.type = type;
	}
	public TranObjectType getType() {
		return type;
	}
	public String getFromUser() {
		return fromUser;
	}
	public void setFromUser(String fromUser) {
		this.fromUser = fromUser;
	}
	public String getToUser() {
		return toUser;
	}
	public void setToUser(String toUser) {
		this.toUser = toUser;
	}
	public T getObject() {
		return object;
	}
	public void setObject(T object) {
		this.object = object;
	}
}
