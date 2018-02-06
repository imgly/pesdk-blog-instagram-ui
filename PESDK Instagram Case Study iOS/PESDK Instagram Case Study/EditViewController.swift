//
//  ViewController.swift
//  PESDK Instagram Case Study
//
//  Created by Malte Baumann on 17.10.17.
//  Copyright © 2017 9elements GmbH. All rights reserved.
//

import UIKit
import PhotoEditorSDK

// Used to keep track of currently used `SpriteEditController` implementation
enum SpriteType {
  case text
  case sticker
}

class EditViewController: UIViewController {
  
  @IBOutlet var previewContainerView: UIView!
  @IBOutlet weak var nextButton: UIButton!
  @IBOutlet var controls: [UIButton]!
  
  // A contained viewcontroller, responsible for rendering the image with all
  // edits applied.
  var previewViewController: PhotoEditPreviewController?
  
  // SpriteEditControllers for text and stickers, that render UI for the selected
  // sprites and handle pan and pinch gestures, when transforming the sprites
  // position and size.
  var textSpriteEditViewController: SpriteEditController<TextSpriteModel, TextGestureController, TextSelectionView>?
  var stickerSpriteEditViewController: SpriteEditController<StickerSpriteModel, StickerGestureController, StickerSelectionView>?
  
  // The asset manager responsible for downloading and caching assets, thats
  // used throughout the app
  let assetManager: AssetManager = AssetManager()
  
  // Temporary helpers
  private var progressActivityIndicatorView: UIActivityIndicatorView?
  private var gestureInProgress = false
  
  
  var currentSpriteType: SpriteType = .text {
    didSet {
      stickerSpriteEditViewController?.view.isUserInteractionEnabled = currentSpriteType == .sticker
      textSpriteEditViewController?.view.isUserInteractionEnabled = currentSpriteType == .text
      if currentSpriteType == .sticker {
        textSpriteEditViewController?.spriteView = nil
      } else {
        stickerSpriteEditViewController?.spriteView = nil
      }
    }
  }
  
  // MARK: - UIViewController
  
  override func viewDidLoad() {
    super.viewDidLoad()
    
    let previewViewController = PhotoEditPreviewController(photoAsset: Photo(image: UIImage(named: "sample")!))
    previewViewController.allowsPreviewImageZoom = true
    previewViewController.spriteViewController.assetManager = assetManager
    previewViewController.assetManager = assetManager
    previewViewController.undoController = UndoController()
    previewViewController.delegate = self
    addChildViewController(previewViewController)
    previewContainerView.addSubview(previewViewController.view)
    previewViewController.didMove(toParentViewController: self)
    self.previewViewController = previewViewController
    
    // In order to support two sprite types in a single view, we need to stack two
    // specific `SpriteEditController` implementations on top of each other. The
    // correct controller will be enabled depending on the selected sprite.
    let textSpriteEditViewController = SpriteEditController<TextSpriteModel, TextGestureController, TextSelectionView>()
    textSpriteEditViewController.delegate = self
    addChildViewController(textSpriteEditViewController)
    previewContainerView.addSubview(textSpriteEditViewController.view)
    textSpriteEditViewController.didMove(toParentViewController: self)
    self.textSpriteEditViewController = textSpriteEditViewController
    let stickerSpriteEditViewController = SpriteEditController<StickerSpriteModel, StickerGestureController, StickerSelectionView>()
    stickerSpriteEditViewController.delegate = self
    addChildViewController(stickerSpriteEditViewController)
    previewContainerView.addSubview(stickerSpriteEditViewController.view)
    stickerSpriteEditViewController.didMove(toParentViewController: self)
    self.stickerSpriteEditViewController = stickerSpriteEditViewController
  }
  
  override var prefersStatusBarHidden: Bool {
    get {
      return true
    }
  }

  // MARK: - Controls
  
  @IBAction func nextTapped(_ sender: UIButton) {
    presentShareSheet()
  }
  
  @IBAction func stickerTapped(_ sender: UIButton) {
    presentStickerTool()
  }
  
