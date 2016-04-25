package ws;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class HttpHeader implements Map<String, String> {
	private Map<String, String> headers = new HashMap<String, String>();

	public HttpHeader(InputStream inputStream) {
		try {
			read(inputStream);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public int size() {
		return headers.size();
	}

	@Override
	public boolean isEmpty() {
		return headers.isEmpty();
	}

	@Override
	public boolean containsKey(Object key) {
		return headers.containsKey(key);
	}

	@Override
	public boolean containsValue(Object value) {
		return headers.containsValue(value);
	}

	@Override
	public String get(Object key) {
		return headers.get(key);
	}

	@Override
	public String put(String key, String value) {
		return headers.put(key, value);
	}

	@Override
	public String remove(Object key) {
		return headers.remove(key);
	}

	@Override
	public void putAll(Map<? extends String, ? extends String> m) {
		headers.putAll(m);
	}

	@Override
	public void clear() {
		headers.clear();
	}

	@Override
	public Set<String> keySet() {
		return headers.keySet();
	}

	@Override
	public Collection<String> values() {
		return headers.values();
	}

	@Override
	public Set<java.util.Map.Entry<String, String>> entrySet() {
		return headers.entrySet();
	}

	public void read(InputStream inputStream) throws IOException {
		InputStreamReader isr = new InputStreamReader(inputStream);
		BufferedReader br = new BufferedReader(isr);
		String line = br.readLine();

		headers.put(line,"");			
		while(line.trim().length() != 0) {
			line = br.readLine();
			if (line.trim().length() > 1) {
				String key = line.split(":")[0].trim();
				String value =  line.substring(line.indexOf(":") + 1, line.length()).trim();
				headers.put(key, value);				
			}
		}
	}
}
