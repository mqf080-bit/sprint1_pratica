# Home 页面搭建教程 — Step 1：页面骨架 top band

目标设计稿：医疗 App 首页（头像 / 问候语 / 铃铛 / 后续的服务九宫格、预约卡片、底部导航）。
本步只做最上面的 **AppBar 区**：头像、`Welcome! Rajesh`、`How is it going today?`、通知铃铛。

---

## 1. 先理解 3 个基础概念

| 概念 | 在本项目里的体现 |
|---|---|
| **Activity = 容器，Fragment = 页面** | `MainActivity` 只负责托管导航图；真正的一屏 = 一个 Fragment |
| **ConstraintLayout = 声明"我挨着谁"** | 每个 View 用 `layout_constraintTop_toBottomOf` 等声明相对位置，而不是靠容器顺序堆叠 |
| **Edge-to-edge + WindowInsets** | 布局从 y=0 开始画（背景延伸到状态栏底下），再手动把状态栏高度补成 padding |

---

## 2. 目录结构总览（本次新增/修改）

```
app/src/main/
├── java/com/example/sprint1/
│   ├── MainActivity.java          ← 改成纯容器（删掉了 ActionBar / Toolbar / FAB）
│   └── HomeFragment.java          ← 新增：首页逻辑 + insets 处理
└── res/
    ├── layout/
    │   ├── activity_main.xml      ← 改成只有一个 NavHostFragment
    │   └── fragment_home.xml      ← 新增：本步的界面
    ├── drawable/
    │   ├── bg_home_header_gradient.xml   ← 顶部粉色渐变氛围
    │   ├── bg_avatar_placeholder.xml     ← 头像占位（圆形 + 人像剪影）
    │   ├── ic_notifications_outline.xml  ← 铃铛图标 vector
    │   └── ic_account_circle.xml         ← 人像剪影 vector
    ├── navigation/nav_graph.xml   ← 起始页改成 HomeFragment
    └── values/
        ├── colors.xml             ← 全套品牌色
        ├── strings.xml            ← 文案抽成字符串
        └── themes.xml             ← Light 主题 + 透明系统栏
```

---

## 3. 手把手步骤

### Step 1.1 — 建立配色（`res/values/colors.xml`）

设计稿里反复出现的颜色先命名，后面所有布局只引用名字，改一处全站生效。

```xml
<color name="navy">#12355B</color>          <!-- 主文字 / 图标 -->
<color name="orange">#F5A623</color>        <!-- 主按钮 -->
<color name="text_secondary">#9AA3AE</color><!-- 副标题灰 -->
<color name="glow_pink">#5CF9A8C0</color>   <!-- 顶部粉雾 -->
```

> `#AARRGGBB`：前两位是透明度。`5C` = 36% 不透明，所以粉雾是"淡淡的"。

### Step 1.2 — 顶部粉雾背景（`drawable/bg_home_header_gradient.xml`）

不切图，用 `layer-list` + 径向渐变做一个"柔光"：

```xml
<layer-list>
    <item>  <!-- 底层：暖白纯色 -->
        <shape android:shape="rectangle"><solid android:color="@color/gradient_top"/></shape>
    </item>
    <item android:top="-120dp" android:right="-40dp" ...>  <!-- 上层：粉色椭圆光晕 -->
        <shape android:shape="oval">
            <gradient android:type="radial"
                      android:gradientRadius="220dp"
                      android:startColor="@color/glow_pink"
                      android:endColor="@color/glow_pink_soft"/>
        </shape>
    </item>
</layer-list>
```

要点：
- `shape="oval"` + `type="radial"` = 光晕；`endColor` 全透明才会自然过渡。
- item 上的 `top/bottom/left/right` 是**偏移量**，可以是负数，用来把光晕推出屏幕外。
- 光晕范围靠 `gradientRadius` 控制，跑起来觉得太大/太小就调这个值。

### Step 1.3 — 图标用 Vector 而不是 PNG

Android Studio 里 **右键 res/drawable → New → Vector Asset → 选 Clip Art** 就能生成 `ic_*.xml`。
本步用的铃铛 / 人像剪影已经放好；`ic_notifications_outline.xml` 里的 `pathData` 就是"路径指令"字符串，`fillColor` 是填充色。

> 用 vector 的好处：任意分辨率都不糊、体积只有几百字节、能整体染色（`app:iconTint` / `app:tint`）。

### Step 1.4 — 顶部区域布局（`layout/fragment_home.xml`）

**关键顺序**：先声明的 View 画在下面。所以粉雾 `View` 放在第一个，等于是背景层。

```xml
<ConstraintLayout android:id="@+id/homeRoot" ...>
    <View android:id="@+id/headerGradient" .../>   <!-- ① 背景层 -->

    <ImageView android:id="@+id/avatar"            <!-- ② 左上头像 -->
        android:layout_width="44dp"
        android:layout_height="44dp"
        android:src="@drawable/bg_avatar_placeholder"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintTop_toTopOf="parent" />

    <MaterialButton android:id="@+id/btnNotifications" .../>  <!-- ③ 右上铃铛 -->

    <TextView android:id="@+id/tvWelcome" .../>               <!-- ④ 问候语 -->
    <TextView android:id="@+id/tvWelcomeSubtitle" .../>       <!-- ⑤ 副标题 -->
</ConstraintLayout>
```

四个必须记住的属性：

