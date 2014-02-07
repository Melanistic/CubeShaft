package com.melanistics;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.GZIPOutputStream;
import java.util.zip.GZIPInputStream;

public class PacketDataSave extends Packet
{
	private DataSave save;
	
	public PacketDataSave(DataSave par1)
	{
		save = par1;
	}
	
	public PacketDataSave()
	{
		
	}
	
	public void writeData(OutputStream out)
	{
		try {
			DataSave.xmlIO.writeIO(new GZIPOutputStream(out), save);
		} catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public void readData(InputStream in)
	{
		
		try {
		save = DataSave.xmlIO.readIO(new GZIPInputStream(in))[0];
		} catch(Exception e){
			e.printStackTrace();
		}
	}
	
}
