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
            System.out.println(bericht.getTijdstip() + " - " + bericht.getAuteur().getNaam() + ": " + bericht.getTekst());
        }
    }

    @Override
    public void voegUserToe(Chatroom chatroom) {
        // Implementatie voor het toevoegen van een gebruiker aan de user story
        System.out.println("Gebruiker toegevoegd aan user story: " + beschrijving);
    }
}
