package ro.ase.semdam.domeniu;

import java.io.Serializable;
import java.time.LocalDate;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.Ignore;

@Entity(tableName = "anunturi")
public class Anunt implements Serializable {

    @PrimaryKey(autoGenerate = true)
    private long id;

    private String titlu;
    private String descriere;
    private double pret;
    private int cantitate;
    private Categorie categorie;
    private boolean nou;
    private LocalDate dataPublicare;

    public String getTitlu() {
        return titlu;
    }

    public void setTitlu(String titlu) {
        this.titlu = titlu;
    }

    public String getDescriere() {
        return descriere;
    }

    public void setDescriere(String descriere) {
        this.descriere = descriere;
    }

    public double getPret() {
        return pret;
    }

    public void setPret(double pret) {
        this.pret = pret;
    }

    public int getCantitate() {
        return cantitate;
    }

    public void setCantitate(int cantitate) {
        this.cantitate = cantitate;
    }

    public Categorie getCategorie() {
        return categorie;
    }

    public void setCategorie(Categorie categorie) {
        this.categorie = categorie;
    }

    public boolean isNou() {
        return nou;
    }

    public void setNou(boolean nou) {
        this.nou = nou;
    }

    public LocalDate getDataPublicare() {
        return dataPublicare;
    }

    public void setDataPublicare(LocalDate dataPublicare) {
        this.dataPublicare = dataPublicare;
    }


    @Ignore
    public Anunt(String titlu, String descriere, double pret, int cantitate, Categorie categorie, boolean nou, LocalDate dataPublicare) {
        this.titlu = titlu;
        this.descriere = descriere;
        this.pret = pret;
        this.cantitate = cantitate;
        this.categorie = categorie;
        this.nou = nou;
        this.dataPublicare = dataPublicare;
    }

    public Anunt(long id, String titlu, String descriere,
                 double pret, int cantitate, Categorie categorie,
                 boolean nou, LocalDate dataPublicare) {

        this.id = id;
        this.titlu = titlu;
        this.descriere = descriere;
        this.pret = pret;
        this.cantitate = cantitate;
        this.categorie = categorie;
        this.nou = nou;
        this.dataPublicare = dataPublicare;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }


    @Override
    public String toString() {
        return "Anunt{" +
                "titlu='" + titlu + '\'' +
                ", descriere='" + descriere + '\'' +
                ", pret=" + pret +
                ", cantitate=" + cantitate +
                ", categorie=" + categorie +
                ", nou=" + nou +
                ", dataPublicare=" + dataPublicare +
                '}';
    }
}
