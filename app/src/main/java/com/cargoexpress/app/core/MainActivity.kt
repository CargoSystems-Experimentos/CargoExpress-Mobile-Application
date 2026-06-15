package com.cargoexpress.app.core

import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.cargoexpress.app.core.common.Routes
import com.cargoexpress.app.core.data.remote.login.LoginService
import com.cargoexpress.app.core.data.remote.register.RegisterService
import com.cargoexpress.app.core.data.remote.trip.TripService
import com.cargoexpress.app.core.data.remote.user.ClientService
import com.cargoexpress.app.core.data.remote.ongoingtrip.OngoingTripService
import com.cargoexpress.app.core.data.remote.user.EntrepreneurService
import com.cargoexpress.app.core.data.remote.user.UserService
import com.cargoexpress.app.core.data.remote.vehicle.VehicleService
import com.cargoexpress.app.core.data.repository.ClientRepository
import com.cargoexpress.app.core.data.repository.EntrepreneurRepository
import com.cargoexpress.app.core.data.repository.LoginRepository
import com.cargoexpress.app.core.data.repository.RegisterRepository
import com.cargoexpress.app.core.data.repository.TripRepository
import com.cargoexpress.app.core.data.repository.UserRepository
import com.cargoexpress.app.core.data.repository.VehicleRepository
import com.cargoexpress.app.core.presentation.vehicle.VehicleListViewModel
import com.cargoexpress.app.core.presentation.login.LoginScreen
import com.cargoexpress.app.core.presentation.login.LoginViewModel
import com.cargoexpress.app.core.presentation.register.RegisterScreen
import com.cargoexpress.app.core.presentation.register.RegisterViewModel
import com.cargoexpress.app.core.presentation.vehicle.VehicleListScreen
import com.cargoexpress.app.core.presentation.trip.TripManagementScreen
import com.cargoexpress.app.core.ui.theme.CargoexpressTheme
import com.cargoexpress.app.core.common.Constants
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.security.SecureRandom
import java.security.cert.X509Certificate
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.DirectionsBusFilled
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.twotone.LocalShipping
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.currentBackStackEntryAsState
import com.cargoexpress.app.R
import com.cargoexpress.app.core.data.remote.alert.AlertService
import com.cargoexpress.app.core.data.remote.driver.DriverService
import com.cargoexpress.app.core.data.remote.expense.ExpenseService
import com.cargoexpress.app.core.data.repository.AlertRepository
import com.cargoexpress.app.core.data.repository.DriverRepository
import com.cargoexpress.app.core.data.repository.ExpenseRepository
import com.cargoexpress.app.core.data.repository.OngoingTripRepository
import com.cargoexpress.app.core.presentation.alert.AlertScreen
import com.cargoexpress.app.core.presentation.driver.driverList.DriverListScreen
import com.cargoexpress.app.core.presentation.driver.driverList.DriverListViewModel
import com.cargoexpress.app.core.presentation.driver.driverList.registerDriver.RegisterDriverScreen
import com.cargoexpress.app.core.presentation.driver.driverList.registerDriver.RegisterDriverViewModel
import com.cargoexpress.app.core.presentation.gps.GpsScreen
import com.cargoexpress.app.core.presentation.profile.ProfileScreen
import com.cargoexpress.app.core.presentation.profile.ProfileViewModel
import com.cargoexpress.app.core.presentation.trip.detailsTrip.TripDetailScreen
import com.cargoexpress.app.core.presentation.trip.editTrip.TripEditScreen
import com.cargoexpress.app.core.presentation.trip.registerExpense.RegisterExpenseScreen
import com.cargoexpress.app.core.presentation.trip.registerExpense.RegisterExpenseViewModel
//import com.cargoexpress.app.core.presentation.record.registerExpense.RegisterExpenseScreen
import com.cargoexpress.app.core.presentation.trip.registerTrip.RegisterTripScreen
import com.cargoexpress.app.core.presentation.trip.registerTrip.RegisterTripViewModel
import com.cargoexpress.app.core.presentation.trip.registerTrip.RegisterTripViewModelFactory
import com.cargoexpress.app.core.presentation.vehicle.registerVehicle.RegisterVehicleScreen
import com.cargoexpress.app.core.presentation.vehicle.registerVehicle.RegisterVehicleViewModel
import com.cargoexpress.app.core.presentation.vehicle.editVehicle.EditVehicleScreen
import com.cargoexpress.app.core.presentation.vehicle.editVehicle.EditVehicleViewModel
import com.cargoexpress.app.core.presentation.driver.driverList.editDriver.EditDriverScreen
import com.cargoexpress.app.core.presentation.driver.driverList.editDriver.EditDriverViewModel
import com.cargoexpress.app.core.presentation.statistics.StatisticsScreen
import com.cargoexpress.app.core.presentation.terms.TermsAndConditionsScreen
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBarDefaults
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import com.cargoexpress.app.core.data.remote.auditlog.AuditLogService
import com.cargoexpress.app.core.data.repository.AuditLogRepository
import com.cargoexpress.app.core.presentation.home.HomeScreen
import com.cargoexpress.app.core.presentation.trip.editExpense.EditExpenseScreen
import com.cargoexpress.app.core.presentation.trip.editExpense.EditExpenseViewModel
import com.cargoexpress.app.core.presentation.trip.editExpense.EditExpenseViewModelFactory
import com.cargoexpress.app.core.presentation.fleet.FleetScreen

