import java.net.IDN;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
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
            boolean running = true;
            while (running) {
                System.out.println("Voer het ID van een epic in  of type /addEpic om een nieuwe epic toe te voegen:\n" +
                        "type /exit om het programma af te sluiten");
                scrumItems = showEpics();
                for (ScrumItem item : scrumItems) {;
                    if (item instanceof Epic) {
                        System.out.println(item.getId() + ": " + item.getBeschrijving());
                    }
                }
                String input = scanner.nextLine();
                if (input.equals("/exit")) {
                    System.out.println("Tot ziens!");
                    running = false;
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
                Chatroom chatroom = new Chatroom(chatroomID);

                epics.add(new Epic(id, beschrijving, trelloID, chatroom));

            }
        } catch (SQLException e) {
            System.out.println("Fout bij het ophalen van epics: " + e.getMessage());
        }
        return epics;
    }

    private static Chatroom getChatroom(int chatroomID) {
        Chatroom chatroom = makeChat(chatroomID);
        return chatroom;
    }

    private static void checkEpicInput(String input, Gebruiker gebruiker) throws SQLException {
        Scanner scanner = new Scanner(System.in);
        if (input.equals("/addEpic")) {
            addEpic(gebruiker);
        } else if (input.matches("\\d+")) {
            int epicId = Integer.parseInt(input);

            boolean manageEpic = true;
            while (manageEpic) {
                showEpicDetails(epicId);
                for (ScrumItem item : scrumItems) {
                    if (item instanceof UserStory) {
                        System.out.println(item.getId() + ": " + item.getBeschrijving());
                    }
                }
                System.out.println("Opties: /showchat, /addUserStory, /back of selecteer een User story van de lijst:");
                String command = scanner.nextLine();
                switch (command) {
                    case "/showchat":
                        Epic selectedEpic = null;
                        for (ScrumItem item : scrumItems) {
                            if (item instanceof Epic && item.getId() == epicId) {
                                selectedEpic = (Epic) item;
                                break;
                            }
                        }
                        if (selectedEpic != null) {
                            selectedEpic.setChatroom(makeChat(selectedEpic.getChatroom().getChat_id()));
                            selectedEpic.toonChatroom();
                            boolean texting = true;
                            while (texting){
                                System.out.println("Verstuur een bericht of type /back:");
                                String berichtTekst = scanner.nextLine();
                                if (berichtTekst.equals("/back")) {
                                    texting = false;
                                    break;
                                }
                                ZonedDateTime tijdstip = ZonedDateTime.now();
                                Bericht bericht = new Bericht(0, berichtTekst, gebruiker, tijdstip, selectedEpic.getChatroom().getChat_id());
                                selectedEpic.getChatroom().slaBerichtOp(bericht);
                            }
                            System.out.println("Verstuur nu een bericht of type /back:");
                            String berichtTekst = scanner.nextLine();
                            ZonedDateTime tijdstip = ZonedDateTime.now();
                            Bericht bericht = new Bericht(0, berichtTekst, gebruiker, tijdstip, selectedEpic.getChatroom().getChat_id());
                            getChatroom(selectedEpic.getChatroom().getChat_id()).slaBerichtOp(bericht);

                        } else {
                            System.out.println("Epic niet gevonden.");
                        }
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
            PreparedStatement storyStmt = conn.prepareStatement("SELECT us_id, beschrijving, trelloID, chatroom  FROM userstory WHERE epic_id = ?");
            storyStmt.setInt(1, epicId);
            ResultSet stories = storyStmt.executeQuery();
            while (stories.next()) {
                int us_id = stories.getInt("us_id");
                String beschrijving = stories.getString("beschrijving");
                String trelloID = stories.getString("trelloID");
                int chatroomId = stories.getInt("chatroom");
                Chatroom chatroom = new Chatroom(chatroomId);
                UserStory userStory = new UserStory(us_id, beschrijving, trelloID, chatroom);
                scrumItems.add(userStory);
            }
        } catch (SQLException e) {
            System.out.println("Fout bij het ophalen van epic details: " + e.getMessage());
        }

    }

    private static Chatroom makeChat(int chatroomID) {
        Chatroom chatroom = new Chatroom(chatroomID);
        try (Connection conn = DatabaseConnection.connectDatabase();
             PreparedStatement stmt = conn.prepareStatement(
                     "SELECT b.b_id, b.tekst, b.tijdstip, g.naam, g.u_id, b.chat_id FROM bericht b " +
                             "JOIN gebruiker g ON b.auteur = g.u_id " +
                             "WHERE b.chat_id = ? ORDER BY b.tijdstip ASC");) {

            stmt.setInt(1, chatroomID);
            ResultSet rs = stmt.executeQuery();
            System.out.println("Chatroom berichten:");
            while (rs.next()) {
                int berichtId = rs.getInt("b_id");
                String berichttekst = rs.getString("tekst");

                int gebruikerId = rs.getInt("u_id");
                String naam = rs.getString("naam");

                String tijdstip = rs.getString("tijdstip");

                LocalDateTime localDateTime = LocalDateTime.parse(tijdstip, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                ZonedDateTime tijdstipZoned = localDateTime.atZone(ZoneId.systemDefault());



                int chatroomId = rs.getInt("chat_id");
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

            String epicSQL = "INSERT INTO userstory (epic_id, beschrijving, trelloID, chatroom) VALUES (?, ?, ?, ?)";
            stmt = conn.prepareStatement(epicSQL, Statement.RETURN_GENERATED_KEYS);
            stmt.setInt(1, epicId);
            stmt.setString(2, beschrijving);
            stmt.setString(3, trelloID);
            stmt.setInt(4, chatroomId);
            stmt.executeUpdate();

            rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                int userstoryID = rs.getInt(1);
                System.out.println("Nieuwe userstory aangemaakt: " + beschrijving + " (ID: " + userstoryID + ")");
            }

            conn.commit();
        } catch (SQLException e) {
            System.out.println("Fout bij het toevoegen van de user story: " + e.getMessage());
            try { if (conn != null) conn.rollback(); } catch (SQLException ignored) {}
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
        }
        return null;
    }
}
