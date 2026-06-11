package com.cargoexpress.app.core.presentation.driver.driverList.registerDriver
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cargoexpress.app.core.data.repository.DriverRepository
import com.cargoexpress.app.core.domain.Driver
import kotlinx.coroutines.launch
import com.cargoexpress.app.core.common.Constants
import com.cargoexpress.app.core.common.Resource


class RegisterDriverViewModel(
    private val driverRepository: DriverRepository
) : ViewModel() {
    var name: String = ""
    var dni: String = ""
    var license: String = ""
    var contactNumber: String = ""

    fun registerDriver(onResult: (Resource<Driver>) -> Unit) {
        viewModelScope.launch {
            val driver = Driver(
                id = 0,
                name = name,
                dni = dni,
                license = license,
                contactNumber = contactNumber,
                state = "",
                entrepreneurId = Constants.ENTREPRENEUR_ID
            )
            val result = driverRepository.addDriver(driver)
            onResult(result)
        }
    }
}