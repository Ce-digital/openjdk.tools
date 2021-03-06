package openjdk.tools.json.internal.pojo;

import java.io.IOException;
import java.io.Writer;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import openjdk.tools.json.exceptions.JsonInputOutputException;

@SuppressWarnings({ "rawtypes", "unchecked" })
public class JsonPojoWriters {
	private JsonPojoWriters() {
	}

	public static class TimeZoneWriter implements JsonPojoWriter.JsonClassWriter {
		public void write(Object obj, boolean showType, Writer output) throws IOException {
			TimeZone cal = (TimeZone) obj;
			output.write("\"zone\":\"");
			output.write(cal.getID());
			output.write('"');
		}

		public boolean hasPrimitiveForm() {
			return false;
		}

		public void writePrimitiveForm(Object o, Writer output) throws IOException {
		}
	}

	public static class CalendarWriter implements JsonPojoWriter.JsonClassWriter {
		public void write(Object obj, boolean showType, Writer output) throws IOException {
			Calendar cal = (Calendar) obj;
			JsonPojoMetaUtils.dateFormat.get().setTimeZone(cal.getTimeZone());
			output.write("\"time\":\"");
			output.write(JsonPojoMetaUtils.dateFormat.get().format(cal.getTime()));
			output.write("\",\"zone\":\"");
			output.write(cal.getTimeZone().getID());
			output.write('"');
		}

		public boolean hasPrimitiveForm() {
			return false;
		}

		public void writePrimitiveForm(Object o, Writer output) throws IOException {
		}
	}

	public static class DateWriter implements JsonPojoWriter.JsonClassWriter, JsonPojoWriter.JsonClassWriterEx {
		public void write(Object obj, boolean showType, Writer output) throws IOException {
			throw new JsonInputOutputException("Should never be called.");
		}

		public void write(Object obj, boolean showType, Writer output, Map args) throws IOException {
			Date date = (Date) obj;
			Object dateFormat = args.get(DATE_FORMAT);
			if (dateFormat instanceof String) {
				dateFormat = new SimpleDateFormat((String) dateFormat, Locale.ENGLISH);
				args.put(DATE_FORMAT, dateFormat);
			}
			if (showType) {
				output.write("\"value\":");
			}

			if (dateFormat instanceof Format) {
				output.write("\"");
				output.write(((Format) dateFormat).format(date));
				output.write("\"");
			} else {
				output.write(Long.toString(((Date) obj).getTime()));
			}
		}

		public boolean hasPrimitiveForm() {
			return true;
		}

		public void writePrimitiveForm(Object o, Writer output) throws IOException {
			throw new JsonInputOutputException("Should never be called.");
		}

		public void writePrimitiveForm(Object o, Writer output, Map args) throws IOException {
			if (args.containsKey(DATE_FORMAT)) {
				write(o, false, output, args);
			} else {
				output.write(Long.toString(((Date) o).getTime()));
			}
		}
	}

	public static class TimestampWriter implements JsonPojoWriter.JsonClassWriter {
		public void write(Object o, boolean showType, Writer output) throws IOException {
			Timestamp tstamp = (Timestamp) o;
			output.write("\"time\":\"");
			output.write(Long.toString((tstamp.getTime() / 1000) * 1000));
			output.write("\",\"nanos\":\"");
			output.write(Integer.toString(tstamp.getNanos()));
			output.write('"');
		}

		public boolean hasPrimitiveForm() {
			return false;
		}

		public void writePrimitiveForm(Object o, Writer output) throws IOException {
		}
	}

	public static class ClassWriter implements JsonPojoWriter.JsonClassWriter {
		public void write(Object obj, boolean showType, Writer output) throws IOException {
			String value = ((Class) obj).getName();
			output.write("\"value\":");
			writeJsonUtf8String(value, output);
		}

		public boolean hasPrimitiveForm() {
			return true;
		}

		public void writePrimitiveForm(Object o, Writer output) throws IOException {
			writeJsonUtf8String(((Class) o).getName(), output);
		}
	}

	public static class JsonStringWriter implements JsonPojoWriter.JsonClassWriter {
		public void write(Object obj, boolean showType, Writer output) throws IOException {
			output.write("\"value\":");
			writeJsonUtf8String((String) obj, output);
		}

		public boolean hasPrimitiveForm() {
			return true;
		}

		public void writePrimitiveForm(Object o, Writer output) throws IOException {
			writeJsonUtf8String((String) o, output);
		}
	}

	public static class LocaleWriter implements JsonPojoWriter.JsonClassWriter {
		public void write(Object obj, boolean showType, Writer output) throws IOException {
			Locale locale = (Locale) obj;

			output.write("\"language\":\"");
			output.write(locale.getLanguage());
			output.write("\",\"country\":\"");
			output.write(locale.getCountry());
			output.write("\",\"variant\":\"");
			output.write(locale.getVariant());
			output.write('"');
		}

