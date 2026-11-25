package ro.ase.semdam.domeniu;

public enum Categorie {
    ELECTRONICE, CARTI, IMBRACAMINTE, ALTELE;

    public static Categorie dinEticheta(String eticheta) {
        return switch (eticheta) {
            case "Electronice" -> ELECTRONICE;
            case "Carti" -> CARTI;
            case "Imbracaminte" -> IMBRACAMINTE;
            default -> ALTELE;
        };
    }

    public static String etichetaDin(Categorie categorie) {
        return switch (categorie) {
            case ELECTRONICE -> "Electronice";
            case CARTI -> "Carti";
            case IMBRACAMINTE -> "Imbracaminte";
            default -> "Altele";
        };
    }
}
