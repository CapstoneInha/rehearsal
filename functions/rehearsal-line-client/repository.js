const MySql = require('mysql');
const SQL = require('./sql');
const config = require('./config.json');
const pool = MySql.createPool({
  host: config.host,
  user: config.user,
  password: config.password,
  database: config.database,
  port: config.port,
  connectionLimit : 5,
  queueLimit: 2
});

function connect() {
  return new Promise((resolve, reject) => {
    pool.getConnection((err, con)=>{
      if(err) throw err;
      resolve(con);
    });
  });
}

function createHistory(connection, param) {
  return new Promise((resolve, reject) => {
    const sqlParams = {fileName: param.audioId, createAt: param.createAt, user_id: param.userId, isCompleted: false};
    connection.query(SQL.INSERT_HISTORY, sqlParams, function (error, results, fields) {
      if (error) throw error;
      resolve(results);
    });
  });
}

function createAudio(connection, param) {
  return new Promise((resolve, reject) => {
    const sqlParams = {id: param.audioId, fileName: param.fileName, createAt: param.createAt, user_id: param.userId};
    connection.query(SQL.INSERT_AUDIO, sqlParams, function (error, results, fields) {
      if (error) throw error;
      resolve(results);
    });
  });
}

function findHistoryAll(connection, param) {
  return new Promise((resolve, reject) => {
    connection.query(SQL.SELECT_HISTORY_BY_USER_ID, param, function (error, results, fields) {
      if (error) throw error;
      console.log(results);
      resolve(results);
    })
  });
}

function findHistoryCompleted(connection, param) {
  return new Promise((resolve, reject) => {
    connection.query(SQL.SELECT_HISTORY_USER_ID_AND_COMPLETE, param, function (error, results, fields) {
      if (error) throw error;
      console.log(results);
      resolve(results);
    })
  });
}

function findHistoryContinued(connection, param) {
  return new Promise((resolve, reject) => {
    connection.query(SQL.SELECT_HISTORY_USER_ID_AND_CONTINUE, param, function (error, results, fields) {
      if (error) throw error;
      console.log(results);
      resolve(results);
    })
  });
}

function findAudioAll(connection, param) {
  return new Promise((resolve, reject) => {
    connection.query(SQL.SELECT_AUDIO_BY_USER_ID, param, function (error, results, fields) {
      if (error) throw error;
      console.log(results);
      resolve(results);
    })
  });
}

module.exports.connect = connect;
module.exports.createAudio = createAudio;
module.exports.findAudioAll = findAudioAll;
module.exports.createHistory = createHistory;
module.exports.findHistoryAll = findHistoryAll;
module.exports.findHistoryCompleted = findHistoryCompleted;
module.exports.findHistoryContinued = findHistoryContinued;
