package ro.ase.semdam;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import ro.ase.semdam.bnr.BnrClient;
import ro.ase.semdam.bnr.CursValutar;
import ro.ase.semdam.databinding.ActivityBnrBinding;

public class BnrActivity extends AppCompatActivity {

    private ActivityBnrBinding binding;

    private final ExecutorService executor =
            Executors.newSingleThreadExecutor();

    private final Handler mainHandler =
            new Handler(Looper.getMainLooper());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        binding = ActivityBnrBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ViewCompat.setOnApplyWindowInsetsListener(binding.main, (v, insets) -> {
            Insets systemBars =
                    insets.getInsets(WindowInsetsCompat.Type.systemBars());

            v.setPadding(
                    systemBars.left,
                    systemBars.top,
                    systemBars.right,
                    systemBars.bottom
            );

            return insets;
        });

        binding.btnActualizeaza.setOnClickListener(v -> {
            binding.btnActualizeaza.setEnabled(false);

            executor.execute(() -> {
                try {
                    List<CursValutar> cursuri = BnrClient.descarca();

                    mainHandler.post(() -> {
                        for (CursValutar curs : cursuri) {
                            if ("EUR".equals(curs.getMoneda())) {
                                binding.tvEur.setText(
                                        "EUR: " + curs.getValoare()
                                );
                            } else if ("USD".equals(curs.getMoneda())) {
                                binding.tvUsd.setText(
                                        "USD: " + curs.getValoare()
                                );
                            }
                        }
                    });
                } catch (Exception e) {
                    mainHandler.post(() -> {
                        Toast.makeText(
                                BnrActivity.this,
                                "Eroare rețea: " + e.getMessage(),
                                Toast.LENGTH_LONG
                        ).show();
                    });
                } finally {
                    mainHandler.post(() ->
                            binding.btnActualizeaza.setEnabled(true)
                    );
                }
            });
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        executor.shutdown();
    }
}