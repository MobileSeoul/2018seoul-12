const jwt = require('jsonwebtoken');
const secretKey = require('../../config/jwtKey').key;

module.exports = {
    // Issue jwt Token
    sign : function(id) {
        const options = {
            algorithm : "HS256",
            expiresIn : 60 * 60 * 24 * 365 // 365 days
        };
        const payload = {
            "id" : id
        };

        let token

        token = jwt.sign(payload, secretKey, options);

        return token;
    },
    // Check jwt
    verify : function(token) {
        let decoded

        decoded = jwt.verify(token, secretKey);

        if(!decoded) {
            return -1;
        }else {
            return decoded;
        }
    }
};