# OpenConnect for Android

[![License: GPL v2](https://img.shields.io/badge/License-GPL%20v2-blue.svg)](https://www.gnu.org/licenses/old-licenses/gpl-2.0.en.html)

基于 [OpenConnect](http://www.infradead.org/openconnect/) Linux 版本构建的 Android VPN 客户端。

本项目 fork 自 [cernekee/ics-openconnect](https://github.com/cernekee/ics-openconnect)，原项目已停止维护。本分支在原项目基础上进行了 Material Design 现代化改造和 Android 15 适配。

## 主要改动

### UI 现代化：Holo → Material Design

- **主题迁移**：`Theme.Holo.Light` → `Theme.Material3.Light`，全面采用 Material Design 3 配色
- **导航栏迁移**：`ActionBar.NAVIGATION_MODE_TABS` → `TabLayout + ViewPager2`，支持左右滑动切换页面
- **移除 splitActionBarWhenNarrow**：解决了底部操作栏遮挡内容区域的问题
- **Toolbar 替代 ActionBar**：MainActivity 使用 `CoordinatorLayout + MaterialToolbar`，支持滚动行为
- **页面边距**：所有内容页面添加 16dp 水平内边距，不再贴边显示
- **FAQ 页面修复**：修复了 Holo 时代遗留的白色字体在白色背景上看不清的问题

### Android 15 适配

- **Edge-to-edge 处理**：Android 15 默认启用边到边显示，通过 `fitsSystemWindows` 和 `WindowInsetsCompat` 确保内容不被状态栏/导航栏遮挡
- **minSdk/targetSdk 升级至 35**：适配 Android 15 API
- **编译工具链升级**：compileSdk 35，Gradle 8.x，AndroidX 依赖

### 关键 Bug 修复

- **密码认证对话框不显示**：`VPNConnector` 中广播接收器的 `RECEIVER_NOT_EXPORTED` 改为 `RECEIVER_EXPORTED`，修复了 Android 14+ 上认证对话框无法弹出的问题
- **PendingIntent 标志**：`OpenVpnService` 中 PendingIntent 添加 `FLAG_IMMUTABLE`，适配 Android 12+ 安全要求
- **VPN 线程重复启动**：`onStartCommand` 中增加重复启动保护
- **表单认证后无法连接**：`OpenConnectManagementThread` 中表单提交后补充 `resetCancel()` 调用
- **空指针崩溃**：`ProfileManager` 中 `prefsdir.list()` 返回值增加空检查

### 依赖迁移

- `android.app.Fragment` / `ListFragment` → `androidx.fragment.app.Fragment`
- `Activity` → `AppCompatActivity`
- 新增 AndroidX 依赖：appcompat、material、fragment、viewpager2、coordinatorlayout

## 截图

![screenshot-0](screenshots/screenshot-0.png)&nbsp;
![screenshot-1](screenshots/screenshot-1.png)

![screenshot-2](screenshots/screenshot-2.png)&nbsp;
![screenshot-3](screenshots/screenshot-3.png)

## 从源码构建

### 前置条件

- Android SDK（设置 `ANDROID_HOME` 环境变量）
- JDK 17+
- Android NDK（如需编译 native 组件）
- git

### 编译

```bash
git clone https://github.com/fengerzh/ics-openconnect
cd ics-openconnect
git submodule init
git submodule update
make -C external    # 编译 native 依赖（需要 Linux 主机）
./gradlew assembleDebug
```

安装到设备：

```bash
adb install -r app/build/outputs/apk/debug/app-debug.apk
```

如果无法编译 native 组件，可以尝试下载 CI 构建缓存：

```bash
./misc/download-artifacts.sh
```

## 许可证

OpenConnect for Android 基于 GPLv2 许可证发布，详见 [COPYING](COPYING) 和 [doc/LICENSE.txt](doc/LICENSE.txt)。

Java 代码部分源自 Arne Schwabe 的 [OpenVPN for Android](https://play.google.com/store/apps/details?id=de.blinkt.openvpn&hl=en)。
