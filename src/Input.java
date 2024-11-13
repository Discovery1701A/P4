

import java.util.Scanner;

public class Input {

    public static String getString() {
        String ergebnis;
        Scanner sc =  new Scanner(System.in);
        ergebnis = sc.nextLine();
        return ergebnis;
    }

    public static int getInt() {
        int ergebnis;
        try {
            ergebnis = Integer.decode(getString());
        } catch (NumberFormatException e) {
            ergebnis = -1;
        }

        return ergebnis;
    }

    public static float getFloat() {
        float ergebnis;
        try {
            ergebnis = Float.valueOf(getString());
        } catch (NumberFormatException e) {
            ergebnis = -1f;
        }

        return ergebnis;
    }

    public static double getDouble() {
        double ergebnis;
        try {
            ergebnis = Double.valueOf(getString());
        } catch (NumberFormatException e) {
            ergebnis = -1d;
        }

        return ergebnis;
    }

    public static boolean getBoolean() {
        boolean ergebnis;
        try {
            ergebnis = Boolean.parseBoolean(getString());
        } catch (NumberFormatException e) {
            ergebnis = false;
        }

        return ergebnis;
    }
    
    public static void main(String[] args) {
    	// Beispiel
		System.out.print("Zeichenkette eingeben: ");
		String eingabe = Input.getString();
		System.out.println(eingabe);
		System.out.println();
		System.out.print("Integer eingeben: ");
		int wert = Input.getInt();
		System.out.println(wert);
	}
}
