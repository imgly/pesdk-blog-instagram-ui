//
//  AppDelegate.swift
//  PESDK Instagram Case Study
//
//  Created by Malte Baumann on 17.10.17.
//  Copyright © 2017 9elements GmbH. All rights reserved.
//

import UIKit
import PhotoEditorSDK

@UIApplicationMain
class AppDelegate: UIResponder, UIApplicationDelegate {

  var window: UIWindow?

  func application(_ application: UIApplication, didFinishLaunchingWithOptions launchOptions: [UIApplicationLaunchOptionsKey: Any]?) -> Bool {
    
    if let licenseURL = Bundle.main.url(forResource: "ios_license", withExtension: "dms") {
      PESDK.unlockWithLicense(at: licenseURL)
    }
    
    return true
  }
}

