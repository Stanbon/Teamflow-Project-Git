import java.time.LocalDateTime;
import java.time.ZonedDateTime;

public class Bericht {
    private int b_id;
    private String tekst;
    private Gebruiker auteur;
    private ZonedDateTime tijdstip;
    private int chat_id;

    public Bericht(int b_id, String tekst, Gebruiker auteur, ZonedDateTime tijdstip, int chat_id) {
        this.b_id = b_id;
        this.tekst = tekst;
        this.auteur = auteur;
        this.tijdstip = tijdstip;
        this.chat_id = chat_id;
    }

    public int getB_id() {
        return b_id;
    }
    public void setB_id(int b_id) {
        this.b_id = b_id;
    }
    public String getTekst() {
        return tekst;
    }

    public void setTekst(String tekst) {
        this.tekst = tekst;
    }
    public Gebruiker getAuteur() {
        return auteur;
    }
    public void setAuteur(Gebruiker auteur) {
        this.auteur = auteur;
    }
    public ZonedDateTime getTijdstip() {
        return tijdstip;
    }
    public void setTijdstip(ZonedDateTime tijdstip) {
        this.tijdstip = tijdstip;
    }
    public int getChat_id() {
        return chat_id;
    }
    public void setChat_id(int chat_id) {
        this.chat_id = chat_id;
    }
}
