//
//  UserEditService.swift
//  KSpot
//
//  Created by 강수진 on 2018. 9. 19..
//  Copyright © 2018년 강수진. All rights reserved.
//

import Foundation
import Alamofire
import SwiftyJSON

struct UserEditService : PostablewithPhoto {
    
    typealias NetworkData = DefaultVO
    static let shareInstance = UserEditService()
    func editProfile(url : String, params : [String : Any], image : [String : Data]?, completion : @escaping (NetworkResult<Any>) -> Void){
        
        savePhotoContent(url, params: params, imageData: image) { (result) in
            switch result {
            case .success(let networkResult):
                switch networkResult.resCode {
                case HttpResponseCode.POST_SUCCESS.rawValue :
                    completion(.networkSuccess(""))
                case HttpResponseCode.UID_ERROR.rawValue :
                    completion(.UIDErr)
                case HttpResponseCode.CONFILCT.rawValue :
                    completion(.duplicated)
                default :
                    print("no 201/204/409 - statusCode is \(networkResult.resCode)")
                    break
                }
                break
            case .error(let errMsg) :
                print(errMsg)
                break
            case .failure(_) :
                completion(.networkFail)
            }
        }
        
        
    }
    
}
