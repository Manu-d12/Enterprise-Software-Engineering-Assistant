package org.aiassistant.utils;

import java.io.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class ZipUtil {

    public static void createZipFile(String srcDir, String destZipFile) {
        File srcFile = new File(srcDir);

        createDestZipFile(destZipFile);

        File[] listFiles = srcFile.listFiles();
        try (FileOutputStream fos = new FileOutputStream(destZipFile);
             ZipOutputStream zos = new ZipOutputStream(fos)) {
            if (listFiles != null) {
                for (File file : listFiles) {
                    if (file.isDirectory()) {
                        addFolderToZip(file, file.getName(), zos);
                    } else {
                        addFileToZip(file, zos);
                    }
                }
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private static void createDestZipFile(String destZipFile) {
       try {
           File f = new File(destZipFile);
           f.getParentFile().mkdirs();
           f.createNewFile();
       } catch (Exception ex) {
           ex.printStackTrace();
       }
    }

    private static void addFolderToZip(File folder, String parentFolder, ZipOutputStream zos) throws IOException {
        File[] folders = folder.listFiles();

        try {
            if (folders != null) {
                for (File file : folders) {
                    if (file.isDirectory()) {
                        addFolderToZip(file, parentFolder + "/" + file.getName(), zos);
                        continue;
                    }
                    try (BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file))) {
                        if (zos != null) {
                            zos.putNextEntry(new ZipEntry(parentFolder + "/" + file.getName()));
                            byte[] bytesIn = new byte[4096];
                            int read = 0;
                            while ((read = bis.read(bytesIn)) != -1) {
                                zos.write(bytesIn, 0, read);
                            }
                        }
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }
            }

        } finally {
            try {
                if (zos != null) {
                    zos.closeEntry();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    private static void addFileToZip(File file, ZipOutputStream zos) {
        try (BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file))) {
            zos.putNextEntry(new ZipEntry(file.getName()));

            byte[] bytesIn = new byte[4096];
            int read = 0;
            while ((read = bis.read(bytesIn)) != -1) {
                zos.write(bytesIn, 0, read);
            }
            zos.closeEntry();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
