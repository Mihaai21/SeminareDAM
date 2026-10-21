package ro.ase.semdam;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.IntentCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import ro.ase.semdam.databinding.ActivityAddBinding;
import ro.ase.semdam.domeniu.Anunt;
import ro.ase.semdam.domeniu.Categorie;

public class AddActivity extends AppCompatActivity {

    private ActivityAddBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        binding = ActivityAddBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ViewCompat.setOnApplyWindowInsetsListener(binding.main, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        String[] categorii = {"Electronice", "Carti", "Imbracaminte", "Altele"};

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, categorii);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        binding.spCategorie.setAdapter(adapter);

        Anunt existent = IntentCompat.getSerializableExtra(getIntent(), "element", Anunt.class);
        int pozSelectie = getIntent().getIntExtra("pozitie", -1);

        if(existent != null)
        {
            binding.etTitlu.setText(existent.getTitlu());
            binding.etDescriere.setText(existent.getDescriere());
            binding.etPret.setText(String.valueOf(existent.getPret()));
            binding.etCantitate.setText(String.valueOf(existent.getCantitate()));
            binding.spCategorie.setSelection(adapter.getPosition(Categorie.etichetaDin(existent.getCategorie())));
            if(existent.isNou())
                binding.rgStare.check(binding.rbNou.getId());
            else
                binding.rgStare.check(binding.rbFolosit.getId());
            binding.etDataPublicare.setText(existent.getDataPublicare().format(DateTimeFormatter.ISO_LOCAL_DATE));
        }

        binding.btnSalveaza.setOnClickListener(v -> {
            String titlu = binding.etTitlu.getText().toString().trim();
            if(titlu.isEmpty())
            {
                binding.etTitlu.setError("Titlul nu poate fi gol!");
                return;
            }


            double pret;
            try {
                pret = Double.parseDouble(binding.etPret.getText().toString().trim());
            } catch (NumberFormatException e) {
                binding.etPret.setError("Pretul trebuie sa fie un numar valid!");
                return;
            }

            if(pret <= 0)
            {
                binding.etPret.setError("Pretul trebuie sa fie strict pozitiv!");
                return;
            }


            int cantitate;
            try {
                cantitate = Integer.parseInt(binding.etCantitate.getText().toString().trim());
            } catch (NumberFormatException e) {
                binding.etCantitate.setError("Cantitatea trebuie sa fie un numar intreg!");
                return;
            }
            if(cantitate < 1 || cantitate > 100)
            {
                binding.etCantitate.setError("Cantitatea trebuie sa fie intre 1 si 100!");
                return;
            }

            LocalDate dataPublicare;
            try {
                dataPublicare = LocalDate.parse(binding.etDataPublicare.getText().toString().trim(), DateTimeFormatter.ISO_LOCAL_DATE);
            } catch (DateTimeParseException e) {
                binding.etDataPublicare.setError("Data trebuie sa fie yyyy-MM-dd!");
                return;
            }
            if(dataPublicare.isAfter(LocalDate.now()))
            {
                binding.etDataPublicare.setError("Data nu poate fi in viitor!");
                return;
            }




            String descriere = binding.etDescriere.getText().toString().trim();
            Categorie categorie = Categorie.dinEticheta(binding.spCategorie.getSelectedItem().toString());
            int idBifat = binding.rgStare.getCheckedRadioButtonId();
            if(idBifat == -1)
            {
                Toast.makeText(this, "Selectati o stare!", Toast.LENGTH_SHORT).show();
                return;
            }
            boolean nou = idBifat == binding.rbNou.getId();
            Anunt anunt = new Anunt(titlu, descriere, pret, cantitate, categorie, nou, dataPublicare);
            Log.d("AddActivity", anunt.toString());
            //Toast.makeText(this, "Salvat: " + anunt.toString(), Toast.LENGTH_SHORT).show();

            Intent data = new Intent();
            data.putExtra("element", anunt);
            setResult(RESULT_OK, data);
            if(pozSelectie >=0)
                data.putExtra("pozitie", pozSelectie);
            finish();
        });

    }
}
