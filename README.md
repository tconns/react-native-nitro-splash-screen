# react-native-nitro-splash-screen

Native splash screen control for React Native built with Nitro Modules.

## Overview

This module provides native-level splash screen control for both Android and iOS. It offers simple JavaScript/TypeScript APIs to show and hide splash screens with custom themes and animations.

## Features

- 🎨 Show/hide native splash screens
- 🌙 Light and dark theme support
- � Cross-platform (iOS & Android)
-  Built with Nitro Modules for native performance
- 🔧 Custom layout support via XML (Android) and XIB (iOS)
- ⚡ Zero-dependency splash screen management
- 🎯 Automatic activity lifecycle management

## Requirements

- React Native >= 0.76
- Node >= 18
- `react-native-nitro-modules` must be installed (Nitro runtime)

## Installation

```bash
npm install react-native-nitro-splash-screen react-native-nitro-modules
# or
yarn add react-native-nitro-splash-screen react-native-nitro-modules
```

## Quick Usage

```typescript
import NitroSplashScreen from 'react-native-nitro-splash-screen'

// Show splash screen with light theme
NitroSplashScreen.show('light')

// Hide splash screen
NitroSplashScreen.hide()

// Check if splash screen is showing
const isShowing = NitroSplashScreen.isShowing()
```

## API Reference

### Methods

#### `show(theme?: 'light' | 'dark'): void`
Shows the splash screen with the specified theme.

**Parameters:**
- `theme` (optional): Theme for the splash screen
  - `'light'` - Light theme with white background (default)
  - `'dark'` - Dark theme with black background

**Example:**
```typescript
// Show with light theme (default)
NitroSplashScreen.show()

// Show with dark theme
NitroSplashScreen.show('dark')

// Show with light theme explicitly
NitroSplashScreen.show('light')
```

#### `hide(): void`
Hides the currently displayed splash screen.

**Example:**
```typescript
NitroSplashScreen.hide()
```

#### `isShowing(): boolean`
Returns whether the splash screen is currently being displayed.

**Returns:** `boolean` - `true` if splash screen is showing, `false` otherwise

**Example:**
```typescript
const isVisible = NitroSplashScreen.isShowing()
if (isVisible) {
  console.log('Splash screen is currently showing')
}
```

## Usage Examples

### Basic Usage in App Component

```typescript
import React, { useEffect } from 'react'
import { Text, View } from 'react-native'
import NitroSplashScreen from 'react-native-nitro-splash-screen'

const App = () => {
  useEffect(() => {
    // Show splash screen when app starts
    NitroSplashScreen.show('light')
    
    // Simulate app initialization
    setTimeout(() => {
      // Hide splash screen after 2 seconds
      NitroSplashScreen.hide()
    }, 2000)
  }, [])

  return (
    <View style={{ flex: 1, justifyContent: 'center', alignItems: 'center' }}>
      <Text>Welcome to My App!</Text>
    </View>
  )
}

export default App
```

### Theme-based Splash Screen

```typescript
import React, { useEffect, useState } from 'react'
import { useColorScheme } from 'react-native'
import NitroSplashScreen from 'react-native-nitro-splash-screen'

const App = () => {
  const colorScheme = useColorScheme()
  const [isReady, setIsReady] = useState(false)

  useEffect(() => {
    const initializeApp = async () => {
      // Show splash screen with system theme
      const theme = colorScheme === 'dark' ? 'dark' : 'light'
      NitroSplashScreen.show(theme)
      
      try {
        // Perform app initialization tasks
        await loadUserData()
        await setupAnalytics()
        await configurePushNotifications()
        
        setIsReady(true)
      } finally {
        // Always hide splash screen
        NitroSplashScreen.hide()
      }
    }

    initializeApp()
  }, [colorScheme])

  if (!isReady) {
    return null // Splash screen is showing
  }

  return (
    // Your main app content
    <MainAppComponent />
  )
}
```

### Manual Control with State

```typescript
import React, { useState } from 'react'
import { View, Button, Text } from 'react-native'
import NitroSplashScreen from 'react-native-nitro-splash-screen'

const SplashControlScreen = () => {
  const [isShowing, setIsShowing] = useState(false)

  const showSplash = (theme: 'light' | 'dark') => {
    NitroSplashScreen.show(theme)
    setIsShowing(true)
  }

  const hideSplash = () => {
    NitroSplashScreen.hide()
    setIsShowing(false)
  }

  const checkStatus = () => {
    const showing = NitroSplashScreen.isShowing()
    setIsShowing(showing)
  }

  return (
    <View style={{ flex: 1, justifyContent: 'center', padding: 20 }}>
      <Text>Splash Screen Status: {isShowing ? 'Showing' : 'Hidden'}</Text>
      
      <Button title="Show Light Splash" onPress={() => showSplash('light')} />
      <Button title="Show Dark Splash" onPress={() => showSplash('dark')} />
      <Button title="Hide Splash" onPress={hideSplash} />
      <Button title="Check Status" onPress={checkStatus} />
    </View>
  )
}
```

