package se.roland.util;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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




    @Nullable
    public static String collectParams(@NotNull HashMap<String, String> params) {
        String form = params.keySet().stream()
                .map(key -> key + "=" + URLEncoder.encode(String.valueOf(params.get(key)), StandardCharsets.UTF_8))
                .collect(Collectors.joining("&"));
        System.out.println("FORM ENCODED::"+form);
        return form;
    }
}
