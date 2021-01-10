package openjdk.tools.utilities;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

public class StreamsUtil {

	public static void copy(InputStream input_stream, OutputStream output_stream) throws IOException {
		byte[] buffer = new byte[1024];
		int read;
		while ((read = input_stream.read(buffer)) != -1) {
			output_stream.write(buffer, 0, read);
		}
	}

	public static OutputStream inputstreamToOutputStream(InputStream input_stream) throws IOException {
		OutputStream output_stream = null;
		copy(input_stream, output_stream);
		return output_stream;
	}

	public static File inputstreamToFile(InputStream input_stream, File target_file) throws IOException {
		FileOutputStream output_stream = new FileOutputStream(target_file);
		copy(input_stream, output_stream);
		return target_file;
	}

	public static File inputstreamToFile(InputStream input_stream, String target_file_path) throws IOException {
		new File(FilesUtil.getDirPath(target_file_path)).mkdirs();
		return inputstreamToFile(input_stream, new File(target_file_path));
	}

	public static File inputstreamToFile(InputStream input_stream) throws IOException {
		File target_file = File.createTempFile(UUID.randomUUID().toString(), ".tmp");
		target_file.deleteOnExit();
		return inputstreamToFile(input_stream, target_file);
	}

	public static String inputstreamToString(InputStream input_stream, String character_set) throws IOException {
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		byte[] buffer = new byte[1024];
		int length;
		while ((length = input_stream.read(buffer)) != -1) {
			output.write(buffer, 0, length);
		}
		return output.toString(character_set);
	}

	public static String inputstreamToString(InputStream input_stream, Charset character_set) throws IOException {
		return inputstreamToString(input_stream, character_set.name());
	}

	public static String inputstreamToString(InputStream input_stream) throws IOException {
		return inputstreamToString(input_stream, StandardCharsets.UTF_8.name());
	}

}
