package com.databuck.util;

import org.springframework.stereotype.Component;

@Component
public class ToCamelCase {

	public static String toCamelCase(String init) {
		if (init == null)
			return null;
		StringBuilder ret = new StringBuilder(init.length());
		String[] words = init.split("_");
		ret.append(Character.toLowerCase(words[0].charAt(0)));
		ret.append(words[0].substring(1));
		for (int i = 1; i < words.length; i++) {
			if (!words[i].isEmpty()) {
				ret.append(Character.toUpperCase(words[i].charAt(0)));
				ret.append(words[i].substring(1).toLowerCase());
			}
			if (!(ret.length() == init.length()))
				ret.append("");
		}
		return ret.toString();
	}

	public static String convertString(String s) {
		int ctr = 0;
		int n = s.length();
		char ch[] = s.toCharArray();
		for (int i = 0; i < n; i++) {
			if (i == 0)
				ch[i] = Character.toUpperCase(ch[i]);
			if (ch[i] == ' ') {
				ch[i + 1] = Character.toUpperCase(ch[i + 1]);
				continue;
			} else
				ch[i] = ch[i];
		}
		return String.valueOf(ch, 0, n - ctr);
	}
}
