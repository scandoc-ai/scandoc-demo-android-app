package com.example.scandoctestapplication

import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.Button
import androidx.compose.ui.Modifier
import androidx.core.app.ActivityCompat
import com.example.scandoctestapplication.ui.theme.ScandocTestApplicationTheme

import io.flutter.embedding.android.FlutterActivity;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.embedding.engine.FlutterEngine;
import io.flutter.embedding.engine.dart.DartExecutor;
import io.flutter.embedding.engine.FlutterEngineCache
import android.Manifest
import android.util.Log

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)
            ActivityCompat.requestPermissions(this, arrayOf<String>(Manifest.permission.CAMERA), 0);

        setContent {
            ScandocTestApplicationTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Button(onClick = {
                        val flutterEngine = FlutterEngine(this)

                        // use your API key and Client name
                        val apiKey = "<api_key>"
                        val clientName = "<client_name>"
                        flutterEngine.navigationChannel.setInitialRoute("/scan?APIKey=$apiKey&ClientName=$clientName")

                        flutterEngine
                            .dartExecutor
                            .executeDartEntrypoint(
                                DartExecutor.DartEntrypoint.createDefault()
                            )

                        FlutterEngineCache.getInstance().put("5", flutterEngine)

                        val channel = MethodChannel(flutterEngine.dartExecutor.binaryMessenger, "scandoc.ai/result")
                        channel.setMethodCallHandler { call, result ->
                            when (call.method) {
                                // this is called when the scan has successfully completed
                                "scanResult" -> {
                                    var scanResult = call.argument<Map<String, Any>>("ScanDocData")
                                    Log.d("TEST", "Scan successful, printing extracted data")
                                    Log.d("TEST", scanResult.toString())
                                    result.success(true)
                                }
                            }
                        }
                        val intent = FlutterActivity
                            .withCachedEngine("5")
                            .build(this)
                        startActivity(intent)
                    }) {
                        Text(text = "Scan")
                    }
                }
            }
        }
    }
}