package org.fasttrackit.util;

import com.google.common.base.Strings;
import com.sdl.selenium.utils.config.WebDriverConfig;
import com.sdl.selenium.web.utils.RetryUtils;
import com.sdl.selenium.web.utils.Utils;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import ro.appsheet.ItemBank;
import technology.tabula.ObjectExtractor;
import technology.tabula.Page;
import technology.tabula.Table;
import technology.tabula.extractors.BasicExtractionAlgorithm;
import technology.tabula.extractors.SpreadsheetExtractionAlgorithm;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
public class FileUtility {

    public static File getFileFromDownload() {
        return getFileFromDownload(null);
    }

    public static File getFileFromDownload(String fileName) {
        Utils.sleep(500);
        List<Path> list = RetryUtils.retry(Duration.ofSeconds(40), () -> {
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

    @SneakyThrows
    public static String getPDFContent(File file) {
        PDDocument document = PDDocument.load(file);
        PDFTextStripper pdfStripper = new PDFTextStripper();
        String text = pdfStripper.getText(document);
        document.close();
        return text;
    }

    public static void getPDFContentV2(File file) {
        try (PDDocument document = PDDocument.load(file)) {
            ObjectExtractor oe = new ObjectExtractor(document);
            SpreadsheetExtractionAlgorithm sea = new SpreadsheetExtractionAlgorithm();
            BasicExtractionAlgorithm bea = new BasicExtractionAlgorithm();
            List<ItemBank> lines = new LinkedList<>();
            for (int i = 1; i <= document.getNumberOfPages(); i++) {
                Page page = oe.extract(i);
//                List<Table> tables = sea.extract(page);
                List<Table> tables = bea.extract(page);

                for (Table table : tables) {
                    log.info("Tabel găsit pe pagina " + i + ":");
                    log.info("---------------------------------");
                    ItemBank item = new ItemBank();
                    List<String> line = new ArrayList<>();
                    for (List<technology.tabula.RectangularTextContainer> row : table.getRows()) {
                        for (technology.tabula.RectangularTextContainer cell : row) {
                            String text = cell.getText().replace("\r", " ").trim();
                            if (!text.isEmpty()) {
                                if (text.contains("REF:")) {
                                    line.add(text);
                                    item.setValue(String.join(",", line));
                                    lines.add(item);
                                    line = new ArrayList<>();
                                    item = new ItemBank();
                                } else if (item.getPrice() == null && text.contains(".")) {
                                    Pattern pattern = Pattern.compile("^\\d{1,3}(,\\d{3})*(\\.\\d{2})?$");
                                    Matcher matcher = pattern.matcher(text);
                                    if (matcher.find()) {
                                        item.setPrice(text);
                                        if (text.equals("49.29")) {
                                            Utils.sleep(1);
                                        }
                                    }
                                } else if (item.getDate() == null && text.contains("/")) {
                                    Pattern pattern = Pattern.compile("(\\d{2}/\\d{2}/\\d{4})");
                                    Matcher matcher = pattern.matcher(text);
                                    if (matcher.find()) {
                                        String group = matcher.group();
                                        item.setDate(group);
                                        try {
                                            text = text.split(group)[1].trim();
                                        } catch (Exception e) {
                                            text = "";
                                        }
                                        line.add(text);
                                    }
                                } else {
                                    line.add(text);
                                }
                            }
                        }
                    }
                    log.info("---------------------------------");
                }
            }
            lines = lines.stream().filter(i -> i.getPrice() != null && !i.getValue().contains("Info clienti:")).toList();
            Utils.sleep(1);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
