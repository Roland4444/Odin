package util

import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.time.Duration

class SMSNotify {
companion object {
    @JvmStatic
    fun sendSMS(login: String, pass: String, number: String, message: String ): String {
        val req = "https://smsc.ru/sys/send.php?login=$login&psw=$pass&phones=$number&mes=${message.replace(" ", "%20")}"
        val request = HttpRequest.newBuilder()
            .uri(URI.create(req))
            .timeout(Duration.ofMinutes(2))
            .GET()
            .build()
        val client = HttpClient.newBuilder()
            .version(HttpClient.Version.HTTP_1_1)
            .connectTimeout(Duration.ofSeconds(20))
            .build()
        val response = client.send(request, HttpResponse.BodyHandlers.ofByteArray())
        if (response.statusCode() == 200)
            return String(response.body())
        return "shit happens"
    }
}

}