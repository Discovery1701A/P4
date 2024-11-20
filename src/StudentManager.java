

import java.sql.*;


public class StudentManager {
    // Konstanten für die Datenbankverbindung
    private static final String URL = "jdbc:postgresql://localhost:5432/P42";
    private static final String USER = "postgres";
    private static final String PASSWORD = "geheim"; 
    private static Connection connection;

    public static void main(String[] args) {
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
                        running = false;  // Programm beenden
                        break;
                    case 1:
                        addStudent();  // Neuen Studierenden hinzufügen
                        break;
                    case 2:
                        showAllStudents();  // Alle Studierenden anzeigen
                        break;
                    case 3:
                        updateStudentName();  // Namen eines Studierenden ändern
                        break;
                    case 4:
                        deleteAllStudents();  // Alle Studierenden löschen
                        break;
                    default:
                        System.out.println("Ungültige Wahl. Bitte erneut versuchen.");
                }
            }

        } catch (SQLException e) {
            System.out.println("Fehler bei der Verbindung zur Datenbank: " + e.getMessage());
        } finally {
            // Verbindung schließen
            closeConnection();
        }
    }

    // Methode zum Hinzufügen eines neuen Studierenden
    private static void addStudent() {
        System.out.println("Bitte geben Sie den Namen des Studierenden ein:");
        String name = Input.getString();
        System.out.println("Bitte geben Sie die Matrikelnummer des Studierenden ein:");
        int matnr = Input.getInt();
        System.out.println("Bitte geben Sie das Semester der Einschreibung ein (z.B. WS 2021/22):");
        String semester = Input.getString(); // Semester als String einlesen
    
        // Überprüfen, ob die Matrikelnummer bereits vorhanden ist
        String checkSql = "SELECT COUNT(*) FROM student WHERE matnr = ?";
    
        try (PreparedStatement checkStmt = connection.prepareStatement(checkSql)) {
            checkStmt.setInt(1, matnr);
            ResultSet rs = checkStmt.executeQuery();
            rs.next();
            int count = rs.getInt(1);

            // Wenn Matrikelnummer bereits vergeben, keine weitere Aktion
            if (count > 0) {
                System.out.println("Fehler: Diese Matrikelnummer ist bereits vergeben.");
                return;
            }
        } catch (SQLException e) {
            System.out.println("Fehler bei der Überprüfung der Matrikelnummer: " + e.getMessage());
            return;
        }
        
        // Einfüge-Operation in die Tabelle 'student'
        String sql = "INSERT INTO student (name, matnr, semester) VALUES (?, ?, ?)";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, name);  // Name einfügen
            stmt.setInt(2, matnr);    // Matrikelnummer einfügen
            stmt.setString(3, semester); // Semester einfügen
    
            stmt.executeUpdate();
            System.out.println("Studierender erfolgreich hinzugefügt!");
        } catch (SQLException e) {
            System.out.println("Fehler: " + e.getMessage());
        }
    }

    // Methode zum Anzeigen aller Studierenden
    private static void showAllStudents() {
        String sql = "SELECT * FROM student";
        
        try (Statement stmt = connection.createStatement()) {
            ResultSet rs = stmt.executeQuery(sql);
    
            // Durchlaufen und Anzeigen aller Studierenden
            while (rs.next()) {
                String name = rs.getString("name");
                int matnr = rs.getInt("matnr");
                String semester = rs.getString("semester");
    
                System.out.println(", Name: " + name + ", Matrikelnummer: " + matnr + ", Semester: " + semester);
            }
        } catch (SQLException e) {
            System.out.println("Fehler: " + e.getMessage());
        }
    }

    // Methode zum Ändern des Namens eines Studierenden
    private static void updateStudentName() {
        System.out.println("Bitte geben Sie die Matrikelnummer des Studierenden ein:");
        int matnr = Input.getInt();
        
        // Überprüfen, ob die Matrikelnummer existiert
        String checkSql = "SELECT COUNT(*) FROM student WHERE matnr = ?";
        try (PreparedStatement checkStmt = connection.prepareStatement(checkSql)) {
            checkStmt.setInt(1, matnr);
            ResultSet rs = checkStmt.executeQuery();
            rs.next();
            int count = rs.getInt(1);
    
            if (count != 1) {
                System.out.println("Fehler: Diese Matrikelnummer existiert nicht.");
                return;
            }
        } catch (SQLException e) {
            System.out.println("Fehler bei der Überprüfung der Matrikelnummer: " + e.getMessage());
            return;
        }
        
        System.out.println("Bitte geben Sie den neuen Namen des Studierenden ein:");
        String name = Input.getString();
    
        // Update-Operation für den Namen
        String sql = "UPDATE student SET name = ? WHERE matnr = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, name);  // Neuer Name setzen
            stmt.setInt(2, matnr);    // Matrikelnummer zur Identifizierung setzen
    
            int count = stmt.executeUpdate();
    
            // Überprüfen, ob Update erfolgreich war
            if (count == 0) {
                System.out.println("Fehler: Kein Studierender mit dieser Matrikelnummer gefunden.");
            } else {
                System.out.println("Name erfolgreich geändert!");
            }
        } catch (SQLException e) {
            System.out.println("Fehler: " + e.getMessage());
        }
    }

    // Methode zum Löschen aller Studierenden
    private static void deleteAllStudents() {
        String sql = "DELETE FROM student";  // Alle Einträge löschen 
        
        try (Statement stmt = connection.createStatement()) {
            stmt.executeUpdate(sql);
            System.out.println("Alle Studierenden erfolgreich gelöscht!");
        } catch (SQLException e) {
            System.out.println("Fehler: " + e.getMessage());
        }
    }

    // Methode zum Schließen der Datenbankverbindung
    private static void closeConnection() {
        try {
            connection.close();
        } catch (SQLException e) {
            System.out.println("Fehler beim Schließen der Verbindung: " + e.getMessage());
        }
    }
}