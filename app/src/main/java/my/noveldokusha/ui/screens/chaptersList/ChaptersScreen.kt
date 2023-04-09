package my.noveldokusha.ui.screens.chaptersList

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.RemoveDone
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.CloudDownload
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.PublishedWithChanges
import androidx.compose.material.icons.outlined.SelectAll
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.BottomAppBarDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import my.noveldokusha.R
import my.noveldokusha.data.ChapterWithContext
import my.noveldokusha.ui.goToWebBrowser
import my.noveldokusha.ui.theme.ColorAccent
import my.noveldokusha.ui.theme.ColorLike
import my.noveldokusha.ui.theme.ColorNotice
import my.noveldokusha.utils.isAtTop
import my.noveldokusha.utils.mix

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChaptersScreen(
    state: ChapterScreenState,
    onLibraryToggle: () -> Unit,
    onSearchBookInDatabase: () -> Unit,
    onResumeReading: () -> Unit,
    onPressBack: () -> Unit,
    onSelectedDeleteDownloads: () -> Unit,
    onSelectedDownload: () -> Unit,
    onSelectedSetRead: () -> Unit,
    onSelectedSetUnread: () -> Unit,
    onSelectedInvertSelection: () -> Unit,
    onSelectAllChapters: () -> Unit,
    onCloseSelectionBar: () -> Unit,
    onChapterClick: (chapter: ChapterWithContext) -> Unit,
    onChapterLongClick: (chapter: ChapterWithContext) -> Unit,
    onSelectionModeChapterClick: (chapter: ChapterWithContext) -> Unit,
    onSelectionModeChapterLongClick: (chapter: ChapterWithContext) -> Unit,
    onChapterDownload: (chapter: ChapterWithContext) -> Unit,
) {
    val context by rememberUpdatedState(newValue = LocalContext.current)
    var showDropDown by rememberSaveable { mutableStateOf(false) }
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    var showBottomSheet by rememberSaveable { mutableStateOf(false) }
    val lazyListState = rememberLazyListState()

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            val isAtTop by lazyListState.isAtTop(threshold = 40.dp)
            val alpha by animateFloatAsState(targetValue = if (isAtTop) 0f else 1f, label = "")
            val backgroundColor by animateColorAsState(
                targetValue = MaterialTheme.colorScheme.background.copy(alpha = alpha),
                label = ""
            )
            val titleColor by animateColorAsState(
                targetValue = MaterialTheme.colorScheme.onPrimary.copy(alpha = alpha),
                label = ""
            )
            Surface(color = backgroundColor) {
                Column {
                    TopAppBar(
                        scrollBehavior = scrollBehavior,
                        colors = TopAppBarDefaults.topAppBarColors(
                            containerColor = Color.Unspecified,
                            scrolledContainerColor = Color.Unspecified,
                        ),
                        title = {
                            Text(
                                text = state.book.value.title,
                                style = MaterialTheme.typography.headlineSmall,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                color = titleColor
                            )
                        },
                        navigationIcon = {
                            IconButton(
                                onClick = onPressBack
                            ) {
                                Icon(Icons.Filled.ArrowBack, null)
                            }
                        },
                        actions = {
                            IconButton(
                                onClick = onLibraryToggle
                            ) {
                                Icon(
                                    if (state.book.value.inLibrary) Icons.Outlined.Favorite else Icons.Outlined.FavoriteBorder,
                                    stringResource(R.string.open_the_web_view),
                                    tint = ColorLike
                                )
                            }
                            IconButton(
                                onClick = { showBottomSheet = !showBottomSheet }
                            ) {
                                Icon(
                                    Icons.Filled.FilterList,
                                    stringResource(R.string.filter),
                                    tint = ColorNotice
                                )
                            }
                            IconButton(onClick = { showDropDown = !showDropDown }) {
                                Icon(
                                    Icons.Default.MoreVert,
                                    stringResource(R.string.options_panel)
                                )
                                DropdownMenu(
                                    expanded = showDropDown,
                                    onDismissRequest = { showDropDown = false }) {
                                    ChaptersDropDown(
                                        onOpenWebView = { context.goToWebBrowser(url = state.book.value.url) },
                                        onSearchBookInDatabase = onSearchBookInDatabase,
                                        onResumeReading = onResumeReading,
                                    )
                                }
                            }
                        }
                    )
                    Divider(Modifier.alpha(alpha))
                }
            }
            AnimatedVisibility(
                visible = state.isInSelectionMode.value,
                enter = expandVertically(initialHeight = { it / 2 }, expandFrom = Alignment.Top)
                        + fadeIn(),
                exit = shrinkVertically(targetHeight = { it / 2 }, shrinkTowards = Alignment.Top)
                        + fadeOut(),
            ) {
                Surface(color = BottomAppBarDefaults.containerColor.mix(ColorAccent, 0.6f)) {
                    TopAppBar(
                        scrollBehavior = scrollBehavior,
                        colors = TopAppBarDefaults.topAppBarColors(
                            containerColor = Color.Unspecified,
                            scrolledContainerColor = Color.Unspecified,
                        ),
                        title = {
                            Text(
                                text = state.selectedChaptersUrl.size.toString(),
                                style = MaterialTheme.typography.headlineSmall,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier.animateContentSize()
                            )
                        },
                        navigationIcon = {
                            IconButton(onClick = onCloseSelectionBar) {
                                Icon(
                                    Icons.Outlined.Close,
                                    stringResource(id = R.string.close_selection_bar)
                                )
                            }
                        },
                        actions = {
                            IconButton(onClick = onSelectAllChapters) {
                                Icon(
                                    Icons.Outlined.SelectAll,
                                    stringResource(id = R.string.select_all_chapters)
                                )
                            }
                            IconButton(onClick = onSelectedInvertSelection) {
                                Icon(
                                    Icons.Outlined.PublishedWithChanges,
                                    stringResource(id = R.string.close_selection_bar)
                                )
                            }
                        }
                    )
                }
            }
        },
        content = { innerPadding ->
            ChaptersScreenBody(
                state = state,
                innerPadding = innerPadding,
                lazyListState = lazyListState,
                onChapterClick = if (state.isInSelectionMode.value) onSelectionModeChapterClick else onChapterClick,
                onChapterLongClick = if (state.isInSelectionMode.value) onSelectionModeChapterLongClick else onChapterLongClick,
                onChapterDownload = onChapterDownload,
            )
        },
        bottomBar = bottomBar@{
            AnimatedVisibility(
                visible = state.isInSelectionMode.value,
                enter = expandVertically(initialHeight = { it / 2 }) + fadeIn(),
                exit = shrinkVertically(targetHeight = { it / 2 }) + fadeOut(),
            ) {
                BottomAppBar(
                    modifier = Modifier.clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)),
                    containerColor = BottomAppBarDefaults.containerColor.mix(ColorAccent, 0.6f)
                ) {
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        IconButton(onClick = onSelectedDeleteDownloads) {
                            Icon(
                                Icons.Outlined.Delete,
                                stringResource(id = R.string.remove_selected_chapters_dowloads)
                            )
                        }
                        IconButton(onClick = onSelectedDownload) {
                            Icon(
                                Icons.Outlined.CloudDownload,
                                stringResource(id = R.string.download_selected_chapters)
                            )
                        }
                        IconButton(onClick = onSelectedSetRead) {
                            Icon(
                                Icons.Filled.Done,
                                stringResource(id = R.string.set_as_read_selected_chapters)
                            )
                        }
                        IconButton(onClick = onSelectedSetUnread) {
                            Icon(
                                Icons.Filled.RemoveDone,
                                stringResource(id = R.string.set_as_not_read_selected_chapters)
                            )
                        }
                    }
                }

            }
        }
    )
}