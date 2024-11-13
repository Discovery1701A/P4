

import java.sql.*;



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
        System.out.println("Bitte geben Sie den Namen des Studierenden ein:");
        String name = Input.getString();
        System.out.println("Bitte geben Sie die Matrikelnummer des Studierenden ein:");
        int matnr = Input.getInt();
        System.out.println("Bitte geben Sie das Semester der Einschreibung ein (z.B. WS 2021/22):");
        String semester = Input.getString(); // Semester als String einlesen
    
        String checkSql = "SELECT COUNT(*) FROM student WHERE matnr = ?";
    
    try (PreparedStatement checkStmt = connection.prepareStatement(checkSql)) {
        checkStmt.setInt(1, matnr);
        ResultSet rs = checkStmt.executeQuery();
        rs.next();
        int count = rs.getInt(1);

        if (count > 0) {
            System.out.println("Fehler: Diese Matrikelnummer ist bereits vergeben.");
            return; // Methode beenden, ohne den Einfügevorgang auszuführen
        }
    } catch (SQLException e) {
        System.out.println("Fehler bei der Überprüfung der Matrikelnummer: " + e.getMessage());
        return;
    }
        
        String sql = "INSERT INTO student (name, matnr, semester) VALUES (?, ?, ?)";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, name);  // Name zuerst setzen
            stmt.setInt(2, matnr);    // Matrikelnummer als zweites setzen
            stmt.setString(3, semester); // Semester als drittes setzen
    
            stmt.executeUpdate();
            System.out.println("Studierender erfolgreich hinzugefügt!");
        } catch (SQLException e) {
            System.out.println("Fehler: " + e.getMessage());
        }
    }

    private static void showAllStudents() {

        String sql = "SELECT * FROM student";
        
        try (Statement stmt = connection.createStatement()) {
            ResultSet rs = stmt.executeQuery(sql);
    
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

    private static void updateStudentName() {

        System.out.println("Bitte geben Sie die Matrikelnummer des Studierenden ein:");
        int matnr = Input.getInt();
        String checkSql = "SELECT COUNT(*) FROM student WHERE matnr = ?";
        try (PreparedStatement checkStmt = connection.prepareStatement(checkSql)) {
            checkStmt.setInt(1, matnr);
            ResultSet rs = checkStmt.executeQuery();
            rs.next();
            int count = rs.getInt(1);
    
            if (count != 1) {
                System.out.println("Fehler: Diese Matrikelnummer exestiert nicht.");
                return; // Methode beenden, ohne den Einfügevorgang auszuführen
            }
        } catch (SQLException e) {
            System.out.println("Fehler bei der Überprüfung der Matrikelnummer: " + e.getMessage());
            return;
        }
        System.out.println("Bitte geben Sie den neuen Namen des Studierenden ein:");
        String name = Input.getString();
    
        String sql = "UPDATE student SET name = ? WHERE matnr = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, name);  // Name zuerst setzen
            stmt.setInt(2, matnr);    // Matrikelnummer als zweites setzen
    
            int count = stmt.executeUpdate();
    
            if (count == 0) {
                System.out.println("Fehler: Kein Studierender mit dieser Matrikelnummer gefunden.");
            } else {
                System.out.println("Name erfolgreich geändert!");
            }
        } catch (SQLException e) {
            System.out.println("Fehler: " + e.getMessage());
        }

    }

    private static void deleteAllStudents() {
        String sql = "DELETE FROM student";  // Alle Einträge löschen 
        
        try (Statement stmt = connection.createStatement()) {
            stmt.executeUpdate(sql);
            System.out.println("Alle Studierenden erfolgreich gelöscht!");
        } catch (SQLException e) {
            System.out.println("Fehler: " + e.getMessage());
        }

    }

    private static void closeConnection() {
        try {
            connection.close();
        } catch (SQLException e) {
            System.out.println("Fehler beim Schließen der Verbindung: " + e.getMessage());
        }
    }

   

   


  
}