  @IBAction func brushTapped(_ sender: UIButton) {
    presentBrushTool()
  }
  
  @IBAction func textTapped(_ sender: UIButton) {
    presentTextTool()
  }
  
  // MARK: - Tools
  
  func presentTextTool(existingModel: TextSpriteModel? = nil) {
    if let textViewController = storyboard?.instantiateViewController(withIdentifier: "textViewController") as? TextViewController {
      textViewController.modalPresentationStyle = .overCurrentContext
      textViewController.modalTransitionStyle = .crossDissolve
      textViewController.delegate = self
    
      self.present(textViewController, animated: true, completion: {
        if let existingModel = existingModel {
          textViewController.textSpriteModel = existingModel
        }
      })
      
      hideControls(deselectSprites: existingModel == nil, completion: { _ in
        if existingModel != nil {
          UIView.animate(withDuration: 0.1, animations: {
            self.textSpriteEditViewController?.spriteView?.alpha = 0
          }, completion: { _ in
            self.textSpriteEditViewController?.spriteView = nil
          })
        }
      })
    }
  }
  
  func presentBrushTool() {
    if let brushViewController = storyboard?.instantiateViewController(withIdentifier: "brushViewController") as? BrushViewController {
      previewViewController?.undoController?.beginUndoGrouping()
      
      brushViewController.modalPresentationStyle = .overCurrentContext
      brushViewController.modalTransitionStyle = .crossDissolve
      brushViewController.delegate = self
      present(brushViewController, animated: true, completion: nil)
    }
    hideControls(completion: nil)
  }
  
  func presentStickerTool() {
    if let stickerViewController = storyboard?.instantiateViewController(withIdentifier: "stickerViewController") as? StickerViewController {
      stickerViewController.delegate = self
      stickerViewController.assetManager = assetManager
      stickerViewController.modalPresentationStyle = .overCurrentContext
      present(stickerViewController, animated: true, completion: nil)
    }
    hideControls(completion: nil)
  }
  
  func presentShareSheet() {
    guard let photoEditModel = previewViewController?.photoEditModel, let originalImage = UIImage(named: "sample") else {
      return
    }
    
    showProgressIndicator()
    
    // To render the brush into the final output, we need to render a `CIImage`
    // that contains all brush strokes and add it to the shared asset manager.
    // During export this asset will be added to the result.
    if let brushSpriteModel = photoEditModel.spriteModels.first(where: { $0 is BrushSpriteModel }),
      let canvasView = previewViewController?.spriteViewController.spriteView(with: brushSpriteModel.uuid) as? CanvasView,
      var image = canvasView.painting.ciImage(with: canvasView.painting.dimensions, backgroundColor: .clear) {
      image = image.transformed(by: CGAffineTransform(scaleX: 1, y: -1))
      image = image.transformed(by: CGAffineTransform(translationX: -image.extent.origin.x, y: -image.extent.origin.y))
      assetManager.setCIImage(image, forIdentifier: brushSpriteModel.uuid.uuidString)
    }
    
    let photoEditRenderer = PhotoEditRenderer()
    photoEditRenderer.photoEditModel = photoEditModel
    photoEditRenderer.originalImage = CIImage(image: originalImage)
    photoEditRenderer.assetManager = assetManager
    photoEditRenderer.generateOutputImageData(withFormat: .jpeg, compressionQuality: 8, metadataSourcePhoto: nil, completionHandler: { (data, _, _) in
      if let imageData = data, let image = UIImage(data: imageData) {
        let activityViewController = UIActivityViewController(activityItems: [image], applicationActivities: nil)
        activityViewController.completionWithItemsHandler = { _, _, _, activityError in
          if let error = activityError {
            print(error.localizedDescription)
          }
        }
        
        self.present(activityViewController, animated: true)
      }
      
      DispatchQueue.main.async {
        self.hideProgressIndicator()
      }
    })
  }
  
  // MARK: - Animations
  
