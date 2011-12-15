package com.doublecheck.bstworkbench;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.StringTokenizer;
import java.util.prefs.Preferences;


/**
 * Class Resources
 * 
 */
public class Resources {
	
	private Preferences prefs;
	//private ResourceBundle messages;
	private Locale locale;
	// Private constructor prevents instantiation from other classes
	
	private Resources() {
		prefs = Preferences.userRoot().node("MicrocontrollerWorkbench");
		locale = Locale.getDefault();
	}
 
   /**
	* SingletonHolder is loaded on the first execution of Singleton.getInstance() 
	* or the first access to SingletonHolder.INSTANCE, not before.
	*/
	private static class ResourcesHolder { 
		public static final Resources instance = new Resources();
	}
	 
	 /**
	 * @return
	 */
	public static Resources getInstance() {
	     return ResourcesHolder.instance;
	}
	
	/**
	 * Init the ResourceBundle with a non-default locale.
	 * @param locale Locale to be used
	 */
	public void initMessages( Locale locale ){
		this.locale = locale;
	}
	

	
    public boolean setPort( String keyName , String port ){
        try {
            prefs.put(keyName,port);
            prefs.flush();
            return true;
        }catch(Exception e){
            e.printStackTrace();
        }
        return false;
    }
	/**
	 * Get Serial Port
	 * @param keyName
	 * @return
	 */
	public String getPort( String keyName ){
		try {
			return prefs.get(keyName,  null);
		}catch(Exception e){
			e.printStackTrace();
		}
		return null;
	}


	/**
	 * Get the string for the given key.
	 * @param string key of the string
	 * @return string to be used
	 */
	//public String getString(String string) {
		//return messages.getString(string);
	//}

	/**
	 * Get the string for the given key with parameters present in <code>args</code>.
	 * @param string key of the string
	 * @param args 
	 * @return string to be used
	 */
	public String getString(String string, Object ... args ) {
		MessageFormat formatter = new MessageFormat("");
		formatter.setLocale(locale);
		formatter.applyPattern(getString(string));
		return  formatter.format(args);
	}

	
	/**
	 * Returns the Locale being used.
	 * @return Locale in use
	 */
	public Locale getLocale() {
		return locale;
	}
	
	
	
}
