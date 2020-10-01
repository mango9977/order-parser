package com.j4me.orderparser.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.j4me.orderparser.model.OrderDTO;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Exchanger;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Реализация интерфейса для парсинга JSON потока
 */

@Service
@Qualifier("fromCSV")
public class ParserServiceFromCSV implements ParserService {

    //CSV file header
    private static final String[] FILE_HEADER_MAPPING = {"orderId", "amount", "currency", "comment"};


    private static final String ORDER_ID = "orderId";
    private static final String ORDER_AMOUNT = "amount";
    private static final String ORDER_CURRENCY = "currency";
    private static final String ORDER_COMMENT = "comment";
    private static CSVParser csvStringParser;

    AtomicBoolean interrupt = new AtomicBoolean(false);
    AtomicInteger lineCount;
    Exchanger<OrderDTO> exchanger = new Exchanger<>();
    List<String> dtoStore = new ArrayList<>();
    static final String dlm = "\"";

    @Override
    public List<String> parse(List<String> order, String fileName) {

        CSVFormat csvHeaderFormat = CSVFormat.DEFAULT.withHeader(FILE_HEADER_MAPPING);
        inQueque.addAll(order);
        lineCount = new AtomicInteger(1);
        final String dlm = "\"";

        final Runnable checkAndTransform = () -> {
            while (!inQueque.isEmpty()) {
                ObjectMapper mapper = new ObjectMapper();
                OrderDTO orderDTO = new OrderDTO();
                Integer cnt = lineCount.getAndIncrement();
                String record = (String) inQueque.poll();
                StringReader sReader = new StringReader(record);
                List<CSVRecord> csvRecords = null;
                try {
                    csvStringParser = new CSVParser(sReader, csvHeaderFormat);
                    csvRecords = csvStringParser.getRecords();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                try {
                    for (CSVRecord csvR : csvRecords) {
                        orderDTO.setId(Long.valueOf(csvR.get(ORDER_ID)));
                        orderDTO.setAmount(Integer.valueOf(csvR.get(ORDER_AMOUNT)));
                        orderDTO.setCurrency(csvR.get(ORDER_CURRENCY));
                        orderDTO.setComment(csvR.get(ORDER_COMMENT));
                    }
                    orderDTO.setLine(cnt);
                    orderDTO.setResult("ОК");
                    orderDTO.setFilename(fileName);
                    exchanger.exchange(orderDTO);
                } catch (IllegalArgumentException | InterruptedException e) {
                    StringBuilder sb = new StringBuilder("{");
                    sb.append(fillBlockResult(orderDTO));
                    sb.append(",").append(dlm + "filename" + dlm + ":").append(dlm + fileName + dlm)
                            .append("," + dlm + "line" + dlm + ":").append(cnt)
                            .append("," + dlm + "result" + dlm + ":" + dlm + e.getMessage() + dlm + "}");
                    dtoStore.add(sb.toString());
                }
            }
            interrupt.set(true);
        };

        final Runnable transformToJson = () -> {

            ObjectMapper mapper = new ObjectMapper();
            while (!interrupt.get()) {
                OrderDTO dto = new OrderDTO();
                try {
                    dto = exchanger.exchange(dto);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                String jsonOut;
                try {
                    jsonOut = mapper.writeValueAsString(dto);
                    dtoStore.add(jsonOut);
                } catch (JsonProcessingException e) {
                    System.out.println(e.getMessage());
                }
            }
        };

        Thread t1 = new Thread(checkAndTransform);
        Thread t2 = new Thread(transformToJson);
        t1.start();
        t2.start();
        while (t1.getState() != Thread.State.TERMINATED || t2.getState() != Thread.State.TERMINATED) {

        }
        return dtoStore;
    }

    private String fillBlockResult(OrderDTO orderDTO) {
        StringBuilder sb = new StringBuilder();
        if (orderDTO.getId() != null) {
            sb.append(dlm + "orderId:" + dlm).append(orderDTO.getId()).append(",");
        } else sb.append(dlm + "orderId:" + dlm).append("null,");

        if (orderDTO.getAmount() != null) {
            sb.append(dlm + "amount:" + dlm).append(orderDTO.getAmount()).append(",");
        } else sb.append(dlm + "amount:" + dlm).append("null,");

        if (orderDTO.getCurrency() != null) {
            sb.append(dlm + "currency:" + dlm).append(orderDTO.getCurrency()).append(",");
        } else sb.append(dlm + "currency:" + dlm).append("null,");

        if (orderDTO.getComment() != null) {
            sb.append(dlm + "comment:" + dlm).append(orderDTO.getComment()).append(",");
        } else sb.append(dlm + "comment:" + dlm).append("null,");

        return sb.toString();
    }
}
