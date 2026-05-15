package com.cargoexpress.app.core.presentation.terms

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TermsAndConditionsScreen(navController: NavController) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Términos y Condiciones") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(imageVector = Icons.Filled.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Text(
                text = "CargoExpress",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            TermsParagraph(
                "Bienvenido a CargoExpress, plataforma digital desarrollada y operada por CargoSystems. " +
                "El presente Acuerdo de Servicio SaaS establece los términos y condiciones que regulan el acceso, " +
                "uso y funcionamiento de la plataforma, así como los derechos, obligaciones y restricciones aplicables a los usuarios.\n\n" +
                "El uso de la plataforma implica la aceptación plena y sin reservas de las disposiciones establecidas en este documento. " +
                "Si el usuario no está de acuerdo con cualquiera de las condiciones aquí descritas, deberá abstenerse de utilizar el servicio."
            )

            TermsSectionTitle("1. Definiciones")
            TermsParagraph(
                "Para efectos del presente acuerdo, los siguientes términos tendrán el significado indicado a continuación:\n\n" +
                "• CargoSystems: Startup responsable del desarrollo, operación y mantenimiento de la plataforma CargoExpress.\n" +
                "• CargoExpress: Plataforma SaaS orientada al monitoreo, seguimiento y gestión de transporte de carga.\n" +
                "• Usuario: Persona natural o jurídica que accede o utiliza la plataforma.\n" +
                "• Cliente: Empresa o entidad que contrata los servicios ofrecidos por CargoExpress.\n" +
                "• Cuenta: Registro creado por el usuario para acceder a las funcionalidades de la plataforma.\n" +
                "• Contenido: Información, datos, documentos, imágenes, ubicaciones GPS, registros de gastos y cualquier otro material cargado o generado dentro de la plataforma.\n" +
                "• Servicio SaaS: Modelo de prestación de software mediante acceso remoto a través de internet, sin necesidad de instalación local completa."
            )

            TermsSectionTitle("2. Objeto del Servicio")
            TermsParagraph(
                "CargoExpress proporciona una plataforma tecnológica orientada a empresas de transporte, operadores logísticos y clientes que requieren monitoreo de carga en tiempo real.\n\n" +
                "La plataforma permite, entre otras funcionalidades:\n\n" +
                "• Seguimiento en tiempo real de vehículos y mercancías.\n" +
                "• Visualización del estado y ubicación de envíos.\n" +
                "• Registro y administración de gastos de transporte.\n" +
                "• Gestión de vehículos y conductores.\n" +
                "• Historial de envíos realizados.\n" +
                "• Alertas relevantes durante el trayecto.\n" +
                "• Comunicación y transparencia entre empresas transportistas y clientes.\n\n" +
                "CargoSystems podrá actualizar, modificar o mejorar las funcionalidades del servicio cuando lo considere necesario para fines operativos, técnicos o de seguridad."
            )

            TermsSectionTitle("3. Elegibilidad y Registro de Usuarios")
            TermsParagraph(
                "Para utilizar la plataforma, el usuario deberá:\n\n" +
                "• Ser mayor de edad conforme a la legislación aplicable.\n" +
                "• Proporcionar información veraz, completa y actualizada.\n" +
                "• Mantener la confidencialidad de sus credenciales de acceso.\n" +
                "• Utilizar la plataforma únicamente para fines legales y autorizados.\n\n" +
                "El usuario es responsable de toda actividad realizada desde su cuenta.\n\n" +
                "CargoSystems se reserva el derecho de rechazar registros, suspender cuentas o cancelar accesos cuando detecte información falsa, actividades sospechosas o incumplimientos del presente acuerdo."
            )

            TermsSectionTitle("4. Licencia de Uso")
            TermsParagraph(
                "CargoSystems concede al usuario una licencia limitada, no exclusiva, intransferible y revocable para acceder y utilizar la plataforma conforme a las condiciones establecidas en este acuerdo.\n\n" +
                "La licencia otorgada no implica transferencia de propiedad intelectual ni derechos sobre el software, infraestructura, diseño, código fuente o funcionalidades de la plataforma.\n\n" +
                "El usuario no podrá:\n\n" +
                "• Copiar, modificar o distribuir el software.\n" +
                "• Descompilar, realizar ingeniería inversa o intentar acceder al código fuente.\n" +
                "• Revender o sublicenciar el servicio sin autorización escrita.\n" +
                "• Utilizar la plataforma para actividades ilícitas o fraudulentas.\n" +
                "• Introducir malware, virus o software malicioso.\n" +
                "• Intentar vulnerar la seguridad o estabilidad de la plataforma."
            )

            TermsSectionTitle("5. Disponibilidad del Servicio")
            TermsParagraph(
                "CargoSystems realizará esfuerzos razonables para mantener la disponibilidad continua del servicio. Sin embargo, no garantiza que la plataforma estará libre de interrupciones, errores o fallos técnicos.\n\n" +
                "El servicio podrá verse temporalmente afectado por:\n\n" +
                "• Mantenimiento programado.\n" +
                "• Actualizaciones de infraestructura.\n" +
                "• Problemas de conectividad.\n" +
                "• Fallos de terceros.\n" +
                "• Casos fortuitos o fuerza mayor.\n\n" +
                "Cuando sea posible, CargoSystems notificará previamente los mantenimientos programados que puedan afectar el funcionamiento de la plataforma."
            )

            TermsSectionTitle("6. Protección de Datos y Privacidad")
            TermsParagraph(
                "CargoSystems se compromete a proteger la información personal y empresarial proporcionada por los usuarios.\n\n" +
                "La recopilación, almacenamiento y tratamiento de datos se realizará conforme a la legislación aplicable en materia de protección de datos personales.\n\n" +
                "La plataforma podrá recopilar información relacionada con:\n\n" +
                "• Datos de identificación.\n" +
                "• Información de contacto.\n" +
                "• Ubicación GPS de vehículos.\n" +
                "• Historial de envíos.\n" +
                "• Registros operativos.\n" +
                "• Datos estadísticos y analíticos.\n\n" +
                "El usuario autoriza el tratamiento de dichos datos para la correcta prestación del servicio, mejora de funcionalidades, generación de reportes, monitoreo logístico, seguridad operativa y cumplimiento de obligaciones legales.\n\n" +
                "CargoSystems implementará medidas razonables de seguridad técnicas y organizativas para proteger la información contra accesos no autorizados, pérdidas o alteraciones."
            )

            TermsSectionTitle("7. Obligaciones del Usuario")
            TermsParagraph(
                "El usuario se compromete a:\n\n" +
                "• Utilizar la plataforma de forma responsable y conforme a la ley.\n" +
                "• No proporcionar información falsa o engañosa.\n" +
                "• Mantener actualizados sus datos de registro.\n" +
                "• Respetar los derechos de propiedad intelectual de CargoSystems.\n" +
                "• No utilizar la plataforma para fines ilícitos o no autorizados.\n" +
                "• Garantizar que posee autorización para compartir la información y documentación cargada en el sistema.\n" +
                "• Adoptar medidas razonables para proteger sus credenciales de acceso.\n\n" +
                "El incumplimiento de estas obligaciones podrá derivar en la suspensión temporal o definitiva de la cuenta."
            )

            TermsSectionTitle("8. Restricciones de Uso")
            TermsParagraph(
                "Queda estrictamente prohibido:\n\n" +
                "• Manipular datos de seguimiento o ubicación.\n" +
                "• Alterar registros de gastos o evidencia documental.\n" +
                "• Compartir accesos con terceros no autorizados.\n" +
                "• Utilizar herramientas automatizadas que afecten la estabilidad del servicio.\n" +
                "• Realizar actividades que generen sobrecarga de infraestructura.\n" +
                "• Acceder sin autorización a cuentas ajenas.\n" +
                "• Utilizar la plataforma para actividades que vulneren derechos de terceros.\n\n" +
                "CargoSystems podrá tomar acciones inmediatas frente a actividades sospechosas o incumplimientos detectados."
            )

            TermsSectionTitle("9. Propiedad Intelectual")
            TermsParagraph(
                "Todos los derechos de propiedad intelectual relacionados con CargoExpress pertenecen exclusivamente a CargoSystems o a sus respectivos titulares.\n\n" +
                "Esto incluye: software, diseño de interfaz, bases de datos, logotipos, marcas, contenido gráfico, arquitectura tecnológica y documentación.\n\n" +
                "El uso de la plataforma no otorga al usuario derechos de propiedad sobre ninguno de estos elementos."
            )

            TermsSectionTitle("10. Contenido Generado por el Usuario")
            TermsParagraph(
                "El usuario conserva la titularidad sobre la información y contenido que cargue en la plataforma.\n\n" +
                "Sin embargo, al utilizar el servicio, concede a CargoSystems una autorización limitada para almacenar, procesar y utilizar dicha información únicamente con fines relacionados a la prestación del servicio, monitoreo logístico, generación de reportes, mejoras operativas, soporte técnico y cumplimiento legal.\n\n" +
                "El usuario declara que posee los derechos necesarios sobre el contenido compartido y asume responsabilidad por cualquier infracción derivada de dicho contenido."
            )

            TermsSectionTitle("11. Seguridad de la Plataforma")
            TermsParagraph(
                "CargoSystems implementa medidas razonables de seguridad para proteger la plataforma y la información almacenada.\n\n" +
                "No obstante, el usuario reconoce que ningún sistema informático es completamente invulnerable y acepta que pueden existir riesgos asociados al uso de servicios digitales.\n\n" +
                "El usuario deberá notificar inmediatamente cualquier acceso no autorizado, vulneración de seguridad o uso indebido de su cuenta."
            )

            TermsSectionTitle("12. Limitación de Responsabilidad")
            TermsParagraph(
                "CargoSystems no será responsable por:\n\n" +
                "• Interrupciones temporales del servicio.\n" +
                "• Pérdidas indirectas o lucro cesante.\n" +
                "• Daños derivados de fallos de conectividad.\n" +
                "• Errores ocasionados por terceros.\n" +
                "• Decisiones comerciales tomadas por los usuarios con base en la información de la plataforma.\n" +
                "• Pérdidas derivadas de uso indebido de credenciales.\n" +
                "• Eventos de fuerza mayor.\n\n" +
                "La responsabilidad total de CargoSystems, en caso de corresponder, estará limitada al monto efectivamente pagado por el usuario durante el período de suscripción inmediatamente anterior al incidente."
            )

            TermsSectionTitle("13. Suspensión y Terminación")
            TermsParagraph(
                "CargoSystems podrá suspender o cancelar el acceso a la plataforma cuando:\n\n" +
                "• El usuario incumpla este acuerdo.\n" +
                "• Existan indicios de fraude o actividad ilícita.\n" +
                "• Se detecten riesgos para la seguridad de la plataforma.\n" +
                "• Exista incumplimiento de obligaciones de pago.\n" +
                "• Sea requerido por autoridad competente.\n\n" +
                "El usuario podrá cancelar su cuenta en cualquier momento conforme a los procedimientos establecidos en la plataforma.\n\n" +
                "La terminación del servicio no extinguirá obligaciones pendientes ni responsabilidades generadas antes de la cancelación."
            )

            TermsSectionTitle("15. Modificaciones del Acuerdo")
            TermsParagraph(
                "CargoSystems podrá modificar el presente acuerdo cuando resulte necesario por razones legales, técnicas, comerciales u operativas.\n\n" +
                "Las modificaciones serán publicadas en la sección \"Terms and Conditions\" del website y entrarán en vigencia desde su publicación o en la fecha indicada.\n\n" +
                "El uso continuado de la plataforma después de dichas modificaciones constituirá aceptación de los nuevos términos."
            )

            TermsSectionTitle("16. Cumplimiento Normativo")
            TermsParagraph(
                "El usuario se compromete a utilizar la plataforma respetando la normativa aplicable relacionada con:\n\n" +
                "• Protección de datos personales.\n" +
                "• Transporte y logística.\n" +
                "• Comercio electrónico.\n" +
                "• Seguridad informática.\n" +
                "• Propiedad intelectual.\n" +
                "• Prevención de actividades ilícitas.\n\n" +
                "CargoSystems podrá colaborar con autoridades competentes cuando sea legalmente requerido."
            )

            TermsSectionTitle("17. Soporte Técnico")
            TermsParagraph(
                "CargoSystems podrá ofrecer soporte técnico mediante los canales habilitados en la plataforma.\n\n" +
                "El soporte incluirá asistencia razonable relacionada con acceso a la plataforma, incidentes técnicos, errores operativos y consultas funcionales.\n\n" +
                "Los tiempos de respuesta podrán variar según la complejidad del incidente y el plan contratado."
            )

            TermsSectionTitle("18. Ley Aplicable y Jurisdicción")
            TermsParagraph(
                "El presente acuerdo se regirá conforme a las leyes vigentes de la República del Perú.\n\n" +
                "Cualquier controversia derivada de la interpretación o ejecución de este acuerdo será sometida a la jurisdicción de los tribunales competentes de Lima, Perú, salvo disposición legal distinta."
            )

            TermsSectionTitle("19. Contacto")
            TermsParagraph(
                "Para consultas relacionadas con este Acuerdo de Servicio SaaS o sobre el funcionamiento de la plataforma, el usuario podrá comunicarse mediante los canales oficiales publicados en la sección \"Contacto\" del website."
            )

            TermsSectionTitle("20. Aceptación de los Términos")
            TermsParagraph(
                "Al registrarse, acceder o utilizar CargoExpress, el usuario declara haber leído, comprendido y aceptado íntegramente el presente Acuerdo de Servicio SaaS.\n\n" +
                "En caso de no estar de acuerdo con cualquiera de las disposiciones establecidas, el usuario deberá abstenerse de utilizar la plataforma."
            )

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun TermsSectionTitle(text: String) {
    Text(
        text = text,
        fontSize = 15.sp,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(top = 16.dp, bottom = 6.dp)
    )
}

@Composable
private fun TermsParagraph(text: String) {
    Text(
        text = text,
        fontSize = 14.sp,
        lineHeight = 22.sp,
        modifier = Modifier.padding(bottom = 4.dp)
    )
}
