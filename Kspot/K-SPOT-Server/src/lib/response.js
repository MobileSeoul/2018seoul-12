const moment = require('moment');

// data 응답
const respondJson = (message, obj, res, status) => {
  console.log(`${moment().format('MMMM Do YYYY, h:mm:ss a')} => message : ${message} / status : ${status}`)

  res
    .status(status)
    .json({
      message,
      data: (obj) ? obj : {}
    });
}

// Error일 경우 응답
const respondOnError = (message, res, status) => {
  console.log(`${moment().format('MMMM Do YYYY, h:mm:ss a')} => message : ${message} / status : ${status}`)
  res
    .status(status)
    .json({
      message
    });
}
  
module.exports = {  
  respondJson,
  respondOnError
}