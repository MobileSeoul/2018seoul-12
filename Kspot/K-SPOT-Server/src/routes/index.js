const express = require('express');
const router = express.Router();

const main = require('./main');
const user = require('./user');
const channel = require('./channel');
const spot = require('./spot');
const theme = require('./theme');
const search = require('./search');


/* main */
router.use('/main', main);

/* user */
router.use('/user', user);

/* channel */
router.use('/channel', channel);

/* spot */
router.use('/spot', spot);

/* theme */
router.use('/theme', theme);

/* search */
router.use('/search', search);



module.exports = router;