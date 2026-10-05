package com.example.sprint1;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.sprint1.databinding.ActivityMainBinding;

/**
 * The activity is a shell: it hosts the navigation graph and nothing else.
 *
 * Its theme is NoActionBar, the nav host fragment fills the whole window, and
 * MainActivity deliberately never touches window insets. Each destination screen
 * owns its header and pads the status bar itself - see HomeFragment.
 */
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Draw behind the status and navigation bars.
        EdgeToEdge.enable(this);

        ActivityMainBinding binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
    }
}
