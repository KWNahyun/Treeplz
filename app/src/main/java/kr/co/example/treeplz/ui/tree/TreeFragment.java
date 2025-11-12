package kr.co.example.treeplz.ui.tree;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;

import kr.co.example.treeplz.R;
import kr.co.example.treeplz.data.viewmodel.UsageViewModel;
import kr.co.example.treeplz.view.TreeView;

public class TreeFragment extends Fragment {

    private UsageViewModel usageViewModel;
    private TreeView treeView;
    private float x1, x2;
    static final int MIN_DISTANCE = 150;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_tree, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        treeView = view.findViewById(R.id.tree_view);
        usageViewModel = new ViewModelProvider(requireActivity()).get(UsageViewModel.class);

        usageViewModel.getTreeProgress().observe(getViewLifecycleOwner(), progress -> {
            if (progress != null) {
                treeView.setTreeState(progress);
            }
        });

        view.setOnTouchListener((v, event) -> {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    x1 = event.getX();
                    break;
                case MotionEvent.ACTION_UP:
                    x2 = event.getX();
                    float deltaX = x2 - x1;
                    if (Math.abs(deltaX) > MIN_DISTANCE) {
                        // Left swipe
                        if (x2 < x1) {
                            NavHostFragment.findNavController(this).navigate(R.id.action_treeFragment_to_usageFragment);
                        }
                    }
                    break;
            }
            return true; // Consume the touch event
        });
    }
}