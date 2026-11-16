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
                    onAppointment = { navController.navigate(Routes.APPOINTMENT) },
                    onFinance = { navController.navigate(Routes.FINANCE) }

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
                .height(390.dp)
        ) {
            Image(
                painter = painterResource(R.drawable.audi_r8),
                contentDescription = "Deportivo destacado de AutoLux",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
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

        vehicles.firstOrNull()?.let { vehicle ->
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
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column {
            Box {
                Image(
                    painter = painterResource(vehicle.image),
                    contentDescription = "${vehicle.brand} ${vehicle.model}",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(215.dp)
                        .clickable(onClick = onDetails),
                    contentScale = ContentScale.Crop
                )
                Surface(
                    modifier = Modifier.align(Alignment.TopStart).padding(12.dp),
                    color = MaterialTheme.colorScheme.primary,
                    shape = RoundedCornerShape(50)
                ) {
                    Text(
                        "DESTACADO",
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp),
                        color = MaterialTheme.colorScheme.onPrimary,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold
                    )
                }
                TextButton(
                    onClick = onFavorite,
                    modifier = Modifier.align(Alignment.TopEnd).padding(4.dp)
                ) {
                    Text(
                        if (isFavorite) "♥" else "♡",
                        fontSize = 30.sp,
                        color = if (isFavorite) MaterialTheme.colorScheme.primary else Color.White
                    )
                }
            }
            Column(Modifier.padding(16.dp)) {
                Text(
                    vehicle.brand.uppercase(),
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold
                )
                Spacer(Modifier.height(4.dp))
                Text(vehicle.model, style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(6.dp))
                Text(
                    "${vehicle.year}  •  ${vehicle.category}",
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
                Spacer(Modifier.height(10.dp))
                Text(vehicle.description, style = MaterialTheme.typography.bodyMedium)
                Spacer(Modifier.height(12.dp))
                Text(
                    formatPrice(vehicle.price),
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
                Spacer(Modifier.height(14.dp))
                Button(onClick = onFinance, modifier = Modifier.fillMaxWidth()) {
                    Text("SIMULAR FINANCIACIÓN")
                }
                Spacer(Modifier.height(6.dp))
                OutlinedButton(onClick = onDetails, modifier = Modifier.fillMaxWidth()) {
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
        modifier = modifier.height(170.dp).clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp)
    ) {
        Box(Modifier.fillMaxSize()) {
            Image(
                painter = painterResource(image),
                contentDescription = title,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
            Box(
                Modifier.fillMaxSize().background(
                    Brush.verticalGradient(
                        listOf(Color.Transparent, Color.Black.copy(alpha = 0.86f))
                    )
                )
            )
            Text(
                "$title  ›",
                modifier = Modifier.align(Alignment.BottomStart).padding(12.dp),
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
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column {
            Image(
                painter = painterResource(vehicle.image),
                contentDescription = "${vehicle.brand} ${vehicle.model}",
                modifier = Modifier.fillMaxWidth().height(190.dp),
                contentScale = ContentScale.Crop
            )
            Column(Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(Modifier.weight(1f)) {
                        Text(vehicle.brand.uppercase(),
                            color = MaterialTheme.colorScheme.primary,
                            style = MaterialTheme.typography.labelMedium)
                        Text(vehicle.model, style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold)
                    }
                    TextButton(onClick = onFavorite) {
                        Text(
                            if (isFavorite) "♥" else "♡",
                            fontSize = 28.sp,
                            color = if (isFavorite) MaterialTheme.colorScheme.primary
                            else MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
                Text("${vehicle.year} • ${vehicle.category}",
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f))
                Spacer(Modifier.height(8.dp))
                Text(vehicle.description, style = MaterialTheme.typography.bodyMedium)
                Spacer(Modifier.height(12.dp))
                Text(formatPrice(vehicle.price),
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(12.dp))
                Button(onClick = onFinance, modifier = Modifier.fillMaxWidth()) {
                    Text("SIMULAR FINANCIACIÓN")
                }
                OutlinedButton(onClick = onAppointment, modifier = Modifier.fillMaxWidth()) {
                    Text("AGENDAR PRUEBA DE MANEJO")
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
    Column(
        modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp)
    ) {
        Text("Mis favoritos", style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(8.dp))
        Text("Tu selección personal de vehículos.",
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f))
        Spacer(Modifier.height(20.dp))

        if (vehicles.isEmpty()) {
            Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(18.dp)) {
                Column(
                    modifier = Modifier.fillMaxWidth().padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("♡", fontSize = 52.sp, color = MaterialTheme.colorScheme.primary)
                    Spacer(Modifier.height(12.dp))
                    Text("Aún no tienes favoritos",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(8.dp))
                    Text("Guarda los vehículos que más te gusten.")
                    Spacer(Modifier.height(16.dp))
                    Button(onClick = onCatalog) { Text("EXPLORAR CATÁLOGO") }
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

// ============================================================
// PERFIL
// ============================================================

@Composable
fun ProfileScreen(
    testDriveRequests: List<TestDriveEntity>,
    vehicles: List<Vehicle>,
    testDriveViewModel: TestDriveViewModel,
    onAppointment: () -> Unit,
    onFinance: () -> Unit
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

    var requestToDelete by remember {
        mutableStateOf<TestDriveEntity?>(null)
    }

    val editReservedTimes = testDriveRequests
        .filter {
            it.appointmentDate == editDate &&
                    it.id != requestToEdit?.id
        }
        .map { it.appointmentTime }
        .toSet()

    Column(
        modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(20.dp)
    ) {
        Text("Bienvenido a AutoLux", style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(8.dp))

        Text(
            "Tu experiencia premium comienza aquí.",
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
        )

        Spacer(Modifier.height(20.dp))

        Text(
            "Mis solicitudes de prueba de manejo",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )

        Spacer(Modifier.height(12.dp))

        if (testDriveRequests.isEmpty()) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp)
            ) {
                Column(Modifier.padding(20.dp)) {
                    Text(
                        "No tienes solicitudes registradas todavía.",
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                }
            }
        } else {
            testDriveRequests.forEach { request ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp)
                ) {
                    Column(Modifier.padding(16.dp)) {
                        Text(
                            "Solicitud de prueba de manejo",
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(Modifier.height(8.dp))
                        Text("Cliente: ${request.customerName}")
                        Text("Teléfono: ${request.customerPhone}")
                        Text("Correo: ${request.customerEmail}")
                        val requestedVehicle = vehicles.firstOrNull { it.id == request.vehicleId }

                        Text(
                            "Vehículo: ${requestedVehicle?.brand ?: "Desconocido"} ${requestedVehicle?.model ?: ""} (${requestedVehicle?.year ?: ""})",
                            fontWeight = FontWeight.SemiBold
                        )
                        Text("Fecha: ${request.appointmentDate}")
                        Text("Hora: ${request.appointmentTime}")
                    }
                }
                Spacer(Modifier.height(12.dp))
                Spacer(Modifier.height(12.dp))

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
                        containerColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text("CANCELAR SOLICITUD")
                }
            }
        }

        Spacer(Modifier.height(24.dp))

        Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(18.dp)) {
            Column(Modifier.padding(20.dp)) {
                Text("Prueba de manejo", style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(8.dp))
                Text("Agenda una experiencia para conocer tu próximo vehículo.")
                Spacer(Modifier.height(14.dp))
                Button(onClick = onAppointment, modifier = Modifier.fillMaxWidth()) {
                    Text("AGENDAR PRUEBA")
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(18.dp)) {
            Column(Modifier.padding(20.dp)) {
                Text("Financiación", style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(8.dp))
                Text("Calcula una cuota mensual estimada para tu vehículo.")
                Spacer(Modifier.height(14.dp))
                Button(onClick = onFinance, modifier = Modifier.fillMaxWidth()) {
                    Text("SIMULAR FINANCIACIÓN")
                }
            }
        }

        Spacer(Modifier.height(24.dp))
        Text("AUTOLUX • CONCESIONARIO PREMIUM",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.primary)
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


    if (requestToEdit != null) {
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
                    Text("Selecciona la fecha de tu prueba")
                }
            )
        }
    }

    Column(
        modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(20.dp)
    ) {
        TextButton(onClick = onBack) { Text("← Volver") }
        Text("Prueba de manejo", style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(8.dp))
        Text("${vehicle.brand} ${vehicle.model} • ${vehicle.year}",
            color = MaterialTheme.colorScheme.primary,
            style = MaterialTheme.typography.titleMedium)
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
                                Text(if (slot in reservedTimes) "OCUPADO · $slot" else slot
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
            Card {
                Column(Modifier.padding(16.dp)) {
                    Text("Solicitud registrada",
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(6.dp))
                    Text("La solicitud para el ${vehicle.brand} ${vehicle.model} quedó registrada en esta sesión. Esta demostración no envía los datos a un concesionario.")
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

    Column(
        modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(20.dp)
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

// ============================================================
// FORMATO DE PRECIOS
// ============================================================

fun formatPrice(value: Long): String {
    val formatted = String.format(Locale.US, "%,d", value).replace(",", ".")
    return "$$formatted COP"
}
