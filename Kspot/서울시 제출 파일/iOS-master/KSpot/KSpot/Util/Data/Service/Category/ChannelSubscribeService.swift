//
//  ChannelSubscribeService.swift
//  KSpot
//
//  Created by 강수진 on 2018. 9. 19..
//  Copyright © 2018년 강수진. All rights reserved.
//

import Foundation

struct ChannelSubscribeService: PostableService, GettableService {
    typealias NetworkData = DefaultVO

    static let shareInstance = ChannelSubscribeService()
    func subscribe(url : String, params : [String : Any], completion : @escaping (NetworkResult<Any>) -> Void){
        post(url, params: params) { (result) in
            switch result {
            case .success(let networkResult):
                switch networkResult.resCode{
                case HttpResponseCode.POST_SUCCESS.rawValue :
                    completion(.networkSuccess(networkResult.resResult.data))
                case HttpResponseCode.UID_ERROR.rawValue :
                    completion(.UIDErr)
                case HttpResponseCode.SERVER_ERROR.rawValue :
                    completion(.serverErr)
                default :
                    print("no 201/204/500 rescode is \(networkResult.resCode)")
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
    
    func unsubscribe(url : String, completion : @escaping (NetworkResult<Any>) -> Void){
        
        get(url, method: .delete) { (result) in
            switch result {
            case .success(let networkResult):
                switch networkResult.resCode{
                case HttpResponseCode.GET_SUCCESS.rawValue :
                    completion(.networkSuccess(networkResult.resResult.data))
                case HttpResponseCode.UID_ERROR.rawValue :
                    completion(.UIDErr)
                case HttpResponseCode.SERVER_ERROR.rawValue :
                    completion(.serverErr)
                default :
                     print("no 200/204/500 rescode is \(networkResult.resCode)")
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
