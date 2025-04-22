

public abstract class ScrumItem{
    protected int id;
    protected String beschrijving;
    protected String trelloID;
    public Chatroom chatroom;

    protected ScrumItem(int id, String beschrijving, String trelloID, Chatroom chatroom) {
        this.id = id;
        this.beschrijving = beschrijving;
        this.trelloID = trelloID;
        this.chatroom = chatroom;
    }
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public String getBeschrijving() {
        return beschrijving;
    }
    public void setBeschrijving(String beschrijving) {
        this.beschrijving = beschrijving;
    }
    public String getTrelloID() {
        return trelloID;
    }
    public void setTrelloID(String trelloID) {
        this.trelloID = trelloID;
    }
    public Chatroom getChatroom() {
        return chatroom;
    }
    public void setChatroom(Chatroom chatroom) {
        this.chatroom = chatroom;
    }

    public abstract void toonChatroom();





}