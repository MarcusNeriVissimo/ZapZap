package br.com.zapzap.features.chatsList

import androidx.compose.ui.tooling.preview.datasource.LoremIpsum
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atTime
import kotlinx.datetime.daysUntil
import kotlinx.datetime.format
import kotlinx.datetime.format.DayOfWeekNames
import kotlinx.datetime.format.FormatStringsInDatetimeFormats
import kotlinx.datetime.format.byUnicodePattern
import kotlinx.datetime.minus
import kotlinx.datetime.toLocalDateTime
import kotlin.math.absoluteValue
import kotlin.random.Random

sealed class ChatsListScreenState{
    data object Loading : ChatsListScreenState()
    data class Success(
        val currentUser: User,
        val filters: List<String> = emptyList(),
        val chats: List<Chat> = emptyList()
    ) : ChatsListScreenState()
}



class Chat(
    val avatar: String?,
    val name: String,
    val lastMessage: Message,
    val unreadMessages: Int
)

class Message(
    val text: String,
    val date: String,
    val isRead: Boolean,
    val author: User
)

data class User(
    val name: String
)

class ChatsListViewModel : ViewModel() {
    private val _state = MutableStateFlow<ChatsListScreenState>(
        ChatsListScreenState.Loading
    )
    val state = _state.asStateFlow()

    init {
        viewModelScope.launch {
            val user = fetchUser()
            val filters = fetchFilters()
            val chat = fetchChats()
            delay(Random.nextLong(1000, 3000))
            _state.update {
                ChatsListScreenState.Success(
                    currentUser = User(user.name),
                    filters = filters,
                    chats = chat
                )
            }
        }
    }

    private fun fetchUser() : User {
        return User("Marcus")
    }

    private fun fetchFilters() : List<String> {
        return listOf(
            "All",
            "Unread",
            "Groups"
        )
    }

    private fun fetchChats(): List<Chat> {
        val avatarIterator = avatars.shuffled().listIterator()
        return List(10) {
            val localDateTime = Clock.System.now()
                .toLocalDateTime(TimeZone.currentSystemDefault())
                .date.minus(it, DateTimeUnit.DAY)
                .atTime(Random.nextInt(0, 23), Random.nextInt(0, 59))
            Chat(
                avatar = if (Random.nextBoolean()) avatarIterator.next() else null,
                name = LoremIpsum(Random.nextInt(1, 10)).values.first(),
                lastMessage = Message(
                    text = LoremIpsum(Random.nextInt(1, 10)).values.first(),
                    date = localDateTime.formattedDateForChatLastMessage(),
                    isRead = false,
                    author = if (Random.nextBoolean()) {
                        User("Marcus")
                    } else {
                        User("Neri")
                    }
                ),
                unreadMessages = Random.nextInt(1, 20)
            )
        }
    }
}

private val avatars = mutableListOf(
    "https://img.freepik.com/psd-gratuitas/ilustracao-3d-de-uma-pessoa-com-oculos-de-sol_23-2149436188.jpg",
    "https://img.freepik.com/psd-gratuitas/ilustracao-3d-de-pessoa-com-cabelo-punk-e-jaqueta_23-2149436198.jpg",
    "https://img.freepik.com/psd-gratuitas/renderizacao-3d-do-personagem-avatar_23-2150611765.jpg",
    "https://img.freepik.com/psd-gratuitas/ilustracao-3d-de-uma-pessoa-com-oculos_23-2149436189.jpg",
    "https://img.freepik.com/psd-gratuitas/renderizacao-3d-de-emoji-de-avatar-de-menino_23-2150603408.jpg",
    "https://img.freepik.com/psd-gratuitas/renderizacao-3d-de-avatar_23-2150833548.jpg",
    "https://img.freepik.com/fotos-gratis/avatar-androgino-de-pessoa-queer-nao-binaria_23-2151100270.jpg"
)

@OptIn(FormatStringsInDatetimeFormats::class)
fun LocalDateTime.formattedDateForChatLastMessage(): String {
    val nowLocalDateTime = Clock.System.now()
        .toLocalDateTime(TimeZone.currentSystemDefault())
    val dayUntil = nowLocalDateTime.date.daysUntil(this.date)
        .absoluteValue
    return when {
        dayUntil == 0 -> {
            this.time.format(LocalTime.Format {
                byUnicodePattern("HH:mm")
            })
        }
        dayUntil < 2 -> {
          "ontem"
        }

        dayUntil < 7 -> {
            this.date.format(LocalDate.Format {
                this.dayOfWeek(
                    names = DayOfWeekNames(
                        monday = "segunda",
                        tuesday = "terça",
                        wednesday = "quarta",
                        thursday = "quinta",
                        friday = "sexta",
                        saturday = "sábado",
                        sunday = "domingo"
                    )
                )
            })
        }

        else -> {
            this.format(LocalDateTime.Format {
                byUnicodePattern("dd/MM/yy")
            })
        }
    }
}

