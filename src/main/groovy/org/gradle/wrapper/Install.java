/*
 * Copyright 2007 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.gradle.wrapper;

import java.io.*;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * @author Hans Dockter
 */
public class Install {
    public static final String WRAPPER_DIR = "gradle-wrapper";
    public static final String WRAPPER_JAR = WRAPPER_DIR + ".jar";
    public static final String WRAPPER_PROPERTIES = WRAPPER_DIR + ".properties";

    private IDownload download = new Download();

    private boolean alwaysDownload;
    private boolean alwaysUnpack;
    private PathAssembler pathAssembler;

    public Install(boolean alwaysDownload, boolean alwaysUnpack, IDownload download, PathAssembler pathAssembler) {
        this.alwaysDownload = alwaysDownload;
        this.alwaysUnpack = alwaysUnpack;
        this.download = download;
        this.pathAssembler = pathAssembler;
    }

    String createDist(String urlRoot, String distBase, String distPath, String distName, String distVersion,
                      String distClassifier, String zipBase, String zipPath) throws Exception {
        String gradleHome = pathAssembler.gradleHome(distBase, distPath, distName, distVersion);
        File gradleHomeFile = new File(gradleHome);
        if (!alwaysDownload && !alwaysUnpack && gradleHomeFile.isDirectory()) {
            return gradleHome;
        }
        File localZipFile = new File(pathAssembler.distZip(zipBase, zipPath, distName, distVersion, distClassifier));
        if (alwaysDownload || !localZipFile.exists()) {
            File tmpZipFile = new File(localZipFile.getParentFile(), localZipFile.getName() + ".part");
            tmpZipFile.delete();
            String downloadUrl = urlRoot + "/" + distName + "-" + distVersion + "-" + distClassifier + ".zip";
            System.out.println("Downloading " + downloadUrl);
            download.download(downloadUrl, tmpZipFile);
            tmpZipFile.renameTo(localZipFile);
        }
        if (gradleHomeFile.isDirectory()) {
            System.out.println("Deleting directory " + gradleHomeFile.getAbsolutePath());
            deleteDir(gradleHomeFile);
        }
        File distDest = gradleHomeFile.getParentFile();
        System.out.println("Unzipping " + localZipFile.getAbsolutePath() + " to " + distDest.getAbsolutePath());
        unzip(localZipFile, distDest);
        return gradleHome;
    }

    public IDownload getDownload() {
        return download;
    }

    public PathAssembler getPathAssembler() {
        return pathAssembler;
    }

    private boolean deleteDir(File dir) {
        if (dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
        }

        // The directory is now empty so delete it
        return dir.delete();
    }

    public void unzip(File zip, File dest) throws IOException {
        Enumeration entries;
        ZipFile zipFile;

        zipFile = new ZipFile(zip);

        entries = zipFile.entries();

        while (entries.hasMoreElements()) {
            ZipEntry entry = (ZipEntry) entries.nextElement();

            if (entry.isDirectory()) {
                (new File(dest, entry.getName())).mkdirs();
                continue;
            }

            copyInputStream(zipFile.getInputStream(entry),
                    new BufferedOutputStream(new FileOutputStream(new File(dest, entry.getName()))));
        }
        zipFile.close();
    }

    public void copyInputStream(InputStream in, OutputStream out) throws IOException {
        byte[] buffer = new byte[1024];
        int len;

        while ((len = in.read(buffer)) >= 0)
            out.write(buffer, 0, len);

        in.close();
        out.close();
    }

}
