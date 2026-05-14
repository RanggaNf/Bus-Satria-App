package com.bussatriaappdriver.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.bussatriaappdriver.R

data class Product(
    val id: Int,
    val image: String,
    val name: String,
    val price: String,
    val weight: String,
    val rating: String,
    val category: String
)

@Composable
fun HomeScreen() {
    var isFilterOpen by remember { mutableStateOf(false) }
    var activeFilters by remember { mutableStateOf(FilterState(emptyList(), 0f..200000f)) }
    var activeCategory by remember { mutableStateOf("Semua") }

    val allProducts = listOf(
        Product(1, "placeholder_sawi_manis", "Sawi Manis", "3000", "1 ikat", "4.9", "Sayuran"),
        Product(2, "placeholder_ikan_tongkol", "Ikan Tongkol", "25000", "1 kg", "4.8", "Ikan"),
        Product(3, "placeholder_daging_sapi", "Daging Sapi", "120000", "1 kg", "4.9", "Daging"),
        Product(4, "placeholder_buah_jeruk", "Buah Jeruk", "25000", "1 kg", "4.7", "Buah")
    )

    val categories = listOf("Semua", "Sayuran", "Buah", "Daging", "Ikan")

    val filteredProducts = allProducts.filter { product ->
        val categoryMatch = activeCategory == "Semua" || product.category == activeCategory
        val priceMatch = product.price.toFloat() in activeFilters.priceRange
        val filterCategoryMatch = activeFilters.categories.isEmpty() || product.category in activeFilters.categories
        categoryMatch && priceMatch && filterCategoryMatch
    }

    Column(modifier = Modifier.fillMaxSize()) {
        SearchBar(
            onFilterClick = { isFilterOpen = true }
        )
        CategorySelector(
            categories = categories,
            activeCategory = activeCategory,
            onCategorySelected = { activeCategory = it }
        )
        ProductGrid(products = filteredProducts)
        BottomNavigation()
    }

    if (isFilterOpen) {
        FilterOverlay(
            onClose = { isFilterOpen = false },
            onApply = { filters ->
                activeFilters = filters
                isFilterOpen = false
            }
        )
    }
}


@Composable
fun SearchBar(onFilterClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF1B5E20))
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        TextField(
            value = "",
            onValueChange = {},
            placeholder = { Text("Cari Produk") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
            modifier = Modifier
                .weight(1f)
                .clip(RoundedCornerShape(50))
                .background(Color.White)
        )
        Spacer(modifier = Modifier.width(8.dp))
        IconButton(onClick = onFilterClick) {
            Icon(Icons.Default.FilterList, contentDescription = "Filter", tint = Color.White)
        }
        IconButton(onClick = {}) {
            Icon(Icons.Default.Notifications, contentDescription = "Notifications", tint = Color.White)
        }
        IconButton(onClick = {}) {
            Icon(Icons.Default.Person, contentDescription = "Profile", tint = Color.White)
        }
    }
}

@Composable
fun CategorySelector(
    categories: List<String>,
    activeCategory: String,
    onCategorySelected: (String) -> Unit
) {
    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(horizontal = 16.dp)
    ) {
        items(categories) { category ->
            Button(
                onClick = { onCategorySelected(category) },
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = if (category == activeCategory) Color(0xFF1B5E20) else Color.LightGray,
                    contentColor = if (category == activeCategory) Color.White else Color.Black
                ),
                shape = RoundedCornerShape(50)
            ) {
                Text(category)
            }
        }
    }
}

@Composable
fun ProductGrid(products: List<Product>) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(products) { product ->
            ProductCard(product)
        }
    }
}

@Composable
fun ProductCard(product: Product) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = 4.dp
    ) {
        Column(modifier = Modifier.padding(8.dp)) {
            Image(
                painter = painterResource(id = R.drawable.pasengger),
                contentDescription = product.name,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(product.name, fontWeight = FontWeight.SemiBold)
            Text("Rp ${product.price}", color = Color(0xFF1B5E20), fontWeight = FontWeight.Bold)
            Text(product.weight, color = Color.Gray, fontSize = 12.sp)
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Star, contentDescription = null, tint = Color.Yellow, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text(product.rating, color = Color.Gray, fontSize = 12.sp)
            }
        }
    }
}

