//
//  ColorSelectionView.swift
//  PESDK Instagram Case Study
//
//  Created by Malte Baumann on 20.10.17.
//  Copyright © 2017 9elements GmbH. All rights reserved.
//

import UIKit
import PhotoEditorSDK

protocol ColorSelectionViewDelegate: class {
  func colorSelectionView(_ view: ColorSelectionView, didSelect color: UIColor)
}

class ColorSelectionView: UIView {
  
  weak var delegate: ColorSelectionViewDelegate?
  var collectionView: MenuCollectionView?

  override init(frame: CGRect) {
    super.init(frame: frame)
    commonInit()
  }
  
  required init?(coder aDecoder: NSCoder) {
    super.init(coder: aDecoder)
    commonInit()
  }
  
  func commonInit() {
    let colorSelectionMenu = MenuCollectionView()
    colorSelectionMenu.translatesAutoresizingMaskIntoConstraints = false
    colorSelectionMenu.dataSource = self
    colorSelectionMenu.delegate = self
    colorSelectionMenu.flowLayout.itemSize = CGSize(width: 30, height: 30)
    colorSelectionMenu.flowLayout.minimumInteritemSpacing = 8
    colorSelectionMenu.flowLayout.minimumLineSpacing = 8
    colorSelectionMenu.flowLayout.sectionInset = UIEdgeInsets(top: 0, left: 10, bottom: 0, right: 10)
    colorSelectionMenu.register(ColorCollectionViewCell.self, forCellWithReuseIdentifier: "ColorCell")
    self.addSubview(colorSelectionMenu)
    colorSelectionMenu.leftAnchor.constraint(equalTo: leftAnchor).isActive = true
    colorSelectionMenu.topAnchor.constraint(equalTo: topAnchor).isActive = true
    colorSelectionMenu.rightAnchor.constraint(equalTo: rightAnchor).isActive = true
    colorSelectionMenu.bottomAnchor.constraint(equalTo: bottomAnchor).isActive = true
    collectionView = colorSelectionMenu
  }
  
  func selectColor(_ color: UIColor) {
    if let index = ColorSelectionView.availableColors.index(of: color) {
      collectionView?.selectItem(at: IndexPath(item: index, section: 0), animated: true, scrollPosition: .centeredHorizontally)
    }
  }
}

extension ColorSelectionView: UICollectionViewDataSource {
  static let availableColors: [UIColor] = {
    var defaultColors = ColorToolControllerOptionsBuilder().availableColors
    defaultColors.removeFirst()
    
    let finalColors: [UIColor] = defaultColors.compactMap { (color) -> UIColor? in
        return color.color
    }
    return finalColors
  }()
  
  func numberOfSections(in collectionView: UICollectionView) -> Int {
    return 1
  }
  
  func collectionView(_ collectionView: UICollectionView, numberOfItemsInSection section: Int) -> Int {
    return ColorSelectionView.availableColors.count
  }
  
  func collectionView(_ collectionView: UICollectionView, cellForItemAt indexPath: IndexPath) -> UICollectionViewCell {
    let cell = collectionView.dequeueReusableCell(withReuseIdentifier: "ColorCell", for: indexPath)
    
    if let cell = cell as? ColorCollectionViewCell {
      cell.colorView.backgroundColor = ColorSelectionView.availableColors[indexPath.row]
      cell.colorView.layer.cornerRadius = 15
      cell.colorView.layer.borderColor = UIColor.white.cgColor
      cell.colorView.layer.borderWidth = 2
      cell.colorView.layer.masksToBounds = true
    }
    
    return cell
  }
}

extension ColorSelectionView: UICollectionViewDelegate {
  func collectionView(_ collectionView: UICollectionView, didSelectItemAt indexPath: IndexPath) {
    let selectedColor = ColorSelectionView.availableColors[indexPath.item]
    delegate?.colorSelectionView(self, didSelect: selectedColor)
  }
}
