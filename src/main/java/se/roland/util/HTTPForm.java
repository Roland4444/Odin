package se.roland.util;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class HTTPForm {
    public static String collectParams(String serie, String number){
        Map<String, String> input = new HashMap<>();
        input.put("serie", serie);
        input.put("number", number);
        String form = input.keySet().stream()
                .map(key -> key + "=" + URLEncoder.encode(input.get(key), StandardCharsets.UTF_8))
                .collect(Collectors.joining("&"));
        return form;
    }
}
