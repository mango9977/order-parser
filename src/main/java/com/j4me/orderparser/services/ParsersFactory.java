package com.j4me.orderparser.services;

/**
 * Интерфейс для реализаций методов старта обработки входящих потоков
 */
public interface ParsersFactory {
    public  void startParse(String[] args);
}
