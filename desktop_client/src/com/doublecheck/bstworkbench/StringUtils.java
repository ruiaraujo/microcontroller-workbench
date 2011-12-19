package com.doublecheck.bstworkbench;
import java.io.UnsupportedEncodingException;

public class StringUtils {
    

	static final byte[] HEX_CHAR_TABLE = {
		(byte)'0', (byte)'1', (byte)'2', (byte)'3',
		(byte)'4', (byte)'5', (byte)'6', (byte)'7',
		(byte)'8', (byte)'9', (byte)'a', (byte)'b',
		(byte)'c', (byte)'d', (byte)'e', (byte)'f'
	};    

	public static String getHexString(byte[] raw) 
		throws UnsupportedEncodingException 
	{
		byte[] hex = new byte[2 * raw.length];
		int index = 0;

		for (byte b : raw) {
			int v = b & 0xFF;
			hex[index++] = HEX_CHAR_TABLE[v >>> 4];
			hex[index++] = HEX_CHAR_TABLE[v & 0xF];
		}
		return new String(hex, "ASCII");
	}
	public static String getHexString(short[] raw) 
	throws UnsupportedEncodingException 
	{
		byte[] hex = new byte[2 * raw.length];
		int index = 0;
		
		for (short b : raw) {
			int v = b & 0xFF;
			hex[index++] = HEX_CHAR_TABLE[v >>> 4];
			hex[index++] = HEX_CHAR_TABLE[v & 0xF];
		}
		return new String(hex, "ASCII");
	}
	public static String getHexString(short raw) {  
		byte[] hex = new byte[2];
		int v = raw & 0xFF;
		hex[0] = HEX_CHAR_TABLE[v >>> 4];
		hex[1] = HEX_CHAR_TABLE[v & 0xF];
		try {
			return new String(hex, "ASCII");
		} catch (UnsupportedEncodingException e) {}
		return "";
	} 
}