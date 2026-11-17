# NowCompose

参考 Now in Android 的组件与状态组织方式，逐步学习 Jetpack Compose。保持单个 `app` 模块，使用本地示例数据，不需要网络、Hilt 或数据库。当前已加入 ViewModel、StateFlow 和四种页面状态。

## 第一版可以体验什么

- 组件页：计数与重置、标签单选、收藏切换、展开收起动画。
- 文章页：五条本地示例文章、收藏与取消、全部与收藏筛选、无收藏时的空态。
- 页签切换和屏幕旋转时恢复界面状态。组件状态使用 `rememberSaveable`，文章收藏和筛选使用 `SavedStateHandle`，未接入持久化存储，不保证退出或重新启动应用后保留。
- 沿用 Material 3 主题，支持跟随系统的浅色和深色模式。

## 建议阅读顺序

源码位于 `app/src/main/java/com/lw/nowcompose/`：

1. `ui/catalog/CatalogScreen.kt`：从 `clickCount` 看“点击事件 → 修改状态 → 界面更新”，再看内容插槽和显示动画。
2. `ui/components/BookmarkToggle.kt`：组件接收 `checked` 和 `onCheckedChange`，不自己保存收藏状态。
3. `ui/articles/ArticleCard.kt`：使用 Row、Column、weight 和主题样式组合文章卡片；文件末尾有浅色、深色 Preview。
4. `ui/articles/ArticlesScreen.kt`：接收 UiState，根据状态展示加载、内容、空态和错误，通过回调向外传递事件。文件末尾有四种状态的 Preview。
5. `ui/articles/ArticlesRoute.kt`：通过 `collectAsStateWithLifecycle()` 收集 ViewModel 状态，把页面操作连接到 ViewModel。
6. `ui/articles/ArticlesUiState.kt` 和 `ui/articles/ArticlesViewModel.kt`：定义状态、响应用户操作，并组合出 StateFlow。
7. `ui/NowComposeApp.kt`：组织底部页签，使用 SaveableStateHolder 保留两个页签的局部状态。

`ui/articles/Article.kt` 提供本地演示模型与数据；文案统一放在 `app/src/main/res/values/strings.xml`。

## 第二阶段：文章列表与状态提升

第二阶段先把状态与显示拆开，最初由 Route 保存状态；第三阶段在此基础上将状态迁到 ViewModel。Route 是页面入口的命名约定，不需要导航库。

```text
ArticlesViewModel              保存收藏 ID 和筛选条件，生成 UiState
    ↓ StateFlow
ArticlesRoute                  收集状态、连接事件
    ↓ 数据与当前状态
ArticlesScreen                 用 LazyColumn 显示列表、筛选按钮与空态
    ↓ 单条文章与收藏状态
ArticleCard → BookmarkToggle   显示卡片和收藏按钮
    ↑ 点击通过回调逐层上报，最终由 ViewModel 修改状态
```

可以从一次收藏操作开始跟读代码：

1. `BookmarkToggle` 的 `onCheckedChange` 报告点击。
2. `ArticleCard` 调用 `onToggleBookmark()`，自己不保存状态。
3. `ArticlesScreen` 把对应的 `article.id` 传给上层。
4. `ArticlesViewModel` 更新 SavedStateHandle 中的收藏 ID，combine 生成新的 UiState，Route 收集后驱动界面更新。
5. 如果正在“只看收藏”，取消收藏的文章会从筛选结果中移除；最后一条移除后显示空态。

这就是状态提升：多个组件需要共同使用的状态，由它们共同的上层管理。Screen 和 Card 不保存重复的收藏状态，也不直接修改外层传进来的列表。

阅读时重点看 `items(feed.articles, key = { it.id })`：稳定 ID 让列表在筛选增删时正确识别文章。滚动位置由 LazyColumn 内部的列表状态管理，不需要放进收藏状态中。

## 第三阶段：ViewModel、UiState 与 StateFlow

- `ArticlesUiState.kt`：`Loading / Content / Empty / Error` 互斥，收藏与筛选条件一起构成页面状态。
- `ArticlesViewModel.kt`：在 `viewModelScope` 中加载数据，使用 `combine` 把加载结果、收藏 ID 和筛选条件转成只读 `StateFlow<ArticlesUiState>`。
- `DemoArticlesRepository.kt`：延迟 700 毫秒返回本地文章；“模拟加载失败”会抛出预期的 IOException，方便体验错误与重试。
- `ArticlesRoute.kt`：在生命周期活跃时收集状态，并把操作连接到 ViewModel 的方法。

单向数据流：用户操作 → ViewModel 方法 → 更新输入状态 → 生成 UiState → Screen 根据状态显示。Screen 不直接修改 StateFlow，也不发起加载。

`stateIn(WhileSubscribed(5_000))` 在没有订阅者 5 秒后停止上游状态组合；数据加载任务属于 ViewModel，首次创建时启动，切换页签不会重新创建它。当前未引入导航，ViewModel 的作用域是 Activity。旋转屏幕时复用 ViewModel，进程恢复时从 SavedStateHandle 恢复收藏与筛选并重新加载本地文章。

这里仅捕获预期的 IOException，不吞掉协程取消。重新加载先取消旧任务，取消不会被显示成错误。ViewModel 负责业务数据，列表滚动位置仍由 Compose 管理。

文章页相关依赖沿用项目的 Lifecycle 版本，并补充运行时协程库。当前通过各状态的 Preview 和下面的手动步骤验证页面。

## 手动验收

在 Android Studio 运行 `app`，或打开各文件末尾的 Preview 查看布局。

1. 组件页点击 +1 后重置；确认归零时重置按钮禁用。
2. 切换标签、切换收藏、展开说明；切换到文章页再回来，确认状态和滚动位置保留。
3. 文章页收藏两条，选择“只看收藏”；确认条目和数量一致。
4. 在收藏筛选下逐条取消收藏；确认移除最后一条时展示空态。
5. 旋转屏幕，确认页签、计数、筛选和收藏状态恢复。
6. 切换系统深色模式，检查文字、卡片和图标；使用大字体检查内容能正常滚动。
7. 首次进入文章页查看短暂的加载态；“重新加载”可再次体验。
8. 点击“模拟加载失败”，确认先显示加载，随后显示错误卡片；点击“重试”恢复文章，收藏和筛选保持。
9. 在文章页旋转屏幕或切换页签，确认不会重复发起首次加载。

后续再逐步加入详情页导航和收藏持久化。
