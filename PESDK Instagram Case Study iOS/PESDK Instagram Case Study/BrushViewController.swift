//
//  BrushViewController.swift
//  PESDK Instagram Case Study
//
//  Created by Malte Baumann on 20.10.17.
//  Copyright © 2017 9elements GmbH. All rights reserved.
//

import UIKit
import PhotoEditorSDK

protocol BrushViewControllerDelegate: class {
  func brushControllerTargetScrollView(_ controller: BrushViewController) -> UIScrollView?
  func brushControllerSpriteViewController(_ controller: BrushViewController) -> SpriteViewController?
  func brushControllerPhotoEditModel(_ controller: BrushViewController) -> PhotoEditModel?
  func undoController(_ controller: BrushViewController) -> UndoController?
  
  func brushController(_ controller: BrushViewController, didChange model: PhotoEditModel)
  func brushControllerDidFinish(_ controller: BrushViewController)
}

class BrushViewController: UIViewController {
  
  weak var delegate: BrushViewControllerDelegate?
  
  private var brushEditController: BrushEditController?

  @IBOutlet weak var colorSelectionView: ColorSelectionView!
  @IBOutlet weak var brushEditContainer: UIView!
  @IBOutlet weak var brushSizeSlider: UISlider!
  @IBOutlet weak var undoButton: UIButton!
  
  // MARK: - UIViewController
  
  override func viewDidLoad() {
    super.viewDidLoad()
    
    // Setup the brush canvas
    let brushEditController = BrushEditController()
    brushEditController.delegate = self
    addChildViewController(brushEditController)
    brushEditContainer.addSubview(brushEditController.view)
    brushEditController.didMove(toParentViewController: self)
    self.brushEditController = brushEditController
    
    // Ensure an existing brush sprite model to draw in exists
    if var photoEditModel = delegate?.brushControllerPhotoEditModel(self) {
      if !photoEditModel.spriteModels.contains(where: { $0 is BrushSpriteModel }) {
        // Create brush model below all other models
        photoEditModel.spriteModels.insert(BrushSpriteModel(), at: 0)
        delegate?.brushController(self, didChange: photoEditModel)
      }
      
      brushEditController.photoEditModel = photoEditModel
    }
    
    setupViews()
  }
  
  // MARK: - Configuration
  
  private func setupViews() {
    guard let brushEditController = brushEditController else { return }
    
    colorSelectionView.delegate = self
    colorSelectionView.selectColor(.white)
    
    brushSizeSlider.translatesAutoresizingMaskIntoConstraints = false
    brushSizeSlider.transform = CGAffineTransform(rotationAngle: 270 / 180 * .pi)
    brushSizeSlider.minimumValue = Float(brushEditController.sliderEditController.slider.minimumValue)
    brushSizeSlider.maximumValue = Float(brushEditController.sliderEditController.slider.maximumValue)
    brushSizeSlider.value = Float(brushEditController.sliderEditController.slider.value)
  }
  
  // MARK: - Undo Management
  
  override func viewDidAppear(_ animated: Bool) {
    super.viewDidAppear(animated)
    updateUndoButton()
    NotificationCenter.default.addObserver(self, selector: #selector(updateUndoButton), name: .UndoControllerDidUndoChange, object: nil)
    NotificationCenter.default.addObserver(self, selector: #selector(updateUndoButton), name: .UndoControllerDidRegisterUndoOperation, object: nil)
  }
  
  override func viewDidDisappear(_ animated: Bool) {
    super.viewDidDisappear(animated)
    NotificationCenter.default.removeObserver(self)
  }
  
  @objc private func updateUndoButton() {
    undoButton?.isEnabled = delegate?.undoController(self)?.canUndoInCurrentGroup ?? false
  }
  
  // MARK: - Actions
  
  @IBAction func undoTapped(_ sender: UIButton) {
    delegate?.undoController(self)?.undoStepInCurrentGroup()
  }
  
  @IBAction func doneTapped(_ sender: UIButton) {
    self.delegate?.brushControllerDidFinish(self)
    dismiss(animated: true, completion: nil)
  }
  
  @IBAction func sizeSliderDidChange(_ sender: UISlider) {
    brushEditController?.size = CGFloat(sender.value) * 36
  }
}

extension BrushViewController: BrushEditControllerDelegate {
  func brushEditControllerPhotoEditModelDidChange(_ brushEditController: BrushEditController) {
    delegate?.brushController(self, didChange: brushEditController.photoEditModel)
  }
  
  func brushEditControllerTargetScrollView(_ brushEditController: BrushEditController) -> UIScrollView? {
    return delegate?.brushControllerTargetScrollView(self)
  }
  
  func brushEditControllerSpriteViewController(_ brushEditController: BrushEditController) -> SpriteViewController? {
    return delegate?.brushControllerSpriteViewController(self)
  }
  
  func brushEditController(_ brushEditController: BrushEditController, didChangePreferredPreviewViewInsetsAnimated animated: Bool) {}
  
  func brushEditController(_ brushEditController: BrushEditController, didUpdateToSize size: CGFloat) {}
  
  func brushEditController(_ brushEditController: BrushEditController, didUpdateToHardness hardness: CGFloat) {}
  
  func brushEditControllerDidStartSliding(_ brushEditController: BrushEditController) {}
  
  func brushEditControllerDidEndSliding(_ brushEditController: BrushEditController) {}
}

extension BrushViewController: ColorSelectionViewDelegate {
  func colorSelectionView(_ view: ColorSelectionView, didSelect color: UIColor) {
    brushEditController?.color = color
  }
}
