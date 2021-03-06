package openjdk.tools.json.internal.pojo;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import openjdk.tools.json.exceptions.JsonInputOutputException;

@SuppressWarnings({ "rawtypes", "unchecked" })
class JsonPojoParser {
	public static final String EMPTY_OBJECT = "~!o~";
	private static final String EMPTY_ARRAY = "~!a~";
	private static final int STATE_READ_START_OBJECT = 0;
	private static final int STATE_READ_FIELD = 1;
	private static final int STATE_READ_VALUE = 2;
	private static final int STATE_READ_POST_VALUE = 3;
	private static final Map<String, String> stringCache = new HashMap<String, String>();

	private final JsonPojoFastPushbackReader input;
	private final Map<Long, JsonPojoElement> objsRead;
	private final StringBuilder strBuf = new StringBuilder(256);
	private final StringBuilder hexBuf = new StringBuilder();
	private final StringBuilder numBuf = new StringBuilder();
	private final boolean useMaps;
	private final Map<String, String> typeNameMap;

	static {
		stringCache.put("", "");
		stringCache.put("true", "true");
		stringCache.put("True", "True");
		stringCache.put("TRUE", "TRUE");
		stringCache.put("false", "false");
		stringCache.put("False", "False");
		stringCache.put("FALSE", "FALSE");
		stringCache.put("null", "null");
		stringCache.put("yes", "yes");
		stringCache.put("Yes", "Yes");
		stringCache.put("YES", "YES");
		stringCache.put("no", "no");
		stringCache.put("No", "No");
		stringCache.put("NO", "NO");
		stringCache.put("on", "on");
		stringCache.put("On", "On");
		stringCache.put("ON", "ON");
		stringCache.put("off", "off");
		stringCache.put("Off", "Off");
		stringCache.put("OFF", "OFF");
		stringCache.put("@id", "@id");
		stringCache.put("@ref", "@ref");
		stringCache.put("@items", "@items");
		stringCache.put("@type", "@type");
		stringCache.put("@keys", "@keys");
		stringCache.put("0", "0");
		stringCache.put("1", "1");
		stringCache.put("2", "2");
		stringCache.put("3", "3");
		stringCache.put("4", "4");
		stringCache.put("5", "5");
		stringCache.put("6", "6");
		stringCache.put("7", "7");
		stringCache.put("8", "8");
		stringCache.put("9", "9");
	}

	JsonPojoParser(JsonPojoFastPushbackReader reader, Map<Long, JsonPojoElement> objectsMap, Map<String, Object> args) {
		input = reader;
		useMaps = Boolean.TRUE.equals(args.get(JsonPojoReader.USE_MAPS));
		objsRead = objectsMap;
		typeNameMap = (Map<String, String>) args.get(JsonPojoReader.TYPE_NAME_MAP_REVERSE);
	}

	private Object readJsonObject() throws IOException {
		boolean done = false;
		String field = null;
		JsonPojoElement<String, Object> object = new JsonPojoElement<String, Object>();
		int state = STATE_READ_START_OBJECT;
		final JsonPojoFastPushbackReader in = input;

		while (!done) {
			int c;
			switch (state) {
			case STATE_READ_START_OBJECT:
				c = skipWhitespaceRead();
				if (c == '{') {
					object.line = in.getLine();
					object.col = in.getCol();
					c = skipWhitespaceRead();
					if (c == '}') {
						return EMPTY_OBJECT;
					}
					in.unread(c);
					state = STATE_READ_FIELD;
				} else {
					error("Input is invalid JSON; object does not start with '{', c=" + c);
				}
				break;

			case STATE_READ_FIELD:
				c = skipWhitespaceRead();
				if (c == '"') {
					field = readString();
					c = skipWhitespaceRead();
					if (c != ':') {
						error("Expected ':' between string field and value");
					}

					if (field.startsWith("@")) {
						if (field.equals("@t")) {
							field = stringCache.get("@type");
						} else if (field.equals("@i")) {
							field = stringCache.get("@id");
						} else if (field.equals("@r")) {
							field = stringCache.get("@ref");
						} else if (field.equals("@k")) {
							field = stringCache.get("@keys");
						} else if (field.equals("@e")) {
							field = stringCache.get("@items");
						}
					}
					state = STATE_READ_VALUE;
				} else {
					error("Expected quote");
				}
				break;

			case STATE_READ_VALUE:
				if (field == null) {
					field = "@items";
				}

				Object value = readValue(object);
				if ("@type".equals(field) && typeNameMap != null) {
					final String substitute = typeNameMap.get(value);
					if (substitute != null) {
						value = substitute;
					}
				}
				object.put(field, value);

				if ("@id".equals(field)) {
					objsRead.put((Long) value, object);
				}
				state = STATE_READ_POST_VALUE;
				break;

			case STATE_READ_POST_VALUE:
				c = skipWhitespaceRead();
				if (c == -1) {
					error("EOF reached before closing '}'");
				}
				if (c == '}') {
					done = true;
				} else if (c == ',') {
					state = STATE_READ_FIELD;
				} else {
					error("Object not ended with '}'");
				}
				break;
			}
		}

		if (useMaps && object.isLogicalPrimitive()) {
			return object.getPrimitiveValue();
		}

		return object;
	}

