package com.start.apps.pheezee.fragments;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;


import start.apps.pheezee.R;
import start.apps.pheezee.databinding.FragmentOnboardingBinding;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class OnBoardingFragment extends Fragment {

    private static String ARG_POSITION = "ARG_POSITION";

    private static String[] onBoardingTitles = {"Welcome to Pheezee", "Track Every Progress", "Set Targets, Stay Motivated", "Smart Session Reports"};

    private static String[] onBoardingDescriptions = {
            "The smart device for generating reports on physiotherapy sessions",
            "Pheezee monitors range of motion, EMG biofeedback and other session parameters",
            "Achieve faster recovery with gamification themes and daily targets",
            "Time-stamped reports with hospital logo and recovery status for easy tracking of progress"
    };

    public static OnBoardingFragment getInstance(Integer position){
        OnBoardingFragment onBoardingFragment = new OnBoardingFragment();
        Bundle args = new Bundle();
        args.putInt(OnBoardingFragment.ARG_POSITION, position);
        onBoardingFragment.setArguments(args);
        return onBoardingFragment;
    }

    private FragmentOnboardingBinding fragmentOnboardingBinding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        fragmentOnboardingBinding = FragmentOnboardingBinding.inflate(inflater, container, false);
        return fragmentOnboardingBinding.getRoot();
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Integer position = requireArguments().getInt(OnBoardingFragment.ARG_POSITION);
        fragmentOnboardingBinding.onBoardTitle.setText(OnBoardingFragment.onBoardingTitles[position]);
        fragmentOnboardingBinding.onBoardDescription.setText(OnBoardingFragment.onBoardingDescriptions[position]);
        InputStream imageStream = this.getResources().openRawResource(getOnBoardImageAssets().get(position));
        Bitmap bitmap = BitmapFactory.decodeStream(imageStream);
        fragmentOnboardingBinding.imageViewOnBoardFragment.setImageBitmap(bitmap);


    }

    private List<Integer> getOnBoardImageAssets(){
        List<Integer> imageAssets = new ArrayList<Integer>();
        imageAssets.add(R.raw.on_boarding_screen_1);
        imageAssets.add(R.raw.on_boarding_screen_2);
        imageAssets.add(R.raw.on_boarding_screen_3);
        imageAssets.add(R.raw.on_boarding_screen_4);
        return imageAssets;
    }
}