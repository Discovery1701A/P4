

import java.sql.*;
import java.sql.Connection;



public class StudentManager {
    private static final String URL = "jdbc:postgresql://localhost:5432/P42";
    private static final String USER = "postgres";
    private static final String PASSWORD = "geheim"; 
    private static Connection connection;

    public static void main(String[] args) {
        try {
            connection = DriverManager.getConnection(URL, USER, PASSWORD);
            boolean running = true;

            while (running) {
                System.out.println("Was wollen Sie?");
                System.out.println("(0) Programm beenden");
                System.out.println("(1) neuen Studierenden hinzufuegen");
                System.out.println("(2) alle Studierenden zeigen");
                System.out.println("(3) Namen eines Studierenden aendern");
                System.out.println("(4) alle Studierenden loeschen");

                int choice = Input.getInt();

                switch (choice) {
                    case 0:
                        running = false;
                        break;
                    case 1:
                        addStudent();
                        break;
                    case 2:
                        showAllStudents();
                        break;
                    case 3:
                        updateStudentName();
                        break;
                    case 4:
                        deleteAllStudents();
                        break;
                    default:
                        System.out.println("Ungültige Wahl. Bitte erneut versuchen.");
                }
            }

        } catch (SQLException e) {
            System.out.println("Fehler bei der Verbindung zur Datenbank: " + e.getMessage());
        } finally {
            closeConnection();
        }
    }

    private static void addStudent() {
        System.out.println("fffff");
    }

    private static void showAllStudents() {

    }

    private static void updateStudentName() {

    }

    private static void deleteAllStudents() {

    }

    private static void closeConnection() {
        try {
            connection.close();
        } catch (SQLException e) {
            System.out.println("Fehler beim Schließen der Verbindung: " + e.getMessage());
        }
    }

   

   


  
}
