package com.melanistics;

import java.util.Arrays;
import java.util.Iterator;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class DataByteArray extends DataBase {

	private String name;
	private byte[] value;
	
	@Override
	public Element writeDataXML(Document parrent) 
	{
		Element e = parrent.createElement("bytearray");
		Element e2 = (Element) e.appendChild(parrent.createElement(name));
		e2.setTextContent(Arrays.toString(value));
		return e;
	}

	@Override
	public void readDataXML(Element e) 
	{
		
	}

	@Override
	public String getName() 
	{
		return name;
	}

	@Override
	public Object getValue() 
	{
		return value;
	}

}
