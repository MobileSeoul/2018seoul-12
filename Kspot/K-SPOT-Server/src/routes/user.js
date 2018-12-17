const express = require('express')
const user = express.Router()

const userController = require('../controller/userController')
const userCheck = require('../middleware/userCheck')
const upload = require('../lib/s3Bucket.js').getMulter('user');

// kakao signin
user.post('/kakao/signin', userController.kakaoSignin)

// facebook signin
user.post('/facebook/signin', userController.facebookSignin)

// user page
user.get('/mypage',userCheck, userController.getUserPage)

// profile edit
user.post('/edit', userCheck, upload.array('profile_img'), userController.setUserProfile)

// user subscription
user.get('/subscription', userCheck, userController.getUserSubcription)

// user subscription
user.get('/scrap', userCheck, userController.getUserScrap)

// 임시 로그인
user.post('/temp/signin', userController.getTempUser)


module.exports = user;