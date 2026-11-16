package com.example.concesionario

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.concesionario.ui.theme.ConcesionarioTheme
import java.util.Locale
import kotlin.math.pow
import com.example.concesionario.data.local.AppDatabase
import com.example.concesionario.viewmodel.AutoLuxViewModelFactory
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.concesionario.viewmodel.FavoriteViewModel
import com.example.concesionario.viewmodel.TestDriveViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.concesionario.data.local.TestDriveEntity
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.TextButton
import androidx.compose.material3.SelectableDates
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.TextButton
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.material3.OutlinedTextField
import androidx.compose.runtime.key
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.foundation.border
import androidx.compose.foundation.BorderStroke
import androidx.compose.ui.text.style.TextAlign


// ============================================================
// MODELO Y RUTAS
// ============================================================

data class Vehicle(
    val id: Int,
    val brand: String,
    val model: String,
    val year: Int,
    val category: String,
    val description: String,
    val price: Long,
    val image: Int
)

object Routes {
    const val HOME = "home"
    const val CATALOG = "catalog"
    const val FAVORITES = "favorites"
    const val PROFILE = "profile"
    const val APPOINTMENT = "appointment"
    const val FINANCE = "finance"

    const val MY_TEST_DRIVES = "my_test_drives"
}

// ============================================================
// ACTIVIDAD
// ============================================================


class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val database = AppDatabase.getDatabase(applicationContext)

        val factory = AutoLuxViewModelFactory(database)

        setContent {
            ConcesionarioTheme {
                AutoLuxApp(factory = factory)
            }
        }
    }
}


// ============================================================
// APLICACIÓN PRINCIPAL
// ============================================================

@Composable
fun AutoLuxApp(factory: AutoLuxViewModelFactory) {
    val navController = rememberNavController()
    val favoriteViewModel: FavoriteViewModel = viewModel(factory = factory)

    val testDriveViewModel: TestDriveViewModel = viewModel(factory = factory)
    val favoriteEntities by favoriteViewModel.favorites.collectAsStateWithLifecycle()
    val testDriveRequests by testDriveViewModel.testDrives.collectAsStateWithLifecycle()

    val favorites = remember(favoriteEntities) {
        favoriteEntities.map { it.vehicleId }.toSet()
    }

    val vehicles = remember {
        listOf(
            Vehicle(1, "Mercedes-Benz", "Clase C", 2024, "Sedán",
                "Elegancia, confort y tecnología para una conducción premium.",
                220_000_000L, R.drawable.mercedes_benz_clase_c_sedan),
            Vehicle(2, "BMW", "Serie 5", 2025, "Sedán",
                "Diseño sofisticado con tecnología de última generación.",
                280_000_000L, R.drawable.bwe_serie_5_sedan),
            Vehicle(3, "BMW", "X5", 2024, "SUV",
                "Espacio, potencia y comodidad para cada aventura.",
                380_000_000L, R.drawable.bmw_x5_suv),
            Vehicle(4, "Porsche", "Macan", 2025, "SUV",
                "Un SUV deportivo con personalidad y precisión.",
                450_000_000L, R.drawable.porsche_macan),
            Vehicle(5, "Range Rover", "Evoque", 2024, "SUV",
                "Lujo contemporáneo con carácter todoterreno.",
                320_000_000L, R.drawable.range_rover_evoque_suv),
            Vehicle(6, "Porsche", "911 Carrera", 2025, "Deportivo",
                "Un deportivo icónico creado para emocionar.",
                750_000_000L, R.drawable.porshe_911_carrera),
            Vehicle(7, "Audi", "R8", 2023, "Deportivo",
                "Diseño audaz y una experiencia de conducción excepcional.",
                850_000_000L, R.drawable.audi_r8),
            Vehicle(8, "Mercedes-AMG", "GT", 2025, "Deportivo",
                "Prestaciones deportivas con un diseño inconfundible.",
                780_000_000L, R.drawable.mercedes_amg_gt),
            Vehicle(9, "Lexus", "LC", 2024, "Coupé de lujo",
                "Artesanía, exclusividad y elegancia en cada detalle.",
                520_000_000L, R.drawable.lexus_lc_coupe),
            Vehicle(10, "Bentley", "Continental GT", 2025, "Coupé de lujo",
                "Gran turismo de lujo con una presencia extraordinaria.",
                1_200_000_000L, R.drawable.bentley_continental_gt__coupe)
        )
    }

    var catalogCategory by remember { mutableStateOf("Todos") }
    var selectedVehicleId by remember { mutableIntStateOf(1) }
    val selectedVehicle = vehicles.firstOrNull { it.id == selectedVehicleId } ?: vehicles.first()

    val entry by navController.currentBackStackEntryAsState()
    val currentRoute = entry?.destination?.route ?: Routes.HOME

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        bottomBar = {
            // La barra inferior está disponible en todas las pantallas.
            AutoLuxBottomBar(
                currentRoute = currentRoute,
                navController = navController,
                favoritesCount = favorites.size
            )
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Routes.HOME,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Routes.HOME) {
                HomeScreen(
                    vehicles = vehicles,
                    favorites = favorites,
                    onCatalog = {
                        catalogCategory = "Todos"
                        navigateToTab(navController, Routes.CATALOG)
                    },
                    onSelectCategory = { category ->
                        catalogCategory = category
                        navigateToTab(navController, Routes.CATALOG)
                    },
                    onFavorite = { id ->
                        if (id in favorites) {
                            favoriteViewModel.removeFavorite(id)
                        } else {
                            favoriteViewModel.addFavorite(id)
                        }
                    },
                    onSelectVehicle = { id ->
                        selectedVehicleId = id
                        navController.navigate(Routes.APPOINTMENT)
                    },
                    onFinance = { id ->
                        selectedVehicleId = id
                        navController.navigate(Routes.FINANCE)
                    }
                )
            }

            composable(Routes.CATALOG) {
                CatalogScreen(
                    vehicles = vehicles,
                    favorites = favorites,
                    initialCategory = catalogCategory,
                    onFavorite = { id ->
                        if (id in favorites) {
                            favoriteViewModel.removeFavorite(id)
                        } else {
                            favoriteViewModel.addFavorite(id)
                        }
                    },
                    onAppointment = { id ->
                        selectedVehicleId = id
                        navController.navigate(Routes.APPOINTMENT)
                    },
                    onFinance = { id ->
                        selectedVehicleId = id
                        navController.navigate(Routes.FINANCE)
                    }
                )
            }

            composable(Routes.FAVORITES) {
                FavoritesScreen(
                    vehicles = vehicles.filter { it.id in favorites },
                    favorites = favorites,
                    onFavorite = { id ->
                        favoriteViewModel.removeFavorite(id)
                    },
                    onAppointment = { id ->
                        selectedVehicleId = id
                        navController.navigate(Routes.APPOINTMENT)
                    },
                    onFinance = { id ->
                        selectedVehicleId = id
                        navController.navigate(Routes.FINANCE)
                    },
                    onCatalog = {
                        catalogCategory = "Todos"
                        navigateToTab(navController, Routes.CATALOG)
                    }
                )
            }

            composable(Routes.PROFILE) {
                ProfileScreen(
                    testDriveRequests = testDriveRequests,
                    vehicles = vehicles,
                    testDriveViewModel = testDriveViewModel,
                    favorites = favorites,
                    onMyTestDrives = { navController.navigate(Routes.MY_TEST_DRIVES) },
                    onFavorites = { navController.navigate(Routes.FAVORITES) },
                    onAppointment = { navController.navigate(Routes.APPOINTMENT) },
                    onFinance = { navController.navigate(Routes.FINANCE) }
                )
            }

            composable(Routes.MY_TEST_DRIVES) {
                MyTestDrivesScreen(
                    testDriveRequests = testDriveRequests,
                    vehicles = vehicles,
                    testDriveViewModel = testDriveViewModel,
                    onBack = {
                        navController.popBackStack()
                    }
                )
            }

            composable(Routes.APPOINTMENT) {
                AppointmentScreen(
                    vehicle = selectedVehicle,
                    testDriveViewModel = testDriveViewModel,
                    testDriveRequests = testDriveRequests,
                    onBack = { navController.popBackStack() }
                )
            }

            composable(Routes.FINANCE) {
                FinanceScreen(
                    vehicle = selectedVehicle,
                    onBack = { navController.popBackStack() }
                )
            }
        }
    }
}