	Object readValue(JsonPojoElement object) throws IOException {
		int c = skipWhitespaceRead();
		if (c == '"') {
			return readString();
		} else if (c >= '0' && c <= '9' || c == '-') {
			return readNumber(c);
		}
		switch (c) {
		case '{':
			input.unread('{');
			return readJsonObject();
		case '[':
			return readArray(object);
		case ']':
			input.unread(']');
			return EMPTY_ARRAY;
		case 'f':
		case 'F':
			readToken("false");
			return Boolean.FALSE;
		case 'n':
		case 'N':
			readToken("null");
			return null;
		case 't':
		case 'T':
			readToken("true");
			return Boolean.TRUE;
		case -1:
			error("EOF reached prematurely");
		}

		return error("Unknown JSON value type");
	}

	private Object readArray(JsonPojoElement object) throws IOException {
		final List<Object> array = new ArrayList();

		while (true) {
			final Object o = readValue(object);
			if (o != EMPTY_ARRAY) {
				array.add(o);
			}
			final int c = skipWhitespaceRead();

			if (c == ']') {
				break;
			} else if (c != ',') {
				error("Expected ',' or ']' inside array");
			}
		}

		return array.toArray();
	}

	private void readToken(String token) throws IOException {
		final int len = token.length();

		for (int i = 1; i < len; i++) {
			int c = input.read();
			if (c == -1) {
				error("EOF reached while reading token: " + token);
			}
			c = Character.toLowerCase((char) c);
			int loTokenChar = token.charAt(i);

			if (loTokenChar != c) {
				error("Expected token: " + token);
			}
		}
	}

	private Number readNumber(int c) throws IOException {
		final JsonPojoFastPushbackReader in = input;
		final StringBuilder number = numBuf;
		number.setLength(0);
		number.appendCodePoint(c);
		boolean isFloat = false;

		while (true) {
			c = in.read();
			if ((c >= '0' && c <= '9') || c == '-' || c == '+') {
				number.appendCodePoint(c);
			} else if (c == '.' || c == 'e' || c == 'E') {
				number.appendCodePoint(c);
				isFloat = true;
			} else if (c == -1) {
				break;
			} else {
				in.unread(c);
				break;
			}
		}

		try {
			if (isFloat) {
				return Double.parseDouble(number.toString());
			} else {
				return Long.parseLong(number.toString());
			}
		} catch (Exception e) {
			return (Number) error("Invalid number: " + number, e);
		}
	}

	private static final int STRING_START = 0;
	private static final int STRING_SLASH = 1;
	private static final int HEX_DIGITS = 2;

	private String readString() throws IOException {
		final StringBuilder str = strBuf;
		final StringBuilder hex = hexBuf;
		str.setLength(0);
		int state = STRING_START;
		final JsonPojoFastPushbackReader in = input;

		while (true) {
			final int c = in.read();
			if (c == -1) {
				error("EOF reached while reading JSON string");
			}

			if (state == STRING_START) {
				if (c == '"') {
					break;
				} else if (c == '\\') {
					state = STRING_SLASH;
				} else {
					str.appendCodePoint(c);
				}
			} else if (state == STRING_SLASH) {
				switch (c) {
				case '\\':
					str.appendCodePoint('\\');
					break;
				case '/':
					str.appendCodePoint('/');
					break;
				case '"':
					str.appendCodePoint('"');
					break;
				case '\'':
					str.appendCodePoint('\'');
					break;
				case 'b':
					str.appendCodePoint('\b');
					break;
				case 'f':
					str.appendCodePoint('\f');
					break;
				case 'n':
					str.appendCodePoint('\n');
					break;
				case 'r':
					str.appendCodePoint('\r');
					break;
				case 't':
					str.appendCodePoint('\t');
					break;
				case 'u':
					hex.setLength(0);
					state = HEX_DIGITS;
					break;
				default:
					error("Invalid character escape sequence specified: " + c);
				}

				if (c != 'u') {
					state = STRING_START;
				}
			} else {
				if ((c >= '0' && c <= '9') || (c >= 'A' && c <= 'F') || (c >= 'a' && c <= 'f')) {
					hex.appendCodePoint((char) c);
					if (hex.length() == 4) {
						int value = Integer.parseInt(hex.toString(), 16);
						str.appendCodePoint(value);
						state = STRING_START;
					}
				} else {
					error("Expected hexadecimal digits");
				}
			}
		}

		final String s = str.toString();
		final String translate = stringCache.get(s);
		return translate == null ? s : translate;
	}

	private int skipWhitespaceRead() throws IOException {
		JsonPojoFastPushbackReader in = input;
		int c;
		do {
			c = in.read();
		} while (c == ' ' || c == '\n' || c == '\r' || c == '\t');
		return c;
	}

	Object error(String msg) {
		throw new JsonInputOutputException(getMessage(msg));
	}

	Object error(String msg, Exception e) {
		throw new JsonInputOutputException(getMessage(msg), e);
	}

	String getMessage(String msg) {
		return msg + "\nline: " + input.getLine() + ", col: " + input.getCol() + "\n" + input.getLastSnippet();
	}
}
