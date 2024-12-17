package br.com.zapzap

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Message
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.CircleNotifications
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.datasource.LoremIpsum
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import br.com.zapzap.features.chatsList.Chat
import br.com.zapzap.features.chatsList.ChatsListScreen
import br.com.zapzap.features.chatsList.ChatsListScreenState
import br.com.zapzap.features.chatsList.ChatsListViewModel
import br.com.zapzap.features.chatsList.Message
import br.com.zapzap.features.chatsList.User
import br.com.zapzap.ui.theme.ZapZapTheme
import kotlin.random.Random

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ZapZapTheme {
                App()
            }
        }
    }
}

@Composable
fun CommunitiesScreen(modifier: Modifier = Modifier) {
    Box(modifier.fillMaxSize()) {
        Text("Communities", Modifier.align(Alignment.Center), style = TextStyle.Default.copy(
            fontSize = 32.sp
        ))
    }
}

@Composable
fun CallsScreen(modifier: Modifier = Modifier) {
    Box(modifier.fillMaxSize()) {
        Text("Calls", Modifier.align(Alignment.Center), style = TextStyle.Default.copy(
            fontSize = 32.sp
        ))
    }
}

@Composable
fun UpdatesScreen(modifier: Modifier = Modifier) {
    Box(modifier.fillMaxSize()) {
        Text("Updates", Modifier.align(Alignment.Center), style = TextStyle.Default.copy(
            fontSize = 32.sp
        ))
    }
}

class BottomAppBarItem(
    val icon: ImageVector,
    val label: String
)

class TopAppBarItem(
    val title: String,
    val icons: List<ImageVector> = emptyList()
)

sealed class ScreenItem(
    val topAppItem: TopAppBarItem,
    val bottomAppItem: BottomAppBarItem
){
    data object Chats : ScreenItem(
        topAppItem = TopAppBarItem(
            title = "Zap Zap",
            icons = listOf(
                Icons.Default.CameraAlt,
                Icons.Default.MoreVert
            )
        ),
        bottomAppItem = BottomAppBarItem(
            Icons.AutoMirrored.Filled.Message,
            "Chats"
        )

    )

    data object Updates : ScreenItem(
        topAppItem = TopAppBarItem(
            title = "Updates",
            icons = listOf(
                Icons.Default.CameraAlt,
                Icons.Default.Search,
                Icons.Default.MoreVert
            )
        ),
        bottomAppItem = BottomAppBarItem(
            Icons.Default.CircleNotifications,
            "Updates"
        )
    )

    data object Communities : ScreenItem(
        topAppItem = TopAppBarItem(
            title = "Communities",
            icons = listOf(
                Icons.Default.CameraAlt,
                Icons.Default.MoreVert
            )
        ),
        bottomAppItem = BottomAppBarItem(
            Icons.Default.People,
            "Communities"
        )
    )

    data object Calls : ScreenItem(
        topAppItem = TopAppBarItem(
            title = "Calls",
            icons = listOf(
                Icons.Default.CameraAlt,
                Icons.Default.Search,
                Icons.Default.MoreVert
            )
        ),
        bottomAppItem = BottomAppBarItem(
            Icons.Default.Call,
            "Calls"
        )
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun App() {
    val screens = remember {
        listOf(
            ScreenItem.Chats,
            ScreenItem.Updates,
            ScreenItem.Communities,
            ScreenItem.Calls
        )
    }
    var currentScreen by remember {
        mutableStateOf(screens.first())
    }
    val pagerState = rememberPagerState {
        screens.size
    }
    LaunchedEffect(currentScreen) {
        pagerState.animateScrollToPage(
            screens.indexOf(currentScreen)
        )
    }
    LaunchedEffect(pagerState.targetPage) {
        currentScreen = screens[pagerState.targetPage]
    }
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
        TopAppBar(title = {
            Text(currentScreen.topAppItem.title)
        },
            actions = {
                Row(Modifier.padding(8.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    currentScreen.topAppItem.icons.forEach { icon ->
                     Icon(icon, contentDescription = null)
                    }
                }
            })
    },
        bottomBar = {
            BottomAppBar {
                screens.forEach{ screen ->
                    with(screen.bottomAppItem) {
                        NavigationBarItem(
                            selected = screen == currentScreen,
                            onClick = {
                                currentScreen = screen
                            },
                            icon = {
                                Icon(icon, contentDescription = null)
                            },
                            label = {
                                Text(label)
                            }
                        )
                    }
                }

            }
        }) { innerPadding ->
        HorizontalPager(pagerState, Modifier.padding(innerPadding)) { page->
            val item = screens[page]
            when(item) {
                ScreenItem.Calls -> CallsScreen()
                ScreenItem.Chats -> {
                    val viewModel = viewModel<ChatsListViewModel>()
                    val state by viewModel.state.collectAsState()
                    ChatsListScreen(state = state)
                }
                ScreenItem.Communities -> CommunitiesScreen()
                ScreenItem.Updates -> UpdatesScreen()
            }
        }
    }
}

@Preview
@Composable
fun AppPreview(){
    ZapZapTheme {
        App()
    }
}