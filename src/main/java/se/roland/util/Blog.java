package se.roland.util;

import javax.smartcardio.*;

class Example {
    public static void main(String[] args) {
        try {
            var factory = TerminalFactory.getDefault();
            var terminals = factory.terminals().list();
            System.out.println("Found terminals:");

            int i = 0;
            for (var t : terminals) {
                i++;
                System.out.printf("  Terminal #%d: %s %n", i, t.getName());
            }
        } catch (CardException e) {
            System.out.println("CardException: " + e.toString());
        }
    }
}