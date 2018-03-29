'use strict';
const lineModule = require('./line-module');
const awsModule = require('./aws-module');
const repository = require('./repository');
const config = require('./config.json');
const crypto = require('crypto');
const moment = require('moment');
const rp = require('request-promise');

const DEFAULT_MESSAGE = ' Rehearsal USAGE \n' +
  'train start\n' +
  'train history all\n' +
  'train history continued\n' +
  'train history completed\n' +
  'upload list\n' +
  'download file-name  \n\n' +
  'If you upload audio file, \n' +
  'You must upload it via voice recording or attachments.\n' +
  'Specified voice file extensions: .mp3, .m4a \n';


function train(params) {
  return new Promise((resolve, reject) => {
    // trigger trainning
    // request
    // const options = {
    //   method: 'POST',
    //   uri: 'http://',
    //   body: {
    //     user: userId
    //   },
    //   json: true
    // };
    //
    // rp(options)
    //   .then(awsModule.createHistory)
    //   .then(result => {
    //     return "Trainning SUCCESSED";
    //   })
    //   .catch(function (err) {
    //     console.log("Trainning Trigger Fail");
    //     console.log(err);
    //     return "Trainning Trigger Fail";
    //   });
    resolve(params);
  });
}

function outputFormat(param) {
  // find result through db
  return new Promise((resolve, reject) => {
    let hisitories = "<Create Date>\t <FileName> \n";
    param.forEach(object => {
      hisitories += (moment(object.createAt, "kr").format("YYYY-MM-DD hh:mm") + "\t" + object.fileName + "\n");
    });

    resolve(hisitories);
  });
}

module.exports.lineClient = (event, context, callback) => {
  console.log(event);
  repository.connect()
    .then((con) => {
      if (event.body !== null && event.body !== undefined) {
        let body = JSON.parse(event.body);
        const hash = crypto.createHmac('sha256', config.channel_secret)
          .update(event.body, 'utf8')
          .digest('base64');
        if (hash !== event.headers['X-Line-Signature']) {
          throw 'Bad Request';
        }

        body.events.forEach((event) => {
          const userId = event.source.userId;
          const replyToken = event.replyToken;
          const msg = event.message;
          const type = msg.type;
          let responsBody = {
            type: 'text',
            text: DEFAULT_MESSAGE
          };

          if (type === 'audio') {
            lineModule.getAudio(msg.id)
              .then((result) => {
                awsModule.upload({content: result, userId: userId, audioId: msg.id})
              })
              .then(
                repository.createAudio(con, {
                  audioId: msg.id,
                  fileName: msg.id,
                  createAt: moment().format('YYYY-MM-DD hh:mm:ss'),
                  userId: userId
                }))
              .then(() => {
                responsBody = {
                  type: 'text',
                  text: "Upload Voice Recording Success"
                };

                lineModule.replyMessage(replyToken, responsBody);
              });
          } else if (type === 'text') {
            let cmd = msg.text.trim().toLowerCase().split(' ');
            if (cmd.length === 2 && cmd[0] === 'train' && cmd[1] === 'start') {
              // start
              repository.createHistory(con, {
                audioId: msg.id,
                userId: userId,
                createAt: moment().format('YYYY-MM-DD hh:mm:ss')
              }).then(train)
                .then(() => {
                  responsBody.text = "Trainnig Start";
                  lineModule.replyMessage(replyToken, responsBody);
                });
            } else if (cmd.length === 3 && cmd[0] === 'train' && cmd[1] === 'history' && cmd[2] === 'all') {

              // history all
              repository.findHistoryAll(con, [userId])
                .then(outputFormat)
                .then(result => {
                  responsBody.text = result;
                  lineModule.replyMessage(replyToken, responsBody);
                });
            } else if (cmd.length === 3 && cmd[0] === 'train' && cmd[1] === 'history' && cmd[2] === 'continued') {
              // history continued
              repository.findHistoryContinued(con, [userId])
                .then(outputFormat)
                .then(result => {
                  responsBody.text = result;
                  lineModule.replyMessage(replyToken, responsBody);
                });
            } else if (cmd.length === 3 && cmd[0] === 'train' && cmd[1] === 'history' && cmd[2] === 'completed') {
              // history completed
              repository.findHistoryCompleted(con, [userId])
                .then(outputFormat)
                .then(result => {
                  responsBody.text = result;
                  lineModule.replyMessage(replyToken, responsBody);
                });
            } else if (cmd.length === 2 && cmd[0] === 'upload' && cmd[1] === 'list') {
              // list
              repository.findAudioAll(con, [userId])
                .then(outputFormat)
                .then(result => {
                  responsBody.text = result;
                  lineModule.replyMessage(replyToken, responsBody);
                });

            } else if (cmd.length === 2 && cmd[0] === 'download' && cmd[1].length > 0) {
              // download
              responsBody = {
                type: 'audio',
                originalContentUrl: config.aws_host + '/' + config.bucket + '/train/' + userId + '/' + cmd[1] + '.m4a',
                duration: 6000000
              };

              lineModule.replyMessage(replyToken, responsBody);
            } else {
              lineModule.replyMessage(replyToken, responsBody);
            }

          } else {
            lineModule.replyMessage(replyToken, responsBody);
          }

        });
      }

      con.release();
      callback(null, {
        statusCode: 200
      });
    })
    .then((err) => {
      callback(err, {
        statusCode: 400
      });
    });

};

