package com.j4me.orderparser.services;

import com.j4me.orderparser.model.OrderDTO;

import java.util.List;
import java.util.concurrent.*;

/**
 * Интерфейс, для реализации парсинга различного типа входных данных
 */

public interface ParserService {
    static ConcurrentLinkedQueue inQueque = new ConcurrentLinkedQueue<String>();

    public List<String> parse(List<String> order, String fileName);

}
