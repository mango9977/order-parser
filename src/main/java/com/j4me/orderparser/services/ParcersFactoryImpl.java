package com.j4me.orderparser.services;

import com.j4me.orderparser.tools.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class ParcersFactoryImpl implements ParsersFactory {

    @Autowired
    @Qualifier("fromCSV")
    private ParserService parserCSVService;

    @Autowired
    @Qualifier("fromJSON")
    private ParserService parserJSONService;

    @Autowired
    @Qualifier("fromEXCEL")
    private ParserService parserExcelService;

    private List<String> stringList;
    private List<String> outList;

    @Override
    public void startParse(String[] args) {

        for (int i = 0; i < args.length; i++) {
            File file = new File(args[i]);
            try {
                if (!file.exists()) throw new FileNotFoundException();
            } catch (Exception e) {
                //TODO
            }
            Pattern pattern = Pattern.compile("\\.json$|\\.csv$");
            Matcher matcher = pattern.matcher(args[i]);

            if (matcher.find()) {
                switch (matcher.group().replace(".", "")) {
                    case "json":
                        stringList = FileUtils.readLineFromFile(file.toPath());
                        parserJSONService.parse(stringList, file.getName()).stream().forEach(System.out::println);
                        break;
                    case "csv":
                        stringList = FileUtils.readLineFromFile(file.toPath());
                        parserCSVService.parse(stringList, file.getName()).stream().forEach(System.out::println);;
                        break;
                    case "xlsx":
                        stringList = FileUtils.readLineFromFile(file.toPath());
                        parserExcelService.parse(stringList, file.getName()).stream().forEach(System.out::println);;
                    default:
                }
            }
        }
    }
}