| 属性 | 作用 |
|---|---|
| `layout_width="0dp"` | 在 ConstraintLayout 里表示 **match_constraint**：在左右约束之间撑满，不是"宽度 0" |
| `layout_constraintTop_toBottomOf="@id/avatar"` | 谁在谁下面（决定垂直顺序的唯一方式） |
| `layout_constraintStart_toStartOf="parent"` | 左对齐父容器（`End` = 右对齐） |
| `layout_margin*` | 和普通布局一样，但只有在**有对应约束的那一侧**才生效 |

**③ 铃铛为什么用 MaterialButton？** 因为它自带圆角背景、水波纹、描边。但 Material 按钮默认最小宽度 88dp、还有 4dp 的 elevation inset，所以必须显式清掉才能得到正圆：

```xml
android:layout_width="44dp" android:layout_height="44dp"
android:minWidth="0dp" android:minHeight="0dp"
android:insetLeft="0dp" android:insetTop="0dp"
android:insetRight="0dp" android:insetBottom="0dp"
android:padding="0dp"
app:cornerRadius="100dp"     <!-- 大于半径即圆 -->
app:icon="@drawable/ic_notifications_outline"
app:iconSize="22dp"
app:strokeColor="@color/stroke_light" app:strokeWidth="1dp"
```

**④ 问候语为什么换行？** 设计稿是两行（`Welcome!` / `Rajesh`）。这里没写死 `\n`，而是给它一个最大宽度，窄屏时自然折行：

```xml
android:maxWidth="220dp"
app:layout_constrainedWidth="true"     <!-- wrap_content 也要受 maxWidth 约束 -->
app:layout_constraintWidth_default="wrap"
```

### Step 1.5 — 页面代码（`java/.../HomeFragment.java`）

`app/build.gradle.kts` 里已开启 viewBinding，所以 `fragment_home.xml` 会自动生成 `FragmentHomeBinding`：

```java
public class HomeFragment extends Fragment {
    private FragmentHomeBinding binding;   // 字段名 = 文件名的驼峰 + Binding

    @Override
    public View onCreateView(...) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        return binding.getRoot();          // 注意是 getRoot()，不是 binding 本身
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        applyWindowInsets(view);
        binding.btnNotifications.setOnClickListener(v -> { /* Step 5 跳通知页 */ });
    }

    @Override
    public void onDestroyView() {          // Fragment 比 View 活得久，必须解绑
        super.onDestroyView();
        binding = null;
    }
}
```

> `@+id/btnNotifications` → `binding.btnNotifications`；`@+id/homeRoot` → `binding.homeRoot`。id 改名后代码会立刻报错，这正是用 viewBinding 的意义。

### Step 1.6 — Edge-to-edge：把状态栏高度补回来

主题是 edge-to-edge（内容画到状态栏下面），所以布局顶部会被状态栏盖住。解决方式是把系统栏尺寸转成 padding：

```java
ViewCompat.setOnApplyWindowInsetsListener(root, (v, insets) -> {
    Insets bars = insets.getInsets(
            WindowInsetsCompat.Type.systemBars() | WindowInsetsCompat.Type.displayCutout());
    v.setPadding(bars.left, bars.top + basePaddingTop, bars.right, bars.bottom);
    return insets;   // 返回原 insets，让子 View 还能继续处理
});

// 背景很浅 → 状态栏图标必须是深色
new WindowInsetsControllerCompat(requireActivity().getWindow(), root)
        .setAppearanceLightStatusBars(true);
```

**踩坑警告（本步已经处理好）**：如果父布局（这里是 `activity_main.xml`）写了 `android:fitsSystemWindows="true"`，它会**先吃掉** insets，Fragment 就永远收不到，最后表现为"顶部白了一大条"。所以 `activity_main.xml` 里绝对不能加它。

### Step 1.7 — 让首页成为启动页

`MainActivity` 里删掉 `setSupportActionBar(...)`、`onCreateOptionsMenu` 等 ActionBar 相关代码；
`activity_main.xml` 直接就是一个 `FragmentContainerView`（`app:navGraph="@navigation/nav_graph"`）；
然后在 `nav_graph.xml` 里把起始页换掉：

```xml
app:startDestination="@id/HomeFragment"
```

### Step 1.8 — 在 Android Studio 里跑起来

1. 打开 `fragment_home.xml`，右侧 **Design / Split** 面板可以实时预览（不用编译）。
2. 顶部工具栏选中 `app` 配置 → 点 ▶ **Run**。
3. 布局有问题时用 **Layout Inspector**（Tools → Layout Inspector）看真实层级和每个 View 的边界。

---

## 4. 本步验证清单

- [ ] 头像和铃铛左右各距屏幕边缘 24dp、距状态栏 12dp
- [ ] 铃铛是一个 44dp 正圆，白底 + 1dp 浅灰描边，点击有水波纹
- [ ] `Welcome!` 和 `Rajesh` 折成两行，字号 28sp、深蓝
- [ ] `How is it going today?` 是 14sp 灰字，在问候语下方 8dp
- [ ] 顶部能看到淡淡的粉色光晕（右上角最明显），并且**延伸到状态栏底下**
- [ ] 状态栏图标是深色、清晰可读

---

## 5. 下一步（Step 2 预告）

`Services` 区块：`LinearLayout` 横排 3 个等宽单元 + `layout_weight`，配圆形浅底图标。
再往后：Step 3 预约卡片（`MaterialCardView` + 圆角 + 左侧色条）、Step 4 可滚动容器、Step 5 底部导航栏 + 中间"加号"。
