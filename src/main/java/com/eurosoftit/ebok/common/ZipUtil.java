package com.eurosoftit.ebok.common;

import com.google.common.collect.Lists;
import com.google.common.io.Files;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;

import javax.annotation.Nullable;
import java.io.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

@Slf4j
public class ZipUtil {

	private static final String ZIP = ".zip";
	private static byte[] buffer = new byte[1024];

	public static File zipFile(File file, @Nullable String fileNamePart) throws IOException {
		String fileName = (fileNamePart != null ? fileNamePart : file.getName()) + ZIP;
		return zipFiles(Lists.newArrayList(file), fileName);
	}

	public static File zipFiles(Collection<File> files, String zipName) throws IOException {
		File tmpZipFile = new File(Files.createTempDir(), zipName);
		FileOutputStream fos = new FileOutputStream(tmpZipFile.getAbsoluteFile());
		ZipOutputStream zipOut = new ZipOutputStream(fos);
		try {
			for (File file : files) {
				addToArchive(file, zipOut);
			}
		} finally {
			zipOut.close();
			fos.close();
		}
		return tmpZipFile;
	}

	private static void addToArchive(File file, ZipOutputStream zos) throws IOException {
		try (FileInputStream fis = new FileInputStream(file)) {
			ZipEntry zipEntry = new ZipEntry(file.getName());
			zos.putNextEntry(zipEntry);

			int length;
			while ((length = fis.read(buffer)) >= 0) {
				zos.write(buffer, 0, length);
			}
		}
	}

	public static List<byte[]> unzipFiles(byte[] data) throws IOException {
		List<byte[]> files = new ArrayList<>();
		try (ZipInputStream zis = new ZipInputStream(new ByteArrayInputStream(data))) {
			ZipEntry ze = zis.getNextEntry();
			while (ze != null) {
				File unzippedFile = new File(Files.createTempDir(), ze.getName());
				try (FileOutputStream fos = new FileOutputStream(unzippedFile)) {
					int len;
					while ((len = zis.read(buffer)) > 0) {
						fos.write(buffer, 0, len);
					}
					files.add(fileToByteArray(unzippedFile));
				}
				ze = zis.getNextEntry();
			}
		}
		return files;
	}

	public static byte[] unzip(byte[] data) throws IOException {
		List<byte[]> files = unzipFiles(data);
		if (files.size() != 1) {
			throw new IllegalArgumentException();
		}
		return files.get(0);
	}

	private static byte[] fileToByteArray(File file) throws IOException {
		return file != null ? IOUtils.toByteArray(new FileInputStream(file)) : new byte[] {};
	}
}
