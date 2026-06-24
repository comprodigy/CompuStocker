package com.shaunwalsh.compustocker.controller;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
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
import java.util.stream.Collectors;

public class InventoryActivity extends AppCompatActivity implements InventoryAdapter.OnItemDeleteListener {

    private RecyclerView recyclerView;
    private InventoryAdapter adapter;
    private List<InventoryItem> itemList;

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

        DatabaseManager.initialize(this);
        DatabaseManager.seedInitialData();

        recyclerView = findViewById(R.id.recyclerViewInventory);
        FloatingActionButton fab = findViewById(R.id.fabAddItem);

        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));

        itemList = new ArrayList<>();
        adapter = new InventoryAdapter(itemList, this);
        recyclerView.setAdapter(adapter);

        loadInventoryFromDatabase();

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

    private void loadInventoryFromDatabase() {
        itemList.clear();
        itemList.addAll(DatabaseManager.getDbHelper().getAllItems());

        //If database is empty, add some sample items
        if (itemList.isEmpty()) {
            addSampleItemsToDatabase();
            itemList.addAll(DatabaseManager.getDbHelper().getAllItems());
        }

        adapter.notifyDataSetChanged();
    }

    private void addSampleItemsToDatabase() {
        DatabaseHelper db = DatabaseManager.getDbHelper();

        InventoryItem item1 = new InventoryItem();
        item1.setName("Corsair Vengeance 32GB DDR5");
        item1.setPartNumber("CMK32GX5M2B5600C36");
        item1.setManufacturer("Corsair");
        item1.setQuantity(24);
        item1.setPrice(129.99);
        item1.setCategoryId(1);
        item1.setMinQuantity(5);
        db.addInventoryItem(item1);

        InventoryItem item2 = new InventoryItem();
        item2.setName("AMD Ryzen 9 9950X3D");
        item2.setPartNumber("RYZEN99950X3D");
        item2.setManufacturer("AMD");
        item2.setQuantity(8);
        item2.setPrice(799.99);
        item2.setCategoryId(2);
        item2.setMinQuantity(3);
        db.addInventoryItem(item2);

        InventoryItem item3 = new InventoryItem();
        item3.setName("Nvidia RTX 5090 32GB");
        item3.setPartNumber("NVDA5090DDR732GB");
        item3.setManufacturer("NVIDIA");
        item3.setQuantity(2);
        item3.setPrice(2799.99);
        item3.setCategoryId(3);
        item3.setMinQuantity(1);
        db.addInventoryItem(item3);
    }

    @Override
    public void onDelete(InventoryItem item, int position) {
        DatabaseManager.getDbHelper().deleteItem(item.getId());
        adapter.removeItem(position);
        Toast.makeText(this, item.getName() + " deleted", Toast.LENGTH_SHORT).show();
    }

    private void showAddItemDialog() {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.add_item_dialog);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);

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

        // Populate Category Spinner
        List<Category> categories = DatabaseManager.getDbHelper().getAllCategories();
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item,
                categories.stream().map(Category::getName).collect(Collectors.toList()));
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategory.setAdapter(adapter);

        btnSave.setOnClickListener(v -> {
            String name = etName.getText().toString().trim();
            String partNumber = etPartNumber.getText().toString().trim();
            String manufacturer = etManufacturer.getText().toString().trim();
            String desc = etDescription.getText().toString().trim();

            int quantity = 0, minQuantity = 0, categoryId = 0;
            double price = 0.0;

            try {
                quantity = Integer.parseInt(etQuantity.getText().toString().trim());
            } catch (Exception e) {
            }
            try {
                minQuantity = Integer.parseInt(etMinQuantity.getText().toString().trim());
            } catch (Exception e) {
            }
            try {
                price = Double.parseDouble(etPrice.getText().toString().trim());
            } catch (Exception e) {
            }

            // Get selected category ID
            if (spinnerCategory.getSelectedItemPosition() >= 0) {
                categoryId = categories.get(spinnerCategory.getSelectedItemPosition()).getId();
            }

            if (name.isEmpty() || partNumber.isEmpty()) {
                Toast.makeText(this, "Name and Part Number are required", Toast.LENGTH_SHORT).show();
                return;
            }

            InventoryItem newItem = new InventoryItem();
            newItem.setName(name);
            newItem.setPartNumber(partNumber);
            newItem.setManufacturer(manufacturer);
            newItem.setQuantity(quantity);
            newItem.setMinQuantity(minQuantity);
            newItem.setPrice(price);
            newItem.setCategoryId(categoryId);
            newItem.setDescription(desc);

            long newId = DatabaseManager.getDbHelper().addInventoryItem(newItem);
            newItem.setId((int) newId);

            loadInventoryFromDatabase();

            Toast.makeText(this, "Item added successfully!", Toast.LENGTH_SHORT).show();
            dialog.dismiss();
        });

        btnCancel.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }
}