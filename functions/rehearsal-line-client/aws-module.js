const config = require('./config.json');
const AWS = require('aws-sdk');
const s3Uploader = require('s3-upload-stream');


function upload(params) {
  const s3Stream = s3Uploader(new AWS.S3({
    accessKeyId: config.aws_access_key_id,
    secretAccessKey: config.aws_secret_access_key,
  }));

  const upload = s3Stream.upload({
    Bucket: config.bucket, ACL: 'public-read', Key: 'audio/' + params.userId + '/' + params.audioId + '.m4a'
  });

  params.content.pipe(upload);
  params.content.on('error', (err) => {
    console.log(err);
  });
}

module.exports.upload = upload;
