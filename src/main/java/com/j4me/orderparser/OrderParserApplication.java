package com.j4me.orderparser;

import com.j4me.orderparser.services.ParsersFactory;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Component;

@ComponentScan
@Component
public class OrderParserApplication {

    public static void main(String[] args) {
        AnnotationConfigApplicationContext ctx =
                new AnnotationConfigApplicationContext(OrderParserApplication.class);

        ParsersFactory parsersFactory = ctx.getBean(ParsersFactory.class);
        if (args.length > 0) {
            parsersFactory.startParse(args);
        }

    }

}
