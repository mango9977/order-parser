package com.j4me.orderparser.services;

import com.fasterxml.jackson.core.JsonLocation;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.j4me.orderparser.model.OrderDTO;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Exchanger;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;


@Service
@Qualifier("fromJSON")
public class ParserServiceFromJson implements ParserService {

    AtomicBoolean interrupt = new AtomicBoolean(false);
    AtomicInteger lineCount;
    Exchanger<OrderDTO> exchanger = new Exchanger<>();
    List<String> dtoStore=new ArrayList<>();

    @Override
    public List<String> parse(List<String> order, final String fileName) {

        inQueque.addAll(order);
        final Integer size = inQueque.size();
        lineCount = new AtomicInteger(1);

        final Runnable checkAndTransform = () -> {
            while (!inQueque.isEmpty()) {

                ObjectMapper mapper = new ObjectMapper();
                OrderDTO orderDTO = new OrderDTO();
                Map<String, Object> orderDTO1;
                JsonLocation jsonLocation = null;
                Integer cnt = lineCount.getAndIncrement();
                final String str = (String) inQueque.poll();
                try {
                    orderDTO = mapper.readValue(str, OrderDTO.class);
                    orderDTO.setLine(cnt);
                    orderDTO.setResult("ОК");
                    orderDTO.setFilename(fileName);
                    exchanger.exchange(orderDTO);
                } catch (JsonProcessingException | InterruptedException e) {//JsonProcessingException
                    int indSymb = str.indexOf("}");
                    StringBuilder sb = new StringBuilder(str.substring(0, indSymb));
                    final String dlm = "\"";
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
            // if (!outQueue.isEmpty()) {
            while (!interrupt.get()) {
                OrderDTO dto = new OrderDTO();
                try {
                    //dto = (OrderDTO) outQueue.take();
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
}
