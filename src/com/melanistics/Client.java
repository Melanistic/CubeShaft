package com.melanistics;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;


public class Client 
{
	public Socket server;
	
	public static void main(String[] args) throws IOException 
	{
		Client c = new Client();
		System.err.println("enter name");
		while(System.in.available()<=0);
		if(System.in.available()>0)
		{
			String name = "";
			while(System.in.available()>0)
			{
				name += (char) System.in.read();
			}
			c.start("localhost", 1234, name);
		}
		System.err.println("start tick");
		while(true)
		{
			c.tick();
		}
	}
	
	public void start(String ip, int port, String name) throws IOException
	{
		Socket s = new Socket(ip, port);
		System.err.println("Start to connect");
		DataOutputStream out = new DataOutputStream(s.getOutputStream());
		out.writeUTF(name);
		out.flush();
		DataInputStream in = new DataInputStream(s.getInputStream());
		while(in.available()<=0);
		if(in.available()>0)
		{
			if(in.readBoolean())
			{	
				ServerSocket sserver = new ServerSocket(s.getLocalPort()+1);
				out.writeBoolean(true);
				out.flush();
				try {
					sserver.setSoTimeout(5000);
					this.server = sserver.accept();
					DataInputStream sin = new DataInputStream(server.getInputStream());
					while(sin.available()<=0);
					if(sin.readBoolean())
					{
						System.err.println("Conected");
					}
					
				} catch (SocketTimeoutException e) {
					System.err.println("Cant reconnect to Server");
				}
			}
			else
			{
				System.err.println(in.readUTF());
			}
		}
		s.shutdownInput();
		s.shutdownOutput();
		s.close();
	}
	public void tick() throws IOException
	{
		if(System.in.available()>0)
		{
			String s = "";
			while(System.in.available() > 0)
			{
				s += (char) System.in.read();
			}
			DataOutputStream out = new DataOutputStream(server.getOutputStream());
			out.writeUTF(s);
			out.flush();
		}
		DataInputStream in = new DataInputStream(server.getInputStream());
		if(in.available()>0)
		{
			for(int i=0;i<in.readInt();i++)
			{
				System.out.println(in.readUTF());
			}
		}
	}
	
}
