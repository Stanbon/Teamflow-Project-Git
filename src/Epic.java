

import java.util.Scanner;

public class Epic extends ScrumItem{


    public Epic(int id, String beschrijving, String trelloID, Chatroom chatroom) {
        super(id, beschrijving, trelloID, chatroom);
    }

    public void setBeschrijving(String beschrijving) {
        super.setBeschrijving(beschrijving);
    }

    @Override
    public void toonChatroom() {
        System.out.println("Chatroom voor epic: " + beschrijving);
        for (Bericht bericht : chatroom.getBerichten()) {

            System.out.println(bericht.getTijdstipString() + " - " + bericht.getAuteur().getNaam() + ": " + bericht.getTekst());
        }

    }


    @Override
   public void voegUserToe(Chatroom chatroom){
        // Implementatie voor het toevoegen van een gebruiker aan de chat
        System.out.println("Gebruiker toegevoegd aan epic: " + beschrijving);
    }
}
