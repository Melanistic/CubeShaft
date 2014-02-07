package com.melanistics;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;



public class Server 
{
	List<Socket> clients = new ArrayList<Socket>();
	List<String> names = new ArrayList<String>();
	HashMap<String, List<String>> map = new HashMap<String, List<String>>();
	
	public static void main(String[] args) throws IOException 
	{
		Server s = new Server();
		s.startServerThread();
		while(true)
		{
			s.tick();
		}
		
	}
	
	public void startServerThread() throws IOException
	{
		Thread t = new Thread(new ServingThread(1234));
		t.start();
	}
	
	public void tick() throws IOException
	{
		if(clients.size() != names.size())
		{
			killSocket(-1);
		}
		
		//HashMap<String, List<String>> map = new HashMap<String, List<String>>();
		
		//Get
		for(int i=0;i<clients.size();i++)
		{
			try {
			Socket client = clients.get(i);
			String name = names.get(i);
			
			DataInputStream in = new DataInputStream(client.getInputStream());
			if(in.available()>0)
			{
				
				String m = in.readUTF();
				if(m==null || m.length()==0)
				{
					continue;
				}
				m = "["+name+"] " + m;
				System.out.println(m);
				
				for(int j=0;j<names.size();j++)
				{
					addMessage(names.get(j), m);
				}
			}
			} catch(SocketException e) {
				killSocket(i);
			}
		}
		
		//send
		for(int i=0;i<clients.size();i++)
		{
			try {
			Socket client = clients.get(i);
			String name = names.get(i);
			
			if(client.isClosed())
			{
				killSocket(i);
			}
			DataOutputStream out = new DataOutputStream(client.getOutputStream());
			
			if(map.containsKey(name))
			{
				List<String> mess = getMessage(name);
				out.writeInt(mess.size());
				for(String m : mess)
				{
					out.writeUTF(m);
					out.flush();
				}
			}
			} catch(SocketException e) {
				killSocket(i);
			}
		}
		
	}
	
	private void killSocket(int par1)
	{
		if(par1>=0)
		{
			if(names.size()>par1)
			{
				String s = names.remove(par1);	
				System.err.println("remove "+s);
			}
			else
				System.err.println("try to remove "+par1+" from names, but there are only "+names.size()+" objects");
			if(clients.size()>par1)
			{			
				Socket s = clients.remove(par1);
				try {
					s.shutdownInput();
					s.shutdownOutput();
					s.close();
				} catch(IOException e) {
					e.printStackTrace();
				}
			}
			else
				System.err.println("try to remove "+par1+" from clients, but there are only "+clients.size()+" objects");
		}
		else
		{
			for(int i=0; i<Math.max(names.size(), clients.size()); i++)
			{
				killSocket(i);
			}
		}
	}
	
	private void addSocket(String name, Socket s)
	{
		clients.add(s);
		names.add(name);
		map.put(name, new ArrayList<String>());
	}
	
	private void addMessage(String name, String message)
	{
		List<String> l = map.get(name);
		l.add(message);
		map.put(name, l);
	}
	
	private List<String> getMessage(String name)
	{
		List<String> l = new ArrayList<String>(map.get(name));
		map.get(name).clear();
		return l;
	}
	
	public boolean isPlayerValid(String par1)
	{
		for(String s : names)
		{
			if(s.equals(par1))
			{
				return false;
			}
		}
		return true;
		
	}

	private class ServingThread implements Runnable
	{
		private int port;
		
		public ServingThread(int pot)
		{
			port = pot;
		}
		
		@Override
		public void run() 
		{		
			try 
			{
				ServerSocket server = new ServerSocket(port);
				while(true)
				{
					if(false)
					{
						break;
					}
					
					Socket s = server.accept();
					DataInputStream in = new DataInputStream(s.getInputStream());
					DataOutputStream out = new DataOutputStream(s.getOutputStream());
					
					String name = in.readUTF();		
					name = name.replace("\b", "").trim();

					if(!isPlayerValid(name))
					{
						out.writeBoolean(false);
						out.writeUTF("Name allready taken");
						out.flush();
						s.shutdownInput();
						s.shutdownOutput();
						s.close();
					}
					else
					{		
						out.writeBoolean(true);
						out.flush();
						
						
						System.err.println("New Player: "+name);
						while(in.available()<=0);
						if(in.readBoolean())
						{
							System.err.println("Start reconnecting");					
							Socket client = new Socket(s.getInetAddress(), s.getPort()+1);
							System.out.println(s.getRemoteSocketAddress());
							System.out.println(client.getRemoteSocketAddress());
							
							s.shutdownOutput();
							s.shutdownInput();
							s.close();

							if(client.isConnected())
							{
								DataOutputStream cout = new DataOutputStream(client.getOutputStream());
								cout.writeBoolean(true);
								cout.flush();
								
								addSocket(name, client);
								
								System.err.println("add "+name);
							}
						
						}
					}
				}
				server.close();
			} catch (IOException e)	{
				e.printStackTrace();
			}
		}
	}
	
}
