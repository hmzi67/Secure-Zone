package io.github.hmzi67.securezone.Activities;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import io.github.hmzi67.securezone.R;
import io.github.hmzi67.securezone.databinding.ActivityEditProfileBinding;

public class EditProfileActivity extends AppCompatActivity {
    ActivityEditProfileBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEditProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        init();
    }
    private void init() {

        // Date of Birth
        binding.dateOfBirth.setOnClickListener(view -> {
            @SuppressLint("SetTextI18n") DatePickerDialog datePickerDialog = new DatePickerDialog(this, (datePicker, year, month, dayOfMonth) -> {
                binding.dateOfBirth.setText(dayOfMonth + "/" + (month + 1) + "/" + year);
            }, 2000, 0, 1);
            datePickerDialog.show();
        });


        // Back Button
        binding.goBack.setOnClickListener(view -> onBackPressed());

        // Select Gender
        // get reference to the string array that we just created
        String[] gender = getResources().getStringArray(R.array.gender);
        // create an array adapter and pass the required parameter
        // in our case pass the context, drop down layout , and array
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this, R.layout.gender_modal, gender);
        // get reference to the autocomplete text view
        AutoCompleteTextView autocompleteTV = binding.selectGender;
        // set adapter to the autocomplete tv to the arrayAdapter
        autocompleteTV.setAdapter(arrayAdapter);

    }
}