/**
 * Navegación de las pestañas principales.
 * Se vuelve a la raíz y se abre la pestaña elegida sin guardar/restaurar
 * estados de navegación que puedan impedir volver a Inicio.
 */
private fun navigateToTab(navController: NavHostController, route: String) {
    navController.navigate(route) {
        popUpTo(Routes.HOME) {
            inclusive = false
            saveState = false
        }
        launchSingleTop = true
        restoreState = false
    }
}

// ============================================================
// BARRA INFERIOR: INICIO SIEMPRE VUELVE A HOMESCREEN
// ============================================================

@Composable
fun AutoLuxBottomBar(
    currentRoute: String,
    navController: NavHostController,
    favoritesCount: Int
) {
    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface,
        contentColor = MaterialTheme.colorScheme.onSurface
    ) {
        val items = listOf(
            Triple(Routes.HOME, "⌂", "Inicio"),
            Triple(Routes.CATALOG, "▤", "Catálogo"),
            Triple(Routes.FAVORITES, "♡", "Favoritos ($favoritesCount)"),
            Triple(Routes.PROFILE, "○", "Perfil")
        )

        items.forEach { (route, symbol, label) ->
            NavigationBarItem(
                selected = currentRoute == route,
                onClick = {
                    if (currentRoute != route) {
                        // Usa la misma navegación corregida para todas las pestañas.
                        navigateToTab(navController, route)
                    }
                },
                icon = {
                    Text(text = symbol, fontSize = 25.sp, fontWeight = FontWeight.Light)
                },
                label = { Text(label) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = MaterialTheme.colorScheme.primary,
                    selectedTextColor = MaterialTheme.colorScheme.primary,
                    indicatorColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)
                )
            )
        }
    }
}

// ============================================================
// INICIO PREMIUM
// ============================================================

@Composable
fun HomeScreen(
    vehicles: List<Vehicle>,
    favorites: Set<Int>,
    onCatalog: () -> Unit,
    onSelectCategory: (String) -> Unit,
    onFavorite: (Int) -> Unit,
    onSelectVehicle: (Int) -> Unit,
    onFinance: (Int) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        // Logotipo y subtítulo
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Auto", fontSize = 29.sp, fontWeight = FontWeight.Bold)
                    Text("Lux", fontSize = 29.sp, fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary)
                }
                Text(
                    "C O N C E S I O N A R I O   P R E M I U M",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.75f)
                )
            }
            Text("○", fontSize = 32.sp)
        }

        // Hero con fotografía y texto superpuesto
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(480.dp)
        ) {
            Image(
                painter = painterResource(R.drawable.audi_r8),
                contentDescription = "Deportivo destacado de AutoLux",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Fit
            )
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.horizontalGradient(
                            listOf(
                                Color.Black.copy(alpha = 0.90f),
                                Color.Black.copy(alpha = 0.58f),
                                Color.Transparent
                            )
                        )
                    )
            )
            Column(
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .fillMaxWidth(0.88f)
                    .padding(start = 20.dp, end = 8.dp)
            ) {
                Text(
                    "EXCLUSIVIDAD EN CADA DETALLE",
                    style = MaterialTheme.typography.labelLarge,
                    color = Color(0xFFFF343B),
                    fontWeight = FontWeight.Bold
                )
                Spacer(Modifier.height(16.dp))
                Text(
                    "Tu próximo nivel\nempieza aquí.",
                    fontSize = 34.sp,
                    lineHeight = 39.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.White
                )
                Spacer(Modifier.height(12.dp))
                Text(
                    "Diseñados para quienes no siguen el camino. Lo definen.",
                    fontSize = 16.sp,
                    lineHeight = 23.sp,
                    color = Color.White.copy(alpha = 0.92f)
                )
                Spacer(Modifier.height(22.dp))
                Button(
                    onClick = onCatalog,
                    shape = RoundedCornerShape(50),
                    modifier = Modifier.height(52.dp)
                ) {
                    Text("DESCUBRIR COLECCIÓN  →", fontWeight = FontWeight.Bold)
                }
            }
        }

        // Atributos de marca
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            BrandFeature("DISEÑO", "Icónico", Modifier.weight(1f))
            BrandFeature("ALTO", "Desempeño", Modifier.weight(1f))
            BrandFeature("LUJO", "Exclusivo", Modifier.weight(1f))
        }

        // Vehículo principal
        Column(Modifier.padding(horizontal = 18.dp)) {
            Text(
                "Selección del experto",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.height(4.dp))
            Text(
                "Vehículos que marcan la diferencia",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
            )
        }

        Spacer(Modifier.height(16.dp))

        vehicles.firstOrNull{ it.id == 9 }?.let { vehicle ->
            FeaturedVehicleCard(
                vehicle = vehicle,
                isFavorite = vehicle.id in favorites,
                onFavorite = { onFavorite(vehicle.id) },
                onDetails = { onSelectVehicle(vehicle.id) },
                onFinance = { onFinance(vehicle.id) }
            )
        }

        // Categorías visuales
        Spacer(Modifier.height(24.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            CategoryImageCard(
                title = "SUV PREMIUM",
                image = R.drawable.bmw_x5_suv,
                modifier = Modifier.weight(1f),
                onClick = { onSelectCategory("SUV") }
            )
            CategoryImageCard(
                title = "DEPORTIVOS",
                image = R.drawable.porshe_911_carrera,
                modifier = Modifier.weight(1f),
                onClick = { onSelectCategory("Deportivo") }
            )
        }

        Spacer(Modifier.height(24.dp))
        OutlinedButton(
            onClick = onCatalog,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .height(50.dp)
        ) {
            Text("EXPLORAR TODOS LOS VEHÍCULOS")
        }
        Spacer(Modifier.height(24.dp))
    }
}

// ============================================================
// COMPONENTES DE INICIO
// ============================================================

@Composable
fun BrandFeature(title: String, subtitle: String, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.heightIn(min = 88.dp),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 14.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                title,
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.height(4.dp))
            Text(subtitle, style = MaterialTheme.typography.bodySmall)
        }
    }
}



@Composable
fun FeaturedVehicleCard(
    vehicle: Vehicle,
    isFavorite: Boolean,
    onFavorite: () -> Unit,
    onDetails: () -> Unit,
    onFinance: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF17181D)
        )
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {

            // Fotografía adaptable al dispositivo
            BoxWithConstraints(
                modifier = Modifier.fillMaxWidth()
            ) {

                val imageHeight = (maxWidth * 0.85f)
                    .coerceIn(240.dp, 380.dp)

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(imageHeight)
                        .clickable(onClick = onDetails)
                ) {

                    Image(
                        painter = painterResource(vehicle.image),
                        contentDescription = "${vehicle.brand} ${vehicle.model}",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )

                    // Degradado inferior
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(
                                        Color.Transparent,
                                        Color(0x33000000),
                                        Color(0xEE000000)
                                    )
                                )
                            )
                    )

                    // Etiqueta DESTACADO
                    Surface(
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .padding(12.dp),
                        color = MaterialTheme.colorScheme.primary,
                        shape = RoundedCornerShape(50)
                    ) {
                        Text(
                            "DESTACADO",
                            modifier = Modifier.padding(
                                horizontal = 12.dp,
                                vertical = 7.dp
                            ),
                            color = MaterialTheme.colorScheme.onPrimary,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    // Favoritos
                    TextButton(
                        onClick = onFavorite,
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(4.dp)
                    ) {
                        Text(
                            if (isFavorite) "♥" else "♡",
                            fontSize = 30.sp,
                            color = if (isFavorite) {
                                MaterialTheme.colorScheme.primary
                            } else {
                                Color.White
                            }
                        )
                    }

                    // Información superpuesta
                    Column(
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Text(
                            vehicle.brand.uppercase(),
                            color = MaterialTheme.colorScheme.primary,
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold
                        )

                        Text(
                            vehicle.model,
                            color = Color.White,
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1
                        )

                        Spacer(Modifier.height(5.dp))

                        Text(
                            "${vehicle.year} • ${vehicle.category}",
                            color = Color.White.copy(alpha = 0.85f),
                            style = MaterialTheme.typography.bodyMedium
                        )

                        Spacer(Modifier.height(10.dp))

                        Text(
                            formatPrice(vehicle.price),
                            color = Color.White,
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1
                        )
                    }
                }
            }

            // Botones fuera de la fotografía
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Button(
                    onClick = onFinance,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("SIMULAR FINANCIACIÓN")
                }

                Spacer(Modifier.height(6.dp))

                OutlinedButton(
                    onClick = onDetails,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("AGENDAR PRUEBA DE MANEJO")
                }
            }
        }
    }
}

