package org.fasttrackit.util;

import com.google.common.base.Strings;
import com.sdl.selenium.utils.config.WebDriverConfig;
import com.sdl.selenium.web.utils.RetryUtils;
import com.sdl.selenium.web.utils.Utils;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.List;
import java.util.Optional;

public class FileUtility {

    public static File getFileFromDownload() {
        return getFileFromDownload(null);
    }

    public static File getFileFromDownload(String fileName) {
        Utils.sleep(500);
        List<Path> list = RetryUtils.retry(Duration.ofSeconds(20), () -> {
            List<Path> paths = Files.list(Paths.get(WebDriverConfig.getDownloadPath())).toList();
            if (!paths.isEmpty()) {
                boolean present = paths.stream().anyMatch(i -> !i.toFile().getName().contains(".crdownload"));
                if (present) {
                    return paths;
                } else {
                    return null;
                }
            } else {
                return null;
            }
        });
        if (list == null) {
            return null;
        } else {
            Optional<Path> first = list.stream().filter(p -> {
                String name = p.toFile().getName();
                return !Files.isDirectory(p) && (Strings.isNullOrEmpty(fileName) || name.startsWith(fileName));
            }).findFirst();
            if (first.isPresent()) {
                Path path = first.get();
                return path.toFile();
            } else {
                return null;
            }
        }
    }

    public static String getPDFContent(File file) throws IOException {
        PDDocument document = PDDocument.load(file);
        PDFTextStripper pdfStripper = new PDFTextStripper();
        String text = pdfStripper.getText(document);
        document.close();
        return text;
    }
}
