    public class Gebruiker {
        private int id;
        private String naam;

        public Gebruiker(int id, String naam) {
            this.naam = naam;
            this.id = id;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getNaam() {
            return naam;
        }

        public void setNaam(String naam) {
            this.naam = naam;
        }
    }
