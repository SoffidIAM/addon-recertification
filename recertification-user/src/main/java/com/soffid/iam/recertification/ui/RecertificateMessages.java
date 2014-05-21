package com.soffid.iam.recertification.ui;

import java.util.Collection;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.Set;

import org.zkoss.util.Locales;

public class RecertificateMessages implements Map<String, String> {
	private static final String BUNDLE_NAME = "com.soffid.iam.recertification.ui.messages"; //$NON-NLS-1$
	private ResourceBundle bundle;

	public RecertificateMessages() {
		super();
		bundle = ResourceBundle.getBundle(BUNDLE_NAME, Locales.getCurrent(), getClass().getClassLoader(),
				ResourceBundle.Control.getNoFallbackControl(
    					ResourceBundle.Control.FORMAT_PROPERTIES));
	}

	public int size() {
		return Integer.MAX_VALUE;
	}


	public boolean isEmpty() {
		return false;
	}


	public boolean containsKey(Object key) {
		return true;
	}


	public boolean containsValue(Object value) {
		return false;
	}


	public String get(Object key) {
		try
		{
			return bundle.getString(key.toString());
		}
		catch (MissingResourceException e)
		{
			return '!' + key.toString() + '!';
		}
	}


	public String put(String key, String value) {
		return null;
	}


	public String remove(Object key) {
		return null;
	}


	public void putAll(Map<? extends String, ? extends String> m) {
	}


	public void clear() {
	}


	public Set<String> keySet() {
		return null;
	}


	public Collection<String> values() {
		return null;
	}


	public Set<java.util.Map.Entry<String, String>> entrySet() {
		return null;
	}
}
