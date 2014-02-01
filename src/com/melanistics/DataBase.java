package com.melanistics;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

public abstract class DataBase 
{
	public abstract String getName();
	
	public abstract Object getValue();
	
	public abstract Element writeDataXML(Document parrent);
	
	public abstract void readDataXML(Element e);
}
