//
//  TextViewController.swift
//  PESDK Instagram Case Study
//
//  Created by Malte Baumann on 18.10.17.
//  Copyright © 2017 9elements GmbH. All rights reserved.
//

import UIKit
import PhotoEditorSDK

protocol TextViewControllerDelegate: class {
  func controller(_ textViewController: TextViewController, willFinishWith textModel: TextSpriteModel?)
  func referenceSizeFor(_ textViewController: TextViewController) -> CGSize?
  func imageToViewScaleFactorFor(_ textViewController: TextViewController) -> CGFloat?
}

class TextViewController: UIViewController {

  weak var delegate: TextViewControllerDelegate?
  
  @IBOutlet weak var backgroundToggleButton: UIButton!
  @IBOutlet weak var textContainerView: UIView!
  @IBOutlet weak var textField: UITextField!
  @IBOutlet weak var textFieldHorizontalCenterConstraint: NSLayoutConstraint!
  @IBOutlet weak var inputContainerBottomDistanceConstraint: NSLayoutConstraint!
  @IBOutlet weak var textFieldLeadingDistanceConstraint: NSLayoutConstraint!
  @IBOutlet weak var colorSelectionView: ColorSelectionView!
  @IBOutlet weak var textFieldTrailingDistanceConstraint: NSLayoutConstraint!
    
  var backgroundEnabled: Bool = false {
    didSet {
      backgroundToggleButton.setImage(backgroundEnabled ? UIImage(named: "fill-selected") : UIImage(named: "fill"), for: .normal)
    }
  }
  
  // MARK: - State updates
  
  var textSpriteModel: TextSpriteModel = TextSpriteModel() {
    didSet {
      textField.backgroundColor = textSpriteModel.backgroundColor
      textField.textAlignment = textSpriteModel.textAlignment
      textField.text = textSpriteModel.text
      if let fontIdentifier = textSpriteModel.fontIdentifier, let font = FontImporter.font(withIdentifier: fontIdentifier) {
        textField.font = UIFont(name: font.fontName, size: convertToAbsoluteValue(from: textSpriteModel.normalizedFontSize))
      }
      textField.textColor = textSpriteModel.textColor
      backgroundEnabled = textSpriteModel.backgroundColor != .clear
      colorSelectionView?.selectColor(backgroundEnabled ? textSpriteModel.backgroundColor : textSpriteModel.textColor)
    }
  }
  
  // MARK: - Size Calculations
  
  func convertToAbsoluteValue(from relativeValue: CGFloat) -> CGFloat {
    if let referenceSize = delegate?.referenceSizeFor(self), let scaleFactor = delegate?.imageToViewScaleFactorFor(self) {
      return relativeValue * min(referenceSize.width, referenceSize.height) * scaleFactor
    }
    
    return 0.0
  }
  
  func convertToRelativeValue(from absoluteVaue: CGFloat) -> CGFloat {
    if let referenceSize = delegate?.referenceSizeFor(self), let scaleFactor = delegate?.imageToViewScaleFactorFor(self) {
      return absoluteVaue / min(referenceSize.width, referenceSize.height) / scaleFactor
    }
    
    return 0.0
  }
  
  // MARK: - UIViewController
  
  override func viewDidLoad() {
    super.viewDidLoad()
    
    textField.keyboardAppearance = .dark
    colorSelectionView.delegate = self
  }
  
  override func viewDidAppear(_ animated: Bool) {
    super.viewDidAppear(animated)
    
    // Set initial text style
    var model = TextSpriteModel()
    let font = FontImporter.all[0]
    FontImporter.all = [font] // Force loading of the chosen font
    textField.font = UIFont(name: font.fontName, size: 24.0)
    model.fontIdentifier = font.identifier
    model.normalizedFontSize = convertToRelativeValue(from: textField.font!.pointSize)
    model.textColor = .white
    model.backgroundColor = .clear
    textSpriteModel = model
  }
  
