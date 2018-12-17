const dao = require('../dao/userDao')
const request = require('request-promise');
const jwt = require('../lib/jwt')
const s3Location = require('../../config/s3Location').location

module.exports = {
    kakaoSignin: async (jwtToken, accessToken) => {
        // kakao user info request variable
        let option = {
            method: 'GET',
            uri: 'https://kapi.kakao.com/v2/user/me',
            json: true,
            headers: {
                'Authorization': "Bearer " + accessToken
            }
        }

        let kakaoUserInfo = await request(option);

        // jwt token user info existence
        if (jwtToken != undefined) {
            // already signin state

            if (jwt.verify(jwtToken).id == kakaoUserInfo.id) {
                console.log("Success Signin")

                return {
                    id: kakaoUserInfo.id,
                    authorization: jwtToken
                }
            }
            else {
                console.log("token expired, generate new token")
                const newToken = jwt.sign(kakaoUserInfo.id)

                return {
                    id: kakaoUserInfo.id,
                    authorization: newToken
                }
            }
        }
        else { // token NO existence
            // DB user info
            const dbUserInfo = await dao.selectUser(kakaoUserInfo.id)

            if (dbUserInfo.length != 0) {
                console.log("other device login")

                const newToken = jwt.sign(kakaoUserInfo.id)

                return {
                    id: kakaoUserInfo.id,
                    authorization: newToken
                }
            }
            else {
                // insertUser
                console.log("new user")

                let profile_img
                // http -> https
                if(kakaoUserInfo.properties.hasOwnProperty('thumbnail_image')){
                    profile_img = 'https' + kakaoUserInfo.properties.thumbnail_image.split('http')[1]
                }
                else{
                    profile_img = s3Location + '/user/2018/09/26/default_img.png'
                }

                await dao.insertUser(kakaoUserInfo.id, kakaoUserInfo.properties.nickname, profile_img)

                const newToken = jwt.sign(kakaoUserInfo.id)

                return {
                    id: kakaoUserInfo.id,
                    authorization: newToken
                }
            }
        }
    },
    facebookSignin: async (jwtToken, accessToken) => {
        let option = {
            method: 'GET',
            uri: 'https://graph.facebook.com/me?fields=id,name,picture&access_token=' + accessToken,
            json: true
        }

        const facebookUserInfo = await request(option);

        console.log(facebookUserInfo)
        //console.log(facebookUserInfo.id + " " + facebookUserInfo.name + " " + facebookProfileImg)

        // jwt token user info existence
        if (jwtToken != undefined) {
            // already signin state
            if (jwt.verify(jwtToken).id == facebookUserInfo.id) {
                console.log("Success Signin")

                return {
                    id: facebookUserInfo.id,
                    authorization: jwtToken
                }
            }
            else {
                console.log("token expired, generate new token")
                const newToken = jwt.sign(facebookUserInfo.id)

                return {
                    id: facebookUserInfo.id,
                    authorization: newToken
                }
            }
        }
        else { // token NO existence
            // DB user info
            const dbUserInfo = await dao.selectUser(facebookUserInfo.id)

            if (dbUserInfo.length != 0) {
                console.log("other device login")

                const newToken = jwt.sign(facebookUserInfo.id)

                return {
                    id: facebookUserInfo.id,
                    authorization: newToken
                }
            }
            else {
                // insertUser
                console.log("new user")
                const facebookProfileImg = "https://graph.facebook.com/" + facebookUserInfo.id + "/picture?type=large&width=720&height=720"
                //console.log(facebookProfileImg)

                await dao.insertUser(facebookUserInfo.id, facebookUserInfo.name, facebookProfileImg)
                
                const newToken = jwt.sign(facebookUserInfo.id)

                return {
                    id: facebookUserInfo.id,
                    authorization: newToken
                }
            }
        }
    },
    getUserPage: async (userId) => {
        // User info
        const userInfo = await dao.selectUser(userId)

        // Subscription info ( 5 : number of data rows )
        let subscriptionInfo = await dao.selectSubscriptionPreview(userId, 5)

        for(var i in subscriptionInfo){
            subscriptionInfo[i].background_img = subscriptionInfo[i].background_img[i]? s3Location + subscriptionInfo[i].background_img : ''
        }

        return {
            user: {
                name: userInfo[0].name,
                profile_img: userInfo[0].profile_img
            },
            channel: subscriptionInfo
        }
    },
    setUserProfile: async (user, name, files) => {
        // User info
        const userInfo = await dao.selectUser(user.id)
        let newName = name
        let newProfileImg

        const userCheck = await dao.selectUserName(name, user.id)
        if(userCheck.length != 0){
            throw Error("Duplicate ID")
        }

        if (newName == undefined || newName == "") {
            newName = userInfo[0].name
        }

        if (files == undefined || files.length == 0) {
            newProfileImg = userInfo[0].profile_img
        }
        else {
            newProfileImg = files[0].location
        }
        
        await dao.updateUser(user.id, newName, newProfileImg)
    },
    getUserSubcription: async (userId, flag) => {
        let celebrity
        let broadcast

        if (flag == 0) {
            celebrity = await dao.selectSubscriptionCelebrityKor(userId)
            broadcast = await dao.selectSubscriptionBroadcastKor(userId)
        }
        else if (flag == 1) {
            celebrity = await dao.selectSubscriptionCelebrityEng(userId)
            broadcast = await dao.selectSubscriptionBroadcastEng(userId)
        }

        for(var i in celebrity){
            celebrity[i].thumbnail_img = celebrity[i].thumbnail_img? s3Location + celebrity[i].thumbnail_img : ''
        }
        for(var i in broadcast){
            broadcast[i].thumbnail_img = broadcast[i].thumbnail_img? s3Location + broadcast[i].thumbnail_img : ''
        }

        return {
            celebrity,
            broadcast
        }
    },
    getUserScrap: async (userId, flag) => {
        let result

        if (flag == 0) {
            result = await dao.selectScrapKor(userId)
        }
        else if (flag == 1) {
            result = await dao.selectScrapEng(userId)
        }

        // group_concat을 통해 string으로 합쳐 받은 channel id 분리
        for (var i in result) {
            result[i].img = result[i].img ? s3Location + result[i].img : ''

            let tempImg = result[i].thumbnail_img ? result[i].thumbnail_img.split(',') : []
            for (var j in tempImg) {
                tempImg[j] = s3Location + tempImg[j]
            }
            
            result[i].channel = {
                channel_id: result[i].channel_id ? result[i].channel_id.split(',') : [],
                thumbnail_img: tempImg
            }

            delete result[i].channel_id;
            delete result[i].thumbnail_img;
        }

        return result
    },
    checkAdmin : async function (userId) {
        
        let admin = await dao.selectUserAdmin(userId);

        if (admin[0].admin == 0) {
            throw new Error('Not Admin')
        }
    },
    getTempUser : async (userId) => {
        const result = await jwt.sign(userId)

        return {
            id : '2163555827048248',
            authorization : result
        }
    }
}