package com.j4me.orderparser.services;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.Future;

@Service
@Qualifier("fromEXCEL")
public class ParserServiceFromExcel implements ParserService{

    @Override
    public List<String> parse(List<String> order, String s) {
        return null;
    }
}
