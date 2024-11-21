import java.sql.*;

public class StudentManager {
    // Konstanten für die Datenbankverbindung
    private static final String URL = "jdbc:postgresql://localhost:5432/P42";
    private static final String USER = "postgres";
    private static final String PASSWORD = "geheim";
    private static Connection connection;

    public static void main(String[] args) {
        StudentManager studentManager = new StudentManager();
        try {
            // Verbindet sich mit der Datenbank
            connection = DriverManager.getConnection(URL, USER, PASSWORD);
            boolean running = true;

            // Hauptprogramm-Schleife für Menüeingaben
            while (running) {
                System.out.println("Was wollen Sie?");
                System.out.println("(0) Programm beenden");
                System.out.println("(1) neuen Studierenden hinzufügen");
                System.out.println("(2) alle Studierenden anzeigen");
                System.out.println("(3) Namen eines Studierenden ändern");
                System.out.println("(4) alle Studierenden löschen");

                // Erfassung der Benutzereingabe
                int choice = Input.getInt();

                // Auswahl ausführen basierend auf Benutzereingabe
                switch (choice) {
                    case 0:
                        running = false; // Programm beenden
                        break;
                    case 1:
                        studentManager.addStudent(); // Neuen Studierenden hinzufügen
                        break;
                    case 2:
                        studentManager.showAllStudents(); // Alle Studierenden anzeigen
                        break;
                    case 3:
                        studentManager.updateStudentName(); // Namen eines Studierenden ändern
                        break;
                    case 4:
                        studentManager.deleteAllStudents(); // Alle Studierenden löschen
                        break;
                    default:
                        System.out.println("Ungültige Wahl. Bitte erneut versuchen.");
                }
            }

        } catch (SQLException e) {
            System.out.println("Fehler bei der Verbindung zur Datenbank: " + e.getMessage());
        } finally {
            // Verbindung schließen
            studentManager.closeConnection();
        }
    }

    private void addStudent() {
        boolean runningSemester = true;
        boolean runningMartNr = true;
        int matnr = 0;
        // RegEx für Semester-Format
        String semesterPattern = "^(WS \\d{4}/\\d{2}|SS \\d{4})$";
        String semester = "";

        System.out.println("Bitte geben Sie den Namen des Studierenden ein:");
        String name = Input.getString();

        while (runningMartNr) {

            System.out.println("Bitte geben Sie die Matrikelnummer des Studierenden ein:");
            matnr = Input.getInt();
            if (checkMartNr(matnr)) {
                System.out.println("Fehler: Matrikelnummer bereits vorhanden.");

            } else {
                runningMartNr = false;
            }
        }

        // Wiederhole Eingabe, bis ein gültiges Semester-Format eingegeben wird
        while (runningSemester) {
            System.out.println("Bitte geben Sie das Semester der Einschreibung ein (z.B. WS 2021/22 oder SS 2020):");
            semester = Input.getString();

            if (semester.matches(semesterPattern)) {
                runningSemester = false; // Gültige Eingabe
            } else {
                System.out.println(
                        "Ungültiges Format. Bitte geben Sie das Semester im Format 'WS 2020/21' oder 'SS 2020' ein.");
            }
        }

        // Einfüge-Operation in die Tabelle 'student'
        String sql = "INSERT INTO student (name, matnr, semester) VALUES ('" + name + "', " + matnr + ", '" + semester
                + "')";

        try (Statement stmt = connection.createStatement()) {
            stmt.executeUpdate(sql); // Übergabe an Datenbank
            System.out.println("Studierender erfolgreich hinzugefügt!");
        } catch (SQLException e) {
            System.out.println("Fehler: " + e.getMessage());
        }
    }

    // Methode zum Anzeigen aller Studierenden
    private void showAllStudents() {
        // Auswahl aller Einträge aus der Tabelle 'student'
        String sql = "SELECT * FROM student";
        boolean studentFound = false;
        try (Statement stmt = connection.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {
            // Durchlaufen und Anzeigen aller Studierenden
            while (rs.next()) {
                String name = rs.getString("name");
                int matnr = rs.getInt("matnr");
                String semester = rs.getString("semester");

                System.out.println("Name: " + name + ", Matrikelnummer: " + matnr + ", Semester: " + semester);
                studentFound = true;
            }
            if (!studentFound) {
                System.out.println("Keine Studierenden gefunden.");
            }
        } catch (SQLException e) {
            System.out.println("Fehler: " + e.getMessage());
        }
    }

    // Methode zum Ändern des Namens eines Studierenden
    private void updateStudentName() {
        boolean running = true;
        while (running) {

            System.out.println("Bitte geben Sie die Matrikelnummer des Studierenden ein:");
            int matnr = Input.getInt();

            // Überprüfen, ob die Matrikelnummer existiert
            if (!checkMartNr(matnr)) {
                System.out.println("Fehler: Kein Studierender mit dieser Matrikelnummer gefunden.");

            } else {

                System.out.println("Bitte geben Sie den neuen Namen des Studierenden ein:");
                String name = Input.getString();

                // Update-Operation für den Namen
                String sql = "UPDATE student SET name = '" + name + "' WHERE matnr = " + matnr;

                try (Statement stmt = connection.createStatement()) {
                    int count = stmt.executeUpdate(sql);

                    // Überprüfen, ob Update erfolgreich war
                    if (count == 0) {
                        System.out.println("Fehler: Kein Studierender mit dieser Matrikelnummer gefunden.");
                    } else {
                        System.out.println("Name erfolgreich geändert!");
                    }
                    running = false;
                } catch (SQLException e) {
                    System.out.println("Fehler: " + e.getMessage());
                }
            }

        }
    }

    // Methode zum Löschen aller Studierenden
    private void deleteAllStudents() {
        String sql = "DELETE FROM student"; // Alle Einträge löschen

        try (Statement stmt = connection.createStatement()) {
            stmt.executeUpdate(sql);
            System.out.println("Alle Studierenden erfolgreich gelöscht!");
        } catch (SQLException e) {
            System.out.println("Fehler: " + e.getMessage());
        }
    }

    // Methode zum Schließen der Datenbankverbindung
    private void closeConnection() {
        try {
            connection.close();
        } catch (SQLException e) {
            System.out.println("Fehler beim Schließen der Verbindung: " + e.getMessage());
        }
    }

    private boolean checkMartNr(int matnr) {
        String checkSql = "SELECT * FROM student WHERE matnr = " + matnr;
        try (Statement stmt = connection.createStatement();
                ResultSet rs = stmt.executeQuery(checkSql)) {

            return rs.next(); // Wenn ein Eintrag gefunden wird, existiert die Matrikelnummer bereits

        } catch (SQLException e) {
            System.out.println("Fehler bei der Überprüfung der Matrikelnummer: " + e.getMessage());
            return false; // Bei Fehler wird die Matrikelnummer als nicht existierend behandelt
        }
    }

}