package com.raddle.utils;

import java.io.File;
import java.io.IOException;
import java.util.LinkedHashSet;
import java.util.Set;

import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;
import org.springframework.util.StringUtils;

public class AntPathFileUtils {
    private static PathMatcher pathMatcher = new AntPathMatcher();

    @SuppressWarnings("unchecked")
    public static Set<File> findFiles(String locationPattern) {
        int prefixEnd = locationPattern.indexOf(":") + 1;
        if (pathMatcher.isPattern(locationPattern.substring(prefixEnd))) {
            String rootDirPath = determineRootDir(locationPattern);
            String subPattern = locationPattern.substring(rootDirPath.length());
            File rootDir = new File(rootDirPath).getAbsoluteFile();
            try {
                return retrieveMatchingFiles(rootDir, subPattern);
            } catch (IOException e) {
                throw new RuntimeException("can't find files by pattern [" + locationPattern + "]", e);
            }
        } else {
            Set result = new LinkedHashSet(8);
            result.add(new File(locationPattern).getAbsoluteFile());
            return result;
        }
    }

    @SuppressWarnings("unchecked")
    private static Set retrieveMatchingFiles(File rootDir, String pattern) throws IOException {
        if (!rootDir.isDirectory()) {
        	return new LinkedHashSet();
        }
        String fullPattern = StringUtils.replace(rootDir.getAbsolutePath(), File.separator, "/");
        if (!pattern.startsWith("/")) {
            fullPattern += "/";
        }
        fullPattern = fullPattern + StringUtils.replace(pattern, File.separator, "/");
        Set result = new LinkedHashSet(8);
        doRetrieveMatchingFiles(fullPattern, rootDir, result);
        return result;
    }

    @SuppressWarnings("unchecked")
    private static void doRetrieveMatchingFiles(String fullPattern, File dir, Set result) throws IOException {
        File[] dirContents = dir.listFiles();
        if (dirContents == null) {
            throw new IOException("Could not retrieve contents of directory [" + dir.getAbsolutePath() + "]");
        }
        for (int i = 0; i < dirContents.length; i++) {
            File content = dirContents[i];
            String currPath = StringUtils.replace(content.getAbsolutePath(), File.separator, "/");
            if (content.isDirectory() && pathMatcher.matchStart(fullPattern, currPath + "/")) {
                doRetrieveMatchingFiles(fullPattern, content, result);
            }
            if (pathMatcher.match(fullPattern, currPath)) {
                result.add(content);
            }
        }
    }

    private static String determineRootDir(String location) {
        int prefixEnd = location.indexOf(":") + 1;
        int rootDirEnd = location.length();
        while (rootDirEnd > prefixEnd && pathMatcher.isPattern(location.substring(prefixEnd, rootDirEnd))) {
            rootDirEnd = location.lastIndexOf('/', rootDirEnd - 2) + 1;
        }
        if (rootDirEnd == 0) {
            rootDirEnd = prefixEnd;
        }
        return location.substring(0, rootDirEnd);
    }
}