  private func hideControls(deselectSprites: Bool = true, completion: ((Bool) -> Void)?) {
    // Ensure that any currently selected sprite gets deselected
    if deselectSprites {
      textSpriteEditViewController?.spriteView = nil
      stickerSpriteEditViewController?.spriteView = nil
    }

    UIView.animate(withDuration: 0.2, animations: {
      self.controls.forEach { $0.alpha = 0 }
    }, completion: { complete in
      self.controls.forEach { $0.isEnabled = false }
      completion?(complete)
    })
  }
  
  private func showControls(completion: ((Bool) -> Void)?) {
    UIView.animate(withDuration: 0.2, animations: {
      self.controls.forEach {
        $0.alpha = 1
        $0.isEnabled = true
      }
    }, completion: completion)
  }
  
  private func showProgressIndicator() {
    let activityIndicatorView = UIActivityIndicatorView(activityIndicatorStyle: .gray)
    activityIndicatorView.frame = nextButton.frame
    activityIndicatorView.alpha = 0
    activityIndicatorView.startAnimating()
    view.insertSubview(activityIndicatorView, aboveSubview: nextButton)
    progressActivityIndicatorView = activityIndicatorView
    UIView.animate(withDuration: 0.2, animations: {
      self.nextButton.setTitle("", for: .normal)
      self.nextButton.isEnabled = false
      activityIndicatorView.alpha = 1.0
      self.controls.forEach {
        if $0 != self.nextButton {
          $0.alpha = 0
        }
      }
    })
  }
  
  private func hideProgressIndicator() {
    if let activityIndicatorView = progressActivityIndicatorView {
      UIView.animate(withDuration: 0.2, animations: {
        activityIndicatorView.alpha = 0
        self.nextButton.setTitle("NEXT", for: .normal)
        self.controls.forEach {
          if $0 != self.nextButton {
            $0.alpha = 1
          }
        }
      }, completion: { _ in
        activityIndicatorView.removeFromSuperview()
        self.progressActivityIndicatorView = nil
        self.nextButton.isEnabled = true
      })
    }
  }
}

// MARK: - PhotoEditor SDK Delegates

extension EditViewController: PhotoEditPreviewControllerDelegate {
  func photoEditPreviewControllerPreviewEnabled(_ photoEditPreviewController: PhotoEditPreviewController) -> Bool {
    return true
  }
  
  func photoEditPreviewControllerRenderMode(_ photoEditPreviewController: PhotoEditPreviewController) -> PESDKRenderMode {
    return .all
  }
  
  func photoEditPreviewControllerBackgroundColor(_ photoEditPreviewController: PhotoEditPreviewController) -> UIColor {
    return .black
  }
  
  func photoEditPreviewControllerPreviewInsets(_ photoEditPreviewController: PhotoEditPreviewController) -> UIEdgeInsets {
    let viewSize = view.bounds.size
    let imageSize = UIImage(named: "sample")!.size
    let imageAspectRatio = imageSize.height / imageSize.width
    let imageHeightInView = viewSize.width * imageAspectRatio
    let yDifference = viewSize.height - imageHeightInView

    return UIEdgeInsets(top: 0, left: 0, bottom: yDifference, right: 0)
  }
  
  func photoEditPreviewControllerPreviewScale(_ photoEditPreviewController: PhotoEditPreviewController) -> CGFloat {
    return 1.0
  }
  
  func photoEditPreviewControllerProxyZoomingActive(_ photoEditPreviewController: PhotoEditPreviewController) -> Bool {
    return true
  }
  
  func photoEditPreviewControllerResetProxyZooming(_ photoEditPreviewController: PhotoEditPreviewController) {}
  
  func photoEditPreviewControllerDidChangePhotoEditModel(_ photoEditPreviewController: PhotoEditPreviewController) {}
}

extension EditViewController: SpriteEditControllerDelegate {
  
  // MARK: - Wiring of SpriteEditController and PreviewController
  
  func spriteEditControllerSpriteViewController<SpriteModelType, GestureController, SpriteSelectionView>(_ spriteEditController: SpriteEditController<SpriteModelType, GestureController, SpriteSelectionView>) -> SpriteViewController? {
    return self.previewViewController?.spriteViewController
  }
  
