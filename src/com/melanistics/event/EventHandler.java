package com.melanistics.event;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.jws.soap.InitParam;

import com.melanistics.Init;

public class EventHandler 
{
	private HashMap<Class<? extends Event>, List<Method>> methods = new HashMap<Class<? extends Event>, List<Method>>();
	private HashMap<Method, Object> classes = new HashMap<Method, Object>();
	
	
	public void registerEventListener(Object o)
	{
		Class<?> c1 = o.getClass();
		Method[] m1 = c1.getDeclaredMethods();
		for(Method m : m1)
		{
			if(m.isAnnotationPresent(Init.Event.class))
			{
				Class<?>[] c2 = m.getParameterTypes();
								
				if(c2.length<1 && c2.length>1)
				{
					throw new IllegalArgumentException("only can register Methods with one argument");
				}
				
				Class<? extends Event> c3;
				try {
				c3 = c2[0].asSubclass(Event.class);
				} catch(ClassCastException e) {
					throw new IllegalArgumentException("argument must be a Subclass from Event");
				}
				
				if(!methods.containsKey(c3))
				{
					methods.put(c3, new ArrayList<Method>());
				}
				
				List<Method> l = methods.get(c3);
				l.add(m);
				methods.put(c3, l);
				classes.put(m, o);
				
				System.out.println("register EventListener "+m);
			}
		}
	}
	
	public void postEvent(Event e)
	{
		try 
		{
			if(methods.containsKey(e.getClass()))
			{
				List<Method> l = methods.get(e.getClass());
				for(Method m : l)
				{
					m.invoke(classes.get(m), e);
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
}
