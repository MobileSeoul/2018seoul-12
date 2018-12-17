request = require('request-promise')
papagoKey = require('../../config/papagoKey')

// 네이버 기계번역 API 

const client_id = papagoKey.clientId;
const client_secret = papagoKey.clientSecret;
const api_url = 'https://openapi.naver.com/v1/language/translate';
//const api_url = 'https://openapi.naver.com/v1/papago/n2mt';

module.exports = {
    koToEn : async (text) => {
        const options = {
            method: 'POST',
            url: api_url,
            json: true,
            form: {'source':'ko', 'target':'en', 'text':text},
            headers: {'X-Naver-Client-Id':client_id, 'X-Naver-Client-Secret': client_secret}
         };

         const result = await request(options)

         return result.message.result.translatedText
    },
    enToKo : async (text) => {
        const options = {
            method: 'POST',
            url: api_url,
            json: true,
            form: {'source':'en', 'target':'ko', 'text':text},
            headers: {'X-Naver-Client-Id':client_id, 'X-Naver-Client-Secret': client_secret}
         };

         const result = await request(options)

         return result.message.result.translatedText
    }
}