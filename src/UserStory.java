public class UserStory extends ScrumItem{

    private Epic epic;


    protected UserStory(int id, String beschrijving, String trelloID, Chatroom chatroom) {
        super(id, beschrijving, trelloID, chatroom);
    }

    public Epic getEpic() {
        return epic;
    }
    public void setEpic(Epic epic) {
        this.epic = epic;
    }

    @Override
    public void toonChatroom() {
        System.out.println("Chatroom voor user story: " + beschrijving);
        for (Bericht bericht : chatroom.getBerichten()) {
            System.out.println(bericht.getTijdstipString() + " - " + bericht.getAuteur().getNaam() + ": " + bericht.getTekst());
        }
    }


}
