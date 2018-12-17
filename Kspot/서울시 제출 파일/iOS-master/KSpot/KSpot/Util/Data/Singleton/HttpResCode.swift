//
//  HttpResCode.swift
//  KSpot
//
//  Created by 강수진 on 2018. 9. 19..
//  Copyright © 2018년 강수진. All rights reserved.
//

import Foundation


enum HttpResponseCode: Int{
    case GET_SUCCESS = 200
    case POST_SUCCESS = 201
    case UID_ERROR = 204
    case SELECT_ERROR = 300
    case ACCESS_DENIED = 401
    case FORBIDDEN = 403
    case CONFILCT = 409
    case SERVER_ERROR = 500
}


enum Result<T> {
    case success(T)
    case error(String)
    case failure(Error)
}

enum NetworkResult<T> {
    case networkSuccess(T)
    case UIDErr
    case selectErr
    case serverErr
    case accessDenied
    ///
    case nullValue
    case duplicated
    case networkFail
    case wrongInput
}
