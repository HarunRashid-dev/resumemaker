package com.example.resumemaker

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import java.io.File
import java.io.FileOutputStream

class MainActivity : ComponentActivity() {

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ResumeMakerApp { name, email, phone, experience, education ->
                checkStoragePermissions { generatePDF(name, email, phone, experience, education) }
            }
        }
    }

    private fun checkStoragePermissions(onPermissionGranted: () -> Unit) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            // Android 10+ does not require WRITE_EXTERNAL_STORAGE permission
            onPermissionGranted()
        } else {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                onPermissionGranted()
            } else {
                requestPermissionLauncher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE)
            }
        }
    }
}

@Composable
fun ResumeMakerApp(onGeneratePDF: (String, String, String, String, String) -> Unit) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var experience by remember { mutableStateOf("") }
    var education by remember { mutableStateOf("") }

    Column(modifier = Modifier.padding(16.dp)) {
        TextField(value = name, onValueChange = { name = it }, label = { Text("Full Name") })
        Spacer(modifier = Modifier.height(8.dp))

        TextField(value = email, onValueChange = { email = it }, label = { Text("Email") })
        Spacer(modifier = Modifier.height(8.dp))

        TextField(value = phone, onValueChange = { phone = it }, label = { Text("Phone") })
        Spacer(modifier = Modifier.height(8.dp))

        TextField(value = experience, onValueChange = { experience = it }, label = { Text("Experience") })
        Spacer(modifier = Modifier.height(8.dp))

        TextField(value = education, onValueChange = { education = it }, label = { Text("Education") })
        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = { onGeneratePDF(name, email, phone, experience, education) }) {
            Text("Generate Resume PDF")
        }
    }
}

fun generatePDF(name: String, email: String, phone: String, experience: String, education: String) {
    val pdfText = """
        Resume
        Name: $name
        Email: $email
        Phone: $phone
        Experience: $experience
        Education: $education
    """.trimIndent()

    val fileName = "Resume.pdf"
    val file = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS), fileName)

    try {
        FileOutputStream(file).use { it.write(pdfText.toByteArray()) }
        println("PDF Generated Successfully at: ${file.absolutePath}")
    } catch (e: Exception) {
        println("Error Writing PDF: ${e.message}")
    }
}
