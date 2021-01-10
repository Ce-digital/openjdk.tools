package openjdk.tools.utilities;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class SystemUtil {

	public static class Crypto {

		public static String encrypt(String key, String clear_text) throws Exception {
			SecretKeySpec skeyspec = new SecretKeySpec(key.getBytes(), "Blowfish");
			Cipher cipher = Cipher.getInstance("Blowfish");
			cipher.init(Cipher.ENCRYPT_MODE, skeyspec);
			byte[] encrypted = cipher.doFinal(clear_text.getBytes());
			return new String(java.util.Base64.getEncoder().encode(encrypted));
		}

		public static Properties encrypt(String key, String identifier_prefix, Properties properties) throws Exception {
			Properties output = (Properties) properties.clone();
			for (String itemKey : output.keySet().toArray(new String[output.keySet().size()])) {
				if (itemKey.startsWith(identifier_prefix)) {
					output.setProperty(itemKey.substring(identifier_prefix.length()),
							encrypt(key, output.getProperty(itemKey)));
					output.remove(itemKey);
				}
			}
			return output;
		}

		public static String decrypt(String key, String encrypted_text) throws Exception {
			SecretKeySpec skeyspec = new SecretKeySpec(key.getBytes(), "Blowfish");
			Cipher cipher = Cipher.getInstance("Blowfish");
			cipher.init(Cipher.DECRYPT_MODE, skeyspec);
			byte[] decrypted = cipher.doFinal(java.util.Base64.getDecoder().decode(encrypted_text));
			return new String(decrypted);
		}

		public static Properties decrypt(String key, String identifier_prefix, Properties properties) throws Exception {
			Properties output = (Properties) properties.clone();
			for (String itemKey : output.keySet().toArray(new String[output.keySet().size()])) {
				if (itemKey.startsWith(identifier_prefix)) {
					output.setProperty(itemKey.substring(identifier_prefix.length()),
							decrypt(key, output.getProperty(itemKey)));
					output.remove(itemKey);
				}
			}
			return output;
		}
	}
	
	public static String exceptionToString(Throwable exception) {
		StringWriter writer = new StringWriter();
		exception.printStackTrace(new PrintWriter(writer));
		return writer.toString();
	}

	public static Map<String, String> Variables() {
		return System.getenv();
	}

	public static Set<String> VariableNames() {
		return System.getenv().keySet();
	}

	public static String Variable(String name) {
		return System.getenv(name);
	}

	public static Properties getProperties() {
		return System.getProperties();
	}

	public static String getProperty(String key) {
		return System.getProperty(key);
	}

	public static void setProperties(Properties properties) {
		System.setProperties(properties);
	}

	public static void setProperty(String key, String value) {
		System.setProperty(key, value);
	}

	public static String Desktop() {
		return System.getProperty("sun.desktop");
	}

	public static String AWTToolkit() {
		return System.getProperty("awt.toolkit");
	}

	public static String JavaSpecificationVersion() {
		return System.getProperty("java.specification.version");
	}

	public static String Encoding() {
		return System.getProperty("sun.jnu.encoding");
	}

	public static String Classpath() {
		return System.getProperty("java.class.path");
	}

	public static String OSVendor() {
		return System.getProperty("java.vm.vendor");
	}

	public static String ArchitectureDataModel() {
		return System.getProperty("sun.arch.data.model");
	}

	public static String OSVendorURL() {
		return System.getProperty("java.vendor.url");
	}

	public static String UserTimezone() {
		return System.getProperty("user.timezone");
	}

	public static String OSName() {
		return System.getProperty("os.name");
	}

	public static String JVMSpecificationVersion() {
		return System.getProperty("java.vm.specification.version");
	}

	public static String JavaLauncher() {
		return System.getProperty("sun.java.launcher");
	}

	public static String UserCountry() {
		return System.getProperty("user.country");
	}

	public static String LibraryPath() {
		return System.getProperty("sun.boot.library.path");
	}

	public static String JavaCommand() {
		return System.getProperty("sun.java.command");
	}

	public static String JDKDebug() {
		return System.getProperty("jdk.debug");
	}

	public static String CPUEndian() {
		return System.getProperty("sun.cpu.endian");
	}

	public static String UserHome() {
		return System.getProperty("user.home");
	}

	public static String UserLanguage() {
		return System.getProperty("user.language");
	}

	public static String JavaSpecificationVendor() {
		return System.getProperty("java.specification.vendor");
	}

	public static String JavaVersionDate() {
		return System.getProperty("java.version.date");
	}

	public static String JavaHome() {
		return System.getProperty("java.home");
	}

	public static String FileSeparator() {
		return System.getProperty("file.separator");
	}

	public static String CompressedOopsMode() {
		return System.getProperty("java.vm.compressedOopsMode");
	}

	public static String LineSeparator() {
		return System.getProperty("line.separator");
	}

	public static String JavaSpecificationName() {
		return System.getProperty("java.specification.name");
	}

	public static String JVMSpecificationVendor() {
		return System.getProperty("java.vm.specification.vendor");
	}

	public static String GraphicsEnvironment() {
		return System.getProperty("java.awt.graphicsenv");
	}

	public static String Compiler() {
		return System.getProperty("sun.management.compiler");
	}

	public static String RuntimeVersion() {
		return System.getProperty("java.runtime.version");
	}

	public static String UserName() {
		return System.getProperty("user.name");
	}

	public static String PathSeparator() {
		return System.getProperty("path.separator");
	}

	public static String OSVersion() {
		return System.getProperty("os.version");
	}

	public static String RuntimeName() {
		return System.getProperty("java.runtime.name");
	}

	public static String FileEncoding() {
		return System.getProperty("file.encoding");
	}

	public static String JVMName() {
		return System.getProperty("java.vm.name");
	}

	public static String JavaBugTrackerURL() {
		return System.getProperty("java.vendor.url.bug");
	}

	public static String UserTempDirectory() {
		return System.getProperty("java.io.tmpdir");
	}

	public static String JavaVersion() {
		return System.getProperty("java.version");
	}

	public static String UserDirectory() {
		return System.getProperty("user.dir");
	}

	public static String OSArchitecture() {
		return System.getProperty("os.arch");
	}

	public static String JVMSpecificationName() {
		return System.getProperty("java.vm.specification.name");
	}

	public static String PrinterJob() {
		return System.getProperty("java.awt.printerjob");
	}

	public static String OSPatchLevel() {
		return System.getProperty("sun.os.patch.level");
	}

	public static String JavaLibraryPath() {
		return System.getProperty("java.library.path");
	}

	public static String JavaVendor() {
		return System.getProperty("java.vendor");
	}

	public static String JVMInfo() {
		return System.getProperty("java.vm.info");
	}

	public static String JVMVersion() {
		return System.getProperty("java.vm.version");
	}

	public static String UnicodeEncoding() {
		return System.getProperty("sun.io.unicode.encoding");
	}

	public static String ClassVersion() {
		return System.getProperty("java.class.version");
	}
}