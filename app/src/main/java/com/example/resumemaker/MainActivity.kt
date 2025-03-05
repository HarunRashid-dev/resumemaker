package com.example.resumemaker

import android.os.Bundle
import android.os.Environment
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.resumemaker.ui.theme.ResumemakerTheme
import java.io.File
import java.io.FileOutputStream

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ResumeMakerApp()
        }
    }
}

@Composable
fun ResumeMakerApp() {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var experience by remember { mutableStateOf("") }
    var education by remember { mutableStateOf("") }

    Column(modifier = Modifier.padding(16.dp)) {
        TextField(value = name, onValueChange = { name = it }, label = { Text("Full Name") })
        TextField(value = email, onValueChange = { email = it }, label = { Text("Email") })
        TextField(value = phone, onValueChange = { phone = it }, label = { Text("Phone") })
        TextField(value = experience, onValueChange = { experience = it }, label = { Text("Experience") })
        TextField(value = education, onValueChange = { education = it }, label = { Text("Education") })

        Spacer(modifier = Modifier.height(20.dp))
        Button(onClick = { generatePDF(name, email, phone, experience, education) }) {
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

    FileOutputStream(file).use { it.write(pdfText.toByteArray()) }
}
