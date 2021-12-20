package se.roland.crypto;

import junit.framework.TestCase;
import org.junit.Test;

import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;


public class Gost3411HashTest extends TestCase {

    @Test
    public void testgetBytesFromBase64() throws NoSuchAlgorithmException {
        String input = "<ns1:SenderProvidedRequestData xmlns:ns1=\"urn://x-artefacts-smev-gov-ru/services/message-exchange/types/1.1\" Id=\"SIGNED_BY_CONSUMER\"><ns1:MessageID>db0486d0-3c08-11e5-95e2-d4c9eff07b77</ns1:MessageID><ns2:MessagePrimaryContent xmlns:ns2=\"urn://x-artefacts-smev-gov-ru/services/message-exchange/types/basic/1.1\"><ns3:BreachRequest xmlns:ns3=\"urn://x-artefacts-gibdd-gov-ru/breach/root/1.0\" Id=\"PERSONAL_SIGNATURE\"><ns3:RequestedInformation><ns4:RegPointNum xmlns:ns4=\"urn://x-artefacts-gibdd-gov-ru/breach/commons/1.0\">Т785ЕС57</ns4:RegPointNum></ns3:RequestedInformation><ns3:Governance><ns4:Name>ГИБДД РФ</ns4:Name><ns4:Code>GIBDD</ns4:Code><ns4:OfficialPerson><ns5:FamilyName xmlns:ns5=\"urn://x-artefacts-smev-gov-ru/supplementary/commons/1.0.1\">Загурский</ns5:FamilyName><ns5:FirstName>Андрей</ns5:FirstName><ns5:Patronymic>Петрович</ns5:Patronymic></ns4:OfficialPerson></ns3:Governance></ns3:BreachRequest></ns2:MessagePrimaryContent><ns1:TestMessage></ns1:TestMessage></ns1:SenderProvidedRequestData>";
        Gost3411Hash hash = new Gost3411Hash();
        BigInteger out1 = new BigInteger(1, hash.hash_byte(input));
        String hex1 = String.format("%02x", out1);
        BigInteger out2 = new BigInteger(1, hash.getBytesFromBase64("e76oVeYGapFDE+PV6glsj0XDjLHydLMd0cSkFPY8fWk="));
        String hex2 = String.format("%02x", out2);
        System.out.println(hex1);
        System.out.println(hex2);
        String key = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAhjH8R0jfvvEJwAHRhJi2Q4fLi1p2z10PaDMIhHbD3fp4OqypWaE7p6n6EHig9qnwC/4U7hCiOCqY6uYtgEoDHfbNA87/X0jV8UI522WjQH7Rgkmgk35r75G5m4cYeF6OvCHmAJ9ltaFsLBdr+pK6vKz/3AzwAc/5a6QcO/vR3PHnhE/qU2FOU3Vd8OYN2qcw4TFvitXY2H6YdTNF4YmlFtj4CqQoPL1u/uI0UpsG3/epWMOk44FBlXoZ7KNmJU29xbuiNEm1SWRJS2URMcUxAdUfhzQ2+Z4F0eSo2/cxwlkNA+gZcXnLbEWIfYYvASKpdXBIzgncMBro424z/KUr3QIDAQAB";
        assertNotNull(Gost3411Hash.getBytesFromBase64(key));
        assertEquals(hash.hash_byte(input).length, hash.getBytesFromBase64("e76oVeYGapFDE+PV6glsj0XDjLHydLMd0cSkFPY8fWk=").length);
    }




}