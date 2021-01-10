package openjdk.tools.utilities;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

@SuppressWarnings("resource")
public class FilesUtil {

	public static String ConvertToPath(String... items) {
		String output = "";
		for (String item : items) {
			output = output + item + File.separator;
		}
		return output.substring(0, output.length() - 1);
	}

	public static String getDirPath(File file) {
		return Paths.get(file.getAbsolutePath()).getParent().toString();
	}

	public static String getDirPath(String file_path) {
		return Paths.get(file_path).getParent().toString();
	}

	public static List<JarEntry> getJarEntries(File jar_file) throws IOException {
		List<JarEntry> entries = new ArrayList<>();
		Enumeration<JarEntry> enumration = new JarFile(jar_file).entries();
		while (enumration.hasMoreElements()) {
			entries.add((JarEntry) enumration.nextElement());
		}
		return entries;
	}

	public static List<JarEntry> getJarEntries(String jar_file_path) throws IOException {
		return getJarEntries(new File(jar_file_path));
	}

	public static JarEntry getJarFileEntry(File jar_file, String entry_path) throws IOException {
		JarFile jar = new JarFile(jar_file);
		return jar.getJarEntry(entry_path);
	}

	public static JarEntry getJarFileEntry(String jar_file_path, String entry_path) throws IOException {
		JarFile jar = new JarFile(new File(jar_file_path));
		return jar.getJarEntry(entry_path);
	}

	public static File extractFileFromJar(File jar_file, String entry_path) throws IOException {
		JarFile jar = new JarFile(jar_file);
		return StreamsUtil.inputstreamToFile(jar.getInputStream(jar.getEntry(entry_path)));
	}

	public static File extractFileFromJar(String jar_file_path, String entry_path) throws IOException {
		return extractFileFromJar(new File(jar_file_path), entry_path);
	}

	public static File extractFileFromJar(File jar_file, String entry_path, File target_file) throws IOException {
		JarFile jar = new JarFile(jar_file);
		return StreamsUtil.inputstreamToFile(jar.getInputStream(jar.getEntry(entry_path)), target_file);
	}

	public static File extractFileFromJar(String jar_file_path, String entry_path, File target_file)
			throws IOException {
		return extractFileFromJar(new File(jar_file_path), entry_path, target_file);
	}

	public static File extractFileFromJar(File jar_file, String entry_path, String target_file_path)
			throws IOException {
		JarFile jar = new JarFile(jar_file);
		new File(getDirPath(target_file_path)).mkdirs();
		return StreamsUtil.inputstreamToFile(jar.getInputStream(jar.getEntry(entry_path)), new File(target_file_path));
	}

	public static File extractFileFromJar(String jar_file_path, String entry_path, String target_file_path)
			throws IOException {
		new File(getDirPath(target_file_path)).mkdirs();
		return extractFileFromJar(new File(jar_file_path), entry_path, new File(target_file_path));
	}

	public static String extractFileTextDataFromJar(File jar_file, String entry_path) throws IOException {
		JarFile jar = new JarFile(jar_file);
		return StreamsUtil.inputstreamToString(jar.getInputStream(jar.getEntry(entry_path)));
	}

	public static String getFileTextDataFromJar(String jar_file_path, String entry_path) throws IOException {
		return extractFileTextDataFromJar(new File(jar_file_path), entry_path);
	}

}
