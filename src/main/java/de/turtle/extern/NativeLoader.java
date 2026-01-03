package de.turtle.extern;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Locale;

import com.sun.jna.Native;

public final class NativeLoader {
    private NativeLoader() {}

    public static Path extractNative(String libBaseName) {
        String os = System.getProperty("os.name").toLowerCase(Locale.ROOT);
        String arch = System.getProperty("os.arch").toLowerCase(Locale.ROOT);

        String folder;
        String fileName;

        if (os.contains("win")) {
            folder = "native/win32-x86_64";
            fileName = libBaseName + ".dll";
        } else if (os.contains("linux")) {
            if (arch.contains("aarch64") || arch.contains("arm64")) {
                folder = "native/linux-aarch64";
            } else if (arch.contains("arm") || arch.contains("armv7l")) {
                folder = "native/linux-armv7l";
            } else {
                folder = "native/linux-" + arch;
            }
            fileName = "lib" + libBaseName + ".so";
        } else {
            throw new IllegalStateException("Unsupported OS/arch: " + os + " / " + arch);
        }

        String resourcePath = "/" + folder + "/" + fileName;
        try (InputStream in = NativeLoader.class.getResourceAsStream(resourcePath)) {
            if (in == null) {
                throw new FileNotFoundException("Resource not found: " + resourcePath);
            }
            Path tmp = Files.createTempFile(libBaseName + "-", fileName);
            Files.copy(in, tmp, StandardCopyOption.REPLACE_EXISTING);
            tmp.toFile().deleteOnExit();
            return tmp;
        } catch (IOException e) {
            throw new UncheckedIOException("Failed to extract native library: " + resourcePath, e);
        }
    }

    public static void loadWithJna(String libBaseName) {
        String dllPath = "C:\\Users\\Admin\\Projects\\PiCloud\\src\\main\\resources\\native\\win32-amd64\\FISext.dll";
        String soPath = "/opt/pi-cloud/linux-aarch64/libFISext.so";
        Native.load(soPath, FisLib.class);
        //Path libPath = extractNative(libBaseName);
        //NativeLibrary.addSearchPath(libBaseName, libPath.getParent().toString());
    }
}
