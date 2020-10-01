package com.j4me.orderparser.tools;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class FileUtils {

    public static List<String> readLineFromFile(Path pathRaw) {
        List<String> lines=new ArrayList<>();
        try (BufferedReader br = Files.newBufferedReader(pathRaw, StandardCharsets.UTF_8)) {

            String line;
            while ((line = br.readLine()) != null) {
                lines.add(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return lines;
    }


}
