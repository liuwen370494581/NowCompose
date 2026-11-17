package com.lw.nowcompose.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.saveable.rememberSaveableStateHolder
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.lw.nowcompose.R
import com.lw.nowcompose.ui.articles.ArticlesRoute
import com.lw.nowcompose.ui.catalog.CatalogScreen
import com.lw.nowcompose.ui.theme.NowComposeTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NowComposeApp(modifier: Modifier = Modifier) {
    var selectedTab by rememberSaveable { mutableIntStateOf(0) }

    val pageStateHolder = rememberSaveableStateHolder()

    Scaffold(
        modifier = modifier.fillMaxSize(),
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    icon = {
                        Icon(painterResource(R.drawable.ic_components), contentDescription = null)
                    },
                    label = { Text(stringResource(R.string.components_tab)) },
                )
                NavigationBarItem(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    icon = {
                        Icon(painterResource(R.drawable.ic_articles), contentDescription = null)
                    },
                    label = { Text(stringResource(R.string.articles_tab)) },
                )
            }
        },
    ) { innerPadding ->
        // 页签离开组合时保存它的局部状态，切回来恢复计数、筛选和滚动位置。
        pageStateHolder.SaveableStateProvider(selectedTab) {
            val pageModifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
            when (selectedTab) {
                0 -> CatalogScreen(modifier = pageModifier)
                1 -> ArticlesRoute(modifier = pageModifier)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun NowComposeAppPreview() {
    NowComposeTheme(dynamicColor = false) {
        NowComposeApp()
    }
}