@Composable
fun CategoryImageCard(
    title: String,
    image: Int,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier
            .aspectRatio(1.8f)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp)
    ) {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            Image(
                painter = painterResource(image),
                contentDescription = title,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            listOf(
                                Color.Transparent,
                                Color.Black.copy(alpha = 0.86f)
                            )
                        )
                    )
            )

            Text(
                "$title  ›",
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(12.dp),
                color = Color.White,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.titleSmall
            )
        }
    }
}


// ============================================================
// CATÁLOGO
// ============================================================

@Composable
fun CatalogScreen(
    vehicles: List<Vehicle>,
    favorites: Set<Int>,
    initialCategory: String = "Todos",
    onFavorite: (Int) -> Unit,
    onAppointment: (Int) -> Unit,
    onFinance: (Int) -> Unit
) {
    var search by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf(initialCategory) }

    LaunchedEffect(initialCategory) {
        selectedCategory = initialCategory
    }

    val categories = listOf("Todos", "Sedán", "SUV", "Deportivo", "Coupé de lujo")

    val filteredVehicles = vehicles.filter { vehicle ->
        val matchesSearch = vehicle.brand.contains(search.trim(), ignoreCase = true) ||
                vehicle.model.contains(search.trim(), ignoreCase = true) ||
                vehicle.category.contains(search.trim(), ignoreCase = true)
        val matchesCategory = selectedCategory == "Todos" ||
                vehicle.category == selectedCategory
        matchesSearch && matchesCategory
    }

    Column(
        modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp)
    ) {
        Text("Nuestro catálogo", style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(6.dp))
        Text("Encuentra el vehículo ideal para ti.",
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f))
        Spacer(Modifier.height(18.dp))
        OutlinedTextField(
            value = search,
            onValueChange = { search = it },
            label = { Text("Buscar marca, modelo o categoría") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
        Spacer(Modifier.height(14.dp))
        Row(
            modifier = Modifier.horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            categories.forEach { category ->
                FilterChip(
                    selected = selectedCategory == category,
                    onClick = { selectedCategory = category },
                    label = { Text(category) }
                )
            }
        }
        Spacer(Modifier.height(18.dp))
        Text("${filteredVehicles.size} vehículos disponibles",
            style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.height(12.dp))

        filteredVehicles.forEach { vehicle ->
            VehicleCard(
                vehicle = vehicle,
                isFavorite = vehicle.id in favorites,
                onFavorite = { onFavorite(vehicle.id) },
                onAppointment = { onAppointment(vehicle.id) },
                onFinance = { onFinance(vehicle.id) }
            )
            Spacer(Modifier.height(16.dp))
        }

        if (filteredVehicles.isEmpty()) {
            Text("No encontramos vehículos con esos criterios.",
                modifier = Modifier.padding(vertical = 24.dp))
        }
    }
}

// ============================================================
// TARJETA REUTILIZABLE DE VEHÍCULO
// ============================================================



@Composable
fun VehicleCard(
    vehicle: Vehicle,
    isFavorite: Boolean,
    onFavorite: () -> Unit,
    onAppointment: () -> Unit,
    onFinance: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF17181D)
        )
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {

            // Fotografía adaptable al ancho disponible
            BoxWithConstraints(
                modifier = Modifier.fillMaxWidth()
            ) {

                val imageHeight = (maxWidth * 0.72f)
                    .coerceIn(220.dp, 340.dp)

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(imageHeight)
                ) {

                    Image(
                        painter = painterResource(vehicle.image),
                        contentDescription = "${vehicle.brand} ${vehicle.model}",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )

                    // Degradado cinematográfico
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(
                                        Color.Black.copy(alpha = 0.05f),
                                        Color.Transparent,
                                        Color.Black.copy(alpha = 0.85f),
                                        Color.Black.copy(alpha = 0.98f)
                                    )
                                )
                            )
                    )

                    // Botón de favoritos
                    TextButton(
                        onClick = onFavorite,
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(8.dp)
                    ) {
                        Text(
                            text = if (isFavorite) "♥" else "♡",
                            fontSize = 32.sp,
                            color = if (isFavorite) {
                                Color(0xFFE32636)
                            } else {
                                Color.White
                            }
                        )
                    }

                    // Información superpuesta
                    Column(
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {

                        Text(
                            vehicle.brand.uppercase(),
                            color = Color(0xFFE94A50),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )

                        Spacer(Modifier.height(3.dp))

                        Text(
                            vehicle.model,
                            color = Color.White,
                            fontSize = 25.sp,
                            fontWeight = FontWeight.ExtraBold,
                            maxLines = 1
                        )

                        Spacer(Modifier.height(4.dp))

                        Text(
                            "${vehicle.year} • ${vehicle.category}",
                            color = Color.White.copy(alpha = 0.85f),
                            fontSize = 13.sp
                        )

                        Spacer(Modifier.height(8.dp))

                        Text(
                            formatPrice(vehicle.price),
                            color = Color(0xFFE94A50),
                            fontSize = 20.sp,
                            fontWeight = FontWeight.ExtraBold,
                            maxLines = 1
                        )
                    }
                }
            }

            // Descripción y acciones
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 14.dp)
            ) {

                Text(
                    vehicle.description,
                    color = Color.White,
                    fontSize = 14.sp,
                    lineHeight = 19.sp,
                    maxLines = 2
                )

                Spacer(Modifier.height(14.dp))

                Button(
                    onClick = onFinance,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(30.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFE32636)
                    )
                ) {
                    Text(
                        "SIMULAR FINANCIACIÓN",
                        fontWeight = FontWeight.Bold
                    )
                }

                OutlinedButton(
                    onClick = onAppointment,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(30.dp),
                    border = BorderStroke(
                        1.dp,
                        Color.White.copy(alpha = 0.65f)
                    )
                ) {
                    Text(
                        "AGENDAR PRUEBA DE MANEJO",
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}



// ============================================================
// FAVORITOS
// ============================================================


@Composable
fun FavoritesScreen(
    vehicles: List<Vehicle>,
    favorites: Set<Int>,
    onFavorite: (Int) -> Unit,
    onAppointment: (Int) -> Unit,
    onFinance: (Int) -> Unit,
    onCatalog: () -> Unit
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.TopCenter
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .widthIn(max = 760.dp)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            Text(
                "Mis favoritos",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(Modifier.height(8.dp))

            Text(
                "Tu selección personal de vehículos.",
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
            )

            Spacer(Modifier.height(20.dp))

            if (vehicles.isEmpty()) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            "♡",
                            fontSize = 52.sp,
                            color = MaterialTheme.colorScheme.primary
                        )

                        Spacer(Modifier.height(12.dp))

                        Text(
                            "Aún no tienes favoritos",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(Modifier.height(8.dp))

                        Text("Guarda los vehículos que más te gusten.")

                        Spacer(Modifier.height(16.dp))

                        Button(onClick = onCatalog) {
                            Text("EXPLORAR CATÁLOGO")
                        }
                    }
                }
            } else {
                vehicles.forEach { vehicle ->
                    VehicleCard(
                        vehicle = vehicle,
                        isFavorite = vehicle.id in favorites,
                        onFavorite = { onFavorite(vehicle.id) },
                        onAppointment = { onAppointment(vehicle.id) },
                        onFinance = { onFinance(vehicle.id) }
                    )

                    Spacer(Modifier.height(16.dp))
                }
            }
        }
    }
}