## Custom Layout Configuration

### Android Custom Layout

Create a custom layout file at `android/app/src/main/res/layout/activity_splash.xml`:

```xml
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:gravity="center"
    android:orientation="vertical"
    android:fitsSystemWindows="false">

    <ImageView
        android:layout_width="200dp"
        android:layout_height="200dp"
        android:layout_marginBottom="32dp"
        android:src="@drawable/splash_logo"
        android:scaleType="centerInside" />

    <TextView
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:text="Loading..."
        android:textSize="18sp"
        android:textColor="#666666" />

    <ProgressBar
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:layout_marginTop="16dp"
        style="?android:attr/progressBarStyle" />

</LinearLayout>
```

Add your logo to `android/app/src/main/res/drawable/splash_logo.png`

### iOS Custom Layout

For iOS, place your splash screen images in the appropriate directories:
- `ios/YourApp/Images.xcassets/SplashLogo.imageset/`

## Platform-Specific Features

### Android Features
- ✅ Custom XML layout support
- ✅ Fullscreen display (hides status bar and navigation bar)
- ✅ Theme-based background colors
- ✅ Activity lifecycle management
- ✅ Memory leak prevention

### iOS Features
- ✅ Custom XIB/Storyboard support
- ✅ Native iOS splash screen integration
- ✅ Theme support
- ✅ Proper view controller management

## Best Practices

### 1. **App Initialization Pattern**
```typescript
useEffect(() => {
  const bootstrap = async () => {
    NitroSplashScreen.show('light')
    
    try {
      await Promise.all([
        initializeServices(),
        loadAsyncStorage(),
        setupNavigator()
      ])
    } finally {
      NitroSplashScreen.hide()
    }
  }
  
  bootstrap()
}, [])
```

### 2. **Error Handling**
```typescript
const initializeApp = async () => {
  NitroSplashScreen.show()
  
  try {
    await loadCriticalData()
  } catch (error) {
    // Always hide splash screen, even on error
    NitroSplashScreen.hide()
    showErrorScreen(error)
    return
  }
  
  NitroSplashScreen.hide()
}
```

### 3. **Theme Consistency**
```typescript
const getThemeForSplash = () => {
  const isDark = useColorScheme() === 'dark'
  return isDark ? 'dark' : 'light'
}

// Use consistent theming
NitroSplashScreen.show(getThemeForSplash())
```

## Troubleshooting

### Common Issues

#### Android
- **Splash screen not fullscreen**: Ensure your XML layout has `android:fitsSystemWindows="false"`
- **Layout appears as square**: Make sure root element uses `match_parent` for width/height
- **Logo not showing**: Check that your logo file exists in `res/drawable/`

#### iOS  
- **Theme not applied**: Verify theme parameter is being passed correctly
- **Splash not hiding**: Ensure `hide()` is called even in error scenarios

#### General
- **Memory leaks**: Always call `hide()` to properly clean up resources
- **Multiple splash screens**: Check `isShowing()` before calling `show()` again

### Debug Tips

```typescript
// Debug splash screen state
console.log('Splash showing:', NitroSplashScreen.isShowing())

// Ensure cleanup in development
useEffect(() => {
  return () => {
    if (NitroSplashScreen.isShowing()) {
      NitroSplashScreen.hide()
    }
  }
}, [])
```

## Migration from Other Libraries

### From react-native-splash-screen

```typescript
// Before
import SplashScreen from 'react-native-splash-screen'
SplashScreen.show()
SplashScreen.hide()

// After  
import NitroSplashScreen from 'react-native-nitro-splash-screen'
NitroSplashScreen.show()
NitroSplashScreen.hide()
```

### From @react-native-async-storage/async-storage

The API is very similar, just import from the new package and optionally add theme support.

## Development

### Regenerating Nitro Artifacts

After editing spec files in `src/specs/*.nitro.ts`:

```bash
npx nitro-codegen
```

### Project Structure

- `android/` — Native Android implementation (Kotlin)
- `ios/` — Native iOS implementation (Swift)
- `src/` — TypeScript API exports and Nitro specs
- `nitrogen/` — Generated Nitro binding code

## Contributing

See `CONTRIBUTING.md` for development setup and contribution guidelines.

## License

MIT © [Thành Công](https://github.com/tconns)

## Acknowledgements

Built with [Nitro Modules](https://github.com/mrousavy/nitro) by Marc Rousavy.
