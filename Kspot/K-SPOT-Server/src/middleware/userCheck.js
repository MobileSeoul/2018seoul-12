const token = require('../lib/jwt')
const { secretKey } = require('../../config/jwtKey')
const { respondJson, respondOnError } = require('../lib/response')

module.exports = async (req, res, next) => {
  const { authorization } = req.headers

  try {

    req.user = await token.verify(authorization)

    if (!req.user) {
      throw new Error('User Authentication Error')
    }
    
    next()
  } catch (error) {
    respondOnError(error.message, res, 401)
  }
}