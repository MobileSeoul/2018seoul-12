const service = require('../service/channelService');
const userService = require('../service/userService');
const { respondJson, respondOnError } = require('../lib/response');
const s3Location = require('../../config/s3Location').location
const token = require('../lib/jwt')

module.exports = {

    // for admin
    getChannels : async (req, res) => {

        try{
    
            let result = await service.getChannels();
            
            respondJson("Success", result, res, 200);

        }catch(error){
            console.log(error);
            respondOnError(error.message, res, error.statusCode);
        } 
    },

    getChannelList : async (req, res) => {

        try{
            const { authorization } = req.headers

            var userId

            if (!authorization) {
                userId = -1
            } else {
                userId = await token.verify(authorization).id
            }

            let flag = req.headers.flag;
    
            let result = await service.getChannelList(userId, flag);
            
            respondJson("Success", result, res, 200);

        }catch(error){
            console.log(error);
            respondOnError(error.message, res, error.statusCode);
        } 
    },

    getChannelDetail : async (req, res) => {

        try{
            const { authorization } = req.headers

            var userId

            if (!authorization) {
                userId = -1
            } else {
                userId = await token.verify(authorization).id
            }

            let channelId = req.params.channel_id;
            let flag = req.headers.flag;
    
            let result = await service.getChannelDetail(userId, channelId, flag);

            respondJson("Success", result, res, 200);

        }catch(error){
            console.log(error);

            if (error.message == 'Wrong Input')
                respondOnError(error.message, res, 400);    
            else
                respondOnError(error.message, res, error.statusCode);
        }
    },
    addSubscriptionChannel : async (req, res) => {

        try{
            let userId = req.user.id;
            let channelId = req.body.channel_id;

            await service.addSubscriptionChannel(userId, channelId);

            respondJson("Success", null, res, 201);

        }catch(error){
            console.log(error);
            respondOnError(error.message, res, error.statusCode);
        }
    },
    removeSubscriptionChannel : async (req, res) => {

        try{
            let userId = req.user.id;
            let channelId = req.params.channel_id;
            
            await service.removeSubscriptionChannel(userId, channelId);

            respondJson("Success", null, res, 200);

        }catch(error){
            console.log(error);
            respondOnError(error.message, res, error.statusCode);
        }
    },
    getSpotWithChannel : async(req, res) => {
        try{
            let result

            if(req.params.is_event == 0){
                result = await service.getPlaceWithChannel(req)
            }
            else if(req.params.is_event == 1){
                result = await service.getEventWithChannel(req)
            }

            respondJson("Success", result, res, 200)
        }
        catch (error) {
            console.log(error)
            respondOnError(error.message, res, error.statusCode)
        }
    },



    // for admin
    addChannel : async (req, res) => {
        try{
            await userService.checkAdmin(req.user.id);

            let type = req.body.type;
            let korName = req.body.kor_name;
            let engName = req.body.eng_name;
            let korCompany = req.body.kor_company;
            let engCompany = req.body.eng_company;

            let backgroundImg = req.files.background_img
            let thumbnailImg = req.files.thumbnail_img

            backgroundImg = backgroundImg? backgroundImg[0].location.split(s3Location)[1] : ''
            thumbnailImg = thumbnailImg? thumbnailImg[0].location.split(s3Location)[1] : ''

            let member_kor = req.body.member_kor;
            let member_eng = req.body.member_eng;
            let memberKor = [];
            let memberEng = [];

            memberKor = member_kor ? member_kor.split(',') : [];
            memberEng = member_eng ? member_eng.split(',') : [];

            console.log(memberKor)
            console.log(memberEng)

            await service.addChannel(type, korName, engName, korCompany, engCompany, backgroundImg, thumbnailImg, memberKor, memberEng);

            respondJson("Success", null, res, 201);

        }catch(error){
            console.log(error);

            if (error.message == 'Not Admin')
                respondOnError(error.message, res, 403);    
            else if (error.message == 'Wrong Input')
                respondOnError(error.message, res, 400);    
            else
                respondOnError(error.message, res, error.statusCode);
        }
    },
    getChannelForEdit : async (req, res) => {

        try{
            await userService.checkAdmin(req.user.id);

            let channelId = req.params.channel_id;
    
            let result = await service.getChannelForEdit(channelId);

            respondJson("Success", result, res, 200);

        }catch(error){
            console.log(error);

            if (error.message == 'Not Admin')
                respondOnError(error.message, res, 403); 
            else if (error.message == 'Wrong Input')
                respondOnError(error.message, res, 400);    
            else
                respondOnError(error.message, res, error.statusCode);
        }
    },
    setChannel : async (req, res) => {
        try {
            await userService.checkAdmin(req.user.id);

            let channelId = req.body.channel_id;
            
            // belong_to, id, kor_name, eng_name, kor_company, eng_company, background_img, thumbnail_img
            let result = await service.getChannelForEdit(channelId);

            let korName = req.body.kor_name;
            let engName = req.body.eng_name;
            let korCompany = req.body.kor_company;
            let engCompany = req.body.eng_company;

            if (korName == undefined || korName == "") {
                korName = result[0].kor_name
            }
            if (engName == undefined || engName == "") {
                engName = result[0].eng_name
            }
            if (korCompany == undefined || korCompany == "") {
                korCompany = result[0].kor_company
            }
            if (engCompany == undefined || engCompany == "") {
                engCompany = result[0].eng_company
            }

            let backgroundImg, thumbnailImg;

            if (req.files.background_img == undefined || req.files.background_img.length == 0) {
                backgroundImg = result[0].background_img
            }
            else {
                backgroundImg = req.files.background_img[0].location.split(s3Location)[1]
            }

            if (req.files.thumbnail_img == undefined || req.files.thumbnail_img.length == 0) {
                thumbnailImg = result[0].thumbnail_img
            }
            else {
                thumbnailImg = req.files.thumbnail_img[0].location.split(s3Location)[1]
            }

            await service.setChannel(channelId, korName, engName, korCompany, engCompany, backgroundImg, thumbnailImg)

            respondJson("Success", null, res, 201)

        } catch (error) {            
            console.log(error);

            if (error.message == 'Not Admin')
                respondOnError(error.message, res, 403); 
            else if (error.message == 'Wrong Input')
                respondOnError(error.message, res, 400);   
            else
                respondOnError(error.message, res, error.statusCode)
        }
    },
    removeChannel : async (req, res) => {

        try{
            await userService.checkAdmin(req.user.id);

            let channelId = req.params.channel_id;

            await service.removeChannel(channelId);

            respondJson("Success", null, res, 200);

        }catch(error){
            console.log(error);

            if (error.message == 'Not Admin')
                respondOnError(error.message, res, 403);   
            else if (error.message == 'Wrong Input')
                respondOnError(error.message, res, 400);    
            else 
                respondOnError(error.message, res, error.statusCode);
        }
    },


}