package com.abhiumale.kartikmartapp.ui.presentation.adminPanel

import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.abhiumale.kartikmartapp.domain.model.Product

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddProductScreen(
    navController: NavController,
    viewModel: AdminViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    var id by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("") }
    var mrp by remember { mutableStateOf("") }
    var imageUrl by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("") }
    var brand by remember { mutableStateOf("") }
    var weight by remember { mutableStateOf("") }
    var rating by remember { mutableStateOf("4.5") }
    var ratingCount by remember { mutableStateOf("0") }

    val imagePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let {
            viewModel.uploadImage(it) { url ->
                imageUrl = url
                Toast.makeText(context, "Image Uploaded", Toast.LENGTH_SHORT).show()
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Add New Product") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Image Picker Section
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .border(1.dp, Color.Gray, RoundedCornerShape(12.dp))
                    .clickable { imagePicker.launch("image/*") },
                contentAlignment = Alignment.Center
            ) {
                if (imageUrl.isNotEmpty()) {
                    AsyncImage(
                        model = imageUrl,
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.AddAPhoto, contentDescription = null, modifier = Modifier.size(48.dp))
                        Text("Add Product Image")
                    }
                }
                if (viewModel.isLoading && imageUrl.isEmpty()) {
                    CircularProgressIndicator()
                }
            }

            OutlinedTextField(value = id, onValueChange = { id = it }, label = { Text("Product ID (Number)") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Product Name") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = price, onValueChange = { price = it }, label = { Text("Price (₹)") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal), modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = mrp, onValueChange = { mrp = it }, label = { Text("MRP (₹)") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal), modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = imageUrl, onValueChange = { imageUrl = it }, label = { Text("Image URL") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = category, onValueChange = { category = it }, label = { Text("Category") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = brand, onValueChange = { brand = it }, label = { Text("Brand") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = weight, onValueChange = { weight = it }, label = { Text("Weight (e.g. 500g, 1kg)") }, modifier = Modifier.fillMaxWidth())

            Button(
                onClick = {
                    if (id.isEmpty() || name.isEmpty() || price.isEmpty() || imageUrl.isEmpty()) {
                        Toast.makeText(context, "Please fill mandatory fields", Toast.LENGTH_SHORT).show()
                        return@Button
                    }
                    val product = Product(
                        id = id.toLongOrNull() ?: System.currentTimeMillis(),
                        name = name,
                        price = price.toDoubleOrNull() ?: 0.0,
                        mrp = mrp.toDoubleOrNull() ?: 0.0,
                        image = imageUrl,
                        category = category,
                        brand = brand,
                        rating = rating.toDoubleOrNull() ?: 0.0,
                        ratingCount = ratingCount.toIntOrNull() ?: 0,
                        weight = weight
                    )
                    viewModel.addProduct(product) {
                        Toast.makeText(context, "Product Added Successfully", Toast.LENGTH_SHORT).show()
                        navController.popBackStack()
                    }
                },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                enabled = !viewModel.isLoading
            ) {
                if (viewModel.isLoading) CircularProgressIndicator(color = MaterialTheme.colorScheme.onPrimary)
                else Text("Add Product", fontWeight = FontWeight.Bold)
            }
        }
    }
}