@Composable
fun BottomNavigation() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        BottomNavItem(icon = Icons.Default.Home, label = "Beranda", isSelected = true)
        BottomNavItem(icon = Icons.Default.Group, label = "Komunitas")
        BottomNavItem(icon = Icons.Default.ShoppingBag, label = "Toko Resmi")
        BottomNavItem(icon = Icons.Default.DateRange, label = "Panen")
        BottomNavItem(icon = Icons.Default.ShoppingCart, label = "Keranjang")
    }
}

@Composable
fun BottomNavItem(icon: ImageVector, label: String, isSelected: Boolean = false) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(
            icon,
            contentDescription = label,
            tint = if (isSelected) Color(0xFF1B5E20) else Color.Gray
        )
        Text(label, fontSize = 12.sp, color = if (isSelected) Color(0xFF1B5E20) else Color.Gray)
    }
}

data class FilterState(
    val categories: List<String>,
    val priceRange: ClosedFloatingPointRange<Float>
)

@Composable
fun FilterOverlay(
    onClose: () -> Unit,
    onApply: (FilterState) -> Unit
) {
    var selectedCategories by remember { mutableStateOf(emptyList<String>()) }
    var priceRange by remember { mutableStateOf(0f..200000f) }

    val categories = listOf("Sayuran", "Buah", "Daging", "Ikan")

    Dialog(onDismissRequest = onClose) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.8f),
            elevation = 8.dp
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Filter", style = MaterialTheme.typography.h6)
                    IconButton(onClick = onClose) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                }

                Text("Kategori", style = MaterialTheme.typography.subtitle1, modifier = Modifier.padding(vertical = 8.dp))
                LazyColumn(modifier = Modifier.fillMaxWidth()) {
                    items(categories) { category ->
                        Button(
                            onClick = {
                                selectedCategories = if (category in selectedCategories) {
                                    selectedCategories - category
                                } else {
                                    selectedCategories + category
                                }
                            },
                            colors = ButtonDefaults.buttonColors(
                                backgroundColor = if (category in selectedCategories) Color(0xFF1B5E20) else Color.LightGray,
                                contentColor = if (category in selectedCategories) Color.White else Color.Black
                            ),
                            shape = RoundedCornerShape(50),
                            modifier = Modifier.padding(vertical = 4.dp)
                        ) {
                            Text(category)
                        }
                    }
                }

                Text("Rentang Harga", style = MaterialTheme.typography.subtitle1, modifier = Modifier.padding(vertical = 8.dp))
                Slider(
                    value = priceRange.endInclusive,
                    onValueChange = { priceRange = priceRange.start..it },
                    valueRange = 0f..200000f,
                    steps = 20
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Rp 0")
                    Text("Rp ${priceRange.endInclusive.toInt()}")
                }

                Spacer(modifier = Modifier.weight(1f))

                Button(
                    onClick = { onApply(FilterState(selectedCategories, priceRange)) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFF1B5E20))
                ) {
                    Text("Terapkan Filter", color = Color.White)
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewHomeScreen() {
    HomeScreen()
}

@Preview(showBackground = true)
@Composable
fun PreviewSearchBar() {
    SearchBar(onFilterClick = {})
}

@Preview(showBackground = true)
@Composable
fun PreviewCategorySelector() {
    CategorySelector(
        categories = listOf("Semua", "Sayuran", "Buah", "Daging", "Ikan"),
        activeCategory = "Semua",
        onCategorySelected = {}
    )
}

@Preview(showBackground = true)
@Composable
fun PreviewProductGrid() {
    ProductGrid(
        products = listOf(
            Product(1, "placeholder_sawi_manis", "Sawi Manis", "3000", "1 ikat", "4.9", "Sayuran"),
            Product(2, "placeholder_ikan_tongkol", "Ikan Tongkol", "25000", "1 kg", "4.8", "Ikan")
        )
    )
}

@Preview(showBackground = true)
@Composable
fun PreviewProductCard() {
    ProductCard(
        product = Product(1, "placeholder_sawi_manis", "Sawi Manis", "3000", "1 ikat", "4.9", "Sayuran")
    )
}

@Preview(showBackground = true)
@Composable
fun PreviewBottomNavigation() {
    BottomNavigation()
}

@Preview(showBackground = true)
@Composable
fun PreviewFilterOverlay() {
    FilterOverlay(onClose = {}, onApply = {})
}