package ro.ase.semdam.domeniu;
import androidx.room.TypeConverter;
public enum Categorie {
    ELECTRONICE, CARTI, IMBRACAMINTE, ALTELE;


    @TypeConverter
    public static Categorie dinEticheta(String eticheta) {

        if (eticheta == null) {
            return null;
        }


        return switch (eticheta) {
            case "Electronice" -> ELECTRONICE;
            case "Carti" -> CARTI;
            case "Imbracaminte" -> IMBRACAMINTE;
            default -> ALTELE;
        };
    }

    @TypeConverter
    public static String etichetaDin(Categorie categorie) {
        if (categorie == null) {
            return null;
        }
        return switch (categorie) {
            case ELECTRONICE -> "Electronice";
            case CARTI -> "Carti";
            case IMBRACAMINTE -> "Imbracaminte";
            default -> "Altele";
        };
    }
}
