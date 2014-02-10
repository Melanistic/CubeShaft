package com.naronco.cubeshaft;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;

import org.lwjgl.input.Keyboard;

public class KeyManager 
{
	private static HashMap<String, Integer> keyDef = new HashMap<String, Integer>();
	private static HashMap<String, Integer> keys = new HashMap<String, Integer>();
	private static List<String> names = new ArrayList<String>();
	
	static
	{
		registerKeyBinding("forward", Keyboard.KEY_W);
		registerKeyBinding("left", Keyboard.KEY_A);
		registerKeyBinding("backward", Keyboard.KEY_S);
		registerKeyBinding("right", Keyboard.KEY_D);
		registerKeyBinding("jump", Keyboard.KEY_SPACE);
		
		registerKeyBinding("drop", Keyboard.KEY_Q);
		registerKeyBinding("inventory", Keyboard.KEY_E);
	}
	
	public static void registerKeyBinding(String keyname, int keydefault)
	{
		keyDef.put(keyname, keydefault);
		keys.put(keyname, keydefault);
		names.add(keyname);
	}
	
	public static int getKey(String keyname)
	{
		return keys.get(keyname);
	}
	
	public static int getKeyDefault(String keyname)
	{
		return keyDef.get(keyname);
	}
	
	public static void setKey(String keyname, int key)
	{
		keys.put(keyname, key);
	}
	
	public static boolean isDefault(String key)
	{
		return keys.get(key) == keyDef.get(key);
	}
	
	public static void resetKey(String key)
	{
		keys.put(key, keyDef.get(key));
	}
	
	public static List<String> getNames()
	{
		return names;
	}
	
	public static void saveKeys(Properties p)
	{
		for(String key : names)
		{
			if(!isDefault(key))
			{
				p.setProperty("key_"+key, Keyboard.getKeyName( getKey(key) ));
			}
		}
		
	}
	
	public static void loadKeys(Properties p)
	{
		for(String key : names)
		{
			if(p.containsKey("key_"+key))
			{
				setKey(key, Keyboard.getKeyIndex( p.getProperty("key_"+key) ));
			}
		}
	}
	
	
/**
Shado47:

Shift - Rennen
Capslock - Schleichen Toggle
Left CTRL - Schleichen
Y - Hinlegen Toggle
E - Inventory
Z - Chat
T - Info/Target (Für Tutorials und so Zeugs, wenn man an ner Infostelle steht kann man sich damit die Info anzeigen lassen bzw mit Target kann man sich info darüber anzeigen lassen worauf das Fadenkreuz grade zeigt, z.B. auf was für einen block oder auf was für einen mob und wie viel leben der mob noch hat und ob er grade passiv oder agressiv ist)

Linksklick - Fire/Punch
Rechtsklick - Use/Eat
R - Reload
Q - Drop
Mausrad Press - Creative: Pick Block, Survival: Zoom In
Mausrad Roll - Inv-Slot wechseln
1234567890 - 10 Inv Slots
# - Kommando eingeben
V - Hud Ausblenden
F1 - Hilfe
F2 - Screenshot
F3 - Debug Screen
F4 - Quicksave
F5 - Toggle 3rd Person View
*/
	
}
