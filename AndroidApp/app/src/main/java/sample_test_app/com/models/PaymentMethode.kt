import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
sealed class PaymentMethod {
    abstract val id: String
    abstract val type: String
}

@Serializable
@SerialName("Paypal")
data class PaypalPaymentMethod(
    override val id: String,
    override val type: String,
    val value: PaypalValue
) : PaymentMethod()

@Serializable
data class PaypalValue(
    val user_paypal: String
)

@Serializable
@SerialName("RIB")
data class RIBPaymentMethod(
    override val id: String,
    override val type: String,
    val value: RIBValue,
    val bank_link: String
) : PaymentMethod()

@Serializable
data class RIBValue(
    val name: String,
    val surname: String,
    val bank_name: String,
    val bank_number: String,
    val box_code: String,
    val account_number: String,
    val RIB_key: String,
    val IBAN: String
)