// ============================================================
// PERFIL
// ============================================================

@Composable
fun ProfileScreen(
    testDriveRequests: List<TestDriveEntity>,
    vehicles: List<Vehicle>,
    testDriveViewModel: TestDriveViewModel,
    favorites: Set<Int>,
    onFavorites: () -> Unit,
    onMyTestDrives: () -> Unit,
    onAppointment: () -> Unit,
    onFinance: () -> Unit
) {
    val nextTestDrive = remember(testDriveRequests) {
        val dateFormat = java.text.SimpleDateFormat(
            "dd/MM/yyyy HH:mm",
            java.util.Locale.getDefault()
        ).apply {
            timeZone = java.util.TimeZone.getTimeZone("UTC")
            isLenient = false
        }

        val currentDate = java.util.Date()

        testDriveRequests
            .mapNotNull { request ->
                try {
                    val appointmentDateTime = dateFormat.parse(
                        "${request.appointmentDate} ${request.appointmentTime}"
                    )

                    if (appointmentDateTime != null &&
                        appointmentDateTime.after(currentDate)
                    ) {
                        request to appointmentDateTime
                    } else {
                        null
                    }
                } catch (e: Exception) {
                    null
                }
            }
            .minByOrNull { it.second }
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.TopCenter
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .widthIn(max = 760.dp)
                .verticalScroll(rememberScrollState())
                .padding(20.dp)
        ) {

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFF15171C)
            )
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "AUTO",
                                    color = Color.White,
                                    fontSize = 30.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    letterSpacing = 1.sp
                                )
                                Text(
                                    text = "LUX",
                                    color = Color(0xFFE32636),
                                    fontSize = 30.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    letterSpacing = 1.sp
                                )
                            }

                            Text(
                                text = "PREMIUM AUTOMOTIVE",
                                color = Color(0xFFE32636),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 2.sp
                            )
                        }

                        Box(
                            modifier = Modifier
                                .background(
                                    Color(0xFFE32636),
                                    RoundedCornerShape(10.dp)
                                )
                                .padding(horizontal = 13.dp, vertical = 9.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "AL",
                                color = Color.White,
                                fontSize = 22.sp,
                                fontWeight = FontWeight.ExtraBold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(3.dp)
                            .background(
                                Color(0xFFE32636),
                                RoundedCornerShape(4.dp)
                            )
                    )

                    Spacer(modifier = Modifier.height(18.dp))

                    Text(
                        text = "Tu próxima experiencia comienza aquí.",
                        color = Color.White,
                        fontSize = 21.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = "Descubre, reserva y disfruta de la exclusividad.",
                        color = Color(0xFFB8BEC7),
                        fontSize = 13.sp
                    )

                }
            }
        }
        Spacer(modifier = Modifier.height(20.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Tarjeta de pruebas de manejo
            Card(
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFF15171C)
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "PRUEBAS DE MANEJO",
                        color = Color(0xFFB8BEC7),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = testDriveRequests.size.toString(),
                        color = Color.White,
                        fontSize = 30.sp,
                        fontWeight = FontWeight.ExtraBold
                    )

                    Text(
                        text = "Solicitudes registradas",
                        color = Color(0xFF858B95),
                        fontSize = 11.sp
                    )
                }
            }

            // Tarjeta de favoritos
            Card(
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFF15171C)
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "MIS FAVORITOS",
                        color = Color(0xFFB8BEC7),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = favorites.size.toString(),
                        color = Color(0xFFE32636),
                        fontSize = 30.sp,
                        fontWeight = FontWeight.ExtraBold
                    )

                    Text(
                        text = "Vehículos guardados",
                        color = Color(0xFF858B95),
                        fontSize = 11.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(15.dp))
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFF15171C)
            )
        ) {
            if (nextTestDrive != null) {
                val (request, appointmentDateTime) = nextTestDrive
                val vehicle = vehicles.find { it.id == request.vehicleId }

                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "TU PRÓXIMA EXPERIENCIA",
                        color = Color(0xFFB8BEC7),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )

                    Text(
                        text = "PRÓXIMA",
                        color = Color.White,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .background(
                                Color(0xFFE32636),
                                RoundedCornerShape(6.dp)
                            )
                            .padding(horizontal = 9.dp, vertical = 5.dp)
                    )

                    Text(
                        text = if (vehicle != null) {
                            "${vehicle.brand} ${vehicle.model}"
                        } else {
                            "Vehículo seleccionado"
                        },
                        color = Color.White,
                        fontSize = 21.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Divider(color = Color(0xFF383A40))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                "FECHA",
                                color = Color(0xFF858B95),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                request.appointmentDate,
                                color = Color.White,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }

                        Column {
                            Text(
                                "HORA",
                                color = Color(0xFF858B95),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                request.appointmentTime,
                                color = Color.White,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }

                    Text(
                        text = "Solicitante: ${request.customerName}",
                        color = Color(0xFFB8BEC7),
                        fontSize = 12.sp
                    )
                }
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(22.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = "SIN AGENDAMIENTOS",
                        color = Color(0xFFE32636),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.5.sp
                    )

                    Text(
                        text = "No tienes pruebas de manejo programadas",
                        color = Color.White,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = "Agenda una experiencia y descubre tu próximo vehículo.",
                        color = Color(0xFFB8BEC7),
                        fontSize = 12.sp
                    )
                }
            }
        }

        Spacer(Modifier.height(15.dp))

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onMyTestDrives() }
                .border(
                    1.dp,
                    Color(0xFF39313A),
                    RoundedCornerShape(22.dp)
                ),
            shape = RoundedCornerShape(22.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFF17171D)
            ),
            elevation = CardDefaults.cardElevation(
                defaultElevation = 8.dp
            )
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        brush = Brush.linearGradient(
                            colors = listOf(
                                Color(0xFF24212A),
                                Color(0xFF15161B),
                                Color(0xFF1D171C)
                            )
                        )
                    )
                    .padding(20.dp)
            ) {
                Column {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(5.dp, 42.dp)
                                .background(
                                    Color(0xFFE32636),
                                    RoundedCornerShape(5.dp)
                                )
                        )

                        Spacer(Modifier.width(14.dp))

                        Column(
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                "GESTIÓN DE CITAS",
                                color = Color(0xFFE32636),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.8.sp
                            )

                            Spacer(Modifier.height(5.dp))

                            Text(
                                "Mis solicitudes",
                                color = Color.White,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Box(
                            modifier = Modifier
                                .size(58.dp)
                                .background(
                                    Color(0xFFE32636).copy(alpha = 0.15f),
                                    CircleShape
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                "${testDriveRequests.size}",
                                color = Color(0xFFFF4655),
                                fontSize = 25.sp,
                                fontWeight = FontWeight.ExtraBold
                            )
                        }
                    }

                    Spacer(Modifier.height(18.dp))

                    Divider(
                        color = Color(0xFF39333D),
                        thickness = 1.dp
                    )

                    Spacer(Modifier.height(16.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                "Pruebas de manejo registradas",
                                color = Color.White,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold
                            )

                            Spacer(Modifier.height(5.dp))

                            Text(
                                "Consulta, modifica o cancela tus citas.",
                                color = Color(0xFFB8BEC7),
                                fontSize = 12.sp
                            )
                        }

                        Spacer(Modifier.width(12.dp))

                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .background(
                                    Color(0xFFE32636),
                                    CircleShape
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                "→",
                                color = Color.White,
                                fontSize = 29.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        // MI GARAJE
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    // Aquí conectaremos la navegación a Favoritos
                },
            shape = RoundedCornerShape(22.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFF17171D)
            ),
            border = BorderStroke(
                1.dp,
                Color(0xFF39313A)
            )
        ) {
            Column(
                modifier = Modifier.padding(20.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            "MI GARAJE",
                            color = Color.White,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )

                        Spacer(Modifier.height(5.dp))

                        Text(
                            "Tu colección personal AutoLux",
                            color = Color(0xFFB8BEC7),
                            fontSize = 12.sp
                        )
                    }

                    Text(
                        text = "🚘",
                        fontSize = 32.sp
                    )
                }

                Spacer(Modifier.height(18.dp))

                Text(
                    "${favorites.size} vehículos guardados",
                    color = Color(0xFFE32636),
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )

                Spacer(Modifier.height(14.dp))

                val favoriteVehicles = vehicles.filter {
                    it.id in favorites
                }.take(2)

                if (favoriteVehicles.isEmpty()) {
                    Text(
                        text = "Aún no tienes vehículos en tu garaje.",
                        color = Color(0xFFB8BEC7),
                        fontSize = 13.sp
                    )
                } else {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        favoriteVehicles.forEach { vehicle ->

                            val vehicleImage = when (vehicle.id) {
                                1 -> R.drawable.mercedes_benz_clase_c_sedan
                                2 -> R.drawable.bwe_serie_5_sedan
                                3 -> R.drawable.bmw_x5_suv
                                4 -> R.drawable.porsche_macan
                                5 -> R.drawable.range_rover_evoque_suv
                                6 -> R.drawable.porshe_911_carrera
                                7 -> R.drawable.audi_r8
                                8 -> R.drawable.mercedes_amg_gt
                                9 -> R.drawable.lexus_lc_coupe
                                10 -> R.drawable.bentley_continental_gt__coupe
                                else -> null
                            }

                            Card(
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(14.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = Color(0xFF22232A)
                                )
                            ) {
                                Column {
                                    if (vehicleImage != null) {
                                        Image(
                                            painter = painterResource(id = vehicleImage),
                                            contentDescription = "${vehicle.brand} ${vehicle.model}",
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(215.dp),
                                            contentScale = ContentScale.Fit
                                        )
                                    }

                                    Column(
                                        modifier = Modifier.padding(10.dp)
                                    ) {
                                        Text(
                                            "${vehicle.brand} ${vehicle.model}",
                                            color = Color.White,
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Bold,
                                            maxLines = 2
                                        )

                                        Spacer(Modifier.height(4.dp))

                                        Text(
                                            "Año ${vehicle.year}",
                                            color = Color(0xFFB8BEC7),
                                            fontSize = 11.sp
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(Modifier.height(16.dp))

                Divider(color = Color(0xFF39333D))

                Spacer(Modifier.height(14.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            onFavorites()
                        },
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "EXPLORAR MIS FAVORITOS",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )

                    Spacer(Modifier.width(10.dp))

                    Text(
                        text = "➜",
                        color = Color(0xFFE32636),
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(Modifier.height(14.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                }
            }
        }

        Spacer(Modifier.height(24.dp))
        Text("AUTOLUX • CONCESIONARIO PREMIUM",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.primary)
        }
    }
}

@Composable
fun MyTestDrivesScreen(
    testDriveRequests: List<TestDriveEntity>,
    vehicles: List<Vehicle>,
    testDriveViewModel: TestDriveViewModel,
    onBack: () -> Unit
) {

    var requestToEdit by remember {
        mutableStateOf<TestDriveEntity?>(null)
    }

    var editName by remember { mutableStateOf("") }
    var editPhone by remember { mutableStateOf("") }
    var editEmail by remember { mutableStateOf("") }

    var editDate by remember { mutableStateOf("") }
    var editDateMillis by remember { mutableStateOf<Long?>(null) }

    var editTime by remember { mutableStateOf("") }
    var editSelectedTime by remember { mutableStateOf("") }

    var showEditDatePicker by remember {
        mutableStateOf(false)
    }

    var requestToDelete by remember {
        mutableStateOf<TestDriveEntity?>(null)
    }


    // Horarios disponibles según la fecha seleccionada
    val editAvailableTimes = remember(editDateMillis) {
        val calendar = java.util.Calendar.getInstance(
            java.util.TimeZone.getTimeZone("UTC")
        ).apply {
            timeInMillis = editDateMillis ?: System.currentTimeMillis()
        }

        val dayOfWeek = calendar.get(java.util.Calendar.DAY_OF_WEEK)

        val endHour = if (
            dayOfWeek == java.util.Calendar.SATURDAY
        ) 12 else 17

        buildList {
            for (hour in 8 until endHour) {
                add(String.format(Locale.getDefault(), "%02d:00", hour))
                add(String.format(Locale.getDefault(), "%02d:30", hour))
            }
        }
    }

    // Horarios que ya están reservados
    val editReservedTimes = testDriveRequests
        .filter {
            it.appointmentDate == editDate &&
                    it.id != requestToEdit?.id
        }
        .map { it.appointmentTime }
        .toSet()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0D0F13)),
        contentAlignment = Alignment.TopCenter
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .widthIn(max = 760.dp)
                .verticalScroll(rememberScrollState())
                .padding(20.dp)
        ) {

        TextButton(
            onClick = {
                onBack()
            }
        ) {
            Text(
                text = "←",
                color = Color(0xFFE32636),
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(Modifier.width(8.dp))

            Text(
                text = "VOLVER AL PERFIL",
                color = Color.White,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )
        }

        Text(
            text = "MIS SOLICITUDES",
            color = Color.White,
            fontSize = 26.sp,
            fontWeight = FontWeight.ExtraBold
        )

        Text(
            text = "Administra tus pruebas de manejo",
            color = Color(0xFFB8BEC7),
            fontSize = 13.sp
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "${testDriveRequests.size} solicitudes registradas",
            color = Color(0xFFE32636),
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(20.dp))

        if (testDriveRequests.isEmpty()) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFF15171C)
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "No tienes solicitudes registradas",
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Cuando agendes una prueba de manejo, aparecerá aquí.",
                        color = Color(0xFFB8BEC7),
                        fontSize = 12.sp
                    )
                }
            }
        } else {
            testDriveRequests.forEach { request ->
                val requestedVehicle = vehicles.firstOrNull {
                    it.id == request.vehicleId
                }

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFF15171C)
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(18.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "PRUEBA DE MANEJO",
                                color = Color(0xFFE32636),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp
                            )

                            Text(
                                text = "#${request.id}",
                                color = Color(0xFF858B95),
                                fontSize = 11.sp
                            )
                        }

                        Text(
                            text = "${requestedVehicle?.brand ?: "Desconocido"} ${requestedVehicle?.model ?: ""}",
                            color = Color.White,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )

                        if (requestedVehicle != null) {
                            Text(
                                text = "${requestedVehicle.year} • ${requestedVehicle.category}",
                                color = Color(0xFFB8BEC7),
                                fontSize = 12.sp
                            )
                        }

                        Divider(color = Color(0xFF383A40))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text(
                                    "FECHA",
                                    color = Color(0xFF858B95),
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    request.appointmentDate,
                                    color = Color.White,
                                    fontSize = 15.sp
                                )
                            }

                            Column {
                                Text(
                                    "HORA",
                                    color = Color(0xFF858B95),
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    request.appointmentTime,
                                    color = Color.White,
                                    fontSize = 15.sp
                                )
                            }
                        }

                        Divider(color = Color(0xFF383A40))

                        Text(
                            text = "INFORMACIÓN DEL CLIENTE",
                            color = Color(0xFFB8BEC7),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )

                        Text(
                            "Cliente: ${request.customerName}",
                            color = Color.White,
                            fontSize = 13.sp
                        )

                        Text(
                            "Teléfono: ${request.customerPhone}",
                            color = Color(0xFFB8BEC7),
                            fontSize = 12.sp
                        )

                        Text(
                            "Correo: ${request.customerEmail}",
                            color = Color(0xFFB8BEC7),
                            fontSize = 12.sp
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        OutlinedButton(
                            onClick = {
                                requestToEdit = request
                                editName = request.customerName
                                editPhone = request.customerPhone
                                editEmail = request.customerEmail
                                editDate = request.appointmentDate
                                editTime = request.appointmentTime
                                editSelectedTime = request.appointmentTime

                                editDateMillis = try {
                                    java.text.SimpleDateFormat(
                                        "dd/MM/yyyy",
                                        Locale.getDefault()
                                    ).apply {
                                        timeZone = java.util.TimeZone.getTimeZone("UTC")
                                    }.parse(request.appointmentDate)?.time
                                } catch (e: Exception) {
                                    null
                                }
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("EDITAR SOLICITUD")
                        }

                        Button(
                            onClick = {
                                requestToDelete = request
                            },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFFE32636)
                            )
                        ) {
                            Text("CANCELAR SOLICITUD")
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))
            }
        }
    }
    }

    if (requestToEdit != null) {

        Text(
            text = "ESTADO DE EDICIÓN ACTIVADO",
            color = Color.Red,
            fontSize = 18.sp
        )

        AlertDialog(
            onDismissRequest = {
                requestToEdit = null
            },
            title = {
                Text("Editar solicitud")
            },
            text = {
                Column {
                    OutlinedTextField(
                        value = editName,
                        onValueChange = { editName = it },
                        label = { Text("Nombre del cliente") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    Spacer(Modifier.height(8.dp))

                    OutlinedTextField(
                        value = editPhone,
                        onValueChange = { newValue ->
                            if (newValue.length <= 10 && newValue.all { char ->
                                    char.isDigit()
                                }) {
                                editPhone = newValue
                            }
                        },
                        label = { Text("Teléfono") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    Spacer(Modifier.height(8.dp))

                    OutlinedTextField(
                        value = editEmail,
                        onValueChange = { editEmail = it },
                        label = { Text("Correo electrónico") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    Spacer(Modifier.height(8.dp))

                    OutlinedTextField(
                        value = editDate,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Fecha de la prueba") },
                        trailingIcon = {
                            TextButton(
                                onClick = {
                                    showEditDatePicker = true
                                }
                            ) {
                                Text("📅")
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                showEditDatePicker = true
                            },
                        singleLine = true
                    )


                    Spacer(Modifier.height(12.dp))

                    Text(
                        "Selecciona un horario",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(Modifier.height(8.dp))

                    editAvailableTimes.chunked(2).forEach { rowTimes ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            rowTimes.forEach { slot ->
                                Button(
                                    onClick = {
                                        editSelectedTime = slot
                                        editTime = slot
                                    },
                                    enabled = slot !in editReservedTimes,
                                    modifier = Modifier.weight(1f),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = when {
                                            slot in editReservedTimes ->
                                                MaterialTheme.colorScheme.errorContainer

                                            editSelectedTime == slot ->
                                                MaterialTheme.colorScheme.primary

                                            else ->
                                                MaterialTheme.colorScheme.surfaceVariant
                                        }
                                    )
                                ) {
                                    Text(
                                        if (slot in editReservedTimes) {
                                            "OCUPADO · $slot"
                                        } else {
                                            slot
                                        }
                                    )
                                }
                            }

                            if (rowTimes.size == 1) {
                                Spacer(Modifier.weight(1f))
                            }
                        }
                    }


                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        val isEmailValid =
                            android.util.Patterns.EMAIL_ADDRESS
                                .matcher(editEmail)
                                .matches()

                        if (
                            editName.isNotBlank() &&
                            editPhone.length == 10 &&
                            editEmail.isNotBlank() &&
                            isEmailValid &&
                            editDate.isNotBlank() &&
                            editTime.isNotBlank() &&
                            editTime !in editReservedTimes
                        ) {
                            requestToEdit?.let { request ->
                                testDriveViewModel.updateTestDrive(
                                    request.copy(
                                        customerName = editName,
                                        customerPhone = editPhone,
                                        customerEmail = editEmail,
                                        appointmentDate = editDate,
                                        appointmentTime = editTime
                                    )
                                )
                            }

                            requestToEdit = null
                        }
                    }
                ) {
                    Text("GUARDAR CAMBIOS")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        requestToEdit = null
                    }
                ) {
                    Text("CANCELAR")
                }
            }
        )
    }

    if (requestToDelete != null) {
        AlertDialog(
            onDismissRequest = {
                requestToDelete = null
            },
            title = {
                Text("Cancelar solicitud")
            },
            text = {
                Text(
                    "¿Estás seguro de que deseas cancelar esta solicitud de prueba de manejo?"
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        requestToDelete?.let { request ->
                            testDriveViewModel.deleteTestDrive(request.id)
                        }
                        requestToDelete = null
                    }
                ) {
                    Text("SÍ, CANCELAR")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        requestToDelete = null
                    }
                ) {
                    Text("VOLVER")
                }
            }
        )
    }

    if (showEditDatePicker) {
        key(editDateMillis) {
            val editDatePickerState = rememberDatePickerState(
                initialSelectedDateMillis = editDateMillis,
                selectableDates = object : SelectableDates {

                    override fun isSelectableDate(
                        utcTimeMillis: Long
                    ): Boolean {
                        val utcTimeZone =
                            java.util.TimeZone.getTimeZone("UTC")

                        val calendar =
                            java.util.Calendar.getInstance(utcTimeZone)

                        calendar.timeInMillis = utcTimeMillis

                        val selectedDay = calendar.get(
                            java.util.Calendar.DAY_OF_WEEK
                        )

                        val localToday =
                            java.util.Calendar.getInstance()

                        val todayUtc =
                            java.util.Calendar.getInstance(utcTimeZone).apply {
                                set(
                                    java.util.Calendar.YEAR,
                                    localToday.get(java.util.Calendar.YEAR)
                                )
                                set(
                                    java.util.Calendar.MONTH,
                                    localToday.get(java.util.Calendar.MONTH)
                                )
                                set(
                                    java.util.Calendar.DAY_OF_MONTH,
                                    localToday.get(java.util.Calendar.DAY_OF_MONTH)
                                )
                                set(java.util.Calendar.HOUR_OF_DAY, 0)
                                set(java.util.Calendar.MINUTE, 0)
                                set(java.util.Calendar.SECOND, 0)
                                set(java.util.Calendar.MILLISECOND, 0)
                            }

                        val year = calendar.get(java.util.Calendar.YEAR)
                        val month = calendar.get(java.util.Calendar.MONTH)
                        val day = calendar.get(
                            java.util.Calendar.DAY_OF_MONTH
                        )

                        return utcTimeMillis >= todayUtc.timeInMillis &&
                                selectedDay != java.util.Calendar.SUNDAY &&
                                !isColombianHoliday(year, month, day)
                    }
                }
            )

            DatePickerDialog(
                onDismissRequest = {
                    showEditDatePicker = false
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            editDateMillis =
                                editDatePickerState.selectedDateMillis

                            editDate = editDateMillis?.let {
                                java.text.SimpleDateFormat(
                                    "dd/MM/yyyy",
                                    Locale.getDefault()
                                ).apply {
                                    timeZone =
                                        java.util.TimeZone.getTimeZone("UTC")
                                }.format(java.util.Date(it))
                            } ?: ""

                            editTime = ""
                            editSelectedTime = ""
                            showEditDatePicker = false
                        }
                    ) {
                        Text("Aceptar")
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = {
                            showEditDatePicker = false
                        }
                    ) {
                        Text("Cancelar")
                    }
                }
            ) {
                DatePicker(
                    state = editDatePickerState,
                    title = {
                        Text("Selecciona la nueva fecha")
                    }
                )
            }
        }
    }
}



