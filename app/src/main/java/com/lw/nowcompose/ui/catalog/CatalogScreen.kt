package com.lw.nowcompose.ui.catalog

import androidx.annotation.StringRes
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.lw.nowcompose.R
import com.lw.nowcompose.ui.components.BookmarkToggle
import com.lw.nowcompose.ui.theme.NowComposeTheme

@Composable
fun CatalogScreen(modifier: Modifier = Modifier) {
    // 状态属于页面，子组件只接收值和回调。rememberSaveable 支持界面重建后恢复。
    var clickCount by rememberSaveable { mutableIntStateOf(0) }
    var selectedTopic by rememberSaveable { mutableIntStateOf(0) }
    var bookmarked by rememberSaveable { mutableStateOf(false) }
    var expanded by rememberSaveable { mutableStateOf(false) }

    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        item {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = stringResource(R.string.catalog_title),
                    style = MaterialTheme.typography.headlineMedium,
                )
                Text(
                    text = stringResource(R.string.catalog_description),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
        item {
            PracticeSection(R.string.buttons_title, R.string.buttons_description) {
                Text(
                    text = stringResource(R.string.click_count, clickCount),
                    style = MaterialTheme.typography.titleLarge,
                )
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Button(onClick = { clickCount++ }) {
                        Text(stringResource(R.string.increment_count))
                    }
                    OutlinedButton(onClick = { clickCount = 0 }, enabled = clickCount > 0) {
                        Text(stringResource(R.string.reset_count))
                    }
                }
            }
        }
        item {
            PracticeSection(R.string.chips_title, R.string.chips_description) {
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    FilterChip(
                        selected = selectedTopic == 0,
                        onClick = { selectedTopic = 0 },
                        label = { Text(stringResource(R.string.topic_layout)) },
                    )
                    FilterChip(
                        selected = selectedTopic == 1,
                        onClick = { selectedTopic = 1 },
                        label = { Text(stringResource(R.string.topic_state)) },
                    )
                }
                Text(
                    stringResource(
                        R.string.selected_topic,
                        stringResource(
                            if (selectedTopic == 0) R.string.topic_layout else R.string.topic_state,
                        ),
                    ),
                )
            }
        }
        item {
            PracticeSection(R.string.bookmark_title, R.string.bookmark_description) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    BookmarkToggle(
                        checked = bookmarked,
                        onCheckedChange = { bookmarked = it },
                    )
                    Text(
                        text = stringResource(
                            if (bookmarked) R.string.bookmarked else R.string.not_bookmarked,
                        ),
                        modifier = Modifier.weight(1f),
                    )
                }
            }
        }
        item {
            PracticeSection(R.string.expand_title, R.string.expand_description) {
                TextButton(onClick = { expanded = !expanded }) {
                    Text(stringResource(if (expanded) R.string.collapse else R.string.expand))
                }
                AnimatedVisibility(visible = expanded) {
                    Text(
                        text = stringResource(R.string.expanded_content),
                        style = MaterialTheme.typography.bodyMedium,
                    )
                }
            }
        }
    }
}

// 内容插槽：复用卡片容器，里面可以放按钮、标签或任意其他 Compose 内容。
@Composable
private fun PracticeSection(
    @StringRes titleRes: Int,
    @StringRes descriptionRes: Int,
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit,
) {
    ElevatedCard(modifier = modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text(stringResource(titleRes), style = MaterialTheme.typography.titleMedium)
            Text(
                text = stringResource(descriptionRes),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            content()
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun CatalogScreenPreview() {
    NowComposeTheme(dynamicColor = false) {
        CatalogScreen()
    }
}
