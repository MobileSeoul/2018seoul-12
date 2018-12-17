const service = require('../service/searchService');
const { respondJson, respondOnError } = require('../lib/response');
const token = require('../lib/jwt')

module.exports = {

    getBestKeyword : async(req, res) => {

        try{
            let flag = req.headers.flag;

            let result = await service.getBestKeyword(flag);
            
            respondJson("Success", result, res, 200);

        }catch(error){
            console.log(error);
            respondOnError(error.message, res, error.statusCode);
        } 
    },
    getChannelAndSpotByKeyword : async(req, res) => {

        try{
            const { authorization } = req.headers

            var userId

            if (!authorization) {
                userId = -1
            } else {
                userId = await token.verify(authorization).id
            }

            let keyword = req.params.keyword;
            let isKorean;
            var numUnicode = keyword.charCodeAt(0);

            if (44032 <= numUnicode && numUnicode <= 55203 || 12593 <= numUnicode && numUnicode <= 12643)
                isKorean = true;
            else 
                isKorean = false;

            let channel = await service.getChannelListByKeyword(userId, keyword, isKorean);
            let place = await service.getSpotListByKeyword(0, keyword, isKorean);
            let event = await service.getSpotListByKeyword(1, keyword, isKorean);

            let result = {
                channel: channel,
                place: place,
                event: event
            };

            respondJson("Success", result, res, 200);

        }catch(error){
            console.log(error);
            respondOnError(error.message, res, error.statusCode);
        } 
    },

    getFilterPlaceByKeyword : async(req, res) => {

        try{
            let keyword = req.params.keyword;
            let order = req.params.order; // 0: 인기순, 1:최신순
            let is_food = req.params.is_food; // 0
            let is_cafe = req.params.is_cafe; // 1
            let is_sights = req.params.is_sights; // 2
            let is_etc = req.params.is_etc; // 6

            let result = await service.getSearchSpotFilterByKeyword(keyword, order, is_food, is_cafe, is_sights, 0, 0, 0, is_etc);

            respondJson("Success", result, res, 200);

        }catch(error){
            console.log(error);
            respondOnError(error.message, res, error.statusCode);
        } 
    },

    // 후순위
    getFilterEventByKeyword : async(req, res) => {

        try{
            let keyword = req.params.keyword;
            let order = req.params.order; // 0: 인기순, 1:최신순
            let is_birth = req.params.is_birth; // 3
            let is_celebrate = req.params.is_celebrate; // 4
            let is_etc = req.params.is_etc; // 5

            let result = await service.getSearchSpotFilterByKeyword(keyword, order, 0, 0, 0, is_birth, is_celebrate, is_etc, 0);

            respondJson("Success", result, res, 200);

        }catch(error){
            console.log(error);
            respondOnError(error.message, res, error.statusCode);
        } 
    },
}