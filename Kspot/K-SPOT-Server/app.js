var createError = require('http-errors');
var express = require('express');
var path = require('path');
var cookieParser = require('cookie-parser');
var logger = require('morgan');
var bodyParser = require('body-parser');
var cors = require('cors');

// 스케줄링 모듈
var schedule = require('node-schedule');
// 매일 23시59분30초에 스케줄링
// 하루가 지난 게시물 관련 channel newpostcheck set 0
const newPostCheckSchedule = require('./src/lib/newPostCheckSchedule')
schedule.scheduleJob('30 59 23 * * *', async() => {
  console.log("scheduling start!");

  await newPostCheckSchedule()

  console.log("scheduling finish!");
});

//var useragent = require('express-useragent'); // Device check module

var app = express();

// parse application/x-www-form-urlencoded
app.use(bodyParser.urlencoded({ extended: false }))

// parse application/json
app.use(bodyParser.json())

//app.use(useragent.express());
// req.useragent

// view engine setup
app.set('views', path.join(__dirname, 'views'));
app.set('view engine', 'jade');

app.use(logger('dev'));
app.use(express.json());
app.use(express.urlencoded({ extended: false }));
app.use(cookieParser());
app.use(express.static(path.join(__dirname, 'public')));
app.use(cors());

var routes = require('./src/routes');
app.use('/', routes)

// catch 404 and forward to error handler
app.use(function(req, res, next) {
  next(createError(404));
});

// error handler
app.use(function(err, req, res, next) {
  // set locals, only providing error in development
  res.locals.message = err.message;
  res.locals.error = req.app.get('env') === 'development' ? err : {};

  // render the error page
  res.status(err.status || 500);
  res.render('error');
});

module.exports = app;
