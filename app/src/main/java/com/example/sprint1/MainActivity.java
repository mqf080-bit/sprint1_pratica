package com.example.sprint1;

import android.content.res.ColorStateList;
import android.os.Bundle;
import android.util.TypedValue;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;

import com.example.sprint1.databinding.ActivityMainBinding;
import com.google.android.material.snackbar.Snackbar;

/**
 * Home screen that mimics the "Welcome / Our Services / Appointment" reference design.
 *
 * The screen is edge-to-edge: the header gradient is drawn behind the status bar and the
 * bottom navigation bar is padded with the system gesture inset at runtime.
 */
public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // The design is light, so force dark system-bar icons.
        WindowInsetsControllerCompat controller =
                WindowCompat.getInsetsController(getWindow(), getWindow().getDecorView());
        controller.setAppearanceLightStatusBars(true);
        controller.setAppearanceLightNavigationBars(true);

        applyWindowInsets();
        bindClicks();
    }

    private void applyWindowInsets() {
        final int horizontal = dp(20);
        final int headerTop = dp(18);
        final int navPadding = dp(6);

        ViewCompat.setOnApplyWindowInsetsListener(binding.getRoot(), (view, insets) -> {
            Insets bars = insets.getInsets(
                    WindowInsetsCompat.Type.systemBars() | WindowInsetsCompat.Type.displayCutout());

            // Header content sits below the status bar; its gradient still paints behind it.
            binding.headerContent.setPadding(horizontal, bars.top + headerTop, horizontal, 0);

            // Keep the bottom navigation above the gesture / navigation bar.
            binding.bottomNav.setPadding(
                    navPadding, navPadding, navPadding, bars.bottom + navPadding);

            return insets;
        });
    }

    private void bindClicks() {
        binding.urgentCare.setOnClickListener(v -> snack(R.string.toast_urgent_care));
        binding.bell.setOnClickListener(v -> snack(R.string.cd_notifications));

        binding.serviceConsultation.setOnClickListener(v -> snack(R.string.service_consultation));
        binding.serviceMedicines.setOnClickListener(v -> snack(R.string.service_medicines));
        binding.serviceAmbulance.setOnClickListener(v -> snack(R.string.service_ambulance));

        binding.seeAll.setOnClickListener(v -> snack(R.string.toast_see_all));
        binding.appointmentCard.setOnClickListener(v -> snack(R.string.toast_appointment));

        binding.navHome.setOnClickListener(v -> {
            selectNav(0);
            snack(R.string.cd_home);
        });
        binding.navRecords.setOnClickListener(v -> {
            selectNav(1);
            snack(R.string.toast_records);
        });
        binding.navAdd.setOnClickListener(v -> snack(R.string.toast_new_appointment));
        binding.navChat.setOnClickListener(v -> {
            selectNav(3);
            snack(R.string.toast_chat);
        });
        binding.navAccount.setOnClickListener(v -> {
            selectNav(4);
            snack(R.string.toast_account);
        });
    }

    /** Highlights the selected bottom-navigation icon and dims the others. */
    private void selectNav(int selectedIndex) {
        int active = ContextCompat.getColor(this, R.color.brand_blue);
        int inactive = ContextCompat.getColor(this, R.color.nav_icon);

        binding.iconHome.setImageTintList(
                ColorStateList.valueOf(selectedIndex == 0 ? active : inactive));
        binding.iconRecords.setImageTintList(
                ColorStateList.valueOf(selectedIndex == 1 ? active : inactive));
        binding.iconChat.setImageTintList(
                ColorStateList.valueOf(selectedIndex == 3 ? active : inactive));
        binding.iconAccount.setImageTintList(
                ColorStateList.valueOf(selectedIndex == 4 ? active : inactive));
    }

    private void snack(int messageRes) {
        Snackbar.make(binding.getRoot(), getString(messageRes), Snackbar.LENGTH_SHORT)
                .setAnchorView(binding.bottomNav)
                .show();
    }

    private int dp(int value) {
        return Math.round(TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, value, getResources().getDisplayMetrics()));
    }
}
