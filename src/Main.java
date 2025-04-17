import java.util.scanner;

class Gebruiker {

    private String naam;
    private int id;

    public Gebruiker (String naam, int id) {
        this.naam = naam;
        this.id = id;
    }

    public void setNaam (String naam) {
        this.naam = naam;
    }
    public String getNaam () {
        return naam;
    }
    public void setId (int id) {
        this.id = id;
    }
    public int getId () {
        return id;
    }

}

class chatroom {
    
}



public class Main {
    public static void main(String[] args) {
        //TIP Press <shortcut actionId="ShowIntentionActions"/> with your caret at the highlighted text
        // to see how IntelliJ IDEA suggests fixing it.
        System.out.printf("Hello and welcome!");

        Gebruiker gebruiker = new Gebruiker (Stan, 984);
        System.out.println(gebruiker)

        for (int i = 1; i <= 5; i++) {
            //TIP Press <shortcut actionId="Debug"/> to start debugging your code. We have set one <icon src="AllIcons.Debugger.Db_set_breakpoint"/> breakpoint
            // for you, but you can always add more by pressing <shortcut actionId="ToggleLineBreakpoint"/>.
            System.out.println("i = " + i);
        }
    }
}