
package ro.ase.semdam;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.IntentCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import ro.ase.semdam.adapters.CustomAdapter;
import ro.ase.semdam.date.AnuntDao;
import ro.ase.semdam.date.AppDatabase;
import ro.ase.semdam.databinding.ActivityMainBinding;
import ro.ase.semdam.domeniu.Anunt;
import ro.ase.semdam.json.IncarcareJson;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "Main";

    private ActivityMainBinding binding;

    private ActivityResultLauncher<Intent> addLauncher;

    private ExecutorService executor;
    private Handler mainHandler;

    private final List<Anunt> anunturi = new ArrayList<>();
    private ArrayAdapter<Anunt> adapter;

    // MENIU
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.meniu_principal, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        int id = item.getItemId();

        // CURS BNR
        if (id == R.id.action_bnr) {
            startActivity(new Intent(this, BnrActivity.class));
            return true;
        }

        // DESPRE
        if (id == R.id.action_despre) {
            Toast.makeText(this,
                    "Aplicatie seminar DAM 1086",
                    Toast.LENGTH_LONG).show();
            return true;
        }

        // TOTAL STOC
        if (id == R.id.action_total_stoc) {

            int total = 0;

            for (Anunt a : anunturi) {
                total += a.getCantitate();
            }

            Toast.makeText(this,
                    String.valueOf(total),
                    Toast.LENGTH_SHORT).show();

            return true;
        }

        // IMPORT JSON + SALVARE IN ROOM
        if (id == R.id.action_json) {

            executor.execute(() -> {
                try {

                    String text = IncarcareJson.descarca();

                    List<Anunt> citite =
                            IncarcareJson.dinText(text);

                    AnuntDao dao = AppDatabase
                            .getInstance(this)
                            .anuntDao();

                    // Inlocuim datele vechi cu cele importate
                    List<Anunt> vechi = dao.getAll();

                    for (Anunt a : vechi) {
                        dao.delete(a);
                    }

                    // Salvam anunturile JSON in Room
                    for (Anunt a : citite) {
                        long idNou = dao.insert(a);
                        a.setId(idNou);
                    }

                    // Calculam numarul anunturilor noi
                    int nrNoi = 0;

                    for (Anunt a : citite) {
                        if (a.isNou()) {
                            nrNoi++;
                        }
                    }

                    // Salvam statisticile
                    SharedPreferences prefs =
                            getSharedPreferences(
                                    "setari_semdam",
                                    MODE_PRIVATE);

                    prefs.edit()
                            .putInt("nr_incarcate", citite.size())
                            .putInt("nr_noi", nrNoi)
                            .apply();

                    // Reincarcam lista din baza de date
                    actualizeazaLista(dao);

                    mainHandler.post(() -> {
                        Toast.makeText(this,
                                "Incarcat: " + citite.size(),
                                Toast.LENGTH_SHORT).show();
                    });

                } catch (Exception e) {

                    Log.e(TAG, "Eroare import JSON", e);

                    mainHandler.post(() -> {
                        Toast.makeText(this,
                                "Eroare JSON: " + e.getMessage(),
                                Toast.LENGTH_LONG).show();
                    });
                }
            });

            return true;
        }

        // ULTIMA ACTIVITATE
        if (id == R.id.action_ultima) {
            startActivity(new Intent(this, SecondActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);

        binding = ActivityMainBinding.inflate(
                getLayoutInflater());

        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);

        executor = Executors.newSingleThreadExecutor();
        mainHandler = new Handler(Looper.getMainLooper());

        ViewCompat.setOnApplyWindowInsetsListener(
                binding.main, (v, insets) -> {

                    Insets systemBars = insets.getInsets(
                            WindowInsetsCompat.Type.systemBars());

                    v.setPadding(
                            systemBars.left,
                            systemBars.top,
                            systemBars.right,
                            systemBars.bottom);

                    return insets;
                });

        Log.d(TAG, "s-a apelat onCreate");

        // SHAREDPREFERENCES - SEMINAR 7
        SharedPreferences prefsDeschidere =
                getSharedPreferences(
                        "setari_semdam",
                        MODE_PRIVATE);

        int nrNoiSalvat =
                prefsDeschidere.getInt("nr_noi", 0);

        Toast.makeText(this,
                "Ultima incarcare: " + nrNoiSalvat,
                Toast.LENGTH_SHORT).show();

        // ADAPTER
        adapter = new CustomAdapter(
                this,
                android.R.layout.simple_list_item_1,
                anunturi);

        binding.lvAnunturi.setAdapter(adapter);

        // ACTIVITY RESULT - ADAUGARE SI EDITARE
        addLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {

                    if (result.getResultCode() == RESULT_OK
                            && result.getData() != null) {

                        Anunt anuntPrimit =
                                IntentCompat.getSerializableExtra(
                                        result.getData(),
                                        "element",
                                        Anunt.class);

                        if (anuntPrimit != null) {

                            int pozSelectie =
                                    result.getData().getIntExtra(
                                            "pozitie", -1);

                            executor.execute(() -> {

                                AnuntDao dao = AppDatabase
                                        .getInstance(this)
                                        .anuntDao();

                                // EDITARE
                                if (pozSelectie >= 0) {

                                    dao.update(anuntPrimit);

                                } else {

                                    // ADAUGARE
                                    long idNou =
                                            dao.insert(anuntPrimit);

                                    anuntPrimit.setId(idNou);
                                }

                                // CITIM DIN NOU DIN ROOM
                                actualizeazaLista(dao);
                            });
                        }
                    }
                });

        // BUTON FAB - ADAUGARE
        binding.fabAdauga.setOnClickListener(view -> {

            Intent intent =
                    new Intent(this, AddActivity.class);

            addLauncher.launch(intent);
        });

        // CLICK PE ANUNT - EDITARE
        binding.lvAnunturi.setOnItemClickListener(
                (parent, view, position, id) -> {

                    Anunt selectat = anunturi.get(position);

                    Intent intent =
                            new Intent(this, AddActivity.class);

                    intent.putExtra("element", selectat);
                    intent.putExtra("pozitie", position);

                    addLauncher.launch(intent);
                });

        // CITIRE INITIALA DIN ROOM
        executor.execute(() -> {

            AnuntDao dao = AppDatabase
                    .getInstance(this)
                    .anuntDao();

            actualizeazaLista(dao);
        });

        // LONG CLICK - STERGERE
        binding.lvAnunturi.setOnItemLongClickListener(
                (parent, view, position, id) -> {

                    Anunt deSters = anunturi.get(position);

                    new AlertDialog.Builder(this)
                            .setTitle("Stergere")
                            .setMessage("Stergeti elementul?")
                            .setPositiveButton("Da", (d, w) -> {

                                executor.execute(() -> {

                                    AnuntDao dao = AppDatabase
                                            .getInstance(this)
                                            .anuntDao();

                                    dao.delete(deSters);

                                    actualizeazaLista(dao);
                                });

                            })
                            .setNegativeButton("Nu", null)
                            .show();

                    return true;
                });
    }

    // REINCARCA LISTA DIN BAZA DE DATE
    // Se apeleaza doar de pe executor
    private void actualizeazaLista(AnuntDao dao) {

        List<Anunt> dinDb = dao.getAll();

        mainHandler.post(() -> {

            anunturi.clear();
            anunturi.addAll(dinDb);

            adapter.notifyDataSetChanged();
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "onStart");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "dar onResume?");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "onStop");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy????");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.d(TAG, "onRestart");
    }
}