		public boolean hasPrimitiveForm() {
			return false;
		}

		public void writePrimitiveForm(Object o, Writer output) throws IOException {
		}
	}

	public static class BigIntegerWriter implements JsonPojoWriter.JsonClassWriter {
		public void write(Object obj, boolean showType, Writer output) throws IOException {
			if (showType) {
				BigInteger big = (BigInteger) obj;
				output.write("\"value\":\"");
				output.write(big.toString(10));
				output.write('"');
			} else {
				writePrimitiveForm(obj, output);
			}
		}

		public boolean hasPrimitiveForm() {
			return true;
		}

		public void writePrimitiveForm(Object o, Writer output) throws IOException {
			BigInteger big = (BigInteger) o;
			output.write('"');
			output.write(big.toString(10));
			output.write('"');
		}
	}

	public static class AtomicBooleanWriter implements JsonPojoWriter.JsonClassWriter {
		public void write(Object obj, boolean showType, Writer output) throws IOException {
			if (showType) {
				AtomicBoolean value = (AtomicBoolean) obj;
				output.write("\"value\":");
				output.write(value.toString());
			} else {
				writePrimitiveForm(obj, output);
			}
		}

		public boolean hasPrimitiveForm() {
			return true;
		}

		public void writePrimitiveForm(Object o, Writer output) throws IOException {
			AtomicBoolean value = (AtomicBoolean) o;
			output.write(value.toString());
		}
	}

	public static class AtomicIntegerWriter implements JsonPojoWriter.JsonClassWriter {
		public void write(Object obj, boolean showType, Writer output) throws IOException {
			if (showType) {
				AtomicInteger value = (AtomicInteger) obj;
				output.write("\"value\":");
				output.write(value.toString());
			} else {
				writePrimitiveForm(obj, output);
			}
		}

		public boolean hasPrimitiveForm() {
			return true;
		}

		public void writePrimitiveForm(Object o, Writer output) throws IOException {
			AtomicInteger value = (AtomicInteger) o;
			output.write(value.toString());
		}
	}

	public static class AtomicLongWriter implements JsonPojoWriter.JsonClassWriter {
		public void write(Object obj, boolean showType, Writer output) throws IOException {
			if (showType) {
				AtomicLong value = (AtomicLong) obj;
				output.write("\"value\":");
				output.write(value.toString());
			} else {
				writePrimitiveForm(obj, output);
			}
		}

		public boolean hasPrimitiveForm() {
			return true;
		}

		public void writePrimitiveForm(Object o, Writer output) throws IOException {
			AtomicLong value = (AtomicLong) o;
			output.write(value.toString());
		}
	}

	public static class BigDecimalWriter implements JsonPojoWriter.JsonClassWriter {
		public void write(Object obj, boolean showType, Writer output) throws IOException {
			if (showType) {
				BigDecimal big = (BigDecimal) obj;
				output.write("\"value\":\"");
				output.write(big.toPlainString());
				output.write('"');
			} else {
				writePrimitiveForm(obj, output);
			}
		}

		public boolean hasPrimitiveForm() {
			return true;
		}

		public void writePrimitiveForm(Object o, Writer output) throws IOException {
			BigDecimal big = (BigDecimal) o;
			output.write('"');
			output.write(big.toPlainString());
			output.write('"');
		}
	}

	public static class StringBuilderWriter implements JsonPojoWriter.JsonClassWriter {
		public void write(Object obj, boolean showType, Writer output) throws IOException {
			StringBuilder builder = (StringBuilder) obj;
			output.write("\"value\":\"");
			output.write(builder.toString());
			output.write('"');
		}

		public boolean hasPrimitiveForm() {
			return true;
		}

		public void writePrimitiveForm(Object o, Writer output) throws IOException {
			StringBuilder builder = (StringBuilder) o;
			output.write('"');
			output.write(builder.toString());
			output.write('"');
		}
	}

	public static class StringBufferWriter implements JsonPojoWriter.JsonClassWriter {
		public void write(Object obj, boolean showType, Writer output) throws IOException {
			StringBuffer buffer = (StringBuffer) obj;
			output.write("\"value\":\"");
			output.write(buffer.toString());
			output.write('"');
		}

		public boolean hasPrimitiveForm() {
			return true;
		}

		public void writePrimitiveForm(Object o, Writer output) throws IOException {
			StringBuffer buffer = (StringBuffer) o;
			output.write('"');
			output.write(buffer.toString());
			output.write('"');
		}
	}

	static final String DATE_FORMAT = JsonPojoWriter.DATE_FORMAT;

	protected static void writeJsonUtf8String(String s, final Writer output) throws IOException {
		JsonPojoWriter.writeJsonUtf8String(s, output);
	}
}
