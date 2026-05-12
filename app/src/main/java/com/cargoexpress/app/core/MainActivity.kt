package com.cargoexpress.app.core

import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
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
import com.cargoexpress.app.core.data.remote.vehicle.VehicleService
import com.cargoexpress.app.core.data.repository.ClientRepository
import com.cargoexpress.app.core.data.repository.EntrepreneurRepository
import com.cargoexpress.app.core.data.repository.LoginRepository
import com.cargoexpress.app.core.data.repository.RegisterRepository
import com.cargoexpress.app.core.data.repository.TripRepository
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
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
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
import com.cargoexpress.app.core.presentation.statistics.StatisticsScreen
import androidx.compose.material.icons.filled.BarChart

class MainActivity : ComponentActivity() {

    companion object {
        const val PICK_IMAGE_REQUEST = 1
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {

        val loginService = Retrofit
            .Builder()
            .baseUrl(Constants.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(LoginService::class.java)

        val registerService = Retrofit
            .Builder()
            .baseUrl(Constants.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(RegisterService::class.java)

        val clientService = Retrofit
            .Builder()
            .baseUrl(Constants.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ClientService::class.java)

        val entrepreneurService = Retrofit
            .Builder()
            .baseUrl(Constants.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(EntrepreneurService::class.java)

        val vehicleService = Retrofit
            .Builder()
            .baseUrl(Constants.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(VehicleService::class.java)

        val tripService = Retrofit
            .Builder()
            .baseUrl(Constants.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(TripService::class.java)

        val driverService = Retrofit
            .Builder()
            .baseUrl(Constants.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(DriverService::class.java)

        val expenseService = Retrofit
            .Builder()
            .baseUrl(Constants.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ExpenseService::class.java)

        val ongoingTripService = Retrofit
            .Builder()
            .baseUrl(Constants.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(OngoingTripService::class.java)

        val alertService = Retrofit
            .Builder()
            .baseUrl(Constants.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(AlertService::class.java)

        val tripRepository = TripRepository(tripService, expenseService)
        val clientRepository = ClientRepository(clientService)

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            CargoexpressTheme {
                val navController = rememberNavController()
                val loginViewModel = LoginViewModel(
                    navController,
                    LoginRepository(loginService),
                    EntrepreneurRepository(entrepreneurService),
                    ClientRepository(clientService)
                )
                val registerViewModel = RegisterViewModel(
                    navController,
                    RegisterRepository(registerService),
                    LoginRepository(loginService),
                    ClientRepository(clientService),
                    EntrepreneurRepository(entrepreneurService)
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
                val isGpsOrAlert = currentRoute == "gps/{tripId}" || currentRoute == "alert/{tripId}"

                val ongoingTripRepository = OngoingTripRepository(ongoingTripService)
                val alertRepository = AlertRepository(alertService)

                @Composable
                fun MyAppBar(onProfileClick: () -> Unit) {
                    TopAppBar(
                        title = { Text("CargoExpress") },
                        navigationIcon = {
                            Image(
                                painter = painterResource(id = R.drawable.logo),
                                contentDescription = "App Logo",
                                modifier = Modifier.size(40.dp)
                            )
                        },// Título de la AppBar
                        actions = {
                            IconButton(onClick = { onProfileClick() }) {
                                Icon(imageVector = Icons.Filled.AccountCircle, modifier = Modifier.size(100.dp), contentDescription = "Perfil")
                            }
                        }
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
                        if (currentRoute != Routes.Login.routes && currentRoute != Routes.Register.routes && !isGpsOrAlert) {
                            NavigationBar {
                                NavigationBarItem(
                                    selected = currentDestination == "trips",
                                    onClick = { navController.navigate("trips") },
                                    icon = {
                                        Icon(
                                            imageVector = Icons.TwoTone.LocalShipping,
                                            contentDescription = "Viajes"
                                        )
                                    },
                                    label = { Text("Viajes") }
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
                                        label = { Text("Estadísticas") }
                                    )
                                } else {
                                    NavigationBarItem(
                                        selected = currentDestination == "vehicles",
                                        onClick = { navController.navigate("vehicles") },
                                        icon = {
                                            Icon(
                                                Icons.Filled.DirectionsBusFilled,
                                                contentDescription = "Vehiculos"
                                            )
                                        },
                                        label = { Text("Vehiculos") }
                                    )
                                    NavigationBarItem(
                                        selected = currentDestination == "drivers",
                                        onClick = { navController.navigate("drivers") },
                                        icon = {
                                            Icon(
                                                Icons.Filled.Groups,
                                                contentDescription = "Conductores"
                                            )
                                        },
                                        label = { Text("Conductores") }
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
                            RegisterScreen(navController, viewModel = registerViewModel)
                        }

                        composable(route = Routes.TripList.routes) {
                            TripManagementScreen(tripRepository = tripRepository, ongoingTripRepository, navController)
                        }
                        composable(route = "trips") {
                            TripManagementScreen(tripRepository = tripRepository, ongoingTripRepository, navController)
                        }
                        composable(route = "vehicles") {
                            VehicleListScreen(viewModel = vehicleListViewModel, navController)
                            // FleetScreen(navController)
                        }
                        composable(route = "drivers") {
                            DriverListScreen(viewModel = driverListViewModel, navController)
                            // TripManagementScreen(tripRepository = tripRepository)
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

                        composable(route = "register_trip") { backStackEntry ->
                            val token = backStackEntry.arguments?.getString("token") ?: ""
                            val registerTripViewModel: RegisterTripViewModel = viewModel(
                                factory = RegisterTripViewModelFactory(
                                    tripRepository = tripRepository,
                                    driverRepository = driverRepository,
                                    vehicleRepository = vehicleRepository,
                                    entrepreneurRepository = entrepreneurRepository
                                )
                            )
                            RegisterTripScreen(viewModel = registerTripViewModel) { trip ->

                            }
                        }

                        composable("edit_trip/{tripId}") { backStackEntry ->
                            val tripId = backStackEntry.arguments?.getString("tripId")?.toInt() ?: 0
                            TripEditScreen(tripId = tripId, tripRepository = tripRepository, navController = navController)
                        }

                        composable("gps/{tripId}") { backStackEntry ->
                            val tripId = backStackEntry.arguments?.getString("tripId")?.toInt() ?: 0
                            GpsScreen(tripId = tripId, tripRepository = tripRepository, navController = navController, ongoingTripRepository = ongoingTripRepository)
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
                    }
                }
            }
        }
    }

    fun openImagePicker() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, PICK_IMAGE_REQUEST)
    }


}