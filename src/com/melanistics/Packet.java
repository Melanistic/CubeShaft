package com.melanistics;

import java.io.InputStream;
import java.io.OutputStream;

public abstract class Packet
{
	public Packet()
	{
		
	}
	
	public abstract void readData(InputStream in);
	
	public abstract void writeData(OutputStream out);
	
	
}
