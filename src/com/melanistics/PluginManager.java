package com.melanistics;

import java.io.File;
import java.io.FileInputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class PluginManager 
{

	public void load(File f) 
	{
		try 
		{
			f.mkdir();
			if (!f.isDirectory()) 
			{
				throw new RuntimeException("File is not a Directory");
			}
			File[] files = f.listFiles();

			List<String> s = new ArrayList<>();
			List<URL> url = new ArrayList<>();
			for (int i = 0; i < files.length; i++) 
			{
				File zip = files[i];
				if (zip.isFile()&& (zip.getName().endsWith(".zip") || zip.getName().endsWith(".jar"))) 
				{
					s.addAll(loadfromZip(zip));
					url.add(zip.toURI().toURL());
				}
				else if(zip.isDirectory())
				{
					s.addAll(loadfromDir(zip,zip));
					url.add(zip.toURI().toURL());
				}
			}
			List<Class<?>> l = loadClass(s.toArray(new String[s.size()]), url.toArray(new URL[url.size()]));

			for (Class<?> c : l) 
			{
				invoke(c);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private List<String> loadfromZip(File f) throws Exception 
	{
		System.out.println("Zip Found.\t" + f);
		ZipInputStream zip = new ZipInputStream(new FileInputStream(f));

		List<String> l = new ArrayList<>();

		while (true) 
		{
			ZipEntry entry = zip.getNextEntry();
			if (entry == null) 
			{
				break;
			}
			String name = entry.getName();
			String s = name.split("/")[name.split("/").length - 1];

			if (!entry.isDirectory() && s != null && s.endsWith(".class")) 
			{
				System.out.println(name.replace('/', '.'));
				l.add(name.replace('/', '.'));
			}

		}
		zip.close();
		return l;
	}
	
	private List<String> loadfromDir(File f, File parent)
	{
		if(f==parent)
			System.out.println("Dir found. \t"+f);
		List<String> l = new ArrayList<>();
		for(File file : f.listFiles())
		{
			if(file == null)
			{
				continue;
			}
			
			if(file.isDirectory())
			{
				l.addAll(loadfromDir(file, parent));
			}
			else if(file.isFile() && file.getName().endsWith(".class"))
			{
				File parentfile = file.getParentFile();
				String name = file.getName();
				while(!parent.equals(parentfile))
				{
					name = parentfile.getName() + "." + name;
					parentfile = parentfile.getParentFile();
				}
				name = parent.getName() + "." + name;
				System.out.println(name);
				l.add(name);
			}
		}
		
		return l;
	}
	
	private List<Class<?>> loadClass(String[] par1, URL[] url) throws Exception 
	{
		URLClassLoader load = new URLClassLoader(url);
		List<Class<?>> l = new ArrayList<>();
		for (int i = 0; i < par1.length; i++) 
		{
			String s = par1[i];
			if (s.contains("$")) 
			{
				continue;
			}
			if (s.endsWith(".class")) 
			{
				s = s.replace(".class", "");
			}
			Class<?> c = load.loadClass(s);
			l.add(c);
			System.out.println("Sucsesfull loading " + c);
		}
		load.close();
		return l;
	}

	private static void invoke(Class<?> c) 
	{
		Method[] m = c.getDeclaredMethods();
		for (int i = 0; i < m.length; i++) 
		{
			if (m[i].isAnnotationPresent(Init.Start.class)) 
			{
				Method run = m[i];
				System.out.println("Try to run [" + run + "] at [" + c + "]");
				
				try {
					run.invoke(c.newInstance());
				} catch (Exception e) {
					System.out.println("Failed !!!");
					e.printStackTrace();
				} 
				
			}
		}
	}
}
