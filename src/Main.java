import java.net.IDN;
import java.sql.*;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Scanner;

public class Main {

    static ArrayList<ScrumItem> scrumItems;

    public static void main(String[] args) throws SQLException {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Vul uw naam in:");
        String naam = scanner.nextLine();
        Gebruiker gebruiker = checkNaamDatabase(naam);

        if (gebruiker != null) {
            System.out.println("Welkom, " + gebruiker.getNaam() + "!");
            boolean epics = true;
            while (epics) {
                System.out.println("Voer het ID van een epic in  of type /addEpic om een nieuwe epic toe te voegen:\n" +
                        "type /exit om het programma af te sluiten");
                scrumItems = showEpics();;
                for (ScrumItem item : scrumItems) {;
                    if (item instanceof Epic) {
                        System.out.println(item.getId() + ": " + item.getBeschrijving());
                    }
                }
                String input = scanner.nextLine();
                if (input.equals("/exit")) {
                    System.out.println("Tot ziens!");
                    epics = false;
                    break;
                }
                checkEpicInput(input, gebruiker);
            }

        } else {
            System.out.println("Er is een fout opgetreden bij het ophalen van de gebruiker.");
        }
    }

    private static ArrayList<ScrumItem> showEpics() {
        ArrayList<ScrumItem> epics = new ArrayList<>();
        try (Connection conn = DatabaseConnection.connectDatabase();
             PreparedStatement stmt = conn.prepareStatement("SELECT epic_id, beschrijving, trelloID, chatroom FROM epic");
             ResultSet rs = stmt.executeQuery()) {

            System.out.println("Beschikbare epics:");
            while (rs.next()) {
                int id = rs.getInt("epic_id");
                String beschrijving = rs.getString("beschrijving");
                String trelloID = rs.getString("trelloID");
                int chatroomID = rs.getInt("chatroom");
                Chatroom chatroom = getChatroom(chatroomID);

                epics.add(new Epic(id, beschrijving, trelloID, chatroom));

            }
        } catch (SQLException e) {
            System.out.println("Fout bij het ophalen van epics: " + e.getMessage());
        }
        return epics;
    }

