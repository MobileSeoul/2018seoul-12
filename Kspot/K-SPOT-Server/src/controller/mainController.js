const service = require('../service/mainService');
const { respondJson, respondOnError } = require('../lib/response');
const token = require('../lib/jwt')

module.exports = {

    getMain : async(req, res) => {

        try{
            const { authorization } = req.headers

            var userId

            if (!authorization) {
                userId = -1
            } else {
                userId = await token.verify(authorization).id
            }

            let flag = req.headers.flag;

            let result = await service.getMain(userId, flag);
            
            respondJson("Success", result, res, 200);

        }catch(error){
            console.log(error);
            respondOnError(error.message, res, error.statusCode);
        } 
    }
}