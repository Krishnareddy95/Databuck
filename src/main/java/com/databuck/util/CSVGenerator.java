package com.databuck.util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.stereotype.Component;

@Component
public class CSVGenerator {

	public String mapToCSV(List<Map<String, Object>> tranDetailsResult) {
		List<String> headers = tranDetailsResult.stream().flatMap(map -> map.keySet().stream()).distinct()
				.collect(Collectors.toList());
		final StringBuffer sb = new StringBuffer();
		for (int i = 0; i < headers.size(); i++) {
			sb.append(headers.get(i));
			sb.append(i == headers.size() - 1 ? "\n" : ",");
		}
		for (Map<String, Object> map : tranDetailsResult) {
			for (int i = 0; i < headers.size(); i++) {
				sb.append(map.get(headers.get(i)));
				sb.append(i == headers.size() - 1 ? "\n" : ",");
			}
		}
		return sb.toString();
	}

	public String toCSV(List<HashMap<String, String>> tranDetailsResult) {
		List<String> headers = tranDetailsResult.stream().flatMap(map -> map.keySet().stream()).distinct()
				.collect(Collectors.toList());
		final StringBuffer sb = new StringBuffer();
		for (int i = 0; i < headers.size(); i++) {
			sb.append(headers.get(i));
			sb.append(i == headers.size() - 1 ? "\n" : ",");
		}
		for (HashMap<String, String> map : tranDetailsResult) {
			for (int i = 0; i < headers.size(); i++) {
				sb.append(map.get(headers.get(i)));
				sb.append(i == headers.size() - 1 ? "\n" : ",");
			}
		}
		return sb.toString();
	}

	public String getCSVString(JSONArray ja, JSONArray headerValues, JSONArray columns) throws JSONException {
		JSONObject jo = ja.optJSONObject(0);
		if (jo != null) {
			JSONArray names = jo.names();
			if (names != null) {
				return rowToString(headerValues) + toString(columns, ja);
			}
		}
		return null;
	}

	private String toString(JSONArray names, JSONArray ja) throws JSONException {
		if (names == null || names.length() == 0) {
			return null;
		}
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < ja.length(); i += 1) {
			JSONObject jo = ja.optJSONObject(i);
			if (jo != null) {
				sb.append(rowToString(jo.toJSONArray(names)));
			}
		}
		return sb.toString();
	}

	private String rowToString(JSONArray ja) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < ja.length(); i += 1) {
			if (i > 0) {
				sb.append(',');
			}
			Object object = ja.opt(i);
			if (object != null) {
				String string = object.toString();
				if (string.length() > 0 && (string.indexOf(',') >= 0 || string.indexOf('\n') >= 0
						|| string.indexOf('\r') >= 0 || string.indexOf(0) >= 0 || string.charAt(0) == '"')) {
					sb.append('"');
					int length = string.length();
					for (int j = 0; j < length; j += 1) {
						char c = string.charAt(j);
						if (c >= ' ' && c != '"') {
							sb.append(c);
						}
					}
					sb.append('"');
				} else {
					sb.append(string);
				}
			}
		}
		sb.append('\n');
		return sb.toString();
	}

}
