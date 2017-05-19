package com.example.server;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * ���д�̵߳Ļ�����
 * @author wutingming
 *
 */
public class OutputThreadMap {
	private HashMap<String, OutputThread> map;
	private static OutputThreadMap instance;

	// ˽�й���������ֹ������ʵ�����Ķ���
	private OutputThreadMap()
	{
		map = new HashMap<String, OutputThread>();
	}

	// ������������ʵ���ķ���
	public synchronized static OutputThreadMap getInstance() 
	{
		if (instance == null) 
		{
			instance = new OutputThreadMap();
		}
		return instance;
	}

	// ���д�̵߳ķ���
	public synchronized void add(String name, OutputThread out) {
		map.put(name, out);
	}

	// �Ƴ�д�̵߳ķ���
	public synchronized void remove(String name) {
		map.remove(name);
	}

	// ȡ��д�̵߳ķ���,Ⱥ�ĵĻ������Ա���ȡ����Ӧд�߳�
	public synchronized OutputThread getByName(String name) {
		return map.get(name);
	}

	// �õ�����д�̷߳��������������������û����͹㲥
	public synchronized List<OutputThread> getAll() {
		List<OutputThread> list = new ArrayList<OutputThread>();
		for (Map.Entry<String, OutputThread> entry : map.entrySet()) {
			list.add(entry.getValue());
		}
		return list;
	}
}