  override func viewWillAppear(_ animated: Bool) {
    super.viewWillAppear(animated)
    
    textField.becomeFirstResponder()
    UIView.animate(withDuration: 0.25) {
      self.view.backgroundColor = UIColor(white: 0, alpha: 0.4)
    }

    NotificationCenter.default.addObserver(self, selector: #selector(TextViewController.animateKeyboardChangeFor), name: NSNotification.Name.UIKeyboardWillShow, object: nil)
    NotificationCenter.default.addObserver(self, selector: #selector(TextViewController.animateKeyboardChangeFor), name: NSNotification.Name.UIKeyboardWillHide, object: nil)
  }
  
  override func viewWillDisappear(_ animated: Bool) {
    super.viewWillDisappear(animated)
    NotificationCenter.default.removeObserver(self)
  }
  
  @objc private func animateKeyboardChangeFor(notification: Notification) {
    if let userInfo = notification.userInfo {
      guard
        let animationDuration = (userInfo[UIKeyboardAnimationDurationUserInfoKey] as? NSNumber)?.doubleValue,
        let keyboardEndFrame = (userInfo[UIKeyboardFrameEndUserInfoKey] as? NSValue)?.cgRectValue,
        let rawAnimationCurve = (userInfo[UIKeyboardAnimationCurveUserInfoKey] as? NSNumber)?.uintValue
      else {
        return
      }
      
      inputContainerBottomDistanceConstraint.constant = keyboardEndFrame.size.height
      let animationCurve = UIViewAnimationOptions.init(rawValue: rawAnimationCurve << 16)
      UIView.animate(withDuration: animationDuration, delay: 0, options: [animationCurve], animations: {
        self.view.layoutIfNeeded()
      }, completion: nil)
    }
  }
  
  // MARK: - Actions
  
  @IBAction func alignmentButtonTapped(sender: UIButton) {
    let availableAlignmentModes: [NSTextAlignment] = [.left, .center, .right]
    guard let currentIndex = availableAlignmentModes.index(of: textSpriteModel.textAlignment) else { return }
    let nextIndex = (currentIndex + 1) % availableAlignmentModes.count
    let newAlignmentMode = availableAlignmentModes[nextIndex]
    var currentModel = textSpriteModel
    currentModel.textAlignment = newAlignmentMode
    textSpriteModel = currentModel
    
    switch newAlignmentMode {
    case .left:
      sender.setImage(UIImage(named: "alignment-left"), for: .normal)
      textFieldHorizontalCenterConstraint.isActive = false
      textFieldLeadingDistanceConstraint.isActive = true
      textFieldTrailingDistanceConstraint.isActive = false
    case .right:
      sender.setImage(UIImage(named: "alignment-right"), for: .normal)
      textFieldHorizontalCenterConstraint.isActive = false
      textFieldLeadingDistanceConstraint.isActive = false
      textFieldTrailingDistanceConstraint.isActive = true
    case .center:
      sender.setImage(UIImage(named: "alignment"), for: .normal)
      textFieldHorizontalCenterConstraint.isActive = true
      textFieldLeadingDistanceConstraint.isActive = false
      textFieldTrailingDistanceConstraint.isActive = false
    default:
      break
    }
    
    view.layoutIfNeeded()
  }
  
  @IBAction func backgroundToggleButtonTapped(sender: UIButton) {
    backgroundEnabled = !backgroundEnabled
    if backgroundEnabled {
      textSpriteModel.backgroundColor = textSpriteModel.textColor == .white ? ColorSelectionView.availableColors[2] : textSpriteModel.textColor
      textSpriteModel.textColor = .white
    } else {
      textSpriteModel.textColor = textSpriteModel.backgroundColor
      textSpriteModel.backgroundColor = .clear
    }
  }
  
  @IBAction func doneButtonTapped(sender: UIButton) {
    // If no center has been set yet, we calculate the current position within
    // the overall image and update the model accordingly
    if textSpriteModel.normalizedCenter == .zero {
      let convertedCenter = textContainerView.convert(textField.center, to: view)
      textSpriteModel.normalizedCenter = CGPoint(x: convertedCenter.x / view.bounds.width, y: convertedCenter.y / view.bounds.height)
    }
    
    textField.resignFirstResponder()
    delegate?.controller(self, willFinishWith: textSpriteModel.text?.trimmingCharacters(in: .whitespacesAndNewlines).count == 0 ? nil : textSpriteModel)
    dismiss(animated: true, completion: nil)
  }
  
  // MARK: - UITextField
  
  @IBAction func textFieldDidChange(_ sender: UITextField) {
    textSpriteModel.text = sender.text
    textSpriteModel.normalizedWidth = convertToRelativeValue(from: textField.bounds.width * 1.3)
  }
}

extension TextViewController: ColorSelectionViewDelegate {
  func colorSelectionView(_ view: ColorSelectionView, didSelect color: UIColor) {
    if backgroundEnabled {
      textSpriteModel.backgroundColor = color
      textField.backgroundColor = color
    } else {
      textSpriteModel.textColor = color
      textField.textColor = color
    }
  }
}
