package xml;


import org.junit.Test;
import se.roland.abstractions.timeBasedUUID;
import se.roland.xml.Extractor;
import se.roland.xml.Injector;


import java.io.FileWriter;
import java.io.IOException;

import static org.junit.Assert.*;

public class InjectorTest {

    @Test
    public void getTagValuetest() {
        String xml = "<S:Envelope xmlns:S=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:ns2=\"urn://x-artefacts-smev-gov-ru/services/message-exchange/types/1.1\"><S:Body><ns2:SendRequestRequest><ns:SenderProvidedRequestData xmlns:ns=\"urn://x-artefacts-smev-gov-ru/services/message-exchange/types/1.1\" xmlns:ns2=\"urn://x-artefacts-smev-gov-ru/services/message-exchange/types/basic/1.1\" Id=\"SIGNED_BY_CONSUMER\"><ns:MessageID>b3b2a8e2-55c5-4423-a570-86e4d1e87ccd</ns:MessageID><ns2:MessagePrimaryContent><ns1:FNSVipULRequest xmlns:ns1=\"urn://x-artefacts-fns-vipul-tosmv-ru/311-14/4.0.6\" ИдДок=\"C289035F-43EB-41BF-899F-E7CFB97FC08B\" НомерДела=\"56C6FAA6-A314-466E-96C9-07ADA1689F1F\"><ns1:ЗапросЮЛ><ns1:ИННЮЛ>1234567890</ns1:ИННЮЛ></ns1:ЗапросЮЛ></ns1:FNSVipULRequest></ns2:MessagePrimaryContent><ns:TestMessage/></ns:SenderProvidedRequestData><ns4:CallerInformationSystemSignature xmlns:ns4=\"urn://x-artefacts-smev-gov-ru/services/message-exchange/types/1.1\" xmlns:ds=\"http://www.w3.org/2000/09/xmldsig#\"><ds:Signature><ds:SignedInfo><ds:CanonicalizationMethod Algorithm=\"http://www.w3.org/2001/10/xml-exc-c14n#\"/><ds:SignatureMethod Algorithm=\"http://www.w3.org/2001/04/xmldsig-more#gostr34102001-gostr3411\"/><ds:Reference URI=\"#SIGNED_BY_CONSUMER\"><ds:Transforms><ds:Transform xmlns:ds=\"http://www.w3.org/2000/09/xmldsig#\" Algorithm=\"http://www.w3.org/2001/10/xml-exc-c14n#\"/><ds:Transform xmlns:ds=\"http://www.w3.org/2000/09/xmldsig#\" Algorithm=\"urn://smev-gov-ru/xmldsig/transform\"/></ds:Transforms><ds:DigestMethod Algorithm=\"http://www.w3.org/2001/04/xmldsig-more#gostr3411\"/><ds:DigestValue>000</ds:DigestValue></ds:Reference></ds:SignedInfo><ds:SignatureValue>000</ds:SignatureValue><ds:KeyInfo><ds:X509Data><ds:X509Certificate>000</ds:X509Certificate></ds:X509Data></ds:KeyInfo></ds:Signature></ns4:CallerInformationSystemSignature></ns2:SendRequestRequest></S:Body></S:Envelope>";
        Injector parcer = new Injector();
        assertEquals("b3b2a8e2-55c5-4423-a570-86e4d1e87ccd", parcer.getTagValue(xml, "ns:MessageID"));
    }

