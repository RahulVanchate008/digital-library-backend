package com.webprogramming.project.config;

import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CSVtoJSONConverter {

    public static List<Map<String, String>> convertCSVtoJSON(String csvFilePath) {
        List<Map<String, String>> jsonList = new ArrayList<>();
        try {
            CsvMapper csvMapper = new CsvMapper();
            CsvSchema schema = CsvSchema.emptySchema().withHeader();
            File file = new File(csvFilePath);
            MappingIterator<Map<String, String>> mappingIterator = csvMapper.readerFor(Map.class).with(schema).readValues(file);
            while (mappingIterator.hasNext()) {
                jsonList.add(mappingIterator.next());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return jsonList;
    }
}
