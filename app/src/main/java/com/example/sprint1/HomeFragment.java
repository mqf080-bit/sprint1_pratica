package com.example.sprint1;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;
import androidx.fragment.app.Fragment;

import com.example.sprint1.databinding.FragmentHomeBinding;

/**
 * Home screen. Step 1 only builds the header band, but the fragment already does
 * the plumbing every screen in this app will need:
 *
 *  - viewBinding  -> typed references to the XML views (fragment_home.xml => FragmentHomeBinding).
 *                    The binding class name comes from the file name, and the field names
 *                    come from the android:id values.
 *  - edge-to-edge -> the screen draws behind the status bar, and we pad the status-bar
 *                    height back in as real padding.
 */
public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        applyWindowInsets(view);

        binding.btnNotifications.setOnClickListener(v -> {
            // Step 1: no destination yet. Step 5 will navigate to a notifications screen.
        });
    }

    /**
     * The theme is edge-to-edge, so the layout starts at y = 0 and the status bar
     * floats on top of it. We ask the system how big the bars are and turn that into
     * padding on the root view: the content is pushed down, while the pink background
     * still paints underneath the status bar.
     */
    private void applyWindowInsets(@NonNull View root) {
        final int basePaddingTop = root.getPaddingTop();

        ViewCompat.setOnApplyWindowInsetsListener(root, (v, windowInsets) -> {
            Insets bars = windowInsets.getInsets(
                    WindowInsetsCompat.Type.systemBars() | WindowInsetsCompat.Type.displayCutout());
            v.setPadding(bars.left, bars.top + basePaddingTop, bars.right, bars.bottom);
            return windowInsets;
        });

        // The gradient is pale, so the status-bar icons must stay dark.
        WindowInsetsControllerCompat controller =
                new WindowInsetsControllerCompat(requireActivity().getWindow(), root);
        controller.setAppearanceLightStatusBars(true);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // A Fragment outlives its view: drop the binding so the old view tree can be collected.
        binding = null;
    }
}
