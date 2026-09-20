package com.shaunwalsh.compustocker.view;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.se.omapi.Session;
import android.telephony.SmsManager;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.shaunwalsh.compustocker.R;
import com.shaunwalsh.compustocker.model.Category;
import com.shaunwalsh.compustocker.model.InventoryItem;
import com.shaunwalsh.compustocker.services.DatabaseHelper;
import com.shaunwalsh.compustocker.services.DatabaseManager;
import com.shaunwalsh.compustocker.services.InventoryAdapter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import com.shaunwalsh.compustocker.session.SessionManager;
import com.shaunwalsh.compustocker.viewmodel.InventoryViewModel;


public class InventoryActivity extends AppCompatActivity implements InventoryAdapter.OnItemDeleteListener {

    private RecyclerView recyclerView;
    private InventoryAdapter adapter;

    private InventoryViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inventory);

        EdgeToEdge.enable(this);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        SessionManager session = new SessionManager(this);
        if (!session.isLoggedIn())
        {
            startActivity(new Intent(this, MainActivity.class));
            finish();
            return;
        }
        DatabaseManager.initialize(this);

        recyclerView = findViewById(R.id.recyclerViewInventory);
        FloatingActionButton fab = findViewById(R.id.fabAddItem);

        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        adapter = new InventoryAdapter(new ArrayList<>(), this);
        recyclerView.setAdapter(adapter);

        viewModel = new ViewModelProvider(this).get(InventoryViewModel.class);

        viewModel.getItems().observe(this, items -> adapter.replaceItems(items));

        viewModel.getMessage().observe(this, text -> {
            if (text != null && !text.isEmpty()) {
                Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
            }
        });

        viewModel.loadItems();

        fab.setOnClickListener(v -> showAddItemDialog());
        showSmsPermissionScreenIfNeeded();
    }

    private void showSmsPermissionScreenIfNeeded() {
        SharedPreferences prefs = getSharedPreferences("app_prefs", MODE_PRIVATE);
        boolean smsScreenShown = prefs.getBoolean("sms_screen_shown", false);

        if (!smsScreenShown) {
            showSmsPermissionDialog();
            prefs.edit().putBoolean("sms_screen_shown", true).apply();
        }
    }

    private void showSmsPermissionDialog() {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_sms_permission);
        dialog.setCancelable(false); // User must interact
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);

        TextView tvStatus = dialog.findViewById(R.id.tvStatus);
        Button btnRequest = dialog.findViewById(R.id.btnRequestPermission);
        Button btnTest = dialog.findViewById(R.id.btnTestNotification);
        Button btnClose = dialog.findViewById(R.id.btnClose);

        updatePermissionStatus(tvStatus);

        btnRequest.setOnClickListener(v -> {
            requestSmsPermission(tvStatus);
        });

        btnTest.setOnClickListener(v -> {
            sendTestSms(tvStatus);
        });

        btnClose.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }

    private void updatePermissionStatus(TextView tvStatus) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS)
                == PackageManager.PERMISSION_GRANTED) {
            tvStatus.setText("Permission Granted");
        } else {
            tvStatus.setText("Permission Not Granted");
        }
    }

    private void requestSmsPermission(TextView tvStatus) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.SEND_SMS},
                    101);
        } else {
            Toast.makeText(this, "Permission already granted", Toast.LENGTH_SHORT).show();
        }
    }

    private void sendTestSms(TextView tvStatus) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS)
                != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "Please grant SMS permission first", Toast.LENGTH_LONG).show();
            return;
        }

        try {
            SmsManager smsManager = SmsManager.getDefault();
            String phoneNumber = "5555555555"; // Change to your number for real testing
            String message = "CompuStocker Alert: Low stock detected!";

            smsManager.sendTextMessage(phoneNumber, null, message, null, null);
            Toast.makeText(this, "Test SMS sent!", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(this, "Failed to send SMS", Toast.LENGTH_LONG).show();
        }
    }



    @Override
    public void onDelete(InventoryItem item, int position) {
        viewModel.deleteItem(item);
    }

    private void showAddItemDialog() {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.add_item_dialog);
        Objects.requireNonNull(dialog.getWindow()).setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );

        EditText etName = dialog.findViewById(R.id.etItemName);
        EditText etPartNumber = dialog.findViewById(R.id.etPartNumber);
        EditText etManufacturer = dialog.findViewById(R.id.etManufacturer);
        EditText etQuantity = dialog.findViewById(R.id.etQuantity);
        EditText etMinQuantity = dialog.findViewById(R.id.etMinQuantity);
        EditText etPrice = dialog.findViewById(R.id.etPrice);
        EditText etDescription = dialog.findViewById(R.id.etDescription);
        Spinner spinnerCategory = dialog.findViewById(R.id.spinnerCategory);

        Button btnSave = dialog.findViewById(R.id.btnSave);
        Button btnCancel = dialog.findViewById(R.id.btnCancel);

        List<Category> categories = viewModel.getCategories();
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                categories.stream().map(Category::getName).collect(Collectors.toList())
        );
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategory.setAdapter(spinnerAdapter);

        btnSave.setOnClickListener(v -> {
            int categoryId = 0;
            int selected = spinnerCategory.getSelectedItemPosition();
            if (selected >= 0 && selected < categories.size()) {
                categoryId = categories.get(selected).getId();
            }

            boolean saved = viewModel.addItem(
                    etName.getText().toString(),
                    etPartNumber.getText().toString(),
                    etManufacturer.getText().toString(),
                    etDescription.getText().toString(),
                    etQuantity.getText().toString(),
                    etMinQuantity.getText().toString(),
                    etPrice.getText().toString(),
                    categoryId
            );

            if (saved) {
                dialog.dismiss();
            }
        });

        btnCancel.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }
}