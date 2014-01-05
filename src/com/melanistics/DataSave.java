package com.melanistics;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class DataSave {

	private HashMap<String, Integer> ints = new HashMap<>();
	private HashMap<String, Float> floats = new HashMap<>();
	private HashMap<String, Double> doubles = new HashMap<>();
	private HashMap<String, Boolean> bools = new HashMap<>();
	private HashMap<String, String> Strings = new HashMap<>();
	private HashMap<String, DataSave> data = new HashMap<>();

	private String name;

	public DataSave(String par1) {
		name = par1;
	}

	public String getName() {
		return name;
	}

	// write
	public void setBoolean(String n, boolean par1) {
		bools.put(n, par1);
	}

	public void setInt(String n, int par1) {
		ints.put(n, par1);
	}

	public void setFloat(String n, float par1) {
		floats.put(n, par1);
	}

	public void setDouble(String n, double par1) {
		doubles.put(n, par1);
	}

	public void setString(String n, String par1) {
		Strings.put(n, par1);
	}

	public void setData(String n, DataSave par1) {
		data.put(n, par1);
	}

	// read
	public int getInt(String n) {
		return ints.get(n);
	}

	public float getFloat(String n) {
		return floats.get(n);
	}

	public double getDouble(String n) {
		return doubles.get(n);
	}

	public String getString(String n) {
		return Strings.get(n);
	}

	public boolean getBoolean(String n) {
		return bools.get(n);
	}

	public DataSave getData(String n) {
		return data.get(n);
	}

	// ///////////////////////////////////
	public static void main(String[] args) {

		File f = new File("E:/Horror/neu2.xml");

		try {
			DataSave[] data = xmlIO.readIO(new FileInputStream(
					"E:/Horror/neu.xml"));

			xmlIO.writeIO(new FileOutputStream(f), data[0]);

			GZIPOutputStream out = new GZIPOutputStream(new FileOutputStream(
					"E:/Horror/neu3.xml.zip"));

			xmlIO.writeIO(out, data[0]);

			out.close();

		} catch (Exception e) {

			e.printStackTrace();
		}
		System.out.println("stop1");

		try {
			GZIPInputStream in = new GZIPInputStream(new FileInputStream(
					"E:/Horror/neu3.xml.zip"));
			DataSave d = xmlIO.readIO(in)[0];

			xmlIO.writeIO(new FileOutputStream("E:/Horror/neu4.xml"), d);
		} catch (Exception e) {

			e.printStackTrace();
		}
		System.out.println("stop2");
	}

	@Override
	public String toString() {
		String s = name + "\n";
		s += "***Strings***\n";

		for (Iterator<String> i = Strings.keySet().iterator(); i.hasNext();) {
			String d = i.next();
			s += d + ";" + Strings.get(d) + "\n";
		}
		s += "***Int***\n";
		for (Iterator<String> i = ints.keySet().iterator(); i.hasNext();) {
			String d = i.next();
			s += d + ";" + ints.get(d) + "\n";
		}
		s += "***float***\n";
		for (Iterator<String> i = floats.keySet().iterator(); i.hasNext();) {
			String d = i.next();
			s += d + ";" + floats.get(d) + "\n";
		}
		s += "***double***\n";
		for (Iterator<String> i = doubles.keySet().iterator(); i.hasNext();) {
			String d = i.next();
			s += d + ";" + doubles.get(d) + "\n";
		}
		s += "***boolean***\n";
		for (Iterator<String> i = bools.keySet().iterator(); i.hasNext();) {
			String d = i.next();
			s += d + ";" + bools.get(d) + "\n";
		}
		return s;
	}

	public static class xmlIO {
		public static void writeIO(OutputStream out, DataSave data)
				throws Exception {
			Document d = DocumentBuilderFactory.newInstance()
					.newDocumentBuilder().newDocument();
			Element e = (Element) d.appendChild(d.createElement("save"));

			e.appendChild(writeData(data, d));

			Transformer localTransformer = TransformerFactory.newInstance()
					.newTransformer();
			DOMSource localDOMSource = new DOMSource(d);
			StreamResult localStreamResult = new StreamResult(out);
			localTransformer.transform(localDOMSource, localStreamResult);
			out.close();

		}

		private static Element writeData(DataSave data, Document parrent)
				throws Exception {

			Element e = (Element) parrent.createElement("data");
			e.setAttribute("name", data.name);

			if (!data.Strings.isEmpty())
				e.appendChild(writeString(data.Strings, parrent));
			if (!data.ints.isEmpty())
				e.appendChild(writeInts(data.ints, parrent));
			if (!data.floats.isEmpty())
				e.appendChild(writeFloats(data.floats, parrent));
			if (!data.doubles.isEmpty())
				e.appendChild(writeDoubles(data.doubles, parrent));
			if (!data.bools.isEmpty())
				e.appendChild(writeBoolean(data.bools, parrent));

			if (!data.data.isEmpty()) {
				for (Iterator<String> i = data.data.keySet().iterator(); i
						.hasNext();) {
					String key = i.next();
					DataSave d = data.data.get(key);
					d.name = key;
					e.appendChild(writeData(d, parrent));
				}
			}
			return e;
		}

		private static Element writeString(HashMap<String, String> l,
				Document parrent) throws Exception {
			Element e = parrent.createElement("string");
			Iterator<String> k = l.keySet().iterator();
			while (k.hasNext()) {
				String key = k.next();
				Element e2 = (Element) e
						.appendChild(parrent.createElement(key));
				e2.setTextContent(l.get(key));
			}
			return (Element) e;
		}

		private static Element writeInts(HashMap<String, Integer> l,
				Document parrent) throws Exception {
			Element e = parrent.createElement("int");
			Iterator<String> k = l.keySet().iterator();
			while (k.hasNext()) {
				String key = k.next();
				Element e2 = (Element) e
						.appendChild(parrent.createElement(key));
				e2.setTextContent(l.get(key) + "");
			}
			return (Element) e;
		}

		private static Element writeFloats(HashMap<String, Float> l,
				Document parrent) throws Exception {
			Element e = parrent.createElement("float");
			Iterator<String> k = l.keySet().iterator();
			while (k.hasNext()) {
				String key = k.next();
				Element e2 = (Element) e
						.appendChild(parrent.createElement(key));
				e2.setTextContent(l.get(key) + "");
			}
			return (Element) e;
		}

		private static Element writeDoubles(HashMap<String, Double> l,
				Document parrent) throws Exception {
			Element e = parrent.createElement("double");
			Iterator<String> k = l.keySet().iterator();
			while (k.hasNext()) {
				String key = k.next();
				Element e2 = (Element) e
						.appendChild(parrent.createElement(key));
				e2.setTextContent(l.get(key) + "");
			}
			return (Element) e;
		}

		private static Element writeBoolean(HashMap<String, Boolean> l,
				Document parrent) throws Exception {
			Element e = parrent.createElement("boolean");
			Iterator<String> k = l.keySet().iterator();
			while (k.hasNext()) {
				String key = k.next();
				Element e2 = (Element) e
						.appendChild(parrent.createElement(key));
				e2.setTextContent(l.get(key) + "");
			}
			return (Element) e;
		}

		public static DataSave[] readIO(InputStream in) throws Exception {
			Document d = null;

			DocumentBuilderFactory localDocumentBuilderFactory = DocumentBuilderFactory
					.newInstance();
			DocumentBuilder localDocumentBuilder = localDocumentBuilderFactory
					.newDocumentBuilder();
			d = localDocumentBuilder.parse(in);

			Element e = d.getDocumentElement();
			NodeList n = e.getChildNodes();

			List<DataSave> l = new ArrayList<>();
			for (int i = 0; i < n.getLength(); i++) {
				Node r = n.item(i);
				if (r.getNodeName().equals("data")) {
					DataSave s = readData((Element) r);
					if (d != null)
						l.add(s);
				}
			}
			DataSave[] data = new DataSave[l.size()];
			in.close();
			return l.toArray(data);

		}

		private static DataSave readData(Element e) {
			DataSave s = new DataSave("");

			NodeList n = e.getChildNodes();
			if (e.hasAttribute("name")) {
				s.name = e.getAttribute("name");
			}
			for (int i = 0; i < n.getLength(); i++) {
				Node r = n.item(i);
				switch (r.getNodeName()) {
				case "string":
					s.Strings = readStrings((Element) r);
					break;
				case "boolean":
					s.bools = readBoolean((Element) r);
					break;
				case "int":
					s.ints = readInts((Element) r);
					break;
				case "float":
					s.floats = readFloats((Element) r);
					break;
				case "double":
					s.doubles = readDoubles((Element) r);
					break;
				case "data":
					s.data.put(readData((Element) r).name,
							readData((Element) r));
					break;
				}

			}
			return s;
		}

		private static HashMap<String, String> readStrings(Element e) {
			HashMap<String, String> l = new HashMap<>();
			NodeList n = e.getChildNodes();
			for (int i = 0; i < n.getLength(); i++) {
				Node r = (Node) (n.item(i));
				if (!r.getNodeName().equals("#text"))
					l.put(r.getNodeName(), String.valueOf(r.getTextContent()));
			}

			return l;

		}

		private static HashMap<String, Integer> readInts(Element e) {
			HashMap<String, Integer> l = new HashMap<>();
			NodeList n = e.getChildNodes();
			for (int i = 0; i < n.getLength(); i++) {
				Node r = (Node) (n.item(i));
				if (!r.getNodeName().equals("#text"))
					l.put(r.getNodeName(), Integer.valueOf(r.getTextContent()));
			}

			return l;

		}

		private static HashMap<String, Float> readFloats(Element e) {
			HashMap<String, Float> l = new HashMap<>();
			NodeList n = e.getChildNodes();
			for (int i = 0; i < n.getLength(); i++) {
				Node r = (Node) (n.item(i));
				if (!r.getNodeName().equals("#text"))
					l.put(r.getNodeName(), Float.valueOf(r.getTextContent()));
			}

			return l;

		}

		private static HashMap<String, Double> readDoubles(Element e) {
			HashMap<String, Double> l = new HashMap<>();
			NodeList n = e.getChildNodes();
			for (int i = 0; i < n.getLength(); i++) {
				Node r = (Node) (n.item(i));
				if (!r.getNodeName().equals("#text"))
					l.put(r.getNodeName(), Double.valueOf(r.getTextContent()));
			}

			return l;

		}

		private static HashMap<String, Boolean> readBoolean(Element e) {
			HashMap<String, Boolean> l = new HashMap<>();
			NodeList n = e.getChildNodes();
			for (int i = 0; i < n.getLength(); i++) {
				Node r = (Node) (n.item(i));
				if (!r.getNodeName().equals("#text"))
					l.put(r.getNodeName(), Boolean.valueOf(r.getTextContent()));
			}

			return l;

		}

	}
}
