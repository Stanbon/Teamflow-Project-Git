import java.util.ArrayList;
import java.sql.*;

public class Chatroom {
    private int chat_id;
    private ArrayList<Bericht> chatberichten;

    public Chatroom(int chat_id){
        this.chat_id = chat_id;
        this.chatberichten = new ArrayList<>();
    }

    public void voegBerichtToe(Bericht bericht){
        chatberichten.add(bericht);
    }

    public void setBerichten(ArrayList<Bericht> berichten){
        this.chatberichten = berichten;
    }

    public ArrayList<Bericht> getBerichten(){
        return chatberichten;
    }

    public int getChat_id() {
        return chat_id;
    }

    public void setChat_id(int chat_id) {
        this.chat_id = chat_id;
    }

    public void slaBerichtOp(Bericht bericht) throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = DatabaseConnection.connectDatabase();
            String sql = "INSERT INTO bericht (tekst, auteur, tijdstip, chat_id) VALUES (?, ?, ?, ?)";
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, bericht.getTekst());
            stmt.setInt(2, bericht.getAuteur().getId());
            stmt.setTimestamp(3, Timestamp.valueOf(bericht.getTijdstip().toLocalDateTime()));
            stmt.setInt(4, bericht.getChat_id());
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Fout bij het controleren van de naam in de database: " + e.getMessage());
        }

    }
}
