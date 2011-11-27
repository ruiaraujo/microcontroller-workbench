package com.doublecheck.bstworkbench.io;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public final class IOUtil {
	public static String readFile(File input){
		final StringBuilder st = new StringBuilder();
		if ( input == null )
			return "";
		try {
		    BufferedReader in = new BufferedReader(new FileReader(input));
		    String str;
		    while ((str = in.readLine()) != null) {
		       st.append(str+'\n');
		    }
		    in.close();
		} catch (IOException e) {
			return null;
		}
		return st.toString();
	}

    public static boolean saveFile(File currentFile, String text) {
        try {
            BufferedWriter out = new BufferedWriter(new FileWriter(currentFile));
            out.write(text);
            out.close();
            return true;
        } catch (IOException e) {
        }
        return false;
    }
}
