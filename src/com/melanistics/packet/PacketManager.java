package com.melanistics.packet;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;

import com.melanistics.Init;

public class PacketManager 
{
	public static final PacketManager instance = new PacketManager();
	
	private HashMap<Integer, Class<? extends Packet>> idmap = new HashMap<Integer, Class<? extends Packet>>();
	private HashMap<Class<? extends Packet>, Integer> packmap = new HashMap<Class<? extends Packet>, Integer>();
	
	private HashMap<Class<? extends Packet>, List<Method>> methods = new HashMap<Class<? extends Packet>, List<Method>>();
	private HashMap<Method, Object> classes = new HashMap<Method, Object>();
	
	public PacketManager()
	{
		registerPacket(PacketDataSave.class, 1);
	}
	
	public void registerPacket(Class<? extends Packet> par1, int id)
	{
		if(id<0)
		{
			throw new IllegalArgumentException("Try to registe a Packet with a negativ id: "+id);
		}
		if(idmap.containsKey(id))
		{
			throw new IllegalArgumentException("Cant register "+par1+", the id "+id+" is already Occupit by "+idmap.get(id));
		}
		idmap.put(id, par1);
		packmap.put(par1, id);
	}
	
	public void registerPacketListener(Object o)
	{
		Class<?> c1 = o.getClass();
		Method[] m1 = c1.getDeclaredMethods();
		for(Method m : m1)
		{
			if( m.isAnnotationPresent(Init.Packet.class))
			{
				
				Class<?>[] c2 = m.getParameterTypes();
				
				
				if(c2.length<1 && c2.length>1)
				{
					throw new IllegalArgumentException("only can register Methods with one argument");
				}
				
				Class<? extends Packet> c3;
				try {
				c3 = c2[0].asSubclass(Packet.class);
				} catch(ClassCastException e) {
					throw new IllegalArgumentException("argument must be a Subclass from Packet");
				}
			
				if(!methods.containsKey(c3))
				{
					methods.put(c3, new ArrayList<Method>());
				}
				
				List<Method> l = methods.get(c3);
				l.add(m);
				methods.put(c3, l);
				
				classes.put(m, o);
				
				System.out.println("register PacketListener "+m);
			}
		}
		
	}
	
	public Class<? extends Packet> getPacketbyID(int id)
	{
		return idmap.get(id);
	}
	
	public int getPacketID(Class<? extends Packet> par1)
	{
		return packmap.  get(par1);
	}
	
	
	public void handelPacketInput(Class<? extends Packet> par1, InputStream in)
	{
		Packet p;
		try 
		{
			p = par1.newInstance();
			p.readData(in);
			if(methods.containsKey(par1))
			{
				List<Method> l = methods.get(par1);
				for(Method m : l)
				{
					m.invoke(classes.get(m), p);
				}
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		
		
	}
	
	public static void main(String[] args) 
	{
		PacketManager.instance.registerPacketListener(new Object()
		{
			@Init.Packet
			public void handelVoid(PacketDataSave pack)
			{
				System.out.println(pack);
			}
		});
		
		PacketManager.instance.handelPacketInput(PacketDataSave.class, System.in);
	}
}
