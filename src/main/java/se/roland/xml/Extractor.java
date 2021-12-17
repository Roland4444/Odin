package se.roland.xml;

import java.io.*;

public class Extractor implements Serializable {
    public Extractor(){
    }

    public String extractAttribute(byte[] input_, String AttributeName){
        String result="";
        String input=new String(input_);
        int pStart=input.indexOf(AttributeName);
        if (pStart==-1)
            return null;
        int offset=0;
        while (input.charAt(pStart+(++offset))!='"'){ }
        offset++;
        while (input.charAt(pStart+(offset))!='"'){
            System.out.println(input.charAt(pStart+(offset)));
            result+=input.charAt(pStart+(offset++));
        }

        return result;
    }

    public String extractTagValue(byte[] input2, String TagName){
        String input = new String(input2);
        int pStart=input.indexOf(TagName);
        if (pStart==-1) return null;
        int offset=0;
        int addOffset=0;
        while (input.charAt(pStart+(offset++))!='>') {};
        while (input.charAt(pStart+offset+(addOffset++))!='<') {};
        addOffset--;
        return input.substring(pStart+offset, pStart+offset+addOffset);
    }

    public String extractTagValue(String input, String TagName) {
        int pStart = input.indexOf(TagName);
        if (pStart == -1) return "";
        int offset = 0;
        int addOffset = 0;
        while (input.charAt(pStart + (offset++)) != '>') {
        }
        ;
        while (input.charAt(pStart + offset + (addOffset++)) != '<') {
        }
        ;
        addOffset--;
        return input.substring(pStart + offset, pStart + offset + addOffset);
    }

    public boolean isNumber(char input){
        switch (input){
            case '0': return true;
            case '1': return true;
            case '2': return true;
            case '3': return true;
            case '4': return true;
            case '5': return true;
            case '6': return true;
            case '7': return true;
            case '8': return true;
            case '9': return true;
        }
        return false;

    }

    public int extractNumber(String input){
        int binPos=input.indexOf(".bin");
        int endpos=binPos;
        while (isNumber(input.charAt(--binPos))) {};
        String number = input.substring(++binPos, endpos);

        return Integer.parseInt(number);
    }

    public String parse(String FileName, String tagToFind) throws IOException {
        String result="";
        String input="";
        int pos=-2;
        int stopper = -2;
        File f = new File(FileName);
        BufferedReader b = new BufferedReader(new FileReader(f));
        String readLine = "";
        while ((readLine = b.readLine()) != null)
            input+=readLine;
        pos = input.indexOf(tagToFind);
        int startParse=pos;
        while (input.charAt(startParse)!='<')
            startParse--;
        stopper = input.indexOf(tagToFind, pos+tagToFind.length() );
        while (input.charAt(stopper)!='>')
            ++stopper;
        result = input.substring(startParse, ++stopper );
        return result;
    }

    public String parseTagFromByte(byte[] inputStream, String tagToFind) throws IOException {
        String result="";
        int pos=-2;
        int stopper = -2;
        int finisher=-2;
        String input=new String(inputStream);
        pos = input.indexOf(tagToFind);
        System.out.println(tagToFind+"<>"+pos+"()"+tagToFind);
        if (pos<0)
            return null;
        if (pos >0)
            if (input.indexOf( tagToFind, pos+1)<0) return null;


        int startParse=pos;
        while (input.charAt(startParse)!='<')
            startParse--;
        stopper = input.indexOf(tagToFind, pos+tagToFind.length() );
        while (input.charAt(stopper)!='>')
            ++stopper;

        result = input.substring(startParse, ++stopper );
        return result;
    }

    public String extractRaw(String FileName, String tagToFind) throws IOException {
        String result="";
        String input="";
        int pos=-2;
        int stopper = -2;
        File f = new File(FileName);
        BufferedReader b = new BufferedReader(new FileReader(f));
        String readLine = "";
        while ((readLine = b.readLine()) != null)
            input+=readLine;
        pos = input.indexOf(tagToFind);
        int startParse=pos;
        while (input.charAt(startParse++)!='>'){}startParse++;
        stopper = input.indexOf(tagToFind, pos+tagToFind.length() );
        while (input.charAt(--stopper)!='<'){}startParse--;
        result = input.substring(startParse, stopper );
        return result;
    }

    public String extractCN(String input){
        StringBuffer str= new StringBuffer();
        int start=input.indexOf("CN=");
        while (input.charAt(++start+2)!=',')
            str.append(input.charAt(start+2));
        return str.toString();
    }
}
