package se.roland.transport;




import javax.xml.soap.*;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.*;

public class SAAJ extends Transport implements Serializable {
    private String url;

    public SAAJ(String url){
        this.url = url;
    }

    private byte[] createSoapResponse(SOAPMessage soapResponse, boolean SupressConsole) throws Exception  {
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        Source sourceContent = soapResponse.getSOAPPart().getContent();
        if (!SupressConsole)
            System.out.println("\n----------SOAP Response-------createSoapResponse(SOAPMessage soapResponse, boolean SupressConsole)----");
        byte[] res=null;
        OutputStream out = new ByteArrayOutputStream();
        StreamResult result = new StreamResult(out        );
        transformer.transform(sourceContent, result);
        StreamResult console = new StreamResult(System.out);
        if (!SupressConsole)
            transformer.transform(sourceContent, console);
        return ((ByteArrayOutputStream) out).toByteArray();
    }

    private void createSoapResponse(SOAPMessage soapResponse, String results) throws Exception  {
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        Source sourceContent = soapResponse.getSOAPPart().getContent();
        System.out.println("\n----------SOAP Response-----------");
        StreamResult result = new StreamResult(new FileOutputStream(results));
        transformer.transform(sourceContent, result);
        StreamResult console = new StreamResult(System.out);
        transformer.transform(sourceContent, console);
    }

    private StreamResult createSoapResponse(SOAPMessage soapResponse, StreamResult result) throws Exception  {
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        Source sourceContent = soapResponse.getSOAPPart().getContent();
        System.out.println("\n----------SOAP Response-----------");
        transformer.transform(sourceContent, result);
        StreamResult console = new StreamResult(System.out);
        transformer.transform(sourceContent, console);
        return result;
    }

    @Override
    public String send(String filename, String result) throws Exception {
        SOAPConnectionFactory soapConnFactory = SOAPConnectionFactory.newInstance();
        SOAPConnection connection = soapConnFactory.createConnection();
        MessageFactory messageFactory = MessageFactory.newInstance();
        SOAPMessage message = messageFactory.createMessage();
        SOAPPart soapPart =     message.getSOAPPart();
        StreamSource preppedMsgSrc = new StreamSource(
                new FileInputStream(filename));
        soapPart.setContent(preppedMsgSrc);

        message.saveChanges();
        System.out.println("\nREQUEST:\n");
        message.writeTo(System.out);
        System.out.println();
        SOAPMessage reply = connection.call(message, this.url);
        createSoapResponse(reply, result);
        connection.close();
        return reply.getSOAPBody().toString();
    }

    public String send(StreamSource preppedMsgSrc, StreamResult result) throws Exception {
        SOAPConnectionFactory soapConnFactory = SOAPConnectionFactory.newInstance();
        SOAPConnection connection = soapConnFactory.createConnection();
        MessageFactory messageFactory = MessageFactory.newInstance();
        SOAPMessage message = messageFactory.createMessage();
        SOAPPart soapPart =     message.getSOAPPart();
        soapPart.setContent(preppedMsgSrc);
        message.saveChanges();
    //    System.out.println("\nREQUEST:\n");
    //    message.writeTo(System.out);
        System.out.println();
        SOAPMessage reply = connection.call(message, this.url);
        createSoapResponse(reply, result);
        connection.close();
        return reply.getSOAPBody().toString();
    }

    @Override
    public byte[] send(StreamSource preppedMsgSrc, boolean SupressConsole) throws Exception {
        SOAPConnectionFactory soapConnFactory = SOAPConnectionFactory.newInstance();
        SOAPConnection connection = soapConnFactory.createConnection();
        MessageFactory messageFactory = MessageFactory.newInstance();
        SOAPMessage message = messageFactory.createMessage();
        SOAPPart soapPart =     message.getSOAPPart();
        soapPart.setContent(preppedMsgSrc);
        message.saveChanges();
        if (!SupressConsole){
            System.out.println("\nREQUEST:\n");
            message.writeTo(System.out);
            System.out.println();
        }

        SOAPMessage reply = connection.call(message, this.url);
        return createSoapResponse(reply, SupressConsole);

    }
}
