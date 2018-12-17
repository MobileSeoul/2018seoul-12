const express = require('express')
const main = express.Router();

const mainController = require('../controller/mainController');
const userCheck = require('../middleware/userCheck');


/* select main list */
main.get('/', mainController.getMain);


module.exports = main