const line = require('@line/bot-sdk');
const config = require('./config.json');


const client = new line.Client({
  channelAccessToken: config.channel_access_token,
  channelSecret: config.channel_secret
});

function replyMessage(token, message) {
  client.replyMessage(token, message);
}

function getAudio(audioId) {
  return client.getMessageContent(audioId);
}

module.exports.replyMessage = replyMessage;
module.exports.getAudio = getAudio;