    @Test
    public void inject() throws IOException {
        String xml = "<S:Envelope xmlns:S=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:ns2=\"urn://x-artefacts-smev-gov-ru/services/message-exchange/types/1.1\"><S:Body><ns2:SendRequestRequest><ns:SenderProvidedRequestData xmlns:ns=\"urn://x-artefacts-smev-gov-ru/services/message-exchange/types/1.1\" xmlns:ns2=\"urn://x-artefacts-smev-gov-ru/services/message-exchange/types/basic/1.1\" Id=\"SIGNED_BY_CONSUMER\"><ns:MessageID>b3b2a8e2-55c5-4423-a570-86e4d1e87ccd</ns:MessageID><ns2:MessagePrimaryContent><ns1:FNSVipULRequest xmlns:ns1=\"urn://x-artefacts-fns-vipul-tosmv-ru/311-14/4.0.6\" ИдДок=\"C289035F-43EB-41BF-899F-E7CFB97FC08B\" НомерДела=\"56C6FAA6-A314-466E-96C9-07ADA1689F1F\"><ns1:ЗапросЮЛ><ns1:ИННЮЛ>1234567890</ns1:ИННЮЛ></ns1:ЗапросЮЛ></ns1:FNSVipULRequest></ns2:MessagePrimaryContent><ns:TestMessage/></ns:SenderProvidedRequestData><ns4:CallerInformationSystemSignature xmlns:ns4=\"urn://x-artefacts-smev-gov-ru/services/message-exchange/types/1.1\" xmlns:ds=\"http://www.w3.org/2000/09/xmldsig#\"><ds:Signature><ds:SignedInfo><ds:CanonicalizationMethod Algorithm=\"http://www.w3.org/2001/10/xml-exc-c14n#\"/><ds:SignatureMethod Algorithm=\"http://www.w3.org/2001/04/xmldsig-more#gostr34102001-gostr3411\"/><ds:Reference URI=\"#SIGNED_BY_CONSUMER\"><ds:Transforms><ds:Transform xmlns:ds=\"http://www.w3.org/2000/09/xmldsig#\" Algorithm=\"http://www.w3.org/2001/10/xml-exc-c14n#\"/><ds:Transform xmlns:ds=\"http://www.w3.org/2000/09/xmldsig#\" Algorithm=\"urn://smev-gov-ru/xmldsig/transform\"/></ds:Transforms><ds:DigestMethod Algorithm=\"http://www.w3.org/2001/04/xmldsig-more#gostr3411\"/><ds:DigestValue>000</ds:DigestValue></ds:Reference></ds:SignedInfo><ds:SignatureValue>000</ds:SignatureValue><ds:KeyInfo><ds:X509Data><ds:X509Certificate>000</ds:X509Certificate></ds:X509Data></ds:KeyInfo></ds:Signature></ns4:CallerInformationSystemSignature></ns2:SendRequestRequest></S:Body></S:Envelope>";
        Injector parcer = new Injector();
        FileWriter wr = new FileWriter("xml4test/out.xml");
        wr.write(parcer.inject(xml));
        wr.close();
    }

    @Test
    public void injectandExtractTest() throws IOException {
        Injector parcer = new Injector();
        parcer.inject("xml4test/removedMessage.xml","xml4test/check.xml");
        Extractor ext = new Extractor();

      //  assertEquals(result, ext.parse("xml4test/check.xml", "MessageID"));
        FileWriter wr = new FileWriter("xml4test/reqdataonly.xml");
        wr.write(ext.parse("xml4test/1.xml", "SenderProvidedRequestData"));
    }

    @Test
    public void inject1() {
    }

    @Test
    public void injectTag() {
        String xml = "<inj></inj>";
        Injector parcer = new Injector();
        assertEquals("<inj>2</inj>", parcer.injectTag(xml,"inj>","2"));
    }

    @Test
    public void injectTagInFile() throws IOException {
        Injector parcer = new Injector();
        String dataInject = timeBasedUUID.generate();
        System.out.println(dataInject);
        parcer.injectTagInFile("xml4test/removedMessage.xml","xml4test/check.xml",":MessageID>", dataInject);
        Extractor ext = new Extractor();
        String result=ext.parse("xml4test/check.xml", "MessageID");
        assertTrue(result.indexOf(dataInject)>0);
        assertEquals(dataInject, ext.extractRaw("xml4test/check.xml", "MessageID"));
        System.out.println(ext.extractRaw("xml4test/check.xml", "MessageID"));
    }

