package com.marioapps.zxingtest;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.zxing.Result;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class ScanActivity extends AppCompatActivity {

    private ZXingScannerView zXingScannerView;
    private static String AES = "AES";
    private static String password = "password";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);

        zXingScannerView = findViewById(R.id.scannerview);

        zXingScannerView = new ZXingScannerView(this);
        setContentView(zXingScannerView);

        zXingScannerView.startCamera();

        zXingScannerView.setResultHandler(new ZXingScannerView.ResultHandler(){
            @Override
            public void handleResult(Result rawResult) {
                String texto = rawResult.getText();
                try {
                    String textoDescifrado = decrypt(texto, password);
                    Toast.makeText(getApplicationContext(), textoDescifrado, Toast.LENGTH_SHORT).show();
                    Log.d("prueba", textoDescifrado);
                    zXingScannerView.resumeCameraPreview(this);
                } catch (Exception e) {
                    Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_SHORT).show();
                    Log.d("prueba", e.toString());
                    zXingScannerView.resumeCameraPreview(this);
                }


            }
        });

    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(ScanActivity.this, MainActivity.class);
        startActivity(intent);
        zXingScannerView.stopCamera();
        finish();
    }

    public String encrypt(String data, String password) throws Exception  {
        SecretKeySpec key = generateKey(password);
        Cipher c = Cipher.getInstance(AES);
        c.init(Cipher.ENCRYPT_MODE, key);
        byte[] encVal = c.doFinal(data.getBytes());
        String encryptedValue = Base64.encodeToString(encVal, Base64.DEFAULT);
        return encryptedValue;
    }

    public String decrypt(String data, String password) throws Exception  {
        SecretKeySpec key = generateKey(password);
        Cipher c = Cipher.getInstance(AES);
        c.init(Cipher.DECRYPT_MODE, key);
        byte[] decodedValue = Base64.decode(data, Base64.DEFAULT);
        byte[] decValue = c.doFinal(decodedValue);
        String decryptedValue = new String(decValue);
        return decryptedValue;
    }

    private SecretKeySpec generateKey(String password) throws Exception {
        final MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] bytes = password.getBytes("UTF-8");
        digest.update(bytes, 0, bytes.length);
        byte[] key = digest.digest();
        SecretKeySpec secretKeySpec = new SecretKeySpec(key, "AES");
        return secretKeySpec;
    }

}
