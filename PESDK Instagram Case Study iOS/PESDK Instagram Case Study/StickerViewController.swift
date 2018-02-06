//
//  StickerViewController.swift
//  PESDK Instagram Case Study
//
//  Created by Malte Baumann on 23.10.17.
//  Copyright © 2017 9elements GmbH. All rights reserved.
//

import UIKit
import PhotoEditorSDK

protocol StickerViewControllerDelegate: class {
  func stickerViewControllerReferenceSize(_ controller: StickerViewController) -> CGSize?
  func stickerViewControllerDidFinish(_ controller: StickerViewController, stickerModel: StickerSpriteModel?)
}

class StickerViewController: UIViewController {
  
  @IBOutlet weak var topSpacingView: UIView!
  @IBOutlet weak var stickerSelectionControllerContainer: UIView!
  
  weak var delegate: StickerViewControllerDelegate?
  var assetManager: AssetManager?
  var stickerSelectionController: StickerSelectionController?
  
  override func viewDidLoad() {
    super.viewDidLoad()
    let stickerSelectionController = StickerSelectionController()
    stickerSelectionController.delegate = self
    stickerSelectionController.assetManager = assetManager
    stickerSelectionController.stickers = StickerCategory.all.first?.stickers ?? []
    stickerSelectionController.collectionView.scrollIndicatorInsets = UIEdgeInsets(top: 8, left: 0, bottom: 0, right: 0)
    stickerSelectionController.assetManager = assetManager
    addChildViewController(stickerSelectionController)
    stickerSelectionController.view.frame = stickerSelectionControllerContainer.bounds
    stickerSelectionControllerContainer.addSubview(stickerSelectionController.view)
    self.stickerSelectionController = stickerSelectionController
    
    let tapGestureRecognizer = UITapGestureRecognizer(target: self, action: #selector(didTapView))
    topSpacingView.addGestureRecognizer(tapGestureRecognizer)
  }
  
  // MARK: - Actions
  
  @objc private func didTapView() {
    delegate?.stickerViewControllerDidFinish(self, stickerModel: nil)
    dismiss(animated: true, completion: nil)
  }
}

extension StickerViewController: StickerSelectionControllerDelegate {
func stickerSelectionController(_ stickerSelectionController: StickerSelectionController, didSelect sticker: Sticker, with image: UIImage) {
  guard let referenceSize = delegate?.stickerViewControllerReferenceSize(self) else { return }
  
  var model = StickerSpriteModel(sticker: sticker)
  let aspectRatio = referenceSize.width / referenceSize.height
  model.normalizedCenter = CGPoint(x: 0.5, y: 0.5)
  model.normalizedSize = CGSize(width: 0.3, height: 0.3 * aspectRatio)
  delegate?.stickerViewControllerDidFinish(self, stickerModel: model)
  dismiss(animated: true, completion: nil)
}
}