    @Test
    public void injectTagInFile4() throws IOException {
        Injector parcer = new Injector();

        String dataInject = timeBasedUUID.generate();
        System.out.println(dataInject);
        parcer.injectTagInFile("xml4test/removedMessage.xml","xml4test/check.xml",":MessageID>", dataInject);
        Extractor ext = new Extractor();
        String result=ext.parse("xml4test/check.xml", "MessageID");
        assertTrue(result.indexOf(dataInject)>0);
        assertEquals(dataInject, ext.extractRaw("xml4test/check.xml", "MessageID"));
        System.out.println(ext.extractRaw("xml4test/check.xml", "MessageID"));
    }





    @Test
    public void burnTagWithData() throws IOException {
        Injector inj = new Injector();
        String data =  "<x1><x2><bull>5444545454</bull></x2></x1>";
        String Etalon =  "<x1></x1>";
        assertEquals(Etalon, inj.burnTagWithData(data, "x2"));
    }


    @Test
    public void burnTagWithDataextra() throws IOException {
        Injector inj = new Injector();
        String data=  "<S:Envelope xmlns:S=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:ns2=\"urn://x-artefacts-smev-gov-ru/services/message-exchange/types/1.1\">\n" +
                "  <S:Body>\n" +
                "    <ns2:SendRequestRequest>\n" +
                "      <ns:SenderProvidedRequestData xmlns:ns=\"urn://x-artefacts-smev-gov-ru/services/message-exchange/types/1.1\" xmlns:ns2=\"urn://x-artefacts-smev-gov-ru/services/message-exchange/types/basic/1.1\" Id=\"SIGNED_BY_CONSUMER\">\n" +
                "        <ns:MessageID>8efe4ff4-b5df-4e6b-ab20-fd14b8e071da</ns:MessageID>\n" +
                "        <ns2:MessagePrimaryContent>\n" +
                "          <ns1:FNSVipULRequest xmlns:ns1=\"urn://x-artefacts-fns-vipul-tosmv-ru/311-14/4.0.5\" ИдДок=\"F69BB651-80C9-4007-B3FB-09A20BBE2DA5\" НомерДела=\"F05367CD-EDC6-4E37-A7EE-A422F067F6B5\">\n" +
                "            <ns1:ЗапросЮЛ>\n" +
                "              <ns1:ОГРН>5087746429843</ns1:ОГРН>\n" +
                "            </ns1:ЗапросЮЛ>\n" +
                "          </ns1:FNSVipULRequest>\n" +
                "        </ns2:MessagePrimaryContent>\n" +
                "        <ns:TestMessage />\n" +
                "      </ns:SenderProvidedRequestData>\n" +
                "      <ns4:CallerInformationSystemSignature xmlns:ns4=\"urn://x-artefacts-smev-gov-ru/services/message-exchange/types/1.1\" xmlns:ds=\"http://www.w3.org/2000/09/xmldsig#\">\n" +
                "        <ds:Signature>\n" +
                "          <ds:SignedInfo>\n" +
                "            <ds:CanonicalizationMethod Algorithm=\"http://www.w3.org/2001/10/xml-exc-c14n#\" />\n" +
                "            <ds:SignatureMethod Algorithm=\"http://www.w3.org/2001/04/xmldsig-more#gostr34102001-gostr3411\" />\n" +
                "            <ds:Reference URI=\"#SIGNED_BY_CONSUMER\">\n" +
                "              <ds:Transforms>\n" +
                "                <ds:Transform xmlns:ds=\"http://www.w3.org/2000/09/xmldsig#\" Algorithm=\"http://www.w3.org/2001/10/xml-exc-c14n#\" />\n" +
                "                <ds:Transform xmlns:ds=\"http://www.w3.org/2000/09/xmldsig#\" Algorithm=\"urn://smev-gov-ru/xmldsig/transform\" />\n" +
                "              </ds:Transforms>\n" +
                "              <ds:DigestMethod Algorithm=\"http://www.w3.org/2001/04/xmldsig-more#gostr3411\" />\n" +
                "              <ds:DigestValue>000</ds:DigestValue>\n" +
                "            </ds:Reference>\n" +
                "          </ds:SignedInfo>\n" +
                "          <ds:SignatureValue>000</ds:SignatureValue>\n" +
                "          <ds:KeyInfo>\n" +
                "            <ds:X509Data>\n" +
                "              <ds:X509Certificate>000</ds:X509Certificate>\n" +
                "            </ds:X509Data>\n" +
                "          </ds:KeyInfo>\n" +
                "        </ds:Signature>\n" +
                "      </ns4:CallerInformationSystemSignature>\n" +
                "    </ns2:SendRequestRequest>\n" +
                "  </S:Body>\n" +
                "</S:Envelope>";
        String Etalon  =  "<S:Envelope xmlns:S=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:ns2=\"urn://x-artefacts-smev-gov-ru/services/message-exchange/types/1.1\">\n" +
                "  <S:Body>\n" +
                "    <ns2:SendRequestRequest>\n" +
                "      <ns:SenderProvidedRequestData xmlns:ns=\"urn://x-artefacts-smev-gov-ru/services/message-exchange/types/1.1\" xmlns:ns2=\"urn://x-artefacts-smev-gov-ru/services/message-exchange/types/basic/1.1\" Id=\"SIGNED_BY_CONSUMER\">\n" +
                "        <ns:MessageID>8efe4ff4-b5df-4e6b-ab20-fd14b8e071da</ns:MessageID>\n" +
                "        <ns2:MessagePrimaryContent>\n" +
                "          <ns1:FNSVipULRequest xmlns:ns1=\"urn://x-artefacts-fns-vipul-tosmv-ru/311-14/4.0.5\" ИдДок=\"F69BB651-80C9-4007-B3FB-09A20BBE2DA5\" НомерДела=\"F05367CD-EDC6-4E37-A7EE-A422F067F6B5\">\n" +
                "            <ns1:ЗапросЮЛ>\n" +
                "              <ns1:ОГРН>5087746429843</ns1:ОГРН>\n" +
                "            </ns1:ЗапросЮЛ>\n" +
                "          </ns1:FNSVipULRequest>\n" +
                "        </ns2:MessagePrimaryContent>\n" +
                "        <ns:TestMessage />\n" +
                "      </ns:SenderProvidedRequestData>\n" +
                "      \n" +
                "    </ns2:SendRequestRequest>\n" +
                "  </S:Body>\n" +
                "</S:Envelope>";
        assertEquals(Etalon, inj.burnTagWithData(data, "ns4:CallerInformationSystemSignature"));

        System.out.println(inj.burnTagWithData(data, "ds:Signature"));
        System.out.println();
        System.out.println();
        System.out.println();
        System.out.println(Etalon);
    }


