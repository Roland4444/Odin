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
    public static Map<String, String> MapParams(@NotNull HashMap<String, String> params) {
        var map2 = params.entrySet().stream()
                .filter(a-> a.getKey().toString().length()>0)
                .collect(Collectors.toMap(
                        e -> e.getKey().toString().replace("::","").replace(" ", "_"),
                        e -> e.getValue()
                ));
        return map2;

    }
}