  func spriteEditControllerPreviewView<SpriteModelType, GestureController, SpriteSelectionView>(_ spriteEditController: SpriteEditController<SpriteModelType, GestureController, SpriteSelectionView>) -> UIView? {
    return self.previewViewController?.previewView
  }
  
  func spriteEditControllerTargetScrollView<SpriteModelType, GestureController, SpriteSelectionView>(_ spriteEditController: SpriteEditController<SpriteModelType, GestureController, SpriteSelectionView>) -> UIScrollView? {
    return self.previewViewController?.previewViewScrollingContainer
  }
  
  // MARK: - Handling updates from SpriteEditController
  
  func spriteEditControllerDidChangePhotoEditModel<SpriteModelType, GestureController, SpriteSelectionView>(_ spriteEditController: SpriteEditController<SpriteModelType, GestureController, SpriteSelectionView>) {
    previewViewController?.photoEditModel = spriteEditController.photoEditModel
  }
  
  func spriteEditController<SpriteModelType, GestureController, SpriteSelectionView>(_ spriteEditController: SpriteEditController<SpriteModelType, GestureController, SpriteSelectionView>, didTapUsing gestureRecognizer: UITapGestureRecognizer) {
    guard let photoEditModel = previewViewController?.photoEditModel else { return }

    // If no spriteView was tapped, we just deselect any previously selected view
    guard let tappedSpriteView = previewViewController?.spriteViewController.spriteView(at: gestureRecognizer.location(in: previewViewController?.previewView)) else {
      textSpriteEditViewController?.spriteView = nil
      stickerSpriteEditViewController?.spriteView = nil
      return
    }
    
    // If a spriteView was tapped, we need to determine it's type and deal
    // with the selection accordingly. For text sprites, this means either selecting
    // or opening the text tool for the connected model. When selecting a
    // text sprite, any sticker sprite must be deselected and vice versa.
    if let (_, spriteModel) = photoEditModel.spriteModel(with: tappedSpriteView.uuid) {
      switch spriteModel {
      case let textSpriteModel as TextSpriteModel:
        currentSpriteType = .text
        textSpriteEditViewController?.photoEditModel = photoEditModel
        if textSpriteEditViewController?.spriteView == nil {
          textSpriteEditViewController?.spriteView = tappedSpriteView
          bringSpriteModelToFront(uuid: tappedSpriteView.uuid)
        } else if let existingSpriteView = textSpriteEditViewController?.spriteView, existingSpriteView as UIView != tappedSpriteView {
          textSpriteEditViewController?.spriteView = tappedSpriteView
          bringSpriteModelToFront(uuid: tappedSpriteView.uuid)
        } else {
          presentTextTool(existingModel: textSpriteModel)
        }
      case is StickerSpriteModel:
        currentSpriteType = .sticker
        stickerSpriteEditViewController?.photoEditModel = photoEditModel
        if stickerSpriteEditViewController?.spriteView == nil {
          stickerSpriteEditViewController?.spriteView = tappedSpriteView
          bringSpriteModelToFront(uuid: tappedSpriteView.uuid)
        } else if let existingSpriteView = stickerSpriteEditViewController?.spriteView, existingSpriteView as UIView != tappedSpriteView {
          stickerSpriteEditViewController?.spriteView = tappedSpriteView
          bringSpriteModelToFront(uuid: tappedSpriteView.uuid)
        }
      default:
        break
      }
    }
  }
  
  private func bringSpriteModelToFront(uuid: UUID?) {
    if let uuid = uuid, let (index, _) = previewViewController?.photoEditModel.spriteModel(with: uuid) {
      guard var spriteModels = previewViewController?.photoEditModel.spriteModels, var photoEditModel = previewViewController?.photoEditModel else { return }
      spriteModels.swapAt(index, spriteModels.count - 1)
      photoEditModel.spriteModels = spriteModels
      stickerSpriteEditViewController?.photoEditModel = photoEditModel
      textSpriteEditViewController?.photoEditModel = photoEditModel
    }
  }
  
