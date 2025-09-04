import type { HybridObject } from 'react-native-nitro-modules'

export interface NitroSplashScreen
  extends HybridObject<{ ios: 'swift'; android: 'kotlin' }> {
  show(theme?: string): void
  hide(): void
}