    @Test
    public void flushTagData() throws IOException {
        Injector inj = new Injector();
        String data=  "<S:Envelope xmlns:S=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:ns2=\"urn://x-artefacts-smev-gov-ru/services/message-exchange/types/1.1\">\n" +
                "  <S:Body>\n" +
                "    <ns2:SendRequestRequest>\n" +
                "      <ns:SenderProvidedRequestData xmlns:ns=\"urn://x-artefacts-smev-gov-ru/services/message-exchange/types/1.1\" xmlns:ns2=\"urn://x-artefacts-smev-gov-ru/services/message-exchange/types/basic/1.1\" Id=\"SIGNED_BY_CONSUMER\">\n" +
                "        <ns:MessageID>8efe4ff4-b5df-4e6b-ab20-fd14b8e071da</ns:MessageID>\n" +
                "        <ns2:MessagePrimaryContent>\n" +
                "          <ns1:FNSVipULRequest xmlns:ns1=\"urn://x-artefacts-fns-vipul-tosmv-ru/311-14/4.0.5\" ИдДок=\"F69BB651-80C9-4007-B3FB-09A20BBE2DA5\" НомерДела=\"F05367CD-EDC6-4E37-A7EE-A422F067F6B5\">\n" +
                "            <ns1:ЗапросЮЛ>\n" +
                "              <ns1:ОГРН>5087746429843</ns1:ОГРН>\n" +
                "            </ns1:ЗапросЮЛ>\n" +
                "          </ns1:FNSVipULRequest>\n" +
                "        </ns2:MessagePrimaryContent>\n" +
                "        <ns:TestMessage />\n" +
                "      </ns:SenderProvidedRequestData>\n" +
                "      <ns4:CallerInformationSystemSignature xmlns:ns4=\"urn://x-artefacts-smev-gov-ru/services/message-exchange/types/1.1\" xmlns:ds=\"http://www.w3.org/2000/09/xmldsig#\">\n" +
                "        <ds:Signature>\n" +
                "          <ds:SignedInfo>\n" +
                "            <ds:CanonicalizationMethod Algorithm=\"http://www.w3.org/2001/10/xml-exc-c14n#\" />\n" +
                "            <ds:SignatureMethod Algorithm=\"http://www.w3.org/2001/04/xmldsig-more#gostr34102001-gostr3411\" />\n" +
                "            <ds:Reference URI=\"#SIGNED_BY_CONSUMER\">\n" +
                "              <ds:Transforms>\n" +
                "                <ds:Transform xmlns:ds=\"http://www.w3.org/2000/09/xmldsig#\" Algorithm=\"http://www.w3.org/2001/10/xml-exc-c14n#\" />\n" +
                "                <ds:Transform xmlns:ds=\"http://www.w3.org/2000/09/xmldsig#\" Algorithm=\"urn://smev-gov-ru/xmldsig/transform\" />\n" +
                "              </ds:Transforms>\n" +
                "              <ds:DigestMethod Algorithm=\"http://www.w3.org/2001/04/xmldsig-more#gostr3411\" />\n" +
                "              <ds:DigestValue>000</ds:DigestValue>\n" +
                "            </ds:Reference>\n" +
                "          </ds:SignedInfo>\n" +
                "          <ds:SignatureValue>000</ds:SignatureValue>\n" +
                "          <ds:KeyInfo>\n" +
                "            <ds:X509Data>\n" +
                "              <ds:X509Certificate>000</ds:X509Certificate>\n" +
                "            </ds:X509Data>\n" +
                "          </ds:KeyInfo>\n" +
                "        </ds:Signature>\n" +
                "      </ns4:CallerInformationSystemSignature>\n" +
                "    </ns2:SendRequestRequest>\n" +
                "  </S:Body>\n" +
                "</S:Envelope>";
        String Etalon  =  "<S:Envelope xmlns:S=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:ns2=\"urn://x-artefacts-smev-gov-ru/services/message-exchange/types/1.1\">\n" +
                "  <S:Body>\n" +
                "    <ns2:SendRequestRequest>\n" +
                "      <ns:SenderProvidedRequestData xmlns:ns=\"urn://x-artefacts-smev-gov-ru/services/message-exchange/types/1.1\" xmlns:ns2=\"urn://x-artefacts-smev-gov-ru/services/message-exchange/types/basic/1.1\" Id=\"SIGNED_BY_CONSUMER\">\n" +
                "        <ns:MessageID>8efe4ff4-b5df-4e6b-ab20-fd14b8e071da</ns:MessageID>\n" +
                "        <ns2:MessagePrimaryContent>\n" +
                "          <ns1:FNSVipULRequest xmlns:ns1=\"urn://x-artefacts-fns-vipul-tosmv-ru/311-14/4.0.5\" ИдДок=\"F69BB651-80C9-4007-B3FB-09A20BBE2DA5\" НомерДела=\"F05367CD-EDC6-4E37-A7EE-A422F067F6B5\">\n" +
                "            <ns1:ЗапросЮЛ>\n" +
                "              <ns1:ОГРН>5087746429843</ns1:ОГРН>\n" +
                "            </ns1:ЗапросЮЛ>\n" +
                "          </ns1:FNSVipULRequest>\n" +
                "        </ns2:MessagePrimaryContent>\n" +
                "        <ns:TestMessage />\n" +
                "      </ns:SenderProvidedRequestData>\n" +
                "      <ns4:CallerInformationSystemSignature xmlns:ns4=\"urn://x-artefacts-smev-gov-ru/services/message-exchange/types/1.1\" xmlns:ds=\"http://www.w3.org/2000/09/xmldsig#\">" +
                "</ns4:CallerInformationSystemSignature>\n" +
                "    </ns2:SendRequestRequest>\n" +
                "  </S:Body>\n" +
                "</S:Envelope>";
        assertEquals(Etalon, inj.flushTagData(data, "ns4:CallerInformationSystemSignature"));

        System.out.println(inj.burnTagWithData(data, "ds:Signature"));
        System.out.println();
        System.out.println();
        System.out.println();
        System.out.println(Etalon);
    }