  func spriteEditControllerDidBeginGesture<SpriteModelType, GestureController, SpriteSelectionView>(_ spriteEditController: SpriteEditController<SpriteModelType, GestureController, SpriteSelectionView>) {
    gestureInProgress = true
    guard presentedViewController == nil else { return }
    if (currentSpriteType == .text && textSpriteEditViewController?.spriteView != nil) ||
      (currentSpriteType == .sticker && stickerSpriteEditViewController?.spriteView != nil) {
      hideControls(deselectSprites: false, completion: nil)
    }
  }
  
  func spriteEditControllerDidEndGesture<SpriteModelType, GestureController, SpriteSelectionView>(_ spriteEditController: SpriteEditController<SpriteModelType, GestureController, SpriteSelectionView>) {
    if presentedViewController == nil, ((currentSpriteType == .text && textSpriteEditViewController?.spriteView != nil) ||
      (currentSpriteType == .sticker && stickerSpriteEditViewController?.spriteView != nil)), gestureInProgress {
      showControls(completion: nil)
    }
    
    gestureInProgress = false
  }
}

// MARK: - Tool Delegates

extension EditViewController: TextViewControllerDelegate {
  func referenceSizeFor(_ textViewController: TextViewController) -> CGSize? {
    return previewViewController?.spriteViewController.referenceSize
  }
  
  func imageToViewScaleFactorFor(_ textViewController: TextViewController) -> CGFloat? {
    return previewViewController?.spriteViewController.spriteContainerView.imageToViewScaleFactor
  }
  
  func controller(_ textViewController: TextViewController, willFinishWith model: TextSpriteModel?) {
    // Depending on whether the returned `TextSpriteModel` had already been part
    // of the image, we update the existing model and blend the spriteView back
    // in, or we just append it.
    if let model = model, let previewController = previewViewController {
      if let (index, _) = previewController.photoEditModel.spriteModel(with: model.uuid) {
        previewController.photoEditModel.spriteModels[index] = model
        if let spriteViewForCurrentModel = previewController.spriteViewController.spriteView(with: model.uuid) {
          UIView.animate(withDuration: 0.1, animations: {
            spriteViewForCurrentModel.alpha = 1
          })
        }
      } else {
        previewController.photoEditModel.spriteModels.append(model)
      }
    }
    
    if let updatedModel = previewViewController?.photoEditModel {
      textSpriteEditViewController?.photoEditModel = updatedModel
    }
    
    showControls(completion: nil)
  }
}

extension EditViewController: BrushViewControllerDelegate {
  func undoController(_ controller: BrushViewController) -> UndoController? {
    return previewViewController?.undoController
  }
  
  func brushControllerPhotoEditModel(_ controller: BrushViewController) -> PhotoEditModel? {
    return previewViewController?.photoEditModel
  }
  
  func brushController(_ controller: BrushViewController, didChange model: PhotoEditModel) {
    previewViewController?.photoEditModel = model
  }
  
  func brushControllerTargetScrollView(_ controller: BrushViewController) -> UIScrollView? {
    return previewViewController?.previewViewScrollingContainer
  }
  
  func brushControllerSpriteViewController(_ controller: BrushViewController) -> SpriteViewController? {
    return previewViewController?.spriteViewController
  }
  
  func brushControllerDidFinish(_ controller: BrushViewController) {
    showControls(completion: nil)
    previewViewController?.undoController?.endUndoGrouping()
  }
}

extension EditViewController: StickerViewControllerDelegate {
  func stickerViewControllerReferenceSize(_ controller: StickerViewController) -> CGSize? {
    return previewViewController?.spriteViewController.referenceSize
  }
  
  func stickerViewControllerDidFinish(_ controller: StickerViewController, stickerModel: StickerSpriteModel?) {
    if let model = stickerModel {
      previewViewController?.photoEditModel.spriteModels.append(model)
      stickerSpriteEditViewController?.photoEditModel.spriteModels.append(model)
    }
    
    showControls(completion: nil)
  }
}
