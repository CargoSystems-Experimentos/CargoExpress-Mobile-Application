package com.cargoexpress.app.core.presentation.register

import androidx.navigation.NavController
import com.cargoexpress.app.core.common.Constants
import com.cargoexpress.app.core.data.repository.ClientRepository
import com.cargoexpress.app.core.data.repository.EntrepreneurRepository
import com.cargoexpress.app.core.data.repository.LoginRepository
import com.cargoexpress.app.core.data.repository.RegisterRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkObject
import io.mockk.unmockkObject
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class RegisterViewModelTest {

    private var registerRepository: RegisterRepository? = null
    private var loginRepository: LoginRepository? = null
    private var clientRepository: ClientRepository? = null
    private var entrepreneurRepository: EntrepreneurRepository? = null
    private var navController: NavController? = null

    @Before
    fun setup() {
        registerRepository = mockk(relaxed = true)
        loginRepository = mockk(relaxed = true)
        clientRepository = mockk(relaxed = true)
        entrepreneurRepository = mockk(relaxed = true)
        navController = mockk(relaxed = true)

        mockkObject(Constants)
        every { Constants.TOKEN } returns ""
        every { Constants.USER_ID } returns 0
        every { Constants.USER_NAME } returns ""
        every { Constants.USER_ROLE } returns ""
        every { Constants.ENTREPRENEUR_ID } returns 0
    }

    @After
    fun tearDown() {
        unmockkObject(Constants)
    }

    @Test
    fun `state initial value is idle`() {
        val viewModel = RegisterViewModel(
            navController!!,
            registerRepository!!,
            loginRepository!!,
            clientRepository!!,
            entrepreneurRepository!!
        )

        val state = viewModel.state.value
        assertEquals(false, state?.isLoading)
    }
}