    private static Chatroom getChatroom(int chatroomID) {
        Chatroom chatroom = null;
        try (Connection conn = DatabaseConnection.connectDatabase();
             PreparedStatement stmt = conn.prepareStatement("SELECT * FROM chatroom WHERE chatroom_id = ?")) {

            stmt.setInt(0, chatroomID);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                int id = rs.getInt("chatroom_id");
                chatroom = new Chatroom(id);
            } else {
                System.out.println("Geen chatroom gevonden met ID: " + chatroomID);
            }
        } catch (SQLException e) {
            System.out.println("Fout bij het ophalen van de chatroom: " + e.getMessage());
        }
        return chatroom;
    }

    private static void checkEpicInput(String input, Gebruiker gebruiker) {
        Scanner scanner = new Scanner(System.in);
        if (input.equals("/addEpic")) {
            addEpic(gebruiker);
        } else if (input.matches("\\d+")) {
            int epicId = Integer.parseInt(input);
            boolean manageEpic = true;
            while (manageEpic) {
                showEpicDetails(epicId);
                System.out.println("Opties: /showchat, /addUserStory, /back of selecteer een User story van de lijst:");
                String command = scanner.nextLine();
                switch (command) {
                    case "/showchat":
                        Chatroom chatroom = makeChat(epicId);
                        break;
                    case "/addUserStory":
                        addUserStory(epicId);
                        break;
                    case "/back":
                        manageEpic = false;
                        break;
                    default:
                        System.out.println("Ongeldige optie.");
                }
            }
        } else {
            System.out.println("Ongeldige invoer. Probeer het opnieuw.");
        }
    }

    private static void showEpicDetails(int epicId) {
        System.out.println("Details van epic met ID " + epicId + ":");
        try (Connection conn = DatabaseConnection.connectDatabase();
             PreparedStatement stmt = conn.prepareStatement("SELECT beschrijving FROM epic WHERE epic_id = ?")) {

            stmt.setInt(1, epicId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                System.out.println("Beschrijving: " + rs.getString("beschrijving"));
            }

            System.out.println("User stories:");
            PreparedStatement storyStmt = conn.prepareStatement("SELECT beschrijving FROM userstory WHERE epic_id = ?");
            storyStmt.setInt(1, epicId);
            ResultSet stories = storyStmt.executeQuery();
            while (stories.next()) {
                System.out.println(stories.getString("us_id") + " - " + stories.getString("beschrijving"));
            }
        } catch (SQLException e) {
            System.out.println("Fout bij het ophalen van epic details: " + e.getMessage());
        }
    }

    private static Chatroom makeChat(int epicId) {
        Chatroom chatroom = new Chatroom(0);
        try (Connection conn = DatabaseConnection.connectDatabase();
             PreparedStatement stmt = conn.prepareStatement(
                     "SELECT b.b_id, b.tekst, b.tijdstip, b.chatroom g.naam, g.u_id  FROM bericht b JOIN gebruiker g ON c.gebruiker_id = g.u_id " +
                             "WHERE c.chatroom_id = (SELECT chatroom FROM epic WHERE epic_id = ?)");) {

            stmt.setInt(0, epicId);
            ResultSet rs = stmt.executeQuery();
            System.out.println("Chatroom berichten:");
            while (rs.next()) {
                int berichtId = rs.getInt("b_id");
                String berichttekst = rs.getString("tekst");
                String tijdstip = rs.getString("tijdstip");
                int gebruikerId = rs.getInt("u_id");

                ZonedDateTime tijdstipZoned = ZonedDateTime.parse(tijdstip);
                String naam = rs.getString("naam");

                int chatroomId = Integer.parseInt(rs.getString("chatroom"));
                chatroom.setChat_id(chatroomId);
                Gebruiker auteur = new Gebruiker(gebruikerId, naam);
                Bericht bericht = new Bericht(berichtId, berichttekst, auteur, tijdstipZoned, chatroomId);
                chatroom.voegBerichtToe(bericht);
            }
        } catch (SQLException e) {
            System.out.println("Fout bij het ophalen van chat: " + e.getMessage());
        }
        return chatroom;
    }

    private static void addUserStory(int epicId) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Voer de beschrijving in van de user story:");
        String beschrijving = scanner.nextLine();

        try (Connection conn = DatabaseConnection.connectDatabase();
             PreparedStatement stmt = conn.prepareStatement(
                     "INSERT INTO userstory (beschrijving, epic_id) VALUES (?, ?)");) {

            stmt.setString(1, beschrijving);
            stmt.setInt(2, epicId);
            stmt.executeUpdate();
            System.out.println("User story toegevoegd.");

        } catch (SQLException e) {
            System.out.println("Fout bij het toevoegen van user story: " + e.getMessage());
        }
    }

    private static void addEpic(Gebruiker gebruiker) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Voer de beschrijving van de nieuwe epic in:");
        String epicBeschrijving = scanner.nextLine();
        System.out.println("Voer de TrelloID in:");
        String trelloID = scanner.nextLine();

        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = DatabaseConnection.connectDatabase();
            conn.setAutoCommit(false);

            String chatroomSQL = "INSERT INTO chatroom () VALUES ()";
            stmt = conn.prepareStatement(chatroomSQL, Statement.RETURN_GENERATED_KEYS);
            stmt.executeUpdate();

            rs = stmt.getGeneratedKeys();
            int chatroomId;
            if (rs.next()) {
                chatroomId = rs.getInt(1);
            } else {
                throw new SQLException("Chatroom aanmaken mislukt.");
            }
            rs.close();
            stmt.close();

            String epicSQL = "INSERT INTO epic (beschrijving, trelloID, chatroom) VALUES (?, ?, ?)";
            stmt = conn.prepareStatement(epicSQL, Statement.RETURN_GENERATED_KEYS);
            stmt.setString(1, epicBeschrijving);
            stmt.setString(2, trelloID);
            stmt.setInt(3, chatroomId);
            stmt.executeUpdate();

            rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                int epicId = rs.getInt(1);
                System.out.println("Nieuwe epic aangemaakt: " + epicBeschrijving + " (ID: " + epicId + ")");
            }

            conn.commit();
        } catch (SQLException e) {
            System.out.println("Fout bij het toevoegen van de epic: " + e.getMessage());
            try { if (conn != null) conn.rollback(); } catch (SQLException ignored) {}
        } finally {
            try { if (rs != null) rs.close(); } catch (SQLException ignored) {}
            try { if (stmt != null) stmt.close(); } catch (SQLException ignored) {}
            try { if (conn != null) conn.setAutoCommit(true); conn.close(); } catch (SQLException ignored) {}
        }
    }

    private static Gebruiker checkNaamDatabase(String naam) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = DatabaseConnection.connectDatabase();
            String selectSQL = "SELECT u_id, naam FROM gebruiker WHERE naam = ?";
            stmt = conn.prepareStatement(selectSQL);
            stmt.setString(1, naam);
            rs = stmt.executeQuery();

            if (rs.next()) {
                int userId = rs.getInt("u_id");
                String userName = rs.getString("naam");
                Gebruiker gebruiker = new Gebruiker(userId, userName);
                System.out.println("Gebruiker gevonden: " + gebruiker.getNaam());
                return gebruiker;
            } else {
                String insertSQL = "INSERT INTO gebruiker (naam) VALUES (?)";
                stmt = conn.prepareStatement(insertSQL, Statement.RETURN_GENERATED_KEYS);
                stmt.setString(1, naam);
                stmt.executeUpdate();

                rs = stmt.getGeneratedKeys();
                if (rs.next()) {
                    int newUserId = rs.getInt(1);
                    Gebruiker newUser = new Gebruiker(newUserId, naam);
                    System.out.println("Nieuwe gebruiker aangemaakt: " + newUser.getNaam());
                    return newUser;
                }
            }

        } catch (SQLException e) {
            System.out.println("Fout bij het controleren van de naam in de database: " + e.getMessage());
        } finally {
            try { if (rs != null) rs.close(); } catch (SQLException ignored) {}
            try { if (stmt != null) stmt.close(); } catch (SQLException ignored) {}
            try { if (conn != null) conn.close(); } catch (SQLException ignored) {}
        }
        return null;
    }
}