class MainActivity : ComponentActivity() {

    companion object {
        const val PICK_IMAGE_REQUEST = 1
    }

    private fun unsafeHttpClient(): OkHttpClient {
        val trustAll = arrayOf<TrustManager>(object : X509TrustManager {
            override fun checkClientTrusted(chain: Array<X509Certificate>, authType: String) {}
            override fun checkServerTrusted(chain: Array<X509Certificate>, authType: String) {}
            override fun getAcceptedIssuers(): Array<X509Certificate> = arrayOf()
        })
        val sslContext = SSLContext.getInstance("SSL").apply {
            init(null, trustAll, SecureRandom())
        }
        return OkHttpClient.Builder()
            .sslSocketFactory(sslContext.socketFactory, trustAll[0] as X509TrustManager)
            .hostnameVerifier { _, _ -> true }
            .build()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {

        val httpClient = unsafeHttpClient()

        fun <T> buildService(serviceClass: Class<T>): T = Retrofit.Builder()
            .baseUrl(Constants.BASE_URL)
            .client(httpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(serviceClass)

        val loginService = buildService(LoginService::class.java)
        val registerService = buildService(RegisterService::class.java)
        val userService = buildService(UserService::class.java)
        val clientService = buildService(ClientService::class.java)
        val entrepreneurService = buildService(EntrepreneurService::class.java)
        val vehicleService = buildService(VehicleService::class.java)
        val tripService = buildService(TripService::class.java)
        val driverService = buildService(DriverService::class.java)
        val expenseService = buildService(ExpenseService::class.java)
        val ongoingTripService = buildService(OngoingTripService::class.java)

        val alertService = buildService(AlertService::class.java)
        val auditLogService = buildService(AuditLogService::class.java)

        val tripRepository = TripRepository(tripService, expenseService)
        val clientRepository = ClientRepository(clientService)
        val auditLogRepository = AuditLogRepository(auditLogService)

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            CargoexpressTheme {
                val navController = rememberNavController()
                val loginViewModel = LoginViewModel(
                    navController,
                    LoginRepository(loginService),
                    UserRepository(userService),
                    EntrepreneurRepository(entrepreneurService),
                    ClientRepository(clientService)
                )
                val registerViewModel = RegisterViewModel(
                    RegisterRepository(registerService)
                )

                val vehicleRepository = VehicleRepository(vehicleService)
                val profileViewModel = ProfileViewModel(
                    navController,
                    EntrepreneurRepository(entrepreneurService),
                    ClientRepository(clientService)
                )
                val vehicleListViewModel = VehicleListViewModel(navController,vehicleRepository )
                //driver
                val driverRepository = DriverRepository(driverService)
                val driverListViewModel = DriverListViewModel(navController, driverRepository)

                //expense
                val expenseRepository = ExpenseRepository(expenseService)

                //vehicle


                //Trip
                val tripRepository = TripRepository(tripService, expenseService)
                val entrepreneurRepository = EntrepreneurRepository(entrepreneurService)
                val currentDestination = navController.currentBackStackEntryAsState().value?.destination?.route
                val currentRoute = navController.currentBackStackEntry?.destination?.route
                val isGpsOrAlert = currentRoute == "gps/{tripId}" || currentRoute == "alert/{tripId}" || currentRoute == Routes.TermsAndConditions.routes
                val isRegisterOrEditScreen = currentRoute == "register_trip" ||
                    currentRoute == "edit_trip/{tripId}" ||
                    currentRoute == "register_vehicle" ||
                    currentRoute == "edit_vehicle/{vehicleId}" ||
                    currentRoute == "register_driver" ||
                    currentRoute == "edit_driver/{driverId}" ||
                    currentRoute == "edit_expense/{expenseId}" ||
                    currentRoute == "register_expense/{tripId}" ||
                    currentRoute == "vehicles" ||
                    currentRoute == "drivers"

                val ongoingTripRepository = OngoingTripRepository(ongoingTripService)
                val alertRepository = AlertRepository(alertService)

                @Composable
                fun MyAppBar(onProfileClick: () -> Unit) {
                    TopAppBar(
                        title = {
                            Box(
                                modifier = Modifier.fillMaxWidth(),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "CARGOEXPRESS",
                                    style = MaterialTheme.typography.bodyLarge.copy(fontSize = 20.sp),
                                    color = Color(0xFFFFEB3B),
                                    fontWeight = FontWeight.ExtraBold
                                )
                            }
                                },
                        actions = {
                            IconButton(onClick = { onProfileClick() }) {
                                Icon(imageVector = Icons.Filled.AccountCircle, modifier = Modifier.size(100.dp), contentDescription = "Perfil")
                            }
                        },
                        colors = TopAppBarDefaults.topAppBarColors(
                            //containerColor = Color.Black
                        )
                    )
                }
                Scaffold(
                    topBar = {
                        if (currentRoute != Routes.Login.routes && currentRoute != Routes.Register.routes && !isGpsOrAlert) {
                            MyAppBar(onProfileClick = {
                                navController.navigate("profile")
                            })
                        }
                    },
                    bottomBar = {
                        if (currentRoute != Routes.Login.routes && currentRoute != Routes.Register.routes && !isGpsOrAlert && !isRegisterOrEditScreen) {
                            NavigationBar(
                                containerColor = MaterialTheme.colorScheme.background
                            ){
                                NavigationBarItem(
                                    selected = currentDestination == Routes.Home.routes,
                                    onClick = { navController.navigate(Routes.Home.routes) },
                                    icon = {
                                        Icon(
                                            imageVector = Icons.Filled.Home,
                                            contentDescription = "Inicio"
                                        )
                                    },
                                    label = { Text("Inicio", style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold, fontSize = 12.sp)) },
                                    colors = NavigationBarItemDefaults.colors(
                                        indicatorColor = Color(0xFFFFEB3B)
                                    )
                                )
                                NavigationBarItem(
                                    selected = currentDestination == "trips",
                                    onClick = { navController.navigate("trips") },
                                    icon = {
                                        Icon(
                                            imageVector = Icons.TwoTone.LocalShipping,
                                            contentDescription = "Viajes"
                                        )
                                    },
                                    label = { Text("Viajes", style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold, fontSize = 12.sp)) },
                                    colors = NavigationBarItemDefaults.colors(
                                        indicatorColor = Color(0xFFFFEB3B)
                                    )
                                )
                                if (Constants.USER_ROLE == "CLIENT") {
                                    NavigationBarItem(
                                        selected = currentDestination == "statistics",
                                        onClick = { navController.navigate("statistics") },
                                        icon = {
                                            Icon(
                                                Icons.Filled.BarChart,
                                                contentDescription = "Estadísticas"
                                            )
                                        },
                                        label = { Text("Estadísticas", style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold, fontSize = 12.sp)) },
                                        colors = NavigationBarItemDefaults.colors(
                                            indicatorColor = Color(0xFFFFEB3B)
                                        )
                                    )
                                } else {
                                    NavigationBarItem(
                                        selected = currentDestination == Routes.Fleet.routes,
                                        onClick = { navController.navigate(Routes.Fleet.routes) },
                                        icon = {
                                            Icon(
                                                Icons.Filled.DirectionsBusFilled,
                                                contentDescription = "Flota"
                                            )
                                        },
                                        label = { Text("Flota", style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold, fontSize = 12.sp)) },
                                        colors = NavigationBarItemDefaults.colors(
                                            indicatorColor = Color(0xFFFFEB3B)
                                        )
                                    )
                                }
                            }
                        }
                    }
                ) { innerPadding ->
                    NavHost(
                        navController = navController,
                        startDestination = Routes.Login.routes,
                        modifier = Modifier.padding(innerPadding)
                    ) {
                        // Login And Register
                        composable(route = Routes.Login.routes) {
                            LoginScreen(viewModel = loginViewModel, navController)
                        }
                        composable(route = Routes.Register.routes) {
                            RegisterScreen(
                                navController = navController,
                                viewModel = registerViewModel,
                                onRegisterSuccess = { u, p -> loginViewModel.signIn(u, p) }
                            )
                        }

                        composable(route = Routes.Home.routes) {
                            HomeScreen(
                                tripRepository = tripRepository,
                                auditLogRepository = auditLogRepository,
                                vehicleRepository = vehicleRepository,
                                driverRepository = driverRepository,
                                alertRepository = alertRepository,
                                navController = navController
                            )
                        }

                        composable(route = Routes.TripList.routes) {
                            TripManagementScreen(tripRepository = tripRepository, ongoingTripRepository, navController)
                        }
                        composable(route = "trips") {
                            TripManagementScreen(tripRepository = tripRepository, ongoingTripRepository, navController)
                        }
                        composable(route = Routes.Fleet.routes) {
                            FleetScreen(navController)
                        }
                        composable(route = "vehicles") {
                            VehicleListScreen(viewModel = vehicleListViewModel, navController)
                        }
                        composable(route = "drivers") {
                            DriverListScreen(viewModel = driverListViewModel, navController)
                        }
                        // MainActivity.kt
                        composable(route = "trip_details/{tripId}") { backStackEntry ->
                            val tripId = backStackEntry.arguments?.getString("tripId")?.toInt() ?: 0
                            TripDetailScreen(
                                tripId = tripId,
                                navController = navController,
                                tripRepository = tripRepository,
                                expenseRepository = expenseRepository,
                                driverRepository = driverRepository,
                                vehicleRepository = vehicleRepository,
                                clientRepository = clientRepository
                            )
                        }

                        composable(route = "register_expense/{tripId}") { backStackEntry ->
                            val tripId = backStackEntry.arguments?.getString("tripId")?.toInt() ?: 0
                            val registerExpenseViewModel = RegisterExpenseViewModel(expenseRepository)
                            RegisterExpenseScreen(viewModel = registerExpenseViewModel, navController = navController) { expense ->

                            }
                        }

                        composable(route = "edit_expense/{expenseId}") { backStackEntry ->
                            val expenseId = backStackEntry.arguments?.getString("expenseId")?.toInt() ?: 0
                            val editExpenseViewModel: EditExpenseViewModel = androidx.lifecycle.viewmodel.compose.viewModel(
                                factory = EditExpenseViewModelFactory(expenseRepository)
                            )
                            EditExpenseScreen(
                                expenseId = expenseId,
                                navController = navController,
                                viewModel = editExpenseViewModel
                            )
                        }

                        composable(route = "register_driver") { backStackEntry ->
                            val token = backStackEntry.arguments?.getString("token") ?: ""
                            val registerDriverViewModel = RegisterDriverViewModel(driverRepository)
                            RegisterDriverScreen(viewModel = registerDriverViewModel, navController = navController) { driver ->

                            }
                        }

                        composable(route = "register_vehicle") { backStackEntry ->
                            val token = backStackEntry.arguments?.getString("token") ?: ""
                            val registerVehicleViewModel = RegisterVehicleViewModel(vehicleRepository)
                            RegisterVehicleScreen(viewModel = registerVehicleViewModel, navController = navController) { vehicle ->

                            }
                        }

                        composable(route = "edit_vehicle/{vehicleId}") { backStackEntry ->
                            val vehicleId = backStackEntry.arguments?.getString("vehicleId")?.toInt() ?: 0
                            val editVehicleViewModel = EditVehicleViewModel(vehicleRepository)
                            EditVehicleScreen(vehicleId = vehicleId, viewModel = editVehicleViewModel, navController = navController)
                        }

                        composable(route = "edit_driver/{driverId}") { backStackEntry ->
                            val driverId = backStackEntry.arguments?.getString("driverId")?.toInt() ?: 0
                            val editDriverViewModel = EditDriverViewModel(driverRepository)
                            EditDriverScreen(driverId = driverId, viewModel = editDriverViewModel, navController = navController)
                        }

                        composable(route = "register_trip") { backStackEntry ->
                            val token = backStackEntry.arguments?.getString("token") ?: ""
                            val registerTripViewModel: RegisterTripViewModel = viewModel(
                                factory = RegisterTripViewModelFactory(
                                    tripRepository = tripRepository,
                                    driverRepository = driverRepository,
                                    vehicleRepository = vehicleRepository,
                                    entrepreneurRepository = entrepreneurRepository,
                                    clientRepository = clientRepository
                                )
                            )
                            RegisterTripScreen(viewModel = registerTripViewModel, navController = navController) { trip ->

                            }
                        }

                        composable("edit_trip/{tripId}") { backStackEntry ->
                            val tripId = backStackEntry.arguments?.getString("tripId")?.toInt() ?: 0
                            TripEditScreen(
                                tripId = tripId,
                                tripRepository = tripRepository,
                                driverRepository = driverRepository,
                                vehicleRepository = vehicleRepository,
                                clientRepository = clientRepository,
                                navController = navController
                            )
                        }

                        composable("gps/{tripId}") { backStackEntry ->
                            val tripId = backStackEntry.arguments?.getString("tripId")?.toInt() ?: 0
                            GpsScreen(
                                tripId = tripId,
                                tripRepository = tripRepository,
                                navController = navController,
                                ongoingTripRepository = ongoingTripRepository,
                                vehicleRepository = vehicleRepository,
                                driverRepository = driverRepository
                            )
                        }

                        composable("alert/{tripId}"){ backStackEntry ->
                            val tripId = backStackEntry.arguments?.getString("tripId")?.toInt() ?: 0
                            AlertScreen(tripId = tripId, tripRepository = tripRepository, navController = navController, alertRepository = alertRepository)
                        }

                        composable(route = "statistics") {
                            StatisticsScreen(tripRepository = tripRepository)
                        }

                        composable(route = "profile") {
                            ProfileScreen(viewModel = profileViewModel, navController)
                        }

                        composable(route = Routes.TermsAndConditions.routes) {
                            TermsAndConditionsScreen(navController)
                        }
                    }
                }
            }
        }
    }
}