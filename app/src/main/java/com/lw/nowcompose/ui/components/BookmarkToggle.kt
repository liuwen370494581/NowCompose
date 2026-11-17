package com.lw.nowcompose.ui.components

import androidx.compose.material3.Icon
import androidx.compose.material3.IconToggleButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.lw.nowcompose.R
import com.lw.nowcompose.ui.theme.NowComposeTheme

// 组件只接收状态并上报事件，不在内部保存收藏状态，可用于练习页和文章卡片。
@Composable
fun BookmarkToggle(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    IconToggleButton(
        checked = checked,
        onCheckedChange = onCheckedChange,
        modifier = modifier,
    ) {
        Icon(
            painter = painterResource(
                if (checked) R.drawable.ic_bookmark else R.drawable.ic_bookmark_border,
            ),
            contentDescription = stringResource(
                if (checked) R.string.remove_bookmark else R.string.add_bookmark,
            ),
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun BookmarkTogglePreview() {
    NowComposeTheme(dynamicColor = false) {
        BookmarkToggle(checked = true, onCheckedChange = {})
    }
}
