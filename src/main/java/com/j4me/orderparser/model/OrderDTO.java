package com.j4me.orderparser.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class OrderDTO {
    @JsonProperty("orderId")
    Long id;
    @JsonProperty("amount")
    Integer amount;
    @JsonProperty("currency")
    String currency;
    @JsonProperty("comment")
    String comment;
    @JsonProperty("filename")
    String filename;
    @JsonProperty("line")
    Integer line;
    @JsonProperty("result")
    String result;



}
