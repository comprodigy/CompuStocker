package com.shaunwalsh.compustocker.controller;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.shaunwalsh.compustocker.R;

public class SMSNotificationActivity extends AppCompatActivity {

    private static final int SMS_PERMISSION_CODE = 101;
    private TextView tvStatus, tvLog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_smsnotification);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        tvStatus = findViewById(R.id.tvStatus);
        tvLog = findViewById(R.id.tvLog);

        Button btnRequest = findViewById(R.id.btnRequestPermission);
        Button btnTest = findViewById(R.id.btnTestNotification);

        updatePermissionStatus();

        btnRequest.setOnClickListener(v -> requestSmsPermission());

        btnTest.setOnClickListener(v -> sendTestSms());
    }

    private void updatePermissionStatus() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS)
                == PackageManager.PERMISSION_GRANTED) {
            tvStatus.setText("Permission Status: Granted");
        } else {
            tvStatus.setText("Permission Status: Not Granted");
        }
    }

    private void requestSmsPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.SEND_SMS},
                    SMS_PERMISSION_CODE);
        } else {
            Toast.makeText(this, "Permission already granted", Toast.LENGTH_SHORT).show();
        }
    }

    private void sendTestSms() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS)
                != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "Please grant SMS permission first", Toast.LENGTH_LONG).show();
            return;
        }

        try {
            SmsManager smsManager = SmsManager.getDefault();
            String phoneNumber = "5555555555";
            String message = "CompuStocker Alert: Low stock on RTX 5090! Current qty: 2";

            smsManager.sendTextMessage(phoneNumber, null, message, null, null);

            appendLog("Test SMS sent successfully!");
            Toast.makeText(this, "Test SMS sent!", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            appendLog("Failed to send SMS: " + e.getMessage());
            Toast.makeText(this, "Failed to send SMS", Toast.LENGTH_LONG).show();
        }
    }

    private void appendLog(String text) {
        tvLog.append(text + "\n");
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == SMS_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                tvStatus.setText("Permission Status: Granted");
                appendLog("SMS Permission granted by user");
            } else {
                tvStatus.setText("Permission Status: Denied");
                appendLog("SMS Permission denied by user");
            }
        }
    }
}