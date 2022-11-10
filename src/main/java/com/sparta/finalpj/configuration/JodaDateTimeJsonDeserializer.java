//package com.sparta.finalpj.configuration;
//
//import com.fasterxml.jackson.core.JsonParser;
//import com.fasterxml.jackson.core.JsonProcessingException;
//import com.fasterxml.jackson.databind.DeserializationContext;
//import com.fasterxml.jackson.databind.JsonDeserializer;
//import org.joda.time.DateTime;
//import org.joda.time.format.DateTimeFormat;
//import org.joda.time.format.DateTimeFormatter;
//import org.springframework.format.annotation.DateTimeFormat;
//
//import java.io.IOException;
//import java.time.format.DateTimeFormatter;
//
//import static com.fasterxml.jackson.databind.type.LogicalType.DateTime;
//
//public class JodaDateTimeJsonDeserializer extends JsonDeserializer<DateTime> {
//    @Override
//    public DateTime deserialize(JsonParser p, DeserializationContext ctxt
//    ) throws IOException, JsonProcessingException {
//        String dateString= p.readValueAs(String.class);
//        DateTimeFormatter dateTimeFormatter = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");
//        return DateTime.parse(dateString,dateTimeFormatter);//
//    }
//}