fun isColombianHoliday(year: Int, month: Int, day: Int): Boolean {

    val holidays = mutableSetOf<String>()

    fun dateKey(calendar: java.util.Calendar): String {
        return String.format(
            Locale.US,
            "%04d-%02d-%02d",
            calendar.get(java.util.Calendar.YEAR),
            calendar.get(java.util.Calendar.MONTH) + 1,
            calendar.get(java.util.Calendar.DAY_OF_MONTH)
        )
    }

    fun createDate(month: Int, day: Int): java.util.Calendar {
        return java.util.Calendar.getInstance(
            java.util.TimeZone.getTimeZone("UTC")
        ).apply {
            clear()
            set(year, month, day)
        }
    }

    fun addHoliday(calendar: java.util.Calendar) {
        holidays.add(dateKey(calendar))
    }

    // Festivos que se trasladan al lunes (Ley Emiliani)
    fun addMondayHoliday(month: Int, day: Int) {
        val calendar = createDate(month, day)

        val daysUntilMonday =
            (2 - calendar.get(java.util.Calendar.DAY_OF_WEEK) + 7) % 7

        calendar.add(java.util.Calendar.DAY_OF_MONTH, daysUntilMonday)
        addHoliday(calendar)
    }

    // Festivos de fecha fija
    val fixedHolidays = listOf(
        0 to 1,   // Año Nuevo
        4 to 1,   // Día del Trabajo
        6 to 20,  // Independencia de Colombia
        7 to 7,   // Batalla de Boyacá
        11 to 8,  // Inmaculada Concepción
        11 to 25  // Navidad
    )

    fixedHolidays.forEach { (month, day) ->
        addHoliday(createDate(month, day))
    }

    // Festivos trasladados al lunes
    val mondayHolidays = listOf(
        0 to 6,   // Reyes Magos
        2 to 19,  // San José
        5 to 29,  // San Pedro y San Pablo
        7 to 15,  // Asunción de la Virgen
        9 to 12,  // Día de la Raza
        10 to 1,  // Todos los Santos
        10 to 11  // Independencia de Cartagena
    )

    mondayHolidays.forEach { (month, day) ->
        addMondayHoliday(month, day)
    }

    // Cálculo del domingo de Pascua (algoritmo gregoriano)
    val a = year % 19
    val b = year / 100
    val c = year % 100
    val d = b / 4
    val e = b % 4
    val f = (b + 8) / 25
    val g = (b - f + 1) / 3
    val h = (19 * a + b - d - g + 15) % 30
    val i = c / 4
    val k = c % 4
    val l = (32 + 2 * e + 2 * i - h - k) % 7
    val m = (a + 11 * h + 22 * l) / 451

    val easterMonth = (h + l - 7 * m + 114) / 31
    val easterDay = ((h + l - 7 * m + 114) % 31) + 1

    val easter = createDate(easterMonth - 1, easterDay)

    // Jueves y Viernes Santo
    val holyThursday = easter.clone() as java.util.Calendar
    holyThursday.add(java.util.Calendar.DAY_OF_MONTH, -3)
    addHoliday(holyThursday)

    val goodFriday = easter.clone() as java.util.Calendar
    goodFriday.add(java.util.Calendar.DAY_OF_MONTH, -2)
    addHoliday(goodFriday)

    // Ascensión, Corpus Christi y Sagrado Corazón
    listOf(39, 60, 68).forEach { days ->
        val calendar = easter.clone() as java.util.Calendar
        calendar.add(java.util.Calendar.DAY_OF_MONTH, days)

        val daysUntilMonday =
            (2 - calendar.get(java.util.Calendar.DAY_OF_WEEK) + 7) % 7

        calendar.add(java.util.Calendar.DAY_OF_MONTH, daysUntilMonday)
        addHoliday(calendar)
    }

    // Comprobar la fecha consultada
    val selectedDate = createDate(month, day)

    return dateKey(selectedDate) in holidays
}

