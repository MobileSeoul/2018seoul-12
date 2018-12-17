//
//  Gallery.swift
//  KSpot
//
//  Created by 강수진 on 2018. 9. 15..
//  Copyright © 2018년 강수진. All rights reserved.
//

import UIKit

protocol Gallery : UIImagePickerControllerDelegate, UINavigationControllerDelegate{
    var homeController : UIViewController? {get set}
    func openGalleryCamera()
}

extension Gallery   {
    func openGalleryCamera(){
        let selectAlert = UIAlertController(title: nil, message: nil, preferredStyle: .actionSheet)
        let libraryAction = UIAlertAction(title: "앨범", style: .default) { _ in if UIImagePickerController.isSourceTypeAvailable(UIImagePickerControllerSourceType.photoLibrary){
            let imagePicker = UIImagePickerController()
            imagePicker.delegate = self
            imagePicker.sourceType = UIImagePickerControllerSourceType.photoLibrary
            imagePicker.allowsEditing = true
            self.homeController?.present(imagePicker, animated: true, completion: nil)
            }
        }
        
        let cameraAction = UIAlertAction(title: "카메라", style: .default) {
            _ in  if UIImagePickerController.isSourceTypeAvailable(UIImagePickerControllerSourceType.camera){
                let imagePicker = UIImagePickerController()
                imagePicker.delegate = self
                imagePicker.sourceType = UIImagePickerControllerSourceType.camera
                imagePicker.allowsEditing = true
                self.homeController?.present(imagePicker, animated: true, completion: nil)
            }
        }
        let cancelAction = UIAlertAction(title: "취소", style: .cancel)
        selectAlert.addAction(libraryAction)
        selectAlert.addAction(cameraAction)
        selectAlert.addAction(cancelAction)
        self.homeController?.present(selectAlert, animated: true, completion: nil)
    }
}
