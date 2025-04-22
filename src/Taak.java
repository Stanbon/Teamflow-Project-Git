public class Taak extends ScrumItem{

    private UserStory userStory;
    protected Taak(int id, String beschrijving, String trelloID, Chatroom chatroom, UserStory userStory) {
        super(id, beschrijving, trelloID, chatroom);
        this.userStory = userStory;
    }
    public UserStory getUserStory() {
        return userStory;
    }
    public void setUserStory(UserStory userStory) {
        this.userStory = userStory;
    }

    @Override
    public void toonChatroom() {
        System.out.println("Chatroom voor taak: " + beschrijving);
        for (Bericht bericht : chatroom.getBerichten()) {
            System.out.println(bericht.getTijdstipString() + " - " + bericht.getAuteur().getNaam() + ": " + bericht.getTekst());
        }
    }




}
