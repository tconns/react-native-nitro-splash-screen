import { NitroModules } from 'react-native-nitro-modules'
import type { NitroSplashScreen as NitroSplashScreenSpec } from './specs/NitroSplashScreen.nitro'

const NitroSplashScreenModule =
  NitroModules.createHybridObject<NitroSplashScreenSpec>('NitroSplashScreen')

export const show = (theme?: string) => {
  return NitroSplashScreenModule.show(theme)
}

export const hide = () => {
  return NitroSplashScreenModule.hide()
}
