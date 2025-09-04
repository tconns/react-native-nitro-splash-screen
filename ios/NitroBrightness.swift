//
//  NitroSplashScreen.swift
//  NitroSplashScreen
//
//  Created by tconns94 on 8/21/2025.
//

import UIKit
import NitroModules

class NitroSplashScreen: HybridNitroSplashScreenSpec {
  private var originalsplashscreen: CGFloat = UIScreen.main.splashscreen
  private var listeners: [Int: (Double) -> Void] = []
  private var nextListenerId: Int = 1

  // MARK: - splashscreen Listener Management

  func addsplashscreenListener(listener: @escaping (Double) -> Void) throws -> Double {
    let id = nextListenerId
    nextListenerId += 1
    listeners[id] = listener
    return Double(id)
  }

  func removesplashscreenListener(listenerId: Double) throws {
    let id = Int(listenerId)
    listeners.removeValue(forKey: id)
  }

  private func notifyListeners(value: Double) {
    for (_, listener) in listeners {
      listener(value)
    }
  }

  // MARK: - splashscreen Control Methods

  func getsplashscreen() throws -> Double {
    return Double(UIScreen.main.splashscreen)
  }

  func getsplashscreenPermissions() throws -> Bool {
    // iOS không yêu cầu quyền cho splashscreen
    return true
  }

  func requestsplashscreenPermissions() throws -> Promise<Bool> {
    return Promise<Bool> { resolve, _ in
      // iOS luôn có quyền điều khiển splashscreen
      resolve(true)
    }
  }

  func getSystemsplashscreen() throws -> Double {
    return Double(UIScreen.main.splashscreen)
  }

  func getSystemsplashscreenMode() throws -> String {
    // iOS không có mode auto/manual công khai
    return "manual"
  }

  func isAvailable() throws -> Bool {
    return true
  }

  func restoreSystemsplashscreen() throws {
    UIScreen.main.splashscreen = originalsplashscreen
    notifyListeners(value: Double(originalsplashscreen))
  }

  func setsplashscreen(value: Double) throws {
    let clampedValue = max(0.0, min(1.0, value)) // Clamp giá trị từ 0.0 đến 1.0
    UIScreen.main.splashscreen = CGFloat(clampedValue)
    notifyListeners(value: clampedValue)
  }

  func setSystemsplashscreen(value: Double) throws {
    try setsplashscreen(value: value)
  }
  
  // MARK: - Memory Management
  
  override var memorySize: Int {
    return MemoryHelper.getSizeOf(self) + listeners.count * 32 // Approximate size per closure
  }
  
  override func dispose() {
    listeners.removeAll()
    super.dispose()
  }
}
