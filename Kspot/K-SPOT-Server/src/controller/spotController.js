const service = require('../service/spotService')
const userService = require('../service/userService')
const { respondJson, respondOnError } = require('../lib/response')

module.exports = {
    getSpotRecommendByGu: async (req, res) => {
        try {
            const result = await service.getSpotRecommendByGu(req)

            respondJson("Success", result, res, 200)
        }
        catch (error) {
            console.log(error)
            
            respondOnError(error.message, res, error.statusCode)
        }
    },
    getSpotRecommendByLocation: async (req, res) => {
        try {
            const result = await service.getSpotRecommendByLocation(req)

            respondJson("Success", result, res, 200)
        }
        catch (error) {
            console.log(error)

            respondOnError(error.message, res, error.statusCode)
        }
    },
    getSpotDetail: async (req, res) => {
        try {
            const result = await service.getSpotDetail(req)

            respondJson("Success", result, res, 200)
        }
        catch (error) {
            console.log(error)
            respondOnError(error.message, res, error.statusCode)
        }
    },
    getReview : async (req, res) => {
        try{
            const result = await service.getReview(req)

            respondJson("Success", result, res, 200)
        }
        catch (error) {
            console.log(error)
            respondOnError(error.message, res, error.statusCode)
        }
    },
    addReview : async (req, res) => {
        try{

            console.log(":: add review ::")
            console.log(req.body)
            
            await service.addReview(req)

            respondJson("Success", null, res, 201)
        }
        catch (error) {
            console.log(error)
            respondOnError(error.message, res, error.statusCode)
        }
    },
    addSpot : async (req, res) => {
        try{
            console.log(req.user.id)
            await userService.checkAdmin(req.user.id);

            await service.addSpot(req)

            respondJson("Success", null, res, 201)
        }
        catch (error) {
            console.log(error)

            if (error.message == 'Not Admin')
                respondOnError(error.message, res, 403)
            else if(error.message == 'insert error')
                respondOnError(error.message, res, 403)    
            else
                respondOnError(error.message, res, error.statusCode)
        }
    },
    getSpotList : async (req, res) => {
        try{

            const result = await service.getSpotList()

            respondJson("Success", result, res, 200)
        }
        catch (error) {
            console.log(error)
            respondOnError(error.message, res, error.statusCode)
        }
    },
    // 이벤트 더보기 for main
    getEventList : async (req, res) => {
        try{
            let flag = req.headers.flag;

            const result = await service.getEventList(flag)

            respondJson("Success", result, res, 200)
        }
        catch (error) {
            console.log(error)
            respondOnError(error.message, res, error.statusCode)
        }
    },
    addScrapSpot : async (req, res) => {
        try{
            let userId = req.user.id;
            let spotId = req.body.spot_id;

            await service.addScrapSpot(userId, spotId)

            respondJson("Success", null, res, 201)
        }
        catch (error) {
            console.log(error)
            respondOnError(error.message, res, error.statusCode)
        }
    },
    removeScrapSpot : async (req, res) => {
        try{
            let userId = req.user.id;
            let spotId = req.params.spot_id;

            await service.removeScrapSpot(userId, spotId)

            respondJson("Success", null, res, 200)
        }
        catch (error) {
            console.log(error)
            respondOnError(error.message, res, error.statusCode)
        }
    },
    getSpotInfo : async (req, res) => {
        try{
            const result = await service.getSpotInfo(req.params.spot_id)

            respondJson("Success", result, res, 200)
        }
        catch (error) {
            console.log(error)
            respondOnError(error.message, res, error.statusCode)
        }
    },
    removeSpot : async (req, res) => {
        try{
            await userService.checkAdmin(req.user.id);

            await service.removeSpot(req.params.spot_id)

            respondJson("Success", null, res, 200)
        }
        catch (error) {
            console.log(error)
            if (error.message == 'Not Admin')
                respondOnError(error.message, res, 403)
            else
                respondOnError(error.message, res, error.statusCode)
        }
    },
    setSpot : async (req, res) => {
        try{
            await userService.checkAdmin(req.user.id);

            await service.setSpot(req)

            respondJson("Success", null, res, 201)
        }
        catch (error) {
            console.log(error)
            if (error.message == 'Not Admin')
                respondOnError(error.message, res, 403)
            else
                respondOnError(error.message, res, error.statusCode)
        }
    },


    // by jiyeon 
    setSpotAddressEng : async (req, res) => {
        try{
            await userService.checkAdmin(req.user.id);

            await service.setSpotAddressEng()

            respondJson("Success", null, res, 201)
        }
        catch (error) {
            console.log(error)
            if (error.message == 'Not Admin')
                respondOnError(error.message, res, 403)
            else
                respondOnError(error.message, res, error.statusCode)
        }
    },

}