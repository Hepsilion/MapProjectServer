package com.example.util;

import java.io.Serializable;

public class LocationMessage implements Serializable{
	private static final long serialVersionUID = 1L;
	private double lat;//����
	private double lng;//γ��

	public void setLng(double lng) {
		this.lng = lng;
	}

	public void setLat(double lat)
	{
		this.lat=lat;
	}
	
	public double getLat()
	{
		return lat;
	}
	
	public double getLng()
	{
		return lng;
	}
	
	
}