    @Test
    public void flushTagWithData() throws IOException {
        Injector inj = new Injector();
        String data =  "<x1><x2><bull>5444545454</bull></x2></x1>";
        String Etalon =  "<x1><x2></x2></x1>";
        assertEquals(Etalon, inj.flushTagData(data, "x2"));
    }

    @Test
    public void flushTagWithDataExtended() throws IOException {
        Injector inj = new Injector();
        String data =  "<x1><ns:x2 ns=\"fuck\"><bull>5444545454</bull>\n</ns:x2></x1>";
        String Etalon =  "<x1><ns:x2 ns=\"fuck\"></ns:x2></x1>";
        assertEquals(Etalon, inj.flushTagData(data, "x2"));
    }

    @Test
    public void extendedtest() throws IOException {
        Injector inj = new Injector();
        String data =       "<S:Envelope xmlns:S=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:ns4=\"urn://x-artefacts-smev-gov-ru/services/message-exchange/types/1.1\" xmlns:ns2=\"urn://x-artefacts-smev-gov-ru/services/message-exchange/types/1.1\">  <S:Body>    <ns2:SendRequestRequest>      <ns:SenderProvidedRequestData xmlns:ds=\"http://www.w3.org/2000/09/xmldsig#\" xmlns:ns=\"urn://x-artefacts-smev-gov-ru/services/message-exchange/types/1.1\" xmlns:ns2=\"urn://x-artefacts-smev-gov-ru/services/message-exchange/types/basic/1.1\" Id=\"SIGNED_BY_CONSUMER\">        <ns:MessageID>zGz</ns:MessageID>        <ns2:MessagePrimaryContent>          <ns0:ExportChargesRequest xmlns:sc=\"http://roskazna.ru/gisgmp/xsd/SearchConditions/2.0.1\" xmlns:com=\"http://roskazna.ru/gisgmp/xsd/Common/2.0.1\" xmlns:ns0=\"urn://roskazna.ru/gisgmp/xsd/services/export-charges/2.0.1\" senderIdentifier=\"3637c9\" senderRole=\"7\" timestamp=\"2018-10-16T09:53:54.8436798+04:00\" Id=\"PERSONAL_SIGNATURE\">            <com:Paging pageNumber=\"1\" pageLength=\"99\" />            <sc:ChargesExportConditions kind=\"CHARGE\">              <sc:ChargesConditions xmlns:sc=\"http://roskazna.ru/gisgmp/xsd/SearchConditions/2.0.1\">                <sc:SupplierBillID>18818101111512037343</sc:SupplierBillID>              </sc:ChargesConditions>            </sc:ChargesExportConditions>          </ns0:ExportChargesRequest>        </ns2:MessagePrimaryContent>        <ns:PersonalSignature />      </ns:SenderProvidedRequestData>      <ns4:CallerInformationSystemSignature xmlns:ns4=\"urn://x-artefacts-smev-gov-ru/services/message-exchange/types/1.1\" xmlns:ds=\"http://www.w3.org/2000/09/xmldsig#\">1212221212</ns4:CallerInformationSystemSignature>    </ns2:SendRequestRequest>  </S:Body></S:Envelope>";
        String Etalon =     "<S:Envelope xmlns:S=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:ns4=\"urn://x-artefacts-smev-gov-ru/services/message-exchange/types/1.1\" xmlns:ns2=\"urn://x-artefacts-smev-gov-ru/services/message-exchange/types/1.1\">  <S:Body>    <ns2:SendRequestRequest>      <ns:SenderProvidedRequestData xmlns:ds=\"http://www.w3.org/2000/09/xmldsig#\" xmlns:ns=\"urn://x-artefacts-smev-gov-ru/services/message-exchange/types/1.1\" xmlns:ns2=\"urn://x-artefacts-smev-gov-ru/services/message-exchange/types/basic/1.1\" Id=\"SIGNED_BY_CONSUMER\">        <ns:MessageID>zGz</ns:MessageID>        <ns2:MessagePrimaryContent>          <ns0:ExportChargesRequest xmlns:sc=\"http://roskazna.ru/gisgmp/xsd/SearchConditions/2.0.1\" xmlns:com=\"http://roskazna.ru/gisgmp/xsd/Common/2.0.1\" xmlns:ns0=\"urn://roskazna.ru/gisgmp/xsd/services/export-charges/2.0.1\" senderIdentifier=\"3637c9\" senderRole=\"7\" timestamp=\"2018-10-16T09:53:54.8436798+04:00\" Id=\"PERSONAL_SIGNATURE\">            <com:Paging pageNumber=\"1\" pageLength=\"99\" />            <sc:ChargesExportConditions kind=\"CHARGE\">              <sc:ChargesConditions xmlns:sc=\"http://roskazna.ru/gisgmp/xsd/SearchConditions/2.0.1\">                <sc:SupplierBillID>18818101111512037343</sc:SupplierBillID>              </sc:ChargesConditions>            </sc:ChargesExportConditions>          </ns0:ExportChargesRequest>        </ns2:MessagePrimaryContent>        <ns:PersonalSignature />      </ns:SenderProvidedRequestData>      <ns4:CallerInformationSystemSignature xmlns:ns4=\"urn://x-artefacts-smev-gov-ru/services/message-exchange/types/1.1\" xmlns:ds=\"http://www.w3.org/2000/09/xmldsig#\"></ns4:CallerInformationSystemSignature>    </ns2:SendRequestRequest>  </S:Body></S:Envelope>";
        assertEquals(Etalon, inj.flushTagData(data, "CallerInformationSystemSignature"));
    }


    @Test
    public void injectAttribute() {
        Injector inj = new Injector();
        String data =  "<x1><x2 id=\"\"><bull>5444545454</bull></x2></x1>";
        String Etalon =  "<x1><x2 id=\"12\"><bull>5444545454</bull></x2></x1>";
        assertEquals(Etalon, inj.injectAttribute(data, "id", "12"));
    }



    @Test
    public void injectAttribute2() {
        Injector inj = new Injector();
        String data =       "<bull if=\"122\">5444545454</bull>";
        String Etalon =     "<bull if=\"155\">5444545454</bull>";
        assertEquals(Etalon, inj.injectAttribute(data, "if", "155"));
    }

    @Test
    public void injectTag1() {
        Injector inj = new Injector();
        String input = "<bs  xm=\"12\">12</bs>";
        String etalon = "<bs  xm=\"12\">16</bs>";
        assertEquals(etalon, inj.injectTagDirect(input, "bs", "16"));

    }





    @Test
    public void generateTimeStamp2() throws InterruptedException {
        Injector inj = new Injector();
        for (int i=0; i<100; i++){
            assertEquals(33, inj.generateTimeStamp().length());
            System.out.println(inj.generateTimeStamp());
        }
    }


}