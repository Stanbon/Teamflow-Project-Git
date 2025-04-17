import java.util.ArrayList;

public class ChatRoom {
    private int chat_id;
    private ArrayList<Bericht> chatberichten;

    public void voegBerichtToe(Bericht bericht){
        chatberichten.add(bericht);
    }
}