// ============================================================
// AGENDAR PRUEBA DE MANEJO
// ============================================================

@Composable
fun AppointmentScreen(vehicle: Vehicle, testDriveViewModel: TestDriveViewModel, testDriveRequests: List<TestDriveEntity>, onBack: () -> Unit) {
    var name by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }

    var date by remember { mutableStateOf("") }
    var time by remember { mutableStateOf("") }

    var showEmailError by remember {
        mutableStateOf(false)
    }

    val isEmailValid = android.util.Patterns.EMAIL_ADDRESS
        .matcher(email)
        .matches()

    var showValidationDialog by remember {
        mutableStateOf(false)
    }

    var showFieldErrors by remember {
        mutableStateOf(false)
    }

    var selectedDate by remember { mutableStateOf<Long?>(null) }
    var selectedTime by remember { mutableStateOf("") }
    val availableTimes = remember(selectedDate) {
        val calendar = java.util.Calendar.getInstance(
            java.util.TimeZone.getTimeZone("UTC")
        ).apply {
            timeInMillis = selectedDate ?: System.currentTimeMillis()
        }

        val dayOfWeek = calendar.get(java.util.Calendar.DAY_OF_WEEK)

        val startHour = 8
        val endHour = if (
            dayOfWeek == java.util.Calendar.SATURDAY
        ) 12 else 17

        buildList {
            for (hour in startHour until endHour) {
                add(String.format(Locale.getDefault(), "%02d:00", hour))
                add(String.format(Locale.getDefault(), "%02d:30", hour))
            }
        }
    }

    val selectedDateString = selectedDate?.let {
        java.text.SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).apply {
            timeZone = java.util.TimeZone.getTimeZone("UTC")
        }.format(java.util.Date(it))
    } ?: ""

    val reservedTimes = testDriveRequests
        .filter { it.appointmentDate == selectedDateString }
        .map { it.appointmentTime }
        .toSet()

    var confirmation by remember { mutableStateOf(false) }

    val datePickerState = rememberDatePickerState(
        selectableDates = object : SelectableDates {

            override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                val utcTimeZone = java.util.TimeZone.getTimeZone("UTC")

                val calendar = java.util.Calendar.getInstance(utcTimeZone)
                calendar.timeInMillis = utcTimeMillis

                val selectedDay = calendar.get(
                    java.util.Calendar.DAY_OF_WEEK
                )

                val localToday = java.util.Calendar.getInstance()

                val todayUtc = java.util.Calendar.getInstance(utcTimeZone).apply {
                    set(
                        java.util.Calendar.YEAR,
                        localToday.get(java.util.Calendar.YEAR)
                    )
                    set(
                        java.util.Calendar.MONTH,
                        localToday.get(java.util.Calendar.MONTH)
                    )
                    set(
                        java.util.Calendar.DAY_OF_MONTH,
                        localToday.get(java.util.Calendar.DAY_OF_MONTH)
                    )

                    set(java.util.Calendar.HOUR_OF_DAY, 0)
                    set(java.util.Calendar.MINUTE, 0)
                    set(java.util.Calendar.SECOND, 0)
                    set(java.util.Calendar.MILLISECOND, 0)
                }

                val selectedYear = calendar.get(java.util.Calendar.YEAR)
                val selectedMonth = calendar.get(java.util.Calendar.MONTH)
                val selectedDayOfMonth = calendar.get(java.util.Calendar.DAY_OF_MONTH)

                return utcTimeMillis >= todayUtc.timeInMillis &&
                        selectedDay != java.util.Calendar.SUNDAY &&
                        !isColombianHoliday(
                            selectedYear,
                            selectedMonth,
                            selectedDayOfMonth
                        )
            }
        }
    )
    var showDatePicker by remember { mutableStateOf(false) }

    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = {
                showDatePicker = false
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        selectedDate = datePickerState.selectedDateMillis

                        date = selectedDate?.let {
                            java.text.SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).apply {
                                timeZone = java.util.TimeZone.getTimeZone("UTC")
                            }.format(java.util.Date(it))
                        } ?: ""

                        selectedTime = ""
                        time = ""
                        showDatePicker = false
                        confirmation = false
                    }
                ) {
                    Text("Aceptar")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showDatePicker = false
                    }
                ) {
                    Text("Cancelar")
                }
            }
        ) {
            DatePicker(
                state = datePickerState,
                title = {
                    Text("Selecciona la fecha de tu prueba",
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth().padding(top = 12.dp))
                }
            )
        }
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.TopCenter
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .widthIn(max = 600.dp)
                .verticalScroll(rememberScrollState())
                .padding(20.dp)
    ) {
            TextButton(onClick = onBack) { Text("← Volver") }
            Text(
                "Prueba de manejo", style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.height(8.dp))
            Text(
                "${vehicle.brand} ${vehicle.model} • ${vehicle.year}",
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(Modifier.height(8.dp))
            Text("Completa tus datos para registrar tu solicitud.")
            Spacer(Modifier.height(20.dp))

            OutlinedTextField(
                value = name,
                onValueChange = {
                    name = it
                    confirmation = false
                },
                label = { Text("Nombre completo") },
                trailingIcon = {
                    if (showFieldErrors && name.isBlank()) {
                        Text("⚠️")
                    }
                },
                isError = showFieldErrors && name.isBlank(),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(Modifier.height(12.dp))
            OutlinedTextField(
                value = phone,
                onValueChange = { newValue ->
                    if (newValue.length <= 10 && newValue.all { it.isDigit() }) {
                        phone = newValue
                        confirmation = false
                    }
                },
                label = { Text("Teléfono de contacto") },
                trailingIcon = {
                    if (showFieldErrors && phone.isBlank()) {
                        Text("⚠️")
                    }
                },
                isError = showFieldErrors && phone.isBlank(),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number
                ),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            Spacer(Modifier.height(12.dp))

            OutlinedTextField(
                value = email,
                onValueChange = {
                    email = it
                    confirmation = false
                },
                label = { Text("Correo electrónico") },
                trailingIcon = {
                    if (
                        showFieldErrors &&
                        (email.isBlank() || !isEmailValid)
                    ) {
                        Text("⚠️")
                    }
                },
                isError = showFieldErrors &&
                        (email.isBlank() || !isEmailValid),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            if (showFieldErrors && email.isNotBlank() && !isEmailValid) {
                Text(
                    "Correo inválido. Ejemplo: usuario@gmail.com ó usuario@hotmail.com",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }
            Spacer(Modifier.height(12.dp))

            OutlinedTextField(
                value = selectedDate?.let {
                    java.text.SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                        .format(java.util.Date(it))
                } ?: "",
                onValueChange = {},
                readOnly = true,
                label = { Text("Fecha de la prueba") },
                placeholder = { Text("Selecciona una fecha") },
                trailingIcon = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (showFieldErrors && date.isBlank()) {
                            Text("⚠️")
                        }

                        TextButton(
                            onClick = { showDatePicker = true }
                        ) {
                            Text("📅")
                        }
                    }
                },
                isError = showFieldErrors && date.isBlank(),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { showDatePicker = true },
                singleLine = true
            )
            Spacer(Modifier.height(12.dp))

            Text(
                "Selecciona un horario",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            if (showFieldErrors && time.isBlank()) {
                Text(
                    "⚠️ Debes seleccionar un horario",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Spacer(Modifier.height(12.dp))

            if (selectedDate == null) {
                Text("Primero selecciona una fecha.")
            } else {
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    availableTimes.chunked(2).forEach { rowTimes ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            rowTimes.forEach { slot ->
                                Button(
                                    onClick = {
                                        selectedTime = slot
                                        time = slot
                                        confirmation = false
                                    },
                                    enabled = slot !in reservedTimes,
                                    modifier = Modifier.weight(1f),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = when {
                                            slot in reservedTimes -> MaterialTheme.colorScheme.errorContainer
                                            selectedTime == slot -> MaterialTheme.colorScheme.primary
                                            else -> MaterialTheme.colorScheme.surfaceVariant
                                        }
                                    )
                                ) {
                                    Text(
                                        if (slot in reservedTimes) "OCUPADO · $slot" else slot
                                    )
                                }
                            }

                            if (rowTimes.size == 1) {
                                Spacer(Modifier.weight(1f))
                            }
                        }
                    }
                }
            }

            Spacer(Modifier.height(20.dp))

            Button(
                onClick = {
                    if (
                        name.isNotBlank() &&
                        phone.length == 10 &&
                        isEmailValid &&
                        date.isNotBlank() &&
                        time.isNotBlank()
                    ) {
                        testDriveViewModel.addTestDrive(
                            TestDriveEntity(
                                vehicleId = vehicle.id,
                                customerName = name,
                                customerPhone = phone,
                                customerEmail = email,
                                appointmentDate = date,
                                appointmentTime = time
                            )
                        )
                        showFieldErrors = false
                        confirmation = true
                    } else {
                        confirmation = false
                        showValidationDialog = true
                        showFieldErrors = true
                    }
                },
                modifier = Modifier.fillMaxWidth().height(52.dp)
            ) {
                Text("REGISTRAR SOLICITUD")
            }


            if (confirmation) {
                Spacer(Modifier.height(16.dp))

                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth(0.80f),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                "Solicitud registrada",
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center
                            )

                            Spacer(Modifier.height(6.dp))

                            Text(
                                "La solicitud para el ${vehicle.brand} ${vehicle.model} fue registrada exitosamente.",
                                modifier = Modifier.fillMaxWidth(),
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }
        }
    }

    if (showValidationDialog) {
        AlertDialog(
            onDismissRequest = {
                showValidationDialog = false
            },
            title = {
                Text("Campos incompletos")
            },
            text = {
                Text(
                    "Por favor, rellene todos los campos de la solicitud de prueba de manejo."
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showValidationDialog = false
                    }
                ) {
                    Text("Aceptar")
                }
            }
        )
    }
}

