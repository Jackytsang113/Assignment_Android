package com.example.assignment_300cem.Fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.assignment_300cem.LocaleHelper;
import com.example.assignment_300cem.LoginActivity;
import com.example.assignment_300cem.MainActivity;
import com.example.assignment_300cem.R;

import java.util.Locale;

public class SettingsFragment extends Fragment {

    TextView language_dialog;
    boolean lang_selected = true;
    Context context;
    Resources resources;

    public static final String[] languages = {"English", "中文"};

    public SettingsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        language_dialog = view.findViewById(R.id.dialog_language);


        language_dialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int checkedItem;

                if(lang_selected)
                    checkedItem = 0;
                else
                    checkedItem = 1;

                final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

                builder.setTitle(getResources().getString(R.string.select_language))
                        .setSingleChoiceItems(languages, checkedItem, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                language_dialog.setText(languages[i]);

                                if(languages[i].equals("English")){
                                    setLocal(getActivity(), "en");

                                }else if(languages[i].equals("中文")){
                                    setLocal(getActivity(), "zh");
                                }
                            }
                        }).setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
                builder.create().show();
            }
        });

        return view;
    }

    public  void setLocal(Activity activity, String langCode){
        Locale locale = new Locale(langCode);
        locale.setDefault(locale);

        Resources resources = activity.getResources();
        Configuration config = resources.getConfiguration();
        config.setLocale(locale);
        resources.updateConfiguration(config,resources.getDisplayMetrics());
    }
}