const service = require('../service/userService')
const { respondJson, respondOnError } = require('../lib/response')

module.exports = {
    kakaoSignin: async (req, res) => {
        try {
            const result = await service.kakaoSignin(req.headers.authorization, req.body.access_token)

            respondJson("Success", result, res, 201)
        }
        catch (error) {
            respondOnError(error.message, res, error.statusCode)
        }
    },
    facebookSignin: async (req, res) => {
        try {
            const result = await service.facebookSignin(req.headers.authorization, req.body.access_token)

            respondJson("Success", result, res, 201)
        } catch (error) {
            respondOnError(error.message, res, error.statusCode)
        }
    },
    getUserPage: async (req, res) => {
        try {
            const result = await service.getUserPage(req.user.id)

            respondJson("Success", result, res, 200)

        } catch (error) {
            respondOnError(error.message, res, error.statusCode)
        }
    },
    setUserProfile: async (req, res) => {
        try {
            await service.setUserProfile(req.user, req.body.name, req.files)

            respondJson("Success", null, res, 201)

        } catch (error) {            
            console.log(error)
            if(error.message == "Duplicate ID"){
                respondOnError("Duplicate ID", res, 409)
            }
            else{
                respondOnError(error.message, res, error.statusCode)
            }
        }
    },
    getUserSubcription: async (req, res) => {
        try {
            const result = await service.getUserSubcription(req.user.id, req.headers.flag)

            respondJson("Success", result, res, 200)

        } catch (error) {
            console.log(error)
            respondOnError(error.message, res, error.statusCode)
        }
    },
    getUserScrap: async (req, res) => {
        try {
            const result = await service.getUserScrap(req.user.id, req.headers.flag)

            respondJson("Success", result, res, 200)

        } catch (error) {
            console.log(error)
            respondOnError(error.message, res, error.statusCode)
        }
    },
    getTempUser : async (req, res) => {
        try{
            const result = await service.getTempUser(req.body.user_id)

            respondJson("Success", result, res, 201)
        } catch (error) {
            console.log(error)
            respondOnError(error.message, res, error.statusCode)
        }
    }
}
