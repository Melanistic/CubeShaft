package com.melanistics;

import java.util.Random;

public class UUID
{
	
	public static String toString(long id)
	{
		String s1 = Integer.toString((int)(id >> 0) % 256);
		String s2 = Integer.toString((int)(id >> 8) % 256);
		String s3 = Integer.toString((int)(id >> 16) % 256);
		
		while(s2.length() < 3)	
			s2 = "0" + s2;
		
		while(s3.length() < 3)	
			s3 = "0" + s3;
		
		return s3 + "." + s2 + "." + s1;
	}
	
	public static long getRandomUUID(boolean par1)
	{
		Random r = new Random();
		int i1, i2, i3;
		i1 = (par1 ? 128 : 0) + r.nextInt(128);
		i2 = r.nextInt(256);
		i3 = r.nextInt(256);
		
		String s1 = "0x" + Integer.toHexString(i1) + Integer.toHexString(i2) + Integer.toHexString(i3);
		
		long l = Long.decode(s1);
		return l;
	}
	
	public static boolean isPlayerUUID(long l)
	{
		return (int)((l >> 16) % 256) > 127;
	}
	
	public static void main(String[] arg0)
	{
		
	}
}

