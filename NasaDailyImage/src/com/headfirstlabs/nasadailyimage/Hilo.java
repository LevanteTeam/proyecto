package com.headfirstlabs.nasadailyimage;

public class Hilo extends Thread {
	
	String nombre;
	IotdHandler2 proceso=null;
	public Hilo(String n, IotdHandler2 p){
		super(n);
		this.nombre=n;
		this.proceso=p;
		
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		super.run();
		
		proceso.processFeed();
		
	}



}
