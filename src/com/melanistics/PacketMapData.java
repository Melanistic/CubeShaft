package com.melanistics;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import com.naronco.cubeshaft.level.Level;

public class PacketMapData extends Packet 
{
	public Level l;
	
	public PacketMapData()
	{
		
	}
	
	public PacketMapData(Level par1)
	{
		l = par1;	
	}
	
	@Override
	public void readData(InputStream par1) 
	{
		try {
			DataInputStream in = new DataInputStream(new GZIPInputStream(par1));
			int h = in.readInt();
			int w = in.readInt();
			int d = in.readInt();
			byte[] b = new byte[h*w*d];
			
			for(int i=0;i<b.length;i++)
			{
				b[i] = in.readByte();
			}
			
			l = new Level();
			l.init("#Server", w, h, d, b);
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}

	@Override
	public void writeData(OutputStream par1) 
	{
		try {
			DataOutputStream out = new DataOutputStream(new GZIPOutputStream(par1));
			
			out.writeInt(l.height);
			out.writeInt(l.width);
			out.writeInt(l.depth);
			
			for(int i=0;i<l.tiles.length;i++)
			{
				out.writeByte(l.tiles[i]);
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
