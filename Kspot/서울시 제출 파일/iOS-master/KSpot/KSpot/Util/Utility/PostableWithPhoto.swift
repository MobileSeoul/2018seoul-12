//
//  PostableWithPhoto.swift
//  KSpot
//
//  Created by 강수진 on 2018. 9. 19..
//  Copyright © 2018년 강수진. All rights reserved.
//

import Foundation
import Alamofire
import SwiftyJSON

protocol PostablewithPhoto {
    
    associatedtype NetworkData : Codable
    typealias networkResult = (resCode : Int, resResult : NetworkData)
    func savePhotoContent(_ URL:String, params : [String : Any], imageData : [String : Data]?, completion : @escaping (Result<networkResult>)->Void)
}

extension PostablewithPhoto {
    
    func gino(_ value : Int?) -> Int {
        return value ?? 0
    }
    
    
    
    func savePhotoContent(_ URL:String, params : [String : Any], imageData : [String : Data]?, completion : @escaping (Result<networkResult>)->Void) {
        
        
        guard let encodedUrl = URL.addingPercentEncoding(withAllowedCharacters: .urlQueryAllowed) else {
            print("networking - invalid url")
            return
        }
        print("url 은 \(encodedUrl)")
        
        let userAuth = UserDefaults.standard.string(forKey: "userAuth") ?? "-1"
        
        var headers: HTTPHeaders?
        
        if userAuth == "-1" {
            headers = [
                "flag" : selectedLang.rawValue.description
            ]
        } else {
            headers = [
                "authorization" : userAuth,
                "flag" : selectedLang.rawValue.description
            ]
        }
      
        
        
        Alamofire.upload(multipartFormData: { multipartFormData in
            
            for (x,y) in params {
                if y is String {
                    multipartFormData.append((y as! String).data(using: .utf8)!, withName: x)
                }
                else if y is Int {
                    multipartFormData.append("\(y)".data(using: .utf8)!, withName: x)
                } else if y is Double {
                    multipartFormData.append("\(y)".data(using: .utf8)!, withName: x)
                }
                print("param key is \(x), value is \(y)")
            }
            
            let calendar = Calendar.current
            let time=calendar.dateComponents([.hour,.minute,.second], from: Date())
            let imgName = "\(time.hour!):\(time.minute!):\(time.second!)"
            
            
            if let images_ = imageData {

                for (x,y) in images_ {
                    multipartFormData.append(y, withName: x, fileName: "\(imgName).jpeg", mimeType: "image/png")
                    
                }
            }
            
        }, to: encodedUrl, method: .post, headers: headers, encodingCompletion:{
            (encodingResult) in
            switch encodingResult {
            case .success(let upload,_,_):
                upload.responseData(completionHandler: { (res) in
                    
                    switch res.result{
                        
                    case .success:
                        if let value = res.result.value {
                            print(res.response?.statusCode ?? 00)
                            print(JSON(value))
                            let decoder = JSONDecoder()
                            do {
                                
                                let resCode = self.gino(res.response?.statusCode)
                                let data = try decoder.decode(NetworkData.self, from: value)
                                
                                let result : networkResult = (resCode, data)
                                completion(.success(result))
                                
                                
                            }catch{
                                completion(.error("error"))
                            }
                            
                            
                        }
                    case .failure(let err):
                        print(err.localizedDescription)
                        
                        break
                    }
                })
                break
            case .failure(let err):
                print(err.localizedDescription)
                break
            }
        })
    }
}

