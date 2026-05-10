package com.abhiumale.kartikmartapp.ui.presentation.homescreen

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.abhiumale.kartikmartapp.ui.navigation.Routs
import com.abhiumale.kartikmartapp.ui.presentation.all_product_screen.ProductGrid
import com.abhiumale.kartikmartapp.ui.presentation.components.BannerSection
import com.abhiumale.kartikmartapp.ui.presentation.components.BottomNavigationBar
import com.abhiumale.kartikmartapp.ui.presentation.components.DrawerContent
import com.abhiumale.kartikmartapp.ui.presentation.components.SectionTitle
import com.abhiumale.kartikmartapp.ui.presentation.components.TopHeader
import com.abhiumale.kartikmartapp.ui.presentation.registrationscreens.AuthViewModel
import kotlinx.coroutines.launch

@Composable
fun HomeScreen(
    navController: NavController,
    viewModel: HomeViewModel = hiltViewModel(),
    authViewModel: AuthViewModel = hiltViewModel()
) {
    LaunchedEffect(Unit) {
        authViewModel.fetchUserData()
    }
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    val userData = authViewModel.userData
    val userName = userData?.get("name")?.toString() ?: "Guest"

    val products = viewModel.productList
    var searchQuery by remember { mutableStateOf("") }

    var selectedCategory by remember { mutableStateOf("All") }

    val filteredProducts = products.filter {
        it.name?.contains(searchQuery, ignoreCase = true) == true
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            DrawerContent(
                userName = userName,
                onDestinationClicked = { route ->
                    scope.launch { drawerState.close() }

                    navController.navigate(route) {
                        popUpTo(navController.graph.startDestinationId) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }
    ) {
        Scaffold(
            topBar = {
                TopHeader(
                    searchQuery = searchQuery,
                    onSearchChange = { searchQuery = it },
                    navController,
                    onMenuClick = {
                        scope.launch { drawerState.open() }
                    }
                )
            },
            bottomBar = {
                BottomNavigationBar(navController, "Home")
            }
        ) { padding ->

            LazyColumn(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize()
            ) {

                item {

                    CategoryRow(
                        selectedCategory = selectedCategory,
                        onCategorySelected = { category ->
                            selectedCategory = category

                            if (category == "All") {
                                viewModel.loadAllProducts()
                            } else {
                                viewModel.filterByCategory(category)
                            }
                        }
                    )       //CategoryRow

                    Spacer(modifier = Modifier.height(15.dp))
                    BannerSection()        //BannerSection

                    Text(
                        "Top Deals",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(start = 12.dp)
                    )
                    ProductHorizontalList(
                        filteredProducts,
                        navController
                    )     //ProductHorizontalList

                    Spacer(modifier = Modifier.height(15.dp))
                    SectionTitle(        //SectionTitle
                        title = "All Products",
                        onViewAllClick = {
                            navController.navigate(Routs.TopDealsRouts)
                        }
                    )
                    ProductGrid(filteredProducts, navController)    //ProductGrid

                }
            }
        }
    }
}
