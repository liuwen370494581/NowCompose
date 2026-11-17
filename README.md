# NowCompose

参考 Now in Android 的组件与状态组织方式，逐步学习 Jetpack Compose。第一版保持单个 `app` 模块，使用本地示例数据，不需要网络、Hilt 或数据库。

## 第一版可以体验什么

- 组件页：计数与重置、标签单选、收藏切换、展开收起动画。
- 文章页：五条本地示例文章、收藏与取消、全部与收藏筛选、无收藏时的空态。
- 页签切换和屏幕旋转时恢复界面状态。收藏使用 `rememberSaveable`，未接入持久化存储，不保证退出或重新启动应用后保留。
- 沿用 Material 3 主题，支持跟随系统的浅色和深色模式。

## 建议阅读顺序

源码位于 `app/src/main/java/com/lw/nowcompose/`：

1. `ui/catalog/CatalogScreen.kt`：从 `clickCount` 看“点击事件 → 修改状态 → 界面更新”，再看内容插槽和显示动画。
2. `ui/components/BookmarkToggle.kt`：组件接收 `checked` 和 `onCheckedChange`，不自己保存收藏状态。
3. `ui/articles/ArticleCard.kt`：使用 Row、Column、weight 和主题样式组合文章卡片；文件末尾有浅色、深色 Preview。
4. `ui/articles/ArticlesScreen.kt`：接收文章和筛选状态，通过稳定 key 识别文章，通过回调向外传递事件。文件末尾有全部、收藏和空态的 Preview。
5. `ui/articles/ArticlesRoute.kt`：在页面上层管理收藏与筛选状态，根据状态计算要显示的文章，再传给 Screen。
6. `ui/NowComposeApp.kt`：组织底部页签，使用 SaveableStateHolder 保留两个页签的局部状态。

`ui/articles/Article.kt` 提供本地演示模型与数据；文案统一放在 `app/src/main/res/values/strings.xml`。

## 第二阶段：文章列表与状态提升

这一阶段沿用第一版的文章功能，重点把状态与显示拆开。Route 是页面入口的命名约定，不需要导航库，也暂不引入 ViewModel。

```text
ArticlesRoute                  保存收藏 ID 和筛选条件，计算可见文章
    ↓ 数据与当前状态
ArticlesScreen                 用 LazyColumn 显示列表、筛选按钮与空态
    ↓ 单条文章与收藏状态
ArticleCard → BookmarkToggle   显示卡片和收藏按钮
    ↑ 点击通过回调逐层上报，最终由 Route 修改状态
```

可以从一次收藏操作开始跟读代码：

1. `BookmarkToggle` 的 `onCheckedChange` 报告点击。
2. `ArticleCard` 调用 `onToggleBookmark()`，自己不保存状态。
3. `ArticlesScreen` 把对应的 `article.id` 传给上层。
4. `ArticlesRoute` 替换收藏 ID 列表，Compose 根据新状态更新收藏图标和计数。
5. 如果正在“只看收藏”，取消收藏的文章会从筛选结果中移除；最后一条移除后显示空态。

这就是状态提升：多个组件需要共同使用的状态，由它们共同的上层管理。Screen 和 Card 不保存重复的收藏状态，也不直接修改外层传进来的列表。

阅读时重点看 `items(articles, key = { it.id })`：稳定 ID 让列表在筛选增删时正确识别文章。滚动位置由 LazyColumn 内部的列表状态管理，不需要放进收藏状态中。

## 手动验收

在 Android Studio 运行 `app`，或打开各文件末尾的 Preview 查看布局。

1. 组件页点击 +1 后重置；确认归零时重置按钮禁用。
2. 切换标签、切换收藏、展开说明；切换到文章页再回来，确认状态和滚动位置保留。
3. 文章页收藏两条，选择“只看收藏”；确认条目和数量一致。
4. 在收藏筛选下逐条取消收藏；确认移除最后一条时展示空态。
5. 旋转屏幕，确认页签、计数、筛选和收藏状态恢复。
6. 切换系统深色模式，检查文字、卡片和图标；使用大字体检查内容能正常滚动。

后续再逐步加入 ViewModel、StateFlow、详情页导航和收藏持久化。
