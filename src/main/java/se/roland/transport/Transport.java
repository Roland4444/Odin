package se.roland.transport;
import javax.xml.transform.stream.StreamSource;

public abstract class  Transport {
    public abstract byte[] send(StreamSource preppedMsgSrc, boolean SupressConsole) throws Exception;
    public abstract String send(String filename, String result) throws Exception;
}
