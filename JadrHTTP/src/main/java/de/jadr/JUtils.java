package de.jadr;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public final class JUtils {

	public static final String ANSI_RESET = "\u001B[0m", ANSI_BLACK = "\u001B[30m", ANSI_RED = "\u001B[31m",
			ANSI_GREEN = "\u001B[32m", ANSI_YELLOW = "\u001B[33m", ANSI_BLUE = "\u001B[34m", ANSI_PURPLE = "\u001B[35m",
			ANSI_CYAN = "\u001B[36m", ANSI_WHITE = "\u001B[37m";

	public static final String ANSI_BLACK_BACKGROUND = "\u001B[40m", ANSI_RED_BACKGROUND = "\u001B[41m",
			ANSI_GREEN_BACKGROUND = "\u001B[42m", ANSI_YELLOW_BACKGROUND = "\u001B[43m",
			ANSI_BLUE_BACKGROUND = "\u001B[44m", ANSI_PURPLE_BACKGROUND = "\u001B[45m",
			ANSI_CYAN_BACKGROUND = "\u001B[46m", ANSI_WHITE_BACKGROUND = "\u001B[47m";

	
	public static byte[] fileToBytearray(File f, long size) throws IOException {
		byte[] b = new byte[(int) size];
		FileInputStream fis = new FileInputStream(f);
		fis.read(b);
		fis.close();
		return b;
	}
	public static final String getFileExtension(File f) {
		String fn = f.getName();
		return fn.substring(fn.lastIndexOf('.') + 1, fn.length());
	}
}