// ============================================================
// SIMULADOR DE FINANCIACIÓN
// ============================================================

@Composable
fun FinanceScreen(vehicle: Vehicle, onBack: () -> Unit) {
    var downPayment by remember { mutableStateOf("50000000") }
    var monthlyRate by remember { mutableStateOf("1.2") }
    var months by remember { mutableStateOf("60") }
    var calculate by remember { mutableStateOf(false) }

    val initialPayment = downPayment.toDoubleOrNull() ?: 0.0
    val rate = (monthlyRate.replace(",", ".").toDoubleOrNull() ?: 0.0) / 100.0
    val numberOfMonths = months.toIntOrNull() ?: 0
    val financedAmount = (vehicle.price.toDouble() - initialPayment).coerceAtLeast(0.0)

    val monthlyPayment = when {
        numberOfMonths <= 0 -> 0.0
        rate <= 0.0 -> financedAmount / numberOfMonths
        else -> {
            val factor = (1.0 + rate).pow(numberOfMonths)
            financedAmount * rate * factor / (factor - 1.0)
        }
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.TopCenter
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .widthIn(max = 600.dp)
                .verticalScroll(rememberScrollState())
                .padding(20.dp)
        ) {
        TextButton(onClick = onBack) { Text("← Volver") }
        Text("Simulador de financiación", style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(8.dp))
        Text("${vehicle.brand} ${vehicle.model}", style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.primary)
        Spacer(Modifier.height(6.dp))
        Text("Precio del vehículo: ${formatPrice(vehicle.price)}")
        Spacer(Modifier.height(20.dp))

        OutlinedTextField(
            value = downPayment,
            onValueChange = { downPayment = it; calculate = false },
            label = { Text("Cuota inicial (COP)") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
        Spacer(Modifier.height(12.dp))
        OutlinedTextField(
            value = monthlyRate,
            onValueChange = { monthlyRate = it; calculate = false },
            label = { Text("Tasa mensual (%)") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
        Spacer(Modifier.height(12.dp))
        OutlinedTextField(
            value = months,
            onValueChange = { months = it; calculate = false },
            label = { Text("Plazo en meses") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
        Spacer(Modifier.height(20.dp))
        Button(
            onClick = {
                calculate = numberOfMonths > 0 &&
                        initialPayment >= 0.0 &&
                        initialPayment <= vehicle.price.toDouble() &&
                        rate >= 0.0
            },
            modifier = Modifier.fillMaxWidth().height(52.dp)
        ) {
            Text("CALCULAR CUOTA")
        }

        if (calculate) {
            Spacer(Modifier.height(20.dp))
            Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(18.dp)) {
                Column(Modifier.padding(20.dp)) {
                    Text("Tu cuota mensual estimada",
                        style = MaterialTheme.typography.titleMedium)
                    Spacer(Modifier.height(8.dp))
                    Text(formatPrice(monthlyPayment.toLong()),
                        style = MaterialTheme.typography.headlineMedium,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(12.dp))
                    Text("Monto financiado: ${formatPrice(financedAmount.toLong())}")
                    Text("Plazo: $numberOfMonths meses")
                    Spacer(Modifier.height(12.dp))
                    Text("Estimación ilustrativa. No incluye seguros, comisiones ni otros cargos. La tasa y las condiciones definitivas dependen de la entidad financiera.")
                }
            }
        }
    }
    }
}

// ============================================================
// FORMATO DE PRECIOS
// ============================================================

fun formatPrice(value: Long): String {
    val formatted = String.format(Locale.US, "%,d", value).replace(",", ".")
    return "$$formatted COP"
}
