package ru.nsu.ccfit.zuev.osu.helper;

import android.os.Build;

import java.io.File;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.LinkedList;

import org.anddev.andengine.util.Debug;

/**
 * @author kairusds
 */
public class FileUtils {

    private FileUtils(){}

    public static File[] listFiles(File directory) {
        return listFiles(directory, (dir, name) -> true);
    }

    public static File[] listFiles(File directory, String endsWith) {
        return listFiles(directory, (dir, name) ->
            name.toLowerCase().endsWith(endsWith));
    }

    public static File[] listFiles(File directory, String[] endsWithExtensions) {
        // some java 7 and 8 methods aren't available until Android 7/8
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            return listFiles(directory, (dir, name) -> {
                for(String extension : endsWithExtensions) {
                    if(name.toLowerCase().endsWith(extension)) {
                        return true;
                    }
                }
                return false;
            });
        }else if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return listFiles(directory, (directory, name) ->
                Arrays.stream(endsWithExtensions).anyMatch(name::endsWith));
        }
    }

    // code is a bit messy
    public static File[] listFiles(File directory, FileFilter filter) {
        File[] filelist = null;
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            filelist = dir.listFiles((dir, name) -> filter.accept(dir, name));
        }else if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            LinkedList<File> cachedFiles = new LinkedList<File>();
            DirectoryStream.Filter<Path> directoryFilter = new DirectoryStream.Filter<Path>() {
                @Override
                public boolean accept(Path entry) {
                    return filter.accept(entry.toFile(), entry.getFileName());
                }
            };
            try(DirectoryStream<Path> stream = Files.newDirectoryStream(Paths.get(directory), directoryFilter)) {
                for(Path path : stream) {
                    cachedFiles.add(path.toFile());
                }
            }catch(Exception err) {
                Debug.e("FileUtils.listFiles: " + err.getMessage(), err);
            }
            filelist = (File[]) cachedFiles.toArray();
        }

        return filelist;
    }

    public interface FileFilter {
        boolean accept(File dir, String name);
    }

}