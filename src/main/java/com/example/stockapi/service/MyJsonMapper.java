package com.example.stockapi.service;

import com.example.stockapi.model.Stock;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Service
public class MyJsonMapper {

    ObjectMapper objectMapper;

    @Autowired
    public MyJsonMapper() {
        this.objectMapper = new ObjectMapper();
    }

    public Stock getFromNode(JsonNode node) throws JsonProcessingException {
        return this.objectMapper.treeToValue(node, Stock.class);
    }

    public List<Stock> getAllFromNode(JsonNode node) throws JsonProcessingException {

        if (node.getNodeType() == JsonNodeType.ARRAY) {

            List<Stock> all = new ArrayList<>();
            Iterator<JsonNode> elements = node.elements();

            while (elements.hasNext())
                all.add(getFromNode(elements.next()));

            return all;
        } else
            throw new IllegalArgumentException("Node passed is not an JsonArray");
    }


}
