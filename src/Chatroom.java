import java.util.ArrayList;

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
}
